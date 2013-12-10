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

import ie.deri.uimr.crosscomanalysis.util.Logging
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.MutableList
import ie.deri.uimr.crosscomanalysis.db.tables.cocit.RankedKeywordsObjects
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import ie.deri.uimr.crosscomanalysis.db.SliceIterator
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.rankedKeywordsObjects
import java.lang.Math.log10
import collection.parallel.ParSeq

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/03/2012
 * Time: 19:09
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * Implements the keyword TF-IDF ranking using parallel collections - may be quite greedy, because it loads first all the
 * keywords for a slice into memory and then converts it into a parallel collection.
 */

abstract class ParallelKeywordRanker extends KeywordRanker with Logging {

  def rank(sliceType: Int) {
    log.debug("Ranking slice type " + SliceTypes(sliceType))
    transaction {
      for (slice <- new SliceIterator(sliceType)) {
        log.info("Ranking " + slice)
        scoreAndStore(keywordsForSlice(slice.beginDate, slice.endDate).toSeq.par, slice.id)
      }
    }
    log.debug("Ranking complete")
  }

  /**
   * @param keywords is a sequence of triples (keywordid, objectid, # occurrences)
   */
  protected def scoreAndStore(keywords: ParSeq[(Long, Long, Int)], sliceId: Long) {
    // first group by (keywordid, objectid), then map values to occurrences, then sum up the occurrences
    // resulting map is (keywordid, objectid)->#occurrences
    val objectKeywords = keywords.groupBy(triple => (triple._1, triple._2)).map(p => (p._1, p._2.map(_._3).sum))
    // group first by the objects (the result is map objectid->((keywordid, objectid)->occurrences)
    // then map the values of the result just to occurrences and sum them up
    // resulting map is (objectid -> # occurrences of all keywords)
    val objectAllKeywordOcc = objectKeywords.groupBy(_._1._2).map(p => (p._1, p._2.seq.values.sum))
    // group by keywordid, and then count how many times the keyword was assigned to an object (ignoring occurrences)
    // resulting map is (keywordid-> # unique objects assigned at least one with the keyword)
    val keywordOccurrencePerObject = keywords.groupBy(_._1).map(p => (p._1, p._2.map(_._2).toSet.size))

    def getTFIDF(objectid: Long, keywordid: Long): Double = {
      (objectKeywords((keywordid, objectid)) / objectAllKeywordOcc(objectid).toDouble) * // TF
        log10(objectAllKeywordOcc.size / keywordOccurrencePerObject(keywordid).toDouble) // IDF
    }

    // rank and store the keywords
    val rankedKeywordsBatch = new MutableList[RankedKeywordsObjects]
    for (((keywordId, objectId), occurrences) <- objectKeywords.seq) {
      rankedKeywordsBatch += new RankedKeywordsObjects(keywordId, getTFIDF(objectId, keywordId), sliceId, objectId)
      if (rankedKeywordsBatch.size % 1000 == 0)
        store(rankedKeywordsBatch)
    }
    if (!rankedKeywordsBatch.isEmpty)
      store(rankedKeywordsBatch) // store the remains of the batch
  }

  protected def store(rankedKeywordsBatch: MutableList[RankedKeywordsObjects]) {
    transaction {
      rankedKeywordsObjects insert rankedKeywordsBatch
      rankedKeywordsBatch.clear()
    }
  }
}