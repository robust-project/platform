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
package eu.robust.simulation;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import eu.robust.simulation.configuration.Parameterset;
import eu.robust.simulation.configuration.ParametersetDBManager;
import eu.robust.simulation.configuration.SimulationresultDBManager;
import eu.robust.simulation.configuration.Simulationrun;
import eu.robust.simulation.configuration.SimulationrunManager;
import eu.robust.simulation.dbconnection.DBParameters;

import eu.robust.simulation.service.interfaces.RobustService;
import eu.robust.simulation.service.parameters.SimulationResult;
import eu.robust.simulation.service.parameters.SimulationInput;


@WebService(endpointInterface = "eu.robust.simulation.service.interfaces.RobustService")
public class RobustServiceImpl implements RobustService {
	
	Logger logger = Logger.getLogger(RobustServiceImpl.class);
	
	
	private Object traverseHashMap(Map<String,Object> inputMap, String[] path) {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(Arrays.asList(path));
		return traverseHashMap(inputMap, list);
	}
	
	
	
	private Object traverseHashMap(Map<String,Object> inputMap, List<String> path) {
		Iterator<String> i = path.iterator();
		Map<String,Object> branch=null;
		Object result = inputMap;;
		boolean exit = false;
		do {
			if (i.hasNext()) {
				branch = (Map<String,Object>) result;
				String currentKey = i.next();
				if (branch.containsKey(currentKey)) {
					result = branch.get(currentKey);
				} else {
					exit = true;
					result = null;
				}
			} else {
				exit = true;
			}
		} while (!exit);	
		return result;
	}
	

	
    @Override
    public String startSimulation( SimulationInput input ) {
    	
    	boolean success = false;
    	

    	SimulationrunManager simrunMan = new SimulationrunManager(new DBParameters("simManagementKoblenz"));
    	ParametersetDBManager parMan = new ParametersetDBManager(new DBParameters("simManagementKoblenz"));
    	
    	// creating the simuation run
    	Simulationrun simrun = new Simulationrun(); 
    	simrun.status = "created";
    	simrun.model = input.getCommunityModel();
    	
    	// defining the parameter set
    	Parameterset params = new Parameterset();
    	
    	params.id 			= simrun.id+"-par";
    	params.creationdate = simrun.creationdate;
    	params.description	= "Web Service Call";

    	params.parameters.put("communityModel", input.getCommunityModel());

    	for (String p : input.getCommunityParameters().keySet()) {
    		params.parameters.put(p, input.getCommunityParameters().get(p));
    	}
    	
    	// storing the parameter set
    	success = parMan.storeParameterset(params);
    	
    	// storing the simulation run
    	
    	simrun.parameterset = params.id;  
    	success &= simrunMan.storeSimulationRun(simrun);
    	
    	if (success) {
    		return simrun.id;
    	} else {
    		return "";
    	}
    	
    	

	}
    

    
    @Override
    public boolean simulationHasFinished(String input) {
    	
    	SimulationrunManager dbman = new SimulationrunManager(new DBParameters("simManagementKoblenz"));
    	Simulationrun run = dbman.getSimulationRunFromId(input);
    	
    	if (run.status.contains("finished")) {
    		return true;
    	} else {
    		return false;
    	}
    	
		
	}
    
    @Override
    public SimulationResult getSimulationResult(String input) {
    	
    	// example id: 2012-09-19-10-52-51-Simplemodel
    	SimulationresultDBManager dbman = new SimulationresultDBManager(new DBParameters("simManagementKoblenz"));
    	Map<String,String> simRes = dbman.getSimulationResultFromId(input);

		SimulationResult result = new SimulationResult();

		result.setResult(simRes);
		return result;

	}
    
    
    private Map<String,String> getTestValues() {
		Map<String,String> responseTimes = new HashMap<String, String>();
		responseTimes.put("56", "3.1");
		responseTimes.put("264", "3.4");
		responseTimes.put("50", "2.77");
		responseTimes.put("256", "3.15");
		responseTimes.put("44", "3.76");
		responseTimes.put("413", "4.4");
		responseTimes.put("419", "3.6");
		return responseTimes;
    }
     
    

}
