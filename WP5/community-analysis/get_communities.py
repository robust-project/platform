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

"""Gets all communities from a datasource."""

import sys, os
import utilities.console as c
import datasources as ds
import ConfigParser

################################################################################

BASE_PATH = os.path.dirname(os.path.realpath(__file__))
GENERAL_CONF = BASE_PATH + "/config/general.conf"
DATASOURCES_CONF = BASE_PATH + "/config/datasources.conf"

################################################################################

def get_communities(dsname):
    """Use datasource get_communities() facility."""
    c.log( 'Starting' )

    # Build the datasource configuration
    dsconfig = ds.build_conf( dsname, DATASOURCES_CONF )

    # Get the cache directory
    config = ConfigParser.RawConfigParser()
    config.read(GENERAL_CONF)
    dsconfig['cache_dir'] = config.get('paths','cache_dir')

    communities = ds.get_communities( dsconfig )
    fields = ds.fields('communities')
    for community in communities:
        id = community[fields['id']]
        title = (community[fields['title']], '(no title)')\
                [community[fields['title']] is None]
        start_date = (community[fields['start_date']], '(no start date)')\
                [community[fields['start_date']] is None]
        end_date = (community[fields['end_date']], '(no end date)')\
                [community[fields['end_date']] is None]
        print '{0}\t{1}\t{2}\t{3}'.format( id, title.encode('UTF-8'),
                                           start_date, end_date )
    c.log( 'Finished' )

################################################################################

# Get the datasource name
if len(sys.argv) != 2:
    print 'Usage: %s [ds_name]' % sys.argv[0]
    sys.exit(1)
else: ds_name = sys.argv[1]

# Get the communities
sys.exit( not get_communities(ds_name) )
