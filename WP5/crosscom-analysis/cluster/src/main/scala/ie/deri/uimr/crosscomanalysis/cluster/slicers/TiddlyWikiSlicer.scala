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

package ie.deri.uimr.crosscomanalysis.cluster.slicers

import ie.deri.uimr.crosscomanalysis.util.Config.config
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.TiddlyWikiSchema._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.graph._
import ie.deri.uimr.crosscomanalysis.util.Logging
import java.util.{TimeZone, Calendar, Date}
import collection.mutable.HashMap
import java.io.{PrintWriter, File}
import edu.uci.ics.jung.graph.{DelegateTree, DirectedSparseGraph, DelegateForest}
import edu.uci.ics.jung.graph.util.EdgeType
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.util.Functions.{filePath, denormalize}
import org.apache.commons.cli.{Option=>CliOption}
import ie.deri.uimr.crosscomanalysis.cluster.exporters.EdgelistExporter

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07/03/2011
 * Time: 10:06
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object TiddlyWikiSlicer extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser {

  private val DIST_LIMIT_OPT = new CliOption("l", "dist-limit", true, "number of hops up the thread to be considered")

  def slice(db: String, outDir: File, windowSize: Int, windowSizeUnitStr: String, distLimit:Int) {
    setUpSessionFactory(db)
    val (begin, end) = interval
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    cal.setTime(begin)
    var lastEdgeId = -1l
    def edgeId = {
      lastEdgeId += 1
      lastEdgeId
    }
    val windowSizeUnit = windowSizeUnitStr match {
      case "year" => Calendar.YEAR
      case "month" => Calendar.MONTH
    }

    while (cal.getTimeInMillis < end.getTime) {
      val currBegin = cal.getTime
      cal.add(windowSizeUnit, windowSize)
      if (cal.getTimeInMillis > end.getTime) cal.setTime(end)
      val currEnd = cal.getTime

      log.info("Exporting slice between " + currBegin + " and " + currEnd)

      val threads = new HashMap[Long, DelegateTree[Long, Long]]
      transaction {
        // we assume the sentDate are not null and it is possible to order the mails accordingly, so that the first target
        // is the root of the thread/tree
        for ((sourceMail, targetMail) <-
             from(thread, mail)((t, m) =>
               where(t.sourceid === m.id and m.sentDate <= Some(currEnd) and m.sentDate >= Some(currBegin)) select ((t.sourceid, t.targetid)) orderBy (m.sentDate).asc)) {
          val tree = if (threads.contains(targetMail)) {
            threads(targetMail)
          } else {
            val newTree = new DelegateTree[Long, Long]
            newTree.setRoot(targetMail)
            threads(targetMail) = newTree
            newTree
          }
          tree.addChild(edgeId, targetMail, sourceMail, EdgeType.DIRECTED)
          threads(sourceMail) = tree
        }
        log.debug("threads # " + threads.values.toSet.size)

        val edgeList = convertThreads(threads.values, distLimit)
        log.debug("converted graph edge # " + edgeList.size)
        if (!edgeList.isEmpty) {
          val ns = new NetworkSlice(currBegin, currEnd, getSliceType(windowSize, windowSizeUnitStr).id)
          networkSlice insert ns
          for (((replier, replied), weight) <- edgeList) {
            val nss = new NetworkSliceStructure(ns.id, replier, replied, weight.toDouble, None)
            networkSliceStructure insert nss
          }
        }
      }
    }
    EdgelistExporter.export(outDir, getSliceType(windowSize, windowSizeUnitStr).id)
  }

  /**
   * @param f forest of mail threads
   * @param distLimit number of hops in the thread up the hierarchy which is to be considered, 1->only the neighbour, -1->all the nodes up to the root (inclusive)
   * @return edgelist of weighted graph of users
   */
  private def convertThreads(threads: Iterable[DelegateTree[Long, Long]], distLimit: Int) = {
    import collection.JavaConversions.collectionAsScalaIterable

    val eList = new HashMap[(Long, Long), Double]

    for (tree <- threads; mail <- tree.getVertices if mail != tree.getRoot) {
      // get path from the mail up to the root
      val path = tree.getPath(mail).toList.reverse
      var dist = 1
      val replier = getSender(mail)
      // go from the mail one step above the source mail up to the root
      for (repliedMail <-  path.slice(1, path.size) if distLimit == -1 || dist <= distLimit) {
        val replied = getSender(repliedMail)
        val w = eList.get((replier, replied))
        if (replier != replied) {
          eList((replier, replied)) = (1d / dist) + (if (w.isDefined) w.get else 0)
        }
        dist += 1
      }
    }

    eList
  }

  private def getSliceType(windowSize: Int, windowSizeUnit: String) = SliceTypes.withName(windowSize + windowSizeUnit)

  private def getSender(mailid: Long) = inTransaction {
    from(sender, mail)((s, m) => where(s.mailid === m.id and m.id === mailid) select (s.sender)).single
  }

  private def interval: (Date, Date) = inTransaction {
    val begin = from(mail)(m => compute(min(m.sentDate))).single.measures
    val end = from(mail)(m => compute(max(m.sentDate))).single.measures
    (begin.get, end.get)
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked)
      slice(getOptValue(DB_OPT), new File(getOptValue(DIR_OPT)), getOptValue(WINDOW_SIZE_OPT).toInt,
        getOptValue(WINDOW_SIZE_UNIT_OPT).toLowerCase, getOptValue(DIST_LIMIT_OPT).toInt)
  }

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: DIR_OPT :: WINDOW_SIZE_OPT :: WINDOW_SIZE_UNIT_OPT :: DIST_LIMIT_OPT :: Nil
}