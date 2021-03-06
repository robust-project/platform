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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.6.1
 * 2013-03-28T15:22:08.522Z
 * Generated source version: 2.6.1
 * 
 */
@WebService(targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", name = "IEvaluationEngineService")
@XmlSeeAlso({ObjectFactory.class})
public interface IEvaluationEngineServicePort {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getCommunityResultsStreamDetails", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.GetCommunityResultsStreamDetails")
    @WebMethod
    @ResponseWrapper(localName = "getCommunityResultsStreamDetailsResponse", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.GetCommunityResultsStreamDetailsResponse")
    public uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StreamDetails getCommunityResultsStreamDetails(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "isEngineRunning", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.IsEngineRunning")
    @WebMethod
    @ResponseWrapper(localName = "isEngineRunningResponse", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.IsEngineRunningResponse")
    public boolean isEngineRunning();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getGeneralStreamDetails", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.GetGeneralStreamDetails")
    @WebMethod
    @ResponseWrapper(localName = "getGeneralStreamDetailsResponse", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.GetGeneralStreamDetailsResponse")
    public uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StreamDetails getGeneralStreamDetails();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "startEngineForTimePeriod", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StartEngineForTimePeriod")
    @WebMethod
    @ResponseWrapper(localName = "startEngineForTimePeriodResponse", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StartEngineForTimePeriodResponse")
    public uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.Status startEngineForTimePeriod(
        @WebParam(name = "arg0", targetNamespace = "")
        javax.xml.datatype.XMLGregorianCalendar arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        javax.xml.datatype.XMLGregorianCalendar arg1
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "stopEngine", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StopEngine")
    @WebMethod
    @ResponseWrapper(localName = "stopEngineResponse", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StopEngineResponse")
    public uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.Status stopEngine();

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "startEngine", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StartEngine")
    @WebMethod
    @ResponseWrapper(localName = "startEngineResponse", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StartEngineResponse")
    public uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.Status startEngine();

    @RequestWrapper(localName = "newEvaluationResult", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.NewEvaluationResult")
    @WebMethod
    @ResponseWrapper(localName = "newEvaluationResultResponse", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.NewEvaluationResultResponse")
    public void newEvaluationResult(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.EvaluationResult arg1
    );

    @RequestWrapper(localName = "updateEvaluationJobStatus", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.UpdateEvaluationJobStatus")
    @WebMethod
    @ResponseWrapper(localName = "updateEvaluationJobStatusResponse", targetNamespace = "http://spec.ws.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", className = "uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.UpdateEvaluationJobStatusResponse")
    public void updateEvaluationJobStatus(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobStatus arg1
    );
}
