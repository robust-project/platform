/////////////////////////////////////////////////////////////////////////
//
// � University of Southampton IT Innovation Centre, 2013
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
package uk.ac.soton.itinnovation.robust.sim.ws.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import org.springframework.context.Lifecycle;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.cat.common.sim.ISimulationService;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig;
import uk.ac.soton.itinnovation.robust.sim.ws.spec.ISimulationJobManager;

@WebService(endpointInterface = "uk.ac.soton.itinnovation.robust.cat.common.sim.ISimulationService")
public class SimulationService implements ISimulationService, Lifecycle
{
    private ISimulationJobManager jobManager;
	private SimulationServiceDescription desc;
    
    static Logger log = Logger.getLogger(SimulationService.class);
    
    /**
     * Default constructor which sets up the job management thread and any other
     * required components.
     */
    public SimulationService()
    {
        log.info("CatSim Simulation Service STARTING UP");
        generatePredictorServiceDescription();
        jobManager = SimulationJobManager.getInstance(desc);
        log.info("CatSim Simulation Service STARTED");
    }
    
    /**
     * Stopping the Job Manager (which should clean up any jobs running etc).
     */
    private void stopManager()
    {
        log.info("Stopping Simulation Job Manager");
        jobManager.stopJobManager();
    }
    
    /**
     * Create the SimulationServiceDescription object, which defines the offerings
     * of the service to any clients.
     */
    private void generatePredictorServiceDescription()
    {
        desc = new SimulationServiceDescription(UUID.fromString("4a8bab2c-0a8a-4321-a885-ecec28ccbfef"), "CATSim Service", "1.0", "A Simulation Service that supports the simulation of impact of users activity on the responsiveness and activity of a community");
		
        List<Parameter> configParams = new ArrayList<Parameter>();
		
		// if simulate user drop
		Parameter userDropFlag = new Parameter(ParameterValueType.BOOLEAN, "Simulate user activity drop", "Set a flag whether to simulate user drop in activity or not", "");
		userDropFlag.addValueConstraint("true", ValueConstraintType.DEFAULT);
        userDropFlag.addValueConstraint("true", ValueConstraintType.VALUESALLOWED);
        userDropFlag.addValueConstraint("false", ValueConstraintType.VALUESALLOWED);
		configParams.add(userDropFlag);
		
		// username and threshold drop
		Parameter userID = new Parameter(ParameterValueType.STRING, "User ID", "Set the user ID for the user to simulate drop of activity of", "");
		configParams.add(userID);
		
		Parameter userDropThreshold = new Parameter(ParameterValueType.FLOAT, "User activity drop", "Set the percentage drop to simulate for the specified user", "%");
		userDropThreshold.addValueConstraint("20", ValueConstraintType.DEFAULT);
        userDropThreshold.addValueConstraint("1", ValueConstraintType.MIN);
        userDropThreshold.addValueConstraint("100", ValueConstraintType.MAX);
		configParams.add(userDropThreshold);
		
		// if calculate TRT probability
		Parameter trtFlag = new Parameter(ParameterValueType.BOOLEAN, "Calculate P(TRT_drop >= threshold)", "Set a flag whether to calculate the probability of the thread resolution time exceeding a given threshold (TRT measured in days)", "");
		trtFlag.addValueConstraint("true", ValueConstraintType.DEFAULT);
        trtFlag.addValueConstraint("true", ValueConstraintType.VALUESALLOWED);
        trtFlag.addValueConstraint("false", ValueConstraintType.VALUESALLOWED);
		configParams.add(trtFlag);
		
        Parameter trtThreshold = new Parameter(ParameterValueType.FLOAT, "TRT drop threshold", "Set the percentage value for Thread Resolution Time drop threshold", "%");
		trtThreshold.addValueConstraint("20", ValueConstraintType.DEFAULT);
        trtThreshold.addValueConstraint("1", ValueConstraintType.MIN);
        trtThreshold.addValueConstraint("100", ValueConstraintType.MAX);
		configParams.add(trtThreshold);
		
		// if calculate community inactivity probability
		Parameter communityActivityFlag = new Parameter(ParameterValueType.BOOLEAN, "Calculate P(community_activity_drop >= threshold)", "Set a flag whether to calculate the probability of the community activity drop exceeding a given threshold (activity measured by the number of posts)", "");
		communityActivityFlag.addValueConstraint("true", ValueConstraintType.DEFAULT);
        communityActivityFlag.addValueConstraint("true", ValueConstraintType.VALUESALLOWED);
        communityActivityFlag.addValueConstraint("false", ValueConstraintType.VALUESALLOWED);
		configParams.add(communityActivityFlag);
		
		Parameter communityActivityThreshold = new Parameter(ParameterValueType.FLOAT, "Community activity drop threshold", "Set the percentage value for the community activity drop threshold", "%");
		communityActivityThreshold.addValueConstraint("20", ValueConstraintType.DEFAULT);
        communityActivityThreshold.addValueConstraint("1", ValueConstraintType.MIN);
        communityActivityThreshold.addValueConstraint("100", ValueConstraintType.MAX);
		configParams.add(communityActivityThreshold);
		
		// the number of runs
		Parameter numRuns = new Parameter(ParameterValueType.INT, "Number of runs", "The number of times the simulation model should run. The more the better in terms of statistical significance, but will be more time consuming the greater this value is.", "");
		numRuns.addValueConstraint("30", ValueConstraintType.DEFAULT);
        numRuns.addValueConstraint("5", ValueConstraintType.MIN);
        numRuns.addValueConstraint("100", ValueConstraintType.MAX);
		configParams.add(numRuns);
		
        desc.setConfigurationParameters(configParams);
    }
    
    @Override
    public SimulationServiceDescription getSimulationServiceDescription()
    {
        log.debug("Incoming request for getSimulationServiceDescription");
        return desc;
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
    public JobDetails createSimulationJob(SimulationServiceJobConfig config)
    {
        log.debug("Incoming request for creating a new simulation job");
        return jobManager.createNewJob(config);
    }

    @Override
    public JobStatus startSimulation(String jobRef)
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
    public SimulationResult getResult(String jobRef)
    {
        log.debug("Incoming request to get results for job with reference: " + jobRef);
        try {
            return jobManager.getResult(jobRef);
        } catch (Exception e) {
            return new SimulationResult(new JobDetails(jobRef, new JobStatus(JobStatusType.ERROR, e.getMessage()), null), null, null);
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
