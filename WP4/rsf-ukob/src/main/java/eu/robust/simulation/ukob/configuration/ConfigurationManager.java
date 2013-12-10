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
 
 package eu.robust.simulation.ukob.configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.robust.simulation.policies.Policy;
import eu.robust.simulation.ukob.entities.User;
import eu.robust.simulation.dbconnection.*;

public class ConfigurationManager extends
		eu.robust.simulation.configuration.ConfigurationManager {
	
	private List<Map<String,String>> userstats = null;
	
	private Policy policy=null;
	
	public ConfigurationManager(String configurationFile) {
		super(configurationFile);
		getUserParameters();
	}
	
	public Map<String,String> drawUserConf(Random r) {
		
		return userstats.get(r.nextInt(userstats.size()-1));
	}
	
	public void setPolicy(Policy p) {
		policy = p;
	}
	
	public int getNumberInitUsers() {
		return configuration.getNumberOfAgents();
	}
	

	// get user parameters
	private void getUserParameters() {
		Connectdb db = new Connectdb("jdbc:oracle:thin:@//robustdb:1521/orcl", "SAP_DATEN", "none");
		String[] cols = {"actduration", "questions", "replies", "points", "points0", "points1", "points2", "points3"};
		String query = "select * from userstats";
		userstats = db.getTable(cols, query);
		logger.debug(userstats.size());
		db.disconnectdb();
	}
	
	public List<User> createUsers() {
		
		// create init users and content items
		
		// create init users
		int numberOfUsers = configuration.getNumberOfAgents();
		Random r = new Random();
		List<User> users = new LinkedList<User>();
		for (int i=0; i<numberOfUsers; i++) {
			Map<String,String> userConf = drawUserConf(r);
			
			int duration = Integer.parseInt(userConf.get("actduration"));
			int questions = Integer.parseInt(userConf.get("questions"));
			int replies = Integer.parseInt(userConf.get("replies"));
			int points = Integer.parseInt(userConf.get("points"));
			
			double avgThreadCreationRate = (double)questions/(double)duration;
			double avgAnswerRate = (double)replies/(double)duration;
			double avgAnswerQuality = (double)points/(double)replies;
			
			eu.robust.simulation.ukob.entities.User u = new eu.robust.simulation.ukob.entities.User(avgThreadCreationRate, avgAnswerRate, avgAnswerQuality);
			
			int[] pointStats = new int[4];
			pointStats[0]=Integer.parseInt(userConf.get("points0"));
			pointStats[1]=Integer.parseInt(userConf.get("points1"));
			pointStats[2]=Integer.parseInt(userConf.get("points2"));
			pointStats[3]=Integer.parseInt(userConf.get("points3"));
			u.setReplyStats(pointStats);
			u.setPolicy(policy);
			
			users.add(u);	
			System.out.println("Create User"+i);
		}
		return users;
	}
	

}
