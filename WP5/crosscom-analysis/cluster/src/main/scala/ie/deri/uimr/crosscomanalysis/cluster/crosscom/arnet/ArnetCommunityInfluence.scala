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

package ie.deri.uimr.crosscomanalysis.cluster.crosscom.arnet

import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.graphdb.importers.DBGraphImporter
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.CommunityInfluenceLike
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import java.io.{PrintWriter, File}
import org.apache.commons.math.linear.OpenMapRealMatrix
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.ArnetSchema._
import collection._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 22/07/2012
 * Time: 18:17
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

object ArnetCommunityInfluence extends SessionFactorySetter with Logging with DBArgsParser with DBGraphImporter
with CommunityInfluenceLike with ClusterArgsParser {

  override val COMMAND_NAME = "arnet-comm-influence"

  protected val commCount = 6699
  protected val userCount = 912537

  private def compute(beginYear: Int, endYear: Int, window: Int, dir: File) {
    for (start <- beginYear to (endYear - window + 1) by window) {
      val end = if ((start + window - 1) > endYear) endYear else (start + window - 1)
      log.info("Computing influence for period " + start + "-" + end)
      outputMatrix(communityMembership(start, end), new File(dir, "membership_" + start + "-" + end + ".csv"))
      outputMatrix(indegreeCentralityMatrix(start, end), new File(dir, "indegree_" + start + "-" + end + ".csv"))
    }
  }

  private def communityMembership(beginYear: Int, endYear: Int): OpenMapRealMatrix = transaction {
    val membershipMatrix = new OpenMapRealMatrix(userCount + 1, commCount + 1)
    for (x <- from(authorship, paper)((a, p) =>
      where(p.year.between(beginYear, endYear) and p.id === a.paperid)
        groupBy(p.venue, a.authorid)
        compute (countDistinct(p.id)))) {
      val (venue, authorid) = x.key
      membershipMatrix.setEntry(authorid, venue, x.measures)
    }

    rowNormalizeMatrix(membershipMatrix)
  }

  private def indegreeCentralityMatrix(beginYear: Int, endYear: Int): OpenMapRealMatrix = transaction {
    val centralityMatrix = new OpenMapRealMatrix(userCount + 1, commCount + 1)
    for (x <- from(citationWithYear, authorship)((c, a) =>
      where(c.yearCited.between(beginYear, endYear) and c.yearCiting.between(beginYear, endYear) and
        c.venueCiting === c.venueCited and a.paperid === c.cited)
        groupBy(a.authorid, c.venueCited)
        compute (count(c.citing)))) {
      val (authorId, venue) = x.key
      centralityMatrix.setEntry(authorId, venue, x.measures)
    }

    centralityMatrix
  }

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      compute(getOptValue(BEGIN_YEAR_OPT).toInt, getOptValue(END_YEAR_OPT).toInt, getOptValue(WINDOW_SIZE_OPT).toInt,
        new File(getOptValue(DIR_OPT)))
    }
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: BEGIN_YEAR_OPT :: END_YEAR_OPT :: WINDOW_SIZE_OPT ::
    DIR_OPT :: Nil
}