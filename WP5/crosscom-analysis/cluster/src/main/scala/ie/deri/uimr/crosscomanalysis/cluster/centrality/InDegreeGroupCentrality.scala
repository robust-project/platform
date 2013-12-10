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

import collection.Set
import scala.collection.JavaConversions.iterableAsScalaIterable
import com.tinkerpop.blueprints.pgm.{Vertex, Graph}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07/12/2011
 * Time: 12:12
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class InDegreeGroupCentrality(graph: Graph, val edgeType: String, val weighted: Boolean = true)
  extends VertexGroupCentrality[Vertex] with DegreeCentrality {

  def getVertexGroupScore(group: Set[Vertex]) = {
    // get edges leading from non-group vertices to the group
    degreeOrStrength(group.map(_.getInEdges(edgeType)).flatten.filterNot(e => group.contains(e.getOutVertex)))
  }
}