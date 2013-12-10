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

package ie.deri.uimr.crosscomanalysis.cluster.feature.comp

import ie.deri.uimr.crosscomanalysis.db.tables.graph.Cluster
import ie.deri.uimr.crosscomanalysis.cluster.centrality.InDegreeGroupCentrality
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType
import com.tinkerpop.blueprints.pgm.Graph
import scala.collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.{GraphProperty, DBGraphLoader}
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07/12/2011
 * Time: 12:48
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object InDegreeGroupCentralityComp extends FeatureExtractor {

  override val COMMAND_NAME = "compute-group-indegree"

  private var centrality: InDegreeGroupCentrality = _
  private var graph: Graph = _

  protected def processCluster(clusterDB: Cluster) {
    val cluster = new ClusterFromRDB(clusterDB)
    storeFeatureValue(clusterDB.id, FeatureType.GROUP_INDEGREE.id, centrality.getVertexGroupScore(graph.getVertices.filter(v => cluster.vertices.contains(v(ORIGID))).toSet))
  }

  override protected def computeSliceStats(sliceId: Long) {
    val edgeType = GraphProperty(-1, "non_typed_graph")
    graph = new DBGraphLoader(sliceId, edgeType).graph
    centrality = new InDegreeGroupCentrality(graph, edgeType.name)
  }
}