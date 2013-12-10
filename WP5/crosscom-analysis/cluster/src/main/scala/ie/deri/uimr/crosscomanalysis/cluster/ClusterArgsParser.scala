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

package ie.deri.uimr.crosscomanalysis.cluster

import ie.deri.uimr.crosscomanalysis.util.ArgsParser
import org.apache.commons.cli.Option
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties
import ie.deri.uimr.crosscomanalysis.util.Functions.parseDate

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 14/03/2011
 * Time: 12:49
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

trait ClusterArgsParser extends ArgsParser {
  val CLUSTER_FORMAT_OPT = new Option("c", "cluster-format", true, "one of: " + ClusterFormats.values.map(_.toString).mkString(", "))
  val CLUSTER_FLAG_OPT = new Option("g", "flag", true, "flag of the clustering")
  val WINDOW_SIZE_OPT = new Option("w", "window-size", true, "size of the sliding window")
  val WINDOW_SIZE_UNIT_OPT = new Option("u", "window-unit", true, "window size unit: month or year")
  val WINDOW_OVERLAP_OPT = new Option("wo", "window-overlap", true, "size of the window overlap")
  val BEGIN_YEAR_OPT = new Option("by", "begin-year", true, "year to begin with")
  val END_YEAR_OPT = new Option("ey", "end-year", true, "year to end with")
  val MATCHING_THRESHOLD_OPT = new Option("mt", "matching-threshold", true, "matching threshold, default: 0.2")
  val ALPHA_VALUE_OPT = new Option("a", "alpha", true, "significance level, default 0.05")
  val EXCLUDE_COAUTHORS_OPT = new Option("e", "exclude-coauthors", false, "Set if you want to exclude coauthors from community fellows. Default: false.")
  val MIN_CLUSTER_SIZE_OPT = new Option("mcs", "min-cluster-size", true, "Optional: Minimum cluster size. Default: 0.")
  val EXPLORED_FUTURE_OPT = new Option("ef", "explored-future", true, "Explored future - how many years of the future should be explored.")
  val MAX_YEAR_OPT = new Option("my", "max-year", true, "Maximum year to consider.")
  val BEGIN_DATE_OPT = new Option("bd", "beginDate", true, "Begin date (DD.MM.YYYY)")
  val END_DATE_OPT = new Option("ed", "endDate", true, "End date (DD.MM.YYYY)")
  val DIRECTED_OPT = new Option("dir", false, "treat the loaded graph as directed (default: undirected)")
  val UNDIRECTED_OPT = new Option("undir", false, "treat the loaded graph as undirected (default: directed)")
  val EDGE_TYPE_OPT = new Option("et", "edge-type", true, "default edge type")
  val THRESHOLD_OPT = new Option("t", "threshold", true, "threshold value")

  override protected def commandLineOptions = CLUSTER_FORMAT_OPT :: CLUSTER_FLAG_OPT :: WINDOW_SIZE_OPT ::
    WINDOW_SIZE_UNIT_OPT :: WINDOW_OVERLAP_OPT :: BEGIN_YEAR_OPT :: END_YEAR_OPT :: MATCHING_THRESHOLD_OPT ::
    ALPHA_VALUE_OPT :: EXCLUDE_COAUTHORS_OPT :: MIN_CLUSTER_SIZE_OPT ::
    EXPLORED_FUTURE_OPT :: MAX_YEAR_OPT :: BEGIN_DATE_OPT :: END_DATE_OPT :: DIRECTED_OPT :: EDGE_TYPE_OPT ::
    THRESHOLD_OPT :: super.commandLineOptions

  protected def getClusterFormat = ClusterFormats.withName(getOptValue(CLUSTER_FORMAT_OPT)).id

  protected def getClusterFlag = if (PARSED_LINE.get.hasOption(CLUSTER_FLAG_OPT.getOpt)) Some(getOptValue(CLUSTER_FLAG_OPT)) else None

  protected def getBeginDate = if (hasOption(BEGIN_DATE_OPT)) Some(parseDate(getOptValue(BEGIN_DATE_OPT))) else None

  protected def getEndDate = if (hasOption(END_DATE_OPT)) Some(parseDate(getOptValue(END_DATE_OPT), end = true)) else None
}