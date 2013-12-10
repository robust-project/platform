/*
 * Copyright 2012 University of Southampton IT Innovation Centre 
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
 * Created by       	: Mariusz Jacyno
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */ 
package uk.ac.soton.itinnovation.robust.catsim.entities;

import java.util.Random;

import org.apache.log4j.Logger;

import uk.ac.soton.itinnovation.prestoprime.iplatform.batch.ModelRunner;

import eu.robust.simulation.distributions.Generic;
import eu.robust.simulation.distributions.Poisson;
import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.Reply;

public class User extends eu.robust.simulation.entities.User{

	static Logger logger = Logger.getLogger(ModelRunner.class);

	private int userId = 0;
	private Random rThreadC = new Random();
	private Random rReplyC = new Random();
	private Random rReplyQ = new Random();
	
	private double avgThreadCreationRate;
	private double avgReplyRate;
	private double avgReplyQuality;
	//private double avgThreadAge = 3;
	private double[] replyStats;
	
	public User(double CavgThreadCreationRate, double CavgReplyRate, double CavgReplyQuality, int userIdRef) {
		super();
		
		logger.debug("created user: id: "+userId+" avg creation rate: "+CavgThreadCreationRate+" avg reply rate: "+CavgReplyRate+" avg reply quality: "+CavgReplyQuality);

		avgThreadCreationRate = CavgThreadCreationRate;
		avgReplyRate = CavgReplyRate;
		avgReplyQuality = CavgReplyQuality;
		
		userId = userIdRef;
	}
	
	public void setReplyStats(int[] points) {
		logger.debug("--> set reply stats method invoked in user with id: "+userId);

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
		logger.debug("--> create threads method invoked in user with id: "+userId);

		int numberOfThreadsToCreate = Math.round(Poisson.getSample(avgThreadCreationRate, rThreadC));
		for(int i=1; i<=numberOfThreadsToCreate; i++) {
			contentItems.add(new Thread(this, 0));
		}
	}
	
	protected void createReplies(CIManager contentItems) {
		logger.debug("--> create replies method invoked in user with id: "+userId);

		int numberOfRepliesToCreate = Math.round(Poisson.getSample(avgReplyRate, rReplyC));
		numberOfRepliesToCreate= Math.min(numberOfRepliesToCreate, contentItems.getSize()); // do not create more replies than threads exist
		for(int i=1; i<=numberOfRepliesToCreate; i++) {
			Thread threadForReply = (Thread) contentItems.getRandom();
			Reply newReply = new eu.robust.simulation.entities.Reply(this, Generic.getSample(replyStats, rReplyQ));
			threadForReply.addReply(newReply);
		}
	}

	protected void revisitOwnOpenThreads(CIManager contentItems) {
		logger.debug("--> revisit own open threads method invoked in user with id: "+userId);
		
		// closes thread
		// awards points
		// replies to own thread
	
	}

	public void act(CIManager contentItems)
	{
		logger.debug("-> act method invoked in user with id: "+userId);
		super.act(contentItems);
	}

}
