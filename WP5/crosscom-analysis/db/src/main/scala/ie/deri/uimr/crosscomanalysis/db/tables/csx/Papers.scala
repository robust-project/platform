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

import java.sql.Timestamp

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/01/11
 * Time: 15:02
 * ©2011 Digital Enterprise Research Institute, NUI Galway. 
 */
/*
| id              | varchar(100)        | NO   | PRI | NULL                |       |
| version         | int(10) unsigned    | NO   | MUL | NULL                |       |
| cluster         | bigint(20) unsigned | YES  | MUL | NULL                |       |
| title           | varchar(255)        | YES  | MUL | NULL                |       |
| abstract        | text                | YES  |     | NULL                |       |
| year            | int(11)             | YES  | MUL | NULL                |       |
| venue           | varchar(255)        | YES  |     | NULL                |       |
| venueType       | varchar(20)         | YES  |     | NULL                |       |
| pages           | varchar(20)         | YES  |     | NULL                |       |
| volume          | int(11)             | YES  |     | NULL                |       |
| number          | int(11)             | YES  |     | NULL                |       |
| publisher       | varchar(100)        | YES  |     | NULL                |       |
| pubAddress      | varchar(100)        | YES  |     | NULL                |       |
| tech            | varchar(100)        | YES  |     | NULL                |       |
| public          | tinyint(4)          | NO   |     | 1                   |       |
| ncites          | int(10) unsigned    | NO   | MUL | 0                   |       |
| versionName     | varchar(20)         | YES  | MUL | NULL                |       |
| crawlDate       | timestamp           | NO   | MUL | CURRENT_TIMESTAMP   |       |
| repositoryID    | varchar(15)         | YES  | MUL | NULL                |       |
| conversionTrace | varchar(255)        | YES  |     | NULL                |       |
| selfCites       | int(10) unsigned    | NO   | MUL | 0                   |       |
| versionTime     | timestamp           | NO   | MUL | 0000-00-00 00:00:00 |       |
*/
class Papers(val id: String, val cluster: Long, val title: String, val year: Int, val venue: String, val venueType: String,
             val publisher: String, val ncites: Int, val crawlDate: Timestamp)