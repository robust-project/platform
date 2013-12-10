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

package ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.jgrapht

import org.jgrapht.graph._
import org.apache.commons.math.random.MersenneTwister
import ie.deri.uimr.crosscomanalysis.util._
import collection._
import JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.AbstractInformationDiffusion
import scala.Some
import org.jgrapht.{UndirectedGraph, DirectedGraph}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 24/05/2013
 * Time: 17:54
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
class ICM[E](g: AbstractBaseGraph[Long, E]) extends AbstractInformationDiffusion with Logging with Trackable {

  def simulate(seed: collection.Set[Long], random: MersenneTwister, track: Boolean) = {
    val trackingGraph =
      if (track) Some(new SimpleDirectedWeightedGraph[Long, DefaultWeightedEdge](classOf[DefaultWeightedEdge]))
      else None

    // initialize the model
    val active = new mutable.HashSet[Long] ++ seed // set of all active nodes
    var newlyActivated = active.toSeq // nodes activated in the previous iteration
    val activeNodes = mutable.MutableList.empty[Set[Long]] // list of activated nodes per each iteration
    activeNodes += seed

    // run the model
    while (!newlyActivated.isEmpty) {
      val result = mutable.Set.empty[Long]
      for (v <- shuffle(newlyActivated, random);
           inE <- activationEdges(v, active);
           outV = oppositeVertex(v, inE)
           if g.getEdgeWeight(inE) >= random.nextDouble()) {
        result += outV
        if (trackingGraph.isDefined) {
          val tg = trackingGraph.get
          tg.addVertex(v)
          tg.addVertex(outV)
          tg.setEdgeWeight(tg.addEdge(v, outV), g.getEdgeWeight(inE))
        }
      }
      active ++= result
      newlyActivated = result.toSeq
      if (!result.isEmpty) {
        activeNodes += result
      }
    }
    log.debug("Number of iterations: " + activeNodes.size)

    (activeNodes, trackingGraph)
  }

  /**
   * @return Edges that are pointing to 'v' and whose source is not yet active.
   */
  private def activationEdges(v: Long, active: collection.Set[Long]) = g match {
    case _: DirectedGraph[_, _] => g.incomingEdgesOf(v).filterNot(e => active.contains(g.getEdgeSource(e)))
    case _: UndirectedGraph[_, _] => g.edgesOf(v).filterNot(e => active.contains(oppositeVertex(v, e)))
  }

  private def oppositeVertex(v: Long, e: E) = {
    val s = g.getEdgeSource(e)
    if (s == v) g.getEdgeTarget(e) else s
  }

  /**
   * Runs the simulation and returns the result - can be called again for different parameters
   * @param seed Set of seed nodes (initially activated)
   * @return array of sets of newly activated users for each iteration
   */
  def simulate(seed: Set[Long], random: MersenneTwister) = simulate(seed, random, track = false)._1
}
