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

package ie.deri.uimr.crosscomanalysis.keywords.tfidf

import java.util.Date

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/03/2012
 * Time: 19:13
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

abstract class KeywordRanker {

  /**
   * Returns an iterator over the keywords for a particular slice / window and the associated objects (authors, mails, etc.)
   * @return Iterator of (keywordId, objectId, # occurrences)
   */
  protected def keywordsForSlice(begin: Date, end: Date): Iterator[(Long,Long,Int)]

  def rank(sliceType: Int)
}
