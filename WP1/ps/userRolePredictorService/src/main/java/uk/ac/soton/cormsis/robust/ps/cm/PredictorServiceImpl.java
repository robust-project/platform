/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre and CORMSIS, 2012
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
//      Created By :            Vegard Engen, Edwin Tye
//      Created Date :          2012-03-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.ps.cm;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import org.springframework.context.Lifecycle;
import uk.ac.soton.cormsis.robust.ps.spec.IPredictorJobManager;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;

@WebService(endpointInterface = "uk.ac.soton.itinnovation.robust.cat.common.ps.IPredictorService")
public class PredictorServiceImpl implements IPredictorService, Lifecycle {

    private IPredictorJobManager jobManager;
    private PredictorServiceDescription desc;
    private PlatformType platformType;
    
    static Logger log = Logger.getLogger(PredictorServiceImpl.class);

    /**
     * Default constructor which sets up the job management thread and any other
     * required components.
     */
    public PredictorServiceImpl() {
        log.info("CM Predictor Service STARTING UP");
        generatePredictorServiceDescription();
        jobManager = PredictorJobManager.getInstance(this.desc, platformType);
        log.info("CM Predictor Service STARTED");
    }

    /**
     * Stopping the Job Manager (which should clean up any jobs running etc).
     */
    private void stopManager() {
        log.info("Stopping CM PredictorJobManager");
        jobManager.stopJobManager();
    }

    /**
     * Create the PredictorServiceDescription object, which defines the
     * offerings of the service to any clients.
     */
    private void generatePredictorServiceDescription() {
        
        // read properties file to get the data source
        platformType = PlatformType.SAP;
        
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("service.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
            // will continue using the default platform
            platformType = PlatformType.SAP;
        }

        try {
            platformType = PlatformType.fromValue(prop.getProperty("platformName"));
            if (platformType == null) {
                throw new IllegalArgumentException("'platformName' not set");
            }
        } catch (Exception ex) {
            log.warn("Error getting 'patformType' parameter from service.properties. " + ex.getMessage(), ex);
            log.warn("Using default platform");
            platformType = PlatformType.SAP;
        }
        
        // set the description according to the platform type
        switch (platformType)
        {
            case IBM:
                desc = PSDGenerator.getIBMdesc();
                break;
            case SAP:
                desc = PSDGenerator.getSAPdesc();
                break;
            case BOARDSIE:
                desc = PSDGenerator.getBOARDSIEdesc();
                break;
            default: // SAP description now
                desc = PSDGenerator.getSAPdesc();
                break;
        }
    }

    @Override
    public PredictorServiceDescription getPredictorServiceDescription() {
        log.debug("Incoming request for PredictorServiceDescription");
        return desc;
    }

    @Override
    public List<JobDetails> getJobs() {
        log.debug("Incoming request to get details of jobs on service");
        return jobManager.getJobs();
    }

    @Override
    public JobDetails getJobDetails(String jobRef) {
        log.debug("Incoming request for job details for reference: " + jobRef);
        try {
            return jobManager.getJobDetails(jobRef);
        } catch (Exception e) {
            log.error("No such job with reference: " + jobRef);
            return new JobDetails(jobRef, new JobStatus(JobStatusType.ERROR, e.getMessage()), null);
        }
    }

    @Override
    public JobDetails createEvaluationJob(PredictorServiceJobConfig config) {
        log.debug("Incoming request for creating a new evaluation job");
        return jobManager.createNewJob(config);
    }

    @Override
    public JobStatus evaluate(String jobRef) {
        log.debug("Incoming request to start job with reference: " + jobRef);
        try {
            return jobManager.startJob(jobRef);
        } catch (Exception e) {
            return new JobStatus(JobStatusType.ERROR, e.getMessage());
        }
    }

    @Override
    public JobStatus getJobStatus(String jobRef) {
        log.debug("Incoming request to get job status for job with reference: " + jobRef);
        try {
            return jobManager.getJobStatus(jobRef);
        } catch (Exception e) {
            return new JobStatus(JobStatusType.ERROR, e.getMessage());
        }
    }

    @Override
    public EvaluationResult getEvaluationResult(String jobRef) {
        log.debug("Incoming request to get evaluation results for job with reference: " + jobRef);
        try {
            return jobManager.getEvaluationResult(jobRef);
        } catch (Exception e) {
            return new EvaluationResult(new JobDetails(jobRef, new JobStatus(JobStatusType.ERROR, e.getMessage()), null), null, null, null);
        }
    }

    @Override
    public JobStatus cancelJob(String jobRef) {
        log.debug("Incoming request to cancel a job with reference: " + jobRef);
        try {
            return jobManager.cancelJob(jobRef);
        } catch (Exception e) {
            log.error("Caught an exception when trying to cancel the job: " + e.getMessage(), e);
            return new JobStatus(JobStatusType.ERROR, e.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            log.debug("Stopping the manager - finalize method");
        } catch (Throwable t) {
        }
        stopManager();
        super.finalize();
    }

    @Override
    public void start() {
        log.debug("Lifecycle.start() called - doing nothing though");
    }

    @Override
    public void stop() {
        log.debug("Lifecycle.stop() called");
        stopManager();
    }

    @Override
    public boolean isRunning() {
        log.debug("Lifecycle.isRunning() called");

        return true;
    }
}
