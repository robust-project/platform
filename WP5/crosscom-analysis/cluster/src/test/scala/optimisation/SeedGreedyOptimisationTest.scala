/*
 * Copyright (c) 2010-2013 Digital Enterprise Research Institute, NUI Galway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package optimisation

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.{LinearThresholdModel, IndependentCascadeModel}
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import com.tinkerpop.blueprints.pgm.{Edge, Vertex}
import collection.Set
import scala.collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.cluster.optimisation.{ClusterSeedGreedyOptimisation, VertexSeedGreedyOptimisation}
import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster
import org.apache.commons.math.random.MersenneTwister
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.jgrapht.{LTM, ICM}
import org.jgrapht.graph.DefaultWeightedEdge

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/10/2012
 * Time: 12:31
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */


@RunWith(classOf[JUnitRunner])
class SeedGreedyOptimisationTest extends FunSuite {
  val random = new MersenneTwister(25)
  val graph = Data.generateTwoStarGraph
  val jgt = Data.generateTwoStarJGraphT
  val icm = new IndependentCascadeModel(500, graph, None, DEFAULT_EDGE, spreadActivation)
  val ltm = new LinearThresholdModel(500, graph, DEFAULT_EDGE, activateNow)
  val icmJGT = new ICM[DefaultWeightedEdge](jgt)
  val ltmJGT = new LTM[DefaultWeightedEdge](jgt)
  val (membership, clusters) = getClusters
  val repetitions = 10

  test("Find the two centres of stars under ICM") {
    val optim = new VertexSeedGreedyOptimisation(icm, repetitions, random, graph.getVertices.toSeq.par.map(v => v(ORIGID)).seq)
    val optimJGT = new VertexSeedGreedyOptimisation(icmJGT, repetitions, random, jgt.vertexSet().toSeq)
    val one = optim.maximise(1).head
    val oneJGT = optimJGT.maximise(1).head
    assert(one == 10 || one == 20, "One of the cores has to be picked")
    assert(oneJGT == 10 || oneJGT == 20, "One of the cores has to be picked")
    assert(optim.maximise(2).toSet === Set(10, 20))
    assert(optimJGT.maximise(2).toSet === Set(10, 20))
  }

  test("Find the two centres of stars under LTM") {
    val optim = new VertexSeedGreedyOptimisation(ltm, repetitions, random, graph.getVertices.toSeq.par.map(v => v(ORIGID)).seq)
    val optimJGT = new VertexSeedGreedyOptimisation(ltmJGT, repetitions, random, jgt.vertexSet().toSeq)
    val one = optim.maximise(1).head
    val oneJGT = optimJGT.maximise(1).head
    assert(one == 10 || one == 20, "One of the cores has to be picked")
    assert(oneJGT == 10 || oneJGT == 20, "One of the cores has to be picked")
    assert(optim.maximise(2).toSet === Set(10, 20))
    assert(optimJGT.maximise(2).toSet === Set(10, 20))
  }

  test("Find the cluster containing centres under ICM") {
    val optim = new ClusterSeedGreedyOptimisation[Cluster[Long]](icm, repetitions, clusters, random, membership, membership, 2, true)
    val optimJGT = new ClusterSeedGreedyOptimisation[Cluster[Long]](icmJGT, repetitions, clusters, random, membership, membership, 2, true)
    assert(optim.maximise(1).head.index === 3)
    assert(optim.maximise(2).head.index === 3)
    assert(optimJGT.maximise(1).head.index === 3)
    assert(optimJGT.maximise(2).head.index === 3)
  }

  test("Find the cluster containing centres under LTM") {
    val optim = new ClusterSeedGreedyOptimisation[Cluster[Long]](ltm, repetitions, clusters, random, membership, membership, 2, true)
    val optimJGT = new ClusterSeedGreedyOptimisation[Cluster[Long]](ltmJGT, repetitions, clusters, random, membership, membership, 2, true)
    assert(optim.maximise(1).head.index === 3)
    assert(optim.maximise(2).head.index === 3)
    assert(optimJGT.maximise(1).head.index === 3)
    assert(optimJGT.maximise(2).head.index === 3)
  }

  def activateNow(active: Set[Vertex], thresholds: Map[Vertex, Double], v: Vertex): Boolean =
    v.getOutEdges(DEFAULT_EDGE.name).filter(e => active.contains(e.getInVertex())).map(e =>
      e(WEIGHT)).sum >= thresholds(v)

  def spreadActivation(random: MersenneTwister, active: Set[Vertex], v: Vertex): Iterable[(Vertex, Vertex, Edge, Boolean)] = {
    assert(v != null, "v is null")
    assert(active != null, "active is null")
    assert(random != null, "random is null")
    v.getInEdges(DEFAULT_EDGE.name).filterNot(e => active.contains(e.getOutVertex())).map(e =>
        (v, e.getOutVertex(), e, e(WEIGHT) >= random.nextDouble()))
  }

  private def getClusters = {
    val c1 = Cluster(1, (1l to 10).toSet)
    val c2 = Cluster(2, (11l to 20).toSet)
    val c3 = Cluster(3, Set(10l, 20l))

    def m(k: (Long, Int)) = {
      val (uid, cid) = k
      if (uid % 10 == 0) {
        // either 10 or 20
        cid match {
          case 1 => 0.1
          case 2 => 0.1
          case 3 => 0.8
        }
      } else {
        // 1..9 and 11..19
        cid match {
          case 3 => 0d
          case _ => 1d
        }
      }
    }

    (m(_), Seq(c1, c2, c3))
  }
}