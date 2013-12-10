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

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.db.DBArgsParser
import java.io.{File, PrintWriter}
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.db.tables.graph.NetworkSlice
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import collection.mutable.HashSet
import collection.Set
import ie.deri.uimr.crosscomanalysis.util.Parallel

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/08/2011
 * Time: 12:21
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Implements basic functionality used in concrete implementations of Discourse Leaders Miners (probabilistic, TFIDF, ...).
 */

abstract class DiscourseLeaderMiner extends CitationsCounter with DBArgsParser with ClusterArgsParser
with SessionFactorySetter with Parallel {

  protected var keywordVectorLoader: Option[KeywordVectorLoader] = None
  protected var sliceType: Int = _
  protected var db: String = _
  protected var clusterFormat: Int = _
  protected var flag: Option[String] = None
  protected var excludeCoauthors: Boolean = _
  protected var alpha: Double = _
  protected var outDir: File = _
  protected var minClusterSize: Int = _
  protected var maxYear: Int = _
  protected var exploredFuture: Int = _

  private var topicStatsOut: PrintWriter = _
  // statistics of citation gains for leaders and their fellows (average)
  private var citationStatsOut: PrintWriter = _
  // statistics of citation gains for leaders and their fellows (individually)
  private var citationStatsIndOut: PrintWriter = _
  // statistics about distances beetween candidate leaders and their fellows
  private var topicAllStatsOut: PrintWriter = _

  /**
   * Initializes the parameters from parsed command line (assumes that parseArgs() has been called).
   * @see #parseArgs
   */
  protected def init() {
    sliceType = getSliceType
    db = getOptValue(DB_OPT)
    clusterFormat = getClusterFormat
    flag = getClusterFlag
    excludeCoauthors = PARSED_LINE.get.hasOption(EXCLUDE_COAUTHORS_OPT.getOpt)
    alpha = getOpt(ALPHA_VALUE_OPT, 0.05)
    outDir = new File(getOptValue(DIR_OPT))
    outDir.mkdirs()
    minClusterSize = getOpt(MIN_CLUSTER_SIZE_OPT, 0)
    maxYear = getOptValue(MAX_YEAR_OPT).toInt
    exploredFuture = getOptValue(EXPLORED_FUTURE_OPT).toInt
    val parDegree = getOpt(PARALLELISM_DEGREE_OPT)
    if (parDegree.isDefined) setParallelismDegree()
  }

  def mine() {
    setUpSessionFactory(db)
    keywordVectorLoader = Some(new KeywordVectorLoader(db))
    val fileSuffix = if (excludeCoauthors) "-excluding-coauthors.csv" else "including-coauthors.csv"
    // detection of leaders
    topicStatsOut = new PrintWriter(outDir.getAbsolutePath + File.separator + "leaders-topic-stats" + fileSuffix)
    // statistics of citation gains for leaders and their fellows (average)
    citationStatsOut = new PrintWriter(outDir.getAbsolutePath + File.separator + "leaders-citation-stats" + fileSuffix)
    // statistics of citation gains for leaders and their fellows (individually)
    citationStatsIndOut = new PrintWriter(outDir.getAbsolutePath + File.separator + "leaders-citation-stats-individual" + fileSuffix)
    topicAllStatsOut = new PrintWriter(outDir.getAbsolutePath + File.separator + "topic-all-stats" + fileSuffix)

    transaction {
      outTopicStats("id", "name", "future year", "distance", "p-value", "mean", "variance", "cluster index",
        "cluster begin", "cluster end", "cluster size")
      outTopicAllStats("id", "name", "future year", "distance", "p-value", "mean", "variance", "cluster index",
        "cluster begin", "cluster end", "cluster size")
      outCitationStats("leader id", "leader citation count", "fellow citation count (average)", "cluster index", "cluster begin",
        "cluster end")
      outCitationIndStats("leader id", "leader citation count", "fellow citation count", "cluster index", "cluster begin",
        "cluster end")
      for (slice <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns) orderBy (ns.beginDate).asc)) {
        processSlice(slice)
      }
    }

    topicStatsOut.close()
    citationStatsOut.close()
    citationStatsIndOut.close()
    topicAllStatsOut.close()
  }

  protected def outTopicStats(s: Any*) {
    synchronized {
      topicStatsOut.println(s.map(_.toString.replaceAll(",", "\\,")).mkString(","))
    }
  }

  protected def outCitationStats(s: Any*) {
    synchronized {
      citationStatsOut.println(s.map(_.toString.replaceAll(",", "\\,")).mkString(","))
    }
  }

  protected def outCitationIndStats(s: Any*) {
    synchronized {
      citationStatsIndOut.println(s.map(_.toString.replaceAll(",", "\\,")).mkString(","))
    }
  }

  protected def outTopicAllStats(s: Any*) {
    synchronized {
      topicAllStatsOut.println(s.map(_.toString.replaceAll(",", "\\,")).mkString(","))
    }
  }

  /**
   * Implements the actual mining process - it is called once for each selected slice (ordered chronologically). It is embedded in transaction.
   * @param slice The NetworkSlice object
   */
  protected def processSlice(slice: NetworkSlice)

  /**
   * @return Set of coauthors' ids given an author's id and a range of years.
   */
  protected def coauthors(authorid: Long, years: Range): Set[Long] = inTransaction {
    // select first articleid
    val articles = from(coauthor, articleVenues)((ca, av) =>
      where(ca.authorid === authorid and ca.articleid === av.articleid and av.year.in(years)) select (ca.articleid)).toSet
    if (!articles.isEmpty) {
      from(coauthor)(ca =>
        where(ca.articleid.in(articles) and not(ca.authorid === authorid)) select (ca.authorid)).toSet
    } else new HashSet[Long]
  }

  protected def coauthors(authorid: Long, year: Int): Set[Long] = coauthors(authorid, year to year)

  protected def retrieveAuthorNames = inTransaction {
    from(authornames)(an => select((an.authorid, an.fullname))).distinct.toMap
  }

  protected def authorName(authorid: Long) = inTransaction {
    from(authornames)(an => where(an.authorid === authorid) select (an.fullname)).head.replaceAll("\n", "").
      replaceAll(",", " ").replaceAll("\""," ").replaceAll("'"," ")
  }

  override protected def commandLineOptions =
    DB_OPT :: SLICE_TYPE_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT :: ALPHA_VALUE_OPT :: EXCLUDE_COAUTHORS_OPT ::
      DIR_OPT :: PARALLELISM_DEGREE_OPT :: MIN_CLUSTER_SIZE_OPT :: MAX_YEAR_OPT :: EXPLORED_FUTURE_OPT :: Nil

  /**
   * @param w Writer to use for the output.
   * @param s Sequence of values to print out - sequence is converted to comma separated strings ("," is escaped to "\,").
   */
  protected def out(w: PrintWriter, s: Any*) {
    synchronized {
      w.println(s.map(_.toString.replaceAll(",", "\\,")).mkString(","))
    }
  }
}