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
//      Created Date :          2012-01-23
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.impl;

import java.net.URI;
import java.util.*;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
//import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.RiskState;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.EEJobDetails;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationJobManager;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IPredictorClient;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.RiskEvaluationDetails;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.CommunityRisksElement;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;
import uk.ac.soton.itinnovation.robust.riskmodel.RiskState;

/**
 * A class that handles the job management, taking care of creating new evaluation
 * jobs and keeping track of the status of these jobs, etc. This class interacts
 * with the Predictor Client to communicate with Predictor Services.
 * 
 * In this implementation, all job management is done in memory and there is no
 * persistence of this information.
 * 
 * @author Vegard Engen
 */
public class EvaluationJobManager implements IEvaluationJobManager
{
    private IPredictorClient predictorClient;
    private IDataLayer dataLayer;
    private URI eeServiceURI;
    
    // a map to keep track of R/Os and their JobDetails (key = Risk UUID)
    // that are being evaluated - erroronous or finished jobs are removed
    private Map<UUID, EEJobDetails> riskJobMap;
    
    // a map to keep track of job references (key) and the Risk UUIDs they are for
    private Map<String, UUID> jobRefToRiskMap;
    
	// a map of evaluation details for each risk (key is risk UUID)
    private Map<UUID, RiskEvaluationDetails> riskEvaluationDetailsMap;
    
    // keeps track of the streaming and non-streaming jobs currently being evaluated
    // the job reference is stored
    private Set<String> streamingJobs;
    private Set<String> nonStreamingJobs;
	
	// keep track of the last date evaluations have been asked for
	private Date lastEvaluationDate;
    
    static Logger log = Logger.getLogger(EvaluationJobManager.class);
    
    /**
     * Constructor that sets up the job management collections and predictor client.
     * @param dataLayer Data Layer implementation, which is used to get details
     * of risks and predictors.
     * @throws Exception 
     */
    public EvaluationJobManager(IDataLayer dataLayer, URI eeSrvURI) throws Exception
    {
        this.dataLayer = dataLayer;
        this.eeServiceURI = eeSrvURI;
        
        this.predictorClient = new PredictorClient(dataLayer, eeServiceURI);
        
        this.riskJobMap = Collections.synchronizedMap(new HashMap<UUID, EEJobDetails>());
        this.jobRefToRiskMap = Collections.synchronizedMap(new HashMap<String, UUID>());
        this.riskEvaluationDetailsMap = Collections.synchronizedMap(new HashMap<UUID, RiskEvaluationDetails>());
        
        this.streamingJobs = Collections.synchronizedSet(new HashSet<String>());
        this.nonStreamingJobs = Collections.synchronizedSet(new HashSet<String>());
    }
    
    /**
     * Set a Data Layer implementation that should be used.
     * @param dataLayer Data Layer implementation, which is used to get details
     * of risks and predictors.
     */
    public void setDataLayer(IDataLayer dataLayer)
    {
        this.dataLayer = dataLayer;
    }
    
    /**
     * Set the Evaluation Engine Service URI.
     * @param eeSrvURI The URI of the Evaluation Engine Service.
     */
    public void setEvaluationEngineServiceURI(URI eeSrvURI)
    {
        this.eeServiceURI = eeSrvURI;
    }
    
    @Override
    public void clearJobData()
    {
        log.debug("Call to clear job data");
        
        if (!riskJobMap.isEmpty())
        {
            try {
                log.debug("Cancelling evaluation jobs");
                predictorClient.cancelEvaluationJobs(riskJobMap.values());
            } catch (Exception ex) {
                log.error("Exception caught when cancelling evaluation jobs: " + ex.toString(), ex);
            }
        }
        
        riskJobMap.clear();
        jobRefToRiskMap.clear();
        riskEvaluationDetailsMap.clear();
        streamingJobs.clear();
        nonStreamingJobs.clear();
    }
    
    /**
     * Get the evaluation details from the risk object.
     * @param risk Risk object.
     * @return NULL if the necessary details are not in the Risk object; otherwise an RiskEvaluationDetails instance.
     */
    private RiskEvaluationDetails getRiskEvaluationDetails(Risk risk)
    {
        RiskEvaluationDetails red = new RiskEvaluationDetails();
        
        if (risk == null) {
            return null;
        }
        
        if (risk.getId() == null) {
            return null;
        }
        
        if (risk.getCat_review_period() == null) {
            return null;
        }
        
        red.setRiskUUID(risk.getId());
        red.setEvaluationPeriod(risk.getCat_review_period());
        red.setEvaluationFrequency(risk.getCat_review_freq());
        
        return red;
    }
    
    /**
     * Check if the risk evaluation details are outdated.
     * @param riskUUID The risk UUID.
     * @param redNew The most up-to-date risk evaluation details.
     * @return True if outdated; false otherwise.
     */
    private boolean isRiskEvaluationDetailsOutdated(UUID riskUUID, RiskEvaluationDetails redNew)
    {
        RiskEvaluationDetails redOld = riskEvaluationDetailsMap.get(riskUUID);
        if (redOld == null) {
            return true;
        }
        
        if (redOld.getEvaluationPeriod() != redNew.getEvaluationPeriod()) {
            return true;
        }
        
        if (redOld.getEvaluationFrequency() != redNew.getEvaluationFrequency()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if the risk is due to be evaluated.
     * @param riskUUID The risk UUID.
     * @param currentDate The current date.
     * @return True if it is due to be evaluated; false otherwise.
     */
    private boolean isRiskDueEvaluation(UUID riskUUID, Date currentDate)
    {
        RiskEvaluationDetails red = riskEvaluationDetailsMap.get(riskUUID);
        
        return red.isRiskDueEvaluation(currentDate);
    }
    
    /**
     * Check if the risk is due to be evaluated.
     * @param ro The risk instance.
     * @param currentDate The current date.
     * @return True if it is due to be evaluated; false otherwise.
     */
    public boolean isRiskDueEvaluation(Risk risk, Date currentDate)
    {
        RiskEvaluationDetails red = getRiskEvaluationDetails(risk);
        
        // check if it is already in the risk details map; if not - add it
        if (!riskEvaluationDetailsMap.containsKey(red.getRiskUUID())) {
            riskEvaluationDetailsMap.put(risk.getId(), red);
        } else if (isRiskEvaluationDetailsOutdated(red.getRiskUUID(), red)) {
            RiskEvaluationDetails redCurrent = riskEvaluationDetailsMap.get(red.getRiskUUID());
            redCurrent.setEvaluationPeriod(red.getEvaluationPeriod());
            redCurrent.setEvaluationFrequency(red.getEvaluationFrequency());
        }

        // check if R/O is due to be evaluated - add to be started if so
        boolean dueEval = isRiskDueEvaluation(risk.getId(), currentDate);
        if (dueEval) {
            log.debug("Risk with UUID '"+risk.getId()+"' is due evaluation");
        } else {
            log.debug("Risk with UUID '"+risk.getId()+"' is NOT due evaluation");
        }
        return dueEval;
    }
    
	public synchronized void setEvaluated(UUID riskUUID, Date date)
    {
        riskEvaluationDetailsMap.get(riskUUID).setLastEvaluatedDate(date);
    }

    @Override
    public Map<UUID, EEJobDetails>  startEvaluationJobs(Date currentDate)
    {
        Map<UUID, EEJobDetails> jobDetailsMap = new HashMap<UUID, EEJobDetails>();
        Set<Risk> rosToStart = new HashSet<Risk>();
        lastEvaluationDate = currentDate;
		
        // get communities names
        log.debug("Getting active risks and opportunities for all communities from the datalayer");
       // Map<Community, Set<Risk>> communityRisks = dataLayer.getAllActiveRisks();
        List<CommunityRisksElement> communityRisks = dataLayer.getAllActiveRisks(); //bmn
        
        if (communityRisks == null || communityRisks.isEmpty())
        {
            log.debug("No more risks in the database.");
            return jobDetailsMap;
        }
        
//        for (Community community : communityRisks.keySet())
//        {
        for (CommunityRisksElement communityelement : communityRisks)//bmn
        {
            Community community = communityelement.getCommunity(); //bmn
            if (communityelement.getRisks() != null) {
                // iterating over risks/opportunities for the community
                for (Risk ro : communityelement.getRisks()) { //bmn
                    // check if the RO is being evaluated
                    if (riskJobMap.containsKey(ro.getId())) {
                        continue;
                    }

                    // check if it is already in the risk details map; if not - add it
                    RiskEvaluationDetails red = getRiskEvaluationDetails(ro);

                    if (!riskEvaluationDetailsMap.containsKey(red.getRiskUUID())) {
                        riskEvaluationDetailsMap.put(ro.getId(), red);
                    } else if (isRiskEvaluationDetailsOutdated(red.getRiskUUID(), red)) {
                        RiskEvaluationDetails redCurrent = riskEvaluationDetailsMap.get(red.getRiskUUID());
                        redCurrent.setEvaluationPeriod(red.getEvaluationPeriod());
                        redCurrent.setEvaluationFrequency(red.getEvaluationFrequency());
                    }

                    // check if R/O is due to be evaluated - add to be started if so
                    if (isRiskDueEvaluation(ro.getId(), currentDate)) {
                        rosToStart.add(ro);
                    } else {
                        log.debug("Risk not due to be evaluated yet (UUID: " + ro.getId() + ")");
                    }
                } // for each risk for the community
            }
        } // for each community
        
        if (!rosToStart.isEmpty())
        {
            log.info("Attempting to start " + rosToStart.size() + " evaluation job(s)");
            try 
            {
				// iterate over each risk and start it
				for (Risk ro : rosToStart)
				{
					EEJobDetails jobDetails = null;
					try {
						jobDetails = predictorClient.startEvaluationJob(ro, currentDate);
					} catch (Exception ex) { // exceptions thrown for any error on the EE side (not PS side - that'll be indicated in the job status object)
						log.error("Unable to start the evaluation job; discarding it from this evaluation cycle", ex);
						continue;
					}
					
					// check the job status - could be failed if issues on the PS side
                    if ((jobDetails.getJobStatus().getStatus() == JobStatusType.FAILED) ||
                        (jobDetails.getJobStatus().getStatus() == JobStatusType.ERROR) )
                    {
                        log.error("Failed to start job for Risk/Opportunity UUID: " + ro.getId().toString());
                        log.error("  - Error message: " + jobDetails.getJobStatus().getMetaData());
                        continue;
                    }
                    
                    log.debug("Job started for Risk/Opportunity UUID: " + ro.getId().toString());
                    log.debug("  - Status: " + jobDetails.getJobStatus().getStatus());
                    
                    riskJobMap.put(ro.getId(), jobDetails);
                    jobRefToRiskMap.put(jobDetails.getJobRef(), ro.getId());
                    if (jobDetails.isStreaming())
                        this.streamingJobs.add(jobDetails.getJobRef());
                    else
                        this.nonStreamingJobs.add(jobDetails.getJobRef());
                    
                    // put in the map that will be returned, so the Evaluation Manager knows which and how many risks started
                    jobDetailsMap.put(ro.getId(), jobDetails);
                } // end for each risk/opportunity to be started
            } catch (Exception ex) {
                log.error("Caught an exception when trying to start evaluation jobs: " + ex.getMessage(), ex);
            }
        }
        else {
            log.info("No risks or opportunities that are due to be evaluated");
        }
        
        return jobDetailsMap;
    }
    
    @Override
    public Map<UUID, EvaluationResult> checkForEvaluationResults()
    {
        Map<UUID, EvaluationResult> resultsMap = new HashMap<UUID, EvaluationResult>();
        
        if (riskJobMap.isEmpty())
        {
            log.debug("There are no current evaluation jobs");
            return resultsMap;
        }
        
        log.debug("Checking if any of the " + riskJobMap.size() + " evaluation jobs are finished");
        try
        {
            Set<EvaluationResult> resultSet = predictorClient.getEvaluationResults(riskJobMap.values());
            
            if ((resultSet != null) && !resultSet.isEmpty())
            {
                for (EvaluationResult res : resultSet)
                {
                    resultsMap.put(res.getRiskUUID(), res);
                    riskEvaluationDetailsMap.get(res.getRiskUUID()).setLastEvaluatedDate(res.getCurrentDate());
                    EEJobDetails jobDetails = riskJobMap.get(res.getRiskUUID());
                    
                    if (!jobDetails.isStreaming())
                    {
                        nonStreamingJobs.remove(jobDetails.getJobRef());
                        riskJobMap.remove(res.getRiskUUID());
                        jobRefToRiskMap.remove(res.getJobDetails().getJobRef());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Got an error when checking if evaluation jobs were finished: " + ex.getMessage(), ex);
        }
        
        return resultsMap;
    }

    @Override
    public Map<UUID, EEJobDetails> getEvaluationJobsDetails()
    {
        return this.riskJobMap;
    }
    
    @Override
    public void newEvaluationResult(String jobRef, EvaluationResult evalRes)
    {
        if (jobRef == null) {
            return;
        }
        if (evalRes == null) {
            return;
        }
        if (jobRefToRiskMap.get(jobRef) == null) {
            log.error("Got a newEvaluationResult event, but the job reference is not known: " + jobRef);
            return;
        }
        
        log.debug("Setting that risk with UUID '" + evalRes.getRiskUUID().toString() + "' has been evaluated on " + DateUtil.getDateString(evalRes.getCurrentDate()));
        setEvaluated(evalRes.getRiskUUID(), evalRes.getCurrentDate());
        
        if (!riskJobMap.get(evalRes.getRiskUUID()).isStreaming())
        {
            log.info("Removing job details for non-streaming job that finished (received evaluation result)");
            nonStreamingJobs.remove(jobRef);
            riskJobMap.remove(evalRes.getRiskUUID());
            jobRefToRiskMap.remove(jobRef);
        }
    }
    
    
    private void riskAdded(UUID riskUUID)
    {
        
    }
    
    
    private void riskDeleted(UUID riskUUID)
    {
        
    }
    
    @Override
    public void checkRiskStatus()
    {
        log.debug("Checking risk status");
        // risks could have been:
        //   1: deleted - only dealing with this for now
        //   2: made inactive
        //   3: added
        
        Set<Risk> newRisks = new HashSet<Risk>();
        Set<UUID> deletedRisks = new HashSet<UUID>();
        Set<UUID> inactiveRisks = new HashSet<UUID>();
        for (UUID riskUUID : riskJobMap.keySet()) {
            deletedRisks.add(UUID.fromString(riskUUID.toString()));
            inactiveRisks.add(UUID.fromString(riskUUID.toString()));
        } // will remove from these according to the risks found below in the datalayer
        
        // get communities names
        log.debug("Getting all risks and opportunities for all communities from the datalayer");
        for (Community community : dataLayer.getCommunities())
        {
            Set<Risk> risks = dataLayer.getRisks(community.getUuid());
            
            // iterating over risks/opportunities for the community
            for (Risk ro : risks)
            {
                // IF ACTIVE
                if (ro.getState().equals(RiskState.ACTIVE))
                {
                    if (riskJobMap.containsKey(ro.getId()))
                    {
                        deletedRisks.remove(ro.getId());
                        inactiveRisks.remove(ro.getId());
                    }
                    else
                    {
                        newRisks.add(ro);
                    }
                }
                else // INACTIVE, TREATMENT, FLAGGED
                {
                    if (riskJobMap.containsKey(ro.getId()))
                    {
                        deletedRisks.remove(ro.getId());
                        inactiveRisks.remove(ro.getId());
                    }
                }
            } // for each risk for the community
        } // for each community
        
        if (!deletedRisks.isEmpty())
        {
            log.debug("There are " + deletedRisks.size() + " deleted risks -- updating the job management status");
            for (UUID riskUUID : deletedRisks)
            {
				if (riskJobMap.get(riskUUID) == null) {
					log.warn("The risk-to-job reference map doesn't have the given risk UUID: " + riskUUID);
					continue;
				}
                String jobRef = riskJobMap.get(riskUUID).getJobRef();
                if (jobRef == null) {
					log.warn("No job reference found for the risk that has been deleted with UUID: " + riskUUID);
                    continue;
                }
                
				log.debug("Updating (removing) the job management status for risk with UUID: " + riskUUID);
                nonStreamingJobs.remove(jobRef);
				streamingJobs.remove(jobRef);
                riskJobMap.remove(riskUUID);
                jobRefToRiskMap.remove(jobRef);
            }
        }
        else {
            log.debug("There were no deleted risks");
        }
    }
    
    @Override
    public void updateEvaluationJobStatus(String jobRef, JobStatus jobStatus)
    {
        if (jobRef == null) {
            return;
        }
        if (jobStatus == null) {
            return;
        }
        if ((jobRefToRiskMap.get(jobRef) == null) || (riskJobMap.get(jobRefToRiskMap.get(jobRef)) == null) ) {
            log.error("Got an updateEvaluationJobStatus event, but the job reference is not known: " + jobRef);
            return;
        }
        
		EEJobDetails jobDetails = riskJobMap.get(jobRefToRiskMap.get(jobRef));
		jobDetails.getRiskUUID();
		jobDetails.getStartDate();
        boolean streaming = jobDetails.isStreaming();
        UUID riskUUID = jobRefToRiskMap.get(jobRef);
		Date date = jobDetails.getStartDate();
		if (date == null) {
			date = lastEvaluationDate;
		}
        if (!streaming) // non-streaming job
        {
            if ((jobStatus.getStatus() == JobStatusType.CANCELLED) ||
                (jobStatus.getStatus() == JobStatusType.ERROR)     ||
                (jobStatus.getStatus() == JobStatusType.FAILED)    ||
                (jobStatus.getStatus() == JobStatusType.FINISHED)  ||
                (jobStatus.getStatus() == JobStatusType.RESULT_AVAILABLE) ) 
            {
				log.debug("Setting that risk with UUID '" + jobDetails.getRiskUUID().toString() + "' has been evaluated on " + DateUtil.getDateString(date));
				setEvaluated(jobDetails.getRiskUUID(), date);
				
                log.debug("Removing non-streaming job which had status: " + jobStatus.getStatus());
                nonStreamingJobs.remove(jobRef);
                riskJobMap.remove(riskUUID);
                jobRefToRiskMap.remove(jobRef);
            }
        }
        else // streaming job
        {
            if (jobStatus.getStatus() == JobStatusType.CANCELLED) // only removing if cancelled - need to consider to remove if error/failed or finished
            {
				log.debug("Setting that risk with UUID '" + jobDetails.getRiskUUID().toString() + "' has been evaluated on " + DateUtil.getDateString(date));
				setEvaluated(jobDetails.getRiskUUID(), date);
				
                log.debug("Removing streaming job which had status: CANCELLED");
                streamingJobs.remove(jobRef);
                riskJobMap.remove(riskUUID);
                jobRefToRiskMap.remove(jobRef);
            }
            else // for all other cases, just update the status
            {
                log.debug("Updating job status for streaming job to: " + jobStatus.getStatus());
                setJobStatus(jobRef, jobStatus);
            }
        }
    }
    
    @Override
    public boolean doesJobExist(String jobRef)
    {
        if (jobRefToRiskMap.get(jobRef) == null) {
            return false;
        } else {
            return true;
        }
    }
    
    @Override
    public UUID getRiskUUIDforJobRef(String jobRef)
    {
        if ((jobRefToRiskMap == null) || jobRefToRiskMap.isEmpty())
            return null;
        
        return jobRefToRiskMap.get(jobRef);
    }

    @Override
    public int getNumJobsBeingEvaluated()
    {       
        if (this.riskJobMap == null)
            return 0;
        
        return this.riskJobMap.size();
    }
    
    @Override
    public int getNumStreamingJobsBeingEvaluated()
    {
        if (this.streamingJobs == null)
            return 0;
        
        return this.streamingJobs.size();
    }
    
    @Override
    public int getNumNonStreamingJobsBeingEvaluated()
    {       
        if (this.nonStreamingJobs == null)
            return 0;
        
        return this.nonStreamingJobs.size();
    }
    
    /**
     * Synchronised access to setting the job status of the JobDetails object in 
     * the jobDetailsMap.
     * @param jobRef
     * @param status 
     */
    private synchronized void setJobStatus(String jobRef, JobStatus status)
    {
        if (jobRefToRiskMap.containsKey(jobRef))
        {
            riskJobMap.get(jobRefToRiskMap.get(jobRef)).getJobStatus().setStatus(status.getStatus());
            riskJobMap.get(jobRefToRiskMap.get(jobRef)).getJobStatus().setMetaData(status.getMetaData());
        }
    }
    
    /**
     * Synchronised access to getting the job status of the JobDetails object in 
     * the jobDetailsMap.
     * @param jobRef
     * @return 
     */
    private synchronized JobStatus getSyncJobStatus(String jobRef)
    {
        if (!jobRefToRiskMap.containsKey(jobRef))
            log.error("There is no record of the job with reference: " + jobRef);
        
        return riskJobMap.get(jobRefToRiskMap.get(jobRef)).getJobStatus();
    }
    
    /**
     * Synchronised access to getting the job status of the JobDetails object in 
     * the jobDetailsMap.
     * @param jobRef
     * @return 
     */
    private synchronized JobStatusType getSyncJobStatusType(String jobRef) throws Exception
    {
        if (!jobRefToRiskMap.containsKey(jobRef))
            log.error("There is no record of the job with reference: " + jobRef);
        
        return riskJobMap.get(jobRefToRiskMap.get(jobRef)).getJobStatus().getStatus();
    }
}
