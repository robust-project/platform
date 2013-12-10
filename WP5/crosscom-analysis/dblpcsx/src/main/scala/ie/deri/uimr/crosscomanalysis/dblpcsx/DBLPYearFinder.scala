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

import ie.deri.uimr.crosscomanalysis.util.Logging
import org.basex.server.ClientSession
import ie.deri.uimr.crosscomanalysis.util.Config.config
import org.basex.core.Context
import org.basex.core.cmd.XQuery
import java.io.ByteArrayOutputStream

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 28/01/2011
 * Time: 18:00
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait DBLPYearFinder extends Logging {
  val session = new ClientSession(config.get("dblp.xmldb.url").get, config.get("dblp.xmldb.port").get.toInt,
    config.get("dblp.xmldb.user").get, config.get("dblp.xmldb.pass").get)
  session.execute("OPEN " + config.get("dblp.xmldb.name").get)

  def findYearInDBLP(stitle: String) = {
    log.debug("Querying stitle " + stitle)
    val sb = new ByteArrayOutputStream(50)
    session.setOutputStream(sb)
    session.execute("XQUERY //stitle[text()='" + stitle + "']/following-sibling::year/text()")
    val res = sb.toString match {
      case year if year.length >= 4 => {
        try {
          log.debug("Processing result set")
          Some(year.substring(0, 4).toInt) // sometimes there are more than one matching records - select the year of the first one
        } catch {
          case e: NumberFormatException => {
            log.error("error " + e.getMessage + " while converting year for stitle: " + stitle, e)
            None
          }
        }
      }
      case _ => None
    }
    session.setOutputStream(null)
    res
  }
}