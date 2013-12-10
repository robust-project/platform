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

package ie.deri.uimr.crosscomanalysis.util

import java.util.logging.{LogRecord, Formatter, Level, Logger}
import java.util.Date
import java.io.{ByteArrayOutputStream, PrintStream}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 18/01/2011
 * Time: 17:56
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait Logging {
  if (System.getProperty("java.util.logging.config.file") == null)
    System.setProperty("java.util.logging.config.file", "logging.properties")

  protected val log = new LoggerWrapper(Logger.getLogger(this.getClass.getName))
}

protected class LoggerWrapper(private val log: Logger) {
  def info(message: => String) {
    if (log.isLoggable(Level.INFO)) log.info(message)
  }

  def error(message: => String, t: Throwable) {
    if (log.isLoggable(Level.SEVERE)) log.log(Level.SEVERE, message, t)
  }

  def error(obj: AnyRef, t: Throwable) {
    if (log.isLoggable(Level.SEVERE)) log.log(Level.SEVERE, obj.toString, t)
  }

  def error(message: => String) {
    if (log.isLoggable(Level.SEVERE)) log.log(Level.SEVERE, message)
  }

  def error(t: Throwable) {
    if (log.isLoggable(Level.SEVERE)) log.log(Level.SEVERE, t.getMessage, t)
  }

  def debug(message: => String) {
    if (log.isLoggable(Level.FINEST)) log.log(Level.FINEST, message)
  }

  def warn(message: => String) {
    if (log.isLoggable(Level.WARNING)) log.warning(message)
  }

  def name = log.getName
}

class LogFormatter extends Formatter {
  def format(record: LogRecord) = formatMessage(record)

  override def formatMessage(record: LogRecord) = {
    val sb = new StringBuffer(new Date(record.getMillis).toString)
    sb.append(" ")
    sb.append(record.getLoggerName)
    sb.append("\n")
    sb.append(record.getLevel.toString)
    sb.append(": ")
    sb.append(record.getMessage)
    sb.append("\n")
    val t = record.getThrown
    if (t != null) {
      sb.append("Stacktrace: ")
      val bos = new ByteArrayOutputStream()
      val ps = new PrintStream(bos)
      t.printStackTrace(ps)
      ps.close
      sb.append(bos.toString)
      bos.close
      sb.append("\n")
    }
    sb.toString
  }
}