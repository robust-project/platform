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

package ie.deri.uimr.crosscomanalysis.cluster.simulation.impact

import util.Random
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import collection.JavaConversions.iterableAsScalaIterable
import jsc.distributions.Geometric
import com.tinkerpop.blueprints.pgm.{Graph, Vertex}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 22/02/2012
 * Time: 12:33
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

class ForestFireModel(val n: Int, val burningProb: Double, val backwardsBurningRatio: Double,
                      val linkType: String, val seed: Int = 25) {
  private val random = new Random(seed)
  private val geomFwd = new Geometric(1 - burningProb)
  private val geomBack = new Geometric(1 - burningProb * backwardsBurningRatio)
  private var vId = 0
  private var eId = 0

  def generateGraph = {
    val g = new TinkerGraph
    g.addVertex(nextVId) // add the first node

    for (i <- vId until n) {
      val n = g.addVertex(nextVId)
      val ambassador = random.shuffle(g.getVertices.toList).head
      burningStep(n, Set(ambassador), Set(), g) // link the node to its ambassador
    }
    g
  }

  protected def burningStep(source: Vertex, targets: collection.Set[Vertex], linkedNodes: collection.Set[Vertex], g: Graph) {
    // link to the target nodes
    var newlyLinkedNodes: collection.Set[Vertex] = Set()
    for (target <- targets) {
      g.addEdge(nextEId, source, target, linkType)
      newlyLinkedNodes += target
    }
    // collect nodes referenced by out (forwards) and in (backwards) edges leading from the newly linked nodes
    val forwards = newlyLinkedNodes.toList.map(n => n.getOutEdges(linkType).map(_.getInVertex)).flatten -- newlyLinkedNodes.toList -- linkedNodes.toList
    val backwards = newlyLinkedNodes.toList.map(n => n.getInEdges(linkType).map(_.getOutVertex)).flatten -- newlyLinkedNodes.toList -- linkedNodes.toList
    // sample the forwards and backwards nodes
    var newTargets: collection.Set[Vertex] = Set()
    val fwdSize = geomFwd.random().toInt
    newTargets ++= random.shuffle(forwards).slice(0, (if (fwdSize > forwards.size) forwards.size else fwdSize))
    val backSize = geomBack.random().toInt
    newTargets ++= random.shuffle(backwards).slice(0, (if (backSize > backwards.size) backwards.size else backSize))

    // propagate the burning process
    if (!newTargets.isEmpty)
      burningStep(source, newTargets, linkedNodes ++ newlyLinkedNodes, g)
  }

  protected def nextVId = {
    vId = vId + 1
    vId
  }

  protected def nextEId = {
    eId = eId + 1
    eId
  }
}
