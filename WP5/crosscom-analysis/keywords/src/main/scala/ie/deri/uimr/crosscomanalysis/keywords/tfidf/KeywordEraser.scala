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

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 09/12/10
 * Time: 17:36
 * ©2010 Digital Enterprise Research Institute, NUI Galway. 
 */

object KeywordEraser extends SessionFactorySetter with DBArgsParser {


  override val COMMAND_NAME = "erase-prepared-keywords"

  def eraseKeywordId {
    transaction {
      keywordId.deleteWhere(k => 1 === 1) // all records
      //      keywordIdEM.deleteWhere(k => 1 === 1)
      //      keywordIdEMToken.deleteWhere(k => 1 === 1)
    }
  }

  def eraseRankedKeywords {
    transaction {
      rankedKeywordsObjects.deleteWhere(k => 1 === 1)
      //      rankedKeywordsEMAuthors.deleteWhere(k => 1 === 1)
      //      rankedKeywordsEMTokenAuthors.deleteWhere(k => 1 === 1)
    }
  }

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))

      eraseKeywordId
      eraseRankedKeywords
    }
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: Nil
}