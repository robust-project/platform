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
//      Created Date :          2013-10-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.sim.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ISimulationService;
import uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationServiceService;

/**
 *
 * @author Vegard Engen
 */
public class SimulationServiceClient
{
    private SimulationServiceService simService;
    private ISimulationService simServicePort;
    
    private String defaultServiceWSDLURI = "http://robust.it-innovation.soton.ac.uk/catSimService-1.0/service";
    private String defaultNameSpace = "http://impl.ws.sim.robust.itinnovation.soton.ac.uk/";
    private String defaultServiceName = "SimulationServiceService";
    private String defaultPortName = "SimulationServicePort";
    
    private String serviceWSDLURI;
    private String nameSpace;
    private String serviceName;
    private String portName;
	
    private static final Logger logger = Logger.getLogger(SimulationServiceClient.class);

    /**
     * Will connect to the Simulation service at the given URI, assuming
     * default name space, service name and port name.
     * @param serviceWSDLURI The URI of the service, including the ?wsdl bit!
     */
    public SimulationServiceClient(String serviceWSDLURI) throws Exception
    {
        connect(serviceWSDLURI, defaultNameSpace, defaultServiceName, defaultPortName);
    }
    
    /**
     * Will connect to the Simulation service with the given parameter values.
     * @param serviceWSDLURI The URI of the service, including the ?wsdl bit!
     * @param nameSpace The namespace of the service.
     * @param serviceName The name of the service.
     * @param portName The port name of the service.
     */
    public SimulationServiceClient(String serviceWSDLURI, String nameSpace, String serviceName, String portName) throws Exception
    {
        this.serviceWSDLURI = serviceWSDLURI;
        this.nameSpace = nameSpace;
        this.serviceName = serviceName;
        this.portName = portName;
        
        connect(serviceWSDLURI, nameSpace, serviceName, portName);
    }
    
    /**
     * Will connect to the service.
     * @param serviceWSDLURI The URI of the service, including the ?wsdl bit!
     * @param nameSpace The namespace of the service.
     * @param serviceName The name of the service.
     * @param portName The port name of the service.
     * @throws Exception 
     */
    private void connect(String serviceWSDLURI, String nameSpace, String serviceName, String portName) throws Exception
    {
        URL wsdlURL = null;
        try {
            wsdlURL = new URL(serviceWSDLURI);
        } catch (MalformedURLException e) {
            logger.error("Cannot initialise the wsdl from: " + wsdlURL, e);
        }
        
        logger.debug("Creating Simulation Client instance");
        logger.debug("  WSDL location: " + wsdlURL);
        logger.debug("  Namespace: " + nameSpace);
        logger.debug("  Service name: " + serviceName);
        logger.debug("  Port name: " + portName);
        
        QName service = new QName(nameSpace, serviceName);
        QName port = new QName(nameSpace, portName);
        
        logger.debug("Instantiating SimulationService instance");
        simService = new SimulationServiceService(wsdlURL, service, port);
        
        logger.debug("Getting SimulationService Port instance");
        simServicePort = simService.getSimulationServicePort();
        
        /* Set NEW Endpoint Location */
        BindingProvider bp = (BindingProvider)simServicePort;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceWSDLURI);
    }
    
    public SimulationServiceDescription getSimulationServiceDescription() throws Exception
    {
		logger.debug("Calling service to get SimulationServiceDescription");
		uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationServiceDescription desc_ws = simServicePort.getSimulationServiceDescription();
        
		logger.debug("Converting WS SimulationServiceDescription object");
		SimulationServiceDescription desc = WS2LocalClassConverter.getSimulationServiceDescription(desc_ws);
		
		return desc;
    }

    public JobDetails getJobDetails(String jobRef) throws Exception
    {
		logger.debug("Calling service to get job details");
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobDetails jd_ws = simServicePort.getJobDetails(jobRef);
		
		logger.debug("Converting WS JobDetails object");
		JobDetails jobDetails = WS2LocalClassConverter.getJobDetails(jd_ws);
		
		return jobDetails;
    }

    public JobDetails createSimulationJob(SimulationServiceJobConfig config) throws Exception
    {
		logger.debug("Calling service to create simulation job");
		logger.debug("Converting LOCAL SimulationServiceJobConfig object");
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationServiceJobConfig config_ws = Local2WSClassConverter.getSimulationSServiceJobConfig(config);
		
		logger.debug("Sending WS SimulationServiceJobConfig object to the service");
		uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobDetails jd_ws = simServicePort.createSimulationJob(config_ws);
		
		logger.debug("Converting WS JobDetails object");
		JobDetails jobDetails = WS2LocalClassConverter.getJobDetails(jd_ws);
		
		return jobDetails;
    }

    public JobStatus startSimulation(String jobRef) throws Exception
    {
		logger.debug("Calling service to start a simulation job");
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatus status_ws = simServicePort.startSimulation(jobRef);
		
		logger.debug("Converting WS JobStatus object");
		JobStatus status = WS2LocalClassConverter.getJobStatus(status_ws);
		
		return status;
    }

    public JobStatus getJobStatus(String jobRef) throws Exception
    {
		logger.debug("Calling service to get job status");
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatus status_ws = simServicePort.getJobStatus(jobRef);
		
		logger.debug("Converting WS JobStatus object");
		JobStatus status = WS2LocalClassConverter.getJobStatus(status_ws);
		
		return status;
    }

    public SimulationResult getResult(String jobRef) throws Exception
    {
		logger.debug("Calling service to get result");
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationResult res_ws = simServicePort.getResult(jobRef);
		
		logger.debug("Converting WS SimulationResult object");
		SimulationResult res = WS2LocalClassConverter.getSimulationResult(res_ws);
		
		return res;
    }

    public JobStatus cancelJob(String jobRef) throws Exception
    {
		logger.debug("Calling service to cancel a job");
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatus status_ws = simServicePort.cancelJob(jobRef);
		
		logger.debug("Converting WS JobStatus object");
		JobStatus status = WS2LocalClassConverter.getJobStatus(status_ws);
		
		return status;
    }
}
