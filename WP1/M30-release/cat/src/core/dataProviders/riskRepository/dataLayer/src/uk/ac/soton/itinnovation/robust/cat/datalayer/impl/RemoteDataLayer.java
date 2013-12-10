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
package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;

import client.RODataServiceWSImplService;
import client.TypeConverter;
import java.net.URL;
import java.util.*;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.RODataServiceWS;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

public class RemoteDataLayer {

    private RODataServiceWS port = null;
    static Logger logger = Logger.getLogger(RemoteDataLayer.class);

    public RemoteDataLayer() {
    }

    public RemoteDataLayer(URL url, QName service) {
        RODataServiceWSImplService svc = new RODataServiceWSImplService(url, service);
        port = svc.getRODataServiceWSImplPort();
    }

    //the path is really on the service side machine
    public void runScript(String path) {
        port.runScript(path);
    }

    public Set<Community> getCommunities() {
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community> listcomms;
        Set<Community> comms = null;
        logger.debug("Calling WS method getCommunities");
        listcomms = port.getCommunities();
        
        if (listcomms != null) {
            logger.debug("got non null response back");
            comms = new HashSet<Community>();
            for (int i = 0; i < listcomms.size(); i++) {
                logger.debug("adding to the list community "+listcomms.get(i).getName());
                comms.add(TypeConverter.getCommunity(listcomms.get(i)));
            }
        }
        return comms;
    }

    public Set<Risk> getRisks(UUID communityUUID) {
        logger.debug("Calling WS method getRisks");
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk> listRisk = port.getRisks(communityUUID.toString());

        Set<Risk> risks = null;

        if (listRisk != null) {
            risks = new HashSet<Risk>();
            for (int i = 0; i < listRisk.size(); i++) {
                risks.add(TypeConverter.getRisk(listRisk.get(i)));
            }
        }
        return risks;

    }

    public Community getCommunityByUUID(UUID comUUID) {
        logger.debug("Calling WS method getCommunityByUUID");
        return TypeConverter.getCommunity(port.getCommunityByUUID(comUUID.toString()));
    }

    public Risk getRiskByUUID(UUID riskUUID) {
        return TypeConverter.getRisk(port.getRiskByUUID(riskUUID.toString()));
    }

    public Set<Objective> getObjectives(UUID communityUUID) {
        Set<Objective> setObjs = null;
        logger.debug("Calling WS method getObjectives");
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective> listObjs = port.getObjectives(communityUUID.toString());
        if (listObjs != null) {
            setObjs = new HashSet<Objective>();
            for (int i = 0; i < listObjs.size(); i++) {
                setObjs.add(TypeConverter.getObjective(listObjs.get(i)));
            }
        }
        return setObjs;
    }

    public Risk saveRisk(Community community, String title, String owner, Boolean type, String group) {
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk rsk = port.saveRisk(TypeConverter.getCommunity(community), title, owner, type, group);
        return TypeConverter.getRisk(rsk);
    }

    public Set<PredictorServiceDescription> getPredictors() {
        Set<PredictorServiceDescription> setPreds = null;
        logger.debug("Calling WS method getPredictors");
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription> listPredic = port.getPredictors();
        if (listPredic != null) {
            setPreds = new HashSet<PredictorServiceDescription>();
            for (int i = 0; i < listPredic.size(); i++) {
                setPreds.add(TypeConverter.getPredictor(listPredic.get(i)));
            }
        }

        return setPreds;
    }

    public PredictorServiceDescription getPredictor(Event ev) {
        return TypeConverter.getPredictor(port.getPredictor(TypeConverter.getEvent(ev)));
    }

    public void updateRisk(Risk risk) throws Exception {
        port.updateRisk(TypeConverter.getRisk(risk));
    }

    public void addCommunity(Community comm) {
        port.addCommunity(TypeConverter.getCommunity(comm));
    }

    public Objective addObjective(UUID communityUuid, Objective obj) {
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective destobj = port.addObjective(communityUuid.toString(), TypeConverter.getObjective(obj));

        return TypeConverter.getObjective(destobj);
    }

    public void deleteObjective(UUID communityUuid, Objective obj) {
        port.deleteObjective(communityUuid.toString(), TypeConverter.getObjective(obj));
    }

    public void addPredictor(PredictorServiceDescription pred) {
        port.addPredictor(TypeConverter.getPredictor(pred));
    }

    public void deletePredictor(UUID uuid) {
        port.deletePredictor(uuid.toString());
    }

    public void deleteCommunityByUUID(UUID communityUUID) {
        port.deleteCommunityByUUID(communityUUID.toString());
    }

    public void deleteRisk(UUID riskuuid) {
        port.deleteRisk(riskuuid.toString());
    }

    public Set<Risk> getActiveRisks(UUID communityUUID) {
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk> risks = port.getActiveRisks(communityUUID.toString());
        Set<Risk> setRisks = null;
        if (risks != null) {
            setRisks = new HashSet<Risk>();
            for (int i = 0; i < risks.size(); i++) {
                setRisks.add(TypeConverter.getRisk(risks.get(i)));
            }

        }

        return setRisks;
    }

    public Map<Community, Set<Risk>> getAllActiveRisks() {
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.CommunityRisksElement> commrisks = port.getAllActiveRisks();
        Map<Community, Set<Risk>> commsetRisks = null;
        if (commrisks != null) {
            commsetRisks = new HashMap<Community, Set<Risk>>();
            for (int i = 0; i < commrisks.size(); i++) {
                Set<Risk> setRisks = new HashSet<Risk>(TypeConverter.getRiskList(commrisks.get(i).getRisks()));

                commsetRisks.put(TypeConverter.getCommunity(commrisks.get(i).getCommunity()), setRisks);
            }

        }

        return commsetRisks;
    }

    public Set<EvaluationResult> getRiskEvalResults(UUID riskuuid, Date startDate, Date endDate) {
        return TypeConverter.getEvalResults(port.getRiskEvalResults(riskuuid.toString(), TypeConverter.dateToGregorian(startDate), TypeConverter.dateToGregorian(endDate)));
    }

    public void saveEvaluationResults(UUID riskid, EvaluationResult evalResult) {
        port.saveEvaluationResults(riskid.toString(), TypeConverter.getEvaluationResult(evalResult));
    }
    
    public void deleteEvaluationResults(UUID riskuuid){
        port.deleteEvaluationResults(riskuuid.toString());
    }
}
