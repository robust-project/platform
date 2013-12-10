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

import java.io.{File, PrintWriter}
import ie.deri.uimr.crosscomanalysis.keywords.tfidf.StopWords._
import ie.deri.uimr.crosscomanalysis.util.Functions._
import ie.deri.uimr.crosscomanalysis.keywords.tokenizer.AbstractTokenizer
import ie.deri.uimr.crosscomanalysis.db.DBArgsParser
import org.apache.commons.cli
import ie.deri.uimr.crosscomanalysis.util.Logging
import io.Source

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 03/04/2013
 * Time: 17:36
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
abstract class CitationInfluenceExporter extends DBArgsParser with Logging {

  private val SENTENCE_MODEL_OPT = new cli.Option("sm", true, "file with sentence model")
  private val TOKENS_MODEL_OPT = new cli.Option("tm", true, "file with tokens model")
  private val MIN_WORD_LEN_OPT = new cli.Option("ml", "min-word-lenght", true, "minimal length of a word token")
  private val MIN_WORD_COUNT_OPT = new cli.Option("mw", "min-word-count", true, "minimal count of words per content")
  private val MIN_WORD_FREQ_OPT = new cli.Option("mf", "min-word-freq", true, "minimal number of the word occurences in the corpus")

  private lazy val tokenizer = new AbstractTokenizer(getOptValue(SENTENCE_MODEL_OPT), getOptValue(TOKENS_MODEL_OPT))
  private lazy val MIN_WORD_LEN = getOptValue(MIN_WORD_LEN_OPT).toInt
  private lazy val MIN_WORD_COUNT = getOptValue(MIN_WORD_COUNT_OPT).toInt
  private lazy val MIN_WORD_FREQ = getOptValue(MIN_WORD_FREQ_OPT).toInt

  protected def exportContent(outFile: File, posts: Iterable[(Long, String, String)]) = {
    val tempOutFile = File.createTempFile(outFile.getName, "unfiltered", outFile.getParentFile)
    val tempOut = new PrintWriter(tempOutFile)
    val wordFreq = collection.mutable.HashMap.empty[String, Int]
    // export everything
    for ((id, title, content) <- posts;
    tokens = tokenizeAndFilter(title) ++ tokenizeAndFilter(content)
    if !tokens.isEmpty) {
      tempOut.print("%d\t%s\n".format(id, tokens.mkString(" ")))
      for (word <- tokens) {
        if (!wordFreq.contains(word)) wordFreq(word) = 0
        wordFreq(word) = 1 + wordFreq(word)
      }
    }
    tempOut.close()
    // filter by min. # of words in a post & min. # occurences of the word in the whole corpus
    val out = new PrintWriter(outFile)
    var exportedPosts = Set.empty[Long]
    for (line <- Source.fromFile(tempOutFile).getLines();
         Array(id, allWords) = line.split("\t")) {
      val words = allWords.split(" ").filter(wordFreq(_) >= MIN_WORD_FREQ)
      if (words.length >= MIN_WORD_COUNT) {
        out.println("%s\t%s".format(id, words.mkString(" ")))
        exportedPosts += id.toLong
      }
    }
    out.close()
    tempOutFile.delete()

    exportedPosts
  }

  protected def filterContent(contentFile: File, nodesWithLinks: Set[Long]) = {
    val filteredFile = File.createTempFile(contentFile.getName, "filtered", contentFile.getParentFile)
    val out = new PrintWriter(filteredFile)
    var filtered = 0
    for (line <- Source.fromFile(contentFile).getLines()) {
      val node = line.split("\t")(0).toLong
      if (nodesWithLinks.contains(node)) {
        out.println(line)
        filtered += 1
      }
    }
    out.close()
    filteredFile.renameTo(contentFile)
    assert(filtered == nodesWithLinks.size, "The number of nodes with content doesn't equal number of nodes with links")
    filtered
  }

  private def tokenizeAndFilter(s: String) =
    if (s == null || s.trim().isEmpty) Array.empty[String]
    else tokenizer.tokenize(s).filter(t => !isStopWord(t) && t.matches("[a-zA-Z\\d]+") && t.length >= MIN_WORD_LEN).map(t =>
      stem(t.toLowerCase))

  override protected def commandLineOptions = DIR_OPT :: DB_OPT :: SENTENCE_MODEL_OPT :: TOKENS_MODEL_OPT ::
    MIN_WORD_LEN_OPT :: HELP_OPT :: MIN_WORD_COUNT_OPT :: MIN_WORD_FREQ_OPT :: Nil
}