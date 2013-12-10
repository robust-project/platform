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

package ie.deri.uimr.crosscomanalysis.cluster.exporters.commgraph

import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import collection.mutable.HashMap
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.{ClusterVertexOverlap, ClusterJaccardOverlap}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/11/2011
 * Time: 11:51
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class UndirectedCommunityGraph(db:String, clusterFormat: Int, clusterFlag: Option[String], sliceId: Long, val jaccard: Boolean)
  extends CommunityGraph(db, clusterFormat, clusterFlag, sliceId) {
  /**
   * @return Weights of an undirected community graph representing Jaccard similarity or just their overlap sizes between the communities' nodes.
   */
  protected def weights(clusters: collection.Set[ClusterFromRDB]) = {
    val overlaps =
      if (jaccard)
        new ClusterJaccardOverlap[Long].clustersOverlaps(clusters, clusters)
      else
        new ClusterVertexOverlap[Long].clustersOverlaps(clusters, clusters)
    val weights = new HashMap[ClusterFromRDB, HashMap[ClusterFromRDB, Double]]
    // turn the overlaps into a set of weights of an undirected graph
    for (firstComm <- clusters; (secondComm, overlap) <- overlaps(firstComm) if overlap > 0 && firstComm != secondComm) {
      if (!weights.contains(firstComm)) {
        weights(firstComm) = new HashMap[ClusterFromRDB, Double]
      }
      if (!weights(firstComm).contains(secondComm)) {
        // edge from 1st to 2nd does not exist, check if it also does not exist in the opposite direction (we want undir graph)
        if (!(weights.contains(secondComm) && weights(secondComm).contains(firstComm))) {
          weights(firstComm)(secondComm) = overlap
        }
      }
    }
    weights
  }
}