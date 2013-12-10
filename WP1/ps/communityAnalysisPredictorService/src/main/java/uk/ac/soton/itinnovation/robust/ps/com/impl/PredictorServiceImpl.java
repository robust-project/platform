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
package uk.ac.soton.itinnovation.robust.ps.com.impl;

import java.util.List;
import java.util.UUID;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import org.springframework.context.Lifecycle;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.ps.com.spec.IPredictorJobManager;

@WebService(endpointInterface = "uk.ac.soton.itinnovation.robust.cat.common.ps.IPredictorService")
public class PredictorServiceImpl implements IPredictorService, Lifecycle
{
    private IPredictorJobManager jobManager;
    private PredictorServiceDescription desc;
    
    static Logger log = Logger.getLogger(PredictorServiceImpl.class);
    
    /**
     * Default constructor which sets up the job management thread and any other
     * required components.
     */
    public PredictorServiceImpl()
    {
        log.info("Community Analysis Predictor Service STARTING UP");
        generatePredictorServiceDescription();
        jobManager = PredictorJobManager.getInstance(this.desc);
        log.info("Community Analysis Predictor Service STARTED");
    }
    
    /**
     * Stopping the Job Manager (which should clean up any jobs running etc).
     */
    private void stopManager()
    {
        log.info("Stopping Community Analysis PredictorJobManager");
        jobManager.stopJobManager();
    }
    
    /**
     * Create the PredictorServiceDescription object, which defines the offerings
     * of the service to any clients.
     */
    private void generatePredictorServiceDescription()
    {
        desc = new PredictorServiceDescription("Community Analysis Predictor Service", "1.3", "A Predictor Service for that makes predictions on events based on the Community Analysis tool");
        desc.setUuid(UUID.fromString("89344cd0-893b-4bee-a484-e5b69e320745"));
		
        // event
        Event evt = new Event("Community Health Event", "Description");
        EventCondition evtCond = new EventCondition(ParameterValueType.FLOAT, "Health Indicator Threshold", "The threshold the event should fire at", "");
        evtCond.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond.addValueConstraint("0.3", ValueConstraintType.DEFAULT);
        evtCond.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond.addValueConstraint("1", ValueConstraintType.MAX);
        evt.setEventCondition(evtCond);
		
		Parameter featureParam = new Parameter(ParameterValueType.STRING, "Health indicator", "Select which health indicator to use", "");
        featureParam.addValueConstraint("Popularity", ValueConstraintType.DEFAULT);
		featureParam.addValueConstraint("In-Degree", ValueConstraintType.VALUESALLOWED);	// 1 
        featureParam.addValueConstraint("Out-Degree", ValueConstraintType.VALUESALLOWED);	// 2
        featureParam.addValueConstraint("Popularity", ValueConstraintType.VALUESALLOWED);	// 3
        featureParam.addValueConstraint("Reciprocity", ValueConstraintType.VALUESALLOWED);	// 4
        featureParam.addValueConstraint("Activity", ValueConstraintType.VALUESALLOWED);		// 5

		evt.addConfigParam(featureParam);
        desc.addEvent(evt);
        
        // forecast period
        Parameter forecastPeriod = new Parameter(ParameterValueType.INT, "Forecast period", "The available forecast period options", "days");
        forecastPeriod.addValueConstraint("7", ValueConstraintType.DEFAULT);
        forecastPeriod.addValueConstraint("7", ValueConstraintType.MIN);
        forecastPeriod.addValueConstraint("28", ValueConstraintType.MAX);
        forecastPeriod.addValueConstraint("7", ValueConstraintType.STEP);
        desc.setForecastPeriod(forecastPeriod);
        
        // no general configuration parameters...
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