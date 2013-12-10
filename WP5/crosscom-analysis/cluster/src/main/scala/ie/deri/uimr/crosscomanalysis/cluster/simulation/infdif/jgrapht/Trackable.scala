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

package ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.jgrapht

import org.apache.commons.math.random.MersenneTwister
import collection._
import org.jgrapht.graph.{DefaultWeightedEdge, SimpleDirectedWeightedGraph}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/06/2013
 * Time: 12:27
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
trait Trackable {
  def simulate(seed: collection.Set[Long], random: MersenneTwister, track: Boolean): (Seq[Set[Long]], Option[SimpleDirectedWeightedGraph[Long, DefaultWeightedEdge]])
}
