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

package ie.deri.uimr.crosscomanalysis.keywords.tokenizer

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}

object TokenizeKeywords extends SessionFactorySetter with DBArgsParser {

  override val COMMAND_NAME = "tokenize-keywords"

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val tokenizer = new AbstractTokenizer(args(0), args(1))
      transaction {
        for (ftItem <- from(fulltexts)(f => select(f))) {
          try {
            //          TokenizedKeywords.insert(new TokenizedKeywords(ftItem.articleid, ftItem.fullText))
          } catch {
            case e: RuntimeException => println(e.getMessage)
          }
        }
      }
    }
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: Nil
}