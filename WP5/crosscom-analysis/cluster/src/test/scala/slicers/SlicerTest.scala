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

package slicers

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import ie.deri.uimr.crosscomanalysis.cluster.slicers.Slicer
import java.util.Date
import ie.deri.uimr.crosscomanalysis.cluster.TimePeriodTypes

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 27/09/2011
 * Time: 17:47
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

@RunWith(classOf[JUnitRunner])
class SlicerTest extends FunSuite {

//  test("Functionality check of the Slicer superclass") {
//    var slicer = new Slicer {
//      protected def createSlice(beginSlice: Date, endSlice: Date, sliceType: Int) {}
//
//      protected def interval = (new Date(80, 0, 1), new Date(5, 11, 31))
//    }
//    slicer.slice("boardsie", 1, 0, TimePeriodTypes.withName("year"))
//    slicer.slice("boardsie", 2, 1, TimePeriodTypes.withName("year"))
//    slicer.slice("boardsie", 2, 1, TimePeriodTypes.withName("month"))
//    // create another instance which begins with at the beginning of a week and ends at the end of the week
//    // i.e. 7.1.1980-30.12.1981
//    slicer = new Slicer {
//      protected def createSlice(beginSlice: Date, endSlice: Date, sliceType: Int) {}
//
//      protected def interval = (new Date(80, 0, 7), new Date(81, 11, 30))
//    }
//    slicer.slice("boardsie", 2, 1, TimePeriodTypes.withName("week"))
//    slicer.slice("boardsie", 2, 1, TimePeriodTypes.withName("day"))
//  }

}