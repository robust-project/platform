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


package eu.robust.simulation.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.robust.simulation.dbconnection.Connectdb;
import eu.robust.simulation.dbconnection.DBParameters;

public class SimulationresultDBManager {
	
	Logger  logger = Logger.getLogger(SimulationresultDBManager.class);
		
		
	private DBParameters dbparameters;
	private String[] columnNames = {"simulationrun","parameter","parametervalue"};
	
	
	public SimulationresultDBManager(DBParameters parameters) {
		
		this.dbparameters = parameters;
		
	}

	public Map<String, String> getSimulationResultFromId(String id) {
		
		
		Map<String, String> simulationResult = null;
		Connectdb db = new Connectdb(dbparameters);
		
		
		String query = "SELECT * FROM simulationresults WHERE simulationrun='"+id+"'";
		
		logger.debug("Retrieving simulation results: "+ query);
		

		List<Map<String,String>> results = db.getTable(columnNames, query);
		
		if (results.size()>0) {
		
			simulationResult = new HashMap<String, String>();
			for (Map<String, String> entry : results) {
				String parameter = entry.get("parameter");
				String value = entry.get("parametervalue");
				simulationResult.put(parameter, value);
			}
		}
		
		db.disconnectdb();
			
		return simulationResult;
		
		
	}
	
	
	public boolean writeSimulationResults(String id, Map<String,String> results) {
		
		Connectdb db = new Connectdb(dbparameters);
		
		String[][] data = new String[results.size()][3];

		
		int i = 0;
		for (String parameter : results.keySet()) {
			data[i][0] = id;
			data[i][1] = parameter;
			data[i][2] = results.get(parameter);
			i++;
		}

		boolean result = db.writeStringsInTable(columnNames, data, "simulationresults");
		db.disconnectdb();
		
		return result;
		
	}
	
	
	

}
