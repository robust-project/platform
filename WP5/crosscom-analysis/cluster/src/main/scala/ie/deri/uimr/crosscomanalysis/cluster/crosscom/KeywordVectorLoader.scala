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

import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import collection.mutable.HashMap
import org.apache.commons.math.linear.{OpenMapRealVector, RealVector}
import org.squeryl.PrimitiveTypeMode._
import collection.Map
import ie.deri.uimr.crosscomanalysis.util.Logging

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 13/03/2011
 * Time: 17:55
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

class KeywordVectorLoader(db: String, val normalize: Boolean = true) extends SessionFactorySetter with Logging {
  setUpSessionFactory(db)

  val keywordIndex = retrieveKeywordIndex

  def loadVectorsForSlice(sliceId: Long) = {
     if (normalize) normalizeVectors(retrieveFeatureVectors(sliceId)) else retrieveFeatureVectors(sliceId)
  }

  private def retrieveFeatureVectors(sliceId: Long): Map[Long, RealVector] = {
    val fv = new HashMap[Long, RealVector]
    inTransaction {
      for (rka <- from(rankedKeywordsObjects)(rka => where(rka.sliceId === sliceId) select (rka))) {
        if (!fv.contains(rka.objectid))
          fv(rka.objectid) = new OpenMapRealVector(keywordIndex.size)
        fv(rka.objectid).setEntry(keywordIndex(rka.keywordid), rka.tfidf)
      }
    }
    if (fv.isEmpty) log.warn("No feature vectors retrieved for slice " + sliceId)
    fv
  }

  private def normalizeVectors(featureVectors: Map[Long, RealVector]) = {
    for (fv <- featureVectors.valuesIterator if fv.getNorm > 0) {
      fv.mapDivideToSelf(fv.getNorm)
    }
    featureVectors
  }

  private def retrieveKeywordIndex: Map[Long, Int] = {
    val ki = new HashMap[Long, Int]
    inTransaction {
      (from(keywordId)(kId => select(kId.id))).foreach(id => ki(id) = ki.size)
    }
    if (ki.isEmpty) log.warn("No keyword indices loaded!")
    ki
  }
}