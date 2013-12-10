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

import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import java.io.{FilenameFilter, File}
import java.util.{Calendar, Date}
import io.Source
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.util.Logging
import collection.mutable.HashSet
import ie.deri.uimr.crosscomanalysis.db.tables.graph.{NetworkSliceVertex, NetworkSliceStructure, NetworkSlice}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 18/07/2011
 * Time: 16:38
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

abstract class EdgeListImporter extends SessionFactorySetter with Logging {

  protected def VALUE_SEPARATOR = " "

  def importFile(file: File, sliceType: Int, db: String) {
    setUpSessionFactory(db)

    val (beginDate, endDate) = dates(file)
    transaction {
      val ns = new NetworkSlice(beginDate, endDate, sliceType)
      val vertices = new HashSet[Long]
      networkSlice insert ns
      log.info("Storing " + ns)
      for (line <- Source.fromFile(file).getLines()) {
        val Array(source, sink, weight) = line.split(VALUE_SEPARATOR)
        vertices ++= Set(source.toLong, sink.toLong)
        networkSliceStructure insert new NetworkSliceStructure(ns.id, source.toLong, sink.toLong, weight.toDouble, None)
      }
      networkSliceVertex insert vertices.map(new NetworkSliceVertex(ns.id, _))
    }
  }

  /**
   * @return (beginDate, endDate) of the slice
   */
  def dates(f: File): (Date, Date)
}

object EdgeListImporterApp extends EdgeListImporter with DBArgsParser {
  override val COMMAND_NAME = "edgelist-import"

  override def main(args: Array[String]) {
    mainStub(args) {
      val dir = new File(getOptValue(DIR_OPT))
      val edgeLists = dir.listFiles(new FilenameFilter {
        def accept(f: File, n: String) = n.endsWith("edges")
      })
      edgeLists.foreach(f => importFile(f, getSliceType, getOptValue(DB_OPT)))
    }
  }

  def dates(f: File) = {
    val EdgeList = """(\d{4})-(\d{4}).+""".r
    val EdgeList(beginYear, endYear) = f.getName
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.MONTH, 0)
    cal.set(Calendar.YEAR, beginYear.toInt)
    val beginDate = cal.getTime
    cal.set(Calendar.YEAR, endYear.toInt)
    cal.set(Calendar.MONTH, 11)
    cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH))
    val endDate = cal.getTime

    (beginDate, endDate)
  }

  override protected def commandLineOptions = DIR_OPT :: SLICE_TYPE_OPT :: DB_OPT :: HELP_OPT :: Nil
}