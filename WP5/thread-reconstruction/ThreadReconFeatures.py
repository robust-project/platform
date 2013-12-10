# coding=utf8
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

# 11/04/2012

###############################################################################
# imports
###############################################################################

import re
from datetime import datetime
import re
from tfidf import tfidf
from cosim import cosim


###############################################################################
# global variables
###############################################################################

letter_map = [(u'\xf1', 'n'), (u'\xf3', 'o'), (u'\xf8', 'o'), (u'\xe6', 'a'), (u'\xe6', 'e'), (u'\xe6', 'ae'), (u'\xe6', 'ea'), (u'\xed', 'i'), (u'\xe9', 'e'), (u'\xe4', 'a'), (u'\xe4', 'ae'), (u'\xe1', 'a'), (u'\xf6', 'o'), (u'\xf6', 'oe'), (u'\xa1', '!'), (u'\xfc', 'u'), (u'\xfc', 'ue'), (u'\xe7', 'c'), (u'\xe3', 'a')]

STOP_WORDS = """a able about across after all almost also am among
an and any are as at be because been but by can cannot could dear did
do does either else ever every for from get got had has have he her
hers him his how however i if in into is it its just least let like
likely may me might most must my neither no nor not of off often on
only or other our own rather said say says she should since so some
than that the their them then there these they this tis to too twas us
wants was we were what when where which while who whom why will with
would yet you your""".split()
	
nonascii = re.compile('[^a-zA-Z0-9>.,;:-= \n]')

emailSig = re.compile("""--~--~---------~--~----~------------~-------~--~----~
You received this message because you are subscribed to the Google Groups \"TiddlyWiki\" group\.
To post to this group, send email to TiddlyWiki@googlegroups\.com
To unsubscribe from this group, send email to TiddlyWiki\+unsubscribe@googlegroups\.com
For more options, visit this group at http://groups\.google\.com/group/TiddlyWiki\?hl=en
-~----------~----~----~----~------~----~------~--~---|--
You received this message because you are subscribed to the Google Groups \"TiddlyWiki\" group\.
To post to this group, send email to tiddlywiki@googlegroups\.com\.
To unsubscribe from this group, send email to tiddlywiki+unsubscribe@googlegroups\.com\.
For more options, visit this group at http://groups\.google\.com/group/tiddlywiki\?hl=en\.""")


###############################################################################
# functions
###############################################################################

def timer(action, note=''):
	global timer_startTime
	if action == 'start': timer_startTime = datetime.now()
	else: 
		timer_stopTime = datetime.now()
		timedelta = timer_stopTime - timer_startTime
		print 'timer stopped: %s %s' % (timedelta, note)


def getTimeDiffH26(pair): #timeDiffH for python2.6 (without total_seconds)
	diff = pair[4] - pair[3]
	return diff.days*24+float(diff.seconds)/3600


def getIndex(replies, orig):
	if replies[0][0] == orig: return 0
	return [replies.index(pair)+1 for pair in replies if pair[1] == orig][0]


def getCosim(replies, pair):
	return cosim(tfidf(getIndex(replies, pair[0])), tfidf(len(replies)))


def parseString(text):
	# letters = re.compile("[^a-zA-Z0-9]")
	return nonascii.sub('', text)

	
def quotedSAP(text, quote):
	quotedSection = re.search('.*?<div class="jive-quote">&gt;'+'(.*?)'+'</div>', quote)
	if quotedSection:
		m = re.search(parseString(quotedSection.group(1)), parseString(text))
		if m != None: return 1
	return 0
	
	
def quotedBoardsie(text, quote):
	quotedSection = re.search('\[QUOTE.*?\](.*?)\[\/QUOTE\]', quote.decode('ascii', 'replace').encode('ascii', 'replace'))
	if quotedSection:
		m = re.search(parseString(quotedSection.group(1)), parseString(text))
		if m != None: return 1
	return 0
	
	
def quotedTidWik(text, quote):
	text = re.sub(nonascii, '', re.sub(emailSig, '', text)).split('\n')
	quote = [item[1:] for item in re.sub(nonascii, '', re.sub(emailSig, '', quote)).split('\n') if item != '' and item[:1] == '>']
	text = [item.strip() for item in text if item != '']
	quote = [item.strip() for item in quote if item != '']
	quotedSection = []
	if quote == []: return 0
	for item in text:
		if item == quote[0]:
			quotedSection += [quote[0]]
			quote = quote[1:]
	        if quote == []: break
	if quote == [] and quotedSection != ['>']: return 1
	return 0


def getQuoted(pair):
	if quotedBoardsie(pair[7], pair[8]) > 0: return 1
	# if quotedSAP(pair[7], pair[8]) > 0: return 1
	if quotedTidWik(pair[7], pair[8]) > 0: return 1
	return 0


def getNewestParent(replies, pair):
	if [item[5] for item in replies[:replies.index(pair)+1]].count(pair[5]) < 2: return 2
	anotherPostInBetween = 0
	chain = [replies[0][5]]+[item[6] for item in replies[:replies.index(pair)+1]]
	if pair[0] == replies[0][0]:
		i = 0
	else:
		i = [replies.index(item) for item in replies if item[1] == pair[0]][0]+1
	if pair[5] in chain[i+1:]:
		anotherPostInBetween = 1
	return abs(anotherPostInBetween-1)


def complexityInformativity(text):
	return cominf.complexity(text), cominf.informativity(text)
	

def getQuality(pair):
	cominf0 = complexityInformativity(pair[7])
	cominf1 = complexityInformativity(pair[8])
	return cominf1[0]-cominf0[0], cominf1[1]-cominf0[1]


def getDistance(replies, pair):
	return len(replies)-getIndex(replies, pair[0])


def getPosition(replies):  # on-the-fly thread length, per reply
	return len(replies)


def getLastOwnPost(replies, pair):
	replies = replies[:replies.index(pair)+1]
	idSeen = [replies[0][0]]
	nameSeen = [replies[0][5]]
	for k in xrange(0, len(replies)):
		idSeen.append(replies[k][1])
		nameSeen.append(replies[k][6])
	if replies[k][5] == replies[k][6]:
		if replies[k][5] == replies[k-1][6]: return 'fs'  # selfReplyImmediate, followup_self		
		else: return 'is'  # selfReplySkipping, inbetween_self
	else:
		if replies[k][6] == replies[k-1][6]: return 'fp'  # doublePostDiffPeers, followup_peer
		else:
			index = idSeen.index(replies[k][0])
			if replies[k][6] in nameSeen[index:-1]: return 'ib'  # replyBeforeLastOwn, inbetween_before
			else:
				if nameSeen[:-1].count(replies[k][6]) > 0:
					index = len(nameSeen)-nameSeen[:-1][::-1].index(replies[k][6])-1
					if replies[k][0] in idSeen[index:]: return 'ip'  # peerInBetween, inbetween_peer
	return 'nl'  # no last post


def getDialogue(replies):
	indexing = [item[1] for item in replies[:-1]][::-1]+[replies[0][0]]
	relevant = [[item[0], item[1], item[5], item[6]] for item in replies[:-1][::-1] if replies[-1][6] in item[5:7]]
	peerSeen = {}
	while relevant != []:
		i = abs(relevant[0][2:4].index(replies[-1][6])-1)
		peer = relevant[0][i], relevant[0][i+2]  # id, name
		peerSeen[peer[0]] = [1, indexing.index(peer[0])+1]
		prevChildName = relevant[0][3]
		prevParentId = relevant[0][0]
		relevantPair = [item for item in relevant if item[1] == prevParentId and item[2] == prevChildName]
		while relevantPair != []:
			peerSeen[peer[0]][0] += 1
			prevChildName = relevantPair[0][3]
			prevParentId = relevantPair[0][0]
			relevantPair = [item for item in relevant if item[1] == prevParentId and item[2] == prevChildName]
		relevant = [item for item in relevant if peer[1] not in item[2:4]]
	# dist = indexing.index(replies[-1][0])+1
	# dist = ([replies[0][5]]+[item[6] for item in replies[:replies.index(replies[-1])]])[::-1].index(replies[-1][5])+1
	if peerSeen == {}:
			return -99, -99#, dist
	if replies[-1][0] not in peerSeen:
		peerSeen[replies[-1][0]] = [0, 99]
	minDist = min([item[1] for item in peerSeen.values()])
	closestDialogue = [item for item in peerSeen.values() if item[1] == minDist][0]
	# return peerSeen[replies[-1][0]][0]-closestDialogue[0], peerSeen[replies[-1][0]][1]-closestDialogue[1], dist
	return peerSeen[replies[-1][0]][0], peerSeen[replies[-1][0]][1]#, dist-(indexing[:].index(replies[-1][0])+1)
	# diaLen, diaDist, lastOccDist: dist to last occurrence of peer


def getActivity(pair):
	author0 = authorActivity[pair[5]]
	author1 = authorActivity[pair[6]]
	total = author1[0]-author0[0]
	relative = author1[0]/float(len(author1[1]))-author0[0]/float(len(author0[1]))
	return total, relative


def getPrevsParent(replies):
	if replies[-1][0] == replies[-2][0]: return 1
	return 0


def getFirstPost(replies):
	if replies[-1][0] == replies[0][0]: return 1
	return 0
