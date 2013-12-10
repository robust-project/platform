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

package ie.deri.uimr.crosscomanalysis.db.tables.cocit

import org.squeryl.annotations._
import org.squeryl.KeyedEntity

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/02/2011
 * Time: 12:26
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 *
 * Contains definitions of tables specific for cocitation analysis tables.
 */


class KeywordsEM(
                  val articleid: Long,
                  val keyword: String,
                  var frequency: Int,
                  val doi: String,
                  val keywordid: Long) {
  def incrementFrequency {
    frequency += 1
  }

  override def toString = "(" + articleid + ", " + keyword + ", " + frequency + ", " + doi + ", " + keywordid + ")"
}

