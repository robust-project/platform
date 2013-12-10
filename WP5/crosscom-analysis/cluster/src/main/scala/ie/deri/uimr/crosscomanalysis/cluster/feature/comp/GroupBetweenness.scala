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
import ie.deri.uimr.crosscomanalysis.jung.algorithms.scoring.SuccessiveGroupBetweenness
import ie.deri.uimr.crosscomanalysis.graphdb.jung.JungDBGraphLoader
import edu.uci.ics.jung.graph.{UndirectedSparseGraph, Graph}
import collection.JavaConversions.{mapAsJavaMap, setAsJavaSet}
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/09/2011
 * Time: 17:07
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object GroupBetweenness extends FeatureExtractor {

  override val COMMAND_NAME = "compute-gbc"

  private var sgb: SuccessiveGroupBetweenness[Long,Long] = _

  protected def processCluster(clusterDB: Cluster) {
    val cluster = new ClusterFromRDB(clusterDB)
    val gb = sgb.getVertexGroupScore(cluster.vertices)
    val normGB = sgb.getNormalizedGBC(cluster.vertices, gb, false)
    log.debug("Storing group betweenness for cluster " + cluster + ": " + gb)
    storeFeatureValue(clusterDB.id, FeatureType.GROUP_BETWEENNESS.id, gb)
    log.debug("Storing normalized group betweenness for cluster " + cluster + ": " + normGB)
    storeFeatureValue(clusterDB.id, FeatureType.NORM_GROUP_BETWEENNESS.id, normGB)
  }

  override protected def computeSliceStats(sliceId: Long) {
    val g = new UndirectedSparseGraph[Long,Long]
    val graphLoader = new JungDBGraphLoader[Graph[Long,Long]](sliceId, g, true)
    log.debug("About to create SGB instance ...")
    sgb = new SuccessiveGroupBetweenness[Long, Long](g,
      Conversions.mapToTransformer(graphLoader.weights.mapValues(_.toDouble)), false)
    log.debug("... SGB instantiated")
  }
}