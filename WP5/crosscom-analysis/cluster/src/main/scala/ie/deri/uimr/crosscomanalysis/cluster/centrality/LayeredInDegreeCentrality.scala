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

import java.io.{PrintWriter, File}
import scala.io.Source
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.apache.commons.cli
import ie.deri.uimr.crosscomanalysis.db.DBArgsParser
import ie.deri.uimr.crosscomanalysis.db.tables.graph.NetworkSlice
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._

/**
 * Created with IntelliJ IDEA.
 * User: vacbel
 * Date: 4/7/13
 * Time: 6:43 PM
 * Common ancestor for translators of edge mixtures / layers obtained by the citation influence model. See
 * https://github.com/bgamari/bayes-stack and citation_influence_edge_mixtures.scala.
 */
abstract class LayeredInDegreeCentrality extends Logging with DBArgsParser with ClusterArgsParser {

  protected val MIN_PROB_OPT = new cli.Option("mp", "min-prob", true, "minimum edge topic probability")
  private var actorMap = Map.empty[Long,Set[Long]]
  private var venueMap = Map.empty[Long,Long]
  private var edges = Set.empty[(Long,Long)]
  private lazy val minProb = getOptValue(MIN_PROB_OPT).toDouble

  protected def processSlices() {
    val begin = getBeginDate.get
    val end = getEndDate.get
    val dir = new File(getOptValue(DIR_OPT))
    val centralityOut = new PrintWriter(new File(dir, "indegree.csv"))
    val membershipOut = new PrintWriter(new File(dir, "membership.csv"))
    centralityOut.println("author,sourceVenue,sinkVenue,topic,slice,inDegree")
    membershipOut.println("author,sourceVenue,sinkVenue,topic,slice,membership")
    log.debug("Layering slices between " + begin.toString + " and " + end.toString)
    inTransaction {
      for (slice <- from(networkSlice)(ns =>
        where(ns.beginDate >= begin and ns.endDate <= end and ns.sliceType === getSliceType)
        select(ns))) {
        log.info("Processing " + slice)
        processLayers(dir, slice, centralityOut, membershipOut)
      }
    }
    centralityOut.close()
    membershipOut.close()
  }

  protected def processLayers(dir: File, slice: NetworkSlice, centralityOut: PrintWriter, membershipOut: PrintWriter) {
    edges = edgesInSlice(slice)
    actorMap = nodeToActor(slice)
    venueMap = nodeToVenue(slice)
    dir.listFiles().filter(_.getName().matches("\\d+\\.dat")).foreach(l => processLayer(l, centralityOut, membershipOut, slice.id))
  }

  private def processLayer(layerFile: File, centralityOut: PrintWriter, membershipOut: PrintWriter, sliceId: Long) {
    val layer = layerFile.getName.split("\\.")(0).toInt
    var centrality = Map.empty[(Long, Long, Long), Int] // author id, source venue id, sink venue id -> in-degree
    var replyCount = Map.empty[(Long, Long, Long), Int] // (author id, source venue id, sink venue id) -> # replies the author has posted
    for (line <- Source.fromFile(layerFile).getLines();
         Array(source, sink, prob) = line.split("\\s+")
         if (prob.toDouble >= minProb) && edges.contains(source.toLong -> sink.toLong)) {
      val sinkActors = actorMap(sink.toLong)
      val sourceActors = actorMap(source.toLong)
      val sourceVenue = venueMap(source.toLong)
      val sinkVenue = venueMap(sink.toLong)
      def increment(actor: Long, m: Map[(Long,Long,Long), Int]) = {
        if (m.contains((actor, sourceVenue, sinkVenue)))
          (actor, sourceVenue, sinkVenue) -> (m((actor, sourceVenue, sinkVenue)) + 1)
        else
          (actor, sourceVenue, sinkVenue) -> 1
      }
      sinkActors.foreach(a => centrality += increment(a, centrality))
      sourceActors.foreach(a => replyCount += increment(a, centrality))
    }
    for (((author, sourceVenue, sinkVenue), inDegree) <- centrality) {
      centralityOut.print("%d,%d,%d,%d,%d,%d\n".format(author, sourceVenue, sinkVenue, layer, sliceId, inDegree))
    }
    for (((author, sourceVenue, sinkVenue), replyCount) <- replyCount) {
      membershipOut.print("%d,%d,%d,%d,%d,%d\n".format(author, sourceVenue, sinkVenue, layer, sliceId, replyCount))
    }
  }

  /**
   * @param slice Network slice currently being exported
   * @return Set of edges to export for the slice.
   */
  protected def edgesInSlice(slice: NetworkSlice): Set[(Long,Long)]

  /**
   * @param slice Network slice currently being exported
   * @return Mapping from a content, i.e. post, paper, etc., node to its author ids.
   */
  protected def nodeToActor(slice: NetworkSlice): Map[Long, Set[Long]]

  /**
   * @param slice Network slice currently being exported
   * @return Mapping from a content, i.e. post, paper, etc., node to the venue it was published at, e.g. forum or conference.
   */
  protected def nodeToVenue(slice: NetworkSlice): Map[Long, Long]

  override protected def commandLineOptions: List[cli.Option] = MIN_PROB_OPT :: HELP_OPT :: DIR_OPT ::
    DB_OPT :: BEGIN_DATE_OPT :: END_DATE_OPT :: SLICE_TYPE_OPT :: Nil
}