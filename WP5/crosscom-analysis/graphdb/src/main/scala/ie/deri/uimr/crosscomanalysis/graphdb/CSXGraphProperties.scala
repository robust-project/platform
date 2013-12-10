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

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 02/04/2011
 * Time: 19:33
 * ©2011 Digital Enterprise Research Institute, NUI Galway.
 */

object CSXGraphProperties extends GraphPropertiesLike {
  //edges
  val AUTHOR_OF = Property("author_of")
  // properties
  val PAPERS = Property[Array[String]]("papers")
  val AUTHOR_ID = Property[Long]("author_id")
  val AUTH_CLUSTER_ID = Property[Long]("auth_cluster_id")
  val CLUSTER_ID = Property[Long]("cluster_id")
  val AFFILIATION = Property[String]("affiliation")
  val EMAIL = Property[String]("email")
  val NAME = Property[String]("name")
  val ADDRESS = Property[String]("address")
  val COCIT_COUNT = Property[Long]("cocit_count")
  // node types
  val CLUSTER = Property[String]("cluster")
  val AUTHOR = Property[String]("author")
}