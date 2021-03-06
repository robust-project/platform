/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre and CORMSIS, 2012
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
//      Created By :            Vegard Engen, Edwin Tye
//      Created Date :          2012-03-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.ps.cm;

import java.util.*;
import org.apache.log4j.Logger;
import uk.ac.soton.cormsis.robust.ps.spec.IEvaluationListener;
import uk.ac.soton.cormsis.robust.ps.spec.IEvaluator;
import uk.ac.soton.cormsis.robust.ps.spec.IPredictorJobManager;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.client.EvaluationEngineClient;

/**
 * This simple Job Manager class manages the evaluation jobs in memory and does
 * not store the state, so if the predictor service goes down, all evaluation
 * jobs will be lost.
 *
 * @author Vegard Engen
 */
public class PredictorJobManager implements IPredictorJobManager {

    // singleton instance of the job manager
    private static PredictorJobManager jobManager = null;
    private PredictorServiceDescription desc;
    
    // collectings for managing the evaluation jobs
    private Map<String, PredictorServiceJobConfig> jobConfigMap; // configuration for all jobs (key = job ref)
    private Map<String, JobDetails> jobDetailsMap;               // map of all jobs (job ref = key), whether queued, evaluating or finished
    private Map<String, EvaluationResult> jobResultMap;          // evaluation results saved for each job reference (key)
    private Queue<String> jobQueue;                              // references to jobs that have been asked to be started
    private Queue<String> streamingJobQueue;                     // references to streaming jobs that have been asked to be started
    private Set<String> jobsBeingEvaluated;                      // the number of non-streaming jobs that are being evaluated
    private Set<String> streamingJobsBeingEvaluated;             // the number of streaming jobs that are being evaluated
    private Map<String, IEvaluator> evaluatorMap;                // map of the evaluator instance(s) for each job (job ref = key)
    
    // sets the maximum number of consecutively running streaming or non-streaming jobs
    private int maxRunningNonStreamingJobs;
    private int maxRunningStreamingJobs;
    
    // sets the platorm type the service is configured to work on
    private PlatformType platformType;
    
    // listener for the PredictorJobManager when starting evaluation jobs to get
    // notifications of new results or errors that may occur.
    private EvaluationListener resultListener;
    
    static Logger log = Logger.getLogger(PredictorJobManager.class);

    /**
     * Default constructor, which initialises the maps used to manage the
     * evaluation jobs.
     */
    private PredictorJobManager(PredictorServiceDescription desc, PlatformType platformType)
    {
        this.desc = desc;
        this.platformType = platformType;
        
        // initialising collections
        jobConfigMap = Collections.synchronizedMap(new HashMap<String, PredictorServiceJobConfig>());
        jobDetailsMap = Collections.synchronizedMap(new HashMap<String, JobDetails>());
        jobResultMap = Collections.synchronizedMap(new HashMap<String, EvaluationResult>());
        jobQueue = new LinkedList<String>();
        streamingJobQueue = new LinkedList<String>();
        jobsBeingEvaluated = Collections.synchronizedSet(new HashSet<String>());
        streamingJobsBeingEvaluated = Collections.synchronizedSet(new HashSet<String>());
        jobConfigMap = Collections.synchronizedMap(new HashMap<String, PredictorServiceJobConfig>());
        evaluatorMap = Collections.synchronizedMap(new HashMap<String, IEvaluator>());
        
        // setting up listener and default queue config
        resultListener = this.new EvaluationListener();
        maxRunningNonStreamingJobs = 2; // by default, supporting running two non-streaming jobs at the same time
        maxRunningStreamingJobs = 0; // by default, supporting running no streaming jobs
        
        log.debug("Getting configuration parameters");
        getConfigs();
    }
    
    /**
     * Returns an instance of the EvaluationEngine.
     * @return
     * @throws Exception 
     */
    public static IPredictorJobManager getInstance(PredictorServiceDescription desc, PlatformType platformType)
    {
        log.debug("Predictor Job Manager getInstance() issued");

        if (jobManager == null)
        {
            log.debug("Predictor Job Manager instance");
            jobManager = new PredictorJobManager(desc, platformType);
        }

        return jobManager;
    }
    
    /**
     * Gets configuration properties from 'service.properties' on the class path.
     */
    private void getConfigs()
    {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("service.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
            return;
        }
        
        try {
            maxRunningNonStreamingJobs = Integer.parseInt(prop.getProperty("maxRunningJobs"));
        } catch (Exception ex) {
            log.error("Error getting and parsing 'maxRunningJobs' parameter from service.properties. " + ex.getMessage(), ex);
            return;
        }
        log.debug("Max running non-streaming jobs:  " + maxRunningNonStreamingJobs);
        /*   NOT SUPPORTED BY THIS SERVICE
        try {
            maxRunningStreamingJobs = Integer.parseInt(prop.getProperty("maxRunningStreamingJobs"));
        } catch (Exception ex) {
            log.error("Error getting and parsing 'maxRunningStreamingJobs' parameter from service.properties. " + ex.getMessage(), ex);
            return;
        }
        log.debug("Max running streaming jobs:  " + maxRunningStreamingJobs);
        */
    }
    
    /**
     * Stops the Predictor Job Manager by setting the run boolean to false, which
     * means the run loop needs to finish processing before the manager has properly
     * stopped.
     */
    @Override
    public void stopJobManager()
    {
        log.info("Stopping the Template PredictorJobManager - may take a moment if there are any running jobs to clean up");
        
        try {
            stopAllRunningJobs();
        } catch (Exception ex) {
            log.error("Exception caught when trying to stop running jobs: " + ex.getMessage());
        }
    }
    
    @Override
    public JobDetails createNewJob(PredictorServiceJobConfig jobConfig)
    {
        JobDetails jobDetails = null;
        
        // create job id
        UUID uuid = null;
        do {
            uuid = UUID.randomUUID();
        } while (jobConfigMap.containsKey(uuid.toString()));
        
        // validate configuration object
        try {
            if (isJobConfigValid(jobConfig))
            {
                log.info("Created job with reference: " + uuid.toString());
                jobDetails = new JobDetails(uuid.toString(), new JobStatus(JobStatusType.READY), jobConfig.getStartDate());
            }
            else
            {
                log.error("Failed to create job since the configuration object could not be validated");
                jobDetails = new JobDetails(uuid.toString(), new JobStatus(JobStatusType.FAILED, "Could not validate the configuration object"), jobConfig.getStartDate());
            }
        } catch (Exception ex) {
            log.error("Failed to create job since the configuration object could not be validated");
            jobDetails = new JobDetails(uuid.toString(), new JobStatus(JobStatusType.FAILED, "Could not validate the configuration object: " + ex.getMessage()), jobConfig.getStartDate());
        }
        
        jobConfigMap.put(uuid.toString(), jobConfig);
        jobDetailsMap.put(uuid.toString(), jobDetails);
        
        return jobDetails;
    }

    /**
     * This method will place the job in a queue to be automatically started when 
     * the service has the capacity to do so in the event it is already at it's 
     * maximum capacity.
     * @param jobRef
     * @return
     * @throws Exception 
     */
    @Override
    public JobStatus startJob (String jobRef) throws Exception
    {
        if (!jobConfigMap.containsKey(jobRef) || !jobDetailsMap.containsKey(jobRef))
            throw new RuntimeException ("There is no such record of job with ID: " + jobRef);
        
        if (getSyncJobStatus(jobRef).getStatus() != JobStatusType.READY)
            throw new RuntimeException ("Cannot start the job because of the status from creating it is " + getSyncJobStatus(jobRef).getStatus() + " (" + jobRef + ")");
        
        if (jobConfigMap.get(jobRef).isStreaming())
        {
            log.debug("Adding job to streaming queue (" + jobRef + ")");
            streamingJobQueue.add(jobRef);
            log.debug("Streaming queue size: " + streamingJobQueue.size());
            setJobStatus(jobRef, new JobStatus(JobStatusType.QUEUED));
            
            startJobFromQueue(true);
        }
        else
        {
            log.debug("Adding job to non-streaming queue (" + jobRef + ")");
            jobQueue.add(jobRef);
            log.debug("Non-streaming queue size: " + jobQueue.size());
            setJobStatus(jobRef, new JobStatus(JobStatusType.QUEUED));
            
            startJobFromQueue(false);
        }
        
        return getSyncJobStatus(jobRef);
    }
    
    /**
     * Attempts to start the first job in the queue.
     * @return
     * @throws Exception 
     */
    public synchronized void startJobFromQueue (boolean isStreaming) throws Exception
    {
        String jobRef = null;
        
        if (isStreaming)
        {
            if (streamingJobQueue.isEmpty() || (streamingJobsBeingEvaluated.size() >= maxRunningStreamingJobs))
                return;
            
            jobRef = streamingJobQueue.poll();
            log.info("Starting streaming job with ID: " + jobRef);
        }
        else
        {
            if (jobQueue.isEmpty() || (jobsBeingEvaluated.size() >= maxRunningNonStreamingJobs))
                return;
            
            jobRef = jobQueue.poll();
            log.info("Starting non-streaming job with ID: " + jobRef);
        }
        
        if (!jobDetailsMap.containsKey(jobRef) || !jobConfigMap.containsKey(jobRef))
        {
            log.error("There is no such record of job with ID: " + jobRef);
            throw new RuntimeException ("There is no such record of job with ID: " + jobRef);
        }
        
        IEvaluator evaluator = null;
        try {
            evaluator = new CMEvaluator();
            evaluator.setEvaluationListener(resultListener);
            evaluator.startJob(jobRef, jobConfigMap.get(jobRef));
        } catch (Throwable ex) {
            setJobStatus(jobRef, new JobStatus(JobStatusType.ERROR, ex.getMessage()));
            log.error("Failed to start a job: " + ex.getMessage(), ex);
            throw new RuntimeException("Failed to start a job: " + ex.getMessage(), ex);
        }
        
        setJobStatus(jobRef, new JobStatus(JobStatusType.EVALUATING));
        evaluatorMap.put(jobRef, evaluator);
        
        if (isStreaming)
        {
            streamingJobsBeingEvaluated.add(jobRef);
        }
        else
        {
            jobsBeingEvaluated.add(jobRef);
        }
    }
    
    @Override
    public List<JobDetails> getJobs()
    {
        List<JobDetails> jobs = new ArrayList<JobDetails>();
        for (String jobRef : jobDetailsMap.keySet())
            jobs.add(jobDetailsMap.get(jobRef));
        return jobs;
    }

    @Override
    public JobDetails getJobDetails(String jobRef) throws Exception
    {
        if (!jobDetailsMap.containsKey(jobRef))
            throw new RuntimeException("There is no record of the job with reference: " + jobRef);
        
        return jobDetailsMap.get(jobRef);
    }

    @Override
    public JobStatus getJobStatus(String jobRef) throws Exception
    {
        if (!jobDetailsMap.containsKey(jobRef))
            throw new RuntimeException("There is no record of the job with reference: " + jobRef);
        
        return getSyncJobStatus(jobRef);
    }
    
    @Override
    public JobStatus cancelJob(String jobRef) throws Exception
    {
        if (jobRef == null)
            return new JobStatus(JobStatusType.ERROR, "Invalid job refence (null)");
        
        if (!jobDetailsMap.containsKey(jobRef) || !jobConfigMap.containsKey(jobRef))
            return new JobStatus(JobStatusType.ERROR, "Invalid job refence (no such job exists)");
        
        JobStatus jobStatus = null;
        boolean streaming = jobConfigMap.get(jobRef).isStreaming();
        String eeServiceURI = jobConfigMap.get(jobRef).getEvaluationEngineServiceURI().toString();
        if (streaming)
        {
            if (streamingJobQueue.contains(jobRef))
            {
                log.debug("Removing streaming job from job queue");
                streamingJobQueue.remove(jobRef);
                jobConfigMap.remove(jobRef);

                jobStatus = new JobStatus(JobStatusType.CANCELLED, "Successfully cancelled by request - the job was still in the queue");
            }
            else if (streamingJobsBeingEvaluated.contains(jobRef))
            {
                log.debug("Streaming job running, so cancelling and removing streaming job from evaluator map etc");
                evaluatorMap.get(jobRef).cancelJob();
                evaluatorMap.remove(jobRef);
                streamingJobsBeingEvaluated.remove(jobRef);
                jobConfigMap.remove(jobRef);

                jobStatus = new JobStatus(JobStatusType.CANCELLED, "Successfully cancelled by request - the job was being evaluated");
            }
            else if (jobResultMap.containsKey(jobRef))
            {
                jobResultMap.remove(jobRef);
                jobConfigMap.remove(jobRef);

                jobStatus = new JobStatus(JobStatusType.CANCELLED, "Successfully cancelled by request - the job was finished");
            }
        }
        else // non-streaming job
        {
            if (jobQueue.contains(jobRef))
            {
                jobQueue.remove(jobRef);
                jobConfigMap.remove(jobRef);
                
                jobStatus = new JobStatus(JobStatusType.CANCELLED, "Successfully cancelled by request - the job was still in the queue");
            }
            else if (jobsBeingEvaluated.contains(jobRef))
            {
                evaluatorMap.get(jobRef).cancelJob();
                evaluatorMap.remove(jobRef);
                jobsBeingEvaluated.remove(jobRef);
                jobConfigMap.remove(jobRef);

                jobStatus = new JobStatus(JobStatusType.CANCELLED, "Successfully cancelled by request - the job was being evaluated");
            }
            else if (jobResultMap.containsKey(jobRef))
            {
                jobResultMap.remove(jobRef);
                jobConfigMap.remove(jobRef);

                jobStatus = new JobStatus(JobStatusType.CANCELLED, "Successfully cancelled by request - the job was finished");
            }
        }
        
        if (jobStatus == null)
        {
            log.error("JobStatus not set, so job not in any of the collections?! Setting to cancelled..");
            jobStatus = new JobStatus(JobStatusType.CANCELLED, "Successfully cancelled by request");
        }
        
        setJobStatus(jobRef, jobStatus);
        log.info("Cancelled job with reference: " + jobRef);
        notifyEvaluationEngineOfJobUpdate(eeServiceURI, jobRef, this.getJobStatus(jobRef));
        
        // STARTING NEW JOB IF ANY IN QUEUE
        if (streaming) // was a streaming job, so check to start a new streaming job
        {
            // if jobs in queue, start
            if (!streamingJobQueue.isEmpty())
            {
                boolean error = false;
                do {
                    error = false;
                    try {
                        log.info("Starting next streaming job from the queue");
                        startJobFromQueue(true); // start streaming job
                    } catch (Exception ex) {
                        log.error("Unable to start the job: " + ex.getMessage(), ex);
                        error = true;
                    }
                } while (error);
            }
        }
        else // was a non-streaming job, so check to start a new non-streaming job
        {
            // if jobs in queue, start
            if (!jobQueue.isEmpty())
            {
                boolean error = false;
                do {
                    error = false;
                    try {
                        log.info("Starting next non-streaming job from the queue");
                        startJobFromQueue(false); // start non-streaming job
                    } catch (Exception ex) {
                        log.error("Unable to start the job: " + ex.getMessage(), ex);
                        error = true;
                    }
                } while (error);
            }
        }
        
        return jobStatus;
    }

    @Override
    public EvaluationResult getEvaluationResult(String jobRef) throws Exception
    {
        if (!jobResultMap.containsKey(jobRef))
        {
            String errorMsg = "There are no evaluation results for the job: " + jobRef + "\n";
            
            if (!jobDetailsMap.containsKey(jobRef))
                errorMsg += "There is no record of this job either!";
            else
                errorMsg += "The status of the job is: " + getSyncJobStatus(jobRef);
            
            throw new RuntimeException(errorMsg);
        }
        
        log.info("Returning Evaluation Results for job: " + jobRef);
        return jobResultMap.get(jobRef); // OBS: the results are still kept in the map
    }
    
    @Override
    public int getNumJobsBeingEvaluated()
    {
        return jobsBeingEvaluated.size();
    }
    
    @Override
    public int getNumQueuedJobs()
    {
        return jobQueue.size() + streamingJobQueue.size();
    }
    
    /**
     * Check if the job configuration is valid. Always returns true in the
     * current implementation.
     *
     * @param jobConfig The job configuration object.
     * @return True if the configuration object is valid; false otherwise.
     */
    private boolean isJobConfigValid(PredictorServiceJobConfig jobConfig) {
        log.debug("Starting to validate the input job config");
        if (jobConfig == null) {
            throw new IllegalArgumentException("The configuration object provided is NULL - so obviously invalid!");
        }
        
        if (jobConfig.getCommunityDetails() == null) {
            throw new IllegalArgumentException("The community object in the job config is NULL");
        }
        
        if ((jobConfig.getEvents() == null) || jobConfig.getEvents().isEmpty()) {
            throw new IllegalArgumentException("The job config has a serious lack of events... completely... no events given...");
        }
        
        if (jobConfig.getStartDate() == null) {
            throw new IllegalArgumentException("The start date in the job config is NULL");
        }
        
        if (jobConfig.getForecastPeriod() == null) {
            throw new IllegalArgumentException("The forecast period parameter in the job config is NULL");
        }
        
        if (jobConfig.isStreaming()) {
            throw new IllegalArgumentException("No streaming jobs accepted by this service - sorry!");
        }
        
        if (jobConfig.getEvaluationEngineServiceURI() == null) {
            throw new IllegalArgumentException("The Evaluation Engine Service URI is NULL");
        }
        
        // TODO: validate event config according to the 
        
        log.info("The description of the events");
        try {
            if (jobConfig.getEvents().isEmpty()) {
                new JobStatus(JobStatusType.ERROR, "No event defined");
            }

            log.info("PredictorServiceJobConfig details:");
            log.info("  Community details:");
            log.info("    Name: " + jobConfig.getCommunityDetails().getCommunityName());
            log.info("    ID:   " + jobConfig.getCommunityDetails().getCommunityID());
            log.info("  Num events: " + jobConfig.getEvents().size());

            if ((desc.getEvents() != null) && !desc.getEvents().isEmpty()) {
            } else {
                log.error("No events gotten from the Description");
                return false;
            }
        } catch (Exception e) {
            log.error("Caught an exception when validating configuration object: " + e.getMessage(), e);
            return false;
        }
        try {
            return ConfigCheck.checkJobValid(jobConfig, desc);
        } catch (Exception e) {
            log.error("Error in validation the job configuration", e);
            return false;
        }
    }

    /**
     * Validates the EvaluationResult.
     *
     * @param evalRes EvaluationResult object.
     * @return True if the EvaluationResult object is valid; false otherwise.
     */
    private boolean isEvaluationResultValid(EvaluationResult evalRes) {
        if (evalRes == null) {
            log.error("The EvaluationResult object is NULL");
            return false;
        }

        if ((evalRes.getResultItems() == null) || evalRes.getResultItems().isEmpty()) {
            log.error("There are no result items in the EvaluationResult object");
            return false;
        }

        if (evalRes.getForecastDate() == null) {
            log.error("The forecast date in the EvaluationResult object is NULL");
            return false;
        }

        return true;
    }
    
    /**
     * @return the jobConfigMap
     */
    public Map<String, PredictorServiceJobConfig> getJobConfigMap()
    {
        return jobConfigMap;
    }

    /**
     * @param jobConfigMap the jobConfigMap to set
     */
    public void setJobConfigMap(Map<String, PredictorServiceJobConfig> jobConfigMap)
    {
        this.jobConfigMap = jobConfigMap;
    }

    /**
     * @return the jobStatusMap
     */
    public Map<String, JobDetails> getJobDetailsMap()
    {
        return jobDetailsMap;
    }

    /**
     * @param jobStatusMap the jobStatusMap to set
     */
    public void setJobDetailsMap(Map<String, JobDetails> jobDetailsMap)
    {
        this.jobDetailsMap = jobDetailsMap;
    }

    /**
     * @return the jobResultMap
     */
    public Map<String, EvaluationResult> getJobResultMap()
    {
        return jobResultMap;
    }

    /**
     * @param jobResultMap the jobResultMap to set
     */
    public void setJobResultMap(Map<String, EvaluationResult> jobResultMap)
    {
        this.jobResultMap = jobResultMap;
    }
    
    /**
     * Synchronised access to setting the job status of the JobDetails object in 
     * the jobDetailsMap.
     * @param jobRef
     * @param status 
     */
    private synchronized void setJobStatus(String jobRef, JobStatus status) throws Exception
    {
        if (jobDetailsMap.containsKey(jobRef))
        {
            jobDetailsMap.get(jobRef).getJobStatus().setStatus(status.getStatus());
            jobDetailsMap.get(jobRef).getJobStatus().setMetaData(status.getMetaData());
        }
    }
    
    /**
     * Synchronised access to getting the job status of the JobDetails object in 
     * the jobDetailsMap.
     * @param jobRef
     * @return 
     */
    private synchronized JobStatus getSyncJobStatus(String jobRef) throws Exception
    {
        if (!jobDetailsMap.containsKey(jobRef))
            throw new RuntimeException("There is no record of the job with reference: " + jobRef);
        
        return jobDetailsMap.get(jobRef).getJobStatus();
    }
    
    private void stopAllRunningJobs()
    {
        if (!evaluatorMap.isEmpty())
        {
            log.info("Cancelling " + evaluatorMap.size() + " evaluation jobs running");
            for (String jobRef : evaluatorMap.keySet())
            {
                log.debug("Cancelling job: " + jobRef);
                IEvaluator evaluator = evaluatorMap.get(jobRef);
                evaluator.cancelJob();
            }
        }
        else
        {
            log.debug("No evaluation jobs to stop");
        }
    }
    
    /**
     * Will make a call to the Evaluation Engine Service to notify of a 
     * job update.
     *
     * @param eeServiceURI The URI of the Evaluation Engine Service.
     * @param jobRef The job reference.
     * @param jobStatus The job status.
     */
    private void notifyEvaluationEngineOfJobUpdate(String eeServiceURI, String jobRef, JobStatus jobStatus)
    {
        if (eeServiceURI == null) {
            return;
        }

        try {
            log.debug("Notifying Evaluation Engine Service of job update");
            EvaluationEngineClient evaluationEngineClient = new EvaluationEngineClient(eeServiceURI);
            log.debug("Created client ok... now sending the job update");
            evaluationEngineClient.updateEvaluationJobStatus(jobRef, jobStatus);
        } catch (Throwable ex) {
            log.error("Failed to notify the Evaluation Engine Service of the job update: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        try { log.debug("Cancelling any running jobs - finalize method"); } catch (Throwable t) {}
        stopAllRunningJobs();
        super.finalize();
    }
    
    /**
     * A class that listens to new results from the evaluation jobs, which will
     * update the job management collections and notify the evaluation engine
     * of any new results.
     */
    protected class EvaluationListener implements IEvaluationListener
    {
        @Override
        public void onNewResult(String jobRef, EvaluationResult result)
        {
            log.info("Got a result notification");
            
            // check that the job hasn't been cancelled meanwhile
            JobStatusType jobStatus = null;
            try {
                jobStatus = getSyncJobStatus(jobRef).getStatus();
            } catch (Exception ex) {
                log.error("Unable to get the status of the jobs, so we can assume something has gone wrong! " + ex.getMessage(), ex);
                return;
            }
            
            if ((jobStatus == JobStatusType.CANCELLED) || (jobStatus == JobStatusType.ERROR) || (jobStatus == JobStatusType.FAILED))
            {
                log.debug("The job has since been cancelled or there was an error, so not processing the result");
                return;
            }

            if (isEvaluationResultValid(result))
            {
                jobResultMap.put(jobRef, result);
                result.setJobDetails(jobDetailsMap.get(jobRef));
                try {
                    // TODO: if streaming, perhaps need to change the status to 
                    // indicate that there's a result available. Otherwise it will
                    // be stuck at 'EVALUATING' forever and the clients will not
                    // know there's a result available
                    setJobStatus(jobRef, evaluatorMap.get(jobRef).getJobStatus());
                } catch (Exception ex) {
                    log.debug("Caught an exception when trying to set the job status for job: " + jobRef, ex);
                }
                
                log.debug("Notifying the evaluation engine of the new result");
                String eeServiceURI = jobConfigMap.get(jobRef).getEvaluationEngineServiceURI().toString();
                notifyEvaluationEngineOfNewResult(eeServiceURI, jobRef, result);
            }
            else
            {
                log.info("The evaluation result object was not valid");
                try {
                    setJobStatus(jobRef, new JobStatus(JobStatusType.ERROR, "The EvaluationResult could not be validated. See service log for details."));
                } catch (Exception ex) {
                    log.debug("Caught an exception when trying to set the job status for job: " + jobRef, ex);
                }
            }

            if (!jobConfigMap.get(jobRef).isStreaming())
            {
                log.debug("Removing non-streaming job from the maps of jobs being evaluated and the evaluator object");
                jobsBeingEvaluated.remove(jobRef);
                evaluatorMap.remove(jobRef);
                // OBS: keeping the job config and job status still
                
                // if jobs in queue, start a new job
                if (!jobQueue.isEmpty())
                {
                    boolean error = false;
                    do {
                        error = false;
                        try {
                            log.info("Starting next non-streaming job from the queue");
                            startJobFromQueue(false); // start non-streaming job
                        } catch (Exception ex) {
                            log.error("Unable to start the job: " + ex.getMessage(), ex);
                            error = true;
                        }
                    } while (error);
                }
            }
        }
        
        @Override
        public void onError(String jobRef, JobStatus jobStatus)
        {
            log.info("Got an error notification: " + jobStatus.getMetaData());
            try {
                setJobStatus(jobRef, jobStatus);
            } catch (Exception ex) {
                log.debug("Caught an exception when trying to set the job status for job: " + jobRef, ex);
            }
            
            log.debug("Removing job from the maps of jobs being evaluated and the evaluator object");
            if (jobConfigMap.get(jobRef).isStreaming())
            {
                streamingJobsBeingEvaluated.remove(jobRef);
            }
            else
            {
                jobsBeingEvaluated.remove(jobRef);
            }
            evaluatorMap.remove(jobRef);
            
            // OBS: keeping the job config and job status still
            String eeServiceURI = jobConfigMap.get(jobRef).getEvaluationEngineServiceURI().toString();
            notifyEvaluationEngineOfJobUpdate(eeServiceURI, jobRef, jobStatus);
            
            // STARTING A NEW JOB
            if (jobConfigMap.get(jobRef).isStreaming())
            {
                // if jobs in queue, start
                if (!streamingJobQueue.isEmpty())
                {
                    boolean error = false;
                    do {
                        error = false;
                        try {
                            log.info("Starting next streaming job from the queue");
                            startJobFromQueue(true); // start streaming job
                        } catch (Exception ex) {
                            log.error("Unable to start the job: " + ex.getMessage(), ex);
                            error = true;
                        }
                    } while (error);
                }
            }
            else
            {
                // if jobs in queue, start
                if (!jobQueue.isEmpty())
                {
                    boolean error = false;
                    do {
                        error = false;
                        try {
                            log.info("Starting next non-streaming job from the queue");
                            startJobFromQueue(false); // start non-streaming job
                        } catch (Exception ex) {
                            log.error("Unable to start the job: " + ex.getMessage(), ex);
                            error = true;
                        }
                    } while (error);
                }
            }
        }

        /**
         * Will make a call to the Evaluation Engine Service to notify of a new
         * evaluation result.
         *
         * @param eeServiceURI The URI of the Evaluation Engine Service.
         * @param jobRef The job reference.
         * @param evalRes The evaluation result.
         */
        private void notifyEvaluationEngineOfNewResult(String eeServiceURI, String jobRef, EvaluationResult evalRes)
        {
            if (eeServiceURI == null) {
                return;
            }

            try {
                log.debug("Notifying Evaluation Engine Service of new evaluation result");
                EvaluationEngineClient evaluationEngineClient = new EvaluationEngineClient(eeServiceURI);
                log.debug("Created client ok... now sending the results");
                evaluationEngineClient.newEvaluationResult(jobRef, evalRes);
            } catch (Throwable ex) {
                log.error("Failed to notify the Evaluation Engine Service of the new result: " + ex.getMessage(), ex);
            }
        }
    }
}
