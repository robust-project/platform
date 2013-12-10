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
//      Created Date :          2012-08-06
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec;

import java.util.Date;
import java.util.UUID;
import javax.jws.WebService;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;

/**
 * Service interface for the Evaluation Engine, which is the high-level component that
 * deals with evaluating risks and opportunities.
 * 
 * The logic of the engine is to evaluate any risks or opportunities specified
 * in the risk repository (database) according to their evaluation schedules.
 *
 * To start the evaluation process, the engine must be started, which is one of the
 * method calls. The engine will then enter an infinite loop to perform its evaluation
 * task until a call to stop() is issued.
 * 
 * It is possible to subscribe to general events such as the engine starting and
 * stopping, as well as getting evaluation results for specific communities. This
 * is done via message streams, and any subscribers can get the URI to subscribe
 * to the specific streams they desire.
 * 
 * @author Vegard Engen
 */
@WebService
public interface IEvaluationEngineService
{
    /**
     * Starts the evaluation engine, which spawns a thread to run it in.
     * All configuration will be done via the service properties file.
     * @return Status object which details if the call was successful and any meta-data (String).
     */
    Status startEngine();
    
    /**
     * The evaluation engine will only evaluate R&Os within the given time period.
     * All other configuration will be done via the service properties file.
     * @param startDate The start date from which the engine will start the evaluation process.
     * @param endDate The end date at which the engine should stop the evaluation process.
     * @return Status object which details if the call was successful and any meta-data (String).
     */
    Status startEngineForTimePeriod(Date startDate, Date endDate);
    
    /**
     * Stops the evaluation engine, which will signal for the evaluation thread
     * to stop (it will finish what it is doing first).
     * @return Status object which details if the call was successful and any meta-data (String).
     */
    Status stopEngine();
    
    /**
     * Check if the evaluation engine has been started and is running.
     * @return True if it has been started; false otherwise.
     */
    boolean isEngineRunning();
    
    /**
     * Get the name and URI to the ActiveMQ topic where general evaluation engine
     * notifications are published, such as 'started' and 'stopped'.
     * @return StreamDetails object that details the name and URI to the ActiveMQ topic, if existing; otherwise an org.apache.cxf.interceptor.Fault exception is thrown.
     */
    StreamDetails getGeneralStreamDetails();
    
    /**
     * Get the name and URI to the ActiveMQ topic for results of a specific community.
     * @param communitID The UUID of a community (can be retrieved from the Data Service).
     * @return StreamDetails object that details the name and URI to the ActiveMQ topic, if existing; otherwise an org.apache.cxf.interceptor.Fault exception is thrown.
     */
    StreamDetails getCommunityResultsStreamDetails(UUID communitID);
    
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
