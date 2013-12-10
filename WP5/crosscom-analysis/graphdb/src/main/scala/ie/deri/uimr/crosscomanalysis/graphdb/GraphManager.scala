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

package ie.deri.uimr.crosscomanalysis.graphdb

import com.tinkerpop.blueprints.pgm.TransactionalGraph
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 16/02/2011
 * Time: 19:21
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

trait GraphManager {

  protected def createNeo4jTransactionalGraph(path: String, conf: java.util.Map[String, String]) = {
    val g = new Neo4jGraph(path, conf)
    g.setMaxBufferSize(0)
    g
  }

  protected def createNeo4jTransactionalGraph(path: String) = {
    val g = new Neo4jGraph(path)
    g.setMaxBufferSize(0)
    g
  }

  protected def graphTransaction[A](action: => A)(implicit g: TransactionalGraph): A = {
    try {
      g.startTransaction()
      val res = action
      g.stopTransaction(TransactionalGraph.Conclusion.SUCCESS)
      res
    } catch {
      case e => {
        g.stopTransaction(TransactionalGraph.Conclusion.FAILURE)
        throw e
      }
    }
  }

  protected def graphTransaction[A](g: TransactionalGraph)(action: => A): A = graphTransaction(action)(g)
}