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

import ie.deri.uimr.crosscomanalysis.util.Logging
import org.apache.commons.cli.{Option => CliOption}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.db.{ClusterIterator, SliceIterator, DBArgsParser, SessionFactorySetter}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import java.io.{PrintWriter, File}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 20/11/2011
 * Time: 18:22
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Export selected features as a CSV file.
 */

object FeatureCSVExport extends Logging with SessionFactorySetter with DBArgsParser with ClusterArgsParser {

  override val COMMAND_NAME = "export-features-csv"

  val FEATURES_OPT = new CliOption("fe", "features", true, "List of features separated by ',': " + FeatureType.values.mkString(", "))

  override def main(args: Array[String]) {
    mainStub(args) {
      export()
    }
  }

  /**
   * @param features Array of feature ids
   */
  def export(features: Array[Int] = getOptValue(FEATURES_OPT).split(",").map(FeatureType.withName(_)).map(_.id),
             sliceType: Int = getSliceType, clusterFormat: Int = getClusterFormat, db: String = getOptValue(DB_OPT),
             clusterFlag: Option[String] = getClusterFlag, outFile: File = new File(getOptValue(OUTFILE_OPT))) {
    setUpSessionFactory(db)
    val out = new PrintWriter(outFile)
    // print the header
    out.println("cluster_id,cluster_index,slice_id," + features.sorted.map(FeatureType(_).toString).mkString(","))
    transaction {
      for (slice <- new SliceIterator(sliceType, getBeginDate, getEndDate);
           cluster <- new ClusterIterator(slice.id, clusterFormat, clusterFlag)) {
        log.info("Exporting cluster " + cluster.id)
        val values = from(clusterFeatures)(cf =>
          where(cf.clusterId === cluster.id and cf.featureType.in(traversableOfNumericalExpressionList(features.toSet)))
            select (cf.featureValue)
            orderBy (cf.featureType).asc).mkString(",")
        out.println(cluster.id + "," + cluster.index + "," + slice.id + "," + values)
      }
    }
    out.close()
  }

  override protected def commandLineOptions = FEATURES_OPT :: SLICE_TYPE_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT ::
    OUTFILE_OPT :: DB_OPT :: Nil
}