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

package ie.deri.uimr.crosscomanalysis.cluster.centrality

import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.NetworkSlice

/**
 * Created with IntelliJ IDEA.
 * User: vacbel
 * Date: 4/7/13
 * Time: 8:54 PM
 * Computes in-degree for each slice of Arnet citation network
 */
object BoardsLayeredInDegree extends LayeredInDegreeCentrality with SessionFactorySetter with DBArgsParser {

  override val COMMAND_NAME: String = "boards-layered-indegree"

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      processSlices()
    }
  }

  /**
   * @return Mapping from a content, i.e. post, paper, etc., node to its author id.
   */
  protected def nodeToActor(slice: NetworkSlice): Map[Long, Set[Long]] = inTransaction {
    from(posts)(p =>
      where(p.posteddate >= slice.beginDate and p.posteddate <= slice.endDate)
        select(p.postid, p.userid)).map(x => (x._1, Set(x._2))).toMap
  }

  /**
   * @return Mapping from a content, i.e. post, paper, etc., node to the venue it was published at, e.g. forum or conference.
   */
  protected def nodeToVenue(slice: NetworkSlice): Map[Long, Long] = inTransaction {
    from(posts, threads)((p, t) =>
      where(p.threadid === t.threadid and p.posteddate >= slice.beginDate and p.posteddate <= slice.endDate)
        select ((p.postid, t.forumid))).toMap
  }

  /**
   * @param slice Network slice currently being exported
   * @return Set of edges to export for the slice.
   */
  protected def edgesInSlice(slice: NetworkSlice): Set[(Long, Long)] = inTransaction {
    from(replies)(r =>
      where(r.origposteddate >= slice.beginDate and r.origposteddate <= slice.endDate and
        r.replyingposteddate >= slice.beginDate and r.replyingposteddate <= slice.endDate)
        select(r.replyingpostid, r.originalpostid)).toSet
  }
}
