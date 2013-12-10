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

"""Consolidation - Tabulate key-value paired values."""

################################################################################

def compute( values, separator='\t' ):
    """Usage: values = compute( values )"""
    output = ''
    for k,v in values.iteritems():
        output += '{0}{1}{2}\n'.format(k,separator,v)
    return output[:-1]   # Remove last \n

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
