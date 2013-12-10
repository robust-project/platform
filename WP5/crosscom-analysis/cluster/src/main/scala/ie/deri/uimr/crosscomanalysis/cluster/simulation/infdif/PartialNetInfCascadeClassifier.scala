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

package ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif

import com.tinkerpop.blueprints.pgm.Graph
import java.io.{PrintWriter, File}
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import scala.collection.JavaConversions.iterableAsScalaIterable
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import collection.mutable.Queue
import com.tinkerpop.blueprints.pgm.Vertex
import ie.deri.uimr.crosscomanalysis.util.{Logging, ArgsParser}
import ie.deri.uimr.crosscomanalysis.graphdb.importers.EdgeListFileLoader
import io.Source

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 04/02/2013
 * Time: 11:23
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 *
 * This object post-process the inf. diff. traces saved by PartialNetInfCascadeExperiment and saves the nodes activated
 * unobservably, visibly, and hiddenly.
 */
object PartialNetInfCascadeClassifier extends ArgsParser with Logging {

  override val COMMAND_NAME = "partnet-classify-nodes"

  override def main(args: Array[String]) {
    mainStub(args) {
      for (expDir <- new File(getOptValue(DIR_OPT)).listFiles().filter(_.isDirectory);
           sampleDir <- expDir.listFiles().filter(f => f.isDirectory && f.getName.startsWith("sample"));
           diff <- 1 to 50) {
        log.info("Sample: " + sampleDir + ", diff: " + diff)
        val trackingGraph = new EdgeListFileLoader(new File(sampleDir, "diffusion-trace-" + diff + ".dat"), ACTIVATED).graph
        val hiddenVertices = (for (line <- Source.fromFile(new File(sampleDir, "hidden_vertices.csv")).getLines())
        yield line.toLong).toSet
        val seeds = (for (line <- Source.fromFile(new File(sampleDir, "seeds.csv")).getLines())
        yield line.toLong).toSet
        val cascade = (for (line <- Source.fromFile(new File(sampleDir, "activated-nodes-" + diff + ".csv")).
          getLines() if line.trim.length > 0)
        yield line.split(",").map(_.toLong).toSet).toArray
        evaluate(trackingGraph, new File(sampleDir, "diffusion-" + diff + "-categories.csv"), hiddenVertices, seeds, cascade)
      }
    }
  }

  /**
   * @param trackingGraph Forest of activation traces, the V and E have the same IDs as in the orig. graph, but  they are distinct objects!
   * @param outFile File to which write the result of the evaluation (cascade sizes)
   * @param hiddenVertices Set of hidden vertices from the main graph
   * @param seed Set of seed vertices
   * @param cascade The cascade results
   * @return (unobserved, hidden, visible, total) cascade sizes (array with sizes per iteration)
   */
  private def evaluate(trackingGraph: Graph, outFile: File, hiddenVertices: Set[Long],
                       seed: Set[Long], cascade: Array[Set[Long]]) = {

    val vertices = trackingGraph.getVertices.map(v => v(ORIGID)).toSet ++ seed
    var unobservablyActivated = Set.empty[Long]
    val hiddenlyActivated = vertices.intersect(hiddenVertices)
    val visibleVertices = vertices.diff(hiddenlyActivated)
    val q = Queue.empty[(Boolean, Vertex)]
    q ++= (for (s <- seed; v = trackingGraph.getVertex(s.asInstanceOf[AnyRef]) if v != null) yield ((false, v)))

    while (!q.isEmpty) {
      val (hiddenPathBackwards, activatingV) = q.dequeue
      for (activationE <- activatingV.getOutEdges(ACTIVATED)) {
        val activatedV = activationE.getInVertex
        val hidden = if (visibleVertices.contains(activatedV(ORIGID))) {
          if (hiddenPathBackwards) {
            unobservablyActivated = unobservablyActivated + activatedV(ORIGID)
            true
          } else false
        } else true

        q += ((hidden, activatedV))
      }
    }
    val visiblyActivated = visibleVertices.diff(unobservablyActivated)

    val totalCascadeSize = visiblyActivated.size + unobservablyActivated.size + hiddenlyActivated.size
    assert(totalCascadeSize == cascade.flatten.toSet.size, "total cascade size: " + totalCascadeSize +
      " has to equal the size of the last active set: " + cascade.flatten.toSet.size)

    val out = new PrintWriter(outFile)
    out.println("nodeid,ctype")
    visiblyActivated.foreach(v => out.println(v + ",visible"))
    unobservablyActivated.foreach(v => out.println(v + ",unobserved"))
    hiddenlyActivated.foreach(v => out.println(v + ",hidden"))
    out.close()
  }

  override protected def commandLineOptions = DIR_OPT :: Nil
}
