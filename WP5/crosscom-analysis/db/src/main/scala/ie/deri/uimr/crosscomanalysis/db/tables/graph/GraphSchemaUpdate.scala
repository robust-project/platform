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

import ie.deri.uimr.crosscomanalysis.util.Config.config
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import org.squeryl.PrimitiveTypeMode._
import java.util.Calendar
import SliceTypes._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07/03/2011
 * Time: 15:46
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

/*
object GraphSchemaUpdate extends SessionFactorySetter {

  def update(db: String) {
    setUpSessionFactory(config.get("db." + db + ".url").get, config.get("db." + db + ".user").get, config.get("db." + db + ".pass").get)
    val cal = Calendar.getInstance
    transaction {
      for (ns <- from(networkSlice)(ns => select(ns))) {
        cal.clear
        cal.set(ns.beginYear, 0, 1)
//        ns.beginDate = cal.getTime
        cal.clear
        cal.set(ns.endYear, 11, 1)
        cal.set(ns.endYear, 11, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
//        ns.endDate = cal.getTime
//        ns.sliceType = sliceType(ns.windowType, ns.rankingType).id
        networkSlice update ns
        for (c <- from(cluster)(cl => where(cl.sliceId === ns.id) select (cl))) {
//          c.beginDate = ns.beginDate
//          c.endDate = ns.endDate
          cluster update c
        }
      }
    }
  }

  private def sliceType(windowType: String, rankingType: String) = {
    windowType + rankingType match {
      case "s3o2cocitation" => S3O2_COCITATION
      case "s3o2cocit" => S3O2_COCIT
      case "s2o1cocitation" => S2O1_COCITATION
      case "s2o1cocit" => S2O1_COCIT
    }
  }

  def main(args: Array[String]) {
    update(args(0))
  }
}*/
