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

package ie.deri.uimr.crosscomanalysis.dblpcsx

import ie.deri.uimr.crosscomanalysis.util.Config.config
import io.Source
import xml.pull._
import xml.MetaData
import java.io.FileWriter
import xml.Utility.escape
import ie.deri.uimr.crosscomanalysis.util.{XMLStreamUtil, Logging}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 28/01/2011
 * Time: 12:11
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object SearchTitleCreator extends App with Logging with SearchTitle with XMLStreamUtil {
  val dblpSource = Source.fromFile(config.get("dblp.orig").get)
  val dblpOutput = new FileWriter(config.get("dblp.stitle").get)
  val reader = new XMLEventReader(dblpSource)
  var processingTitle = false
  var title = ""
  val ignoredElements = "cite" :: Nil

  for (event <- reader) {
    event match {
      case EvElemStart(_, elemName, _, _) => {
        if (elemName == "title")
          processingTitle = true
        if (!ignoredElements.contains(elemName))
          out(toXML(event))
      }
      case EvElemEnd(_, elemName) => {
        if (elemName == "title") {
          processingTitle = false
          out(toXML(event)) // end the <title> element
          out("<stitle>" + createSearchTitle(title) + "</stitle>")
        } else if (!ignoredElements.contains(elemName)) {
          out(toXML(event))
        }
      }
      case EvText(text) => {
        if (processingTitle) {
          title = text
        }
        out(toXML(event))
      }
      case _ => out(toXML(event))
    }
  }

  dblpOutput.flush
  dblpOutput.close

  private def out(s: String) {
    dblpOutput.write(s)
  }
}