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

package ie.deri.uimr.crosscomanalysis.db.schemas

import org.squeryl.Schema
import ie.deri.uimr.crosscomanalysis.db.tables.cocit._
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/02/2011
 * Time: 12:26
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object CocitSchema extends Schema {
  val fulltexts = table[Fulltexts]("fulltexts")
  val tokenizedKeywords = table[TokenizedKeywords]("tokenized_keywords")
  val articleVenues = table[ArticleVenue]("articlevenue")
  val coauthor = table[Coauthor]("coauthor")
  val keywords = table[Keywords]("keywords")
  val keywordsEM = table[KeywordsEM]("keywords_em")
  val rankedKeywordsObjects = table[RankedKeywordsObjects]("ranked_keywords_objects")
  val keywordId = table[KeywordIDAutoInc]("keyword_id")        // keyword_id
  val keywordIdEMCSX = table[KeywordID]("keyword_id")
  val doi = table[DOI]("doi")
  val authornames = table[AuthorNames]("authornames")
  val authorKeyword = table[AuthorKeyword]("author_keyword")
  val citations = table[Citation]("citations")
  val documentFreq = table[DocumentFreq]("document_freq")

  on(keywordId) (k=> declare(k.id is(autoIncremented)))
}