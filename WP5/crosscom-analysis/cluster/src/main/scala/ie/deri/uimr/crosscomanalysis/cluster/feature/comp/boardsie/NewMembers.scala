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

package ie.deri.uimr.crosscomanalysis.cluster.feature.comp.boardsie

import ie.deri.uimr.crosscomanalysis.cluster.feature.comp.FeatureExtractor
import ie.deri.uimr.crosscomanalysis.db.tables.graph.Cluster
import collection.{Map,Set}
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/04/2012
 * Time: 14:25
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes number of members of a community, who have not been active in the community previously.
 */

object NewMembers extends FeatureExtractor {

  override val COMMAND_NAME = "compute-new-members"

  // community index -> set of members that have been at least once active until the last period
  private var previousCommunityMembers = Map.empty[Int, Set[Long]]
  private var currentCommunityMembers = Map.empty[Int, Set[Long]]


  protected def processCluster(clusterDB: Cluster) {
    val cluster = new ClusterFromRDB(clusterDB)
    log.debug(cluster + " has " + cluster.vertices.size + " members")
    val prevMembers = previousCommunityMembers.getOrElse(cluster.index, cluster.vertices.empty)
    log.debug(cluster + " had had " + prevMembers.size + " members until the last snapshot")
    storeFeatureValue(clusterDB.id, FeatureType.NEW_MEMBERS.id, (cluster.vertices -- prevMembers).size)
    log.debug("Storing " + (cluster.vertices -- prevMembers).size + " as a count of new members")
    currentCommunityMembers = currentCommunityMembers + (cluster.index -> (prevMembers ++ cluster.vertices))
  }

  /**
   * By-default empty hook for cleaning-up after all clusters in a slice have been processed
   */
  override protected def cleanUpAfterSlice(sliceId: Long) {
    previousCommunityMembers = currentCommunityMembers
    currentCommunityMembers = currentCommunityMembers.empty
  }
}
