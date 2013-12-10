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

package optimisation

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import ie.deri.uimr.crosscomanalysis.cluster.optimisation.VertexSeedDegreeDiscount
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import scala.Predef._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 04/12/2012
 * Time: 17:44
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
@RunWith(classOf[JUnitRunner])
class VertexSeedDegreeDiscountTest extends FunSuite {

  val graph = Data.generateTwoComponentJGTForDDiscount

  test("Test degree discount seed selection") {
    val dd = new VertexSeedDegreeDiscount(graph, 0.01, discount = true)
    val seeds = dd.findSeeds(2)
    assert(seeds.contains(6l) && (seeds.contains(2l) || seeds.contains(4l)),
      "The seeds (2 || 4) & 6 have to be selected instead of " + seeds.mkString(","))
  }

  test("Test degree without discount") {
    val dd = new VertexSeedDegreeDiscount(graph, 0.01, discount = false)
    val seeds = dd.findSeeds(2)
    assert(seeds == Set(2l, 4l), "The seeds 2 and 4 have to be selected instead of " + seeds.mkString(","))
  }

  test("Test of sensitivity to hidden node") {
    val dd = new VertexSeedDegreeDiscount(graph, 0.01, discount = true, hiddenVertices = Set(4l))
    val seeds = dd.findSeeds(2)
    assert(seeds == Set(2l, 6l), "The seeds 2 and 6 have to be selected instead of " + seeds.mkString(","))
  }
}
