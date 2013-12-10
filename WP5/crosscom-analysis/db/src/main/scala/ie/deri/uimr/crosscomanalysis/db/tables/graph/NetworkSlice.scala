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

package ie.deri.uimr.crosscomanalysis.db.tables.graph

import org.squeryl.annotations._
import org.squeryl.KeyedEntity
import java.util.{Calendar, Date}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05-Dec-2010
 * Time: 16:29:09
 * ©2010 Digital Enterprise Research Institute, NUI Galway. 
 */
class NetworkSlice(
                    @Column("begin_date")
                    val beginDate: Date,
                    @Column("end_date")
                    val endDate: Date,
                    @Column("type")
                    val sliceType: Int) extends KeyedEntity[Long] {
  var id = -1l

  override def toString = "Network Slice #" + id + " in " + beginDate + "-" + endDate + " of type " + SliceTypes(sliceType).toString

  def beginYear = getYear(beginDate)

  def endYear = getYear(endDate)

  protected def getYear(d: Date) = {
    val cal = Calendar.getInstance
    cal.setTime(d)
    cal.get(Calendar.YEAR)
  }
}

