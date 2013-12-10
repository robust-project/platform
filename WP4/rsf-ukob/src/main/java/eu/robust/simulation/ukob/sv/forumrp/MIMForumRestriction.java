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
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.robust.simulation.dbconnection.Connectdb;
import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.UserManager;			

import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationTemplate;
import uk.ac.soton.itinnovation.prestoprime.iplatform.init.SimulationClock;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.SimulationModelSchema;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.interfaces.IPlatformSetup;

import eu.robust.simulation.policies.Policy;
import eu.robust.simulation.ukob.config.ModelConfiguration;



public class MIMForumRestriction extends SimulationModelSchema implements IPlatformSetup{

	//this is class used to configure the model
	private ModelConfiguration modelConfiguration = null;
	
	private List<Map<String,String>> userstats = null;
	private List<Map<String,String>> userstatsTs = null;
	private List<Map<String,String>> userstatsRs = null;
	
	private Policy policy=null;
	
	//simulation management objects
	private UserManager<User> managerUsers = null;
	private List<User> users = null;
	private CIManagerForums managerContent = null;
	
	public MIMForumRestriction()
	{
		super();
	}


	public void setSimulationClock(SimulationClock arg0) {
		// TODO Auto-generated method stub
		simulationClock = arg0;
		logger.info("successfully set simulation clock...");
	}


	public void setModelConfiguration(Object arg0) {
		modelConfiguration = (ModelConfiguration)arg0;
		logger.info("succcessfully loaded model configuration: "+modelConfiguration.templateName);
		
	}

	public void registerEventWatchers(SimulationClock arg0)
	{
		arg0.registerStartSimulationClockEvent(new EventWatcherStartSimulation(this,0));
		arg0.registerEndSimulationClockEvent(new EventWatcherEndSimulation(this,1));
		
		arg0.registerDailyClockEvent(new EventWatcherDaily(this,3));
		//arg0.registerTenMinuteClockEvent(new EventWatcherTenMinute(this,3));
		
		arg0.registerMonthlyClockEvent(new EventWatcherMonthly(this,4));
		
		logger.info("successfully loaded simulation events...");
	}
	
/*	public int getMaxThreadCount() {
		return  modelConfiguration.maxCount;
	}
*/
	public void initialiseManagers()
	{
		//set the reference to data export object that will correctly format the exported output
//		dataExport = new DataExport(this);
		// choose Policy
		policy = new SCNForumPolicyRP();
		SCNForumPolicyRP specPolicy = (SCNForumPolicyRP) policy;
		specPolicy.setMaxThreadCount(modelConfiguration.maxCount);
		
		managerUsers = new UserManager<User>();
		managerContent = new CIManagerForums();
		managerContent.setMaxThreadCount(modelConfiguration.maxCount);
		
		// create all initial users
		getUserParameters();
		users = createUsers();
		managerUsers.add(users);
	}
	
	private void getUserParameters() {
//		DBParameters dbpar = new DBParameters("sapDataKoblenz");
//		Connectdb db = new Connectdb(dbpar);
		Connectdb db = new Connectdb("jdbc:postgresql://localhost/postgres","postgres", "testdb");

		// general statistics
		logger.debug("gathering general statistics");
		String[] cols = {"contributor", "actduration", "questions", "replies", "points", "points0", "points1", "points2", "points3"};
		String query = "select * from userstats";
		userstats = db.getTable(cols, query);
		
		// forum thread statistics
		logger.debug("gathering forum specific thread statistics");
		String[] colsTs = {"contributor", "forumuri", "posts"};
		String queryTs = "select contributor, forumuri, count(*) as posts from questions group by contributor, forumuri";
		userstatsTs = db.getTable(colsTs, queryTs);
		
		// forum reply statistics
		logger.debug("gathering forum specific reply statistics");
		String[] colsRs = {"contributor", "forumuri", "posts"};
		String queryRs = "select contributor, forumuri, count(*) as posts from replies group by contributor, forumuri";
		userstatsRs = db.getTable(colsRs, queryRs);
		
		logger.debug(userstatsRs.size());
		db.disconnectdb();
	}
	
	public List<User> createUsers() {
		
		// create init users and content items
		
		// create init users
		int numberOfUsers = modelConfiguration.numberOfAgents;
		Random r = new Random();
		List<User> users = new LinkedList<User>();
		
		Map<Integer,User> userMap = new HashMap<Integer,User>();
		
		// greate all needed users
		for (int i=0; i<userstats.size()-1; i++) {
			//Map<String,String> userConf = userstats.get(r.nextInt(userstats.size()-1));
			Map<String,String> userConf = userstats.get(i);
			
			int contributor = Integer.parseInt(userConf.get("contributor"));
			int duration = Integer.parseInt(userConf.get("actduration"));
			int questions = Integer.parseInt(userConf.get("questions"));
			int replies = Integer.parseInt(userConf.get("replies"));
			int points = Integer.parseInt(userConf.get("points"));
			
			double avgThreadCreationRate = (double)questions/(double)duration/(double)1;
			double avgAnswerRate = (double)replies/(double)duration/(double)1;
			double avgAnswerQuality = (double)points/(double)replies;
			
			User u = new User(avgThreadCreationRate, avgAnswerRate, avgAnswerQuality);
			
			int[] pointStats = new int[4];
			pointStats[0]=Integer.parseInt(userConf.get("points0"));
			pointStats[1]=Integer.parseInt(userConf.get("points1"));
			pointStats[2]=Integer.parseInt(userConf.get("points2"));
			pointStats[3]=Integer.parseInt(userConf.get("points3"));
			u.setReplyStats(pointStats);
			u.setPolicy(policy);
			u.setAbsActivityThread(questions);
			u.setAbsActivityReply(replies);
			
			userMap.put(contributor,u);	

		}
		
		// add forum statistics
		for (Map<String,String> dataset : userstatsTs) {
			int contributor = Integer.parseInt(dataset.get("contributor"));
			Map<Integer,Integer> threadFrequency = new HashMap<Integer,Integer>();
			int sum=0;
			// parse db results
			if (userMap.containsKey(contributor)) {
				int threads = Integer.parseInt(dataset.get("posts"));
				String forumS = dataset.get("forumuri").substring(36);
				forumS =forumS.substring(0,forumS.indexOf("#"));
				//System.out.println(forumS);
				int forum = Integer.parseInt(forumS);
				threadFrequency.put(forum, threads);
				sum=sum+threads;
			}
			User u = userMap.get(contributor);
			for (int forum: threadFrequency.keySet()) {
				u.setForumActivityThread(forum, (double)( (double)threadFrequency.get(forum) / (double)sum ));
			}
		}
		
		// add forum statistics
		for (Map<String,String> dataset : userstatsRs) {
			int contributor = Integer.parseInt(dataset.get("contributor"));
			Map<Integer,Integer> threadFrequency = new HashMap<Integer,Integer>();
			int sum=0;
			// parse db results
			if (userMap.containsKey(contributor)) {
				int threads = Integer.parseInt(dataset.get("posts"));
				String forumS = dataset.get("forumuri").substring(36);
				forumS =forumS.substring(0,forumS.indexOf("#"));
				int forum = Integer.parseInt(forumS);
				threadFrequency.put(forum, threads);
				sum=sum+threads;
			}
			User u = userMap.get(contributor);
			for (int forum: threadFrequency.keySet()) {
				u.setForumActivityReply(forum, (double)( (double)threadFrequency.get(forum) / (double)sum ));
			}
		}
		
		for (User u : userMap.values()) {
			users.add(u);
			//System.out.println(u.forumActivityReply);
		}

		System.out.println("Created users: "+users.size());
		
		return users;
	}

	
	public SimulationClock getSimulationClock() {
		return simulationClock;
	}

	public ModelConfiguration getModelConfiguration() {
		return modelConfiguration;
	}

//	public DataExport getDataExport() {
//		return dataExport;
//	}


	public UserManager<User> getManagerUsers() {
		return managerUsers;
	}

	public List<User> getUsers() {
		return users;
	}
	
	public Policy getPolicy() {
		return policy;
	}

	public CIManager getManagerContent() {
		return managerContent;
	}
	
	public ToolConfigurationTemplate getToolConfiguration() {
		return toolConfigurationTemplate;
	}
	
	
}
