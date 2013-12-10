###############################################################################
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
# functions
###############################################################################

def cosim(vectorA, vectorB):
	aboveFraction = sum([a*b for a, b in zip(vectorA, vectorB)])
	belowFraction = (sum([item**2 for item in vectorA])**0.5)*(sum([item**2 for item in vectorB])**0.5)
	if aboveFraction == 0 or belowFraction == 0: return 0.0
	return aboveFraction/float(belowFraction)