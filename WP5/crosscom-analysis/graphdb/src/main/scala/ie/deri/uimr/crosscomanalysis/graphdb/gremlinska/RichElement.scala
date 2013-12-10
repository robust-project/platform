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

package ie.deri.uimr.crosscomanalysis.graphdb.gremlinska

import collection.mutable.Map
import collection.JavaConversions.collectionAsScalaIterable
import com.tinkerpop.blueprints.pgm.Element
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperty

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 11/04/2011
 * Time: 20:56
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * * <b>Acknowledgments:</b>This work was supported by Science Foundation Ireland (SFI) projects
 * Grant No. SFI/08/CE/I1380 (Lion-2) and Grant No. 08/SRC/I1407 (Clique: Graph & Network Analysis Cluster).
 */

class RichElement(val elem: Element) extends Map[String, AnyRef] {

  def +=(prop: (String, AnyRef)) = {
    elem.setProperty(prop._1, prop._2)
    this
  }

  def -=(prop: String) = {
    elem.removeProperty(prop)
    this
  }

  def iterator = elem.getPropertyKeys.map(prop => (prop, elem.getProperty(prop))).iterator

  def get(prop: String) = {
    val ret = elem.getProperty(prop)
    if (ret == null) None
    else Some(ret)
  }

  def apply[T](prop: GraphProperty[T]) = get(prop.name).get.asInstanceOf[T]
}