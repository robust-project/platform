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

package ie.deri.uimr.crosscomanalysis.keywords

import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import ie.deri.uimr.crosscomanalysis.util.Logging
import collection.mutable.HashMap
import ie.deri.uimr.crosscomanalysis.util.Functions.stem
import ie.deri.uimr.crosscomanalysis.db.tables.cocit.AuthorKeyword
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.keywords.tfidf.StopWords.isStopWord

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/06/2011
 * Time: 17:42
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Selects and stores keywords for each author and/per year.
 */

object PerYearOccurrenceCalculator extends SessionFactorySetter with Logging with DBArgsParser with KeywordArgsParser {

  override val COMMAND_NAME = "occurrence-calculator"

  def calculateAndStoreOccurrences(db: String, tokenize: Boolean) {
    setUpSessionFactory(db)

    val beginYear = 2000
    val endYear = 2009
    val keywordIds = retrieveKeywordIds

    for (year <- beginYear to endYear) {
      log.info("Processing year " + year)
      transaction {
        // authorid -> (keyword -> occurrences)
        val authorsKeywordsOcc = new HashMap[Long, HashMap[String, Int]]

        def processKeyword(keyword: String, partialOccurrence: Int, authorid: Long) {
          val normalizedKeyword = stem(keyword.trim.toLowerCase)
          if (!isStopWord(normalizedKeyword) && !isStopWord(keyword.trim)) {
            var keywordOccurrences = authorsKeywordsOcc.get(authorid)
            if (keywordOccurrences.isEmpty) {
              keywordOccurrences = Some(new HashMap[String, Int])
              authorsKeywordsOcc(authorid) = keywordOccurrences.get
            }
            var occurrences = keywordOccurrences.get.get(normalizedKeyword)
            if (occurrences.isEmpty) {
              occurrences = Some(
                if (partialOccurrence > 0) partialOccurrence.toInt
                else 1)
              keywordOccurrences.get(normalizedKeyword) = occurrences.get
            }
          }
        }

        // load all keywords and calculate their occurrences per author
        for ((keyword, partialOccurrence, authorid) <- keywordsForYear(year)) {
          processKeyword(keyword, partialOccurrence, authorid)
          if (tokenize) {
            val tokens = keyword.trim.split("\\s+")
            if (tokens.length > 1) {
              for (t <- tokens)
                processKeyword(t, partialOccurrence, authorid)
            }
          }
        }
        // store the occurrences
        for ((authorid, keywordOccurrences) <- authorsKeywordsOcc;
             (keyword, occurrences) <- keywordOccurrences) {
          authorKeyword insert new AuthorKeyword(authorid, keywordIds(keyword), year, occurrences)
        }
        log.info("Done. Stored " + authorsKeywordsOcc.size + " authors")
      }
    }
  }

  private def keywordsForYear(year: Int) = inTransaction {
    // EM keywords (mined by EM)
    val minedKeywords = from(keywordsEM, coauthor, articleVenues)((k, ca, av) =>
      where(k.articleid === ca.articleid and ca.articleid === av.articleid and av.year === year)
        select ((k.keyword, k.frequency, ca.authorid))).toSeq
    // scraped keywords
    val scrapedKeywords = from(keywords, coauthor, articleVenues)((k, ca, av) =>
      where(k.articleid === ca.articleid and ca.articleid === av.articleid and av.year === year)
        select ((k.keyword, 1, ca.authorid))).toSeq
    minedKeywords ++ scrapedKeywords
  }

  private def retrieveKeywordIds = inTransaction {
    from(keywordId)(kId => select((kId.keyword, kId.id))).toMap
  }

  override protected def commandLineOptions = DB_OPT :: TOKENIZE_OPT :: Nil

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      calculateAndStoreOccurrences(getOptValue(DB_OPT), hasOption(TOKENIZE_OPT))
    }
  }
}