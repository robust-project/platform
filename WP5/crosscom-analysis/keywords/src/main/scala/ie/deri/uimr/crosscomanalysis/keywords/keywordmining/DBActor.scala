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
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.cocit._
import org.squeryl.PrimitiveTypeMode._
import java.sql.SQLException
import ie.deri.uimr.crosscomanalysis.util.Config.config
import ie.deri.uimr.crosscomanalysis.util.Logging
import collection.mutable.{HashSet, MutableList}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/01/11
 * Time: 17:10
 * ©2011 Digital Enterprise Research Institute, NUI Galway. 
 */

object DBActor extends Actor with Logging {

  val minerBatchSize = config.get("miner.keywords.batchSize").get.toInt
  val xFilesBatchSize = config.get("dbactor.xFiles.batchSize").get.toInt
  private val xFilesBatch = new MutableList[XFiles]

  def act() {
    trapExit = true
    loop {
      react {
        case xFiles: XFiles => {
          storeXFiles(xFiles)
          xFiles.sender ! MineXFiles(minerBatchSize)
        }
        case Exit(from, reason) => {
          log.info("Stopping DBActor receive Exit from: " + from + ", reason: " + reason.toString)
          flushCache
          exit(reason)
        }
        case msg => sys.error("Unknown message: " + msg)
      }
    }
  }

  private def storeXFiles(xFiles: XFiles) {
    if (!xFiles.x.isEmpty)
      xFilesBatch += xFiles
    // flush batch if it's above limit
    if (xFilesBatch.size >= xFilesBatchSize)
      storeXFilesBatch
  }

  private def storeXFilesBatch {
    val storedIDs = new HashSet[Long]
    transaction {
      for (xFile <- xFilesBatch) {
        // insert topics
        xFile.x.foreach(storeTopics(_))
        // insert dois
        doi insert xFile.dois
        log.info("Stored new " + xFile.dois.size + " article ids")
        // insert new keyword ids
        // it is necessary to control the novelty of the retrieved keyword, as the control at each miner is not completely
        // reliable, because they act concurrently - this is the only point, where isNew field is read/written synchronously
        for (kid <- xFile.keywordIDs if kid.isNew) {
          try {
            keywordIdEMCSX insert new KeywordID(kid.id, kid.stemmedKeyword, kid.origKeyword)
            kid.isNew = false
            storedIDs += kid.id
          } catch {
            case e: RuntimeException => log.error("Error while inserting keyword id " + kid, e)
          }
        }
        log.info("Stored new " + xFile.keywordIDs.size + " keyword ids")
      }
    }
    KeywordIDActor ! StoredKeywordIDs(storedIDs.toSet)
    xFilesBatch.clear
  }

  private def storeTopics(topics: Seq[KeywordsEM]) {
    if (!topics.isEmpty) {
      for (topic <- topics) {
        try {
          keywordsEM insert topic
        } catch {
          case e: RuntimeException => log.error("Error while inserting topic: " + topic, e)
        }
      }
      //    log.info((topics)
      log.info("Inserted " + topics.size + " keywords for paper " + topics.head.doi)
    }
  }

  private def flushCache {
    transaction {
      if (!xFilesBatch.isEmpty) {
        log.info("Flushing cache of " + xFilesBatch.size + " xFiles")
        storeXFilesBatch
      }
    }
  }
}