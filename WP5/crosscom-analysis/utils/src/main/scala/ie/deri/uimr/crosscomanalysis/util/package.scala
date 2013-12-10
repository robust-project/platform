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

package ie.deri.uimr.crosscomanalysis

import collection.mutable
import org.apache.commons.math.random.MersenneTwister
import collection.generic.CanBuildFrom
import collection.mutable.ArrayBuffer

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/11/2012
 * Time: 13:49
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */
package object util {

  /**
   * Implements Floyd's algorithm - see http://eyalsch.wordpress.com/2010/04/01/random-sample/
   * @param items Universe to sample from
   * @param m Size of the sample
   * @param rnd Random number generator
   * @tparam A Universe elements' type
   * @return The random sample of size m.
   */
  def randomSample[A](items: collection.Seq[A], m: Int, rnd: MersenneTwister): collection.Set[A] = {
    val res = new mutable.HashSet[A]
    val n = items.size
    assert(n >= m)

    for (i <- (n - m) until n) {
      val pos = rnd.nextInt(i + 1)
      val item = items(pos)
      if (res.contains(item))
        res.add(items(i))
      else
        res.add(item)
    }

    res
  }

  /** Returns a new collection of the same type in a randomly chosen order.
    *
    *  @return         the shuffled collection
    */
  def shuffle[T, CC[X] <: TraversableOnce[X]](xs: CC[T], rnd: MersenneTwister)(implicit bf: CanBuildFrom[CC[T], T, CC[T]]): CC[T] = {
    val buf = new ArrayBuffer[T] ++= xs

    def swap(i1: Int, i2: Int) {
      val tmp = buf(i1)
      buf(i1) = buf(i2)
      buf(i2) = tmp
    }

    for (n <- buf.length to 2 by -1) {
      val k = rnd.nextInt(n)
      swap(n - 1, k)
    }

    (bf(xs) ++= buf).result()
  }
}
