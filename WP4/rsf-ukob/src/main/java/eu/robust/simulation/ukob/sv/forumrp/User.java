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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.robust.simulation.distributions.Generic;
import eu.robust.simulation.distributions.Poisson;
import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.Reply;

public class User extends eu.robust.simulation.entities.User{

	
	private Random rThreadC = new Random();
	private Random rReplyC = new Random();
	private Random rReplyQ = new Random();
	private Random rThreadF = new Random();
	private Random rReplxF = new Random();

	
	private int absActivityThread;
	private int absActivityReply;
	
	private double avgThreadCreationRate;
	private double avgReplyRate;
	private double avgReplyQuality;
	//private double avgThreadAge = 3;
	private double[] replyStats;
	public Map<Integer,Double> forumActivityThread = new HashMap<Integer,Double>();
	public Map<Integer,Double> forumActivityReply = new HashMap<Integer,Double>();
	
	
	public void setAbsActivityThread(int absActivityThreadS) {
		absActivityThread = absActivityThreadS;
	}
	
	public void setAbsActivityReply(int absActivityReplyS) {
		absActivityReply = absActivityReplyS;
	}

	public void setForumActivityThread(int forum, double postings) {
		forumActivityThread.put(forum, postings );
	}
	
	public void setForumActivityReply(int forum, double postings) {
		forumActivityReply.put(forum, postings );
	}

	// TeStat
	static public int allReps=0;
	
	public User(double CavgThreadCreationRate, double CavgReplyRate, double CavgReplyQuality) {
		super();
		avgThreadCreationRate = CavgThreadCreationRate;
		avgReplyRate = CavgReplyRate;
		avgReplyQuality = CavgReplyQuality;
	}
	

	
	public void setReplyStats(int[] points) {
		int sumReplies=0;
		replyStats = new double[points.length];
		for (int i=0; i< points.length; i++) {
			sumReplies=sumReplies+points[i];
		}
		for (int i=0; i< points.length; i++) {
			replyStats[i]=points[i]/(double)sumReplies;
		}
	}
	

		
	
	protected void createThreads(CIManager contentItems) {
		
		SCNForumPolicyRP pol = (SCNForumPolicyRP) policy;
		
		// number of new threads to create according to personal acitivity rate
		int numberOfThreadsToCreate = Math.round(Poisson.getSample(avgThreadCreationRate, rThreadC));
		
		// create the specific number of threads
		for(int i=1; i<=numberOfThreadsToCreate; i++) {
			int forumOfThread;
			
			// choose forum for new thread
			// if there exist forum statistics about the user, choose according to user's preferences
			if (! forumActivityThread.isEmpty()) {
				forumOfThread = Generic.getSample(forumActivityThread, rReplyQ);
			} else {
			// else get a random forum from a random existing thread
				Thread randomThread = (Thread) contentItems.getRandom();
				forumOfThread = randomThread.getForum();
			}
			
			Thread newThread = new Thread(this, forumOfThread);
			
			// the policy 'decides' whether the new thread will be created within the platform
			pol.addThread(this, (CIManagerForums) contentItems, newThread); 
		}

	}
	
	protected void createReplies(CIManager contentItems) {

		// number of replies according to personal activity rate
		int numberOfRepliesToCreate = Math.round(Poisson.getSample(avgReplyRate, rReplyC));
		numberOfRepliesToCreate= Math.min(numberOfRepliesToCreate, contentItems.getSize()); // do not create more replies than threads exist
		
		// create the specific number of replies
		SCNForumPolicyRP pol = (SCNForumPolicyRP) policy;
		for(int i=1; i<=numberOfRepliesToCreate; i++) {		
			// choose thread for replying according to policy (personal preferences)
			Thread threadForReply = (Thread) pol.selectCI(this, contentItems);
			Reply newReply = new eu.robust.simulation.entities.Reply(this, Generic.getSample(replyStats, rReplyQ));
			newReply.setInitValues(0, this.actSimulationTick);
			threadForReply.addReply(newReply);
			// TeStat
			contentItems.incExtCount();

		}
	}
	
	
	protected void revisitOwnOpenThreads() {
	
		// closes thread
		// awards points
		// replies to own thread
	
	}

}