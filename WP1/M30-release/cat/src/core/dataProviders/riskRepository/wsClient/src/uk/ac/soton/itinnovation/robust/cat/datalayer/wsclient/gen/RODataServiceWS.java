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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.6.1
 * 2013-04-04T09:57:49.381+01:00
 * Generated source version: 2.6.1
 * 
 */
@WebService(targetNamespace = "http://dataservice.ws.robust.swmind.pl/", name = "RODataServiceWS")
@XmlSeeAlso({ObjectFactory.class})
public interface RODataServiceWS {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getPredictor", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetPredictor")
    @WebMethod
    @ResponseWrapper(localName = "getPredictorResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetPredictorResponse")
    public uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription getPredictor(
        @WebParam(name = "arg0", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getRisks", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetRisks")
    @WebMethod
    @ResponseWrapper(localName = "getRisksResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetRisksResponse")
    public java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk> getRisks(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getAllActiveRisks", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetAllActiveRisks")
    @WebMethod
    @ResponseWrapper(localName = "getAllActiveRisksResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetAllActiveRisksResponse")
    public java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.CommunityRisksElement> getAllActiveRisks();

    @RequestWrapper(localName = "deleteRisk", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeleteRisk")
    @WebMethod
    @ResponseWrapper(localName = "deleteRiskResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeleteRiskResponse")
    public void deleteRisk(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getPredictors", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetPredictors")
    @WebMethod
    @ResponseWrapper(localName = "getPredictorsResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetPredictorsResponse")
    public java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription> getPredictors();

    @RequestWrapper(localName = "deleteCommunityByUUID", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeleteCommunityByUUID")
    @WebMethod
    @ResponseWrapper(localName = "deleteCommunityByUUIDResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeleteCommunityByUUIDResponse")
    public void deleteCommunityByUUID(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @RequestWrapper(localName = "addCommunity", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.AddCommunity")
    @WebMethod
    @ResponseWrapper(localName = "addCommunityResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.AddCommunityResponse")
    public void addCommunity(
        @WebParam(name = "arg0", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community arg0
    );

    @RequestWrapper(localName = "deleteEvaluationResults", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeleteEvaluationResults")
    @WebMethod
    @ResponseWrapper(localName = "deleteEvaluationResultsResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeleteEvaluationResultsResponse")
    public void deleteEvaluationResults(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getCommunities", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetCommunities")
    @WebMethod
    @ResponseWrapper(localName = "getCommunitiesResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetCommunitiesResponse")
    public java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community> getCommunities();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "saveRisk", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.SaveRisk")
    @WebMethod
    @ResponseWrapper(localName = "saveRiskResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.SaveRiskResponse")
    public uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk saveRisk(
        @WebParam(name = "arg0", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        java.lang.String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        java.lang.String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        java.lang.Boolean arg3,
        @WebParam(name = "arg4", targetNamespace = "")
        java.lang.String arg4
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getRiskEvalResults", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetRiskEvalResults")
    @WebMethod
    @ResponseWrapper(localName = "getRiskEvalResultsResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetRiskEvalResultsResponse")
    public java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationResult> getRiskEvalResults(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        javax.xml.datatype.XMLGregorianCalendar arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        javax.xml.datatype.XMLGregorianCalendar arg2
    );

    @RequestWrapper(localName = "addPredictor", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.AddPredictor")
    @WebMethod
    @ResponseWrapper(localName = "addPredictorResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.AddPredictorResponse")
    public void addPredictor(
        @WebParam(name = "arg0", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getObjectives", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetObjectives")
    @WebMethod
    @ResponseWrapper(localName = "getObjectivesResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetObjectivesResponse")
    public java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective> getObjectives(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @RequestWrapper(localName = "deletePredictor", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeletePredictor")
    @WebMethod
    @ResponseWrapper(localName = "deletePredictorResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeletePredictorResponse")
    public void deletePredictor(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getActiveRisks", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetActiveRisks")
    @WebMethod
    @ResponseWrapper(localName = "getActiveRisksResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetActiveRisksResponse")
    public java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk> getActiveRisks(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getCommunityByUUID", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetCommunityByUUID")
    @WebMethod
    @ResponseWrapper(localName = "getCommunityByUUIDResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetCommunityByUUIDResponse")
    public uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community getCommunityByUUID(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @RequestWrapper(localName = "saveEvaluationResults", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.SaveEvaluationResults")
    @WebMethod
    @ResponseWrapper(localName = "saveEvaluationResultsResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.SaveEvaluationResultsResponse")
    public void saveEvaluationResults(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationResult arg1
    );

    @RequestWrapper(localName = "updateRisk", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.UpdateRisk")
    @WebMethod
    @ResponseWrapper(localName = "updateRiskResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.UpdateRiskResponse")
    public void updateRisk(
        @WebParam(name = "arg0", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk arg0
    ) throws Exception_Exception;

    @RequestWrapper(localName = "deleteObjective", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeleteObjective")
    @WebMethod
    @ResponseWrapper(localName = "deleteObjectiveResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.DeleteObjectiveResponse")
    public void deleteObjective(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective arg1
    );

    @RequestWrapper(localName = "runScript", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.RunScript")
    @WebMethod
    @ResponseWrapper(localName = "runScriptResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.RunScriptResponse")
    public void runScript(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getRiskByUUID", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetRiskByUUID")
    @WebMethod
    @ResponseWrapper(localName = "getRiskByUUIDResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.GetRiskByUUIDResponse")
    public uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk getRiskByUUID(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "addObjective", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.AddObjective")
    @WebMethod
    @ResponseWrapper(localName = "addObjectiveResponse", targetNamespace = "http://dataservice.ws.robust.swmind.pl/", className = "uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.AddObjectiveResponse")
    public uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective addObjective(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective arg1
    );
}
