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

package ie.deri.uimr.crosscomanalysis.cluster.feature.comp.boardsie

import ie.deri.uimr.crosscomanalysis.cluster.feature.comp.FeatureExtractor
import ie.deri.uimr.crosscomanalysis.db.tables.graph.Cluster
import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType.POST_COUNT

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 15/11/2011
 * Time: 14:34
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes a count of posts for individual fora.
 */

object PostsCount extends FeatureExtractor {

  override val COMMAND_NAME = "boardsie-posts-count"

  override protected def processSlice(sliceId: Long, clusterFormat: Int, flag: Option[String]) {
    transaction {
      val ns = networkSlice.lookup(sliceId).get
      val clusterId =
        if (flag.isDefined)
          from (cluster)(c => where(c.sliceId === sliceId and c.format === clusterFormat and c.flag === flag) select((c.index, c.id))).toMap
        else
          from (cluster)(c => where(c.sliceId === sliceId and c.format === clusterFormat) select((c.index, c.id))).toMap
      log.info("Processing " + ns)
      for (pair <- from(posts, threads)((p,t) =>
        where(p.threadid === t.threadid and p.posteddate >= ns.beginDate and
          p.posteddate <= ns.endDate and p.userid <> 0)
          groupBy(t.forumid)
          compute(countDistinct(p.postid)))) {
        storeFeatureValue(clusterId(pair.key.toInt), POST_COUNT.id, pair.measures)
      }
    }
  }

   // computation done on the slice level
  protected def processCluster(clusterDB: Cluster) {}
}