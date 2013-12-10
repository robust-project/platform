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

"""MySQL Generic forum-like database translator."""

import interface as mysql

################################################################################

def get_users( dsconf ):
    """Get all users from the database."""

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Get the users from database
    cursor.execute( 'SELECT `users`.`id` AS `id` FROM `users`' )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

def get_communities( dsconf ):
    """Get all available communities from the database."""

    # Connect to database
    cursor = mysql.get_cursor( dsconf )

    # Get the community from database
    cursor.execute( 'SELECT `cm`.`id`, `cm`.`title`,' +\
                           'MAX(`m`.`datetime`) AS `start_date`,' +\
                           'MIN(`m`.`datetime`) AS `end_date` ' +\
                      'FROM `communities` AS `cm` ' +\
                'INNER JOIN `conversations` AS `c` ' +\
                        'ON (`cm`.`id` = `c`.`community_id`) ' +\
                'INNER JOIN `messages` AS `m` ' +\
                        'ON (`c`.`id` = `m`.`conversation_id`) ' +\
                  'GROUP BY `cm`.`id`, `cm`.`title`' )
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
        where_clause += ' WHERE `conversations`.`community_id` = %s'
        where_arguments = (community,)

    # Get the conversations from database
    cursor.execute( 'SELECT `conversations`.`id` AS `id`,'+\
                           '`conversations`.`community_id` AS `community_id`'+\
                      'FROM `conversations`' + where_clause, where_arguments )
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
        where_clause += ' AND `messages`.`conversation_id` IN ('+\
                        'SELECT `conversations`.`id` FROM `conversations` '+\
                         'WHERE `conversations`.`community_id` = %s)'
        where_arguments = (community,)

    # Execute query to get all users messages
    query = 'SELECT `messages`.`id` AS `id`,'+\
                   '`messages`.`user_id` AS `user_id`,'+\
                   '`messages`.`conversation_id` AS `conversation_id`,'+\
                   '`messages`.`datetime` AS `datetime`,'+\
                   '`replies`.`target_id` AS `target_id`,'+\
                   '`replies`.`target_datetime` AS `target_datetime` '+\
              'FROM `messages` '+\
              'LEFT JOIN `replies` '+\
                     'ON (`replies`.`source_id` = `messages`.`id`) '+\
             'WHERE `messages`.`datetime` BETWEEN %s AND %s' + where_clause
    cursor.execute( query , ( start , end ) + where_arguments )
    results = cursor.fetchall()
    cursor.close()
    return results

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
