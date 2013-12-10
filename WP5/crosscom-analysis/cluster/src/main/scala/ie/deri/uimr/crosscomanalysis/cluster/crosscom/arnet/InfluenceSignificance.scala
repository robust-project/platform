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

package ie.deri.uimr.crosscomanalysis.cluster.crosscom.arnet

import ie.deri.uimr.crosscomanalysis.db.{DBArgsParser, SessionFactorySetter}
import ie.deri.uimr.crosscomanalysis.util.Logging
import ie.deri.uimr.crosscomanalysis.db.schemas.ArnetSchema._
import org.squeryl.PrimitiveTypeMode._
import util.Random
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph
import ie.deri.uimr.crosscomanalysis.graphdb.GraphProperties._
import collection.JavaConversions.iterableAsScalaIterable
import collection.mutable.HashMap
import ie.deri.uimr.crosscomanalysis.cluster.ClusterArgsParser
import java.io.{PrintWriter, File}
import org.apache.commons.cli.{Option => CliOption}
import org.apache.commons.math.linear.OpenMapRealMatrix
import scala.collection
import ie.deri.uimr.crosscomanalysis.cluster.slicers.Slicer

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 13/08/2012
 * Time: 18:51
 * ©2012 Digital Enterprise Research Institute, NUI Galway
 */

object InfluenceSignificance extends SessionFactorySetter with DBArgsParser with ClusterArgsParser with Logging {
  override val COMMAND_NAME = "arnet-xcom-significance"

  val ITER_OPT = new CliOption("ni", "iter-num", true, "Number of iterations")
  val MIN_VENUE_OPT = new CliOption("mv", "min-venue", true, "Minimum # distinct venues an author has to publish at")

  override def main(args: Array[String]) {
    mainStub(args) {
      setUpSessionFactory(getOptValue(DB_OPT))
      val start = getOptValue(BEGIN_YEAR_OPT).toInt
      val end = getOptValue(END_YEAR_OPT).toInt
      val window = getOptValue(WINDOW_SIZE_OPT).toInt
      val overlap = getOptValue(WINDOW_OVERLAP_OPT).toInt
      val minDistVenue = getOptValue(MIN_VENUE_OPT).toInt
      val nIter = getOptValue(ITER_OPT).toInt
      val alpha = getOptValue(ALPHA_VALUE_OPT).toFloat

      for (beginYear <- start to end by (window - overlap)) {
        val endYear = beginYear + window - 1
        log.info("Running the simmulation for slice " + beginYear + "-" + endYear)
        val infSign = new InfluenceSignificanceSlice(beginYear, endYear, minDistVenue)
        infSign.simulate(nIter, alpha, new File("sign_" + alpha + "_" + nIter + "_" + minDistVenue + "_" + beginYear +
          "-" + endYear + ".csv"))
      }
    }
  }

  override protected def commandLineOptions = DB_OPT :: HELP_OPT :: ALPHA_VALUE_OPT :: ITER_OPT ::
    MIN_VENUE_OPT ::BEGIN_YEAR_OPT :: END_YEAR_OPT :: WINDOW_OVERLAP_OPT :: WINDOW_SIZE_OPT :: Nil
}

class InfluenceSignificanceSlice(val beginYear: Int, val endYear: Int, minVenue: Int) extends Logging {
  private val MAX_VENUE_ID = 6699
  private val R = new Random(23)
  private lazy val graph = getCitationGraph()
  private lazy val (pidToAuthor, authors) = getPidToAuthor(minDistinctVenues = minVenue)

  def simulate(iter: Int, alpha: Float, outFile: File) {
    val empiricalXComCitCounts = getEmpiricalCounts
    val out = new PrintWriter(outFile)
    out.println("citing_venue,cited_venue,p,empirical_count")
    val empiricalLECount = new OpenMapRealMatrix(MAX_VENUE_ID + 1, MAX_VENUE_ID + 1) // venue x venue -> times the empirical count is lower or equal
    for (i <- 1 to iter) {
      log.debug("Starting the iteration: " + i)
      // randomize the graph/venues and count the citations
      authors.foreach(_.shuffleVenues()) // randomize venues
      val pidToAuthorShuffled = pidToAuthor.mapValues(R.shuffle(_).head) // for each paper, randomly choose one co-author which will decide the shuffled venue
      log.debug("Shuffling done, starting to count")
      val randomCitCount = new OpenMapRealMatrix(MAX_VENUE_ID + 1, MAX_VENUE_ID + 1) // venue x venue -> times the empirical count is lower or equal // venue X venue -> citation count (i->j)
      for ((pid, a) <- pidToAuthorShuffled; citing = graph.getVertex(pid) if citing != null) {
        val citingRV = a.getRandomVenue(pid)
        for (outE <- citing.getOutEdges()) {
          val citedPID = outE.getInVertex.getId.toString.toInt
          if (pidToAuthorShuffled.contains(citedPID)) {
            val citedRV = pidToAuthorShuffled(citedPID).getRandomVenue(citedPID)
            randomCitCount.setEntry(citingRV, citedRV, randomCitCount.getEntry(citingRV, citedRV) + 1)
          }
        }
      }
      val randDir = new File("random_counts" + File.separator + beginYear + "-" + endYear)
      randDir.mkdirs()
      val randO = new PrintWriter(randDir.getAbsolutePath + File.separator + "random_counts_" + i + ".csv")
      randO.println("citing,cited,random_count,empirical_count,emp_le_count")
      for (i <- 1 to MAX_VENUE_ID; j <- 1 to MAX_VENUE_ID) {
        if (empiricalXComCitCounts.getEntry(i, j) <= randomCitCount.getEntry(i, j)) {
          empiricalLECount.setEntry(i, j, empiricalLECount.getEntry(i, j) + 1)
        }
        if (empiricalXComCitCounts.getEntry(i, j) > 0 || randomCitCount.getEntry(i, j) > 0) {
          randO.println(i + "," + j + "," + randomCitCount.getEntry(i, j) + "," + empiricalXComCitCounts.getEntry(i, j) + "," + empiricalLECount.getEntry(i, j) )
        }
      }
      randO.close()
    }
    for (i <- 1 to MAX_VENUE_ID; j <- 1 to MAX_VENUE_ID) {
      if ((empiricalLECount.getEntry(i, j) / iter) < alpha) {
        out.println(i + "," + j + "," + empiricalLECount.getEntry(i, j) / iter + "," + empiricalXComCitCounts.getEntry(i, j))
      }
    }
    out.close()
  }

  private def getEmpiricalCounts = {
    val xComCitCounts = new OpenMapRealMatrix(MAX_VENUE_ID + 1, MAX_VENUE_ID + 1) // venue X venue -> citation count (i->j)
    log.debug("About to start the calculation of the empirical counts")
    for ((pid, authors) <- pidToAuthor; citing = graph.getVertex(pid) if citing != null) {
      val a = authors.head // it does not matter which co-author we choose, because all have the same original venue
      val citingOrigVenue = a.origVenues(pid)
      for (citation <- citing.getOutEdges()) {
        val citedPID = citation.getInVertex.getId.toString.toInt
        if (pidToAuthor.contains(citedPID)) {
          val citedOrigVenue = pidToAuthor(citedPID).head.origVenues(citedPID)
          xComCitCounts.setEntry(citingOrigVenue, citedOrigVenue, xComCitCounts.getEntry(citingOrigVenue, citedOrigVenue) + 1)
        }
      }
    }
    log.debug("Finished with empirical citation counts")

    xComCitCounts
  }

  private def getCitationGraph(excludedPapers: Set[Int] = Set.empty) = inTransaction {
    val g = new TinkerGraph
    def addVertex(id: Int) = {
      val v = g.getVertex(id.asInstanceOf[AnyRef])
      if (v == null) g.addVertex(id.asInstanceOf[AnyRef])
      else v
    }
    log.debug("About to load the citation graph")
    var count = 0
    for (c <- from(citationWithYear)(c => where(c.yearCited.between(beginYear, endYear) and c.yearCiting.between(beginYear, endYear)) select(c))
         if !excludedPapers.contains(c.citing) && !excludedPapers.contains(c.cited)) {
      val citing = addVertex(c.citing)
      val cited = addVertex(c.cited)
      val citation = g.addEdge(c.id, citing, cited, CITES)
      count += 1
      if (count % 1000 == 0) log.debug("Loaded " + count + " citations so far.")
    }
    log.debug("Loaded graph with: " + g.getVertices.size + " vertices, and " + g.getEdges.size + " edges")

    g
  }

  private def getPidToAuthor(minDistinctVenues: Int = 1) = inTransaction {
    var authors = Set.empty[Author]
    var count = 0
    val m = new HashMap[Int, collection.immutable.Set[Author]]
    // load only authors who have (been) cited at least once
    for (r <- from(authorship, citation, paper)((a, c, p) =>
      where(a.paperid === p.id and (a.paperid === c.citing or a.paperid === c.cited) and p.year.between(beginYear, endYear))
        groupBy(a.authorid)
        compute (countDistinct(p.venue)))) {
      val aid = r.key
      val a = new Author(aid, beginYear, endYear)
      if (r.measures >= minDistinctVenues) {
        authors += a
        for (pid <- a.origVenues.keys) {
          if (m.contains(pid)) {
            m(pid) = m(pid) + a
            count += 1
          } else {
            m(pid) = Set(a)
          }
        }
        if (authors.size % 1000 == 0) log.debug("Loaded " + authors.size + " authors so far.")
      }
    }
    log.debug("Loaded " + authors.size + " authors (" + count + " co-authors) and " + m.size + " papers altogether.")

    (m.mapValues(_.toList), authors.par)
  }
}

class Author(val aid: Int, beginYear: Int, endYear: Int) {
  private val R = new Random(23)
  val origVenues = inTransaction {
    (from(authorship, paper)((a, p) => where(a.authorid === aid and a.paperid === p.id and p.year.between(beginYear,endYear))
      select ((p.id, p.venue)))).toMap
  }
  private var venues = origVenues

  def shuffleVenues() {
    val shuffledKeys = R.shuffle(venues.keys).toSeq
    val values  = R.shuffle(venues.values).toSeq
    venues = (for (i <- 0 until shuffledKeys.size) yield (shuffledKeys(i), values(i))).toMap
  }

  def getRandomVenue(pid: Int) = venues(pid)
}