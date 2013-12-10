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

import org.apache.commons.math.linear.RealVector
import collection.Map
import ie.deri.uimr.crosscomanalysis.db.tables.graph.{Cluster => ClusterDB}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07-Dec-2010
 * Time: 14:16:31
 * ©2010 Digital Enterprise Research Institute, NUI Galway. 
 */

/**
 * @param featureIndex feature id -> feature index
 * @param featureVectors node id -> feature vector
 */
class SWIRCluster(c: ClusterDB,
                  fetIndices: Map[Long, Int],
                  fetVectors: Map[Long, RealVector]) extends ClusterFromRDB(c) with ClusterWithContent[Long] {

  val featureIndex: Map[Long, Int] = fetIndices
  val featureVectors: Map[Long, RealVector] = fetVectors

  def isIR = {
    val characterizingKeywords = highestRankedKeywords(20)
    if (characterizingKeywords.isDefined)
      characterizingKeywords.get.exists(_.matches(".*(information retriev)|(ir)|(retriev).*"))
    else false
  }

  def isSW = {
    val characterizingKeywords = highestRankedKeywords(20)
    if (characterizingKeywords.isDefined)
      characterizingKeywords.get.exists(_.matches(".*(semantic web)|(ontolog)|(rdf)|(resource description framework)|(linked data).*"))
    else false
  }

  override def toString = super.toString + (if (isSW && isIR) "\n*" else "")
}