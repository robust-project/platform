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

package ie.deri.uimr.crosscomanalysis.util

import org.apache.commons.math.linear.RealVector
import stemmer.{Stemmer => PorterStemmer}
import collection.Set
import java.io.File
import java.util.{Calendar, Date}
import collection.Map
import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07-Dec-2010
 * Time: 14:28:04
 * ©2010 Digital Enterprise Research Institute, NUI Galway. 
 */

object Functions {
  def cosineDistance(v1: RealVector, v2: RealVector): Double = {
    if (v1.getNorm > 0 && v2.getNorm > 0)
      v1.dotProduct(v2) / (v1.getNorm * v2.getNorm)
    else
      0d
  }

  def stem(s: String) = {
    val stemmer = new PorterStemmer()
    stemmer.add(s.toArray, s.length())
    stemmer.stem()

    stemmer.toString()
  }

  def jaccardSim[T](set1: Set[T], set2: Set[T]) = (set1 intersect set2).size.toDouble / (set1 union set2).size.toDouble

  def filePath(outDir: File, prefix: String, beginDate: Date, endDate: Date, suffix: String) = {
    val cal = Calendar.getInstance
    def formatDate(d: Date) = {
      cal.clear
      cal.setTime(d)
      cal.get(Calendar.DAY_OF_MONTH) + "_" + (cal.get(Calendar.MONTH) + 1) + "_" + cal.get(Calendar.YEAR)
    }
    outDir.getAbsolutePath + File.separator + prefix + formatDate(beginDate) + "-" + formatDate(endDate) + suffix
  }

  def filePath(outDir: File, sliceId: Long, maxSliceId: Long, beginDate: Date, endDate: Date, suffix: String) {
    filePath(outDir, offset(maxSliceId, sliceId) + sliceId + "_", beginDate, endDate, ".net")
  }

  def offset(maxId: Long, id: Long) = (for (i <- 0 until maxId.toString.length - id.toString.length) yield "0").mkString

  def denormalize[V](edges: Map[(V, V), Double]) = {
    if (edges.isEmpty)
      Map.empty[(V, V), Long]
    else {
      val minW = edges.values.min
      edges.mapValues(origW => (origW / minW).round)
    }
  }

  def parseDate(s: String, end: Boolean = false) = {
    val df = new SimpleDateFormat("dd.MM.yyyy")
    val d = df.parse(s)
    if (end) {
      d.setHours(23)
      d.setMinutes(59)
      d.setSeconds(59)
    }
    d
  }
}
