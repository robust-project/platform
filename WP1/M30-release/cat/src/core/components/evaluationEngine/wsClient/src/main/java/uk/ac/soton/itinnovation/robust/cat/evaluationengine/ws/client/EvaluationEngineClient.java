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
//      Created Date :          2012-08-07
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.EvaluationEngineService;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.IEvaluationEngineServicePort;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.Status;

/**
 *
 * @author Vegard Engen
 */
public class EvaluationEngineClient
{
    private static final Logger log = LoggerFactory.getLogger(EvaluationEngineClient.class);
    private EvaluationEngineService evaluationEngineService;
    private IEvaluationEngineServicePort evaluationEngineServicePort;
    
    private String defaultServiceURI = "http://robust.it-innovation.soton.ac.uk/evaluationEngineService-1.2/service";
    private String defaultNameSpace = "http://impl.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/";
    private String defaultServiceName = "EvaluationEngineServiceService";
    private String defaultPortName = "EvaluationEngineServicePort";
    
    private String serviceURI;
    private String nameSpace;
    private String serviceName;
    private String portName;

    /**
     * Will connect to the Evaluation Engine service at the given URI, assuming
     * default name space, service name and port name.
     * @param serviceURI The URI of the service (not including the ?wsdl bit!
     */
    public EvaluationEngineClient(String serviceURI) throws Exception
    {
        // TODO: should be able to get the nameSpace, serviceName and portName from the wsdl!
        connect(serviceURI, defaultNameSpace, defaultServiceName, defaultPortName);
    }
    
    /**
     * Will connect to the Evaluation Engine service with the given parameter values.
     * @param serviceURI The URI of the service (not including the ?wsdl bit!
     * @param nameSpace The namespace of the service.
     * @param serviceName The name of the service.
     * @param portName The port name of the service.
     */
    public EvaluationEngineClient(String serviceURI, String nameSpace, String serviceName, String portName) throws Exception
    {
        this.serviceURI = serviceURI;
        this.nameSpace = nameSpace;
        this.serviceName = serviceName;
        this.portName = portName;
        
        // TODO: should be able to get the nameSpace, serviceName and portName from the wsdl!
        connect(serviceURI, nameSpace, serviceName, portName);
    }
    
    /**
     * Will connect to the service.
     * @param The URI of the service (not including the ?wsdl bit!
     * @param nameSpace The namespace of the service.
     * @param serviceName The name of the service.
     * @param portName The port name of the service.
     * @throws Exception 
     */
    private void connect(String serviceURI, String nameSpace, String serviceName, String portName) throws Exception
    {
        URL wsdlURL = null;
        try {
            wsdlURL = new URL(serviceURI + "?wsdl");
        } catch (MalformedURLException e) {
            log.error("Cannot initialise the wsdl from: " + wsdlURL, e);
        }
        
        log.debug("Creating EvaluationEngineClient instance");
        log.debug("  Service URI: " + serviceURI);
        log.debug("  WSDL location: " + wsdlURL.toString());
        log.debug("  Namespace: " + nameSpace);
        log.debug("  Service name: " + serviceName);
        log.debug("  Port name: " + portName);
        
        QName service = new QName(nameSpace, serviceName);
        QName port = new QName(nameSpace, portName);
        
        log.debug("Instantiating EvaluationEngineService instance");
        evaluationEngineService = new EvaluationEngineService(wsdlURL, service, port);
        
        log.debug("Getting EvaluationEngineServicePort instance");
        evaluationEngineServicePort = evaluationEngineService.getEvaluationEngineServicePort();
        
        /* Set NEW Endpoint Location */
        BindingProvider bp = (BindingProvider)evaluationEngineServicePort;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURI);
    }
    
    public Status startEngine() throws Exception
    {
        log.debug("Invoking startEngine...");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.Status statusWS = evaluationEngineServicePort.startEngine();
        
        log.debug("Converting WS Status object to local instance");
        Status status = WS2LocalClassConverter.getStatus(statusWS);
        
        return status;
    }
    
    public Status startEngineForTimePeriod(Date fromDate, Date toDate) throws Exception
    {
        log.debug("Invoking startEngineForTimePeriod...");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.Status statusWS = evaluationEngineServicePort.startEngineForTimePeriod(DateUtil.getXMLGregorianCalendar(fromDate), DateUtil.getXMLGregorianCalendar(toDate));
        
        log.debug("Converting WS Status object to local instance");
        Status status = WS2LocalClassConverter.getStatus(statusWS);
        
        return status;
    }
    
    public Status stopEngine() throws Exception
    {
        log.debug("Invoking stopEngine...");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.Status statusWS = evaluationEngineServicePort.stopEngine();
        
        log.debug("Converting WS Status object to local instance");
        Status status = WS2LocalClassConverter.getStatus(statusWS);
        
        return status;
    }
    
    public boolean isEngineRunning() throws Exception
    {
        log.debug("Invoking isEngineRunning...");
        return evaluationEngineServicePort.isEngineRunning();
    }
    
    public StreamDetails getGeneralStreamDetails() throws Exception
    {
        log.debug("Invoking getGeneralStreamDetails...");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StreamDetails streamDetailsWS = evaluationEngineServicePort.getGeneralStreamDetails();
        
        log.debug("Converting WS StreamDetails object to local instance");
        StreamDetails streamDetails = WS2LocalClassConverter.getStreamDetails(streamDetailsWS);
        
        return streamDetails;
    }
    
    public StreamDetails getCommunityResultsStreamDetails(UUID uuid) throws Exception
    {
        log.debug("Invoking getCommunityResultsStreamDetails...");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StreamDetails streamDetailsWS = evaluationEngineServicePort.getCommunityResultsStreamDetails(uuid.toString());
        
        log.debug("Converting WS StreamDetails object to local instance");
        StreamDetails streamDetails = WS2LocalClassConverter.getStreamDetails(streamDetailsWS);
        
        return streamDetails;
    }
    
    public void newEvaluationResult(String jobRef, EvaluationResult evalRes) throws Exception
    {
        log.debug("Invoking newEvaluationResult...");
        if (jobRef == null)
        {
            log.error("The job reference for the evaluation result was NULL");
            return;
        }
        
        if (evalRes == null)
        {
            log.error("The evaluation result object was NULL");
            return;
        }
        
        log.debug("Converting local EvaluationResult object to WS instance");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.EvaluationResult evalResWS = Local2WSClassConverter.getEvaluationResult(evalRes);
        
        if (evalResWS == null)
        {
            log.error("The converted (to WS) EvaluationResult object is NULL, so won't be sending this to the Evaluation Engine Service!");
            return;
        }
        
        log.debug("Sending WS instance of EvaluationResult off to the service");
        evaluationEngineServicePort.newEvaluationResult(jobRef, evalResWS);
    }
    
    /**
     * Provide a status update on a particular evaluation job, such as a job
     * failed after it started.
     * @param jobRef The job reference.
     * @param jobStatus The job status.
     */
    public void updateEvaluationJobStatus(String jobRef, JobStatus jobStatus)
    {
        log.debug("Invoking newEvaluationResult...");
        if (jobRef == null)
        {
            log.error("The job reference for the evaluation result was NULL");
            return;
        }
        
        if (jobStatus == null)
        {
            log.error("The job status object was NULL");
            return;
        }
        
        log.debug("Converting local JobStatus object to WS instance");
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobStatus jobStatusWS = Local2WSClassConverter.getJobStatus(jobStatus);
        
        if (jobStatusWS == null)
        {
            log.error("The converted (to WS) JobStatus object is NULL, so won't be sending this to the Evaluation Engine Service!");
            return;
        }
        
        log.debug("Sending WS instance of JobStatus off to the service");
        evaluationEngineServicePort.updateEvaluationJobStatus(jobRef, jobStatusWS);
    }
}
