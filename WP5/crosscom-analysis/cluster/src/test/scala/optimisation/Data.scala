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

import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import ie.deri.uimr.crosscomanalysis.graphdb.importers.GraphImporter
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties.DEFAULT_EDGE
import org.jgrapht.graph.{DefaultEdge, SimpleGraph, DefaultWeightedEdge, DefaultDirectedWeightedGraph}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/10/2012
 * Time: 12:23
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
object Data extends GraphImporter {

  /**
   * @return A graph consisting of two components/stars of size 10 (9 being pointed to from the centre (id 10 and 20))
   */
  def generateTwoStarGraph = {
    implicit val g = new TinkerGraph
    reInitializeGraphImporter()
    retrieveOrCreateVertex(10)
    retrieveOrCreateVertex(2)
    for (i <- 1 to 9) {
      retrieveOrCreateVertex(i)
      retrieveOrCreateVertex(i + 10)
      createEdge(i, 10, DEFAULT_EDGE, weight = Some(1d))
      createEdge(i + 10, 20, DEFAULT_EDGE, weight = Some(1d))
    }

    g
  }

  def generateTwoStarJGraphT = {
    val g = new DefaultDirectedWeightedGraph[Long, DefaultWeightedEdge](classOf[DefaultWeightedEdge])
    (1l to 20l).foreach(g.addVertex(_))
    for (i <- 1l to 9l) {
      g.setEdgeWeight(g.addEdge(i, 10), 1)
      g.setEdgeWeight(g.addEdge(i + 10, 20), 1)
    }
    g
  }

  def generateTwoComponentGraphForDDiscount = {
    implicit val graph = new TinkerGraph
    reInitializeGraphImporter()
    def undirEdge(i: Int, j: Int) =
      (createEdge(i.toLong, j.toLong, DEFAULT_EDGE), createEdge(j.toLong, i.toLong, DEFAULT_EDGE))

    retrieveOrCreateVertex(1)
    retrieveOrCreateVertex(2)
    retrieveOrCreateVertex(3)
    retrieveOrCreateVertex(4)
    retrieveOrCreateVertex(5)
    retrieveOrCreateVertex(6)
    retrieveOrCreateVertex(7)
    retrieveOrCreateVertex(8)
    undirEdge(1,2)
    undirEdge(2,3)
    undirEdge(2,4)
    undirEdge(3,4)
    undirEdge(4,5)

    undirEdge(6,7)
    undirEdge(6,8)

    graph
  }

  def generateTwoComponentJGTForDDiscount = {
    val g = new SimpleGraph[Long, DefaultEdge](classOf[DefaultEdge])
    for (v <- 1 to 8) g.addVertex(v)

    g.addEdge(1,2)
    g.addEdge(2,3)
    g.addEdge(2,4)
    g.addEdge(3,4)
    g.addEdge(4,5)

    g.addEdge(6,7)
    g.addEdge(6,8)

    g
  }
}
