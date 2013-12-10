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

package ie.deri.uimr.crosscomanalysis.util

import java.io.InputStream

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/12/10
 * Time: 18:00
 * ©2010 Digital Enterprise Research Institute, NUI Galway. 
 */

object Properties {
  def loadFile(is: InputStream) = {
    val props = new java.util.Properties
    props.load(is)
    is.close()
    val iter = props.entrySet.iterator
    val vals = scala.collection.mutable.Map[String, String]()
    while (iter.hasNext) {
      val item = iter.next
      vals += (item.getKey.toString -> item.getValue.toString)
    }
    vals.toMap.withDefaultValue("")
  }

  def saveFile(sprops: Map[String, String], fname: String) {
    val jprops = new java.util.Properties
    sprops.foreach(a => jprops.put(a._1, a._2))
    val file = new java.io.FileOutputStream(fname)
    jprops.store(file, "Scala Properties: " + fname)
    file.close()
  }
}