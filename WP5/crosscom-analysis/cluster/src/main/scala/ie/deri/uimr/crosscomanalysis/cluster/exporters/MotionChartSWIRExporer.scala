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

package ie.deri.uimr.crosscomanalysis.cluster.exporters

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.KeywordVectorLoader
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.model.SWIRCluster
import org.apache.commons.math.linear.{RealVector, OpenMapRealVector}
import collection.{Seq, Set}
import ie.deri.uimr.crosscomanalysis.util.Functions.cosineDistance
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import java.util.{Calendar, Date}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 23/03/2011
 * Time: 15:19
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object MotionChartSWIRExporer extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser {

  def export(sliceType: Int, clusterFormat: Int, db: String) {
    setUpSessionFactory(db)
    val keywordLoader = new KeywordVectorLoader(db)
    def computeCentroid(features: Set[RealVector]) = {
      var c: RealVector = new OpenMapRealVector(keywordLoader.keywordIndex.size)
      for (fv <- features) {
        c = c.add(fv)
      }
      c.mapDivideToSelf(features.size)

      c
    }

    transaction {
      for (sliceId <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns.id) orderBy (ns.beginDate).asc)) {
        log.debug("Processing slice " + sliceId)
        val vectors = keywordLoader.loadVectorsForSlice(sliceId)
        val clusters = (for (cluster <- from(cluster)(c => where(c.sliceId === sliceId and c.format === clusterFormat) select (c)))
        yield new SWIRCluster(cluster, keywordLoader.keywordIndex, vectors)).toSet
        val swClusters = clusters.filter(_.isSW)
        val irClusters = clusters.filter(_.isIR)
        // compute the IR and SW centroids -- assuming clusters are not overlapping
        val swCentroid = computeCentroid(swClusters.map(_.clusterFeatures).flatten)
        val irCentroid = computeCentroid(irClusters.map(_.clusterFeatures).flatten)
        // printout all SW/IR clusters
        val cal = Calendar.getInstance
        def getDate(c: SWIRCluster) = {
          cal.setTime(c.clusterDB.beginDate)
          "new Date(" + cal.get(Calendar.YEAR) + ",0,1)"
        }
        def out(c: SWIRCluster) {
//          val keywords = c.highestRankedKeywords(5) (if (keywords.isDefined) ":{" + keywords.get.mkString(",") + "}" else "")
          println("['" + c.index +  "'," + getDate(c) + "," +
            cosineDistance(c.centroid, swCentroid) + "," + cosineDistance(c.centroid, irCentroid) + "," + c.vertices.size + "],")
        }
        swClusters.foreach(out)
        irClusters.foreach(out)
      }
    }
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      export(getSliceType, getClusterFormat, getOptValue(DB_OPT))
    }
  }

  override protected def commandLineOptions = DB_OPT :: SLICE_TYPE_OPT :: CLUSTER_FORMAT_OPT :: HELP_OPT :: Nil
}