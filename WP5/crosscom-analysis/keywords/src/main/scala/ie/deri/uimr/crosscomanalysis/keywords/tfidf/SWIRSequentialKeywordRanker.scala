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

import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import java.util.{Calendar, Date}
import ie.deri.uimr.crosscomanalysis.util.Functions.stem
import ie.deri.uimr.crosscomanalysis.keywords.tfidf.StopWords.isStopWord
import collection.mutable.MutableList
import ie.deri.uimr.crosscomanalysis.keywords.KeywordArgsParser

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 19/07/2011
 * Time: 17:32
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object SWIRSequentialKeywordRanker extends SequentialKeywordRanker with SessionFactorySetter with DBArgsParser with KeywordArgsParser {

  override val COMMAND_NAME = "rank-swirkeywords"
  private var tokenize = false

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      this.tokenize = hasOption(TOKENIZE_OPT)
      setUpSessionFactory(getOptValue(DB_OPT))
      rank(getSliceType)
    }
  }

  /**
   * Returns an iterator over the keywords for a particular slice / window and the associated objects (authors, mails, etc.)
   * @return Iterator of (keywordId, objectId)
   */
  protected def keywordsForSlice(begin: Date, end: Date) = inTransaction {
    val cal = Calendar.getInstance()
    cal.setTime(begin)
    val beginYear = cal.get(Calendar.YEAR)
    cal.setTime(end)
    val endYear = cal.get(Calendar.YEAR)
    val result = new MutableList[(Long, Long, Int)]

    // first load the map of keyword ids
    val kid = from(keywordId)(k => select((k.keyword, k.id))).toMap
    def processKeyword(keyword: String, objectId: Long) {
      val k = stem(keyword.trim.toLowerCase)
      if (!isStopWord(k) && !isStopWord(keyword.trim.toLowerCase))
        result += ((kid(k), objectId, 1))
    }
    // load scraped keywords
    for ((keyword, authorid) <- from(keywords, coauthor, articleVenues)((k, ca, av) =>
      where(k.articleid === av.articleid and av.year.between(beginYear, endYear) and ca.articleid === av.articleid)
        select ((k.keyword, ca.authorid)))) {
      processKeyword(keyword, authorid)
      if (tokenize) {
        val tokens = keyword.trim.split("\\s+")
        if (tokens.length > 1) {
          for (t <- tokens)
            processKeyword(t, authorid)
        }
      }
    }
    // load mined keywords
    for ((keyword, frequency, authorid) <- from(keywordsEM, coauthor, articleVenues)((k, ca, av) =>
      where(k.articleid === av.articleid and av.year.between(beginYear, endYear) and ca.articleid === av.articleid)
        select ((k.keyword, k.frequency, ca.authorid)))) {
      for (i <- 0 until frequency.toInt) {
        processKeyword(keyword, authorid)
        if (tokenize) {
          val tokens = keyword.trim.split("\\s+")
          if (tokens.length > 1) {
            for (t <- tokens)
              processKeyword(t, authorid)
          }
        }
      }
    }
    log.debug("Found " + result.size + " keywords for slice between " + begin + " and " + end)
    result.iterator
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: SLICE_TYPE_OPT :: TOKENIZE_OPT :: Nil
}