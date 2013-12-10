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

package ie.deri.uimr.crosscomanalysis.db.schemas

import org.squeryl.Schema
import ie.deri.uimr.crosscomanalysis.db.tables.csxcitegraph.{CitegraphWithYears, Citegraph, Clusters, Papers}
import ie.deri.uimr.crosscomanalysis.db.tables.csx.Authors

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 10/01/11
 * Time: 14:54
 * ©2011 Digital Enterprise Research Institute, NUI Galway. 
 */

object CSXCitegraphSchema extends Schema {
  val papers = table[Papers]("papers")
  val clusters = table[Clusters]("clusters")
  val citegraph = table[Citegraph]("citegraph")
  val citegraphWithYears = table[CitegraphWithYears]("citegraph_with_years")
  val authors = table[Authors]("authors")
}