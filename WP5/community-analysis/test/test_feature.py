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

import sys
import time as t
import datetime as dt
import random
import ConfigParser

# Add the current path
# This script should be called from the parent directory.
sys.path.append('.')

import datasources as ds

GENERAL_CONF = "config/general.conf"
DATASOURCES_CONF = "config/datasources.conf"

# Get the feature name to test
if len(sys.argv) != 3:
    print 'Usage: %s [datasource_name] [feat_name]' % sys.argv[0]
    sys.exit(1)

ds_name = sys.argv[1]
feat_name = sys.argv[2]

# Build the datasource configuration
dsconfig = ds.build_conf( ds_name, DATASOURCES_CONF )

# Get the cache directory
config = ConfigParser.RawConfigParser()
config.read(GENERAL_CONF)
dsconfig['cache_dir'] = config.get('paths','cache_dir')

# Get a community for testing
communities = ds.get_communities( dsconfig )
communityId = random.choice(communities)[0]

# Get some dates
t1 = dt.date( 2006, 1, 1 )
t2 = dt.date( 2006, 1, 31 )

# Compute the feature
exec( 'from features import %s as feat' % feat_name )
print 'Computing \'%s\' ...' % feat_name
start_time = t.time()
values = feat.compute( dsconfig, communityId, t1 , t2 )
print 'Time elapsed: %f s' % ( t.time() - start_time )
print values
