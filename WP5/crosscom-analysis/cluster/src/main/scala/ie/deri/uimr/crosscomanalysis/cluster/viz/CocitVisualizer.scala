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

package ie.deri.uimr.crosscomanalysis.cluster.viz

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import collection.mutable.{MutableList, HashMap}
import com.tinkerpop.blueprints.pgm.{Edge, Vertex}
import org.squeryl.PrimitiveTypeMode._
import java.io.File
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions._
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.importers.DBGraphImporter
import ie.deri.uimr.crosscomanalysis.util.Functions.{filePath, offset}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import org.apache.commons.cli.{Option => CliOption}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/02/2011
 * Time: 18:25
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object CocitVisualizer extends SessionFactorySetter with Logging with DBGraphImporter with DBArgsParser with ClusterArgsParser {

  override val COMMAND_NAME = "cocit-vis"

  private val edgeWeights = new HashMap[Edge, Double]
  private val renderer: Renderer = JUNGRenderer(edgeWeights)
  private val nodeSize = new HashMap[Vertex, Double]

  def visualize(db: String, sliceType: Int, clusterFormat: Int, flag: Option[String], outDir: File) {
    if (!outDir.exists) outDir.mkdirs
    setUpSessionFactory(db)
    transaction {
      val maxSliceId = from(networkSlice)(ns => where(ns.sliceType === sliceType) compute (max(ns.id))).single.measures
      for (slice <- from(networkSlice)(ns =>
        (where(ns.sliceType === sliceType) select (ns) orderBy (ns.beginDate).asc))) {
        val clusters = new MutableList[(Int, Set[Vertex])]
        val g = loadGraph(slice.id, edgeWeights)
        val query = if (flag.isDefined) from(cluster)(c => (where(c.sliceId === slice.id and c.format === clusterFormat and c.flag === flag) select (c)))
        else from(cluster)(c => (where(c.sliceId === slice.id and c.format === clusterFormat) select (c)))
        for (cluster <- query) {
          val cStructure = from(clusterStructure)(cs => (where(cs.id === cluster.id)) select (cs.vertexid)).toSet
          val clusterVertices = cStructure.collect {
            case id: Long => g.getVertex(id)
          }
          clusters += ((cluster.index, clusterVertices))
        }
        val scorer = new BetweennessCentrality[Vertex, Edge](asUndirectedGraphJung(g), edgeWeights.mapValues(_.asInstanceOf[java.lang.Double]))
        g.getVertices.foreach(v => nodeSize(v) = scorer.getVertexScore(v).doubleValue)
        renderer.render(g, clusters.toSeq, nodeSize, new File(filePath(outDir, offset(maxSliceId.get, slice.id) + slice.id + "_", slice.beginDate, slice.endDate, ".png")))
      }
    }
  }

  override def main(args: Array[String]) {
    val line = parseArgs(args)
    if (!printHelpIfAsked) {
      visualize(getOptValue(DB_OPT), getSliceType, getClusterFormat, getClusterFlag, new File(getOptValue(DIR_OPT)))
    }
  }

  override protected def commandLineOptions = DB_OPT :: SLICE_TYPE_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT :: DIR_OPT :: HELP_OPT :: Nil
}