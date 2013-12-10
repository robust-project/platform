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

package ie.deri.uimr.crosscomanalysis.db.schemas

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.tables.tiddlywiki._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 18/02/2011
 * Time: 16:58
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object TiddlyWikiSchema extends Schema {
  val header = table[Header]("header")
  val mail = table[Mail]("mail")
  val sender = table[Sender]("sender")
  val receiver = table[Receiver]("receiver")
  val user = table[User]("user")
  val thread = table[Thread]("thread")

  on(mail)(m => declare(m.id is (autoIncremented)))
  on(user)(u => declare(u.id is (autoIncremented)))
}