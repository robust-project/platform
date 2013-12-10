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

import ie.deri.uimr.crosscomanalysis.db.{ClusterIterator, SessionFactorySetter, DBArgsParser}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.util.{Parallel, Logging}
import java.io.{PrintWriter, File}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.graphdb._
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.PartialNetInfCascadeExperiment.{TARGET_SIZE_OPT, NORM_BY_OUTS_OPT, WEIGHT_VAL_OPT}
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.XComInfCascadeExperiment.MODEL_OPT
import org.apache.commons.math.random.MersenneTwister
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.loadMembership
import ie.deri.uimr.crosscomanalysis.graphdb.jgrapht.DBGraphLoader
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.jgrapht.{LTM, ICM}
import org.apache.commons.cli


/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/10/2012
 * Time: 18:50
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
object GreedyDBClusterSeedFinder extends DBArgsParser with ClusterArgsParser with Logging with SessionFactorySetter
with Parallel {

  override val COMMAND_NAME = "greedy-dbcluster-seed"
  val SLICE_BEGIN_OPT = new cli.Option("bs", "begin-slice", true, "ID of the slice to start with")
  val SLICE_END_OPT = new cli.Option("es", "end-slice", true, "ID of the slice to end with")
  val SEED_SAMPLE_MEMBERSHIP_DIR_OPT = new cli.Option("sd", "seed-membership-dir", true, "directory with membership for seed sampling for each slice")
  val COMMUNITY_MEMBERSHIP_DIR_OPT = new cli.Option("cd", "community-membership-dir", true, "directory with community membership for each slice")
  val START_USER_SAMPLE_RANGE_OPT = new cli.Option("usr", "user-sample-range", true, "range of the sample of users in each targeted community, e.g. '1-10'")
  val COMM_SPREAD_OPT = new cli.Option("cs", false, "True - utility based on community spread, False - utility based on user. spread")

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      setParallelismDegree()
      val outDir = new File(getOptValue(DIR_OPT))
      outDir.mkdirs()
      val seedMembershipDir = new File(getOptValue(SEED_SAMPLE_MEMBERSHIP_DIR_OPT))
      val commMembershipDir = new File(getOptValue(COMMUNITY_MEMBERSHIP_DIR_OPT))
      for (sliceId <- getOptValue(SLICE_BEGIN_OPT).toInt to getOptValue(SLICE_END_OPT).toInt) {
        log.info("Processing slice: " + sliceId)
        val seedMembershipFile = new File(seedMembershipDir, sliceId + ".csv") // filename has to have format "{sliceid}.csv"
        val commMembershipFile = new File(commMembershipDir, sliceId + ".csv")
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
        val (nonEmptyCommunities, seedMemberships) = loadMembership(seedMembershipFile)
        val (_, commMemberships) = loadMembership(commMembershipFile)
        val clusters = transaction {
          new ClusterIterator(sliceId, getClusterFormat, getClusterFlag).map(new ClusterFromRDB(_)).
            toList.filter(c => nonEmptyCommunities.contains(c.index))
        }
        val random = new MersenneTwister(getOptValue(SEED_OPT).toInt)
        val model = getOptValue(MODEL_OPT) match {
          case "linear-threshold" => new LTM(graph)
          case "ind-cascade" => new ICM(graph)
        }
        val repetitions = getOptValue(REPETITIONS_OPT).toInt
        val k = getOptValue(TARGET_SIZE_OPT).toInt
        val Array(start, end) = getOptValue(START_USER_SAMPLE_RANGE_OPT).split("-").map(_.toInt)
        for (uss <- start to end) {
          val optimiser = new ClusterSeedGreedyOptimisation[ClusterFromRDB](model, repetitions, clusters,
            random, seedMemberships, commMemberships, uss, hasOption(COMM_SPREAD_OPT))

          log.info("Finding the optimal set for uss: " + uss)
          val out = new PrintWriter(new File(outDir, "seed_slice" + sliceId + "_k" + k + "_uss" + uss + ".csv"))
          val solution = optimiser.maximise(k)
          out.println(solution.map(_.index).mkString(","))
          log.info("Found solution:\n" + solution.mkString("\n"))
          out.close()
          log.info("Done. The found utility was: " + optimiser.util)
        }
      }
    }
  }

  override protected def commandLineOptions = DB_OPT :: DIR_OPT :: MODEL_OPT :: REPETITIONS_OPT ::
    TARGET_SIZE_OPT :: SEED_OPT :: CLUSTER_FORMAT_OPT :: START_USER_SAMPLE_RANGE_OPT ::
    CLUSTER_FLAG_OPT :: PARALLELISM_DEGREE_OPT :: WEIGHT_VAL_OPT :: SLICE_BEGIN_OPT :: SLICE_END_OPT ::
    SEED_SAMPLE_MEMBERSHIP_DIR_OPT :: COMMUNITY_MEMBERSHIP_DIR_OPT :: COMM_SPREAD_OPT :: UNDIRECTED_OPT :: Nil
}
