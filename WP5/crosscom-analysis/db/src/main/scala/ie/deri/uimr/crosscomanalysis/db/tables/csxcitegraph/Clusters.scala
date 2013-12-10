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

package ie.deri.uimr.crosscomanalysis.db.tables.csxcitegraph

import org.squeryl.KeyedEntity

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/01/11
 * Time: 14:35
 * ©2011 Digital Enterprise Research Institute, NUI Galway. 
 */

/*
| id           | bigint(20) unsigned | NO   | PRI | NULL              | auto_increment |
| size         | int(10) unsigned    | YES  | MUL | NULL              |                |
| incollection | tinyint(4)          | NO   | MUL | 0                 |                |
| cauth        | text                | YES  |     | NULL              |                |
| ctitle       | varchar(255)        | YES  | MUL | NULL              |                |
| cvenue       | varchar(255)        | YES  | MUL | NULL              |                |
| cventype     | varchar(20)         | YES  | MUL | NULL              |                |
| cyear        | int(10) unsigned    | YES  | MUL | NULL              |                |
| cpages       | varchar(20)         | YES  |     | NULL              |                |
| cpublisher   | varchar(100)        | YES  |     | NULL              |                |
| cvol         | int(10) unsigned    | YES  |     | NULL              |                |
| cnum         | int(10) unsigned    | YES  |     | NULL              |                |
| ctech        | varchar(100)        | YES  |     | NULL              |                |
| observations | mediumtext          | YES  |     | NULL              |                |
| selfCites    | int(10) unsigned    | NO   |     | 0                 |                |
| updated      | timestamp           | NO   | MUL | CURRENT_TIMESTAMP |                |

cbooktitle was added as a result of a CSXBootTitleMatcher process (matching against DBLP)
venue was added as a result of a cleaning up and collapsing cbooktitle into abbreviations
 */
class Clusters(val id: Long, val cauth: String, val ctitle: String, val cvenue: String, var cyear: Option[Int],
               val cpublisher: String, var cbooktitle: Option[String], var venue: Option[String]) extends KeyedEntity[Long] {
  def this() = this(-1l, "", "", "", Some(-1), "", None, None)
}
