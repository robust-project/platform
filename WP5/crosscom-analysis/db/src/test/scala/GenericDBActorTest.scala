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

import ie.deri.uimr.crosscomanalysis.db.{DBQueryMessage, GenericDBActor}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import org.squeryl.PrimitiveTypeMode._
import actors.Actor

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 28/11/2011
 * Time: 13:26
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

//@RunWith(classOf[JUnitRunner])
//class GenericDBActorTest extends FunSuite {
//
//  test("Query without session should fail") {
//    val query = from(networkSlice)(ns => compute(count(ns.id)))
//    val db = new GenericDBActor("cocit")
//    db.start()
//    val a = new TestActor
//    a.start()
//    a ! "Ahoj"
//    db ! DBQueryMessage(query, a)
//    a ! "Zdar"
//  }
//}
//
//class TestActor extends Actor {
//  def act() {
//    loop {
//      receive {
//        case m => println(m)
//      }
//    }
//  }
//}m