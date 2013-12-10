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

import ie.deri.uimr.crosscomanalysis.db.DBArgsParser
import java.io.{FilenameFilter, File}
import java.util.Calendar

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 21/11/2011
 * Time: 10:44
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object CommGraphEdgeListImporter extends EdgeListImporter with DBArgsParser {

  override val COMMAND_NAME = "commgraph-edgelist-import"

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
    val EdgeList = """(\d+)_(\d{1,2})_(\d{1,2})_(\d{4})-(\d{1,2})_(\d{1,2})_(\d{4}).+""".r
    val EdgeList(_, beginDay, beginMonth, beginYear, endDay, endMonth, endYear) = f.getName
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, beginDay.toInt)
    cal.set(Calendar.MONTH, beginMonth.toInt - 1)
    cal.set(Calendar.YEAR, beginYear.toInt)
    cal.set(Calendar.HOUR, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val beginDate = cal.getTime
    cal.set(Calendar.DAY_OF_MONTH, endDay.toInt)
    cal.set(Calendar.MONTH, endMonth.toInt - 1)
    cal.set(Calendar.YEAR, endYear.toInt)
    cal.set(Calendar.HOUR, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND))
    val endDate = cal.getTime

    (beginDate, endDate)
  }

  override protected def commandLineOptions = DIR_OPT :: SLICE_TYPE_OPT :: DB_OPT :: HELP_OPT :: Nil
}