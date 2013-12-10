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

import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster
import collection.{immutable, Seq}
import org.apache.commons.math.random.MersenneTwister
import ie.deri.uimr.crosscomanalysis.cluster.simulation.r.R

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 02/10/2012
 * Time: 17:28
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * This trait contains methods for sampling set of targeted actors from a set of targeted communities and for evaluating
 * community activation fractions.
 *
 * Requires R with rJava and sampling packages.
 */
trait XComInfDiffusion {

  /**
   * @param clusters Clusters to sample from
   * @param uss # users sampled per cluster
   * @param random Random number generator
   * @param membership Membership mapping: (userid, comm.id) -> membership
   * @tparam C Type of cluster (clustered items)
   * @return Set of sampled items
   */
  protected def activateCommunityNodes[C <: Cluster[Long]](clusters: Seq[C], uss: Int, random: MersenneTwister,
                                                           membership: ((Long, Int)) => Double): immutable.Set[Long] = {
    assert(uss > 0)
    clusters.map(c => {
      val vertices = c.vertices.filter(v => membership(v -> c.index) > 0)
      if (vertices.size <= uss) {
        // return all nodes with non-zero probability
        vertices
      } else {
        // sample uss nodes
        sequentialSampling(vertices, c.index, uss, random, membership)
      }
    }).flatten.toSet
  }

  /**
   * Uses simple roulette-wheel algorithm (see http://stackoverflow.com/a/2151885)
   */
  private def sequentialSampling(vertexSet: collection.Set[Long], index: Int, uss: Int, random: MersenneTwister,
                                                     membership: ((Long, Int)) => Double): immutable.Set[Long] = {
    // vertices have to be sorted s.t. we can do the sampling and also cluster members' could be determined differently
    // then the supplied membership function, e.g. by posts vs. reply counts
    var vertices = vertexSet.toList.sortBy(v => membership(v -> index)).reverse
    var cumWeights = vertices.map(v => membership(v, index)).scanLeft(0d)(_ + _).drop(1) // compute cumulative sums
    assert(cumWeights.last > 0) // sanity check
    var sample = Set.empty[Long]
    while (sample.size < uss) {
      val s = random.nextDouble() * cumWeights.last
      val i = cumWeights.indexWhere(_ >= s)
      sample = sample + vertices(i)
      vertices = remove(i, vertices)
      cumWeights = vertices.map(v => membership(v, index)).scanLeft(0d)(_ + _).drop(1) // recompute cumulative sums
      assert(cumWeights.last > 0) // sanity check
    }
    sample
  }

  /**
   * Removes an i-th element from the list.
   * @param i
   * @param list
   * @tparam T
   * @return
   */
  private def remove[T](i: Int, list: List[T]) = {
    if (i < 0 || i >= list.size) throw new IllegalArgumentException("Index out of bounds: " + i)
    list.take(i) ::: list.drop(i+1)
  }

  /**
   * Uses PPS sampling from R sampling package. May be more accurate and slower than the sequential method.
   */
  private def poissonSampling[C <: Cluster[Long]](c: C, uss: Int, random: MersenneTwister,
                                                  membership: ((Long, Int)) => Double): collection.immutable.Set[Long] = {
    import R.{rEngine, eval}
    // get memberships, R sampling methods don't work with zero probabilities, thus cannot use directly the column
    val vertices = c.vertices.toArray.sorted
    val memberships = vertices.map(v => membership(v -> c.index))
    assert(memberships.forall(_ > 0))
    eval("set.seed(" + random.nextInt() + ")")
    eval("library(sampling)")
    // compute the inclusion probabilities
    if (eval("exists(\"" + id("pik", c.index, uss) + "\")").asInteger() == 0) {
      rEngine.assign(id("m", c.index, uss), memberships)
      eval(id("pik", c.index, uss) +
        "<- inclusionprobabilities(" + id("m", c.index, uss) + "," + uss + ")")
    }
    val sample =
      if (uss > 1) {
        // sample using the conditional poisson sampling (maximum entropy design)
        if (eval("exists(\"" + id("q", c.index, uss) + "\")").asInteger() == 0) {
          /*
           compute the q matrix for conditional poisson sampling (with fixed size)
           this is expensive operation so should be cached - see impl. of UPmaxentropy for details
           */
          // first compute an adjusted vector of in. probabilities without p = 1 (those have to be included for sure)
          eval(id("pik2", c.index, uss) + " <- " + id("pik", c.index, uss) +
            "[" + id("pik", c.index, uss) + " != 1]")
          eval(id("pikt", c.index, uss) +
            " <- UPMEpiktildefrompik(" + id("pik2", c.index, uss) + ")")
          eval(id("w", c.index, uss) + " <- " +
            id("pikt", c.index, uss) + " / (1 - " + id("pikt", c.index, uss) + ")")
          eval(id("q", c.index, uss) + " <- UPMEqfromw(" + id("w", c.index, uss) +
            ", .as_int(sum(" + id("pik2", c.index, uss) + ")))")
        }
        // take a sample of items with p < 1 and combine it with the items with p = 1
        eval(id("s2", c.index, uss) + " <- UPMEsfromq(" + id("q", c.index, uss) + ")")
        eval(id("s", c.index, uss) + " <- rep(0, times = " + memberships.length + ")")
        eval(id("s", c.index, uss) + "[" + id("pik", c.index, uss) + " == 1] <- 1")
        eval(id("s", c.index, uss) + "[" + id("pik", c.index, uss) + " != 1]" +
          "[" + id("s2", c.index, uss) + " == 1] <- 1")
        eval(id("s", c.index, uss))
      } else {
        // draw from multinomial because uss=1 and piktilde=pik for this case
        eval("as.vector(rmultinom(1, 1," + id("pik", c.index, uss) + "))")
      }

    if (sample.isNull)
      throw new SamplingException("The obtained sample is null.", vertices, memberships)
    else
      sample.asIntegers().map(p => vertices(p)).toSet
  }

  /**
   * Computes the weighted count of activated communities.
   * @param activeNodes Set of activated users.
   * @param membership Mapping (userid, comm.id) -> membership
   * @return Sum of fractions of activated users per community (weighted by their memberships).
   */
  protected def countActiveCommunities[C <: Cluster[Long]](activeNodes: collection.immutable.Set[Long],
                                                           clusters: Seq[C],
                                                           membership: ((Long, Int)) => Double): Double = {
    var res = List.empty[Double]
    for (c <- clusters) {
      val activatedCardinality = c.vertices.intersect(activeNodes).map(a => membership(a -> c.index)).sum
      val totalCardinality = c.vertices.map(v => membership(v -> c.index)).sum
      res = (activatedCardinality / totalCardinality) :: res
    }

    res.sum
  }

  private def id(variable: String, cid: Int, usersPerSample: Int) = variable + "_" + cid + "_" + usersPerSample
}

class SamplingException(m: String, vertices: Array[Long], memberships: Array[Double]) extends Exception(m) {
  override def getMessage = m + "\nvertices: " + vertices.mkString(",") + "\nmemberships: " + memberships.mkString(",")
}