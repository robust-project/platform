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
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.AbstractInformationDiffusion
import scala.collection.{Set, mutable}
import scala.Some
import org.jgrapht.{UndirectedGraph, DirectedGraph}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 24/05/2013
 * Time: 16:17
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
class LTM[E](g: AbstractBaseGraph[Long, E]) extends AbstractInformationDiffusion with Logging with Trackable {

  private val vertices = g.vertexSet().toSet

  def simulate(seed: collection.Set[Long], random: MersenneTwister, track: Boolean) = {
    val trackingGraph =
      if (track) Some(new SimpleDirectedWeightedGraph[Long, DefaultWeightedEdge](classOf[DefaultWeightedEdge]))
      else None
    // initialize the model
    val active = mutable.Set.empty[Long] ++ seed // set of all active nodes
    var newlyActivated = active // set of the nodes activated in the previous iteration
    val thresholds = (for (v <- vertices) yield (v, random.nextDouble())).toMap

    val activeNodes = mutable.MutableList.empty[Set[Long]] // list of activated nodes per each iteration
    activeNodes += seed

    // run the model until convergence or until maxIter has been reached
    while (!newlyActivated.isEmpty) {
      // iterate over non-activated nodes to see if the newly activated
      // nodes can move the non-activated neighbours, i.e. 'v', above their thresholds
      val result = mutable.Set.empty[Long]
      for (n <- newlyActivated; // newly activated node
           outV <- neighbours(n)
           if !active.contains(outV) && !result.contains(outV)) {
        val outE = activationEdges(outV, active)
        val totalWeight = outE.map(g.getEdgeWeight(_)).sum
        if (totalWeight >= thresholds(outV)) {
          result += outV
          if (trackingGraph.isDefined) {
            val tg = trackingGraph.get
            // track and re-normalize all the edges that contributed to the activation
            tg.addVertex(outV)
            for (e <- outE) {
              val inV = oppositeVertex(outV, e)
              tg.addVertex(inV)
              tg.setEdgeWeight(tg.addEdge(inV, outV), g.getEdgeWeight(e) / thresholds(outV)) // re-normalize the weights to measure the relative contribution
            }
          }
        }
      }
      newlyActivated = result
      active ++= newlyActivated
      if (!newlyActivated.isEmpty) {
        activeNodes += newlyActivated
      }
    }

    (activeNodes, trackingGraph)
  }

  /**
   * @return Neighbours x of v: (x->v)
   */
  private def neighbours(v: Long) = g match {
    case _: DirectedGraph[_, _] => g.incomingEdgesOf(v).map(e => g.getEdgeSource(e))
    case _: UndirectedGraph[_, _] => g.edgesOf(v).map(e => oppositeVertex(v, e))
  }

  private def activationEdges(v: Long, active: collection.Set[Long]) = g match {
    case _: DirectedGraph[_, _] => g.outgoingEdgesOf(v).filter(e => active.contains(g.getEdgeTarget(e)))
    case _: UndirectedGraph[_, _] => g.edgesOf(v).filter(e =>
      active.contains(g.getEdgeSource(e)) || active.contains(g.getEdgeTarget(e)))
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