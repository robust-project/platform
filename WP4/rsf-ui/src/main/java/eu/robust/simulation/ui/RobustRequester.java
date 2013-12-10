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
package eu.robust.simulation.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import eu.robust.simulation.service.interfaces.RobustService;
import eu.robust.simulation.service.parameters.SimulationInput;
import eu.robust.simulation.service.parameters.SimulationResult;

public class RobustRequester {

	//private String simId;
	
	private ArrayList<SimulationTask> tasks;
	
	public RobustRequester(ArrayList<SimulationTask> tasks){
		
		this.tasks = tasks;
		
		//getRobustService();
		
	}
	
	
	public String getSimId(int id){
		
		return tasks.get(id).getSimId();
		
	}
	
	private RobustService getRobustService() {
		String url = "http://robustdb.west.uni-koblenz.de:8080/rsf-ukob-soap/simulationService";
		Service service = null;
		try {
			service = Service
					.create(new URL(url + "?wsdl"), new QName(
							"http://simulation.robust.eu/",
							"RobustServiceImplService"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RobustService robustService;
		robustService = service.getPort(RobustService.class);
		return robustService;
	}

	public void createNewSimulationRun(int id) {

		RobustService robustService = getRobustService();

		// This variable defines the type of simulation that is to be executed
		// these types are predefined and hard coded
		// String communityModel = "MIMListOrders";

		Map<String, String> input = tasks.get(id).getModel().getMap();
		
		for ( String elem : input.keySet() )
			  System.out.println( elem + ": " + input.get(elem) );

		SimulationInput parSet = new SimulationInput();

		String communityModel = input.get("communityModel");
		parSet.setCommunityModel(input.get("communityModel"));
		input.remove("communityModel");
		String subcommunityId = input.get("subCommunity");
		input.remove("subCommunity");
		input.put("subcommunityId", subcommunityId);
		
		//parSet.setCommunityParameters(getSampleMap());
		parSet.setCommunityParameters(input);
		
		input.remove("subcommunityId");
		input.put("subCommunity", subcommunityId);
		input.put("communityModel", communityModel);
		
		tasks.get(id).setSimId(robustService.startSimulation(parSet));
		System.out.println("simID:" + tasks.get(id).getSimId());
	}

	public boolean checkForSimulationFinish(int id) {

		RobustService robustService = getRobustService();

		boolean simHasFinishedResult;
		//simHasFinishedResult = robustService.simulationHasFinished("2012-09-19-10-52-51-Simplemodel");
		simHasFinishedResult = robustService.simulationHasFinished(tasks.get(id).getSimId());
		// A simple boolean value
		//System.out.println("Sim '"+tasks.get(id).getSimId()+"' has finished: " + simHasFinishedResult);
		
		return simHasFinishedResult;
	}

	/**
	 * When using the simulation id "2012-09-19-10-52-51-Simplemodel" you get a
	 * precomputed simulation result without the need for starting a simulation
	 */
	public void retrieveSimulationResult(int id) {

		RobustService robustService = getRobustService();

		SimulationResult simResultResult = new SimulationResult();
	    //simResultResult = robustService.getSimulationResult("2012-09-19-10-52-51-Simplemodel");
		simResultResult = robustService.getSimulationResult(tasks.get(id).getSimId());
		// The result is a Map<String,String> where each thread size is assigned
		// the number of threads that
		// have this size (e.g. 5 threads have size 9, then you get the
		// key-value pair 9->5, the same for all
		// other sizes)
		System.out.println("Sim result: " + simResultResult.getResult());
		
		tasks.get(id).setOutputData(simResultResult.getResult());

	}
	
	private Map<String, String> getSampleMap(){
		Map<String, String> parameters = new HashMap<String, String>();

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
		parameters.put("subcommunityId", "c76ebc42-5b21-41c9-99f4-7f05b926ff0c");

		// each policy alternative gets assigned a float value of the
		// probability of being applied by the
		// virtual community members.
		// The sum of these value has to be 1.0 (1.0 + 0 + 0 = 1.0) !!!
		parameters.put("PolicyLatestActivityCreationDate", "1.0");
		parameters.put("PolicyThreadCreationDate", "0");
		parameters.put("PolicyThreadSize", "0");
		
		return parameters;
	}


	public void stopSimulationRun(int tasksGetTabAt) {
		// TODO Auto-generated method stub
		
	}

}
