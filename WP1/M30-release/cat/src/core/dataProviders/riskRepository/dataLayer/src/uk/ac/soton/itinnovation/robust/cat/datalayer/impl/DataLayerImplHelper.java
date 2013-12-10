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
//      Created Date :          04-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;
import uk.ac.soton.itinnovation.robust.riskmodel.*;

public class DataLayerImplHelper {

    private static String dbURL;
    private static String dbName;
    private static String userName;
    private static String password;
    static Logger logger = Logger.getLogger(DataLayerImplHelper.class);

    public static Map<String,String> getConfigs() {
        Map<String,String> configs=new HashMap<String, String>();
        Properties prop = new Properties();

        try {
            prop.load(DataLayerImplHelper.class.getClassLoader().getResourceAsStream("data.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("Error with loading configuration file data.properties. " + ex.getMessage(), ex);
        }


        dbURL = prop.getProperty("dburl");
        dbName = prop.getProperty("dbname");
        userName = prop.getProperty("username");
        password = prop.getProperty("password");
        
        String url= prop.getProperty("url");
        String namespace= prop.getProperty("namespace");
        String localpart= prop.getProperty("localpart");
        String webservice=prop.getProperty("webservice");
        
        configs.put("dburl", dbURL);
        configs.put("dbname", dbName);
        configs.put("username", userName);
        configs.put("password", password);
        
        configs.put("url", url);
        configs.put("namespace", namespace);
        configs.put("localpart", localpart);
        configs.put("webservice", webservice);

        
        if((webservice!=null) && webservice.equals("true"))
            logger.debug("using webservice");
        else 
            logger.debug("Using local DB configs: dbURL= " + dbURL + ", Username=" + userName);
        
        return configs;
    }
    
    
    //checks if uuid is there. this may be redundant if in the DB the uuid is set to be unique
    public static boolean containsUUID(String table, UUID uuid, DatabaseConnector dbCon) {
        try {
            String query = "SELECT * from " + table + " WHERE uuid='" + uuid.toString() + "'";
            ResultSet rs = dbCon.executeQuery(query);
            if (rs.next()) {
                return true;
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error while checking UUID " + uuid + " in the database." + ex.getMessage(), ex);
        }

        return false;

    }
    
    //this returns the entity DB id given its uuid
    public static int getEntityIDByUUID(String table, UUID uuid, DatabaseConnector dbCon){
        try {
            String query = "SELECT * from " + table + " WHERE uuid='" + uuid.toString() + "'";
            ResultSet rs = dbCon.executeQuery(query);
            if (rs.next()) {
                int id=rs.getInt(1);
                return id;
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error while checking UUID " + uuid + " in the database." + ex.getMessage(), ex);
        }

        return -1;
    }
    
    
     public static int findConditionID(EventCondition existingCondition, DatabaseConnector dbCon) {

        try {
            String instanceQuery = "SELECT * from `" + dbName + "`.`condition` WHERE condition.cond_uuid='" + existingCondition.getUUID().toString() + "'";

            //String instanceQuery = "SELECT * from `"+dbName+"`.condition Where condition.cond_uuid='" + existingCondition.getUUID().toString() + "'";
            logger.debug(instanceQuery);
            ResultSet rsInstance = dbCon.executeQuery(instanceQuery);

            if (rsInstance.next()) {
                return rsInstance.getInt("idcondition");
            } else {
                return -1;
            }

        } catch (Exception ex) {
            throw new RuntimeException("Datalayer: Error while getting condition ID for " + existingCondition.getName() + ". " + ex.getMessage(), ex);
        }
    }
     
     
      public static void updateImpact(Impact newImp, int idrisk, Impact existingImp, DatabaseConnector dbCon) throws Exception {

        try {

            if ((newImp == null || newImp.getImpactMap().isEmpty()) && existingImp != null && !existingImp.getImpactMap().isEmpty()) {
                deleteImpacts(idrisk,dbCon);
            }

            if (newImp != null && newImp.getImpactMap() != null && !newImp.getImpactMap().isEmpty()) {//TODO consider the case when these are null. delete them from db if exist
                //find impacts in the DB that are not in the newimpacts

                for (Map.Entry<Objective, ImpactLevel> entry : existingImp.getImpactMap().entrySet()) {
                    //System.out.println("Testing " + entry.getKey().getTitle() + " uuid " + entry.getKey().getId());
                    if (!newImp.containsObjective(entry)) {

                        String uuid = entry.getKey().getId();
                        //System.out.println("deleting " + entry.getKey().getTitle() + ", uuid:" + uuid);
                        int objid = getObjectiveIDByUUID(uuid, dbCon);
                        deleteImpactEntry(objid, idrisk, dbCon);
                    }
                }


                for (Objective obj : newImp.getImpactMap().keySet()) {//some obj impacts may be newly added while others are new//TODO: consider the case when some are deleted
                    //get Impact id given objectiveid and riskid
                    int objid = getObjectiveIDByUUID(obj.getId(), dbCon);
                    boolean impactExist = impactExists(objid, idrisk, dbCon);

                    if (impactExist) {
                        //update impact value and polarity with the newImp
                        String impQuery = "UPDATE impact SET "
                                + "`value`='" + newImp.getImpactMap().get(obj).toString() + "' "
                                //   + "`ispositive`=" + newImp.getImpactMap().get(obj).isIsPositive()
                                + " WHERE `riskop_idriskop`=" + idrisk
                                + " and `objective_idobjective`=" + objid;


                        dbCon.execute(impQuery, Statement.RETURN_GENERATED_KEYS);
                    } else {
                        String impQuery = "INSERT INTO impact (`riskop_idriskop`, `value`, `objective_idobjective`) "//, `ispositive`
                                + "VALUES "
                                + "(" + idrisk
                                + ", '" + newImp.getImpactMap().get(obj).toString()
                                + "', " + objid
                                //    + ", " + newImp.getImpactMap().get(obj).isIsPositive()
                                + ")";
                        dbCon.execute(impQuery, Statement.RETURN_GENERATED_KEYS);
                    }
                }
            }
        } catch (Exception ex) {
            logger.debug("Datalayer: error while updating impact. " + ex.getMessage(), ex);
            throw new RuntimeException("Datalayer: error while updating impact. " + ex.getMessage(), ex);
        }
    }
      
    public static void deleteImpacts(int idrisk, DatabaseConnector dbCon) {
        try {

            logger.debug("deleting impact entry of risk id " + idrisk);
            //System.out.println("deleting impact entry of risk id " + idrisk);

            String query = "Delete from Impact "
                    + "Where `riskop_idriskop`=" + idrisk;

            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting impacts of risk id: " + idrisk + ". " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while deleting impacts of risk id: " + idrisk + ". " + exc.getMessage(), exc);
        }
    }
    
    public static int getObjectiveIDByUUID(String uuid, DatabaseConnector dbCon) {
        String query = "SELECT objective.idobjective FROM objective WHERE objective.uuid='" + uuid + "'";
        int idObjective = -1;

        try {
            ResultSet rs = dbCon.executeQuery(query);

            if (rs.next()) {
                idObjective = rs.getInt(1);
            } else {
                // throw an exception from here
                logger.debug("DataLayer: no index returned for objective " + uuid);
                throw new RuntimeException("DataLayer: no index returned for objective " + uuid);
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting objective DB id. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting objective DB id. " + exc.getMessage());

        }

        return idObjective;
    }
    
     public static boolean impactExists(int objid, int idrisk, DatabaseConnector dbCon) {
        if (idrisk < 1 || objid < 1) {
            throw new IllegalArgumentException("objective or risk id are non valid!");
        }

        try {
            String query = "SELECT * from impact Where riskop_idriskop=" + idrisk + " and objective_idobjective= " + objid;
            ResultSet rs = dbCon.executeQuery(query);
            if (rs.next()) {
                //get impact if exists
                int idriskop = rs.getInt("riskop_idriskop");

                if (idriskop > 0) {
                    return true;
                }
            }

            return false;

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting impact id By objective and risk IDs. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while getting impact id By objective and risk IDs. " + exc.getMessage(), exc);
        }
    }
     
    public static void deleteImpactEntry(int objid, int idrisk, DatabaseConnector dbCon) {
        try {

            logger.debug("deleting impact entry on objective id " + objid);
            //System.out.println("deleting impact entry on objective id " + objid);

            String query = "Delete from Impact "
                    + "Where `objective_idobjective`=" + objid
                    + " and "
                    + "`riskop_idriskop`=" + idrisk;

            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting impact entry with risk id: " + idrisk + ", objective id: " + objid + ". " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while deleting impact entry with risk id: " + idrisk + ", objective id: " + objid + ". " + exc.getMessage(), exc);
        }
    }
    
    public static int findRiskByUUID(String uuid, DatabaseConnector dbCon) {
        int idrisk = -1;

        try {
            //dbCon.connect();
            String query = "Select * from riskop where "
                    + "riskop.uuid='" + uuid + "'";
            ResultSet rs = dbCon.executeQuery(query);
            if (rs.next()) {
                idrisk = rs.getInt(1);
            }

            // move to the end of the result set
            rs.last();
            // get the row number of the last row which is also the row count
            int rowCount = rs.getRow();
            logger.debug("rowCount=" + rowCount);
            if (rowCount > 1) {
                throw new RuntimeException("Datalayer: more than one risk with these attributes found in DB. ");
            }

        } catch (Exception ex) {
            logger.debug("Datalayer: Error while finding risk by attributes. " + ex, ex);
            throw new RuntimeException("Datalayer: Error while finding risk by attributes. " + ex, ex);
        } 

        return idrisk;
    }
    
    public static TreatmentWFs getTreatementByRiskID(int riskid , DatabaseConnector dbCon) {

        TreatmentWFs treatWFs = new TreatmentWFs();
// TODO:REFACTOR TreatmentWF persistence (sorry!)
        try {        
            String query = "select * from treat_risk where "
                    + "treat_risk.riskop_idriskop='" + riskid + "' ";


            ResultSet rs = dbCon.executeQuery(query);
            while (rs.next()) {
                String uuidString = rs.getString("uuid");

                Float priority = rs.getFloat("priority"); 
                String strTitle = rs.getString("title");
                String strDesc = rs.getString("description");
                
                UUID id = UUID.fromString(uuidString);
                
                TreatmentTemplate tmpl=new TreatmentTemplate(strTitle, strDesc, id);

                treatWFs.addTreatmentTemplate(tmpl, priority); //addWorkflow(priority, id);
                logger.debug("Extracted treatments: ");
            }
        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting treatment. ", exc);
            throw new RuntimeException("Datalayer: Error while getting treatment. ", exc);
        } 

        return treatWFs;
    }
    
    public static Impact getImpactsByRiskID(int idrisk,DatabaseConnector dbCon ) {

        Impact impact = new Impact();
        Map<Objective, ImpactLevel> impactMap = new HashMap<Objective, ImpactLevel>();

        try {
//            dbCon.connect();          
            String query = "SELECT * from impact, objective Where impact.riskop_idriskop=" + idrisk
                    + " and "
                    + "objective.idobjective=impact.objective_idobjective";

            ResultSet rs = dbCon.executeQuery(query);


            while (rs.next()) {
                String impactValue = rs.getString("value");
                //  boolean ispositive = rs.getBoolean("ispositive");

                String objective_description = rs.getString("description");
                String uuid = rs.getString("uuid");
                String title = rs.getString("title");

                Objective obj = new Objective(title, objective_description, UUID.fromString(uuid));

                ImpactLevel impLevel = ImpactLevel.fromString(impactValue);
                // impLevel.setIsPositive(ispositive);

                impactMap.put(obj, impLevel);

                logger.debug("Extracted Impact: " + ImpactLevel.fromString(impactValue));
            }

            impact.setImpactMap(impactMap);

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting impact. ", exc);
            throw new RuntimeException("Datalayer: Error while getting impact. ", exc);
        } 

        return impact;
    }
    
    
     /**
     * check if set of objectives contain certain objective.
     */
    public static boolean objectivesContain(Set<Objective> obs, Objective obj) {
        for (Objective temp : obs) {
            if (temp.equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
     /**
     * given a community, return the DB index. this method assumes that the
     * community already exists. this method throws an exception if the
     * community wasnt found
     */
    public static int getCommunityIDByUUID(UUID communityUUID, DatabaseConnector dbCon) {
        //TODO shouldnt connect and close connection again

        String query = "SELECT community.idcommunity FROM community WHERE community.com_uuid='" + communityUUID.toString() + "'";
        int communityID = -1;

        try {
            dbCon.connect();
            ResultSet rs = dbCon.executeQuery(query);

            if (rs.next()) {
                communityID = rs.getInt(1);
            } else {
                // throw an exception from here
                throw new RuntimeException("DataLayer: no index returned for community " + communityUUID.toString());
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting community names. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting community names. " + exc.getMessage());

        } finally {
            dbCon.close();
        }

        return communityID;

    }
    
     public static Community getCommunityByUUID(UUID comUUID, DatabaseConnector dbCon) {
        Community com=null;
        
         try {
            
            String query = "SELECT * from community WHERE com_uuid='" + comUUID.toString() + "'";
            ResultSet rs = dbCon.executeQuery(query);
            com=extractCommunity(rs);
//            while (rs.next()) {
//                String communityName = rs.getString("com_name");
//                String strUuid = rs.getString("com_uuid");
//                String strUri = rs.getString("com_uri");
//                Boolean isStream = rs.getBoolean("com_isStream");
//                UUID uuid = UUID.fromString(strUuid);
//                URI uri = new URI(strUri);
//                String strId = rs.getString("com_id");
//                String strStreamName = rs.getString("com_streamName");
//                
//                com =new Community(communityName, uuid, strId, strStreamName,uri, isStream);// new Community(communityName, uuid, uri, isStream);          
//                logger.debug("Extracted: " + rs.getLong("idcommunity") + ": " + communityName);
//            }


        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting communities. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting communities. " + exc.getMessage());

        } 
         
         return com;
    }
    
     //this gets the predictor event without its parameters
     //parameters are extracted separately along with the parameter instaces per risk
    public static Event getPredictorEventByID(int idevent, DatabaseConnector dbCon) {

        try {
            Event ev = null;
            String query = "SELECT * from event WHERE idevents=" + idevent;

            ResultSet rs = dbCon.executeQuery(query);

            if (rs.next()) {
                int idEvent = rs.getInt("idevents");
                String title = rs.getString("event_title");
                String desc = rs.getString("event_desc");
                String struuid = rs.getString("event_uuid");

                //config params
                //List<Parameter> configParams = getParams(idEvent, false, dbCon);

                //conditions
                List<EventCondition> evtConditions = getPredictorEvConditions(idEvent, dbCon);

                ev = new Event((struuid == null ? null : UUID.fromString(struuid)), title, desc, evtConditions, null);

            }
            return ev;
        } catch (Exception ex) {
            throw new RuntimeException("Datalayer: error while getting event by event id. " + ex.getMessage(), ex);
        }
    }
    
    
    
    
    public static List<EventCondition> getPredictorEvConditions(int idEvent, DatabaseConnector dbCon) {
        try {
            List<EventCondition> conds = new ArrayList<EventCondition>();

            String conditionQuery = "SELECT * from `" + dbName + "`.`condition` where event_idevents=" + idEvent;

            ResultSet rs = dbCon.executeQuery(conditionQuery);
            while (rs.next()) {
                int idcondition = rs.getInt("idcondition");
                String name = rs.getString("cond_name");
                String struuid = rs.getString("cond_uuid");
                UUID uuid = UUID.fromString(struuid);
                String desc = rs.getString("cond_desc");
                String unit = rs.getString("cond_unit");

                String strValuesAllowedType = rs.getString("values_allowed_type");
                ValuesAllowedType valueAllowedType = ValuesAllowedType.fromValue(strValuesAllowedType);

                String strValueType = rs.getString("cond_value_type");
                ParameterValueType valueType = ParameterValueType.fromValue(strValueType);

                List<ValueConstraint> cons = getConstraints(idcondition, false,dbCon);

                EventCondition cond = new EventCondition(uuid, valueType, name, desc, unit, valueAllowedType, cons);
                conds.add(cond);
            }

            if (conds.size() > 0) {
                return conds;
            }
            return null;
        } catch (Exception ex) {
            logger.debug("Datalayer: Error while getting conditions for idEvent=" + idEvent + ex.getMessage(), ex);
            throw new RuntimeException("Datalayer: Error while getting conditions for idEvent=" + idEvent + ex.getMessage(), ex);

        }
    }
    
    /**
     * this adds parameters and their parameter instance values. this is used in
     * save risk and not predictors. as predictor-event-parameter doesnt contain
     * value. however the fulleventparameter here contains value related to risk
     */
    public static void updateFullEventParameters(int riskid, int eventid, List<Parameter> configurationParams, DatabaseConnector dbCon) {

        if (configurationParams == null || configurationParams.isEmpty()) {
            return;
        }

        String paramQuery = null;

        for (Parameter param : configurationParams) {
            try {
                //this is in case eventParam,
                //check if the parameter already exists, then delete it and insert the new one
                int paramid = getParamIDFromUUID(param.getUUID(), dbCon);

                logger.debug("deleting existing param instances");
                deleteParamInstance(paramid, riskid, dbCon);
                logger.debug("inserting event param instance" + param.getName());
                System.out.println("inserting event param instance" + param.getName());
                logger.debug("inserting param instances to db");
                paramQuery = "INSERT INTO paraminstance (`paraminstance_value`,`paraminstance_eval_type`,`parameter_idparameter`, `riskop_idriskop`) "
                        + "VALUES("
                        + (param.getValue() == null || param.getValue().getValue() == null ? "NULL" : "'" + param.getValue().getValue() + "'")
                        + ", "
                        + (param.getValue() == null || param.getValue().getValueEvaluationType() == null ? "NULL" : "'" + param.getValue().getValueEvaluationType() + "'")
                        + ", "
                        + paramid
                        + ", "
                        + riskid
                        + ")";

//                paramQuery = "INSERT INTO parameter (`event_idevent`,`param_uuid` ,`param_name`, `param_desc`, `param_unit`, "
//                        + "`param_value_type`, `param_allowed_type`, `param_value`, `param_value_eval_type`, `forecast_period_param`) "
//                        + "VALUES (" + eventid + ",'" + param.getUUID().toString() + "','" + param.getName() + "','" + param.getDescription() + "','" + param.getUnit() + "','"
//                        + param.getType() + "','" + param.getValuesAllowedType() + "',"
//                        + (param.getValue() == null || param.getValue().getValue() == null ? "NULL" : "'" + param.getValue().getValue() + "'")
//                        + ","
//                        + (param.getValue() == null || param.getValue().getValueEvaluationType() == null ? "NULL" : "'" + param.getValue().getValueEvaluationType() + "'")
//                        + ","
//                        + false
//                        + ")";


                ResultSet rsParam = dbCon.execute(paramQuery, Statement.RETURN_GENERATED_KEYS);
                int idparam = -1;
                if (rsParam.next()) {
                    idparam = rsParam.getInt(1);
                }

//                for (ValueConstraint cons : param.getValueConstraints()) {
//                    //add constraint
//                    //get param id
//                    String consQuery = "INSERT INTO `" + dbName + "`.`constraint` (`cons_value`, `cons_type`, `parameter_idparameter`) "
//                            + "VALUES ('" + cons.getValue() + "', '" + cons.getConstraintType() + "'," + idparam + ")";
//                    dbCon.execute(consQuery, Statement.RETURN_GENERATED_KEYS);
//                }
            } catch (Exception e) {
                logger.debug("Datalayer: Error while updating parameter instance" + param.getName() + " with value "+param.getValue()+ e.getMessage(), e);
                throw new RuntimeException("Datalayer: Error while updating parameter instance" + param.getName()+ " with value "+param.getValue()+ e.getMessage(), e);

            }
        }
    }

    public static void addParameters(int id, List<Parameter> configurationParams, boolean isPredictorParam, boolean isForecastParam, DatabaseConnector dbCon) {

        if (configurationParams == null || configurationParams.isEmpty()) {
            return;
        }

        String paramQuery = null;

        for (Parameter param : configurationParams) {
            try {
                if (isForecastParam) {
                    logger.debug("inserting forecast param " + param.getName());
                    System.out.println("inserting forecast param " + param.getName());
                    paramQuery = "INSERT INTO parameter (`predictor_idpredict`, `event_idevent`,`param_uuid`, `param_name`, `param_desc`, `param_unit`, "
                            + "`param_value_type`, `param_allowed_type`, `param_value`, `param_value_eval_type`, `forecast_period_param`) "
                            + "VALUES (" + id + ",NULL,'" + param.getUUID().toString() + "','" + param.getName() + "','" + param.getDescription() + "','" + param.getUnit() + "','"
                            + param.getType() + "','" + param.getValuesAllowedType()
                            + "',"
                            + (param.getValue() == null ? "NULL" : ("'" + param.getValue().getValue() + "'"))
                            + ","
                            + (((param.getValue() == null)||(param.getValue().getValueEvaluationType() == null) )? "NULL" : ("'" + param.getValue().getValueEvaluationType() + "'"))
                            + ","
                            + isForecastParam
                            + ")";
                }

                if (isPredictorParam && !isForecastParam) {//this is in case predicotrparam,
                    logger.debug("inserting predictor config param " + param.getName());
                    System.out.println("inserting predictor config param " + param.getName());
                    paramQuery = "INSERT INTO parameter (`predictor_idpredict`, `event_idevent`,`param_uuid`, `param_name`, `param_desc`, `param_unit`, "
                            + "`param_value_type`, `param_allowed_type`, `param_value`, `param_value_eval_type`, `forecast_period_param`) "
                            + "VALUES (" + id + ",NULL,'" + param.getUUID().toString() + "','" + param.getName() + "','" + param.getDescription() + "','" + param.getUnit() + "','"
                            + param.getType() + "','" + param.getValuesAllowedType()
                            + "',"
                            + (param.getValue() == null ? "NULL" : ("'" + param.getValue().getValue() + "'"))
                            + ","
                            + (param.getValue() == null ? "NULL" : ("'" + param.getValue().getValueEvaluationType() + "'"))
                            + ","
                            + isForecastParam
                            + ")";
                }
                
                if (!isPredictorParam && !isForecastParam) {//this is in case eventParam,
                    //check if the parameter already exists, then delete it and insert the new one
                    deleteParam(param.getUUID(),dbCon);
                    logger.debug("inserting event param " + param.getName());
                    System.out.println("inserting event param " + param.getName());
                    paramQuery = "INSERT INTO parameter (`event_idevent`,`param_uuid` ,`param_name`, `param_desc`, `param_unit`, "
                            + "`param_value_type`, `param_allowed_type`, `param_value`, `param_value_eval_type`, `forecast_period_param`) "
                            + "VALUES (" + id + ",'" + param.getUUID().toString() + "','" + param.getName() + "','" + param.getDescription() + "','" + param.getUnit() + "','"
                            + param.getType() + "','" + param.getValuesAllowedType() + "',"
                            + (param.getValue() == null || param.getValue().getValue() == null ? "NULL" : "'" + param.getValue().getValue() + "'")
                            + ","
                            + (param.getValue() == null || param.getValue().getValueEvaluationType() == null ? "NULL" : "'" + param.getValue().getValueEvaluationType() + "'")
                            + ","
                            + false
                            + ")";
                }

                ResultSet rsParam = dbCon.execute(paramQuery, Statement.RETURN_GENERATED_KEYS);
                int idparam = -1;
                if (rsParam.next()) {
                    idparam = rsParam.getInt(1);
                }

                for (ValueConstraint cons : param.getValueConstraints()) {
                    //add constraint
                    //get param id
                    String consQuery = "INSERT INTO `" + dbName + "`.`constraint` (`cons_value`, `cons_type`, `parameter_idparameter`) "
                            + "VALUES ('" + cons.getValue() + "', '" + cons.getConstraintType() + "'," + idparam + ")";
                    dbCon.execute(consQuery, Statement.RETURN_GENERATED_KEYS);
                }
            } catch (Exception e) {
                logger.debug("Datalayer: Error while adding parameter " + param.getName() + e.getMessage(), e);
                throw new RuntimeException("Datalayer: Error while adding parameter " + param.getName() + e.getMessage(), e);

            }
        }
    }

     public static void addEvents(int idpred, List<Event> events, DatabaseConnector dbCon) {
        try {
            for (Event ev : events) {
                String eventQuery = "INSERT INTO event (`event_uuid`, `predictor_idpredictor`, `event_title`, `event_desc`) "
                        + "VALUES ('"
                        + ev.getUuid().toString()
                        + "', "
                        + idpred
                        + ", '"
                        + ev.getTitle()
                        + "', '"
                        + ev.getDescription()
                        + "')";
                ResultSet rsEvent = dbCon.execute(eventQuery, Statement.RETURN_GENERATED_KEYS);

                int idEvent = -1;
                if (rsEvent.next()) {
                    idEvent = rsEvent.getInt(1);
                }

                //add conditions
                addConditions(idEvent, ev.getEventConditions(), dbCon);
                
                //add config parameters!!
                addParameters(idEvent, ev.getConfigParams(), false, false, dbCon);

            }


        } catch (Exception exc) {
            logger.debug("Datalayer: Error while adding predictor events. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while adding predictor events. " + exc.getMessage(), exc);
        }

    }

    public static void addConditions(int idEvent, List<EventCondition> eventConditions, DatabaseConnector dbCon) {


        try {

            for (EventCondition cond : eventConditions) {
                String condQuery = "INSERT INTO `" + dbName + "`.`condition` (`event_idevents`,`cond_uuid`, `cond_value_type`, `cond_name`, `cond_desc`, `values_allowed_type`) "
                        + "VALUES ("
                        + idEvent
                        + ",'"
                        + cond.getUUID().toString()
                        + "','"
                        + cond.getType()
                        + "','"
                        + cond.getName()
                        + "','"
                        + cond.getDescription()
                        + "'"
                        //                        + (cond.getUnit()==null? "NULL": ("'"+cond.getUnit()+"'" ) )
                        + ",'"
                        + cond.getValuesAllowedType()
                        + "')"; //allowed set is not supported yet!

                //System.out.println(condQuery);

                //insert constraints
                ResultSet rsCond = dbCon.execute(condQuery, Statement.RETURN_GENERATED_KEYS);

                int idCond = -1;
                if (rsCond.next()) {
                    idCond = rsCond.getInt(1);
                }

                for (ValueConstraint cons : cond.getValueConstraints()) {
                    //add constraint
                    //get param id
                    String consQuery = "INSERT INTO `" + dbName + "`.`constraint` (`cons_value`, `cons_type`, `condition_idcondition`) "
                            + "VALUES ('" + cons.getValue() + "', '" + cons.getConstraintType() + "'," + idCond + ")";
                    dbCon.execute(consQuery, Statement.RETURN_GENERATED_KEYS);
                }
            }


        } catch (Exception exc) {
            logger.debug("Datalayer: Error while adding conditions to event. " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while adding conditions to event. " + exc.getMessage(), exc);
        }
    }

    /**
     * return null if the idcondition is not valid or event not found.
     */
//    public static Event getEventByConId(int idcondition, DatabaseConnector dbCon) {
//        if (idcondition < 1) {
//            return null;
//        }
//
//        try {
//            Event ev = null;
//            String query = "SELECT * from `" + dbName + "`.`condition` WHERE idcondition=" + idcondition;
//
//            ResultSet rs = dbCon.executeQuery(query);
//            int idevent = -1;
//            if (rs.next()) {
//                idevent = rs.getInt("event_idevents");
//            }
//
//            if (idevent > 0) {
//                ev = getPredictorEventByID(idevent,dbCon);
//            }
//
//            return ev;
//        } catch (Exception ex) {
//            throw new RuntimeException("Datalayer: error while getting event by condition id. " + ex.getMessage(), ex);
//        }
//    }

    /**
     * get the predictor event and fill in with the condition instance data 
     * we need the riskid because event may be connected to multiple risks via multiple conditioninstances
     */
    public static Event getFullEventById(int riskid, int eventID, DatabaseConnector dbCon ) {

        String precond_value = null;
        String strprecond_evaltype = null;
        String postcond_value = null;
        String strpostcond_evaltype = null;

        logger.debug("Extracting event ID "+eventID);
        
        //get the event from event table, this includes the event parameters
        Event event = DataLayerImplHelper.getPredictorEventByID(eventID, dbCon);//this is missing the instance UUID

        //TODO get the events parameters separately and fill in with their instance data, then set to the event
        List<Parameter> configParams=getEventParamsForRisk(riskid, eventID, dbCon);
        
        try {
            //get the event conditions by the event id
            String query = "SELECT * from `" + dbName + "`.`condition` WHERE event_idevents=" + eventID;

            ResultSet rs = dbCon.executeQuery(query);

            while (rs.next()) {//loop for conditions
                int condID = rs.getInt("idcondition");
                //get the event conditions objects by id
                EventCondition evCond =getEventconditionById(condID,dbCon);
                if (evCond != null) {
                    //get condition instances for the condition condID
                    logger.debug("Extracting condition ID "+condID);
                    String query2 = "SELECT * from conditioninstance Where condition_idcondition=" + condID + " AND riskop_idriskop="+riskid;

                    ResultSet rss = dbCon.executeQuery(query2);

                    while (rss.next()) {//loop for condition instance
                        precond_value = rss.getString("pre_value");
                        strprecond_evaltype = rss.getString("pre_val_eval_type");
                        EvaluationType precond_evaltype = (strprecond_evaltype == null ? null : EvaluationType.fromValue(strprecond_evaltype));

                        postcond_value = rss.getString("pos_value");
                        strpostcond_evaltype = rss.getString("pos_val_eval_type");
                        
                        EvaluationType poscond_evaltype =strpostcond_evaltype==null?null: EvaluationType.fromValue(strpostcond_evaltype);

                        //fit the condition instances in the evCond
                        evCond.setPreConditionValue(new ParameterValue(precond_value, precond_evaltype));
                        evCond.setPostConditionValue(new ParameterValue(postcond_value, poscond_evaltype));


                        //add the evCond to the event
                        //update the event with the instance/user choice values
                        updateEventPrePostValues(event, evCond, dbCon);

                        List<EventCondition> conds = event.getEventConditions();
                        for (EventCondition evcond : conds) {
                            logger.debug("event "+event.getTitle()+" now with event condition pre and post values:"
                                    + (evcond.getPreConditionValue() == null ? "null" : evcond.getPreConditionValue().getValue())
                                    + " , "
                                    + (evcond.getPostConditionValue() == null ? "null" : evcond.getPostConditionValue().getValue()));

                        }
                    }


                }

            }

            event.setConfigParams(configParams);
            return event;
        } catch (Exception ex) {
            throw new RuntimeException("Datalayer: error while getting full event by event id. " + ex.getMessage(), ex);
        }

    }
    
    /**
     * this gets the params for a risk including the param instance values
     */
    public static List<Parameter> getEventParamsForRisk(int riskid, int eventID, DatabaseConnector dbCon) {

        try {
            List<Parameter> params = new ArrayList<Parameter>();
            //get param attributes given the id
            String paramquery = "SELECT * from parameter,paraminstance WHERE "
                    + "parameter.event_idevent=" + eventID + ""
                    + " AND parameter.forecast_period_param=false"
                    + " AND parameter.idparameter=paraminstance.parameter_idparameter"
                    + " AND paraminstance.riskop_idriskop=" + riskid;

            ResultSet rs = dbCon.executeQuery(paramquery);
            while (rs.next()) {
                int idparameter = rs.getInt("idparameter");
                String name = rs.getString("param_name");
                String param_uuid = rs.getString("param_uuid");
                UUID uuid = UUID.fromString(param_uuid);

                String description = rs.getString("param_desc");
                String unit = rs.getString("param_unit");
                String strtype = rs.getString("param_value_type");
                ParameterValueType type = null;
                if (strtype != null && !strtype.equals("")) {
                    type = ParameterValueType.fromValue(strtype);
                }

                String strvalue = rs.getString("paraminstance_value");
                String strvalEvalType = rs.getString("paraminstance_eval_type");
                EvaluationType valEvalType = null;

                if (strvalEvalType != null && !strvalEvalType.equals("")) {
                    valEvalType = EvaluationType.fromValue(strvalEvalType);
                }

                ParameterValue paramValue = null;

                if (strvalue != null) {
                    paramValue = new ParameterValue(strvalue, valEvalType);
                }

                String strValuesAllowedType = rs.getString("param_allowed_type");
                ValuesAllowedType valuesAllowedType = ValuesAllowedType.valueOf(strValuesAllowedType);

                //get constraints
                List<ValueConstraint> cons = getConstraints(idparameter, true, dbCon);

                Parameter param = new Parameter(uuid, type, name, description, unit, paramValue, valuesAllowedType, cons);


                params.add(param);

            }

            if (params.size() > 0) {
                return params;
            }
            return null;
        } catch (Exception ex) {
            logger.debug("Datalayer: Error while getting params for riskid=" + riskid + " eventID=" + eventID + ex.getMessage(), ex);
            throw new RuntimeException("Datalayer: Error while getting params for riskid=" + riskid + " eventID=" + eventID + ex.getMessage(), ex);

        }
    }
    /**
     * this returns the parameters given a predictor or event id it checks that
     * the parameter is not a forecast period param
     */
       public static  List<Parameter> getParams(int id, boolean isPredicParam, DatabaseConnector dbCon ) {
        try {
            List<Parameter> params = new ArrayList<Parameter>();
            //get param attributes given the id
            String paramquery = "SELECT * from parameter WHERE " + (isPredicParam ? ("predictor_idpredict=" + id) : ("event_idevent=" + id))+ " AND forecast_period_param=false";

            ResultSet rs = dbCon.executeQuery(paramquery);
            while (rs.next()) {
//                int idparameter = rs.getInt("idparameter");
//                String name = rs.getString("param_name");
//                String param_uuid = rs.getString("param_uuid");
//                UUID uuid = UUID.fromString(param_uuid);
//
//                String description = rs.getString("param_desc");
//                String unit = rs.getString("param_unit");
//                String strtype = rs.getString("param_value_type");
//                ParameterValueType type = null;
//                if(strtype!=null && !strtype.equals(""))
//                    type=ParameterValueType.fromValue(strtype);
//
//                String strvalue = rs.getString("param_value");
//                String strvalEvalType = rs.getString("param_value_eval_type");
//                EvaluationType valEvalType=null;
//                
//                if(strvalEvalType!=null && !strvalEvalType.equals(""))
//                        valEvalType = EvaluationType.fromValue(strvalEvalType);
//                
//                ParameterValue paramValue = null;
//                
//                if(strvalue!=null && valEvalType!=null)
//                    paramValue=new ParameterValue(strvalue, valEvalType);
//
//                String strValuesAllowedType = rs.getString("param_allowed_type");
//                ValuesAllowedType valuesAllowedType = ValuesAllowedType.valueOf(strValuesAllowedType);
//
//                //get constraints
//                List<ValueConstraint> cons = getConstraints(idparameter, true,dbCon);

                Parameter param = resultSetToParam(rs, dbCon);
                params.add(param);

            }

            if (params.size() > 0) {
                return params;
            }
            return null;
        } catch (Exception ex) {
            logger.debug("Datalayer: Error while getting params for id=" + id + ex.getMessage(), ex);
            throw new RuntimeException("Datalayer: Error while getting params for id=" + id + ex.getMessage(), ex);

        }
    }
       /**

     * this takes in a results set of paramaters and return a parameter
     */
    private static Parameter resultSetToParam(ResultSet rs,  DatabaseConnector dbCon) {
        try {
            int idparameter = rs.getInt("idparameter");
            String name = rs.getString("param_name");
            String param_uuid = rs.getString("param_uuid");
            UUID uuid = UUID.fromString(param_uuid);

            String description = rs.getString("param_desc");
            String unit = rs.getString("param_unit");
            String strtype = rs.getString("param_value_type");
            ParameterValueType type = null;
            if (strtype != null && !strtype.equals("")) {
                type = ParameterValueType.fromValue(strtype);
            }

            String strvalue = rs.getString("param_value");
            String strvalEvalType = rs.getString("param_value_eval_type");
            EvaluationType valEvalType = null;

            if (strvalEvalType != null && !strvalEvalType.equals("")) {
                valEvalType = EvaluationType.fromValue(strvalEvalType);
            }

            ParameterValue paramValue = null;

            if (strvalue != null /*&& valEvalType != null*/) {
                paramValue = new ParameterValue(strvalue, valEvalType);
            }

            String strValuesAllowedType = rs.getString("param_allowed_type");
            ValuesAllowedType valuesAllowedType = ValuesAllowedType.valueOf(strValuesAllowedType);

            //get constraints
            List<ValueConstraint> cons = getConstraints(idparameter, true, dbCon);

            Parameter param = new Parameter(uuid, type, name, description, unit, paramValue, valuesAllowedType, cons);

            return param;
        } catch (Exception ex) {
             logger.debug("Datalayer: Error while extracting params for resultset" + ex.getMessage(), ex);
            throw new RuntimeException("Datalayer: Error while extracting params for resultset" + ex.getMessage(), ex);
        }
    }

    public static List<ValueConstraint> getConstraints(int id, boolean isParamConst, DatabaseConnector dbCon ) {
        try {
            List<ValueConstraint> cons = new ArrayList<ValueConstraint>();
            //get param attributes given the id
            String constraintQuery = "SELECT * from `constraint` WHERE " + (isParamConst ? ("parameter_idparameter=" + id) : ("condition_idcondition=" + id));
            //System.out.println(constraintQuery);
            ResultSet rs = dbCon.executeQuery(constraintQuery);
            while (rs.next()) {

                String value = rs.getString("cons_value");
                String strtype = rs.getString("cons_type");
                ValueConstraintType type = ValueConstraintType.valueOf(strtype);

                ValueConstraint constraint = new ValueConstraint(value, type);
                cons.add(constraint);
            }

            if (cons.size() > 0) {
                return cons;
            }
            return null;
        } catch (Exception ex) {
            logger.debug("Datalayer: Error while getting constraints for id=" + id + ex.getMessage(), ex);
            throw new RuntimeException("Datalayer: Error while getting constraints for id=" + id + ex.getMessage(), ex);

        }

    }

       public static  List<Event> getPredictorEvents(int idpredic, DatabaseConnector dbCon ) {

        try {
            List<Event> events = new ArrayList<Event>();
            //get param attributes given the id
            String eventsQuery = "SELECT * from event WHERE predictor_idpredictor=" + idpredic;

            ResultSet rs = dbCon.executeQuery(eventsQuery);
            while (rs.next()) {
                int idEvent = rs.getInt("idevents");
                String strUUID = rs.getString("event_uuid");
                UUID uuid = UUID.fromString(strUUID);
                String title = rs.getString("event_title");
                String desc = rs.getString("event_desc");

                //config params
                List<Parameter> configParams = getParams(idEvent, false,dbCon);

                //conditions
                List<EventCondition> evtConditions = getPredictorEvConditions(idEvent, dbCon);

                Event ev = new Event(uuid, title, desc, evtConditions, configParams);

                events.add(ev);
            }

            if (events.size() > 0) {
                return events;
            }
            return null;
        } catch (Exception ex) {
            logger.debug("Datalayer: Error while getting events for idpredic=" + idpredic + ex.getMessage(), ex);
            throw new RuntimeException("Datalayer: Error while getting events for idpredic=" + idpredic + ex.getMessage(), ex);

        }
    }

    public static EventCondition getEventconditionById(int idcondition, DatabaseConnector dbCon ) {
        try {
            EventCondition cond = null;

            String conditionQuery = "SELECT * from `" + dbName + "`.`condition` where idcondition=" + idcondition;

            ResultSet rs = dbCon.executeQuery(conditionQuery);
            while (rs.next()) {
                String name = rs.getString("cond_name");
                String struuid = rs.getString("cond_uuid");
                UUID uuid = UUID.fromString(struuid);
                String desc = rs.getString("cond_desc");
                String unit = rs.getString("cond_unit");

                String strValuesAllowedType = rs.getString("values_allowed_type");
                ValuesAllowedType valueAllowedType = ValuesAllowedType.fromValue(strValuesAllowedType);

                String strValueType = rs.getString("cond_value_type");
                ParameterValueType valueType = ParameterValueType.fromValue(strValueType);

                List<ValueConstraint> cons = getConstraints(idcondition, false,dbCon);

                cond = new EventCondition(uuid, valueType, name, desc, unit, valueAllowedType, cons);

            }

            return cond;
        } catch (Exception ex) {
            logger.debug("Datalayer: Error while getting condition of idcondition=" + idcondition + ex.getMessage(), ex);
            throw new RuntimeException("Datalayer: Error while getting condition idcondition=" + idcondition + ex.getMessage(), ex);

        }

    }
    
    
    
    //copying the pre and post instance information to the event object
    public static void updateEventPrePostValues(Event ev, EventCondition newEvCondition, DatabaseConnector dbCon) {
        try {
            if (ev == null || newEvCondition == null) {
                return;
            }

            List<EventCondition> conds = ev.getEventConditions();

            if (conds != null) {
                for (EventCondition evcond : conds) {
//                if (evcond.equalsConditionValues(newEvCondition)) {
                    if (evcond.getUUID().equals(newEvCondition.getUUID())) {
                        evcond.setPreConditionValue(newEvCondition.getPreConditionValue());
                        evcond.setPostConditionValue(newEvCondition.getPostConditionValue());
                    }
                }
                conds = ev.getEventConditions();
                for (EventCondition evcond : conds) {
                    logger.debug("updated the event with event condition pre and post values:"
                            + (evcond.getPreConditionValue() == null ? "null" : evcond.getPreConditionValue().getValue())
                            + " , "
                            + (evcond.getPostConditionValue() == null ? "null" : evcond.getPostConditionValue().getValue()));

                }

            }
        } catch (Exception ex) {
            throw new RuntimeException("error updating the ev condition values", ex);

        }

    }

    public static void getCondIDForPredicEvent(int idPredic, Event event, int idrisk, DatabaseConnector dbCon) {
        //get all events for idpredic and compare their conditions to event conditions
        //return the id of the matched condition

        try {
            for (EventCondition evCond : event.getEventConditions()) {

                String query = "SELECT condition.idcondition from event, condition WHERE event.predictor_idpredictor=" + idPredic
                        + " AND condition.events_idevents=event.idevents"
                        + " AND condition.cond_uuid='" + evCond.getUUID().toString() + "'"
                        + " AND condition.cond_name=" + evCond.getName()
                        + " AND condition.cond_value_type =" + evCond.getType().toString()
                        + " AND condition.values_allowed_type =" + evCond.getValuesAllowedType();

                ResultSet rs = dbCon.executeQuery(query);
                if (rs.next()) {
                    int idcondition = rs.getInt(1);
                    //find if there an instance already connected to it for that specific risk
                    if (idcondition > 0) {
                        String instanceQuery = "SELECT * from conditioninstance WHERE condition_idcondition=" + idcondition + " AND riskop_idriskop=" + idrisk;
                        ResultSet rsInstance = dbCon.executeQuery(instanceQuery);
                        if (rsInstance.next()) {
                            //there is existing instance, update it
                            //get instance id
                            int idinstance = rsInstance.getInt("idconditioninstance");

                            String updateQuery = "UPDATE `" + dbName + "`.`conditioninstance` SET "
                                    + "`pre_value`='"
                                    + evCond.getPreConditionValue().getValue()
                                    + "', "
                                    + "`pre_val_eval_type`='"
                                    + evCond.getPreConditionValue().getValueEvaluationType().toString()
                                    + "', "
                                    + "`pos_value`='"
                                    + evCond.getPostConditionValue().getValue()
                                    + "', "
                                    + "`pos_val_eval_type`='"
                                    + evCond.getPostConditionValue().getValueEvaluationType().toString()
                                    + "' "
                                    + "WHERE `idconditioninstance`='"
                                    + idinstance
                                    + "'";

                            dbCon.executeQuery(updateQuery);


                        } else {
                            //there is no instance, insert one
                            String inserQuery = "INSERT INTO `" + dbName + "`.`conditioninstance` "
                                    + "( `pre_value`, `pre_val_eval_type`, `pos_value`, `pos_val_eval_type`, `riskop_idriskop`, `condition_idcondition`) "
                                    + "VALUES ("
                                    + "'"
                                    + evCond.getPreConditionValue().getValue()
                                    + "', '"
                                    + evCond.getPreConditionValue().getValueEvaluationType().toString()
                                    + "', '"
                                    + evCond.getPostConditionValue().getValue()
                                    + "', '"
                                    + evCond.getPostConditionValue().getValueEvaluationType().toString()
                                    + "', "
                                    + idrisk
                                    + ", "
                                    + idcondition
                                    + ")";

                            dbCon.execute(inserQuery, Statement.RETURN_GENERATED_KEYS);
                        }

                    }

                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Dtaalayer: error while getting Cond ID For Predic Event. " + ex.getMessage(), ex);

        }
    }
    
    
    
      /**
     * creates or updates event according to the event id provided. returns the
     * id of the updated/created event. if the new event is null, the method just
     * returns. it doesnt delete any event from the DB. There must always an
     * event to update the DB.
     */
    public static  void updateEvent(Risk risk, int idrisk, DatabaseConnector dbCon/*
             * , PredictorServiceDescription predic
             */) {
        
        try {
            //if there are events already, delete the existing condition instances 
            Set<Event> existingEvents = getEventByRiskID(idrisk, dbCon);

            if (!existingEvents.isEmpty()) {
                //Here there are existing events. 
                //need to remove the existing conditioninstances and add the new ones
                //the existing condition instances may be linked to an old event. The new ones then need to be linked to the new event
                for (Event existingEvent : existingEvents) {                    
                    //get the existing condition instance
                    List<EventCondition> existingConditions = existingEvent.getEventConditions();
                    //delete condition instances related to the specific risk id and these conditions
                    for (EventCondition existingCondition : existingConditions) {
                        int existingCondID = DataLayerImplHelper.findConditionID(existingCondition, dbCon);
                        if (existingCondID > 0) {
                            //delete the condition instance related to the specific condition and risk
                            String delQuery = "DELETE from conditioninstance WHERE condition_idcondition=" + existingCondID + " AND riskop_idriskop=" + idrisk;
                            logger.debug(delQuery);
                            dbCon.execute(delQuery, Statement.RETURN_GENERATED_KEYS);
                        }
                    }
                }
            }

            //now there are no links to condition instances (thus no links to events)
            //add the new condition instances and link to the events

            for (Event newEvent : risk.getSetEvent()) {
                for (EventCondition evCond : newEvent.getEventConditions()) {
                    //there is no condition instance, insert one
                    //find the condition id for this specific event and condition uuid
                    String query = "SELECT `condition`.idcondition from `event`, `condition` "
                            + "WHERE event.event_uuid='" + newEvent.getUuid().toString() + "'"
                            + " AND `condition`.event_idevents=event.idevents"
                            + " AND `condition`.cond_uuid='" + evCond.getUUID().toString() + "'";
                    logger.debug(query);
                    ResultSet rs = dbCon.executeQuery(query);
                    if (rs.next()) {
                        int idExistingCond = rs.getInt(1);

                        String inserQuery = "INSERT INTO `conditioninstance` "
                                + "( `pre_value`, `pre_val_eval_type`, `pos_value`, `pos_val_eval_type`, `riskop_idriskop`, `condition_idcondition`) "
                                + "VALUES ("
                                + (evCond.getPreConditionValue() == null || evCond.getPreConditionValue().getValue()==null ? "NULL" : ("'" + evCond.getPreConditionValue().getValue() + "'"))
                                //                                        + evCond.getPreConditionValue().getValue() //check for nulls
                                + ", "
                                + (evCond.getPreConditionValue() == null || evCond.getPreConditionValue().getValueEvaluationType()==null? "NULL" : ("'" + evCond.getPreConditionValue().getValueEvaluationType().toString() + "'"))
                                //                                        + evCond.getPreConditionValue().getValueEvaluationType().toString()//check for nulls
                                + ", "
                                + (evCond.getPostConditionValue()==null ||evCond.getPostConditionValue().getValue()==null?"NULL" : ("'"+evCond.getPostConditionValue().getValue()+"'"))//check for nulls
                                + ", "
                                + ((evCond.getPostConditionValue()==null || evCond.getPostConditionValue().getValueEvaluationType()==null)?"NULL" : ("'"+ evCond.getPostConditionValue().getValueEvaluationType().toString()+ "'"))
                                + ", "
                                + idrisk
                                + ", "
                                + idExistingCond
                              //  +", "
                               // +  (newEvent.getInstanceUUID()== null ? "NULL" : "'"+newEvent.getInstanceUUID()+"'")
                                + ")";

                        dbCon.execute(inserQuery, Statement.RETURN_GENERATED_KEYS);
                    }
                }
                
                //add or update event parameters
                int eventID=getEventId(newEvent, dbCon);
                
                //update the parameter instances according to the new values
                //including deleting the existing ones and replacing them by the new ones
                logger.debug("updating full event parameters for risk "+risk.getTitle());
                updateFullEventParameters(idrisk,eventID, newEvent.getConfigParams(), dbCon);
                //this is not good because the update event method is used for creating events too, not only updating them
//                updateEventParameters(eventID, newEvent.getConfigParams(), dbCon);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Datalayer: error while updating risk event with the condition instance. " + ex.getMessage(), ex);

        }

    }

    public static void updateTreatments(TreatmentWFs newTreats, int idrisk, TreatmentWFs existingTreats, DatabaseConnector dbCon) {

//TODO:REFACTOR TreatmentWF persistence (sorry!)
        try {
            //check if theupdate is about deleting the existing treats
            if ((newTreats == null || newTreats.getOrderedCopyOfTreatmentTemplates().isEmpty()) && existingTreats != null) {
                deleteTreatments(existingTreats, idrisk,dbCon);
            }

            if (newTreats != null && newTreats.getOrderedCopyOfTreatmentTemplates() != null && !newTreats.getOrderedCopyOfTreatmentTemplates().isEmpty()) {

                //find the treats that are in the DB but not in the new set.
                //delete the difference from the DB
                for (Map.Entry<Float, TreatmentTemplate> wf : existingTreats.getOrderedCopyOfTreatmentTemplates()) {
                    if (!newTreats.containUUID(wf.getValue().getID())) {
                        deleteWF(wf, idrisk,dbCon);
                    }
                }

                //update treats 
                for (Map.Entry<Float, TreatmentTemplate> newTreat : newTreats.getOrderedCopyOfTreatmentTemplates()) {//some treats may be newly added, others changed priorities 
                    //get newTreat id if exists
                    boolean newTreatExist = treatExists(newTreat, idrisk,dbCon);
                    if (newTreatExist) {
                        //update treatment prioirity with the newTreat
                        String treatQuery = "UPDATE treat_risk SET "
                                + "`priority`='" + newTreat.getKey() + "' "
                                + "WHERE `riskop_idriskop`=" + idrisk
                                + " and `uuid`='" + newTreat.getValue().getID().toString() + "'";
                        dbCon.execute(treatQuery, Statement.RETURN_GENERATED_KEYS);

                    } else {
                        //add new treatment
                        String treatQuery = "INSERT INTO treat_risk (`riskop_idriskop`, `priority`, `uuid`, `title`, `description`) "
                                + "VALUES ("
                                + idrisk + ", "
                                + newTreat.getKey()
                                + ",'" + newTreat.getValue().getID().toString() + "'"
                                + ",'" + newTreat.getValue().getTitle() + "'"
                                 + ",'" + newTreat.getValue().getDescription() + "'"
                                + ")";

                        dbCon.execute(treatQuery, Statement.RETURN_GENERATED_KEYS);
                    }

                }
            }
        } catch (Exception ex) {
            logger.debug("DataLayer: Error while updating Treatments," + ex.getMessage(), ex);
            throw new RuntimeException("DataLayer: Error while updating Treatments," + ex.getMessage(), ex);
        }
    }



 public static int getPredictorIDByUUID(String predicuuid,DatabaseConnector dbCon) {
        String query = "SELECT predictor.idpredictor FROM predictor WHERE predic_uuid='" + predicuuid + "'";
        int idPredictor = -1;

        try {
            ResultSet rs = dbCon.executeQuery(query);

            if (rs.next()) {
                idPredictor = rs.getInt(1);
            } else {
                // throw an exception from here
                logger.debug("DataLayer: no index returned for predictor uuid= " + predicuuid);
                throw new RuntimeException("DataLayer: no index returned for predictor uuid= " + predicuuid);
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting  predictor. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting  predictor. " + exc.getMessage(), exc);

        }

        return idPredictor;
    }

 
 
    /**
     * given a UUID find the treatment db id in treatments table throws
     * exception is the treatment with such uuid doesnt exist in the DB
     */
    public static int getTreatIDbyUUID(UUID value,DatabaseConnector dbCon) {
        int idTreatment = -1;

        if ((value == null) || (value.equals(""))) {
            throw new RuntimeException("UUID value is null or empty");
        }

        try {
//            dbCon.connect();          
            String query = "SELECT * from treat_risk Where uuid='" + value.toString() + "'";

            ResultSet rs = dbCon.executeQuery(query);


            if (rs.next()) {
                idTreatment = rs.getInt(1);
                logger.debug("Extracted: treatment id" + idTreatment + "for uuid: " + value);
            } else {
                logger.debug("treatment with uuid " + value + " doesnt exist in the DB. Please save treatment first.");
                throw new RuntimeException("treatment with uuid " + value + " doesnt exist in the DB. Please save treatment first.");
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting treatment ID. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting treatment ID. " + exc.getMessage());
        }

        return idTreatment;
    }

    /**
     * return FULL event including condition instances given risk id, throw exception if not found
     * algo: risk id -> condition instances
     * for each condition instance get the condition id ->event id
     * for each event id, get the full event including the condition instances
     */
    public static Set<Event> getEventByRiskID(int idrisk,DatabaseConnector dbCon) {

        if (idrisk < 1) {
            return null;
        }

        //int idcondition = -1;
//        String precond_value = null;
//        String strprecond_evaltype = null;
//        String postcond_value = null;
//        String strpostcond_evaltype = null;
        Set<Event> setEvent = new HashSet<Event>();

        try {

            String query = "SELECT * from conditioninstance Where riskop_idriskop=" + idrisk + "";

            ResultSet rs = dbCon.executeQuery(query);

            //get the event ids 
            //Set<Integer> conditionsIds = new HashSet<Integer>();
            Set<Integer> eventsIds = new HashSet<Integer>();

            while (rs.next()) {
                int idcondition = rs.getInt("condition_idcondition");
                // conditionsIds.add(idcondition);
                // for (int id : conditionsIds) {
                String query2 = "SELECT * from `" + dbName + "`.`condition` WHERE idcondition=" + idcondition;

                ResultSet rss = dbCon.executeQuery(query2);
                int idevent = -1;
                if (rss.next()) {
                    idevent = rss.getInt("event_idevents");
                    eventsIds.add(idevent);//the idea here is that there are no duplicates (because multiple conditions connected to one event)
                }
                // }
            }

            //for each event get all content including the condition and condition instances
            for (int eventid : eventsIds) {
                Event ev = DataLayerImplHelper.getFullEventById(idrisk, eventid,dbCon);
                setEvent.add(ev);
                logger.debug("Extracted: event of id" + eventid);
            }

            return setEvent;

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting event by Risk ID. " + exc.getMessage(),exc);
            throw new RuntimeException("Datalayer: Error while getting event by Risk ID. " + exc.getMessage(),exc);
        }

    }

    /**
     * return predictor given id throw exception if not found
     */
    public static PredictorServiceDescription getPredictorByID(int predicid,DatabaseConnector dbCon) {

        if (predicid < 1) {
            return null;
        }

        PredictorServiceDescription predic = null;
        try {

            String query = "SELECT * from predictor Where idpredictor=" + predicid + "";

            ResultSet rs = dbCon.executeQuery(query);

            if (rs.next()) {
                //get predictor if exists

                String predicName = rs.getString("predic_name");
                String predicVersion = rs.getString("predic_version");
                String predicuuid = rs.getString("predic_uuid");
                String predic_desc = rs.getString("predic_desc");
                String predic_uri = rs.getString("predic_uri");
                String predic_svctargetnamespace = rs.getString("predic_svctargetnamespace");
                String predic_svcname = rs.getString("predic_svcname");
                String predic_portname = rs.getString("predic_portname");


                predic = new PredictorServiceDescription(UUID.fromString(predicuuid), predicName, predicVersion, predic_desc);
//                predic.preConfigure(predicName);//TODO: remove this when all info is stored in DB
                predic.setServiceName(predic_svcname);
                predic.setServiceTargetNamespace(predic_svctargetnamespace);
                predic.setServicePortName(predic_portname);
                predic.setServiceURI(predic_uri == null ? null : new URI(predic_uri));

                logger.debug("Extracted: predictor with id" + predicid);
            } else {
                logger.debug("Datalayer: couldnt find predictor with id " + predicid);
                throw new RuntimeException("Datalayer: couldnt find predictor with id " + predicid);
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting PredictorByID. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting PredictorByID. " + exc.getMessage());
        }

        return predic;
    }

    /**
     * return the id if found or -1 if not
     */
    public static int getPredIDByEventID(int idevent,DatabaseConnector dbCon) {
        if (idevent < 1) {
            return -1;
        }

        try {
            String query = "SELECT * from event Where idevents=" + idevent;
            ResultSet rs = dbCon.executeQuery(query);
            if (rs.next()) {
                //get predictor if exists
                int idpredic = rs.getInt("predictor_idpredictor");

                logger.debug("Extracted: predictor with id " + idpredic + " linked to " + idevent);
                return idpredic;
            } else {
                return -1;
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting Predictor id By event ID. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting Predictor id By event ID. " + exc.getMessage());
        }
    }

    
    /**
     * returns 1 if found, 0 if not found
     */
    public static boolean treatExists(Map.Entry<Float, TreatmentTemplate> newTreat, int idrisk,DatabaseConnector dbCon) {

//      return true; // Do not 'fail' tests
      
//TODO:REFACTOR TreatmentWF persistence (sorry!)      
        int idTreatmentRisk = -1;

        if (newTreat == null) {
            throw new IllegalArgumentException("New Treatment shouldnot be null");
        }

        if ((newTreat.getValue() == null) || (newTreat.getValue().equals(""))) {
            throw new RuntimeException("UUID value is null or empty");
        }

        try {
            String query = "SELECT * from treat_risk Where "
                    + "`uuid`='" + newTreat.getValue().getID().toString() + "'"
                    + " and `riskop_idriskop`=" + idrisk;
System.out.println(query);
            ResultSet rs = dbCon.executeQuery(query);


            if (rs.next()) {
                idTreatmentRisk = rs.getInt("riskop_idriskop");
                logger.debug("Datalayer: Extracted treatment for risk id=" + idTreatmentRisk);
                if (idTreatmentRisk > 0) {
                    return true;
                }
            }

            return false;

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting treatment ID. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting treatment ID. " + exc.getMessage());
        }
    }

    public static void deleteTreatments(TreatmentWFs existingTreats, int idrisk,DatabaseConnector dbCon) {
//TODO:REFACTOR TreatmentWF persistence (sorry!)  
        for (Map.Entry<Float, TreatmentTemplate> wf : existingTreats.getOrderedCopyOfTreatmentTemplates()) {
            deleteWF(wf, idrisk,dbCon);
        }
    }

    public static void deleteWF(Map.Entry<Float, TreatmentTemplate> wf, int idrisk,DatabaseConnector dbCon) {
        try {

            logger.debug("deleting workflow " + wf.getValue());
            //System.out.println("deleting workflow " + wf.getValue());

            String query = "Delete from treat_risk "
                    + "Where `uuid`='" + wf.getValue().toString() + "'"
                    + " and "
                    + "`riskop_idriskop`=" + idrisk;

            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting workflow treatment UUID: " + wf.getValue() + ", of risk id " + idrisk + ". " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while deleting workflow treatment UUID: " + wf.getValue() + ", of risk id " + idrisk + ". " + exc.getMessage(), exc);
        }
    }

    public static ArrayList<String> getProcIds(int idrisk, DatabaseConnector dbCon) {

        ArrayList<String> procIds = new ArrayList<String>();

        try {

            String query = "SELECT * from procid Where procid.riskop_idriskop=" + idrisk;


            ResultSet rs = dbCon.executeQuery(query);


            while (rs.next()) {
                String idValue = rs.getString("procid");
                procIds.add(idValue);
            }


        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting treatment proccesses Ids. ", exc);
            throw new RuntimeException("Datalayer: Error while getting treatment proccesses Ids. ", exc);
        }

        if(procIds.isEmpty())
            return null;
        
        return procIds;
    }

    static void updateProcIDs(ArrayList<String> treatProcIDS, int idrisk, DatabaseConnector dbCon) {
        //rollback if things go wrong!!!
        //this is done in the update risk method already. no need to do it here
        
        //delete existing proc IDs 
        try {
            logger.debug("delete existing procIDs");
            String query = "Delete from procid "
                    + "Where `riskop_idriskop`=" + idrisk;
            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

        } catch (Exception ex) {
            logger.debug("Datalayer: Error while getting existing treatment process Ids ", ex);
            throw new RuntimeException("Datalayer: Error while getting existing treatment process Ids" + ex.getMessage(), ex);
        }


        //insert the new ones
        if (treatProcIDS != null && !treatProcIDS.isEmpty()) {
            try {
                logger.debug("insert the new procIDs");
                for (String id : treatProcIDS) {
                    //add proc ID
                    String query = "INSERT INTO procid (`riskop_idriskop`, `procid`) "
                            + "VALUES "
                            + "(" + idrisk
                            + ", '" + id
                            + "' )";
                    dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);
                }
            } catch (Exception ex) {
                logger.debug("Datalayer: Error while inserting treatment process Ids ", ex);
                throw new RuntimeException("Datalayer: Error while inserting existing treatment process Ids" + ex.getMessage(), ex);
            }
        }

    }
    
    //considers that there is only one community in the resultset
    public static Community extractCommunity(ResultSet rs) {
        Community com = null;

        try {
            if (rs != null) {
                if (rs.next()) {
                    String communityName = rs.getString("com_name");
                    String strUuid = rs.getString("com_uuid");
                    String strUri = rs.getString("com_uri");
                    Boolean isStream = rs.getBoolean("com_isStream");
                    String strId = rs.getString("com_id");
                    String strStreamName = rs.getString("com_streamName");
                    UUID uuid = UUID.fromString(strUuid);
                    URI uri = null;
                    if (strUri != null) {
                        uri = new URI(strUri);
                    }

                    com = new Community(communityName, uuid, strId, strStreamName, uri, isStream);//new Community(communityName, uuid, uri, isStream);          
                    logger.debug("Extracted: " + rs.getLong("idcommunity") + ": " + communityName);
                }
            }
        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting communities. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting communities. " + exc.getMessage());
        }
        return com;
    }

    
    public static Community getCommunityByID(int community_idcommunity, DatabaseConnector dbCon) {
       Community com=null;
        
         try {
            
            String query = "SELECT * from community WHERE idcommunity=" + community_idcommunity + "";
            ResultSet rs = dbCon.executeQuery(query);
           com=extractCommunity(rs);

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting communities. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting communities. " + exc.getMessage());

        } 
         
         return com; 
    }

    public static Parameter getForecastParam(int idpredic, DatabaseConnector dbCon) {
        //get param attributes given the id
        Parameter param = null;        
        String paramquery = "SELECT * from parameter WHERE predictor_idpredict=" + idpredic + " AND forecast_period_param=true";

        try {
            ResultSet rs = dbCon.executeQuery(paramquery);
            while (rs.next()) {
                int idparameter = rs.getInt("idparameter");
                String name = rs.getString("param_name");
                String param_uuid = rs.getString("param_uuid");
                UUID uuid = UUID.fromString(param_uuid);

                String description = rs.getString("param_desc");
                String unit = rs.getString("param_unit");
                String strtype = rs.getString("param_value_type");
                ParameterValueType type = null;
                if (strtype != null && !strtype.equals("")) {
                    type = ParameterValueType.fromValue(strtype);
                }

                String strvalue = rs.getString("param_value");
                String strvalEvalType = rs.getString("param_value_eval_type");
                EvaluationType valEvalType = null;


                if ((strvalEvalType!= null) && !strvalEvalType.equals("")) {
                    valEvalType = EvaluationType.fromValue(strvalEvalType);
                }

                ParameterValue paramValue = null;

                if (strvalue != null /*&& valEvalType != null*/) {
                    paramValue = new ParameterValue(strvalue, valEvalType);
                }

                String strValuesAllowedType = rs.getString("param_allowed_type");
                ValuesAllowedType valuesAllowedType = ValuesAllowedType.valueOf(strValuesAllowedType);

                //get constraints
                List<ValueConstraint> cons = getConstraints(idparameter, true, dbCon);

                param = new Parameter(uuid, type, name, description, unit, paramValue, valuesAllowedType, cons);


            }

            return param;
        } catch (Exception ex) {
            throw new RuntimeException("Datalayer Error while getting forecast parameter for predictor id "+idpredic+". "+ex.getMessage(), ex);
        }
    }

    private static int getEventId(Event newEvent, DatabaseConnector dbCon) {
        try {

             int idExistingCond=-1;
             
            String query = "SELECT * from event "
                    + "WHERE event.event_uuid='" + newEvent.getUuid().toString() + "'";
            logger.debug(query);
            ResultSet rs = dbCon.executeQuery(query);
            if (rs.next()) {
                 idExistingCond = rs.getInt(1);
            }
            
             return idExistingCond;
        } catch (Exception ex) {
            throw new RuntimeException("Data layer error while getting event id of event " + newEvent.getTitle(), ex);
        }
       
    }

    private static void updateEventParameters(int eventID, List<Parameter> configParams, DatabaseConnector dbCon) {
        if (configParams != null && dbCon != null) {
            for (Parameter param : configParams) {
                try {
                    String query = "UPDATE parameter "
                            + "SET "
                            + "param_value="
                            +(param.getValue()==null?"NULL":"'" + param.getValue() + "'")
                            + ", "
                            + "param_value_eval_type="
                            + ((param.getValue()==null || param.getValue().getValueEvaluationType()==null)?"NULL":"'" + param.getValue().getValueEvaluationType() + "'")
                            + " WHERE event_idevent=" + eventID;
                    logger.debug(query);
                    dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

                } catch (Exception ex) {
                    throw new RuntimeException("Data layer error while updating event param " + param.getName(), ex);
                }
            }
        }
    }

    private static void deleteParam(UUID uuid, DatabaseConnector dbCon) {
           try {

            logger.debug("deleting param if exists, uuid " + uuid);
            //System.out.println("deleting impact entry of risk id " + idrisk);

            String query = "Delete from parameter "
                    + "Where `param_uuid`='" + uuid+"'";

            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting parameter uuid: " + uuid + ". " + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while deleting parameter uuid: " + uuid + ". " + exc.getMessage(), exc);
        }
    }
    
    private static void deleteParamInstance(int paramid,int riskid, DatabaseConnector dbCon) {
           try {

            logger.debug("deleting param instances if exists, param id= " + paramid +" riskid= "+riskid);
            
            String query = "Delete from paraminstance "
                    + "Where parameter_idparameter=" + paramid
                    + " AND riskop_idriskop="+riskid;

            dbCon.execute(query, Statement.RETURN_GENERATED_KEYS);

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while deleting parameter instances,  param id= " + paramid +" riskid= "+riskid + exc.getMessage(), exc);
            throw new RuntimeException("Datalayer: Error while deleting parameter instance, param id= " + paramid +" riskid= "+riskid+ exc.getMessage(), exc);
        }
    }

    private static int getParamIDFromUUID(UUID uuiD,DatabaseConnector dbCon) {
        String query = "SELECT parameter.idparameter FROM parameter WHERE param_uuid='" + uuiD + "'";
        int idPredictor = -1;

        try {
            ResultSet rs = dbCon.executeQuery(query);

            if (rs.next()) {
                idPredictor = rs.getInt(1);
            } else {
                // throw an exception from here
                logger.debug("DataLayer: no index returned for parameter uuid= " + uuiD);
                throw new RuntimeException("DataLayer: no index returned for parameter uuid= " + uuiD);
            }

        } catch (Exception exc) {
            logger.debug("Datalayer: Error while getting  parameter id form uuid. " + exc.getMessage());
            throw new RuntimeException("Datalayer: Error while getting  parameter id form uuid. " + exc.getMessage(), exc);

        }

        return idPredictor;
    }
}
