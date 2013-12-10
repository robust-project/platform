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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.User;
import eu.robust.simulation.entities.UserManager;
import eu.robust.simulation.policies.Policy;
import eu.robust.simulation.ukob.configuration.ConfigurationManager;


public class RoSim {
	
	static Logger logger = Logger.getLogger(RoSim.class);
	
	/**
	 * The main method of the Robust Simulation Framework
	 * @param args
	 */
	

	
	private static void doUkobSimulation(String[] args) {
		
		long starttime = new Date().getTime();
		
		// get Configuration
		ConfigurationManager configurationManager = new eu.robust.simulation.ukob.configuration.ConfigurationManager("");
		
		// choose Policy
		Policy policy = new eu.robust.simulation.ukob.policies.SCNForumPolicy();
		configurationManager.setPolicy(policy);
		
		UserManager<eu.robust.simulation.ukob.entities.User> um = new UserManager<eu.robust.simulation.ukob.entities.User>();
		CIManager cm = new CIManager();
		
		// create all initial users
		List<eu.robust.simulation.ukob.entities.User> initUsers = configurationManager.createUsers();
		um.add(initUsers);
	
		// do simulation
		int ticks=configurationManager.getSimulationTicksNumber();
		for(int t=0; t<ticks; t++) {
			cm.setCurrentTick(t);
			// let every user act on content items (cm)
			for(User u: um.getUsers()) {
				u.act(cm);
			}
			logger.debug("After Tick: "+t+" Threads:"+cm.getSize() + " Replies "+cm.getExtCount() );
			
		}
		
		//writeOutFinalState();
		long endtime = new Date().getTime();
		logger.debug("Simulation Time in Seconds: "+(double)(endtime-starttime)/1000);

		
	
	}
	 
	
	public static void main(String[] args) 
	{


		doUkobSimulation(args);


	}

}
