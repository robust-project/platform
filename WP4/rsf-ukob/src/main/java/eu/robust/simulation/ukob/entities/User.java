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
 
package eu.robust.simulation.ukob.entities;

import java.util.Random;

import eu.robust.simulation.distributions.Generic;
import eu.robust.simulation.distributions.Poisson;
import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.Reply;

public class User extends eu.robust.simulation.entities.User{

	
	private Random rThreadC = new Random();
	private Random rReplyC = new Random();
	private Random rReplyQ = new Random();
	
	private double avgThreadCreationRate;
	private double avgReplyRate;
	private double avgReplyQuality;
	//private double avgThreadAge = 3;
	private double[] replyStats;
	
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
		int numberOfThreadsToCreate = Math.round(Poisson.getSample(avgThreadCreationRate, rThreadC));
		for(int i=1; i<=numberOfThreadsToCreate; i++) {
			contentItems.add(new Thread(this, 0));
		}

	}
	
	protected void createReplies(CIManager contentItems) {
		int numberOfRepliesToCreate = Math.round(Poisson.getSample(avgReplyRate, rReplyC));
		numberOfRepliesToCreate= Math.min(numberOfRepliesToCreate, contentItems.getSize()); // do not create more replies than threads exist
		for(int i=1; i<=numberOfRepliesToCreate; i++) {
			Thread threadForReply = (Thread) policy.selectCI(this, contentItems);
			Reply newReply = new eu.robust.simulation.entities.Reply(this, Generic.getSample(replyStats, rReplyQ));
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