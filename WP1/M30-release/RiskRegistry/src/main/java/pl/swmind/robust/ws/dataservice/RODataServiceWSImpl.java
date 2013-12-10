/** 
*Copyright 2013 Software Mind SA
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
package pl.swmind.robust.ws.dataservice;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.riskmodel.CommunityRisksElement;
import pl.swmind.robust.ws.dataservice.utils.CommunitiesRisksMapper;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import java.util.*;


/**
 * User: rafal
 * Date: 23.07.12
 */

@WebService(endpointInterface = "pl.swmind.robust.ws.dataservice.RODataServiceWS")
public class RODataServiceWSImpl implements RODataServiceWS {

    private static final Logger log = LoggerFactory.getLogger(RODataServiceWSImpl.class);

    @Autowired
    private IDataLayer dataLayer;

    public void setDataLayer(IDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public void runScript(String path) {
        log.debug("Executing runScript with params:  path='{}'", path);
        dataLayer.runScript(path);
    }

    @Override
    public Set<Community> getCommunities() {
        log.debug("Executing getCommunities");
        return dataLayer.getCommunities();
    }

    @Override
    public Set<Risk> getRisks(UUID communityUUID) {
        log.debug("Executing getRisks with params: communityUUID='{}'", communityUUID);
        return dataLayer.getRisks(communityUUID);
    }

    @Override
    public Community getCommunityByUUID(UUID comUUID) {
        log.debug("Executing getCommunityByUUID with params: comUUID='{}'", comUUID);
        return dataLayer.getCommunityByUUID(comUUID);
    }

    @Override
    public Risk getRiskByUUID(UUID riskUUID) {
        log.debug("Executing getRiskByUUID with params: riskUUID='{}'", riskUUID);
        return dataLayer.getRiskByUUID(riskUUID);
    }

    @Override
    public Set<Objective> getObjectives(UUID communityUUID) {
        log.debug("Executing getObjectives with params: communityUUID='{}'", communityUUID);
        return dataLayer.getObjectives(communityUUID);
    }

    @Override
    public Risk saveRisk(Community community, String title, String owner, Boolean type, String group) {
        log.debug("Executing saveRisk with params: title='{}', owner ='{}'", title, owner);
        log.debug("type='{}', group='{}'", type, group);

        return dataLayer.saveRisk(community, title, owner, type, group);
    }

    @Override
    public Set<PredictorServiceDescription> getPredictors() {
        log.debug("Executing getPredictors");
        return dataLayer.getPredictors();
    }

    @Override
    public PredictorServiceDescription getPredictor(Event ev) {
        log.debug("Executing getPredictor with params: ev='{}'", ev.getTitle());
        return dataLayer.getPredictor(ev);
    }

    @Override
    public void updateRisk(Risk risk) throws Exception {
        log.debug("Executing updateRisk with params: risk='{}'", risk.getId());
        dataLayer.updateRisk(risk);
    }

    @Override
    public void addCommunity(Community comm) {
        log.debug("Executing addCommunity with params: comm='{}'", comm.getName());
        dataLayer.addCommunity(comm);
    }

    @Override
    public Objective addObjective(UUID communityUuid, Objective obj) {
        log.debug("Executing addObjective with params: communityUuid='{}' ,obj='{}'", communityUuid, obj.getId());
        return dataLayer.addObjective(communityUuid, obj);
    }

    @Override
    public void deleteObjective(UUID communityUuid, Objective obj) {
        log.debug("Executing deleteObjective with params: communityUuid='{}' ,obj='{}'", communityUuid, obj.getId());
        dataLayer.deleteObjective(communityUuid, obj);
    }

    @Override
    public void addPredictor(PredictorServiceDescription pred) {
        log.debug("Executing addPredictor with params: pred='{}'", pred.getName());
        dataLayer.addPredictor(pred);
    }

    @Override
    public void deletePredictor(UUID uuid) {
        log.debug("Executing deletePredictor with params: uuid='{}'", uuid);
        dataLayer.deletePredictor(uuid);
    }

    @Override
    public void deleteCommunityByUUID(UUID communityUUID) {
        log.debug("Executing deleteCommunityByUUID with params: communityUUID='{}'", communityUUID);
        dataLayer.deleteCommunityByUUID(communityUUID);
    }

    @Override
    public void deleteRisk(UUID riskuuid) {
        log.debug("Executing deleteRisk with params: riskuuid='{}'", riskuuid);
        dataLayer.deleteRisk(riskuuid);
    }

    @Override
    public Set<EvaluationResult> getRiskEvalResults(UUID riskuuid, Date startDate, Date endDate) {
        log.debug("Executing getRiskEvalResults with params: riskuuid='{}' ,startDate='{}'", riskuuid, startDate);
        log.debug("endDate='{}'", endDate);
        return dataLayer.getRiskEvalResults(riskuuid, startDate, endDate);
    }

    @Override
    public Set<Risk> getActiveRisks(UUID communityUUID) {
        log.debug("Executing getActiveRisks with params: communityUUID='{}'", communityUUID);
        return dataLayer.getActiveRisks(communityUUID);
    }

    @Override
    public List<CommunityRisksElement> getAllActiveRisks() {
        log.debug("Executing getAllActiveRisks.");
        Map<Community, Set<Risk>> map = dataLayer.getAllActiveRisks();
        List<CommunityRisksElement> list = CommunitiesRisksMapper.map(map);
        log.debug("Executing getAllActiveRisks done, found " + list.size() + "elements");
        return list;
    }

    @Override
    public void saveEvaluationResults(UUID riskid, EvaluationResult evalResult) {
        log.debug("saveEvaluationResults with params: riskid='{}' ,evalResult='{}'", riskid, evalResult.getResultUUID());
        dataLayer.saveEvaluationResults(riskid, evalResult);
    }
    
    @Override
    public void deleteEvaluationResults(UUID riskid){
        log.debug("deleteEvaluationResults with params: riskid='{}'", riskid);
        dataLayer.deleteEvaluationResults(riskid);
    }
}
