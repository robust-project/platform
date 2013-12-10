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
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import com.tinkerpop.blueprints.pgm.{Graph, Vertex}
import scala.Array
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperty
import org.apache.commons.math.random.MersenneTwister
import ie.deri.uimr.crosscomanalysis.util.Logging

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/12/2011
 * Time: 12:37
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class LinearThresholdModel(private val maxIter: Int, private val graph: Graph, private val edgeType: GraphProperty[_],
                            private val activateNow: (Set[Vertex], Map[Vertex, Double], Vertex) => Boolean)
  extends AbstractInformationDiffusion with Logging {
  private val vertices = graph.getVertices.toSet

  def simulate(seed: collection.Set[Long], random: MersenneTwister) = {
    // initialize the model
    var active = vertices.filter(v => seed.contains(v(ORIGID)))
    var newlyActivated = active
    val thresholds = (for (v <- vertices) yield (v, random.nextDouble())).toMap

    val activeNode = new Array[collection.Set[Long]](maxIter + 1)
    activeNode(0) = seed

    // run the model until convergence or until maxIter has been reached
    var realIter = 0
    for (i <- 1 to maxIter if !newlyActivated.isEmpty) {
      if (i % 100 == 0) log.info("Iteration: " + i)
      // iterate over non-activated nodes neighbouring with newly activated ones, this is to see if the newly activated
      // nodes, e.g. 'v', can move the non-activated neighbours, i.e. 'x', above their threshold
      val result =
        (for (v <- newlyActivated.map(v => v.getInEdges().map(_.getOutVertex).filterNot(x => active.contains(x))).flatten)
         yield (v, activateNow(active, thresholds, v)))
      newlyActivated = result.filter(pair => pair._2).map(_._1)
      active = active ++ newlyActivated
      activeNode(i) = newlyActivated.map(v => v(ORIGID))
      realIter = i
    }

    activeNode.slice(0, realIter + 1).toList
  }
}