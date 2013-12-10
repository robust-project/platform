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

package ie.deri.uimr.crosscomanalysis.keywords

import ie.deri.uimr.crosscomanalysis.keywords.exporters.{BoardsPostsExporter, KeywordDiffusionInClusterBoardsExporter, ArnetCitationInfluenceExporter, BoardsCitationInfluenceExporter}
import keywordmining.{CorpusCleaner, GeneralCorpusProcessor, KeywordIDDBBuilder, RenumberingCorpusProcessor}
import tfidf._
import tokenizer.TokenizeKeywords
import ie.deri.uimr.crosscomanalysis.util.Application

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 07-Dec-2010
 * Time: 16:22:25
 * ©2010 Digital Enterprise Research Institute, NUI Galway. 
 */

object App extends Application {

  val commands = TokenizeKeywords :: KeywordEraser :: RenumberingCorpusProcessor :: CSXIDFRankerSequential ::
    GeneralCorpusProcessor :: KeywordIDDBBuilder :: PerYearOccurrenceCalculator :: SWIRSequentialKeywordRanker ::
    KeywordIDGenerator :: CorpusCleaner :: CSXIDFRankerParallel ::BoardsCitationInfluenceExporter ::
    ArnetCitationInfluenceExporter :: KeywordDiffusionInClusterBoardsExporter :: BoardsPostsExporter :: Nil
}