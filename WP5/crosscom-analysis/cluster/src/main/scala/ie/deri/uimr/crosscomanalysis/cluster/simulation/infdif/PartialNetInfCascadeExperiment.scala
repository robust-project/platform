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

import java.io.{FileOutputStream, PrintWriter, File}
import scala.collection.JavaConversions.{iterableAsScalaIterable, asScalaIterator, asJavaCollection}
import collection.{mutable, Set, Seq}
import ie.deri.uimr.crosscomanalysis.graphdb._
import exporters.EdgeListWriter
import org.apache.commons.math.random.MersenneTwister
import ie.deri.uimr.crosscomanalysis.util._
import ie.deri.uimr.crosscomanalysis.cluster.optimisation.VertexSeedDegreeDiscount
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.jgrapht.{ICM, LTM}
import org.jgrapht.graph._
import org.jgrapht.UndirectedGraph
import ie.deri.uimr.crosscomanalysis.graphdb.loadUndirectedGraphFromFile
import org.jgrapht.traverse.TopologicalOrderIterator
import org.apache.commons.cli

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 03/10/2012
 * Time: 16:08
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
object PartialNetInfCascadeExperiment extends ArgsParser with Logging with Parallel {

  override val COMMAND_NAME = "partnet-infdif-exp"

  val SAMPLING_METHOD_OPT = new cli.Option("sm", "sampling-method", true, "method for sampling vertices or nodes, {uniform}")
  val FRAC_HIDDEN_V_OPT = new cli.Option("hv", "hidden-vertices", true, "fraction of hidden vertices, e.g. 0.1 for 10%")
  val FRAC_HIDDEN_E_OPT = new cli.Option("he", "hidden-edges", true, "fraction of hidden edges, e.g. 0.1 for 10%")
  val TARGET_SIZE_OPT = new cli.Option("ts", "target-size", true, "# actors targeted (initial seed size)")
  val NORM_BY_OUTS_OPT = new cli.Option("no", "norm-out-strength", true, "normalize by out-strength ('out') or in-strength ('in')")
  val WEIGHT_VAL_OPT = new cli.Option("wv", "weight-value", true, "the global transmission probability")
  val PARTIAL_DIFF_OPT = new cli.Option("td", "comp-partial-diff", false, "if set, will also compute cascade sizes on the partial graph")
  val SAVE_TRACES_OPT = new cli.Option("st", "save-diff-traces", true, "if set, will save the first n diffusion traces per sample")
  val TARGETS_HEUR_OPT = new cli.Option("th", "targets-heuristics", true, "strategy for sampling seeds, one of 'degree','disdegree','random'")
  val SAVE_ACT_NODES_OPT = new cli.Option("sa", "save-active-nodes", false, "saves activated nodes for each diffusion trial")
  val MODEL_OPT = new cli.Option("mo", "model", true, "either 'linear-threshold' or 'ind-cascade'")

  override def main(args: Array[String]) {
    mainStub(args) {
      setParallelismDegree()
      val modelName = getOptValue(MODEL_OPT)
      val random = new MersenneTwister(getOptValue(SEED_OPT).toLong)
      val repetitions = getOptValue(REPETITIONS_OPT).toInt
      val outDir = new File(getOptValue(DIR_OPT))
      outDir.mkdirs()

      val g = loadUndirectedGraphFromFile(new File(getOptValue(INFILE_OPT)))
      if (hasOption(WEIGHT_VAL_OPT)) {
        setWeights(getOptValue(WEIGHT_VAL_OPT).toDouble, g)
      }
      val vertices = g.vertexSet().toSet

      // pre-generate seeds for random number generators for individual diffusions
      val randomGens = for (i <- 1 to repetitions) yield new MersenneTwister(random.nextInt())

      for (samplingTrial <- (1 to repetitions).par) {
        log.info("Running " + samplingTrial + ". sampling trial")
        val sampleOutDir = new File(outDir, "sample" + samplingTrial)
        sampleOutDir.mkdirs()

        val (hiddenVertices, hiddenEdges) = sampleGraph(getOptValue(SAMPLING_METHOD_OPT),
          getOptValue(FRAC_HIDDEN_V_OPT).toFloat, getOptValue(FRAC_HIDDEN_E_OPT).toFloat, randomGens(samplingTrial - 1), g)
        log.info("Finding seeds")
        val seed = sampleSeed(g, randomGens(samplingTrial - 1), hiddenVertices)

        // for each sample, run the diffusion model r-times
        val oracleModel = modelName match {
          case "linear-threshold" => new LTM[DefaultWeightedEdge](g)
          case "ind-cascade" => new ICM[DefaultWeightedEdge](g)
          case _ => sys.error("Unknown diffusion model")
        }
        val out = new PrintWriter(new File(sampleOutDir, "all-diffusions.csv"))
        out.println("diffusion,totalCascadeSize,totalVisibility,visibleCS,phantomCS,hiddenCS")
        for (diffusionTrial <- 1 to repetitions) {
          log.info("Running " + diffusionTrial + ". diffusion trial (oracle network)")
          val (cascade, trackingGraph) = oracleModel.simulate(seed, randomGens(samplingTrial - 1), track = true)
          // save the diffusion traces
          if (hasOption(SAVE_TRACES_OPT) && diffusionTrial <= getOptValue(SAVE_TRACES_OPT).toInt) {
            val trOut = new FileOutputStream(new File(sampleOutDir, "diffusion-trace-" + diffusionTrial + ".dat"))
            EdgeListWriter.outputGraph(trackingGraph.get, trOut)
            trOut.close()
          }
          // save activated nodes
          if (hasOption(SAVE_ACT_NODES_OPT)) {
            val anOut = new PrintWriter(new File(sampleOutDir, "activated-nodes-" + diffusionTrial + ".csv"))
            cascade.foreach(na => anOut.println(na.mkString(",")))
            anOut.close()
          }

          val totalCascadeSize = cascade.map(_.size).sum
          val (totalVisibility, visibleCS, phantomCS, hiddenCS) = evaluate(trackingGraph.get,
            new File(sampleOutDir, "diffusion-" + diffusionTrial + ".csv"), hiddenVertices,
            hiddenEdges, seed, cascade, randomGens(samplingTrial - 1), g, totalCascadeSize)

          out.println("%d,%d,%e,%d,%d,%d".format(diffusionTrial, totalCascadeSize, totalVisibility, visibleCS,
            phantomCS, hiddenCS))
        }
        out.close()

        // compute the diffusion on a graph with hidden vertices/edges removed
        if (hasOption(PARTIAL_DIFF_OPT)) {
          val partialGraphDir = new File(sampleOutDir, "truncated_graph")
          partialGraphDir.mkdirs()

          val partialGraph = g.clone().asInstanceOf[SimpleWeightedGraph[Long, DefaultWeightedEdge]]
          partialGraph.removeAllEdges(hiddenEdges)
          partialGraph.removeAllVertices(hiddenVertices)

          assert(partialGraph.vertexSet().size == vertices.size - hiddenVertices.size)
          assert(partialGraph.edgeSet().size == g.edgeSet().size() - hiddenEdges.size)

          val partialModel = modelName match {
            case "linear-threshold" => new LTM(partialGraph)
            case "ind-cascade" => new ICM(partialGraph)
            case _ => sys.error("Unknown diffusion model")
          }

          val allPartDifOut = new PrintWriter(new File(partialGraphDir, "all-diffusions.csv"))
          allPartDifOut.println("diffusion,partialCascadeSize,correctedSize,expandedSize")
          for (diffusionTrial <- 1 to repetitions) {
            log.info("Computing " + diffusionTrial + ". diffusion trial (truncated graph)")
            val (cascade, trackingGraph) = partialModel.simulate(seed, randomGens(samplingTrial - 1), track = true)
            val cascadeSize = cascade.map(_.size).sum
            val out = new PrintWriter(new File(partialGraphDir, "diffusion-" + diffusionTrial + ".csv"))
            out.println("cascade_size")
            for (i <- 1 to cascade.length) {
              out.println(cascade.take(i).flatten.length)
            }
            out.close()
            // save the diffusion traces
            if (hasOption(SAVE_TRACES_OPT) && diffusionTrial <= getOptValue(SAVE_TRACES_OPT).toInt) {
              val trOut = new FileOutputStream(new File(partialGraphDir, "diffusion-trace-" + diffusionTrial + ".dat"))
              EdgeListWriter.outputGraph(partialGraph, trOut)
              trOut.close()
            }
            // save activated nodes
            if (hasOption(SAVE_ACT_NODES_OPT)) {
              val anOut = new PrintWriter(new File(partialGraphDir, "activated-nodes-" + diffusionTrial + ".csv"))
              cascade.foreach(na => anOut.println(na.mkString(",")))
              anOut.close()
            }
            // Partial Cascade Correction

            val correctedSize = paco(trackingGraph.get, partialGraph, getOptValue(WEIGHT_VAL_OPT).toFloat,
              getOptValue(FRAC_HIDDEN_V_OPT).toFloat, cascade, randomGens(samplingTrial - 1))
            val expandedSize = expandPartialCascade(trackingGraph.get, partialGraph,
              getOptValue(WEIGHT_VAL_OPT).toDouble, getOptValue(FRAC_HIDDEN_V_OPT).toDouble, cascade,
              randomGens(samplingTrial - 1))
            allPartDifOut.println("%d,%d,%f,%f".format(diffusionTrial, cascadeSize, correctedSize, expandedSize))
          }
          allPartDifOut.close()
          log.info("Evaluation done.")
        }

        storeSamples(g, seed, hiddenVertices, hiddenEdges, sampleOutDir)
      }
    }
  }

  /**
   * @return (set of seed vertices, set of non-seed (the rest) vertices)
   */
  private def sampleSeed(g: SimpleWeightedGraph[Long, DefaultWeightedEdge],
                         random: MersenneTwister, hiddenVertices: Set[Long]) = {
    val visibleVertices = g.vertexSet().toSet -- hiddenVertices
    val seedSize =
      if (getOptValue(TARGET_SIZE_OPT).startsWith("0."))
        (getOptValue(TARGET_SIZE_OPT).toFloat * visibleVertices.size).round
      else getOptValue(TARGET_SIZE_OPT).toInt
    assert(seedSize > 0 && seedSize <= visibleVertices.size,
      "Seed set size has to be at least 1 and maximally the size of the non-hidden vertices")
    log.debug("Selecting " + seedSize + " (sampled) out of " + visibleVertices.size + " vertices as initial seed")

    val seed =
      getOptValue(TARGETS_HEUR_OPT) match {
        case "degree" => {
          val opt = new VertexSeedDegreeDiscount(g, prob = getOptValue(WEIGHT_VAL_OPT).toDouble,
            discount = false, hiddenVertices = hiddenVertices)
          opt.findSeeds(seedSize)
        }
        case "disdegree" => {
          val opt = new VertexSeedDegreeDiscount(g, prob = getOptValue(WEIGHT_VAL_OPT).toDouble,
            discount = true, hiddenVertices = hiddenVertices)
          opt.findSeeds(seedSize)
        }
        case "random" => randomSample(visibleVertices.toSeq, seedSize, random)
        case _ => sys.error("Unknown seed selection strategy")
      }

    seed
  }

  private def sampleGraph[V, E](method: String, fractionOfHiddenV: Float, fractionOfHiddenE: Float,
                                random: MersenneTwister, g: UndirectedGraph[V, E]): (Set[V], Set[E]) = {
    method match {
      case "uniform" => {
        // select hidden vertices
        val vertices = g.vertexSet().toSeq
        val vCut = math.round(vertices.size * fractionOfHiddenV)
        assert(vCut <= vertices.size, "Fraction of the hidden nodes  has to be <= 1")
        val hiddenV = randomSample(vertices, vCut, random)
        // by hiding the vertices, some edges are also hidden
        val edges = g.edgeSet().toSet
        var hiddenE = hiddenV.map(v => g.edgesOf(v)).flatten.toSet
        val nHiddenEDueToVertices = hiddenE.size
        val eCut = math.round(edges.size * fractionOfHiddenE - hiddenE.size)
        if (eCut > 0) {
          hiddenE ++= randomSample((edges -- hiddenE).toSeq, eCut, random)
        } else if (fractionOfHiddenE > 0) {
          log.warn("All desired (-he) edges (possibly even more) were removed due to removed vertices: " + nHiddenEDueToVertices)
        }
        log.debug("# hidden vertices: " + hiddenV.size + " # hidden E: " + hiddenE.size + " (out of which " +
          nHiddenEDueToVertices + " were removed due to vertex sampling), |E|=" + edges.size + ",|V|=" + vertices.size)

        (hiddenV, hiddenE)
      }
      case _ => sys.error("Unknown sampling method!")
    }
  }

  private def storeSamples[V, E](g: SimpleWeightedGraph[V, E], seed: Set[V], hiddenVertices: Set[V], hiddenEdges: Set[E],
                                 dir: File) {
    def out(s: Set[V], o: PrintWriter) {
      s.foreach(o.println(_))
      o.close()
    }
    out(seed, new PrintWriter(new File(dir, "seeds.csv")))
    out(hiddenVertices, new PrintWriter(new File(dir, "hidden_vertices.csv")))
    val outE = new PrintWriter(new File(dir, "hidden_edges.csv"))
    hiddenEdges.foreach(e => outE.println(g.getEdgeSource(e) + "," + g.getEdgeTarget(e)))
    outE.close()
  }

  /**
   * @param trackingGraph Forest of activation traces, the V and E have the same IDs as in the orig. graph, but  they are distinct objects!
   * @param outFile File to which write the result of the evaluation (cascade sizes)
   * @param hiddenVertices Set of hidden vertices from the main graph
   * @param hiddenEdges Set of hidden edges from the main graph
   * @param seed Set of seed vertices
   * @param cascade The cascade results
   * @return (totalVisibility,visibleCS,phantomCS,hiddenCS)
   */
  private def evaluate(trackingGraph: SimpleDirectedWeightedGraph[Long, DefaultWeightedEdge], outFile: File,
                       hiddenVertices: Set[Long], hiddenEdges: Set[DefaultWeightedEdge], seed: Set[Long],
                       cascade: Seq[collection.Set[Long]], random: MersenneTwister,
                       graph: SimpleWeightedGraph[Long, DefaultWeightedEdge], totalCascadeSize: Int) = {
    // a visible node may be activated by one visible and one hidden node, in that case it's visibility is 0.5
    val visibilityProb = for (t <- 1 to cascade.size) yield mutable.Map.empty[Long, Double]
    val activationTime = mutable.Map.empty[Long, Int] // vertex -> time of activation
    cascade.head.foreach(s => {
      // seed nodes
      visibilityProb(0)(s) = 1
      activationTime(s) = 0
    })

    for (v <- new TopologicalOrderIterator(trackingGraph);
         inEdges = trackingGraph.incomingEdgesOf(v)
         if !inEdges.isEmpty) {
      // this node was activated right after the last one of the vertices that activated it
      val time = inEdges.map(e => activationTime(trackingGraph.getEdgeSource(e))).max + 1
      activationTime(v) = time
      // compute the visibility of the activation of the node v
      if (hiddenVertices.contains(v)) {
        visibilityProb(time)(v) = 0
      } else {
        visibilityProb(time)(v) = inEdges.map(e => {
          // probability that the activating node was visible (on average)
          val source = trackingGraph.getEdgeSource(e)
          val target = trackingGraph.getEdgeTarget(e)
          if (hiddenEdges.contains(graph.getEdge(source, target))) 0
          else visibilityProb(activationTime(source))(source)
        }).sum / inEdges.size.toDouble
      }
    }

    val out = new PrintWriter(outFile)
    out.println("time,vertex,visibility,cascadeType")
    var visibleCascadeSize: Int = 0
    var phantomCascadeSize: Int = 0
    var hiddenCascadeSize: Int = 0
    var totalVisibility: Double = 0
    for ((vertex, time) <- activationTime) {
      val vis = visibilityProb(time)(vertex)
      assert(vis >= 0 && vis <= 1)
      totalVisibility += vis
      val cascadeType =
        if (vis == 1) {
          visibleCascadeSize += 1
          "visible"
        } else {
          if (hiddenVertices.contains(vertex)) {
            hiddenCascadeSize += 1
            "hidden"
          } else {
            phantomCascadeSize += 1
            "phantom"
          }
        }
      out.println("%d,%d,%f,%s".format(time, vertex, vis, cascadeType))
    }
    out.close()
    assert(totalCascadeSize == (visibleCascadeSize + phantomCascadeSize + hiddenCascadeSize),
      "Total cascade size %d != visible (%d) + phantom (%d) + hidden (%d)".format(totalCascadeSize, visibleCascadeSize,
        phantomCascadeSize, hiddenCascadeSize))

    (totalVisibility, visibleCascadeSize, phantomCascadeSize, hiddenCascadeSize)
  }

  /**
   * Implements the Partial Cascade Correction (PACO)
   * @param trackingGraph Tracking graph of the diffusion over the partial network.
   */
  private def paco(trackingGraph: SimpleDirectedWeightedGraph[Long, DefaultWeightedEdge],
                   partialGraph: SimpleWeightedGraph[Long, DefaultWeightedEdge],
                   activationProb: Double, samplingProb: Double,
                   cascade: Seq[collection.Set[Long]],
                    random: MersenneTwister): Double = {
    val correctedCascadeSizes = Array.ofDim[Double](cascade.size)
    correctedCascadeSizes(0) = cascade.head.size // number of seeds stay the same
    for (t <- 1 until cascade.size) {
      val meanDegreePrev = cascade(t - 1).map(v => partialGraph.degreeOf(v) / (1 - samplingProb)).sum / cascade(t - 1).size
      correctedCascadeSizes(t) = cascade(t).size / (1 - samplingProb) + // original cascade size at time t
        (correctedCascadeSizes(t - 1) - cascade(t - 1).size) * meanDegreePrev * activationProb //+ // exp. number of dummy descendants of dummy nodes from time t-1
//        cascade(t - 1).map(v => ((partialGraph.degreeOf(v) / (1 - samplingProb)) - partialGraph.degreeOf(v)) * activationProb).sum // exp. number of dummy descendants of real nodes from time t-1
    }
    correctedCascadeSizes.sum
  }

  private def expandPartialCascade(trackingGraph: SimpleDirectedWeightedGraph[Long, DefaultWeightedEdge],
                                   partialGraph: SimpleWeightedGraph[Long, DefaultWeightedEdge],
                                   activationProb: Double, samplingProb: Double,
                                   cascade: Seq[collection.Set[Long]], random: MersenneTwister): Double = {
    val subTreeSize = mutable.Map.empty[Long, Int] // vertex -> size of the sub-tree beginning with that vertex
    cascade.flatten.foreach {v => subTreeSize(v) = 0} // initialize the subtree sizes map

    for (t <- (0 until cascade.size).reverse;
         vertex <- cascade(t)) {
      val degree = partialGraph.degreeOf(vertex)
      val correctedDegree = math.round(degree / (1 - samplingProb)).toInt
      if (correctedDegree > degree) {
        var dummySubTreesSize = 0
        for (dummyNode <- 1 to (correctedDegree - degree) if random.nextDouble() <= activationProb) {
          if (t < cascade.size - 1) {
            // sample the size of the subtree beginning with the dummy node - from the subtrees of the previous level
            dummySubTreesSize += randomSample(cascade(t + 1).toSeq.map(v => subTreeSize(v)), 1, random).head
          } else {
            dummySubTreesSize += 1 // add just one dummy node - a leave
          }
        }
        val realSubTreesSize =
          if (trackingGraph.containsVertex(vertex))
            trackingGraph.outgoingEdgesOf(vertex).map(e => subTreeSize(trackingGraph.getEdgeTarget(e))).sum
          else
           0

        subTreeSize(vertex) = 1 + dummySubTreesSize + realSubTreesSize
      }
    }

    cascade.head.map(seed => subTreeSize(seed)).sum
  }

  private def rbinom(sampleSize: Int, probability: Double, random: MersenneTwister): Int =
    (for (s <- 1 to sampleSize if random.nextDouble() <= probability) yield 1).size

  override protected def commandLineOptions = INFILE_OPT :: DIR_OPT :: HELP_OPT :: REPETITIONS_OPT ::
    SEED_OPT :: SAMPLING_METHOD_OPT :: MODEL_OPT :: FRAC_HIDDEN_E_OPT :: FRAC_HIDDEN_V_OPT :: TARGET_SIZE_OPT ::
    WEIGHT_VAL_OPT :: PARTIAL_DIFF_OPT :: SAVE_TRACES_OPT :: TARGETS_HEUR_OPT ::
    SAVE_ACT_NODES_OPT :: PARALLELISM_DEGREE_OPT :: Nil
}