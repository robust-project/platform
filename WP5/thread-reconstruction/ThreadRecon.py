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
__author__ = 'erik <erik.aumayr@deri.org>'
__version__ = 'prototype'
'''
description: this jython script connects to a specified database and classifies posts in a thread into reply and non-reply. MySQL connection details and query are to be specified in the config.ini
'''

from com.ziclix.python.sql import zxJDBC as sql
from datetime import datetime as d
import random as rnd
import re
from tfidf import preprocessCorpus
from tfidf import tfidf
from cosim import cosim
import java.io.FileReader as FileReader
import weka.core.Instances as Instances
import weka.core.Instance as Instance
import weka.classifiers.trees.J48 as J48
import weka.classifiers.functions.LinearRegression as LinReg
import weka.core.SerializationHelper as serialize
import os.path as Path
from ThreadReconFeatures import *
import sys



class MySQLAccess:
	
	
	db = None
	driver = 'org.gjt.mm.mysql.Driver'
	cursorList = []
	
	
	def __init__(self, url='', user='', passwd='', toQuery=''):
		self.url = url
		self.user = user
		self.passwd = passwd
		self.toQuery = toQuery
		
		
	def connect(self):
		db = sql.connect(self.url, self.user, self.passwd, self.driver)
		return db
		
	
	def getCursor(self):
		cursor = db.cursor()
		self.cursorList.append(cursor)
		return cursor
	
	
	def insert(self, cursor, origpostid, replyingpostid):
		cursor.execute(toQuery % (origpostid, replyingpostid))
		
		
	def disconnect(self):
		try: 
			for cursor in self.cursorList: cursor.close()
		except:
			print 'Could not close cursors'
		try:
			db.commit()
		except:
			db.rollback()
			print 'Could not commit changes'
		try:
			db.close()
		except:
			print 'Could not close db'


		
class ThreadRecon:


	def computeFeatures(self, threadSoFar):
		time = getTimeDiffH26(threadSoFar[-1])
		cos = getCosim(threadSoFar, threadSoFar[-1])
		quoted = getQuoted(threadSoFar[-1])
		newestparent = getNewestParent(threadSoFar, threadSoFar[-1])
		# quality = getQuality(threadSoFar[-1])	
		# complexity = quality[0]
		# informativity = quality[1]
		dist = getDistance(threadSoFar, threadSoFar[-1])
		pos = getPosition(threadSoFar)
		lastown = getLastOwnPost(threadSoFar, threadSoFar[-1])
		dialogue = getDialogue(threadSoFar)
		diaLen = dialogue[0]
		diaDist = dialogue[1]
		# activity = getActivity(threadSoFar[-1])
		# activityTotal = activity[0]
		# activityRelative = activity[1]
		prevsparent = getPrevsParent(threadSoFar)
		firstpost = getFirstPost(threadSoFar)
		return time, cos, quoted, newestparent, dist, pos, lastown, diaLen, diaDist, prevsparent, firstpost

	'''
	postid 		threadid	posteddate 	content 		userid
	postid_PAR	postid_CHI	threadid	posteddate_PAR	posteddate_CHI	username_PAR	username_CHI	content_PAR	content_CHI
	0			1			2			3				4				5				6				7			8
	'''

	def threadWrapper_add(self, threadSoFar, post_PAR, post_CHI):
		threadSoFar += [[post_PAR[0], post_CHI[0], post_PAR[1], post_PAR[2], post_CHI[2], post_PAR[4], post_CHI[4], post_PAR[3], post_CHI[3]]]

	
	def timer(self, action, note=''):
		if action == 'start': self.timer_startTime = datetime.now()
		else: 
			self.timer_stopTime = datetime.now()
			timedelta = self.timer_stopTime - self.timer_startTime
			print 'timer stopped: %s %s' % (timedelta, note)

		
	def processThread(self, tempCursor, postChain, data, dataNum, j48, linreg):
		if len(postChain) < 2: return  # return if there are no replies
		threadSoFar = []
		self.threadWrapper_add(threadSoFar, postChain[0], postChain[1])
		preprocessCorpus([item[3] for item in postChain])
		dbHandler.insert(tempCursor, postChain[0][0], postChain[1][0])
		for i in xrange(2, len(postChain)):
			child = [postChain[i][0], []]
			hiLinreg = [[-1, -1.0], [-1, -1.0]]  # [postid, linregValue]
			posJ48 = [-1, 0]  # [postid, sum of yields]
			for j in xrange(0, i):
				self.threadWrapper_add(threadSoFar, postChain[j], postChain[i])
				time, cos, quoted, newestparent, dist, pos, lastown, diaLen, diaDist, prevsparent, firstpost = self.computeFeatures(threadSoFar)
				threadSoFar = threadSoFar[:-1]

				postPair = Instance(data.numAttributes())
				postPair.setValue(0, time)
				postPair.setValue(1, cos)
				postPair.setValue(2, quoted)
				postPair.setValue(3, newestparent)
				postPair.setValue(4, dist)
				postPair.setValue(5, pos)
				postPair.setValue(6, data.attribute(6).indexOfValue(lastown))
				postPair.setValue(7, diaLen)
				postPair.setValue(8, diaDist)
				postPair.setValue(9, prevsparent)
				postPair.setValue(10, firstpost)

				postPair.setDataset(dataNum)
				linregValue = linreg.classifyInstance(postPair)
				if linregValue > hiLinreg[0][1]:
					hiLinreg[1] = hiLinreg[0]
					hiLinreg[0] = [postChain[j][0], linregValue]
				else:
					if linregValue > hiLinreg[1][1]:
						hiLinreg[1] = [postChain[j][0], linregValue]
				
				postPair.setDataset(data)
				j48Class = j48.classifyInstance(postPair)
				if j48Class == 1.0:
					posJ48 = [postChain[j][0], posJ48[1]+1]
			if child[1] == []:
				if posJ48[1] == 0:
					child[1] = [hiLinreg[1][0]]
				else:
					if posJ48[1] > 1:
						child[1] = [hiLinreg[0][0]]
					else:
						child[1] = [posJ48[0]]
			self.threadWrapper_add(threadSoFar, [item for item in postChain if item[0] == child[1][0]][0], postChain[i])
			dbHandler.insert(tempCursor, child[1][0], child[0])


if __name__ == "__main__":

	def inform(text):
		print '---', text, '@', d.now()


	def progress(lastCheckPoint, threadCount):
		now = d.now()
		if (now - lastCheckPoint).seconds >= 60:
			inform('%s threads processed' % threadCount)
			return now
		else:
			return lastCheckPoint
		
	
	TrainData = 'TrainData.arff'
	TrainDataNum = 'TrainDataNum.arff'  # for lin reg classification (tweaking false positives)
	model = 'j48.model'
	modelNum = 'linreg.model'
	
	if Path.isfile('./config.ini'):
		file = open('./config.ini', 'r')
		for line in file:
			eqPos = line.find('=')
			if line[:eqPos] == 'url': url = line[eqPos+1:].strip()
			if line[:eqPos] == 'user': user = line[eqPos+1:].strip()
			if line[:eqPos] == 'passwd': passwd = line[eqPos+1:].strip()
			if line[:eqPos] == 'fromQuery': fromQuery = line[eqPos+1:].strip()
			if line[:eqPos] == 'toQuery': toQuery = re.sub(r'\/\w+?\/', r'%s', line[eqPos+1:]).strip()
		file.close()
	else:
		inform('configuration file (config.ini) is missing')
		sys.exit()

	inform('getting database connection')
	dbHandler = MySQLAccess(url, user, passwd, toQuery)
	db = dbHandler.connect()

	arffFile = FileReader(TrainData)
	data = Instances(arffFile)
	data.setClassIndex(data.numAttributes() - 1)
	arffFile.close()

	arffFileNum = FileReader(TrainDataNum)
	dataNum = Instances(arffFileNum)
	dataNum.setClassIndex(dataNum.numAttributes() - 1)
	arffFileNum.close()

	if not Path.isfile(model):
		inform('training J48')
		j48 = J48()
		j48.setOptions('-R -N 3 -Q 1 -M 2'.split()) # reduced error pruning, use j48.setOptions('-C 0.1 -M 2'.split()) for confidence interval pruning
		j48.buildClassifier(data)
		serialize.write(model, j48)
		inform('saved model to '+model)
	else:
		inform('import '+model)
		j48 = serialize.read(model)

	if not Path.isfile(modelNum):
		inform('training LinReg')
		linreg = LinReg()
		linreg.buildClassifier(dataNum)
		serialize.write(modelNum, linreg)
		inform('saved model to '+modelNum)
	else:
		inform('import '+modelNum)
		linreg = serialize.read(modelNum)
	
	inform('processing threads')
	
	threadRecon = ThreadRecon()
	threadCount = 0
	currentThread = -1
	lastCheckPoint = d.now()
	postChain = []
	cursor = dbHandler.getCursor()
	tempCursor = dbHandler.getCursor()
	cursor.execute(fromQuery)
	
	for x in xrange(0, cursor.rowcount):
		row = cursor.fetchone()
		if row[1] != currentThread:
			if postChain != []:
				threadRecon.processThread(tempCursor, postChain, data, dataNum, j48, linreg)
				threadCount += 1
				lastCheckPoint = progress(lastCheckPoint, threadCount)
			currentThread = row[1]
			postChain = []
		if not None in row:
			postChain.append(row)
	threadRecon.processThread(tempCursor, postChain, data, dataNum, j48, linreg)
	
	dbHandler.disconnect()
	inform('databdase connection closed, reconstructed threads saved')
