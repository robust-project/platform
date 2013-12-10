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
import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}
import org.apache.commons.cli.{Option => CliOption}
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.cluster.model.{ClusterWithContent, ClusterFromRDB}
import org.squeryl.PrimitiveTypeMode._
import collection.Set
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import ie.deri.uimr.crosscomanalysis.cluster.{ClusterFormats, ClusterArgsParser}
import ie.deri.uimr.crosscomanalysis.util.Functions.jaccardSim
import ie.deri.uimr.crosscomanalysis.graphdb.jung.JungDBGraphLoader
import edu.uci.ics.jung.graph.UndirectedSparseGraph
import edu.uci.ics.jung.algorithms.metrics.Metrics
import java.util.{HashMap => JavaHashMap, Map => JavaMap}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/03/2011
 * Time: 18:18
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object MergeSplitWekaExporter extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser {

  override val COMMAND_NAME = "merge-split-weka-export"
  private val ancestorOverlap = new ClusterAncestorOverlap[Long]
  private val descendantOverlap = new ClusterDescendantOverlap[Long]
  // merged - the cluster which sustained
  // persisting - the cluster which was marging (and thus dissappeared)
  private val headerMerge = "@RELATION swir-merge\n\n" +
    "@ATTRIBUTE structsim NUMERIC\n" +
    "@ATTRIBUTE sizeratio NUMERIC\n" +
    "@ATTRIBUTE ccmerging NUMERIC\n" +
    "@ATTRIBUTE ccpersisting NUMERIC\n" +
    "@ATTRIBUTE topsim NUMERIC\n" +
    "@ATTRIBUTE tcmerging NUMERIC\n" +
    "@ATTRIBUTE tcpersisting NUMERIC\n" +
    "@ATTRIBUTE merged {TRUE,FALSE}\n\n" +
    "@DATA\n"
  private val headerSplit = "@RELATION swir-split\n\n" +
    "@ATTRIBUTE relsize NUMERIC\n" +
    "@ATTRIBUTE cc NUMERIC\n" +
    "@ATTRIBUTE tcsplitting NUMERIC\n" +
    //    "@ATTRIBUTE cc-ratio NUMERIC\n" +
    "@ATTRIBUTE split {TRUE,FALSE}\n\n" +
    "@DATA\n"

  def export(sliceType: Int, db: String, clusterFormat: Int, flag: Option[String], outdir: File, threshold: Double) {
    setUpSessionFactory(db)
    val mergeOut = new PrintWriter(outdir + File.separator + SliceTypes(sliceType).toString + "-" +
      ClusterFormats(clusterFormat).toString + "-merge-" + threshold + ".arff")
    mergeOut.print(headerMerge)
    val splitOut = new PrintWriter(outdir + File.separator + SliceTypes(sliceType).toString + "-" +
      ClusterFormats(clusterFormat).toString + "-split-" + threshold + ".arff")
    splitOut.print(headerSplit)

    transaction {
      val keywordVectorLoader = new KeywordVectorLoader(db)
      var formerClusters: Option[Set[ClusterFromRDB with ClusterWithContent[Long]]] = None
      var formerClustCoefMap: Option[JavaMap[Long, java.lang.Double]] = None
      var formerGraphLoader: Option[JungDBGraphLoader[UndirectedSparseGraph[Long, Long]]] = None

      for (sliceId <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns.id) orderBy (ns.beginDate).asc)) {
        log.info("Processing slice #" + sliceId)
        val clusterQuery = if (flag.isDefined) from(cluster)(c => where(c.sliceId === sliceId and c.flag === flag and c.format === clusterFormat) select (c))
        else from(cluster)(c => where(c.sliceId === sliceId and c.format === clusterFormat) select (c))
        val vectors = keywordVectorLoader.loadVectorsForSlice(sliceId)
        val laterClusters =
          (for (cluster <- clusterQuery)
          yield new ClusterFromRDB(cluster) with ClusterWithContent[Long] {
              val featureVectors = vectors
              val featureIndex = keywordVectorLoader.keywordIndex
            }).toSet
        log.debug("Number of clusters: " + laterClusters.size)
        val graphLoader = new JungDBGraphLoader(sliceId, new UndirectedSparseGraph[Long, Long], true)
        val clusteringCoefMap = Metrics.clusteringCoefficients(graphLoader.graph)
        if (formerClusters.isDefined) {
          val ancestors = ancestorOverlap.clustersOverlaps(formerClusters.get, laterClusters)
          val descendants = descendantOverlap.clustersOverlaps(formerClusters.get, laterClusters)
          val formerOverlapsRelToMerging = descendantOverlap.clustersOverlaps(formerClusters.get, formerClusters.get)
          for (formerCluster <- formerClusters.get) {
            var split = false
            for (laterCluster <- laterClusters if formerCluster.index != laterCluster.index) {
              // plain community merge
              // find a previous instance of a 'laterCluster' such that we can compare the two clusters in the previous step
              // 'merged' -- the one that survived
              val mergedClusterOpt = formerClusters.get.find(_.index == laterCluster.index)
              if (mergedClusterOpt.isDefined) {
                val merged = descendants(formerCluster)(laterCluster) >= threshold// && laterClusters.find(_.index == formerCluster.index).isEmpty
//                if (descendants(formerCluster)(laterCluster) >= threshold) {
                  val mergedCluster = mergedClusterOpt.get
//                  val merged = laterClusters.find(_.index == formerCluster.index).isEmpty
                  val topicSim = mergedCluster.cosineSimilarity(formerCluster)
//                  mergeOut.print(jaccardSim(mergedCluster.vertices, formerCluster.vertices) + ",")
                  mergeOut.print(formerOverlapsRelToMerging(formerCluster)(mergedCluster) + ",")
                  mergeOut.print((mergedCluster.vertices.size.toDouble / formerCluster.vertices.size) + ",")
                  mergeOut.print(topicSim + ",")
                  mergeOut.print((formerCluster.vertices.map(formerClustCoefMap.get.get(_).doubleValue).sum / formerCluster.vertices.size) + ",")
                  mergeOut.print((mergedCluster.vertices.map(formerClustCoefMap.get.get(_).doubleValue).sum / mergedCluster.vertices.size) + ",")
                  mergeOut.print(formerCluster.clusterFeatureCohesiveness + ",")
                  mergeOut.print(mergedCluster.clusterFeatureCohesiveness + ",")
                  mergeOut.println(merged.toString.toUpperCase)
//                }
              }
              // community split
              if (ancestors(formerCluster)(laterCluster) >= threshold)
                split = true
            }
            val clusteringCoef = formerCluster.vertices.map(formerClustCoefMap.get.get(_).doubleValue).sum / formerCluster.vertices.size
            splitOut.print(formerCluster.vertices.size.toDouble / formerGraphLoader.get.graph.getVertexCount + ",")
            splitOut.print(clusteringCoef + ",")
            splitOut.print(formerCluster.clusterFeatureCohesiveness + ",")
            splitOut.println(split.toString.toUpperCase)
          }
        }
        formerClusters = Some(laterClusters)
        formerClustCoefMap = Some(clusteringCoefMap)
        formerGraphLoader = Some(graphLoader)
      }
    }
    mergeOut.close()
    splitOut.close()
  }

  override def main(args: Array[String]) {
    val line = parseArgs(args)
    if (!printHelpIfAsked) {
      val t = if (line.hasOption(THRESHOLD_OPT.getOpt)) getOptValue(THRESHOLD_OPT).toDouble else 0.5
      export(getSliceType, getOptValue(DB_OPT), getClusterFormat, getClusterFlag, new File(getOptValue(DIR_OPT)), t)
    }
  }

  override protected def commandLineOptions = THRESHOLD_OPT :: SLICE_TYPE_OPT :: DB_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT :: DIR_OPT :: HELP_OPT :: Nil
}