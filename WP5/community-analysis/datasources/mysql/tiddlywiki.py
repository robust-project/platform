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

"""MySQL TiddlyWiki database translator."""

import interface as mysql

################################################################################

def get_users( dsconf ):
    """Get all users from the database."""

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Get the users from database
    cursor.execute( 'SELECT `user`.`id` FROM `user`' )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

def get_communities( dsconf ):
    """Get all available communities from the database."""

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Get the communities from database
    cursor.execute( 'SELECT 1 AS `id`,'+\
                           '"TiddlyWiki" AS `title`,'+\
                           'MIN(`posts`.`datetime`) AS `start_date`,'+\
                           'MAX(`posts`.`datetime`) AS `end_date` '+\
                      'FROM `posts`' )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

def get_conversations( dsconf, community ):
    """Get all available conversations from the database."""

    # Only community 1L is available
    if community != 1L and community is not None:
        return ()
    return ((1L,1L),)

################################################################################

def get_messages( dsconf, start, end, community ):
    """Get all messages between times 'start' and 'end' from the database."""

    # Only community 1L is available
    if community != 1L and community is not None:
        return ()

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Execute query to get all users messages
    query = 'SELECT `posts`.`id` AS `id`,'+\
                   '`posts`.`userid` AS `user_id`,'+\
                   '1 AS `conversation_id`,'+\
                   '`posts`.`datetime` AS `datetime`,'+\
                   '`thread`.`targetid` AS `target_id`,'+\
                   '`thread`.`replyDate` AS `target_datetime` '+\
              'FROM `posts` '+\
              'LEFT JOIN `thread` '+\
                     'ON (`thread`.`sourceid` = `posts`.`id`) '+\
             'WHERE `posts`.`datetime` BETWEEN %s AND %s'
    cursor.execute( query , ( start , end ) )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
