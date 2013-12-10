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

import ie.deri.uimr.crosscomanalysis.keywords.tfidf.ParallelKeywordRanker
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import collection.mutable.MutableList
import ie.deri.uimr.crosscomanalysis.db.tables.cocit.RankedKeywordsObjects
import java.util.Date

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/03/2012
 * Time: 12:23
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
@RunWith(classOf[JUnitRunner])
class ParallelKeywordRankerTest extends ParallelKeywordRanker with FunSuite {

  test("Scores must match") {
    rank(0) // slice type doesn't matter
  }

  protected def keywordsForSlice(begin: Date, end: Date) = null

  override def rank(sliceType: Int) {
    scoreAndStore(KeywordRankerTestData.KEYWORDS_FOR_SLICE.par, 1) // slice id does not matter
  }

  override protected def store(rankedKeywordsBatch: MutableList[RankedKeywordsObjects]) {
    for (rko <- rankedKeywordsBatch) {
      assert(rko.tfidf === KeywordRankerTestData.TF_IDF(rko.objectid)(rko.keywordid))
    }
  }
}
