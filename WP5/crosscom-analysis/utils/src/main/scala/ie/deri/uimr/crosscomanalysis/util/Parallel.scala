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

import org.apache.commons.cli.Option

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 25/05/2013
 * Time: 10:57
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 */
trait Parallel {

  this: ArgsParser =>

  protected val PARALLELISM_DEGREE_OPT = new Option("p", "par-degree", true, "Optional: degree of parallelism (suggested number of threads)")

  protected def setParallelismDegree() {
    setParallelismDegree(getOptValue(PARALLELISM_DEGREE_OPT).toInt)
  }

  protected def setParallelismDegree(d: Int) {
    collection.parallel.ForkJoinTasks.defaultForkJoinPool.setParallelism(d)
  }

}
