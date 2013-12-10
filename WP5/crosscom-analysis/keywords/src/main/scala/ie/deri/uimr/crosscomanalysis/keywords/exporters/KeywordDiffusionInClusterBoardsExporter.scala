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

import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, ClusterIterator, DBArgsParser}
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.apache.commons.cli
import ie.deri.uimr.crosscomanalysis.keywords.tokenizer.AbstractTokenizer
import org.squeryl.PrimitiveTypeMode._
import java.io.PrintWriter

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 27/06/2013
 * Time: 18:04
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
object KeywordDiffusionInClusterBoardsExporter extends DBArgsParser with Logging with SessionFactorySetter {

  override val COMMAND_NAME = "compute-language-diff-boards"

  private val SENTENCE_MODEL_OPT = new cli.Option("sm", true, "file with sentence model")
  private val TOKENS_MODEL_OPT = new cli.Option("tm", true, "file with tokens model")
  private val MIN_WORD_LEN_OPT = new cli.Option("ml", "min-word-lenght", true, "minimal length of a word token")
  private val SLICE_BEGIN_OPT = new cli.Option("sb", "slice-begin", true, "id of the first slice to export")
  private val SLICE_END_OPT = new cli.Option("se", "slice-end", true, "id of the last slice to export")

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val tokenizer = new AbstractTokenizer(getOptValue(SENTENCE_MODEL_OPT), getOptValue(TOKENS_MODEL_OPT))
      val out = new PrintWriter(getOptValue(OUTFILE_OPT))
      out.println("slice,forum,user,origScore,jaccardScore,relJaccardScore")
      for (sliceId <- getOptValue(SLICE_BEGIN_OPT).toInt to getOptValue(SLICE_END_OPT).toInt) {
        log.info("Processing slice " + sliceId)
        transaction {
          for (cluster <- new ClusterIterator(sliceId, 9, None)) { // 9 is the 'explicit' clustering type
            log.info("Processing cluster " + cluster.index)
            val (origScores, jaccardScores, relScores) = new KeywordDiffusionInCluster(cluster, getOptValue(MIN_WORD_LEN_OPT).toInt,
              tokenizer).computeScore()
            for (user <- origScores.keys) {
              out.println("%d,%d,%d,%e,%e,%e".format(sliceId, cluster.index, user, origScores(user),
                jaccardScores(user), relScores(user)))
            }
          }
        }
      }
      out.close()
    }
  }

  override protected def commandLineOptions = HELP_OPT :: SENTENCE_MODEL_OPT :: SLICE_BEGIN_OPT :: SLICE_END_OPT ::
    TOKENS_MODEL_OPT :: MIN_WORD_LEN_OPT :: OUTFILE_OPT :: DB_OPT :: Nil
}
