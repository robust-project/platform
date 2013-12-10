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

import collection.{mutable, Seq}
import org.jgrapht.util.{FibonacciHeapNode, FibonacciHeap}


/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/10/2012
 * Time: 19:17
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 *
 * This class implements a hill climbing strategy for optimisation, it computes the set of the best candidates evaluated
 * by the utility function, each of them selected by the highest utility, one at a time (individually).
 *
 * The utility function must NOT have any non-thread-safe side effects, because the candidates are processed in parallel.
 *
 * @param candidates Set of candidate solutions
 * @param submodular True - the utility function is submodular. The algorithm then exploits it to prune the space.
 */
abstract class GreedyMaximiser[T](val candidates: Seq[T], submodular: Boolean = true) extends Logging {

  private var _util: Double = 0

  def maximise(k: Int): Seq[T] = {
    _util = 0 // reset

    var _k = k
    if (k > candidates.size) {
      log.warn("There is not enough required candidates: " + k)
      log.warn("Running for k=" + candidates.size + " instead")
      _k = candidates.size
    }

    if (submodular)
      submodularMaximisation(_k)
    else {
      val (res, u) = climbHill(Nil, _k)
      _util = u
      res
    }
  }

  /**
   * Finds the optimal set by greedy search pruned by exploiting the submodularity (diminishing returns) property of
   * the utility function.
   * @param k Size of the solution.
   * @return The sequence of solutions ordered from the highest to lowest utility gain.
   */
  protected def submodularMaximisation(k: Int): Seq[T] = {
    assert(k <= candidates.size)
    val candidatesQueue = new FibonacciHeap[T]
    candidates.foreach(c => candidatesQueue.insert(new FibonacciHeapNode[T](c), -utility(Seq(c))))
    var solution = List.empty[T]
    while (solution.size < k && !candidatesQueue.isEmpty) {
      // find the best candidate and return the other back to queue
      var bestCandidate = candidatesQueue.removeMin().getData
      var bestCandidateUtility = utility(bestCandidate :: solution)
      var bestCandidateGain = bestCandidateUtility - util // the gain of the current best candidate
      // check if there's any better candidate than the current best
      var checkOthers = true
      // iterate while there's still a solution in the queue with higher previous estimate of gain
      while(!candidatesQueue.isEmpty && checkOthers) {
        val otherCandidateNode = candidatesQueue.removeMin()
        val otherCandidate = otherCandidateNode.getData
        if (-otherCandidateNode.getKey > bestCandidateGain) { // the gains are stored as negative values on the heap
          // check if the other solution has higher utility even after update
          val updatedOtherUtility = utility(otherCandidate :: solution)
          val updatedOtherGain = updatedOtherUtility - util
          if (updatedOtherGain > bestCandidateGain) {
            // swap the best and the other, remove the other from the heap - it's the best candidate in this iteration
            candidatesQueue.insert(new FibonacciHeapNode[T](bestCandidate), bestCandidateGain)
            bestCandidate = otherCandidate
            bestCandidateGain = updatedOtherGain
            bestCandidateUtility = updatedOtherUtility
          } else { // the best candidate is still the best, update the gain for the other
            candidatesQueue.insert(new FibonacciHeapNode[T](otherCandidate), updatedOtherGain)
          }
        } else {
          checkOthers = false // there was no candidate with previously estimated higher gain than the current one
          candidatesQueue.insert(new FibonacciHeapNode[T](otherCandidate), otherCandidateNode.getKey)
        }
      }
//      candidatesQueue ++= others // put all the candidates that were not better back to the queue
      solution = bestCandidate :: solution
      _util = bestCandidateUtility
      log.debug("Current best set:\n" + solution.reverse.mkString("\n"))
      log.debug("Current util: " + util)
    }
    if (k > solution.size) log.warn("Found only " + solution.size + " out of " + k + " required solutions")

    solution.reverse
  }

  protected def climbHill(alreadyChosen: List[T], k: Int): (List[T], Double) = {
    // sort by utility
    val (nextBest, util) = candidates.filterNot(alreadyChosen.contains(_)).map(c =>
      (c, utility(c :: alreadyChosen))).seq.sortBy(p => p._2).last
    log.debug("Current best set: " + (nextBest :: alreadyChosen).reverse)
    if ((alreadyChosen.size + 1) == k)
      ((nextBest :: alreadyChosen).reverse, util)
    else
      climbHill(nextBest :: alreadyChosen, k)
  }

  /**
   * @return Utility assigned to the found solution so far (or the final one at the end).
   */
  def util = _util

  /**
   * This function evaluates candidates' subsets.
   * @param candidates The ordered seq ((proposed solution),...,2nd best,the best)
   * @return Utility - the higher the better.
   */
  protected def utility(candidates: Seq[T]): Double
}