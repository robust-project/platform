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

import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.{XComInfDiffusion, AbstractInformationDiffusion}
import ie.deri.uimr.crosscomanalysis.util.{Logging, GreedyMaximiser}
import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster
import org.apache.commons.math.random.MersenneTwister
import collection._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/10/2012
 * Time: 11:39
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 * @param seedSamplingMemberships Mapping (userid, comm.id) -> membership
 */
class ClusterSeedGreedyOptimisation[C <: Cluster[Long]](val diffusionModel: AbstractInformationDiffusion,
                                                  val repetitions: Int,
                                                  val clusters: Seq[C],
                                                  val random: MersenneTwister,
                                                  val seedSamplingMemberships: ((Long, Int)) => Double,
                                                  val communityMembership: ((Long, Int)) => Double,
                                                  val sampledActorsPerComm: Int,
                                                  val commSpread: Boolean)
  extends GreedyMaximiser[C](clusters) with XComInfDiffusion with Logging {

  private val randomGens = for (i <- 1 to repetitions) yield new MersenneTwister(random.nextInt())

  /**
   * Computes the average cascade size for the given seed
   * @param candidates Seed of clusters (targeted clusters)
   * @return The mean cascade size (averaged first over R repetitions of diffusion process and then over R samples from the clusters
   */
  protected def utility(candidates: Seq[C]) =
    (for (sampleRep <- 1 to repetitions;
          seed = activateCommunityNodes(candidates, sampledActorsPerComm, random, seedSamplingMemberships))
      yield (for (diffRep <- (0 until repetitions).par)
        yield spread(diffusionModel.simulate(seed, randomGens(diffRep)))).sum / repetitions).sum / repetitions

  private def spread(cascade: Seq[Set[Long]]): Double = {
    val activated = cascade.flatten.toSet
    if (commSpread) {
      countActiveCommunities(activated, clusters, communityMembership)
    } else {
      activated.size
    }
  }
}