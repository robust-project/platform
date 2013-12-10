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
 * Creation Time    	: 05.03.2012
 *
 * Created for Project  : ROBUST
 */
 
package eu.robust.simulation.ukob.test;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import org.junit.Test;

import eu.robust.simulation.ukob.config.ModelConfiguration;
import eu.robust.simulation.ukob.sv.forumrp.SimulationResult;
import eu.robust.simulation.ukob.sv.forumrp.startSim;

import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationTemplate;

import uk.ac.soton.itinnovation.prestoprime.iplatform.service.ServiceRunner;




public class TestService {
	
	Logger logger = Logger.getLogger(TestService.class);

	/**
	 * @param args
	 */
	
	@Test
	public void TestSim() {
		
		// startSim ob = new startSim();
		// ob.runSim();
		
/*		//this class represents a 'service wrapper' that issues tasks to simplatform directly from java code
		ServiceRunner sr = new ServiceRunner();
		
		//prepar tool configuration settings
		ToolConfigurationTemplate toolConfigurationTemplate = new ToolConfigurationTemplate();
		toolConfigurationTemplate.setBatchFolderLocation("bin"+File.separator+"batch");
		toolConfigurationTemplate.setResultsFolderLocation("bin"+File.separator+"results");
		//this is important as it indicates we run simplatform in service mode!
		toolConfigurationTemplate.setOperationMode(3);
		toolConfigurationTemplate.setShowGUI(false);
		//define class name of the model and its configuration that will be handled by simplatform
		toolConfigurationTemplate.setModelConfigurationClassToInstantiate("eu.robust.simulation.ukob.config.ModelConfiguration");
		toolConfigurationTemplate.setModelClassToInstantiate("eu.robust.simulation.ukob.sv.forumrp.MIMForumRestriction");
		//define class name of the model simulation output object that will be handled by simplatform
		toolConfigurationTemplate.setSimulationResultsClassToInstantiate("eu.robust.simulation.ukob.sv.forumrp.SimulationResult");
		//name of the file where simulation results would be temporarily held
		toolConfigurationTemplate.setSimulationResultsFileName("simulationGeneratedResults.txt");

		
		//here we initialise our custom class that holds model configuration
		ModelConfiguration modelConfigurationTemplate = new ModelConfiguration();
		modelConfigurationTemplate.startHour = 8;
		modelConfigurationTemplate.startDay = 1;
		modelConfigurationTemplate.startMonth= 1;
		modelConfigurationTemplate.startYear = 2012;
		modelConfigurationTemplate.endHour = 8;
		modelConfigurationTemplate.endDay = 10;
		modelConfigurationTemplate.endMonth= 1;
		modelConfigurationTemplate.endYear = 2012;
		//make sure template name has not spaces!
		modelConfigurationTemplate.templateName = "test1-[cr0.0]";
		
		modelConfigurationTemplate.numberOfSimulationRepetitions = 1;

		//========================== job submission to service ==============================
		//returns job reference based on which we can track its progress
		String jobRefId = sr.createSimulationJob(toolConfigurationTemplate, modelConfigurationTemplate);
		logger.info("got job refID : "+jobRefId);

		//========================== job execution on service ==============================
		//here we start the simulation
		sr.runSimulation();

		//========================== job status check to verify if it has finished ==============================
		//monitor if simulation finished by verifyhing if job ref id is creataed (folder) with results in results folder location
		//this monitoring of job execution should be perofrmed in iterative manner until the job is completed (eg., every 5 minutes)
		boolean isJobCompleted = sr.isJobCompleted(jobRefId, toolConfigurationTemplate);
		logger.info("job with id: "+jobRefId+" completed: "+isJobCompleted);

		//========================== job execution results retrieval ==============================
		//if finished - read results and return to user
		SimulationResult simulationResults = (SimulationResult)sr.getSimulationResults(jobRefId, toolConfigurationTemplate);
		//logger.info("received simulation result for job with jobRefId: "+simulationResult.getJobRefId()+" result: "+simulationResults.getProbability());
*/	}

}
