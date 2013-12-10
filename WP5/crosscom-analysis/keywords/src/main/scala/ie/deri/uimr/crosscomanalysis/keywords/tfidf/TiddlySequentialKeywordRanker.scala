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

package ie.deri.uimr.crosscomanalysis.keywords.tfidf

import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.util.Logging
import java.util.Date
import ie.deri.uimr.crosscomanalysis.db.schemas.TiddlyWikiSchema._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.keywordsEM
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import ie.deri.uimr.crosscomanalysis.util.Config.config

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 11/03/2011
 * Time: 15:59
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object TiddlySequentialKeywordRanker extends SequentialKeywordRanker with SessionFactorySetter {


  def main(args: Array[String]) {
    setUpSessionFactory(args(1))
    rank(SliceTypes.withName(args(0)).id)
  }

  protected def keywordsForSlice(begin: Date, end: Date) = {
    inTransaction {
      from(keywordsEM, mail, sender)((k, m, s) =>
        where(k.articleid === m.id and m.id === s.mailid and m.sentDate >= Some(begin) and m.sentDate <= Some(end)) select ((k.keywordid, s.sender, k.frequency))).iterator
    }
  }
}