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

import com.tinkerpop.blueprints.pgm.Edge
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 29/11/2011
 * Time: 19:30
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

trait DegreeCentrality {

  val weighted: Boolean

  protected def degreeOrStrength(edges: Iterable[Edge]): Double = {
    if (edges.isEmpty) {
      0
    } else {
      if (weighted)
        edges.map(_(WEIGHT)).sum
      else
        edges.size
    }
  }
}