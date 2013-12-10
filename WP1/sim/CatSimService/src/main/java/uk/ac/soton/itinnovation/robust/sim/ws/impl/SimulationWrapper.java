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
//      Created Date :          2013-10-10
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.sim.ws.impl;

import java.io.File;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationTemplate;
import uk.ac.soton.itinnovation.prestoprime.iplatform.init.ModelRunner;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig;
import uk.ac.soton.itinnovation.robust.catsim.SAPAggModel;
import uk.ac.soton.itinnovation.robust.catsim.config.ModelConfiguration;
import uk.ac.soton.itinnovation.robust.sim.ws.spec.ISimulationListener;
import uk.ac.soton.itinnovation.robust.sim.ws.spec.ISimulationWrapper;

/**
 * An example Evaluator implementation class, creating evaluation with randomly
 * generated probabilities. Emulates working on both streaming and non-streaming
 * data.
 * 
 * @author Vegard Engen
 */
public class SimulationWrapper implements Runnable, ISimulationWrapper
{
    // used in the randomised run-loop for generating evaluation results
    private String propertiesFile = "catSimService.properties";
	private Properties props;
	private boolean deleteResults = true; // whether to delete the outputs from the model after running (result object generated, though)
    
    // job configuration
    private String jobRef;
    private SimulationServiceJobConfig jobConfig;
	private JobStatus status;
	private SimulationConfigurer simConfigurer;
	private ResultProcessor resultProcessor;
    
    // listener to notify of new result or errors
    private ISimulationListener simListener;
    
    // thread for running this evaluator in
    private Thread simWrapperThread;
    
    static Logger logger = Logger.getLogger(SimulationWrapper.class);
    
    /**
     * Default constructor that initialises the simulation wrapper.
     */
    public SimulationWrapper(ISimulationListener simListener)
    {
        this.simListener = simListener;
        this.status = new JobStatus(JobStatusType.READY);
		
		this.props = getProperties();
		if (props.getProperty("deleteResultsAfterJobFinished") != null)
		{
			try {
				deleteResults = Boolean.parseBoolean(props.getProperty("deleteResultsAfterJobFinished"));
			} catch (Exception ex) {
				logger.warn("Unable to parse 'deleteResultsAfterJobFinished' to a boolean - leaving as default");
			}
		}
        
        this.simWrapperThread = new Thread((SimulationWrapper)this, "Simulation Wrapper Thread");
    }

    @Override
    public void run()
    {
        try
        {
			logger.debug("Simulation wrapper started");
			status = new JobStatus(JobStatusType.EVALUATING);
			
			logger.debug("Getting general tool and model configurations");
			ToolConfigurationTemplate toolConfiguration = simConfigurer.getToolConfig();
			ModelConfiguration modelConfiguration = simConfigurer.getModelConfig();
			File bootstrapDataDir = simConfigurer.getBootstrapDataDir();
			
			logger.debug("Starting simulation job (" + modelConfiguration.getNumberOfSimulationRepetitions() + " runs)");
			for (int run = 0; run < modelConfiguration.getNumberOfSimulationRepetitions(); run++)
			{
				logger.debug("Initialising the model for run #" + (run+1));
				
				// yes, this is stupid, but no time to write a reset functionality for the SAPAggModel
				// so it currently reads input data etc for each run to bootstrap a model run
				SAPAggModel model = new SAPAggModel();
				model.initialise(modelConfiguration);

				logger.debug("Running model");
				ModelRunner modelRunner = new ModelRunner(toolConfiguration, model, bootstrapDataDir.getAbsolutePath());
			}
			
			logger.debug("Simulation finished - will process the results now");
			if (status.getStatus() != JobStatusType.CANCELLED)
			{
				logger.debug("Simulation finished");
				status = new JobStatus(JobStatusType.FINISHED);
				
				logger.debug("Processing results");
				resultProcessor = new ResultProcessor();
				File resultsDir = new File(toolConfiguration.getResultsFolderLocation() + File.separator + bootstrapDataDir.getName());
				resultProcessor.initialise(simConfigurer, resultsDir);
				
				SimulationResult result = resultProcessor.getSimulationResult();
				ResultProcessor.printResults(result);
				
				if(simListener != null)
				{
					logger.debug("Notifying simulation listener of new result");
					simListener.onNewResult(jobRef, result);
				}
			}
			
			if (this.deleteResults) {
				deleteResultFiles(new File(toolConfiguration.getResultsFolderLocation()));
			}
			
			logger.info("Simulation wrapper thread completed");
        } catch (Exception ex) {
            logger.error("Caught an exception in the Simulation wrapper ", ex);
            status = new JobStatus(JobStatusType.ERROR, "Caught an exception in the Simulation wrapper ");
            logger.debug("Notifying listener of error");
            simListener.onError(jobRef, status);
        }
        
        logger.debug("Goodbye and thanks for all the fish!");
    }

    @Override
    public void startJob(String jobRef, SimulationServiceJobConfig config) throws Exception
    {
        this.jobRef = jobRef;
        this.jobConfig = config;
		
		logger.debug("Initialising SimulationConfigurer");
		simConfigurer = new SimulationConfigurer();
		simConfigurer.initialise(props, config, jobRef);
        
        simWrapperThread.start();
    }
	
    @Override
    public void cancelJob()
    {
        status = new JobStatus(JobStatusType.CANCELLED);
        logger.debug("Cancelled job");
    }

    @Override
    public JobStatus getJobStatus()
    {
        return status;
    }
    
    @Override
    public void setSimulationListener(ISimulationListener simListener)
    {
        this.simListener = simListener;
    }
	
	private void deleteResultFiles(File resultsDir)
	{
		logger.debug("Deleting results directory: " + resultsDir.getAbsolutePath());
		try {
			FileUtils.deleteDirectory(resultsDir);
		} catch (Exception ex) {
			logger.warn("Exception caught when trying to delete results dir: " + ex, ex);
		}
	}
	
	private Properties getProperties()
	{
		Properties props = new Properties();

        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream(propertiesFile));
        } catch (Exception ex) {
            logger.error("Error with loading configuration file catSimTest.properties. " + ex.getMessage(), ex);
            throw new RuntimeException("Error with loading configuration file catSimTest.properties. " + ex.getMessage(), ex);
        }
		
		return props;
	}
}