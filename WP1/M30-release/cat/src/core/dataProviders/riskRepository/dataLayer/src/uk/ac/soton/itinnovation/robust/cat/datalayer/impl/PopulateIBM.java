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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.Treatment;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;
import uk.ac.soton.itinnovation.robust.riskmodel.*;

public class PopulateIBM {

    static final Logger logger = Logger.getLogger(TestDataLayer.class);
    static final String treatRoleComposition = "96c83bf0-773d-4553-b891-84deefe2e779"; // See TreatmentPlan_01.bpmn20.xml
    
//predictor info to be pulled from properties file
    private static String ibmPredName = null;
    private static String ibmPredVer = null;
    private static String ibmPredDesc = null;
    private static String ibmCMPredURI = null;
    private static String ibmCMPredNamespace = null;
    private static String ibmCMPredServiceName = null;
    private static String ibmCMPredPortName = null;
    private static String ibmcommunityName = "IBM Community";//formerly SAP community
    private static String scriptPath = null;
    ///communities info
    Community comRatSmart = null;
    String comRatSmartName = "IBM Rational Smart Community Leadership";
    String comRatSmartID = "c76ebc42-5b21-41c9-99f4-7f05b926ff0c";
    Community comNotes = null;
    String comNotesName = "Notes/Domino V2V Community";
    String comNotesID = "dd3ce105-bec1-4e54-a832-e3498371fa9f";
    Community comIBMWeb = null;
    String comIBMWebName = "IBM Web and Digital Presence";
    String comIBMWebID = "5e12dbe9-b860-4c05-aa81-ed83aa8b7176";
    Community comWWW = null;
    String comWWWName = "Worldwide Corporate HR Blue Community";
    String comWWWID = "c64097aa-091a-4ffd-bb11-bb0725ed0537";
    Community comEurope = null;
    String comEuropeName = "Europe Innovation Hub";
    String comEuropeID = "6e7f3d22-8946-4b18-840a-02e184661448";
    
    //objectives
    Objective obj1 =null;
    Objective obj2 =null;
    Objective obj3 =null;
    Objective obj4 =null;
    Objective obj5 =null;
    
        //event condition and event for CM IBM
    private static EventCondition roleDecreaseEventCond = null;
    private static Event roleDecreaseEvent = null;
    private static EventCondition roleDecreaseEventCond12 = null;
    private static Event roleDecreaseEvent12 = null;
    private static EventCondition roleIncreateEvtCnd = null;
    private static Event roleIncreaseEvent = null;
    private static Parameter roleParamIBMDec = null;
    private static Parameter roleParamIBMDec12 = null;
    private static Parameter roleParamIBMInc2 = null;
    private static Parameter forecastPeriod = null;
    

    private void addIBMCommunity(String communityName, String communityID, DataLayerImpl datalayer) {
        try {
            /////IBM scenario//////////////////////////

//            String streamName = null;
            UUID uuid = UUID.fromString(communityID);
            URI uri = new URI("ibm.it-innovation.soton.ac.uk");
//            (String name, UUID uuid, String communityID, String streamName, URI uri, Boolean isStream)
            Community com = new Community(communityName, uuid, communityID, null, uri, false);
            
            datalayer.addCommunity(com);
            addObjectives(com, datalayer);
            
        } catch (Exception ex) {
            throw new RuntimeException("error while adding IBM community data to the DB", ex);
        }
    }
    
    private void addObjectives(Community com, DataLayerImpl datalayer) {
        try {
            //Objective info
            obj1 = new Objective("Knowledge sharing", "Knowledge sharing amongst the employees");
            obj2 = new Objective("Diverse activity level", "Diverse activity level to ensure participation by many members");
            obj3 = new Objective("Quality of experience", "Usage of the platform should be easy and fruitful for employees");
            obj4 = new Objective("Quality of content", "Very good quality content needed");
            obj5 = new Objective("High activity level", "High activity level needed to ensure engagement of members");
            
            logger.debug("setting objectives for community " + com.getName());
            obj1 = datalayer.addObjective(com.getUuid(), obj1);
            obj2 = datalayer.addObjective(com.getUuid(), obj2);
            obj3 = datalayer.addObjective(com.getUuid(), obj3);
            obj4 = datalayer.addObjective(com.getUuid(), obj4);
            obj5 = datalayer.addObjective(com.getUuid(), obj5);
        } catch (Exception ex) {
            throw new RuntimeException("error while set IBM community objectives to community " + com.getName(), ex);
        }
        
    }
    
    private void addIBMRisk(DataLayerImpl datalayer) throws URISyntaxException {
        try {
            
             //-------------- add risk Role composition -----------------------
            logger.debug("saving risk: Risk of role composition");
            Risk risk = datalayer.saveRisk(comRatSmart, "Role Composition", "manager", Boolean.TRUE, "Default group");
            risk.setScope(Scope.COMMUNITY);
            risk.setState(RiskState.ACTIVE);

            logger.debug("saving opp: Demo opp");
            Risk opp = datalayer.saveRisk(comRatSmart, "Demo opp", "manager", Boolean.TRUE, "Default group");
            opp.setScope(Scope.COMMUNITY);
            opp.setState(RiskState.ACTIVE);



            //---------------- add risk event ---------------------
            logger.debug("");
            logger.debug("adding risk role composition event");
            List<EventCondition> conds = new ArrayList<EventCondition>();

            //add event condition for role decrease
            //ParameterValue preconDecrease = new ParameterValue("0.9", EvaluationType.GREATER_OR_EQUAL);
            ParameterValue postconDecrease = new ParameterValue("-10", EvaluationType.GREATER_OR_EQUAL);
            //roleDecreaseEventCond.setPreConditionValue(preconDecrease);
            roleDecreaseEventCond.setPostConditionValue(postconDecrease);
            conds.add(roleDecreaseEventCond);
            roleDecreaseEvent.setEventConditions(conds);
            risk.addEvent(roleDecreaseEvent);

            //add event condition for role increase
            //ParameterValue preconIncrease = new ParameterValue("0.9", EvaluationType.GREATER_OR_EQUAL);
            ParameterValue postconIncrease = new ParameterValue("10", EvaluationType.GREATER_OR_EQUAL);
            //roleDecreaseEventCond.setPreConditionValue(preconIncrease);
            roleIncreateEvtCnd.setPostConditionValue(postconIncrease);
            conds.add(roleIncreateEvtCnd);
            roleIncreaseEvent.setEventConditions(conds);
            risk.addEvent(roleIncreaseEvent);

            try {
                datalayer.updateRisk(risk);
            } catch (Exception ex) {
                logger.debug("couldnt update risk " + ex.getMessage(), ex);
            }


            // ---------------- add risk impact----------------------------

            logger.debug("");
            logger.debug("adding risk impact");
            Map<Objective, ImpactLevel> impactMap = new HashMap<Objective, ImpactLevel>();

            ImpactLevel impLevel11 = ImpactLevel.NEG_HIGH;
            // impLevel11.setIsPositive(false);

            impactMap.put(obj1, impLevel11);
            impactMap.put(obj2, impLevel11);
            impactMap.put(obj3, impLevel11);
            impactMap.put(obj5, impLevel11);

            Impact impact = new Impact(impactMap);

            risk.setImpact(impact);

            try {
                datalayer.updateRisk(risk);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.debug("couldnt update risk " + ex.getMessage());
            }

            //----------------- add IBM risk treatment--------------------------
            logger.debug("");
            logger.debug("adding risk treatment");
            TreatmentWFs treatment1 = new TreatmentWFs();
            TreatmentTemplate exampleTemplate = new TreatmentTemplate("Manage drop in roles",
                    "Use this treatment to try to prevent any further drop in roles",
                    UUID.fromString(treatRoleComposition));

            treatment1.addTreatmentTemplate(exampleTemplate, 1.0f);

            risk.setTreatment(treatment1);

            try {
                datalayer.updateRisk(risk);
            } catch (Exception ex) {
                logger.debug("couldnt update risk " + ex.getMessage(), ex);
            }

        } catch (Exception ex) {
            throw new RuntimeException("error while adding IBM risk",ex);
        }

    }
    
     private void addIBMOp(DataLayerImpl datalayer) throws URISyntaxException {
        try {
            generateOPDemoPSD();
        } catch (Exception ex) {
            throw new RuntimeException("error while adding IBM risk", ex);
        }


    }

    private void getConfigs() {
        Properties prop = new Properties();

        try {
            prop.load(PopulateDB.class.getClassLoader().getResourceAsStream("data.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("Error with loading configuration file data.properties. " + ex.getMessage(), ex);
        }

        ibmPredName = prop.getProperty("ibmCMpredname");
        ibmPredDesc = prop.getProperty("ibmCMpredDesc");
        ibmPredVer = prop.getProperty("ibmCMpredVer");
        
        ibmCMPredURI = prop.getProperty("ibmCMpredURI");
        ibmCMPredNamespace = prop.getProperty("ibmCMpredNamespace");
         ibmCMPredServiceName= prop.getProperty("ibmCMpredSvcname");
        ibmCMPredPortName = prop.getProperty("ibmCMpredPortname");
        scriptPath= prop.getProperty("repoSQLSchema");

    }
    
    public void populateIBM(DataLayerImpl datalayer){
        try {
             addIBMCommunity(comRatSmartName,comRatSmartID,datalayer);
             addIBMCommunity(comEuropeName,comEuropeID,datalayer);
             addIBMCommunity(comNotesName,comNotesID,datalayer);
             addIBMCommunity(comWWWName,comWWWID,datalayer);
            //--------------add predictors-------------------- 
            PredictorServiceDescription ibmCMPredDesc = generatePredictorServiceDescription();
            datalayer.addPredictor(ibmCMPredDesc);
            
//            addIBMRisk(datalayer);
//            addIBMOp(datalayer);
        } catch (Exception ex) {
            throw new RuntimeException("URI syntac exception", ex);
        }
    }

    public static void main(String[] args) {

        System.out.println("************************************************************************************************");
        System.out.println("THIS WILL RUN THE SCRIPT AGAINST THE DATABASE. THIS WILL DROP AND RECREATE THE DB STRUCTURE");
        System.out.println("************************************************************************************************");

        PopulateIBM pop=new PopulateIBM();
        
        BasicConfigurator.configure();
        pop.getConfigs();
        DataLayerImpl datalayer = null;
        try {
            datalayer = new DataLayerImpl();
             logger.debug("Creating DB using script at "+scriptPath);
            datalayer.runScript(scriptPath);
        } catch (Exception ex) {
            logger.debug("Error while getting the mysql schema script at "+scriptPath+". Will continue assuming that the DB exist and is empty. " + ex.getMessage());
            //throw new RuntimeException(ex);
        }


        pop.populateIBM(datalayer);


        //------------ print all risks ------------------
                   /*
         * Set<Risk> risks = datalayer.getRisks("IBM Community"); for (Risk temp
         * : risks) {
         *
         * logger.debug("\n****Risk: "); logger.debug( " uuid: " + temp.getId()
         * + "\n" + " title:" + temp.getTitle() + "\n" + " owner: " +
         * temp.getOwner() + "\n" +" type: "+temp.getType()+"\n" +" expirydate:
         * "+temp.getExpiryDate()+"\n" +" admin_review_freq: " +
         * temp.getAdmin_review_freq()+"\n" +" cat_review_freq: " +
         * temp.getCat_review_freq()+"\n" +" group: " + temp.getGroup()+"\n" +"
         * state: " + temp.getState()+"\n" +" scope: " + temp.getScope()+"\n" +"
         * cat_review_period: " + temp.getCat_review_period()+"\n" +"
         * admin_review_period: " + temp.getAdmin_review_period());
         *
         * if (temp.getImpact() != null && temp.getImpact().getImpactMap() !=
         * null) { logger.debug(" Impact: "); for (Objective obj :
         * temp.getImpact().getImpactMap().keySet()) { logger.debug( " obj id: "
         * + obj.getId() + " title:" + obj.getTitle() + " impactlevel: " +
         * temp.getImpact().getImpactMap().get(obj).name() + " impactPolarity: "
         * + temp.getImpact().getImpactMap().get(obj).isIsPositive()); } }
         *
         * if (temp.getEvent() != null) { logger.debug(" Event: " +
         * temp.getEvent().getTitle()); logger.debug( " Event param: " +
         * temp.getEvent().getParamName() + " precond value: " +
         * temp.getEvent().getPrecondValue() + " postcond value: " +
         * temp.getEvent().getPostcondValue()); }
         *
         *
         * if(temp.getTreatment()!=null){ logger.debug(" Treatment: ");
         * for(Entry<Float,UUID> entry:temp.getTreatment().getWorkflowIDs()){
         * logger.debug(" Priority: "+entry.getKey()+", uuid:
         * "+entry.getValue()); } }
         *
         *
         * }
         */
        //END of scenarios



    }
    
    
    private PredictorServiceDescription generatePredictorServiceDescription() {
        try {
            getConfigs();
            PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), ibmPredName, ibmPredVer, ibmPredDesc);
            desc.setServiceURI(new URI(ibmCMPredURI));
            desc.setServiceTargetNamespace(ibmCMPredNamespace);
            desc.setServiceName(ibmCMPredServiceName);
            desc.setServicePortName(ibmCMPredPortName);

            // PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), "CM Predictor Service", "1.0", "CM Predictor service)");

            // We first start to define the forecast period
            forecastPeriod = new Parameter(ParameterValueType.INT, "Forecast period", "The available forecast period options", "weeks");
            forecastPeriod.addValueConstraint("1", ValueConstraintType.DEFAULT);
            forecastPeriod.addValueConstraint("1", ValueConstraintType.MIN);
            forecastPeriod.addValueConstraint("52", ValueConstraintType.MAX);
            forecastPeriod.addValueConstraint("1", ValueConstraintType.STEP);
            desc.setForecastPeriod(forecastPeriod);


            // Role parameter
           Parameter roleParamIBM = new Parameter(ParameterValueType.STRING, "Roles for the IBM use case", "Insert the name of the role", "");
            roleParamIBM.addValueConstraint("Inactive", ValueConstraintType.DEFAULT);
            roleParamIBM.addValueConstraint("Inactive", ValueConstraintType.VALUESALLOWED);                      // 0 
            roleParamIBM.addValueConstraint("Lurker", ValueConstraintType.VALUESALLOWED);                        // 1
            roleParamIBM.addValueConstraint("Contributor", ValueConstraintType.VALUESALLOWED);                        // 2
            roleParamIBM.addValueConstraint("Super User", ValueConstraintType.VALUESALLOWED);                        // 3
            roleParamIBM.addValueConstraint("Follower", ValueConstraintType.VALUESALLOWED);                        // 4
            roleParamIBM.addValueConstraint("BroadCaster", ValueConstraintType.VALUESALLOWED);                        // 5
            roleParamIBM.addValueConstraint("Daily User", ValueConstraintType.VALUESALLOWED);                        // 6
            roleParamIBM.addValueConstraint("Leader", ValueConstraintType.VALUESALLOWED);                        // 7
            roleParamIBM.addValueConstraint("Celebrity", ValueConstraintType.VALUESALLOWED);                        // 8
            roleParamIBM.addValueConstraint("Unmatched", ValueConstraintType.VALUESALLOWED);                        // -1

            // events (s)

            //////// Now we start to define the events where each event will have their own parameter that needs to be specified

            // First event
            /* The event that a particular role has dropped below a certain threshold 
             * Finding the probability that it is below a value i.e. P(X \le x) 
             */
            UUID eventUUID1 = UUID.fromString("714c0d86-a828-4d1f-a9d4-c9335839b919");
            roleDecreaseEvent = new Event("Decrease in percentage of user in role 1", "");
            roleDecreaseEvent.setUuid(eventUUID1);
            roleDecreaseEventCond = new EventCondition(ParameterValueType.FLOAT, "Decrease in percentage of number of users in a role", "Percentage change in total number of user", "");
            roleDecreaseEventCond.setValuesAllowedType(ValuesAllowedType.SINGLE);
            roleDecreaseEventCond.addValueConstraint("-10", ValueConstraintType.DEFAULT);
            roleDecreaseEventCond.addValueConstraint("-100", ValueConstraintType.MIN);
            roleDecreaseEventCond.addValueConstraint("0", ValueConstraintType.MAX);
            roleDecreaseEvent.setEventCondition(roleDecreaseEventCond);
            roleParamIBMDec = new Parameter(roleParamIBM);
            roleParamIBMDec.setUUID(UUID.randomUUID());
            roleDecreaseEvent.addConfigParam(roleParamIBMDec);
            desc.addEvent(roleDecreaseEvent);

            // Second event - same as the first
            UUID eventUUID12 = UUID.fromString("674b4887-ef25-49e7-9a3f-7b12af9fc788");
            roleDecreaseEvent12 = new Event("Decrease in percentage of user in role 2", "");
            roleDecreaseEvent12.setUuid(eventUUID12);
            roleDecreaseEventCond12 = new EventCondition(ParameterValueType.FLOAT, "Decrease in percentage of number of users in a role", "Percentage change in total number of users", "");
            roleDecreaseEventCond12.setValuesAllowedType(ValuesAllowedType.SINGLE);
            roleDecreaseEventCond12.addValueConstraint("-10", ValueConstraintType.DEFAULT);
            roleDecreaseEventCond12.addValueConstraint("-100", ValueConstraintType.MIN);
            roleDecreaseEventCond12.addValueConstraint("0", ValueConstraintType.MAX);
            roleDecreaseEvent12.setEventCondition(roleDecreaseEventCond12);
            roleParamIBMDec12 = new Parameter(roleParamIBM);
            roleParamIBMDec12.setUUID(UUID.randomUUID());
            roleDecreaseEvent12.addConfigParam(roleParamIBMDec12);
            desc.addEvent(roleDecreaseEvent12);

            // Second event
            /* Inverse of the first, which means we want to know the probability that 
             * it we have increased by x\% or more, i.e. P(X \ge x)
             */
            UUID eventUUID2 = UUID.fromString("54156207-7bd8-41d4-9923-22e4f331499b");
            roleIncreaseEvent = new Event("Increase in percentage of user in a role", "");
            roleIncreaseEvent.setUuid(eventUUID2);
            roleIncreateEvtCnd = new EventCondition(ParameterValueType.FLOAT, "Increase in percentage of number of users in a role", "Percentage change in total number of users", "");
            roleIncreateEvtCnd.setValuesAllowedType(ValuesAllowedType.SINGLE);
            roleIncreateEvtCnd.addValueConstraint("10", ValueConstraintType.DEFAULT);
            roleIncreateEvtCnd.addValueConstraint("0", ValueConstraintType.MIN);
            roleIncreateEvtCnd.addValueConstraint("1000", ValueConstraintType.MAX);
            roleIncreateEvtCnd.setPostConditionValue(new ParameterValue("10"));
            roleIncreaseEvent.setEventCondition(roleIncreateEvtCnd);
            roleParamIBMInc2 = new Parameter(roleParamIBM);
            roleParamIBMInc2.setUUID(UUID.randomUUID());
            roleIncreaseEvent.addConfigParam(roleParamIBMInc2);
            desc.addEvent(roleIncreaseEvent);

            // The third and fourth event.  Same as the second event but instead of using a threshold, we want to use an absolute number
            UUID eventUUID3 = UUID.fromString("4552723d-3c2a-46e5-a2dc-a31e6e4cee6e");
            Event evt3 = new Event("Number of user below a value", "");
            evt3.setUuid(eventUUID3);
            EventCondition evtCond3 = new EventCondition(ParameterValueType.FLOAT, "Number of Users below the specified value", "Number of user below the specified value", "");
            evtCond3.setValuesAllowedType(ValuesAllowedType.SINGLE);
            evtCond3.addValueConstraint("10", ValueConstraintType.DEFAULT);
            evtCond3.addValueConstraint("0", ValueConstraintType.MIN);
            evtCond3.addValueConstraint("1000", ValueConstraintType.MAX);
            evtCond3.setPostConditionValue(new ParameterValue("10"));
            evt3.setEventCondition(evtCond3);
            Parameter roleParamIBMcopy3 = new Parameter(roleParamIBM);
            roleParamIBMcopy3.setUUID(UUID.randomUUID());
            evt3.addConfigParam(roleParamIBMcopy3);
            desc.addEvent(evt3);

            UUID eventUUID4 = UUID.fromString("1552723d-3c2a-46e5-a2dc-a31e6e4cee6e");
            Event evt4 = new Event("Number of user above a value", "");
            evt4.setUuid(eventUUID4);
            EventCondition evtCond4 = new EventCondition(ParameterValueType.FLOAT, "Number of Users above the specified value", "Number of user above the specified value", "");
            evtCond4.setValuesAllowedType(ValuesAllowedType.SINGLE);
            evtCond4.addValueConstraint("10", ValueConstraintType.DEFAULT);
            evtCond4.addValueConstraint("0", ValueConstraintType.MIN);
            evtCond4.addValueConstraint("1000", ValueConstraintType.MAX);
            evtCond4.setPostConditionValue(new ParameterValue("10"));
            evt4.setEventCondition(evtCond4);
            Parameter roleParamIBMcopy4 = new Parameter(roleParamIBM);
            roleParamIBMcopy4.setUUID(UUID.randomUUID());
            evt4.addConfigParam(roleParamIBMcopy4);
            desc.addEvent(evt4);

            
            
            ///-----add predictor configs values for this scenario

            //roleDecreaseEventCond.setPostConditionValue(new ParameterValue("-0.2"));
            roleDecreaseEventCond.setPostConditionValue(new ParameterValue("10"));
            roleDecreaseEventCond12.setPostConditionValue(new ParameterValue("10"));
            roleIncreateEvtCnd.setPostConditionValue(new ParameterValue("10"));
            
            ParameterValue forecastPeriodVal = new ParameterValue("5");
            forecastPeriod.setValue(forecastPeriodVal);

            return desc;
        } catch (Exception ex) {
            throw new RuntimeException("error while generating IBM risk predictor description ", ex);
        }
    }


    
   private PredictorServiceDescription generateOPDemoPSD() 
   {
       return null;
   }
    
}
