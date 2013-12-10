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
import ie.deri.uimr.crosscomanalysis.db.tables.ancestry._
import ie.deri.uimr.crosscomanalysis.db.tables.boardsie.Forum

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 27/03/2012
 * Time: 12:25
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

object Ancestry extends Schema {
  val forums = table[Forum]("forums")
  val posts = table[Post]("posts")
//  val replies = table[Reply]("replies_with_dates")
  val threads = table[Thread]("threads")
  val users = table[User]("users")
}
