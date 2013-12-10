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

import java.util.*;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValidationObject;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.sim.ws.spec.ISimulationListener;
import uk.ac.soton.itinnovation.robust.sim.ws.spec.ISimulationWrapper;
import uk.ac.soton.itinnovation.robust.sim.ws.spec.ISimulationJobManager;

/**
 * This simple Job Manager class manages the evaluation jobs in memory and does not
 * store the state, so if the predictor service goes down, all evaluation jobs
 * will be lost.
 * 
 * @author Vegard Engen
 */
public class SimulationJobManager implements ISimulationJobManager
{
    // singleton instance of the job manager
    private static SimulationJobManager jobManager = null;
    
    // description object used for general validation of config objects
    private SimulationServiceDescription simDesc;
    
    // collectings for managing the evaluation jobs
    private Map<String, SimulationServiceJobConfig> jobConfigMap; // configuration for all jobs (key = job ref)
    private Map<String, JobDetails> jobDetailsMap;                // map of all jobs (job ref = key), whether queued, evaluating or finished
    private Map<String, SimulationResult> jobResultMap;           // simulation results saved for each job reference (key)
    private Queue<String> jobQueue;                               // references to jobs that have been asked to be started
    private Set<String> jobsBeingEvaluated;                       // the job reference of jobs that are being evaluated
    private Map<String, ISimulationWrapper> evaluatorMap;                 // map of the evaluator instance(s) for each job (job ref = key)
    
    // the maximum number of consecutively running jobs
    private int maxRunningJobs;
    
    // listener for the PredictorJobManager when starting evaluation jobs to get
    // notifications of new results or errors that may occur.
    private SimulationListener resultListener;
    
    static Logger logger = Logger.getLogger(SimulationJobManager.class);

    /**
     * Default constructor, which initialises the maps used to manage the
     * evaluation jobs.
     * @param desc Simulation Service Description object, needed for doing general
     *            validation of configuration objects.
     */
    private SimulationJobManager(SimulationServiceDescription desc)
    {
        this.simDesc = desc;
        
        this.jobConfigMap = Collections.synchronizedMap(new HashMap<String, SimulationServiceJobConfig>());
        this.jobDetailsMap = Collections.synchronizedMap(new HashMap<String, JobDetails>());
        this.jobResultMap = Collections.synchronizedMap(new HashMap<String, SimulationResult>());
        this.jobQueue = new LinkedList<String>();
        this.jobsBeingEvaluated = Collections.synchronizedSet(new HashSet<String>());
        this.evaluatorMap = Collections.synchronizedMap(new HashMap<String, ISimulationWrapper>());
        
        this.resultListener = this.new SimulationListener();
        this.maxRunningJobs = 2; // by default, supporting running two jobs at the same time
        
        logger.debug("Getting configuration parameters");
        getConfigs();
    }
    
    /**
     * Returns an instance of the Predictor Job Manager.
     * @param desc Simulation Service Description object, needed for doing general
     *            validation of configuration objects.
     * @return
     * @throws Exception 
     */
    public static ISimulationJobManager getInstance(SimulationServiceDescription desc)
    {
        logger.debug("Predictor Job Manager getInstance() issued");

        if (jobManager == null)
        {
            logger.debug("Predictor Job Manager instance");
            jobManager = new SimulationJobManager(desc);
        }

        return jobManager;
    }
    
    /**
     * Gets configuration properties from 'catSimService.properties' on the class path.
     */
    private void getConfigs()
    {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("catSimService.properties"));
        } catch (Exception ex) {
            logger.error("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
            return;
        }
        
        try {
            maxRunningJobs = Integer.parseInt(prop.getProperty("maxRunningJobs"));
        } catch (Exception ex) {
            logger.error("Error getting and parsing 'maxRunningJobs' parameter from catSimService.properties. " + ex.getMessage(), ex);
            return;
        }
        logger.debug("Max running jobs:  " + maxRunningJobs);
    }
    
    /**
     * Stops the Predictor Job Manager by setting the run boolean to false, which
     * means the run loop needs to finish processing before the manager has properly
     * stopped.
     */
    @Override
    public void stopJobManager()
    {
        logger.info("Stopping the Simulation Job Manager - may take a moment if there are any running jobs to clean up");
        
        try {
            stopAllRunningJobs();
        } catch (Exception ex) {
            logger.error("Exception caught when trying to stop running jobs: " + ex.getMessage());
        }
    }
    
    @Override
    public JobDetails createNewJob(SimulationServiceJobConfig jobConfig)
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
                logger.info("Created job with reference: " + uuid.toString());
                jobDetails = new JobDetails(uuid.toString(), new JobStatus(JobStatusType.READY), jobConfig.getStartDate());
            }
            else
            {
                logger.error("Failed to create job since the configuration object could not be validated");
                jobDetails = new JobDetails(uuid.toString(), new JobStatus(JobStatusType.FAILED, "Could not validate the configuration object"), jobConfig.getStartDate());
            }
        } catch (Exception ex) {
            logger.error("Failed to create job since the configuration object could not be validated: " + ex.toString(), ex);
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
        if (!jobConfigMap.containsKey(jobRef))
            throw new RuntimeException ("There is no such record of job with ID: " + jobRef);
        
        if (getSyncJobStatus(jobRef).getStatus() != JobStatusType.READY)
            throw new RuntimeException ("Cannot start the job because of the status from creating it is " + getSyncJobStatus(jobRef).getStatus() + " (" + jobRef + ")");
        
		logger.debug("Adding job to queue (" + jobRef + ")");
		jobQueue.add(jobRef);
		logger.debug("Queue size: " + jobQueue.size());
		setJobStatus(jobRef, new JobStatus(JobStatusType.QUEUED));

		startJobFromQueue();
        
        return getSyncJobStatus(jobRef);
    }
    
    /**
     * Attempts to start the first job in the queue.
     * @return
     * @throws Exception 
     */
    public synchronized void startJobFromQueue () throws Exception
    {
        String jobRef = null;
        
		if (jobQueue.isEmpty() || (jobsBeingEvaluated.size() >= maxRunningJobs))
			return;

		jobRef = jobQueue.poll();
		logger.info("Starting job with ID: " + jobRef);
        
        if (!jobDetailsMap.containsKey(jobRef) || !jobConfigMap.containsKey(jobRef))
        {
            logger.error("There is no such record of job with ID: " + jobRef);
            throw new RuntimeException ("There is no such record of job with ID: " + jobRef);
        }
        
        ISimulationWrapper simWrapper = null;
        try {
			simWrapper = new SimulationWrapper(resultListener);
			simWrapper.startJob(jobRef, jobConfigMap.get(jobRef));
        } catch (Throwable ex) {
            setJobStatus(jobRef, new JobStatus(JobStatusType.ERROR, ex.getMessage()));
            logger.error("Failed to start a job: " + ex.getMessage(), ex);
            throw new RuntimeException("Failed to start a job: " + ex.getMessage(), ex);
        }
        
        setJobStatus(jobRef, new JobStatus(JobStatusType.EVALUATING));
        evaluatorMap.put(jobRef, simWrapper);
        jobsBeingEvaluated.add(jobRef);
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
        
        if (jobStatus == null)
        {
            logger.error("JobStatus not set, so job not in any of the collections?! Setting to cancelled..");
            jobStatus = new JobStatus(JobStatusType.CANCELLED, "Successfully cancelled by request");
        }
        
        setJobStatus(jobRef, jobStatus);

        // STARTING NEW JOB IF ANY IN QUEUE
		if (!jobQueue.isEmpty())
		{
			boolean error = false;
			do {
				error = false;
				try {
					logger.info("Starting next job from the queue");
					startJobFromQueue();
				} catch (Exception ex) {
					logger.error("Unable to start the job: " + ex.getMessage(), ex);
					error = true;
				}
			} while (error && !jobQueue.isEmpty());
		}
        
        return jobStatus;
    }

    @Override
    public SimulationResult getResult(String jobRef) throws Exception
    {
        if (!jobResultMap.containsKey(jobRef))
        {
            String errorMsg = "There are no simulation results for the job: " + jobRef + "\n";
            
            if (!jobDetailsMap.containsKey(jobRef))
                errorMsg += "There is no record of this job either!";
            else
                errorMsg += "The status of the job is: " + getSyncJobStatus(jobRef);
            
            throw new RuntimeException(errorMsg);
        }
        
        logger.info("Returning Simulation Result for job: " + jobRef);
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
        return jobQueue.size();
    }
    
    /**
     * Check if the job configuration is valid.
     * Always returns true in the current implementation.
     * @param jobConfig The job configuration object.
     * @return True if the configuration object is valid; otherwise an exception with a meaningful message to return.
     */
    private boolean isJobConfigValid(SimulationServiceJobConfig jobConfig)
    {
		
		if (jobConfig == null) {
            throw new IllegalArgumentException("The job config object was NULL");
        }
		
		if (jobConfig.getCommunityDetails() == null) {
            throw new IllegalArgumentException("The Community Details object was NULL");
        }
		
		if (jobConfig.getCommunityDetails().getCommunityID() == null) {
            throw new IllegalArgumentException("The community ID was NULL");
        }
		
		if (jobConfig.getCommunityDetails().getCommunityName() == null) {
            throw new IllegalArgumentException("The community name was NULL");
        }
		
		if (jobConfig.getStartDate() == null) {
            throw new IllegalArgumentException("The simulation start date was NULL");
        }
		
		if (jobConfig.getEndDate() == null) {
            throw new IllegalArgumentException("The simulation end date was NULL");
        }
		
		if (jobConfig.getConfigurationParameters() == null) {
            throw new IllegalArgumentException("The configuration parameters were NULL");
        }
		
		if (jobConfig.getConfigurationParameters().isEmpty()) {
            throw new IllegalArgumentException("There were no configuration parameters");
        }
		
		logger.debug("Simulation job configuration:");
		logger.debug(" * Community Details:");
		logger.debug("   - " + jobConfig.getCommunityDetails().getCommunityID());
		logger.debug("   - " + jobConfig.getCommunityDetails().getCommunityName());
		logger.debug(" * Simulation dates:");
		logger.debug("   - Start: " + DateUtil.getDateString(jobConfig.getStartDate()));
		logger.debug("   - End:   " + DateUtil.getDateString(jobConfig.getEndDate()));
		logger.debug(" * Configuration parameters:");
		for (Parameter p : jobConfig.getConfigurationParameters())
		{
			if (p == null) {
				throw new IllegalArgumentException("Got a parameter that was NULL");
			} else if (p.getName() == null) {
				throw new IllegalArgumentException("Got a parameter whose name was NULL");
			} else if ((p.getValue() == null) || (p.getValue().getValue() == null)) {
				throw new IllegalArgumentException("No value set for parameter: " + p.getName());
			}
			
			logger.debug("   - " + p.getName() + ": " + p.getValue().getValue());
		}
        
        return true;
    }
    
    /**
     * Validates the SimulationResult.
     * @param res SimulationResult object.
     * @return True if the SimulationResult object is valid; false otherwise.
     */
    private ValidationObject isResultValid(SimulationResult res)
    {
        if (res == null) {
            return new ValidationObject(false, "The SimulationResult object is NULL");
        }
        
        if (res.getJobDetails() == null) {
            return new ValidationObject(false, "The SimulationResult JobDetails object is NULL");
		}
		
		if (res.getSimulationDate() == null) {
            return new ValidationObject(false, "The SimulationResult Simulation Date object is NULL");
		}
		
		if (res.getResultGroups() == null) {
            return new ValidationObject(false, "The SimulationResult ResultGroups is NULL");
		}
		
        if (res.getResultGroups().isEmpty()) {
            return new ValidationObject(false, "The SimulationResult has no ResultGroups");
		}
		
		for (ResultGroup rg : res.getResultGroups())
		{
			if (rg == null) {
				return new ValidationObject(false, "The SimulationResult has a ResultGroup that was NULL");
			}
			
			if (rg.getName() == null) {
				return new ValidationObject(false, "The SimulationResult has a ResultGroup with no name");
			}
			
			if ((rg.getResults() == null) || (rg.getResults().isEmpty())) {
				return new ValidationObject(false, "The SimulationResult has a ResultGroup with no results");
			}
		}
		
        return new ValidationObject(true);
    }
    
    /**
     * @return the jobConfigMap
     */
    public Map<String, SimulationServiceJobConfig> getJobConfigMap()
    {
        return jobConfigMap;
    }

    /**
     * @param jobConfigMap the jobConfigMap to set
     */
    public void setJobConfigMap(Map<String, SimulationServiceJobConfig> jobConfigMap)
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
    public Map<String, SimulationResult> getJobResultMap()
    {
        return jobResultMap;
    }

    /**
     * @param jobResultMap the jobResultMap to set
     */
    public void setJobResultMap(Map<String, SimulationResult> jobResultMap)
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
            logger.info("Cancelling " + evaluatorMap.size() + " simulation jobs running");
            for (String jobRef : evaluatorMap.keySet())
            {
                logger.debug("Cancelling job: " + jobRef);
                ISimulationWrapper evaluator = evaluatorMap.get(jobRef);
                evaluator.cancelJob();
            }
        }
        else
        {
            logger.debug("No simulation jobs to stop");
        }
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        logger.debug("Cancelling any running jobs");
        stopAllRunningJobs();
        super.finalize();
    }
    
    /**
     * A class that listens to new results from the evaluation jobs, which will
     * update the job management collections and notify the evaluation engine
     * of any new results.
     */
    protected class SimulationListener implements ISimulationListener
    {
        @Override
        public void onNewResult(String jobRef, SimulationResult result)
        {
            logger.info("Got a result notification");
            
            // check that the job hasn't been cancelled meanwhile
            JobStatusType jobStatus = null;
            try {
                jobStatus = getSyncJobStatus(jobRef).getStatus();
            } catch (Exception ex) {
                logger.error("Unable to get the status of the jobs, so we can assume something has gone wrong! " + ex.getMessage(), ex);
                return;
            }
            
            if ((jobStatus == JobStatusType.CANCELLED) || (jobStatus == JobStatusType.ERROR) || (jobStatus == JobStatusType.FAILED))
            {
                logger.debug("The job has since been cancelled or there was an error, so not processing the result");
                return;
            }
			
			JobDetails jd = jobDetailsMap.get(jobRef);
			jd.setCompletionDate(new Date());
			result.setJobDetails(jd);

			ValidationObject vo = isResultValid(result);
            if (vo.valid)
            {
                jobResultMap.put(jobRef, result);
                try {
                    setJobStatus(jobRef, evaluatorMap.get(jobRef).getJobStatus());
                } catch (Exception ex) {
                    logger.debug("Caught an exception when trying to set the job status for job: " + jobRef, ex);
                }
            }
            else
            {
                logger.info("The simulation result object was not valid: " + vo.msg);
                try {
                    setJobStatus(jobRef, new JobStatus(JobStatusType.ERROR, "The SimulationResult could not be validated: " + vo.msg));
                } catch (Exception ex) {
                    logger.debug("Caught an exception when trying to set the job status for job: " + jobRef, ex);
                }
            }
			
			jobsBeingEvaluated.remove(jobRef);
            evaluatorMap.remove(jobRef);
			jobConfigMap.remove(jobRef);
            // OBS: keeping the job status still
            
            // STARTING A NEW JOB (if any)
			if (!jobQueue.isEmpty())
			{
				boolean error = false;
				do {
					error = false;
					try {
						logger.info("Starting next job from the queue");
						startJobFromQueue(); 
					} catch (Exception ex) {
						logger.error("Unable to start the job: " + ex.getMessage(), ex);
						error = true;
					}
				} while (error && !jobQueue.isEmpty());
			}
        }
        
        @Override
        public void onError(String jobRef, JobStatus jobStatus)
        {
            logger.info("Got an error notification: " + jobStatus.getMetaData());
            try {
                setJobStatus(jobRef, jobStatus);
            } catch (Exception ex) {
                logger.debug("Caught an exception when trying to set the job status for job: " + jobRef, ex);
            }
            
            logger.debug("Removing job from the map of jobs being evaluated and the simulation object");
            jobsBeingEvaluated.remove(jobRef);
            evaluatorMap.remove(jobRef);
			jobConfigMap.remove(jobRef);
            // OBS: keeping the job status still
            
            // STARTING A NEW JOB (if any)
			if (!jobQueue.isEmpty())
			{
				boolean error = false;
				do {
					error = false;
					try {
						logger.info("Starting next job from the queue");
						startJobFromQueue(); 
					} catch (Exception ex) {
						logger.error("Unable to start the job: " + ex.getMessage(), ex);
						error = true;
					}
				} while (error && !jobQueue.isEmpty());
			}
        }
    }
}
