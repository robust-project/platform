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

import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLReader
import com.tinkerpop.blueprints.pgm.{Edge, Element, Vertex}
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.Pipeline
import ie.deri.uimr.crosscomanalysis.graphdb.traversals.Cocitation
import java.io.FileInputStream
import java.util.HashSet
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.CSXGraphProperties._

@RunWith(classOf[JUnitRunner])
class CocitationTraversalTest extends FunSuite {
  test("Cocitation relationship inferrence") {
    val graph = new TinkerGraph
    //    val graph = new Neo4jGraph("/Users/vacbel/Data/csx/cocit/papers_graph")
    GraphMLReader.inputGraph(graph, new FileInputStream("/Users/vacbel/Codes/crosscom-analysis/graphdb/data/citegraph.xml"))

    var cocit = Cocitation.clusterCocitations(graph.getVertex(6l))
    assert(cocit(graph.getVertex(7l)) === 1)
    assert(cocit(graph.getVertex(8l)) === 1)
    assert(Cocitation.authorPipe(graph.getVertex(6l)).head.getProperty("name") === "Karel Tandori")

    cocit = Cocitation.clusterCocitations(graph.getVertex(7l))
    assert(cocit(graph.getVertex(6l)) === 1)
    assert(cocit(graph.getVertex(8l)) === 2)
    assert(cocit(graph.getVertex(9l)) === 1)
    assert(Cocitation.authorPipe(graph.getVertex(7l)).find(_.getProperty("name") == "Hrabe Morfius").isDefined)
    assert(Cocitation.authorPipe(graph.getVertex(7l)).find(_.getProperty("name") == "Slicna Anafeles").isDefined)
    Cocitation.authorPipe(graph.getVertex(7l)).foreach(v => println(v.getProperty("name")))
    assert(Cocitation.authorPipe(graph.getVertex(7l)).size === 2)

    cocit = Cocitation.clusterCocitations(graph.getVertex(8l))
    assert(cocit(graph.getVertex(6l)) === 1)
    assert(cocit(graph.getVertex(7l)) === 2)
    assert(cocit(graph.getVertex(9l)) === 1)
    assert(Cocitation.authorPipe(graph.getVertex(8l)).head.getProperty("name") === "Pavel Patek")

    cocit = Cocitation.clusterCocitations(graph.getVertex(9l))
    assert(cocit(graph.getVertex(7l)) === 1)
    assert(cocit(graph.getVertex(8l)) === 1)
    assert(Cocitation.authorPipe(graph.getVertex(9l)).head.getProperty("name") === "Zaphod Beebelbrox")

    assert(Cocitation.clusterCocitations(graph.getVertex(4l)).isEmpty)
    assert(Cocitation.authorPipe(graph.getVertex(4l)).isEmpty)
    assert(Cocitation.clusterCocitations(graph.getVertex(5l)).isEmpty)
    assert(Cocitation.authorPipe(graph.getVertex(5l)).isEmpty)

    graph.shutdown()
  }

  test("Author cocitation traversal") {
    val graph = new TinkerGraph
    GraphMLReader.inputGraph(graph, new FileInputStream("/Users/vacbel/Codes/crosscom-analysis/graphdb/data/citegraph.xml"))
    val authorCocit = Cocitation.authorCocitations(graph.getVertex(1l), 2000, 2002)
    println(authorCocit.rawPipeline)
    for (auth <- authorCocit) {
      println(auth(NAME))
      import collection.JavaConversions.asScalaBuffer
      println(authorCocit.pipe(4).getPath.last.asInstanceOf[Edge](COCIT_COUNT))
    }
    def citationCount(authorId: Long) = {
      val authorV = graph.getVertex(authorId.asInstanceOf[AnyRef])
      authorV.trav[Long].outE(AUTHOR_OF).inV.strain {v =>
      v(YEAR) >= 2000 && v(YEAR) <= 2003}.inE(CITES).count.cap.head
    }
    println("Counts")
    for (i <- 1l :: 2l :: 3l :: 10l :: 11l :: Nil) {
      println(citationCount(i))
    }
    graph.shutdown()
  }
}