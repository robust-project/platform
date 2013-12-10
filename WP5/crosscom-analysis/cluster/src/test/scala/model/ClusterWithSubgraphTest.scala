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

package model

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.graphdb.DBGraphLoader
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import ie.deri.uimr.crosscomanalysis.db.{ClusterIterator, SliceIterator, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.cluster.ClusterFormats
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDBWithSubgraph
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07/03/2011
 * Time: 12:03
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

@RunWith(classOf[JUnitRunner])
class ClusterWithSubgraphTest extends FunSuite with SessionFactorySetter {
  setUpSessionFactory("cocit")

//  test("Instantiation of cluster with subgraph cannot fail") {
//    transaction {
//      for (slice <- new SliceIterator(SliceTypes.S3O2_COCIT.id).slice(0,1)) {
//        val graphLoader = new DBGraphLoader(slice.id)
//        assert(graphLoader.graph.getVertices.iterator.hasNext, slice + " cannot result in an empty graph!")
//        for (clusterDB <- new ClusterIterator(slice.id, ClusterFormats.OSLOM.id, Some("s25i1l1"))) {
//          val cluster = new ClusterFromRDBWithSubgraph(graphLoader.graph, clusterDB)
//          val subgraphVertices = cluster.subgraph.getVertices.map(_(ORIGID)).toSet
//          assert(cluster.vertices === subgraphVertices,
//            "Cluster's vertices have to be the same in the cluster object and its subgraph. Difference: " + (cluster.vertices &~ subgraphVertices))
//          val edgeCount = from(networkSliceStructure)(nss =>
//            where(nss.source.in(cluster.vertices) and nss.sink.in(cluster.vertices) and nss.sliceId === slice.id)
//              compute(count(nss.sink))).single.measures
//          assert(edgeCount == cluster.subgraph.getEdges.size, "Cluster's edges have to have the same size in the subgraph: " +
//            edgeCount + " vs. " + cluster.subgraph.getEdges.size)
//        }
//      }
//    }
//  }
}