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

package ie.deri.uimr.crosscomanalysis.cluster.viz

import java.io.File
import java.awt.image.BufferedImage
import edu.uci.ics.jung.visualization.VisualizationImageServer
import javax.imageio.ImageIO
import ie.deri.uimr.crosscomanalysis.jung.algorithms.layout.AggregateLayout
import java.lang.Math
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer
import java.awt.{Shape, Color, Paint, Dimension}
import scala.util.Random
import com.tinkerpop.blueprints.pgm.{Vertex, Edge, Graph}
import edu.uci.ics.jung.algorithms.layout.{Layout, StaticLayout, FRLayout}
import java.util.{HashMap => JavaHashMap}
import ie.deri.uimr.crosscomanalysis.util.Logging
import collection.{Set, Map, Seq}
import java.awt.geom.{Ellipse2D, Rectangle2D, Point2D}
import org.apache.commons.collections15.{Predicate, TransformerUtils, Transformer}
import edu.uci.ics.jung.graph.util.Context
import edu.uci.ics.jung.graph.{UndirectedSparseGraph, Graph => JUNGGraph}
import ie.deri.uimr.crosscomanalysis.graphdb.Conversions.blueprintsGraphToJUNG
import collection.mutable.{HashSet, HashMap}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 09/02/2011
 * Time: 18:06
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait JUNGRenderer extends Renderer with Logging {
  protected val maxIteration: Int
  protected val repulsionMultiplier: Double
  protected val attractionMultiplier: Double
  protected val edgeWeights: HashMap[Edge, Double]

  protected var lastLayout: Option[Layout[Vertex, Edge]] = None
  protected var continuing = true
  protected var directed = false

  private val randomSeed = 25
  private val random = new Random(randomSeed)
  //(1600, 1200)
  private val dimension = new Dimension(1600, 1200)
  private val vertexColors = random.shuffle(colorSpectrum)
  private val edgeColors = wbSpectrum(1f, 1000)
  private val vertexPaints = new HashMap[Vertex, Paint]
  private val vertexLabels = new HashMap[Vertex, String]
  private val legendVertices = new HashSet[Vertex]

  def render(g: Graph, clusters: Seq[(Int, Set[Vertex])], vertexSize: Map[Vertex, Double], output: File) {
    colorAndLabelClusters(clusters)

    // initialise layout
    val legendDim = new Dimension(100, dimension.height)
    val graphDim = new Dimension(dimension.width - legendDim.width, dimension.height)
    val graphCenter = new Point2D.Double(graphDim.width / 2 + legendDim.width, graphDim.height / 2)
    val graphLayout = new FRLayout[Vertex, Edge](g, graphDim)
    if (continuing && lastLayout.isDefined)
      graphLayout.setInitializer(lastLayout.get)
    else
      graphLayout.setInitializer(new RandomLocationTransformer[Vertex](graphDim, randomSeed))
    graphLayout.setMaxIterations(maxIteration)
    graphLayout.setRepulsionMultiplier(repulsionMultiplier)
    graphLayout.setAttractionMultiplier(attractionMultiplier)

    // delegate layout for AggregateLayout is never used - it's just a hack
    // so as to aggregate graph layout next to legend
    val mainLayout = new AggregateLayout[Vertex, Edge](g)
    mainLayout.setSize(dimension)
    generateLegend(mainLayout, legendDim, g, clusters)
    mainLayout.put(graphLayout, graphCenter)

    // render
    val vv = new VisualizationImageServer[Vertex, Edge](mainLayout, dimension)
    val context = vv.getRenderContext()
    context.setVertexFillPaintTransformer(lazyMapTransformer[Vertex, Paint](vertexPaints, Color.WHITE))
    context.setVertexDrawPaintTransformer(new Transformer[Vertex, Paint] {
      def transform(v: Vertex) = Color.DARK_GRAY
    })
    context.setEdgeDrawPaintTransformer(new Transformer[Edge, Paint] {
      def transform(e: Edge) = {
        Math.round(edgeWeights(e) * edgeColors.size).toInt match {
          case i if i > 0 => edgeColors(i - 1)
          case _ => edgeColors(0)
        }
      }
    })
    context.setEdgeArrowPredicate(new Predicate[Context[JUNGGraph[Vertex, Edge], Edge]] {
      def evaluate(p: Context[JUNGGraph[Vertex, Edge], Edge]) = directed
    })
    context.setVertexLabelTransformer(lazyMapTransformer[Vertex, String](vertexLabels, ""))
    context.setVertexShapeTransformer(new Transformer[Vertex, Shape] {
      def transform(v: Vertex) = {
        if (v.getProperty("legend") == null) {
          val size = vertexSize(v)
          val factor = if (size > 10) Math.log10(size) else 1d
          new Ellipse2D.Double(-10, -10, 10 * factor, 10 * factor)
        } else new Rectangle2D.Double(-10, -10, 10, 10)
      }
    })

    vv.setBackground(Color.WHITE)

    val relaxer = vv.getModel().getRelaxer()
    relaxer.stop()
    log.debug("Starting to iterate the layout")
    while (!graphLayout.done()) graphLayout.step
    log.debug("Iteration finished")
    log.debug("Saving the file to " + output.toString)
    saveImage(output, vv)
    log.debug("File saved")

    if (continuing) lastLayout = Some(graphLayout)
    clean(g)
  }

  private def generateLegend(mainLayout: AggregateLayout[Vertex, Edge], legendDim: Dimension, g: Graph, clusters: Seq[(Int, Set[Vertex])]) {
    val legendGraph = new UndirectedSparseGraph[Vertex, Edge]
    val legend = (for ((index, _) <- clusters) yield index).sorted
    val positions = new JavaHashMap[Vertex, Point2D]
    var x = 20
    var y = 0
    for (clusterIndex <- legend) {
      if (y + 50 > legendDim.height) {
        y = 0
        x += 30
      } else {
        y += 30
      }

      val vertexName = clusterIndex.toString() + "_L" // auxiliary node for representing number/index of a cluster
      val ver = g.addVertex(vertexName)
      ver.setProperty("legend", true) // todo the legend vertices should be removed in case of non-memory graph DB
      legendGraph.addVertex(ver)
      legendVertices += ver

      val c = vertexColors(clusterIndex % vertexColors.size)
      vertexLabels(ver) = clusterIndex.toString
      vertexPaints(ver) = c

      positions.put(ver, new Point2D.Double(x, y))
    }
    val legendLayout = new StaticLayout[Vertex, Edge](legendGraph, TransformerUtils.mapTransformer(positions), legendDim)
    mainLayout.put(legendLayout, new Point2D.Double(legendDim.width / 2, legendDim.height / 2))
  }

  private def saveImage(image: File, vv: VisualizationImageServer[Vertex, Edge]) {
    vv.repaint()
    val im = vv.getImage(new Point2D.Double(dimension.width / 2, dimension.height / 2), dimension)
    val bi = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB)
    val g2 = bi.createGraphics()
    g2.drawImage(im, 0, 0, null)
    g2.dispose()
    ImageIO.write(bi, "PNG", image)
  }

  private def colorAndLabelClusters(clusters: Seq[(Int, Set[Vertex])]) {
    if (clusters.size > vertexColors.length) log.info("There are no different colors left to differentiate clusters.")
    for ((index, cluster) <- clusters) {
      val c = vertexColors(index % vertexColors.size)
      for (vertex <- cluster) {
        vertexLabels(vertex) = index.toString
        vertexPaints(vertex) = c
      }
    }
  }

  private def lazyMapTransformer[I, O](mapping: Map[I, O], default: O) = {
    new Transformer[I, O] {
      def transform(in: I) = if (mapping.contains(in))
        mapping(in)
      else
        default
    }
  }

  /**
   * @return White to black color spectrum.
   */
  private def wbSpectrum(maxColor: Float, shades: Int) = (for (i <- 1 to shades)
  yield new Color(i * maxColor / shades, i * maxColor / shades, i * maxColor / shades)).reverse

  private def colorSpectrum = for (r <- 55 to (255, 50); g <- 5 to (255, 50); b <- 5 to (205, 50)) yield new Color(r, g, b)

  private def clean(g: Graph) {
    vertexLabels.clear
    vertexPaints.clear
    legendVertices.foreach(g.removeVertex(_))
    legendVertices.clear
  }
}

object JUNGRenderer {
  def apply(ew: HashMap[Edge, Double]) = {
    new JUNGRenderer {
      protected val edgeWeights = ew
      protected val attractionMultiplier = 1d
      protected val repulsionMultiplier = 1.5
      protected val maxIteration = 700
    }
  }
}