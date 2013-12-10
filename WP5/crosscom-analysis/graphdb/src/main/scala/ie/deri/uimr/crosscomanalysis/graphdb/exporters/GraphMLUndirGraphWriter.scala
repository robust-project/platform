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

package ie.deri.uimr.crosscomanalysis.graphdb.exporters

import ie.deri.uimr.crosscomanalysis.util.XMLStreamUtil
import com.tinkerpop.blueprints.pgm.Graph
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLWriter
import java.io.{PrintStream, FileOutputStream, File, OutputStream}
import io.Source
import xml.pull.{EvElemStart, XMLEventReader}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/11/2011
 * Time: 11:19
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Outputs a graph in GraphML format using the TinkerPop implementation, but modify the result s.t. the graph is undirected.
 */

object GraphMLUndirGraphWriter extends XMLStreamUtil {
  def outputGraph(graph: Graph, os: OutputStream) {
    val graphmlFile = File.createTempFile("graphml", "orig")
    val tempOS = new FileOutputStream(graphmlFile)
    GraphMLWriter.outputGraph(graph, tempOS)
    tempOS.close()
    val ps = new PrintStream(os)
    def out(s: String) {
      ps.print(s)
    }

    for (event <- new XMLEventReader(Source.fromFile(graphmlFile))) {
      event match {
        case EvElemStart(_, "graphml", _, _) => {
          out("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:y=\"http://www.yworks.com/xml/graphml\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\">\n")
        }
        case EvElemStart(_, "graph", _, _) => {
          out("<graph id=\"G\" edgedefault=\"undirected\">")
        }
        case _ => out(toXML(event))
      }
    }
    ps.close()
    graphmlFile.delete()
  }
}