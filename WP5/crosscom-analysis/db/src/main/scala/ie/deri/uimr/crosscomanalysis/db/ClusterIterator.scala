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

package ie.deri.uimr.crosscomanalysis.db

import schemas.GraphSchema._
import tables.graph.Cluster
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 20/11/2011
 * Time: 18:42
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

/**
 * Implements an iterable object representing clusters selected according to the passed criteria in an ascending date order.
 * Assumes the SessionFactory has been set.
 */
class ClusterIterator(val sliceId: Long, val clusterFormat: Int, val flag: Option[String]) extends Iterable[Cluster] {
  def iterator = inTransaction {
    if (flag.isDefined)
      from(cluster)(c => where(c.sliceId === sliceId and c.flag === flag and c.format === clusterFormat) select (c)).iterator
    else
      from(cluster)(c => where(c.sliceId === sliceId and c.format === clusterFormat) select (c)).iterator
  }
}