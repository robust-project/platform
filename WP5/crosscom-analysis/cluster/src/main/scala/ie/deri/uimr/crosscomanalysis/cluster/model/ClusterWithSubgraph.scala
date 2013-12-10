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

package ie.deri.uimr.crosscomanalysis.cluster.model

import com.tinkerpop.blueprints.pgm.{Graph, Vertex}
import collection.JavaConversions.iterableAsScalaIterable
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 23/02/2011
 * Time: 11:04
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

trait ClusterWithSubgraph {

  this: Cluster[_] =>

  val graph: Graph
  lazy val subgraph = filterGraph

  private def filterGraph = {
    val subGraph = new TinkerGraph
    val vert = vertices collect {
      case v: Vertex => v
      // if the initial set of vertices is not of Vertex type, use origid to get Vertices
      case v: Long => {
        try {
          graph.getVertices.find(v == _.getProperty(ORIGID)).get
        } catch {
          case e: NoSuchElementException => sys.error("Vertex " + v + " not found for cluster " + this.toString())
        }
      }
      case v => sys.error("Unknown vertex type: " + v)
    }
    // add all vertices - the subgraph is thus not necessarily connected
    for (v <- vert) {
      val newV = subGraph.addVertex(v.getId.toString.toLong)
      newV.setProperty(ORIGID, v.getId.toString.toLong)
    }
    // add edges whose both ends are within the cluster's nodes
    for (edge <- graph.getEdges if vert.contains(edge.getInVertex) && vert.contains(edge.getOutVertex)) {
      val inV = retrieveOrAddVertex(edge.getInVertex.getId.toString.toLong, subGraph)
      val outV = retrieveOrAddVertex(edge.getOutVertex.getId.toString.toLong, subGraph)
      val e = subGraph.addEdge(edge.getId, inV, outV, edge.getLabel)
      e.setProperty(WEIGHT, edge.getProperty(WEIGHT))
    }
    subGraph
  }

  private def retrieveOrAddVertex(id: Long, g: Graph) = {
    val v = g.getVertex(id)
    if (v == null) {
      val newV = g.addVertex(id)
      newV.setProperty(ORIGID, id)
      newV
    } else v
  }
}