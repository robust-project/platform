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

package uk.ac.soton.itinnovation.robust.catsim.bootstrap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import uk.ac.soton.itinnovation.prestoprime.iplatform.batch.ModelRunner;
import uk.ac.soton.itinnovation.robust.catsim.ModelInterface;
import uk.ac.soton.itinnovation.robust.catsim.config.ModelConfiguration;
import uk.ac.soton.itinnovation.robust.catsim.entities.User;

public class BootstrapManager {

	static Logger logger = Logger.getLogger(ModelRunner.class);
	
	private ModelInterface modelInterface = null;
	//this is where model configuration is kept
	private ModelConfiguration configuration = null;
	
	private List<Map<String,String>> userstats = null;
	
	public BootstrapManager(ModelInterface modelInterfaceRef) {
		logger.info("successfully initialised bootstrap manager...");
		this.modelInterface = modelInterfaceRef;
		this.configuration = modelInterface.getModelConfiguration();
	}
	
	public Map<String,String> drawUserConf(Random r) {
		return userstats.get(r.nextInt(userstats.size()-1));
	}
	
	public List<User> createUsers() {
		logger.debug("creating users (simulation agents)...");
		
		// create init users and content items
		
		// create init users
		int numberOfUsers = configuration.numberOfAgents;
		Random r = new Random();
		List<User> users = new LinkedList<User>();
		for (int i=0; i<numberOfUsers; i++) {
			
			int duration = Integer.parseInt("1");
			int questions = Integer.parseInt("2");
			int replies = Integer.parseInt("3");
			int points = Integer.parseInt("4");
//			Map<String,String> userConf = drawUserConf(r);
//			int duration = Integer.parseInt(userConf.get("actduration"));
//			int questions = Integer.parseInt(userConf.get("questions"));
//			int replies = Integer.parseInt(userConf.get("replies"));
//			int points = Integer.parseInt(userConf.get("points"));
			
			double avgThreadCreationRate = (double)questions/(double)duration;
			double avgAnswerRate = (double)replies/(double)duration;
			double avgAnswerQuality = (double)points/(double)replies;
			
			User u = new User(avgThreadCreationRate, avgAnswerRate, avgAnswerQuality, i);
			
			int[] pointStats = new int[4];
//			pointStats[0]=Integer.parseInt(userConf.get("points0"));
//			pointStats[1]=Integer.parseInt(userConf.get("points1"));
//			pointStats[2]=Integer.parseInt(userConf.get("points2"));
//			pointStats[3]=Integer.parseInt(userConf.get("points3"));

			pointStats[0]=Integer.parseInt("1");
			pointStats[1]=Integer.parseInt("2");
			pointStats[2]=Integer.parseInt("3");
			pointStats[3]=Integer.parseInt("4");
	
			u.setReplyStats(pointStats);
			
			users.add(u);		
			
			logger.debug("users (simulation agents) successfully created...");
		}
		return users;
	}
	

}
