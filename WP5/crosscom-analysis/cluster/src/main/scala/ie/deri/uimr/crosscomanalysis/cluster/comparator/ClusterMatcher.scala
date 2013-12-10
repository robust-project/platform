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

package ie.deri.uimr.crosscomanalysis.cluster.comparator

import collection.mutable.HashMap
import collection.Map
import collection.immutable.Set
import ie.deri.uimr.crosscomanalysis.util.Functions.jaccardSim

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 11/02/2011
 * Time: 11:24
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait ClusterMatcher[C] {

  protected var matchingThreshold = 0.2

  protected def clusterSim(c1: C, c2: C): Double

  /**
   * Matches formerClusters (e.g. former ones) with laterClusters (e.g. later ones). Uses Hungarian algorithm.
   * @param formerClusters former clusters
   * @param laterClusters later clusters
   * @return mapping laterClusters->formerClusters
   */
  def matchClusters(formerClusters: Set[C], laterClusters: Set[C]): Map[C, C] = {
    val matching = new HashMap[C,C]
    val formerAsSeq = formerClusters.toSeq
    val laterAsSeq = laterClusters.toSeq
    val n = math.max(formerAsSeq.size, laterAsSeq.size)
    val bpMatcher = new BipartiteMatcher(n)

    for (f <- 0 until n; l <- 0 until n) {
      val sim =
        if (f <= formerAsSeq.size - 1 && l <= laterAsSeq.size - 1)
          clusterSim(formerAsSeq(f), laterAsSeq(l))
        else Double.NegativeInfinity
      if (sim >= matchingThreshold)
        bpMatcher.setWeight(f, l, sim)
      else
        bpMatcher.setWeight(f, l, Double.NegativeInfinity)
    }
    val assignmentMatrix = bpMatcher.getMatching
    for (f <- 0 until n if assignmentMatrix(f) > -1)
      matching(laterAsSeq(assignmentMatrix(f))) = formerAsSeq(f)

    matching
  }

  /**
   * Matches formerClusters (e.g. former ones) with laterClusters (e.g. later ones). Uses greedy algorithm.
   * @param formerClusters former clusters
   * @param laterClusters later clusters
   * @return mapping laterClusters->formerClusters
   */
  def matchClustersGreedy(formerClusters: Set[C], laterClusters: Set[C]): Map[C, C] = {
    val matching = new HashMap[C, C]

    def adjustMatching(current: C, unavailMappings: List[C]) {
      val mostSim = (formerClusters -- unavailMappings).toSeq.sorted(relativeOrdering(current))
      if (!mostSim.isEmpty) {
        // check if the most similar is already matched
        val mostSimilarC = matching.find(pair => pair._2 == mostSim.head)
        if (mostSimilarC.isDefined) {
          val (collidingKey, collidingValue) = mostSimilarC.get
          // the most similar is colliding with another already matched cluster - compare which one is more similar
          if (clusterSim(mostSim.head, collidingValue) >= clusterSim(mostSim.head, current)) {
            // the similarity of the current mapping is higher, find another one for cluster 'current'
            adjustMatching(current, mostSim.head :: unavailMappings)
          } else {
            // the similarity of the current mapping is lower, find another one for it
            matching(current) = mostSim.head
            matching.remove(collidingKey)
            adjustMatching(collidingKey, mostSim.head :: unavailMappings)
          }
        } else if (clusterSim(mostSim.head, current) >= matchingThreshold) {
          matching(current) = mostSim.head
        }
      }
    }

    for (laterC <- laterClusters) {
      adjustMatching(laterC, Nil)
    }

    matching
  }

  private def relativeOrdering(relativeTo: C) = {
    new Ordering[C] {
      def compare(x: C, y: C) = (clusterSim(relativeTo, y) - clusterSim(relativeTo, x)) match {
        case d if d > 0 => 1
        case d if d < 0 => -1
        case _ => 0
      }
    }
  }
}