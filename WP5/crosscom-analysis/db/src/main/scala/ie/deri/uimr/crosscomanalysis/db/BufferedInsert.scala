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

import org.squeryl.Table
import scala.Option
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.squeryl.PrimitiveTypeMode._
import java.sql.BatchUpdateException

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 24/04/2013
 * Time: 12:30
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
trait BufferedInsert {

  this: Logging =>

  /**
   * Inserts data from the iterator (after processing) in batches of bufferSize.
   * @param data Main data loop
   * @param transform Mapping from each datum to a table object
   * @param table Table to insert to
   * @param message Optional logging message, e.g. 'Number of items inserted thus far: '
   * @param bufferSize Size of the buffer
   * @tparam D Datum type
   * @tparam T Table type
   */
  protected def bufferedInsert[D, T](data: Iterable[D], table: Table[T], message: Option[String],
                                     bufferSize: Int = 1000)(transform: D => T) {
    try {
      var buffer = List.empty[T]
      for (datum <- data) {
        buffer = transform(datum) :: buffer
        if (buffer.size == bufferSize) {
          if (message.isDefined) log.info(message.get + bufferSize)
          transaction {
            table insert buffer
          }
          buffer = List.empty[T]
        }
      }
      if (!buffer.isEmpty) {
        if (message.isDefined) log.info(message.get + buffer.size)
        transaction {
          table insert buffer
        }
      }
    } catch {
      case e: BatchUpdateException => {
        log.error(e.getNextException)
        throw e
      }
    }
  }

}
