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
package uk.ac.soton.itinnovation.robust.catsim;

import java.util.Hashtable;
import java.util.List;

import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.UserManager;

import uk.ac.soton.itinnovation.prestoprime.iplatform.init.SimulationClock;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.SimulationModelSchema;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.interfaces.IPlatformSetup;
import uk.ac.soton.itinnovation.robust.catsim.bootstrap.BootstrapManager;
import uk.ac.soton.itinnovation.robust.catsim.config.ModelConfiguration;
import uk.ac.soton.itinnovation.robust.catsim.dataexport.DataExport;
import uk.ac.soton.itinnovation.robust.catsim.dataexport.ExportManager;
import uk.ac.soton.itinnovation.robust.catsim.entities.User;
import uk.ac.soton.itinnovation.robust.catsim.events.EventWatcherDaily;
import uk.ac.soton.itinnovation.robust.catsim.events.EventWatcherEndSimulation;
import uk.ac.soton.itinnovation.robust.catsim.events.EventWatcherHourly;
import uk.ac.soton.itinnovation.robust.catsim.events.EventWatcherMonthly;
import uk.ac.soton.itinnovation.robust.catsim.events.EventWatcherStartSimulation;

public class ModelInterface extends SimulationModelSchema implements IPlatformSetup{

	//this is class used to configure the model
	private ModelConfiguration modelConfiguration = null;
	private DataExport dataExport = null;
	
	//simulation management objects
	private BootstrapManager managerBootstrap = null;
	private UserManager<User> managerUsers = null;
	private List<User> users = null;
	private CIManager managerContent = null;
	
	public ModelInterface()
	{
		super();
	}

	@Override
	public void setSimulationClock(SimulationClock arg0) {
		// TODO Auto-generated method stub
		simulationClock = arg0;
		logger.info("successfully set simulation clock...");
	}

	@Override
	public void setModelConfiguration(Object arg0) {
		modelConfiguration = (ModelConfiguration)arg0;
		logger.info("succcessfully loaded model configuration: "+modelConfiguration.templateName);
	}

	@Override
	public void registerEventWatchers(SimulationClock arg0)
	{
		arg0.registerStartSimulationClockEvent(new EventWatcherStartSimulation(this,0));
		arg0.registerEndSimulationClockEvent(new EventWatcherEndSimulation(this,1));
		
		arg0.registerHourlyClockEvent(new EventWatcherHourly(this,2));
		arg0.registerDailyClockEvent(new EventWatcherDaily(this,3));
		arg0.registerMonthlyClockEvent(new EventWatcherMonthly(this,4));
		
		logger.info("successfully loaded simulation events...");
	}

	public void initialiseManagers()
	{
		//set the reference to data export object that will correctly format the exported output
		dataExport = new DataExport(this);
		managerBootstrap = new BootstrapManager(this);
		managerUsers = new UserManager<User>();
		managerContent = new CIManager();
		
		// create all initial users
		users = managerBootstrap.createUsers();
		managerUsers.add(users);
	}
	
	public SimulationClock getSimulationClock() {
		return simulationClock;
	}

	public ModelConfiguration getModelConfiguration() {
		return modelConfiguration;
	}

	public DataExport getDataExport() {
		return dataExport;
	}

	public BootstrapManager getManagerBootstrap() {
		return managerBootstrap;
	}

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
