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

package ie.deri.uimr.crosscomanalysis.util

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 21/11/2011
 * Time: 11:01
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

abstract class Application {

  val commands: List[ArgsParser]

  protected def helpText = commands.map(_.COMMAND_NAME).mkString("Supported commands:\n\n","\n","")

  def main(args: Array[String]) {
    if (args.length > 0) {
      val command = commands.find(_.COMMAND_NAME == args(0))
      if (command.isDefined)
        command.get.main(args.slice(1, args.length))
      else
        println(helpText)
    } else
      println(helpText)
  }
}