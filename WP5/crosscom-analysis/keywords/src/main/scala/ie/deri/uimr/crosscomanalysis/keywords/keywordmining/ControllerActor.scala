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

import ie.deri.uimr.crosscomanalysis.util.Logging
import actors.{AbstractActor, Actor, Exit}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/01/11
 * Time: 17:46
 * ©2011 Digital Enterprise Research Institute, NUI Galway. 
 */

class ControllerActor(val miners: Seq[MinerActor], articleIdActor: ArticleIDActor) extends Actor with Logging {
  private var exited = 0
  private var notFinished = true
  private val otherActors = DBActor :: articleIdActor :: KeywordIDActor :: WalkerActor :: Nil

  def act() {
    trapExit = true
    miners.foreach(link(_))
    otherActors.foreach(link(_))

    loopWhile(notFinished) {
      react {
        // if miner actor exited, we can continue until all of them are gone
        case Exit(from: MinerActor, reason) => {
          processReason(from, reason)
          exited += 1
          if (exited == miners.size) {
            log.info("All miners have exited, stopping other actors")
            otherActors.foreach(_ ! Exit(this, "All miners are gone"))
            notFinished = false
          }
        }
        // if any other actor exits, we are finished
        case Exit(from, reason) => {
          processReason(from, reason)
          log.info("Stopping other actors")
          otherActors.filterNot(_ == from).foreach(_ ! Exit(this, reason))
          notFinished = false
          Thread.sleep(300000)
          System.exit(-1)
        }
      }
    }
  }

  private def processReason(from: AbstractActor, reason: AnyRef) {
    log.info("Actor " + from + " has exited, because: ")
    reason match {
      case e: Exception => {
        log.info("an exception occured: " + e.getMessage)
        log.error(e, e)
      }
      case other => log.info(other.toString)
    }
  }
}

