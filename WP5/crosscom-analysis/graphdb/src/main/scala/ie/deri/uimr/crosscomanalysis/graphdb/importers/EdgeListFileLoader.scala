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

import java.io.File
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import io.Source
import ie.deri.uimr.crosscomanalysis.util.Logging
import com.tinkerpop.blueprints.pgm.{Graph, Edge}
import collection.mutable
import ie.deri.uimr.crosscomanalysis.graphdb._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import GraphProperties.WEIGHT

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 03/10/2012
 * Time: 16:19
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * Loads a graph file from an edge list.
 *
 * source[SEP]sink[SEP]weight
 *
 * Whatever is after weight is ignored.
 *
 * @param directed If false, then edges also in the opposite directions are added.
 * @param normalizeByOutStrength Should the weight w_ij be normalized by out or in-strength of the node i? None if not to normalize
 */
class EdgeListFileLoader(val edgeList: File, val edgeType: GraphProperty[Nothing],
                         val normalizeByOutStrength: Option[Boolean] = None, val directed: Boolean = true,
                         val delimiter: String = "\\s+")
  extends GraphImporter with Logging {

  private var _graph: Option[Graph] = None
  val edgePairs = new mutable.HashMap[Edge, Edge]

  def load() {
    implicit val g = new TinkerGraph

    log.debug("Starting to parsing edgelist " + edgeList.getAbsolutePath)
    var l = 0
    for (line <- Source.fromFile(edgeList).getLines()) {
      l += 1
      val tokens = line.split(delimiter)
      try {
        val Array(source, sink, _*) = tokens
        val w = if (normalizeByOutStrength.isDefined) Some(tokens(2).toDouble) else None
        val e = createEdge(source.toLong, sink.toLong, edgeType, w)
        if (!directed) {
          val oppositeE = createEdge(sink.toLong, source.toLong, edgeType, w)
          edgePairs(e) = oppositeE
          edgePairs(oppositeE) = e
        }
      } catch {
        case e: MatchError => {
          log.error("Matching error on line " + l + ": " + line + "\nDetails: " + e.getMessage())
          sys.exit(-1)
        }
      }
    }
    log.debug("Parsing done")

    _graph = Some(if (normalizeByOutStrength.isDefined) normalizeWeights(g, normalizeByOutStrength.get, edgeType) else g)
  }

  def graph: Graph = {
    if (_graph.isEmpty) load()
    _graph.get
  }
}