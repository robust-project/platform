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

"""Feature - In-degree"""

import datasources as ds
import features.common as common
import utilities.console as c
import collections

################################################################################

def compute( dsconf, community, start, end ):
    """Compute community in-degrees."""
    c.log( 'Starting' )
    indegrees = collections.defaultdict(long)

    # Get all messages and replies for the community
    messages = ds.get_messages(dsconf, start, end, community)
    messages = common.build_msgmap(messages, ds.field('messages','id'))

    # Get in-degrees
    for (id,message) in messages.iteritems():
        if message[0][ds.field('messages','target_id')] is not None:
            indegrees[message[0][ds.field('messages','user_id')]] += 1

    # Return the in-degrees
    c.log( 'Finished' )
    return indegrees

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
