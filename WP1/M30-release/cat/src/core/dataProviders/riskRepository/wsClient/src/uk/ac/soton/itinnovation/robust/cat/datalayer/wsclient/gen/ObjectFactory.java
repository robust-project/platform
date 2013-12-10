/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2011
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
//      Created By :            bmn
//      Created Date :          26-Sep-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen package. 
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

    private final static QName _DeleteRiskResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deleteRiskResponse");
    private final static QName _GetRisks_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getRisks");
    private final static QName _GetCommunityByUUID_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getCommunityByUUID");
    private final static QName _GetObjectivesResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getObjectivesResponse");
    private final static QName _SaveEvaluationResults_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "saveEvaluationResults");
    private final static QName _SaveRisk_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "saveRisk");
    private final static QName _AddCommunity_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "addCommunity");
    private final static QName _DeleteRisk_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deleteRisk");
    private final static QName _GetCommunityByUUIDResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getCommunityByUUIDResponse");
    private final static QName _GetRiskEvalResults_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getRiskEvalResults");
    private final static QName _GetRiskByUUID_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getRiskByUUID");
    private final static QName _UpdateRiskResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "updateRiskResponse");
    private final static QName _AddPredictorResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "addPredictorResponse");
    private final static QName _GetAllActiveRisksResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getAllActiveRisksResponse");
    private final static QName _DeleteCommunityByUUIDResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deleteCommunityByUUIDResponse");
    private final static QName _DeleteEvaluationResults_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deleteEvaluationResults");
    private final static QName _GetRiskByUUIDResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getRiskByUUIDResponse");
    private final static QName _GetRisksResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getRisksResponse");
    private final static QName _GetActiveRisks_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getActiveRisks");
    private final static QName _DeleteCommunityByUUID_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deleteCommunityByUUID");
    private final static QName _AddObjective_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "addObjective");
    private final static QName _GetAllActiveRisks_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getAllActiveRisks");
    private final static QName _GetPredictor_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getPredictor");
    private final static QName _DeleteObjective_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deleteObjective");
    private final static QName _UpdateRisk_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "updateRisk");
    private final static QName _DeletePredictorResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deletePredictorResponse");
    private final static QName _GetCommunitiesResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getCommunitiesResponse");
    private final static QName _AddCommunityResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "addCommunityResponse");
    private final static QName _DeleteObjectiveResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deleteObjectiveResponse");
    private final static QName _DeleteEvaluationResultsResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deleteEvaluationResultsResponse");
    private final static QName _DeletePredictor_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "deletePredictor");
    private final static QName _GetActiveRisksResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getActiveRisksResponse");
    private final static QName _GetPredictors_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getPredictors");
    private final static QName _GetPredictorResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getPredictorResponse");
    private final static QName _GetPredictorsResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getPredictorsResponse");
    private final static QName _SaveEvaluationResultsResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "saveEvaluationResultsResponse");
    private final static QName _GetRiskEvalResultsResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getRiskEvalResultsResponse");
    private final static QName _RunScriptResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "runScriptResponse");
    private final static QName _AddObjectiveResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "addObjectiveResponse");
    private final static QName _RunScript_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "runScript");
    private final static QName _GetObjectives_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getObjectives");
    private final static QName _Exception_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "Exception");
    private final static QName _AddPredictor_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "addPredictor");
    private final static QName _SaveRiskResponse_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "saveRiskResponse");
    private final static QName _GetCommunities_QNAME = new QName("http://dataservice.ws.robust.swmind.pl/", "getCommunities");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetRisksResponse }
     * 
     */
    public GetRisksResponse createGetRisksResponse() {
        return new GetRisksResponse();
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

    /**
     * Create an instance of {@link ParameterValue }
     * 
     */
    public ParameterValue createParameterValue() {
        return new ParameterValue();
    }

    /**
     * Create an instance of {@link Impact.ImpactMap.Entry }
     * 
     */
    public Impact.ImpactMap.Entry createImpactImpactMapEntry() {
        return new Impact.ImpactMap.Entry();
    }

    /**
     * Create an instance of {@link DeleteCommunityByUUID }
     * 
     */
    public DeleteCommunityByUUID createDeleteCommunityByUUID() {
        return new DeleteCommunityByUUID();
    }

    /**
     * Create an instance of {@link GetRiskEvalResultsResponse }
     * 
     */
    public GetRiskEvalResultsResponse createGetRiskEvalResultsResponse() {
        return new GetRiskEvalResultsResponse();
    }

    /**
     * Create an instance of {@link Community }
     * 
     */
    public Community createCommunity() {
        return new Community();
    }

    /**
     * Create an instance of {@link Objective }
     * 
     */
    public Objective createObjective() {
        return new Objective();
    }

    /**
     * Create an instance of {@link TreatmentWFs.TreatmentsTemplatesByID.Entry }
     * 
     */
    public TreatmentWFs.TreatmentsTemplatesByID.Entry createTreatmentWFsTreatmentsTemplatesByIDEntry() {
        return new TreatmentWFs.TreatmentsTemplatesByID.Entry();
    }

    /**
     * Create an instance of {@link SaveEvaluationResults }
     * 
     */
    public SaveEvaluationResults createSaveEvaluationResults() {
        return new SaveEvaluationResults();
    }

    /**
     * Create an instance of {@link TreatmentWFs.TemplatesByOrder.Entry }
     * 
     */
    public TreatmentWFs.TemplatesByOrder.Entry createTreatmentWFsTemplatesByOrderEntry() {
        return new TreatmentWFs.TemplatesByOrder.Entry();
    }

    /**
     * Create an instance of {@link GetCommunityByUUID }
     * 
     */
    public GetCommunityByUUID createGetCommunityByUUID() {
        return new GetCommunityByUUID();
    }

    /**
     * Create an instance of {@link TreatmentTemplate }
     * 
     */
    public TreatmentTemplate createTreatmentTemplate() {
        return new TreatmentTemplate();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link Risk }
     * 
     */
    public Risk createRisk() {
        return new Risk();
    }

    /**
     * Create an instance of {@link UpdateRisk }
     * 
     */
    public UpdateRisk createUpdateRisk() {
        return new UpdateRisk();
    }

    /**
     * Create an instance of {@link Impact }
     * 
     */
    public Impact createImpact() {
        return new Impact();
    }

    /**
     * Create an instance of {@link ValueConstraint }
     * 
     */
    public ValueConstraint createValueConstraint() {
        return new ValueConstraint();
    }

    /**
     * Create an instance of {@link GetCommunitiesResponse }
     * 
     */
    public GetCommunitiesResponse createGetCommunitiesResponse() {
        return new GetCommunitiesResponse();
    }

    /**
     * Create an instance of {@link AddCommunityResponse }
     * 
     */
    public AddCommunityResponse createAddCommunityResponse() {
        return new AddCommunityResponse();
    }

    /**
     * Create an instance of {@link GetPredictorResponse }
     * 
     */
    public GetPredictorResponse createGetPredictorResponse() {
        return new GetPredictorResponse();
    }

    /**
     * Create an instance of {@link GetPredictors }
     * 
     */
    public GetPredictors createGetPredictors() {
        return new GetPredictors();
    }

    /**
     * Create an instance of {@link DeleteEvaluationResults }
     * 
     */
    public DeleteEvaluationResults createDeleteEvaluationResults() {
        return new DeleteEvaluationResults();
    }

    /**
     * Create an instance of {@link DeleteObjective }
     * 
     */
    public DeleteObjective createDeleteObjective() {
        return new DeleteObjective();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link GetRiskByUUIDResponse }
     * 
     */
    public GetRiskByUUIDResponse createGetRiskByUUIDResponse() {
        return new GetRiskByUUIDResponse();
    }

    /**
     * Create an instance of {@link JobDetails }
     * 
     */
    public JobDetails createJobDetails() {
        return new JobDetails();
    }

    /**
     * Create an instance of {@link SaveRisk }
     * 
     */
    public SaveRisk createSaveRisk() {
        return new SaveRisk();
    }

    /**
     * Create an instance of {@link AddObjectiveResponse }
     * 
     */
    public AddObjectiveResponse createAddObjectiveResponse() {
        return new AddObjectiveResponse();
    }

    /**
     * Create an instance of {@link DeleteRiskResponse }
     * 
     */
    public DeleteRiskResponse createDeleteRiskResponse() {
        return new DeleteRiskResponse();
    }

    /**
     * Create an instance of {@link RunScriptResponse }
     * 
     */
    public RunScriptResponse createRunScriptResponse() {
        return new RunScriptResponse();
    }

    /**
     * Create an instance of {@link TreatmentWFs.TreatmentsTemplatesByID }
     * 
     */
    public TreatmentWFs.TreatmentsTemplatesByID createTreatmentWFsTreatmentsTemplatesByID() {
        return new TreatmentWFs.TreatmentsTemplatesByID();
    }

    /**
     * Create an instance of {@link EvaluationResult }
     * 
     */
    public EvaluationResult createEvaluationResult() {
        return new EvaluationResult();
    }

    /**
     * Create an instance of {@link GetPredictor }
     * 
     */
    public GetPredictor createGetPredictor() {
        return new GetPredictor();
    }

    /**
     * Create an instance of {@link GetAllActiveRisks }
     * 
     */
    public GetAllActiveRisks createGetAllActiveRisks() {
        return new GetAllActiveRisks();
    }

    /**
     * Create an instance of {@link EventCondition }
     * 
     */
    public EventCondition createEventCondition() {
        return new EventCondition();
    }

    /**
     * Create an instance of {@link GetRisks }
     * 
     */
    public GetRisks createGetRisks() {
        return new GetRisks();
    }

    /**
     * Create an instance of {@link PredictorServiceDescription }
     * 
     */
    public PredictorServiceDescription createPredictorServiceDescription() {
        return new PredictorServiceDescription();
    }

    /**
     * Create an instance of {@link Event }
     * 
     */
    public Event createEvent() {
        return new Event();
    }

    /**
     * Create an instance of {@link GetObjectives }
     * 
     */
    public GetObjectives createGetObjectives() {
        return new GetObjectives();
    }

    /**
     * Create an instance of {@link DeleteCommunityByUUIDResponse }
     * 
     */
    public DeleteCommunityByUUIDResponse createDeleteCommunityByUUIDResponse() {
        return new DeleteCommunityByUUIDResponse();
    }

    /**
     * Create an instance of {@link UpdateRiskResponse }
     * 
     */
    public UpdateRiskResponse createUpdateRiskResponse() {
        return new UpdateRiskResponse();
    }

    /**
     * Create an instance of {@link Impact.ImpactMap }
     * 
     */
    public Impact.ImpactMap createImpactImpactMap() {
        return new Impact.ImpactMap();
    }

    /**
     * Create an instance of {@link SaveRiskResponse }
     * 
     */
    public SaveRiskResponse createSaveRiskResponse() {
        return new SaveRiskResponse();
    }

    /**
     * Create an instance of {@link DeletePredictorResponse }
     * 
     */
    public DeletePredictorResponse createDeletePredictorResponse() {
        return new DeletePredictorResponse();
    }

    /**
     * Create an instance of {@link DeleteObjectiveResponse }
     * 
     */
    public DeleteObjectiveResponse createDeleteObjectiveResponse() {
        return new DeleteObjectiveResponse();
    }

    /**
     * Create an instance of {@link RunScript }
     * 
     */
    public RunScript createRunScript() {
        return new RunScript();
    }

    /**
     * Create an instance of {@link AddObjective }
     * 
     */
    public AddObjective createAddObjective() {
        return new AddObjective();
    }

    /**
     * Create an instance of {@link DeleteRisk }
     * 
     */
    public DeleteRisk createDeleteRisk() {
        return new DeleteRisk();
    }

    /**
     * Create an instance of {@link DeletePredictor }
     * 
     */
    public DeletePredictor createDeletePredictor() {
        return new DeletePredictor();
    }

    /**
     * Create an instance of {@link CommunityRisksElement }
     * 
     */
    public CommunityRisksElement createCommunityRisksElement() {
        return new CommunityRisksElement();
    }

    /**
     * Create an instance of {@link AddPredictor }
     * 
     */
    public AddPredictor createAddPredictor() {
        return new AddPredictor();
    }

    /**
     * Create an instance of {@link GetActiveRisksResponse }
     * 
     */
    public GetActiveRisksResponse createGetActiveRisksResponse() {
        return new GetActiveRisksResponse();
    }

    /**
     * Create an instance of {@link TreeMap }
     * 
     */
    public TreeMap createTreeMap() {
        return new TreeMap();
    }

    /**
     * Create an instance of {@link GetRiskEvalResults }
     * 
     */
    public GetRiskEvalResults createGetRiskEvalResults() {
        return new GetRiskEvalResults();
    }

    /**
     * Create an instance of {@link JobStatus }
     * 
     */
    public JobStatus createJobStatus() {
        return new JobStatus();
    }

    /**
     * Create an instance of {@link GetCommunities }
     * 
     */
    public GetCommunities createGetCommunities() {
        return new GetCommunities();
    }

    /**
     * Create an instance of {@link GetAllActiveRisksResponse }
     * 
     */
    public GetAllActiveRisksResponse createGetAllActiveRisksResponse() {
        return new GetAllActiveRisksResponse();
    }

    /**
     * Create an instance of {@link GetCommunityByUUIDResponse }
     * 
     */
    public GetCommunityByUUIDResponse createGetCommunityByUUIDResponse() {
        return new GetCommunityByUUIDResponse();
    }

    /**
     * Create an instance of {@link SaveEvaluationResultsResponse }
     * 
     */
    public SaveEvaluationResultsResponse createSaveEvaluationResultsResponse() {
        return new SaveEvaluationResultsResponse();
    }

    /**
     * Create an instance of {@link GetPredictorsResponse }
     * 
     */
    public GetPredictorsResponse createGetPredictorsResponse() {
        return new GetPredictorsResponse();
    }

    /**
     * Create an instance of {@link GetActiveRisks }
     * 
     */
    public GetActiveRisks createGetActiveRisks() {
        return new GetActiveRisks();
    }

    /**
     * Create an instance of {@link GetObjectivesResponse }
     * 
     */
    public GetObjectivesResponse createGetObjectivesResponse() {
        return new GetObjectivesResponse();
    }

    /**
     * Create an instance of {@link DeleteEvaluationResultsResponse }
     * 
     */
    public DeleteEvaluationResultsResponse createDeleteEvaluationResultsResponse() {
        return new DeleteEvaluationResultsResponse();
    }

    /**
     * Create an instance of {@link TreatmentWFs.TemplatesByOrder }
     * 
     */
    public TreatmentWFs.TemplatesByOrder createTreatmentWFsTemplatesByOrder() {
        return new TreatmentWFs.TemplatesByOrder();
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
     * Create an instance of {@link ArrayList }
     * 
     */
    public ArrayList createArrayList() {
        return new ArrayList();
    }

    /**
     * Create an instance of {@link TreatmentWFs }
     * 
     */
    public TreatmentWFs createTreatmentWFs() {
        return new TreatmentWFs();
    }

    /**
     * Create an instance of {@link GetRiskByUUID }
     * 
     */
    public GetRiskByUUID createGetRiskByUUID() {
        return new GetRiskByUUID();
    }

    /**
     * Create an instance of {@link AddCommunity }
     * 
     */
    public AddCommunity createAddCommunity() {
        return new AddCommunity();
    }

    /**
     * Create an instance of {@link AddPredictorResponse }
     * 
     */
    public AddPredictorResponse createAddPredictorResponse() {
        return new AddPredictorResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteRiskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deleteRiskResponse")
    public JAXBElement<DeleteRiskResponse> createDeleteRiskResponse(DeleteRiskResponse value) {
        return new JAXBElement<DeleteRiskResponse>(_DeleteRiskResponse_QNAME, DeleteRiskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRisks }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getRisks")
    public JAXBElement<GetRisks> createGetRisks(GetRisks value) {
        return new JAXBElement<GetRisks>(_GetRisks_QNAME, GetRisks.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCommunityByUUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getCommunityByUUID")
    public JAXBElement<GetCommunityByUUID> createGetCommunityByUUID(GetCommunityByUUID value) {
        return new JAXBElement<GetCommunityByUUID>(_GetCommunityByUUID_QNAME, GetCommunityByUUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetObjectivesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getObjectivesResponse")
    public JAXBElement<GetObjectivesResponse> createGetObjectivesResponse(GetObjectivesResponse value) {
        return new JAXBElement<GetObjectivesResponse>(_GetObjectivesResponse_QNAME, GetObjectivesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveEvaluationResults }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "saveEvaluationResults")
    public JAXBElement<SaveEvaluationResults> createSaveEvaluationResults(SaveEvaluationResults value) {
        return new JAXBElement<SaveEvaluationResults>(_SaveEvaluationResults_QNAME, SaveEvaluationResults.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveRisk }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "saveRisk")
    public JAXBElement<SaveRisk> createSaveRisk(SaveRisk value) {
        return new JAXBElement<SaveRisk>(_SaveRisk_QNAME, SaveRisk.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddCommunity }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "addCommunity")
    public JAXBElement<AddCommunity> createAddCommunity(AddCommunity value) {
        return new JAXBElement<AddCommunity>(_AddCommunity_QNAME, AddCommunity.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteRisk }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deleteRisk")
    public JAXBElement<DeleteRisk> createDeleteRisk(DeleteRisk value) {
        return new JAXBElement<DeleteRisk>(_DeleteRisk_QNAME, DeleteRisk.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCommunityByUUIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getCommunityByUUIDResponse")
    public JAXBElement<GetCommunityByUUIDResponse> createGetCommunityByUUIDResponse(GetCommunityByUUIDResponse value) {
        return new JAXBElement<GetCommunityByUUIDResponse>(_GetCommunityByUUIDResponse_QNAME, GetCommunityByUUIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRiskEvalResults }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getRiskEvalResults")
    public JAXBElement<GetRiskEvalResults> createGetRiskEvalResults(GetRiskEvalResults value) {
        return new JAXBElement<GetRiskEvalResults>(_GetRiskEvalResults_QNAME, GetRiskEvalResults.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRiskByUUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getRiskByUUID")
    public JAXBElement<GetRiskByUUID> createGetRiskByUUID(GetRiskByUUID value) {
        return new JAXBElement<GetRiskByUUID>(_GetRiskByUUID_QNAME, GetRiskByUUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateRiskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "updateRiskResponse")
    public JAXBElement<UpdateRiskResponse> createUpdateRiskResponse(UpdateRiskResponse value) {
        return new JAXBElement<UpdateRiskResponse>(_UpdateRiskResponse_QNAME, UpdateRiskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddPredictorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "addPredictorResponse")
    public JAXBElement<AddPredictorResponse> createAddPredictorResponse(AddPredictorResponse value) {
        return new JAXBElement<AddPredictorResponse>(_AddPredictorResponse_QNAME, AddPredictorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllActiveRisksResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getAllActiveRisksResponse")
    public JAXBElement<GetAllActiveRisksResponse> createGetAllActiveRisksResponse(GetAllActiveRisksResponse value) {
        return new JAXBElement<GetAllActiveRisksResponse>(_GetAllActiveRisksResponse_QNAME, GetAllActiveRisksResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCommunityByUUIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deleteCommunityByUUIDResponse")
    public JAXBElement<DeleteCommunityByUUIDResponse> createDeleteCommunityByUUIDResponse(DeleteCommunityByUUIDResponse value) {
        return new JAXBElement<DeleteCommunityByUUIDResponse>(_DeleteCommunityByUUIDResponse_QNAME, DeleteCommunityByUUIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteEvaluationResults }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deleteEvaluationResults")
    public JAXBElement<DeleteEvaluationResults> createDeleteEvaluationResults(DeleteEvaluationResults value) {
        return new JAXBElement<DeleteEvaluationResults>(_DeleteEvaluationResults_QNAME, DeleteEvaluationResults.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRiskByUUIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getRiskByUUIDResponse")
    public JAXBElement<GetRiskByUUIDResponse> createGetRiskByUUIDResponse(GetRiskByUUIDResponse value) {
        return new JAXBElement<GetRiskByUUIDResponse>(_GetRiskByUUIDResponse_QNAME, GetRiskByUUIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRisksResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getRisksResponse")
    public JAXBElement<GetRisksResponse> createGetRisksResponse(GetRisksResponse value) {
        return new JAXBElement<GetRisksResponse>(_GetRisksResponse_QNAME, GetRisksResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetActiveRisks }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getActiveRisks")
    public JAXBElement<GetActiveRisks> createGetActiveRisks(GetActiveRisks value) {
        return new JAXBElement<GetActiveRisks>(_GetActiveRisks_QNAME, GetActiveRisks.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCommunityByUUID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deleteCommunityByUUID")
    public JAXBElement<DeleteCommunityByUUID> createDeleteCommunityByUUID(DeleteCommunityByUUID value) {
        return new JAXBElement<DeleteCommunityByUUID>(_DeleteCommunityByUUID_QNAME, DeleteCommunityByUUID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddObjective }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "addObjective")
    public JAXBElement<AddObjective> createAddObjective(AddObjective value) {
        return new JAXBElement<AddObjective>(_AddObjective_QNAME, AddObjective.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllActiveRisks }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getAllActiveRisks")
    public JAXBElement<GetAllActiveRisks> createGetAllActiveRisks(GetAllActiveRisks value) {
        return new JAXBElement<GetAllActiveRisks>(_GetAllActiveRisks_QNAME, GetAllActiveRisks.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPredictor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getPredictor")
    public JAXBElement<GetPredictor> createGetPredictor(GetPredictor value) {
        return new JAXBElement<GetPredictor>(_GetPredictor_QNAME, GetPredictor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteObjective }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deleteObjective")
    public JAXBElement<DeleteObjective> createDeleteObjective(DeleteObjective value) {
        return new JAXBElement<DeleteObjective>(_DeleteObjective_QNAME, DeleteObjective.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateRisk }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "updateRisk")
    public JAXBElement<UpdateRisk> createUpdateRisk(UpdateRisk value) {
        return new JAXBElement<UpdateRisk>(_UpdateRisk_QNAME, UpdateRisk.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePredictorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deletePredictorResponse")
    public JAXBElement<DeletePredictorResponse> createDeletePredictorResponse(DeletePredictorResponse value) {
        return new JAXBElement<DeletePredictorResponse>(_DeletePredictorResponse_QNAME, DeletePredictorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCommunitiesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getCommunitiesResponse")
    public JAXBElement<GetCommunitiesResponse> createGetCommunitiesResponse(GetCommunitiesResponse value) {
        return new JAXBElement<GetCommunitiesResponse>(_GetCommunitiesResponse_QNAME, GetCommunitiesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddCommunityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "addCommunityResponse")
    public JAXBElement<AddCommunityResponse> createAddCommunityResponse(AddCommunityResponse value) {
        return new JAXBElement<AddCommunityResponse>(_AddCommunityResponse_QNAME, AddCommunityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteObjectiveResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deleteObjectiveResponse")
    public JAXBElement<DeleteObjectiveResponse> createDeleteObjectiveResponse(DeleteObjectiveResponse value) {
        return new JAXBElement<DeleteObjectiveResponse>(_DeleteObjectiveResponse_QNAME, DeleteObjectiveResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteEvaluationResultsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deleteEvaluationResultsResponse")
    public JAXBElement<DeleteEvaluationResultsResponse> createDeleteEvaluationResultsResponse(DeleteEvaluationResultsResponse value) {
        return new JAXBElement<DeleteEvaluationResultsResponse>(_DeleteEvaluationResultsResponse_QNAME, DeleteEvaluationResultsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeletePredictor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "deletePredictor")
    public JAXBElement<DeletePredictor> createDeletePredictor(DeletePredictor value) {
        return new JAXBElement<DeletePredictor>(_DeletePredictor_QNAME, DeletePredictor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetActiveRisksResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getActiveRisksResponse")
    public JAXBElement<GetActiveRisksResponse> createGetActiveRisksResponse(GetActiveRisksResponse value) {
        return new JAXBElement<GetActiveRisksResponse>(_GetActiveRisksResponse_QNAME, GetActiveRisksResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPredictors }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getPredictors")
    public JAXBElement<GetPredictors> createGetPredictors(GetPredictors value) {
        return new JAXBElement<GetPredictors>(_GetPredictors_QNAME, GetPredictors.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPredictorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getPredictorResponse")
    public JAXBElement<GetPredictorResponse> createGetPredictorResponse(GetPredictorResponse value) {
        return new JAXBElement<GetPredictorResponse>(_GetPredictorResponse_QNAME, GetPredictorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPredictorsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getPredictorsResponse")
    public JAXBElement<GetPredictorsResponse> createGetPredictorsResponse(GetPredictorsResponse value) {
        return new JAXBElement<GetPredictorsResponse>(_GetPredictorsResponse_QNAME, GetPredictorsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveEvaluationResultsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "saveEvaluationResultsResponse")
    public JAXBElement<SaveEvaluationResultsResponse> createSaveEvaluationResultsResponse(SaveEvaluationResultsResponse value) {
        return new JAXBElement<SaveEvaluationResultsResponse>(_SaveEvaluationResultsResponse_QNAME, SaveEvaluationResultsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRiskEvalResultsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getRiskEvalResultsResponse")
    public JAXBElement<GetRiskEvalResultsResponse> createGetRiskEvalResultsResponse(GetRiskEvalResultsResponse value) {
        return new JAXBElement<GetRiskEvalResultsResponse>(_GetRiskEvalResultsResponse_QNAME, GetRiskEvalResultsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RunScriptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "runScriptResponse")
    public JAXBElement<RunScriptResponse> createRunScriptResponse(RunScriptResponse value) {
        return new JAXBElement<RunScriptResponse>(_RunScriptResponse_QNAME, RunScriptResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddObjectiveResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "addObjectiveResponse")
    public JAXBElement<AddObjectiveResponse> createAddObjectiveResponse(AddObjectiveResponse value) {
        return new JAXBElement<AddObjectiveResponse>(_AddObjectiveResponse_QNAME, AddObjectiveResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RunScript }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "runScript")
    public JAXBElement<RunScript> createRunScript(RunScript value) {
        return new JAXBElement<RunScript>(_RunScript_QNAME, RunScript.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetObjectives }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getObjectives")
    public JAXBElement<GetObjectives> createGetObjectives(GetObjectives value) {
        return new JAXBElement<GetObjectives>(_GetObjectives_QNAME, GetObjectives.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddPredictor }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "addPredictor")
    public JAXBElement<AddPredictor> createAddPredictor(AddPredictor value) {
        return new JAXBElement<AddPredictor>(_AddPredictor_QNAME, AddPredictor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveRiskResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "saveRiskResponse")
    public JAXBElement<SaveRiskResponse> createSaveRiskResponse(SaveRiskResponse value) {
        return new JAXBElement<SaveRiskResponse>(_SaveRiskResponse_QNAME, SaveRiskResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCommunities }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dataservice.ws.robust.swmind.pl/", name = "getCommunities")
    public JAXBElement<GetCommunities> createGetCommunities(GetCommunities value) {
        return new JAXBElement<GetCommunities>(_GetCommunities_QNAME, GetCommunities.class, null, value);
    }

}
