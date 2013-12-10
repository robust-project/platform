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

package ie.deri.uimr.crosscomanalysis.db

import org.squeryl.{SessionFactory, Session}
import ie.deri.uimr.crosscomanalysis.util.Config.config
import org.squeryl.internals.DatabaseAdapter
import squeryl.adapters.{MysqlAdapter, PostgreSqlAdapter}
import ie.deri.uimr.crosscomanalysis.util.Logging
import java.sql.DriverManager

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 31/01/2011
 * Time: 12:55
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait SessionFactorySetter extends Logging {

  private val SQUERYL_LOGGER_PROP = "squeryl.logging"

  protected def setUpSessionFactory(dbUrl: String, dbUser: String, dbPassword: String, adapter: DatabaseAdapter, autoCommit: Boolean = false) {
    adapter match {
      case _: MysqlAdapter => Class.forName("com.mysql.jdbc.Driver")
      case _: PostgreSqlAdapter => Class.forName("org.postgresql.Driver")
    }

    val sessionClosure = () => {
      val con = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
      con.setAutoCommit(autoCommit)
      val s = Session.create(con, adapter)
      if (isLoggingEnabled) s.setLogger(log.debug(_))
      s
    }
    SessionFactory.concreteFactory = Some(sessionClosure)
  }

  protected def setUpSessionFactory(db: String) {
    val dbUrl = config.get("db." + db + ".url").get
    val adapter =
      if (dbUrl.matches(".*mysql.*"))
        new MysqlAdapter
      else if (dbUrl.matches(".*postgresql.*"))
        new PostgreSqlAdapter
      else sys.error("Unknown database")
    setUpSessionFactory(dbUrl, config.get("db." + db + ".user").get,
      config.get("db." + db + ".pass").get, adapter)
  }

  protected def setUpSessionFactory(db: String, adapter: DatabaseAdapter) {
    setUpSessionFactory(config.get("db." + db + ".url").get, config.get("db." + db + ".user").get,
      config.get("db." + db + ".pass").get, adapter)
  }

  private def isLoggingEnabled = System.getProperty(SQUERYL_LOGGER_PROP, "false").toBoolean
}