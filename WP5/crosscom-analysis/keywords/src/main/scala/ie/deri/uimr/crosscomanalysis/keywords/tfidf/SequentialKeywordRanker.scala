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

package ie.deri.uimr.crosscomanalysis.keywords.tfidf

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import collection.mutable.{MutableList, HashSet, HashMap}
import java.lang.Math.log10
import ie.deri.uimr.crosscomanalysis.db.tables.cocit.RankedKeywordsObjects
import ie.deri.uimr.crosscomanalysis.keywords.model.Keyword
import java.util.Date
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 04-Dec-2010
 * Time: 21:01:31
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 *
 * Ranks keywords per object (author, document) and per slice (slice is a scope).
 *
 * Space complexity
 *
 * nk - number of keywords
 * no - number of objects
 * sl - size of long (bytes)
 * sk - size of keyword object = 8 + 4 + 8 (object overhead)
 *
 * no * (sk * 250 + 3*sl) + nk * (sk + sl)
 */

abstract class SequentialKeywordRanker extends KeywordRanker with Logging {

  // map object (author, mail, ...) ->set of keywords
  private val objectKeywords = new HashMap[Long, HashSet[Keyword]]
  // map object->number of all occurrences of all keywords
  private val objectAllKeywordOcc = new HashMap[Long, Long]
  // map keyword->number of authors assigned with the keyword
  private val keywordOccurrencePerObject = new HashMap[Keyword, Long]

  def rank(sliceType: Int) {
    log.debug("Ranking slice type " + SliceTypes(sliceType))
    transaction {
      for ((begin, end, sliceId) <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select ((ns.beginDate, ns.endDate, ns.id)))) {
        log.info("Ranking slice #" + sliceId + " in " + begin + " and " + end)
        for ((keywordId, objectId, occurrences) <- keywordsForSlice(begin, end)) {
          processKeyword(keywordId, objectId, occurrences)
        }
        // rank and store the keywords
        val rankedKeywordsBatch = new MutableList[RankedKeywordsObjects]
        for ((objectid, keywordSet) <- objectKeywords; keyword <- keywordSet) {
          rankedKeywordsBatch += new RankedKeywordsObjects(keyword.id, getTFIDF(objectid, keyword), sliceId, objectid)
          if (rankedKeywordsBatch.size % 1000 == 0) {
            transaction {
              rankedKeywordsObjects insert rankedKeywordsBatch
              rankedKeywordsBatch.clear()
            }
          }
        }
        if (!rankedKeywordsBatch.isEmpty) {
          transaction {
            rankedKeywordsObjects insert rankedKeywordsBatch // store the remains of the batch
          }
        }
        resetStats()
      }
    }
    log.debug("Ranking complete")
  }

  private def getTFIDF(objectid: Long, keyword: Keyword): Double = {
    (keyword.frequency / objectAllKeywordOcc(objectid).toDouble) * // TF
      log10(objectKeywords.size / keywordOccurrencePerObject(keyword).toDouble) // IDF
  }


  private def processKeyword(keywordId: Long, objectId: Long, frequency: Int = 1) {
    val keyword = new Keyword(keywordId, frequency.toInt)

    if (!objectKeywords.contains(objectId)) {
      objectKeywords(objectId) = new HashSet[Keyword]
    }
    val keywordSet = objectKeywords(objectId)
    val existingKeyword = keywordSet.find(_ == keyword)

    if (existingKeyword == None) {
      keywordSet.add(keyword)
      if (keywordOccurrencePerObject.contains(keyword)) {
        keywordOccurrencePerObject(keyword) = (keywordOccurrencePerObject(keyword) + 1)
      } else {
        keywordOccurrencePerObject(keyword) = 1
      }
    } else {
      keyword.incrementFrequency(frequency)
    }

    // increment all keyword occurrences per author
    if (objectAllKeywordOcc.contains(objectId)) {
      objectAllKeywordOcc(objectId) = (objectAllKeywordOcc(objectId) + frequency)
    } else {
      objectAllKeywordOcc(objectId) = frequency
    }
  }

  private def resetStats() {
    objectKeywords.clear()
    objectAllKeywordOcc.clear()
    keywordOccurrencePerObject.clear()
  }
}