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

package ie.deri.uimr.crosscomanalysis.cluster.importers

import java.io.File
import java.util.Calendar
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/03/2011
 * Time: 14:38
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object OslomBatchImporter extends OslomImporter with BatchImporter with SessionFactorySetter with DBArgsParser with ClusterArgsParser {

  override val COMMAND_NAME = "oslom-batch-import"

  private var flag:Option[String] = None

  override def main(args: Array[String]) {
    val line = parseArgs(args)

    if (!printHelpIfAsked) {
      setUpSessionFactory(getOptValue(DB_OPT))
      this.matchingThreshold = getOpt(MATCHING_THRESHOLD_OPT, this.matchingThreshold)
      if (line.hasOption(CLUSTER_FLAG_OPT.getOpt)) {
        flag = getClusterFlag
        batchImport(new File(getOptValue(DIR_OPT)), "tp", _ => getClusterFlag)
      }
      else
        batchImport(new File(getOptValue(DIR_OPT)), "tp")
    }
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: DIR_OPT :: SLICE_TYPE_OPT :: CLUSTER_FLAG_OPT ::
    MATCHING_THRESHOLD_OPT :: Nil


  /**
   * @return (beginDate, endDate, sliceType, flag)
   */

  protected def sliceInfo(f: File) = {
    val Dates = """\d+_(\d{1,2})_(\d{1,2})_(\d{4})-(\d{1,2})_(\d{1,2})_(\d{4})\..+""".r
    val Dates(bDay, bMonth, bYear, eDay, eMonth, eYear) = f.getParentFile.getName
    val cal = Calendar.getInstance
    cal.clear
    cal.set(bYear.toInt, bMonth.toInt - 1, bDay.toInt)
    val beginDate = cal.getTime
    cal.clear
    cal.set(eYear.toInt, eMonth.toInt - 1, eDay.toInt)
    val endDate = cal.getTime
    (beginDate, endDate, getSliceType, flag)
  }

  override protected def mapping(sourceFile: File) = {
    case i: Long => i
  }


}