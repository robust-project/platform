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

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.util.Config.config
import ie.deri.uimr.crosscomanalysis.db.schemas.CSXCitegraphSchema._
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl
import java.util.HashMap
import ie.deri.uimr.crosscomanalysis.graphdb.CSXGraphProperties._
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import org.neo4j.graphdb.NotFoundException
import org.neo4j.index.lucene.LuceneIndexBatchInserterImpl
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions.asRelationshipType
import ie.deri.uimr.crosscomanalysis.db.squeryl.adapters.PostgreSqlAdapter

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 02/02/2011
 * Time: 12:15
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 *
 * Loads the citegraph (paper cites paper) into a Neo4J database. Each paper has then properties like year, array of
 * authors and array of dois.
 */
object CSXImporter extends SessionFactorySetter with Logging with DBArgsParser {
  private lazy val inserter = new BatchInserterImpl(config.get("graphdb.csxcitegraph.path").get, BatchInserterImpl.loadProperties("neo4j.properties"))
  private lazy val indexService = new LuceneIndexBatchInserterImpl(inserter)

  override val COMMAND_NAME = "load-csxgraph"

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      setUpSessionFactory(getOptValue(DB_OPT), new PostgreSqlAdapter)

      loadVertices()
      assignAuthorsToClusters()
      loadCitegraph()

      indexService.shutdown()
      inserter.shutdown()
    }
  }

  private def loadVertices() {
    inTransaction {
      // first load all authors
      log.info("Loading authors ...")
      var c = 0
      for (author <- from(authors)(a => select(a))) {
        // load only distinct author clusters (unfortunately Squeryl cannot select distinct column:/)
        if (indexService.getSingleNode(AUTH_CLUSTER_ID, author.cluster) == -1) {
          val p = verProps(author.cluster, AUTH_CLUSTER_ID)
          if (author.address.isDefined) p.put(ADDRESS, author.address.get)
          if (author.affil.isDefined) p.put(AFFILIATION, author.affil.get)
          if (author.email.isDefined) p.put(EMAIL, author.email.get)
          p.put(NAME, author.name.trim)
          p.put(AUTHOR_ID, author.id.asInstanceOf[AnyRef])
          val newAuthVert = inserter.createNode(p)
          indexService.index(newAuthVert, NODE_TYPE, AUTHOR.toString)
          indexService.index(newAuthVert, AUTH_CLUSTER_ID, author.cluster)

          c += 1
          if (c % 10000 == 0) log.info("Loaded " + c + " authors")
        }
      }
      log.info("Loaded " + c + " authors altogether")
      // then, load all clusters
      // if year is not known, it is set to 0
      // anything above 0 should be fine
      log.info("Authors loaded. Starting to load clusters ...")
      c = 0
      for (cluster <- from(clusters)(c => where(c.cyear > 0) select (c))) {
        val prop = verProps(cluster.id, CLUSTER_ID)
        prop.put(YEAR, cluster.cyear.get.asInstanceOf[AnyRef])
        val paperSet = from(papers)(p => where(p.cluster === cluster.id) select (p.id)).toSet
        if (!paperSet.isEmpty)
          prop.put(PAPERS, paperSet.toArray)
        // create a node representing the cluster
        val newClusterVer = inserter.createNode(prop)
        indexService.index(newClusterVer, CLUSTER_ID, cluster.id)
        indexService.index(newClusterVer, NODE_TYPE, CLUSTER.toString)
        indexService.index(newClusterVer, YEAR, cluster.cyear.get)
        c += 1
        if (c % 10000 == 0) log.info("Loaded " + c + " clusters")
      }
      log.info("Loaded " + c + " clusters altogether")
      // optimize indices
      log.info("Clusters loaded. Optimizing indices ...")
      indexService.optimize()
      log.info("Indices optimized.")
    }
  }

  private def assignAuthorsToClusters() {
    inTransaction {
      log.info("Assigning authors to clusters.")
      var c = 0
      for ((clusterId, authorClusterId) <- from(clusters, authors, papers)((c, a, p) =>
        where(c.id === p.cluster and p.id === a.paperid and c.cyear > 0) select ((c.id, a.cluster)))) {
        val authorV = indexService.getSingleNode(AUTH_CLUSTER_ID, authorClusterId)
        val clusterV = indexService.getSingleNode(CLUSTER_ID, clusterId)
        try {
          inserter.createRelationship(authorV, clusterV, AUTHOR_OF.name, null)
          c += 1
          if (c % 10000 == 0) log.info("Loaded " + c + " authors-clusters edges")
        } catch {
          case e: NotFoundException =>
            log.error(e)
            log.info("authorV: " + authorV + ", id: " + authorClusterId)
            log.info("clusterV: " + clusterV + ", id: " + clusterId)
        }
      }
      log.info("Loaded " + c + " author-cluster edges altogether")
    }
  }

  private def loadCitegraph() {
    inTransaction {
      log.info("Loading citegraph")
      var c = 0
      for ((sourceCluster, sinkCluster) <- from(citegraph)(cg => select((cg.citing, cg.cited)))) {
        val sourceV = indexService.getSingleNode(CLUSTER_ID, sourceCluster)
        val sinkV = indexService.getSingleNode(CLUSTER_ID, sinkCluster)
        if (sourceV > -1 && sinkV > -1) {
          // if id is -1, there's no such node (may be omitted because of missing cyear field)
          inserter.createRelationship(sourceV, sinkV, CITES.name, null)
          c += 1
          if (c % 10000 == 0) log.info("Loaded " + c + " citations")
        }
      }
      log.info("Loaded " + c + " citation altogether")
    }
  }

  private def transformName(name: String) = name.trim.toLowerCase

  private def verProps(id: Long, rel: String) = {
    val props = new HashMap[String, Object]
    props.put(rel, id.asInstanceOf[AnyRef])
    props
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: Nil
}