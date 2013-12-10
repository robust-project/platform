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

package ie.deri.uimr.crosscomanalysis.cluster.simulation

import io.Source
import java.io.File
import ie.deri.uimr.crosscomanalysis.util.Logging

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 21/03/2013
 * Time: 11:38
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
package object infdif extends Logging {

  /**
   * @return Set of non-empty communities and mapping ((userid,comm.id) -> membership)
   */
  def loadMembership(input: File) = {
    var communities = Set.empty[Int]
    var membership = Map.empty[(Long, Int), Double]
    for (line <- Source.fromFile(input).getLines()) {
      val Array(userId, commId, value) = line.split(",")
      communities = communities + commId.toInt
      membership = membership + ((userId.toLong, commId.toInt) -> value.toDouble)
    }
    log.debug("Loaded " + membership.size + " memberships for " + communities.size + " non-empty communities.")
    (communities, membership.getOrElse(_:(Long, Int), 0d))
  }

}
