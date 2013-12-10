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

package ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif

import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import com.tinkerpop.blueprints.pgm.{Edge, Graph, Vertex}
import scala.Array
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.util._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperty
import scala.collection.{Set, Iterable}
import com.tinkerpop.blueprints.pgm.util.GraphHelper
import org.apache.commons.math.random.MersenneTwister

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 15/12/2011
 * Time: 16:27
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class IndependentCascadeModel(private val maxIter: Int, private val g: Graph, private val trackingGraph: Option[Graph],
                              private val edgeType: GraphProperty[_],
                              private val spreadActivation: (MersenneTwister, Set[Vertex], Vertex) => Iterable[(Vertex, Vertex, Edge, Boolean)])
  extends AbstractInformationDiffusion with Logging {

  def simulate(seed: collection.Set[Long], random: MersenneTwister) = {
    // initialize the model
    var active = seed.map(s => g.getVertex(s.asInstanceOf[AnyRef]))
//    var newlyActivated = active.toSeq.sortBy(_.getId.toString) // nodes activated in the previous iteration
    var newlyActivated = active.toSeq // nodes activated in the previous iteration
    val activeNodes = new Array[collection.Set[Long]](maxIter + 1)
    activeNodes(0) = seed
    // run the model
    var realIter = 0
    for (i <- 1 to maxIter if !newlyActivated.isEmpty) {
      // run until convergence or until max #iterations has been reached
      if (i % 100 == 0) log.info("Iteration: " + i)

      // collection of tuples (source, sink, activated?)
      val result = (for (v <- shuffle(newlyActivated, random)) yield spreadActivation(random, active, v)).flatten
      val activations = (for ((source, sink, edge, succeeded) <- result if succeeded)
        yield (source, sink, edge)).toSet
      val activated = activations.map(_._2)

      if (trackingGraph.isDefined) {
        // add edges recording the spreading process
        var alreadyTracked = Set.empty[Vertex] // set of already tracked newly activated sinks, some may be activated multiple times
        var c = 0
        // track the activations SEQUENTIALLY (modifying the graph!)
        for ((source, sink, edge) <- activations if !alreadyTracked.contains(sink)) {
          val e = GraphHelper.addEdge(trackingGraph.get, edge.getId, getOrCreateV(source), getOrCreateV(sink), ACTIVATED)
          alreadyTracked = alreadyTracked + sink
          e(TIME) = i.asInstanceOf[AnyRef]
          c += 1
        }
        assert(c == activated.size, "All activated vertices have to be traced")
        log.debug("Tracked " + c + " out of " + activations.size + " activations")
      }

      active = active ++ activated
//      newlyActivated = activated.toSeq.sortBy(_.getId.toString)
      newlyActivated = activated.toSeq
      activeNodes(i) = activated.map(v => v(ORIGID))
      realIter = i
    }
    log.debug("Number of iterations: " + realIter)

    activeNodes.slice(0, realIter + 1).toList
  }

  private def getOrCreateV(v: Vertex): Vertex = {
    val vert = trackingGraph.get.getVertex(v.getId)

    if (vert == null)
      GraphHelper.addVertex(trackingGraph.get, v.getId, ORIGID.name, v.getProperty(ORIGID))
    else vert
  }
}