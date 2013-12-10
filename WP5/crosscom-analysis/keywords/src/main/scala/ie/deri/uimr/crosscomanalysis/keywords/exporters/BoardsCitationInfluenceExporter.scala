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
import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE._
import java.io.{PrintWriter, File}
import org.squeryl.PrimitiveTypeMode._
import org.apache.commons.cli
import java.util.Date
import java.text.{ParsePosition, SimpleDateFormat}


/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/03/2013
 * Time: 16:29
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 *
 * Exports data from boards schema to format required by Citation Influence Model:

 * https://github.com/bgamari/bayes-stack/blob/stable/docs/usage.markdown
 */
object BoardsCitationInfluenceExporter extends CitationInfluenceExporter with SessionFactorySetter {
  private val BEGIN_DATE_OPT = new cli.Option("bd", "beginDate", true, "Begin date (DD.MM.YYYY)")
  private val END_DATE_OPT = new cli.Option("ed", "endDate", true, "End date (DD.MM.YYYY)")

  override val COMMAND_NAME = "export-boards-ci"

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val df = new SimpleDateFormat("dd.MM.yyyy")
      val beginDate = df.parse(getOptValue(BEGIN_DATE_OPT), new ParsePosition(0))
      val endDate = df.parse(getOptValue(END_DATE_OPT), new ParsePosition(0))
      export(beginDate, endDate)
    }
  }

  def export(beginDate: Date, endDate: Date) {
    val outDir = new File(getOptValue(DIR_OPT))
    outDir.mkdirs()
    // export posts (content)
    val contentFile = new File(outDir, "posts.dat")
    val exportedPosts =
      transaction {
        // export original posts (sinks)
        exportContent(contentFile, from(posts, replies)((p, r) =>
          where((p.postid === r.originalpostid or p.postid === r.replyingpostid)
            and r.origposteddate >= beginDate and r.origposteddate <= endDate and
            r.replyingposteddate >= beginDate and r.replyingposteddate <= endDate)
            select ((p.postid, p.title, p.content))).distinct)
      }
    log.info("Exported " + exportedPosts.size + " posts altogether.")
    // export replies (graph)
    val repliesOut = new PrintWriter(new File(outDir, "replies.dat"))
    var c = 0
    var nodesWithLinks = Set.empty[Long]
    transaction {
      for ((replying, replied) <-
           from(replies)(r =>
             where(r.origposteddate >= beginDate and r.origposteddate <= endDate and
               r.replyingposteddate >= beginDate and r.replyingposteddate <= endDate and
               r.originalpostid <> r.replyingpostid)
               select ((r.replyingpostid, r.originalpostid)))
           if exportedPosts.contains(replying) && exportedPosts.contains(replied)) {
        repliesOut.printf("%s\t%s\n", replying.toString, replied.toString)
        nodesWithLinks ++= Set(replying, replied)
        c += 1
        if (c % 1000 == 0) log.info("Processed " + c + " replies.")
      }
    }
    repliesOut.close()
    log.info("Processed " + c + " replies (edges) altogether.")
    log.info("Number of filtered nodes with content: " + filterContent(contentFile, nodesWithLinks))
  }

  override protected def commandLineOptions = BEGIN_DATE_OPT :: END_DATE_OPT :: super.commandLineOptions
}