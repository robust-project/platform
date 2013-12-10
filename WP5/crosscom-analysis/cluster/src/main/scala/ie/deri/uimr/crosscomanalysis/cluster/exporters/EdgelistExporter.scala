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

package ie.deri.uimr.crosscomanalysis.cluster.exporters

import ie.deri.uimr.crosscomanalysis.util.Functions.{filePath, offset, denormalize}
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import org.squeryl.PrimitiveTypeMode._
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.util.{ Logging}
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/03/2011
 * Time: 16:58
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 *
 * Exports the network into a simple 'source sink weight' edgelist format required by e.g. OSLOM.
 */

object EdgelistExporter extends SessionFactorySetter with Logging with DBArgsParser {

  override val COMMAND_NAME = "export-edgelist"

  def export(outDir: File, sliceType: Int) {
    transaction {
      val maxSliceId = from(networkSlice) (ns => where(ns.sliceType === sliceType) compute(max(ns.id))).single.measures
      outDir.mkdirs()
      for (netSlice <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns) orderBy (ns.beginDate).asc)) {
        val fileName = filePath(outDir, offset(maxSliceId.get, netSlice.id) + netSlice.id + "_", netSlice.beginDate, netSlice.endDate, ".edges")
        val out = new PrintWriter(fileName)
        for (((source, sink), weight) <- from(networkSliceStructure)(nss => where(nss.sliceId === netSlice.id)
          select(((nss.source, nss.sink), nss.weight))))
          out.println(source + " " + sink + " " + weight)
        out.close()
        log.info("Slice " + netSlice + " exported to " + fileName)
      }
    }
  }

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      export(new File(getOptValue(DIR_OPT)), getSliceType)
    }
  }

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: SLICE_TYPE_OPT :: DIR_OPT :: Nil
}
