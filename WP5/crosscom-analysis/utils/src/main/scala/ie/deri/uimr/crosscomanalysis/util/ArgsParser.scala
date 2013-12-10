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

import org.apache.commons.cli.{Option => CliOption, _}
import java.io.{StringWriter, PrintWriter}

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/03/2011
 * Time: 12:54
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

trait ArgsParser extends Logging {
  private val argsPosixParser = new PosixParser
  protected val HELP_OPT = new CliOption("h", "help", false, "prints help output")
  protected val DIR_OPT = new CliOption("d", "dir", true, "output or input directory")
  protected val OUTFILE_OPT = new CliOption("f", "output-file", true, "output file")
  protected val INFILE_OPT = new CliOption("i", "input-file", true, "input file")
  protected val MAX_ITER_OPT = new CliOption("mi", "max-iter", true, "maximum number of iterations")
  protected val SEED_OPT = new CliOption("se", "seed", true, "seed of random number generator")
  protected val REPETITIONS_OPT = new CliOption("re", "repetitions", true, "number of repetitions (runs) for each configurations")
  protected var PARSED_LINE: Option[CommandLine] = None
  val COMMAND_NAME = "java" // should be overridden

  def parseArgs(args: Array[String]) = {
    PARSED_LINE = Some(argsPosixParser.parse(argOptions, args))
    PARSED_LINE.get
  }

  def helpText = {
    val sb = new StringWriter
    val pw = new PrintWriter(sb)
    val formatter = new HelpFormatter
    formatter.printHelp(pw, 120, COMMAND_NAME, "", argOptions, 0, 0, "")
    sb.toString
  }

  protected def commandLineOptions: List[CliOption] = HELP_OPT :: DIR_OPT :: OUTFILE_OPT :: Nil

  protected def printHelp {
    val formatter = new HelpFormatter
    formatter.printHelp(COMMAND_NAME, argOptions)
  }

  protected def printHelpIfAsked = {
    if (PARSED_LINE.get.hasOption(HELP_OPT.getOpt)) {
      printHelp
      true
    } else false
  }

  private def argOptions = {
    val opts = new Options
    commandLineOptions.foreach(opts.addOption(_))
    opts
  }

  protected def getOptValue(opt: CliOption) = {
    val value = PARSED_LINE.get.getOptionValue(opt.getOpt)
    if (value == null) {
      Console.err.println("Argument not supplied: " + opt.getOpt)
      printHelp
      System.exit(-1)
    }
    value
  }

  protected def getOpt(opt: CliOption) = {
    if (PARSED_LINE.get.hasOption(opt.getOpt))
      Some(PARSED_LINE.get.getOptionValue(opt.getOpt))
    else None
  }

  protected def getOpt[T](opt: CliOption, default: T) = {
    if (PARSED_LINE.get.hasOption(opt.getOpt)) {
      val o = PARSED_LINE.get.getOptionValue(opt.getOpt)
      val r = default match {
        case _: Int => o.toInt
        case _: Boolean => o.toBoolean
        case _: Short => o.toShort
        case _: Double => o.toDouble
        case _: Long => o.toLong
        case _: Float => o.toFloat
        case _: String => o
        case _: Byte => o.toByte
      }
      r.asInstanceOf[T] // to make the compiler happy
    }
    else default
  }

  protected def hasOption(o:CliOption) = PARSED_LINE.get.hasOption(o.getOpt)

  protected def mainStub(args: Array[String])(body: => Unit) {
    parseArgs(args)
    if (!printHelpIfAsked) {
      try {
        body
      } catch {
        case e: Exception => log.error(e) // catching of the last resort - just to log the error
      }
    }
  }

  def main(args:Array[String]) {
    sys.error("This method has to be overriden!")
  }
}