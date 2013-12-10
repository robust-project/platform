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

package ie.deri.uimr.crosscomanalysis.cluster.feature.comp

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import java.util.Date
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.{ClusterFeature, Cluster}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/09/2011
 * Time: 18:32
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Generic class for feature extraction from clusters. It goes over the slices of the given type and over all clusters
 * in each slice and calls a method processCluster(). The feature extraction is supposed to be implemented in that method.
 * If parallel=true, then the slices will be processed in parallel.
 */
abstract class FeatureExtractor(val parallel: Boolean = false) extends SessionFactorySetter with Logging with ClusterArgsParser with DBArgsParser {

  /**
   * Goes over the selected period of data (or the whole if start and end not defined) and process them in the
   * chronological order (ascending).
   */
  def compute(db: String = getOptValue(DB_OPT), clusterFormat: Int = getClusterFormat, sliceType: Int = getSliceType,
              flag: Option[String] = getClusterFlag, start: Option[Date] = getBeginDate, end: Option[Date] = getEndDate) {
    assert((start.isDefined && end.isDefined) || (start.isEmpty && end.isEmpty))

    setUpSessionFactory(db)
    transaction {
      val sliceIds =
        if (start.isDefined && end.isDefined) {
          log.debug("Running for period " + start + " --- " + end)
          from(networkSlice)(ns =>
            where(ns.sliceType === sliceType and ns.beginDate >= start.get and ns.endDate <= end.get)
              select (ns.id)
              orderBy (ns.beginDate).asc).toList
        } else {
          log.debug("Running for all slices")
          from(networkSlice)(ns =>
            where(ns.sliceType === sliceType)
              select (ns.id)
              orderBy (ns.beginDate).asc).toList
        }
      if (parallel)
        sliceIds.par.foreach(processSlice(_, clusterFormat, flag))
      else
        sliceIds.foreach(processSlice(_, clusterFormat, flag))
    }
  }

  /**
   * It is called for each slice once (in a chronological order)
   */
  protected def processSlice(sliceId: Long, clusterFormat: Int, flag: Option[String]) {
    log.info("Processing slice " + sliceId)
    computeSliceStats(sliceId)
    val clusterQuery =
      if (flag.isDefined) from(cluster)(c => where(c.sliceId === sliceId and c.flag === flag and c.format === clusterFormat) select (c))
      else from(cluster)(c => where(c.sliceId === sliceId and c.format === clusterFormat) select (c))
    clusterQuery.foreach(processCluster(_))
    cleanUpAfterSlice(sliceId)
  }

  /**
   * Implements the actual computation - it is called for each cluster in each slice once (in a chronological order).
   */
  protected def processCluster(clusterDB: Cluster)

  /**
   * By-default empty hook for computing per-slice statistics.
   */
  protected def computeSliceStats(sliceId: Long) {}

  /**
   * By-default empty hook for cleaning-up after all clusters in a slice have been processed
   */
  protected def cleanUpAfterSlice(sliceId: Long) {}

  override protected def commandLineOptions = DB_OPT :: SLICE_TYPE_OPT :: CLUSTER_FLAG_OPT :: BEGIN_DATE_OPT ::
    END_DATE_OPT :: CLUSTER_FORMAT_OPT :: Nil

  protected def storeFeatureValue(clusterId: Long, featureType: Int, value: Double) {
    inTransaction {
      clusterFeatures insert new ClusterFeature(clusterId, featureType, value)
    }
  }

  override def main(args: Array[String]) {
    mainStub(args) {
      compute()
    }
  }
}