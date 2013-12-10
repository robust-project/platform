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

package ie.deri.uimr.crosscomanalysis.graphdb.gremlinska

import com.tinkerpop.pipes.util.{Pipeline => JavaPipeline}
import com.tinkerpop.pipes.{Pipe}
import com.tinkerpop.blueprints.pgm.Element
import ie.deri.uimr.crosscomanalysis.graphdb.traversals.pipes.filter.ClosureFilterPipe
import collection.JavaConversions.{asScalaIterator, asScalaBuffer}
import collection.mutable.{HashMap, MutableList,Iterable}
import ie.deri.uimr.crosscomanalysis.graphdb.traversals.pipes.sideeffect.ClosurePipe
import com.tinkerpop.pipes.sideeffect.{SideEffectPipe, CountPipe, AggregatePipe}
import com.tinkerpop.pipes.transform._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 18/04/2011
 * Time: 13:21
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * * <b>Acknowledgments:</b>This work was supported by Science Foundation Ireland (SFI) projects
 * Grant No. SFI/08/CE/I1380 (Lion-2) and Grant No. 08/SRC/I1407 (Clique: Graph & Network Analysis Cluster).
 */

class Pipeline[S, E](val start: collection.Iterable[S]) extends Iterable[E] {

  private val line = new MutableList[Pipe[_, _]]
  private val pipeNS = new HashMap[String, AnyRef] // pipe's name space
  private var pipeline:Option[JavaPipeline[S,E]] = None

  def outE(label: String) = addPipe(new OutEdgesPipe(label))

  def outE = addPipe(new OutEdgesPipe)

  def bothE(label: String) = addPipe(new BothEdgesPipe(label))

  def bothE = addPipe(new BothEdgesPipe)

  def inE(label: String) = addPipe(new InEdgesPipe(label))

  def inE = addPipe(new InEdgesPipe)

  def inV = addPipe(new InVertexPipe)

  def bothV = addPipe(new BothVerticesPipe)

  def outV = addPipe(new OutVertexPipe)

  def strain(f: Element => Boolean) = addPipe(new ClosureFilterPipe[Element](f))

  def aggregate[E](col: java.util.Collection[E]) = addPipe(new AggregatePipe(col))

  def apply[A](c: Element => A) = addPipe(new ClosurePipe[Element,A](c))

  def count = addPipe(new CountPipe[Element])

  def cap = {
    confirmOpenPipelineOrDie()
    val sideEffectPipe = line.last.asInstanceOf[SideEffectPipe[_,_]]
    line(line.size - 1) = new SideEffectCapPipe(sideEffectPipe)
    this
  }

  private def addPipe(p: Pipe[_,_]) = {
    confirmOpenPipelineOrDie()
    line += p
    this
  }

  def iterator = asScalaIterator(rawPipeline)

  def path = asScalaBuffer(rawPipeline.getPath)

  def rawPipeline =
    if (pipeline.isEmpty) {
      import collection.JavaConversions.{mutableSeqAsJavaList,asJavaIterable}
      pipeline = Some(new JavaPipeline[S, E](line))
      pipeline.get.setStarts(start)
      pipeline.get
    } else
      pipeline.get

  def pipe(n:Int) = line(n)

  private def confirmOpenPipelineOrDie() {
    if (pipeline.isDefined) throw new IllegalStateException("Pipeline has already been compiled!")
  }
}