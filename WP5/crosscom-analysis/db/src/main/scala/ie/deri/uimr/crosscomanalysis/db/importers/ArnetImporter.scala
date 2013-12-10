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

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.ArnetSchema._
import java.io.File
import io.Source
import ie.deri.uimr.crosscomanalysis.db.tables.arnet._
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.apache.commons._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 20/07/2012
 * Time: 11:33
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * Importer of the dumps from http://arnetminer.org/citation.
 *
 * Format:
#* --- paperTitle
#@ --- Authors
#year ---- Year
#conf --- publication venue
#citation --- citation number (both -1 and 0 means none)
#index ---- index id of this paper
#arnetid ---- pid in arnetminer database
#% ---- the id of references of this paper (there are multiple lines, with each indicating a reference)
#! --- Abstract
 */

object ArnetImporter extends SessionFactorySetter with DBArgsParser with Logging {

  override val COMMAND_NAME = "arnet-import"

  def processFile(dump: File) {
    // infer the file format: either old (V1-V3) or new (V5), the file has to have name format "arnet-v{version number}.txt"
    val newFormat = dump.getName.split("\\.")(0).split("-")(1).substring(1).toInt >= 4

    var title = ""
    var summary: StringBuffer = null
    var venueId = -1
    var year = -1
    var indexId = -1
    var arnetId = -1
    var citationCount = -1
    var authors = Map.empty[String, Int]
    var venues = Map.empty[String, Int]
    var references = Set.empty[(Int, Int)]
    var paperAuthors = Set.empty[String]

    var count = 0

    def storePaper() {
      if (!title.isEmpty) {
        val p = new Paper(title, if (summary == null) "" else summary.toString, year, venueId, citationCount,
          indexId, arnetId)
        paper insert p
        log.debug("Inserting " + p)
        for ((citing, cited) <- references) {
          citation insert new Citation(citing, cited)
        }
        for (name <- paperAuthors) {
          authorship insert new Authorship(authors(name), indexId)
        }
        references = references.empty
        paperAuthors = paperAuthors.empty
        venueId = -1
        title = ""
        summary = null
        year = -1
        indexId = -1
        arnetId = -1
        citationCount = -1

        count += 1
        if (count % 10000 == 0) {
          log.info("Imported " + count + " papers.")
        }
      }
    }
    val lines = Source.fromFile(dump).getLines()
    lines.next() // first line is the total number of records
    for (line <- lines) {
      if (newFormat) {
        if (line.startsWith("#*")) {
          title = line.substring(2)
        } else if (line.startsWith("#@")) {
          paperAuthors = line.substring(2).split(",").map(_.toUpperCase).toSet
          for (name <- paperAuthors) {
            if (!authors.contains(name)) {
              authors = authors + (name-> (authors.size + 1))
              author insert new Author(name, authors(name))
            }
          }
        } else if (line.startsWith("#year")) {
          year = line.substring(5).toInt
        } else if (line.startsWith("#conf")) {
          val v = line.substring(5).toUpperCase
          if (!venues.contains(v)) {
            venues = venues + (v-> (venues.size + 1))
            venue insert new Venue(venues(v), v)
          }
          venueId = venues(v)
        } else if (line.startsWith("#citation")) {
          citationCount = line.substring(9).toInt match {
            case -1 => 0
            case cc => cc
          }
        } else if (line.startsWith("#index")) {
          indexId = line.substring(6).toInt
        } else if (line.startsWith("#arnetid")) {
          arnetId = line.substring(8).toInt
        } else if (line.startsWith("#%")) {
          references = references + (indexId -> line.substring(2).toInt)
        } else if (line.startsWith("#!")) {
          summary = new StringBuffer(line.substring(2))
        } else if (summary != null && !line.trim.isEmpty) {
          // this line is likely a continuation of an abstract
          summary.append(" ").append(line)
        } else if (line.trim.isEmpty) {
          storePaper()
        } else {
          log.error("Unknown line format!")
          log.error(line)
        }
      } else { // old format v1-v3
        if (line.startsWith("#*")) {
          title = line.substring(2)
        } else if (line.startsWith("#@")) {
          paperAuthors = line.substring(2).split(",").map(_.toUpperCase).toSet
          for (name <- paperAuthors) {
            if (!authors.contains(name)) {
              authors = authors + (name-> (authors.size + 1))
              author insert new Author(name, authors(name))
            }
          }
        } else if (line.startsWith("#t")) {
          year = line.substring(2).toInt
        } else if (line.startsWith("#c")) {
          val v = line.substring(2).toUpperCase
          if (!venues.contains(v)) {
            venues = venues + (v-> (venues.size + 1))
            venue insert new Venue(venues(v), v)
          }
          venueId = venues(v)
        } else if (line.startsWith("#index")) {
          indexId = line.substring(6).toInt
        } else if (line.startsWith("#%")) {
          references = references + (indexId -> line.substring(2).toInt)
        } else if (line.startsWith("#!")) {
          summary = new StringBuffer(line.substring(2))
        } else if (summary != null && !line.trim.isEmpty) {
          // this line is likely a continuation of an abstract
          summary.append(" ").append(line)
        } else if (line.trim.isEmpty) {
          storePaper()
        } else {
          log.error("Unknown line format!")
          log.error(line)
        }
      }
    }

    log.info("Stored " + count + " papers altogether.")
    log.info("Stored " + authors.size + " authors altogether.")
    log.info("Stored " + venues.size + " venues altogether.")
  }

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      transaction {
        processFile(new File(getOptValue(INFILE_OPT)))
      }
    }
  }

  override protected def commandLineOptions = DB_OPT :: INFILE_OPT :: HELP_OPT :: Nil
}