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
import tools.nsc.io.{File, Directory}
import java.io.{File => JavaFile}
import collection.mutable.MutableList
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.doi

/**
 * Created by IntelliJ IDEA.
 * User: vaclav.belak@deri.org
 * Date: 12/01/2011
 * Time: 15:19
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object WalkerActor extends Actor with Logging {

  private val walker = new Directory(new JavaFile(config.get("corpus").get)).deepFiles.filter(f =>
    f.hasExtension("txt") && f.length <= config.get("walker.maxFileSize").get.toLong && f.length > 0)
  private val processedDOIs = new MutableList[String]


  init

  def act() {
    trapExit = true
    loop {
      react {
        case NextFile => reply(nextFile)
        case Exit(from, reason) => {
          log.info("Stopping WalkerActor, received Exit from: " + from + ", reason: " + reason.toString)
          exit(reason)
        }
        case msg => error("Unknown message: " + msg)
      }
    }
  }

  private def init {
    transaction {
      processedDOIs ++= (from(doi)(d => select(d.doi))).toSeq
      log.info("Adding " + processedDOIs.size + " processed DOIs")
    }
  }

  private def nextFile: Option[File] = {
    if (walker.hasNext) {
      val f = walker.next.toFile
      if (!processedDOIs.contains(fileID(f))) {
        processedDOIs += fileID(f)
        Some(f)
      } else {
        log.info("Skipping file " + f)
        nextFile
      }
    } else None
  }

  private def fileID(file: File) = file.name.substring(0, file.name.length - 4)
}