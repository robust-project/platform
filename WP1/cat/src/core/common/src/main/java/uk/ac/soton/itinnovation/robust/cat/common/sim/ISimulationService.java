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
package uk.ac.soton.itinnovation.robust.cat.common.sim;

import javax.jws.WebService;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;

/**
 * An interface for simulation services integrated in the CAT Simulation Centre.
 * @author Vegard Engen
 */
@WebService
public interface ISimulationService
{
	/**
     * Get details about the service, including the configuration space for any
	 * parameters that can be set.
     * @return 
     */
    SimulationServiceDescription getSimulationServiceDescription();
    
    /**
     * Create an simulation job according to the configuration passed on to this 
     * service call. The configuration should be based on the options exposed by 
     * the service in the getSimulationServiceDescription() method. This SHOULD NOT 
     * start the simulation job, but typically put it in a queue ready to be 
     * started when a call to startSimulation(job ref) is made (see below).
     * @param config The job configuration object.
     * @return A JobDetails object containing a job reference (String) and a 
     *         JobStatus object. The status SHOULD be one of: READY, QUEUED, FAILED or ERROR. 
     *         If an error, an error message should be included as meta-data.
     */
    JobDetails createSimulationJob(SimulationServiceJobConfig config);
    
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
    JobStatus startSimulation(String jobRef);
	
    /**
     * Get the result of a simulation job, assuming that it has finished; otherwise
	 * the result object should specify an error state that should be checked for.
     * @param jobRef Job reference of an evaluation job to get the (latest) result for.
     * @return Simulation result object, possibly with an error state if the given 
     *         job reference is unknown
     */
    SimulationResult getResult(String jobRef);
    
	/**
     * Get the details of a specific job (details such as time of job request, 
     * start time, finish time, status, etc).
     * @param jobRef
     * @return 
     */
    JobDetails getJobDetails(String jobRef);
    
    /**
     * Returns a JobStatus object. If ERROR or FAILED, a description in the should
	 * be included in the meta-data.
     * @param jobRef Job reference of an evaluation job.
     * @return Job status, possibly an error state if the given job reference is 
     *         unknown.
     */
    JobStatus getJobStatus(String jobRef);
	
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
