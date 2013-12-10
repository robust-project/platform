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

import sys, os
import datetime as dt
import ConfigParser

# Add the current path
# This script should be called from the parent directory.
sys.path.append('.')

import datasources as ds

GENERAL_CONF = "config/general.conf"
DATASOURCES_CONF = "config/datasources.conf"

# Get the datasource to test
if len(sys.argv) != 2:
    print 'Usage: %s [ds_name]' % sys.argv[0]
    sys.exit(1)
else: ds_name = sys.argv[1]

# Build the datasource configuration
dsconfig = ds.build_conf( ds_name, DATASOURCES_CONF )

# Get the cache directory
config = ConfigParser.RawConfigParser()
config.read(GENERAL_CONF)
dsconfig['cache_dir'] = config.get('paths','cache_dir')

# Begin testing
print 'Testing \'%s\' datasource ...' % ds_name

# Create some dates
start = dt.date( 2005 , 1 , 1 )
end = dt.date( 2005 , 12 , 31 )

# Test get_user() facility
users = ds.get_users( dsconfig )
print 'Got {0} users from datasource.'.format(len(users))
del users

# Test get_communities() facility
community = None
communities = ds.get_communities( dsconfig )
print 'Got {0} community(ies) from datasource.'.format(len(communities))
if len(communities) > 0:
    community = communities[0][0]
del communities

# Test get_conversations() facility
conversations = ds.get_conversations( dsconfig, community )
print 'Got {0} conversation(s) from datasource for community {1}.' \
      .format(len(conversations), community)
del conversations

# Test get_messages() facility
if community is not None:
    messages = ds.get_messages( dsconfig, start, end, community )
    print 'Got {0} messages between {1} and {2} for community {3}.' \
          .format(len(messages), start, end, community)
    del messages
else:
    print 'No comunnities found. Skipping get_counts() test.'

# Finished
print 'Finished.'
