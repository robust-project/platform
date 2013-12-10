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

package keywords.tfidf

import java.lang.Math.log10

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/03/2012
 * Time: 11:54
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * This object contains testing data for TF-IDF rankers.
 */

object KeywordRankerTestData {
  // (keywordid, objectid, #occurrences)
  val KEYWORDS_FOR_SLICE: Seq[(Long, Long, Int)] = Seq(
    (1, 1, 1),
    (1, 1, 2),
    (2, 1, 1),
    (2, 2, 2),
    (2, 2, 1),
    (3, 2, 2)
  )

  // mapping objectid->(keywordid->tf-idf)
  val TF_IDF: Map[Long, Map[Long, Double]] = Map(
    1l -> Map(1l -> 3d/4 * log10(2).toDouble, 2l -> 1d/4*log10(1).toDouble, 3l -> 0d),
    2l -> Map(1l -> 0d, 2l -> 3d/5 * log10(1).toDouble, 3l -> 2d/5 * log10(2).toDouble)
  )


}
