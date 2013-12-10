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

package ie.deri.uimr.crosscomanalysis.cluster.slicers

import ie.deri.uimr.crosscomanalysis.db.DBArgsParser
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 03/06/2011
 * Time: 18:30
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Infers a range of slices for CSX data-set.
 */

object CSXRangeSlicer extends DBArgsParser {
  override val COMMAND_NAME = "csx-range-slicer"

  override def main(args:Array[String]) {
    mainStub(args) {
      val db = getOptValue(DB_OPT)
      CSXSlicer.slice(db, 3, 2, S3O2_COCIT.id, false)
      CSXSlicer.slice(db, 3, 1, S3O1_COCIT.id, false)
      CSXSlicer.slice(db, 3, 0, S3O0_COCIT.id, false)
      CSXSlicer.slice(db, 2, 1, S2O1_COCIT.id, false)
      CSXSlicer.slice(db, 2, 0, S2O0_COCIT.id, false)
      CSXSlicer.slice(db, 1, 0, S1O0_COCIT.id, true)
    }
  }

  override protected def commandLineOptions = DB_OPT :: Nil
}