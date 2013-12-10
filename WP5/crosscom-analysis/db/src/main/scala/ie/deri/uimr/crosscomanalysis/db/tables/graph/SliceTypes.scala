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

package ie.deri.uimr.crosscomanalysis.db.tables.graph

import ie.deri.uimr.crosscomanalysis.util.RichEnumeration

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07/03/2011
 * Time: 15:54
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object SliceTypes extends RichEnumeration {
  type SliceTypes = Value
  // SWIR types
  val S3O2_COCITATION = Value(0, "s3yo2_cocitation")
  val S3O2_COCIT = Value(1, "s3yo2_cocit")
  val S3O1_COCIT = Value(7, "s3yo1_cocit")
  val S3O0_COCIT = Value(9, "s3yo0_cocit")
  val S2O1_COCITATION = Value(2, "s2yo1_cocitation")
  val S2O1_COCIT = Value(3, "s2yo1_cocit")
  val S2O0_COCIT = Value(8, "s2yo0_cocit")
  val S1O0_COCIT = Value(10, "s1yo0_cocit")
  // tiddly wiki types
  val S1YO0 = Value(4, "s1yo0") // slice of 1 year with 0 overlap
  val S6MO0 = Value(5, "s6mo0") // slice of 6 months with 0 overlap
  val S3MO0 = Value(6, "s3mo0")

  val S2YO1 = Value(11, "s2yo1")
  val S2MO1 = Value(12, "s2mo1")
  val S2WO1 = Value(13, "s2wo1")
  val S2DO1 = Value(14, "s2do1")

  val S1MO0 = Value(15, "s1mo0")
  val S1WO0 = Value(16, "s1wo0")
  val S2WO0 = Value(17, "s2wo0")

  val S1WO0_CG_UNDIR_JC = Value(18, "s1wo0_cg_undir_jc") // undirected comm. graph weighed by Jaccard sim between the vertex sets
  val S1WO0_CG_UNDIR_OVERLAP = Value(19, "s1wo0_cg_undir_overlap") // undirected comm. graph weighted by the size of the vertex intersection
  val S1WO0_CG_DIR = Value(20, "s1wo0_cg_dir") // directed comm. graph
  val S1WO0_CG_DIR_SL = Value(21, "s1wo0_cg_dir_sl") // directed comm. graph with self-loops
  
  val S60DO0 = Value(22, "s60do0")
  val S2MO0 = Value(23, "s2mo0")

  val S3YO2 = Value(24, "s3yo2")

  val S3YO2_CA = Value(25, "s3yo2_ca") // coauthorship type of network
  val S2YO1_CA = Value(26, "s2yo1_ca")
  val S1YO0_CA = Value(27, "s1yo0_ca")
  val S2YO0_CA = Value(28, "s2yo0_ca")

  val S4YO3 = Value(29, "s4yo3")
  val S4YO2 = Value(30, "s4yo2")
  val S4YO1 = Value(31, "s4yo1")
}