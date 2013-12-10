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

import java.util.Date
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import collection.mutable.{HashMap, MutableList}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterFormats
import ie.deri.uimr.crosscomanalysis.db.tables.graph.{ClusterStructure, Cluster, NetworkSliceStructure, NetworkSlice}
import java.text.{ParsePosition, SimpleDateFormat}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 27/09/2011
 * Time: 16:33
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Implements the slicing process for boards.ie data-set. It generates a respective reply-to networks (weighted by the
 * # of replies) and it also creates explicit communities/clusters for each slice. If not set, the start and end dates
 * are queried as a minimal, resp. maximal post dates from the database.
 */

object BoardsIESlicer extends Slicer {

  override val COMMAND_NAME = "boardsie-slicer"

  protected def createSlice(beginSlice: Date, endSlice: Date, sliceType: Int) {
    transaction {
      // create networks slice (reply-to network between users)
      val ns = new NetworkSlice(beginSlice, endSlice, sliceType)
      networkSlice insert ns
      // load edges
      val nssBuffer = new MutableList[NetworkSliceStructure]
      val edgeCounts = new HashMap[(Long, Long), Int] // (replying, replied) -> count
      val postAuthors = from(posts)(p => select((p.postid, p.userid))).toMap // postid -> userid
      for (r <- from(replies)(r =>
        where(r.replyingposteddate.between(beginSlice, endSlice) and r.origposteddate.between(beginSlice, endSlice))
          select (r))) {
        val replyingUser = postAuthors(r.replyingpostid)
        val repliedUser = postAuthors(r.originalpostid)
        if (replyingUser != 0 && repliedUser != 0) { // userid 0 represents the 'guest' user
          val ec = edgeCounts.get((replyingUser, repliedUser))
          if (ec.isDefined)
            edgeCounts((replyingUser, repliedUser)) = ec.get + 1
          else
            edgeCounts((replyingUser, repliedUser)) = 1
        }
      }
      // store edges
      for (((replyingUser, repliedUser), count) <- edgeCounts) {
        nssBuffer += new NetworkSliceStructure(ns.id, replyingUser, repliedUser, count, None)
        if (nssBuffer.size % 1000 == 0) {
          log.debug("Storing buffer with " + nssBuffer.size + " edges")
          networkSliceStructure insert nssBuffer
          nssBuffer.clear()
        }
      }
      if (nssBuffer.size > 0) {
        log.debug("Storing buffer with " + nssBuffer.size + " edges")
        networkSliceStructure insert nssBuffer
        nssBuffer.clear()
      }
      log.info("Stored " + edgeCounts.size + " edges")
      // free resources
      edgeCounts.clear()
      // store the structure of implicit communities (fora memberships) for the given slice
      val clusters = new HashMap[Long, Cluster] // forumid -> cluster
      val csBuffer = new MutableList[ClusterStructure]
      for ((forumid, userid) <-
           from(posts, threads)((p, t) =>
             where(t.threadid === p.threadid and p.posteddate >= beginSlice and p.posteddate <= endSlice and p.userid <> 0)
               select ((t.forumid, p.userid))).toSet) {
        if (!clusters.contains(forumid)) {
          // create & store new cluster
          val c = new Cluster(ClusterFormats.EXPLICIT.id, forumid.toInt, forumid.toInt, beginSlice, endSlice, ns.id, None, None, None, None)
          cluster insert c
          clusters(forumid) = c
        }
        csBuffer += new ClusterStructure(clusters(forumid).id, userid, None, None)
        if (csBuffer.size % 1000 == 0) {
          log.debug("Storing buffer with " + csBuffer.size + " members")
          clusterStructure insert csBuffer
          csBuffer.clear()
        }
      }
      if (csBuffer.size > 0) {
        log.debug("Storing buffer with " + csBuffer.size + " members")
        clusterStructure insert csBuffer
        csBuffer.clear()
      }
      log.info("Stored " + clusters.size + " explicit communities")
    }
  }

  /**
   * If you want to constrain this to start with the first complete week, set the start to 16.2.1998. Likewise for the end:
   * 10.2.2008.
   */
  protected def interval = transaction {
    val df = new SimpleDateFormat("dd.MM.yyyy")
    val begin =
      if (hasOption(BEGIN_DATE_OPT))
        df.parse(getOptValue(BEGIN_DATE_OPT), new ParsePosition(0))
      else
        from(posts)(p => compute(min(p.posteddate))).single.measures.get
    val end =
      if (hasOption(END_DATE_OPT)) {
        val d = df.parse(getOptValue(END_DATE_OPT), new ParsePosition(0))
        d.setHours(23)
        d.setMinutes(59)
        d.setSeconds(59)
        d
      } else
        from(posts)(p => compute(max(p.posteddate))).single.measures.get

    (begin, end)
  }

  override def main(args: Array[String]) {
    mainStub(args) {
      slice()
    }
  }

  override protected def commandLineOptions = BEGIN_DATE_OPT :: END_DATE_OPT :: super.commandLineOptions
}