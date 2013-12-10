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

package ie.deri.uimr.crosscomanalysis.cluster.comparator

import ie.deri.uimr.crosscomanalysis.util.Logging
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import collection.mutable.HashMap
import collection.JavaConversions.{iterableAsScalaIterable, setAsJavaSet}
import ie.deri.uimr.crosscomanalysis.jung.algorithms.scoring.SuccessiveGroupBetweenness
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions._
import com.tinkerpop.blueprints.pgm.{Edge, Vertex}
import ie.deri.uimr.crosscomanalysis.cluster.model.{ClusterWithSubgraph, ClusterFromRDB}
import ie.deri.uimr.crosscomanalysis.graphdb.DBGraphLoader
import java.util.Calendar
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/02/2011
 * Time: 11:18
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object ComputeGroupBetweenness extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser {


  override val COMMAND_NAME = "compute-sgb"

  def compute(db:String, start: Int, end: Int, clusterFormat: Int, sliceType: Int) {
    setUpSessionFactory(db)
    val cal = Calendar.getInstance
    for (beginYear <- start to end) {
      cal.clear
      cal.set(beginYear, 0, 1)
      log.info("Starting to process start year: " + beginYear)
      transaction {
        val slice = (from(networkSlice)(ns =>
          (where(ns.beginDate === cal.getTime and ns.sliceType === sliceType) select (ns)))).head
        val weights = new HashMap[Vertex, Double]
        val graphLoader = new DBGraphLoader(slice.id)
        log.debug("About to create SGB instance ...")
        val sgb = new SuccessiveGroupBetweenness[Vertex, Edge](asUndirectedGraphJung(graphLoader.graph), mapToTransformer(graphLoader.weights.mapValues(_.toDouble)), false)
        log.debug("... SGB instantiated")
        for (c <- from(cluster)(c =>
          (where(c.sliceId === slice.id and c.format === clusterFormat) select (c)))) {
          val loadedCluster = new ClusterFromRDB(c) with ClusterWithSubgraph {
            val graph = graphLoader.graph
          }
          val vertexGroup = setAsJavaSet(loadedCluster.subgraph.getVertices.toSet)
          c.betweenness = Some(sgb.getVertexGroupScore(vertexGroup).doubleValue)
          if (c.betweenness.get < 0) {
            log.warn("Negative GBC " + c.betweenness.get + ", resetting to 0")
            c.betweenness = Some(0d)
          }
          c.normBetweenness = Some(sgb.getNormalizedGBC(vertexGroup, c.betweenness.get, false))
          c.normOverallBetweenness = Some(sgb.getNormalizedGBC(vertexGroup, c.betweenness.get, true))
          log.info("Setting GBC of " + loadedCluster + " to: " + c.betweenness.get)
          transaction {
            cluster update c
          }
        }
      }
    }
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked)
      compute(getOptValue(DB_OPT), getOptValue(BEGIN_YEAR_OPT).toInt, getOptValue(END_YEAR_OPT).toInt, getClusterFormat, getSliceType)
  }

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: BEGIN_YEAR_OPT :: END_YEAR_OPT :: CLUSTER_FORMAT_OPT :: SLICE_TYPE_OPT :: Nil
}