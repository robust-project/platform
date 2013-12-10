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

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import ie.deri.uimr.crosscomanalysis.cluster.comparator.OmegaComparator
import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster
import crosscom.OverlapTestData

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 16/06/2011
 * Time: 17:20
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

@RunWith(classOf[JUnitRunner])
class OmegaComparatorTest extends FunSuite {
  test("Omega index test from table 6 of the original paper") {
    // clustering 1
    val c1 = Cluster(1, Set(1, 2, 3, 4))
    val c2 = Cluster(2, Set(4, 5, 6, 7))
    val c3 = Cluster(3, Set(8, 9))
    val c4 = Cluster(4, Set(10))
    // clustering 2
    val c5 = Cluster(5, Set(1, 2, 3, 4))
    val c6 = Cluster(6, Set(3, 4, 5, 6, 7))
    val c7 = Cluster(7, Set(8, 9, 10))
    val omegaComparator = new OmegaComparator[Int](Set(c1, c2, c3, c4), Set(c5, c6, c7)) // 10
    assert(omegaComparator.compare === (45*39 - (27*32d + 17*13)) / (math.pow(45, 2) - (27*32d + 17*13)))
  }

  test("Omega test for OverlapTestData") {
    val omegaComparator = new OmegaComparator[Int](OverlapTestData.FORMER_CLUSTERS.toSet, OverlapTestData.LATER_CLUSTERS.toSet) // 7
    assert(omegaComparator.compare === 104/209d)
  }
}