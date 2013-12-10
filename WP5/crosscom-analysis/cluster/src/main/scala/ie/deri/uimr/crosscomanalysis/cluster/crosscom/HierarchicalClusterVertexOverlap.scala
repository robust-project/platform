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
import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster
import collection.{Set, Map}
import collection.mutable.{HashMap, HashSet}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 15/11/2011
 * Time: 15:16
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class HierarchicalClusterVertexOverlap[V] extends Logging {

  private var index = 0

  /**
   * @return List of mappings cl.index X cl.index -> overlap, which in general is a forest, where bottom-most level consists of \
   * the original clusters and the hier level consists of the upper level overlaps. The bottom is in index 0, the highest is on \
   * the tail of the returned list.
   */
  def clustersOverlaps[C <: Cluster[V]](clusters: Set[C]): List[Map[Int, Map[Int, SubCluster[V]]]] = {
    index = clusters.map(_.index).max
    clustersOverlapsComp(clusters.map(c => new SubCluster[V](c.index, new HashSet[V] ++= c.vertices)))
  }

  protected def clustersOverlapsComp(clusters: Set[SubCluster[V]]): List[Map[Int, Map[Int, SubCluster[V]]]] = {
    val clusterSeq = clusters.toSeq
    val overlapLevel = new HashMap[Int, HashMap[Int, SubCluster[V]]]
    val nonEmptyOverlaps = new HashSet[SubCluster[V]]
    for (i <- 0 until clusterSeq.size; j <- i until clusterSeq.size if i != j) {
      val c1 = clusterSeq(i)
      val c2 = clusterSeq(j)
      val over = c1.overlap(c2)
      if (over.size > 0) {
        // if the sub-cluster with a given overlap already exists, associate it with c1 and c2
        val subC = nonEmptyOverlaps.find(_.vertices == over)
        if (subC.isDefined) {
          log.debug("Found already a cluster " + subC.get + "\n with overlap: " + over)
          subC.get.origClusters ++= (c1.getOrigClustersOrSelf ++ c2.getOrigClustersOrSelf)
          subC.get.parents ++= Set(c1, c2)
        } else {
          if (!overlapLevel.contains(c1.index))
            overlapLevel(c1.index) = new HashMap[Int, SubCluster[V]]
          val sc  = new SubCluster[V](nextIndex, over, new HashSet[SubCluster[V]] ++= Set(c1,c2),
            new HashSet[SubCluster[V]] ++= (c1.getOrigClustersOrSelf ++ c2.getOrigClustersOrSelf))
          overlapLevel(c1.index)(c2.index) = sc
          assert(sc.origClusters.size != 0 && sc.parents.size != 0)
          nonEmptyOverlaps += sc
          log.debug("Adding overlap of " + c1 + "\nand\n" + c2 + ":\n" + overlapLevel(c1.index)(c2.index))
        }
      }
    }
    if (!nonEmptyOverlaps.isEmpty) {
      log.debug("Detected " + nonEmptyOverlaps.size + " non-empty overlaps, continuing to the higher level")
      nonEmptyOverlaps.foreach(_.cleanUpParents())
      overlapLevel :: clustersOverlapsComp(nonEmptyOverlaps)
    } else Nil
  }

  def nextIndex = {
    index += 1
    index
  }
}

// todo Unify with the class in OmegaComparator.scala ... don't have time right now;-)
protected class SubCluster[V](index: Int, val vertices: HashSet[V], val parents: HashSet[SubCluster[V]], val origClusters: HashSet[SubCluster[V]])
  extends Cluster[V](index) {

  def this(index: Int, vert: HashSet[V]) = {
    this (index, vert, new HashSet[SubCluster[V]], new HashSet[SubCluster[V]])
  }

  def getOrigClustersOrSelf =
    if (origClusters.isEmpty)
      new HashSet[SubCluster[V]] += this
    else
      origClusters.toSet

  def cleanUpParents() {
    for (p <- parents) {
      p.vertices --= this.vertices
      p.cleanUpParents()
    }
  }

  def overlap(that: SubCluster[V]): HashSet[V] = this.vertices.intersect(that.vertices)

  override def toString = "Cluster #" + index + "\n" +
    "Intersection of clusters: " + parents.map(_.index).mkString(",") + "\n" +
    "Original clusters: " + origClusters.map(_.index).mkString(",") + "\n" +
    "Vertices: " + vertices.mkString(",")
}