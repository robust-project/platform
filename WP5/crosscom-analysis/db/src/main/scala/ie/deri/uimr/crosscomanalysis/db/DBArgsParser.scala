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

package ie.deri.uimr.crosscomanalysis.db

import ie.deri.uimr.crosscomanalysis.util.ArgsParser
import tables.graph.SliceTypes
import org.apache.commons.cli.Option

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/03/2011
 * Time: 14:14
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

trait DBArgsParser extends ArgsParser {

  val SLICE_TYPE_OPT = new Option("s", "slice-type", true, "one of: " + SliceTypes.values.mkString(", "))
  val SLICE_FLAG_OPT = new Option("sf", "slice-flag", true, "one of: " + SliceTypes.values.map(s =>
    s.substring(s.indexOf("_") + 1, s.length)).mkString(", "))        // todo fix: if there's no '_', the type should not be used for a flag
  val DB_OPT = new Option("m", "database", true, "name of the database - consult config.properties")

  override protected def commandLineOptions = SLICE_TYPE_OPT :: SLICE_FLAG_OPT :: DB_OPT :: super.commandLineOptions

  protected def getSliceType =
    if (hasOption(SLICE_FLAG_OPT))
      SliceTypes.withName(getOptValue(SLICE_TYPE_OPT) + "_" + getOptValue(SLICE_FLAG_OPT)).id
    else
      SliceTypes.withName(getOptValue(SLICE_TYPE_OPT)).id
}