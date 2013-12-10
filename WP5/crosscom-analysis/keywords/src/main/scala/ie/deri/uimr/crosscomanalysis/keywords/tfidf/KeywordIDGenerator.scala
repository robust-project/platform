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

import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.HashSet
import ie.deri.uimr.crosscomanalysis.keywords.tfidf.StopWords.isStopWord
import ie.deri.uimr.crosscomanalysis.db.tables.cocit._
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.util.Functions.stem
import org.apache.commons.cli.{Option => CliOption}
import ie.deri.uimr.crosscomanalysis.keywords.KeywordArgsParser

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08-Dec-2010
 * Time: 16:26:51
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 *
 * Generates unique ids for each keyword (both scraped and mined). Each keyword is trimmed, lower-cased and finally stemmed.
 * It does NOT perform any tokenization!
 */

object KeywordIDGenerator extends SessionFactorySetter with Logging with DBArgsParser with KeywordArgsParser {

  override val COMMAND_NAME = "generate-keywords-id"

  private val allKeywords = new HashSet[KeywordIDAutoInc]
  private val stemmedKeywords = new HashSet[String]

  def createUniqueKeywordIDs(db: String, tokenize: Boolean) {
    setUpSessionFactory(db)
    transaction {
      for (keyword <- (from(keywords)(k => select(k.keyword))))
        processKeyword(keyword, tokenize)
      log.info("Scraped keywords processed")

      for (keyword <- from(keywordsEM)(k => select(k.keyword)))
        processKeyword(keyword, tokenize)
      log.info("Mined keywords processed")

      // store keywords
      if (!allKeywords.isEmpty) keywordId insert allKeywords
      log.info(allKeywords.size + " keywords inserted. Terminating ...")
    }
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked)
      createUniqueKeywordIDs(getOptValue(DB_OPT), PARSED_LINE.get.hasOption(TOKENIZE_OPT.getOpt))
  }


  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: TOKENIZE_OPT :: Nil

  private def processKeyword(keyword: String) {
    val k = stem(keyword.trim().toLowerCase())
    if (!isStopWord(k) && !isStopWord(keyword.trim().toLowerCase) && !stemmedKeywords.contains(k)) {
      allKeywords += new KeywordIDAutoInc(k, keyword.trim())
      stemmedKeywords += k
    }
  }

  private def processKeyword(keyword: String, tokenize: Boolean) {
    processKeyword(keyword)
    if (tokenize) {
      val tokens = keyword.trim.split("\\s+")
      if (tokens.length > 1) {
        for (t <- tokens)
          processKeyword(t)
      }
    }
  }
}
