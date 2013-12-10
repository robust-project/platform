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

import ie.deri.uimr.crosscomanalysis.db.BufferedInsert
import java.util.{Calendar, Date}
import ie.deri.uimr.crosscomanalysis.util.Functions._
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.tables.graph._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.schemas.ArnetSchema._
import ie.deri.uimr.crosscomanalysis.cluster.ClusterFormats

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/08/2013
 * Time: 17:51
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 *
 * Slices a co-authorship network.
 */
object ArnetCoauthorshipSlicer extends Slicer with BufferedInsert {

  override val COMMAND_NAME = "arnet-ca-slicer"

  /**
   * @param beginSlice Begin date of the slice
   * @param endSlice End date of the slice
   * @param sliceType Type of the slice to be created (edge types, e.g. 'cocit/cocitation' are not supported currently)
   */
  protected def createSlice(beginSlice: Date, endSlice: Date, sliceType: Int) {
    if (!SliceTypes(sliceType).toString.endsWith("_ca"))
      throw new IllegalArgumentException("The type of slice is not coauthorship: " + SliceTypes(sliceType).toString)

    val beginYear = year(beginSlice)
    val endYear = year(endSlice)
    transaction {
      // create network slice
      val ns = new NetworkSlice(beginSlice, endSlice, sliceType)
      networkSlice insert ns
      // load authorships
      val paperToAuthor = from(authorship, paper)((a, p) =>
        where(a.paperid === p.id and p.year.between(beginYear, endYear))
          select(a.paperid, a.authorid)).toMap
      // load edges
      var edges = Map.empty[(Int, Int), Long] // author, author -> weight/count
      for (res <- from(authorship,authorship,paper)((a1,a2,p) =>
        where(a1.paperid === p.id and a2.paperid === p.id and a1.authorid <> a2.authorid and p.year.between(beginYear, endYear))
        groupBy(a1.authorid, a2.authorid)
        compute(countDistinct(p.id))
      )) {
        val author = res.key._1
        val coauthor = res.key._2
        edges += (author, coauthor) -> res.measures
      }
      // store edges
      bufferedInsert(edges, networkSliceStructure, Some("Edges processed: ")) {
        edge =>
          val ((source, sink), w) = edge
          new NetworkSliceStructure(ns.id, source, sink, w, None)
      }
      // load and store community structure
      var currentCommunity = Option.empty[Cluster]
      var nodes = Set.empty[Int]
      bufferedInsert(from(paper, authorship)((p, a) =>
        where(p.id === a.paperid and p.year.between(beginYear, endYear))
          select(a.authorid, p.venue)
          orderBy (p.venue)).distinct, clusterStructure, Some("Processed community members ")) {
        r =>
          val (authorid, venueid) = r
          if (currentCommunity.isEmpty || currentCommunity.get.index != venueid) {
            // create new community
            val c = new Cluster(ClusterFormats.EXPLICIT.id, venueid, venueid, beginSlice, endSlice, ns.id, None, None, None, None)
            cluster insert c
            currentCommunity = Some(c)
          }
          nodes += authorid
          new ClusterStructure(currentCommunity.get.id, authorid, None, None)
      }
      // store nodes / net. slice nodes (even the single ones)
      bufferedInsert(nodes, networkSliceVertex, Some("Processed nodes ")) {
        n => new NetworkSliceVertex(ns.id, n)
      }
    }
  }

  /**
   * @return (begin date, end date) of the data to be sliced.
   */
  protected def interval = (parseDate(getOptValue(BEGIN_DATE_OPT)), parseDate(getOptValue(END_DATE_OPT), end = true))

  override def main(args: Array[String]) {
    mainStub(args) {
      slice()
    }
  }

  private def year(date: Date): Int = {
    val cal = Calendar.getInstance()
    cal.setTime(date)
    cal.get(Calendar.YEAR)
  }

  override protected def commandLineOptions = BEGIN_DATE_OPT :: END_DATE_OPT :: super.commandLineOptions
}