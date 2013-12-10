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


import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.robust.simulation.dbconnection.Connectdb;
import eu.robust.simulation.dbconnection.DBParameters;

public class SimulationrunManager {
	
	Logger  logger = Logger.getLogger(SimulationrunManager.class);
	
	private DBParameters dbparameters;
	private String[] columnNamesStandard = {"id","model","parameterset","creationdate","status","startdate","finishdate"};
	
	
	public SimulationrunManager(DBParameters parameters) {
		
		this.dbparameters = parameters;
		
	}

	public Simulationrun getSimulationRunFromId(String id) {
		
		Connectdb db = new Connectdb(dbparameters);
		
		Simulationrun result = getSimulationRunFromId(id, db);
		
		db.disconnectdb();
		
		return result;	
	}
	
	private Simulationrun getSimulationRunFromId(String id, Connectdb db) {
		

		
		String query = "SELECT * FROM simulationruns WHERE id='"+id+"'";
		
		logger.debug("Retrieving simulation run: "+ query);
		
		List<Map<String,String>> results = db.getTable(columnNamesStandard, query);
		if (results.size()>1) {
			logger.error("More than one Simulationrun with id: "+id);
			return null;
		}
		
		if (results.size()==0) return null;
		

		Map<String, String> theResult = results.get(0);
		
		
		Simulationrun srun = new Simulationrun();	
		srun.id 			= theResult.get("id");
		srun.creationdate 	= theResult.get("creationdate");
		srun.startdate		= theResult.get("startdate");
		srun.finishdate 	= theResult.get("finishdate");
		srun.model			= theResult.get("model");
		srun.parameterset	= theResult.get("parameterset");
		srun.status			= theResult.get("status");

		return srun;
		
	}
	
	
	
	public boolean storeSimulationRun(Simulationrun srun) {
		
		Connectdb db = new Connectdb(dbparameters);
		
		// check if dataset already exists
		String query = "SELECT count(*) NUM FROM simulationruns WHERE id='"+srun.id+"'";
		String[] columnNames = {"num"};
		List<Map<String,String>> results = db.getTable(columnNames, query);
		String resString = (String) results.get(0).get("num");
		int occurences = Integer.parseInt(resString);
		
		if (occurences>0) {
			logger.error("Simulation run already exists with id: "+srun.id);
			db.disconnectdb();
			return false;
		} else {
			//System.out.println(occurences);
			
			String[][] data = new String[1][7];
			data[0][0] = srun.id;
			data[0][1] = srun.model;
			data[0][2] = srun.parameterset;
			data[0][3] = srun.creationdate;
			data[0][4] = srun.status;
			data[0][5] = srun.startdate;
			data[0][6] = srun.finishdate;
			
			logger.debug("Creating simulation run in database with id :"+srun.id);
			db.writeStringsInTable(columnNamesStandard, data, "simulationruns");
				
			db.disconnectdb();
			return true;
		}

	}
	

	/**
	 * Fetches from the db a simulationrun that is still in the status 'created' (not processed so far) and complies to
	 * the model of modelName. If successfull the simulationrun is set to the status 'started' and given as result.
	 * @param modelName model name of the searched simulationrun
	 * @return the updated simulationrun
	 */
	public Simulationrun getNewRunAndUpdateStatusAsStarted(String modelName) {
		
		Simulationrun simrun = null;
		
		Connectdb db = new Connectdb(dbparameters);
		String query = "SELECT id FROM simulationruns WHERE model='"+modelName+"' AND status='created'";
		String[] columnNames = {"id"};
		List<Map<String,String>> results = db.getTable(columnNames, query);
		
		if (results.size()==0) {
			logger.info("No processable simulationrun found for Model: "+modelName);
		} else {
			String id = (String) results.get(0).get("id");
			this.updateStatusToStarted(id, db);
			simrun = this.getSimulationRunFromId(id, db);
		}
		
		db.disconnectdb();
		return simrun;
		
	}
	
	private boolean updateStatusToStarted(String id, Connectdb db) {
		

		String timestamp = Simulationrun.getTimestamp();
		String newStatusCombined = "started" +" ("+Simulationrun.getComputername()+")";
		String update = "UPDATE simulationruns SET status='"+newStatusCombined+"', startdate='"+timestamp+"' WHERE id='"+id+"'";
		
		logger.debug("Updating simulation run: "+ update);
		
		boolean result = db.executeUpdate(update);
		
		return result;
	}
	

	public boolean updateStatusToFinished(String id) {
		
		Connectdb db = new Connectdb(dbparameters);
		String timestamp = Simulationrun.getTimestamp();
		String newStatusCombined = "finished" +" ("+Simulationrun.getComputername()+")";
		String update = "UPDATE simulationruns SET status='"+newStatusCombined+"', finishdate='"+timestamp+"' WHERE id='"+id+"'";
		
		logger.debug("Updating simulation run: "+ update);
		
		boolean result = db.executeUpdate(update);
		db.disconnectdb();
		
		return result;
	}

	public boolean updateStatusToAborted(String id) {
		
		Connectdb db = new Connectdb(dbparameters);
		String timestamp = Simulationrun.getTimestamp();
		String newStatusCombined = "aborted" +" ("+Simulationrun.getComputername()+")";
		String update = "UPDATE simulationruns SET status='"+newStatusCombined+"', finishdate='"+timestamp+"' WHERE id='"+id+"'";
		
		logger.debug("Updating simulation run: "+ update);
		
		boolean result = db.executeUpdate(update);
		db.disconnectdb();
		
		return result;
	}
	

}
