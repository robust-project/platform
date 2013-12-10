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

package ie.deri.uimr.crosscomanalysis.keywords.exporters

import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import ie.deri.uimr.crosscomanalysis.db.schemas.TiddlyWikiSchema._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.util.Config.config
import org.squeryl.PrimitiveTypeMode._
import java.io.{FileWriter, File}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/03/2011
 * Time: 15:02
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object TiddlywikiExporter extends SessionFactorySetter {

  def export(db: String, sliceType: Int, outdir: File) {
    setUpSessionFactory(db)

    transaction {
      for ((sliceId, beginDate, endDate) <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select ((ns.id, ns.beginDate, ns.endDate)))) {
        val sliceDir = new File(outdir.getAbsolutePath + File.separator + sliceId)
        sliceDir.mkdir
        for ((mailid, text) <- from(mail)(m => where(m.sentDate >= Some(beginDate) and m.sentDate <= Some(endDate)) select ((m.id, m.content))) if text.isDefined) {
          val out = new FileWriter(sliceDir.getAbsolutePath + File.separator + mailid + ".txt")
          out.write(text.get)
          out.close
        }
      }
    }
  }

  def main(args: Array[String]) {
    export(args(0), SliceTypes.withName(args(1)).id, new File(args(2)))
  }
}