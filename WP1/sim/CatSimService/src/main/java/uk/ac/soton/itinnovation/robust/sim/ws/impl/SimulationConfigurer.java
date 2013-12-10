/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Vegard Engen
//      Created Date :          2013-10-28
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.sim.ws.impl;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationTemplate;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.config.ModelConfigurationManager;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.config.ModelConfigurationTemplate;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.catsim.config.ModelConfiguration;

/**
 * 
 * @author Vegard Engen
 */
public class SimulationConfigurer
{
	private SimulationServiceJobConfig config;
	private ToolConfigurationTemplate toolConfig;
	private ModelConfiguration modelConfig;
	private File bootstrapDataDir;
	private String jobRef;
	
    private boolean isUserDrop, isTRTprob, isCommunityActivityProb;
	private double userDrop, trtThreshold, communityActivityThreshold;
	private String userID;
	private int numRuns;
	
	private static Logger logger = Logger.getLogger(SimulationConfigurer.class);
	
	public SimulationConfigurer() 
	{
		this.isTRTprob = false;
		this.isCommunityActivityProb = false;
		this.isUserDrop = false;
		this.numRuns = 30;
	}
	
	/**
	 * Initialise method, which will set the tool config, model config and bootstrap
	 * data directories according to the properties and simulation service job config.
	 * @param props General configuration properties
	 * @param config Simulation configuration job object
	 * @param jobRef Job reference
	 * @throws Exception 
	 */
	public void initialise(Properties props, SimulationServiceJobConfig config, String jobRef) throws Exception
	{
		this.config = config;
		this.jobRef = jobRef;
		
		String communityID = config.getCommunityDetails().getCommunityID();
		Date simulationStartDate = config.getStartDate();
		Date simulationEndDate = config.getEndDate();
		
		logger.info("Setting variables for simulation run");
		// getting model bootstrap date and number of simulation days
		Date bootstrapStartDate = DateUtil.getFromDate(simulationEndDate, FrequencyType.WEEKLY);
		int numSimulationDays = DateUtil.getNumDaysBetweenDates(simulationEndDate, simulationStartDate);
		
		// getting directories for tool config
		File forumDataDir = getForumDataDir(props, communityID); // data/forum/
		bootstrapDataDir = new File (forumDataDir.getAbsolutePath() + File.separator + DateUtil.getDateString(bootstrapStartDate)); // data/forum/snapshot/
		// getting result directory; based on the root results directory in props + job reference
		File resultsDir = getResultsDir(props, jobRef);
		
		logger.info("Getting general tool and model configurations");
		toolConfig = getToolConfiguration(forumDataDir, resultsDir);
		modelConfig = getModelConfiguration(props, toolConfig, simulationStartDate, numSimulationDays, communityID);
		
		// set config flags
		setConfigFlags(config);
		
		modelConfig.setNumberOfSimulationRepetitions(this.numRuns);
		
		if (isIsUserDrop())
		{
			setUserDropConfig(config);
			
			modelConfig.dropUserActivity = true;
			modelConfig.userToDropActivityOf = getUserID();
			modelConfig.userActivityDrop = getUserDrop();
		
			logger.debug("Will simulate user drop");
			logger.debug(" - Username: " + this.getUserID());
			logger.debug(" - Drop %:   " + this.getUserDrop());
		}
		
		if (isIsTRTprob())
		{
			setTRTConfig(config);
			logger.debug("Will calculate P(TRT_incr >= " + this.getTrtThreshold() + ")");
		}
		
		if (isIsCommunityActivityProb())
		{
			setCommunityDropConfig(config);
			logger.debug("Will calculate P(community_activity_drop >= " + this.getCommunityActivityThreshold() + ")");
		}
		
		// check if simulate user drop
		logger.info("Setting user drop parameters");
		checkAndSetUserDropConfig(config, modelConfig);
		
		logToolConfig(toolConfig);
		logModelConfig(modelConfig);
	}
	
	/**
	 * Get the tool configuration - the initialise method must have been called
	 * first to set this.
	 * @return
	 * @throws Exception 
	 */
	public ToolConfigurationTemplate getToolConfig() throws Exception
	{
		return this.toolConfig;
	}
	
	/**
	 * Get the model configuration - the initialise method must have been called
	 * first to set this.
	 * @return
	 * @throws Exception 
	 */
	public ModelConfiguration getModelConfig() throws Exception
	{
		return this.modelConfig;
	}
	
	/**
	 * Get the bootstrap data directory - the initialise method must have been
	 * called first to set this.
	 * @return
	 * @throws Exception 
	 */
	public File getBootstrapDataDir() throws Exception
	{
		return this.bootstrapDataDir;
	}
	
	private void setConfigFlags(SimulationServiceJobConfig config)
	{
		// if simulate user drop
		for (Parameter param : config.getConfigurationParameters())
		{
			if (param.getName().trim().equalsIgnoreCase("Simulate user activity drop"))
			{
				if (param.getValue().getValue().equalsIgnoreCase("true"))
				{
					this.isUserDrop = true;
				}
			}
			else if (param.getName().trim().equalsIgnoreCase("Calculate P(TRT_drop >= threshold)"))
			{
				if (param.getValue().getValue().equalsIgnoreCase("true"))
				{
					this.isTRTprob = true;
				}
			}
			else if (param.getName().trim().equalsIgnoreCase("Calculate P(community_activity_drop >= threshold)"))
			{
				if (param.getValue().getValue().equalsIgnoreCase("true"))
				{
					this.isCommunityActivityProb = true;
				}
			}
			else if (param.getName().trim().equalsIgnoreCase("Number of runs"))
			{
				try {
					this.numRuns = Integer.parseInt(param.getValue().getValue());
				} catch (Exception ex) {
					logger.warn("Unable to parse number of runs parameter value to an int - will use default value: " + ex);
				}
			}
		}
	}
	
	private void setUserDropConfig(SimulationServiceJobConfig config) throws Exception
	{
		for (Parameter param : config.getConfigurationParameters())
		{
			if (param.getName().trim().equalsIgnoreCase("User ID"))
			{
				this.userID = param.getValue().getValue();
			}
			else if (param.getName().trim().equalsIgnoreCase("User activity drop"))
			{
				this.userDrop = Double.parseDouble(param.getValue().getValue());
			}
		}
	}
	
	private void setTRTConfig(SimulationServiceJobConfig config) throws Exception
	{
		for (Parameter param : config.getConfigurationParameters())
		{
			if (param.getName().trim().equalsIgnoreCase("TRT drop threshold"))
			{
				this.trtThreshold = Double.parseDouble(param.getValue().getValue());
				break;
			}
		}
	}
	
	private void setCommunityDropConfig(SimulationServiceJobConfig config) throws Exception
	{
		for (Parameter param : config.getConfigurationParameters())
		{
			if (param.getName().trim().equalsIgnoreCase("Community activity drop threshold"))
			{
				this.communityActivityThreshold = Double.parseDouble(param.getValue().getValue());
				break;
			}
		}
	}
	
	private void checkAndSetUserDropConfig(SimulationServiceJobConfig config, ModelConfiguration modelConfiguration)
	{
		if ((config.getConfigurationParameters() == null) || config.getConfigurationParameters().isEmpty()) {
			return;
		}
		
		boolean simulateActivityDrop = false;
		for (Parameter p: config.getConfigurationParameters())
		{
			if (p.getName().equalsIgnoreCase("Simulate user activity drop"))
			{
				try {
					simulateActivityDrop = Boolean.parseBoolean(p.getValue().getValue());
				} catch (Exception ex) {
					logger.warn("Unable to convert the simulate user activity drop parameter value to a boolean...", ex);
				}
				break;
			}
		}
		
		
		if (!simulateActivityDrop)
		{
			modelConfiguration.dropUserActivity = false;
			this.isUserDrop = false;
			return;
		}
		
		// get the user ID and drop if simulating activity drop..
		String userID = null;
		Double userDrop = null;
		for (Parameter p: config.getConfigurationParameters())
		{
			try 
			{
				if (p.getName().trim().equalsIgnoreCase("User ID"))
				{
					userID = p.getValue().getValue();
				}
				else if (p.getName().trim().equalsIgnoreCase("User activity drop"))
				{
					userDrop = Double.parseDouble(p.getValue().getValue());
				}
			} catch (Exception ex) {
				logger.warn("Unable to get User ID or activity drop: " + ex, ex);
			}
		}
		
		if ((userID == null) || (userDrop == null)) {
			throw new RuntimeException("Unable to get User ID or activity drop from config parameters");
		}
		
		modelConfiguration.dropUserActivity = true;
		modelConfiguration.userToDropActivityOf = userID;
		modelConfiguration.userActivityDrop = userDrop;
		
		this.isUserDrop = true;
		this.userID = userID;
		this.userDrop = userDrop;
	}
	
	private ModelConfiguration getModelConfiguration(Properties props, ToolConfigurationTemplate toolConfiguration, Date simulationStartDate, int numSimulationDays, String communityID) throws Exception
	{
		File modelConfigFile = getModelConfigFile(props);
		ModelConfiguration modelConfiguration = (ModelConfiguration)getModelConfigurationTemplate(toolConfiguration, modelConfigFile);
		
		modelConfiguration.simulationStartDate = DateUtil.getDateString(simulationStartDate);
		modelConfiguration.setNumberOfSimulationDays(numSimulationDays);
		modelConfiguration.forumID = communityID;
		
		return modelConfiguration;
	}
	
	private File getForumDataDir(Properties props, String communityID) throws Exception
	{
		if(props.getProperty("communityDataRootDir") == null) {
			throw new RuntimeException("communityDataRootDir not set in the config file");
		}
		
		File f = new File(props.getProperty("communityDataRootDir"));
		
		if (!f.exists()) {
			throw new RuntimeException("Root community data directory does not exist: " + f.getAbsolutePath());
		}
		
		String[] split1 = communityID.split("/");
		String forumID = split1[split1.length-1].split("#")[0];
		
		f = new File(f.getAbsoluteFile() + File.separator + "forum_" + forumID);
		if (!f.exists()) {
			throw new RuntimeException("Forum community data directory does not exist: " + f.getAbsolutePath());
		}
		
		return f;
	}
	
	private File getResultsDir(Properties props, String jobRef) throws Exception
	{
		if(props.getProperty("outputDataRootDir") == null) {
			throw new RuntimeException("outputDataRootDir not set in the config file");
		}
		
		File f = new File(props.getProperty("outputDataRootDir"));
		
		if (!f.exists()) {
			throw new RuntimeException("Root output data directory does not exist: " + f.getAbsolutePath());
		}
		
		f = new File(f.getAbsoluteFile() + File.separator + jobRef);
		try {
			f.mkdirs();
		} catch (Exception ex) {
			throw new RuntimeException("Unable to create results directory: " + f.getAbsolutePath(), ex);
		}
		
		return f;
	}
	
	private ToolConfigurationTemplate getToolConfiguration(File forumDataDir, File resultsDir)
	{
		ToolConfigurationTemplate toolConfiguration = new ToolConfigurationTemplate();
		toolConfiguration.setOperationMode(0); // single model
		toolConfiguration.setShowGUI(false);
		toolConfiguration.setModelConfigurationClassToInstantiate("uk.ac.soton.itinnovation.robust.catsim.config.ModelConfiguration");
		
		toolConfiguration.setBootstrapDataFolderLocation(forumDataDir.getAbsolutePath());
		toolConfiguration.setResultsFolderLocation(resultsDir.getAbsolutePath());
		
		return toolConfiguration;
	}
	
	private File getModelConfigFile(Properties props) throws Exception
	{
        if(props.getProperty("modelConfigFile") == null) {
			throw new RuntimeException("modelConfigFile not set in the config file");
		}
		
		File modelConfigFile = new File(props.getProperty("modelConfigFile"));
		if (!modelConfigFile.exists()) {
			throw new RuntimeException("The model config file given does not exist: " + modelConfigFile.getAbsolutePath());
		}
		
		return modelConfigFile;
	}
	
	private ModelConfigurationTemplate getModelConfigurationTemplate(ToolConfigurationTemplate toolConfiguration, File modelConfigurationFile) throws Exception
	{
		ModelConfigurationTemplate modelConfiguration = (ModelConfigurationTemplate) ModelConfigurationManager.getManager(toolConfiguration).getModelConfigurationFromFile(modelConfigurationFile.getAbsolutePath());
		
		return modelConfiguration;
	}
	
	
	private void logToolConfig(ToolConfigurationTemplate toolConfiguration)
	{
		logger.debug("Tool & batch configuration:");
		logger.debug(" - Operation mode: " + toolConfiguration.getOperationMode());
		if (toolConfiguration.getOperationMode() == 0) {
			logger.debug(" - Show GUI: " + toolConfiguration.isShowGUI());
		}
		logger.debug(" - Results folder location: " + toolConfiguration.getResultsFolderLocation());
		logger.debug(" - Bootstrap data location: " + toolConfiguration.getBootstrapDataFolderLocation());
	}
	
	private void logModelConfig(ModelConfiguration modelConfiguration)
	{
		logger.debug("Model configuration:");
		logger.debug(" - Forum ID: " + modelConfiguration.forumID);
		logger.debug(" - Simulation start date: " + modelConfiguration.simulationStartDate);
		logger.debug(" - Number of simulation days: " + modelConfiguration.getNumberOfSimulationDays());
		logger.debug(" - Number of bootstrap snapshots: " + modelConfiguration.numBootstrapSnapshots);
		logger.debug(" - Number of runs: " + modelConfiguration.getNumberOfSimulationRepetitions());
		logger.debug(" - Drop user activity: " + modelConfiguration.dropUserActivity);
		
		if (modelConfiguration.dropUserActivity)
		{
			logger.debug("   - User ID: " + modelConfiguration.userToDropActivityOf);
			logger.debug("   - Drop %:  " + modelConfiguration.userActivityDrop);
		}
	}

	/**
	 * @return the config
	 */
	public SimulationServiceJobConfig getConfig()
	{
		return config;
	}

	/**
	 * @return the jobRef
	 */
	public String getJobRef()
	{
		return jobRef;
	}

	/**
	 * @return the isUserDrop
	 */
	public boolean isIsUserDrop()
	{
		return isUserDrop;
	}

	/**
	 * @return the isTRTprob
	 */
	public boolean isIsTRTprob()
	{
		return isTRTprob;
	}

	/**
	 * @return the isCommunityActivityProb
	 */
	public boolean isIsCommunityActivityProb()
	{
		return isCommunityActivityProb;
	}

	/**
	 * @return the userDrop
	 */
	public double getUserDrop()
	{
		return userDrop;
	}

	/**
	 * @return the trtThreshold
	 */
	public double getTrtThreshold()
	{
		return trtThreshold;
	}

	/**
	 * @return the communityActivityThreshold
	 */
	public double getCommunityActivityThreshold()
	{
		return communityActivityThreshold;
	}

	/**
	 * @return the userID
	 */
	public String getUserID()
	{
		return userID;
	}

	/**
	 * @return the numRuns
	 */
	public int getNumRuns()
	{
		return numRuns;
	}
}
