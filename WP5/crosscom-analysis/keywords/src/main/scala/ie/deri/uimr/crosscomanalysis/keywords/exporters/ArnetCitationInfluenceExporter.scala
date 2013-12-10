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
import org.squeryl.PrimitiveTypeMode._
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.db.schemas.ArnetSchema._
import org.apache.commons.cli

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 03/04/2013
 * Time: 17:49
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
object ArnetCitationInfluenceExporter extends CitationInfluenceExporter with SessionFactorySetter {
  override val COMMAND_NAME = "export-arnet-ci"

  private val BEGIN_YEAR_OPT = new cli.Option("by", "begin year", true, "Start of the interval")
  private val END_YEAR_OPT = new cli.Option("ey", "end year", true, "End of the interval")

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      export(getOptValue(BEGIN_YEAR_OPT).toInt, getOptValue(END_YEAR_OPT).toInt)
    }
  }

  def export(beginYear: Int, endYear: Int) {
    val outDir = new File(getOptValue(DIR_OPT))
    outDir.mkdirs()
    // export content
    val contentFile = new File(outDir, "papers.dat")
    val exportedPapers =
      transaction {
        // export original posts (sinks)
        exportContent(contentFile, from(paper, citationWithYear)((p, c) =>
          where(p.summary.isNotNull and p.year.between(beginYear, endYear) and
            (c.citing === p.id or c.cited === p.id) and
            c.yearCiting.between(beginYear, endYear) and c.yearCited.between(beginYear, endYear))
            select ((p.id.toLong, p.title, p.summary))).distinct)
      }
    log.info("Exported " + exportedPapers.size + " papers altogether.")
    // export citations (graph)
    val citationsOut = new PrintWriter(new File(outDir, "citations.dat"))
    var c = 0
    var nodesWithLinks = Set.empty[Long]
    var outDegree = Map.empty[Int, Int]
    var inDegree = Map.empty[Int, Int]
    def increment(m: Map[Int, Int], k: Int) = {
      if (!m.contains(k))
        m + (k -> 1)
      else
        m + (k -> (m(k) + 1))
    }
    transaction {
      for ((citing, cited) <-
           from(citationWithYear)(c =>
             where(c.yearCiting.between(beginYear, endYear) and c.yearCited.between(beginYear, endYear) and
             c.citing <> c.cited)
               select ((c.citing, c.cited)))
           if exportedPapers.contains(citing) && exportedPapers.contains(cited)) {
        citationsOut.println(citing + "\t" + cited)
        nodesWithLinks ++= Set(citing.toLong, cited.toLong)
        outDegree = increment(outDegree, citing)
        inDegree = increment(inDegree, cited)
        c += 1
        if (c % 1000 == 0) log.info("Processed " + c + " citations.")
      }
    }
    citationsOut.close()
    log.info("Processed " + c + " citations (edges) altogether.")
    log.info("Number of filtered nodes with content: " + filterContent(contentFile, nodesWithLinks))
    log.info("Average in-degree: " + inDegree.values.sum.toDouble / inDegree.size)
    log.info("Average out-degree: " + outDegree.values.sum.toDouble / outDegree.size)
  }

  override protected def commandLineOptions = BEGIN_YEAR_OPT :: END_YEAR_OPT :: super.commandLineOptions
}
