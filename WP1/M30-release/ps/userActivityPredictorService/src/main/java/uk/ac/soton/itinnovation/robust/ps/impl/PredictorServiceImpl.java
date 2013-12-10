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
//      Created Date :          2012-03-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.ps.impl;

import java.util.List;
import java.util.UUID;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.ps.gs.BoardsieDataExtractor;
import uk.ac.soton.itinnovation.robust.ps.spec.IPredictorJobManager;

@WebService(endpointInterface = "uk.ac.soton.itinnovation.robust.cat.common.ps.IPredictorService")
public class PredictorServiceImpl implements IPredictorService, Lifecycle
{
    private IPredictorJobManager jobManager;
    private PredictorServiceDescription desc;
    
    static Logger log = Logger.getLogger(PredictorServiceImpl.class);
    
    // Data extractor that's used for getting historical user activity data
    // Autowired here because of not being able to do it within the GSBoardsieEvaluator
    // class as that class is not created by Spring itself. This is hopefully a
    // temporary solution, until we have a way to do this within the class it should
    // be done within!
    @Autowired
    BoardsieDataExtractor dataExtractor;
    private boolean dataExtractorSet = false;
    
    /**
     * Default constructor which sets up the job management thread and any other
     * required components.
     */
    public PredictorServiceImpl()
    {
        log.info("User Activity Predictor Service (GS) STARTING UP");
        generatePredictorServiceDescription();
        jobManager = PredictorJobManager.getInstance(desc);
        log.info("User Activity Predictor Service (GS)  STARTED");
    }
    
    /**
     * Stopping the Job Manager (which should clean up any jobs running etc).
     */
    private void stopManager()
    {
        log.info("Stopping Template PredictorJobManager");
        jobManager.stopJobManager();
    }
    
    /**
     * Create the PredictorServiceDescription object, which defines the offerings
     * of the service to any clients.
     */
    private void generatePredictorServiceDescription()
    {
        UUID psdUUID = UUID.fromString("a19b4722-3257-3b7c-c36c-970565ecb552");
        desc = new PredictorServiceDescription(psdUUID, "User Activity Predictor Service (GS)", "1.0", "A Predictor Service uses a Gibbs Sampler to calculate the probability of users dropping in activity");

        // event(s)
        UUID eventUUID = UUID.fromString("e19b4722-4257-4a7c-a36c-970565ecb552");
        Event evt = new Event("User activity drop", "Users activity drop according to some percentage treshold");
        evt.setUuid(eventUUID);
        
        UUID eventCondUUID = UUID.fromString("87084ba9-edac-4e79-b23a-76a14e52730e");
        EventCondition evtCond = new EventCondition(ParameterValueType.FLOAT, "Activity drop Threshold", "", "");
        evtCond.setUUID(eventCondUUID);
        evtCond.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond.addValueConstraint("0.2", ValueConstraintType.DEFAULT);
        evtCond.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond.addValueConstraint("1", ValueConstraintType.MAX);
        //evtCond.addAllowedEvaluationType(EvaluationType.GREATER_OR_EQUAL);

        evt.setEventCondition(evtCond);
        desc.addEvent(evt);

        // size of timewindow - fixed to 1 week
        Parameter forecastPeriod = new Parameter(ParameterValueType.FLOAT, "The forecast period, which specifies the size of the time window", "The parameter that defines the total number of days for each covariate", "weeks");
        forecastPeriod.setValue(new ParameterValue("1", EvaluationType.EQUAL));
        desc.setForecastPeriod(forecastPeriod);

        // number of covariate aka number of snapshots required for prediction
        // The total number of covariate is the same as the total number of observation as Y can be inferred from X
        // and X is going to be acting as covariate
        // Intercept is not included
        // Max is yet to be decided due to other restrictions that may apply i.e. the amount of historical observation currently available
        UUID numCovariatesUUID = UUID.fromString("75302132-65db-4c59-a8f0-d2c4ae702de3");
        Parameter numCovariates = new Parameter(ParameterValueType.INT, "Number of historical snapshots", "The total number of covariates that will be used in the GS", "");
        numCovariates.setUUID(numCovariatesUUID);
        numCovariates.addValueConstraint("12", ValueConstraintType.DEFAULT);
        numCovariates.addValueConstraint("3", ValueConstraintType.MIN);
        numCovariates.addValueConstraint("28", ValueConstraintType.MAX);
        numCovariates.addValueConstraint("1", ValueConstraintType.STEP);
        desc.addConfigurationParam(numCovariates);

        // Definition of 'user' being active
        // Sets requirement that all users must have a total activity over the prediction period greater than this threshold to ensure that covariate matrix is not sparse & focus on key users
        // Note: for snapshots where 'users' do not 'exist' they have activity -1.
        // Question: Is there any way to set the max to the max within the database - thinking not.
        UUID activeThresholdUUID = UUID.fromString("0dce1066-5c85-4fd0-a115-a5d66ddda6b7");
        Parameter activeThreshold = new Parameter(ParameterValueType.INT, "Activity filter threshold", "The minimum number of posts a user should have posted in the prediction period to be included in the population", "posts");
        activeThreshold.setUUID(activeThresholdUUID);
        activeThreshold.addValueConstraint("0", ValueConstraintType.DEFAULT);
        activeThreshold.addValueConstraint("0", ValueConstraintType.MIN);
        activeThreshold.addValueConstraint("100", ValueConstraintType.MAX);
        desc.addConfigurationParam(activeThreshold);
    }
    
    @Override
    public PredictorServiceDescription getPredictorServiceDescription()
    {
        log.debug("Incoming request for PredictorServiceDescription");
        return desc;
    }

    @Override
    public List<JobDetails> getJobs()
    {
        log.debug("Incoming request to get details of jobs on service");
        return jobManager.getJobs();
    }

    @Override
    public JobDetails getJobDetails(String jobRef)
    {
        log.debug("Incoming request for job details for reference: " + jobRef);
        try {
            return jobManager.getJobDetails(jobRef);
        } catch (Exception e) {
            log.error("No such job with reference: " + jobRef);
            return new JobDetails(jobRef, new JobStatus(JobStatusType.ERROR, e.getMessage()), null);
        }
    }

    @Override
    public JobDetails createEvaluationJob(PredictorServiceJobConfig config)
    {
        // setting the data extractor at this point because it will not have
        // been autowired by the time the constructor of this class
        // is called
        if (!dataExtractorSet)
        {
            log.info("Setting the boardsie data extractor");
            if (dataExtractor == null) {
                log.info("It is still NULL though, so let's see how this goes...");
            }
            ((PredictorJobManager)jobManager).setBoardsieDataExtractor(dataExtractor);
        }
        
        log.debug("Incoming request for creating a new evaluation job");
        return jobManager.createNewJob(config);
    }

    @Override
    public JobStatus evaluate(String jobRef)
    {
        log.debug("Incoming request to start job with reference: " + jobRef);
        try {
            return jobManager.startJob(jobRef);
        } catch (Exception e) {
            return new JobStatus(JobStatusType.ERROR, e.getMessage());
        }
    }

    @Override
    public JobStatus getJobStatus(String jobRef)
    {
        log.debug("Incoming request to get job status for job with reference: " + jobRef);
        try {
            return jobManager.getJobStatus(jobRef);
        } catch (Exception e) {
            return new JobStatus(JobStatusType.ERROR, e.getMessage());
        }
    }

    @Override
    public EvaluationResult getEvaluationResult(String jobRef)
    {
        log.debug("Incoming request to get evaluation results for job with reference: " + jobRef);
        try {
            return jobManager.getEvaluationResult(jobRef);
        } catch (Exception e) {
            return new EvaluationResult(new JobDetails(jobRef, new JobStatus(JobStatusType.ERROR, e.getMessage()), null), null, null, null);
        }
    }

    @Override
    public JobStatus cancelJob(String jobRef)
    {
        log.debug("Incoming request to cancel a job with reference: " + jobRef);
        try {
            return jobManager.cancelJob(jobRef);
        } catch (Exception e) {
            log.error("Caught an exception when trying to cancel the job: " + e.getMessage(), e);
            return new JobStatus(JobStatusType.ERROR, e.getMessage());
        }
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        stopManager();
        super.finalize();
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
        stopManager();
    }

    @Override
    public boolean isRunning()
    {
        log.debug("Lifecycle.isRunning() called");
        
        return true;
    }
}
