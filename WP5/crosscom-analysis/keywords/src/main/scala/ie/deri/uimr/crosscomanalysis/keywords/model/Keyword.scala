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

package ie.deri.uimr.crosscomanalysis.keywords.model

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/02/2011
 * Time: 11:23
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

class Keyword(val id: Long, var frequency: Int) {

  def incrementFrequency(inc: Int = 1) = {
    frequency += inc
    frequency
  }

  override def equals(obj: Any) = {
    obj match {
      case k: Keyword => k.id == this.id && canEqual(obj)
      case _ => false
    }
  }

  override def hashCode = id.hashCode

  protected def canEqual(that: Any) = that.isInstanceOf[Keyword]
}