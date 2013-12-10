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

package ie.deri.uimr.crosscomanalysis.db.importers

import java.util.Properties
import ie.deri.uimr.crosscomanalysis.db.schemas.TiddlyWikiSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.tiddlywiki._
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.util.Config.config
import org.squeryl.PrimitiveTypeMode._
import javax.mail.{Message, Folder, Session, URLName}
import collection.mutable.HashMap
import ie.deri.uimr.crosscomanalysis.util.Logging
import javax.mail.internet.AddressException
import java.io.UnsupportedEncodingException

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 18/02/2011
 * Time: 15:47
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object MBoxImporter extends SessionFactorySetter with Logging {
  setUpSessionFactory("tiddlywiki")

  private val userIds = new HashMap[String, Long]
  private val Email = """([^<]*<)?(.+@[^>]+)(>[^>]*)?""".r

  def importFile(mbox: String) {
    log.debug("Beginning importing file " + mbox)
    val session = Session.getDefaultInstance(new Properties)
    val store = session.getStore(new URLName("mstor:" + mbox))
    store.connect
    val folder = store.getDefaultFolder
    folder.open(Folder.READ_ONLY)
    log.debug("File opened, starting parsing")
    val count = folder.getMessageCount
    log.debug("Total messages to be processed: " + count)
    val step = if (count > 1000) 1000 else count
    transaction {
      for (i <- 1 to (count, step)) {
        val j = if (i + step - 1 > count) count else i + step - 1
        folder.getMessages(i, j).foreach(processMsg)
        log.debug("Processed " + j + " messages")
      }
    }
  }

  private def processMsg(m: Message) {
    try {
      val newMail = new Mail(Some(m.getReceivedDate), Some(m.getSentDate), subject(m), content(m))
      mail insert newMail
      m.getFrom.foreach(s => sender insert new Sender(newMail.id, retrieveOrCreateUser(s.toString)))
      m.getAllRecipients.foreach(r => receiver insert new Receiver(newMail.id, retrieveOrCreateUser(r.toString)))
      val h = m.getAllHeaders
      while (h.hasMoreElements) {
        val nextHeader = h.nextElement.asInstanceOf[javax.mail.Header]
        val headerName = nextHeader.getName
        if (headerName == "Return-Path" ||
          headerName == "Date" ||
          headerName == "Message-ID" ||
          headerName == "In-Reply-To") {
          header insert new Header(newMail.id, headerName, Some(nextHeader.getValue))
        }
      }
    } catch {
      case e => log.error("Error while storing mail #" + m.getMessageNumber, e)
    }
  }

  private def subject(m: Message) = {
    val s = m.getSubject
    if (s == null) None
    else Some(if (s.length > 500) s.substring(0, 500) else s)
  }

  private def content(m: Message) = {
    val Array(mime, _*) = m.getContentType.trim.toLowerCase.split(";")
    mime match {
      case "text/plain" => {
        val c = m.getContent.toString
        Some(if (c.length > 25000) c.substring(0, 25000) else c)
      }
      case _ => {
        log.info("Ignoring content of message " + m.getMessageNumber + " with type " + mime)
        None
      }
    }
  }

  private def retrieveOrCreateUser(name: String) = {
    val Email(_, address, _) = name.trim.toLowerCase
    val ret = userIds.get(address)
    if (ret.isDefined) ret.get
    else {
      val u = new User(address)
      user insert u
      userIds(address) = u.id
      u.id
    }
  }

  def main(args: Array[String]) {
    importFile(args(0))
  }
}