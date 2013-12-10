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

import co.polecat.robust.equalizer.IHealthIndicator
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetup
import ie.deri.uimr.crosscomanalysis.util.Config.config
import java.util.Date
import ie.deri.uimr.crosscomanalysis.cluster.feature.FeatureType
import ie.deri.uimr.crosscomanalysis.cluster.feature._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 23/09/2011
 * Time: 15:02
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class AvgClustCoeffService extends IHealthIndicator {

  SessionFactorySetup.setup(config.get("default.db").get)

  def getHealthScore(communityIndex: Int, begin: Date, end: Date) = getFeatureValue(FeatureType.AVG_CLUST_COEFF,
    communityIndex, begin, end).toFloat

  def getName = FeatureType.AVG_CLUST_COEFF.toString
}