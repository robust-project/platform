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
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/09/2011
 * Time: 12:31
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes and stores changes of sizes of clusters as an indicator of their health - -1 means very rapid decline in size
 * whereas 1 means very high influx of new members. 0 means stability/no change.
 */

object SizeChange extends FeatureExtractor {

  override val COMMAND_NAME = "compute-size-change"

  private val previousSizes = new HashMap[Int, Int] // cluster index -> previous size
  private val activeClusters = new HashSet[Int] // set of cluster indices that have been active this snapshot
  /*
  Parameter k influences how much the size change influences the health score. Value 2 means than if the cluster
  grows by the factor of two, then it is already considered as super healthy (~>1), or if it lowers by the same factor,
  it is considered as very unhealthy (~>-1), while no change means neutral health (0). Higher factors demands higher changes
  while lower factors increase the sensitivity to change.
   */
  private val k: Double = 2

  protected def processCluster(clusterDB: Cluster) {
    activeClusters += clusterDB.index
    val cluster = new ClusterFromRDB(clusterDB)
    val prevSize = previousSizes.get(clusterDB.index)
    val featureValue =
      if (prevSize.isDefined)
        math.tanh((cluster.vertices.size - prevSize.get) * 4 * k / math.max(cluster.vertices.size, prevSize.get))
      else
        0
    previousSizes(clusterDB.index) = cluster.vertices.size
    log.debug("Size change of cluster " + cluster + " with previous size: " + prevSize + ": " + featureValue)
    storeFeatureValue(clusterDB.id, FeatureType.SIZE_CHANGE.id, featureValue)
  }

  override protected def cleanUpAfterSlice(sliceId: Long) {
    // clean all sizes of clusters that have not been active in this slice (that have disappeared)
    val disappearedClusters = previousSizes.keySet -- activeClusters
    log.debug("Removing " + disappearedClusters + " disappeared clusters")
    disappearedClusters.foreach(previousSizes.remove(_))
    activeClusters.clear()
  }
}