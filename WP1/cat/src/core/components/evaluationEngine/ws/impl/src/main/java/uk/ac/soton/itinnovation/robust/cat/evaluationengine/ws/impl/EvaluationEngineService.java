/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
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
//      Created Date :          2012-08-06
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.impl;

import java.net.URI;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import javax.jws.WebService;
import org.apache.cxf.interceptor.Fault;
import org.apache.log4j.Logger;
import org.springframework.context.Lifecycle;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.datalayer.impl.DataLayerImpl;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.impl.EvaluationEngine;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationEngine;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.IEvaluationEngineService;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.Status;

/**
 * Evaluation Engine Service, exposing an interface to start and stop the evaluation
 * engine, and for predictor services to provide evaluation results and evaluation
 * job updates.
 * 
 * @author Vegard Engen
 */
@WebService(endpointInterface = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.IEvaluationEngineService")
public class EvaluationEngineService implements IEvaluationEngineService, Lifecycle
{
    private IEvaluationEngine evalEng;
    private EvaluationEngineMonitor evalEngMon;
    private StreamManager streamManager;
    private IDataLayer dataLayer;
    private URI eeServiceURI;
    
    // initialisation params
    private boolean initialised;
    private long initDelayMS;
    
    // streaming related variables
    private boolean publishToLocalhost;
    private String streamBrokerHostname;
    private int streamBrokerPort;
    
    static Logger log = Logger.getLogger(EvaluationEngineService.class);
    
    /**
     * Default constructor, which creates an evaluation engine monitor, which listens
     * to events from the evaluation engine.
     */
    public EvaluationEngineService() throws Exception
    {
        log.info("Evaluation Engine Service STARTING UP");
        initialised = false;
        initDelayMS = 1000 * 5; // default 5 sec
        
        getConfigs();
        log.info("Evaluation Engine Service CONFIGURED");
        
        Thread it = new Thread(new InitialiseThread());
        it.start();
    }
    
    /**
     * Gets configuration properties from 'evalEngService.properties' on the class path.
     */
    private void getConfigs()
    {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("evalEngService.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file evalEngService.properties. " + ex.getMessage(), ex);
            throw new RuntimeException("Error with loading configuration file evalEngService.properties. " + ex.getMessage(), ex);
        }
        
        try {
            String eeServiceURIstr = prop.getProperty("eeServiceURI");
            eeServiceURI = new URI(eeServiceURIstr);
        } catch (Exception ex) {
            log.error("Error getting 'eeServiceURI' parameter from service.properties. " + ex.getMessage(), ex);
            throw new RuntimeException("Error getting 'eeServiceURI' parameter from service.properties. " + ex.getMessage(), ex);
        }
        log.info("EE Service URI: " + eeServiceURI);
        
        try {
            publishToLocalhost = Boolean.parseBoolean(prop.getProperty("publishToLocalhost"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting the 'publishToLocalhost' parameter from evalEngService.properties. " + ex.getMessage(), ex);
            throw new RuntimeException("Error with loading getting and converting the 'publishToLocalhost' parameter from evalEngService.properties. " + ex.getMessage(), ex);
        }
        log.info("Publish to localhost: " + publishToLocalhost);
        
        try {
            streamBrokerHostname = prop.getProperty("streamBrokerHostname");
        } catch (Exception ex) {
            log.error("Error with loading getting the 'streamBrokerHostname' parameter from evalEngService.properties. " + ex.getMessage(), ex);
            throw new RuntimeException("Error with loading getting the 'streamBrokerHostname' parameter from evalEngService.properties. " + ex.getMessage(), ex);
        }
        log.info("Stream broker hostname: " + streamBrokerHostname);
        
        try {
            streamBrokerPort = Integer.parseInt(prop.getProperty("streamBrokerPort"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting the 'streamBrokerPort' parameter from evalEngService.properties. " + ex.getMessage(), ex);
            throw new RuntimeException("Error with loading getting and converting the 'streamBrokerPort' parameter from evalEngService.properties. " + ex.getMessage(), ex);
        }
        log.info("Stream broker port: " + streamBrokerPort);
        
        try {
            initDelayMS = Long.parseLong(prop.getProperty("initDelay")) * 1000;
            if (initDelayMS < 0)
            {
                log.warn("The 'initDelay' parameter from evalEngService.properties was less than 0 - setting to default value.");
                initDelayMS = 1000 * 5; // 5 seconds
            }
        } catch (Exception ex) {
            log.warn("Error with loading getting and converting the 'initDelay' parameter from evalEngService.properties - setting to default value.", ex);
            initDelayMS = 1000 * 5; // 5 seconds
        }
        log.info("Initialisation delay: " + (initDelayMS/1000) + " seconds");
    }
    
    private void init() throws Exception
    {
        if (dataLayer == null)
        {
            log.info("Setting up R/O Data Service client");
            try {
                dataLayer = new DataLayerImpl();
            } catch (Exception ex) {
                dataLayer = null;
                log.error("Failed to make a connection to the R/O Data Service: " + ex.getMessage());
                log.error("Unable to start Evaluation Engine Service");
                throw ex;
            }
        }
        
        if (streamManager == null)
        {
            log.info("Setting up the Stream Manager");
            try {
                streamManager = new StreamManager(publishToLocalhost, streamBrokerHostname, streamBrokerPort, dataLayer);
                streamManager.setUpGeneralStream();
                log.info("getting the communities from datalayer.");
                streamManager.setUpCommunityStreams(dataLayer.getCommunities());
                log.info("streamManager.setUpCommunityStreams done!");
            } catch (Exception ex) {
                streamManager = null;
                log.error("The Stream Manager could not be set up! " + ex.getMessage());
                log.error("Unable to start Evaluation Engine Service");
                throw ex;
            }
        }
        
        if (evalEngMon == null)
        {
            log.info("Setting up the Evaluation Engine Monitor");
            evalEngMon = new EvaluationEngineMonitor(streamManager, false);
        }
        
        if (evalEng == null)
        {
            try {
                log.info("Creating Evaluation Engine instance");
                evalEng = EvaluationEngine.getInstance(dataLayer, eeServiceURI);
            } catch (Exception ex) {
                evalEng = null;
                log.error("Unable to create Evaluation Engine instance: " + ex.getMessage());
                log.error("Unable to start Evaluation Engine Service");
                throw ex;
            }
        }
        
        evalEng.addEvaluationEngineListener(evalEngMon);
        evalEng.addEvaluationResultListener(evalEngMon);
        
        initialised = true;
        log.info("Evaluation Engine Service finished initialisation successfully!");
        log.info("---------------------------------------------------------------");
    }
    
    @Override
    public Status startEngine()
    {
        log.info("Call to startEngine()");
        
        // first check if not initialised - try to intialise
        if (!initialised)
        {
            try {
                init();
            } catch (Throwable t) {
                log.error("Failed to start the evaluation engine because it was not initialised properly: " + t.toString(), t);
                return new Status(false, "Failed to start the evaluation engine because it was not initialised properly");
            }
        }
        
        // second check if not initialised, return false
        if (!initialised)
        {
            log.error("Failed to start the evaluation engine because it was not initialised properly");
            return new Status(false, "Failed to start the evaluation engine because it was not initialised properly");
        }
        
        if (evalEngMon.isEngineRunning())
        {
            log.debug("The evaluation engine is already running, so doing nothing");
            return new Status(false, "The evaluation engine is already running");
        }
        
        try {
            evalEng.start();
        } catch (Exception ex) {
            log.error(ex);
            return new Status(false, "Failed to start the evaluation engine: " + ex.getMessage());
        }
        
        return new Status(true);
    }
    
    @Override
    public Status startEngineForTimePeriod(Date startDate, Date endDate)
    {
        log.info("Call to startEngineForTimePeriod(..)");
        
        if (!initialised)
        {
            log.error("Failed to start the evaluation engine because it was not initialised properly");
            return new Status(false, "Failed to start the evaluation engine because it was not initialised properly");
        }
        
        if (evalEngMon.isEngineRunning())
        {
            log.warn("The evaluation engine is already running");
            return new Status(false, "The evaluation engine is already running");
        }
        
        try {
            evalEng.start(startDate, endDate);
        } catch (Exception ex) {
            log.error(ex);
            return new Status(false, "Failed to start the evaluation engine: " + ex.getMessage());
        }
        
        return new Status(true);
    }
    
    @Override
    public Status stopEngine()
    {
        log.info("Call to stopEngine()");
        if (evalEngMon.isEngineRunning())
        {
            try {
                evalEng.stop();
            } catch (Exception ex) {
                log.error(ex);
                return new Status(false, "Failed to stop the evaluation engine: " + ex.getMessage());
            }
        }
        
        return new Status(true);
    }
    
    @Override
    public boolean isEngineRunning()
    {
        log.info("Call to isEngineRunning()");
        return evalEngMon.isEngineRunning();
    }
    
    @Override
    public StreamDetails getGeneralStreamDetails()
    {
        log.info("Call to getGeneralStreamDetails()");
        
        if (!initialised)
        {
            log.error("The evaluation engine service has not been initialised, so cannot return stream details");
            throw new Fault (new RuntimeException("The evaluation engine service has not been initialised, so cannot return stream details"));
        }
        
        try {
            return this.streamManager.getGeneralStreamDetails();
        } catch (Exception e) {
            log.error("Failed to provide the stream details: " + e.getMessage(), e);
            throw new Fault(e);
        }
    }
    
    @Override
    public StreamDetails getCommunityResultsStreamDetails(UUID communityID)
    {
        log.info("Call to getCommunityResultsStreamDetails(..)");
        
        if (!initialised)
        {
            log.error("The evaluation engine service has not been initialised, so cannot return stream details");
            throw new Fault (new RuntimeException("The evaluation engine service has not been initialised, so cannot return stream details"));
        }
        
        try {
            return this.streamManager.getCommunityResultsStreamDetails(communityID);
        } catch (Exception e) {
            log.error("Failed to provide the stream details: " + e.getMessage());
            throw new Fault(e);
        }
    }
    
    @Override
    public void newEvaluationResult(String jobRef, EvaluationResult evalRes)
    {
        log.info("Call to newEvaluationResult(..)");
        
        try {
			Thread t = new Thread(new NewEvaluationResultThread(jobRef, evalRes));
			t.start();
		} catch (Exception ex) {
			log.error("Exception caught when processing a new evaluation result", ex);
		}
    }
	
    @Override
    public void updateEvaluationJobStatus(String jobRef, JobStatus jobStatus)
    {
		log.info("Call to updateEvaluationJobStatus(..)");

		try {
			Thread t = new Thread(new UpdateEvaluationJobStatusThread(jobRef, jobStatus));
			t.start();
		} catch (Exception ex) {
			log.error("Exception caught when processing a job update", ex);
		}
    }
	
    @Override
    public void start()
    {
        log.debug("Lifecycle.start() called - doing nothing though");
    }
    
    @Override
    public void stop()
    {
        log.debug("Lifecycle.stop() called");
        if (evalEngMon.isEngineRunning())
        {
            try {
                evalEng.stop();
            } catch (Exception ex) {
                log.error("Failed to stop the evaluation engine: " + ex.getMessage(), ex);
            }
        }
    }
    
    @Override
    public boolean isRunning()
    {
        log.debug("Lifecycle.isRunning() called");
        
        return initialised;
    }
    
    protected class InitialiseThread implements Runnable
    {
        @Override
        public void run()
        {
            try {
                if (initDelayMS > 0)
                {
                    log.info("Completing the initialisation in " + (initDelayMS/1000) + " seconds ...");
                    Thread.sleep(initDelayMS);
                }
                init();
            } catch (InterruptedException iex) {
                log.error("Caught an interrupted exception when initialising the evaluation engine", iex);
            } catch (Throwable e) {
                log.error("Caught an exception when initialising the evaluation engine", e);
            }
        }
    }
	
	protected class NewEvaluationResultThread implements Runnable
	{
		private String jobRef;
		private EvaluationResult evalRes;
		
		public NewEvaluationResultThread(String jobRef, EvaluationResult evalRes)
		{
			this.jobRef = jobRef;
			this.evalRes = evalRes;
		}
		
		@Override
		public void run()
		{
			try {
				if (!initialised) {
					log.warn("The evaluation engine service has not been initialised, so cannot deal with the new evaluation result");
					return;
				}

				if (jobRef == null)
				{
					log.error("The job reference for the evaluation result was NULL");
					return;
				}

				if (evalRes == null)
				{
					log.error("The evaluation result object was NULL");
					return;
				}

				evalEng.newEvaluationResult(jobRef, evalRes);
			} catch (Throwable t) {
				log.error("Caught an exception trying to deal with a call to update the status for an evaluation job", t);
			}
		}
	}
	
	protected class UpdateEvaluationJobStatusThread implements Runnable
	{
		private String jobRef;
		private JobStatus jobStatus;
		
		public UpdateEvaluationJobStatusThread(String jobRef, JobStatus jobStatus)
		{
			this.jobRef = jobRef;
			this.jobStatus = jobStatus;
		}
		
		@Override
		public void run()
		{
			try {
				if (!initialised) {
					log.warn("The evaluation engine service has not been initialised, so cannot deal with the job status update");
					return;
				}

				if (this.jobRef == null)
				{
					log.error("The job reference for the evaluation result was NULL");
					return;
				}

				if (this.jobStatus == null)
				{
					log.error("The job status object was NULL");
					return;
				}

				evalEng.updateEvaluationJobStatus(jobRef, jobStatus);
			} catch (Throwable t) {
				log.error("Caught an exception trying to deal with a call to update the status for an evaluation job", t);
			}
		}
	}
}
