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

package ie.deri.uimr.crosscomanalysis.cluster.exporters.commgraph

import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.cluster.model.ClusterFromRDB
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import scala.collection.Map
import ie.deri.uimr.crosscomanalysis.graphdb.importers.GraphImporter
import com.tinkerpop.blueprints.pgm.Graph
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/11/2011
 * Time: 11:08
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

abstract class CommunityGraph(val db: String, val clusterFormat: Int, val clusterFlag: Option[String], val sliceId: Long)
  extends SessionFactorySetter with Logging with GraphImporter {
  val RELATION_TYPE = "share-nodes-with"

  lazy val graph = loadCommunityGraph

  protected def loadCommunityGraph: Graph = {
    setUpSessionFactory(db)
    implicit val g = new TinkerGraph
    transaction {
      val query =
        if (clusterFlag.isDefined)
          from(cluster)(c => where(c.format === clusterFormat and c.flag === clusterFlag and c.sliceId === sliceId) select (c))
        else
          from(cluster)(c => where(c.format === clusterFormat and c.flag.isNull and c.sliceId === sliceId) select (c))
      val clusters = (for (c <- query) yield new ClusterFromRDB(c)).toSet
      val w = weights(clusters)

      for (firstComm <- w.keys; (secondComm, weight) <- w(firstComm) if weight > 0) {
        // compute the weight
        val source = retrieveOrCreateVertex(firstComm.index)
        source(SIZE) = firstComm.vertices.size.asInstanceOf[AnyRef]
        val sink = retrieveOrCreateVertex(secondComm.index)
        sink(SIZE) = secondComm.vertices.size.asInstanceOf[AnyRef]
        val e = g.addEdge(newEdgeId, source, sink, RELATION_TYPE)
        e(WEIGHT) = weight.asInstanceOf[AnyRef]
      }
      log.debug("Loaded " + clusters.size + " communities into a community graph")
    }
    g
  }

  /**
   * @return Weights of the edges between clusters - have to control for self-loops on its own (if needed)
   */
  protected def weights(clusters: collection.Set[ClusterFromRDB]): Map[ClusterFromRDB, Map[ClusterFromRDB, Double]]
}