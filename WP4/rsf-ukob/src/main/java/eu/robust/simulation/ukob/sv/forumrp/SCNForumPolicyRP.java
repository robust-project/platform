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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.robust.simulation.distributions.Generic;
import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.ContentItem;
import eu.robust.simulation.policies.Policy;




public class SCNForumPolicyRP extends Policy{
	
	private Random rForum = new Random();
	private Random focusFirstThreadsR = new Random();
	

	// the number of postings that are allowed per month and user
	private int maxPostCount=10000000;
	// counts the number of postings a user has made per month
	private HashMap<Integer, Integer> policyPostCount= new HashMap<Integer, Integer>();
	
	// the maximum number of new threads allowed per month and forum for all users
	private static int maxNewThreadsPerMonth=10000000;
	private HashMap<Integer, Integer> threadsLastMonth = new HashMap<Integer, Integer>();
	

	// the number of threads on the first page, i.e. that are answered with priority
	private int pagesize = 7;
	// probability of choosing a thread only from the first page
	private double probPririzeFirstPage = 0.9;
	
	
	// Content Selection - cast for other cimanagers
	public ContentItem selectCI(User user, CIManager contentItems) {
		CIManagerForums cim = (CIManagerForums) contentItems;
		return selectCI(user, cim);
	}
	
	
	// Content Selection
	public ContentItem selectCI(User user, CIManagerForums contentItems) {

		// if no forums statistics for user available, choose arbitrary existing thread and exit
		if (user.forumActivityReply.size()==0) {
			return contentItems.getRandom();
		}
		
		// if forum statistics for user available, 
		// the user chooses amongst all the forums he is interested in, a thread is chosen amongst all threads of the forums the user is interested in
		// i.e. all threads in these forums have the same probablity of being chosen
		
		// cumulate all past posts in relevant forums
		int sumsize=0;
		for (int forum: user.forumActivityReply.keySet()) {
			sumsize=sumsize+contentItems.getForumSize(forum);
		}
		// compute relative probability of all forums and choose one forum
		Map<Integer, Double> relForumsizes = new HashMap<Integer,Double>();
		for (int forum: user.forumActivityReply.keySet()) {
			relForumsizes.put(forum, ((double) ((double) contentItems.getForumSize(forum)) / ((double) sumsize )    ));
		}
		int forumToChoose = Generic.getSample(relForumsizes, rForum); 

		
		ContentItem ci = selectThreadFromForum(contentItems, forumToChoose);
		
		//System.out.println("Special Policy select CI from "+forumCountToChoose+" different forums: forum "+forum+" CI: "+ci);
		return ci;
	}
	
	// this method checks whether the user is allowed to create more content, if so, the content is created, else no new content is created
	public boolean addThread(User user, CIManagerForums contentItems, Thread newThread) {
		int forumNumber = newThread.getForum();
		
		// check if the user has exceeded his personal number of allowed postings per month
		if (newThreadsAllowedUserCheck(user, contentItems) &&
				newThreadsAllowedGlobalCheck(contentItems, forumNumber)) {
			contentItems.add(newThread); 
			return true;
		} else {
			// user is not allowed to post this content item
			return false;
		}
	}
	
	// checks whether the overall count of threads (all forums!) is higher than allowed, USER SPECIFIC policy
	private boolean newThreadsAllowedUserCheck(User user, CIManagerForums contentItems) {
		int userID = user.getID();
		
		// check if the user has exceeded his personal number of allowed postings per month
		if (getCurrentUserPostCount(userID)>=maxPostCount) {
			// user has already posted enough and is not allowed to post more items
			return false;
		} else {
			// user is allowed to post this content item
			increaseCurrentUserPostCount(userID);
			return true;
		}
	}
	
	
	// checks whether the overall count of threads per forum is higher than allowed, globally i.e. not user specific policy
	private boolean newThreadsAllowedGlobalCheck(CIManagerForums contentItems, int forumNumber) {
		int numberLastMonth = 0;
		int numberActual = contentItems.getForumSize(forumNumber);
		if (threadsLastMonth.containsKey(forumNumber)) {
			numberLastMonth = threadsLastMonth.get(forumNumber);
		} else {
			numberLastMonth = 0;
		}
		if (numberActual-numberLastMonth>=maxNewThreadsPerMonth) 
			return false;
		else
			return true;
	}

	
	
	private int getCurrentUserPostCount(int userID) {
		if (policyPostCount.containsKey(userID)) {
			return policyPostCount.get(userID);
		} else {
			return 0;
		}
	}
	
	private void increaseCurrentUserPostCount(int userID) {
		int actCount = getCurrentUserPostCount(userID);
		policyPostCount.put(userID, actCount+1);
	}
	
	
	public void setMaxThreadCount(int maxCount) {
		maxPostCount = maxCount;
	}
	
	
	public void newMonth(CIManagerForums contentItems) {
		policyPostCount.clear();
		
		threadsLastMonth.clear();
		for (int forum : contentItems.getForums()) {
			threadsLastMonth.put(forum, contentItems.getForumSize(forum));
		}

	}
	
	private Thread selectThreadFromForum(CIManagerForums contentItems, int forumNumber) {
		
		int forumNumberToChoose;
		
		// if no thread exists in the forum a random forum is chosen
		if (! contentItems.forumHasThreads(forumNumber)) {
			forumNumberToChoose = contentItems.getRandomForumNumber();
		} else {
			forumNumberToChoose = forumNumber;
		}
		
		//return contentItems.getRandomThreadFromForumN(forumNumberToChoose);
		
		// returns thread according to model of Kumar et.al 2010
		//return contentItems.getRandomThreadAccordingTimeDiscountAndDegree(forumNumberToChoose);
		
		// returns thread according to model with strict border of first page;
		// with a specific probability only the first page is considered for choosing a thread to reply to
		if (focusFirstThreadsR.nextDouble()<=probPririzeFirstPage) {
			// the user chooses a thread from the first site
			return contentItems.getNewestXRandomThreadFromForumN(forumNumberToChoose, pagesize);
		} else  {
			// the user chooses a thread from all existing threads
			return contentItems.getRandomThreadFromForumN(forumNumberToChoose);
		}
		
		
	}
	
	

}
