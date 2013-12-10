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

import collection.JavaConversions.iterableAsScalaIterable
import collection.{mutable, Seq, Set}
import ie.deri.uimr.crosscomanalysis.util.{GreedyMaximiser, Logging}
import org.jgrapht.graph.AbstractBaseGraph

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 03/12/2012
 * Time: 11:45
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 *
 * Finds the best seeds using the degree discount heuristic from Chen, W., Wang, Y., & Yang, S. (2009). Efficient
 * influence maximization in social networks. Proceedings of the 15th ACM SIGKDD.
 * Retrieved from http://dl.acm.org/citation.cfm?id=1557047
 *
 * Works only for an undirected graph (i.e. each edge is replicated in both directions)
 *
 * @param g Undirected graph
 * @param prob Global infection/transmission probability
 * @param hiddenVertices Set of vertices not to be considered for the degree calculation (i.e. in the PartNet experiment)
 */
class VertexSeedDegreeDiscount[V, E](val g: AbstractBaseGraph[V, E], prob: Double, val discount: Boolean = true,
                                     val hiddenVertices: Set[V] = Set.empty[V]) extends Logging {

  private val nonHiddenVertices = g.vertexSet().toSet -- hiddenVertices
  private val totalDegrees = mutable.Map.empty[Seq[V], Double]

  def findSeeds(k: Int): Set[V] = {
    val maximiser = new GreedyMaximiser[V](nonHiddenVertices.toSeq) {
      /**
       * This function evaluates candidates' subsets.
       * @param candidates The ordered seq ((proposed solution),...,2nd best,the best)
       * @return Utility - the higher the better.
       */
      protected def utility(candidates: Seq[V]) = totalDegree(candidates) + util
    }
    maximiser.maximise(k).toSet
  }

  private def totalDegree(candidates: Seq[V]): Double = {
    val v = candidates.head
    val seeds = candidates.drop(1)
    val degree = degreeOf(v)

    if (seeds.isEmpty) {
      degree
    } else {
      val dis: Double =
        if (discount)
          g.edgesOf(v).filter(e => seeds.contains(g.getEdgeSource(e)) || seeds.contains(g.getEdgeTarget(e))).size.toDouble
        else 0
      degree - 2 * dis - (degree - dis) * dis * prob
    }
  }

  private def degreeOf(v: V): Int = {
    val d = g.degreeOf(v)
    if (hiddenVertices.isEmpty) {
      d // no need to subtract the edges from the hidden nodes
    } else {
      d - g.edgesOf(v).filter(e => hiddenVertices.contains(g.getEdgeSource(e)) ||
        hiddenVertices.contains(g.getEdgeTarget(e))).size
    }
  }
}
