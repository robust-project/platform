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

package ie.deri.uimr.crosscomanalysis.cluster.simulation.r

import org.rosuda.REngine.{REngine, REngineOutputInterface, REngineCallbacks}
import ie.deri.uimr.crosscomanalysis.util.Logging
import org.rosuda.JRI.Rengine
import org.rosuda.REngine.JRI.JRIEngine

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 15/03/2013
 * Time: 13:55
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 *
 * Holds a reference to REngine object. There can be only one instance of the object in the memory.
 */
object R extends Logging {

  lazy val rEngine = createR()

  private def createR() = {
    assert(Rengine.versionCheck(), "REngine version mismatch")
    JRIEngine.createEngine(Array("--no-save"), new RLogger, false)
  }

  def eval(cmd: String) = {
    log.debug(cmd)
    rEngine.parseAndEval(cmd)
  }

  def shutdownR() {
    rEngine.close()
  }

  def cleaR() {
    rEngine.parseAndEval("rm(list=ls())")
  }

}

class RLogger extends REngineCallbacks with REngineOutputInterface with Logging {
  def RWriteConsole(re: REngine, message: String, oType: Int) {
    log.debug(message + ", type: " + oType)
  }

  def RShowMessage(re: REngine, message: String) {
    log.warn(message)
  }

  def RFlushConsole(re: REngine) {}
}