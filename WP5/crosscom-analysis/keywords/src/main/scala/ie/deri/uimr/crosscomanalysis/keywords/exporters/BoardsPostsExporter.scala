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

package ie.deri.uimr.crosscomanalysis.keywords.exporters

import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE._
import ie.deri.uimr.crosscomanalysis.keywords.tfidf.StopWords._
import ie.deri.uimr.crosscomanalysis.util.Functions._
import ie.deri.uimr.crosscomanalysis.keywords.tokenizer.AbstractTokenizer
import org.apache.commons.cli
import java.io.PrintWriter
import ie.deri.uimr.crosscomanalysis.util.Logging

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 26/07/2013
 * Time: 15:48
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
object BoardsPostsExporter extends DBArgsParser with SessionFactorySetter with Logging {

  private val SENTENCE_MODEL_OPT = new cli.Option("sm", true, "file with sentence model")
  private val TOKENS_MODEL_OPT = new cli.Option("tm", true, "file with tokens model")
  private val MIN_WORD_LEN_OPT = new cli.Option("ml", "min-word-lenght", true, "minimal length of a word token")

  private lazy val tokenizer: AbstractTokenizer = new AbstractTokenizer(getOptValue(SENTENCE_MODEL_OPT), getOptValue(TOKENS_MODEL_OPT))
  private lazy val minWordLength = getOptValue(MIN_WORD_LEN_OPT).toInt

  override val COMMAND_NAME = "export-boards-posts"

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val out = new PrintWriter(getOptValue(OUTFILE_OPT))
      var c = 0
      transaction {
        for (post <- posts) {
          c += 1
          val title = tokenizeAndFilter(post.title)
          val content = tokenizeAndFilter(post.content)
          if (!(title.isEmpty && content.isEmpty)) {
            out.print("%d %tF %s\n".format(post.postid, post.posteddate, (title ++ content).mkString(" ")))
          }
          if (c % 1000 == 0) log.info("Exported %d posts".format(c))
        }
      }
      out.close()
      log.info("Exported %d posts altogether".format(c))
    }
  }

  private def tokenizeAndFilter(s: String) =
    if (s == null || s.trim().isEmpty) Seq.empty[String]
    else tokenizer.tokenize(s).toSeq.filter(t => !isStopWord(t) && t.matches("[a-zA-Z\\d]+") &&
      t.length >= minWordLength).map(t => stem(t.toLowerCase))

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: SENTENCE_MODEL_OPT :: TOKENS_MODEL_OPT ::
    MIN_WORD_LEN_OPT :: OUTFILE_OPT :: Nil
}
