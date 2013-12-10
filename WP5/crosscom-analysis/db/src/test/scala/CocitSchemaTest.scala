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

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 29/01/2011
 * Time: 20:54
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

import ie.deri.uimr.crosscomanalysis.db.SessionFactorySetter
import ie.deri.uimr.crosscomanalysis.db.tables.cocit.KeywordID
import ie.deri.uimr.crosscomanalysis.db.schemas.CocitSchema.keywordIdEMCSX
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.util.Config.config

@RunWith(classOf[JUnitRunner])
class CocitSchemaTest extends FunSuite with SessionFactorySetter {
//  setUpSessionFactory(config.get("csxcocit.db.url").get, config.get("db.user").get, config.get("db.pass").get)
//
//  test("Interrupted transaction should not be stored") {
//    transaction {
//      keywordIdEMCSX.deleteWhere(kid => 1 === 1) // erase all keyword ids
//    }
//    var c = -1l
//    transaction {
//      c = from(keywordIdEMCSX)(kid => compute(count(kid.id))).single.measures
//    }
//    assert(c == 0)
//
//    try {
//      transaction {
//        keywordIdEMCSX insert new KeywordID(1, "keyword", "origKeyword")
//        error("An error occurred!")
//      }
//    } catch {
//      case _ => // do nothing - it is a test
//    }
//    transaction {
//      c = from(keywordIdEMCSX)(kid => compute(count(kid.id))).single.measures
//    }
//    assert(c == 0)
//
//    transaction {
//      keywordIdEMCSX insert new KeywordID(1, "keyword", "origKeyword")
//      c = from(keywordIdEMCSX)(kid => compute(count(kid.id))).single.measures
//      assert(c == 1) // inside transaction
//    }
//    transaction {
//      c = from(keywordIdEMCSX)(kid => compute(count(kid.id))).single.measures
//    }
//    assert(c == 1) // commited (outside transaction)
//  }
}