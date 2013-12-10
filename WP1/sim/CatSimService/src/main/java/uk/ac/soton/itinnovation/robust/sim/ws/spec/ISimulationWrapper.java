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
package uk.ac.soton.itinnovation.robust.sim.ws.spec;

import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig;

/**
 *
 * @author Vegard Engen
 */
public interface ISimulationWrapper
{
    /**
     * Start an evaluation job according to the configuration object.
     * @param jobRef The job reference.
     * @param config Job configuration object.
     * @throws Exception 
     */
    void startJob(String jobRef, SimulationServiceJobConfig config) throws Exception;
    
    /**
     * Cancel the evaluation job.
     * @throws Exception 
     */
    void cancelJob();
    
    /**
     * Get the status of the evaluation job.
     * @return JobStatus object.
     * @throws Exception 
     */
    JobStatus getJobStatus();
    
    /**
     * Set the listener for getting a notification event when a new result
     * is ready or any errors.
     * 
     * @param simListener Implementation of the ISimulationListener interface.
     */
    void setSimulationListener(ISimulationListener simListener);
}
