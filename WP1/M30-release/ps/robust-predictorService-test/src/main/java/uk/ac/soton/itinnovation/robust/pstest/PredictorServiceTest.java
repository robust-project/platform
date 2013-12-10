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
package uk.ac.soton.itinnovation.robust.pstest;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValidationObject;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.PredictorServiceClient;

/**
 *
 * @author ve
 */
public class PredictorServiceTest
{
    private static final Logger log = LoggerFactory.getLogger(PredictorServiceTest.class);

    public static void main(String[] args) throws Exception
    {
        PredictorServiceTest test = new PredictorServiceTest();
        
        // General PS parameter + URI of the evaluation engine
        String psServiceURI = "";
        String psNameSpace = "";
        String psServiceName = "";
        String psPortName = "";
        String eeServiceURI = "";
        
        // ID of the community the PS is to be tested on
        String communityName = "";
        String communityID = "";
        Date startDate = DateUtil.getDateObject("2010-01-01");
        
        // Streaming related parameters - if testing the PS on streaming data
        boolean isStreaming = false;
        String streamName = "";
        String streamURI = "";
        
        if (args.length == 8)
        {
            log.info("Setting up config parameters from command line arguments - no streaming parameters given");
            psServiceURI = args[0];
            psNameSpace = args[1];
            psServiceName = args[2];
            psPortName = args[3];
            eeServiceURI = args[4];
            communityName = args[5];
            communityID = args[6];
            startDate = DateUtil.getDateObject(args[7]);
            isStreaming = false;
        }
        else if (args.length == 11)
        {
            log.info("Setting up config parameters from command line arguments - streaming parameters given");
            psServiceURI = args[0];
            psNameSpace = args[1];
            psServiceName = args[2];
            psPortName = args[3];
            eeServiceURI = args[4];
            communityName = args[5];
            communityID = args[6];
            startDate = DateUtil.getDateObject(args[7]);
            isStreaming = Boolean.parseBoolean(args[8]);
            if (isStreaming)
            {
                streamName = args[9];
                streamURI = args[10];
            }
        }
        else
        {
            log.info("Setting up config parameters from pre-configured Java code");
            
            /* Template Predictor Service v 1.5 */
            psServiceURI = "http://localhost:8080/predictorServiceTemplateStreaming-1.5/service";
            psNameSpace = "http://tmpl.ps.robust.itinnovation.soton.ac.uk/";
            psServiceName = "PredictorServiceImplService";
            psPortName = "PredictorServiceImplPort";
            eeServiceURI = "http://localhost:8080/evaluationEngineService-1.5/service";
            
            communityName = "Test community";
            communityID = "http://community.com/forum/123#id";
            isStreaming = false;
        }
        
        log.info(" - PS service URI:  " + psServiceURI);
        log.info(" - PS namespace:    " + psNameSpace);
        log.info(" - PS service name: " + psServiceName);
        log.info(" - PS port name:    " + psPortName);
        log.info(" - EE service URI:  " + eeServiceURI);
        
        log.info(" - Community name:  " + communityName);
        log.info(" - Community ID:    " + communityID);
        log.info(" - Start date:      " + DateUtil.getDateString(startDate));
        log.info(" - Is streaming:    " + isStreaming);
        
        if (isStreaming)
        {
            log.info(" - Stream name:     " + streamName);
            log.info(" - Stream URI:      " + streamURI);
        }
        
        test(psServiceURI, psNameSpace, psServiceName, psPortName, eeServiceURI, communityName, communityID, startDate, isStreaming, streamName, streamURI);
    }
    
    /**
     * Test method that will connect to the Predictor Service according to the service
     * details provided and will: 1) get the PredictorServiceDescription, 2) create
     * a new evaluation job based on the description, and 3) wait for the results.
     * @param psServiceURI Predictor Service URI.
     * @param nameSpace Predictor Service namespace.
     * @param serviceName Predictor Service name.
     * @param portName Predictor Service port name.
     * @param eeServiceURI Evaluation Engine Service URI.
     * @param communityName Community name used when creating an evaluation job.
     * @param communityID Community ID used when creating an evaluation job.
     * @param startDate The start date for the evaluation job.
     * @param isStreaming Flag to indicate if streaming case, used when creating an evaluation job.
     * @param streamName Community data stream name used when creating an evaluation job.
     * @param streamURI Community data stream URI used when creating an evaluation job.
     * @throws Exception Any errors are logged and thrown.
     */
    private static void test(String psServiceURI, String nameSpace, String serviceName, 
            String portName, String eeServiceURI, String communityName, String communityID, 
            Date startDate, boolean isStreaming, String streamName, String streamURI) throws Exception
    {
        log.info("Starting web-service client");
        log.info("----------------------------");
        
        PredictorServiceClient predictorServiceClient = new PredictorServiceClient(psServiceURI, nameSpace, serviceName, portName);

        log.info("Getting PredictorServiceDescription");
        PredictorServiceDescription psDesc = predictorServiceClient.getPredictorServiceDescription();
        
        ValidationObject vo = PSDValidator.isPredictorServiceDescriptionValid(psDesc);
        if (!vo.valid)
        {
            log.info("The PredictorServiceDescription is not valid: " + vo.msg);
            return;
        }
        
        log.info("Service name: " + psDesc.getName());
        
        if ((psDesc.getEvents() != null) && !psDesc.getEvents().isEmpty())
        {
            log.info("Num events: " + psDesc.getEvents().size());
            for (Event e : psDesc.getEvents()) {
                log.info(" - " + e.getTitle());
            }
        }
        else
        {
            log.error("No events gotten from the PSD");
        }
        
        if ((psDesc.getConfigurationParams() != null) && !psDesc.getConfigurationParams().isEmpty())
        {
            log.info("Configuration parameters:");
            for (Parameter p : psDesc.getConfigurationParams())
            {
                if (p == null) {
                    log.error("p is NULL");
                } else if (p.getName() == null) {
                    log.error("p.getName() == null");
                }
                
                try {
                    String defaultValue = p.getDefaultValue();
                    log.info(" - " + p.getName() + ": " + defaultValue);
                } catch (Exception e) {
                    log.info(" - " + p.getName());
                }
            }
        }
        
        log.info("Creating job configuration object based on available events and configuration parameters");
        PredictorServiceJobConfig jobConfig = getPredictorServiceJobConfig(psDesc, startDate, eeServiceURI, communityName, communityID, isStreaming, streamName, streamURI);
        
        log.info("Creating a new evaluation job");
        JobDetails jobDetails = predictorServiceClient.createEvaluationJob(jobConfig);
        
        log.info("Job reference: " + jobDetails.getJobRef());
        log.info("Job status: " + jobDetails.getJobStatus().getStatus());

        if ((jobDetails.getJobStatus().getStatus().equals(JobStatusType.FAILED)) ||
            (jobDetails.getJobStatus().getStatus().equals(JobStatusType.ERROR)))
        {
            log.error("ERROR MSG: " + jobDetails.getJobStatus().getMetaData());
            return;
        }
        
        log.info("Starting evaluation job: " + jobDetails.getJobRef());
        JobStatus jobStatus = predictorServiceClient.evaluate(jobDetails.getJobRef());
        log.info("Job status: " + jobStatus.getStatus());
        
        if ((jobStatus.getStatus().equals(JobStatusType.FAILED)) ||
            (jobStatus.getStatus().equals(JobStatusType.ERROR)))
        {
            log.error("ERROR MSG: " + jobStatus.getMetaData());
            return;
        }
        
        boolean polling = true;
        while (polling)
        {
            log.info("Checking status of job: " + jobDetails.getJobRef());
            jobStatus = predictorServiceClient.getJobStatus(jobDetails.getJobRef());
            log.info("Job status: " + jobStatus.getStatus());
            
            if ((jobStatus.getStatus().equals(JobStatusType.FINISHED)) || (jobStatus.getStatus().equals(JobStatusType.RESULT_AVAILABLE)))
            {
                EvaluationResult evaluationResult = predictorServiceClient.getEvaluationResult(jobDetails.getJobRef());
                if (evaluationResult == null) {
                    log.error("evaluationResult == null");
                } else if (evaluationResult.getForecastDate() == null) {
                    log.error("evaluationResult.getForecastDate() == null");
                }
                log.info("Results from running job with id " + jobDetails.getJobRef() + ":");
                log.info(" - Forecast date: " + DateUtil.getDateString(evaluationResult.getForecastDate()));

                List<ResultItem> resultItems = evaluationResult.getResultItems();
                if ((resultItems != null) && !resultItems.isEmpty())
                {
                    for (ResultItem ri : resultItems) {
                        log.info(" - " + ri.getName() + ": " + ri.getProbability());
                    }
                }
                else
                {
                    log.info("No results :(");
                }
                break;
            }
            else if ((jobStatus.getStatus().equals(JobStatusType.FINISHED)) ||
                     (jobStatus.getStatus().equals(JobStatusType.ERROR)))
            {
                log.error("There was an error in the evaluation: " + jobStatus.getMetaData());
                break;
            }
            
            Thread.sleep(5000);
        }
        
        if (isStreaming)
        {
            log.info("Cancelling streaming job: " + jobDetails.getJobRef());
            jobStatus = predictorServiceClient.cancelJob(jobDetails.getJobRef());
            
            if (jobStatus != null)
            {
                log.info(" - status: " + jobStatus.getStatus());
                if ((jobStatus.getMetaData() != null) && !jobStatus.getMetaData().isEmpty()) {
                    log.info(" - meta-data: " + jobStatus.getMetaData());
                }
            }
        }
        
        log.info("----------------------------");
        log.info("Done");
    }
    
    /**
     * Creates a PredictorServiceJobConfig object according to the given parameters.
     * Will take only one event, if more defined in the PredictorServiceDescription
     * object, and will set values for the event and other configuration parameters
     * according to the default values given.
     * @param psDesc PredictorServiceDescription object, used to create the job config from.
     * @param startDate The start date of the evaluation job.
     * @param eeServiceURI Evaluation Engine Service URI.
     * @param communityName Community name used when creating an evaluation job.
     * @param communityID Community ID used when creating an evaluation job.
     * @param isStreaming Flag to indicate if streaming case, used when creating an evaluation job.
     * @param streamName Community data stream name used when creating an evaluation job.
     * @param streamURI Community data stream URI used when creating an evaluation job.
     * @return
     * @throws Exception 
     */
    private static PredictorServiceJobConfig getPredictorServiceJobConfig(PredictorServiceDescription psDesc, 
            Date startDate, String eeServiceURI, String communityName, String communityID, boolean isStreaming, 
            String streamName, String streamURI) throws Exception
    {
        PredictorServiceJobConfig jobConfig = new PredictorServiceJobConfig();
        jobConfig.setStartDate(startDate);
        jobConfig.setEvaluationEngineServiceURI(new URI(eeServiceURI));
        jobConfig.setCommunityDetails(new CommunityDetails(communityName, communityID));
        jobConfig.setStreaming(isStreaming);
        if (isStreaming) {
            jobConfig.setStreamDetails(new StreamDetails(streamName, new URI(streamURI)));
        }
		
		jobConfig.setForecastPeriod(psDesc.getForecastPeriod());
        if (psDesc.getForecastPeriod().getDefaultValue() != null) {
            jobConfig.getForecastPeriod().setValue(new ParameterValue(jobConfig.getForecastPeriod().getDefaultValue()));
        } else if (psDesc.getForecastPeriod().getValue() == null) {
            log.warn("Forecast period had no default value, and no fixed value was set, so this may lead to errors at Predictor Service side");
        }
        
        // set event - taking the first if more than one
        Set<Event> events = new HashSet<Event>();
        Event event = psDesc.getEvents().iterator().next();
        events.add(event);
        
        // set the post condition value to the default value
        for (EventCondition ec : event.getEventConditions()) {
            ParameterValue paramVal = new ParameterValue(ec.getDefaultValue());
            if (ec.getAllowedEvaluationTypes().isEmpty()) {
                ec.setAllowedEvaluationTypes(EvaluationType.getAllowedEvaluationTypes(ec.getType()));
            }
            paramVal.setValueEvaluationType(ec.getAllowedEvaluationTypes().get(0));
            ec.setPostConditionValue(paramVal);
        }
        
        // set default values for any event config parameters
        if ((event.getConfigParams() != null) && !event.getConfigParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psDesc.getConfigurationParams()) {
                ParameterValue paramVal = new ParameterValue(p.getDefaultValue());
                if (!p.getAllowedEvaluationTypes().isEmpty()) {
                    paramVal.setValueEvaluationType(p.getAllowedEvaluationTypes().get(0));
                }
                p.setValue(paramVal);
            }
        }
        jobConfig.setEvents(events);
        
        // set config paramters with default values
        if ((psDesc.getConfigurationParams() != null) && !psDesc.getConfigurationParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psDesc.getConfigurationParams()) {
                ParameterValue paramVal = new ParameterValue(p.getDefaultValue());
                if (!p.getAllowedEvaluationTypes().isEmpty()) {
                    paramVal.setValueEvaluationType(p.getAllowedEvaluationTypes().get(0));
                }
                p.setValue(paramVal);
            }
            
            jobConfig.setConfigurationParams(psDesc.getConfigurationParams()); // don't think this is required
        }
        
        if ((psDesc.getConfigurationParams() != null) && !psDesc.getConfigurationParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psDesc.getConfigurationParams()) {
                ParameterValue paramVal = new ParameterValue(p.getDefaultValue());
                if (!p.getAllowedEvaluationTypes().isEmpty()) {
                    paramVal.setValueEvaluationType(p.getAllowedEvaluationTypes().get(0));
                }
                p.setValue(paramVal);
            }
            
            jobConfig.setConfigurationParams(psDesc.getConfigurationParams());
        }
        
        return jobConfig;
    }
}
