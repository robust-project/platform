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

import eu.robust.simulation.dbconnection.DBParameters;

public class ParametersetDBManagerTest {


	

	
//	@Test
	public void testParmetersetStore() {
		
		  Parameterset parset = new Parameterset();
		
		  String communityModel = "MIMListOrders";
		  
		  Map<String, String> parameters = parset.parameters;
		  
		  
		  // the time period that is going to be simulated
		  parameters.put("startHour", "8");
		  parameters.put("startDay", "1");
		  parameters.put("startMonth", "12");
		  parameters.put("startYear", "2009");
		  parameters.put("endHour", "8");
		  parameters.put("endDay", "1");
		  parameters.put("endMonth", "12");
		  parameters.put("endYear", "2010");
		  
		  // what community is to be simulated
		  parameters.put("communityData", "IBM");
		  
		  // the subcommunity that is to be simulated is still to be defined
		  parameters.put("subcommunityId", "to be defined");
		  
		  // each policy alternative gets assigned a float value of the probability of being applied by the 
		  // virtual community members.
		  // The sum of these value has to be 1.0 (1.0 + 0 + 0 = 1.0) !!!
		  parameters.put("PolicyLatestActivityCreationDate", "1.0");
		  parameters.put("PolicyThreadCreationDate", "0");
		  parameters.put("PolicyThreadSize", "0");
		  
		  parset.creationdate = "now";
		  parset.id = "test";
		  
		  ParametersetDBManager parM = new ParametersetDBManager(new DBParameters("simManagementKoblenz"));
		  
		  assertTrue(parM.storeParameterset(parset));
		  
	  
		  }
	
//	@Test
	public void testParametersetGet() {
		
		ParametersetDBManager padb = new ParametersetDBManager(new DBParameters("simManagementKoblenz"));
		Parameterset params = padb.getParameterset("2012-11-26-16-10-09-robustdb-par");
		System.out.println(params.parameters);
		
	}

	
	


	
}
