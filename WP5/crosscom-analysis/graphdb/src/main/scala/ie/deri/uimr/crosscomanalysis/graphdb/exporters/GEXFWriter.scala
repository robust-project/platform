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

package ie.deri.uimr.crosscomanalysis.graphdb.exporters

import collection.{Seq,Set}
import ie.deri.uimr.crosscomanalysis.graphdb.gremlinska.GremlinSka._
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import java.awt.Color
import java.util.{Date}
import util.Random
import com.tinkerpop.blueprints.pgm.{Edge, Vertex, Graph}
import xml._
import java.io.{File}
import collection.mutable.MutableList
import java.text.{SimpleDateFormat, DateFormat}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/11/2011
 * Time: 18:47
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 */

object GEXFWriter {

  val colors = {
    val random = new Random(25)
    random.shuffle(colorSpectrum)
  }

  def outputDynamicGraph(graphs: Seq[(Graph, Date, Date)], allVertices: Set[Vertex], allEdges: Set[Edge], file: File, directed: Boolean) {
    val df = new SimpleDateFormat("yyyy-MM-dd")
    val nodes = new MutableList[Elem]

    // go over all vertices and find the slices which it was active
    for (vertex <- allVertices; c = colors(vertex(ORIGID).toInt % colors.size)) {
      val spells = new MutableList[Elem]
      val attributes = new MutableList[Elem]
      for ((graph, begin, end) <- graphs if graph.getVertex(vertex.getId) != null) {
        spells += <spell/> % attr("start", df.format(begin)) % attr("end", df.format(end))
        attributes += <attrvalue/> % attr("start", df.format(begin)) % attr("end", df.format(end)) % attr("for", SIZE.id.toString) % attr("value", vertex(SIZE).toString)
        attributes += <attrvalue/> % attr("start", df.format(begin)) % attr("end", df.format(end)) % attr("for", ORIGID.id.toString) % attr("value", vertex(ORIGID).toString)
      }
      val color = <viz:color/> % attr("r", c.getRed.toString) % attr("g", c.getGreen.toString) % attr("b", c.getBlue.toString)
      nodes +=
        <node>
          <spells>
            {spells}
          </spells>
          <attrvalues>
            {attributes}
          </attrvalues>
          {color}
        </node> % attr("id", vertex(ORIGID).toString) % attr("label", vertex(ORIGID).toString)
    }
    val edges = new MutableList[Elem]
    for (edge <- allEdges) {
      val spells = new MutableList[Elem]
      val attributes = new MutableList[Elem]
      for ((graph, begin, end) <- graphs if graph.getEdge(edge.getId) != null) {
        spells += <spell/> % attr("start", df.format(begin)) % attr("end", df.format(end))
        attributes += <attrvalue/> % attr("start", df.format(begin)) % attr("end", df.format(end)) % attr("for", WEIGHT.id.toString) % attr("value", edge(WEIGHT).toString)
      }
      edges +=
        <edge>
          <spells>
            {spells}
          </spells>
          <attrvalues>
            {attributes}
          </attrvalues>
        </edge> % attr ("id", edge.getId.toString) % attr("source", edge.getOutVertex()(ORIGID).toString) % attr("target", edge.getInVertex()(ORIGID).toString)
    }
    val meta =
      <meta>
        <creator>vaclav.belak@deri.org</creator>
        <description>A dynamic network - very helpful, huh?:)</description>
      </meta> % attr("lastmodifieddate", df.format(new Date()))
    val sizeAttr = <attribute title="size" type="integer"/> % attr("id", SIZE.id.toString)
    val origidAttr = <attribute title="origid" type="integer"/> % attr("id", ORIGID.id.toString)
    val weightAttr = <attribute title="weight" type="float"/> % attr("id", WEIGHT.id.toString)
    val graph =
      <graph mode="dynamic" timeformat="date">
        <attributes class="node" mode="dynamic">
          {sizeAttr}
          {origidAttr}
        </attributes>
        <attributes class="edge" mode="dynamic">
          {weightAttr}
        </attributes>
        <nodes>
          {nodes}
        </nodes>
        <edges>
          {edges}
        </edges>
      </graph> % attr("start", df.format(graphs.head._2)) % attr("end", df.format(graphs.last._3)) % attr("defaultedgetype", (if (directed) "directed" else "undirected"))
    val gexf =
      <gexf xmlns="http://www.gexf.net/1.1draft" version="1.1" xmlns:viz="http://www.gexf.net/1.1draft/viz" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.gexf.net/1.1draft http://www.gexf.net/1.1draft/gexf.xsd">
        {meta}
        {graph}
      </gexf>

    XML.save(file.getAbsolutePath, gexf, xmlDecl = true, enc = "UTF-8")
  }

  private def colorSpectrum = for (r <- 55 to (255, 50); g <- 5 to (255, 50); b <- 5 to (205, 50)) yield new Color(r, g, b)

  private def attr(key: String, value: String): Attribute = Attribute(None, key, Text(value), Null)
}