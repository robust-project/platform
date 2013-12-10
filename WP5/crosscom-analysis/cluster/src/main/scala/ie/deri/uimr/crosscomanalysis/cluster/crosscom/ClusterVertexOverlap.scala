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

package ie.deri.uimr.crosscomanalysis.cluster.crosscom

import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 22/02/2011
 * Time: 15:49
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

class ClusterVertexOverlap[V] extends ClusterOverlap[V] {
  def clusterOverlap[C <: Cluster[V]](cluster1: C, cluster2: C) = cluster1.vertices.intersect(cluster2.vertices).size
}