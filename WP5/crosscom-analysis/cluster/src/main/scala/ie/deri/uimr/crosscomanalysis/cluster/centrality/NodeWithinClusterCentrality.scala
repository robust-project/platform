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

package ie.deri.uimr.crosscomanalysis.cluster.centrality

import collection.Map
import ie.deri.uimr.crosscomanalysis.cluster.model.{Cluster, ClusterWithSubgraph}


/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/11/2011
 * Time: 17:38
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

abstract class NodeWithinClusterCentrality[C <: Cluster[Long] with ClusterWithSubgraph] {
  /**
   * @param cluster Cluster for which we want to compute the centralities of the nodes.
   * @return Mapping vertexid -> centrality
   */
  def compute(cluster: C): Map[Long,Double]
}