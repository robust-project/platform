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

"""RobustCommunityAnalysis web service server."""

import os
import argparse
from ZSI.wstools import logging
from ZSI import ServiceContainer
from ZSI.version import Version as zsiversion
import datetime as dt
import ConfigParser

################################################################################

from utilities import console as c
import datasources as ds

# Features
import features.activity
import features.indegree
import features.outdegree
import features.reciprocity
import features.popularity

# Aggregations
import aggregations.stats
import aggregations.sum

# Common consolidations
import consolidations.popstats2hlth

# Consolidations for TiddlyWiki
import consolidations.twindstats2hlth
import consolidations.twoutdstats2hlth
import consolidations.twrecpstats2hlth
import consolidations.twactsum2hlth

# Consolidations for Boards.ie
import consolidations.biindstats2hlth
import consolidations.bioutdstats2hlth
import consolidations.birecpstats2hlth
import consolidations.biactsum2hlth

# Consolidations for SAP Community Network
import consolidations.scndegrstats2hlth
import consolidations.scnrecpstats2hlth
import consolidations.scnactsum2hlth

################################################################################

BASE_PATH = os.path.dirname(os.path.realpath(__file__))
GENERAL_CONF = BASE_PATH + "/config/general.conf"
DATASOURCES_CONF = BASE_PATH + "/config/datasources.conf"
WEBSERVICE_CONF = BASE_PATH + "/config/webservice.conf"

WSDL = BASE_PATH + "/webservice/RobustCommunityAnalysis.wsdl"

DATASOURCES = {
    1: {
        'name': 'tiddlywiki',
        'title': 'TiddlyWiki mailing list'
    },
    2: {
        'name': 'boardsie',
        'title': 'Boards.ie public forums'
    },
    3: {
        'name': 'sapscn',
        'title': 'SAP Community Network (SCN) corporate forums'
    },
    4: {
        'name': 'streamingsap',
        'title': 'Streaming SAP Community Network (SCN) corporate forums'
    },
}

INDICATORS = {
    1: {
        'title': 'In-Degree',
        'feature': features.indegree,
        'aggregation': aggregations.stats,
        'consolidation': {
            1: consolidations.twindstats2hlth,
            2: consolidations.biindstats2hlth,
            3: consolidations.scndegrstats2hlth,
            4: consolidations.scndegrstats2hlth,
        }
    },
    2: {
        'title': 'Out-Degree',
        'feature': features.outdegree,
        'aggregation': aggregations.stats,
        'consolidation': {
            1: consolidations.twoutdstats2hlth,
            2: consolidations.bioutdstats2hlth,
            3: consolidations.scndegrstats2hlth,
            4: consolidations.scndegrstats2hlth,
        }
    },
    3: {
        'title': 'Popularity',
        'feature': features.popularity,
        'aggregation': aggregations.stats,
        'consolidation': {
            1: consolidations.popstats2hlth,
            2: consolidations.popstats2hlth,
            3: consolidations.popstats2hlth,
            4: consolidations.popstats2hlth,
        }
    },
    4: {
        'title': 'Reciprocity',
        'feature': features.reciprocity,
        'aggregation': aggregations.stats,
        'consolidation': {
            1: consolidations.twrecpstats2hlth,
            2: consolidations.birecpstats2hlth,
            3: consolidations.scnrecpstats2hlth,
            4: consolidations.scnrecpstats2hlth,
        }
    },
    5: {
        'title': 'Activity',
        'feature': features.activity,
        'aggregation': aggregations.sum,
        'consolidation': {
            1: consolidations.twactsum2hlth,
            2: consolidations.biactsum2hlth,
            3: consolidations.scnactsum2hlth,
            4: consolidations.scnactsum2hlth,
        }
    }
}

################################################################################

from webservice.RobustCommunityAnalysis_server import *

class RobustCommunityAnalysisImpl( RobustCommunityAnalysis ):
    """RobustCommunityAnalysis implementation."""
    # Make the WSDL available for HTTP GET
    _wsdl = "".join( open(WSDL).readlines() )

    # Implements getDataSources()
    def soap_getDataSources(self, ps, **kw):
        c.log( 'Starting' )
        request, response = RobustCommunityAnalysis \
                            .soap_getDataSources(self, ps, **kw)

        # Get arguments
        accessKey = request.get_element_accessKey()
        if not checkKey(accessKey):
            raise Exception("Your accessKey is not valid.")

        # Compute the available datasources
        response = getDataSourcesResponse()
        datasources = []
        for id, dsrc in DATASOURCES.iteritems():
            datasource = response.new_datasources().new_datasource()
            datasource.set_element_id(id)
            datasource.set_element_title(dsrc['title'])
            datasources += [ datasource ]
        _datasources = response.new_datasources()
        _datasources.set_element_datasource( datasources )
        response.set_element_datasources( _datasources )

        # Return response
        c.log( 'Finished' )
        return request, response

    # Implements getCommunities()
    def soap_getCommunities(self, ps, **kw):
        request, response = RobustCommunityAnalysis \
                            .soap_getCommunities(self, ps, **kw)

        # Get arguments
        accessKey = request.get_element_accessKey()
        if not checkKey(accessKey):
            raise Exception("Your accessKey is not valid.")
        dataSourceId = request.get_element_dataSourceId()
        c.log( 'Starting for dataSourceId {}'.format(dataSourceId) )

        # Build the datasource configuration
        dsconfig = ds.build_conf( DATASOURCES[dataSourceId]['name'],
                                  DATASOURCES_CONF )

        # Get the cache directory
        config = ConfigParser.RawConfigParser()
        config.read(GENERAL_CONF)
        dsconfig['cache_dir'] = config.get('paths','cache_dir')

        # Compute the available communities
        COMMUNITIES = ds.get_communities(dsconfig)
        response = getCommunitiesResponse()
        communities = []
        for id, title, startDate, endDate in COMMUNITIES:
            if startDate is None or endDate is None:
                continue
            community = response.new_communities().new_community()
            community.set_element_id(id)
            community.set_element_title(title)
            community.set_element_startDate( startDate.timetuple() )
            community.set_element_endDate( endDate.timetuple() )
            communities += [ community ]
        _communities = response.new_communities()
        _communities.set_element_community( communities )
        response.set_element_communities( _communities )

        # Return response
        c.log( 'Finished' )
        return request, response

    # Implements getIndicators()
    def soap_getIndicators(self, ps, **kw):
        request, response = RobustCommunityAnalysis \
                            .soap_getIndicators(self, ps, **kw)

        # Get arguments
        accessKey = request.get_element_accessKey()
        dataSourceId = request.get_element_dataSourceId()
        communityId = request.get_element_communityId()
        c.log( 'Starting for dataSourceId {} and communityId {}' \
               .format(dataSourceId, communityId) )

        # Return available indicators mapping
        response = getIndicatorsResponse()
        indicators = []
        for id, ind in INDICATORS.iteritems():
            indicator = response.new_indicators().new_indicator()
            indicator.set_element_id( id )
            indicator.set_element_title( ind['title'] )
            indicators += [ indicator ]
        _indicators = response.new_indicators()
        _indicators.set_element_indicator( indicators )
        response.set_element_indicators( _indicators )

        # Return response
        c.log( 'Finished' )
        return request, response

    # Implements getHealthScore()
    def soap_getHealthScore(self, ps, **kw):
        request, response = RobustCommunityAnalysis \
                            .soap_getHealthScore(self, ps, **kw)

        # Get arguments
        accessKey = request.get_element_accessKey()
        if not checkKey(accessKey):
            raise Exception("Your accessKey is not valid.")
        dataSourceId = request.get_element_dataSourceId()
        indicatorId = request.get_element_indicatorId()
        communityId = request.get_element_communityId()
        startDate = request.get_element_startDate()
        startDate = dt.datetime(startDate[0], startDate[1], startDate[2],
                                startDate[3], startDate[4], startDate[5])
        endDate = request.get_element_endDate()
        endDate = dt.datetime(endDate[0], endDate[1], endDate[2],
                              endDate[3], endDate[4], endDate[5])
        c.log( ('Starting for dataSourceId {}, indicatorId {}, communityId {}, ' +\
               'startDate {} and endDate {}').format(dataSourceId, indicatorId,
               communityId, startDate, endDate) )

        # Compute the indicator score
        response = getHealthScoreResponse()
        score = getIndicatorScore( indicatorId, dataSourceId, communityId,
                                   startDate, endDate )
        _score = response.new_score()
        _indicator = _score.new_indicator()
        _indicator.set_element_id( indicatorId )
        _indicator.set_element_title( INDICATORS[indicatorId]['title'] )
        _score.set_element_indicator( _indicator )
        _score.set_element_value( score )
        response.set_element_score( _score )

        # Return response
        c.log( 'Finished' )
        return request, response

    # Implements getHealthScores()
    def soap_getHealthScores(self, ps, **kw):
        c.log( 'Starting' )
        request, response = RobustCommunityAnalysis \
                            .soap_getHealthScores(self, ps, **kw)

        # Get arguments
        accessKey = request.get_element_accessKey()
        if not checkKey(accessKey):
            raise Exception("Your accessKey is not valid.")
        dataSourceId = request.get_element_dataSourceId()
        communityId = request.get_element_communityId()
        startDate = request.get_element_startDate()
        startDate = dt.datetime(startDate[0], startDate[1], startDate[2],
                                startDate[3], startDate[4], startDate[5])
        endDate = request.get_element_endDate()
        endDate = dt.datetime(endDate[0], endDate[1], endDate[2],
                              endDate[3], endDate[4], endDate[5])
        c.log( ('Starting for dataSourceId {}, communityId {}, '+
               'startDate {} and endDate {}').format(dataSourceId,
               communityId, startDate, endDate) )

        # Compute all indicators scores
        response = getHealthScoresResponse()
        scores = []
        for indicatorId in INDICATORS:
            score = getIndicatorScore( indicatorId, dataSourceId, communityId,
                                       startDate, endDate )
            _score = response.new_scores().new_score()
            _indicator = _score.new_indicator()
            _indicator.set_element_id( indicatorId )
            _indicator.set_element_title( INDICATORS[indicatorId]['title'] )
            _score.set_element_indicator( _indicator )
            _score.set_element_value( score )
            scores += [ _score ]
        _scores = response.new_scores()
        _scores.set_element_score( scores )
        response.set_element_scores( _scores )

        # Return response
        c.log( 'Finished' )
        return request, response

################################################################################

def checkKey( key ):
    """Check if given key is allowed to proceed."""
    # Read the access keys configuration
    config = ConfigParser.RawConfigParser()
    config.read(WEBSERVICE_CONF)
    accesskeys = config.options('accesskeys')

    # Check key
    if key in accesskeys:
        c.log( "Allowed key {key} ({description}).".format(
               key=key, description=config.get('accesskeys',key)) )
        return True

    # No key
    c.log( "DENIED key {key}.".format(key=key) )
    return False

################################################################################

def getIndicatorScore( indicatorId, dataSourceId, communityId,
                       startDate, endDate ):
    """Computes the indicator health score."""
    c.log( 'Starting' )

    # Build the datasource configuration
    dsconfig = ds.build_conf( DATASOURCES[dataSourceId]['name'],
                              DATASOURCES_CONF )

    # Get the cache directory
    config = ConfigParser.RawConfigParser()
    config.read(GENERAL_CONF)
    dsconfig['cache_dir'] = config.get('paths','cache_dir')

    # Prepare score parameters
    feature = INDICATORS[indicatorId]['feature']
    aggregation = INDICATORS[indicatorId]['aggregation']
    consolidation = INDICATORS[indicatorId]['consolidation'][dataSourceId]
    score = consolidation.compute(aggregation.compute(feature.compute(
                dsconfig, communityId, startDate, endDate
            )))
    c.log( 'Finished' )
    return score

################################################################################

def main():
    """Main function."""
    # Process server arguments
    parser = argparse.ArgumentParser(description="WebService server.")
    parser.add_argument( "-l", "--loglevel",
                   help="loglevel (DEBUG,WARN)", metavar="LOGLEVEL" )
    parser.add_argument( "-a", "--address",
                   help="IP address for binding (default: 127.0.0.1)",
                   metavar="ADDRESS", default="127.0.0.1" )
    parser.add_argument( "-p", "--port",
                   help="HTTP port to use (default: 8080)",
                   metavar="PORT", default=8080, type=int )
    arguments = parser.parse_args()

    # Set the log level
    if arguments.loglevel:
        loglevel = eval(arguments.loglevel, logging.__dict__)
        logger = logging.getLogger("")
        logger.setLevel(loglevel)

    # Print ZSI installed version
    c.log( 'Starting' )
    c.log( 'ZSI version: {0}'.format(zsiversion) )

    # Run the server (use address='' for binding to all interfaces)
    c.log( 'Waiting for requests ...' )
    ServiceContainer.AsServer( address=arguments.address, port=arguments.port,
                               services=[RobustCommunityAnalysisImpl(),] )

################################################################################

if __name__ == '__main__':
    main()
