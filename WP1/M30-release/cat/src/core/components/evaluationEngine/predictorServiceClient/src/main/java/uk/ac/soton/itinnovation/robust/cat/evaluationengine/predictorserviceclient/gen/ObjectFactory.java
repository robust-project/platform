/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
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
//      Created Date :          2013-04-29
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen package. 
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

    private final static QName _GetPredictorServiceDescriptionResponse_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getPredictorServiceDescriptionResponse");
    private final static QName _CreateEvaluationJobResponse_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "createEvaluationJobResponse");
    private final static QName _Evaluate_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "evaluate");
    private final static QName _EvaluateResponse_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "evaluateResponse");
    private final static QName _GetJobStatusResponse_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getJobStatusResponse");
    private final static QName _GetJobDetails_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getJobDetails");
    private final static QName _CancelJobResponse_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "cancelJobResponse");
    private final static QName _GetJobsResponse_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getJobsResponse");
    private final static QName _CancelJob_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "cancelJob");
    private final static QName _GetJobs_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getJobs");
    private final static QName _GetPredictorServiceDescription_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getPredictorServiceDescription");
    private final static QName _GetJobDetailsResponse_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getJobDetailsResponse");
    private final static QName _GetEvaluationResult_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getEvaluationResult");
    private final static QName _GetEvaluationResultResponse_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getEvaluationResultResponse");
    private final static QName _CreateEvaluationJob_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "createEvaluationJob");
    private final static QName _GetJobStatus_QNAME = new QName("http://ps.common.cat.robust.itinnovation.soton.ac.uk/", "getJobStatus");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EventCondition }
     * 
     */
    public EventCondition createEventCondition() {
        return new EventCondition();
    }

    /**
     * Create an instance of {@link GetJobsResponse }
     * 
     */
    public GetJobsResponse createGetJobsResponse() {
        return new GetJobsResponse();
    }

    /**
     * Create an instance of {@link GetJobStatusResponse }
     * 
     */
    public GetJobStatusResponse createGetJobStatusResponse() {
        return new GetJobStatusResponse();
    }

    /**
     * Create an instance of {@link Evaluate }
     * 
     */
    public Evaluate createEvaluate() {
        return new Evaluate();
    }

    /**
     * Create an instance of {@link Event }
     * 
     */
    public Event createEvent() {
        return new Event();
    }

    /**
     * Create an instance of {@link StreamDetails }
     * 
     */
    public StreamDetails createStreamDetails() {
        return new StreamDetails();
    }

    /**
     * Create an instance of {@link GetJobDetails }
     * 
     */
    public GetJobDetails createGetJobDetails() {
        return new GetJobDetails();
    }

    /**
     * Create an instance of {@link PredictorServiceJobConfig }
     * 
     */
    public PredictorServiceJobConfig createPredictorServiceJobConfig() {
        return new PredictorServiceJobConfig();
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

    /**
     * Create an instance of {@link EvaluationResult }
     * 
     */
    public EvaluationResult createEvaluationResult() {
        return new EvaluationResult();
    }

    /**
     * Create an instance of {@link CreateEvaluationJobResponse }
     * 
     */
    public CreateEvaluationJobResponse createCreateEvaluationJobResponse() {
        return new CreateEvaluationJobResponse();
    }

    /**
     * Create an instance of {@link KeyValuePair }
     * 
     */
    public KeyValuePair createKeyValuePair() {
        return new KeyValuePair();
    }

    /**
     * Create an instance of {@link ResultItem }
     * 
     */
    public ResultItem createResultItem() {
        return new ResultItem();
    }

    /**
     * Create an instance of {@link ValueConstraint }
     * 
     */
    public ValueConstraint createValueConstraint() {
        return new ValueConstraint();
    }

    /**
     * Create an instance of {@link JobStatus }
     * 
     */
    public JobStatus createJobStatus() {
        return new JobStatus();
    }

    /**
     * Create an instance of {@link CommunityDetails }
     * 
     */
    public CommunityDetails createCommunityDetails() {
        return new CommunityDetails();
    }

    /**
     * Create an instance of {@link EvaluateResponse }
     * 
     */
    public EvaluateResponse createEvaluateResponse() {
        return new EvaluateResponse();
    }

    /**
     * Create an instance of {@link GetPredictorServiceDescriptionResponse }
     * 
     */
    public GetPredictorServiceDescriptionResponse createGetPredictorServiceDescriptionResponse() {
        return new GetPredictorServiceDescriptionResponse();
    }

    /**
     * Create an instance of {@link GetJobDetailsResponse }
     * 
     */
    public GetJobDetailsResponse createGetJobDetailsResponse() {
        return new GetJobDetailsResponse();
    }

    /**
     * Create an instance of {@link CancelJobResponse }
     * 
     */
    public CancelJobResponse createCancelJobResponse() {
        return new CancelJobResponse();
    }

    /**
     * Create an instance of {@link CreateEvaluationJob }
     * 
     */
    public CreateEvaluationJob createCreateEvaluationJob() {
        return new CreateEvaluationJob();
    }

    /**
     * Create an instance of {@link GetEvaluationResult }
     * 
     */
    public GetEvaluationResult createGetEvaluationResult() {
        return new GetEvaluationResult();
    }

    /**
     * Create an instance of {@link ParameterValue }
     * 
     */
    public ParameterValue createParameterValue() {
        return new ParameterValue();
    }

    /**
     * Create an instance of {@link JobDetails }
     * 
     */
    public JobDetails createJobDetails() {
        return new JobDetails();
    }

    /**
     * Create an instance of {@link GetEvaluationResultResponse }
     * 
     */
    public GetEvaluationResultResponse createGetEvaluationResultResponse() {
        return new GetEvaluationResultResponse();
    }

    /**
     * Create an instance of {@link GetJobStatus }
     * 
     */
    public GetJobStatus createGetJobStatus() {
        return new GetJobStatus();
    }

    /**
     * Create an instance of {@link GetPredictorServiceDescription }
     * 
     */
    public GetPredictorServiceDescription createGetPredictorServiceDescription() {
        return new GetPredictorServiceDescription();
    }

    /**
     * Create an instance of {@link CancelJob }
     * 
     */
    public CancelJob createCancelJob() {
        return new CancelJob();
    }

    /**
     * Create an instance of {@link GetJobs }
     * 
     */
    public GetJobs createGetJobs() {
        return new GetJobs();
    }

    /**
     * Create an instance of {@link PredictorServiceDescription }
     * 
     */
    public PredictorServiceDescription createPredictorServiceDescription() {
        return new PredictorServiceDescription();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPredictorServiceDescriptionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getPredictorServiceDescriptionResponse")
    public JAXBElement<GetPredictorServiceDescriptionResponse> createGetPredictorServiceDescriptionResponse(GetPredictorServiceDescriptionResponse value) {
        return new JAXBElement<GetPredictorServiceDescriptionResponse>(_GetPredictorServiceDescriptionResponse_QNAME, GetPredictorServiceDescriptionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateEvaluationJobResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "createEvaluationJobResponse")
    public JAXBElement<CreateEvaluationJobResponse> createCreateEvaluationJobResponse(CreateEvaluationJobResponse value) {
        return new JAXBElement<CreateEvaluationJobResponse>(_CreateEvaluationJobResponse_QNAME, CreateEvaluationJobResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Evaluate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "evaluate")
    public JAXBElement<Evaluate> createEvaluate(Evaluate value) {
        return new JAXBElement<Evaluate>(_Evaluate_QNAME, Evaluate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "evaluateResponse")
    public JAXBElement<EvaluateResponse> createEvaluateResponse(EvaluateResponse value) {
        return new JAXBElement<EvaluateResponse>(_EvaluateResponse_QNAME, EvaluateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobStatusResponse")
    public JAXBElement<GetJobStatusResponse> createGetJobStatusResponse(GetJobStatusResponse value) {
        return new JAXBElement<GetJobStatusResponse>(_GetJobStatusResponse_QNAME, GetJobStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobDetails")
    public JAXBElement<GetJobDetails> createGetJobDetails(GetJobDetails value) {
        return new JAXBElement<GetJobDetails>(_GetJobDetails_QNAME, GetJobDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelJobResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "cancelJobResponse")
    public JAXBElement<CancelJobResponse> createCancelJobResponse(CancelJobResponse value) {
        return new JAXBElement<CancelJobResponse>(_CancelJobResponse_QNAME, CancelJobResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobsResponse")
    public JAXBElement<GetJobsResponse> createGetJobsResponse(GetJobsResponse value) {
        return new JAXBElement<GetJobsResponse>(_GetJobsResponse_QNAME, GetJobsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelJob }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "cancelJob")
    public JAXBElement<CancelJob> createCancelJob(CancelJob value) {
        return new JAXBElement<CancelJob>(_CancelJob_QNAME, CancelJob.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobs")
    public JAXBElement<GetJobs> createGetJobs(GetJobs value) {
        return new JAXBElement<GetJobs>(_GetJobs_QNAME, GetJobs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPredictorServiceDescription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getPredictorServiceDescription")
    public JAXBElement<GetPredictorServiceDescription> createGetPredictorServiceDescription(GetPredictorServiceDescription value) {
        return new JAXBElement<GetPredictorServiceDescription>(_GetPredictorServiceDescription_QNAME, GetPredictorServiceDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobDetailsResponse")
    public JAXBElement<GetJobDetailsResponse> createGetJobDetailsResponse(GetJobDetailsResponse value) {
        return new JAXBElement<GetJobDetailsResponse>(_GetJobDetailsResponse_QNAME, GetJobDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEvaluationResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getEvaluationResult")
    public JAXBElement<GetEvaluationResult> createGetEvaluationResult(GetEvaluationResult value) {
        return new JAXBElement<GetEvaluationResult>(_GetEvaluationResult_QNAME, GetEvaluationResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEvaluationResultResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getEvaluationResultResponse")
    public JAXBElement<GetEvaluationResultResponse> createGetEvaluationResultResponse(GetEvaluationResultResponse value) {
        return new JAXBElement<GetEvaluationResultResponse>(_GetEvaluationResultResponse_QNAME, GetEvaluationResultResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateEvaluationJob }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "createEvaluationJob")
    public JAXBElement<CreateEvaluationJob> createCreateEvaluationJob(CreateEvaluationJob value) {
        return new JAXBElement<CreateEvaluationJob>(_CreateEvaluationJob_QNAME, CreateEvaluationJob.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetJobStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ps.common.cat.robust.itinnovation.soton.ac.uk/", name = "getJobStatus")
    public JAXBElement<GetJobStatus> createGetJobStatus(GetJobStatus value) {
        return new JAXBElement<GetJobStatus>(_GetJobStatus_QNAME, GetJobStatus.class, null, value);
    }

}
