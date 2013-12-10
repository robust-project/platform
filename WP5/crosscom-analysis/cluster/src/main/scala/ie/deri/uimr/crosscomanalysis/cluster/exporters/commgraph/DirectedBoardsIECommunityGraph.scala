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

package ie.deri.uimr.crosscomanalysis.cluster.exporters.commgraph

import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE._
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.ClusterJaccardOverlap
import collection.mutable.HashMap
import java.util.Date
import collection.Set

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/11/2011
 * Time: 11:52
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

/**
 * Oriented edge's weight between fora a and b:
 * pa - number of posts by users of forum a in forum a
 * pb - number of posts by users of forum b in forum b
 * pa* - total number of posts posted by forum a in _all_ fora
 * @param relativeToTotalPosts True - the weights are normalized to the total number of posts by the outgoing forum (pa*). True=with self-loops, false without.
 */
class DirectedBoardsIECommunityGraph(db:String, clusterFormat: Int, clusterFlag: Option[String], sliceId: Long, val relativeToTotalPosts: Boolean)
  extends CommunityGraph(db, clusterFormat, clusterFlag, sliceId) {

  /**
   * @return Weights AB defined as #posts by members of A posted in forum B divided by #posts by A in both fora A and B
   */
  protected def weights(clusters: Set[ClusterFromRDB]) = {
    val overlaps = new ClusterJaccardOverlap[Long].clustersOverlaps(clusters, clusters)
    val weights = new HashMap[ClusterFromRDB, HashMap[ClusterFromRDB, Double]]
    for (firstComm <- clusters; (secondComm, overlap) <- overlaps(firstComm) if overlap > 0 && (relativeToTotalPosts || firstComm != secondComm)) {
      val divisor =
        if (relativeToTotalPosts)
          postCount(firstComm.vertices, firstComm.clusterDB.beginDate, firstComm.clusterDB.endDate)
        else
          (postCount(firstComm.vertices, firstComm.clusterDB.beginDate, firstComm.clusterDB.endDate) - postCount(firstComm.index, firstComm.clusterDB.beginDate, firstComm.clusterDB.endDate))
      // #posts the members of first community posted in the first
      val postCountFirstInSecond = postCount(secondComm.index, firstComm.vertices, firstComm.clusterDB.beginDate, firstComm.clusterDB.endDate)
      if (!weights.contains(firstComm))
        weights(firstComm) = new HashMap[ClusterFromRDB, Double]
      if (divisor > 0)
        weights(firstComm)(secondComm) = postCountFirstInSecond.toDouble / divisor
    }
    weights
  }

  /**
   * @return #posts written by users within the slice in the forum.
   */
  private def postCount(forumId: Long, userIds: Set[Long], begin: Date, end: Date): Long = inTransaction {
    from(posts, threads)((p,t) =>
      where(t.forumid === forumId and p.threadid === t.threadid and p.userid.in(userIds) and
        p.posteddate.between(begin, end))
        compute(countDistinct(p.postid))).single.measures
  }

  /**
   * @return #posts written in the forum within [begin, end]
   */
  private def postCount(forumId: Long, begin: Date, end: Date): Long = inTransaction {
    from(posts, threads)((p,t) =>
      where(t.forumid === forumId and p.threadid === t.threadid and p.posteddate.between(begin, end) and p.userid <> 0)
        compute(countDistinct(p.postid))).single.measures
  }

  /**
   * @return #posts written by users within the slice (in all fora)
   */
  private def postCount(userIds: Set[Long], begin: Date, end: Date):Long = inTransaction {
    from(posts, threads)((p,t) =>
      where(p.threadid === t.threadid and p.userid.in(userIds) and p.posteddate.between(begin, end))
        compute(countDistinct(p.postid))).single.measures
  }
}