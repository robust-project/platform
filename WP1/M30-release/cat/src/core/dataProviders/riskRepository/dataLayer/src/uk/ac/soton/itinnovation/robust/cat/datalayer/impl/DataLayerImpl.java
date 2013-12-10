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
//      Created Date :          03-Nov-2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;

import client.RODataServiceWSImplService;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import com.mysql.jdbc.Connection;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import javax.xml.namespace.QName;
import org.apache.commons.io.IOUtils;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.riskmodel.*;

public class DataLayerImpl implements IDataLayer {

    static Logger logger = Logger.getLogger(DataLayerImpl.class);
    //private String dbName;
    private DatabaseConnector dbConnector;
    RemoteDataLayer svc = null;
    private boolean ws = false;

//    private Connection con;
    public DataLayerImpl() throws Exception {
        Map<String, String> configs = DataLayerImplHelper.getConfigs();
        String webservice = null;

        try {

            try {
                webservice = configs.get("webservice");
            } catch (Exception ex) {
                logger.info("couldnt find webservice parameter set to true in data.properties. using the local database info", ex);
            }

            if ((webservice != null) && webservice.equals("true")) {
                //get the service details url, namespace, localpart

                String strUrl = configs.get("url");
                URL url = null;
                try {
                    url = new URL(strUrl);
                } catch (MalformedURLException e) {
                    throw new RuntimeException("couldnt obtain a valid url from the provided url: " + strUrl, e);
                }

                String namespace = configs.get("namespace");
                String localpart = configs.get("localpart");

                QName service = new QName(namespace, localpart);
                logger.info("********************************");
                logger.info("initializing Datalayer client");
                logger.info("Remote DataLayer service at "+url);
                logger.info("********************************");
                
                //create client
                svc = new RemoteDataLayer(url, service);
                ws = true;
            } else {
                logger.info("********************************");
                logger.info("initializing Local Datalayer");
                logger.info("Local DataBase username: "+configs.get("username") +", password:"+configs.get("password"));
                logger.info("********************************");
                dbConnector = new DatabaseConnector(configs.get("dburl"), configs.get("dbname"), configs.get("username"), configs.get("password"));
            }
        } catch (Exception ex) {
            throw new RuntimeException("DATALAYER: error while initializing datalayer implementation. " + ex.getLocalizedMessage(), ex);
        }


    }

    @Override
    public Set<Risk> getRisks(UUID communityUUID) {
        if (communityUUID == null) {
            throw new RuntimeException("input community uuid is null");
        }

        //if webservice call the service
        if (ws) {
            return svc.getRisks(communityUUID);
        }

        Set<Risk> risks = new HashSet<Risk>();
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            
            dbCon.connect();

            String query = "SELECT * from riskop, community Where community.com_uuid='" + communityUUID.toString() + "' "
                    + "and "
                    + "community.idcommunity=riskop.community_idcommunity";


            ResultSet rs = dbCon.executeQuery(query);


            while (rs.next()) {
                //get risk scope, state, notification, scheduling params
                String scope = rs.getString("scope");
                String state = rs.getString("state");
                String id = rs.getString("uuid");
                String title = rs.getString("title");
                String owner = rs.getString("owner");
                String group = rs.getString("group");
                Boolean type = rs.getBoolean("type");

                String cat_period = rs.getString("cat_review_period");
                int cat_freq = rs.getInt("cat_review_freq");

                String admin_period = rs.getString("admin_review_period");
                int admin_freq = rs.getInt("admin_review_freq");
                Boolean notification = rs.getBoolean("notification");

                //get Community
//                String comname=rs.getString("com_name");
//                String comUUID=rs.getString("com_uuid");
//                String comURI=rs.getString("com_uri");
//                Boolean comStream=rs.getBoolean("com_isStream");
////                Community comm=new Community(comname, comUUID)
                Community comm = DataLayerImplHelper.getCommunityByUUID(communityUUID, dbCon);

                //get event if exists
                Set<Event> events = null;
                int idrisk = rs.getInt("idriskop");
                //TODO get set of events
                events = DataLayerImplHelper.getEventByRiskID(idrisk, dbCon);


                //getImpact if exists
                Impact impact = null;
                impact = DataLayerImplHelper.getImpactsByRiskID(rs.getInt("idriskop"), dbCon);

                //get treatment if exists
                TreatmentWFs treatWFs = DataLayerImplHelper.getTreatementByRiskID(rs.getInt("idriskop"), dbCon);
                
                //get  treatment process ids
                ArrayList<String> treatProcIDs=DataLayerImplHelper.getProcIds(rs.getInt("idriskop"), dbCon);

                ///fill in the params
                Risk risk = new Risk();
                risk.setAdmin_review_freq(admin_freq);
                risk.setAdmin_review_period(admin_period == null ? null : Period.fromString(admin_period));

                risk.setCat_review_freq(cat_freq);
                risk.setCat_review_period(cat_period == null ? null : Period.fromString(cat_period));

                risk.setNotification(notification);
                risk.setScope(scope == null ? null : Scope.fromString(scope));
                risk.setState(state == null ? null : RiskState.fromString(state));

                //TODO set set of events
                risk.setSetEvent(events);
                risk.setImpact(impact);
                risk.setTreatment(treatWFs);
                risk.setId(id);
                risk.setTitle(title);
                risk.setOwner(owner);
                risk.setType(type);
                risk.setGroup(group);
                risk.setCommunity(comm);
                risks.add(risk);
                risk.setTreatProcIDS(treatProcIDs);

                //+((risk.getEvent()==null)?("no event"):(risk.getEvent().getPostcondition())));
                logger.debug("Extracted risk: " + risk.getTitle() + ", " + risk.getScope() + ", " + risk.getState() + ", ");
                // +((risk.getEvent()==null)?("no event"):(risk.getEvent().getPostcondition())));
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting community RISKS. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while getting community RISKS. " + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }

        return risks;
    }

    @Override
    public synchronized Set<Risk> getActiveRisks(UUID communityUUID) {
        if (communityUUID == null) {
            throw new RuntimeException("input community uuid is null");
        }

        //if webservice call the service
        if (ws) {
            return svc.getActiveRisks(communityUUID);
        }

        Set<Risk> risks = new HashSet<Risk>();
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            logger.debug("connect to db to get active risks");
            dbCon.connect();

            String query = "SELECT * from riskop, community Where community.com_uuid='" + communityUUID.toString() + "' "
                    + "and "
                    + "community.idcommunity=riskop.community_idcommunity and riskop.state='"+ RiskState.ACTIVE+"'";

logger.debug("query db to get active risks");
            ResultSet rs = dbCon.executeQuery(query);


            while (rs.next()) {
                //get risk scope, state, notification, scheduling params
                String scope = rs.getString("scope");
                String state = rs.getString("state");
                String id = rs.getString("uuid");
                String title = rs.getString("title");
                String owner = rs.getString("owner");
                String group = rs.getString("group");
                Boolean type = rs.getBoolean("type");

                String cat_period = rs.getString("cat_review_period");
                int cat_freq = rs.getInt("cat_review_freq");

                String admin_period = rs.getString("admin_review_period");
                int admin_freq = rs.getInt("admin_review_freq");
                Boolean notification = rs.getBoolean("notification");
                Community comm = DataLayerImplHelper.getCommunityByUUID(communityUUID, dbCon);

                //get event if exists
                Set<Event> events = null;
                int idrisk = rs.getInt("idriskop");
                //TODO get set of events
                events = DataLayerImplHelper.getEventByRiskID(idrisk, dbCon);


                //getImpact if exists
                Impact impact = null;
                impact = DataLayerImplHelper.getImpactsByRiskID(rs.getInt("idriskop"), dbCon);

                //get treatment if exists
                TreatmentWFs treatWFs = DataLayerImplHelper.getTreatementByRiskID(rs.getInt("idriskop"), dbCon);
                
                //get  treatment process ids
                ArrayList<String> treatProcIDs=DataLayerImplHelper.getProcIds(rs.getInt("idriskop"), dbCon);

                ///fill in the params
                Risk risk = new Risk();
                risk.setAdmin_review_freq(admin_freq);
                risk.setAdmin_review_period(admin_period == null ? null : Period.fromString(admin_period));

                risk.setCat_review_freq(cat_freq);
                risk.setCat_review_period(cat_period == null ? null : Period.fromString(cat_period));

                risk.setNotification(notification);
                risk.setScope(scope == null ? null : Scope.fromString(scope));
                risk.setState(state == null ? null : RiskState.fromString(state));

                //TODO set set of events
                risk.setSetEvent(events);
                risk.setImpact(impact);
                risk.setTreatment(treatWFs);
                risk.setId(id);
                risk.setTitle(title);
                risk.setOwner(owner);
                risk.setType(type);
                risk.setGroup(group);
                risk.setCommunity(comm);
                risks.add(risk);
                risk.setTreatProcIDS(treatProcIDs);

                //+((risk.getEvent()==null)?("no event"):(risk.getEvent().getPostcondition())));
                logger.debug("Extracted risk: " + risk.getTitle() + ", " + risk.getScope() + ", " + risk.getState() + ", ");
                // +((risk.getEvent()==null)?("no event"):(risk.getEvent().getPostcondition())));
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting community RISKS. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while getting community RISKS. " + exc.getMessage(), exc);
        } finally {
            logger.debug("close db to get active risks");
            dbCon.close();
        }

        return risks;
    }

    @Override
    public synchronized Map<Community, Set<Risk>> getAllActiveRisks() {
        Map<Community, Set<Risk>> allActiveRisks=new HashMap<Community, Set<Risk>>();
        
        Set<Community> comms=getCommunities();
        for(Community com:comms){
            Set<Risk> risks=getActiveRisks(com.getUuid());
            if(risks!=null && !risks.isEmpty())
                allActiveRisks.put(com, risks);
        }
        
        if(allActiveRisks.isEmpty())
            return null;
        
        return allActiveRisks;
    }

    @Override
    public synchronized void deleteRisk(UUID riskuuid) {

        if (riskuuid == null) {
            throw new RuntimeException("input risk uuid is null");
        }

        //if webservice call the service
        if (ws) {
            svc.deleteRisk(riskuuid);
            return;
        }

        DatabaseConnector dbCon=dbConnector.cloneCon();
        
        try {

            logger.debug("deleting risk/opp uuid=" + riskuuid);
            //System.out.println("deleting risk/opp uuid=" + riskuuid );

            String query = "Delete from riskop "
                    + "Where `uuid`='" + riskuuid + "'";

            dbCon.connect();
            dbCon.getConnection().setAutoCommit(false);


            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

            dbCon.getConnection().commit();

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting risk/opp with uuid= " + riskuuid + ". " + exc.getMessage(), exc);

            try {
                dbCon.getConnection().rollback();
            } catch (Exception ex) {
                logger.debug("Datalayer: Error while rolling back risk/opp deletion operations. " + ex.getMessage(), ex);
                throw new RuntimeException("Datalayer: Error while rolling back risk/opp deletion operations. " + ex.getMessage(), ex);
            }

            throw new RuntimeException("Datalayer: Error while deleting risk/opp with uuid= " + riskuuid + ". Rolled back DB operations. " + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }
    }

    //TODO: delete risk ops along with condition instances but not events
    @Override
    public synchronized void deleteCommunityByUUID(UUID communityUUID) {
        if (communityUUID == null) {
            throw new RuntimeException("input comunity uuid is null");
        }

        //if webservice call the service
        if (ws) {
            svc.deleteCommunityByUUID(communityUUID);
            return;
        }
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {

            //int commId = getCommunityIDByUUID(communityUUID);

            dbCon.connect();
            dbCon.getConnection().setAutoCommit(false);

            String query = "DELETE FROM community WHERE com_uuid='" + communityUUID.toString() + "'";

            //System.out.println("deleting " + communityName);
            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);//// DELETE FROM `robustwp1`.`community` WHERE `idcommunity`='1';
            dbCon.getConnection().commit();
        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting community. Rolling back any changes. " + communityUUID + ". " + exc.getMessage(), exc);

            try {
                dbCon.getConnection().rollback();
            } catch (Exception ex) {
                logger.debug("Datalayer: Error while rolling back community deletion operations. " + ex.getMessage(), ex);
                throw new RuntimeException("Datalayer: Error while rolling back community deletion operations. " + ex.getMessage(), ex);
            }

            throw new RuntimeException("Datalayer: Error while deleting community. Rolled back any changes. " + communityUUID + ". " + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }
    }

    /**
     * runs script in file at specified path
     */
    public void runScript(String path) {
        if (path == null) {
            throw new RuntimeException("input path is null");
        }
        
        //if webservice call the service
        if (ws) {
            svc.runScript(path);
            return;
        }
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            
            dbCon.ConnectToServer();
            
            String query = IOUtils.toString(new FileReader(path));
            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);
            dbCon.close();
        } catch (Exception ex) {
            throw new RuntimeException(" couldnt find script." + ex.getMessage(), ex);
        }





    }

    /**
     * method returns if the community name already exists
     */
    @Override
    public synchronized void addCommunity(Community comm) {
        if (comm == null) {
            throw new RuntimeException("input comm is null");
        }

        //if webservice call the service
        if (ws) {
            svc.addCommunity(comm);
            return;
        }

        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            //check if exists. if true, return
            Set<Community> communities = getCommunities();
            
            /* KEM - contains here does not work, as comm is a new object
            if (communities.contains(comm)) {
                throw new RuntimeException("Community already exists");
            }
            */
            
            for (Community community : communities)
            {
                if (comm.getName().equals( community.getName() ))
                    throw new RuntimeException("Community name already exists");
               /* if (comm.getCommunityID().equals( community.getCommunityID() ))
                    throw new RuntimeException("Community ID already exists");*/ //BMN communities may have different names but same id  
            }

            dbCon.connect();
            //TODO add the other attributes
            String query = "INSERT INTO community (`com_name`,`com_id`,`com_uuid`,`com_uri`, `com_isStream`, `com_streamName`) VALUES ('" 
                    + comm.getName() + "', "
                    +( comm.getCommunityID()==null ? "NULL" : "'" + comm.getCommunityID() + "'")
                    + ", "
                    +( comm.getUuid()==null ? "NULL" : "'" + comm.getUuid().toString() + "'")
                    + ", "
                    + ( comm.getUri()==null ? "NULL" : "'" + comm.getUri().toString() + "'")
                    + ", "
                    + comm.getIsStream() //TODO check if this is ok. boolean saved in mysql
                    + ", "
                    + ( comm.getStreamName() == null ? "NULL" : "'" + comm.getStreamName() + "'" )
                    + ")";

            ResultSet rs = dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

            //just for debugging
//            if (rs.next()) {
//                communityID = rs.getInt(1);
//                //System.out.println("community ID returned: " + communityID);
//            } else {
//                // throw an exception from here
//                throw new RuntimeException("DataLayer: no index returned after inserting community " + comm.getName());
//            }//end of debugging

            logger.debug("Inserted: Community " + comm.getName());
        } catch (Exception exc) {
            logger.debug("Datalayer: Error while adding community. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while adding community. " + exc.getMessage(), exc);

        } finally {
            dbCon.close();
        }
    }

    @Override
    public synchronized Objective addObjective(UUID communityUuid, Objective obj) {

        if (communityUuid == null) {
            throw new RuntimeException("input comm uuid is null");
        }

        if (obj == null) {
            throw new RuntimeException("input obj is null");
        }

        //if webservice call the service
        if (ws) {
            return svc.addObjective(communityUuid, obj);
        }

        logger.debug(" setting objective for community " + communityUuid);
        int communityID = -1;

        //check if community exists
        //get community index
        communityID = DataLayerImplHelper.getCommunityIDByUUID(communityUuid, dbConnector.cloneCon());
        if (communityID < 0) {
            throw new RuntimeException("Datalayer error: Community with such uuid does not exist in the DB");
        }

        DatabaseConnector dbCon=dbConnector.cloneCon();
        
        try {

            //check if objective already exists in this comm objectives. if true, return
            if (getObjectives(communityUuid) != null && getObjectives(communityUuid).contains(obj)) {
                for (Objective temp : getObjectives(communityUuid)) {
                    if (temp.getTitle().equals(obj.getTitle())) {
                        return temp;
                    }
                }
            }


            dbCon.connect();
            String query = "INSERT INTO objective (`uuid`, `description`, `community_idcommunity`, `title`) "
                    + "VALUES ('" + obj.getId() + "','" + obj.getDescription() + "'," + communityID + ",'" + obj.getTitle() + "')";
            ResultSet rs = dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);
            logger.debug(" Inserted: objective " + obj.getDescription());
            //System.out.println(" Inserted: objective " + obj.getDescription());

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while setting objective to community " + communityUuid + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while setting objective to community " + communityUuid + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }

        return obj;

    }

    @Override
    public synchronized void deleteObjective(UUID commUUID, Objective obj) {


        if (commUUID == null) {
            throw new RuntimeException("input comm uuid is null");
        }

        if (obj == null) {
            throw new RuntimeException("input obj is null");
        }

        //if webservice call the service
        if (ws) {
            svc.deleteObjective(commUUID, obj);
            return;
        }
        DatabaseConnector dbCon=dbConnector.cloneCon();
        //DELETE FROM `robustwp1`.`objective` WHERE `idobjective`='5' and`community_idcommunity`='4';
        try {

            logger.debug("deleting objective " + obj.getDescription() + " from community uuid" + commUUID);
            //System.out.println("deleting objective " + obj.getDescription() + " from community " + comunityName);
            int commId = DataLayerImplHelper.getCommunityIDByUUID(commUUID, dbCon);
            String query = "Delete from objective "
                    + "Where `community_idcommunity`='" + commId + "'"
                    + " and "
                    + "description='" + obj.getDescription() + "'";

            dbCon.connect();
            dbCon.getConnection().setAutoCommit(false);
            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);
            dbCon.getConnection().commit();
        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting objective " + obj.getDescription() + " from community " + commUUID + ". " + exc.getMessage(), exc);

            try {
                dbCon.getConnection().rollback();
            } catch (Exception ex) {
                logger.debug("Datalayer: Error while rolling back objective deletion operations. " + ex.getMessage(), ex);
                throw new RuntimeException("Datalayer: Error while rolling back objective deletion operations. " + ex.getMessage(), ex);
            }

            throw new RuntimeException("Datalayer: Error while deleting objective. Rolled back the DB deletion operations. " + obj.getDescription() + " from community " + commUUID + ". " + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }
    }

//this is called for event , impact, treatment adding
    //or modification of any of the other parameters
    //this requires that the risk is in the DB.
    @Override
    public synchronized void updateRisk(Risk risk) throws Exception {


        if (risk == null) {
            throw new RuntimeException("input risk is null");
        }



        //if webservice call the service
        if (ws) {
            //check if the newly modified risk duplicates existing one
            Set<Risk> currentRisks = svc.getRisks(risk.getCommunity().getUuid());
//        if (currentRisks.containsObjective(risk)) {
//            throw new RuntimeException("the new updates create duplicate risks. A duplicate risk with title " + risk.getTitle() + " exists!!");
//        }
            for (Risk ris : currentRisks) {
                if (ris.equals(risk)) {
                    logger.debug("Risk with title " + risk.getTitle() + " already exists!! No changes made to the DB.");
                    throw new RuntimeException("Risk with title " + risk.getTitle() + " already exists!! No changes made to the DB.");
                }
            }
            svc.updateRisk(risk);
            return;
        }

        int idrisk = -1;
        int idevent = -1;
        Impact existingImp = null;
        TreatmentWFs existingTreats = null;

        if (risk.getState() == null) {
            risk.setState(RiskState.INACTIVE);
        }
        if (risk.getScope() == null) {
            risk.setScope(Scope.USER);
        }
        if (risk.getOwner() == null) {
            risk.setOwner("manager");
        }
        if (risk.getCat_review_period() == null) {
            risk.setCat_review_period(Period.WEEK);
        }
        if (risk.getAdmin_review_period() == null) {
            risk.setAdmin_review_period(Period.WEEK);
        }

        //check if the newly modified risk duplicates existing one
        Set<Risk> currentRisks = getRisks(risk.getCommunity().getUuid());
//        if (currentRisks.containsObjective(risk)) {
//            throw new RuntimeException("the new updates create duplicate risks. A duplicate risk with title " + risk.getTitle() + " exists!!");
//        }
        for (Risk ris : currentRisks) {
            if (ris.equals(risk)) {
                logger.debug("The risk " + risk.getTitle() + " is already in the database. No updates made. ");
                throw new RuntimeException("The risk " + risk.getTitle() + " is already in the database. No updates made. ");
            }
        }
        
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            dbCon.connect();
            dbCon.getConnection().setAutoCommit(false);

            // make sure that the risk exists in DB, and there is only one
            //get event id, predictor id, impact id, treatment ids
            String riskquery = "Select * from riskop where riskop.uuid='" + risk.getId().toString() + "'";
            ResultSet rs = dbCon.executeQuery(riskquery);
            if (rs.next()) {
                idrisk = rs.getInt(1);
//                idevent = rs.getInt("events_idevents"); 
                //getImpact if exists

                existingImp = DataLayerImplHelper.getImpactsByRiskID(rs.getInt("idriskop"), dbCon);

                //get treatment if exists
                existingTreats = DataLayerImplHelper.getTreatementByRiskID(rs.getInt("idriskop"), dbCon);
            }

//            // move to the end of the result set
//            rs.last();
//            // get the row number of the last row which is also the row count
//            int rowCount = rs.getRow();
//            logger.debug("rowCount=" + rowCount);
//            if (rowCount > 1) {
//                throw new RuntimeException("Datalayer: more than one risk with these attributes found in DB. ");
//            }



            if (idrisk > 0) {//check if risk is there

                //update event if exists
//                if (predic != null) {
                DataLayerImplHelper.updateEvent(risk, idrisk, dbCon/*
                         * , predic
                         */);
//                }

                //update impact if exists
                DataLayerImplHelper.updateImpact(risk.getImpact(), idrisk, existingImp, dbCon);

                //update treatment if exists
                DataLayerImplHelper.updateTreatments(risk.getTreatment(), idrisk, existingTreats, dbCon);
                
                //update proc IDs
                DataLayerImplHelper.updateProcIDs(risk.getTreatProcIDS(),idrisk,dbCon);

                //for each risk attribute update/create with new value
                String query = "UPDATE riskop SET "
                        + "`state`='" + risk.getState().toString() + "', "
                        + "`title`='" + risk.getTitle() + "', "
                        + "`scope`='" + risk.getScope().toString() + "', "
                        + "`cat_review_freq`=" + risk.getCat_review_freq() + ", "
                        + "`cat_review_period`='" + risk.getCat_review_period().toString() + "', "
                        + "`admin_review_freq`=" + risk.getAdmin_review_freq() + ", "
                        + "`admin_review_period`='" + risk.getAdmin_review_period().toString() + "', "
                        + "`notification`=" + risk.isNotification() + ", "
                        + "`owner`='" + risk.getOwner() + "', "
                        + "`type`=" + risk.getType() + ", "
                        + "`group`='" + risk.getGroup() + "' "
                        + " WHERE `uuid`='" + risk.getId().toString() + "'";

                dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

                dbCon.getConnection().commit();
            } else {
                logger.debug("Datalayer: error while updating risk. couldnt find risk in DB. ");
                throw new RuntimeException("Datalayer: error while updating risk. couldnt find risk in DB. ");
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Rolling back because of Error while updating risk. " + exc.getMessage(), exc);
            //System.out.println("Datalayer: Rolling back because of Error while updating risk. " + exc.getMessage());
            exc.printStackTrace();

            try {
                dbCon.getConnection().rollback();
            } catch (Exception ex) {
                logger.debug("Datalayer: Error while rolling back risk update operations. " + ex.getMessage(), ex);
                throw new RuntimeException("Datalayer: Error while rolling back risk update operations. " + ex.getMessage(), ex);
            }
            logger.debug("Datalayer: Error while updating risk. Rolling back risk update operations." + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while updating risk. Rolling back risk update operations." + exc.getMessage(), exc);

        } finally {
            dbCon.close();
        }

        //System.out.println("risk updated");
    }

    //TODO: update this method
    @Override
    public synchronized void addPredictor(PredictorServiceDescription pred) {

        if (pred == null) {
            throw new RuntimeException("input pred is null");
        }
        //if webservice call the service
        if (ws) {
            svc.addPredictor(pred);
            return;
        }

        Set<PredictorServiceDescription> predictors = getPredictors();
        if (predictors.contains(pred)) {
            //System.out.println("didnt add predictor. already in the database");
            return;
        }
        DatabaseConnector dbCon=dbConnector.cloneCon();
        //transaction
        try {
            dbCon.connect();
            dbCon.getConnection().setAutoCommit(false);

            String query = "INSERT INTO predictor (`predic_uuid`, `predic_name`, `predic_version`, `predic_desc`, `predic_uri`,`predic_svctargetnamespace`, `predic_svcname`, `predic_portname` ) "
                    + "VALUES ('" + pred.getUuid() + "','" + pred.getName() + "','" + pred.getVersion() + "','" + pred.getDescription() + "',"
                    + (pred.getServiceURI() == null ? "NULL" : ("'" + pred.getServiceURI() + "'"))
                    + "," + (pred.getServiceTargetNamespace() == null ? "NULL" : ("'" + pred.getServiceTargetNamespace() + "'"))
                    + "," + (pred.getServiceName() == null ? "NULL" : ("'" + pred.getServiceName() + "'"))
                    + "," + (pred.getServicePortName() == null ? "NULL" : ("'" + pred.getServicePortName() + "'"))
                    + ")";

            logger.debug(" qurey: " + query);
            ResultSet rsPredict = dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);
            logger.debug("Inserted: Predictor " + pred.getName() + " qurey: " + query);

            //get the predictor id
            int idpred = -1;
            if (rsPredict.next()) {
                idpred = rsPredict.getInt(1);
            }

            //add   List<Parameter> configurationParams;
            DataLayerImplHelper.addParameters(idpred, pred.getConfigurationParams(), true, false, dbCon);

            // Parameter forecastPeriod;
            if (pred.getForecastPeriod() != null) {
                List<Parameter> forecastList = new ArrayList<Parameter>();
                forecastList.add(pred.getForecastPeriod());
                DataLayerImplHelper.addParameters(idpred, forecastList, true, true, dbCon);
            }

            // List<Event> events;
            DataLayerImplHelper.addEvents(idpred, pred.getEvents(), dbCon);


            dbCon.getConnection().commit();
            //end transaction
        } catch (Exception exc) {
            try {
                dbCon.getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Datalayer: Error while rolling back. " + exc.getMessage(), exc);
            }
            logger.debug("Datalayer: Error while adding predictor. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while adding predictor. " + exc.getMessage(), exc);

        } finally {
            dbCon.close();
        }
    }

    @Override
    public synchronized Set<PredictorServiceDescription> getPredictors() {

        //if webservice call the service
        if (ws) {
            return svc.getPredictors();
        }

        Set<PredictorServiceDescription> predictors = new HashSet<PredictorServiceDescription>();

        logger.debug(" getting predictors: ");
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            dbCon.connect();
            String query = "SELECT * from predictor";
            ResultSet rs = dbCon.executeQuery(query);
            while (rs.next()) {
                int idpredic = rs.getInt("idpredictor");
                String predicuuid = rs.getString("predic_uuid");
                String predicName = rs.getString("predic_name");
                String predicVersion = rs.getString("predic_version");
                String predic_desc = rs.getString("predic_desc");
                String predic_uri = rs.getString("predic_uri");

                String predic_svctargetnamespace = rs.getString("predic_svctargetnamespace");
                String predic_svcname = rs.getString("predic_svcname");
                String predic_portname = rs.getString("predic_portname");



                PredictorServiceDescription predic = new PredictorServiceDescription(
                        UUID.fromString(predicuuid),
                        predicName,
                        predicVersion,
                        predic_desc, predic_uri == null ? null : new URI(predic_uri));

                predic.setServiceName(predic_svcname);
                predic.setServiceTargetNamespace(predic_svctargetnamespace);
                predic.setServicePortName(predic_portname);


                //still to get forecastPeriod; 
                Parameter forecastParam =DataLayerImplHelper.getForecastParam(idpredic, dbCon);

                List<Parameter> configParams = DataLayerImplHelper.getParams(idpredic, true, dbCon);


                List<Event> events = DataLayerImplHelper.getPredictorEvents(idpredic, dbCon);

                predic.addEvents(events);
                predic.addConfigurationParams(configParams);
                predic.setForecastPeriod(forecastParam);


                predictors.add(predic);

                logger.debug("Extracted: " + predicName + ": " + predicVersion);
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting predictors. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while getting predictors. " + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }

        return predictors;
    }

    @Override
    public synchronized void deletePredictor(UUID uuid) {

        if (uuid == null) {
            throw new RuntimeException("input uuid is null");
        }
        //if webservice call the service
        if (ws) {
            svc.deletePredictor(uuid);
            return;
        }
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {

            logger.debug("deleting predictor uuid=" + uuid + ". "
                    + "This will delete all events, parameters, conditions and condition instances associated with this perdictor. ");
            //System.out.println("deleting predictor uuid=" + uuid
            // + ". This will delete all events, parameters, conditions and condition instances associated with this perdictor. ");


            String query = "Delete from predictor "
                    + "Where `predic_uuid`='" + uuid + "'";

            dbCon.connect();
            dbCon.getConnection().setAutoCommit(false);
            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);
            dbCon.getConnection().commit();

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting predictor uuid=" + uuid + ". " + exc.getMessage(), exc);
            try {
                dbCon.getConnection().rollback();
            } catch (Exception ex) {
                logger.debug("Datalayer: Error while rolling back predictor deletion operations. " + ex.getMessage(), ex);
                throw new RuntimeException("Datalayer: Error while rolling back predictor deletion operations. " + ex.getMessage(), ex);
            }

            throw new RuntimeException("Datalayer: Error while deleting predictor uuid=" + uuid + ". Rolled back DB operations. " + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }
    }

    @Override
    public synchronized PredictorServiceDescription getPredictor(Event ev) {
        if (ev == null) {
            throw new RuntimeException("input ev is null");
        }
        //if webservice call the service
        if (ws) {
            return svc.getPredictor(ev);
        }

        PredictorServiceDescription predic = null;
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {

            dbCon.connect();
            String query = "SELECT * from predictor,event Where idpredictor=event.predictor_idpredictor and event.event_uuid='" + ev.getUuid().toString() + "'";

            ResultSet rs = dbCon.executeQuery(query);

            if (rs.next()) {
                //get predictor if exists
                int idpredic = rs.getInt("idpredictor");
                String predicName = rs.getString("predic_name");
                String predicVersion = rs.getString("predic_version");
                String predicuuid = rs.getString("predic_uuid");
                String predic_desc = rs.getString("predic_desc");
                String predic_uri = rs.getString("predic_uri");
                String predic_svctargetnamespace = rs.getString("predic_svctargetnamespace");
                String predic_svcname = rs.getString("predic_svcname");
                String predic_portname = rs.getString("predic_portname");

                predic = new PredictorServiceDescription(UUID.fromString(predicuuid), predicName, predicVersion, predic_desc);
                predic.setServiceName(predic_svcname);
                predic.setServiceTargetNamespace(predic_svctargetnamespace);
                predic.setServicePortName(predic_portname);
                predic.setServiceURI(predic_uri == null ? null : new URI(predic_uri));

//                predic.preConfigure(predicName);//TODO: remove this when all info is stored in DB

                  //still to get forecastPeriod; 
                Parameter forecastParam =DataLayerImplHelper.getForecastParam(idpredic, dbCon);
                
                List<Parameter> configParams = DataLayerImplHelper.getParams(idpredic, true, dbCon);


                List<Event> events = DataLayerImplHelper.getPredictorEvents(idpredic, dbCon);

                predic.setForecastPeriod(forecastParam);
                predic.addEvents(events);
                predic.addConfigurationParams(configParams);


                logger.debug("Extracted: predictor for event with uuid " + ev.getUuid().toString());
            } else {
                logger.debug("Datalayer: couldnt find predictor with id " + ev.getUuid().toString());
                throw new RuntimeException("Datalayer: couldnt find predictor with id " + ev.getUuid().toString());
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting PredictorByID. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting PredictorByID. " + exc.getMessage());
        } finally {
            dbCon.close();
        }

        return predic;
    }

    //for now the start and end dates are being ignored. all the evaluation results are returned
    @Override
    public synchronized Set<EvaluationResult> getRiskEvalResults(UUID riskuuid, Date start, Date end) {

        if(start!=null && end!=null && start.after(end))
            throw new RuntimeException("start date is later than the end date");
        
        if (riskuuid == null) {
            throw new RuntimeException("input riskuuid is null");
        }
        //if webservice call the service
        if (ws) {
            return svc.getRiskEvalResults(riskuuid, start, end);
        }

        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        
        Set<EvaluationResult> setEvalRst = new HashSet<EvaluationResult>();
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            
            dbCon.connect();
            //get risk id from risk uuid
            int riskId = DataLayerImplHelper.getEntityIDByUUID("riskop", riskuuid, dbCon);

            //get eval results
            String query = "SELECT * from evaluationresult WHERE riskop_idriskop=" + riskId 
                    +((start!=null)?" AND forecastdate>='"+formatter.format(start)+"'":"")
                    +((end!=null)?" AND forecastdate<='" + formatter.format(end)+"'":"");
            
            ResultSet rs = dbCon.executeQuery(query);
            while (rs.next()) {
                //fill in eval results + job details
                int idEvalResult = rs.getInt("idevalresults");
                String strCurDate = rs.getString("currentdate");
                Date Curdate = (strCurDate.equals("NULL")) ? null : (Date) formatter.parse(strCurDate);

                String strUUID = rs.getString("uuid");
                String strforecastdate = rs.getString("forecastdate");
                Date forecastdate = (strforecastdate.equals("NULL")) ? null : (Date) rs.getDate("forecastdate");//formatter.parse(strforecastdate)

                String strcurrentobs = rs.getString("currentobs");

                String strjobref = rs.getString("jobref");
                String strstatus = rs.getString("status");
                String strmetadata = rs.getString("metadata");
                JobStatus status = new JobStatus(JobStatusType.valueOf(strstatus), strmetadata);

                String strrequestdate = rs.getString("requestdate");
                Date requestdate = (strrequestdate.equals("NULL")) ? null : (Date) formatter.parse(strrequestdate);

                String strstartdate = rs.getString("startdate");
                Date startdate = (strstartdate.equals("NULL")) ? null : (Date) formatter.parse(strstartdate);

                String strcompletiondate = rs.getString("completiondate");
                Date completiondate = (strcompletiondate.equals("NULL")) ? null : (Date) formatter.parse(strcompletiondate);

                JobDetails jbdtls = new JobDetails(strjobref, status, requestdate, startdate, completiondate);//TODO: sort out the dates problems



                EvaluationResult evalRst = new EvaluationResult();
                evalRst.setResultUUID(UUID.fromString(strUUID));
                evalRst.setCurrentDate(Curdate);
                //evalRst.setCurrentObservation(strcurrentobs); // TODO: sort out current observation
                evalRst.setForecastDate(forecastdate);
                evalRst.setJobDetails(jbdtls);
                //evalRst.setMetaData(strmetadata);
                evalRst.setRiskUUID(riskuuid);


                //get result items
                String resItemquery = "SELECT * from resultitem WHERE evaluationresult_idevalresults=" + idEvalResult;
                ResultSet rsItems = dbCon.executeQuery(resItemquery);
                while (rsItems.next()) {
                    String name = rsItems.getString("name");
                    Double prob = rsItems.getDouble("prob");
                    ResultItem rsItem = new ResultItem(name, prob);
                    //add tp the set of resultItems
                    evalRst.addResultItem(rsItem);
                }



                //add the result to the set
                setEvalRst.add(evalRst);

            }

            return setEvalRst;
        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting evaluation results for risk uuid: " + riskuuid + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting evaluation results for risk uuid: " + riskuuid + exc.getMessage());

        } finally {
            dbCon.close();
        }

    }

    @Override
    public synchronized Set<Community> getCommunities() {

        //if webservice call the service
        if (ws) {
            return svc.getCommunities();
        }

        // KEM - use LinkedHashSet to get ordered set
        Set<Community> comms = new LinkedHashSet<Community>();
        
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            logger.debug("Connect to db");
            dbCon.connect();
            String query = "SELECT * from community ORDER BY idcommunity";
            logger.debug("query db");
            ResultSet rs = dbCon.executeQuery(query);


            while (rs.next()) {
                String communityName = rs.getString("com_name");
                String strUuid = rs.getString("com_uuid");
                String strUri = rs.getString("com_uri");
                Boolean isStream = rs.getBoolean("com_isStream");
                
                String strId = rs.getString("com_id");
                String strStreamName = rs.getString("com_streamName");
                
                UUID uuid = UUID.fromString(strUuid);
                URI uri = null;
                if(strUri!=null)
                    uri=new URI(strUri);

                //TODO: add other attributes, add atts to constructor below
                Community comm = new Community(communityName, uuid, strId, strStreamName,uri, isStream);
              
                if(comm!=null)
                    comms.add(comm);

                logger.debug("Extracted: " + rs.getLong("idcommunity") + ": " + comm.getName());
            }


        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting communities. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting communities. " + exc.getMessage());

        } finally {
            logger.debug("close connection");
            dbCon.close();
        }

        return comms;
    }

    @Override
    public Set<Objective> getObjectives(UUID communityUUID) {
        if (communityUUID == null) {
            throw new RuntimeException("input communityUUID is null");
        }
        //if webservice call the service
        if (ws) {
            return svc.getObjectives(communityUUID);
        }

        //Set<Objective> obs = new HashSet<Objective>();
        // KEM - use LinkedHashSet to get ordered set
        Set<Objective> obs = new LinkedHashSet<Objective>();

        logger.debug(" getting objectives for community " + communityUUID.toString());
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            dbCon.connect();
            String query = "SELECT * from objective, community Where "
                    + "community.com_uuid='" + communityUUID.toString() + "' and community.idcommunity=objective.community_idcommunity"
                    + " ORDER BY idobjective";
            ResultSet rs = dbCon.executeQuery(query);
            while (rs.next()) {
                String objective_description = rs.getString("description");
                String uuid = rs.getString("uuid");
                String title = rs.getString("title");
                Objective obj = new Objective(title, objective_description, UUID.fromString(uuid));
                obs.add(obj);
                logger.debug("Extracted: " + communityUUID.toString() + ": " + title);
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting objectives. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while getting objectives. " + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }

        return obs;
    }

    @Override
    public synchronized Risk saveRisk(Community community, String title, String owner, Boolean type, String group) {
        if (community == null) {
            throw new RuntimeException("input community is null");
        }
        //if webservice call the service
        if (ws) {
            return svc.saveRisk(community, title, owner, type, group);
        }
        /////////////////////////////
        Risk risk = new Risk(community, title, owner, type, group);
        Set<Risk> currentRisks = getRisks(community.getUuid());

        //TODO change this to compare title owner type and group only??
//        if(currentRisks.containsObjective(risk))   
//            throw new RuntimeException("Risk already exists in the DB");
//        else
//            //System.out.println("new risk being added");

        for (Risk ris : currentRisks) {
            if (ris.equals(risk)) {
                logger.debug("The risk " + risk.getTitle() + " is already in the database. No updates made. ");
                throw new RuntimeException("The risk " + risk.getTitle() + " is already in the database. No updates made. ");
            }
        }

        DatabaseConnector dbCon=dbConnector.cloneCon();
        //save risk and its attributes
        try {
            //get community id
            int idcommunity = DataLayerImplHelper.getCommunityIDByUUID(community.getUuid(), dbCon);
            dbCon.connect();
            dbCon.getConnection().setAutoCommit(false);

            //insert risk op, link to event and community
            String queryRisk = "INSERT INTO riskop (`community_idcommunity`, `uuid`, `title`,`owner`,`type`, `group`,"
                    + "`scope`, `cat_review_freq`, `cat_review_period`, `admin_review_freq`, `admin_review_period`, `notification`) "
                    + "VALUES (" + idcommunity
                    + ",'" + risk.getId().toString() + "','"
                    + title + "','"
                    + owner + "',"
                    + type + ",'"
                    + group + "','"
                    + risk.getScope() + "',"
                    + risk.getCat_review_freq() + ",'"
                    + risk.getCat_review_period() + "',"
                    + risk.getAdmin_review_freq() + ",'"
                    + risk.getAdmin_review_period() + "',"
                    + risk.isNotification()
                    + ")";

            ResultSet rsRisk = dbCon.execute(queryRisk, Statement.RETURN_GENERATED_KEYS);
            dbCon.getConnection().commit();

        } catch (Exception exc) {
            logger.debug("Datalayer: Rolling back because of Error while adding risk. " + exc.getMessage(), exc);
            //System.out.println("Datalayer: Rolling back because of Error while adding risk. " + exc.getMessage());
            exc.printStackTrace();

            try {
                dbCon.getConnection().rollback();
            } catch (Exception ex) {
                logger.debug("Datalayer: Error while rolling back risk addition operations. " + ex.getMessage(), ex);
                throw new RuntimeException("Datalayer: Error while rolling back risk addition operations. " + ex.getMessage(), ex);
            }

            logger.debug("Datalayer: Error while adding risk. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while adding risk. " + exc.getMessage(), exc);

        } finally {
            dbCon.close();
        }


        return risk;
    }

    @Override
    public synchronized void saveEvaluationResults(UUID riskuuid, EvaluationResult evalResult) {

        if (evalResult == null) {
            throw new IllegalArgumentException("evalResult shouldnot be null");
        }

        //if webservice call the service
        if (ws) {
            svc.saveEvaluationResults(riskuuid, evalResult);
            return;
        }

        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            dbCon.connect();
            dbCon.getConnection().setAutoCommit(false);
            //check if evaluation result with the same uuid is already in DB
            UUID evalResUUID = evalResult.getResultUUID();
            if (DataLayerImplHelper.containsUUID("evaluationresult", evalResUUID, dbCon)) {
                throw new RuntimeException("Datalayer Error: UUID already exists in the database.");
            }

            //get the riskid from risk uuid
            int riskId = DataLayerImplHelper.getEntityIDByUUID("riskop", riskuuid, dbCon);

            DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");

            String strcurrentdate = evalResult.getCurrentDate() == null ? "NULL" : formatter.format(evalResult.getCurrentDate());

            String strForecastDate = evalResult.getForecastDate() == null ? "NULL" : formatter.format(evalResult.getForecastDate());
            String strRequestDate = evalResult.getJobDetails().getRequestDate() == null ? "NULL" : formatter.format(evalResult.getJobDetails().getRequestDate());
            String strStartDate = evalResult.getJobDetails().getStartDate() == null ? "NULL" : formatter.format(evalResult.getJobDetails().getStartDate());
            String strCompletionDate = evalResult.getJobDetails().getCompletionDate() == null ? "NULL" : formatter.format(evalResult.getJobDetails().getCompletionDate());
            //save evluation results object without the result items
            String query = "INSERT INTO evaluationresult "
                    + "(`riskop_idriskop`, `uuid`, `currentdate`, `forecastdate`, `metadata`, `jobref`, `status`, `requestdate`, `startdate`, `completiondate`) "
                    + "VALUES "
                    + "(" + riskId
                    + ", '" + evalResUUID
                    + "', '" + strcurrentdate
                    + "', '" + strForecastDate
                    + "', '" + ""//evalResult.getMetaData() // TODO: update saving of meta-data when the DB schema is updated
                    + "', '" + evalResult.getJobDetails().getJobRef()
                    + "', '" + evalResult.getJobDetails().getJobStatus().getStatus().toString()
                    + "', '" + strRequestDate
                    + "', '" + strStartDate
                    + "', '" + strCompletionDate + "')";

            logger.debug("Evalresult save: " + query);

            ResultSet rsEvalRst = dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

            //get the id of the evalution result object in DB
            int evalrsltID = -1;
            if (rsEvalRst.next()) {
                evalrsltID = rsEvalRst.getInt(1);
            }

            logger.debug("saved eval result ID: " + evalrsltID);

            if (evalResult.getResultItems() != null) {
                //add result items to DB given the evaluation result ID
                for (ResultItem item : evalResult.getResultItems()) {
                    //save item,add eval result id
                    String itemquery = "INSERT INTO resultitem (`evaluationresult_idevalresults`, `name`, `prob`) "
                            + "VALUES (" + evalrsltID + ", '" + item.getName() + "', " + item.getProbability() + ")";
                    ResultSet rsRstItem = dbCon.execute(itemquery, Statement.RETURN_GENERATED_KEYS);
                }
            }

            dbCon.getConnection().commit();
        } catch (Exception exc) {
            logger.debug("Datalayer: Rolling back because of Error while adding evaluation results. " + exc.getMessage(), exc);
            //System.out.println("Datalayer: Rolling back because of Error while adding risk. " + exc.getMessage());
            exc.printStackTrace();

            try {
                dbCon.getConnection().rollback();
            } catch (Exception ex) {
                logger.debug("Datalayer: Error while rolling back evaluation results addition operations. " + ex.getMessage(), ex);
                throw new RuntimeException("Datalayer: Error while rolling back evaluation results  addition operations. " + ex.getMessage(), ex);
            }

            logger.debug("Datalayer: Error while adding evaluation results. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while adding evaluation results. " + exc.getMessage(), exc);

        } finally {
            dbCon.close();
        }


    }

    @Override
    public Community getCommunityByUUID(UUID comUUID) {
        if (comUUID == null) {
            throw new RuntimeException("input comUUID is null");
        }
        //if webservice call the service
        if (ws) {
            return svc.getCommunityByUUID(comUUID);
        }

        Community com = null;
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            dbCon.connect();
            String query = "SELECT * from community WHERE com_uuid='" + comUUID.toString() + "'";
            ResultSet rs = dbCon.executeQuery(query);
            com=DataLayerImplHelper.extractCommunity(rs);
        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting communities. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting communities. " + exc.getMessage());

        } finally {
            dbCon.close();
        }

        return com;
    }

    @Override
    public synchronized Risk getRiskByUUID(UUID riskUUID) {
        if (riskUUID == null) {
            throw new RuntimeException("input riskUUID is null");
        }
        //if webservice call the service
        if (ws) {
            return svc.getRiskByUUID(riskUUID);
        }

        Risk risk = null;
        DatabaseConnector dbCon=dbConnector.cloneCon();
        try {
            dbCon.connect();

            String query = "SELECT * from riskop Where uuid='" + riskUUID.toString() + "'";


            ResultSet rs = dbCon.executeQuery(query);


            while (rs.next()) {
                //get risk scope, state, notification, scheduling params
                String scope = rs.getString("scope");
                String state = rs.getString("state");
                String id = rs.getString("uuid");
                String title = rs.getString("title");
                String owner = rs.getString("owner");
                String group = rs.getString("group");
                Boolean type = rs.getBoolean("type");

                String cat_period = rs.getString("cat_review_period");
                int cat_freq = rs.getInt("cat_review_freq");

                String admin_period = rs.getString("admin_review_period");
                int admin_freq = rs.getInt("admin_review_freq");
                Boolean notification = rs.getBoolean("notification");
                
                int community_idcommunity=rs.getInt("community_idcommunity");

Community comm = DataLayerImplHelper.getCommunityByID(community_idcommunity, dbCon);

                //get event if exists
                Set<Event> events = null;
                int idrisk = rs.getInt("idriskop");
                //TODO get set of events
                events = DataLayerImplHelper.getEventByRiskID(idrisk, dbCon);


                //getImpact if exists
                Impact impact = null;
                impact = DataLayerImplHelper.getImpactsByRiskID(rs.getInt("idriskop"), dbCon);

                //get treatment if exists
                TreatmentWFs treat = DataLayerImplHelper.getTreatementByRiskID(rs.getInt("idriskop"), dbCon);
                
                //get treatment procIds
                ArrayList<String> treatProcIds=DataLayerImplHelper.getProcIds(idrisk, dbCon);

                ///fill in the params
                risk = new Risk();
                risk.setAdmin_review_freq(admin_freq);
                risk.setAdmin_review_period(admin_period == null ? null : Period.fromString(admin_period));

                risk.setCat_review_freq(cat_freq);
                risk.setCat_review_period(cat_period == null ? null : Period.fromString(cat_period));

                risk.setNotification(notification);
                risk.setScope(scope == null ? null : Scope.fromString(scope));
                risk.setState(state == null ? null : RiskState.fromString(state));

                //TODO set set of events
                risk.setSetEvent(events);
                risk.setImpact(impact);
                risk.setTreatment(treat);
                risk.setId(id);
                risk.setTitle(title);
                risk.setOwner(owner);
                risk.setType(type);
                risk.setGroup(group);
                risk.setTreatProcIDS(treatProcIds);
                risk.setCommunity(comm);

                logger.debug("Extracted risk: " + risk.getTitle() + ", " + risk.getScope() + ", " + risk.getState() + ", ");

            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting community RISKS. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while getting community RISKS. " + exc.getMessage(), exc);
        } finally {
            dbCon.close();
        }

        return risk;

    }
    
    @Override
    public synchronized void deleteEvaluationResults(UUID riskuuid){
  
        
        if (riskuuid == null) {
            throw new RuntimeException("input riskuuid is null");
        }
        //if webservice call the service
        if (ws) {
            svc.deleteEvaluationResults(riskuuid);
        } else {

            DatabaseConnector dbCon = dbConnector.cloneCon();


            try {

                dbCon.connect();
                //get risk id from risk uuid
                int riskId = DataLayerImplHelper.getEntityIDByUUID("riskop", riskuuid, dbCon);
                String query = "Delete from evaluationresult "
                        + "Where `riskop_idriskop`='" + riskId + "'";


                ResultSet rs = dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

            } catch (Exception exc) {
                logger.debug("Datalayer: Error while getting evaluation results for risk uuid: " + riskuuid + exc.getMessage());
                throw new RuntimeException("Datalayer: Error while getting evaluation results for risk uuid: " + riskuuid + exc.getMessage());

            } finally {
                dbCon.close();
            }
        }

    }
}
