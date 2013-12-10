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
import org.apache.commons.cli.{Option => CliOption}
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.db.DBArgsParser
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.{keywordsEM, documentFreq}
import ie.deri.uimr.crosscomanalysis.db.schemas.CSXCitegraphSchema.{clusters, papers, authors}
import java.util.{TimeZone, Calendar, Date}
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/03/2012
 * Time: 22:30
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

object CSXIDFRankerParallel extends ParallelKeywordRanker with SessionFactorySetter with Logging with DBArgsParser {

  override val COMMAND_NAME = "rank-csxkeywords-par"

  val MIN_DF = new CliOption("mid", "min-docfreq", true, "Minimum document frequency of the keyword to be considered (over all the corpus)")
  val MAX_DF = new CliOption("mad", "max-docfreq", true, "Maximum document frequency of the keyword to be considered (over all the corpus)")
  private var minDocumentFreq: Int = 0
  private var maxDocumentFreq: Int = Int.MaxValue


  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      setUpSessionFactory(getOptValue(DB_OPT))
      if (hasOption(MIN_DF))
        minDocumentFreq = getOptValue(MIN_DF).toInt
      if (hasOption(MAX_DF))
        maxDocumentFreq = getOptValue(MAX_DF).toInt

      rank(getSliceType)
    }
  }

  /**
   * Returns an iterator over the keywords for a particular slice / window and the associated objects (authors, mails, etc.)
   * @return Iterator of (keywordId, objectId, occurences)
   */
  protected def keywordsForSlice(begin: Date, end: Date) = {
    inTransaction {
      val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
      cal.setTime(begin)
      val beginYear = cal.get(Calendar.YEAR)
      cal.setTime(end)
      val endYear = cal.get(Calendar.YEAR)
      from(keywordsEM, clusters, papers, authors, documentFreq)((k, c, p, a, d) =>
        where(k.doi === p.id and p.cluster === c.id and c.cyear.between(beginYear, endYear) and a.cluster <> 0 and
          a.paperid === p.id and k.keywordid === d.keywordid and d.distinctArticleId.between(minDocumentFreq, maxDocumentFreq))
          select ((k.keywordid, a.cluster, k.frequency))).iterator
    }
  }

  override protected def commandLineOptions = DB_OPT :: MIN_DF :: MAX_DF :: SLICE_TYPE_OPT :: HELP_OPT :: Nil
}