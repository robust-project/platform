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
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.util.Date;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;

/**
 * This is an interface for a manager class used by the Evaluation Engine,
 * which deals with the process of evaluating risks and opportunities, and
 * checking for results.
 * 
 * The class implementing this interface should be run in a thread, allowing
 * a run-loop to start evaluation jobs and check for results at regular intervals.
 * The implementation class should also take care to manage the listeners and 
 * notify them of new results.
 * 
 * @author Vegard Engen
 */
public interface IEvaluationManager
{
    /**
     * Stop the manager thread.
     * @throws Exception 
     */
    void stop() throws Exception;
    
    /**
     * Configure the manager to only evaluate R&Os within the given time period.
     * @param startDate The start date from which the engine will start the evaluation process.
     * @param endDate The end date at which the engine should stop the evaluation process
     */
    void setSimulationDates(Date startDate, Date endDate);
    
    /**
     * Configure the manager to only evaluate R&Os within the given time period,
     * and increment the time ticks by the given frequency type (only applicable
     * if running in demo mode).
     * @param startDate The start date from which the engine will start the evaluation process.
     * @param endDate The end date at which the engine should stop the evaluation process
     * @param incrementFrequency The increment at each 'tick' for the engine (simulated time), e.g., daily or weekly.
     */
    void setSimulationDatesAndFrequency(Date startDate, Date endDate, FrequencyType incrementFrequency);
    
    /**
     * Provide a new evaluation result for a particular job that would have been
     * started by the engine (it should recognise the job reference given).
     * @param jobRef The job reference.
     * @param evalRes The evaluation result.
     */
    public void newEvaluationResult(String jobRef, EvaluationResult evalRes);
    
    /**
     * Provide a status update on a particular evaluation job, such as a job
     * failed after it started.
     * @param jobRef The job reference.
     * @param jobStatus The job status.
     */
    void updateEvaluationJobStatus(String jobRef, JobStatus jobStatus);
    
    /**
     * Add a listener for evaluation engine events (new results).
     * @param listener 
     */
    public void addListener(IEvaluationManagerListener listener);
    
    /**
     * A method for a component to use to remove themselves as a listener.
     * @param listener 
     */
    public void removeListener(IEvaluationManagerListener listener);
}
