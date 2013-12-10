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

package ie.deri.uimr.crosscomanalysis.cluster.feature

import ie.deri.uimr.crosscomanalysis.util.RichEnumeration

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 12/09/2011
 * Time: 18:10
 * ©2011 Digital Enterprise Research Institute, NUI Galway
 */

object FeatureType extends RichEnumeration {

  type FeatureType = Value

  val RELATIVE_EDGE_DENSITY = Value(1, "relative_edge_density")
  val AVG_CLUST_COEFF = Value(2, "avg_clust_coeff")
  val RELATIVE_SIZE = Value(3, "rel_size")
  val SIZE_CHANGE = Value(4, "size_change")
  val GROUP_BETWEENNESS = Value(5, "group_betwenness")
  val NORM_GROUP_BETWEENNESS = Value(6, "normalized_group_betweenness")
  val NORM_AGE = Value(7, "normalized_age")
  val POST_COUNT = Value(8, "post_count")
  val OVERLAP_SET_COUNT = Value(9, "overlap_set_count") // each overlapping user is counted exactly once
  val OVERLAP_MULTI_COUNT = Value(10, "overlap_multi_count") // each overlapping user counted multiple times if she's a member of more than one community
  val NEW_VERTICES_COUNT = Value(11, "new_vertices_count")
  val SIZE = Value(12, "size")
  val GROUP_INDEGREE = Value(13, "group_indegree")
  val RELATIVE_SIZE_CHANGE = Value(14, "relative_size_change")
  val THREAD_PERSISTENCE_MEAN = Value(15, "thread_persistence_mean")
  val THREAD_PERSISTENCE_STDEV = Value(16, "thread_persistence_stdev")
  val POST_PERSISTENCE_MEAN = Value(17, "post_persistence_mean")
  val POST_PERSISTENCE_STDEV = Value(18, "post_persistence_stdev")
  val NEW_MEMBERS = Value(19, "new_members_count")
}