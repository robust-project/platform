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

package ie.deri.uimr.crosscomanalysis.cluster.crosscom

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.util.Functions.cosineDistance
import collection.{Map, Set}
import org.apache.commons.math.stat.inference.TTestImpl
import org.apache.commons.math.distribution.TDistributionImpl
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import ie.deri.uimr.crosscomanalysis.db.tables.graph.{SliceTypes, NetworkSlice}
import org.apache.commons.math.linear.{OpenMapRealVector, RealVector}
import scala.collection.JavaConversions.asScalaIterator

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/06/2011
 * Time: 19:15
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object DiscourseLeadersMinerCosine extends DiscourseLeaderMiner with SessionFactorySetter with Logging with ClusterArgsParser {

  override val COMMAND_NAME = "discourse-leaders-miner-cos"

  protected def processSlice(slice: NetworkSlice) {
    log.info("Processing " + slice)
    val clusterQuery =
      if (flag.isDefined) from(cluster)(c => where(c.sliceId === slice.id and c.flag === flag and c.format === clusterFormat) select (c))
      else from(cluster)(c => where(c.sliceId === slice.id and c.format === clusterFormat) select (c))
    val clusters = (for (cluster <- clusterQuery) yield new ClusterFromRDB(cluster)).toSet
    log.debug("Number of clusters: " + clusters.size)
    val endOfExploredPeriod =
      if ((slice.beginYear + exploredFuture) <= maxYear)
        slice.beginYear + exploredFuture
      else
        maxYear
    // for each cluster in the current slice, check its size and then compare the keyword dist. of an author in time
    // t with the keywords of his fellows in time t+k - if these two distributions are more similar then the keywords
    // of fellows between times t and t+k, conclude the author is a leader
    for (cluster <- clusters if cluster.vertices.size >= minClusterSize) {
      log.info(cluster.toString)
      val keywordsPerYear = retrieveKeywordsPerYear
      for (vertex <- cluster.vertices.par if keywordsPerYear(slice.beginYear).contains(vertex)) {
        val authorsVector = keywordsPerYear(slice.beginYear)(vertex)
        val fellows = cluster.vertices - vertex -- (
          if (excludeCoauthors) coauthors(vertex, slice.beginYear to endOfExploredPeriod)
          else cluster.vertices.empty)
        log.debug("Excluding " + (cluster.vertices.size - fellows.size - 1) + " of coauthors of vertex " + vertex)
        val fellowsCurrentVectors = for (f <- fellows if keywordsPerYear(slice.beginYear).contains(f)) yield keywordsPerYear(slice.beginYear)(f)
        log.debug("Loaded " + fellowsCurrentVectors.size + " fellows' current vectors")
        // check only distribution that are non-zero (we have some keywords for the authors)
        if (!fellowsCurrentVectors.isEmpty && fellowsCurrentVectors.size >= 2) {
          for (year <- (slice.beginYear + 1) to endOfExploredPeriod) {
            // compute average distances of the fellows with themselves in the future and see if it differs from the
            // distance of the author in hand - ideally the author will be nearer signifying his impact
            val fellowsFutureCentroid = centroid(fellows, keywordsPerYear(year).filterKeys(fellows.contains(_)))
            val fellowsCurrentCentroid = centroid(fellows, keywordsPerYear(slice.beginYear).filterKeys(fellows.contains(_)))
            val defaultDistance = cosineDistance(fellowsCurrentCentroid, fellowsFutureCentroid)
            // if the distance between fellows' centroids is zero, that means they are on average orthogonal - in such cases
            // any other individual would be a leader - there's no point to consider such cases as they rather mean
            // noise/incomplete data
            //            if (defaultDistance > 0) {
            val stats = new SummaryStatistics
//            val sampleValues = new MutableList[Double]
            for (fcv <- fellowsCurrentVectors) {
              val d = cosineDistance(fcv, fellowsFutureCentroid)
              stats.addValue(d)
//              sampleValues += d
            }
            val distanceWithFuture = cosineDistance(authorsVector, fellowsFutureCentroid)
            log.debug("Testing cluster " + cluster + " with topics for " + stats.getN + " authors")
            if (stats.getN >= minClusterSize) {
              val ttest = new TTestImpl
              val tdistribution = new TDistributionImpl(stats.getN)
//              val ztest = new Ztest(sampleValues.toArray, distanceWithFuture, H1.LESS_THAN)
              val t = -ttest.t(distanceWithFuture, stats)
              val p = 1 - tdistribution.cumulativeProbability(t)
//              val p = ztest.getSP
              outTopicAllStats(vertex, authorName(vertex), year, distanceWithFuture, p, stats.getMean, stats.getVariance,
                cluster.index, slice.beginYear, slice.endYear, cluster.vertices.size)
              // prune non-significant or trivial cases, i.e. when mean=0 and thus all fellows are completely dissimilar
              if (p <= alpha) {       // logs can be negative
                // ok, we detected a significant leader, let's spit it out to the output
                outTopicStats(vertex, authorName(vertex), year, distanceWithFuture, p, stats.getMean, stats.getVariance,
                  cluster.index, slice.beginYear, slice.endYear, cluster.vertices.size)
                // now, let's look how does s/he compare to hier fellows in terms of citations
                // as we compute citations over all the future period, we have to redefine 'fellows'
                val authorsCitCount = citationsCount(vertex, fellows, 2000 to slice.beginYear, (slice.beginYear + 1) to year)
                // compute average citations count of original fellows (obtained for the topic shift detection)
                val fellowsCitationsCount = fellows.map(f => {
                  // fellows of the fellows ... now it starts to be _a bit_ complicated:D
                  val secondOrderFellows = cluster.vertices - f -- (
                    if (excludeCoauthors) coauthors(f, slice.beginYear to endOfExploredPeriod)
                    else fellows.empty)
                  citationsCount(f, secondOrderFellows, 2000 to slice.beginYear, (slice.beginYear + 1) to year)
                })
                val fellowsCitationsCountAvg =
                  if (fellowsCitationsCount.size > 0) // let's be optimistic:)
                    fellowsCitationsCount.sum / fellowsCitationsCount.size.toDouble
                  else 0d
                // uff! let's spit it out before we forget it:D
                outCitationStats(vertex, authorsCitCount, fellowsCitationsCountAvg, cluster.index, slice.beginYear, slice.endYear)
                // print out individual statistics (not the average of fellows)
                if (fellowsCitationsCount.size > 0) {
                  // print the first line with the stats of the author and the rest with author's stats as "NA"
                  val fellowsCitationsCountList = fellowsCitationsCount.toList
                  outCitationIndStats(vertex, authorsCitCount, fellowsCitationsCountList.head, cluster.index,
                    slice.beginYear, slice.endYear)
                  fellowsCitationsCountList.slice(1, fellowsCitationsCountList.size).foreach(c =>
                    outCitationIndStats(vertex, "NA", c, cluster.index, slice.beginYear, slice.endYear))
                }
              }
            }
            //            }
          }
        }
      }
    }
  }

  private def isVectorNonZero(v: RealVector) = v.sparseIterator().find(_.getValue > 0).isDefined

  /**
   * @return Centroid of actors' keyword vectors for a given keywords map (e.g. for a particular year).
   */
  private def centroid(actors: Set[Long], keywords: Map[Long, RealVector]) = {
    var c: RealVector = new OpenMapRealVector(keywordVectorLoader.get.keywordIndex.size)
    for (fv <- keywords.filterKeys(actors.contains(_)).valuesIterator) {
      c = c.add(fv)
    }
    c.mapDivideToSelf(actors.size)
  }

  /**
   * This methods loads keywords ranked for slice type s1o0_cocit - it should not matter whether cocit or cocitation,
   * important is whether the keywords are ranked per year, which is true in this case.
   * @return Map beginYear->(authorId->keyword vector)
   */
  private def retrieveKeywordsPerYear = inTransaction {
    for (slice <- from(networkSlice)(ns => where(ns.sliceType === SliceTypes.withName("s1yo0_cocit").id)
      select (ns) orderBy (ns.beginDate).asc))
    yield (slice.beginYear, keywordVectorLoader.get.loadVectorsForSlice(slice.id))
  }.toMap

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      init()
      mine()
    }
  }
}