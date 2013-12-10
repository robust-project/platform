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

package ie.deri.uimr.crosscomanalysis.cluster.model

import collection.mutable.MutableList
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import org.squeryl.PrimitiveTypeMode._
import collection.JavaConversions.asScalaIterator

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 22/02/2011
 * Time: 20:21
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait ClusterWithContent[V] extends ClusterWithFeatures[V] {

  this: Cluster[V] =>

  protected def keyword(id: Long) = inTransaction {
    (from(keywordId)(k => where(k.id === id) select (k.orig))).head
  }

  def highestRankedKeywords(n: Int) = {
    val unsorted = new MutableList[Pair[Int, Double]]
    // have to wrap by an object, as OpenMapRealVector does some monkey business with Entry objects - perhaps re-using
    for (e <- centroid.sparseIterator())
      unsorted += new Pair(e.getIndex, e.getValue)
    val max = (if (unsorted.size < n) unsorted.size else n)

    if (unsorted.isEmpty) None
    else {
      Some(for (sorted <- unsorted.sortWith(_._2 > _._2).slice(0, max)) // reverse sorting (highest first)
      yield keyword(featureIndex.find(_._2 == sorted._1).get._1)) // find a keyword id for a given keyword index
    }
  }

  override def toString = {
    val keywords = highestRankedKeywords(10)
    if (keywords.isDefined)
      super.toString + "\n characteristic keywords: " + keywords.get.mkString(", ")
    else
      super.toString
  }
}