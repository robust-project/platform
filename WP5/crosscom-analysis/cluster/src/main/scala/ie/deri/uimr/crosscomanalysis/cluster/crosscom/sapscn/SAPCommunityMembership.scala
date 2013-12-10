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

package ie.deri.uimr.crosscomanalysis.cluster.crosscom.sapscn

import ie.deri.uimr.crosscomanalysis.db.{SliceIterator, DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.graphdb.importers.DBGraphImporter
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.CommunityInfluenceLike
import java.io.File
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.util.Functions._
import java.util.Date
import org.apache.commons.math.linear.OpenMapRealMatrix
import ie.deri.uimr.crosscomanalysis.db.schemas.SAPSCN._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/06/2013
 * Time: 12:27
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
object SAPCommunityMembership extends SessionFactorySetter with Logging with DBArgsParser with DBGraphImporter
with CommunityInfluenceLike {
  override val COMMAND_NAME = "sap-comm-membership"

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
      for (gwm <- from(posts, threads, users)((p, t, u) =>
        where(p.threadid === t.threadid and p.posteddate.between(begin, end) and p.userid === u.userid)
          groupBy(t.forumid, u.longUserId)
          compute (count(p.postid)))) {
        val (forumid, userid) = gwm.key
        membershipMatrix.setEntry(userid.toInt, forumid.toInt, gwm.measures)
      }
    }
    // divide elements of rows by their sums
    rowNormalizeMatrix(membershipMatrix)
  }

  private def totalNumberOfUsers = transaction {
    from(posts, users)((p, u) => where(p.userid === u.userid) compute (max(u.longUserId))).single.measures.get.toInt
  }

  private def totalNumberOfFora = transaction {
    from(posts, threads)((p, t) => where(p.threadid === t.threadid) compute (max(t.forumid))).single.measures.get.toInt
  }
}
