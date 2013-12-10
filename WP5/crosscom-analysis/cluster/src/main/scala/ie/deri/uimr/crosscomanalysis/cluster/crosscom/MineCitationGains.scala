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

import ie.deri.uimr.crosscomanalysis.util.{Parallel, Logging}
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import java.io.{PrintWriter, File}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import io.Source
import collection.Set
import collection.mutable.{HashSet, HashMap}
import org.apache.commons.cli.{Option => CliOption}
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/08/2011
 * Time: 12:06
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Purpose of this class is to mine citation gains of leaders and their fellows, which do NOT contain any leaders. This
 * should lead to more significant differences as the meas over fellows possibly containing leaders can be biased.
 */
object MineCitationGains extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser
with CitationsCounter with Parallel {

  val maxYear = 2009
  val exploredFuture = 2  // todo this is parametrisable by discourse leaders miners!

  val MIN_LEADER_COUNT_OPT = new CliOption("ml", "min-leader-count", true, "Minimum number of leadership counts to be considered. Default: 1.")

  override val COMMAND_NAME = "discourse-leaders-citations"

  def mine(db: String, sliceType: Int, clusterFormat: Int, flag: Option[String],
           outDir: File, leadersFile: File, minLeaderCount: Int, minClusterSize: Int) {
    setUpSessionFactory(db)
    // file containing citations stats for leaders and their fellows, which do *not* contain any leader
    val citationStatsOut = new PrintWriter(outDir.getAbsolutePath + File.separator +
      leadersFile.getName.substring(0,leadersFile.getName.length() - 4) + "-separated.csv")
    def outCitationStats(s: Any*) {
      synchronized {
        citationStatsOut.println(s.map(_.toString.replaceAll(",", "\\,")).mkString(","))
      }
    }
    val (leaders, leadersCount) = retrieveLeaders(leadersFile)

    transaction {
      outCitationStats("leader id", "leader citation count", "fellow citation count", "cluster index", "cluster begin",
        "cluster end", "future year")
      for (slice <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns) orderBy (ns.beginDate).asc)) {
        log.info("Processing slice #" + slice)
        val clusterQuery =
          if (flag.isDefined) from(cluster)(c => where(c.sliceId === slice.id and c.flag === flag and c.format === clusterFormat) select (c))
          else from(cluster)(c => where(c.sliceId === slice.id and c.format === clusterFormat) select (c))
        val clusters = (for (cluster <- clusterQuery) yield new ClusterFromRDB(cluster)).toSet
        log.debug("Number of clusters: " + clusters.size)
        val endOfExploredPeriod =
          if ((slice.beginYear + exploredFuture) <= maxYear)
            slice.beginYear + exploredFuture
          else
            maxYear
        for (cluster <- clusters.par if cluster.vertices.size >= minClusterSize) {
          log.info(cluster.toString)
          for (year <- (slice.beginYear + 1) to endOfExploredPeriod
               if leaders.contains(slice.beginYear) && leaders(slice.beginYear).contains(cluster.index) &&
                 leaders(slice.beginYear)(cluster.index).contains(year)) {
            val leadersIds = leaders(slice.beginYear)(cluster.index)(year).toList.filter(l => leadersCount(l) >= minLeaderCount)
            if (!leadersIds.forall(cluster.vertices.contains(_))) sys.error("Inconsistent leaders!")
            if (!leadersIds.isEmpty) {
              val fellows = (cluster.vertices -- leadersIds).toList
              val leadersCitationCounts = leadersIds.map(leader =>
                citationsCount(leader, cluster.vertices - leader, 2000 to slice.beginYear, (slice.beginYear + 1) to year)).toList
              val fellowsCitationCounts = fellows.map(f =>
                citationsCount(f, cluster.vertices - f, 2000 to slice.beginYear, (slice.beginYear + 1) to year)).toList
              for (i <- 0 until math.min(leadersCitationCounts.size, fellowsCitationCounts.size))
                outCitationStats(leadersIds(i), leadersCitationCounts(i), fellowsCitationCounts(i), cluster.index,
                  slice.beginYear, slice.endYear, year)
              for (i <- math.min(leadersCitationCounts.size, fellowsCitationCounts.size) until math.max(leadersCitationCounts.size, fellowsCitationCounts.size)) {
                val leaderCount = if (leadersCitationCounts.size < i + 1) "NA" else leadersCitationCounts(i)
                val fellowCount = if (fellowsCitationCounts.size < i + 1) "NA" else fellowsCitationCounts(i)
                val leaderId = if (leadersIds.size < i + 1) "NA" else leadersIds(i)
                outCitationStats(leaderId, leaderCount, fellowCount, cluster.index, slice.beginYear, slice.endYear, year)
              }
            }
          }
        }
      }
    }
    citationStatsOut.close()
  }

  /**
   * @return Tuple (leaders,leadersCount). Leaders: beginYear -> (clusterIndex -> (futureYear) -> {leaders}), leadersCount: id -> # being detected
   */
  private def retrieveLeaders(leadersStats: File) = {
    val leaders = new HashMap[Int, HashMap[Int, HashMap[Int, Set[Long]]]]
    val leadersCount = new HashMap[Long, Int] // id -> count
    // "id", "name", "future year", "distance", "p-value", "mean", "variance", "cluster index", "cluster begin", "cluster end"
    for (line <- Source.fromFile(leadersStats).getLines();
         Array(id, _, futureYear, _, _, _, _, clusterIndex, beginYear, _, _) = line.split(",") if !id.equals("id")) {
      if (!leaders.contains(beginYear.toInt))
        leaders(beginYear.toInt) = new HashMap[Int, HashMap[Int, Set[Long]]]
      if (!leaders(beginYear.toInt).contains(clusterIndex.toInt))
        leaders(beginYear.toInt)(clusterIndex.toInt) = new HashMap[Int, Set[Long]]
      if (!leaders(beginYear.toInt)(clusterIndex.toInt).contains(futureYear.toInt))
        leaders(beginYear.toInt)(clusterIndex.toInt)(futureYear.toInt) = new HashSet[Long]
      if (!leadersCount.contains(id.toLong)) leadersCount(id.toLong) = 0
      leaders(beginYear.toInt)(clusterIndex.toInt)(futureYear.toInt) += id.toLong
      leadersCount(id.toLong) += 1
    }
    (leaders, leadersCount)
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      val parDegree = getOpt(PARALLELISM_DEGREE_OPT)
      if (parDegree.isDefined) setParallelismDegree()
      val minLeaderCount = getOpt(MIN_LEADER_COUNT_OPT, 0)
      val minClusterSize = getOpt(MIN_CLUSTER_SIZE_OPT, 0)
      mine(getOptValue(DB_OPT), getSliceType, getClusterFormat, getClusterFlag, new File(getOptValue(DIR_OPT)),
        new File(getOptValue(INFILE_OPT)), minLeaderCount, minClusterSize)
    }
  }

  override protected def commandLineOptions = DB_OPT :: SLICE_TYPE_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT ::
    DIR_OPT :: PARALLELISM_DEGREE_OPT :: INFILE_OPT :: MIN_LEADER_COUNT_OPT :: MIN_CLUSTER_SIZE_OPT :: Nil
}