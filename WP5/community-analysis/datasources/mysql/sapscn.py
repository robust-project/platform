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

"""MySQL SAP SCN database translator."""

import interface as mysql

################################################################################

def get_users( dsconf ):
    """Get all users from the database."""

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Get the users from database
    cursor.execute( 'SELECT `users`.`userid` AS `id` FROM `users`' )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

def get_communities( dsconf ):
    """Get all available communities from the database."""

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Get the communities from database
    cursor.execute( 'SELECT DISTINCT(`threads`.`forumid`) AS `id`,'+\
                           '`forums`.`title` AS `title`,'+\
                           'MIN(`posts`.`posteddate`) AS `start_date`,'+\
                           'MAX(`posts`.`posteddate`) AS `end_date` '+\
                      'FROM `threads` '+\
                 'LEFT JOIN `forums` '+\
                        'ON (`threads`.`forumid` = `forums`.`forumid`) '+\
                 'LEFT JOIN `posts` '+\
                        'ON (`posts`.`threadid` = `threads`.`threadid`) '+\
                  'GROUP BY `threads`.`forumid`' )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

def get_conversations( dsconf, community ):
    """Get all available conversations from the database."""

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Community given?
    where_clause = ''
    where_arguments = ()
    if community is not None:
        where_clause += ' WHERE `threads`.`forumid` = %s'
        where_arguments = (community,)
        
    # Get the conversations from database
    cursor.execute( 'SELECT `threads`.`threadid` AS `id`,'+\
                           '`threads`.`forumid` AS `community_id`'+\
                      'FROM `threads`' + where_clause, where_arguments )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

def get_messages( dsconf, start, end, community ):
    """Get all messages between times 'start' and 'end' from the database."""

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Community given?
    where_clause = ''
    where_arguments = ()
    if community is not None:
        where_clause += ' AND `posts`.`threadid` IN ('+\
                        'SELECT `threads`.`threadid` FROM `threads` '+\
                         'WHERE `threads`.`forumid` = %s)'
        where_arguments = (community,)

    # Execute query to get all users messages
    query = 'SELECT `posts`.`postid` AS `id`,'+\
                   '`posts`.`userid` AS `user_id`,'+\
                   '`posts`.`threadid` AS `conversation_id`,'+\
                   '`posts`.`posteddate` AS `datetime`,'+\
                   '`replieswithposts`.`origpostid` AS `target_id`,'+\
                   '`replieswithposts`.`postdate` AS `target_datetime` '+\
              'FROM `posts` '+\
              'LEFT JOIN `replieswithposts` '+\
              'ON (`replieswithposts`.`replyingpostid` = `posts`.`postid`) '+\
             'WHERE `posts`.`posteddate` BETWEEN %s AND %s' + where_clause
    cursor.execute( query , ( start , end ) + where_arguments )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
