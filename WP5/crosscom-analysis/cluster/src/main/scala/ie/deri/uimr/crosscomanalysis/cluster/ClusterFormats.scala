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

import ie.deri.uimr.crosscomanalysis.util.RichEnumeration

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 09/02/2011
 * Time: 15:35
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object ClusterFormats extends RichEnumeration {
  type ClusterFormats = Value

  val CONGA = Value("conga")
  val KWTT = Value("wakita-tsurumi")
  val LOUVAIN_SINGLE = Value("louvain-ss") // 2
  val INFOMAP = Value("infomap")           // 3
  // Derek's coclustering algo
  val COCLUSTER = Value("spectral-coclustering")
  val MOSES = Value("moses")
  val GCE = Value("gce")
  // multi-slice Louvain
  val LOUVAIN_MS = Value("louvain-ms") // 7
  val OSLOM = Value("oslom")           // 8
  val EXPLICIT = Value("explicit") // 9
}