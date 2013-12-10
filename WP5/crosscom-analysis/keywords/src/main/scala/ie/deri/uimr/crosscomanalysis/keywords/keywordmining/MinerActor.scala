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

import actors.{Actor, Exit}
import ie.deri.unlp.expertisemining.core.nlp.TopicExtractor
import ie.deri.unlp.gate.expertisemining.ExtractedTopic
import collection.mutable.MutableList
import scala.collection.JavaConversions.collectionAsScalaIterable
import tools.nsc.io.File
import ie.deri.uimr.crosscomanalysis.util.Functions.stem
import ie.deri.uimr.crosscomanalysis.db.tables.cocit._
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.util.Config.config
import java.util.{Collections, List => JavaList}
import collection.Seq

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/01/11
 * Time: 17:00
 * ©2011 Digital Enterprise Research Institute, NUI Galway. 
 */

class MinerActor(private val te: TopicExtractor, private val articleIdActor: ArticleIDActor) extends Actor with Logging {

  private var shouldRun = true
  private val MAX_TOPICS_PER_DOC = config("miner.max.topics.per.doc").toInt
  assert(MAX_TOPICS_PER_DOC >= 0, "maximum number of topics has to be at least 0")

  def act() {
    trapExit = true
    loopWhile(shouldRun) {
      react {
        case MineXFiles(x) => DBActor ! mineXFiles(x)
        case Exit(from, reason) => {
          log.info("Stopping MinerActor, received Exit from: " + from + ", reason: " + reason.toString)
          exit(reason)
        }
        case msg => error("Unknown message: " + msg)
      }
    }
  }

  private def mineXFiles(x: Int) = {
    val minedFiles = new MutableList[Seq[KeywordsEM]]
    val dois = new MutableList[DOI]
    val keywordIDs = new MutableList[RetrievedKeywordID]

    var mined = 0
    var response: Option[File] = None
    try {
      response = (WalkerActor !? NextFile).asInstanceOf[Option[File]]
      while (mined < x && response.isDefined) {
        val topics = te.getTopicsFromUrl(response.get.toURI.toString).getExtractedTopics
        Collections.sort(topics)
        val selectedTopics = topics.slice(0, (if(MAX_TOPICS_PER_DOC == 0) topics.size else MAX_TOPICS_PER_DOC))
        minedFiles += processTopics(selectedTopics,fileID(response.get), dois, keywordIDs)
        mined += 1

        if (mined < x)
          response = (WalkerActor !? NextFile).asInstanceOf[Option[File]]
      }
    } catch {
      case e : Exception => log.error("Error while mining file " + response.get.name, e)
    }
    // if the response is empty, there's no file left to process
    if (response.isEmpty) {
      log.info("No files left, stopping MinerActor")
      shouldRun = false
    }

    XFiles(minedFiles, dois, keywordIDs, this)
  }

  private def processTopics(topics: Iterable[ExtractedTopic], doi: String, dois: MutableList[DOI], keywordIDs: MutableList[RetrievedKeywordID]) = {
    val articleId = (articleIdActor !? doi).asInstanceOf[Long]
    dois += new DOI(doi, articleId)
    val result = new MutableList[KeywordsEM]
    for (topic <- topics if checkTopic(topic)) {
      val stemmed = stem(topic.getTopicString.toLowerCase.trim).trim
      val t = result.find(_.keyword == stemmed)
      if (t.isDefined) {
        t.get.incrementFrequency
      } else {
        val keywordId = (KeywordIDActor !? SendMeKeywordID(stemmed, topic.getTopicString.toLowerCase)).asInstanceOf[RetrievedKeywordID]
        if (keywordId.isNew) keywordIDs += keywordId
        result += new KeywordsEM(articleId, stemmed, 1, doi, keywordId.id)
      }
    }

    result.toSeq
  }

  private def fileID(file: File) = file.name.substring(0, file.name.length - 4)

  private def checkTopic(t: ExtractedTopic) = {
    val topicString = t.getTopicString.toLowerCase.trim
    // 500 is the current column constraint
    if (topicString.length > 500 || topicString.length == 0)
      false
    else {
      val stemmed = stem(topicString).trim
      if (stemmed.length == 0)
        false
      else
        true
    }
  }
}