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

package ie.deri.uimr.crosscomanalysis.db.tables.csx

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/01/11
 * Time: 15:02
 * ©2011 Digital Enterprise Research Institute, NUI Galway. 
 */
/*
| id      | bigint(20) unsigned | NO   | PRI | NULL    | auto_increment |
| cluster | bigint(20) unsigned | YES  | MUL | NULL    |                |
| name    | varchar(100)        | NO   | MUL | NULL    |                |
| affil   | varchar(255)        | YES  |     | NULL    |                |
| address | varchar(255)        | YES  |     | NULL    |                |
| email   | varchar(100)        | YES  |     | NULL    |                |
| ord     | int(11)             | NO   |     | NULL    |                |
| paperid | varchar(100)        | NO   | MUL | NULL    |                |
 */
class Authors(val id: Long, val cluster: Long, val name: String, val affil: Option[String],
              val address: Option[String], val email: Option[String], val paperid: String) {
  def this() = this (-1, -1, "", Some(""), Some(""), Some(""), "")

  override def equals(that: Any) = {
    that match {
      case t: Authors => t.name.trim.equalsIgnoreCase(this.name.trim) && canEqual(t)
      case _ => false
    }
  }

  protected def canEqual(that: Any) = that.isInstanceOf[Authors]

  override def hashCode = this.name.trim.toLowerCase.hashCode
}