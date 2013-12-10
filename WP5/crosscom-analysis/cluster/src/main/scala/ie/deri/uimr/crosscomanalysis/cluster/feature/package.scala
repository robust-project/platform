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

package ie.deri.uimr.crosscomanalysis.cluster

import TimePeriodTypes._
import java.util.Date
import ie.deri.uimr.crosscomanalysis.util.Config.config
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import feature.FeatureType._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */
package object feature {

  def getPeriodType(begin:Date, end:Date) = {
    assert (begin.before(end))
    val diff = (end.getTime - begin.getTime) / (1000 * 60 * 60 * 24) // difference in days
    if (diff == 365 || diff == 366)
      YEAR
    else if (diff >= 28 && diff <= 31)
      MONTH
    else if (diff == 7)
      WEEK
    else if (diff == 1)
      DAY
    else sys.error("Unsupported time period")
  }

  def getFeatureValue(featureType: FeatureType, communityIndex: Int, begin: Date, end: Date): Double = {
    val sliceType = SliceTypes.withName(config.get("health.sliceType." + getPeriodType(begin, end).toString).get)
    transaction {
      from (cluster, clusterFeatures, networkSlice) ((c, cf, ns) =>
        where(c.id === cf.clusterId and c.sliceId === ns.id and ns.sliceType === sliceType.id and ns.beginDate >= begin
          and ns.endDate <= end and cf.featureType === featureType.id and c.index === communityIndex)
        select(cf.featureValue)).head
    }
  }
}
