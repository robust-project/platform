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

import comparator.{CocitClusteringComparator, CompareClusterings, ComputeGroupBetweenness}
import crosscom._
import arnet.{InfluenceSignificance, ArnetCommunityInfluence}
import crosscom.boardsie.BoardsCommunityMembership
import csx.CSXCommunityInfluence
import exporters.commgraph.CommunityGraphExporter
import exporters.{PajekExporter, EdgelistExporter}
import feature.comp._
import feature.comp.boardsie.{NewMembers, Persistence, PostsCount}
import feature.{IsolatedCommunityUsersSize, NumberOfCommunitiesPerSlice, FeatureCSVExport}
import importers.OslomBatchImporter
import optimisation.{GreedyDBClusterSeedFinder, GreedyVertexSeedFinder}
import ie.deri.uimr.crosscomanalysis.cluster.simulation.infdif.{PartialNetInfCascadeExperiment, XComInfCascadeExperiment}
import ie.deri.uimr.crosscomanalysis.cluster.slicers._
import viz.CocitVisualizer
import ie.deri.uimr.crosscomanalysis.util.Application
import ie.deri.uimr.crosscomanalysis.cluster.centrality.{ArnetLayeredInDegree, BoardsLayeredInDegree}
import ie.deri.uimr.crosscomanalysis.cluster.crosscom.sapscn.SAPCommunityMembership

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/02/2011
 * Time: 19:24
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */

object App extends Application {

  val commands = ComputeGroupBetweenness :: CSXSlicer :: EdgelistExporter :: PajekExporter :: OslomBatchImporter ::
    CocitVisualizer :: FeatureEvolutionMiner :: MergeSplitWekaExporter :: DiscourseLeadersMinerCosine ::
    CSXRangeSlicer :: MineCitationGains :: CompareClusterings :: CocitClusteringComparator :: CitationBoostFactor :: RelativeEdgeDensity ::
    RelativeSize :: SizeChange :: GroupBetweenness :: AvgClustCoeff :: BoardsIESlicer :: CommunityGraphExporter :: NewVerticesCount ::
    OverlapDistribution :: FeatureCSVExport :: Size :: PostsCount :: BoardsCommunityMembership :: NumberOfCommunitiesPerSlice ::
    InDegreeGroupCentralityComp :: XComInfCascadeExperiment :: SAPSCNSlicer :: IsolatedCommunityUsersSize :: Persistence ::
    RelativeSizeChange :: NewMembers :: CSXCommunityInfluence :: ArnetCommunityInfluence :: InfluenceSignificance ::
    GreedyVertexSeedFinder :: GreedyDBClusterSeedFinder :: PartialNetInfCascadeExperiment ::
    ArnetLayeredInDegree :: BoardsLayeredInDegree :: ArnetCitationSlicer ::
    SAPCommunityMembership :: ArnetCoauthorshipSlicer :: Nil
}