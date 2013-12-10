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
 
 package eu.robust.simulation.ukob.config;

import uk.ac.soton.itinnovation.prestoprime.iplatform.model.config.ModelConfigurationTemplate;

//this class should be used as a configuration template fetched to the model
//it should contain all the parameters used by the model
public class ModelConfiguration  extends ModelConfigurationTemplate
{
	//public String templateName = "Default Model Configuration Template1";

/*	public boolean useCurrentTimeAsStartDate = false;
	//start simulation date
	public int startYear = 2011;
	public int startMonth = 7;
	public int startDay = 1;
	public int startHour = 8;
	
	public int endYear = 2011;
	public int endMonth = 9;
	public int endDay = 1;
	public int endHour = 8;
	

	public int numberOfForums = 300;*/
	
	public int numberOfAgents = 200;
	public int numberOfForums = 300;
	public int maxCount = 5;
	
	public ModelConfiguration() {
		
		super();
		
		templateName = "Default Model Configuration Template1";
		
		useCurrentTimeAsStartDate = false;
		startYear = 2011;
		startMonth = 7;
		startDay = 1;
		startHour = 8;
		
		endYear = 2011;
		endMonth = 9;
		endDay = 1;
		endHour = 8;

		numberOfSimulationRepetitions = 1;
		
	}
	
}
