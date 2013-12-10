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
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema.networkSliceStructure

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/09/2011
 * Time: 12:29
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes relative sizes of the clusters (relative to the size of the whole slice).
 */

object RelativeSize extends FeatureExtractor {

  override val COMMAND_NAME = "compute-rel-size"

  private var sizeOfCurrentSlice: Int = _

  protected def processCluster(clusterDB: Cluster) {
    val cluster = new ClusterFromRDB(clusterDB)
    log.debug("Storing size of cluster " + cluster + ": " + cluster.vertices.size / sizeOfCurrentSlice.toDouble)
    storeFeatureValue(clusterDB.id, FeatureType.RELATIVE_SIZE.id, cluster.vertices.size / sizeOfCurrentSlice.toDouble)
  }

  override protected def computeSliceStats(sliceId: Long) {
    // recompute the total size of the slice
    sizeOfCurrentSlice = from(networkSliceStructure) (nss =>
      where(nss.sliceId === sliceId)
      select((nss.source, nss.sink))).map(p => Set(p._1, p._2)).flatten.toSet.size
  }
}