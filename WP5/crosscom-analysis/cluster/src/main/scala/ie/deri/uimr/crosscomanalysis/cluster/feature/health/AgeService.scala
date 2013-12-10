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

package ie.deri.uimr.crosscomanalysis.cluster.feature.health

import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetup
import ie.deri.uimr.crosscomanalysis.util.Config._
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType._
import co.polecat.robust.equalizer.IHealthIndicator
import java.util.Date
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import ie.deri.uimr.crosscomanalysis.cluster.feature._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 04/11/2011
 * Time: 14:43
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class AgeService extends  IHealthIndicator {

  SessionFactorySetup.setup(config.get("default.db").get)

  def getHealthScore(communityIndex: Int, begin: Date, end: Date) = transaction {
    val sliceType = SliceTypes.withName(config.get("health.sliceType." + getPeriodType(begin, end).toString).get)
    val sequenceLength = from(networkSlice)(ns =>
      where(ns.sliceType === sliceType.id)
        compute(countDistinct(ns.id))).single.measures
    val activity = from(networkSlice, cluster)((ns,c) =>
      where(ns.sliceType === sliceType.id and ns.id === c.sliceId and c.index === communityIndex and c.endDate <= end)
        compute(countDistinct(c.id))).single.measures
    (activity.toDouble / sequenceLength).toFloat
  }

  def getName = NORM_AGE.toString
}