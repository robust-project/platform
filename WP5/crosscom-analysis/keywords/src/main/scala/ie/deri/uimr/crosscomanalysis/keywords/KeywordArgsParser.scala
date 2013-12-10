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

import ie.deri.uimr.crosscomanalysis.util.ArgsParser
import org.apache.commons.cli.Option

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/09/2011
 * Time: 12:10
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

trait KeywordArgsParser extends ArgsParser {

  val TOKENIZE_OPT = new Option("t", false, "Whether to also tokenize the keyphrases or not (tokens will be added). Default: false.")

  override protected def commandLineOptions = TOKENIZE_OPT :: super.commandLineOptions
}