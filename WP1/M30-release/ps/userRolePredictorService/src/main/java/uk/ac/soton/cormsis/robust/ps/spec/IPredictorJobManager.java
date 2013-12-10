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
package uk.ac.soton.cormsis.robust.ps.spec;

import java.util.List;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;

/**
 *
 * @author Vegard Engen
 */
public interface IPredictorJobManager
{
    /**
     * Stop the Predictor Job Manager.
     */
    void stopJobManager();
    
    /**
     * Create a new evaluation job entry for the given job configuration.
     * @param jobConfig
     * @return A JobDetails object, detailing inter alia the job reference and status, etc.
     */
    JobDetails createNewJob(PredictorServiceJobConfig jobConfig);
    
    /**
     * Start an evaluation job for the given job reference. This may be put
     * in a queue, depending on the functionality offered by the respective
     * predictor service.
     * @param jobRef The reference to the job that should be started.
     * @return 
     */
    JobStatus startJob (String jobRef) throws Exception;
    
    /**
     * Cancel a job.
     * @param jobRef The reference to the job that should be cancelled.
     * @return 
     */
    JobStatus cancelJob(String jobRef) throws Exception;
    
    /**
     * Get the number of jobs that are currently being evaluated or are queued to
     * be evaluated.
     * @return 
     */
    int getNumJobsBeingEvaluated();
    
    /**
     * Get the number of jobs waiting to be evaluated.
     * @return 
     */
    int getNumQueuedJobs();
    
    /**
     * Get a list of all jobs, regardless of status.
     * @return 
     */
    List<JobDetails> getJobs();
    
    /**
     * Get the job details for a specific job.
     * @param jobRef The job reference.
     * @return
     * @throws Exception 
     */
    JobDetails getJobDetails(String jobRef) throws Exception;
    
    /**
     * Get the job status for a specific job.
     * @param jobRef The job reference.
     * @return
     * @throws Exception 
     */
    JobStatus getJobStatus(String jobRef) throws Exception;
    
    /**
     * Get the (latest) evaluation result for a specific job.
     * @param jobRef The job reference.
     * @return
     * @throws Exception 
     */
    EvaluationResult getEvaluationResult(String jobRef) throws Exception;
}
