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

package ie.deri.uimr.crosscomanalysis.graphdb.jung

import edu.uci.ics.jung.graph.Graph
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import collection.mutable.HashMap

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 11/03/2011
 * Time: 08:59
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

class JungDBGraphLoader[G <: Graph[Long, Long]](sliceId:Long, val graph: G, val normalize: Boolean) {
  val weights = new HashMap[Long,Double]

  init(sliceId)

  def init(sliceId: Long) = {
    inTransaction {
      var edgeId = 0l
      for ((source, sink, weight) <-
           from(networkSliceStructure)(nss => where(nss.sliceId === sliceId) select (nss.source, nss.sink, nss.weight))) {
        if (!graph.containsVertex(source)) graph.addVertex(source)
        if (!graph.containsVertex(sink)) graph.addVertex(sink)
        graph.addEdge(edgeId, source, sink)
        weights(edgeId) = weight
        edgeId += 1
      }
    }
    if (normalize) {
      val maxW = weights.values.max
      if (maxW > 1) weights.keys.foreach(edge => weights(edge) /= maxW)
    }
  }
}

