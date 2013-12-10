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

import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDBWithSubgraph
import ie.deri.uimr.crosscomanalysis.cluster.centrality.NodeWithinClusterCentrality
import org.apache.commons.math.linear.{OpenMapRealMatrix, RealMatrixPreservingVisitor, RealMatrix}
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/06/2012
 * Time: 16:05
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

trait CommunityInfluenceLike {

  protected val userCount: Int
  protected val commCount: Int

  protected def outputMatrix(m: RealMatrix, f: File) {
    val out = new PrintWriter(f)
    m.walkInOptimizedOrder(new RealMatrixPreservingVisitor {
      def visit(row: Int, column: Int, value: Double) {
        if (value != 0)
          out.println(row + "," + column + "," + value)
      }
      def end = 0d
      def start(rows: Int, columns: Int, startRow: Int, endRow: Int, startColumn: Int, endColumn: Int) {}
    })
    out.close()
  }

  protected def centralityAsMatrix(clusters: collection.immutable.Set[ClusterFromRDBWithSubgraph],
                         centralityMeasure: NodeWithinClusterCentrality[ClusterFromRDBWithSubgraph]): OpenMapRealMatrix = {
    val centralityMatrix = new OpenMapRealMatrix(userCount + 1, commCount + 1)
    for (cluster <- clusters; (userid, centrality) <- centralityMeasure.compute(cluster)) {
      centralityMatrix.setEntry(userid.toInt, cluster.index, centrality)
    }
    centralityMatrix
  }

  protected def maxSliceId(sliceType: Int) = transaction {
    from(networkSlice)(ns => where(ns.sliceType === sliceType) compute (max(ns.id))).single.measures.get
  }

  protected def rowNormalizeMatrix(m: OpenMapRealMatrix): OpenMapRealMatrix = {
    for (r <- 0 until m.getRowDimension) {
      val row = m.getRow(r)
      val rowSum = row.sum
      if (rowSum > 0)
        m.setRow(r, row.map(_ / rowSum))
    }

    m
  }
}
