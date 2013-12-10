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

package exporters

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import java.util.Date
import ie.deri.uimr.crosscomanalysis.graphdb.exporters.GEXFWriter
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 03/11/2011
 * Time: 11:04
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

@RunWith(classOf[JUnitRunner])
class GEXFExporterTest extends FunSuite {
  test("Dynamic Graph Exporter") {
    val g1 = new TinkerGraph
    val begin1 = new Date(100, 0, 1)
    val end1 = new Date(100, 0, 31)
    val g2 = new TinkerGraph
    val begin2 = new Date(100, 1, 1)
    val end2 = new Date(100, 1, 28)

    val v11 = g1.addVertex(1)
    val v12 = g1.addVertex(2)
    v11(ORIGID) = 1l.asInstanceOf[AnyRef]
    v11(SIZE) = 10.asInstanceOf[AnyRef]
    v12(ORIGID) = 2l.asInstanceOf[AnyRef]
    v12(SIZE) = 15.asInstanceOf[AnyRef]
    val e11 = g1.addEdge(1, v11, v12, "test")
    e11(WEIGHT) = 0.5.asInstanceOf[AnyRef]

    val v21 = g2.addVertex(1)
    val v22 = g2.addVertex(2)
    val v23 = g2.addVertex(3)
    v21(ORIGID) = 1l.asInstanceOf[AnyRef]
    v21(SIZE) = 15.asInstanceOf[AnyRef]
    v22(ORIGID) = 2l.asInstanceOf[AnyRef]
    v22(SIZE) = 10.asInstanceOf[AnyRef]
    v23(ORIGID) = 3l.asInstanceOf[AnyRef]
    v23(SIZE) = 5.asInstanceOf[AnyRef]
    val e21 = g2.addEdge(1, v21, v22, "test")
    e21(WEIGHT) = 0.7.asInstanceOf[AnyRef]
    val e22 = g2.addEdge(2, v21, v23, "test")
    e22(WEIGHT) = 0.3.asInstanceOf[AnyRef]
    val e23 = g2.addEdge(3, v22, v23, "test")
    e23(WEIGHT) = 0.5.asInstanceOf[AnyRef]

    GEXFWriter.outputDynamicGraph(Seq((g1, begin1, end1), (g2, begin2, end2)), Set(v11,v12,v23), Set(e11,e22,e23), new File("/Users/vacbel/Temp/dyngraph.gexf"), true)
  }
}