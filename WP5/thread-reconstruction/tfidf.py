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
# imports
###############################################################################

import re
import PorterStemmer
import math


###############################################################################
# global variables
###############################################################################

porter = PorterStemmer.PorterStemmer()
idfValues = {}
tfidfVectors = {}
stemmedWords = {}
corpus_processed = []
initialVector = []

STOP_WORDS = set('''a able about across after all almost also am among
an and any are as at be because been but by can cannot could dear did
do does either else ever every for from get got had has have he her
hers him his how however i if in into is it its just least let like
likely may me might most must my neither no nor not of off often on
only or other our own rather said say says she should since so some
than that the their them then there these they this tis to too twas us
wants was we were what when where which while who whom why will with
would yet you your'''.split())
NO_WORDS = re.compile("[^a-z0-9' ]")


###############################################################################
# global functions
###############################################################################

def preprocessCorpus(corpus):  # corpus is a list of documents
	global stemmedWords, corpus_processed, idfValues, initialVector, tfidfVectors
	corpus_processed = []
	stemmedWords = {}
	for i in xrange(0, len(corpus)):
		document_processed = []
		for word in (item for item in re.sub(NO_WORDS, '', corpus[i].lower()).split() if item not in STOP_WORDS):
			if word not in stemmedWords: 
				word_stemmed = porter.stem(word, 0, len(word)-1)
				stemmedWords[word] = word_stemmed
			else:
				word_stemmed = stemmedWords[word]
			document_processed += [word_stemmed]
		corpus_processed += [document_processed]
	tfidfVectors = {}
	initialVector = [item for item in sorted(set(stemmedWords.values()))]
	idfValues = buildIdfValues(corpus_processed)


def buildIdfValues(corpus_processed):
	idfValues = {}
	documentCount = float(len(corpus_processed))
	for word_stemmed in set(stemmedWords.values()):
		idfValues[word_stemmed] = math.log(documentCount/len([document for document in corpus_processed if word_stemmed in document]), 10)
	return idfValues


def tfidf(documentId):
	if documentId in tfidfVectors:
		return tfidfVectors[documentId]
	tfidfVector = computeTfidfVector(documentId)
	tfidfVectors[documentId] = tfidfVector
	return tfidfVector
	

def computeTfidfVector(documentId):
	wordVector = [item for item in initialVector]
	for i in xrange(0, len(wordVector)):
		tf = computeTf(wordVector[i], documentId)
		idf = idfValues[wordVector[i]]
		wordVector[i] = tf*idf
	return wordVector


def computeTf(word, documentId):
	return corpus_processed[documentId].count(word)