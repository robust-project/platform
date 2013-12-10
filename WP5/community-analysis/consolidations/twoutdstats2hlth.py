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

"""Consolidation - TiddlyWiki out-degrees [0,inf[ stats to health [0,1]."""

import consolidations.common as common

################################################################################

def compute( stats ):
    """Usage: health = compute( outdegree_stats )"""
    if stats['used_size'] == 0:   # No stats could be computed
        return 0.0
    return common.stats2tanh( stats, 'mean', 68.0 )   # Should be auto-computed!

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
