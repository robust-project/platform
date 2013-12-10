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
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen package. 
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

    private final static QName _StartEngine_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "startEngine");
    private final static QName _StopEngineResponse_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "stopEngineResponse");
    private final static QName _StopEngine_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "stopEngine");
    private final static QName _IsEngineRunningResponse_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "isEngineRunningResponse");
    private final static QName _StartEngineResponse_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "startEngineResponse");
    private final static QName _StartEngineForTimePeriod_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "startEngineForTimePeriod");
    private final static QName _NewEvaluationResultResponse_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "newEvaluationResultResponse");
    private final static QName _GetCommunityResultsStreamDetails_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "getCommunityResultsStreamDetails");
    private final static QName _GetGeneralStreamDetails_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "getGeneralStreamDetails");
    private final static QName _GetCommunityResultsStreamDetailsResponse_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "getCommunityResultsStreamDetailsResponse");
    private final static QName _NewEvaluationResult_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "newEvaluationResult");
    private final static QName _StartEngineForTimePeriodResponse_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "startEngineForTimePeriodResponse");
    private final static QName _GetGeneralStreamDetailsResponse_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "getGeneralStreamDetailsResponse");
    private final static QName _UpdateEvaluationJobStatusResponse_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "updateEvaluationJobStatusResponse");
    private final static QName _UpdateEvaluationJobStatus_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "updateEvaluationJobStatus");
    private final static QName _IsEngineRunning_QNAME = new QName("http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "isEngineRunning");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EvaluationResult }
     * 
     */
    public EvaluationResult createEvaluationResult() {
        return new EvaluationResult();
    }

    /**
     * Create an instance of {@link GetGeneralStreamDetails }
     * 
     */
    public GetGeneralStreamDetails createGetGeneralStreamDetails() {
        return new GetGeneralStreamDetails();
    }

    /**
     * Create an instance of {@link NewEvaluationResult }
     * 
     */
    public NewEvaluationResult createNewEvaluationResult() {
        return new NewEvaluationResult();
    }

    /**
     * Create an instance of {@link KeyValuePair }
     * 
     */
    public KeyValuePair createKeyValuePair() {
        return new KeyValuePair();
    }

    /**
     * Create an instance of {@link GetGeneralStreamDetailsResponse }
     * 
     */
    public GetGeneralStreamDetailsResponse createGetGeneralStreamDetailsResponse() {
        return new GetGeneralStreamDetailsResponse();
    }

    /**
     * Create an instance of {@link JobDetails }
     * 
     */
    public JobDetails createJobDetails() {
        return new JobDetails();
    }

    /**
     * Create an instance of {@link UpdateEvaluationJobStatusResponse }
     * 
     */
    public UpdateEvaluationJobStatusResponse createUpdateEvaluationJobStatusResponse() {
        return new UpdateEvaluationJobStatusResponse();
    }

    /**
     * Create an instance of {@link GetCommunityResultsStreamDetailsResponse }
     * 
     */
    public GetCommunityResultsStreamDetailsResponse createGetCommunityResultsStreamDetailsResponse() {
        return new GetCommunityResultsStreamDetailsResponse();
    }

    /**
     * Create an instance of {@link GetCommunityResultsStreamDetails }
     * 
     */
    public GetCommunityResultsStreamDetails createGetCommunityResultsStreamDetails() {
        return new GetCommunityResultsStreamDetails();
    }

    /**
     * Create an instance of {@link StartEngineForTimePeriodResponse }
     * 
     */
    public StartEngineForTimePeriodResponse createStartEngineForTimePeriodResponse() {
        return new StartEngineForTimePeriodResponse();
    }

    /**
     * Create an instance of {@link JobStatus }
     * 
     */
    public JobStatus createJobStatus() {
        return new JobStatus();
    }

    /**
     * Create an instance of {@link StartEngineResponse }
     * 
     */
    public StartEngineResponse createStartEngineResponse() {
        return new StartEngineResponse();
    }

    /**
     * Create an instance of {@link IsEngineRunning }
     * 
     */
    public IsEngineRunning createIsEngineRunning() {
        return new IsEngineRunning();
    }

    /**
     * Create an instance of {@link StreamDetails }
     * 
     */
    public StreamDetails createStreamDetails() {
        return new StreamDetails();
    }

    /**
     * Create an instance of {@link Status }
     * 
     */
    public Status createStatus() {
        return new Status();
    }

    /**
     * Create an instance of {@link UpdateEvaluationJobStatus }
     * 
     */
    public UpdateEvaluationJobStatus createUpdateEvaluationJobStatus() {
        return new UpdateEvaluationJobStatus();
    }

    /**
     * Create an instance of {@link NewEvaluationResultResponse }
     * 
     */
    public NewEvaluationResultResponse createNewEvaluationResultResponse() {
        return new NewEvaluationResultResponse();
    }

    /**
     * Create an instance of {@link StartEngineForTimePeriod }
     * 
     */
    public StartEngineForTimePeriod createStartEngineForTimePeriod() {
        return new StartEngineForTimePeriod();
    }

    /**
     * Create an instance of {@link StartEngine }
     * 
     */
    public StartEngine createStartEngine() {
        return new StartEngine();
    }

    /**
     * Create an instance of {@link StopEngineResponse }
     * 
     */
    public StopEngineResponse createStopEngineResponse() {
        return new StopEngineResponse();
    }

    /**
     * Create an instance of {@link IsEngineRunningResponse }
     * 
     */
    public IsEngineRunningResponse createIsEngineRunningResponse() {
        return new IsEngineRunningResponse();
    }

    /**
     * Create an instance of {@link StopEngine }
     * 
     */
    public StopEngine createStopEngine() {
        return new StopEngine();
    }

    /**
     * Create an instance of {@link ResultItem }
     * 
     */
    public ResultItem createResultItem() {
        return new ResultItem();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartEngine }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "startEngine")
    public JAXBElement<StartEngine> createStartEngine(StartEngine value) {
        return new JAXBElement<StartEngine>(_StartEngine_QNAME, StartEngine.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopEngineResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "stopEngineResponse")
    public JAXBElement<StopEngineResponse> createStopEngineResponse(StopEngineResponse value) {
        return new JAXBElement<StopEngineResponse>(_StopEngineResponse_QNAME, StopEngineResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopEngine }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "stopEngine")
    public JAXBElement<StopEngine> createStopEngine(StopEngine value) {
        return new JAXBElement<StopEngine>(_StopEngine_QNAME, StopEngine.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsEngineRunningResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "isEngineRunningResponse")
    public JAXBElement<IsEngineRunningResponse> createIsEngineRunningResponse(IsEngineRunningResponse value) {
        return new JAXBElement<IsEngineRunningResponse>(_IsEngineRunningResponse_QNAME, IsEngineRunningResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartEngineResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "startEngineResponse")
    public JAXBElement<StartEngineResponse> createStartEngineResponse(StartEngineResponse value) {
        return new JAXBElement<StartEngineResponse>(_StartEngineResponse_QNAME, StartEngineResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartEngineForTimePeriod }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "startEngineForTimePeriod")
    public JAXBElement<StartEngineForTimePeriod> createStartEngineForTimePeriod(StartEngineForTimePeriod value) {
        return new JAXBElement<StartEngineForTimePeriod>(_StartEngineForTimePeriod_QNAME, StartEngineForTimePeriod.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NewEvaluationResultResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "newEvaluationResultResponse")
    public JAXBElement<NewEvaluationResultResponse> createNewEvaluationResultResponse(NewEvaluationResultResponse value) {
        return new JAXBElement<NewEvaluationResultResponse>(_NewEvaluationResultResponse_QNAME, NewEvaluationResultResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCommunityResultsStreamDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "getCommunityResultsStreamDetails")
    public JAXBElement<GetCommunityResultsStreamDetails> createGetCommunityResultsStreamDetails(GetCommunityResultsStreamDetails value) {
        return new JAXBElement<GetCommunityResultsStreamDetails>(_GetCommunityResultsStreamDetails_QNAME, GetCommunityResultsStreamDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetGeneralStreamDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "getGeneralStreamDetails")
    public JAXBElement<GetGeneralStreamDetails> createGetGeneralStreamDetails(GetGeneralStreamDetails value) {
        return new JAXBElement<GetGeneralStreamDetails>(_GetGeneralStreamDetails_QNAME, GetGeneralStreamDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCommunityResultsStreamDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "getCommunityResultsStreamDetailsResponse")
    public JAXBElement<GetCommunityResultsStreamDetailsResponse> createGetCommunityResultsStreamDetailsResponse(GetCommunityResultsStreamDetailsResponse value) {
        return new JAXBElement<GetCommunityResultsStreamDetailsResponse>(_GetCommunityResultsStreamDetailsResponse_QNAME, GetCommunityResultsStreamDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NewEvaluationResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "newEvaluationResult")
    public JAXBElement<NewEvaluationResult> createNewEvaluationResult(NewEvaluationResult value) {
        return new JAXBElement<NewEvaluationResult>(_NewEvaluationResult_QNAME, NewEvaluationResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartEngineForTimePeriodResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "startEngineForTimePeriodResponse")
    public JAXBElement<StartEngineForTimePeriodResponse> createStartEngineForTimePeriodResponse(StartEngineForTimePeriodResponse value) {
        return new JAXBElement<StartEngineForTimePeriodResponse>(_StartEngineForTimePeriodResponse_QNAME, StartEngineForTimePeriodResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetGeneralStreamDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "getGeneralStreamDetailsResponse")
    public JAXBElement<GetGeneralStreamDetailsResponse> createGetGeneralStreamDetailsResponse(GetGeneralStreamDetailsResponse value) {
        return new JAXBElement<GetGeneralStreamDetailsResponse>(_GetGeneralStreamDetailsResponse_QNAME, GetGeneralStreamDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateEvaluationJobStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "updateEvaluationJobStatusResponse")
    public JAXBElement<UpdateEvaluationJobStatusResponse> createUpdateEvaluationJobStatusResponse(UpdateEvaluationJobStatusResponse value) {
        return new JAXBElement<UpdateEvaluationJobStatusResponse>(_UpdateEvaluationJobStatusResponse_QNAME, UpdateEvaluationJobStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateEvaluationJobStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "updateEvaluationJobStatus")
    public JAXBElement<UpdateEvaluationJobStatus> createUpdateEvaluationJobStatus(UpdateEvaluationJobStatus value) {
        return new JAXBElement<UpdateEvaluationJobStatus>(_UpdateEvaluationJobStatus_QNAME, UpdateEvaluationJobStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsEngineRunning }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "isEngineRunning")
    public JAXBElement<IsEngineRunning> createIsEngineRunning(IsEngineRunning value) {
        return new JAXBElement<IsEngineRunning>(_IsEngineRunning_QNAME, IsEngineRunning.class, null, value);
    }

}
