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
 * Creation Time    	: 16.01.2012
 *
 * Created for Project  : ROBUST
 */ 

package eu.robust.simulation.configuration;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.junit.Test;

import eu.robust.simulation.dbconnection.DBParameters;

public class SimulationrunManagerTest {


	
			
	
//	@Test
	public void testSimulationrunManagerGet() {
		
		DBParameters par = new DBParameters("simManagementKoblenz");
		
		SimulationrunManager srm = new SimulationrunManager(par);
		
		Simulationrun s = srm.getSimulationRunFromId("1");

		assertTrue(s.id == "1");
	}
	
//	@Test
	public void testSimulationrunManagerStore() {
		
		DBParameters par = new DBParameters("simManagementKoblenz");
		
		SimulationrunManager srm = new SimulationrunManager(par);
		
		Simulationrun srun = new Simulationrun();
		srun.id 			= "5";
		srun.creationdate 	= "2012-09-11";
		srun.finishdate 	= "2012-09-11";
		srun.model			= "MIMListOrders";
		srun.parameterset	= "tbd";
		srun.startdate		= "2012-09-11";
		srun.status			= "created";
		
		srm.storeSimulationRun(srun);

		
		assertTrue(false);
	}
	
//	@Test
	public void testSimulationrunManagerUpateStatusToStarted() {
		
		DBParameters par = new DBParameters("simManagementKoblenz");
		
		SimulationrunManager srm = new SimulationrunManager(par);
		
		boolean result = srm.updateStatusToFinished("5");
		
		assertTrue(result);
	}
	
	


	
}
