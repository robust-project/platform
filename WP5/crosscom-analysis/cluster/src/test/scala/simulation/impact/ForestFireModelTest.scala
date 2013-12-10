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

package simulation.impact

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import ie.deri.uimr.crosscomanalysis.cluster.simulation.impact.ForestFireModel
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLWriter
import java.io.{FileOutputStream, File}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 23/02/2012
 * Time: 15:30
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

@RunWith(classOf[JUnitRunner])
class ForestFireModelTest extends FunSuite {
  test("Generation of a FF graph") {
    val ffm = new ForestFireModel(1000, 0.37, 0.32, "cites")
    val g = ffm.generateGraph
    val f = new File("/Users/vacbel/Temp/ffgraph.graphml")
    val fos = new FileOutputStream(f)
    GraphMLWriter.outputGraph(g, fos)
    fos.close()
  }
}
