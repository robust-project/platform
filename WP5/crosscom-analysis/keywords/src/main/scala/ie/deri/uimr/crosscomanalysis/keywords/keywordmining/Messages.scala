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

import actors.Actor
import ie.deri.uimr.crosscomanalysis.db.tables.cocit._

/**
 * Created by IntelliJ IDEA.
 * User: vacbel
 * Date: 12/01/2011
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
case class MineXFiles(x: Int)

case class XFiles(x: Seq[Seq[KeywordsEM]], dois: Seq[DOI], keywordIDs: Seq[RetrievedKeywordID], sender: Actor)

case class StartMining

case class Stop

case class NextFile

case class SendMeKeywordID(stemmedKeyword: String, origKeyword: String)

case class RetrievedKeywordID(id: Long, stemmedKeyword: String, origKeyword: String, var isNew: Boolean)

case class StoredKeywordIDs(ids: Set[Long])