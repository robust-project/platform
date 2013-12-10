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

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.DBArgsParser
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.cluster.TimePeriodTypes._
import ie.deri.uimr.crosscomanalysis.cluster.{TimePeriodTypes, ClusterArgsParser}
import ie.deri.uimr.crosscomanalysis.db.tables.graph.SliceTypes
import java.util._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 27/09/2011
 * Time: 16:37
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

/**
 * Abstract super class for slicers.
 */
abstract class Slicer extends SessionFactorySetter with Logging with DBArgsParser with ClusterArgsParser {

  protected val cal = new GregorianCalendar()
  cal.setLenient(true)

  /**
   * Goes over the defined sequence (disjunctive intervals) and calls createSlice for every iteration. The beginning and
   * end of the interval is taken from interval method, the slice type is taken from getSliceType and at the end the
   * clean() method is called to free any open resources.
   */
  def slice(db: String = getOptValue(DB_OPT), windowSize: Int = getOptValue(WINDOW_SIZE_OPT).toInt,
            overlap: Int = getOptValue(WINDOW_OVERLAP_OPT).toInt,
            periodType: TimePeriodTypes = TimePeriodTypes.withName(getOptValue(WINDOW_SIZE_UNIT_OPT))) {
    setUpSessionFactory(db)

    log.info("Beginning slicing process")
    val sliceType = getSliceType(windowSize, overlap, periodType)
    val (begin, end) = interval
    cal.setTime(begin)

    setCalToStartOfDay()
    while (cal.getTimeInMillis < end.getTime) {
      val currBegin = cal.getTime
      // add an increment
      periodType match {
        case TimePeriodTypes.DAY => cal.add(Calendar.DAY_OF_MONTH, windowSize - 1)
        case TimePeriodTypes.WEEK => cal.add(Calendar.DAY_OF_MONTH, 7 * windowSize - 1)
        case TimePeriodTypes.MONTH => {
          log.debug("Calendar state before adding increment: " + cal.getTime)
          cal.add(Calendar.MONTH, windowSize) // add the whole month
          log.debug("Calendar state after adding months: " + cal.getTime)
          cal.add(Calendar.DAY_OF_MONTH, -1) // subtract one day (intervals has to be disjunctive)
          log.debug("Calendar state after subtracting one day: " + cal.getTime)
        }
        case TimePeriodTypes.YEAR => {
          cal.add(Calendar.YEAR, windowSize) // add one year
          cal.add(Calendar.DAY_OF_MONTH, -1) // see above
        }
      }
      setCalToEndOfDay()
      val reachedEnd =
        if (cal.getTimeInMillis >= end.getTime) {
          cal.setTime(end)
          true
        } else false
      log.debug("Final calendar state after increments: " + cal.getTime)
      val currEnd = cal.getTime
      log.info("Exporting slice between " + currBegin + " and " + currEnd)
      createSlice(currBegin, currEnd, sliceType.id)
      // set the next beginning time: without overlaps that's just the one day next, with overlap it is the overlap period back
      if (!reachedEnd) {
        setCalToStartOfDay()
        periodType match {
          case TimePeriodTypes.DAY => cal.add(Calendar.DAY_OF_MONTH, 1 - overlap)
          case TimePeriodTypes.WEEK => cal.add(Calendar.DAY_OF_MONTH, 1 - 7 * overlap)
          case TimePeriodTypes.MONTH => {
            cal.add(Calendar.DAY_OF_MONTH, 1)
            cal.add(Calendar.MONTH, -overlap)
          }
          case TimePeriodTypes.YEAR => {
            cal.add(Calendar.DAY_OF_MONTH, 1)
            cal.add(Calendar.YEAR, -overlap)
          }
        }
        log.debug("Setting beginning of the next period to: " + cal.getTime)
      }
    }
    clean()
  }

  protected def getSliceType(windowSize: Int = getOptValue(WINDOW_SIZE_OPT).toInt,
                             overlap: Int = getOptValue(WINDOW_OVERLAP_OPT).toInt,
                             periodType: TimePeriodTypes = TimePeriodTypes.withName(getOptValue(WINDOW_SIZE_UNIT_OPT))) = {
    val p = periodType match {
      case TimePeriodTypes.DAY => "d"
      case TimePeriodTypes.WEEK => "w"
      case TimePeriodTypes.MONTH => "m"
      case TimePeriodTypes.YEAR => "y"
    }
    if (hasOption(SLICE_FLAG_OPT))
      SliceTypes.withName("s" + windowSize + p + "o" + overlap + "_" + getOptValue(SLICE_FLAG_OPT))
    else
      SliceTypes.withName("s" + windowSize + p + "o" + overlap)
  }

  /**
   * @param beginSlice Begin date of the slice
   * @param endSlice End date of the slice
   * @param sliceType Type of the slice to be created (edge types, e.g. 'cocit/cocitation' are not supported currently)
   */
  protected def createSlice(beginSlice: Date, endSlice: Date, sliceType: Int)

  /**
   * @return (begin date, end date) of the data to be sliced.
   */
  protected def interval: (Date, Date)

  protected def clean() {}

  override protected def commandLineOptions = HELP_OPT :: DB_OPT :: WINDOW_SIZE_OPT :: WINDOW_OVERLAP_OPT ::
    WINDOW_SIZE_UNIT_OPT :: SLICE_FLAG_OPT :: Nil

  protected def setCalToEndOfDay() {
    cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY))
    cal.set(Calendar.MINUTE, cal.getMaximum((Calendar.MINUTE)))
    cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND))
    cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND))
  }

  protected def setCalToStartOfDay() {
    cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY))
    cal.set(Calendar.MINUTE, cal.getMinimum((Calendar.MINUTE)))
    cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND))
    cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND))
  }
}