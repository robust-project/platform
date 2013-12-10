/*
 * Copyright 2012 University of Southampton IT Innovation Centre 
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
 * Created by       	: Mariusz Jacyno
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */ 
package uk.ac.soton.itinnovation.robust.catsim.dataexport;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationManager;
import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationTemplate;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.config.ModelConfigurationManager;
import uk.ac.soton.itinnovation.robust.catsim.ModelInterface;

public class ExportManager 
{
	static Logger logger = Logger.getLogger(ExportManager.class);

	private ModelInterface modelInterface = null;
	private ToolConfigurationTemplate toolConfiguration = null;
	private String resultsDirectory = "results";
	
	public ExportManager(ModelInterface modelInterfaceRef)
	{
		this.modelInterface = modelInterfaceRef;

		logger.info("export manager initialised...");
    	toolConfiguration = ToolConfigurationManager.getManager().readInitialToolConfiguration("configuration.txt");
    	resultsDirectory = toolConfiguration.getResultsFolderLocation();
	}
	
    public void exportData()
    {
    	try
    	{
    		//check if we need to create results dir
    		createResultsDir();
    		
    		//create directory where export files are moved after the simulation run
    		createDataExporDirForSimulationRun();
    	}
    	catch(Exception exc)
    	{
    		exc.printStackTrace();
    		System.exit(0);
    	}
    }
    
    public void createResultsDir()
    {
    	File file = new File(resultsDirectory);
    	if(!file.exists())
    	{
        	logger.info("creating dir: "+"results");
        	new File(resultsDirectory).mkdir();
    	}
    }
    
    private void createDataExporDirForSimulationRun() throws Exception
    {
    	String date = getCurrentDate();
    	
       	String exportPath = "";
    	if(toolConfiguration.getOperationMode() == 0)
    	{
    		exportPath = resultsDirectory+File.separator+date;
        	logger.info("creating export directory: "+exportPath);
        	new File(exportPath).mkdir();
    	}
    	else if(toolConfiguration.getOperationMode() == 1)
    	{
    		exportPath = resultsDirectory+File.separator+date+"("+getBatchConfigurationFileName()+")";
        	logger.info("creating export directory for batch mode: "+exportPath);
        	new File(exportPath).mkdir();
    	}
 
    	//copy log files to the dir
    	logger.info("copying file: outputModelLogs.log to following location (in bin directory: " +exportPath+File.separator+"outputModelLogs.log");
    	FileCopy.copy("outputModelLogs.log", exportPath+File.separator+"outputModelLogs.log");

    	logger.info("copying file: outputSystemEvents.log to following location (in bin directory: " +exportPath+File.separator+"outputSystemEvents.log");
    	FileCopy.copy("outputSystemEvents.log", exportPath+File.separator+"outputSystemEvents.log");
    	
    	logger.info("copying file: outputSystemPerformance.log to following location (in bin directory: " +exportPath+File.separator+"outputSystemPerformance.log");
    	FileCopy.copy("outputSystemPerformance.log", exportPath+File.separator+"outputSystemPerformance.log");

    	logger.info("copying file: outputUserEvents.log to following location (in bin directory: " +exportPath+File.separator+"outputUserEvents.log");
    	FileCopy.copy("outputUserEvents.log", exportPath+File.separator+"outputUserEvents.log");

    	if(toolConfiguration.getOperationMode() == 1)
    	{
        	logger.info("copying file: "+toolConfiguration.getBatchFolderLocation()+File.separator+getBatchConfigurationFileName()+" to following location (in bin directory: " +exportPath+File.separator+getBatchConfigurationFileName());
        	FileCopy.copy(toolConfiguration.getBatchFolderLocation()+File.separator+getBatchConfigurationFileName(), exportPath+File.separator+getBatchConfigurationFileName());
    	}
    }
    
    private String getBatchConfigurationFileName()
    {
    	String fileName = modelInterface.getModelConfiguration().templateName;
    	return fileName;
    }
    private String getCurrentDate()
    {
    	Date dateNow = new Date();
    	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    	StringBuilder dateString = new StringBuilder(dateformat.format(dateNow));

        return dateString.toString();
    }
}
