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

package ie.deri.uimr.crosscomanalysis.graphdb

import collection.mutable.HashMap

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 23/04/2011
 * Time: 22:53
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

trait GraphPropertiesLike {

  private var _id = -1

  private val _values = new HashMap[Int, GraphProperty[_]]
  private val _valuesToProperties = new HashMap[String, GraphProperty[_]]

  private def nextId = {
    _id += 1
    _id
  }

  def apply(id: Int) = _values(id)

  def apply(name: String) = _valuesToProperties(name)

  protected def Property[T](name: String) = {
    checkProperty(name)
    Property[T](nextId, name)
  }

  protected def Property[T](id: Int, name: String) = {
    checkProperty(id, name)
    this._id = id
    val p = GraphProperty[T](id, name)
    _values(p.id) = p
    _valuesToProperties(p.name) = p
    p
  }

  private def checkProperty(id: Int, name: String) {
    if (_values.contains(id) || _values.values.toSet.find(_.name == name).isDefined)
      throw new PropertyAlreadyExistsException(name, id)
  }

  private def checkProperty(name: String) {
    checkProperty(-1, name)
  }

  implicit def asGraphPropertyString(p: GraphProperty[_]) = p.name
}

class PropertyAlreadyExistsException(val property: String, val id: Int) extends Exception {
  override def getMessage = "Either property: " + property + ", or its id: " + id + " already exists"
}