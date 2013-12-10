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

package crosscom

import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster
import ie.deri.uimr.crosscomanalysis.cluster.comparator.ClusterMatcher
import ie.deri.uimr.crosscomanalysis.util.Functions.jaccardSim

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 29/03/2011
 * Time: 16:14
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object OverlapTestData extends ClusterMatcher[Cluster[Int]] {

  val FORMER = List(Set(1, 2), Set(3, 4), Set(5, 6, 7))
  val LATER = List(Set(1, 2, 3), Set(3, 4), Set(4), Set(4, 5, 6), Set(5, 6, 7))


  val FORMER_CLUSTERS = FORMER.map(vertSet => Cluster(origIndex, vertSet))

  val LATER_CLUSTERS = {
    val laterClusters = LATER.map(vertSet => Cluster(origIndex, vertSet))
    val matching = matchClusters(FORMER_CLUSTERS.toSet, laterClusters.toSet)
    for (lc <- laterClusters) {
      val matched = matching.get(lc)
      if (matched.isDefined)
        lc.index = matched.get.index
    }
    laterClusters
  }

  protected def clusterSim(c1: Cluster[Int], c2: Cluster[Int]) = jaccardSim(c1.vertices, c2.vertices)

  private var lastOrigIndex = -1

  private def origIndex = {
    lastOrigIndex += 1
    lastOrigIndex
  }
}