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

import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.PredictorServiceImpl;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.PredictorServiceImplPortType;

/**
 *
 * @author Vegard Engen
 */
public class PredictorServiceClient
{
    private static final Logger log = LoggerFactory.getLogger(PredictorServiceClient.class);
    private PredictorServiceImpl predictorServiceImpl;
    private PredictorServiceImplPortType predictorService;
    private BindingProvider provider;
    
    private String serviceURI;
    private String nameSpace;
    private String serviceName;
    private String portName;
    
    /**
     * TODO: should be able to get the nameSpace, serviceName and portName from the wsdl!
     * @param serviceURI
     * @param nameSpace
     * @param serviceName
     * @param portName 
     *
    public PredictorServiceClient(String serviceURI) throws Exception
    {
        this.serviceURI = serviceURI;
        this.nameSpace = "http://dummy.predictorservice.robust.itinnovation.soton.ac.uk/";
        this.serviceName = "PredictorServiceImplService";
        this.portName = "PredictorServiceImplPort";
        
        URL wsdlURL = null;
        try {
            wsdlURL = new URL(serviceURI + "?wsdl");
        } catch (MalformedURLException e) {
            log.error("Cannot initialise the wsdl from: " + wsdlURL, e);
        }
        
        log.info("Creating PredictorServiceClient instance");
        log.info("Service URI: " + serviceURI);
        log.info("WSDL location: " + wsdlURL.toString());
        log.info("Namespace: " + nameSpace);
        log.info("Service name: " + serviceName);
        log.info("Port name: " + portName);

        QName service = new QName(nameSpace, serviceName);
        QName port = new QName(nameSpace, portName);
        
        log.debug("Instantiating PredictorServiceImpl instance");
        predictorServiceImpl = new PredictorServiceImpl(wsdlURL, service, port);
        
        log.debug("Getting PredictorServiceImplPort instance");
        predictorService = predictorServiceImpl.getPredictorServiceImplPort();
        
        setUpBindingProvider(serviceURI);
    }*/

    /**
     * TODO: should be able to get the nameSpace, serviceName and portName from the wsdl!
     * @param serviceURI
     * @param nameSpace
     * @param serviceName
     * @param portName 
     */
    public PredictorServiceClient(String serviceURI, String nameSpace, String serviceName, String portName) throws Exception
    {
        this.serviceURI = serviceURI;
        this.nameSpace = nameSpace;
        this.serviceName = serviceName;
        this.portName = portName;
        
        URL wsdlURL = null;
        try {
            wsdlURL = new URL(serviceURI + "?wsdl");
        } catch (MalformedURLException e) {
            log.error("Cannot initialise the wsdl from: " + wsdlURL, e);
        }
        
        log.debug("Creating PredictorServiceClient instance");
        log.debug("  Service URI: " + serviceURI);
        log.debug("  WSDL location: " + wsdlURL.toString());
        log.debug("  Namespace: " + nameSpace);
        log.debug("  Service name: " + serviceName);
        log.debug("  Port name: " + portName);

        QName service = new QName(nameSpace, serviceName);
        QName port = new QName(nameSpace, portName);
        
        log.debug("Instantiating PredictorServiceImpl instance");
        predictorServiceImpl = new PredictorServiceImpl(wsdlURL, service, port);
        
        log.debug("Getting PredictorServiceImplPort instance");
        predictorService = predictorServiceImpl.getPredictorServiceImplPort();
        
        setUpBindingProvider(serviceURI);
    }
    
    /**
     * Sets up a Binding Provider to resolve issues with localhost references in service wsdls.
     * @param serviceURI 
     */
    private void setUpBindingProvider(String serviceURI)
    {
        log.debug("Creating Binding Provider for service at URI: " + serviceURI);
        provider = (BindingProvider) predictorService;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURI);
    }

 /*   
    public PredictorServiceClient(String serviceURI)
    {
        log.info("Creating PredictorServiceClient instance for service at URI: " + serviceURI);
        File f = new File("src/main/resources/PredictorService.wsdl");
        URL url = null;
        try
        {
            url = new URL("file:/" + f.getAbsolutePath());
        } catch (MalformedURLException e)
        {
            log.error("Cannot initialise the default wsdl from: " + url);
        }

        QName service = new QName(serviceURI, "PredictorServiceImpl");
        QName port = new QName(serviceURI, "PredictorServiceImplPort");
        
        log.info("Instantiating PredictorServiceImpl instance");
        //predictorServiceImpl = new PredictorServiceImpl(url, service);
        predictorServiceImpl = new PredictorServiceImpl(url, service, port);
        
        log.info("Getting PredictorServiceImplPortType instance");
        predictorService = predictorServiceImpl.getPredictorServiceImplPort();
    }
/**/
    //@Override
    public PredictorServiceDescription getPredictorServiceDescription() throws Exception
    {
        log.info("Getting predictor service description from: " + serviceURI);
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.PredictorServiceDescription desc_ws = predictorService.getPredictorServiceDescription();
        
        log.debug("Converting WS object into local instance");
        PredictorServiceDescription desc = WS2LocalClassConverter.getPredictorServiceDescription(desc_ws);
        
        return desc;
    }

    //@Override
    public List<JobDetails> getJobs() throws Exception
    {
        log.info("Calling on the service (" + serviceURI + ") to get the details of any jobs");
        List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobDetails> jobs_ws = predictorService.getJobs();
        
        if ((jobs_ws == null) || jobs_ws.isEmpty())
        {
            log.debug("No jobs");
            return new ArrayList<JobDetails>();
        }
        
        log.debug("Got " + jobs_ws.size() + " jobs, which will now be converted into local JobDetail objects");
        
        List<JobDetails> jobs = new ArrayList<JobDetails>();
        
        for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobDetails job_ws : jobs_ws)
            jobs.add(WS2LocalClassConverter.getJobDetails(job_ws));
        
        return jobs;
    }

    //@Override
    public JobDetails getJobDetails(String jobRef) throws Exception
    {
        log.info("Calling on the service (" + serviceURI + ") to get the status of the job: " + jobRef);
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobDetails job_ws = predictorService.getJobDetails(jobRef);
        
        log.debug("Converting WS JobStatus object to local instance");
        JobDetails jobDetails = WS2LocalClassConverter.getJobDetails(job_ws);
        
        return jobDetails;
    }
    
    //@Override
    public JobDetails createEvaluationJob(PredictorServiceJobConfig config) throws Exception
    {
        log.info("Creating evaluation job at service: " + serviceURI);
        
        log.debug("Converting configuration object to WS instance");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.PredictorServiceJobConfig config_ws = Local2WSClassConverter.getPredictorServiceJobConfig(config);
        
        log.debug("Calling on WS to create the evaulation job");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobDetails job_ws = predictorService.createEvaluationJob(config_ws);
        
        if (job_ws == null)
        {
            log.error("");
            throw new RuntimeException("Failed to create evaluation job because of an error with the service - perhaps it is offline!");
        }
        
        log.debug("Converting WS Job Details object to local instance");
        JobDetails jobDetails = WS2LocalClassConverter.getJobDetails(job_ws);
        
        return jobDetails;
    }

    //@Override
    public JobStatus evaluate(String jobRef) throws Exception
    {
        log.info("Calling on the service (" + serviceURI + ") to start evaluating the job: " + jobRef);
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobStatus job_ws = predictorService.evaluate(jobRef);
        
        log.debug("Converting WS JobStatus object to local instance");
        JobStatus jobStatus = WS2LocalClassConverter.getJobStatus(job_ws);
        
        return jobStatus;
    }

    //@Override
    public JobStatus getJobStatus(String jobRef) throws Exception
    {
        log.info("Calling on the service (" + serviceURI + ") to get the status of the job: " + jobRef);
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobStatus job_ws = predictorService.getJobStatus(jobRef);
        
        log.debug("Converting WS JobStatus object to local instance");
        JobStatus jobStatus = WS2LocalClassConverter.getJobStatus(job_ws);
        
        return jobStatus;
    }

    //@Override
    public EvaluationResult getEvaluationResult(String jobRef) throws Exception
    {
        log.info("Getting evaluation results from the service (" + serviceURI + ") for job: " + jobRef);
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationResult res_ws = predictorService.getEvaluationResult(jobRef);
        
        log.debug("Converting WS EvaluationResult object to local instance");
        EvaluationResult res = WS2LocalClassConverter.getEvaluationResult(res_ws);
        
        return res;
    }

    //@Override
    public JobStatus cancelJob(String jobRef) throws Exception
    {
        log.info("Calling on service (" + serviceURI + ") to cancel job: " + jobRef);
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobStatus job_ws = predictorService.cancelJob(jobRef);
        
        log.debug("Converting WS JobStatus object to local instance");
        JobStatus jobStatus = WS2LocalClassConverter.getJobStatus(job_ws);
        
        return jobStatus;
    }
}
