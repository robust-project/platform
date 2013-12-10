#!/usr/bin/python
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

"""RobustCommunityAnalysis web service client."""

import sys
import argparse
import random
from ZSI.version import Version as zsiversion

################################################################################

from webservice.RobustCommunityAnalysis_client import *

################################################################################

def main():
    """Main function."""
    # Process client arguments
    parser = argparse.ArgumentParser(description="WebService testing client.")
    parser.add_argument( "-u", "--url",
                   help="service URL for connecting", metavar="URL" )
    parser.add_argument( "-k", "--accessKey", required=True,
                   help="service access key", metavar="KEY" )
    arguments = parser.parse_args()

    # Print ZSI installed version
    print "Installed ZSI version: {0}".format(zsiversion)
    print

    # Get access to service
    loc = RobustCommunityAnalysisLocator()
    service = loc.getRobustCommunityAnalysisSoap( url=arguments.url,
                                                 tracefile=sys.stderr )
    print "Service located in: {url}".format(
          url=loc.RobustCommunityAnalysisSoap_address)
    print

    # Test getDataSources()
    print "Testing getDataSources() ..."
    try:
        request = getDataSourcesRequest()
        request.set_element_accessKey( arguments.accessKey )
        response = service.getDataSources( request )
        datasources = response.get_element_datasources()

        _datasources = []
        for datasource in datasources.get_element_datasource():
            _datasources += [ datasource.get_element_id() ]
            print "DataSource ID {0}: {1}".format(
               datasource.get_element_id(), datasource.get_element_title()
            )
        dataSourceId = random.choice(_datasources)
        print "DataSource ID for further tests: {dataSourceId}"\
              .format(dataSourceId=dataSourceId)
        print
    except: # Error processing
        print "** An error ocurred, see the returned response. **"
        print
        return False

    # Test getCommunities()
    print "Testing getCommunities() ..."
    try:
        request = getCommunitiesRequest()
        request.set_element_accessKey( arguments.accessKey )
        request.set_element_dataSourceId( dataSourceId )
        response = service.getCommunities( request )
        communities = response.get_element_communities()

        _communities = []
        for community in communities.get_element_community():
            _communities += [ community.get_element_id() ]
            print "Community ID {0}: {1} [start={2},end={3}]".format(
               community.get_element_id(), community.get_element_title(),
               community.get_element_startDate(),
               community.get_element_endDate()
            )
        communityId = random.choice(_communities)
        print "Community ID for further tests: {communityId}"\
              .format(communityId=communityId)
        print
    except: # Error processing
        print "** An error ocurred, see the returned response. **"
        print
        return False

    # Test getIndicators()
    print "Testing getIndicators() ..."
    try:
        request = getIndicatorsRequest()
        request.set_element_accessKey( arguments.accessKey )
        request.set_element_dataSourceId( dataSourceId )
        request.set_element_communityId( communityId )
        response = service.getIndicators( request )
        indicators = response.get_element_indicators()

        _indicators = []
        for indicator in indicators.get_element_indicator():
            _indicators += [ indicator.get_element_id() ]
            print "Indicator ID {0}: {1}".format(
               indicator.get_element_id(), indicator.get_element_title() )
        indicatorId = random.choice(_indicators)
        print "Indicator ID for further tests: {indicatorId}"\
              .format(indicatorId=indicatorId)
        print
    except: # Error processing
        print "** An error ocurred, see the returned response. **"
        print
        return False
 
    # Test getHealthScore()
    print "Testing getHealthScore() ..."
    try:
        request = getHealthScoreRequest()
        request.set_element_accessKey( arguments.accessKey )
        request.set_element_dataSourceId( dataSourceId )
        request.set_element_communityId( communityId )
        request.set_element_indicatorId( indicatorId )
        request.set_element_startDate( (2006, 1, 1, 0, 0, 0, 0, 0, -1) )
        request.set_element_endDate( (2006, 12, 31, 0, 0, 0, 0, 0, -1) )
        response = service.getHealthScore( request )
        print "Indicator '{0}', score = {1}".format(
              response.get_element_score().get_element_indicator()\
                                          .get_element_title(),
              response.get_element_score().get_element_value()
        )
        print
    except: # Error processing
        print "** An error ocurred, see the returned response. **"
        print
        return False

    # Test getHealthScores()
    print "Testing getHealthScores() ..."
    try:
        request = getHealthScoresRequest()
        request.set_element_accessKey( arguments.accessKey )
        request.set_element_dataSourceId( dataSourceId )
        request.set_element_communityId( communityId )
        request.set_element_startDate( (2007, 1, 1, 0, 0, 0, 0, 0, -1) )
        request.set_element_endDate( (2007, 12, 31, 0, 0, 0, 0, 0, -1) )
        response = service.getHealthScores( request )
        scores = response.get_element_scores()
        for score in scores.get_element_score():
            print "Indicator '{0}', score = {1}".format(
                score.get_element_indicator().get_element_title(),
                score.get_element_value()
            )
    except: # Error processing
        print "** An error ocurred, see the returned response. **"
        print
        return False

    # Finished
    return True

################################################################################

if __name__ == '__main__':
    sys.exit(not main())
