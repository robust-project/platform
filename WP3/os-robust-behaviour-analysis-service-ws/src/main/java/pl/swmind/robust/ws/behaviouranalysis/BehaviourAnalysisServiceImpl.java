/**
 *Copyright 2013 Knowledge Media Institute
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
package pl.swmind.robust.ws.behaviouranalysis;

import eu.project.robust.behaviour.analysis.Composition;
import eu.project.robust.behaviour.analysis.UserRoles;
import eu.project.robust.behaviour.corpora.Boardsie.BoardsieDBConnection;
import eu.project.robust.behaviour.corpora.Boardsie.analysis.BoardsieMacroCommunityRuleRunner;
import eu.project.robust.behaviour.corpora.Boardsie.analysis.BoardsieMicroCommunityRuleRunner;
import eu.project.robust.behaviour.exception.RobustBehaviourServiceException;
import eu.project.robust.behaviour.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.jws.WebService;
import java.sql.Timestamp;
import java.util.*;

@WebService(endpointInterface = "pl.swmind.robust.ws.behaviouranalysis.BehaviourAnalysisService")
public class BehaviourAnalysisServiceImpl implements BehaviourAnalysisService{

    private static final Logger log = LoggerFactory.getLogger(BehaviourAnalysisServiceImpl.class);

    private static final String BOARDSIE_PLATFORM ="BOARDSIE";


    private BoardsieMacroCommunityRuleRunner boardsieMacroCommunityRuleRunner;
    private BoardsieMicroCommunityRuleRunner boardsieMicroCommunityRuleRunner;





    //======================
    //   macro assessments
    //======================

    public Composition deriveMacroComposition(String platformID, String communityID, Date date) throws RobustBehaviourServiceException {
        try {
            log.info("deriveMacroComposition params:  communityId='{}', date='{}'", communityID, date);
            log.info("deriveMacroComposition params:  platformId='{}'", platformID);

            // to normalise the hour of the day due to time zoning
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY,0);
            date = c.getTime();

            Composition composition =null;
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                composition = boardsieMacroCommunityRuleRunner.deriveComposition(communityID,date);
            }
            else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM);
            }
            return composition;
        } catch(Exception e) {
            log.error("Can't derive macro composition, {}", e.getMessage());
            throw new RobustBehaviourServiceException(e);
        }
    }

    @Override
    public Set<DeriveTSMacroCompositionsResultEntry> deriveTSMacroCompositions(String platformID, String communityID, Date startDate, Date endDate) throws RobustBehaviourServiceException {
        try {
            Set<DeriveTSMacroCompositionsResultEntry> resultSet = new HashSet<DeriveTSMacroCompositionsResultEntry>();
            log.info("deriveTSMacroComposition params:  communityId='{}', startDate='{}'", communityID, startDate);
            log.info("deriveTSMacroComposition params:  endDate='{}', platformID='{}'", endDate, platformID);

            // to normalise the hour of the day due to time zoning
            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.set(Calendar.HOUR_OF_DAY,0);
            startDate = c.getTime();
            c = Calendar.getInstance();
            c.setTime(endDate);
            c.set(Calendar.HOUR_OF_DAY,0);
            endDate = c.getTime();


            TreeMap<Date,Composition> tsCompositions = null;
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                tsCompositions = boardsieMacroCommunityRuleRunner.deriveTimeSeriesComposition(communityID, startDate, endDate);
            }else{
                throw new Exception("The community platform has not been initialized. Options " +
                         BOARDSIE_PLATFORM);
            }


            for (Map.Entry<Date,Composition> entry : tsCompositions.entrySet() ){
                DeriveTSMacroCompositionsResultEntry element = new DeriveTSMacroCompositionsResultEntry();
                element.setDate(entry.getKey());
                element.setComposition(entry.getValue());
                resultSet.add(element);
            }
            return resultSet;
        } catch(Exception e) {
            log.error("Can't derive time series macro composition, {}", e.getMessage());
            throw new RobustBehaviourServiceException(e);
        }
    }

    @Override
    public int getRoleCount(String platformID, String communityID, int roleID, Date date) throws RobustBehaviourServiceException {
        try {
            log.info("getRoleCount params:  platformId='{}'", platformID);
            log.info("getRoleCount params:  communityId='{}', roleID='{}'", communityID, roleID);
            log.info("getRoleCount params:  date='{}'", date);

            // to normalise the hour of the day due to time zoning
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY,0);
            date = c.getTime();

            int count = 0;
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                count = boardsieMacroCommunityRuleRunner.getRoleCount(communityID,roleID,date);
            }else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM);
            }


            return count;
        } catch (Exception e){
            log.error("Can't get role count, {}", e.getMessage());
            e.printStackTrace();
            throw new RobustBehaviourServiceException(e);
        }
    }

    @Override
    public double getRoleProportion(String platformID, String communityID, int roleID, Date date) throws RobustBehaviourServiceException {
        try {
            log.info("getRoleProportion params:  communityId='{}', roleID='{}'", communityID, roleID);
            log.info("getRoleProportion params:  date='{}'", communityID, roleID);
            // to normalise the hour of the day due to time zoning
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY,0);
            date = c.getTime();

            double prop = 0.0;
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                prop = boardsieMacroCommunityRuleRunner.getRoleProportion(communityID,roleID,date);
            }else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM);
            }
            return prop;
        } catch(Exception e) {
            log.error("Can't derive time series macro composition, {}", e.getMessage());
            e.printStackTrace();
            throw new RobustBehaviourServiceException(e);
        }
    }

    //======================
    //   micro assessments
    //======================


    @Override
    public Set<GetRolePathResultEntry> getRolePath(String platformID, String communityID, String userid, Date endDate) throws RobustBehaviourServiceException {
        try {
            Set<GetRolePathResultEntry> resultSet = new HashSet<GetRolePathResultEntry>();
            log.info("getRolePath params:  platformId='{}'", platformID);
            log.info("getRolePath params:  communityId='{}', userID='{}'", communityID, userid);
            log.info("getRolePath params:  endDate='{}'", endDate);

            // to normalise the hour of the day due to time zoning
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.set(Calendar.HOUR_OF_DAY,0);
            endDate = c.getTime();

            TreeMap<Date, Integer> rolePath = null;
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                rolePath = boardsieMacroCommunityRuleRunner.getRolePathOfUser(communityID,userid,endDate);
            }else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM);
            }


            for (Map.Entry<Date, Integer> entry : rolePath.entrySet()) {
                GetRolePathResultEntry element = new GetRolePathResultEntry();
                element.setDate(entry.getKey());
                element.setRole(entry.getValue());
                resultSet.add(element);
            }

            return resultSet;
        } catch (Exception e) {
            log.error("Can't derive get role path for user, {}", e.getMessage());
            e.printStackTrace();
            throw new RobustBehaviourServiceException(e);
        }
    }

    @Override
    public Set<GetRolePathResultEntry> getTSRolePath(String platformID, String communityID, String userid, Date startDate, Date endDate) throws RobustBehaviourServiceException{
        try {
            Set<GetRolePathResultEntry> resultSet = new HashSet<GetRolePathResultEntry>();
            log.info("getTSRolePath params:  platformId='{}'", platformID);
            log.info("getTSRolePath params:  communityId='{}', userID='{}'", communityID, userid);
            log.info("getTSRolePath params:  startDate='{}', endDate={}", startDate, endDate);

            // to normalise the hour of the day due to time zoning
            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.set(Calendar.HOUR_OF_DAY,0);
            startDate = c.getTime();
            c = Calendar.getInstance();
            c.setTime(endDate);
            c.set(Calendar.HOUR_OF_DAY,0);
            endDate = c.getTime();

            TreeMap<Date, Integer> rolePath = null;
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                rolePath = boardsieMacroCommunityRuleRunner.getTSRolePathOfUser(communityID,userid,startDate, endDate);
            }else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM);
            }


            for (Map.Entry<Date, Integer> entry : rolePath.entrySet()) {
                GetRolePathResultEntry element = new GetRolePathResultEntry();
                element.setDate(entry.getKey());
                element.setRole(entry.getValue());
                resultSet.add(element);
            }

            return resultSet;
        } catch (Exception e) {
            log.error("Can't derive get TS role path for user, {}", e.getMessage());
            e.printStackTrace();
            throw new RobustBehaviourServiceException(e);
        }
    }


    @Override
    public UserRoles deriveMicroComposition(String platformID, String communityID, Date date) throws RobustBehaviourServiceException {
        try {
            log.info("deriveMicroCompositions params:  platformId='{}'", platformID);
            log.info("deriveMicroComposition params:  communityId='{}', date='{}'", communityID, date);
            // to normalise the hour of the day due to time zoning
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY,0);
            date = c.getTime();

            UserRoles userRoles = null;
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                userRoles = boardsieMicroCommunityRuleRunner.deriveComposition(communityID, date);
            }
            else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM);
            }


            return userRoles;
        } catch(Exception e) {
            log.error("Can't derive micro composition, {}", e.getMessage());
            e.printStackTrace();
            throw new RobustBehaviourServiceException(e);
        }
    }

    @Override
    public Set<DeriveTSMicroCompositionsResultEntry> deriveTSMicroCompositions(String platformID, String communityID, Date startDate, Date endDate) throws RobustBehaviourServiceException {
        try {
            Set<DeriveTSMicroCompositionsResultEntry> resultSet = new HashSet<DeriveTSMicroCompositionsResultEntry>();
            log.info("deriveTSMicroCompositions params:  platformId='{}'", platformID);
            log.info("deriveTSMicroCompositions params:  communityId='{}', startDate='{}'", communityID, startDate);
            log.info("deriveTSMicroCompositions params:  endDate='{}'", endDate);

            // to normalise the hour of the day due to time zoning
            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.set(Calendar.HOUR_OF_DAY,0);
            startDate = c.getTime();
            c = Calendar.getInstance();
            c.setTime(endDate);
            c.set(Calendar.HOUR_OF_DAY,0);
            endDate = c.getTime();

            TreeMap<Date, UserRoles> compositions = null;
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                compositions = boardsieMicroCommunityRuleRunner.deriveTimeSeriesComposition(communityID,startDate,endDate);
            }
            else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM);
            }

            for (Map.Entry<Date, UserRoles> entry : compositions.entrySet()) {
                DeriveTSMicroCompositionsResultEntry element = new DeriveTSMicroCompositionsResultEntry();
                element.setDate(entry.getKey());
                element.setUserRoles(entry.getValue());
                resultSet.add(element);
            }

            return resultSet;
        } catch (Exception e) {
            log.error("Can't derive time series micro compositions, {}", e.getMessage());
            e.printStackTrace();
            throw new RobustBehaviourServiceException(e);
        }
    }




    //========================
    // UI FUNCIONS
    //========================

    @Override
    public Set<String> getPlatformIDs() {
        log.info("getPlatformIDs");
        Set<String> platformIDs = new HashSet<String>();
        platformIDs.add(BOARDSIE_PLATFORM);
        return platformIDs;
    }

    @Override
    public Set<DeriveCommunityNameEntry> getCommunityIDs(String platformID) throws RobustBehaviourServiceException {
        try {
            log.info("getCommunityIDs for platformID={}", platformID);
            Set<DeriveCommunityNameEntry> communityIDs = new HashSet<DeriveCommunityNameEntry>();
            HashMap<String, String> communityIDsResult;

            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                communityIDsResult = BoardsieDBConnection.getCommunityIDs();
            }else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM);
            }
            for (Map.Entry<String, String> entry : communityIDsResult.entrySet()) {
                DeriveCommunityNameEntry element = new DeriveCommunityNameEntry();
                element.setCommunityID(entry.getKey());
                element.setCommunityName(entry.getValue());
                communityIDs.add(element);
            }
            return communityIDs;
        } catch (Exception e) {
            log.error("Can't obtain community IDs, {}", e.getMessage());
            e.printStackTrace();
            throw new RobustBehaviourServiceException(e);
        }
    }

    @Override
    public Set<String> getUserIDs(String platformID, String communityID, int numUsers) throws RobustBehaviourServiceException {
        log.info("getUsersForCommunity params:  communityID='{}', platformID={}", communityID, platformID);
        Set<String> communityIDs = null;
        try {
            if(platformID.equalsIgnoreCase(BOARDSIE_PLATFORM)){
                communityIDs = BoardsieDBConnection.getUserIDs(communityID, numUsers);
            }else{
                throw new Exception("The community platform has not been initialized. Options " +
                        BOARDSIE_PLATFORM + " - platformID=" + platformID);
            }
            return communityIDs;
        } catch (Exception e) {
            log.error("Can't obtain users IDs, {}", e.getMessage());
            e.printStackTrace();
            throw new RobustBehaviourServiceException(e);
        }
    }





    //========================
    // GETTERS AND SETTERS
    //========================


    public void setBoardsieMacroCommunityRuleRunner(BoardsieMacroCommunityRuleRunner boardsieMacroCommunityRuleRunner) {
        this.boardsieMacroCommunityRuleRunner = boardsieMacroCommunityRuleRunner;
    }

    public void setBoardsieMicroCommunityRuleRunner(BoardsieMicroCommunityRuleRunner boardsieMicroCommunityRuleRunner) {
        this.boardsieMicroCommunityRuleRunner = boardsieMicroCommunityRuleRunner;
    }


}
