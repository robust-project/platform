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

package ie.deri.uimr.crosscomanalysis.db.importers

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}
import java.io.File
import io.Source
import java.util.Date
import xml.pull.{EvText, EvElemEnd, EvElemStart, XMLEventReader}
import java.text.SimpleDateFormat
import collection.immutable.HashMap
import ie.deri.uimr.crosscomanalysis.db.tables.ancestry.{User, Post, Thread}
import ie.deri.uimr.crosscomanalysis.db.tables.boardsie.Forum
import ie.deri.uimr.crosscomanalysis.db.schemas.Ancestry._
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 27/03/2012
 * Time: 13:09
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

object AncestryPostImporter extends DBArgsParser with Logging with SessionFactorySetter {

  override val COMMAND_NAME = "import-ancestry"

  private val dateFormat = new SimpleDateFormat("dd MMM yyyy")
  private var trecToNumericalThreadID = new HashMap[String, Long]
  private var forumID = new HashMap[String, Long]
  private var storedFora = Set.empty[String]
  private var storedUsers = Set.empty[Long]
  private var postBuffer = List.empty[Post]
  private var userBuffer = List.empty[User]
  private var forumBuffer = List.empty[Forum]
  private var threadBuffer = List.empty[Thread]

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      for (file <- new File(getOptValue(DIR_OPT)).listFiles().filter(_.getName.endsWith("trectext"))) {
        log.info("Processing file " + file)
        processFile(file)
      }
      // store the remaining records in the buffers
      storeBuffers(0)
    }
  }

  private def processFile(file: File) {
    val reader = new XMLEventReader(Source.fromFile(file))
    var postID: Long = -1
    var processingPostID: Boolean = false
    var forumName: String = ""
    var processingForumName: Boolean = false
    var datePosted: Date = new Date()
    var processingDatePosted: Boolean = false
    var threadId: String = ""
    var processingThreadID: Boolean = false
    var trecPostID: String = ""
    var processingTrecPostID: Boolean = false
    var postURL: String = ""
    var processingPostURL: Boolean = false
    var userName: String = ""
    var processingUserName: Boolean = false
    var userID: Long = -1
    var processingUserID: Boolean = false
    var title: String = ""
    var processingTitle: Boolean = false
    var content: String = ""
    var processingContent: Boolean = false

    def setStateToFalse() {
      processingPostID = false
      processingForumName = false
      processingDatePosted = false
      processingThreadID = false
      processingTrecPostID = false
      processingPostURL = false
      processingUserName = false
      processingUserID = false
      processingTitle = false
      processingContent = false
    }

    for (event <- reader) {
      event match {
        case EvElemStart(_, elemName, _, _) => {
          if (elemName == "PID") processingPostID = true
          else if (elemName == "SUBFORUM") processingForumName = true
          else if (elemName == "DATE_STR") processingDatePosted = true
          else if (elemName == "THREAD_ID") processingThreadID = true
          else if (elemName == "POST_ID") processingTrecPostID = true
          else if (elemName == "POST_URL") processingPostURL = true
          else if (elemName == "AUTHOR_NAME") processingUserName = true
          else if (elemName == "AUTHOR") processingUserID = true
          else if (elemName == "POST_TITLE") processingTitle = true
          else if (elemName == "TEXT") processingContent = true
        }
        case EvElemEnd(_, elemName) => {
          setStateToFalse()
          if (elemName == "DOC")
            storePost(postID, forumName, datePosted, threadId, trecPostID, postURL, userName, userID, title, content)
        }
        case EvText(text) if text.trim.size > 0 => {
          if (processingPostID) postID = text.toLong
          else if (processingForumName) forumName = text
          else if (processingDatePosted) datePosted = dateFormat.parse(text)
          else if (processingThreadID) threadId = text
          else if (processingTrecPostID) trecPostID = text
          else if (processingPostURL) postURL = text
          else if (processingUserName) userName = text
          else if (processingUserID) userID = text.toLong
          else if (processingTitle) title = text
          else if (processingContent) content = text
        }
        case _ => // do nothing
      }
    }
  }

  private def storePost(postID: Long, forumName: String, datePosted: Date, threadID: String, trecPostID: String,
                        postURL: String, userName: String, userID: Long, title: String, content: String) {
    log.debug("Storing post with data: " + postID + ", " + forumName + ", " + datePosted + ", " + threadID + ", " +
      trecPostID + ", " + postURL + ", " + userName + ", " + userID + ", " + title + ", " + content)
    // store forum if not previously stored
    if (!storedFora.contains(forumName)) {
      forumID = forumID + (forumName -> (forumID.size + 1).toLong)
      forumBuffer = new Forum(forumID(forumName), forumName, 0) :: forumBuffer // by defaul no parents
      storedFora = storedFora + forumName
    }
    // store thread if not previously stored
    if (!trecToNumericalThreadID.contains(threadID)) {
      trecToNumericalThreadID = trecToNumericalThreadID + (threadID -> (trecToNumericalThreadID.size + 1).toLong)
      threadBuffer = new Thread(trecToNumericalThreadID(threadID), threadID, forumID(forumName), userID) :: threadBuffer
    }
    // store user if not previously stored
    if (!storedUsers.contains(userID)) {
      userBuffer = new User(userID, userName) :: userBuffer
      storedUsers = storedUsers + userID
    }
    postBuffer = new Post(postID, trecToNumericalThreadID(threadID), userID, title, datePosted, content,
      postURL, trecPostID) :: postBuffer
    storeBuffers(1000)
  }
  
  private def storeBuffers(limit: Int) {
    transaction {
      if (forumBuffer.size > limit) {
        forums insert forumBuffer
        forumBuffer = List.empty[Forum]
      }
      if (userBuffer.size > limit) {
        users insert userBuffer
        userBuffer = List.empty[User]
      }
      if (threadBuffer.size > limit) {
        threads insert threadBuffer
        threadBuffer = List.empty[Thread]
      }
      if (postBuffer.size > limit) {
        posts insert postBuffer
        postBuffer = List.empty[Post]
      }
    }
  } 

  override protected def commandLineOptions = DIR_OPT :: DB_OPT :: HELP_OPT :: Nil
}