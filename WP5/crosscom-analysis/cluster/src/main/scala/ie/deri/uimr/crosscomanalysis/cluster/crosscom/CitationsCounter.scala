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

package ie.deri.uimr.crosscomanalysis.cluster.crosscom

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema._
import collection.Set

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01/08/2011
 * Time: 12:28
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 *
 * Offers methods for counting citatations, i.e. how many times her publications has been cited, of a given author.
 */

trait CitationsCounter {

  /**
   * @return Count of citations coming from fellows' papers published within yearsCiting citing authorid's papers published within yearsPublication.
   */
  protected def citationsCount(authorid: Long, fellows: Set[Long], yearsPublication: Range, yearsCiting: Range): Long = transaction {
    // first obtain set of fellows' candidate (citing) papers within yearsCiting
    // todo Should I exclude self-citations?
    val fellowsPapers = from(coauthor, articleVenues)((ca, av) =>
      where(ca.articleid === av.articleid and av.year.in(yearsCiting) and ca.authorid.in(fellows)) select (ca.articleid)).toSet
    if (fellowsPapers.isEmpty)
      0
    else {
      // find # citations from papers published by fellows within yearsCiting citing papers published by the author within yearsPublication
      from(coauthor, citations, articleVenues)((ca, ci, av) =>
        where(ca.articleid === ci.citedArticleId and av.articleid === ca.articleid and av.year.in(yearsPublication)
          and ca.authorid === authorid and ci.citationArticleId.in(fellowsPapers))
          compute (countDistinct(ci.citationArticleId))).single.measures
    }
  }

  /**
   * This method does NOT restrict the citating sources to author's fellows.
   * @return Count of citations coming from papers published within yearsCiting citing authorid's papers published within yearsPublication.
   */
  protected def citationsCount(authorid: Long, yearsPublication: Range, yearsCiting: Range): Long = transaction {
    val citingPapers = from(articleVenues)(av => where(av.year.in(yearsCiting)) select(av.articleid)).toSet
    if (citingPapers.isEmpty)
      0
    else {
      // find # citations from papers published within yearsCiting citing papers published by the author within yearsPublication
      from(coauthor, citations, articleVenues)((ca, ci, av) =>
        where(ca.articleid === ci.citedArticleId and av.articleid === ca.articleid and av.year.in(yearsPublication)
          and ca.authorid === authorid and ci.citationArticleId.in(citingPapers))
          compute (countDistinct(ci.citationArticleId))).single.measures
    }
  }

}