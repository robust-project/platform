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

import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import collection.mutable.MutableList
import ie.deri.uimr.crosscomanalysis.db.tables.csxcitegraph.Clusters
import java.io.File
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CSXCitegraphSchema._
import ie.deri.uimr.crosscomanalysis.util.Config

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 23/03/2012
 * Time: 17:03
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

object CSXBookTitleMatcher extends SessionFactorySetter with SearchTitle with DBArgsParser {

  override val COMMAND_NAME = "match-booktitle"

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val batch = new MutableList[Clusters]
      val loader = new SearchTitleLoader(new File(Config.config.get("dblp.orig").get))
      log.info("Loading stitle map")
      loader.load()
      log.info("Loading finished")

      var count = 0
      log.info("Starting the matching process")
      transaction {
        for (cluster <- from(clusters)(c => where(c.cbooktitle.isNull and c.ctitle.isNotNull) select (c)) if cluster.ctitle.trim.length > 0) {
          val title = loader.findBookTitleInDBLP(createSearchTitle(cluster.ctitle))
          if (title.isDefined) {
            log.info("Adding title " + title.get + " to cluster '" + cluster.ctitle + "'")
            cluster.cbooktitle = Some(title.get)
            count += 1
          } else {
            log.info("The title for cluster '" + cluster.ctitle + "' not found")
          }
          batch += cluster
          if (batch.size >= 1000) {
            transaction {
              batch.foreach(clusters update _)
            }
            log.info("Storing " + batch.size + " clusters' titles")
            batch.clear()
          }
        }
      }
      if (!batch.isEmpty) {
        transaction {
          batch.foreach(clusters update _)
        }
        log.info("Storing " + batch.size + " clusters' titles")
        batch.clear()
      }
      log.info(count + " titles found")
    }
  }

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: Nil
}
