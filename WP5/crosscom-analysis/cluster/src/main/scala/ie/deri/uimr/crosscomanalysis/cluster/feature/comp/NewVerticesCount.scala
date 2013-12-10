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
import collection.mutable.{HashSet, HashMap}
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType.NEW_VERTICES_COUNT
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 16/11/2011
 * Time: 14:55
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object NewVerticesCount extends FeatureExtractor {
  override val COMMAND_NAME = "new-vertices-count"
  private val pastMembers = new HashMap[Int, HashSet[Long]] // cl. index -> all previously active members

  protected def processCluster(clusterDB: Cluster) {
    val cluster = new ClusterFromRDB(clusterDB)
    val past = pastMembers.get(cluster.index)
    storeFeatureValue(clusterDB.id, NEW_VERTICES_COUNT.id, (cluster.vertices -- (if (past.isDefined) past.get else cluster.vertices.empty)).size)
    if (past.isEmpty)
      pastMembers(cluster.index) = new HashSet[Long]
    pastMembers(cluster.index) ++= cluster.vertices
  }
}