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

package ie.deri.uimr.crosscomanalysis.db.squeryl.adapters
import org.squeryl.adapters.{PostgreSqlAdapter => PSQLAdapter}
import org.squeryl.internals.StatementWriter
import java.sql.{PreparedStatement, Connection}
import org.squeryl.Session
import java.util.Properties
import collection.Map

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/05/2011
 * Time: 09:40
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class PostgreSqlAdapter(private var conf: Map[String, AnyRef]) extends PSQLAdapter {

  def this() = this(Map.empty)

  override def executeQuery(s: Session, sw: StatementWriter) = exec(s, sw) { params =>
    val st = s.connection.prepareStatement(sw.statement)
    import DatabaseAdapterConf._

    if (conf.contains(FETCH_SIZE)) st.setFetchSize(conf(FETCH_SIZE).asInstanceOf[Int])
    else st.setFetchSize(100)

    fillParamsInto(params, st)
    (st.executeQuery, st)
  }
}

object DatabaseAdapterConf {
  val FETCH_SIZE = "fetch_size"
}