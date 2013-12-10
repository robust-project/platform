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

import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster
import collection.{Set, Seq}
import collection.mutable.{MutableList, HashSet, HashMap}
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.apache.commons.math.util.MathUtils.binomialCoefficient

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 13/06/2011
 * Time: 22:25
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

/**
 * Implements rand index adjusted to overlapping clusters - measure Omega.
 *
 * See (Dent & Collins, 1988) Omega A General Formulation of the Rand Index of Cluster Recovery Suitable for Non-disjoint Solutions
 */
class OmegaComparator[V](val clusters1: Set[_ <: Cluster[V]], val clusters2: Set[_ <: Cluster[V]], val graphSize: Long) extends Logging {

  val overlaps = new MutableList[SubCluster[V]]
  val overlapsIndex = new HashMap[Set[SubCluster[V]], Int]
  val FIRST_CLUSTERING = 1
  val SECOND_CLUSTERING = 2
  val SUBCLUSTER = 3

  /**
   * Infers the size of the graph as a maximum size of the union over clustering1 or clustering2
   */
  def this(clusters1: Set[_ <: Cluster[V]], clusters2: Set[_ <: Cluster[V]]) = {
    this (clusters1, clusters2, math.max(clusters1.map(_.vertices).flatten.toSet.size, clusters2.map(_.vertices).flatten.toSet.size))
  }

  def compare = {
    // add the original clusters as overlaps (level 0)
    // clustering 1
    clusters1.foreach(c => overlaps += new SubCluster[V](nextIndex, new HashSet[V] ++= c.vertices, 0, FIRST_CLUSTERING))
    val clusters1EndIndex = lastIndex
    log.debug("Computing clustering1 overlaps")
    computeOverlapLevels(generateUniquePairs(0, clusters1EndIndex), 0)
    val clusters1OverlapsEndIndex = lastIndex
    log.debug("Overlaps size at level 0: " + overlaps.size)
    // clustering 2
    clusters2.foreach(c => overlaps += new SubCluster[V](nextIndex, new HashSet[V] ++= c.vertices, 0, SECOND_CLUSTERING))
    val clusters2EndIndex = lastIndex
    log.debug("Computing clustering2 overlaps")
    computeOverlapLevels(generateUniquePairs(clusters1OverlapsEndIndex + 1, clusters2EndIndex), 0)
    val clusters2OverlapsEndIndex = lastIndex
    log.debug("Overlaps computed. In total: " + overlaps.size)
    // compute overlaps between overlaps of clustering1 and clustering2
    val crossClusteringPairs =
      for (i <- 0 to clusters1OverlapsEndIndex; j <- (clusters1OverlapsEndIndex + 1) to clusters2OverlapsEndIndex) yield (i, j)
    // compute the overlaps
    computeOverlapLevels(crossClusteringPairs, 1)
    log.debug("Overlaps size: " + overlaps.size)
    log.debug("Summarizing pair counts")
    if (overlaps.isEmpty) {
      0d
    } else {
      // compute omega
      val (summary, maxRow, maxCol) = summarizePairs
      var expected: Double = 0
      var trace: Long = 0
      var marginalRow: Long = 0
      var marginalCol: Long = 0
      for (i <- 0 to math.min(maxRow, maxCol)) {
        val rowSum = summary(i).sum
        val colSum = (0 to maxRow).map(j => summary(j)(i)).sum
        expected += rowSum * colSum
        trace += summary(i)(i)
        marginalRow += rowSum
        marginalCol += colSum
      }
      val marginal = if (maxRow > maxCol) marginalCol else marginalRow

      (marginal * trace - expected) / (math.pow(marginal, 2) - expected)
    }
  }

  /**
   * Iterates in a bottom-up manner and computes pair-wise overlaps from the previous level and stores them into overlaps.
   * Note that the overlaps between overlaps of different levels are NOT managed
   */
  def computeOverlapLevels(pairs: Seq[(Int, Int)], level: Int) {
    val origIndex = lastIndex
    // iterate over combinations (pairs) of the sub clusters from the previous level
    for ((i, j) <- pairs) {
      // if the overlap has already been computed, associate it to its predecessors
      val subc = findSubCluster(overlaps(i), overlaps(j))
      if (subc.isDefined) {
        if (subc.get != overlaps(i))
          subc.get.parents += overlaps(i)
        if (subc.get != overlaps(j))
          subc.get.parents += overlaps(j)
      } else {
        // othwerwise store the new overlap
        val o = overlap(overlaps(i), overlaps(j))
        if (o.size > 1) {
          // if the pair's overlap is not empty (in terms of pairs), store the combined overlap
          addOverlap(new SubCluster[V](nextIndex, new HashSet ++= o, new HashSet ++= Set(overlaps(i), overlaps(j)),
            overlaps(i).getOrigClustersOrSelf ++ overlaps(j).getOrigClustersOrSelf, level, SUBCLUSTER))
        }
      }
    }
    // if this level resulted in any non-empty overlaps, compute higher level
    if ((lastIndex - origIndex) > 1) {
      computeOverlapLevels(generateUniquePairs(origIndex + 1, lastIndex), level + 1)
    }
  }

  private def addOverlap(s: SubCluster[V]) {
    overlaps += s
    overlapsIndex(s.origClusters) = lastIndex
  }

  /**
   * @return Matrix (# clusters in which pair is together in clustering1 X # clusters in which pairs together in clustering2) -> # pairs of objects
   */
  def summarizePairs = {
    val summary = new HashMap[Int, HashMap[Int, Long]]
    summary(0) = new HashMap[Int, Long]
    var pairs: Long = 0
    val maxLevel = overlaps.maxBy(_.level).level
    var maxRow = 0
    var maxCol = 0
    for (l <- (0 to maxLevel).reverseIterator;
         overlap <- overlaps.filter(_.level == l).reverseIterator if overlap.vertices.size > 1) {
      overlap.updateParentsPairs
      val pairCount = overlap.pairCount
      val origClusters = overlap.getOrigClustersOrSelf
      val clustering1ParentCount = origClusters.filter(_.clustering == FIRST_CLUSTERING).size
      val clustering2ParentCount = origClusters.filter(_.clustering == SECOND_CLUSTERING).size
      if (clustering1ParentCount > maxRow) maxRow = clustering1ParentCount
      if (clustering2ParentCount > maxCol) maxCol = clustering2ParentCount
      if (!summary.contains(clustering1ParentCount))
        summary(clustering1ParentCount) = new HashMap[Int, Long]
      if (!summary(clustering1ParentCount).contains(clustering2ParentCount))
        summary(clustering1ParentCount)(clustering2ParentCount) = pairCount
      else
        summary(clustering1ParentCount)(clustering2ParentCount) += pairCount
      if (clustering1ParentCount + clustering2ParentCount > 0)
        pairs += pairCount
      overlap.higherLevelsPairs.clear() // clear the auxiliary pairs - safe memory:)
    }
    summary(0)(0) = graphSize * (graphSize - 1) / 2 - pairs // infer the rest of pairs (non-co-occurring pairs)
    val ret = Array.ofDim[Long](maxRow + 1, maxCol + 1)
    for ((cl1, m) <- summary;
         (cl2, v) <- m) {
      ret(cl1)(cl2) = v
    }

    (ret, maxRow, maxCol)
  }

  def generateUniquePairs(start: Int, end: Int) = {
    log.debug("Generating unique pairs: " + start + ", " + end)
    for (i <- start to end; j <- start to end; if i > j) yield (i, j)
  }

  def findSubCluster(sc1: SubCluster[V], sc2: SubCluster[V]): Option[SubCluster[V]] = findSubCluster(sc1.getOrigClustersOrSelf ++ sc2.getOrigClustersOrSelf)

  /**
   * @return An overlap of sets of origClusters if already computed.
   */
  def findSubCluster(origClusters: Set[SubCluster[V]]): Option[SubCluster[V]] = {
    val i = overlapsIndex.get(origClusters)
    if (i.isDefined)
      Some(overlaps(i.get))
    else None
  }

  def nextIndex = overlaps.size // starting with 0

  def lastIndex = overlaps.size - 1

  def overlap(c1: Cluster[V], c2: Cluster[V]) = c1.vertices.intersect(c2.vertices)
}

protected class SubCluster[V](index: Int, val vertices: HashSet[V], val parents: HashSet[SubCluster[V]],
                              val origClusters: Set[SubCluster[V]], val level: Int, val clustering: Int)
  extends Cluster[V](index) {

  var higherLevelsPairs = new HashSet[Pair[V]]

  def this(index: Int, vertices: HashSet[V], level: Int, clustering: Int) = {
    this (index, vertices, new HashSet[SubCluster[V]], Set.empty[SubCluster[V]], level, clustering)
  }

  def getOrigClustersOrSelf =
    if (origClusters.isEmpty)
      Set(this)
    else
      origClusters

  def pairCount = binomialCoefficient(vertices.size, 2) - higherLevelsPairs.size

  def updateParentsPairs {
    parents.foreach(_.higherLevelsPairs ++= generatePairs(vertices))
  }

  def generatePairs(vertices: Set[V]) = {
    val v = vertices.toSeq
    for (i <- 0 until v.size; j <- 0 until v.size if i > j) yield new Pair[V](v(i), v(j))
  }
}

protected class Pair[V](val first: V, val second: V) {

  override def equals(that: Any) =
    if (that.isInstanceOf[Pair[V]]) {
      val p: Pair[V] = that.asInstanceOf[Pair[V]]
      (p.first == this.first && p.second == this.second) || (p.first == this.second && p.second == this.first)
    } else false

  override def hashCode() = first.hashCode() + second.hashCode()

  override def toString = "(" + first + ", " + second + ")"
}