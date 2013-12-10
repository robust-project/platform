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
import org.apache.log4j.Logger;



//this class is used to read initial model configuration from file (i would recommend using JSON as a readable configuration template that would deserialise the file into java object of type Configuration)
public class ConfigurationManager {
	
	protected Logger logger = Logger.getLogger(ConfigurationManager.class);

	@SuppressWarnings("unused")
	private String configurationFile = "";
	protected Configuration configuration = null;
	
	//setup the location of configuration file
	public ConfigurationManager(String configurationFile)
	{
		this.configurationFile = configurationFile;
		//get configuration in java object
		this.configuration = getConfiguration(); 
	}
	
	private Configuration getConfiguration()
	{
		//mockup - should recreate configuration from file template 
		return new Configuration();
	}
	

	
	public int getSimulationTicksNumber()
	{
		//read this from configuration object
		return configuration.getSimulationTicks();
	}
}
