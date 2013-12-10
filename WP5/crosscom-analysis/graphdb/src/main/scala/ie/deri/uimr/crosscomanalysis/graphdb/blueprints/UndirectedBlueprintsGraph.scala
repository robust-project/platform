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

//package ie.deri.uimr.crosscomanalysis.graphdb.blueprints
//
//import java.lang.String
//import com.tinkerpop.blueprints.pgm.{Edge, Vertex, Graph}
//import collection.JavaConversions.{asScalaIterable, asJavaIterable}
//
///**
// * Created by IntelliJ IDEA.
// * Author: vaclav.belak@deri.org
// * Date: 25/02/2011
// * Time: 17:19
// * ©2011 Digital Enterprise Research Institute, NUI Galway.
// */
//
//// todo finish implementation - have to instantiate special vertices instead of standard ones
//class UndirectedBlueprintsGraph(protected val g: Graph) extends Graph {
//  def shutdown = g.shutdown
//
//  def clear = g.clear
//
//  def getEdges = g.getEdges
//
//  def removeEdge(e: Edge) = g.removeEdge(e)
//
//  def getEdge(e: AnyRef): Edge = g.getEdge(e)
//
//  def addEdge(id: AnyRef, source: Vertex, sink: Vertex, label: String): Edge = g.addEdge(id, source, sink, label)
//
//  def getVertices = g.getVertices
//
//  def removeVertex(v: Vertex) = g.removeVertex(v)
//
//  def getVertex(v: AnyRef): Vertex = g.getVertex(v)
//
//  def addVertex(v: AnyRef): Vertex = g.getVertex(v)
//
//
//}
//
//protected class VertexInUndirectedGraph(protected val v: Vertex) extends Vertex {
//  def getInEdges = (v.getInEdges ++ v.getOutEdges).toIterable
//
//  def getOutEdges = getInEdges
//
//  def getId = v.getId
//
//  def removeProperty(p: String) = v.removeProperty(p)
//
//  def setProperty(p: String, value: AnyRef) = v.setProperty(p, value)
//
//  def getPropertyKeys = v.getPropertyKeys
//
//  def getProperty(p: String) = v.getProperty(p)
//}