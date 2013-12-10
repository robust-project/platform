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

import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.graphdb.DBGraphLoader
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions.{blueprintsGraphToJUNG, mapToTransformer}
import edu.uci.ics.jung.io.PajekNetWriter
import com.tinkerpop.blueprints.pgm.{Edge, Vertex}
import ie.deri.uimr.crosscomanalysis.util.Functions.filePath
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.apache.commons.collections15.Transformer
import java.io.{PrintWriter, File}
import io.Source
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/03/2011
 * Time: 11:01
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object PajekExporter extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser {


  override val COMMAND_NAME = "pajek-export"

  def export(db: String, outDir: File, sliceType: Int) {
    setUpSessionFactory(db)
    transaction {
      for (netSlice <- from(networkSlice)(ns => where(ns.sliceType === sliceType) select (ns) orderBy (ns.beginDate).asc)) {
        val graphLoader = new DBGraphLoader(netSlice.id)
        val pnw = new PajekNetWriter[Vertex, Edge]
        val fileName = filePath(outDir, "", netSlice.beginDate, netSlice.endDate, ".unfiltered.net")
        pnw.save(blueprintsGraphToJUNG(graphLoader.graph), fileName, new Transformer[Vertex, String] {
          def transform(v: Vertex) = v.getId.toString
        }, mapToTransformer(graphLoader.weights.mapValues(_.asInstanceOf[java.lang.Number])))
        filterStarredMetaData(fileName, filePath(outDir, "", netSlice.beginDate, netSlice.endDate, ".filtered.net"))
        log.info("Slice " + netSlice + " exported to " + fileName)
      }
    }
  }

  private def filterStarredMetaData(source: String, target: String) {
    val out = new PrintWriter(target)
    for (line <- Source.fromFile(source).getLines if !line.startsWith("*")) {
      out.println(line)
    }
    out.close
  }

  override def main(args: Array[String]) {
    parseArgs(args)
    if (!printHelpIfAsked)
      export(getOptValue(DB_OPT), new File(getOptValue(DIR_OPT)), getSliceType)
  }

  override protected def commandLineOptions = DB_OPT :: DIR_OPT :: SLICE_TYPE_OPT :: Nil
}