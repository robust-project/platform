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

package ie.deri.uimr.crosscomanalysis.graphdb.traversals.pipes.sideeffect

import com.tinkerpop.pipes.AbstractPipe
import com.tinkerpop.pipes.sideeffect.SideEffectPipe

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 18/04/2011
 * Time: 11:31
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

class ClosurePipe[S,A](action: S => A) extends AbstractPipe[S,S] with SideEffectPipe[S,A] {
  private var sideEffect: Option[A] = None

  def processNextStart() = {
    if (starts.hasNext) {
      val s = starts.next
      sideEffect = Some(action(s))
      s
    } else throw new NoSuchElementException("No more start elements in the pipe")
  }

  def getSideEffect = sideEffect.get
}

//class ClosurePipeWithoutParam[S,A](action: => A) extends AbstractPipe[S,S] with SideEffectPipe[S,S,A] {
//  private var sideEffect: Option[A] = None
//
//  def processNextStart() = {
//    if (starts.hasNext) {
//      val s = starts.next
//      sideEffect = Some(action)
//      s
//    } else throw new NoSuchElementException("No more start elements in the pipe")
//  }
//
//  def getSideEffect = sideEffect.get
//}