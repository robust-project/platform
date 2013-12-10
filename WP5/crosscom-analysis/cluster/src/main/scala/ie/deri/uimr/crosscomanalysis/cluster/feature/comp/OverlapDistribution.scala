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

import ie.deri.uimr.crosscomanalysis.db.tables.graph.Cluster
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType.{OVERLAP_MULTI_COUNT, OVERLAP_SET_COUNT}
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.HierarchicalClusterVertexOverlap
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import collection.mutable.{HashMap, HashSet}
import ie.deri.uimr.crosscomanalysis.util.Functions._
import java.io.{File, PrintWriter}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.ClusterIterator

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 15/11/2011
 * Time: 14:57
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes the size of overlaps up to the maximal order and stores the to a file. The absolute overlap is then stored to
 * database.
 */

object OverlapDistribution extends FeatureExtractor(parallel = true) {

  override val COMMAND_NAME = "overlap-dist"

  /**
   * All the computation is actually done in processSlice()
   */
  protected def processCluster(clusterDB: Cluster) {}

  override protected def processSlice(sliceId: Long, clusterFormat: Int, flag: Option[String]) {
    log.info("Processing slice " + sliceId)
    val clusters = inTransaction {
      (for (c <- new ClusterIterator(sliceId, clusterFormat, flag)) yield new ClusterFromRDB(c)).toSet
    }
    val clusterIds = clusters.map(c => (c.index, c.clusterDB.id)).toMap
    val overlapDist = new HashMap[Int, HashMap[Int, Int]] // (cl. index X overlap order) -> count
    val outDir = new File(getOptValue(DIR_OPT))
    outDir.mkdirs()
    val maxSliceId = inTransaction {
      from(networkSlice)(ns => where(ns.sliceType === getSliceType) compute (max(ns.id))).single.measures.get
    }
    val out = new PrintWriter(new File(outDir, offset(maxSliceId, sliceId) + sliceId + "_overlap_dist.csv"))
    val multiCount = new HashMap[Int, Int] // cl. index -> count
    val setCount = new HashMap[Int, Int] // cl. index -> count
    out.println("cluster_id,overlap_order,count,cluster_size,cluster_index,slice")
    if (!clusters.isEmpty) {
      val hierClusterOverlap = new HierarchicalClusterVertexOverlap[Long]
      val overlaps = hierClusterOverlap.clustersOverlaps(clusters)

      // initialize
      for (c <- clusters) {
        multiCount(c.index) = 0
        setCount(c.index) = 0
        overlapDist(c.index) = new HashMap[Int, Int]
        overlapDist(c.index)(1) = 0
      }

      //compute
      for (i <- 0 until overlaps.size; (_, pair) <- overlaps(i); (_, overlap) <- pair if overlap.vertices.size > 0) {
        for (origCluster <- overlap.origClusters) {
          log.debug("Overlap:")
          log.debug(overlap.toString)
          log.debug("Orig. clusters")
          log.debug(origCluster.toString)
          log.debug("==============================================")
          multiCount(origCluster.index) = multiCount(origCluster.index) + overlap.vertices.size * overlap.origClusters.size
          setCount(origCluster.index) = setCount(origCluster.index) + overlap.vertices.size
          if (!overlapDist(origCluster.index).contains(overlap.origClusters.size))
            overlapDist(origCluster.index)(overlap.origClusters.size) = 0
          overlapDist(origCluster.index)(overlap.origClusters.size) = overlap.vertices.size + overlapDist(origCluster.index)(overlap.origClusters.size)
        }
      }
      for ((index, count) <- multiCount)
        storeFeatureValue(clusterIds(index), OVERLAP_MULTI_COUNT.id, count.toDouble)
      for ((index, count) <- setCount)
        storeFeatureValue(clusterIds(index), OVERLAP_SET_COUNT.id, count.toDouble)
      for ((index, pair) <- overlapDist; (order, count) <- pair) {
        out.println(clusterIds(index) + "," + order + "," + count + "," + clusters.find(_.index == index).get.vertices.size +
          "," + index + "," + sliceId)
      }
    }
    out.close()
    log.info("Finished processing of slice " + sliceId)
  }

  override protected def commandLineOptions = DIR_OPT :: super.commandLineOptions
}