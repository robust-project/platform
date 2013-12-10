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

package ie.deri.uimr.crosscomanalysis.dblpcsx

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CSXCitegraphSchema.clusters
import collection.mutable.MutableList
import ie.deri.uimr.crosscomanalysis.db.tables.csxcitegraph.Clusters
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import java.io.File
import ie.deri.uimr.crosscomanalysis.util.Config.config

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 28/01/2011
 * Time: 19:05
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object CSXYearMatcher extends SessionFactorySetter with SearchTitle with DBArgsParser {

  override val COMMAND_NAME = "match-year"

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val batch = new MutableList[Clusters]
      val loader = new SearchTitleLoader(new File(config.get("dblp.orig").get))
      log.info("Loading stitle map")
      loader.load()
      log.info("Loading finished")

      var count = 0
      log.info("Starting the matching process")
      transaction {
        for (cluster <- from(clusters)(c => where(c.cyear.isNull and c.ctitle.isNotNull) select (c)) if cluster.ctitle.trim.length > 0) {
          val year = loader.findYearInDBLP(createSearchTitle(cluster.ctitle))
          if (year.isDefined) {
            log.info("Adding year " + year.get + " to cluster '" + cluster.ctitle + "'")
            cluster.cyear = Some(year.get)
            count += 1
          } else {
            log.info("The year for cluster '" + cluster.ctitle + "' not found - setting to 0")
            cluster.cyear = Some(0)
          }
          batch += cluster
          if (batch.size >= 1000) {
            transaction {
              batch.foreach(clusters update _)
            }
            log.info("Storing " + batch.size + " clusters' years")
            batch.clear()
          }
        }
      }
      if (!batch.isEmpty) {
        transaction {
          batch.foreach(clusters update _)
        }
        log.info("Storing " + batch.size + " clusters' years")
        batch.clear()
      }

      log.info(count + " years found")
    }
  }

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: Nil
}