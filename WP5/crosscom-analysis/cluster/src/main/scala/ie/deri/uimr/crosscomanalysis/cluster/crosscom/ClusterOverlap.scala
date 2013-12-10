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

import collection.{Set, Map}
import collection.mutable.HashMap
import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 22/02/2011
 * Time: 15:48
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

abstract class ClusterOverlap[V] {

  def clusterOverlap[C <: Cluster[V]](formerCluster: C, laterCluster: C): Double

  /**
   * @return map formerCluster X laterCluster -> overlap
   */
  def clustersOverlaps[C <: Cluster[V]](formerClusters: Set[C], laterClusters: Set[C]): Map[C, Map[C, Double]] = {
    val m = new HashMap[C, HashMap[C, Double]]
    for (c1 <- formerClusters; c2 <- laterClusters) {
      if (!m.contains(c1)) m(c1) = new HashMap[C, Double]
      m(c1)(c2) = clusterOverlap(c1, c2)
    }
    m
  }
}