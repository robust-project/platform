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

import org.squeryl.Session
import org.squeryl.internals.StatementWriter
import org.squeryl.adapters.MySQLAdapter
import java.sql.ResultSet

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/03/2013
 * Time: 18:46
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
class MysqlAdapter(val streaming: Boolean = false) extends MySQLAdapter {

  override def executeQuery(s: Session, sw: StatementWriter) = exec(s, sw) {
    params =>
      val st =
        if (streaming)
          s.connection.prepareStatement(sw.statement, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
        else
          s.connection.prepareStatement(sw.statement)
      if (streaming)
        st.setFetchSize(Integer.MIN_VALUE)

      fillParamsInto(params, st)
      (st.executeQuery, st)
  }
}
