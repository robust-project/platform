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

package ie.deri.uimr.crosscomanalysis.graphdb.traversals

import collection.JavaConversions.{asScalaMap, asScalaIterable}
import com.tinkerpop.pipes.sideeffect.GroupCountPipe
import java.util.{HashSet, Arrays, HashMap}
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.CSXGraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import com.tinkerpop.blueprints.pgm.{Element, Vertex}
import com.tinkerpop.pipes.transform.{InVertexPipe, OutEdgesPipe, OutVertexPipe, InEdgesPipe}
import com.tinkerpop.pipes.util.Pipeline
import com.tinkerpop.pipes.filter.{FilterPipe, ObjectFilterPipe}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 11/04/2011
 * Time: 16:05
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object Cocitation {

  /**
   * @return Mapping V->N, where V are clusters cocited with 'start' vertex, and N is cocitation count.
   */
  def clusterCocitations(start: Vertex) = {
    val citedByEdge = new InEdgesPipe(CITES)
    val citedByV = new OutVertexPipe
    val citesE = new OutEdgesPipe(CITES)
    val citedV = new InVertexPipe
    val count = new HashMap[Vertex, java.lang.Number]
    val filterStartV = new ObjectFilterPipe[Vertex](start, FilterPipe.Filter.NOT_EQUAL)
    val countCocits = new GroupCountPipe[Vertex](count)
    val cocitPipe = new Pipeline[Vertex, Vertex](citedByEdge, citedByV, citesE, citedV, filterStartV, countCocits)
    cocitPipe.setStarts(Arrays.asList(start))
    while (cocitPipe.hasNext) cocitPipe.next // traverse the whole pipe (count the cocits)

    asScalaMap(count).mapValues(_.longValue())
  }

  /**
   * @return Authors of the cluster.
   */
  def authorPipe(cluster: Vertex) = {
    val authorE = new InEdgesPipe("author_of")
    val authorV = new OutVertexPipe
    val clusterAuthorP = new Pipeline[Vertex, Vertex](authorE, authorV)
    clusterAuthorP.setStarts(Arrays.asList(cluster))

    asScalaIterable(clusterAuthorP)
  }

  def authorCocitations(author: Vertex, startYear: Int, endYear: Int) = {
    val authorsClusters = new HashSet[Element]
    author.trav[Vertex].outE(AUTHOR_OF).inV.strain {
      v =>
        v(YEAR) >= startYear && v(YEAR) <= endYear
    }.
      aggregate(authorsClusters).bothE(COCITED).bothV.strain {
      v =>
        v(YEAR) >= startYear && v(YEAR) <= endYear && !authorsClusters.contains(v)
    }.
      inE(AUTHOR_OF).outV.strain(_ != author)
  }
}