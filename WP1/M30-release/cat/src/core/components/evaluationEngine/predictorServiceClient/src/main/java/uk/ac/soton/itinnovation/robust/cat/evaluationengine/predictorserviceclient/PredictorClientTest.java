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
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 *
 * @author ve
 */
public class PredictorClientTest
{
    private static final Logger log = LoggerFactory.getLogger(PredictorClientTest.class);

    public static void main(String[] args) throws Exception
    {
        PredictorClientTest test = new PredictorClientTest();
        
        String eeServiceURI = "";
        String psServiceURI = "";
        String nameSpace = "";
        String serviceName = "";
        String portName = "";
        
        if (args.length == 6)
        {
            log.info("Setting up predictor client from command line arguments");
            eeServiceURI = args[0];
            psServiceURI = args[1];
            nameSpace = args[2];
            serviceName = args[3];
            portName = args[4];
        }
        else
        {
            log.info("Setting up predictor client from pre-configured Java code");
            
            eeServiceURI = "http://localhost:8080/evaluationEngineService-1.4-SNAPSHOT/service";
            
            /* Sim *
            serviceURI = "http://localhost:8080/catSimPredictorService/service"; // local service
            //serviceURI = "http://gucio.softwaremind.pl:8190/predictorCatSimService/predictorCatSimService"; // SMIND service
            //serviceURI = "http://robust-www.softwaremind.pl/PredictorService/"; // SMIND JBI
            nameSpace = "http://catsim.ps.robust.itinnovation.soton.ac.uk/";
            serviceName = "CatSimPredictorServiceService";
            portName = "CatSimPredictorServicePort";
            /**/

            /* Demo Risk Predictor *
            serviceURI = "http://localhost:8080/riskPredictorServiceDemo/service";
            nameSpace = "http://impl.riskdemo.ps.robust.itinnovation.soton.ac.uk/";
            serviceName = "PredictorServiceImplService";
            portName = "PredictorServiceImplPort";
            /**/
            
            /* Demo Opportunity Predictor *
            serviceURI = "http://localhost:8080/opportunityPredictorServiceDemo/service";
            nameSpace = "http://impl.oppdemo.ps.robust.itinnovation.soton.ac.uk/";
            serviceName = "PredictorServiceImplService";
            portName = "PredictorServiceImplPort";
            /**/
            
            /* Template Predictor 1.1.1 *
            serviceURI = "http://localhost:8080/predictorServiceDummy-1.1.1/service";
            nameSpace = "http://dummy.predictorservice.robust.itinnovation.soton.ac.uk/";
            serviceName = "PredictorServiceImplService";
            portName = "PredictorServiceImplPort";
            /**/
            
            /* Template Predictor 1.4 */
            psServiceURI = "http://localhost:8080/predictorServiceTemplate-1.4/service";
            nameSpace = "http://tmpl.ps.robust.itinnovation.soton.ac.uk/";
            serviceName = "PredictorServiceImplService";
            portName = "PredictorServiceImplPort";
            /**/
            
            /* Template Predictor Streaming 1.3 *
            serviceURI = "http://localhost:8080/predictorServiceTemplateStreaming-1.3/service";
            nameSpace = "http://tmpl.ps.robust.itinnovation.soton.ac.uk/";
            serviceName = "PredictorServiceImplService";
            portName = "PredictorServiceImplPort";
            /**/
            
            /* CM Predictor 1.3 *
            serviceURI = "http://localhost:8080/predictorServiceCM-1.3/service";
            nameSpace = "http://cm.ps.robust.cormsis.soton.ac.uk/";
            serviceName = "PredictorServiceImplService";
            portName = "PredictorServiceImplPort";
            /**/
            
            /* GS Predictor 1.3 *
            serviceURI = "http://localhost:8080/predictorServiceGS-1.3/service";
            nameSpace = "http://gs.ps.robust.cormsis.soton.ac.uk/";
            serviceName = "PredictorServiceImplService";
            portName = "PredictorServiceImplPort";
            /**/
        }
        
        boolean streaming = false;
        //Random rand = new Random();
        //if (rand.nextBoolean())
            test.test(eeServiceURI, psServiceURI, nameSpace, serviceName, portName, streaming);
        //else
        //    test.cancelTest(serviceURI, nameSpace, serviceName, portName);
    }
    
    private void test(String eeServiceURI, String serviceURI, String nameSpace, String serviceName, String portName, boolean streaming) throws Exception
    {
        log.info("Starting web-service client");
        log.info("----------------------------");
        
        PredictorServiceClient predictorServiceClient = new PredictorServiceClient(serviceURI, nameSpace, serviceName, portName);

        log.info("Getting PredictorServiceDescription");
        PredictorServiceDescription predictorServiceDesc = predictorServiceClient.getPredictorServiceDescription();
        log.info("Service name: " + predictorServiceDesc.getName());
        
        if ((predictorServiceDesc.getEvents() != null) && !predictorServiceDesc.getEvents().isEmpty())
        {
            log.info("Num events: " + predictorServiceDesc.getEvents().size());
            for (Event e : predictorServiceDesc.getEvents()) {
                log.info(" - " + e.getTitle());
            }
        }
        else
        {
            log.error("No events gotten from the PSD");
        }
        
        if ((predictorServiceDesc.getConfigurationParams() != null) && !predictorServiceDesc.getConfigurationParams().isEmpty())
        {
            log.info("Configuration parameters:");
            for (Parameter p : predictorServiceDesc.getConfigurationParams())
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
        PredictorServiceJobConfig jobConfig = getPredictorServiceJobConfig(eeServiceURI, DateUtil.getDateObject("2010-01-01"), predictorServiceDesc.getEvents().iterator().next(), predictorServiceDesc.getConfigurationParams(), streaming);
        
        log.info("Creating a new evaluation job");
        JobDetails jobDetails = predictorServiceClient.createEvaluationJob(jobConfig);
        
        log.info("Job reference: " + jobDetails.getJobRef());
        log.info("Job status: " + jobDetails.getJobStatus().getStatus());

        if ((jobDetails.getJobStatus().getStatus() == JobStatusType.FAILED) ||
            (jobDetails.getJobStatus().getStatus() == JobStatusType.ERROR))
        {
            log.error("ERROR MSG: " + jobDetails.getJobStatus().getMetaData());
            return;
        }
        
        log.info("Starting evaluation job: " + jobDetails.getJobRef());
        JobStatus jobStatus = predictorServiceClient.evaluate(jobDetails.getJobRef());
        log.info("Job status: " + jobStatus.getStatus());
        
        if ((jobStatus.getStatus() == JobStatusType.FAILED) ||
            (jobStatus.getStatus() == JobStatusType.ERROR))
        {
            log.error("ERROR MSG: " + jobStatus.getMetaData());
            return;
        }
        
        log.info("Sleeping for a bit before checking the job status");
        Thread.sleep(5000);
        
        boolean gotResultOrError = false;
        int counter = 0;
        int maxPolls = 30;
        while (counter < maxPolls)
        {
            log.info("Checking status of job: " + jobDetails.getJobRef());
            jobStatus = predictorServiceClient.getJobStatus(jobDetails.getJobRef());
            counter++;
            
            log.info("Job status: " + jobStatus.getStatus());
            
            if ((jobStatus.getStatus() == JobStatusType.FINISHED) || (jobConfig.isStreaming() && (jobStatus.getStatus() == JobStatusType.RESULT_AVAILABLE)))
            {
                EvaluationResult evaluationResult = predictorServiceClient.getEvaluationResult(jobDetails.getJobRef());
                if (evaluationResult == null) {
                    log.error("evaluationResult == null");
                } else if (evaluationResult.getForecastDate() == null) {
                    log.error("evaluationResult.getForecastDate() == null");
                }
                log.info("Results from running job with id " + jobDetails.getJobRef() + ":");
                log.info(" - Forecast date: " + DateUtil.getDateString(evaluationResult.getForecastDate()));
                /*if (evaluationResult.getCurrentObservation() != null) {
                    log.info(" - Current obs: " + evaluationResult.getCurrentObservation());
                }*/

                List<ResultItem> resultItems = evaluationResult.getResultItems();
                if ((resultItems != null) && !resultItems.isEmpty())
                {
                    log.info(" - Result items:");
                    for (ResultItem ri : resultItems) {
                        log.info("   - " + ri.getName() + ": " + ri.getProbability() + ", " + ri.getCurrentObservation());
                    }
                }
                else
                {
                    log.info(" - No results :(");
                }
                
                if ((evaluationResult.getMetaData() != null) && !evaluationResult.getMetaData().isEmpty())
                {
                    log.info(" - Meta-data");
                    for (KeyValuePair kvp : evaluationResult.getMetaData()) {
                        log.info("   - " + kvp.getKey() + ", " + kvp.getValue());
                    }
                }
                
                gotResultOrError = true;
                break;
            }
            else if ((jobStatus.getStatus() == JobStatusType.CANCELLED) ||
                     (jobStatus.getStatus() == JobStatusType.ERROR))
            {
                log.error("There was an error in the evaluation: " + jobStatus.getMetaData());
                gotResultOrError = true;
                break;
            }
            
            Thread.sleep(5000);
        }
        
        if (!gotResultOrError || jobConfig.isStreaming())
        {
            log.info("");
            log.info("Cancelling job");
            jobStatus = predictorServiceClient.cancelJob(jobDetails.getJobRef());
            log.info("Job status: " + jobStatus.getStatus());
            
            if (jobStatus.getStatus() == JobStatusType.ERROR)
            {
                log.info("Error msg: " + jobStatus.getMetaData());
            }
        }
        
        log.info("----------------------------");
        log.info("Done");
    }
    
    private void cancelTest(String eeServiceURI, String serviceURI, String nameSpace, String serviceName, String portName, boolean streaming) throws Exception
    {
        log.info("Starting web-service client");
        log.info("----------------------------");
        
        PredictorServiceClient predictorServiceClient = new PredictorServiceClient(serviceURI, nameSpace, serviceName, portName);
        
        log.info("Getting PredictorServiceDescription");
        PredictorServiceDescription predictorServiceDesc = predictorServiceClient.getPredictorServiceDescription();
        log.info("Service name: " + predictorServiceDesc.getName());
        
        if ((predictorServiceDesc.getEvents() != null) && !predictorServiceDesc.getEvents().isEmpty())
        {
            log.info("Num events: " + predictorServiceDesc.getEvents().size());
            for (Event e : predictorServiceDesc.getEvents()) {
                log.info(" - " + e.getTitle());
            }
        }
        else
        {
            log.error("No events gotten from the PSD");
        }
        
        if ((predictorServiceDesc.getConfigurationParams() != null) && !predictorServiceDesc.getConfigurationParams().isEmpty())
        {
            log.info("Configuration parameters:");
            for (Parameter p : predictorServiceDesc.getConfigurationParams())
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
        PredictorServiceJobConfig jobConfig = getPredictorServiceJobConfig(eeServiceURI, DateUtil.getDateObject("2010-01-01"), predictorServiceDesc.getEvents().iterator().next(), predictorServiceDesc.getConfigurationParams(), streaming);
        
        log.info("Creating a new evaluation job");
        JobDetails jobDetails = predictorServiceClient.createEvaluationJob(jobConfig);
        
        log.info("Job reference: " + jobDetails.getJobRef());
        log.info("Job status: " + jobDetails.getJobStatus().getStatus());

        if ((jobDetails.getJobStatus().getStatus() == JobStatusType.FAILED) ||
            (jobDetails.getJobStatus().getStatus() == JobStatusType.ERROR))
        {
            log.error("ERROR MSG: " + jobDetails.getJobStatus().getMetaData());
            return;
        }
        
        log.info("Starting evaluation job: " + jobDetails.getJobRef());
        JobStatus jobStatus = predictorServiceClient.evaluate(jobDetails.getJobRef());
        log.info("Job status: " + jobStatus.getStatus());
        
        if ((jobStatus.getStatus() == JobStatusType.FAILED) ||
            (jobStatus.getStatus() == JobStatusType.ERROR))
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
            
            if (jobStatus.getStatus() == JobStatusType.FINISHED)
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
                polling = false;
                break;
            }
            else if ((jobStatus.getStatus() == JobStatusType.FINISHED) ||
                     (jobStatus.getStatus() == JobStatusType.ERROR) ||
                     (jobStatus.getStatus() == JobStatusType.CANCELLED))
            {
                log.error("There was an error in the evaluation: " + jobStatus.getMetaData());
                polling = false;
                break;
            }
            
            Thread.sleep(3000);
            
            log.info("Cancelling job: " + jobDetails.getJobRef());
            jobStatus = predictorServiceClient.cancelJob(jobDetails.getJobRef());
            log.info("Job status: " + jobStatus.getStatus());
            if (jobStatus.getStatus() == JobStatusType.ERROR) {
                log.error("ERROR MSG: " + jobStatus.getMetaData());
            }
        }
        
        log.info("----------------------------");
        log.info("Done");
    }
    
    private static PredictorServiceJobConfig getPredictorServiceJobConfig(String eeServiceURI, Date startDate, Event evt, List<Parameter> configParams, boolean streaming) throws Exception
    {
        PredictorServiceJobConfig jobConfig = new PredictorServiceJobConfig();
        jobConfig.setStartDate(startDate);
        jobConfig.setEvaluationEngineServiceURI(new URI(eeServiceURI));
        
        Set<Event> events = new HashSet<Event>();
        events.add(evt);
        jobConfig.setEvents(events);
        
        CommunityDetails cd = new CommunityDetails("SAP Business One Core", "http://forums.sdn.sap.com/uim/forum/264#id");
        jobConfig.setCommunityDetails(cd);
        
        jobConfig.setStreaming(streaming);
        StreamDetails sd = new StreamDetails("SAP Stream", new URI("tcp://localhost:61616"));
        jobConfig.setStreamDetails(sd);
        
        jobConfig.setForecastPeriod(new Parameter(new ParameterValue("7"))); // unit is days
        
        if ((configParams != null) && !configParams.isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: configParams) {
                p.setValue(new ParameterValue(p.getDefaultValue()));
            }
            
            jobConfig.setConfigurationParams(configParams);
        }
        
        return jobConfig;
    }
}
