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

import com.tinkerpop.blueprints.pgm.Graph
import collection.JavaConversions.iterableAsScalaIterable
import java.io.{PrintWriter, OutputStream}
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/11/2011
 * Time: 14:40
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object EdgeListWriter {

  def outputGraph(graph: Graph, os: OutputStream) {
    val out = new PrintWriter(os)
    for (e <- graph.getEdges) {
      if (e.contains(WEIGHT)) {
        out.printf("%d %d %f\n", e.getOutVertex()(ORIGID).asInstanceOf[AnyRef], e.getInVertex()(ORIGID).asInstanceOf[AnyRef],
          e(WEIGHT).asInstanceOf[AnyRef])
      } else {
        out.printf("%d %d\n", e.getOutVertex()(ORIGID).asInstanceOf[AnyRef], e.getInVertex()(ORIGID).asInstanceOf[AnyRef])
      }
    }
    out.close()
  }

  def outputGraph[V,E](graph: org.jgrapht.Graph[V,E], os: OutputStream) {
    val out = new PrintWriter(os)
    for (e <- graph.edgeSet()) {
      out.print("%d %d %f\n".format(graph.getEdgeSource(e), graph.getEdgeTarget(e), graph.getEdgeWeight(e)))
    }
    out.close()
  }
}