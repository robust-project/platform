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
import ie.deri.uimr.crosscomanalysis.util.Config.config
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.util.Logging
import tokyocabinet.HDB
import collection.mutable.HashMap
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.keywordIdEMCSX

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/01/2011
 * Time: 23:05
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object KeywordIDActor extends Actor with Logging {

  // start with 0 after increment
  private var lastID = -1l
  private val cache = createCache
  // map of waiting-to-be-stored keyword ids
  private val pendingToBeStored = new HashMap[Long, RetrievedKeywordID]


  init

  def act() {
    trapExit = true
    loop {
      react {
        case SendMeKeywordID(stemmed, orig) => reply(retrieveKeywordID(stemmed, orig))
        case StoredKeywordIDs(ids) => pendingToBeStored --= ids
        case Exit(from, reason) => {
          log.info("Stopping KeywordIDActor, received Exit from: " + from + ", reason: " + reason.toString)
          shutdown
          exit(reason)
        }
        case msg => error("Unknown message: " + msg)
      }
    }
  }

  private def retrieveKeywordID(stemmed: String, orig: String) = {
    val id = cache.get(stemmed)
    if (id == null) {
      val newId = incrementAndReturnID()
      if (!cache.put(stemmed, newId.toString)) error("Error saving pair '" + stemmed + "'->" + id + ", reason: " + cache.errmsg)
      val kid = RetrievedKeywordID(newId, stemmed, orig, true)
      pendingToBeStored(newId) = kid
      kid
    } else {
      val idAsLong = id.toLong
      /*
       The logic behind this pending to be stored is that if the new keyword is sent to the miner, we do not know, when it will
       be stored, so we are sending is 'as new' to all miners, and the DBActor will set it as stored, i.e. not new after
       it stores the first reference to the retrieved keyword id. All subsequent attempts to store it will be thus filtered
       by DBActor, which also sends periodically set of stored keyword ids, so that this actor can remove them from the
       pending keyword ids.
       */
      if (pendingToBeStored.contains(idAsLong))
        pendingToBeStored(idAsLong)
      else
        RetrievedKeywordID(id.toLong, stemmed, orig, false)
    }
  }

  private def incrementAndReturnID() = {
    lastID += 1
    lastID
  }

  private def init = {
    transaction {
      val maxID = from(keywordIdEMCSX)(kid => compute(max(kid.id))).single.measures
      if (maxID.isDefined) {
        lastID = maxID.get
        log.info("Setting " + lastID + " as an initial keywordid")
      }
    }
  }

  private def createCache = {
    val hdb = new HDB
    if (!hdb.open(config.get("keyidactor.db.path").get, HDB.OWRITER | HDB.OCREAT))
      error(hdb.errmsg)
    hdb
  }

  private def shutdown {
    if (!cache.close)
      log.error("Unsuccessful closing of keyword id cache: " + cache.errmsg)
  }
}