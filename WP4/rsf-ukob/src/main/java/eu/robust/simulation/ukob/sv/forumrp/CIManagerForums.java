/*
 * Copyright 2012 WeST Institute - University of Koblenz-Landau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by       	: Felix Schwagereit 
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */
 
package eu.robust.simulation.ukob.sv.forumrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import eu.robust.simulation.distributions.Generic;
import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.ContentItem;

public class CIManagerForums extends CIManager {
	
	private Map<Integer,List<Thread>> forums = new HashMap<Integer,List<Thread>>();
	

	private Random chooseThreadR = new Random();
	
	private int postCount=0;
	private int maxPostCount=1000000;
	
	private Map<Integer,Map<Integer, Double>> probStatistics = new HashMap<Integer,Map<Integer, Double>>();
	
	private void addToForum(Thread t) {
		int forumnumber = t.getForum();
		if (! forums.containsKey(forumnumber)) {
			List<Thread> threadlist = new ArrayList<Thread>();
			forums.put(forumnumber, threadlist);
		}
		List<Thread> threadlist = forums.get(forumnumber);
		threadlist.add(t);
	}
	
	// Policy for monthly content restriction
	public void newMonth() {
		postCount=0;
	}
	public void setMaxThreadCount(int value) {
		maxPostCount=value;
	}
	
	public Set<Integer> getForums() {
		return forums.keySet();
	}
	
	public void add(ContentItem ci) {
		Thread t = (Thread) ci;

		if (t.getForum()!=56) {
			super.add(t);
			addToForum(t);	
		} else {
			if (postCount>=maxPostCount) {
				// nothing
			} else {
				postCount++;
				super.add(t);
				addToForum(t);
			}
		}
		
		
//		if (!(t.getForum()==56 && postCount>=maxPostCount)) {
//			super.add(t);
//			addToForum(t);	
//		} 
				
	}
	
	public int getForumSize(int forum) {
		if (forums.containsKey(forum)) {
			return forums.get(forum).size();
		} else {
			return 0;
		}
	}
	
	public int getRandomForumNumber() {
		List<Integer> forumList = new LinkedList<Integer>();
		forumList.addAll(forums.keySet());
		
		Random r = new Random();
		int position = r.nextInt(forumList.size());
		return forumList.get(position);
	}
	

	public boolean forumHasThreads(int forumNumber) {
		
		if (forums.containsKey(forumNumber))
			if (forums.get(forumNumber).size()>0) 
				return true;
			
		return false;
	}
	
	public Thread getRandomThreadFromForumN(int forumNumber) {
		List<Thread> threadlist = forums.get(forumNumber);
		int numberOfThreads = threadlist.size();
		int threadToChoose = chooseThreadR.nextInt(numberOfThreads);
		return threadlist.get(threadToChoose);
	}
	
	public Thread getNewestXRandomThreadFromForumN(int forumNumber, int newestXThreadsToConsider) {
		List<Thread> threadlist = forums.get(forumNumber);
		int numberOfThreads = threadlist.size();
		int consideredNumber = Math.min(numberOfThreads, newestXThreadsToConsider);
		int threadToChoose = chooseThreadR.nextInt(consideredNumber);
		return threadlist. get(numberOfThreads-1-threadToChoose);
	}
	
	
	// according to generative thread model of Kumar et.al 2010
	public Thread getRandomThreadAccordingTimeDiscountAndDegree(int forumNumber) {
		
		return (Thread) items.get(Generic.getSample(probStatistics.get(forumNumber), chooseThreadR));
		
	}
	
	public void setCurrentTick(int aTick) {
		super.setCurrentTick(aTick);
		updateCIStatistics();
	}	

	public void updateCIStatistics() {
		

		double alpha = 0.01;
		double tau = 0.001;
		double delta = 0;

		
		for (int forumNumber : forums.keySet()) {
			
			double probsum = 0;
		
			Map<Integer, Double> tempThreadProbability = new HashMap<Integer, Double>();
			
			for (Thread t : forums.get(forumNumber)) {
				double degree = t.replies.size();
				double age = (actualTick - t.creationtime)/(6*24);
				double probability =  Math.pow(tau, age); //+alpha*degree; // +delta +alpha*degree;
				tempThreadProbability.put(t.getID(), probability);
				probsum=probsum+probability;
			}
			
			Map<Integer, Double> relThreadProbability = new HashMap<Integer, Double>();
			
			for (int threadNumber : tempThreadProbability.keySet()) {
				double relProb = tempThreadProbability.get(threadNumber) / probsum;
				relThreadProbability.put(threadNumber , relProb);
			}
			probStatistics.put(forumNumber, relThreadProbability);
			
		}
	
		
	}
	
	
	
	
/*	public Thread getRandomThreadFromForum(int forum) {
		List<Thread> threadlist = forums.get(forum);
		
		// if no thread exists in the forum a random forum is chosen
		if (threadlist == null) {
			Random r = new Random();
			int forumCountToChoose = r.nextInt(forums.size());
			List<List<Thread>> forumTempList = new LinkedList<List<Thread>>();
			forumTempList.addAll(forums.values());
			threadlist = forumTempList.get(forumCountToChoose);
		}
		
		
		int numberOfThreads = threadlist.size();
		
		// the number of threads that are answered with priority
		int pagesize = 30;
		
		if ((focusFirstThreadsR.nextDouble()<=0.9) && (numberOfThreads>=pagesize)) {
			// the user chooses a thread from the first site
			int threadToChoose = numberOfThreads-1-chooseThreadR.nextInt(pagesize);
			return threadlist.get(threadToChoose);
		} else  {
			// the user chooses a thread from all existing threads
			int threadToChoose = chooseThreadR.nextInt(numberOfThreads);
			return threadlist.get(threadToChoose);
		}
		
		
	}*/

}
