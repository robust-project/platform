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

package ie.deri.uimr.crosscomanalysis.graphdb.importers

import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.HashMap
import com.tinkerpop.blueprints.pgm.{Edge, Graph}
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperty
import collection.Map

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/02/2011
 * Time: 22:58
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait DBGraphImporter extends GraphImporter {

  def loadGraph(sliceId: Long, g: Graph, edgeWeights: HashMap[Edge, Double], edgeType: GraphProperty[_] = COCITED): Graph = {
    reInitializeGraphImporter()
    implicit val graph = g
    inTransaction {
      // load all vertices first
      for (vertexid <- from(networkSliceVertex)(nsv => where(nsv.sliceId === sliceId) select(nsv.vertexid))) {
        val v = retrieveOrCreateVertex(vertexid)
        v.setProperty(ORIGID, vertexid.asInstanceOf[Any])
      }
      for ((source, sink, weight) <-
           from(networkSliceStructure)(nss => where(nss.sliceId === sliceId) select (nss.source, nss.sink, nss.weight))) {
        val sV = retrieveOrCreateVertex(source)
        val sS = retrieveOrCreateVertex(sink)
        val edge = g.addEdge(newEdgeId, sV, sS, edgeType)
        edge.setProperty(WEIGHT, weight.asInstanceOf[Any])
        edgeWeights(edge) = weight
      }
    }
    if (!edgeWeights.isEmpty) {
      val maxW =  edgeWeights.values.max
      edgeWeights.keys.foreach(edge => edgeWeights(edge) /= maxW)
    }
    g
  }

  /**
   * Loads graph with edge type = COCITED
   */
  def loadGraph(sliceId: Long, edgeWeights: HashMap[Edge, Double]): Graph = {
    val g = new TinkerGraph
    loadGraph(sliceId, g, edgeWeights)
  }

  /**
   * @return (graph, weights)
   */
  def loadGraph(sliceId: Long, edgeType: GraphProperty[_]): (Graph, Map[Edge, Double]) = {
    val w = new HashMap[Edge, Double]
    val g = new TinkerGraph
    (loadGraph(sliceId, g, w, edgeType), w)
  }
}