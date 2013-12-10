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

package r

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import ie.deri.uimr.crosscomanalysis.cluster.simulation.r.R

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 13/03/2013
 * Time: 16:29
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
@RunWith(classOf[JUnitRunner])
class REngineTest extends FunSuite {

  test("Creation of JRIEngine") {
    import R.rEngine
    assert(rEngine != null)
    rEngine.parseAndEval("library(sampling)")
    val pik = Array(0.07, 0.17, 0.41, 0.61, 0.83, 0.91)
    rEngine.assign("pik", pik)
    rEngine.parseAndEval("n <- sum(pik)")
    rEngine.parseAndEval("pikt <- UPMEpiktildefrompik(pik)")
    rEngine.parseAndEval("w <- pikt / (1 - pikt)")
    rEngine.parseAndEval("q <- UPMEqfromw(w,n)")
    val s = rEngine.parseAndEval("UPMEsfromq(q)")
    assert(rEngine.parseAndEval("exists(\"q\")").asInteger() === 1)
    assert(rEngine.parseAndEval("exists(\"q\")").asInteger() != 0)
    println(s.asIntegers().mkString(","))
  }
}
