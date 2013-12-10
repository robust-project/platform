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

package ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif

import org.apache.commons.cli.Option
import ie.deri.uimr.crosscomanalysis.db.{ClusterIterator, SessionFactorySetter, DBArgsParser}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import io.Source
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.util.{Parallel, Logging}
import ie.deri.uimr.crosscomanalysis.graphdb.jgrapht.DBGraphLoader
import org.jgrapht.graph.DefaultWeightedEdge
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.jgrapht.{ICM, LTM}
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.graphdb._
import org.apache.commons.math.random.MersenneTwister
import org.apache.commons.cli

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 16/12/2011
 * Time: 13:37
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Community-seeding inf. diffusion experiment. The input file contains seed community ids per network slice:
 * 'slice_id com1,com2,...,comN' or, in the case of Greedy Maximisation, it also contains # users sampled per community:
 * 'slice_id uss com1,com2,...,comN'.
 */

object XComInfCascadeExperiment extends DBArgsParser with ClusterArgsParser with Logging with SessionFactorySetter with
XComInfDiffusion with Parallel {

  override val COMMAND_NAME = "inf-cascade-exp"

  val START_COMM_OPT = new Option("sc", "start-comm", true, "indices of starting communities separated by comma, eg. '1,2,3'")
  val START_COUNT_OPT = new Option("nc", "start-count", true, "number of starting communities")
  val START_USER_SAMPLE_RANGE_OPT = new Option("usr", "user-sample-range", true, "range of the sample of users in each targeted community, e.g. '1-10'")
  val MODEL_OPT = new Option("mo", "model", true, "either 'linear-threshold' or 'ind-cascade'")
  val WEIGHT_VAL_OPT = new Option("wv", "weight-value", true, "the global transmission probability")
  val SEED_SAMPLE_MEMBERSHIP_DIR_OPT = new cli.Option("sd", "seed-membership-dir", true, "directory with membership for seed sampling for each slice")
  val COMMUNITY_MEMBERSHIP_DIR_OPT = new cli.Option("cd", "community-membership-dir", true, "directory with community membership for each slice")

  override def main(args: Array[String]) {
    mainStub(args) {
      setParallelismDegree()
      setUpSessionFactory(getOptValue(DB_OPT))
      val random = new MersenneTwister(getOptValue(SEED_OPT).toInt)
      val outDir = new File(getOptValue(DIR_OPT))
      outDir.mkdirs()
      val cascSizesOut = new PrintWriter(getOptValue(OUTFILE_OPT))
      cascSizesOut.println("slice,sample,diffusion,k,uss,activeCount,caf")

      for (line <- Source.fromFile(getOptValue(INFILE_OPT)).getLines()) {
        val inputTokens = line.split("\\s+")
        val sliceId = inputTokens(0).toLong
        log.info("Simulating for slice " + sliceId)
        val (seedComm, uss) =
          if (inputTokens.length == 2) {
            (inputTokens(1).split(",").map(_.toInt).toList, None)
          } else if (inputTokens.length == 3) {
            (inputTokens(2).split(",").map(_.toInt).toList, Some(inputTokens(1).toInt))
          } else sys.error("Invalid input: " + line)
        val graph = transaction {
          val g = DBGraphLoader.loadGraph(sliceId, directed = !hasOption(UNDIRECTED_OPT))
          if (hasOption(WEIGHT_VAL_OPT)) {
            setWeights(getOptValue(WEIGHT_VAL_OPT).toDouble, g)
            g
          } else {
            normalizeWeights(g, normalizeByOutStrength = true)
            g
          }
        }
        val (nonEmptyCommunities, membership) = loadMembership(new File(getOptValue(SEED_SAMPLE_MEMBERSHIP_DIR_OPT),
          sliceId + ".csv"))
        val (_, postCountMembership) = loadMembership(new File(getOptValue(COMMUNITY_MEMBERSHIP_DIR_OPT), sliceId + ".csv"))
        val clusters = transaction {
          new ClusterIterator(sliceId, getClusterFormat, getClusterFlag).filter(c =>
            nonEmptyCommunities.contains(c.index)).map(new ClusterFromRDB(_)).toList
        }
        val model = getOptValue(MODEL_OPT) match {
          case "linear-threshold" => new LTM[DefaultWeightedEdge](graph)
          case "ind-cascade" => new ICM[DefaultWeightedEdge](graph)
        }
        val repetitions = getOptValue(REPETITIONS_OPT).toInt
        val randoms = for (r <- 1 to repetitions) yield new MersenneTwister(random.nextInt)
        for (k <- 1 to seedComm.size) {
          val startComm = seedComm.take(k)
          log.info("Running simulation for seeds " + startComm.mkString(","))
          def simulateForUSS(sampleSize: Int) = {
            log.info("Running simulation for user sample size " + sampleSize)
            for (sampleRep <- 1 to repetitions) {
              // sample the seed nodes from the targeted communities
              val active =
                activateCommunityNodes(clusters.filter(c => startComm.contains(c.index)), sampleSize, random, membership)
              def run(diffusionRep: Int) = {
                val casc = model.simulate(active, randoms(diffusionRep - 1))
                val activatedNodes = casc.flatten.toSet
                (diffusionRep, activatedNodes.size, countActiveCommunities(activatedNodes, clusters, postCountMembership))
              }
              for ((diffusion, activeCount, caf) <- (1 to repetitions).par.map(run).seq) {
                cascSizesOut.println("%d,%d,%d,%d,%d,%d,%e".format(sliceId, sampleRep, diffusion, k, sampleSize,
                  activeCount, caf))
              }
            }
          }
          if (uss.isEmpty) {
            // read the user sample size from the command line input
            val Array(start, end) = getOptValue(START_USER_SAMPLE_RANGE_OPT).split("-").map(_.toInt)
            for (sampleSize <- start to end) {
              simulateForUSS(sampleSize)
            }
          } else {
            simulateForUSS(uss.get)
          }
        }
      }
      cascSizesOut.close()
    }
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: INFILE_OPT :: UNDIRECTED_OPT ::
    CLUSTER_FLAG_OPT :: CLUSTER_FORMAT_OPT :: SEED_OPT :: START_USER_SAMPLE_RANGE_OPT ::
    MODEL_OPT :: REPETITIONS_OPT :: DIR_OPT :: PARALLELISM_DEGREE_OPT :: OUTFILE_OPT :: WEIGHT_VAL_OPT ::
    SEED_SAMPLE_MEMBERSHIP_DIR_OPT :: COMMUNITY_MEMBERSHIP_DIR_OPT :: Nil

}