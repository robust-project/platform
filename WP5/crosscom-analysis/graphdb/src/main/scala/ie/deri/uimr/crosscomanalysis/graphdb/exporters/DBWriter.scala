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

import java.util.Date
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.util.Logging
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import collection.mutable.MutableList
import ie.deri.uimr.crosscomanalysis.db.tables.graph.{NetworkSliceVertex, NetworkSliceStructure, NetworkSlice}
import com.tinkerpop.blueprints.pgm.{Vertex, Graph}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 21/11/2011
 * Time: 16:59
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object DBWriter extends Logging {
  def outputGraph(graph: Graph, sliceType: Int, begin: Date, end: Date) {
    inTransaction {
      val ns = new NetworkSlice(begin, end, sliceType)
      networkSlice insert ns
      log.info("Storing " + ns)
      val buffer = new MutableList[NetworkSliceStructure]
      for (edge <- graph.getEdges) {
        buffer += new NetworkSliceStructure(ns.id, edge.getOutVertex()(ORIGID), edge.getInVertex()(ORIGID), edge(WEIGHT), None)
        if (buffer.size >= 1000) {
          networkSliceStructure insert buffer
          log.debug("Storing buffer of size " + buffer.size)
          buffer.clear()
        }
      }
      if (!buffer.isEmpty) {
        networkSliceStructure insert buffer
        log.debug("Storing buffer of size " + buffer.size)
        buffer.clear()
      }
      log.debug("Storing vertices")
      networkSliceVertex insert graph.getVertices.map((v: Vertex) => new NetworkSliceVertex(ns.id, v(ORIGID)))
    }
  }

}