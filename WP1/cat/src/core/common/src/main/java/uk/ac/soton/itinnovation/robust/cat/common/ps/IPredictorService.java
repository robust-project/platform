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
//      Created Date :          2012-01-11
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.ps;

import java.util.List;
import javax.jws.WebService;

/**
 * An interface for predictor services, which is used as a common way for the 
 * evaluation engine to evaluate risks and opportunities.
 * @author Vegard Engen
 */
@WebService
public interface IPredictorService
{
    /**
     * Get details about the service, including what events the service offers, 
     * configuration space for each event supported, and configuration space for 
     * general configuration parameters.
     * @return 
     */
    PredictorServiceDescription getPredictorServiceDescription();
    
    /**
     * Get a list of jobs at the service (each job includes details such as time 
     * of job request, start time, finish time, status, etc).
     * @return 
     */
    List<JobDetails> getJobs();
    
    /**
     * Get the details of a specific job (details such as time of job request, 
     * start time, finish time, status, etc).
     * @param jobRef
     * @return 
     */
    JobDetails getJobDetails(String jobRef);
    
    /**
     * Create an evaluation job according to the configuration passed on to this 
     * service call. The configuration should be based on the options exposed by 
     * the service in the getPredictorServiceDescription() method. This SHOULD NOT 
     * start the evaluation job, but typically put it in a queue ready to be 
     * started when a call to evaluate(job ref) is made (see below).
     * @param config The job configuration object.
     * @return A JobDetails object containing a job reference (String) and a 
     *         JobStatus object. The status SHOULD be one of: READY, QUEUED, FAILED or ERROR. 
     *         If an error, an error message should be included as meta-data.
     */
    JobDetails createEvaluationJob(PredictorServiceJobConfig config);
    
    /**
     * This call should issue that a job with the given reference should be started, 
     * assuming that createEvaluationJob(...) has been called first. Bear in mind 
     * somebody might make a call to start an evaluation job with a job reference
     * that doesn’t exist.
     * @param jobRef Job reference of an evaluation job that should be started,
     *        assuming that a job with the given reference has been created.
     * @return JobStatus object, and the status SHOULD be one of: QUEUED, RUNNING,
     *         or ERROR/FAILED. If an error, an error message should be included as
     *         meta-data.
     */
    JobStatus evaluate(String jobRef);
    
    /**
     * Returns a JobStatus object. If ERROR or FAILED status, a description should
	 * be included in the meta-data.
     * @param jobRef Job reference of an evaluation job.
     * @return Job status, possibly an error state if the given job reference is 
     *         unknown.
     */
    JobStatus getJobStatus(String jobRef);

    /**
     * Although evaluation results will be sent to the Evaluation Engine
     * asynchronously, this call is necessary in case the Evaluation Engine needs
     * to get some results it may have missed if it crashed. So the service should 
     * ideally store evaluation results at least until it has been successfully
     * fetched or pushed to the Evaluation Engine.
     * @param jobRef Job reference of an evaluation job to get the (latest) result for.
     * @return Evaluation result object, possibly with an error state if the given 
     *         job reference is unknown
     */
    EvaluationResult getEvaluationResult(String jobRef);
    
    /**
     * This call should cancel/remove a job, whether it’s queued or running. 
     * If a running algorithm cannot be stopped, an evaluation result shouldn't be 
     * made afterwards and sent to the evaluation engine.
     * @param jobRef Job reference of an evaluation job to cancel.
     * @return Job status, possibly an error state if the given job reference is
     *         unknown.
     */
    JobStatus cancelJob(String jobRef);
}
