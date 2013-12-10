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

package ie.deri.uimr.crosscomanalysis.cluster.exporters

import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.util.Config.config
import collection.mutable.{MutableList, HashSet}
import java.io.{PrintWriter, File}
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 08/02/2011
 * Time: 17:44
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 *
 * Exports a given network to multislice format for slicer utility from Louvain package
 */

object MultiSlicedLouvainExporter extends Logging with SessionFactorySetter {
  setUpSessionFactory("tiddlywikigroup")

  def generate(sliceType:Int, output: File) {
    val nodes = new HashSet[Long]
    val edges = new HashSet[(Long, Long, Double, Int)] // source, sink, weight, slice
    val slices = new MutableList[(Int, Long)] // slice index, db slice id

    transaction {
      for (slice <- from(networkSlice)(ns =>
        (where(ns.sliceType === sliceType) select (ns) orderBy (ns.beginDate).asc))) {
        slices += ((slices.size + 1, slice.id))
        for ((source, sink, weight) <-
             from(networkSliceStructure)(nss => where(nss.sliceId === slice.id) select(nss.source, nss.sink, nss.weight))) {
          nodes += source
          nodes += sink
          edges += ((source, sink, weight, slices.size))
        }
      }
    }

    val pw = new PrintWriter(output)
    pw.println(">")
    nodes.foreach(nodeId => pw.println(nodeId + " Node " + nodeId))
    pw.println(">")
    for ((id, name) <- slices) {
      pw.println(id + " " + name)
    }
    pw.println(">")
    for ((source, sink, weight, slice) <- edges) {
      pw.println(source + " " + sink + " " + weight + " " + slice)
    }
    pw.println
    pw.close
  }

  def main(args: Array[String]) {
    generate(args(0).toInt, new File(args(1)))
  }
}