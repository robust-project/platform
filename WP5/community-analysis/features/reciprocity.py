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

"""Feature - Reciprocity."""

import datetime as dt
import datasources as ds
import features.common as common
import utilities.console as c
import collections

################################################################################

def compute( dsconf, community, start, end ):
    """Compute community reciprocities with 'seconds' resolution."""
    c.log( 'Starting' )
    reciprocities = collections.defaultdict(float)

    # Get all messages and replies for 'users'
    messages = ds.get_messages(dsconf, start, end, community)
    messages = common.build_msgmap(messages, ds.field('messages','user_id'))
    replies = common.build_replies(messages, ds.fields('messages'))

    # Get requested users reciprocities
    c.log( 'Computing reciprocities ...' )
    for (user,user_messages) in messages.iteritems():
        # Calculate the sum of reply delays
        delays_sum = dt.timedelta( 0 )
        for reply in replies[user]:
            if reply[2].total_seconds() >= 0: # Ignore negative delay replies
                delays_sum += reply[2] # txy component

        # Calculate reciprocity
        if not replies[user]:
            reciprocities[user] = float('inf')
        else:
            reciprocities[user] = delays_sum.total_seconds() * 1.0 / \
                                          len( replies[user] )

    # Return users reciprocities
    c.log( 'Finished' )
    return reciprocities

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
