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

import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.util.Config.config
import java.io.File
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.doi


/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 22/11/2011
 * Time: 16:49
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * If the out-dir parameter is set, it moves the already mined documents to that dir, otherwise it deletes them.
 */
object CorpusCleaner extends SessionFactorySetter with DBArgsParser with Logging {
  override val COMMAND_NAME = "clean-corpus"

  def clean(db: String = getOptValue(DB_OPT), corpus: File = new File(config("corpus")), dir: Option[File] = outDir) {
    setUpSessionFactory(db)
    transaction {
      for (name <- from(doi)(d => select(d.doi))) {
        val f = new File(corpus,name + ".txt")
        if (dir.isDefined) {
          // move the file
          if (f.renameTo(new File(outDir.get,f.getName)))
            log.info("File " + f.getAbsolutePath + "was successfuly moved")
          else
            log.error("File " + f.getAbsolutePath + " was not moved")
        } else {
          // delete the file
          if (f.delete())
            log.info("File " + f.getAbsolutePath + " deleted")
          else
            log.error("File " + f.getAbsolutePath + " could not be deleted")
        }
      }
    }
  }

  override def main(args: Array[String]) {
    mainStub(args) {
      clean()
    }
  }

  private def outDir =
    if (hasOption(DIR_OPT))
      Some(new File(getOptValue(DIR_OPT)))
    else
      None

  override protected def commandLineOptions = DIR_OPT :: DB_OPT :: HELP_OPT :: Nil
}