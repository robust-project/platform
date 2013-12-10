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

import java.util.Date
import schemas.GraphSchema._
import tables.graph.NetworkSlice
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 20/11/2011
 * Time: 18:50
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

/**
 * Iterator over a specified slices in an ascending date order.
 */
class SliceIterator(val sliceType: Int, val begin: Option[Date] = None, val end: Option[Date] = None) extends Iterable[NetworkSlice] {
  def iterator = inTransaction {
    if (begin.isDefined && end.isDefined) {
      from(networkSlice)(ns =>
        where(ns.sliceType === sliceType and ns.beginDate >= begin.get and ns.endDate <= end.get)
          select (ns)
          orderBy (ns.beginDate).asc).iterator
    } else if (begin.isDefined && end.isEmpty) {
      from(networkSlice)(ns =>
        where(ns.sliceType === sliceType and ns.beginDate >= begin.get)
          select (ns)
          orderBy (ns.beginDate).asc).iterator
    } else if (begin.isEmpty && end.isDefined) {
      from(networkSlice)(ns =>
        where(ns.sliceType === sliceType and ns.endDate <= end.get)
          select (ns)
          orderBy (ns.beginDate).asc).iterator
    } else {
      from(networkSlice)(ns =>
        where(ns.sliceType === sliceType)
          select (ns)
          orderBy (ns.beginDate).asc).iterator
    }
  }
}