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
package uk.ac.soton.itinnovation.robust.cat.sim.client.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.ac.soton.itinnovation.robust.cat.sim.client.gen package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetJobDetailsResponse_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "getJobDetailsResponse");
    private final static QName _CreateSimulationJobResponse_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "createSimulationJobResponse");
    private final static QName _GetSimulationServiceDescriptionResponse_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "getSimulationServiceDescriptionResponse");
    private final static QName _GetJobStatus_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "getJobStatus");
    private final static QName _StartSimulation_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "startSimulation");
    private final static QName _CancelJobResponse_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "cancelJobResponse");
    private final static QName _GetJobDetails_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "getJobDetails");
    private final static QName _CancelJob_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "cancelJob");
    private final static QName _CreateSimulationJob_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "createSimulationJob");
    private final static QName _GetResult_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "getResult");
    private final static QName _GetJobStatusResponse_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "getJobStatusResponse");
    private final static QName _GetSimulationServiceDescription_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "getSimulationServiceDescription");
    private final static QName _StartSimulationResponse_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "startSimulationResponse");
    private final static QName _GetResultResponse_QNAME = new QName("http://sim.common.cat.robust.itinnovation.soton.ac.uk/", "getResultResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.ac.soton.itinnovation.robust.cat.sim.client.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JobStatus }
     * 
     */
    public JobStatus createJobStatus() {
        return new JobStatus();
    }

    /**
     * Create an instance of {@link JobDetails }
     * 
     */
    public JobDetails createJobDetails() {
        return new JobDetails();
    }

    /**
     * Create an instance of {@link CreateSimulationJobResponse }
     * 
     */
    public CreateSimulationJobResponse createCreateSimulationJobResponse() {
        return new CreateSimulationJobResponse();
    }

    /**
     * Create an instance of {@link GetResult }
     * 
     */
    public GetResult createGetResult() {
        return new GetResult();
    }

    /**
     * Create an instance of {@link CancelJobResponse }
     * 
     */
    public CancelJobResponse createCancelJobResponse() {
        return new CancelJobResponse();
    }

    /**
     * Create an instance of {@link CreateSimulationJob }
     * 
     */
    public CreateSimulationJob createCreateSimulationJob() {
        return new CreateSimulationJob();
    }

    /**
     * Create an instance of {@link GetSimulationServiceDescription }
     * 
     */
    public GetSimulationServiceDescription createGetSimulationServiceDescription() {
        return new GetSimulationServiceDescription();
    }

    /**
     * Create an instance of {@link GetResultResponse }
     * 
     */
    public GetResultResponse createGetResultResponse() {
        return new GetResultResponse();
    }

    /**
     * Create an instance of {@link GetSimulationServiceDescriptionResponse }
     * 
     */
    public GetSimulationServiceDescriptionResponse createGetSimulationServiceDescriptionResponse() {
        return new GetSimulationServiceDescriptionResponse();
    }

    /**
     * Create an instance of {@link SimulationResult }
     * 
     */
    public SimulationResult createSimulationResult() {
        return new SimulationResult();
    }

    /**
     * Create an instance of {@link CommunityDetails }
     * 
     */
    public CommunityDetails createCommunityDetails() {
        return new CommunityDetails();
    }

    /**
     * Create an instance of {@link SimulationServiceJobConfig }
     * 
     */
    public SimulationServiceJobConfig createSimulationServiceJobConfig() {
        return new SimulationServiceJobConfig();
    }

    /**
     * Create an instance of {@link GetJobStatus }
     * 
     */
    public GetJobStatus createGetJobStatus() {
        return new GetJobStatus();
    }

    /**
     * Create an instance of {@link StartSimulation }
     * 
     */
    public StartSimulation createStartSimulation() {
        return new StartSimulation();
    }

    /**
     * Create an instance of {@link CancelJob }
     * 
     */
    public CancelJob createCancelJob() {
        return new CancelJob();
    }

    /**
     * Create an instance of {@link KeyValuePair }
     * 
     */
    public KeyValuePair createKeyValuePair() {
        return new KeyValuePair();
    }

    /**
     * Create an instance of {@link SimulationServiceDescription }
     * 
     */
    public SimulationServiceDescription createSimulationServiceDescription() {
        return new SimulationServiceDescription();
    }

    /**
     * Create an instance of {@link ParameterValue }
     * 
     */
    public ParameterValue createParameterValue() {
        return new ParameterValue();
    }

    /**
     * Create an instance of {@link GetJobDetails }
     * 
     */
    public GetJobDetails createGetJobDetails() {
        return new GetJobDetails();
    }

    /**
     * Create an instance of {@link GetJobStatusResponse }
     * 
     */
    public GetJobStatusResponse createGetJobStatusResponse() {
        return new GetJobStatusResponse();
    }

    /**
     * Create an instance of {@link ValueConstraint }
     * 
     */
    public ValueConstraint createValueConstraint() {
        return new ValueConstraint();
    }

    /**
     * Create an instance of {@link ResultGroup }
     * 
     */
    public ResultGroup createResultGroup() {
        return new ResultGroup();
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

    /**
     * Create an instance of {@link GetJobDetailsResponse }
     * 
     */
    public GetJobDetailsResponse createGetJobDetailsResponse() {
        return new GetJobDetailsResponse();
    }

    /**
     * Create an instance of {@link StartSimulationResponse }
     * 
     */
    public StartSimulationResponse createStartSimulationResponse() {
        return new StartSimulationResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobDetailsResponse")
    public JAXBElement<GetJobDetailsResponse> createGetJobDetailsResponse(GetJobDetailsResponse value) {
        return new JAXBElement<GetJobDetailsResponse>(_GetJobDetailsResponse_QNAME, GetJobDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateSimulationJobResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "createSimulationJobResponse")
    public JAXBElement<CreateSimulationJobResponse> createCreateSimulationJobResponse(CreateSimulationJobResponse value) {
        return new JAXBElement<CreateSimulationJobResponse>(_CreateSimulationJobResponse_QNAME, CreateSimulationJobResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSimulationServiceDescriptionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "getSimulationServiceDescriptionResponse")
    public JAXBElement<GetSimulationServiceDescriptionResponse> createGetSimulationServiceDescriptionResponse(GetSimulationServiceDescriptionResponse value) {
        return new JAXBElement<GetSimulationServiceDescriptionResponse>(_GetSimulationServiceDescriptionResponse_QNAME, GetSimulationServiceDescriptionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobStatus")
    public JAXBElement<GetJobStatus> createGetJobStatus(GetJobStatus value) {
        return new JAXBElement<GetJobStatus>(_GetJobStatus_QNAME, GetJobStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartSimulation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "startSimulation")
    public JAXBElement<StartSimulation> createStartSimulation(StartSimulation value) {
        return new JAXBElement<StartSimulation>(_StartSimulation_QNAME, StartSimulation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelJobResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "cancelJobResponse")
    public JAXBElement<CancelJobResponse> createCancelJobResponse(CancelJobResponse value) {
        return new JAXBElement<CancelJobResponse>(_CancelJobResponse_QNAME, CancelJobResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobDetails")
    public JAXBElement<GetJobDetails> createGetJobDetails(GetJobDetails value) {
        return new JAXBElement<GetJobDetails>(_GetJobDetails_QNAME, GetJobDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelJob }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "cancelJob")
    public JAXBElement<CancelJob> createCancelJob(CancelJob value) {
        return new JAXBElement<CancelJob>(_CancelJob_QNAME, CancelJob.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateSimulationJob }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "createSimulationJob")
    public JAXBElement<CreateSimulationJob> createCreateSimulationJob(CreateSimulationJob value) {
        return new JAXBElement<CreateSimulationJob>(_CreateSimulationJob_QNAME, CreateSimulationJob.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "getResult")
    public JAXBElement<GetResult> createGetResult(GetResult value) {
        return new JAXBElement<GetResult>(_GetResult_QNAME, GetResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobStatusResponse")
    public JAXBElement<GetJobStatusResponse> createGetJobStatusResponse(GetJobStatusResponse value) {
        return new JAXBElement<GetJobStatusResponse>(_GetJobStatusResponse_QNAME, GetJobStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSimulationServiceDescription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "getSimulationServiceDescription")
    public JAXBElement<GetSimulationServiceDescription> createGetSimulationServiceDescription(GetSimulationServiceDescription value) {
        return new JAXBElement<GetSimulationServiceDescription>(_GetSimulationServiceDescription_QNAME, GetSimulationServiceDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartSimulationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "startSimulationResponse")
    public JAXBElement<StartSimulationResponse> createStartSimulationResponse(StartSimulationResponse value) {
        return new JAXBElement<StartSimulationResponse>(_StartSimulationResponse_QNAME, StartSimulationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResultResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sim.common.cat.robust.itinnovation.soton.ac.uk/", name = "getResultResponse")
    public JAXBElement<GetResultResponse> createGetResultResponse(GetResultResponse value) {
        return new JAXBElement<GetResultResponse>(_GetResultResponse_QNAME, GetResultResponse.class, null, value);
    }

}
