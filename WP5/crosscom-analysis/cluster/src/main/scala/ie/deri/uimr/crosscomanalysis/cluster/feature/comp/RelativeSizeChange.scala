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

import collection.mutable.{HashMap, HashSet}
import ie.deri.uimr.crosscomanalysis.db.tables.graph.Cluster
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/04/2012
 * Time: 11:21
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes and stores change of community size as a a fraction (total users in t+1)/(total users in t).
 */

object RelativeSizeChange extends FeatureExtractor {

  override val COMMAND_NAME = "compute-rel-size-change"

  private val previousSizes = new HashMap[Int, Int] // cluster index -> previous size
  private val activeClusters = new HashSet[Int] // set of cluster indices that have been active this snapshot

  protected def processCluster(clusterDB: Cluster) {
    activeClusters += clusterDB.index
    val cluster = new ClusterFromRDB(clusterDB)
    val prevSize = previousSizes.get(clusterDB.index)
    val featureValue =
      if (prevSize.isDefined)
        cluster.vertices.size / prevSize.get.toDouble
      else
        0d
    previousSizes(clusterDB.index) = cluster.vertices.size
    log.debug("Size change of cluster " + cluster + " with previous size: " + prevSize + ": " + featureValue)
    storeFeatureValue(clusterDB.id, FeatureType.RELATIVE_SIZE_CHANGE.id, featureValue)
  }

  override protected def cleanUpAfterSlice(sliceId: Long) {
    // clean all sizes of clusters that have not been active in this slice (that have disappeared)
    val disappearedClusters = previousSizes.keySet -- activeClusters
    log.debug("Removing " + disappearedClusters + " disappeared clusters")
    disappearedClusters.foreach(previousSizes.remove(_))
    activeClusters.clear()
  }
}