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

"""Common functions for all consolidations."""

import numpy
import math

TANH_LIMIT = 18.714973875118524      # Must be computed per platform

################################################################################

def arccot( number ):
    """Computes the arccot of 'number'."""
    if number == 0:
        return numpy.pi / 2
    else:
        return math.atan( 1.0 / number )

def stats2tanh( stats, field, ref_value ):
    """Usage: tanh = stats2tanh( stats, field, ref_value )"""
    return numpy.tanh(float(stats[field]) * TANH_LIMIT / ref_value) \
           * ( float(stats['used_size']) / stats['data_size'] )

def stats2arccot( stats, field, ref_value ):
    """Usage: arccot = stats2arccot( stats, field, ref_value )"""
    return arccot(float(stats[field]) / ref_value) \
           / ( numpy.pi / 2 ) \
          * ( float(stats['used_size']) / stats['data_size'] )

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
