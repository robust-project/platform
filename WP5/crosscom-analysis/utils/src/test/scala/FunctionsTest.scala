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

import ie.deri.uimr.crosscomanalysis.util.Functions
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/04/2011
 * Time: 13:53
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

@RunWith(classOf[JUnitRunner])
class FunctionsTest extends FunSuite {
  test("Denormalize Edges") {
    val edges = Map(
      (1, 2) -> 0.5,
      (1, 3) -> 2d,
      (3, 2) -> 1.5)
    val denormalizedEdges = Functions.denormalize(edges)
    assert(denormalizedEdges((1,2)) === 1)
    assert(denormalizedEdges((1,3)) === 4)
    assert(denormalizedEdges((3,2)) === 3)
  }
}