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

"""Handy console utilities."""

import sys
import datetime
import inspect

################################################################################

def log( msg, verbose = True ):
    """Echo high-precision time stamped messages to stderr output."""

    # 'No output' object
    class Discard(object): #pylint: disable-msg=R0903,C0111
        def write(self, data): #pylint: disable-msg=C0111
            pass

    # Verbose output?
    output = Discard()
    if verbose:
        output = sys.stderr

    # Print message
    caller = inspect.stack()[1][3]
    print >> output, '{time:%Y-%m-%d %H:%M:%S.%f} [{caller}] {msg}' \
             .format( time=datetime.datetime.now(), caller=caller, msg=msg )

################################################################################

if __name__ == '__main__':
    print 'Module: %s' % __doc__
