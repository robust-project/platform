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

package crosscom

import org.scalatest.FunSuite
import OverlapTestData._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.{HierarchicalClusterVertexOverlap, ClusterDescendantOverlap, ClusterAncestorOverlap, ClusterJaccardOverlap}
import ie.deri.uimr.crosscomanalysis.cluster.model.Cluster

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 29/03/2011
 * Time: 16:08
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

@RunWith(classOf[JUnitRunner])
class ClusterOverlapTest extends FunSuite {

  test("Jaccard Coefficient Test") {
    val jaccardOverlap = new ClusterJaccardOverlap[Int]
    val overlaps = jaccardOverlap.clustersOverlaps(FORMER_CLUSTERS.toSet, LATER_CLUSTERS.toSet)

    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(0)) === 2d / 3)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(1)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(2)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(3)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(4)) === 0)

    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(0)) === 1d / 4)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(1)) === 1d)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(2)) === 1d / 2)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(3)) === 1d / 4)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(4)) === 0d)

    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(0)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(1)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(2)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(3)) === 2d / 4)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(4)) === 1d)
  }

  test("Ancestors Overlap Test") {
    val ancestorOverlap = new ClusterAncestorOverlap[Int]
    val overlaps = ancestorOverlap.clustersOverlaps(FORMER_CLUSTERS.toSet, LATER_CLUSTERS.toSet)

    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(0)) === 2d / 3)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(1)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(2)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(3)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(4)) === 0)

    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(0)) === 1d / 3)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(1)) === 1d)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(2)) === 1d)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(3)) === 1d / 3)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(4)) === 0d)

    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(0)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(1)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(2)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(3)) === 2d / 3)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(4)) === 1d)
  }

  test("Descendant Overlap Test") {
    val descendantOverlap = new ClusterDescendantOverlap[Int]
    val overlaps = descendantOverlap.clustersOverlaps(FORMER_CLUSTERS.toSet, LATER_CLUSTERS.toSet)

    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(0)) === 1d)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(1)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(2)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(3)) === 0)
    assert(overlaps(FORMER_CLUSTERS(0))(LATER_CLUSTERS(4)) === 0)

    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(0)) === 1d / 2)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(1)) === 1d)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(2)) === 1d / 2)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(3)) === 1d / 2)
    assert(overlaps(FORMER_CLUSTERS(1))(LATER_CLUSTERS(4)) === 0d)

    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(0)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(1)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(2)) === 0d)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(3)) === 2d / 3)
    assert(overlaps(FORMER_CLUSTERS(2))(LATER_CLUSTERS(4)) === 1d)
  }

  test("Hierarchical Cluster Vertex Overlap Test") {
    val hierOverlap = new HierarchicalClusterVertexOverlap[Int]
    val overlaps = hierOverlap.clustersOverlaps(LATER_CLUSTERS.toSet)(0)
    for ((index1, pair) <- overlaps; (index2, overlap) <- pair) {
      println("Overlap between:")
      print(index1)
      print(" and ")
      print(index2)
      println(" is")
      println(overlap)
      println("==========================================================")
    }

    val overlap12 =
      if (overlaps.contains(1))
        overlaps(1)(2)
      else
        overlaps(2)(1)
    assert(overlap12.vertices === Set(3), "Overlap has to be 3")
    assert(overlap12.parents.size === 2, "Overlap has to have two parents")
    val parents12 = overlap12.parents.toSeq
    assert(parents12.map(_.vertices.size).sum === 2, "Overlapping nodes have to be removed from parents")

    // we don't know which combination will be stored - the true number of parents is stored in the overlap's 'parents' field
    val overlap67 =
      if (overlaps.contains(6) && overlaps(6).contains(7))
        overlaps(6)(7)
      else if (overlaps.contains(7) && overlaps(7).contains(6))
        overlaps(7)(6)
      else if (overlaps.contains(2) && overlaps(2).contains(7))
        overlaps(2)(7)
      else if (overlaps.contains(7) && overlaps(7).contains(2))
        overlaps(7)(2)
      else if (overlaps.contains(2) && overlaps(2).contains(7))
        overlaps(2)(7)
      else if (overlaps.contains(2) && overlaps(2).contains(6))
        overlaps(2)(6)
      else
        overlaps(6)(2)

    assert(overlap67.vertices === Set(4), "Overlap has to be 4")
    assert(overlap67.parents.size === 3, "Overlap has to have three parents")
    val parents67 = overlap67.parents.toSeq
    assert(parents67.map(_.vertices.size).sum === 0, "Overlapping nodes have to be removed from parents")

    val overlap37 =
      if (overlaps.contains(3))
        overlaps(3)(7)
      else
        overlaps(7)(3)
    assert(overlap37.vertices === Set(5, 6), "Overlap has to be (5,6)")
    assert(overlap37.parents.size === 2, "Overlap has to have two parents")
    val parents37 = overlap37.parents.toSeq
    assert(parents37.map(_.vertices.size).sum === 1, "Overlapping nodes have to be removed from parents")
  }

  test("Hierarchical Cluster Vertex Overlap Test 2") {
    val hierOverlap = new HierarchicalClusterVertexOverlap[Int]
    val overlaps = hierOverlap.clustersOverlaps(Set(Cluster(1, Set(1,2)),Cluster(2,Set(2,3,4)),Cluster(3,Set(2,4))))

    for (overlapLayer <- overlaps; (index1, pair) <- overlapLayer; (index2, overlap) <- pair) {
      println("Overlap between:")
      print(index1)
      print(" and ")
      print(index2)
      println(" is")
      println(overlap)
      println("==========================================================")
    }

    assert(overlaps.size == 2)
  }
}

