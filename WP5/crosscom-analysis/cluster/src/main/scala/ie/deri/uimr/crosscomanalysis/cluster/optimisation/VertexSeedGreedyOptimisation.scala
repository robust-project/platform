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

import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.AbstractInformationDiffusion
import ie.deri.uimr.crosscomanalysis.util.GreedyMaximiser
import scala.collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties.ORIGID
import org.apache.commons.math.random.MersenneTwister


/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/10/2012
 * Time: 11:20
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
class VertexSeedGreedyOptimisation(val diffusionModel: AbstractInformationDiffusion, val repetitions: Int,
                                   val random: MersenneTwister, vertices: collection.Seq[Long])
  extends GreedyMaximiser[Long](vertices)  {

  /**
   * Computes the average cascade size for the given seed
   * @param seed Seed of vertex ids
   * @return The mean cascade size
   */
  protected def utility(seed: Seq[Long]) =
    (for (r <- 1 to repetitions) yield diffusionModel.simulate(seed.toSet, random).flatten.length).sum.toDouble / repetitions
}
