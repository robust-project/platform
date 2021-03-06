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

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 20/11/2011
 * Time: 20:33
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object Size extends FeatureExtractor {

  override val COMMAND_NAME = "compute-size"

  protected def processCluster(clusterDB: Cluster) {
    val cluster = new ClusterFromRDB(clusterDB)
    storeFeatureValue(clusterDB.id, FeatureType.SIZE.id, cluster.vertices.size)
  }
}