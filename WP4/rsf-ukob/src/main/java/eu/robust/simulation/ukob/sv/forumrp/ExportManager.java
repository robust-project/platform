package eu.robust.simulation.ukob.sv.forumrp;

//########################################################################
//#
//# © University of Southampton IT Innovation Centre, 2011 
//# Copyright in this library belongs to the University of Southampton 
//# University Road, Highfield, Southampton, UK, SO17 1BJ 
//# This software may not be used, sold, licensed, transferred, copied 
//# or reproduced in whole or in part in any manner or form or in or 
//# on any media by any person other than in accordance with the terms 
//# of the Licence Agreement supplied with the software, or otherwise 
//# without the prior written consent of the copyright owners.
//#
//# This software is distributed WITHOUT ANY WARRANTY, without even the 
//# implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, 
//# except where stated in the Licence Agreement supplied with the software.
//#
//#	Created By :			Mariusz Jacyno
//#	Created Date :			2011-01-05
//#	Created for Project :	PrestoPRIME
//#
//########################################################################


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationManager;
import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationTemplate;



public class ExportManager 
{
	static Logger logger = Logger.getLogger(ExportManager.class);

	private MIMForumRestriction modelInterface = null;
	private ToolConfigurationTemplate toolConfiguration = null;
	private String resultsDirectory = "results";
	
	public ExportManager(MIMForumRestriction modelInterfaceRef)
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
