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

import ie.deri.uimr.crosscomanalysis.util.GreedyMaximiser
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/10/2012
 * Time: 19:50
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
@RunWith(classOf[JUnitRunner])
class GreedyMaximiserTest extends FunSuite {

  val data = Seq(1,2,3,4,5,6,7,8,9,10)

  val maximiser = new GreedyMaximiser[Int](data) {
    protected def utility(candidates: Seq[Int]) = candidates.sum.toDouble
  }

  test("Test of simple number ordering") {
    for (k <- 1 to data.length) {
      assert(maximiser.maximise(k) === data.reverse.slice(0, k))
    }
  }

  test("Test submodularity") {
    val maximiser = new GreedyMaximiser[Int](Seq(1,2,3,4,5)) {
      /**
       * This function evaluates candidates' subsets.
       * @param candidates The ordered seq ((proposed solution),...,2nd best,the best)
       * @return Utility - the higher the better.
       */
      protected def utility(candidates: Seq[Int]) = candidates match {
        case Seq(1) => 5
        case Seq(2) => 5
        case Seq(3) => 4
        case Seq(4) => 3
        case Seq(5) => 2
        case Seq(4,1) => 8
        case Seq(5,4,1) => 10
        case _ => 5 // only the two special cases have some gain, all others do not
      }
    }
    assert(maximiser.maximise(3) === Seq(1,4,5))
  }
}
