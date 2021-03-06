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

import ie.deri.uimr.crosscomanalysis.jung.algorithms.scoring.VertexGroupScorer
import collection.JavaConversions
import collection.Set

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07/12/2011
 * Time: 12:17
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

abstract class VertexGroupCentrality[V] extends VertexGroupScorer[V, java.lang.Double] {
  def getVertexGroupScore(group: Set[V]): Double

  def getVertexGroupScore(group: java.util.Set[V]) = getVertexGroupScore(JavaConversions.iterableAsScalaIterable(group).toSet)
}