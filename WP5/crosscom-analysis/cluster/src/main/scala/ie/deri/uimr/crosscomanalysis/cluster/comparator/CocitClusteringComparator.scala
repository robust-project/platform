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

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.graph._
import collection.mutable.HashMap
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.apache.commons.math.linear.{OpenMapRealVector, RealVector}
import ie.deri.uimr.crosscomanalysis.util.Logging
import java.util.Calendar
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.{KeywordVectorLoader, FeatureEntropy}
import ie.deri.uimr.crosscomanalysis.cluster.model.{ClusterWithContent, ClusterFromRDB, SWIRCluster}
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import collection.Map

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07-Dec-2010
 * Time: 14:58:09
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

class CocitClusteringComparator(val db:String, val clusterFormat: Int, sliceType: Int,
                                val flag: Option[String]) extends Logging with SessionFactorySetter {
  val BEGIN_YEAR = 2000
  val END_YEAR = 2009

  val keywordLoader = new KeywordVectorLoader(db)
  var featureVectorsFormer: Option[Map[Long,RealVector]] = None

  private val cal = Calendar.getInstance
  cal.clear
  cal.set(BEGIN_YEAR, 0, 1)

  def compare {
    setUpSessionFactory(db)

    val overallAvgCCRatio = new SummaryStatistics
    val overallAvgTC = new SummaryStatistics

    for (beginYear <- BEGIN_YEAR to END_YEAR) {
      val yearlyAvgCCRatio = new SummaryStatistics
      val yearlyAvgTC = new SummaryStatistics
      cal.clear
      cal.set(beginYear, 0 ,1)

      transaction{
        val slice = (from(networkSlice)(ns =>
          (where(ns.beginDate === cal.getTime and ns.sliceType === sliceType) select (ns)))).head
        val featureVectors = keywordLoader.loadVectorsForSlice(slice.id)
        val sliceCentroid = computeSliceCentroid(featureVectors.valuesIterator)
        val clusters = retrieveClusters(slice, featureVectors).toList
//        val featureEntropy:Option[FeatureEntropy] = if (featureVectorsFormer.isDefined)
//          Some(new FeatureEntropy(keywordLoader.keywordIndex, featureVectorsFormer.get, featureVectors)) else None

        for (c <- clusters) {
          val keywords = c.highestRankedKeywords(20)
//          if (featureEntropy.isDefined) c.featureEntropy = Some(featureEntropy.get.clusterEntropy(c))

          log.debug(c.toString)
          log.debug(c + " keywords: " + (if (keywords.isDefined) keywords.get.mkString(", ")))

          val ccr = c.CCRatio(sliceCentroid)
          val ctc = c. clusterFeatureCohesiveness
          overallAvgCCRatio.addValue(ccr)
          overallAvgTC.addValue(ctc)
          yearlyAvgCCRatio.addValue(ccr)
          yearlyAvgTC.addValue(ctc)
        }
//        if (featureEntropy.isDefined) {
//          println(clusters.sortWith(_.featureEntropy.get > _.featureEntropy.get))
//        }
        featureVectorsFormer = Some(featureVectors)
      }
      println("Average C/C for " + beginYear + ": " + yearlyAvgCCRatio.getMean)
      println("Average TC for " + beginYear + ": " + yearlyAvgTC.getMean)
    }
    println("Overall average C/C: " + overallAvgCCRatio.getMean)
    println("Overall average TC: " + overallAvgTC.getMean)
  }

  private def retrieveClusters(slice: NetworkSlice, fetVectors: Map[Long,RealVector]) = {
    if (flag.isDefined) {
      for (c <- (from(cluster)(c =>
        (where(c.sliceId === slice.id and c.flag.get === flag.get and c.format === clusterFormat) select (c)))))
      yield new ClusterFromRDB(c) with ClusterWithContent[Long] {
        val featureVectors = fetVectors
        val featureIndex = keywordLoader.keywordIndex
      }
    } else {
      for (c <- (from(cluster)(c =>
        (where(c.sliceId === slice.id and c.format === clusterFormat) select (c)))))
      yield new ClusterFromRDB(c) with ClusterWithContent[Long] {
        val featureVectors = fetVectors
        val featureIndex = keywordLoader.keywordIndex
      }
    }
  }

  private def computeSliceCentroid(featureVectors: TraversableOnce[RealVector]): RealVector = {
    var sc: RealVector = new OpenMapRealVector(keywordLoader.keywordIndex.size)
    var authorCount = 0
    for (fv <- featureVectors) {
      sc = sc.add(fv)
      authorCount += 1
    }
    sc.mapDivideToSelf(authorCount)

    sc
  }
}

object CocitClusteringComparator extends DBArgsParser with ClusterArgsParser {

  override val COMMAND_NAME = "feature-comparator"

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      val ccc = new CocitClusteringComparator(getOptValue(DB_OPT), getClusterFormat, getSliceType, getClusterFlag)
      ccc.compare
    }
  }

  override protected def commandLineOptions = DB_OPT :: CLUSTER_FORMAT_OPT :: SLICE_TYPE_OPT :: HELP_OPT :: CLUSTER_FLAG_OPT :: Nil
}