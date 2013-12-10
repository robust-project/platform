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

package ie.deri.uimr.crosscomanalysis.cluster.model

import collection.Map
import ie.deri.uimr.crosscomanalysis.util.Functions.cosineDistance
import org.apache.commons.math.linear.{OpenMapRealVector, RealVector}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 22/02/2011
 * Time: 20:28
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait ClusterWithFeatures[V] {

  this: Cluster[V] =>

  val featureIndex: Map[Long, Int]
  val featureVectors: Map[V, RealVector]

  lazy val centroid = computeCentroid
  var featureEntropy: Option[Double] = None
  lazy val clusterFeatures = featureVectors.filterKeys(vertices.contains(_)).values.toSeq

  /**
   * Cluster/Feature ratio (or: Conor's Cluster Quality:])
   */
  def CCRatio(sliceCentroid: RealVector): Double = {
    var intraClusterSim: Double = 0
    for (v <- clusterFeatures) {
      intraClusterSim += cosineDistance(v, centroid)
    }
    intraClusterSim /= vertices.size
    val interClusterSim = cosineDistance(centroid, sliceCentroid)

    if (interClusterSim > 0) intraClusterSim / interClusterSim
    else 0
  }

  def clusterFeatureCohesiveness =
    if (clusterFeatures.size > 1) {
      var sim = 0d
      val features = clusterFeatures.toIndexedSeq
      for (i <- 0 until features.size; j <- (i + 1) until features.size) {
        sim += cosineDistance(features(i), features(j))
      }

      // normalize by number of elements of the upper triangular matrix without diagonal elements (n^2 - n)/2
      sim * 2 / (features.size * features.size - features.size)
    } else {
      0
    }

  private def computeCentroid = {
    var c: RealVector = new OpenMapRealVector(featureIndex.size)
    for (fv <- clusterFeatures) {
      c = c.add(fv)
    }
    c.mapDivideToSelf(vertices.size)

    c
  }

  def cosineSimilarity(that: Cluster[V] with ClusterWithFeatures[V]) = cosineDistance(this.centroid, that.centroid)

  override def toString = super.toString + (if (featureEntropy.isDefined) "\n H=" + featureEntropy.get else "")
}