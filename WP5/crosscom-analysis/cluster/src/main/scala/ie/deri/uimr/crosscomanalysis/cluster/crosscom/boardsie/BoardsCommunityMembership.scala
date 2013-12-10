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

package ie.deri.uimr.crosscomanalysis.cluster.crosscom.boardsie

import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema.networkSlice
import org.squeryl.PrimitiveTypeMode._
import java.util.Date
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.{ClusterIterator, SliceIterator, DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterFormats
import ie.deri.uimr.crosscomanalysis.graphdb.importers.DBGraphImporter
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDBWithSubgraph
import ie.deri.uimr.crosscomanalysis.util.Functions.offset
import org.apache.commons.math.linear.{RealMatrixPreservingVisitor, RealMatrix, OpenMapRealMatrix}
import ie.deri.uimr.crosscomanalysis.cluster.centrality.{OutDegreeCentrality, InDegreeCentrality, PageRankCentrality, NodeWithinClusterCentrality}
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.CommunityInfluenceLike

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/11/2011
 * Time: 17:33
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object BoardsCommunityMembership extends SessionFactorySetter with Logging with DBArgsParser with DBGraphImporter
  with CommunityInfluenceLike {

  override val COMMAND_NAME = "boardsie-comm-membership"

  lazy val commCount = totalNumberOfFora
  lazy val userCount = totalNumberOfUsers

  override def main(args: Array[String]) {
    mainStub(args) {
      compute()
    }
  }

  override protected def commandLineOptions = DB_OPT :: SLICE_TYPE_OPT :: HELP_OPT :: DIR_OPT :: Nil

  def compute(sliceType: Int = getSliceType, db: String = getOptValue(DB_OPT), outDir: File = new File(getOptValue(DIR_OPT))) {
    setUpSessionFactory(db)
    outDir.mkdirs()
    transaction {
      val maxSID = maxSliceId(sliceType)
      for (slice <- new SliceIterator(sliceType, None, None)) {
        log.info("Processing " + slice)
        val memberships = communityMembership(slice.beginDate, slice.endDate)
        outputMatrix(memberships, new File(outDir, offset(maxSID, slice.id) + slice.id + "_" + "memberships.csv"))
      }
    }
  }

  /**
   * @return User X Communities matrix, whose rows contain users' membership coefficients.
   */
  def communityMembership(begin: Date, end: Date): OpenMapRealMatrix = {
    val membershipMatrix = new OpenMapRealMatrix(userCount + 1, commCount + 1)
    transaction {
      for (gwm <- from(posts, threads)((p, t) =>
        where(p.threadid === t.threadid and p.posteddate.between(begin, end) and p.userid <> 0)
          groupBy(t.forumid, p.userid)
          compute (count(p.postid)))) {
        val (forumid, userid) = gwm.key
        membershipMatrix.setEntry(userid.toInt, forumid.toInt, gwm.measures)
      }
    }
    // divide elements of rows by their sums
    rowNormalizeMatrix(membershipMatrix)
  }

  private def totalNumberOfUsers = transaction {
    from(posts)(p => where(p.userid <> 0) compute (max(p.userid))).single.measures.get.toInt
  }

  private def totalNumberOfFora = transaction {
    from(posts, threads)((p, t) => where(p.userid <> 0 and p.threadid === t.threadid) compute (max(t.forumid))).single.measures.get.toInt
  }
}