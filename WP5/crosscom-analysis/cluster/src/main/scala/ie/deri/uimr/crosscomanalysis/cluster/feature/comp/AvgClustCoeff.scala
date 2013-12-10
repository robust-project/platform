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

import ie.deri.uimr.crosscomanalysis.db.tables.graph.Cluster
import edu.uci.ics.jung.graph.{Graph, UndirectedSparseGraph}
import ie.deri.uimr.crosscomanalysis.graphdb.jung.JungDBGraphLoader
import edu.uci.ics.jung.algorithms.metrics.Metrics
import java.util.{Map => JavaMap}
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/09/2011
 * Time: 11:27
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes average clustering coefficients per cluster and then stores them into the database. Note that the average
 * is computed from individual clustering coefficient and there's no guarantee that only the edges _within_ the cluster
 * are considered. The current implementation treats the graph as undirected and unweighted.
 */

object AvgClustCoeff extends FeatureExtractor {

  override val COMMAND_NAME = "compute-avg-cc"

  private var clustCoeffCurrentSlice: JavaMap[Long, java.lang.Double] = _

  protected def processCluster(clusterDB: Cluster) {
    // compute the average for the cluster
    val cluster = new ClusterFromRDB(clusterDB)
    val avgCC = cluster.vertices.map(clustCoeffCurrentSlice.get(_).doubleValue()).sum / cluster.vertices.size
    log.debug("Avg. CC for cluster " + cluster + " is " + avgCC)
    storeFeatureValue(clusterDB.id, FeatureType.AVG_CLUST_COEFF.id, avgCC)
  }

  override protected def computeSliceStats(sliceId: Long) {
    // compute clust. coeff. for the new slice
    val g: Graph[Long, Long] = new UndirectedSparseGraph[Long, Long]
    val graphLoader = new JungDBGraphLoader[Graph[Long, Long]](sliceId, g, true)
    clustCoeffCurrentSlice = Metrics.clusteringCoefficients(g)
  }
}