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
import ie.deri.uimr.crosscomanalysis.db.schemas.ArnetSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.NetworkSlice

/**
 * Created with IntelliJ IDEA.
 * User: vacbel
 * Date: 4/7/13
 * Time: 8:52 PM
 * Computes in-degree for each slice of Arnet citation network
 */
object ArnetLayeredInDegree extends LayeredInDegreeCentrality with SessionFactorySetter with DBArgsParser {

  override val COMMAND_NAME: String = "arnet-layered-indegree"

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      processSlices()
    }
  }

  /**
   * @param slice Network slice currently being exported
   * @return Mapping from a content, i.e. post, paper, etc., node to its author ids.
   */
  protected def nodeToActor(slice: NetworkSlice) = inTransaction {
    var res = Map.empty[Long, Set[Long]]
    for ((paper, author) <- from(paper, authorship)((p, a) =>
      where(p.id === a.paperid and p.year.between(slice.beginYear, slice.endYear))
        select(p.id, a.authorid))) {
      if (res.contains(paper))
        res += paper.toLong -> (res(paper.toLong) + author.toLong)
      else
        res += paper.toLong -> Set(author.toLong)
    }
    res
  }

  /**
   * @param slice Network slice currently being exported
   * @return Mapping from a content, i.e. post, paper, etc., node to the venue it was published at, e.g. forum or conference.
   */
  protected def nodeToVenue(slice: NetworkSlice) = inTransaction {
    from(paper)(p =>
      where(p.year.between(slice.beginYear, slice.endYear))
      select(p.id, p.venue)).map(x => (x._1.toLong, x._2.toLong)).toMap
  }

  /**
   * @param slice Network slice currently being exported
   * @return Set of edges to export for the slice.
   */
  protected def edgesInSlice(slice: NetworkSlice): Set[(Long, Long)] = inTransaction {
    from(citationWithYear)(c =>
      where(c.yearCited.between(slice.beginYear, slice.endYear) and c.yearCiting.between(slice.beginYear, slice.endYear))
        select(c.citing, c.cited)).map(x => (x._1.toLong, x._2.toLong)).toSet
  }
}
