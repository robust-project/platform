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
import scala.collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/11/2011
 * Time: 18:01
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes in-degrees for nodes _within_ cluster.
 */
class InDegreeCentrality[C <: Cluster[Long] with ClusterWithSubgraph](val weighted: Boolean, val edgeType: String)
  extends NodeWithinClusterCentrality[C] with DegreeCentrality {

  def compute(cluster: C) = {
    cluster.subgraph.getVertices.map(v => (v(ORIGID), degreeOrStrength(v.getInEdges(edgeType)))).toMap
  }
}