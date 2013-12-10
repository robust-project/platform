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

"""Aggregation - Computes statistics of 'values'."""

import numpy

################################################################################

def compute( values ):
    """Usage: stats = compute( values )"""

    # Filter out inf, -inf values
    data_size = len(values)
    values = [ x for x in values.values()
               if x != float('inf') and x != float('-inf') ]
    used_size = len(values)
    if used_size == 0:
        return { 'data_size': data_size, 'used_size': used_size }

    # Compute stats
    min_ = numpy.min( values )
    max_ = numpy.max( values )
    average = numpy.average( values )
    mean = numpy.mean( values )
    median = numpy.median( values )
    std = numpy.std( values )
    var = numpy.var( values )
    histogram = numpy.histogram( values, numpy.floor(numpy.sqrt(used_size)) )

    return { 'data_size': data_size, 'used_size': used_size,
             'min': min_, 'max': max_, 'average': average,
             'mean': mean, 'median': median, 'std': std,
             'var': var, 'hist_freqs': histogram[0].tolist(),
             'hist_edges': histogram[1].tolist() }

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
