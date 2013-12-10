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

"""Computes a community feature value given associated parameters."""

import os, sys
import time as t
import datetime as dt
import ConfigParser
import utilities.console as c
import datasources as ds

################################################################################

BASE_PATH = os.path.dirname(os.path.realpath(__file__))
GENERAL_CONF = BASE_PATH + "/config/general.conf"
DATASOURCES_CONF = BASE_PATH + "/config/datasources.conf"

################################################################################

def imp_module( name ):
    """Imports a named module."""
    return __import__( name, globals(), fromlist='*' )

def compute_feature( parms ):
    """Do the actual feature computation."""
    c.log( 'Starting' )

    # Try to load the requested feature
    try:
        feature = imp_module( 'features.'+parms['feature_name'] )
    except ImportError:
        c.log( "The feature '{0}' could not be loaded." \
               .format(parms['feature_name']) )
        return False

    # Try to load the requested aggregation
    try:
        aggregation = imp_module( 'aggregations.' +
                                  parms['aggregation_name'] )
    except ImportError:
        c.log( "The aggregation '{0}' could not be loaded." \
               .format(parms['aggregation_name']) )
        return False

    # Try to load the requested consolidation
    try:
        consolidation = imp_module( 'consolidations.' +
                                     parms['consolidation_name'] )
    except ImportError:
        c.log( "The consolidation '{0}' could not be loaded." \
               .format(parms['consolidation_name']) )
        return False

    # Build the datasource configuration
    dsconfig = ds.build_conf( parms['data_source'],
                             DATASOURCES_CONF )

    # Get the cache directory
    config = ConfigParser.RawConfigParser()
    config.read(GENERAL_CONF)
    dsconfig['cache_dir'] = config.get('paths','cache_dir')

    # Compute the requested feature
    c.log("Computing '{0}' in {1}, from {2} to {3} ..." \
          .format( parms['feature_name'],
          ('community id {0}'.format(parms['community_id']),
           'all communities')[parms['community_id'] is None],
          parms['start_date'], parms['end_date'] ))
    start_time = t.time()
    values = feature.compute( dsconfig,
                             parms['community_id'],
                             parms['start_date'], parms['end_date'])
    c.log('Time elapsed: {0}s'.format(t.time() - start_time) )

    # Compute aggregation/consolidation
    values = consolidation.compute( aggregation.compute(values) )

    # Output values
    print values
    c.log('Finished')

################################################################################

# Check arguments
if len(sys.argv) < 8:
    print 'Usage: {0} [data_source] [feature_name] [aggregation_name]' \
          .format(sys.argv[0]), \
          '[consolidation_name] [community_id] [start_date] [end_date]'
    print
    print 'data_source is one of the configured data sources in config.py'
    print
    print 'feature_name is one of: activity, indegree, outdegree, ' \
          'reciprocity, popularity'
    print
    print 'agreggation_name is one of: none, stats, sum'
    print
    print 'consolidation_name is one of: none, tabulate, csvlist,', \
        'twactsum2hlth, twindstats2hlth, twoutdstats2hlth, twrecpstats2hlth,',\
        'biactsum2hlth, biindstats2hlth, bioutdstats2hlth, birecpstats2hlth',\
        'scnactsum2hlth, scndegrstats2hlth, scnrecpstats2hlth, popstats2hlth'
    print
    print 'community_id should be an integer (use -1 for all data) and', \
          '(start|end)_date in YYYY-MM-DD format.'
    print
    sys.exit(1)

# Compute the feature
sys.exit( not compute_feature( {
        'data_source': sys.argv[1], 'feature_name': sys.argv[2],
        'aggregation_name': sys.argv[3], 'consolidation_name': sys.argv[4],
        'community_id': (int(sys.argv[5]),None)[sys.argv[5]=='-1'],
        'start_date': dt.datetime.strptime(sys.argv[6], '%Y-%m-%d').date(),
        'end_date': dt.datetime.strptime(sys.argv[7], '%Y-%m-%d').date()
}))
