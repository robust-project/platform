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

package ie.deri.uimr.crosscomanalysis.keywords.keywordmining

import ie.deri.uimr.crosscomanalysis.util.Logging
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import ie.deri.uimr.crosscomanalysis.util.Config.config
import tokyocabinet._
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 21/01/2011
 * Time: 09:57
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object KeywordIDDBBuilder extends Logging with SessionFactorySetter with DBArgsParser {

  override val COMMAND_NAME = "build-keyiddb"

  override def main(args:Array[String]) {
    mainStub(args) {
      rebuild()
    }
  }

  def rebuild(db:String = getOptValue(DB_OPT)) {
    setUpSessionFactory(db)
    val hdb = new HDB

    if (!hdb.open(config.get("keyidactor.db.path").get, HDB.OWRITER | HDB.OTRUNC | HDB.OCREAT))
      error("Error opening " + hdb.errmsg)
    if (!hdb.vanish)
      error("Error wanishing db: " + hdb.errmsg)

    log.info("DB opened, beginning with import")
    transaction {
      val pageLength = config.get("keyiddbbuilder.pageLength").get.toLong
      val total = from(keywordIdEMCSX)(kid => compute(countDistinct(kid.id))).single.measures
      val maxIdOpt = from(keywordIdEMCSX)(kid => compute(max(kid.id))).single.measures
      if (maxIdOpt.isDefined) {
        val maxId = maxIdOpt.get
        var count = 0l
        for (i <- 0l to (maxId / pageLength); (id, stemmed) <- getPairs(i * pageLength, (i + 1) * pageLength - 1)) {
          if (hdb.get(stemmed) != null) error("Duplicate pair: '" + stemmed + "'->" + id)
          if (!hdb.put(stemmed, id.toString)) error("Error saving pair '" + stemmed + "'->" + id + ", reason: " + hdb.errmsg)
          count += 1
          if (count % 10000 == 0) {
            log.info("Loaded " + count * 100d / total + "% of pairs")
            hdb.trancommit
          }
        }
        assert(total == count, "Number of loaded has to be the same as number of total")
        hdb.trancommit
        log.info("Saved " + count)
      }
    }

    if (!hdb.close)
      log.error("Error closing " + hdb.errmsg)
    log.info("Finished loading keyword id db")
  }

  private def getPairs(offset: Long, pageLength: Long) = {
    val query = from(keywordIdEMCSX)(kid => where(kid.id.between(offset, pageLength)) select ((kid.id, kid.keyword)))
    log.debug(query.statement)
    query
  }

  override protected def commandLineOptions = DB_OPT :: Nil
}