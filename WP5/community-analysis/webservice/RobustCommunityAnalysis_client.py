##################################################
#
# Copyright 2013 DERI, National University of Ireland Galway.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
#
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# file: RobustCommunityAnalysis_client.py
# 
# client stubs generated by "ZSI.generate.wsdl2python.WriteServiceModule"
#     /usr/bin/wsdl2py --complexType RobustCommunityAnalysis.wsdl
# 
##################################################

from RobustCommunityAnalysis_types import *
import urlparse, types
from ZSI.TCcompound import ComplexType, Struct
from ZSI import client
from ZSI.schema import GED, GTD
import ZSI
from ZSI.generate.pyclass import pyclass_type

# Locator
class RobustCommunityAnalysisLocator:
    RobustCommunityAnalysisSoap_address = "http://uimr.deri.ie/services/RobustCommunityAnalysis/"
    def getRobustCommunityAnalysisSoapAddress(self):
        return RobustCommunityAnalysisLocator.RobustCommunityAnalysisSoap_address
    def getRobustCommunityAnalysisSoap(self, url=None, **kw):
        return RobustCommunityAnalysisSoapSOAP(url or RobustCommunityAnalysisLocator.RobustCommunityAnalysisSoap_address, **kw)

# Methods
class RobustCommunityAnalysisSoapSOAP:
    def __init__(self, url, **kw):
        kw.setdefault("readerclass", None)
        kw.setdefault("writerclass", None)
        # no resource properties
        self.binding = client.Binding(url=url, **kw)
        # no ws-addressing

    # op: getDataSources
    def getDataSources(self, request, **kw):
        if isinstance(request, getDataSourcesRequest) is False:
            raise TypeError, "%s incorrect request type" % (request.__class__)
        # no input wsaction
        self.binding.Send(None, None, request, soapaction="", **kw)
        # no output wsaction
        response = self.binding.Receive(getDataSourcesResponse.typecode)
        return response

    # op: getCommunities
    def getCommunities(self, request, **kw):
        if isinstance(request, getCommunitiesRequest) is False:
            raise TypeError, "%s incorrect request type" % (request.__class__)
        # no input wsaction
        self.binding.Send(None, None, request, soapaction="", **kw)
        # no output wsaction
        response = self.binding.Receive(getCommunitiesResponse.typecode)
        return response

    # op: getIndicators
    def getIndicators(self, request, **kw):
        if isinstance(request, getIndicatorsRequest) is False:
            raise TypeError, "%s incorrect request type" % (request.__class__)
        # no input wsaction
        self.binding.Send(None, None, request, soapaction="", **kw)
        # no output wsaction
        response = self.binding.Receive(getIndicatorsResponse.typecode)
        return response

    # op: getHealthScore
    def getHealthScore(self, request, **kw):
        if isinstance(request, getHealthScoreRequest) is False:
            raise TypeError, "%s incorrect request type" % (request.__class__)
        # no input wsaction
        self.binding.Send(None, None, request, soapaction="", **kw)
        # no output wsaction
        response = self.binding.Receive(getHealthScoreResponse.typecode)
        return response

    # op: getHealthScores
    def getHealthScores(self, request, **kw):
        if isinstance(request, getHealthScoresRequest) is False:
            raise TypeError, "%s incorrect request type" % (request.__class__)
        # no input wsaction
        self.binding.Send(None, None, request, soapaction="", **kw)
        # no output wsaction
        response = self.binding.Receive(getHealthScoresResponse.typecode)
        return response

getDataSourcesRequest = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getDataSourcesRequest").pyclass

getDataSourcesResponse = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getDataSourcesResponse").pyclass

getCommunitiesRequest = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getCommunitiesRequest").pyclass

getCommunitiesResponse = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getCommunitiesResponse").pyclass

getIndicatorsRequest = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getIndicatorsRequest").pyclass

getIndicatorsResponse = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getIndicatorsResponse").pyclass

getHealthScoreRequest = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getHealthScoreRequest").pyclass

getHealthScoreResponse = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getHealthScoreResponse").pyclass

getHealthScoresRequest = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getHealthScoresRequest").pyclass

getHealthScoresResponse = GED("http://robust.softwaremind.pl/HealthIndicatorService", "getHealthScoresResponse").pyclass