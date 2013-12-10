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

package ie.deri.uimr.crosscomanalysis.cluster.feature

/*
 * Copyright ©2010-2012, Digital Enterprise Research Institute (DERI), NUI Galway, http://www.deri.ie
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of DERI nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL DERI BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import ie.deri.uimr.crosscomanalysis.graphdb.DBGraphLoader
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions.blueprintsGraphToJUNG
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer
import com.tinkerpop.blueprints.pgm.{Edge, Vertex}
import io.Source
import ie.deri.uimr.crosscomanalysis.util.Logging
import collection.mutable.HashMap
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import collection.JavaConversions.iterableAsScalaIterable
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.{ClusterIterator, SessionFactorySetter, DBArgsParser}
import collection.Map
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.cluster.model.{ClusterFromRDB, Cluster}
import java.io.{PrintWriter, File}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 11/04/2012
 * Time: 12:36
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * This object computes the fraction of users, that cannot be reached from outside of the community (for each community).
 */

object IsolatedCommunityUsersSize extends ClusterArgsParser with DBArgsParser with Logging with SessionFactorySetter {

  override val COMMAND_NAME = "compute-community-isolated-users"

  override protected def commandLineOptions = DIR_OPT :: DB_OPT :: CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT :: OUTFILE_OPT :: Nil

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val out = new PrintWriter(getOptValue(OUTFILE_OPT))
      out.println("slice_id,comm_index,isolated_size,cardinality")
      for (membershipFile <- new File(getOptValue(DIR_OPT)).listFiles()) {
        log.info("Processing file " + membershipFile)
        val SlicePatter = """0*(\d+)_memberships.csv""".r
        val SlicePatter(sliceIdString) = membershipFile.getName
        val (memberships, cardinalities) = loadMembership(membershipFile)
        processSlice(sliceIdString.toLong, getClusterFormat, getClusterFlag, memberships, cardinalities, out)
      }
      out.close()
    }
  }

  private def processSlice(sliceId: Long, clusterFormat: Int, clusterFlag: Option[String],
                           memberships: Map[Long, Map[Int, Double]], cardinalities: Map[Int, Double], out: PrintWriter) {
    val graphLoader = new DBGraphLoader(sliceId, DEFAULT_EDGE)
    val componenter = new WeakComponentClusterer[Vertex, Edge]
    val components = componenter.transform(graphLoader.graph).map(c => c.map(v => v(ORIGID)).toSet).toSet // convert to scala sets of longs
    val clusters = transaction {
      new ClusterIterator(sliceId, clusterFormat, clusterFlag).map(new ClusterFromRDB(_)).toSet
    }
    // predicate that a node is reachable within its component from another community
    def isUserReachableFromAnotherComm(cluster: ClusterFromRDB, node: Long) = {
      val component = components.find(c => c.contains(node)).get
      component.find(x => x != node && !memberships(x).filterKeys(k => k != cluster.index).isEmpty).isDefined
    }

    // find isolated users for each community
    for (cluster <- clusters) {
      val isolates = cluster.vertices.filterNot(isUserReachableFromAnotherComm(cluster, _))
      val isolatedSize =
        if (isolates.size > 0) {
          var s:Double = 0
          for (i <- isolates) {
            s += memberships(i)(cluster.index)
          }
          s
        } else 0
      out.println(sliceId + "," + cluster.index + "," + isolatedSize + "," + cardinalities(cluster.index))
    }
  }

  /**
   * @return Mappings (user->(community->membership), community->cardinality)
   */
  private def loadMembership(file: File): (Map[Long,Map[Int,Double]], Map[Int,Double]) = {
    // load the membership matrix
    val membership = new HashMap[Long, HashMap[Int, Double]] // user -> (community -> membership)
    val cardinalities = new HashMap[Int, Double]
    for (line <- Source.fromFile(file).getLines()) {
      val Array(user, comm, mem) = line.split(",")
      if (!membership.contains(user.toLong)) {
        membership(user.toLong) = new HashMap[Int, Double]
      }
      membership(user.toLong)(comm.toInt) = mem.toDouble
      if (!cardinalities.contains(comm.toInt)) {
        cardinalities(comm.toInt) = 0
      }
      cardinalities(comm.toInt) = cardinalities(comm.toInt) + mem.toDouble
    }

    (membership, cardinalities)
  }
}