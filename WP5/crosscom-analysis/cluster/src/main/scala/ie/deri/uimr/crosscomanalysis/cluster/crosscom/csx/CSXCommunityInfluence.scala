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

package ie.deri.uimr.crosscomanalysis.cluster.crosscom.csx

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.graphdb.importers.DBGraphImporter
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.CommunityInfluenceLike
import collection._
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CSXCitegraphSchema._
import org.apache.commons.math.linear.OpenMapRealMatrix
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import java.io.{PrintWriter, File}
import org.apache.commons.cli.Option

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/06/2012
 * Time: 16:06
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

object CSXCommunityInfluence extends SessionFactorySetter with Logging with DBArgsParser with DBGraphImporter
with CommunityInfluenceLike with ClusterArgsParser {

  val OVERALL_INDEGREE_OPT = new Option("oi", false, "If set, the overall in-degree (regardless of venue) will be computed.")

  override val COMMAND_NAME = "csx-comm-influence"

  private lazy val venueID = retriveVenues
  protected lazy val commCount = venueID.keySet.size
  protected lazy val userCount = countActors

  private def compute(beginYear: Int, endYear: Int, window: Int, dir: File, overallIndegree: Boolean) {
    for (start <- beginYear to (endYear - window + 1) by window) {
      val end = if ((start + window - 1) > endYear) endYear else (start + window - 1)
      log.info("Computing influence for period " + start + "-" + end)
      outputMatrix(communityMembership(start, end), new File(dir, "membership_" + start + "-" + end + ".csv"))
      outputMatrix((if (overallIndegree) indegreeCentralityMatrixOverall(start, end) else indegreeCentralityMatrix(start, end)),
        new File(dir, "indegree_" + start + "-" + end + ".csv"))
    }
  }

  /**
   * @param beginYear
   * @param endYear
   * @return Pair of a set of clusters and a set of the conference abbreviations
   */
  private def communityMembership(beginYear: Int, endYear: Int): OpenMapRealMatrix = transaction {
    val membershipMatrix = new OpenMapRealMatrix(userCount + 1, commCount + 1)
    for (x <- from(clusters, authors, papers)((c, a, p) =>
      where(c.cyear.between(beginYear, endYear) and c.cbooktitle.isNotNull and c.id === p.cluster and p.id === a.paperid and
        c.id <> 0 and a.cluster <> 0)
        groupBy(c.venue, a.cluster)
        compute (countDistinct(c.id)))) {
      val (venue, actorID) = x.key
      membershipMatrix.setEntry(actorID.toInt, venueID(venue.get), x.measures)
    }

    rowNormalizeMatrix(membershipMatrix)
  }

  /**
   * @param beginYear
   * @param endYear
   * @return An indegree centrality matrix (# of citations an actor A received within each conference C)
   */
  private def indegreeCentralityMatrix(beginYear: Int, endYear: Int): OpenMapRealMatrix = transaction {
    val centralityMatrix = new OpenMapRealMatrix(userCount + 1, commCount + 1)
    for (x <- from(citegraphWithYears, authors, papers)((cg, a, p) =>
      where(a.paperid === p.id and p.cluster === cg.cited and cg.citedyear.between(beginYear, endYear) and
        cg.citingyear.between(beginYear, endYear) and cg.citingvenue === cg.citedvenue and
        a.cluster <> 0 and cg.cited <> 0 and cg.citing <> 0)
        groupBy(a.cluster, cg.citedvenue)
        compute (count(cg.citing)))) {
      val (actorID, venue) = x.key
      centralityMatrix.setEntry(actorID.toInt, venueID(venue), x.measures)
    }

    centralityMatrix
  }

  /**
   * @param beginYear
   * @param endYear
   * @return An indegree centrality marix (# of citations an actor A received by authors from conference C regardless
   *         of the publication venue.
   */
  private def indegreeCentralityMatrixOverall(beginYear: Int, endYear: Int): OpenMapRealMatrix = transaction {
    val centralityMatrix = new OpenMapRealMatrix(userCount + 1, commCount + 1)
    for (x <- from(citegraphWithYears, authors, papers)((cg, a, p) =>
      where(a.paperid === p.id and p.cluster === cg.cited and cg.citedyear.between(beginYear, endYear) and
        cg.citingyear.between(beginYear, endYear) and
        a.cluster <> 0 and cg.cited <> 0 and cg.citing <> 0)
        groupBy(a.cluster, cg.citedvenue)
        compute (count(cg.citing)))) {
      val (actorID, venue) = x.key
      centralityMatrix.setEntry(actorID.toInt, venueID(venue), x.measures)
    }

    centralityMatrix
  }

  private def retriveVenues: immutable.Map[String, Int] = transaction {
    var id = 0
    from(clusters)(c => where(c.venue.isNotNull) select (c.venue) orderBy(c.venue).asc).distinct.map(v => (v.get, {id += 1; id})).toMap
  }

  private def countActors: Int = transaction {
    from(clusters, authors, papers)((c, a, p) =>
      where(c.cyear.isNotNull and c.venue.isNotNull and c.id === p.cluster and p.id === a.paperid and c.id <> 0 and a.cluster <> 0)
        compute (max(a.cluster))).head.measures.get.toInt
  }

  private def filterVenues(minPubCount: Int): immutable.Set[String] = transaction {
    from(clusters)(c => where(c.venue.isNotNull) groupBy(c.venue) compute(countDistinct(c.id))).filter(_.measures >= minPubCount).map(_.key.get).toSet
  }

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      compute(getOptValue(BEGIN_YEAR_OPT).toInt, getOptValue(END_YEAR_OPT).toInt, getOptValue(WINDOW_SIZE_OPT).toInt,
        new File(getOptValue(DIR_OPT)), hasOption(OVERALL_INDEGREE_OPT))
      val venueMapping = new PrintWriter(getOptValue(DIR_OPT) + File.separator + "venue_mapping.csv")
      venueMapping.println("venue,id")
      for ((venue, id) <- venueID) {
        venueMapping.println(venue + "," + id)
      }
      venueMapping.close()
    }
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: BEGIN_YEAR_OPT :: END_YEAR_OPT :: WINDOW_SIZE_OPT ::
    DIR_OPT :: OVERALL_INDEGREE_OPT :: Nil
}