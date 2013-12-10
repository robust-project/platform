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


import java.util.*;

import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.CommunityRisksElement;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

import javax.jws.WebService;

/**
 * This is the interface of the risk repository. it stores the risks and opportunities for a community as specified by the risk manager.
 * it also stores the risk evaluation results as calculated by the evaluation engine (via predictor services).
 */

@WebService
public interface RODataServiceWS {
    /**
     * this runs a script against the database. it is being used in order to drop and recreate the DB schema.
     * this method should be removed at later stages.
     */
    public void runScript(String path);

    /**
     * this methods returns the set of communities in the DB
     */
    public Set<Community> getCommunities();

    /**
     * this method returns the set of risks and opportunities in the DB for a specific community with the provided uuid
     */
    public Set<Risk> getRisks(UUID communityUUID);

    /**
     * returns the community with the provided uuid
     */
    public Community getCommunityByUUID(UUID comUUID);

    /**
     * returns the risk with the provided uuid
     */
    public Risk getRiskByUUID(UUID riskUUID);

    /**
     * returns the objectives of the community with the provided uuid
     */
    public Set<Objective> getObjectives(UUID communityUUID);

    /**
     * saves risk to the DB.
     * @param Community
     * @param title of the risk
     * @param  owner of the risk
     * @param type whether risk or opportunity (TODO: remove this since it can be risk and opp at the same time)
     * @param group (this parameter is not being used at the moment)
     * @return Risk object with the provided attributes
     */
    public Risk saveRisk(Community community, String title, String owner, Boolean type, String group);

    /**
     * returns the predictors stored in the DB
     */
    public Set<PredictorServiceDescription> getPredictors();

    /**
     * returns the predictor for the provided event. The method counts on the event uuid to identify the associated predictor.
     */
    public PredictorServiceDescription getPredictor(Event ev);

    /**
     * updates ALL the risk components including the event, impact and treatments.
     */
    public void updateRisk(Risk risk) throws Exception;

    /**
     * adds community to the DB
     */
    public void addCommunity(Community comm);

    /**
     * adds objective to the community with the provided uuid
     */
    public Objective addObjective(UUID communityUuid, Objective obj);

    /**
     * deletes objective from the community with the provided uuid
     */
    public void deleteObjective(UUID communityUuid, Objective obj);

    /**
     * add predictor to the database
     */
    public void addPredictor(PredictorServiceDescription pred);

    /**
     * deletes the predictor with the provided uuid from the DB along with its associated events
     * TODO: check behavior when a risk is already linked to the predictor
     */
    public void deletePredictor(UUID uuid);

    /**
     * delete community with uuid. this includes the objectives, risks and opportunities
     */
    public void deleteCommunityByUUID(UUID communityUUID);

    /**
     * delete risk with the uuid
     */
    public void deleteRisk(UUID riskuuid);

    /**
     * get the risk /opp evaluation results stored in the DB
     */
    public Set<EvaluationResult> getRiskEvalResults(UUID riskuuid, Date startDate, Date endDate);

    /**
     * get the active risks for the community.
     * NOT FULLY IMPLEMENTED YET
     */
    public Set<Risk> getActiveRisks(UUID communityUUID);

    /**
     * returns all risks for all communities
     * NOT FULLY IMPLEMENTED YET
     */
    //public Map<Community,Set<Risk>> getAllActiveRisks();
    public List<CommunityRisksElement> getAllActiveRisks();

    /**
     * saves evaluation results of risk or opportunities given the uuid
     */
    public void saveEvaluationResults(UUID riskid, EvaluationResult evalResult);


     /**
     * deletes ALL evaluation results of risk or opportunities given the uuid
     */
    public void deleteEvaluationResults(UUID riskid);
    
}