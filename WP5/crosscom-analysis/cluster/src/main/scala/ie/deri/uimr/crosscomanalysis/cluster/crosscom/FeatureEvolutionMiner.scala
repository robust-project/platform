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

import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.cluster.model.{ClusterWithContent, ClusterFromRDB}
import collection.Set
import collection.mutable.HashSet
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import org.apache.commons.cli.{Option => CliOption}
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import ie.deri.uimr.crosscomanalysis.cluster.{ClusterFormats, ClusterArgsParser}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 13/03/2011
 * Time: 15:51
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object FeatureEvolutionMiner extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser {

  override val COMMAND_NAME = "feature-miner"

  private val ancestorOverlap = new ClusterAncestorOverlap[Long]
  private val descendantOverlap = new ClusterDescendantOverlap[Long]

  val SHIFT_EVENT = "shift"
  val MERGE_PLAIN_EVENT = "merge-plain"
  val COMMUNITY_LOST_EVENT = "community-lost"

  def mine(sliceType: Int, db: String, clusterFormat: Int, flag: Option[String], threshold: Double, outdir: File) {
    setUpSessionFactory(db)

    // define output functions
    val crosscomPW = new PrintWriter(outdir.getAbsolutePath + File.separator + "crosscom-"+ SliceTypes(sliceType) + "-"
      + ClusterFormats(clusterFormat) + (if (flag.isDefined) "_" + flag.get else "") + "t" + threshold + ".csv")
    def crosscomOut(s: Any*) {
      synchronized {
        crosscomPW.println(s.map(_.toString.replaceAll(",", "\\,")).mkString(","))
      }
    }

    crosscomOut("indexParent","indexChild","event","beginParent", "beginChild")
    transaction {
      val keywordVectorLoader = new KeywordVectorLoader(db)
      var formerClusters: Option[Set[ClusterFromRDB with ClusterWithContent[Long]]] = None
      var formerSliceVertices: Option[HashSet[Long]] = None
      for (sliceId <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns.id) orderBy (ns.beginDate).asc)) {
        log.info("Processing slice #" + sliceId)
        val laterSliceVertices = retrieveSliceVertices(sliceId)
        val clusterQuery =
          if (flag.isDefined) from(cluster)(c => where(c.sliceId === sliceId and c.flag === flag and c.format === clusterFormat) select (c))
          else from(cluster)(c => where(c.sliceId === sliceId and c.format === clusterFormat) select (c))
        val vectors = keywordVectorLoader.loadVectorsForSlice(sliceId)
        val laterClusters =
          (for (cluster <- clusterQuery)
          yield new ClusterFromRDB(cluster) with ClusterWithContent[Long] {
              val featureVectors = vectors
              val featureIndex = keywordVectorLoader.keywordIndex
            }).toSet
        log.debug("Number of clusters: " + laterClusters.size)
        if (formerClusters.isDefined) {
          val ancestors = ancestorOverlap.clustersOverlaps(formerClusters.get, laterClusters)
          val descendants = descendantOverlap.clustersOverlaps(formerClusters.get, laterClusters)
          var verticesInLostCommunitiesCount = 0d
          for (formerCluster <- formerClusters.get) {
            for (laterCluster <- laterClusters) {
              // community shift
              val sim = formerCluster.cosineSimilarity(laterCluster)
//              log.debug("Similarity between \n" + formerCluster + " and \n" + laterCluster + "\n: " + sim)
              val shift = ancestors(formerCluster)(laterCluster) * (1 - sim)
//              log.debug("Community shift between \n" + formerCluster + " and \n" + laterCluster + "\n: " + shift)
              if (shift >= threshold) {
                out("Shift: \n" + formerCluster + "\n -> \n" + laterCluster + "\n= " + shift + "\n")
                crosscomOut(formerCluster.index, laterCluster.index, SHIFT_EVENT, formerCluster.clusterDB.beginYear,
                  laterCluster.clusterDB.beginYear)
              }
              // plain community merge
              if (descendants(formerCluster)(laterCluster) >= threshold && formerCluster.index != laterCluster.index) {
                out("Merge (plain): \n" + formerCluster + "\n -> \n" + laterCluster + "\n")
                crosscomOut(formerCluster.index, laterCluster.index, MERGE_PLAIN_EVENT, formerCluster.clusterDB.beginYear,
                  laterCluster.clusterDB.beginYear)
              }
            }
            // community lost
            val communityLostOverlap = formerCluster.vertices.intersect(laterSliceVertices).size.toDouble / formerCluster.vertices.size
            if (laterClusters.find(_.index == formerCluster.index).isEmpty && communityLostOverlap <= threshold) {
              verticesInLostCommunitiesCount += formerCluster.vertices.size
              out("Community lost " + formerCluster + "\n: " + communityLostOverlap + "\n")
              crosscomOut(formerCluster.index, "NULL", COMMUNITY_LOST_EVENT, formerCluster.clusterDB.beginYear, "NULL")
            }
          }
          // calculate lost probability
//          val unionSize = (laterSliceVertices ++ formerSliceVertices.get).size
//          val generalTurnoverProb = (unionSize - laterSliceVertices.intersect(formerSliceVertices.get).size.toDouble) / unionSize
//          val verticesInCommunities = new HashSet[Long]
//          formerClusters.get.foreach(verticesInCommunities ++= _.vertices)
//          laterClusters.foreach(verticesInCommunities ++= _.vertices)
//          val lostCommProb = verticesInLostCommunitiesCount / verticesInCommunities.size
//          out("General lost probability: " + generalTurnoverProb)
//          out("Community lost probability: " + lostCommProb)
        }
        formerClusters = Some(laterClusters)
        formerSliceVertices = Some(laterSliceVertices)
      }
    }
    crosscomPW.close()
  }

  override def main(args: Array[String]) {
    val line = parseArgs(args)
    val t = if (line.hasOption(THRESHOLD_OPT.getOpt)) getOptValue(THRESHOLD_OPT).toDouble else 0.5
    mine(getSliceType, getOptValue(DB_OPT), getClusterFormat, getClusterFlag, t, new File(getOptValue(DIR_OPT)))
  }

  override protected def commandLineOptions = SLICE_TYPE_OPT :: DB_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT ::
    THRESHOLD_OPT :: DIR_OPT :: Nil

  private def out(s: String) {
    println(s)
  }

  private def retrieveSliceVertices(sliceId: Long) = {
    val vert = new HashSet[Long]
    for ((source, sink) <- from(networkSliceStructure)(nss => where(nss.sliceId === sliceId) select ((nss.source, nss.sink)))) {
      vert += source
      vert += sink
    }
    vert
  }
}