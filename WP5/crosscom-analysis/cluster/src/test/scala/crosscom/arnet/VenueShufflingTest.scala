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

package crosscom.arnet

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.db.schemas.ArnetSchema._
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.arnet.Author
import collection.mutable

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 20/08/2012
 * Time: 19:50
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
@RunWith(classOf[JUnitRunner])
class VenueShufflingTest extends FunSuite with SessionFactorySetter {
  setUpSessionFactory("arnet")

  test("Shuffling has to randomize venues repeatedly!"){
//    transaction {
//      val minDistinctVenues = 1 // effectively does not filter
//      val authors = new mutable.HashSet[Author]
//      for (r <- from(authorship, citation, paper)((a, c, p) =>
//        where(a.paperid === p.id and (a.paperid === c.citing or a.paperid === c.cited) and a.authorid.between(1,50))
//          groupBy(a.authorid)
//          compute (countDistinct(p.venue)))) {
//        val aid = r.key
//        val a = new Author(aid)
//        if (r.measures >= minDistinctVenues) {
//          authors += a
//        }
//      }
//      println("Loaded " + authors.size + " authors for testing.")
//      authors.foreach(_.shuffleVenues())
//      var collisionCount = 0
//      var totalCount = 0
//      for (i <- 1 to 10) {
//        for (a <- authors; paper <- a.origVenues.keys) {
//          val previousRV = a.getRandomVenue(paper)
//          a.shuffleVenues()
//          if (previousRV == a.getRandomVenue(paper)) {
//            collisionCount += 1 // count "collisions"
//          }
//          totalCount += 1
//        }
//      }
//      println("total count: " + totalCount + ", collision count: " + collisionCount)
//      assert(totalCount > collisionCount)
//    }
  }
}
