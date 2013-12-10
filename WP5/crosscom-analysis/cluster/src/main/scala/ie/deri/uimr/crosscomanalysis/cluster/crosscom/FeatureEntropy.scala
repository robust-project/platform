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

import org.apache.commons.math.linear.RealVector
import collection.Map
import ie.deri.uimr.crosscomanalysis.util.Functions.cosineDistance
import org.apache.commons.math.util.MathUtils.log
import collection.mutable.HashMap
import ie.deri.uimr.crosscomanalysis.cluster.model.{ClusterWithContent, Cluster, SWIRCluster}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/12/10
 * Time: 17:37
 * ©2010 Digital Enterprise Research Institute, NUI Galway. 
 */

class FeatureEntropy(val featureIndex: Map[Long, Int], val featureVectors: Map[Long, RealVector],
                     val featureVectorsCurrent: Map[Long, RealVector]) {

  private val probabilities = probabilityMap

  def clusterEntropy(c: Cluster[Long] with ClusterWithContent[Long]) = {
    var entropy = 0d
    var count = 0
    for (n <- c.vertices if featureVectorsCurrent.contains(n)) {
      val p = probabilities(featureVectorsCurrent(n))
      entropy += (if (p > 0) -1 * p * log2(p) else 0)
      count += 1
    }

    if (count > 0) entropy / count
    else 0d
  }

  private def probabilityMap: Map[RealVector, Double] = {
    val vectorSimilarities = new HashMap[RealVector, Double]
    var totalSim = 0d

    for (v1 <- featureVectors.valuesIterator; v2 <- featureVectorsCurrent.valuesIterator) {
      val cos = cosineDistance(v1, v2)
      totalSim += cos
      if (vectorSimilarities.contains(v2))
        vectorSimilarities(v2) = vectorSimilarities(v2) + cos
      else
        vectorSimilarities(v2) = cos
    }

    vectorSimilarities.map(e => (e._1, e._2 / totalSim))
  }

  //  private def aboveThreshold(v1: RealVector, v2: RealVector) = cosineDistance(v1, v2) >= epsilon

  private def log2(x: Double) = log(2, x)
}