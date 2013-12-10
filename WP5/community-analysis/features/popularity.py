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

"""Feature - Popularity."""

import datasources as ds
import features.common as common
import utilities.console as c
import collections

################################################################################

def compute( dsconf, community, start, end ):
    """Compute community popularities."""
    c.log( 'Starting' )
    popularities = collections.defaultdict(float)

    # Get all messages and replies for the community
    messages = ds.get_messages(dsconf, start, end, community)
    messages = common.build_msgmap(messages, ds.field('messages','user_id'))
    replies = common.build_replies(messages, ds.fields('messages'))

    # Get requested users popularities
    c.log( 'Computing users popularities ...' )
    for (user,user_messages) in messages.iteritems():
        popularities[user] = len( replies[user] ) * 1.0 / \
                                    len( user_messages )

    # Return users popularities
    c.log( 'Finished' )
    return popularities

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
