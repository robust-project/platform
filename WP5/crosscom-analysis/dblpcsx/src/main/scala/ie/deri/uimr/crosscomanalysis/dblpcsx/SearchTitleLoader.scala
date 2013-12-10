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

package ie.deri.uimr.crosscomanalysis.dblpcsx

import collection.mutable.HashMap
import io.Source
import java.io.File
import xml.pull._
import ie.deri.uimr.crosscomanalysis.util.Logging

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 04/05/2011
 * Time: 09:40
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class SearchTitleLoader(val dblpFile: File) extends Logging with SearchTitle {
  private val yearsMap = new HashMap[String, Int]
  private val bookTitleMap = new HashMap[String, String]

  def findYearInDBLP(stitle: String) = yearsMap.get(stitle)
  
  def findBookTitleInDBLP(stitle: String) = bookTitleMap.get(stitle)

  def load() {
    val dblpSource = Source.fromFile(dblpFile)
    val reader = new XMLEventReader(dblpSource)
    var processingTitle = false
    var processingYear = false
    var processingBookTitle = false
    var stitle = ""
    var year = 0
    var bookTitle = ""
    val items = "article" :: "inproceedings" :: "proceedings" :: "book" :: "incollection" :: "phdthesis" :: "mastersthesis" :: "www" :: Nil
    var count = 0

    for (event <- reader) {
      event match {
        case EvElemStart(_, elemName, _, _) =>
          if (elemName == "title") processingTitle = true
          else if (elemName == "year") processingYear = true
          else if (elemName == "booktitle") processingBookTitle = true
        case EvElemEnd(_, elemName) =>
          if (items.contains(elemName)) {
            if (stitle != "" && year > 0) {
              // we have found something
              yearsMap(stitle) = year
              count += 1
              if (count % 1000 == 0) log.info("Found " + count + " years so far")
              log.debug("Adding year: " + year + " to stitle '" + stitle + "'")
            } else log.debug("Year not found for title: " + stitle)
            if (stitle != "" && bookTitle != "") {
              // we have found book title
              bookTitleMap(stitle) = bookTitle
            } else log.debug("Book title not found for title: " + stitle)
            // reset values
            stitle = ""
            year = 0
            bookTitle = ""
            processingTitle = false
            processingYear = false
          }
          else if (elemName == "title") processingTitle = false
          else if (elemName == "year") processingYear = false
          else if (elemName == "booktitle") processingBookTitle = false
        case EvText(text) if text.trim.size > 0 =>
          if (processingTitle) {
            stitle = createSearchTitle(text.trim)
          } else if (processingYear) {
            try {
              year = text.trim.toInt
            } catch {
              case e: NumberFormatException => log.error(e)
            }
          } else if (processingBookTitle) {
            bookTitle = text.trim
          }
        case _ => // do nothing
      }
    }
    log.info("Loaded " + yearsMap.size + " publications' years")
    log.info("Loaded " + bookTitleMap.size + " publications' book titles")
    dblpSource.close()
  }
}