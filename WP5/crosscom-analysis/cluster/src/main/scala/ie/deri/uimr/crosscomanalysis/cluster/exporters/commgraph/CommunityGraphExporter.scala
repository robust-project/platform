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

import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.apache.commons.cli.{Option => CliOption}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.util.Functions.{filePath, offset}
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE.forums
import java.io.{File, FileOutputStream}
import java.util.Date
import ie.deri.uimr.crosscomanalysis.graphdb.exporters._
import collection.mutable.{HashSet, MutableList}
import com.tinkerpop.blueprints.pgm.{Edge, Vertex, Graph}
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 29/09/2011
 * Time: 12:43
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object CommunityGraphExporter extends SessionFactorySetter with Logging with ClusterArgsParser with DBArgsParser {

  override val COMMAND_NAME = "export-community-graph"
  val BOARDS_COMM_DIGRAPH = new CliOption("bcdg", "boards-comm-digraph", false, "Exports the oriented version of comm. graph for Boards.ie")
  val GRAPH_ML = new CliOption("gml", "graphml", false, "Exports the graph in GraphML format")
  val EDGELIST = new CliOption("el", "edgelist", false, "Exports the graph in simple edge-list format")
  val GEXF = new CliOption("ge", "gephi", false, "Exports the graph in Gephi GE-XF format")
  val SELF_LOOP = new CliOption("sl", "self-loop", false, "Normalize edges to the total number of posts of the users of the outgoing community?")
  val JACCARD = new CliOption("j", "jaccard", false, "Should the undirected graph has normalized edges using Jaccard coef or just overlap sizes?")
  val BOARDSIE = new CliOption("b", "boards", false, "Add additional attributes from Boards.IE data-set")
  val DB_OUT_OPT = new CliOption("db", "database-out", true, "An optional slice type under which the graph will be stored into DB. If not set, nothing will be stored into DB.")

  def export(db: String = getOptValue(DB_OPT), sliceType: Int = getSliceType, clusterFlag: Option[String] = getClusterFlag,
             clusterFormat: Int = getClusterFormat, graphML: Boolean = hasOption(GRAPH_ML),
             edgeList: Boolean = hasOption(EDGELIST), boardsDigraph: Boolean = hasOption(BOARDS_COMM_DIGRAPH),
             selfLoop: Boolean = hasOption(SELF_LOOP), jaccard: Boolean = hasOption(JACCARD),
             gexf: Boolean = hasOption(GEXF), dir: File = new File(getOptValue(DIR_OPT)), boardsIE: Boolean = hasOption(BOARDSIE),
              dbOutSliceType: Option[Int] = getDBOutSliceType) {
    assert(!(selfLoop && jaccard), "Self loops and Jaccard does not make sense!")
    assert(!(boardsDigraph && jaccard), "Digraph and Jaccard does not make sense!")
    assert(!(selfLoop && !boardsDigraph), "Self-loops are only available for digraphs")
    assert(gexf || graphML || edgeList, "Some output format has to be set: GraphML, GEXF, or edgelist")
    setUpSessionFactory(db)
    dir.mkdirs()
    transaction {
      val maxSliceId = from(networkSlice)(ns => where(ns.sliceType === sliceType) compute (max(ns.id))).single.measures.get
      val graphSequence = new MutableList[(Graph, Date, Date)] // slice, begin, end
      val allVertices = new HashSet[Vertex]
      val allEdges = new HashSet[Edge]
      for (slice <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns) orderBy (ns.beginDate).asc)) {
        log.info("Processing " + slice)
        val commGraph =
          if (boardsDigraph) {
            log.debug("Loading directed graph " + (if (selfLoop) "with" else "without") + " self-loops")
            new DirectedBoardsIECommunityGraph(db, clusterFormat, clusterFlag, slice.id, selfLoop).graph
          } else {
            log.debug("Loading undirected " + (if (jaccard) "" else "non-") + "normalized")
            new UndirectedCommunityGraph(db, clusterFormat, clusterFlag, slice.id, jaccard).graph
          }
        if (boardsIE) labelBoardsIEClusters(commGraph)
        if (gexf) {
          graphSequence += ((commGraph, slice.beginDate, slice.endDate))
          allVertices ++= commGraph.getVertices
          allEdges ++= commGraph.getEdges
        }
        if (graphML) {
          val os = new FileOutputStream(filePath(dir, offset(maxSliceId, slice.id) + slice.id + "_", slice.beginDate, slice.endDate, ".graphml"))
          if (boardsDigraph)
            GraphMLVisoneDigraphWriter.outputGraph(commGraph, os)
          else
            GraphMLUndirGraphWriter.outputGraph(commGraph, os)
          os.close()
        }
        if (edgeList) {
          val os = new FileOutputStream(filePath(dir, offset(maxSliceId, slice.id) + slice.id + "_", slice.beginDate, slice.endDate, ".edges"))
          EdgeListWriter.outputGraph(commGraph, os)
          os.close()
        }
        if (dbOutSliceType.isDefined) {
          DBWriter.outputGraph(commGraph, dbOutSliceType.get, slice.beginDate, slice.endDate)
        }
      }
      if (gexf) {
        val outFile = new File(dir, "dynamic-community-graph.gexf")
        GEXFWriter.outputDynamicGraph(graphSequence, allVertices, allEdges, outFile, boardsDigraph)
      }
    }
  }

  override protected def commandLineOptions = DB_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT :: HELP_OPT ::
    SLICE_TYPE_OPT :: DIR_OPT :: BOARDS_COMM_DIGRAPH :: GRAPH_ML :: EDGELIST :: GEXF :: SELF_LOOP :: JACCARD :: BOARDSIE ::
    DB_OUT_OPT :: Nil

  override def main(args: Array[String]) {
    mainStub(args) {
      export()
    }
  }

  private def labelBoardsIEClusters(graph: Graph) {
    for (v <- graph.getVertices) {
      v(CLUSTER_NAME) = inTransaction {
        from(forums)(f => where(f.forumid === v(ORIGID)) select (f.title)).single.asInstanceOf[AnyRef]
      }
    }
  }

  private def getDBOutSliceType =
    if (hasOption(DB_OUT_OPT))
      Some(SliceTypes.withName(getOptValue(DB_OUT_OPT)).id)
    else
      None
}
