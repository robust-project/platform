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

package ie.deri.uimr.crosscomanalysis.keywords.keywordmining

import actors.{Actor, Exit}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.doi

/**
 * Created by IntelliJ IDEA.
 * User: vacbel
 * Date: 12/01/2011
 * Time: 12:02
 *
 * WARNING: Depending on the closure articleIdTransformer, this class may be required to instantiated only _once_, because
 * multiple instances would then share the state through it and won't be independent!
 */

class ArticleIDActor(private val articleIdTransformer: (String => Long)) extends Actor with Logging {

  private var lastID = -1l
  init

  def act() {
    trapExit = true
    loop {
      react {
        case doi: String => {
          // generate new articleid
          val articleid = articleIdTransformer(doi)
          reply(articleid)
        }
        case Exit(from, reason) => {
          log.info("Stopping ArticleIDActor receive Exit from: " + from + ", reason: " + reason.toString)
          exit(reason)
        }
        case msg => error("Unknown message: " + msg)
      }
    }
  }

  private def incrementAndReturnID(s: String) = {
    lastID += 1
    lastID
  }

  private def init {
    transaction {
      val maxID = (from(doi)(d => compute(max(d.articleid)))).single.measures
      if (maxID.isDefined) {
        lastID = maxID.get
        log.info("Setting " + lastID + " as an initial articleid")
      }
    }
  }
}
