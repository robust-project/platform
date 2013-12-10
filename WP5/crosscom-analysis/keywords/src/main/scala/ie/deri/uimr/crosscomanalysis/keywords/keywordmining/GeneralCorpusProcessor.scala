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
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.doi
import ie.deri.uimr.crosscomanalysis.db.{SessionFactorySetter, DBArgsParser}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/03/2011
 * Time: 17:10
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 *
 * Mines the keywords and stores them under the original id of the document (name of the file).
 */


object GeneralCorpusProcessor extends Logging with DBArgsParser with SessionFactorySetter {

  override val COMMAND_NAME = "mine-keywords-origid"

  private val minersCount = config.get("minersCount").get.toInt
  System.setProperty("gate.home", config.get("gate.home").get)
  System.setProperty("em.GatePoolSize", config.get("minersCount").get)

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val te = new TopicExtractorImpl
      val articleIdActor = new ArticleIDActor((s:String) => s.toLong)
      val miners = for (i <- 1 to minersCount) yield new MinerActor(te, articleIdActor)
      articleIdActor.start()
      DBActor.start()
      KeywordIDActor.start()
      WalkerActor.start()
      val controller = new ControllerActor(miners, articleIdActor)
      miners.foreach(_.start())
      controller.start()
      miners.foreach(_ ! MineXFiles(DBActor.minerBatchSize))
    }
  }

  override protected def commandLineOptions = DB_OPT :: Nil
}