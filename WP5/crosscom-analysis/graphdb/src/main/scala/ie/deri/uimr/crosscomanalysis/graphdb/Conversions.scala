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

package ie.deri.uimr.crosscomanalysis.graphdb

import com.tinkerpop.blueprints.pgm.{Graph, Edge, Vertex}
import com.tinkerpop.blueprints.pgm.oupls.jung.GraphJung
import collection.Map
import collection.JavaConversions.collectionAsScalaIterable
import org.apache.commons.collections15.Transformer
import edu.uci.ics.jung.graph.{UndirectedSparseGraph, UndirectedGraph, Graph => JUNGGraph}
import edu.uci.ics.jung.graph.util.EdgeType
import org.neo4j.graphdb.RelationshipType

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/02/2011
 * Time: 17:12
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object Conversions {
  implicit def blueprintsGraphToJUNG(g: Graph): JUNGGraph[Vertex, Edge] = new GraphJung(g)

  implicit def mapToTransformer[I: ClassManifest, O: ClassManifest](m: Map[I, O]) = new Transformer[I, O] {
    def transform(in: I) = m(in)
  }

  def asUndirectedGraphJung(g: Graph): UndirectedGraph[Vertex, Edge] = {
    val outG = new UndirectedSparseGraph[Vertex, Edge]
    val gj = new GraphJung(g)
    gj.getVertices.foreach(outG.addVertex(_))
    gj.getEdges.foreach(e => outG.addEdge(e, e.getOutVertex, e.getInVertex, EdgeType.UNDIRECTED))
    outG
  }

  implicit def asRelationshipType(rel: String): RelationshipType = new RelationshipType {
    def name = rel
  }

  implicit def partialFunctionAsTransformer[D,R](f: PartialFunction[D,R]) = new Transformer[D,R] {
    def transform(in: D) = f(in)
  }

  implicit def closureAsTransformer[I,O](c: I => O) = new Transformer[I,O] {
    def transform(in: I) = c(in)
  }
}