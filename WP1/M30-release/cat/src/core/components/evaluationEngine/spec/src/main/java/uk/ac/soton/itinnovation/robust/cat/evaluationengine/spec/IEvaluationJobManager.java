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
//      Created Date :          2012-01-26
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 * This is an interface for a Job Manager used by the Evaluation Manager
 * to abstract the implementation details of starting evaluation jobs
 * and checking for results.
 * 
 * @author Vegard Engen
 */
public interface IEvaluationJobManager
{
    /**
     * Start evaluation jobs, for the date given as an argument.
     * This will check if there are any risks or opportunities that are due to 
     * be evaluated. If any, then evaluation jobs will be created and started
     * according to the details for each respective risk or opportunity.
     * @param startDate The date the evaluation of the risks should be done from.
     * @return A Map of Risk UUIDs and JobDetails for any that have been started.
     */
    Map<UUID, EEJobDetails> startEvaluationJobs(Date startDate);
    
    /**
     * If any jobs have been started, this method will return evaluation results
     * for any that are finished.
     * 
     * If there are no jobs, or none are finished, an empty map is returned.
     * 
     * OBS: If a risk or opportunity failed somehow, an EvaluationResult is still returned,
     * but with JobDetails specifying that it failed.
     * @return A Map of Risk/Opportunity UUIDs and their respective Evaluation Results.
     */
    Map<UUID, EvaluationResult> checkForEvaluationResults();
    
    /**
     * Get the details of Risks and Opportunities that are in the process of
     * being evaluated.
     * @return A Map of Risk UUIDs and JobDetails for any that are being evaluated.
     */
    Map<UUID, EEJobDetails> getEvaluationJobsDetails();
    
    /**
     * Provide a new evaluation result for a particular job that would have been
     * started by the engine (it should recognise the job reference given).
     * @param jobRef The job reference.
     * @param evalRes The evaluation result.
     */
    void newEvaluationResult(String jobRef, EvaluationResult evalRes);
    
    /**
     * A method to check the status of risks in the datalayer, which should pick
     * up whether any new risks have been added or if any old risks have been
     * deleted.
     */
    void checkRiskStatus();
    
    /**
     * Provide a status update on a particular evaluation job, such as a job
     * failed after it started.
     * @param jobRef The job reference.
     * @param jobStatus The job status.
     */
    void updateEvaluationJobStatus(String jobRef, JobStatus jobStatus);
    
    /**
     * Check if a job exists according to its job reference.
     * @param jobRef The job reference.
     * @return True if the job is known; false otherwise.
     */
    boolean doesJobExist(String jobRef);
    
    /**
     * Get the Risk UUID for a job reference, if it is a valid job reference for
     * a risk that has a started evaluation job for it.
     * @param jobRef Job reference.
     * @return The UUID if such a mapping exists, otherwise NULL.
     */
    UUID getRiskUUIDforJobRef(String jobRef);
    
    /**
     * Get the number of jobs for risks or opportunities currently being evaluated,
     * both streaming and non-streaming jobs.
     * @return the number of non-streaming jobs for risks or opportunities currently being evaluated.
     */
    int getNumJobsBeingEvaluated();
    
    /**
     * Get the number of streaming jobs for risks or opportunities currently being evaluated.
     * @return the number of streaming jobs for risks or opportunities currently being evaluated.
     */
    int getNumStreamingJobsBeingEvaluated();
    
    /**
     * Get the number of non-streaming jobs for risks or opportunities currently being evaluated.
     * @return the number of non-streaming jobs for risks or opportunities currently being evaluated.
     */
    int getNumNonStreamingJobsBeingEvaluated();
    
    /**
     * Clear any job information - reset method.
     */
    void clearJobData();
}
