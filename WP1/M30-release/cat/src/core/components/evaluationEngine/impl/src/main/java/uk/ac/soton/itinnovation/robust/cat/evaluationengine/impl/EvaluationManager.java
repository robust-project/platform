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
//      Created Date :          2012-01-19
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.impl;

import java.net.URI;
import java.util.*;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.EEJobDetails;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationJobManager;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationManager;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationManagerListener;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 * The Evaluation Manager is responsible for time management and how to execute
 * the different modes the Evaluation Engine can run in: test, demo or live (not
 * supported in the current version). This class is responsible for starting
 * new evaluation jobs, but the actual logic for the job management is done in
 * the EvaluationJobManager class.
 * 
 * @author Vegard Engen
 */
public class EvaluationManager extends UFAbstractEventManager implements IEvaluationManager, Runnable
{
    private IEvaluationJobManager jobManager;
    private IDataLayer dataLayer;
    private URI eeServiceURI;
    
    // a boolean signifying whether the evaluation manager is in an active run mode or not
    private boolean run;
    
    // determines whether the Engine should evaluate risks according to the current
    // date, or do this pased on historical data.
    // OBS: this is currently not in use yet!
    private boolean liveMode;
    
    // introduced this for the prototype to help testing without having to deploy
    // a service to evaluate risks/opportunities.
    private boolean testMode;
    
    // the time in milliseconds that the Engine will sleep between checking
    // if any risks or opportunities are due to be evaluated and incrementing the
    // simulated date. Default is 5 seconds.
    private long sleepTime;
    
    // the current date, whether real or simulated, which is used to determine
    // if a risk or opportunity is due to be evaluated, and is also passed on
    // to predictor services
    private Date currentDate;
    
    // the date at which the engine should start evaluating risks and opportunities
    private Date startDate;
    
    // the date at which the evaluation engine should stop evaluating risks and
    // opportunities
    private Date endDate;
    
    // when not in live mode, the simulated clock will increment according to this
    // variable
    private FrequencyType simDateIncrement;
    
    // configuration parameters related to simulation mode
    // 1: if the engine should wait until all (non-streaming) jobs are finished
    //    before starting new jobs (and incrementing simulated date)
    // 2: if the engine should loop back to the start date once the end date
    //    has been reached
    // 3: how long the engine should wait when restarting, default is 0 seconds
    // 4: if the engine should delete all evaluation results when restarting
    private boolean waitForAllJobsToFinishBeforeStartingNew;
    private boolean simLoop;
    private long restartDelay;
    private boolean deleteEvaluationResultsOnRestart;
    
    // a variable to signifify whether the current evaluation epoch has started
    private boolean epochStarted;
    
    static Logger log = Logger.getLogger(EvaluationManager.class);
    
    /**
     * Default constructor which creates an EvaluationJobManager object and
     * gets default configuration parameters from 'evalEng.properties' on the 
     * class path.
     * @throws Exception 
     */
    public EvaluationManager(IDataLayer dataLayer, URI eeSrvURI) throws Exception
    {
        this.dataLayer = dataLayer;
        this.eeServiceURI = eeSrvURI;
        
        run = true;
        testMode = true;
        liveMode = false;
        epochStarted = false;
        simLoop = true;
        waitForAllJobsToFinishBeforeStartingNew = false;
        restartDelay = 0;
        deleteEvaluationResultsOnRestart = true;
        sleepTime = 1000 * 5;
        simDateIncrement = FrequencyType.DAILY;
        
        jobManager = new EvaluationJobManager(dataLayer, eeServiceURI);
        currentDate = DateUtil.getDateObject("2010-01-01");
        startDate = DateUtil.getDateObject("2010-01-01");
        endDate = DateUtil.getDateObject("2012-01-01");
        
        this.getConfigs();
    }
    
    /**
     * Gets configuration properties from 'evalEng.properties' on the class path.
     */
    private void getConfigs()
    {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("evalEng.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file evalEng.properties. " + ex.getMessage(), ex);
            return;
        }

        try {
            liveMode = Boolean.parseBoolean(prop.getProperty("liveMode"));
        } catch (Exception ex) {
            //log.error("Error with loading getting and converting 'liveMode' parameter from evalEng.properties. " + ex.getMessage(), ex);
            //return;
            log.error("Error with loading getting and converting 'liveMode' parameter from evalEng.properties; setting to false");
            liveMode = false;
        }
        log.info("Engine liveMode:  " + liveMode);
        
        try {
            testMode = Boolean.parseBoolean(prop.getProperty("testMode"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'testMode' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine testMode:  " + testMode);
        
        try {
            sleepTime = Integer.parseInt(prop.getProperty("sleepTime")) * 1000; // sleepTime is given in seconds in the text file
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'sleepTime' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine sleepTime: " + sleepTime);
        
        try {
            startDate = DateUtil.getDateObject(prop.getProperty("simStartDate"));
            currentDate = DateUtil.getDateObject(prop.getProperty("simStartDate"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'simStartDate' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine startDate: " + DateUtil.getDateString(startDate));
        
        try {
            endDate = DateUtil.getDateObject(prop.getProperty("simEndDate"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'simEndDate' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine endDate:   " + DateUtil.getDateString(endDate));
        
        try {
            simDateIncrement = FrequencyType.fromValue(prop.getProperty("simDateIncrement"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'simDateIncrement' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine dateIncrement: " + simDateIncrement);
        
        try {
            waitForAllJobsToFinishBeforeStartingNew = Boolean.parseBoolean(prop.getProperty("waitForAllJobsToFinishBeforeStartingNew"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'waitForAllJobsToFinishBeforeStartingNew' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine waitForAllJobsToFinishBeforeStartingNew:  " + waitForAllJobsToFinishBeforeStartingNew);
        
        try {
            simLoop = Boolean.parseBoolean(prop.getProperty("simLoop"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'simLoop' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine simLoop:  " + simLoop);
        
        try {
            restartDelay = Long.parseLong(prop.getProperty("restartDelay"));
            if (restartDelay < 0) {
                log.error("The restartDelay parameter (" + restartDelay + ") is not valid - cannot be less than 0. Setting to 0.");
                restartDelay = 0;
            }
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'restartDelay' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine restartDelay:  " + restartDelay + " seconds");
        
        try {
            deleteEvaluationResultsOnRestart = Boolean.parseBoolean(prop.getProperty("deleteEvaluationResultsOnRestart"));
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'deleteEvaluationResultsOnRestart' parameter from evalEng.properties. " + ex.getMessage(), ex);
            return;
        }
        log.info("Engine deleteEvaluationResultsOnRestart:  " + deleteEvaluationResultsOnRestart);
    }
    
    /**
     * Set the data layer.
     * @param dataLayer Data layer implementation.
     */
    public void setDataLayer(IDataLayer dataLayer)
    {
        this.dataLayer = dataLayer;
        ((EvaluationJobManager)jobManager).setDataLayer(dataLayer);
    }
    
    /**
     * Set the Evaluation Engine Service URI.
     * @param eeSrvURI The URI of the Evaluation Engine Service.
     */
    public void setEvaluationEngineServiceURI(URI eeSrvURI)
    {
        this.eeServiceURI = eeSrvURI;
        ((EvaluationJobManager)jobManager).setEvaluationEngineServiceURI(eeServiceURI);
    }
    
    @Override
    public void run()
    {
        log.info("Starting the EvaluationManager thread");
        
        boolean notifyOfStop = false;
        boolean evaluationStarted = false;
        
        while (run)
        {
            if (!evaluationStarted)
            {
                log.debug("Checking if there are any risks in the database before we really start the process");
                Map<Community, Set<Risk>> riskMap = dataLayer.getAllActiveRisks();
                if ((riskMap == null) || riskMap.isEmpty()) {
                    log.debug("No risks, so having a snooze for " + (sleepTime / 1000) + " seconds");
                    try {
                        Thread.currentThread().sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        log.error("Caught an InterruptedException when trying to sleep.. " + ex.toString(), ex);
                    }
                    continue;
                }
                
                log.debug("There are some risks there, so will start the evaluation process");
                evaluationStarted = true;
            }
            
            // checking if the end time has been reached
            if ((endDate.getTime() - currentDate.getTime()) < 86400000) // 86400000 = a day in milliseconds
            {
                log.info("End date reached ("+ DateUtil.getDateString(endDate) +").");
                
                if (!simLoop)
                {
                    notifyOfStop = true;
                    break;
                }
                else // should restart and loop
                {
                    try {
                        log.info("Waiting " + restartDelay + " seconds before restarting");
                        Thread.currentThread().sleep(this.restartDelay * 1000);
                    } catch (InterruptedException ex) { }
                    
                    if (this.deleteEvaluationResultsOnRestart)
                    {
                        log.info("Deleting evaluation results");
                        deleteEvaluationResults();
                        log.info("Delay period over - restarting");
                        reset(true);
                    }
                    else // don't delete evaluation results before restarting
                    {
                        reset(true);
                    }
                    
                    notifyListenersOfRestart();
                    
                    log.info("Restarted - back to " + DateUtil.getDateString(startDate));
                    try {
                        Thread.currentThread().sleep(5000); // sleeping for 5 sec to allow notifications to pass through the system
                    } catch (InterruptedException ex) { }
                }
            }
            
            if (!epochStarted) {
                notifyListenersOfEvaluationEpochStarted(currentDate);
            }
            
            if (testMode) {
                testLoop();
            } else {
                simulationLoop();
            }
        }

        if (notifyOfStop)
        {
            notifyListenersOfStop();
            notifyOfStop = false;
        }
        
        // re-set run since the EvaluationManager object might be re-used in a new thread
        reset(false);
        
        log.info("EvaluationManager thread has now come to a graceful end");
    }
    
    /**
     * Resets the properties of the manager class and clears any job data from
     * the job manager.
     * @param restartFlag Flag to indicate that it was called from the restart setting.
     */
    private synchronized void reset(boolean restartFlag)
    {
        // don't restart if the run flag is already false, when called from the restart context
        // this means the EE has been stopped while delaying a restart
        if (restartFlag && !run) {
            return;
        }
        
        run = true;
        epochStarted = false;
        currentDate = startDate;
        jobManager.clearJobData();
    }
    
    /**
     * The simulation evaluation loop, which will start jobs with services and check
     * when finished, to get results.
     */
    private void simulationLoop()
    {
        try
        {
            log.debug("Currently " + jobManager.getNumJobsBeingEvaluated() + " job(s) are being evaluated (" + jobManager.getNumNonStreamingJobsBeingEvaluated() + " non-streaming jobs)");
            
            if ((waitForAllJobsToFinishBeforeStartingNew && (jobManager.getNumNonStreamingJobsBeingEvaluated() == 0)) ||
                    (!waitForAllJobsToFinishBeforeStartingNew))
            {
                log.info("Starting evaluation jobs - if any risks or opportunities are due to be evaluated");
                Map<UUID, EEJobDetails> jobDetails = jobManager.startEvaluationJobs(currentDate);
                if ((jobDetails != null) && !jobDetails.isEmpty())
                {
                    log.info(jobDetails.size() + " jobs were started");
                }
                else
                {
                    log.info("No evaluation jobs were started");
                    // check if a new epoch should be started - only when there are no non-streaming
                    // jobs being evaluated
                    if (run && (jobManager.getNumNonStreamingJobsBeingEvaluated() == 0))
                    {
                        notifyListenersOfEvaluationEpochFinished(currentDate);
                        currentDate = DateUtil.getToDate(currentDate, simDateIncrement);
                        log.info("Incremented the simulation date to: " + DateUtil.getDateString(currentDate));
                        epochStarted = false;
                    }
                }
                
                epochStarted = true;
            }
            else
            {
                // check status of risks - which could discover that risks that
                // have pending evaluation jobs (causing an infinite loop) have
                // been deleted.
                jobManager.checkRiskStatus();
            }

            log.debug("Having a snooze for " + (sleepTime / 1000) + " seconds");
            Thread.currentThread().sleep(sleepTime);

        } catch (InterruptedException ex) {
            log.error("Caught an exception in the run loop of the Evaluation Manager", ex);
        }
    }
    
    /**
     * A test loop, which just returns a random evaluation result to any listeners
     */
    private void testLoop()
    {
        try
        {
            epochStarted = true;
            Thread.currentThread().sleep(sleepTime);
            
            List<Risk> rosToStart = new ArrayList<Risk>();

            // get all active risks for all communities
            log.debug("Getting active risks and opportunities for all communities from the datalayer");
            Map<Community, Set<Risk>> communityRiskMap = dataLayer.getAllActiveRisks();
            if (communityRiskMap != null)
            {
                for (Community community : communityRiskMap.keySet())
                {
                    // get risks and opportunities for the community
                    Set<Risk> ros = communityRiskMap.get(community);
                    
                    if (ros != null)
                    {
                        // add R/Os to the set to be started
                        for (Risk ro : ros) {
                            rosToStart.add(ro);
                        }
                    }
                }
            }

            if (run)
            {
                if (!rosToStart.isEmpty())
                {
                    log.debug("Creating some random results for each of the " + rosToStart.size() + " risks");
                    for (Risk r : rosToStart)
                    {
                        EvaluationResult evalRes = getRandomEvaluationResult(r.getId());
                        try {
                            log.debug("Saving the evaluation result");
                            dataLayer.saveEvaluationResults(r.getId(), evalRes);
                        } catch (Throwable t) {
                            log.error("Unable to save evaluation result: " + t.toString(), t);
                        }
                        notifyListenersOfNewResults(evalRes);
                        Thread.currentThread().sleep(100); // sleeping for a moment
                    }
                }
                else {
                    log.debug("There were no risks and opportunities to evaluate");
                }
                
                notifyListenersOfEvaluationEpochFinished(currentDate);
                epochStarted = false;

                currentDate = DateUtil.getToDate(currentDate, simDateIncrement);
                log.info("Incremented the simulation date to: " + DateUtil.getDateString(currentDate));
            }
        } catch (InterruptedException ex) {
            log.error("Caught an interrupted exception in the test loop of the Evaluation Manager", ex);
        } catch (Exception e) {
            log.error("Caught an exception in the test loop of the Evaluation Manager", e);
        }
    }
    
    /**
     * Get a an EvaluationResult with a random probability.
     * @return 
     */
    private EvaluationResult getRandomEvaluationResult(UUID riskUUID)
    {
        Random rand = new Random();
        EvaluationResult res = new EvaluationResult();
        res.setRiskUUID(riskUUID);
        res.setCurrentDate(currentDate);
        res.setForecastDate(DateUtil.getToDate(currentDate, simDateIncrement));
        res.addResultItem(new ResultItem("probability", rand.nextDouble()));
        
        JobDetails jobDetails = new JobDetails();
        jobDetails.setJobRef(UUID.randomUUID().toString());
        jobDetails.setRequestDate(new Date());
        jobDetails.setStartDate(new Date());
        jobDetails.setCompletionDate(new Date());
        jobDetails.setJobStatus(new JobStatus(JobStatusType.FINISHED));
        res.setJobDetails(jobDetails);
        
        return res;
    }
    
    /**
     * Check if there are any results for any jobs that may have been started.
     */
    private void checkForResults()
    {
        if (jobManager.getNumJobsBeingEvaluated() > 0)
        {
            log.info("Checking if any of the " + jobManager.getNumJobsBeingEvaluated() + " evaluation jobs have finished");
            Map<UUID, EvaluationResult> results = jobManager.checkForEvaluationResults();
            if ((results != null) && !results.isEmpty())
            {
                log.info("Got some results, which will now be processed");
                processResults(results);
            }
            else
            {
                log.info("No evaluation results available...");
            }
        }
    }
    
    /**
     * Processes the results, which will notify any listeners of any results that did not fail.
     * @param results 
     */
    private void processResults(Map<UUID, EvaluationResult> results)
    {
        if ((results == null) || results.isEmpty())
            return;
        
        log.info("Processing " + results.size() + " result(s)");

        for (UUID roUUID : results.keySet())
        {
            EvaluationResult res = results.get(roUUID);
            
            if (res == null) {
                log.error("res is NULL");
                continue;
            }
            
            if (res.getJobDetails() == null) {
                log.error("res.getJobDetails() is NULL");
                continue;
            }
            
            if (res.getJobDetails().getJobStatus().getStatus() == null) {
                log.error("res.getJobDetails().getJobStatus().getStatus() is NULL");
                continue;
            }
            
            if ((res.getJobDetails().getJobStatus().getStatus() == JobStatusType.FAILED) || (res.getJobDetails().getJobStatus().getStatus() == JobStatusType.ERROR))
            {
                log.error("There result had a failed/erroronous status");
                continue;
            }
            
            if (res.getRiskUUID() == null)
                res.setRiskUUID(roUUID);
            
            notifyListenersOfNewResults(res);
            dataLayer.saveEvaluationResults(roUUID, res);
        }
        
        // check if a new epoch should be started - only when there are no non-streaming
        // jobs being evaluated
        if (run && (jobManager.getNumNonStreamingJobsBeingEvaluated() == 0))
        {
            notifyListenersOfEvaluationEpochFinished(currentDate);
            currentDate = DateUtil.getToDate(currentDate, simDateIncrement);
            log.info("Incremented the simulation date to: " + DateUtil.getDateString(currentDate));
            epochStarted = false;
        }
    }
    
    @Override
    public void newEvaluationResult(String jobRef, EvaluationResult evalRes)
    {
        if (jobRef == null) {
            log.error("Got some new evaluation results, but the job reference was NULL");
            return;
        }
        if (evalRes == null) {
            log.error("Got some new evaluation results, but the object was NULL");
            return;
        }
        if (!jobManager.doesJobExist(jobRef)) {
            log.error("Got some new evaluation results, but no risk evaluation job with the given job reference was not known: " + jobRef);
            return;
        }
        
        UUID riskUUID = jobManager.getRiskUUIDforJobRef(jobRef);
        if (riskUUID == null) {
            log.error("Got some new evaluation results, but no risk was associated with the job with the given reference: " + jobRef);
            return;
        }
        
        if (evalRes.getRiskUUID() == null) {
            evalRes.setRiskUUID(riskUUID);
        }
        
        log.info("Got some new results for job ref: " + jobRef);
        jobManager.newEvaluationResult(jobRef, evalRes);
        try {
            log.debug("Saving the evaluation result");
            dataLayer.saveEvaluationResults(riskUUID, evalRes);
        } catch (Throwable t) {
            log.error("Unable to save evaluation result: " + t.toString(), t);
        }
        notifyListenersOfNewResults(evalRes);
        
        // check if a new epoch should be started - only when there are no non-streaming
        // jobs being evaluated
        if (run && (jobManager.getNumNonStreamingJobsBeingEvaluated() == 0))
        {
            notifyListenersOfEvaluationEpochFinished(currentDate);
            currentDate = DateUtil.getToDate(currentDate, simDateIncrement);
            log.info("Incremented the simulation date to: " + DateUtil.getDateString(currentDate));
            epochStarted = false;
        }
    }
    
    @Override
    public void updateEvaluationJobStatus(String jobRef, JobStatus jobStatus)
    {
        jobManager.updateEvaluationJobStatus(jobRef, jobStatus);
    }
    
    /**
     * Will notify any listeners of starting an evaluation "epoch".
     */
    private void notifyListenersOfEvaluationEpochStarted(Date date)
    {
        //log.debug("Notifying any listeners about the new evaluation results");
        List<IEvaluationManagerListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            //log.debug("There's " + listeners.size() + " who will now enjoy some new results...");
            for (IEvaluationManagerListener listener : listeners)
            {
                try {
                listener.onEvaluationManagerEvaluationEpochStarted(date);
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener");
                }
            }
        }
    }
    
    /**
     * Will notify any listeners of starting an evaluation "epoch".
     */
    private void notifyListenersOfEvaluationEpochFinished(Date date)
    {
        //log.debug("Notifying any listeners about the new evaluation results");
        List<IEvaluationManagerListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            //log.debug("There's " + listeners.size() + " who will now enjoy some new results...");
            for (IEvaluationManagerListener listener : listeners)
            {
                try {
                listener.onEvaluationManagerEvaluationEpochFinished(date);
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener");
                }
            }
        }
    }

    /**
     * Will notify any listeners of the new evaluation result.
     * @param res Evaluation result object.
     */
    private void notifyListenersOfNewResults(EvaluationResult res)
    {
        //log.debug("Notifying any listeners about the new evaluation results");
        List<IEvaluationManagerListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            //log.debug("There's " + listeners.size() + " who will now enjoy some new results...");
            for (IEvaluationManagerListener listener : listeners)
            {
                try {
                listener.onNewEvaluationResults(res);
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener: " + ex, ex);
                }
            }
        }
    }
    
    /**
     * Will notify any listeners of the new evaluation results.
     * @param results A map of UUIDs (key) for risks/opportunities and their respective results.
     */
    private void notifyListenersOfNewResults(Map<UUID, EvaluationResult> results)
    {
        //log.debug("Notifying any listeners about the new evaluation results");
        List<IEvaluationManagerListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            //log.debug("There's " + listeners.size() + " who will now enjoy some new results...");
            for (IEvaluationManagerListener listener : listeners)
            {
                for (UUID uuid : results.keySet())
                {
                    try {
                    listener.onNewEvaluationResults(results.get(uuid));
                    } catch (Exception ex) {
                        log.error("Caught an exception when trying to notify a listener");
                    }
                }
            }
        }
    }
    
    /**
     * Will notify any listeners that the evaluation engine has stopped.
     */
    private void notifyListenersOfStop()
    {
        //log.debug("Notifying any listeners that the engine has stopped");
        List<IEvaluationManagerListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            //log.debug("There's " + listeners.size() + " who will now get a notification...");
            for (IEvaluationManagerListener listener : listeners)
            {
                try {
                listener.onEvaluationManagerStop();
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener");
                }
            }
        }
    }
    
    /**
     * Will notify any listeners that the evaluation engine has stopped.
     */
    private void notifyListenersOfRestart()
    {
        //log.debug("Notifying any listeners that the engine has stopped");
        List<IEvaluationManagerListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            //log.debug("There's " + listeners.size() + " who will now get a notification...");
            for (IEvaluationManagerListener listener : listeners)
            {
                try {
                    listener.onEvaluationManagerRestart();
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener");
                }
            }
        }
    }

    @Override
    public void stop() throws Exception
    {
        log.debug("Stopping the EvaluationManager thread - need to wait for the run loop to finish");
        run = false;
    }

    @Override
    public void addListener(IEvaluationManagerListener listener)
    {
        super.addListener((IUFListener) listener);
    }

    @Override
    public void removeListener(IEvaluationManagerListener il)
    {
        log.warn("OBS: remove IEvaluationManagerListener method not implemented!");
    }
    
    @Override
    public void setSimulationDates(Date startDate, Date endDate)
    {
        this.currentDate = startDate;
        this.startDate = startDate;
        this.endDate = endDate;
        
        log.info("Engine startDate: " + DateUtil.getDateString(this.startDate));
        log.info("Engine endDate:   " + DateUtil.getDateString(this.endDate));
    }
    
    @Override
    public void setSimulationDatesAndFrequency(Date startDate, Date endDate, FrequencyType incrementFrequency)
    {
        this.currentDate = startDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.simDateIncrement = incrementFrequency;
        
        log.info("Engine startDate: " + DateUtil.getDateString(this.startDate));
        log.info("Engine endDate:   " + DateUtil.getDateString(this.endDate));
        log.info("Engine dateIncrement: " + this.simDateIncrement);
    }
    
    /**
     * Temporary method for the M15 prototype to set the test mode.
     * If testMode is TRUE, then web services will not be called and random
     * results will be generated instead.
     * @param testMode 
     */
    public void setTestMode(boolean testMode)
    {
        this.testMode = testMode;
    }
    
    /**
     * Gets the list of risk job details from the job manager and iterates over each
     * risk to delete the results from the datalayer.
     */
    private void deleteEvaluationResults()
    {
        // get communities names
        log.info("Getting all risks and opportunities for all communities from the datalayer to delete evaluation results");
        for (Community community : dataLayer.getCommunities())
        {
            Set<Risk> risks = dataLayer.getRisks(community.getUuid());
            
            // iterating over risks/opportunities for the community
            for (Risk ro : risks)
            {
                if (ro.getId() == null) {
                    continue;
                }

                try {
                    log.info(" - Deleting evaluation results for risk with UUID: " + ro.getId().toString());
                    dataLayer.deleteEvaluationResults(ro.getId());
                } catch (Throwable t) { log.warn("Caught an exception when trying to delete results for risk with UUID " + ro.getId().toString() + ": " + t.toString(), t); }
            } // for each risk for the community
        } // for each community
    }
}
