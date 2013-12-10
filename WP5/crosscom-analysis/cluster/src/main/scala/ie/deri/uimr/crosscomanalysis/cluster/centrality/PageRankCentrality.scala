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

package ie.deri.uimr.crosscomanalysis.cluster.centrality

import ie.deri.uimr.crosscomanalysis.cluster.model.{ClusterWithSubgraph, Cluster}
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import edu.uci.ics.jung.algorithms.scoring.PageRank
import scala.collection.JavaConversions.iterableAsScalaIterable
import com.tinkerpop.blueprints.pgm.{Graph, Vertex, Edge}
import collection.Map
import java.lang.Double

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/11/2011
 * Time: 17:39
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class PageRankCentrality[C <: Cluster[Long] with ClusterWithSubgraph](val weighted: Boolean, val edgeType: String)
  extends NodeWithinClusterCentrality[C] {

  def compute(cluster: C) = {
    val pr =
      if (weighted)
        new PageRank[Vertex,Edge](cluster.subgraph, mapToTransformer(normalizedWeights(cluster.subgraph)), 0.15)
      else
        new PageRank[Vertex,Edge](cluster.subgraph, 0.15)
    cluster.subgraph.getVertices.map(v => (v(ORIGID), pr.getVertexScore(v).toDouble)).toMap
  }

  private def normalizedWeights(g: Graph): Map[Edge, Double] = {
    val strengths = (for (v <- g.getVertices; strength = v.getOutEdges(edgeType).map(_(WEIGHT)).sum) yield (v, strength)).toMap
    (for (v <- g.getVertices; e <- v.getOutEdges(edgeType)) yield (e, new Double((e(WEIGHT) / strengths(v))))).toMap
  }
}