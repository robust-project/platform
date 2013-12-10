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

package ie.deri.uimr.crosscomanalysis.cluster.comparator

import ie.deri.uimr.crosscomanalysis.util.Logging
import org.apache.commons.cli.{Option => CliOption}
import collection.Seq
import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.cluster.{ClusterFormats, ClusterArgsParser}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 28/06/2011
 * Time: 14:54
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object CompareClusterings extends Logging with DBArgsParser with ClusterArgsParser with SessionFactorySetter {
  override val COMMAND_NAME = "compare-clusterings"

  val CLUSTERINGS_OPT = new CliOption("cl", "clusterings", true, "clusterings to compare in format '$cluster1:$flag1,$cluster2:$flag2,...', e.g. 'oslom:flag,louvain:flag'")

  def compare(clusterings: Seq[(Int, Option[String])], db: String, sliceType: Int) {
    setUpSessionFactory(db)
    log.debug("Comparing mutually clusterings " + clusterings)

    transaction {
      for (slice <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns) orderBy (ns.beginDate).asc)) {
        for (i <- 0 until clusterings.size; j <- 0 until clusterings.size if i > j) {
          val (format1, flag1) = clusterings(i)
          val (format2, flag2) = clusterings(j)
          val clustering1 = retrieveClusters(format1, flag1, slice.id)
          val clustering2 = retrieveClusters(format2, flag2, slice.id)
          log.info("Comparing " + clusteringInfo(format1, flag1) + " with " + clusteringInfo(format2, flag2))
          val omega = new OmegaComparator[Long](clustering1, clustering2)
          out("Omega of " + clusteringInfo(format1, flag1) + " and " + clusteringInfo(format2, flag2) + " in " +
            slice.beginYear + "-"  + slice.endYear + " is " + omega.compare)
        }
      }
    }
  }

  private def clusteringInfo(format: Int, flag: Option[String]) =
    ClusterFormats(format).toString + ":" + (if (flag.isDefined) flag.get else "N/A")

  private def out(m: String) {
    println(m)
  }

  private def retrieveClusters(format: Int, flag: Option[String], sliceId: Long) = {
    // todo check for flags - it has to be differentiated whether it is defined or not, if not, flag has to be NULL!!
    (for (c <- from(cluster)(c => where(c.format === format and c.flag === flag and c.sliceId === sliceId) select (c)))
    yield new ClusterFromRDB(c)).toSet
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      val Pair = """(.+):(.*)""".r
      val clusterings = getOptValue(CLUSTERINGS_OPT).split(",").collect({
        case Pair(clusterFormat, flag) => (ClusterFormats.withName(clusterFormat).id, (if (flag.isEmpty) None else Option(flag)))
      }).toSeq
      compare(clusterings, getOptValue(DB_OPT), getSliceType)
    }
  }

  override protected def commandLineOptions =
    SLICE_TYPE_OPT :: HELP_OPT :: CLUSTERINGS_OPT :: DB_OPT :: Nil
}