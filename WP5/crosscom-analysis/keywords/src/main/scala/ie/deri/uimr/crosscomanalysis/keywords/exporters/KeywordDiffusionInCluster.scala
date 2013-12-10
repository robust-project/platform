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

package ie.deri.uimr.crosscomanalysis.keywords.exporters

import org.squeryl.PrimitiveTypeMode._
import ie.deri.uimr.crosscomanalysis.db.schemas.BoardsIE._
import ie.deri.uimr.crosscomanalysis.db.schemas.GraphSchema._
import ie.deri.uimr.crosscomanalysis.db.tables.graph.Cluster
import org.jgrapht.graph.{DefaultEdge, SimpleDirectedGraph}
import collection._
import ie.deri.uimr.crosscomanalysis.keywords.tfidf.StopWords._
import ie.deri.uimr.crosscomanalysis.util.Functions._
import ie.deri.uimr.crosscomanalysis.keywords.tokenizer.AbstractTokenizer
import org.jgrapht.traverse.BreadthFirstIterator
import ie.deri.uimr.crosscomanalysis.util.Logging
import collection.JavaConversions.iterableAsScalaIterable
import collection.JavaConversions.asScalaIterator

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 27/06/2013
 * Time: 14:25
 * ©2013 Digital Enterprise Research Institute, NUI Galway
 *
 * Computes the language diffusion score as used by David Huffaker, HCR 2010: Online Leaders and Social Influence.
 */
class KeywordDiffusionInCluster(val cluster: Cluster, val minWordLenght: Int, val tokenizer: AbstractTokenizer)
  extends Logging {

  private val Quote = """(?i)\[quote\].*\[/quote\]""".r
  private val Tag = """\[.+\]""".r

  /**
   * @return Language diffusion score for each user from the cluster within the slice.
   */
  def computeScore(): (Map[Long, Double], Map[Long, Double], Map[Long, Double]) = {
    val g = new SimpleDirectedGraph[Long, DefaultEdge](classOf[DefaultEdge])
    val postAuthor = mutable.Map.empty[Long, Long] // postid -> authorid
    val postContent = mutable.Map.empty[Long, Set[String]] // postid -> set of keywords
    val authorDiffusionScores = mutable.Map.empty[Long, Double] // orig. Huffaker's scores
    val authorDiffusionScoresJaccard = mutable.Map.empty[Long, Double] // like Huffaker's, but Jaccard coefficients
    val authorNonEmptyPostCount = mutable.Map.empty[Long, Int] // author -> # non empty posts

    transaction {
      for (authorId <- from(clusterStructure)(cs => where(cs.id === cluster.id) select(cs.vertexid))) {
        authorDiffusionScores(authorId) = 0
        authorDiffusionScoresJaccard(authorId) = 0
      }
    }

    log.debug("Loading the post authors and content")
    transaction {
      for ((postId, authorId, content) <- from(posts, threads)((p, t) =>
        where(p.threadid === t.threadid and t.forumid === cluster.index and
          p.posteddate >= cluster.beginDate and p.posteddate <= cluster.endDate)
          select(p.postid, p.userid, p.content))) {
        postAuthor(postId) = authorId
        val tokenizedContent = tokenizeAndFilter(content)
        postContent(postId) = tokenizedContent
        if (!tokenizedContent.isEmpty) {
          authorNonEmptyPostCount(authorId) = 1 + authorNonEmptyPostCount.getOrElse(authorId, 0)
        }
      }
    }
    log.debug("Loading the replies graph")
    transaction {
      for ((replying, replied) <- from(replies, posts, threads, posts, threads)((r, p1, t1, p2, t2) =>
        where(r.origposteddate >= cluster.beginDate and r.origposteddate <= cluster.endDate and
          r.replyingposteddate >= cluster.beginDate and r.replyingposteddate <= cluster.endDate and
          p1.threadid === t1.threadid and t1.forumid === cluster.index and
          p1.postid === r.originalpostid and
          p2.threadid === t2.threadid and t2.forumid === cluster.index and
          p2.postid === r.replyingpostid)
          select(r.replyingpostid, r.originalpostid))) {
        g.addVertex(replying)
        g.addVertex(replied)
        g.addEdge(replied, replying) // reverse => information flow graph
      }

    }
    log.debug("Computing the diffusion scores")
    val roots = g.vertexSet().filter(v => g.inDegreeOf(v) == 0).toSet
    for (root <- roots;
         rootContent = postContent(root)
         if !rootContent.isEmpty) {
      val rootAuthor = postAuthor(root)
      val nodeIter = new BreadthFirstIterator(g, root)
      nodeIter.next() // the first one is root
      for (postId <- nodeIter) {
        val currentPostAuthor = postAuthor(postId)
        if (currentPostAuthor != rootAuthor) {
          authorDiffusionScores(rootAuthor) = authorDiffusionScores(rootAuthor) +
            postContent(postId).intersect(rootContent).size / rootContent.size.toDouble
          authorDiffusionScoresJaccard(rootAuthor) = authorDiffusionScoresJaccard(rootAuthor) +
            postContent(postId).intersect(rootContent).size / (rootContent.size.toDouble + postContent(postId).size)
        }
        var parent = g.getEdgeSource(g.incomingEdgesOf(postId).head) // a post is in reply to exactly one parent
        while (parent != root) {
          val parentAuthor = postAuthor(parent)
          val parentContent = postContent(parent)
          if (currentPostAuthor != parentAuthor && parentContent.size > 0) {
            authorDiffusionScores(parentAuthor) = authorDiffusionScores(parentAuthor) +
             postContent(postId).intersect(parentContent).size / parentContent.size.toDouble
            authorDiffusionScoresJaccard(parentAuthor) = authorDiffusionScoresJaccard(parentAuthor) +
             postContent(postId).intersect(parentContent).size / (parentContent.size.toDouble + postContent(postId).size)
          }
          parent = g.getEdgeSource(g.incomingEdgesOf(parent).head)
        }
      }
    }
    log.debug("Done")

    (authorDiffusionScores, authorDiffusionScoresJaccard,
      authorDiffusionScoresJaccard.map(p => (p._1, p._2 / authorNonEmptyPostCount.getOrElse(p._1, 1))).toMap) // normalize by the # posts
  }

  private def tokenizeAndFilter(s: String) =
    if (s == null || s.trim().isEmpty) Set.empty[String]
    else tokenizer.tokenize(Tag.replaceAllIn(Quote.replaceAllIn(s, ""), "")).toSet.filter(t =>
      !isStopWord(t) && t.length >= minWordLenght).map(t => stem(t.toLowerCase))
}