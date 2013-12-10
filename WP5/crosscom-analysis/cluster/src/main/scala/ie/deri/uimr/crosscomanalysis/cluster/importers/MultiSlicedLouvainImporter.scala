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

import ie.deri.uimr.crosscomanalysis.util.Logging
import java.io.File
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.graph._
import io.Source
import collection.mutable.{HashSet, HashMap}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterFormats
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 09/02/2011
 * Time: 12:09
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait MultiSlicedLouvainImporter extends Logging with ClusteringImporter {

  def importFile(input: File, flag: Option[String]) {
    val (origId, sliceIndex, sliceId) = readMapping(new File(input.getAbsolutePath.substring(0, input.getAbsolutePath.length - 7) + "nodemap"))
    val sliceIndices = sliceIndex.values.toSet
    val modules = new HashMap[Int, HashSet[Long]] // module index -> new node ids
    for (line <- Source.fromFile(input).getLines) {
      val Array(newId, module) = line.split("\\s+")
      if (!modules.contains(module.toInt))
        modules(module.toInt) = new HashSet[Long]
      modules(module.toInt) += newId.toLong
    }

    val clusters = new HashSet[Cluster]
    transaction {
      for ((moduleIndex, moduleStructure) <- modules) {
        // partition modules to clusters (per window/slice)
        val clustersPerSlice = moduleStructure.groupBy(sliceIndex(_))
        for ((sliceInd, nodes) <- clustersPerSlice) {
          val retrievedCluster = clusters.find(c => c.origIndex == moduleIndex && c.sliceId == sliceId(sliceInd))
          val c = if (retrievedCluster.isDefined)
            retrievedCluster.get
          else {
            // create a new cluster db object which will represent this node set / module index in this slice
            val slice = networkSlice.lookup(sliceId(sliceInd)).get
            val newCluster = new Cluster(ClusterFormats.LOUVAIN_MS.id, moduleIndex, moduleIndex, slice.beginDate, slice.endDate, slice.id, flag, None, None, None)
            cluster insert newCluster
            clusters += newCluster
            newCluster
          }
          // add nodes to the cluster
          clusterStructure insert (for (node <- nodes) yield new ClusterStructure(c.id, origId(node.toLong), None, None))
        }
      }
    }
  }

  private def readMapping(mappingFile: File) = {
    val origId = new HashMap[Long, Long] // newId -> origId
    val sliceIndex = new HashMap[Long, Long] // newId -> sliceIndex
    val sliceId = new HashMap[Long, Long] // sliceIndex -> slice db id
    for (line <- Source.fromFile(mappingFile).getLines) {
      val Array(id, _, _, sliceInd, sliceDbId, newId) = line.split("\\s+")
      origId(newId.toLong) = id.toLong
      sliceIndex(newId.toLong) = sliceInd.toLong
      sliceId(sliceInd.toLong) = sliceDbId.toLong
    }

    (origId, sliceIndex, sliceId)
  }
}

object MultiSlicedLouvainImporter extends MultiSlicedLouvainImporter {
  def main(args: Array[String]) {
    importFile(new File(args(0)), Some(args(1)))
  }
}