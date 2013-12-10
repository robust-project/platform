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
 
package eu.robust.simulation.ukob;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eu.robust.simulation.dbconnection.*;
import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.UserManager;			

import uk.ac.soton.itinnovation.prestoprime.iplatform.init.SimulationClock;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.SimulationModelSchema;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.interfaces.IPlatformSetup;
//import uk.ac.soton.itinnovation.robust.catsim.bootstrap.BootstrapManager;
//import uk.ac.soton.itinnovation.robust.catsim.config.ModelConfiguration;
//import uk.ac.soton.itinnovation.robust.catsim.dataexport.DataExport;
//import uk.ac.soton.itinnovation.robust.catsim.dataexport.ExportManager;
//import uk.ac.soton.itinnovation.robust.catsim.entities.User;

import eu.robust.simulation.policies.Policy;
import eu.robust.simulation.ukob.config.ModelConfiguration;
import eu.robust.simulation.ukob.entities.User;
import eu.robust.simulation.ukob.events.EventWatcherDaily;
import eu.robust.simulation.ukob.events.EventWatcherEndSimulation;
import eu.robust.simulation.ukob.events.EventWatcherStartSimulation;

public class ModelInterfaceInitModel extends SimulationModelSchema implements IPlatformSetup{

	//this is class used to configure the model
	private ModelConfiguration modelConfiguration = null;
	
	private List<Map<String,String>> userstats = null;
	
	private Policy policy=null;
	
	//simulation management objects
	private UserManager<User> managerUsers = null;
	private List<User> users = null;
	private CIManager managerContent = null;
	
	public ModelInterfaceInitModel()
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
		
		logger.info("successfully loaded simulation events...");
	}

	public void initialiseManagers()
	{
		//set the reference to data export object that will correctly format the exported output
//		dataExport = new DataExport(this);
		// choose Policy
		policy = new eu.robust.simulation.ukob.policies.SCNForumPolicy();
		managerUsers = new UserManager<User>();
		managerContent = new CIManager();
		
		// create all initial users
		getUserParameters();
		users = createUsers();
		managerUsers.add(users);
	}
	
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
		int numberOfUsers = modelConfiguration.numberOfAgents;
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
	
	public Map<String,String> drawUserConf(Random r) {
		return userstats.get(r.nextInt(userstats.size()-1));
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

	public CIManager getManagerContent() {
		return managerContent;
	}
	
	
}
