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

package ie.deri.uimr.crosscomanalysis.cluster.optimisation

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.PartialNetInfCascadeExperiment.{TARGET_SIZE_OPT, NORM_BY_OUTS_OPT, WEIGHT_VAL_OPT}
import ie.deri.uimr.crosscomanalysis.graphdb.importers.EdgeListFileLoader
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import util.Random
import ie.deri.uimr.crosscomanalysis.graphdb._
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.XComInfCascadeExperiment.MODEL_OPT
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.{IndependentCascadeModel, LinearThresholdModel}
import collection.Set
import com.tinkerpop.blueprints.pgm.{Edge, Vertex}
import scala.collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import org.apache.commons.math.random.MersenneTwister

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/10/2012
 * Time: 18:30
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
object GreedyVertexSeedFinder extends ClusterArgsParser with Logging {

  override val COMMAND_NAME = "greedy-vertex-seed"

  override def main(args: Array[String]) {
    mainStub(args) {
      val random = new MersenneTwister(getOptValue(SEED_OPT).toLong)
      val maxIter = getOptValue(MAX_ITER_OPT).toInt
      val repetitions = getOptValue(REPETITIONS_OPT).toInt
      val outDir = new File(getOptValue(DIR_OPT))
      outDir.mkdirs()
      val normOutS =
        if (hasOption(NORM_BY_OUTS_OPT)) {
          getOptValue(NORM_BY_OUTS_OPT) match {
            case "out" => Some(true)
            case "in" => Some(false)
            case _ => sys.error("Unknown normalizing parameter")
          }
        } else None

      val graphLoader = new EdgeListFileLoader(new File(getOptValue(INFILE_OPT)), DEFAULT_EDGE,
        normalizeByOutStrength = normOutS, directed = !hasOption(UNDIRECTED_OPT)) // default is directed network
      if (hasOption(WEIGHT_VAL_OPT)) {
        setWeights(getOptValue(WEIGHT_VAL_OPT).toDouble, graphLoader.graph)
        // if the default weight is set, then it might be necessary to normalize them
        if (normOutS.isDefined)
          normalizeWeights(graphLoader.graph, normOutS.get, DEFAULT_EDGE)
      }

      val model = getOptValue(MODEL_OPT) match {
        case "linear-threshold" => {
          new LinearThresholdModel(maxIter, graphLoader.graph, DEFAULT_EDGE, activateNow)
        }
        case "ind-cascade" => {
          new IndependentCascadeModel(maxIter, graphLoader.graph, None, DEFAULT_EDGE, spreadActivation)
        }
        case _ => sys.error("Unknown diffusion model")
      }
      log.info("Building the optimiser object and loading the graph")
      val optimiser = new VertexSeedGreedyOptimisation(model, repetitions, random,
        graphLoader.graph.getVertices.toSeq.par.map(v => v(ORIGID)).seq)
      val k = getOptValue(TARGET_SIZE_OPT).toInt
      assert(graphLoader.graph.getVertices.size >= k)

      log.info("Finding the optimal set")
      val out = new PrintWriter(new File(outDir, "seed-" + k + ".csv"))
      optimiser.maximise(k).foreach(out.println(_))
      out.close()
      log.info("Done. The found utility was: " + optimiser.util)
    }
  }

  def activateNow(active: Set[Vertex], thresholds: Map[Vertex, Double], v: Vertex): Boolean =
    v.getInEdges(DEFAULT_EDGE).filter(e => active.contains(e.getOutVertex)).map(_(WEIGHT)).sum >= thresholds(v)

  def spreadActivation(random: MersenneTwister, active: Set[Vertex], v: Vertex): Iterable[(Vertex, Vertex, Edge, Boolean)] = {
    v.getOutEdges(DEFAULT_EDGE).filterNot(e => active.contains(e.getInVertex())).map(e =>
      (v, e.getInVertex(), e, e(WEIGHT) >= random.nextDouble()))
  }

  override protected def commandLineOptions = INFILE_OPT :: TARGET_SIZE_OPT :: NORM_BY_OUTS_OPT :: WEIGHT_VAL_OPT ::
    MAX_ITER_OPT :: REPETITIONS_OPT :: SEED_OPT :: MODEL_OPT :: UNDIRECTED_OPT :: DIR_OPT :: Nil
}