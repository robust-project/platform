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

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.util.Config.config
import org.neo4j.kernel.EmbeddedGraphDatabase
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.CSXGraphProperties._
import ie.deri.uimr.crosscomanalysis.db.tables.graph._
import collection.mutable.MutableList
import org.neo4j.index.lucene.LuceneIndexService
import collection.JavaConversions.iterableAsScalaIterable
import collection.mutable.HashMap
import com.tinkerpop.blueprints.pgm.impls.neo4j.util.Neo4jVertexSequence
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import math._
import ie.deri.uimr.crosscomanalysis.graphdb.traversals.Cocitation.authorCocitations
import com.tinkerpop.blueprints.pgm.Edge
import ie.deri.uimr.crosscomanalysis.db.squeryl.adapters.PostgreSqlAdapter
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import com.tinkerpop.blueprints.pgm.impls.neo4j.{Neo4jVertex, Neo4jGraph}
import java.util.{TimeZone, Calendar}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/03/2011
 * Time: 15:00
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object CSXSlicer extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser {

  override val COMMAND_NAME = "csx-slicer"
  private val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
  private lazy val graph = new Neo4jGraph(new EmbeddedGraphDatabase(config.get("graphdb.csxcitegraph.path").get,
    EmbeddedGraphDatabase.loadConfigurations("neo4j.properties")))
  private lazy val index = new LuceneIndexService(graph.getRawGraph)
  private lazy val authors = new Neo4jVertexSequence(index.getNodes(NODE_TYPE, AUTHOR), graph).toSet

  def slice(db: String, windowSize: Int, overlap: Int, sliceType: Int, shutdownGraph: Boolean = true) {
    setUpSessionFactory(db, new PostgreSqlAdapter)

    log.info("Beginning slicing process")

    val minYear = 1983 // first cocitations appear in '83
    val maxYear = 2010 // last year of the data

    // generate slice sequence
    for (beginSlice <- minYear to ((maxYear - windowSize + 1), windowSize - overlap))
      createSlice(beginSlice, beginSlice + windowSize - 1, sliceType)
    // check if there's something left out (because it didn't fit the window size/overlap)
    val remain = (maxYear - minYear + overlap + 1) % (windowSize - overlap)
    if (remain > 0)
      createSlice(maxYear - remain, maxYear, sliceType)

    if (shutdownGraph) graph.shutdown()
  }

  private def createSlice(beginSlice: Int, endSlice: Int, sliceType: Int) {
    cal.set(beginSlice, 0, 1)
    val beginDate = cal.getTime
    cal.set(endSlice, 11, 1)
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    val endDate = cal.getTime
    val cocitEdges = new HashMap[(Long, Long), Long] // authorid x authorid -> weight

    // infer cocitations between authors - ignore the default author cluster '0'
    for (authorV <- authors if authorV(AUTH_CLUSTER_ID) != 0) {
      val cocitPipe = authorCocitations(authorV, beginSlice, endSlice)
      for (cocitedAuthorV <- cocitPipe if cocitedAuthorV(AUTH_CLUSTER_ID) != 0) {
        val authorId = authorV(AUTH_CLUSTER_ID)
        val cocitedAuthorId = cocitedAuthorV(AUTH_CLUSTER_ID)
        //          log.debug("cocitation pipe: " + cocitPipe.rawPipeline)
        // 5th pipe is bothE(COCIT_COUNT)
        val cocitCount = cocitPipe.pipe(4).getPath.last.asInstanceOf[Edge](COCIT_COUNT)
        val (authPair, w) =
          if (cocitEdges.contains((authorId, cocitedAuthorId)))
            ((authorId, cocitedAuthorId), cocitEdges((authorId, cocitedAuthorId)))
          else if (cocitEdges.contains((cocitedAuthorId, authorId)))
            ((cocitedAuthorId, authorId), cocitEdges((cocitedAuthorId, authorId)))
          else ((authorId, cocitedAuthorId), 0l)
        cocitEdges(authPair) = w + cocitCount
      }
    }
    // store cocitations into DB
    val ns = new NetworkSlice(beginDate, endDate, sliceType)
    transaction {
      networkSlice insert ns
    }
    log.info(ns.toString)
    val buffer = new MutableList[NetworkSliceStructure]
    for (((authID, cocitauthID), count) <- cocitEdges) {
      buffer += new NetworkSliceStructure(ns.id, authID, cocitauthID,
        transformWeight(authID, cocitauthID, count, sliceType, beginSlice, endSlice), None)
      if (buffer.size >= 1000) {
        transaction {
          networkSliceStructure insert buffer
        }
        buffer.clear()
      }
    }
    if (!buffer.isEmpty) {
      transaction {
        networkSliceStructure insert buffer
      }
    }
    log.info(cocitEdges.size + " cocit edges between authors have been inferred")
  }

  /**
   * @param authID cluster author id
   * @param cocitauthID cluster id of the cocited author
   * @param w cocitation count
   * @param sliceType type of the slice
   * @param start begin year
   * @param end end year
   */
  private def transformWeight(authID: Long, cocitauthID: Long, w: Long, sliceType: Int, start: Int, end: Int) = {
    def citationCount(authorId: Long) = {
      val authorV = new Neo4jVertex(index.getSingleNode(AUTH_CLUSTER_ID, authorId), graph)
      val count = authorV.trav[Long].outE(AUTHOR_OF).inV.strain {
        v => v(YEAR) >= start && v(YEAR) <= end
      }.inE(CITES).count.cap.head
      if (count == 0) 1l else count
    }
    def cocitScore() = {
      val authorCitCount = citationCount(authID)
      val cocitauthCitCount = citationCount(cocitauthID)
      pow(w / 2d, 2) / (authorCitCount.min(cocitauthCitCount) * ((authorCitCount + cocitauthCitCount) / 2))
    }

    SliceTypes(sliceType) match {
      // divide the weight by two because cocit is undirected relation and during the traversal it was counted twice
      case SliceTypes.S3O2_COCITATION => w / 2d
      case SliceTypes.S3O2_COCIT => cocitScore()
      case SliceTypes.S3O1_COCIT => cocitScore()
      case SliceTypes.S3O0_COCIT => cocitScore()
      case SliceTypes.S2O1_COCIT => cocitScore()
      case SliceTypes.S2O0_COCIT => cocitScore()
      case SliceTypes.S1O0_COCIT => cocitScore()
    }
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      slice(getOptValue(DB_OPT), getOptValue(WINDOW_SIZE_OPT).toInt, getOptValue(WINDOW_OVERLAP_OPT).toInt, getSliceType)
    }
  }

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: WINDOW_SIZE_OPT :: WINDOW_OVERLAP_OPT :: SLICE_TYPE_OPT :: Nil
}