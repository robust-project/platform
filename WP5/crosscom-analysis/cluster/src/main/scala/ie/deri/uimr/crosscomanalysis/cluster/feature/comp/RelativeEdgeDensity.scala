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

package ie.deri.uimr.crosscomanalysis.cluster.feature.comp

import ie.deri.uimr.crosscomanalysis.graphdb.importers.DBGraphImporter
import ie.deri.uimr.crosscomanalysis.cluster.model.{ClusterWithSubgraph, ClusterFromRDB}
import com.tinkerpop.blueprints.pgm.{Edge, Graph}
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.{GraphProperty, GraphProperties}
import ie.deri.uimr.crosscomanalysis.db.tables.graph.Cluster
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType._
import GraphProperties._
import collection.Map
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/09/2011
 * Time: 19:02
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes relative edge density (considers the graph to be undirected as it is not clear how to compute this measure
 * for directed graphs. It stores the computed values into database.
 */

object RelativeEdgeDensity extends FeatureExtractor with DBGraphImporter {

  override val COMMAND_NAME = "compute-relative-edge-density"

  private var lastGraph: Graph = _
  private var weights: Map[Edge, Double] = _
  private var edgeType: GraphProperty[_] = _
  //  private var directed = true

  override def main(args: Array[String]) {
    mainStub(args) {
      edgeType = GraphProperties(getOptValue(EDGE_TYPE_OPT))
      //      directed = hasOption(DIRECTED_OPT)
      compute()
    }
  }

  protected def processCluster(clusterDB: Cluster) {
    // compute the measure
    val cluster = new ClusterFromRDB(clusterDB) with ClusterWithSubgraph {
      val graph = lastGraph
    }
    log.debug("Processing cluster " + cluster)
    val clusterEdges = cluster.vertices.map(lastGraph.getVertex(_)).map(v =>
      (v.getInEdges(edgeType.name).toList ++ v.getOutEdges(edgeType.name).toList)).flatten.toList
    def isIntEdge(e: Edge) = {
      val source = e.getOutVertex()(ORIGID)
      val sink = e.getInVertex()(ORIGID)
      val int = cluster.vertices.contains(source) && cluster.vertices.contains(sink)
      log.debug("Source: " + source + ", sink: " + sink + " are internal?: " + int)
      int
    }
    val intEdges = clusterEdges.filter(isIntEdge(_))
    val intDegree = intEdges.map(weights(_)).sum
    log.debug("Internal degree of " + intEdges.size + " edges is " + intDegree)
    val totalDegree = clusterEdges.map(weights(_)).sum
    log.debug("Total degree of " + clusterEdges.size + " edges is " + totalDegree)
    // internal degree is multiplied by two in case of undirected graph
    val relEdgeDens = intDegree / totalDegree
    // store the measure
    storeFeatureValue(clusterDB.id, RELATIVE_EDGE_DENSITY.id, relEdgeDens)
    log.debug("Relative edge density for cluster " + cluster + " is " + relEdgeDens + ". Stored.")
  }


  override protected def computeSliceStats(sliceId: Long) {
    // load new slice as a graph
    val (g, w) = loadGraph(sliceId, edgeType)
    lastGraph = g
    weights = w
  }

  override protected def commandLineOptions = EDGE_TYPE_OPT :: super.commandLineOptions
}