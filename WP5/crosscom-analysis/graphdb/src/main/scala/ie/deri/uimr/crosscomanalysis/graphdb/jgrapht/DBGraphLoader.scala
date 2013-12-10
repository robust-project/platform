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

package ie.deri.uimr.crosscomanalysis.graphdb.jgrapht

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import org.jgrapht.graph.{SimpleWeightedGraph, DefaultWeightedEdge, DefaultEdge, DefaultDirectedWeightedGraph}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 24/05/2013
 * Time: 15:51
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
object DBGraphLoader {

  def loadDirectedGraph(slice: Long) = inTransaction {
    val g = new DefaultDirectedWeightedGraph[Long, DefaultWeightedEdge](classOf[DefaultWeightedEdge])
    for (v <- from(networkSliceVertex)(nsv => where(nsv.sliceId === slice) select(nsv.vertexid))) {
      g.addVertex(v)
    }
    for ((source, sink, weight) <- from(networkSliceStructure)(nss => where(nss.sliceId === slice)
      select(nss.source, nss.sink, nss.weight))) {
      g.setEdgeWeight(g.addEdge(source, sink), weight)
    }
    g
  }

  def loadUndirectedGraph(slice: Long) = {
    val g = new SimpleWeightedGraph[Long, DefaultWeightedEdge](classOf[DefaultWeightedEdge])
    for (v <- from(networkSliceVertex)(nsv => where(nsv.sliceId === slice) select(nsv.vertexid))) {
      g.addVertex(v)
    }
    for ((source, sink, weight) <- from(networkSliceStructure)(nss => where(nss.sliceId === slice)
      select(nss.source, nss.sink, nss.weight))) {
      val e = g.addEdge(source, sink)
      g.setEdgeWeight(if (e == null) g.getEdge(source, sink) else e, weight)
    }
    g
  }

  def loadGraph(slice: Long, directed: Boolean) =
    if (directed) loadDirectedGraph(slice)
    else loadUndirectedGraph(slice)
}
