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

package comparator

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import ie.deri.uimr.crosscomanalysis.cluster.comparator.ClusterMatcher
import ie.deri.uimr.crosscomanalysis.util.Functions
import crosscom.OverlapTestData._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 09/03/2011
 * Time: 14:35
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */
@RunWith(classOf[JUnitRunner])
class ClusterMatcherTest extends FunSuite {

  test("Mapping has to be a function from later to former clusters maximizing the similarity") {
    val matcher = new ClusterMatcher[Set[Int]] {
      protected def clusterSim(c1: Set[Int], c2: Set[Int]) = Functions.jaccardSim(c1, c2)
    }
    val mapping = matcher.matchClusters(FORMER.toSet, LATER.toSet)
    assert(mapping(LATER.head) === FORMER.head)
    assert(mapping(LATER(1)) === FORMER(1))
    assert(!mapping.contains(LATER(2)))
    assert(!mapping.contains(LATER(3)))
    assert(mapping(LATER.last) === FORMER.last)
  }

  test("Mapping has to discard similarity below threshold") {
    val matcher = new ClusterMatcher[Set[Int]] {
      this.matchingThreshold = 1
      protected def clusterSim(c1: Set[Int], c2: Set[Int]) = Functions.jaccardSim(c1, c2)
    }

    val mapping = matcher.matchClusters(FORMER.toSet, LATER.toSet) // prune all different clusters
    assert(!mapping.contains(LATER.head))
    assert(mapping(LATER(1)) === FORMER(1))
    assert(!mapping.contains(LATER(2)))
    assert(!mapping.contains(LATER(3)))
    assert(mapping(LATER.last) === FORMER.last)
  }

}