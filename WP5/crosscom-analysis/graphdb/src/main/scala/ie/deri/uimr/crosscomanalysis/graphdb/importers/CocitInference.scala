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

package ie.deri.uimr.crosscomanalysis.graphdb.importers

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 16/04/2011
 * Time: 17:42
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

import java.util.{HashMap => JavaHashMap}
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl
import com.tinkerpop.blueprints.pgm.impls.neo4j.util.Neo4jVertexSequence
import org.neo4j.index.lucene.LuceneIndexService
import ie.deri.uimr.crosscomanalysis.util.Logging
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph
import org.neo4j.kernel.EmbeddedGraphDatabase
import collection.mutable.HashMap
import com.tinkerpop.blueprints.pgm.Vertex
import ie.deri.uimr.crosscomanalysis.graphdb.CSXGraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.traversals.Cocitation
import ie.deri.uimr.crosscomanalysis.util.Config.config
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions.asRelationshipType

object CocitInference extends Logging {

  private val clusterCocits = new HashMap[(Long, Long), Long]

  def main(args: Array[String]) {
    infer()
    store()
  }

  private def infer() {
    val graph = new Neo4jGraph(
      new EmbeddedGraphDatabase(config.get("graphdb.csxcitegraph.path").get,
        EmbeddedGraphDatabase.loadConfigurations("neo4j.properties")))
    val index = new LuceneIndexService(graph.getRawGraph)
    log.info("Inferring edges")
    for (clusterV <- new Neo4jVertexSequence(index.getNodes(NODE_TYPE, CLUSTER), graph); clusterId = id(clusterV);
         (cocitClusterV, count) <- Cocitation.clusterCocitations(clusterV); cocitClusterId = id(cocitClusterV)) {
      val (clusterPair, w) =
        if (clusterCocits.contains((clusterId, cocitClusterId)))
          ((clusterId, cocitClusterId), clusterCocits((clusterId, cocitClusterId)))
        else if (clusterCocits.contains((cocitClusterId, clusterId)))
          ((cocitClusterId, clusterId), clusterCocits((cocitClusterId, clusterId)))
        else ((clusterId, cocitClusterId), 0l)
      clusterCocits(clusterPair) = w + count.longValue
    }
    log.info(clusterCocits.size + " cocitations inferred.")
    index.shutdown()
    graph.shutdown()
  }

  private def store() {
    val inserter = new BatchInserterImpl(config.get("graphdb.csxcitegraph.path").get, BatchInserterImpl.loadProperties("neo4j.properties"))
    var c = 0
    log.info("Storing the relations.")
    for (((cluster1, cluster2), count) <- clusterCocits) {
      val prop = new JavaHashMap[String, AnyRef]
      prop.put(COCIT_COUNT, (count / 2).asInstanceOf[AnyRef])
      val e = inserter.createRelationship(cluster1, cluster2, COCITED.name, prop)
      c += 1
      if (c % 10000 == 0) log.info("Stored " + c + " cocitations.")
    }
    log.info("Stored " + c + " cocitations altogether.")
    inserter.shutdown()
  }

  private def id(v: Vertex) = v.getId.asInstanceOf[java.lang.Long].longValue
}