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
 * Time: 19:54
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */
package ie.deri.uimr.crosscomanalysis.util.test
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class LoggingTest extends FunSuite with Logging {

  test("Logger should have the same name as its class") {
    log.info("Test")
    log.error(new Exception("test exception"))
    assert(log.name === "ie.deri.uimr.crosscomanalysis.util.test.LoggingTest")
  }
}