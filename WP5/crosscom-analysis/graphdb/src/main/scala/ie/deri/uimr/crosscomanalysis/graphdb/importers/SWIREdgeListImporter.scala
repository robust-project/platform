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

package ie.deri.uimr.crosscomanalysis.graphdb.importers

import java.io.{FileFilter, File}
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph
import io.Source
import org.neo4j.kernel.EmbeddedGraphDatabase
import com.tinkerpop.blueprints.pgm.TransactionalGraph
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphManager

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/02/2011
 * Time: 18:12
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object SWIREdgeListImporter extends GraphImporter with GraphManager {

  def importDir(dir: File, rankingType: String, windowType: String) {
    implicit val graph = createNeo4jTransactionalGraph("swir-" + rankingType + "-" + windowType, EmbeddedGraphDatabase.loadConfigurations("neo4j.properties"))
    graph.setMaxBufferSize(0)

    for (edgeList <- dir.listFiles(new FileFilter {
      def accept(f: File) = f.getName.matches(".+\\.edges")
    })) {
      val tokens = edgeList.getName.split("\\.")
      val Array(begin, end) = rankingType match {
        case "cocit" => tokens(3).split("-")
        case "cocitation" => tokens(2).split("-")
      }
      println("Beginning with import of  " + edgeList)
      graphTransaction {
        for (line <- Source.fromFile(edgeList).getLines) {
          val Array(source, sink, weight) = line.split("\\s")
          val sourceV = retrieveOrCreateVertex(source.toLong)
          sourceV.setProperty(ORIGID.toString, source.toLong)
          sourceV.setProperty(begin, true)
          val sinkV = retrieveOrCreateVertex(sink.toLong)
          sinkV.setProperty(ORIGID.toString, sink.toLong)
          sinkV.setProperty(begin, true)
          val edge = graph.addEdge(newEdgeId, sourceV, sinkV, COCITED.toString)
          edge.setProperty(begin, true)
          val w = rankingType match {
            case "cocitation" => weight.toLong
            case "cocit" => weight.toDouble
          }
          edge.setProperty(WEIGHT.toString, w)
        }
      }
      println(edgeList + " has been imported")
    }
    graph.shutdown
  }

  def main(args: Array[String]) {
    importDir(new File(args(0)), args(1), args(2))
  }
}