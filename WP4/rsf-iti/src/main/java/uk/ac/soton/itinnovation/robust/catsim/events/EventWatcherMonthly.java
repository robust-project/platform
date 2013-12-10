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
package uk.ac.soton.itinnovation.robust.catsim.events;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import uk.ac.soton.itinnovation.prestoprime.iplatform.batch.ModelRunner;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.interfaces.SimulationClockEvent;
import uk.ac.soton.itinnovation.robust.catsim.ModelInterface;
import uk.ac.soton.itinnovation.robust.catsim.config.ModelConfiguration;
import uk.ac.soton.itinnovation.robust.catsim.dataexport.DataExport;

public class EventWatcherMonthly implements SimulationClockEvent{

	static Logger logger = Logger.getLogger(ModelRunner.class);
	private ModelInterface modelInterface = null;
	private int id = 0;

	//this is class used to configure the model
	private ModelConfiguration modelConfiguration = null;
	
	public EventWatcherMonthly(ModelInterface modelInterfaceRef, int idRef)
	{
		this.id = idRef;
		this.modelInterface = modelInterfaceRef;
		this.modelConfiguration = modelInterface.getModelConfiguration();
	}
	
	@Override
	public void triggerEvent() {
		try
		{
			logger.info("-------> monthly event triggered by simplatform clock..."+modelConfiguration.numberOfAgents);
			//TODO this generates data output from the model that is logged in files
			Hashtable outputTable = new Hashtable();
			modelInterface.getDataExport().exportHourlyOutput(outputTable);
		}
		catch(Exception exc)
		{
			logger.error("Error: ", exc);
			exc.printStackTrace();
		}
	}

	@Override
	public int getEventId() {
		// TODO Auto-generated method stub
		return id;
	}

}
