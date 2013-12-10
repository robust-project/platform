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
//      Created Date :          2012-01-18
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.util.Date;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;

/**
 * Interface for the EvaluationEngine, which is the high-level component that
 * deals with evaluating risks and opportunities.
 * 
 * The logic of the engine is to evaluate any risks or opportunities specified
 * in the risk repository (database) according to their evaluation schedules.
 *
 * To start the evaluation process, the engine must be started, which is one of the
 * method calls. The engine will then enter an infinite loop to perform its evaluation
 * task until a call to stop() is issued.
 * 
 * It is possible to register to listen to new evaluation results with the
 * addListener() method. The component wishing to do so must implement the
 * IEvaluationEngineListener interface.
 * 
 * @author Vegard Engen
 */
public interface IEvaluationEngine
{
    /**
     * Starts the evaluation engine, which spawns a thread to run it in.
     * All configuration will be done via the properties file.
     * @throws Exception 
     */
    void start() throws Exception;
    
    /**
     * The evaluation engine will only evaluate R&Os within the given time period.
     * All other configuration will be done via the properties file.
     * @param startDate The start date from which the engine will start the evaluation process.
     * @param endDate The end date at which the engine should stop the evaluation process
     * @throws Exception 
     */
    void start(Date startDate, Date endDate) throws Exception;
    
    /**
     * The evaluation engine will only evaluate R&Os within the given time period,
     * and the simulated 'current' date will be incremented according to the
     * frequency type given.
     * All other configuration will be done via the properties file.
     * @param startDate The start date from which the engine will start the evaluation process.
     * @param endDate The end date at which the engine should stop the evaluation process
     * @param incrementFrequency The increment at each 'tick' for the engine (simulated time), e.g., weekly.
     * @throws Exception 
     */
    void start(Date startDate, Date endDate, FrequencyType incrementFrequency) throws Exception;
    
    /**
     * Stops the evaluation engine, which will signal for the evaluation thread
     * to stop (it will finish what it is doing first).
     * @throws Exception 
     */
    void stop() throws Exception;
    
    /**
     * Check if the Evaluation Engine is running.
     * @return True if running (a call to start has been made); false otherwise.
     */
    boolean isEngineRunning();
    
    /**
     * Add a listener for evaluation engine events.
     * @param listener Listener implementation.
     */
    void addEvaluationEngineListener(IEvaluationEngineListener listener);
    
    /**
     * A method for a component to use to remove themselves as a listener for
     * general evaluation engine events.
     * @param listener 
     */
    void removeEvaluationEngineListener(IEvaluationEngineListener listener);
    
    /**
     * Add a listener for new results.
     * @param listener Listener implementation.
     */
    void addEvaluationResultListener(IEvaluationResultListener listener);
    
    /**
     * Add a listener for new results of a specific community.
     * @param listener Listener implementation.
     * @param communityUUID Community UUID.
     */
    boolean addEvaluationResultListener(IEvaluationResultListener listener, UUID communityUUID);
    
    /**
     * A method for a component to use to remove themselves as a listener.
     * @param listener 
     */
    void removeEvaluationResultListener(IEvaluationResultListener listener);
    
    /**
     * Provide a new evaluation result for a particular job that would have been
     * started by the engine (it should recognise the job reference given).
     * @param jobRef The job reference.
     * @param evalRes The evaluation result.
     */
    void newEvaluationResult(String jobRef, EvaluationResult evalRes);
    
    /**
     * Provide a status update on a particular evaluation job, such as a job
     * failed after it started.
     * @param jobRef The job reference.
     * @param jobStatus The job status.
     */
    void updateEvaluationJobStatus(String jobRef, JobStatus jobStatus);
}
