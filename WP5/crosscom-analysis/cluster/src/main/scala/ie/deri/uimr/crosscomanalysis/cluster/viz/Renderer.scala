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

package ie.deri.uimr.crosscomanalysis.cluster.viz

import java.io.File
import com.tinkerpop.blueprints.pgm.{Vertex, Graph}
import scala.collection.{Set,Map,Seq}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 09/02/2011
 * Time: 18:07
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait Renderer {
  /**
   * @param g network to be visualized
   * @param clusters sequence of tuples of (cluster index/name, set of corresponding nodes)
   * @param vertexSize map nodes -> size of nodes [0,1]
   */
  def render(g: Graph, clusters: Seq[(Int, Set[Vertex])], nodeSize: Map[Vertex, Double], output: File)
}