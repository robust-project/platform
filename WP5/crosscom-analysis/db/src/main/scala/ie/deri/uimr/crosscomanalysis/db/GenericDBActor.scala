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

package ie.deri.uimr.crosscomanalysis.db

import ie.deri.uimr.crosscomanalysis.util.Logging
import org.squeryl.Query
import actors.{Exit, Actor}
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 28/11/2011
 * Time: 13:35
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class GenericDBActor(val db: String) extends Actor with Logging with SessionFactorySetter {
  setUpSessionFactory(db)
  println("Initialized")

  def act() {
    trapExit = true
    loop {
      receive {
        case DBQueryMessage(q, sender) => {
          sender ! "response"
        }
        case Exit(from, reason) =>
          println("Stopping")
          log.info("Stopping Generic DB Actor. Reason: " + reason)
          exit(reason)
        case m => sys.error("Unknown message: " + m)
      }
    }
  }

  protected def processDBQuery[T](q: Query[T]) = {
    transaction {}
    "response"
  }

}

case class DBQueryMessage(q: Query[_], sender: Actor)