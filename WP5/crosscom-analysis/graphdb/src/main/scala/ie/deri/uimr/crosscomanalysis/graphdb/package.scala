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

package ie.deri.uimr.crosscomanalysis

import com.tinkerpop.blueprints.pgm.Graph
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.util.Logging
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import org.jgrapht.graph._
import java.io.File
import scala.io.Source
import org.jgrapht.{DirectedGraph, UndirectedGraph}


/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/10/2012
 * Time: 12:04
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
package object graphdb extends Logging {

  /**
   * Normalizes weights in the given graph, s.t. the
   * @param graph Graph to normalize
   * @param normalizeByOutStrength If true, each w_ij normalized by the total out-strength of i, otherwise by its in-strength
   * @param edgeType Type of edges which weights to normalize
   * @return The graph (just for convenience)
   */
  def normalizeWeights(graph: Graph, normalizeByOutStrength: Boolean, edgeType: GraphProperty[Nothing]) = {
    log.debug("Normalizing weights")
    for (e <- graph.getEdges) {
      val source = e.getOutVertex
      val normalizer =
        if (normalizeByOutStrength) source.getOutEdges(edgeType.name).map(_(WEIGHT)).sum
        else source.getInEdges(edgeType.name).map(_(WEIGHT)).sum

      val w = e(WEIGHT)
      log.debug("Normalizing weight " + w + " to " + (w / normalizer))
      //      e(WEIGHT) = (w / (outStrength + inStrength)).asInstanceOf[AnyRef]
      e(WEIGHT) = (w / normalizer).asInstanceOf[AnyRef]
    }
    log.debug("Done with normalization of weights")

    graph
  }

  /**
   * Normalizes the weights of the graph by the node's in-strength (false) or out-strength (true) or simply by the
   * sum of the weights of the adjacent edges in the case of undirected graph.
   * @param g
   * @param normalizeByOutStrength true - normalization by the node's out-strenght, false - in-strenght; ignored for undir. graphs
   * @tparam V
   * @tparam E
   * @return
   */
  def normalizeWeights[V, E](g: AbstractBaseGraph[V, E], normalizeByOutStrength: Boolean) = {
    log.debug("Normalizing weights")
    for (v <- g.vertexSet()) {
      val edges = g match {
        case _: UndirectedGraph[_,_] => g.edgesOf(v)
        case _: DirectedGraph[_,_] => {
          if (normalizeByOutStrength) g.outgoingEdgesOf(v)
          else g.incomingEdgesOf(v)
        }
      }
      if (!edges.isEmpty) {
        val sum = edges.map(e => g.getEdgeWeight(e)).sum
        for (e <- edges) {
          val w = g.getEdgeWeight(e) / sum
          g.setEdgeWeight(e, w)
          log.debug("Normalizing weight of edge " + e + " to " + w)
        }
      }
    }
    g
  }

  def setWeights(p: Double, g: Graph) = {
    g.getEdges.foreach(e => e(WEIGHT) = p.asInstanceOf[AnyRef])
    g
  }


  def setWeights[V, E](weight: Double, g: AbstractBaseGraph[V, E]) = {
    g.edgeSet().foreach(e => g.setEdgeWeight(e, weight))
    g
  }

  def loadDirectedGraphFromFile(edgeList: File, delimiter: String = "\\s+") = {
    log.debug("Starting to parting the edgelist: " + edgeList.getAbsolutePath)
    val g = new SimpleDirectedWeightedGraph[Long, DefaultWeightedEdge](classOf[DefaultWeightedEdge])
    var l = 0
    for (line <- Source.fromFile(edgeList).getLines() if !line.startsWith("#") && !line.startsWith("%")) {
      val tokens = line.split(delimiter)
      val Array(source, sink, _*) = tokens
      if (source != sink) { // loops are not allowed in simple graphs
        val w =
          if (tokens.length > 2) tokens(2).toDouble
          else 1d
        g.addVertex(source.toLong)
        g.addVertex(sink.toLong)
        g.setEdgeWeight(g.addEdge(source.toLong, sink.toLong), w)
        l += 1
        if (l % 1000 == 0) log.debug("Loaded " + l + " edges")
      }
    }
    log.info("Loaded " + l + " edges altogether")
    g
  }

  def loadUndirectedGraphFromFile(edgeList: File, delimiter: String = "\\s+") = {
    log.debug("Starting to parting the edgelist: " + edgeList.getAbsolutePath)
    val g = new SimpleWeightedGraph[Long, DefaultWeightedEdge](classOf[DefaultWeightedEdge])
    var l = 0
    for (line <- Source.fromFile(edgeList).getLines() if !line.startsWith("#") && !line.startsWith("%")) {
      val tokens = line.split(delimiter)
      val Array(source, sink, _*) = tokens
      if (source != sink) { // loops are not allowed in simple graphs
        val w =
          if (tokens.length > 2) tokens(2).toDouble
          else 1d
        g.addVertex(source.toLong)
        g.addVertex(sink.toLong)
        g.setEdgeWeight(g.addEdge(source.toLong, sink.toLong), w)
        l += 1
        if (l % 1000 == 0) log.debug("Loaded " + l + " edges")
      }
    }
    log.info("Loaded " + l + " edges altogether")
    g
  }
}
