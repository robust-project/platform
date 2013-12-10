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
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import eu.robust.simulation.dbconnection.Connectdb;
import eu.robust.simulation.dbconnection.DBParameters;

public class SimulationresultDBManagerTest {


	

	
	
//	@Test
	public void testSimulationresultDBManagerGet() {
		
		DBParameters par = new DBParameters("simManagementKoblenz");
		
		SimulationresultDBManager srm = new SimulationresultDBManager(par);
		
		Map<String, String> s = srm.getSimulationResultFromId("2012-09-19-10-52-51-Simplemodel");


		assertTrue(s.size()>0);
	}
	
//	@Test
	public void testWriteSimulationResults() {
		
		DBParameters par = new DBParameters("simManagementKoblenz");
		
		SimulationresultDBManager srm = new SimulationresultDBManager(par);
		
		Map<String,String> testresults = new HashMap<String,String>();
		
		testresults.put("forum1","10");
		testresults.put("forum2","12");
		
		assertTrue(srm.writeSimulationResults("TestId1", testresults));
		

	}
	
}
