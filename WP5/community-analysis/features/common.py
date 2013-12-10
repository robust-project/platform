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

"""Common functions for all features."""

import utilities.console as c
import collections

################################################################################

def build_msgmap( messages, field ):
    """Build a mapped version of the messages."""
    c.log( 'Starting' )
    mapped = collections.defaultdict(list)

    # Any messages?
    if not messages:
        c.log( 'No messages given -- nothing to do.' )
        return mapped

    # Build the mapped version, indexed by 'field'
    for message in messages:
        mapped[message[field]] += [ message ]
    c.log( '{0} mappings done.'.format(len(mapped)) )
    return mapped

def build_replies( messages, fields ):
    """Compute the replies tuples given user-mapped 'messages'
    Usage: {userid:set((pstx,psty,txy))} = replies( messages ), where:
           pstx = message id that was replied by 'psty',
           psty = message id that is a reply to 'pstx',
           txy = time delay between 'pstx' and 'psty'"""
    c.log( 'Starting' )
    replies = collections.defaultdict(set)

    # Any messages or users?
    if not messages:
        c.log( 'No messages or users given -- nothing to do.' )
        return replies

    # Compute each user reply tuples (pstx,psty,txy)
    for user in messages.keys():
        for message in messages[user]:
            if ( message[fields['target_id']] is not None ) and \
               ( message[fields['target_datetime']] is not None ):
                replies[user].add((
                    message[fields['target_id']],
                    message[fields['id']],
                    message[fields['datetime']] - \
                        message[fields['target_datetime']]
                ))

    # Return reply tuples
    c.log( '{0} user(s) with replies found.'.format(len(replies)) )
    return replies

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
