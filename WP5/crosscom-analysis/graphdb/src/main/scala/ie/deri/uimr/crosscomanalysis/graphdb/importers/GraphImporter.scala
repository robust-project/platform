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

package ie.deri.uimr.crosscomanalysis.graphdb.importers

import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import collection.mutable.HashMap
import com.tinkerpop.blueprints.pgm.{Vertex, Graph}
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperty

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/02/2011
 * Time: 17:24
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait GraphImporter {
  protected var edgeIdLast = -1l

  protected def newEdgeId = {
    edgeIdLast += 1
    edgeIdLast
  }

  protected val origIdsToVertices = new HashMap[Long, Vertex]

  protected def retrieveOrCreateVertex(id: Long)(implicit g: Graph) = {
    val ret = origIdsToVertices.get(id)
    if (ret.isEmpty) {
      val v = g.addVertex(id)
      v.setProperty(ORIGID, id.asInstanceOf[java.lang.Long])
      origIdsToVertices(id) = v
      v
    }
    else ret.get
  }

  /**
   * Adds an edge with a specified ID (or generates a new one if it's not specified. It generates the vertices with the
   * given IDs if they don't exist. This method does NOT check whether the edge already exists!
   * @param source ID of source vertex
   * @param sink ID of sink vertex
   * @param edgeType Type of edge
   * @param weight Weight of edge
   * @param id Edge id
   * @param g Graph to modify
   * @return The newly created edge
   */
  protected def createEdge(source: Long, sink: Long, edgeType: GraphProperty[_],
                           weight: Option[Double] = None, id: Option[Long] = None)(implicit g: Graph) = {
    val e = g.addEdge(id.getOrElse(newEdgeId).asInstanceOf[AnyRef], retrieveOrCreateVertex(source),
    retrieveOrCreateVertex(sink), edgeType.name)
    if (weight.isDefined)
      e.setProperty(WEIGHT, weight.get.asInstanceOf[AnyRef])

    e
  }

  protected def reInitializeGraphImporter() {
    this.edgeIdLast = -1
    this.origIdsToVertices.clear()
  }
}