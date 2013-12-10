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

package ie.deri.uimr.crosscomanalysis.cluster.feature

import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import java.io.{PrintWriter, File}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 30/11/2011
 * Time: 11:57
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object NumberOfCommunitiesPerSlice extends DBArgsParser with ClusterArgsParser with Logging with SessionFactorySetter{

  override val COMMAND_NAME = "count-comm-per-slice"

  def compute(sliceType: Int = getSliceType, clusterFormat: Int = getClusterFormat, db: String = getOptValue(DB_OPT),
              flag: Option[String] = getClusterFlag, outFile: File = new File(getOptValue(OUTFILE_OPT))) {
    setUpSessionFactory(db)
    val out = new PrintWriter(outFile)

    out.println("slice_id,comm_count")
    transaction {
      val q =
        if (flag.isDefined) {
          from(networkSlice, cluster) ((ns, c) =>
            where(ns.sliceType === sliceType and ns.id === c.sliceId and c.flag === flag and c.format === clusterFormat)
              groupBy(ns.id)
              compute(countDistinct(c.id))
              orderBy(ns.beginDate).asc)
        } else {
          from(networkSlice, cluster) ((ns, c) =>
            where(ns.sliceType === sliceType and ns.id === c.sliceId and c.format === clusterFormat)
              groupBy(ns.id)
              compute(countDistinct(c.id))
              orderBy(ns.beginDate).asc)
        }
      log.info("Starting the computation")
      log.debug(q.statement)
      for (mes <- q) {
        out.println(mes.key + "," + mes.measures)
      }
      log.info("Finished")
    }
    out.close()
  }


  override def main(args: Array[String]) {
    mainStub(args) {
      compute()
    }
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT ::
    SLICE_TYPE_OPT :: OUTFILE_OPT :: Nil
}