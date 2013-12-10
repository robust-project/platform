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
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValidationObject;
import uk.ac.soton.itinnovation.robust.cat.common.ps.CommunityDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.datalayer.impl.DataLayerImpl;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.PredictorServiceClient;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.EEJobDetails;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IPredictorClient;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.PredictorConnectionDetails;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 * Predictor Client used to connect and interact with Predictor Services for
 * starting jobs and getting results.
 * 
 * @author Vegard Engen
 */
public class PredictorClient implements IPredictorClient
{
    private IDataLayer dataLayer;
    private URI eeServiceURI;
    
    // introduced for demo purposes, to set if only a preconfigured service should
    // be used, instead of getting the details from the datalayer.
    // will look for these parameters in the evalEng.properties file.
    private boolean usePreconfiguredService;
    private String serviceURI = "http://localhost:8080/robust-predictorCatSimService/predictorCatSimService";
    private String serviceNameSpace = "http://catsim.predictorservice.robust.itinnovation.soton.ac.uk/";
    private String serviceName = "PredictorServiceImplService";
    private String servicePort = "PredictorServiceImplPort";
    
    private static final Logger log = LoggerFactory.getLogger(PredictorClient.class);
    
    public PredictorClient(IDataLayer dataLayer, URI eeSrvURI) throws Exception
    {
        log.debug("Creating Predictor Service Client");
        usePreconfiguredService = false;
        getConfigs();
        this.dataLayer = dataLayer;
        this.eeServiceURI = eeSrvURI;
    }
    
    private void getConfigs()
    {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("evalEng.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file evalEng.properties. " + ex.getMessage(), ex);
        }

        try {
            usePreconfiguredService = Boolean.parseBoolean(prop.getProperty("usePreconfiguredService"));
        } catch (Exception ex) {
            log.error("Error with getting usePreconfiguredService parameter from evalEng.properties. " + ex.getMessage(), ex);
            log.error("Assuming that since it has not been defined in a valid manner, we should not use pre-configured service details");
            return;
        }
        
        if (usePreconfiguredService)
        {
            try {
                serviceURI = prop.getProperty("serviceURI");
                serviceNameSpace = prop.getProperty("serviceNameSpace");
                serviceName = prop.getProperty("serviceName");
                servicePort = prop.getProperty("servicePort");
            } catch (Exception ex) {
                log.error("Error with getting service parameter from engine.properties. " + ex.getMessage(), ex);
                return;
            }

            log.info("Using pre-configured predictor service:");
            log.info("  - serviceURI: " + serviceURI);
            log.info("  - serviceNameSpace: " + serviceNameSpace);
            log.info("  - serviceName: " + serviceName);
            log.info("  - servicePort: " + servicePort);
        }
    }

    @Override
    public Map<UUID, EEJobDetails> startEvaluationJobs(Set<Risk> riskList, Date startDate) throws Exception
    {
        Map<UUID, EEJobDetails> jobDetailsMap = new HashMap<UUID, EEJobDetails>();

        if ((riskList == null) || riskList.isEmpty())
        {
            log.debug("No risks in the list to evaluate - must be a bug somewhere!");
            return jobDetailsMap;
        }

        log.debug("Attempting to start " + riskList.size() + " evaluation jobs");
        for (Risk ro : riskList)
        {
            if (ro == null)
            {
                log.error("The risk/opportunity sent to start a job for is NULL");
                continue;
            }
            
            if (ro.getId() == null)
            {
                log.error("The risk/opportunity sent to start a job for does not have a UUID");
                continue;
            }
            
            if ((ro.getCommunity() == null) || (ro.getCommunity().getUuid() == null))
            {
                log.error("The risk/opportunity sent to start a job for does not have valid Community details (NULL)");
                continue;
            }
            
            UUID riskUUID = ro.getId();
            UUID communityUUID = ro.getCommunity().getUuid();
            boolean stream = ro.getCommunity().getIsStream();
            
            if (ro.getTitle() != null)
                log.debug("Starting evaluation job for risk/opp '" + ro.getTitle() + "' (" + ro.getId().toString() + ")");
            else
                log.debug("Starting evaluation job for risk/opp with ID '" + ro.getId().toString() + "'");
            
            log.debug("Getting the service description");
            PredictorConnectionDetails psDetails = null;
            try {
                psDetails = getPredictorServiceDescription(ro.getSetEvent());
            } catch (Exception e) {
                log.error("Failed to the predictor service details for risk/opp '" + ro.getId().toString() + "'. Exception msg: " + e.getMessage(), e);
                jobDetailsMap.put(ro.getId(), getFailedJobDetails(riskUUID, communityUUID, stream, psDetails, "Unable to get the predictor information for the risk: " + e.getMessage()));
                continue;
            }
            
            log.debug("Getting predictor service client instance for: " + psDetails.getServiceURI().toString());
            PredictorServiceClient predictorServiceClient = null;
            
            try {
                predictorServiceClient = getPredictorServiceClient(psDetails);
            } catch (Exception e) {
                log.error("Failed to create the predictor service client: " + e.getMessage(), e);
                jobDetailsMap.put(ro.getId(), getFailedJobDetails(riskUUID, communityUUID, stream, psDetails, "Unable to contact the service: " + e.getMessage()));
                continue;
            }
            
            log.debug("Getting job configuration");
            PredictorServiceJobConfig jobConfig = getPredictorServiceJobConfig(ro, startDate);
            logConfig(ro, jobConfig);
            
            log.info("Creating evaluation job");
            JobDetails jobDetails = null;
            try {
                jobDetails = predictorServiceClient.createEvaluationJob(jobConfig);
            } catch (Exception e) {
                log.error("Failed to create the evaluation job: " + e.getMessage(), e);
                jobDetailsMap.put(ro.getId(), getFailedJobDetails(riskUUID, communityUUID, stream, psDetails, "Unable to create the evaluation job: " + e.getMessage()));
                continue;
            }

            // validate the job details
            if (!isJobValid(jobDetails))
            {
                log.error("The job creation probably failed since an invalid response was given by the service");
                jobDetailsMap.put(ro.getId(), getDefaultFailedJobDetails(riskUUID, communityUUID, stream, psDetails));
                continue;
            }
            
            log.info("Job ref: " + jobDetails.getJobRef());
            log.info("Status:  " + jobDetails.getJobStatus().getStatus());

            if (jobDetails.getJobStatus().getStatus() == JobStatusType.FAILED)
            {
                if (!hasErrorMessage(jobDetails.getJobStatus()))
                {
                    log.error("The job creation failed, but no error message was provided so a default one is created");
                    jobDetails.setJobStatus(getDefaultConfigErrorJobStatus());
                } else {
                    log.error("The job creation failed: " + jobDetails.getJobStatus().getMetaData());
                }

                jobDetailsMap.put(ro.getId(), new EEJobDetails(jobDetails, riskUUID, communityUUID, stream, psDetails));
                continue;
            }
            
            log.info("Calling on the service to start evaluating job: " + jobDetails.getJobRef());
            JobStatus jobStatus = null;
            
            try {
                jobStatus = predictorServiceClient.evaluate(jobDetails.getJobRef());
            } catch (Exception e) {
                log.error("Caught an exception when trying to start the evaluation job: " + e.getMessage(), e);
                log.error("Setting a job status to FAILED");
                jobStatus = new JobStatus(JobStatusType.FAILED, e.getMessage());
            }

            // validate the jobStatus
            if (!isJobValid(jobStatus))
            {
                log.error("Status object returned is invalid!");
                jobDetails.setJobStatus(getDefaultEvalErrorJobStatus());
            } else if (jobStatus.getStatus() == JobStatusType.FAILED)
            {
                if (!hasErrorMessage(jobStatus))
                {
                    log.error("The evaluation request failed, but no message was set.");
                    jobDetails.setJobStatus(getDefaultEvalErrorJobStatus());
                } else {
                    log.error("The evaluation request failed: " + jobStatus.getMetaData());
                }
            }

            log.info("Saving the job details");
            jobDetailsMap.put(ro.getId(), new EEJobDetails(jobDetails, riskUUID, communityUUID, stream, psDetails));
        }

        return jobDetailsMap;
    }

    @Override
    public Set<EvaluationResult> getEvaluationResults(Collection<EEJobDetails> jobSet) throws Exception
    {
        Set<EvaluationResult> resultMap = new HashSet<EvaluationResult>();
        
        if ((jobSet == null) || jobSet.isEmpty())
        {
            log.error("Cannot get evaluation results because the collection of job details is NULL or empty");
            return resultMap;
        }

        // iterate over each R/O evaluation job
        for (EEJobDetails jobDetails : jobSet)
        {
            if (jobDetails == null)
            {
                log.error("The job details object is NULL - cannot check if that's finished...");
                continue;
            }
            
            if (jobDetails.getJobRef() == null)
            {
                log.error("The job details object has no job reference, so cannot check if the job is finished...");
                continue;
            }
            
            if (jobDetails.getRiskUUID() == null)
            {
                log.error("The job details object has no risk UUID specified, so cannot process the job");
                continue;
            }
            
            if (jobDetails.getPredictorConnectionDetails() == null)
            {
                log.error("The job details object has no risk Predictor Connection Details specified, so cannot process the job");
                continue;
            }
            
            log.debug("Checking if job with ID '" + jobDetails.getJobRef() + "' is finished");
            log.debug("Getting predictor service client instance for predictor with UUID: " + jobDetails.getPredictorConnectionDetails().getServiceURI().toString());
            PredictorServiceClient predictorServiceClient = null;
            try {
                predictorServiceClient = getPredictorServiceClient(jobDetails.getPredictorConnectionDetails());
            } catch (Exception e) {
                log.error("Failed to create the predictor service client to contact the service: " + e.getMessage(), e);
                jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, "Failed to create the predictor service client to contact the service: " + e.getMessage()));
                resultMap.add(getDefaultErronousEvaluationResult(jobDetails.getRiskUUID(), jobDetails));
                continue;
            }

            // get status of the job
            log.info("Checking status of job: " + jobDetails.getJobRef());
            JobStatus status = null;
            try {
                status = predictorServiceClient.getJobStatus(jobDetails.getJobRef());
            } catch (Exception e) {
                log.error("Failed to get the status of the evaluation job. Exception msg: " + e.getMessage(), e);
                jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, "Failed to get the status of the evaluation job. Exception msg: " + e.getMessage()));
                resultMap.add(getDefaultErronousEvaluationResult(jobDetails.getRiskUUID(), jobDetails));
                continue;
            }

            // check if returned status object is valid
            if (!isJobValid(status))
            {
                log.error("Status object returned is NOT valid!");
                resultMap.add(getDefaultErronousEvaluationResult(jobDetails.getRiskUUID(), jobDetails));
                continue;
            }
            
            log.info("Status: " + status.getStatus());

            // check if the status is finished - if so, save the results to the list
            if ((status.getStatus() == JobStatusType.FINISHED) || (status.getStatus() == JobStatusType.RESULT_AVAILABLE))
            //if ((status.getStatus() == JobStatusType.FINISHED) || (status.getStatus() == JobStatusType.READY) || (status.getStatus() == JobStatusType.EVALUATING))
            {
                log.info("The evaluation job finished/result available!");
                EvaluationResult res = null;
                try {
                    res = predictorServiceClient.getEvaluationResult(jobDetails.getJobRef());
                }  catch (Exception e) {
                    log.error("Failed to get the evaluation result from the predictor service client: " + e.getMessage(), e);
                    jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, "Failed to get the evaluation result from the predictor service client: " + e.getMessage()));
                    resultMap.add(getDefaultErronousEvaluationResult(jobDetails.getRiskUUID(), jobDetails));
                    continue;
                }
                
                res.setRiskUUID(jobDetails.getRiskUUID());
                
                ValidationObject vo = validateEvaluationResult(res);
                if (vo.valid)
                {
                    log.info("The result object returned is valid");
                    jobDetails.setJobStatus(status);
                    if (res.getJobDetails() == null)
                        res.setJobDetails(jobDetails);
                    
                    resultMap.add(res);
                }
                else
                {
                    log.error("The result object was not valid, so returning a default erroronous result object");
                    jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, "The result object returned from the web service was not valid: " + vo.msg));
                    resultMap.add(getDefaultErronousEvaluationResult(jobDetails.getRiskUUID(), jobDetails));
                }
            }
            else if ((status.getStatus() == JobStatusType.FAILED) || (status.getStatus() == JobStatusType.ERROR))
            {
                log.error("The evaluation job failed!");

                if (!hasErrorMessage(status))
                {
                    log.error("No error message was given, so a default message is generated");
                    jobDetails.setJobStatus(this.getDefaultEvalErrorJobStatus());
                }
                else
                {
                    jobDetails.setJobStatus(status);
                    log.error("The error message given by the service: " + status.getMetaData());
                }

                log.debug("Saving erroronous result");
                resultMap.add(getDefaultErronousEvaluationResult(jobDetails.getRiskUUID(), jobDetails));
            }
            else if (status.getStatus() == JobStatusType.CANCELLED)
            {
                log.info("The evaluation job was cancelled!");

                if (!hasErrorMessage(status))
                {
                    log.debug("No error message was given, so a default message is generated");
                    jobDetails.setJobStatus(this.getDefaultEvalCancelledJobStatus());
                }
                else
                {
                    jobDetails.setJobStatus(status);
                    log.debug("The error message given by the service: " + status.getMetaData());
                }

                log.debug("Saving cancelled result");
                resultMap.add(getDefaultErronousEvaluationResult(jobDetails.getRiskUUID(), jobDetails));
            }
        }

        return resultMap;
    }

    public void cancelEvaluationJobs(Collection<EEJobDetails> jobSet) throws Exception
    {
        if ((jobSet == null) || jobSet.isEmpty())
        {
            log.error("Cannot cancel evaluation jobs because the collection of job details is NULL or empty");
            return;
        }

        // iterate over each R/O evaluation job
        for (EEJobDetails jobDetails : jobSet)
        {
            if (jobDetails == null)
            {
                log.error("The job details object is NULL - cannot check if that's finished...");
                continue;
            }
            
            if (jobDetails.getJobRef() == null)
            {
                log.error("The job details object has no job reference, so cannot check if the job is finished...");
                continue;
            }
            
            if (jobDetails.getRiskUUID() == null)
            {
                log.error("The job details object has no risk UUID specified, so cannot process the job");
                continue;
            }
            
            if (jobDetails.getPredictorConnectionDetails() == null)
            {
                log.error("The job details object has no risk Predictor Connection Details specified, so cannot process the job");
                continue;
            }
            
            log.info("Cancelling job with ID '" + jobDetails.getJobRef() + "'");
            log.debug("Getting predictor service client instance for predictor with UUID: " + jobDetails.getPredictorConnectionDetails().getServiceURI().toString());
            PredictorServiceClient predictorServiceClient = null;
            try {
                predictorServiceClient = getPredictorServiceClient(jobDetails.getPredictorConnectionDetails());
            } catch (Exception e) {
                log.error("Failed to create the predictor service client to contact the service: " + e.getMessage(), e);
                jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, "Failed to create the predictor service client to contact the service: " + e.getMessage()));
                continue;
            }

            JobStatus status = null;
            try {
                status = predictorServiceClient.cancelJob(jobDetails.getJobRef());
            } catch (Exception e) {
                log.error("Failed to cancel the evaluation job. Exception msg: " + e.getMessage(), e);
                jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, "Failed to cancel the evaluation job. Exception msg: " + e.getMessage()));
                continue;
            }

            // check if returned status object is valid
            if (!isJobValid(status))
            {
                log.error("Status object returned is NOT valid!");
                continue;
            }
            
            log.debug("Status: " + status.getStatus());
            jobDetails.setJobStatus(status);
        }
    }

    
    private boolean isJobValid(JobDetails jobDetails)
    {
        if (jobDetails == null)
        {
            return false;
        }

        if (jobDetails.getJobRef() == null)
        {
            return false;
        }

        if (jobDetails.getRequestDate() == null)
        {
            return false;
        }

        return isJobValid(jobDetails.getJobStatus());
    }

    private boolean isJobValid(JobStatus jobStatus)
    {
        if (jobStatus == null)
        {
            return false;
        }

        if (jobStatus.getStatus() == null)
        {
            return false;
        }

        //if (jobStatus.getMetaData() == null)
        //   return false;

        return true;
    }

    private boolean hasErrorMessage(JobStatus jobStatus)
    {
        if (jobStatus == null)
        {
            return false;
        }

        if (jobStatus.getStatus() == null)
        {
            return false;
        }

        if (jobStatus.getMetaData() == null)
        {
            return false;
        }

        return true;
    }

    private EEJobDetails getDefaultErrorJobDetails(UUID riskUUID, UUID communityUUID, boolean stream, PredictorConnectionDetails psDetails)
    {
        EEJobDetails jobDetails = new EEJobDetails(riskUUID, communityUUID, stream, psDetails);
        jobDetails.setJobStatus(new JobStatus(JobStatusType.ERROR, "The call to the service did not return valid data"));

        return jobDetails;
    }
    
    private EEJobDetails getDefaultFailedJobDetails(UUID riskUUID, UUID communityUUID, boolean stream, PredictorConnectionDetails psDetails)
    {
        EEJobDetails jobDetails = new EEJobDetails(riskUUID, communityUUID, stream, psDetails);
        jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, "The call to the service did not return valid data"));

        return jobDetails;
    }
    
    private EEJobDetails getFailedJobDetails(UUID riskUUID, UUID communityUUID, boolean stream, PredictorConnectionDetails psDetails, String errorMsg)
    {
        EEJobDetails jobDetails = new EEJobDetails(riskUUID, communityUUID, stream, psDetails);
        if (errorMsg == null)
            jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, "The call to the service did not return valid data"));
        else
            jobDetails.setJobStatus(new JobStatus(JobStatusType.FAILED, errorMsg));

        return jobDetails;
    }

    private JobStatus getDefaultConfigErrorJobStatus()
    {
        return new JobStatus(JobStatusType.FAILED, "The service did not return an error message when a job creation failed");
    }

    private JobStatus getDefaultEvalErrorJobStatus()
    {
        return new JobStatus(JobStatusType.FAILED, "The service did not return an error message when the evaluation call failed");
    }
    
    private JobStatus getDefaultEvalCancelledJobStatus()
    {
        return new JobStatus(JobStatusType.CANCELLED, "Evaluation job cancelled");
    }

    
    private ValidationObject validateEvaluationResult(EvaluationResult res)
    {
        if (res == null)
        {
            log.error("EvaluationResult is NULL, so not valid...");
            return new ValidationObject(false, "EvaluationResult is NULL");
        }
        
        if (res.getResultItems() == null)
        {
            log.error("The EvaluationResult has got no actual results, so not valid...");
            return new ValidationObject(false, "The EvaluationResult has got no actual results");
        }
        
        if (res.getForecastDate() == null)
        {
            log.error("The EvaluationResult has got no forecast date, so not valid...");
            return new ValidationObject(false, "The EvaluationResult has got no forecast date specified");
        }
        
        if (res.getCurrentDate() == null)
        {
            log.error("The EvaluationResult has got no current date, so not valid...");
            return new ValidationObject(false, "The EvaluationResult has got no current date specified");
        }
        
        return new ValidationObject(true);
    }

    private EvaluationResult getDefaultErronousEvaluationResult(UUID riskUUID, EEJobDetails jobDetails)
    {
        EvaluationResult res = new EvaluationResult();
        res.setRiskUUID(riskUUID);
        jobDetails.setJobStatus(getDefaultEvalErrorJobStatus());
        res.setJobDetails(jobDetails);
        return res;
    }
    
    private PredictorConnectionDetails getPredictorServiceDescription(Set<Event> evt)
    {
        PredictorConnectionDetails psDetails = null;
        
        if (usePreconfiguredService)
        {
            log.debug("  - Using pre-configured service details");
            try {
                psDetails = new PredictorConnectionDetails();
                psDetails.setServiceURI(new URI(serviceURI));
                psDetails.setServiceTargetNamespace(serviceNameSpace);
                psDetails.setServiceName(serviceName);
                psDetails.setServicePortName(servicePort);
            } catch (URISyntaxException ex) {
                log.error("Unable to create URI object from " + serviceURI, ex);
            }
        }
        else
        {
            log.debug("  - Getting service details from the datalayer");
            PredictorServiceDescription desc = dataLayer.getPredictor(evt.iterator().next());
            
            if (desc == null)
                return null;
            
            psDetails = new PredictorConnectionDetails();
            psDetails.setPredictorUUID(desc.getUuid());
            psDetails.setServiceURI(desc.getServiceURI());
            psDetails.setServiceTargetNamespace(desc.getServiceTargetNamespace());
            psDetails.setServiceName(desc.getServiceName());
            psDetails.setServicePortName(desc.getServicePortName());
        }
        
        return psDetails;
    }
    
    private PredictorServiceClient getPredictorServiceClient(PredictorConnectionDetails psDetails) throws Exception
    {
        PredictorServiceClient predictorServiceClient = new PredictorServiceClient(psDetails.getServiceURI().toString(), psDetails.getServiceTargetNamespace(), psDetails.getServiceName(), psDetails.getServicePortName());
        
        return predictorServiceClient;
    }
    
    private PredictorServiceJobConfig getPredictorServiceJobConfig(Risk risk, Date startDate)
    {
        PredictorServiceJobConfig jobConfig = new PredictorServiceJobConfig();
        jobConfig.setStartDate(startDate);
        jobConfig.setEvents(risk.getSetEvent());
        jobConfig.setCommunityDetails(new CommunityDetails(risk.getCommunity().getName(), risk.getCommunity().getCommunityID()));
        jobConfig.setStreaming(risk.getCommunity().getIsStream());
        if (risk.getCommunity().getIsStream()) {
            StreamDetails sd = new StreamDetails(risk.getCommunity().getStreamName(), risk.getCommunity().getUri());
            jobConfig.setStreamDetails(sd);
        }
        jobConfig.setEvaluationEngineServiceURI(eeServiceURI);
        
        // setting forecast period - need to check if it has been set in the risk object first, though
        PredictorServiceDescription psd = dataLayer.getPredictor(risk.getSetEvent().iterator().next());
        if ((psd != null) && (psd.getForecastPeriod() != null) && (psd.getForecastPeriod().getValue() != null)) {
            jobConfig.setForecastPeriod(psd.getForecastPeriod());
        } else {
            log.debug("Setting default forecast period because...");
            if (psd == null){
                log.debug("psd == null");
            } else if (psd.getForecastPeriod() == null) {
                log.debug("psd.getForecastPeriod() == null");
            } else if (psd.getForecastPeriod().getValue() == null) {
                log.debug("psd.getForecastPeriod().getValue() == null");
            }
            jobConfig.setForecastPeriod(new Parameter(ParameterValueType.INT, "Forecast Period", "", "days", new ParameterValue("7")));
        }
        
        if (psd.getConfigurationParams() != null) {
            jobConfig.setConfigurationParams(psd.getConfigurationParams());
        }
        
        return jobConfig;
    }
    
    private void logConfig(Risk ro, PredictorServiceJobConfig pjc)
    {
        try {
        log.debug("The PredictorServiceJobConfig details for risk '" + ro.getTitle() + "' (UUID " + ro.getId().toString() + ")");
        
        log.debug(" * Num events: " + pjc.getEvents().size());
        for (Event evt : pjc.getEvents())
        {
            if (evt == null) {
                log.debug("    - Got a NULL event...");
                continue;
            }
            
            if (evt.getTitle() == null) {
                log.debug("    - Got a event with no name...");
                continue;
            }
            
            log.debug("    - " + evt.getTitle() + " (" + evt.getUuid().toString() + ")");
            
            if (evt.getEventConditions() == null) {
                log.debug("    - Event condition collection is NULL");
                continue;
            }
            
            log.debug("    - Conditions: " + evt.getEventConditions().size());
            for (EventCondition ec : evt.getEventConditions())
            {
                if (ec == null) {
                    log.debug("      - Got a NULL condition...");
                    continue;
                }
                
                if (ec.getName() == null) {
                    log.debug("      - Got a condition with no name...");
                    continue;
                }
                
                log.debug("      - " + ec.getName() + " (" + ec.getUUID().toString() + ")");
                
                if (ec.getPreConditionValue() != null)
                {
                    
                    if (ec.getPreConditionValue().getValue() == null) {
                        log.debug("        - pre-condition: no value");
                    } else {
                        log.debug("        - pre-condition: " + ec.getPostConditionValue().getValue());
                    }
                } else {
                    log.debug("        - pre-condition not set");
                }
                
                if (ec.getPostConditionValue() != null)
                {
                    if (ec.getPostConditionValue().getValue() == null) {
                        log.debug("        - post-condition: no value");
                    } else {
                        log.debug("        - post-condition: " + ec.getPostConditionValue().getValue());
                    }
                } else {
                    log.debug("        - post-condition not set");
                }
            }
            
            if (evt.getConfigParams() != null)
            {
                log.debug("    - Configs: " + evt.getConfigParams().size());
                for (Parameter p : evt.getConfigParams())
                {
                    if (p == null) {
                        log.debug("      - Got a NULL parameter...");
                        continue;
                    }

                    if (p.getName() == null) {
                        log.debug("      - Got a parameter with no name...");
                        continue;
                    }

                    if (p.getValue() != null)
                    {
                        if (p.getValue().getValue() != null) {
                            log.debug("      - " + p.getName() + " (" + p.getUUID() + "): " + p.getValue().getValue());
                        } else {
                            log.debug("      - " + p.getName() + " (" + p.getUUID() + "): no value");
                        }
                    } else {
                        log.debug("      - parameter " + p.getName() + " (" + p.getUUID() + ") value not set");
                    }
                }
            } else {
                log.debug("    - No event config parameters");
            }
        }
        
        if (pjc.getConfigurationParams() != null)
        {
            log.debug(" * Num config params: " + pjc.getConfigurationParams().size());
            for (Parameter p : pjc.getConfigurationParams())
            {
                if (p == null) {
                    log.debug("    - Got a NULL parameter...");
                    continue;
                }

                if (p.getName() == null) {
                    log.debug("    - Got a parameter with no name...");
                    continue;
                }
                if (p.getValue() != null)
                {
                    if (p.getValue().getValue() != null) {
                        log.debug("      - " + p.getName() + " (" + p.getUUID() + "): " + p.getValue().getValue());
                    } else {
                        log.debug("      - " + p.getName() + " (" + p.getUUID() + "): no value");
                    }
                } else {
                    log.debug("      - parameter " + p.getName() + " (" + p.getUUID() + ") value not set");
                }
            }
        }
        
        log.debug(" * Start date:      " + pjc.getStartDate());
        log.debug(" * Forecast period: " + pjc.getForecastPeriod().getValue().getValue());
        log.debug(" * Community details:");
        log.debug("   - Community name: " + pjc.getCommunityDetails().getCommunityName());
        log.debug("   - Community ID:   " + pjc.getCommunityDetails().getCommunityID());
        } catch (Exception ex) { log.debug("Exception thrown when logging the job config details: " + ex.toString()); }
    }
}
