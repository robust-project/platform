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
 * Date: 18/07/2011
 * Time: 17:52
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object SWIREdgeListImporter extends EdgeListImporter with DBArgsParser {

  override val COMMAND_NAME = "swir-edgelist-import"

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
    val EdgeList = """swir-cocitation.weighted.(\d{4})-(\d{4}).+""".r
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