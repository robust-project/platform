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
//      Created By :            Bassem Nasser
//      Created Date :          28-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package client;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
//import org.dozer.DozerBeanMapper;
//import org.dozer.Mapper;
//import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Exception_Exception;
//import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ImpactLevel;
//import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.RODataServiceWS;

import uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.*;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;

public class SampleClient {

    private static final QName SERVICE_NAME = new QName("http://ws.robust.swmind.pl/", "RobustDataServiceBoardsIEWSImplService");

    private SampleClient() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = RobustDataServiceBoardsIEWSImplService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        RobustDataServiceBoardsIEWSImplService ss = new RobustDataServiceBoardsIEWSImplService(wsdlURL, SERVICE_NAME);
        RobustDataServiceBoardsIEWS port = ss.getRobustDataServiceBoardsIEWSImplPort();
        
        System.out.println("Invoking getAllForums...");
        List<ForumDto> forums = port.getAllForums();
        
        List<Community> communities = new ArrayList<Community>();
        
        for (ForumDto forum : forums) {
            String name = forum.getForumTitle();
            URI uri = null;
            boolean isStreaming = false;
            String communityID = Long.toString(forum.getForumId());
            String streamName = null;
            Community community = new Community(name, uri, /*isStreaming,*/ communityID, streamName); //bmn constructor doesnt need this anymore
            communities.add(community);
        }

        
        System.out.println("Exiting");
        /*
        {
        System.out.println("Invoking getCommunities...");
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community> _getCommunities__return = port.getCommunities();
        System.out.println("getCommunities.result=" + _getCommunities__return);
        uk.ac.soton.itinnovation.robust.riskmodel.Community destObject = TypeConverter.getCommunity(_getCommunities__return.get(0));
        System.out.println("new object type is " + destObject.getClass().toString());
        System.out.println("Community " + destObject.getName());
        System.out.println("Community UUID " + destObject.getUuid().toString());
        System.out.println("Community uri " + destObject.getUri());
        System.out.println("Community stream " + destObject.getIsStream());
        System.out.println("Invoking getRisks...");
        java.lang.String _getRisks_arg0 = destObject.getUuid().toString();
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk> _getRisks__return = port.getRisks(_getRisks_arg0);
        System.out.println("getRisks.result=" + _getRisks__return);
        //TODO convert risk
        for(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk risk:_getRisks__return ){
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event> events=risk.getSetEvent();
        System.out.println("test converting eventConditions");
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition newEventCond=TypeConverter.getEventCondition(events.get(0).getEventConditions().get(0));
        System.out.println("converting events");
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event newEvent=TypeConverter.getEvent(events.get(0));
        System.out.println("converting risks");
        uk.ac.soton.itinnovation.robust.riskmodel.Risk newRisk=TypeConverter.getRisk(risk);
        }
        System.out.println("Invoking getObjectives...");
        java.lang.String _getObjectives_arg0 = destObject.getUuid().toString();
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective> _getObjectives__return = port.getObjectives(_getObjectives_arg0);
        System.out.println("getObjectives.result=" + _getObjectives__return);
        for (uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective obj : _getObjectives__return) {
        uk.ac.soton.itinnovation.robust.riskmodel.Objective newobj = TypeConverter.getObjective(obj);
        System.out.println("objective title " + newobj.getTitle());
        System.out.println("objective description " + newobj.getDescription());
        System.out.println("objective id " + newobj.getId());
        }
        }
        {
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ImpactLevel impLvl=uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ImpactLevel.NEG_MEDIUM;
        uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel newImpLvl=TypeConverter.getImpactLevel(impLvl);
        System.out.println(newImpLvl.getName());
            
        }
         */


         
                 
        
        

//        {
//        System.out.println("Invoking deleteRisk...");
//        java.lang.String _deleteRisk_arg0 = "";
//        port.deleteRisk(_deleteRisk_arg0);
//
//
//        }
//        {
//        System.out.println("Invoking addCommunity...");
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community _addCommunity_arg0 = null;
//        port.addCommunity(_addCommunity_arg0);
//
//
//        }
//        {
//        System.out.println("Invoking deleteCommunityByUUID...");
//        java.lang.String _deleteCommunityByUUID_arg0 = "";
//        port.deleteCommunityByUUID(_deleteCommunityByUUID_arg0);
//
//
//        }
//        {
//        System.out.println("Invoking getPredictor...");
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event _getPredictor_arg0 = null;
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription _getPredictor__return = port.getPredictor(_getPredictor_arg0);
//        System.out.println("getPredictor.result=" + _getPredictor__return);
//
//
//        }
//        {
//        System.out.println("Invoking getPredictors...");
//        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription> _getPredictors__return = port.getPredictors();
//        System.out.println("getPredictors.result=" + _getPredictors__return);
//
//
//        }
//        {
//        System.out.println("Invoking saveRisk...");
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community _saveRisk_arg0 = null;
//        java.lang.String _saveRisk_arg1 = "";
//        java.lang.String _saveRisk_arg2 = "";
//        java.lang.Boolean _saveRisk_arg3 = null;
//        java.lang.String _saveRisk_arg4 = "";
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk _saveRisk__return = port.saveRisk(_saveRisk_arg0, _saveRisk_arg1, _saveRisk_arg2, _saveRisk_arg3, _saveRisk_arg4);
//        System.out.println("saveRisk.result=" + _saveRisk__return);
//
//
//        }


//        {
//        System.out.println("Invoking getAllActiveRisks...");
//        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.CommunityRisksElement> _getAllActiveRisks__return = port.getAllActiveRisks();
//        System.out.println("getAllActiveRisks.result=" + _getAllActiveRisks__return);
//
//
//        }
//        {
//        System.out.println("Invoking getActiveRisks...");
//        java.lang.String _getActiveRisks_arg0 = "";
//        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk> _getActiveRisks__return = port.getActiveRisks(_getActiveRisks_arg0);
//        System.out.println("getActiveRisks.result=" + _getActiveRisks__return);
//
//
//        }
//        {
//        System.out.println("Invoking getRiskEvalResults...");
//        java.lang.String _getRiskEvalResults_arg0 = "";
//        javax.xml.datatype.XMLGregorianCalendar _getRiskEvalResults_arg1 = null;
//        javax.xml.datatype.XMLGregorianCalendar _getRiskEvalResults_arg2 = null;
//        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationResult> _getRiskEvalResults__return = port.getRiskEvalResults(_getRiskEvalResults_arg0, _getRiskEvalResults_arg1, _getRiskEvalResults_arg2);
//        System.out.println("getRiskEvalResults.result=" + _getRiskEvalResults__return);
//
//
//        }
//        {
//        System.out.println("Invoking addObjective...");
//        java.lang.String _addObjective_arg0 = "";
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective _addObjective_arg1 = null;
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective _addObjective__return = port.addObjective(_addObjective_arg0, _addObjective_arg1);
//        System.out.println("addObjective.result=" + _addObjective__return);
//
//
//        }

//        {
//        System.out.println("Invoking getRiskByUUID...");
//        java.lang.String _getRiskByUUID_arg0 = "";
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk _getRiskByUUID__return = port.getRiskByUUID(_getRiskByUUID_arg0);
//        System.out.println("getRiskByUUID.result=" + _getRiskByUUID__return);
//
//
//        }
//        {
//        System.out.println("Invoking updateRisk...");
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk _updateRisk_arg0 = null;
//        try {
//            port.updateRisk(_updateRisk_arg0);
//
//        } catch (Exception_Exception e) { 
//            System.out.println("Expected exception: Exception has occurred.");
//            System.out.println(e.toString());
//        }
//            }
//        {
//        System.out.println("Invoking deleteObjective...");
//        java.lang.String _deleteObjective_arg0 = "";
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective _deleteObjective_arg1 = null;
//        port.deleteObjective(_deleteObjective_arg0, _deleteObjective_arg1);
//
//
//        }
//        {
//        System.out.println("Invoking runScript...");
//        java.lang.String _runScript_arg0 = "";
//        port.runScript(_runScript_arg0);
//
//
//        }
//        {
//        System.out.println("Invoking saveEvaluationResults...");
//        java.lang.String _saveEvaluationResults_arg0 = "";
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationResult _saveEvaluationResults_arg1 = null;
//        port.saveEvaluationResults(_saveEvaluationResults_arg0, _saveEvaluationResults_arg1);
//
//
//        }
//        {
//        System.out.println("Invoking addPredictor...");
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription _addPredictor_arg0 = null;
//        port.addPredictor(_addPredictor_arg0);
//
//
//        }
//        {
//        System.out.println("Invoking deletePredictor...");
//        java.lang.String _deletePredictor_arg0 = "";
//        port.deletePredictor(_deletePredictor_arg0);
//
//
//        }
//        {
//        System.out.println("Invoking getCommunityByUUID...");
//        java.lang.String _getCommunityByUUID_arg0 = "";
//        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community _getCommunityByUUID__return = port.getCommunityByUUID(_getCommunityByUUID_arg0);
//        System.out.println("getCommunityByUUID.result=" + _getCommunityByUUID__return);
//
//
//        }

        System.exit(0);
        }
}
