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

import ie.deri.unlp.expertisemining.core.nlp.TopicExtractorImpl
import ie.deri.uimr.crosscomanalysis.util.Config.config
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.doi
import ie.deri.uimr.crosscomanalysis.util.{ArgsParser, Logging}
import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/01/11
 * Time: 17:46
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 *
 * Mine the keywords from the corpus and associates them with the new id which is generated sequentially.
 */

object RenumberingCorpusProcessor extends DBArgsParser with SessionFactorySetter with Logging {

  override val COMMAND_NAME = "mine-keywords-renum"

  override def main(args:Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))

      val minersCount = config.get("minersCount").get.toInt
      System.setProperty("gate.home", config.get("gate.home").get)
      System.setProperty("em.GatePoolSize", config.get("minersCount").get)

      val te = new TopicExtractorImpl

      var lastArticleID = transaction {
        val maxID = (from(doi)(d => compute(max(d.articleid)))).single.measures
        if (maxID.isDefined) {
          log.info("Setting " + maxID.get + " as an initial articleid")
          maxID.get
        } else -1l
      }

      def incrementAndReturnID(s: String) = {
        lastArticleID += 1
        lastArticleID
      }

      val articleIdActor = new ArticleIDActor(incrementAndReturnID)

      val miners = for (i <- 1 to minersCount) yield new MinerActor(te, articleIdActor)

      articleIdActor.start
      DBActor.start
      KeywordIDActor.start
      WalkerActor.start
      val controller = new ControllerActor(miners, articleIdActor)
      miners.foreach(_.start)
      controller.start
      miners.foreach(_ ! MineXFiles(DBActor.minerBatchSize))
    }
  }

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: Nil
}
