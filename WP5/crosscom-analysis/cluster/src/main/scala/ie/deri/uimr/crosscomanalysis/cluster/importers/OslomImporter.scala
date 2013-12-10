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
import io.Source
import ie.deri.uimr.crosscomanalysis.util.Functions.jaccardSim
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.cluster.comparator.ClusterMatcher
import collection.immutable.Set
import ie.deri.uimr.crosscomanalysis.cluster.ClusterFormats._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.{ClusterStructure, Cluster => ClusterDB}
import collection.mutable.HashMap
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster
import java.util.Date
import collection.Map

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 15/02/2011
 * Time: 12:20
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait OslomImporter extends Logging with ClusterMatcher[Cluster[Long]] with ClusteringImporter {
  private var clusterIndex = -1
  // (origIndex, set of vertex ids)
  private var formerClusters: Option[Set[Cluster[Long]]] = None


  protected def clusterSim(c1: Cluster[Long], c2: Cluster[Long]) = jaccardSim(c1.vertices, c2.vertices)

  def importFile(input: File, flag: Option[String]) {
    val (beginDate, endDate, sliceType, flag) = sliceInfo(input)
    val slice = transaction {
      from(networkSlice)(ns => where(ns.sliceType === sliceType and
        ns.beginDate === beginDate and ns.endDate === endDate) select (ns)).single
    }
    val newToOldID = mapping(input)
    var oi = -1
    def origIndex = {
      oi += 1
      oi
    }
    val laterClusters = (for (line <- Source.fromFile(input).getLines if !line.startsWith("#");
                              vertices = line.split("\\s").map(newId => newToOldID(newId.toLong)).toSet if vertices.size > 1)
    yield Cluster(origIndex, vertices)).toSet
    val matching = if (formerClusters.isDefined) Some(matchClusters(formerClusters.get, laterClusters)) else None
    transaction {
      for (c <- laterClusters if c.vertices.size > 1) {
        val matched = if (matching.isDefined) matching.get.get(c) else None
        c.index = if (matched.isDefined) matched.get.index else nextClusterIndex
        val dbCluster = new ClusterDB(OSLOM.id, c.index, c.origIndex, slice.beginDate, slice.endDate, slice.id, flag, None, None, None)
        cluster insert dbCluster
        clusterStructure insert (for (vertex <- c.vertices) yield new ClusterStructure(dbCluster.id, vertex, None, None))
      }
    }
    log.debug("Imported file " + input + ", clusters # " + laterClusters.filter(_.vertices.size > 1).size)
    formerClusters = Some(laterClusters)
  }

  private def nextClusterIndex = {
    clusterIndex += 1
    clusterIndex
  }


  protected def mapping(sourceFile: File): PartialFunction[Long, Long] = {
    val f = new File(sourceFile.getAbsolutePath.substring(0, sourceFile.getAbsolutePath.length - 17) + "mapping")
    val map = new HashMap[Long, Long]
    for (line <- Source.fromFile(f).getLines; val Array(newId, oldId) = line.split("\\s")) {
      map(newId.toLong) = oldId.toLong
    }
    map
  }

  /**
   * @return (beginDate, endDate, sliceType, flag)
   */
  protected def sliceInfo(f: File): (Date, Date, Int, Option[String])
}