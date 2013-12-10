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
//      Created Date :          16-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.commons.configuration.ConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.Treatment;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;
import uk.ac.soton.itinnovation.robust.riskmodel.*;
import org.apache.commons.configuration.XMLConfiguration;

public class PopulateBoards {

    static final Logger logger = Logger.getLogger(TestDataLayer.class);
    static final String treatRoleComposition = "96c83bf0-773d-4553-b891-84deefe2e779"; // See TreatmentPlan_01.bpmn20.xml
    
//predictor info to be pulled from db.xml file
   static String path="db.xml";

    private static String scriptPath = null;
   
    
 
    
        //event condition and event for CM IBM
//    private static EventCondition roleDecreaseEventCond = null;
//    private static Event roleDecreaseEvent = null;
//    private static EventCondition roleDecreaseEventCond12 = null;
//    private static Event roleDecreaseEvent12 = null;
//    private static EventCondition roleIncreateEvtCnd = null;
//    private static Event roleIncreaseEvent = null;
//    private static Parameter roleParamIBMDec = null;
//    private static Parameter roleParamIBMDec12 = null;
//    private static Parameter roleParamIBMInc2 = null;
//    private static Parameter forecastPeriod = null;
    

    private Community addCommunity(String communityName, String communityID, DataLayerImpl datalayer) {
        try {

//            String streamName = null;
            UUID uuid = UUID.randomUUID();
//            (String name, UUID uuid, String communityID, String streamName, URI uri, Boolean isStream)
            Community com = new Community(communityName, uuid, communityID, null, null, false);
            datalayer.addCommunity(com);
            return com;
            
        } catch (Exception ex) {
            throw new RuntimeException("error while adding IBM community data to the DB", ex);
        }
    }
    
   
    
     private void addObjective(Community com, Objective ob, DataLayerImpl datalayer) {
        try {
          
            logger.debug("setting objectives for community " + com.getName());
            datalayer.addObjective(com.getUuid(), ob);
            
        } catch (Exception ex) {
            throw new RuntimeException("error while set community objectives to community " + com.getName(), ex);
        }
        
    }
    
//    private void addBoardsRisk(DataLayerImpl datalayer) throws URISyntaxException {
//        try {
//            
//             //-------------- add risk Role composition -----------------------
//            logger.debug("saving risk: Risk of role composition");
//            Risk risk = datalayer.saveRisk(comPrimetime, "Role Composition", "manager", Boolean.TRUE, "Default group");
//            risk.setScope(Scope.COMMUNITY);
//            risk.setState(RiskState.ACTIVE);
//
//            logger.debug("saving opp: Demo opp");
//            Risk opp = datalayer.saveRisk(comPrimetime, "Demo opp", "manager", Boolean.TRUE, "Default group");
//            opp.setScope(Scope.COMMUNITY);
//            opp.setState(RiskState.ACTIVE);
//
//            //---------------- add risk event ---------------------
//            logger.debug("");
//            logger.debug("adding risk role composition event");
//            List<EventCondition> conds = new ArrayList<EventCondition>();
//
//            //add event condition for role decrease
//            //ParameterValue preconDecrease = new ParameterValue("0.9", EvaluationType.GREATER_OR_EQUAL);
//            ParameterValue postconDecrease = new ParameterValue("-10", EvaluationType.GREATER_OR_EQUAL);
//            //roleDecreaseEventCond.setPreConditionValue(preconDecrease);
//            roleDecreaseEventCond.setPostConditionValue(postconDecrease);
//            conds.add(roleDecreaseEventCond);
//            roleDecreaseEvent.setEventConditions(conds);
//            risk.addEvent(roleDecreaseEvent);
//
//            //add event condition for role increase
//            //ParameterValue preconIncrease = new ParameterValue("0.9", EvaluationType.GREATER_OR_EQUAL);
//            ParameterValue postconIncrease = new ParameterValue("10", EvaluationType.GREATER_OR_EQUAL);
//            //roleDecreaseEventCond.setPreConditionValue(preconIncrease);
//            roleIncreateEvtCnd.setPostConditionValue(postconIncrease);
//            conds.add(roleIncreateEvtCnd);
//            roleIncreaseEvent.setEventConditions(conds);
//            risk.addEvent(roleIncreaseEvent);
//
//            try {
//                datalayer.updateRisk(risk);
//            } catch (Exception ex) {
//                logger.debug("couldnt update risk " + ex.getMessage(), ex);
//            }
//
//
//            // ---------------- add risk impact----------------------------
//
//            logger.debug("");
//            logger.debug("adding risk impact");
//            Map<Objective, ImpactLevel> impactMap = new HashMap<Objective, ImpactLevel>();
//
//            ImpactLevel impLevel11 = ImpactLevel.NEG_HIGH;
//            // impLevel11.setIsPositive(false);
//
//            impactMap.put(obj1, impLevel11);
//            impactMap.put(obj2, impLevel11);
//            impactMap.put(obj3, impLevel11);
//            impactMap.put(obj5, impLevel11);
//
//            Impact impact = new Impact(impactMap);
//
//            risk.setImpact(impact);
//
//            try {
//                datalayer.updateRisk(risk);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                logger.debug("couldnt update risk " + ex.getMessage());
//            }
//
//            //----------------- add IBM risk treatment--------------------------
//            logger.debug("");
//            logger.debug("adding risk treatment");
//            TreatmentWFs treatment1 = new TreatmentWFs();
//            TreatmentTemplate exampleTemplate = new TreatmentTemplate("Manage drop in roles",
//                    "Use this treatment to try to prevent any further drop in roles",
//                    UUID.fromString(treatRoleComposition));
//
//            treatment1.addTreatmentTemplate(exampleTemplate, 1.0f);
//
//            risk.setTreatment(treatment1);
//
//            try {
//                datalayer.updateRisk(risk);
//            } catch (Exception ex) {
//                logger.debug("couldnt update risk " + ex.getMessage(), ex);
//            }
//
//        } catch (Exception ex) {
//            throw new RuntimeException("error while adding IBM risk",ex);
//        }
//
//    }


    private void getConfigs() {
        Properties prop = new Properties();

        try {
            prop.load(PopulateDB.class.getClassLoader().getResourceAsStream("data.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("Error with loading configuration file data.properties. " + ex.getMessage(), ex);
        }

        scriptPath= prop.getProperty("repoSQLSchema");

    }

    public static void main(String[] args) {

     
        
        System.out.println("************************************************************************************************");
        System.out.println("THIS WILL RUN THE SCRIPT AGAINST THE DATABASE. THIS WILL DROP AND RECREATE THE DB STRUCTURE");
        System.out.println("************************************************************************************************");

        PopulateBoards pop = new PopulateBoards();
        if(args!=null && args.length!=0){
            path=args[0];
        }

        BasicConfigurator.configure();
        pop.getConfigs();
        DataLayerImpl datalayer = null;
        try {
            datalayer = new DataLayerImpl();
            logger.debug("Creating DB using script at " + scriptPath);
           // datalayer.runScript(scriptPath);
              datalayer.runScript(scriptPath);//../../../repository/robust.sql 
     
        } catch (Exception ex) {
            logger.debug("Error while getting the mysql schema script at " + scriptPath + "." + ex.getMessage());
            throw new RuntimeException("Error while getting the mysql schema script at " + scriptPath + ex,ex);
        }

        try {
            XMLConfiguration config = new XMLConfiguration(path);

            // get the number of communities
            Object coms = config.getProperty("communities.community.name");
            if (coms instanceof Collection) {
                System.out.println("Number of comms: " + ((Collection) coms).size());
                for (int i = 0; i < ((Collection) coms).size(); i++) {
                    //get community details
                    String name = config.getString("communities.community(" + i + ").name");
                    String id = config.getString("communities.community(" + i + ").id");
                    Community com = pop.addCommunity(name, id, datalayer);

                    //get objectives number
                    Object obs = config.getProperty("communities.community(" + i + ").objectives.objective.title");
                    System.out.println("Number of objectives: " + ((Collection) obs).size());
                    for (int j = 0; j < ((Collection) obs).size(); j++) {
                        String title = config.getString("communities.community(" + i + ").objectives.objective(" + j + ").title");
                        String desc = config.getString("communities.community(" + i + ").objectives.objective(" + j + ").desc");
                        Objective obj = new Objective(title, desc);
                        pop.addObjective(com, obj, datalayer);
                    }
                }
            }

            //add predictor services
            //get number of predictors
            Object preds = config.getProperty("predictors.predictor.predname");
            if (preds instanceof Collection) {
                System.out.println("Number of predictors: " + ((Collection) preds).size());
                for (int i = 0; i < ((Collection) preds).size(); i++) {
                    //get community details
                    String name = config.getString("predictors.predictor(" + i + ").predname");
                    String preddesc = config.getString("predictors.predictor(" + i + ").preddesc");
                    String predver = config.getString("predictors.predictor(" + i + ").predver");
                    String preduri = config.getString("predictors.predictor(" + i + ").preduri");
                    String predns = config.getString("predictors.predictor(" + i + ").predns");
                    String predsvcname = config.getString("predictors.predictor(" + i + ").predsvcname");
                    String predportname = config.getString("predictors.predictor(" + i + ").predportname");
                    if (name != null && name.contains("GS")) {
                        PredictorServiceDescription GSpsd = pop.getGSPredictorServiceDescription(name, preddesc, predver, preduri, predns, predsvcname, predportname);

                        datalayer.addPredictor(GSpsd);
                    }

                    if (name != null && name.contains("CM")) {
                        PredictorServiceDescription CMpsd = pop.getCMPredictorServiceDescription(name, preddesc, predver, preduri, predns, predsvcname, predportname);

                        datalayer.addPredictor(CMpsd);
                    }
                }
            }

        } catch (ConfigurationException cex) {
            throw new RuntimeException("error getting the configurations to populate boardsie data ", cex);
        }

    }

    private PredictorServiceDescription getGSPredictorServiceDescription(String name, String preddesc, String predver, String preduri,
            String predns, String predsvcname, String predportname) {

        try {

            UUID psdUUID = UUID.fromString("a19b4722-3257-3b7c-c36c-970565ecb552");
            PredictorServiceDescription desc = new PredictorServiceDescription(psdUUID, name, predver, preddesc);
            desc.setServiceURI(new URI(preduri));
            desc.setServiceTargetNamespace(predns);
            desc.setServiceName(predsvcname);
            desc.setServicePortName(predportname);

            // event(s)
            UUID eventUUID = UUID.fromString("e19b4722-4257-4a7c-a36c-970565ecb552");
            Event evt = new Event("User activity drop", "Users activity drop according to some percentage treshold");
            evt.setUuid(eventUUID);

            UUID eventCondUUID = UUID.fromString("87084ba9-edac-4e79-b23a-76a14e52730e");
            EventCondition evtCond = new EventCondition(ParameterValueType.FLOAT, "Activity drop Threshold", "", "");
            evtCond.setUUID(eventCondUUID);
            evtCond.setValuesAllowedType(ValuesAllowedType.SINGLE);
            evtCond.addValueConstraint("0.2", ValueConstraintType.DEFAULT);
            evtCond.addValueConstraint("0", ValueConstraintType.MIN);
            evtCond.addValueConstraint("1", ValueConstraintType.MAX);
            //evtCond.addAllowedEvaluationType(EvaluationType.GREATER_OR_EQUAL);

            evt.setEventCondition(evtCond);
            desc.addEvent(evt);

            // size of timewindow - fixed to 1 week
            Parameter forecastPeriod = new Parameter(ParameterValueType.FLOAT, "The forecast period, which specifies the size of the time window", "The parameter that defines the total number of days for each covariate", "weeks");
            forecastPeriod.setValue(new ParameterValue("1"));
            
            desc.setForecastPeriod(forecastPeriod);

            ParameterValue v= forecastPeriod.getValue();
            String s=v.getValue();
            // number of covariate aka number of snapshots required for prediction
            // The total number of covariate is the same as the total number of observation as Y can be inferred from X
            // and X is going to be acting as covariate
            // Intercept is not included
            // Max is yet to be decided due to other restrictions that may apply i.e. the amount of historical observation currently available
            UUID numCovariatesUUID = UUID.fromString("75302132-65db-4c59-a8f0-d2c4ae702de3");
            Parameter numCovariates = new Parameter(ParameterValueType.INT, "Number of historical snapshots", "The total number of covariates that will be used in the GS", "");
            numCovariates.setUUID(numCovariatesUUID);
            numCovariates.addValueConstraint("12", ValueConstraintType.DEFAULT);
            numCovariates.addValueConstraint("3", ValueConstraintType.MIN);
            numCovariates.addValueConstraint("28", ValueConstraintType.MAX);
            numCovariates.addValueConstraint("1", ValueConstraintType.STEP);
            desc.addConfigurationParam(numCovariates);

            // Definition of 'user' being active
            // Sets requirement that all users must have a total activity over the prediction period greater than this threshold to ensure that covariate matrix is not sparse & focus on key users
            // Note: for snapshots where 'users' do not 'exist' they have activity -1.
            // Question: Is there any way to set the max to the max within the database - thinking not.
            UUID activeThresholdUUID = UUID.fromString("0dce1066-5c85-4fd0-a115-a5d66ddda6b7");
            Parameter activeThreshold = new Parameter(ParameterValueType.INT, "Activity filter threshold", "The minimum number of posts a user should have posted in the prediction period to be included in the population", "posts");
            activeThreshold.setUUID(activeThresholdUUID);
            activeThreshold.addValueConstraint("0", ValueConstraintType.DEFAULT);
            activeThreshold.addValueConstraint("0", ValueConstraintType.MIN);
            activeThreshold.addValueConstraint("100", ValueConstraintType.MAX);
            desc.addConfigurationParam(activeThreshold);

//            PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), name, predver, preddesc);
//            desc.setServiceURI(new URI(preduri));
//            desc.setServiceTargetNamespace(predns);
//            desc.setServiceName(predsvcname);
//            desc.setServicePortName(predportname);
//
//            // event(s)
//            UUID eventUUID = UUID.fromString("e19b4722-4257-4a7c-a36c-970565ecb552");
//            Event evt = new Event("User activity drop", "Users activity drop according to some percentage treshold");
//            evt.setUuid(eventUUID);
//
//            UUID eventCondUUID = UUID.fromString("87084ba9-edac-4e79-b23a-76a14e52730e");
//            EventCondition evtCond = new EventCondition(ParameterValueType.FLOAT, "Activity drop Threshold", "", "");
//            evtCond.setUUID(eventCondUUID);
//            evtCond.setValuesAllowedType(ValuesAllowedType.SINGLE);
//            evtCond.addValueConstraint("0.2", ValueConstraintType.DEFAULT);
//            evtCond.addValueConstraint("0", ValueConstraintType.MIN);
//            evtCond.addValueConstraint("1", ValueConstraintType.MAX);
//            evtCond.addAllowedEvaluationType(EvaluationType.GREATER_OR_EQUAL);
//
//            evt.setEventCondition(evtCond);
//            desc.addEvent(evt);
//
//            // size of timewindow - fixed to 1 week
//            Parameter forecastPeriod = new Parameter(ParameterValueType.INT, "The forecast period, which specifies the size of the time window", "The parameter that defines the total number of days for each covariate", "weeks");
////            forecastPeriod.setValue(new ParameterValue("1"));
//            forecastPeriod.addValueConstraint("1", ValueConstraintType.DEFAULT);
//            desc.setForecastPeriod(forecastPeriod);
//
//            // number of covariate aka number of snapshots required for prediction
//            // The total number of covariate is the same as the total number of observation as Y can be inferred from X
//            // and X is going to be acting as covariate
//            // Intercept is not included
//            // Max is yet to be decided due to other restrictions that may apply i.e. the amount of historical observation currently available
//            UUID numCovariatesUUID = UUID.fromString("75302132-65db-4c59-a8f0-d2c4ae702de3");
//            Parameter numCovariates = new Parameter(ParameterValueType.INT, "Number of historical snapshots", "The total number of covariates that will be used in the GS", "");
//            numCovariates.setUUID(numCovariatesUUID);
//            numCovariates.addValueConstraint("12", ValueConstraintType.DEFAULT);
//            numCovariates.addValueConstraint("3", ValueConstraintType.MIN);
//            numCovariates.addValueConstraint("28", ValueConstraintType.MAX);
//            numCovariates.addValueConstraint("1", ValueConstraintType.STEP);
//            desc.addConfigurationParam(numCovariates);
//
//            // Definition of 'user' being active
//            // Sets requirement that all users must have a total activity over the prediction period greater than this threshold to ensure that covariate matrix is not sparse & focus on key users
//            // Note: for snapshots where 'users' do not 'exist' they have activity -1.
//            // Question: Is there any way to set the max to the max within the database - thinking not.
//            UUID activeThresholdUUID = UUID.fromString("0dce1066-5c85-4fd0-a115-a5d66ddda6b7");
//            Parameter activeThreshold = new Parameter(ParameterValueType.INT, "Activity filter threshold", "The minimum number of posts a user should have posted in the prediction period to be included in the population", "posts");
//            activeThreshold.setUUID(activeThresholdUUID);
//            activeThreshold.addValueConstraint("0", ValueConstraintType.DEFAULT);
//            activeThreshold.addValueConstraint("0", ValueConstraintType.MIN);
//            activeThreshold.addValueConstraint("100", ValueConstraintType.MAX);
//            desc.addConfigurationParam(activeThreshold);

            return desc;
        } catch (Exception ex) {
            throw new RuntimeException("error while generating GS risk predictor description ", ex);
        }
    }
    
    private PredictorServiceDescription getCMPredictorServiceDescription(String name, String preddesc, String predver, String preduri,
            String predns, String predsvcname, String predportname) {
        try {
            UUID psdUUID = UUID.fromString("68afb02e-7e89-4e5e-96c2-473c85c4ea8e");
            UUID event1UUID = UUID.fromString("ccce03cf-2df9-4dbd-bb39-ba5e341b5878");
            UUID event2UUID = UUID.fromString("23b37482-2a33-4144-be4f-7ddf3d7c9081");
            UUID event3UUID = UUID.fromString("5d8e9d73-c751-481c-a805-55b380e22324");
            UUID event4UUID = UUID.fromString("a3fd457a-ee52-4117-91b0-a9d7ef3443e1");
            UUID event1_roleParamBOARDSIEUUID = UUID.fromString("966f1759-57d7-4be5-92ef-e28782e1f58e");
            UUID event2_roleParamBOARDSIEUUID = UUID.fromString("dfd2e0ca-f747-49e2-8e13-57b94fc439fe");
            UUID event3_roleParamBOARDSIEUUID = UUID.fromString("9f6b87fb-88b7-4566-9e6a-b602b62ac692");
            UUID event4_roleParamBOARDSIEUUID = UUID.fromString("1589e427-a935-4d01-b7eb-2fc6e93f868e");

           // PredictorServiceDescription desc = new PredictorServiceDescription(psdUUID, "User Role (CM) Predictor Service", "1.3", "A Predictor service using the Compartment Model to make predictions about roles of users in the community");

            PredictorServiceDescription desc = new PredictorServiceDescription(psdUUID, name, predver, preddesc);
            desc.setServiceURI(new URI(preduri));
            desc.setServiceTargetNamespace(predns);
            desc.setServiceName(predsvcname);
            desc.setServicePortName(predportname);
            
            // We first start to define the forecast period
            Parameter forecastPeriod = new Parameter(ParameterValueType.INT, "Forecast period", "The available forecast period options", "weeks");
            forecastPeriod.addValueConstraint("1", ValueConstraintType.DEFAULT);
            forecastPeriod.addValueConstraint("1", ValueConstraintType.MIN);
            forecastPeriod.addValueConstraint("52", ValueConstraintType.MAX);
            forecastPeriod.addValueConstraint("1", ValueConstraintType.STEP);
            forecastPeriod.setValue(new ParameterValue("1"));
            desc.setForecastPeriod(forecastPeriod);

            // The the general parameter that is suitable for all of the events
            Parameter roleParamBOARDSIE = new Parameter(ParameterValueType.STRING, "Roles for the use case", "Insert the name of the role", "");
            roleParamBOARDSIE.addValueConstraint("Lurker", ValueConstraintType.DEFAULT);
            roleParamBOARDSIE.addValueConstraint("Inactive", ValueConstraintType.VALUESALLOWED);                      // 0 
            roleParamBOARDSIE.addValueConstraint("Lurker", ValueConstraintType.VALUESALLOWED);                        // 1
            roleParamBOARDSIE.addValueConstraint("Contributor", ValueConstraintType.VALUESALLOWED);                   // 2
            roleParamBOARDSIE.addValueConstraint("Super User", ValueConstraintType.VALUESALLOWED);                    // 3
            roleParamBOARDSIE.addValueConstraint("Follower", ValueConstraintType.VALUESALLOWED);                      // 4
            roleParamBOARDSIE.addValueConstraint("BroadCaster", ValueConstraintType.VALUESALLOWED);                   // 5
            roleParamBOARDSIE.addValueConstraint("Daily User", ValueConstraintType.VALUESALLOWED);                    // 6
            roleParamBOARDSIE.addValueConstraint("Leader", ValueConstraintType.VALUESALLOWED);                        // 7
            roleParamBOARDSIE.addValueConstraint("Celebrity", ValueConstraintType.VALUESALLOWED);                     // 8
            roleParamBOARDSIE.addValueConstraint("Unmatched", ValueConstraintType.VALUESALLOWED);                     // -1

            // events (s)

            //////// Now we start to define the events where each event will have their own parameter that needs to be specified

            // First event
        /* The event that a particular role has dropped below a certain threshold 
             * Finding the probability that it is below a value i.e. P(X \le x) 
             */

            Event evt1 = new Event("Decrease in percentage of user in a role", "");
            evt1.setUuid(event1UUID);
            EventCondition evtCond1 = new EventCondition(ParameterValueType.FLOAT, "Decrease in percentage of number of users in a role", "Percentage change in total number of user", "");
            evtCond1.setValuesAllowedType(ValuesAllowedType.SINGLE);
            evtCond1.addValueConstraint("-20", ValueConstraintType.DEFAULT);
            evtCond1.addValueConstraint("-100", ValueConstraintType.MIN);
            evtCond1.addValueConstraint("0", ValueConstraintType.MAX);
            evt1.setEventCondition(evtCond1);
            Parameter roleParamBOARDSIEcopy1 = new Parameter(roleParamBOARDSIE);
            roleParamBOARDSIEcopy1.setUUID(event1_roleParamBOARDSIEUUID);
            evt1.addConfigParam(roleParamBOARDSIEcopy1);
            desc.addEvent(evt1);

            // Second event
        /* Inverse of the first, which means we want to know the probability that 
             * it we have increased by x\% or more, i.e. P(X \ge x)
             */

            Event evt2 = new Event("Increase in percentage of user in a role", "");
            evt2.setUuid(event2UUID);
            EventCondition evtCond2 = new EventCondition(ParameterValueType.FLOAT, "Increase in percentage of number of users in a role", "Percentage change in total number of users", "");
            evtCond2.setValuesAllowedType(ValuesAllowedType.SINGLE);
            evtCond2.addValueConstraint("20", ValueConstraintType.DEFAULT);
            evtCond2.addValueConstraint("0", ValueConstraintType.MIN);
            evtCond2.addValueConstraint("200", ValueConstraintType.MAX);
            evt2.setEventCondition(evtCond2);
            Parameter roleParamBOARDSIEcopy2 = new Parameter(roleParamBOARDSIE);
            roleParamBOARDSIEcopy2.setUUID(event2_roleParamBOARDSIEUUID);
            evt2.addConfigParam(roleParamBOARDSIEcopy2);
            desc.addEvent(evt2);

            // The third and fourth event.  Same as the second event but instead of using a threshold, we want to use an absolute number

            Event evt3 = new Event("Number of user below a value", "");
            evt3.setUuid(event3UUID);
            EventCondition evtCond3 = new EventCondition(ParameterValueType.FLOAT, "Number of Users below the specified value", "Number of user below the specified value", "");
            evtCond3.setValuesAllowedType(ValuesAllowedType.SINGLE);
            evtCond3.addValueConstraint("10", ValueConstraintType.DEFAULT);
            evtCond3.addValueConstraint("0", ValueConstraintType.MIN);
            evtCond3.addValueConstraint("10000", ValueConstraintType.MAX);
            evt3.setEventCondition(evtCond3);
            Parameter roleParamBOARDSIEcopy3 = new Parameter(roleParamBOARDSIE);
            roleParamBOARDSIEcopy3.setUUID(event3_roleParamBOARDSIEUUID);
            evt3.addConfigParam(roleParamBOARDSIEcopy3);
            desc.addEvent(evt3);


            Event evt4 = new Event("Number of user above a value", "");
            evt4.setUuid(event4UUID);
            EventCondition evtCond4 = new EventCondition(ParameterValueType.FLOAT, "Number of Users above the specified value", "Number of user above the specified value", "");
            evtCond4.setValuesAllowedType(ValuesAllowedType.SINGLE);
            evtCond4.addValueConstraint("10", ValueConstraintType.DEFAULT);
            evtCond4.addValueConstraint("0", ValueConstraintType.MIN);
            evtCond4.addValueConstraint("10000", ValueConstraintType.MAX);
            evt4.setEventCondition(evtCond4);
            Parameter roleParamBOARDSIEcopy4 = new Parameter(roleParamBOARDSIE);
            roleParamBOARDSIEcopy4.setUUID(event4_roleParamBOARDSIEUUID);
            evt4.addConfigParam(roleParamBOARDSIEcopy4);
            desc.addEvent(evt4);

            return desc;

//            //getConfigs();
//            PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), name, predver, preddesc);
//            desc.setServiceURI(new URI(preduri));
//            desc.setServiceTargetNamespace(predns);
//            desc.setServiceName(predsvcname);
//            desc.setServicePortName(predportname);
//
//            // PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), "CM Predictor Service", "1.0", "CM Predictor service)");
//
//            // We first start to define the forecast period
//            forecastPeriod = new Parameter(ParameterValueType.INT, "Forecast period", "The available forecast period options", "weeks");
//            forecastPeriod.addValueConstraint("1", ValueConstraintType.DEFAULT);
//            forecastPeriod.addValueConstraint("1", ValueConstraintType.MIN);
//            forecastPeriod.addValueConstraint("52", ValueConstraintType.MAX);
//            forecastPeriod.addValueConstraint("1", ValueConstraintType.STEP);
//            desc.setForecastPeriod(forecastPeriod);
//
//
//            // Role parameter
//           Parameter roleParamIBM = new Parameter(ParameterValueType.STRING, "Roles for the IBM use case", "Insert the name of the role", "");
//            roleParamIBM.addValueConstraint("Inactive", ValueConstraintType.DEFAULT);
//            roleParamIBM.addValueConstraint("Inactive", ValueConstraintType.VALUESALLOWED);                      // 0 
//            roleParamIBM.addValueConstraint("Lurker", ValueConstraintType.VALUESALLOWED);                        // 1
//            roleParamIBM.addValueConstraint("Contributor", ValueConstraintType.VALUESALLOWED);                        // 2
//            roleParamIBM.addValueConstraint("Super User", ValueConstraintType.VALUESALLOWED);                        // 3
//            roleParamIBM.addValueConstraint("Follower", ValueConstraintType.VALUESALLOWED);                        // 4
//            roleParamIBM.addValueConstraint("BroadCaster", ValueConstraintType.VALUESALLOWED);                        // 5
//            roleParamIBM.addValueConstraint("Daily User", ValueConstraintType.VALUESALLOWED);                        // 6
//            roleParamIBM.addValueConstraint("Leader", ValueConstraintType.VALUESALLOWED);                        // 7
//            roleParamIBM.addValueConstraint("Celebrity", ValueConstraintType.VALUESALLOWED);                        // 8
//            roleParamIBM.addValueConstraint("Unmatched", ValueConstraintType.VALUESALLOWED);                        // -1
//
//            // events (s)
//
//            //////// Now we start to define the events where each event will have their own parameter that needs to be specified
//
//            // First event
//            /* The event that a particular role has dropped below a certain threshold 
//             * Finding the probability that it is below a value i.e. P(X \le x) 
//             */
//            UUID eventUUID1 = UUID.fromString("714c0d86-a828-4d1f-a9d4-c9335839b919");
//            roleDecreaseEvent = new Event("Decrease in percentage of user in role 1", "");
//            roleDecreaseEvent.setUuid(eventUUID1);
//            roleDecreaseEventCond = new EventCondition(ParameterValueType.FLOAT, "Decrease in percentage of number of users in a role", "Percentage change in total number of user", "");
//            roleDecreaseEventCond.setValuesAllowedType(ValuesAllowedType.SINGLE);
//            roleDecreaseEventCond.addValueConstraint("-10", ValueConstraintType.DEFAULT);
//            roleDecreaseEventCond.addValueConstraint("-100", ValueConstraintType.MIN);
//            roleDecreaseEventCond.addValueConstraint("0", ValueConstraintType.MAX);
//            roleDecreaseEvent.setEventCondition(roleDecreaseEventCond);
//            
//            roleParamIBMDec = new Parameter(roleParamIBM);
//            roleParamIBMDec.setUUID(UUID.randomUUID());
//            roleDecreaseEvent.addConfigParam(roleParamIBMDec);
//            desc.addEvent(roleDecreaseEvent);
//
//            // Second event - same as the first
//            UUID eventUUID12 = UUID.fromString("674b4887-ef25-49e7-9a3f-7b12af9fc788");
//            roleDecreaseEvent12 = new Event("Decrease in percentage of user in role 2", "");
//            roleDecreaseEvent12.setUuid(eventUUID12);
//            roleDecreaseEventCond12 = new EventCondition(ParameterValueType.FLOAT, "Decrease in percentage of number of users in a role", "Percentage change in total number of users", "");
//            roleDecreaseEventCond12.setValuesAllowedType(ValuesAllowedType.SINGLE);
//            roleDecreaseEventCond12.addValueConstraint("-10", ValueConstraintType.DEFAULT);
//            roleDecreaseEventCond12.addValueConstraint("-100", ValueConstraintType.MIN);
//            roleDecreaseEventCond12.addValueConstraint("0", ValueConstraintType.MAX);
//            roleDecreaseEvent12.setEventCondition(roleDecreaseEventCond12);
//            roleParamIBMDec12 = new Parameter(roleParamIBM);
//            roleParamIBMDec12.setUUID(UUID.randomUUID());
//            roleDecreaseEvent12.addConfigParam(roleParamIBMDec12);
//            desc.addEvent(roleDecreaseEvent12);
//
//            // Second event
//            /* Inverse of the first, which means we want to know the probability that 
//             * it we have increased by x\% or more, i.e. P(X \ge x)
//             */
//            UUID eventUUID2 = UUID.fromString("54156207-7bd8-41d4-9923-22e4f331499b");
//            roleIncreaseEvent = new Event("Increase in percentage of user in a role", "");
//            roleIncreaseEvent.setUuid(eventUUID2);
//            roleIncreateEvtCnd = new EventCondition(ParameterValueType.FLOAT, "Increase in percentage of number of users in a role", "Percentage change in total number of users", "");
//            roleIncreateEvtCnd.setValuesAllowedType(ValuesAllowedType.SINGLE);
//            roleIncreateEvtCnd.addValueConstraint("10", ValueConstraintType.DEFAULT);
//            roleIncreateEvtCnd.addValueConstraint("0", ValueConstraintType.MIN);
//            roleIncreateEvtCnd.addValueConstraint("1000", ValueConstraintType.MAX);
//            roleIncreateEvtCnd.setPostConditionValue(new ParameterValue("10"));
//            roleIncreaseEvent.setEventCondition(roleIncreateEvtCnd);
//            roleParamIBMInc2 = new Parameter(roleParamIBM);
//            roleParamIBMInc2.setUUID(UUID.randomUUID());
//            roleIncreaseEvent.addConfigParam(roleParamIBMInc2);
//            desc.addEvent(roleIncreaseEvent);
//
//            // The third and fourth event.  Same as the second event but instead of using a threshold, we want to use an absolute number
//            UUID eventUUID3 = UUID.fromString("4552723d-3c2a-46e5-a2dc-a31e6e4cee6e");
//            Event evt3 = new Event("Number of user below a value", "");
//            evt3.setUuid(eventUUID3);
//            EventCondition evtCond3 = new EventCondition(ParameterValueType.FLOAT, "Number of Users below the specified value", "Number of user below the specified value", "");
//            evtCond3.setValuesAllowedType(ValuesAllowedType.SINGLE);
//            evtCond3.addValueConstraint("10", ValueConstraintType.DEFAULT);
//            evtCond3.addValueConstraint("0", ValueConstraintType.MIN);
//            evtCond3.addValueConstraint("1000", ValueConstraintType.MAX);
//            evtCond3.setPostConditionValue(new ParameterValue("10"));
//            evt3.setEventCondition(evtCond3);
//            Parameter roleParamIBMcopy3 = new Parameter(roleParamIBM);
//            roleParamIBMcopy3.setUUID(UUID.randomUUID());
//            evt3.addConfigParam(roleParamIBMcopy3);
//            desc.addEvent(evt3);
//
//            UUID eventUUID4 = UUID.fromString("1552723d-3c2a-46e5-a2dc-a31e6e4cee6e");
//            Event evt4 = new Event("Number of user above a value", "");
//            evt4.setUuid(eventUUID4);
//            EventCondition evtCond4 = new EventCondition(ParameterValueType.FLOAT, "Number of Users above the specified value", "Number of user above the specified value", "");
//            evtCond4.setValuesAllowedType(ValuesAllowedType.SINGLE);
//            evtCond4.addValueConstraint("10", ValueConstraintType.DEFAULT);
//            evtCond4.addValueConstraint("0", ValueConstraintType.MIN);
//            evtCond4.addValueConstraint("1000", ValueConstraintType.MAX);
//            evtCond4.setPostConditionValue(new ParameterValue("10"));
//            evt4.setEventCondition(evtCond4);
//            Parameter roleParamIBMcopy4 = new Parameter(roleParamIBM);
//            roleParamIBMcopy4.setUUID(UUID.randomUUID());
//            evt4.addConfigParam(roleParamIBMcopy4);
//            desc.addEvent(evt4);
//
//            
//            
//            ///-----add predictor configs values for this scenario
//
//            //roleDecreaseEventCond.setPostConditionValue(new ParameterValue("-0.2"));
//            roleDecreaseEventCond.setPostConditionValue(new ParameterValue("10"));
//            roleDecreaseEventCond12.setPostConditionValue(new ParameterValue("10"));
//            roleIncreateEvtCnd.setPostConditionValue(new ParameterValue("10"));
//            
//            ParameterValue forecastPeriodVal = new ParameterValue("5");
//            forecastPeriod.setValue(forecastPeriodVal);
//
//            return desc;
        } catch (Exception ex) {
            throw new RuntimeException("error while generating CM risk predictor description ", ex);
        }
    }


    
   private PredictorServiceDescription generateOPDemoPSD() 
   {
       return null;
   }
    
}
