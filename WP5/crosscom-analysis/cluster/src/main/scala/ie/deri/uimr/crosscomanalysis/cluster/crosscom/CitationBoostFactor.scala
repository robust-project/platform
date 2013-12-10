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

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/07/2011
 * Time: 19:15
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * This object extracts time series of citation boost measure presented in Mazloumian et al. (2011)
 * 'How Citation Boosts Promote Scientific Paradigm Shifts and Nobel Prizes'.
 */

object CitationBoostFactor extends Logging with DBArgsParser with ClusterArgsParser with SessionFactorySetter with CitationsCounter {

  override val COMMAND_NAME = "citation-boost"

  def compute(db: String, sliceType: Int, outFile: File, windowSize: Int) {
    setUpSessionFactory(db)

    val outWriter = new PrintWriter(outFile)
    def out(s: Any*) {
      synchronized {
        outWriter.println(s.map(_.toString.replaceAll(",", "\\,")).mkString(","))
      }
    }

    //    val windowSize = 2 // 'w' parameter in the paper
    val k = 4 // parameter to reduce noise - see the paper
    val beginYear = 2000
    val endYear = 2009

    out("year", "authorid", "citboost", "prevcitcount", "futurecitcount", "papercountuptoyear")
    transaction {
      val authors =
        from(networkSlice, networkSliceStructure)((ns, nss) =>
          where(ns.sliceType === sliceType and ns.id === nss.sliceId)
          select ((nss.source, nss.sink))).map(p => Set(p._1, p._2)).flatten.toSet.par
      log.info(authors.size + " authors loaded (for all slices)")
      for (author <- authors;
           year <- beginYear to endYear) {
        val futureYear = if (year + windowSize > endYear) endYear else year + windowSize
        val futureCitCount = citationsCount(author, 2000 to year, (year + 1) to futureYear)
        val prevCitCount = citationsCount(author, 2000 to year, (year - windowSize + 1) to year)
        val r = if (prevCitCount != 0) math.pow(futureCitCount, k) / math.pow(prevCitCount, k) else 0
        log.debug("future cit. count: " + futureCitCount)
        log.debug("previous cit. count: " + prevCitCount)
        log.debug("boost: " + r)
        out(year, author, r, prevCitCount, futureCitCount, paperCount(author, year))
      }
    }
    log.info("Done")
    outWriter.close()
  }

  /**
   * @return # papers published by the author until the specified year (inclusive).
   */
  private def paperCount(author: Long, until: Int): Long = transaction {
    from(coauthor, articleVenues)((ca, av) =>
      where(ca.articleid === av.articleid and av.year.lte(until) and ca.authorid === author)
        compute (countDistinct(ca.articleid))).single.measures
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      compute(getOptValue(DB_OPT), getSliceType, new File(getOptValue(OUTFILE_OPT)), getOptValue(WINDOW_SIZE_OPT).toInt)
    }
  }

  override protected def commandLineOptions = DB_OPT :: OUTFILE_OPT :: SLICE_TYPE_OPT :: HELP_OPT :: WINDOW_SIZE_OPT :: Nil
}