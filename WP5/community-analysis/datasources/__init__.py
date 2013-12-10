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

"""Datasources package."""

import os
import cPickle
import utilities.console as c
import ConfigParser

CACHE_PROTOCOL = cPickle.HIGHEST_PROTOCOL
FIELDS = {
    'communities': ('id','title','start_date','end_date'),
    'conversations': ('id','community_id'),
    'messages': ('id','user_id','conversation_id','datetime',
                 'target_id','target_datetime'),
    'users': ('id',)
}

################################################################################

def build_conf( dsname, config_fname ):
    """Build a DataSource configuration from a datasources config file."""
    config = ConfigParser.RawConfigParser()
    config.read( config_fname )
    return {
        'engine': config.get(dsname,'engine'),
        'source': config.get(dsname,'source'),
        'host': config.get(dsname,'host'),
        'user': config.get(dsname,'user'),
        'password': config.get(dsname,'password'),
        'dbname': config.get(dsname,'dbname'),
        'encoding': config.get(dsname,'encoding'),
        'caching': config.getboolean(dsname,'caching')
    }

def get_engine( dsconf ):
    """Imports a datasource engine."""
    return __import__( dsconf['engine'] , globals() )

def field( table, field_name ):
    """Returns the index of the 'field_name' name on the 'table'."""
    return FIELDS[table].index(field_name)

def fields( table ):
    """Returns a field map of the 'table'."""
    table_fields = {}
    for field_name in FIELDS[table]:
        table_fields[field_name] = field(table, field_name)
    return table_fields

def get_cache_name( dsconf, object, arguments ):
    """Builds a cache object name."""

    # Build the conditions part
    conditions = ''
    for argument in arguments:
        conditions += '_{0}'.format(argument)

    # Build the cache object name
    name = '{0}_{1}_{2}{3}.pickle'.format(
           dsconf['engine'], dsconf['source'], object, conditions )
    return name

def in_cache( dsconf, object, arguments=() ):
    """Checks if an object is in cache."""
    name = get_cache_name( dsconf, object, arguments )
    if os.path.isfile(dsconf['cache_dir']+'/'+name):
        c.log( "'{0}' is in cache.".format(name) )
        return True
    c.log( "'{0}' is not in cache.".format(name) )
    return False

def get_cache( dsconf, object, arguments=() ):
    """Gets an object from cache."""
    name = get_cache_name( dsconf, object, arguments )
    file = open(dsconf['cache_dir']+'/'+name, 'rb')
    data = cPickle.load(file)
    file.close()
    c.log( "Got '{0}' from cache.".format(name) )
    return data

def store_cache( dsconf, object, data, arguments=() ):
    """Stores an object into cache."""
    name = get_cache_name( dsconf, object, arguments )
    file = open(dsconf['cache_dir']+'/'+name, 'wb')
    cPickle.dump(data, file, CACHE_PROTOCOL)
    file.close()
    c.log( "Stored '{0}' into cache.".format(name) )

################################################################################

def get_users( dsconf ):
    """get_users() facility wrapper."""
    # Results: tuple of (id,) tuples

    c.log( 'Starting' )
    if dsconf['caching'] and in_cache( dsconf, 'users' ):
        results = get_cache( dsconf, 'users' )
    else:
        results = get_engine( dsconf ) \
                  .get_source( dsconf ) \
                  .get_users( dsconf )
        if dsconf['caching']:
            store_cache( dsconf, 'users', results )
    c.log( '{0} user(s) found.'.format(len(results)) )
    return results

def get_communities( dsconf ):
    """get_communities() facility wrapper."""
    # Results: tuple of (id, title, start_date, end_date) tuples

    c.log( 'Starting' )
    if dsconf['caching'] and in_cache( dsconf, 'communities' ):
        results = get_cache( dsconf, 'communities' )
    else:
        results = get_engine( dsconf ) \
                  .get_source( dsconf ) \
                  .get_communities( dsconf )
        if dsconf['caching']:
            store_cache( dsconf, 'communities', results )
    c.log( '{0} community(ies) found.'.format(len(results)) )
    return results

def get_conversations( dsconf, community=None ):
    """get_conversations() facility wrapper."""
    # Results: tuple of (id, community_id) tuples

    c.log( 'Starting' )
    if dsconf['caching'] and in_cache(dsconf,'conversations',(community,)):
        results = get_cache( dsconf, 'conversations', (community,) )
    else:
        results = get_engine( dsconf ) \
                  .get_source( dsconf ) \
                  .get_conversations( dsconf, community )
        if dsconf['caching']:
            store_cache( dsconf, 'conversations', results, (community,) )
    c.log( '{0} conversation(s) found.'.format(len(results)) )
    return results

def get_messages( dsconf, start, end, community=None ):
    """get_messages() facility wrapper."""
    # Results: tuple of (id, user_id, conversation_id, datetime,
    #                     target_id, target_datetime) tuples

    c.log( 'Starting' )
    if dsconf['caching'] and in_cache(dsconf,'messages',(start,end,community)):
        results = get_cache( dsconf, 'messages', (start,end,community) )
    else:
        results = get_engine( dsconf ) \
                  .get_source( dsconf ) \
                  .get_messages( dsconf, start, end, community )
        if dsconf['caching']:
            store_cache( dsconf, 'messages', results, (start,end,community) )
    c.log( '{0} message(s) found.'.format(len(results)) )
    return results

################################################################################

if __name__ == '__main__':
    print 'Module: {0}'.format(__doc__)
