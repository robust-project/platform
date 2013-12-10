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

import xml.Utility._
import xml.pull._
import xml.MetaData

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/11/2011
 * Time: 11:14
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Helper trait with methods for serialization of XML events in a stream processing.
 */
trait XMLStreamUtil {

  protected def toXML(ev: XMLEvent) = {
    ev match {
      case EvElemStart(pre, label, attrs, scope) => {
        "<" + label + attrsToString(attrs) + ">"
      }
      case EvElemEnd(pre, label) => {
        "</" + label + ">\n"
      }
      case EvText(text) => if (text.trim.isEmpty) "" else escape(text)
      case EvComment(comment) => "<!--" + comment + "-->\n"
      case EvProcInstr(target, text) => "<?" + target + " " + text + "?>\n"
      case _ => ""
    }
  }

  protected def attrsToString(attrs: MetaData) = {
    attrs.length match {
      case 0 => ""
      case _ => attrs.map((m: MetaData) => " " + m.key + "='" + m.value + "'").reduceLeft(_ + _)
    }
  }

}