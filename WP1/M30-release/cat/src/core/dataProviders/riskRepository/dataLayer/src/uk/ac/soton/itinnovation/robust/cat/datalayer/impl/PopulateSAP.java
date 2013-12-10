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
//      Created Date :          20-Nov-2012
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
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;
import uk.ac.soton.itinnovation.robust.riskmodel.*;

public class PopulateSAP {

    //---------SAP communities
    private String sapAbapName = "SAP ABAP General";
    private String sapAbapID = "http://forums.sdn.sap.com/uim/forum/50#id";
    private String sapBusinessOneName = "SAP Business One SDK";
    private String sapBusinessOneID = "http://forums.sdn.sap.com/uim/forum/56#id";
    private String sapProcessIntName = "Process Integration ";
    private String sapProcessIntID = "http://forums.sdn.sap.com/uim/forum/44#id";
    private String sapGRCName = "Governance, Risk and Compliance";
    private String sapGRCID = "http://forums.sdn.sap.com/uim/forum/256#id";
    private String sapBOReportName = "SAP Business One Reporting & Printing ";
    private String sapBOReportID = "http://forums.sdn.sap.com/uim/forum/353#id";
    private String sapNetWeaverName = "Business Planning and Consolidations for SAP NetWeaver ";
    private String sapNetWeaverID = "http://forums.sdn.sap.com/uim/forum/412#id";
    private String sapMSName = "Business Planning and Consolidations, for MS Platform ";
    private String sapMSID = "http://forums.sdn.sap.com/uim/forum/413#id";
    private String sapBOAddonName = "SAP Business One - SAP Add-on ";
    private String sapBOAddonID = "http://forums.sdn.sap.com/uim/forum/418#id";
    //--------------objectives--------------------
    private Objective sapobj1 = null;
    private Objective sapobj2 = null;
    private Objective sapobj3 = null;
    private static String scriptPath = null;
    static final Logger logger = Logger.getLogger(PopulateSAP.class);
    
    //predictor info to be pulled from properties file
    private static String sapPredName = null;
    private static String sapPredVer = null;
    private static String sapPredDesc = null;
    private static String sapPredURI = null;
    private static String sapPredNamespace = null;
    private static String sapPredServiceName = null;
    private static String sapPredPortName = null;
    EventCondition evtCond = null;
    Event evt = null;
    ///treatment uuid
    static final String treatUUID = "01ac6047-4f13-4959-aa52-a9b392edefb0";

    private void getConfigs() {
        Properties prop = new Properties();

        try {
            prop.load(PopulateDB.class.getClassLoader().getResourceAsStream("data.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("Error with loading configuration file data.properties. " + ex.getMessage(), ex);
        }


        sapPredName = prop.getProperty("sapGSpredname");
        sapPredDesc = prop.getProperty("sapGSpredDesc");
        sapPredVer = prop.getProperty("sapGSpredVer");

        sapPredURI = prop.getProperty("sapGSpredURI");
        sapPredNamespace = prop.getProperty("sapGSpredNamespace");
        sapPredServiceName = prop.getProperty("sapGSpredSvcname");
        sapPredPortName = prop.getProperty("sapGSpredPortname");

        scriptPath = prop.getProperty("repoSQLSchema");

    }

    public static void main(String[] args) {
        System.out.println("************************************************************************************************");
        System.out.println("THIS WILL RUN THE SCRIPT AGAINST THE DATABASE. THIS WILL DROP AND RECREATE THE DB STRUCTURE");
        System.out.println("************************************************************************************************");

        PopulateSAP pop = new PopulateSAP();

        BasicConfigurator.configure();
        pop.getConfigs();
        DataLayerImpl datalayer = null;
        try {
            datalayer = new DataLayerImpl();
            logger.debug("Creating DB using script at " + scriptPath);
            datalayer.runScript(scriptPath);
        } catch (Exception ex) {
            logger.debug("Error while getting the mysql schema script at " + scriptPath + ". Will continue assuming that the DB exist and is empty. " + ex.getMessage());
            //throw new RuntimeException(ex);
        }

        try {
            pop.populateSAP(datalayer);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Community addSAPCommunity(String comName, String comID, DataLayerImpl datalayer) {
        try {
            sapobj1 = new Objective("Customer support", "Supporting customers by answering questions about products in a timely manner.");
            sapobj2 = new Objective("Healthy community growth", "More users in the community (whilst not compromising customer support).");
            sapobj3 = new Objective("High quality of content", "High quality of answers given - avoiding spam and malicious content.");

            URI uri = new URI("sap.it-innovation.soton.ac.uk");


            String streamName = null;

            Community community = new Community(comName, uri, Boolean.FALSE, comID, streamName);

            datalayer.addCommunity(community);

            logger.debug("setting objective obj1 for community " + comName);
            sapobj1 = datalayer.addObjective(community.getUuid(), sapobj1);

            logger.debug("setting objective obj2 for community " + comName);
            sapobj2 = datalayer.addObjective(community.getUuid(), sapobj2);

            logger.debug("setting objective obj2 for community " + comName);
            sapobj3 = datalayer.addObjective(community.getUuid(), sapobj3);

            return community;
        } catch (Exception ex) {
            throw new RuntimeException("error adding SAP community and objectives", ex);
        }

    }

    public void addSAPRisk(Community community, DataLayerImpl datalayer) {

        try {
            //-------------- add risks -----------------------
            logger.debug("saving risk: sap Risk of community becoming inactive");
            Risk sapInactiveUsersRisk = datalayer.saveRisk(community, "Risk of users becoming inactive", "manager", Boolean.TRUE, "Default group");
            sapInactiveUsersRisk.setScope(Scope.COMMUNITY);
            sapInactiveUsersRisk.setState(RiskState.ACTIVE);

            //---------------- add risk event ---------------------
            logger.debug("");
            logger.debug("adding risk1:community becoming inactive, event");

            ParameterValue postcon = new ParameterValue("0.2", EvaluationType.GREATER_OR_EQUAL);
            evtCond.setPostConditionValue(postcon);

            List<EventCondition> conds = new ArrayList<EventCondition>();
            conds.add(evtCond);
            evt.setEventConditions(conds);
            sapInactiveUsersRisk.addEvent(evt);

            try {
                datalayer.updateRisk(sapInactiveUsersRisk);
            } catch (Exception ex) {
                logger.debug("couldnt update risk " + ex.getMessage(), ex);
            }


            // ---------------- add risk1 impact----------------------------

            logger.debug("");
            logger.debug("adding risk impact");
            Map<Objective, ImpactLevel> sapimpactMap = new HashMap<Objective, ImpactLevel>();

            ImpactLevel sapimpLevel11 = ImpactLevel.NEG_HIGH;
//        sapimpLevel11.setIsPositive(false);

            ImpactLevel sapimpLevel12 = ImpactLevel.NEG_MEDIUM;
//        sapimpLevel12.setIsPositive(false);

            sapimpactMap.put(sapobj1, sapimpLevel11);
            sapimpactMap.put(sapobj2, sapimpLevel12);

            Impact sapimpact = new Impact(sapimpactMap);

            sapInactiveUsersRisk.setImpact(sapimpact);

            try {
                datalayer.updateRisk(sapInactiveUsersRisk);
            } catch (Exception ex) {
                logger.debug("couldnt update risk " + ex.getMessage(), ex);
            }

            //----------------- add treatment--------------------------
//        logger.debug("");
//        logger.debug("adding risk1 treatment");
//        TreatmentWFs treatment1 = new TreatmentWFs();
//        TreatmentTemplate exampleTemplate = new TreatmentTemplate( "Manage drop in user activity",
//                                                                   "Use this treatment to try to prevent any further drop in user activity with your forum",
//                                                                   UUID.fromString(treatUUID) );
//        
//        treatment1.addTreatmentTemplate( exampleTemplate, 1.0f );
//
//        sapInactiveUsers.setTreatment(treatment1);
//
//        try {
//            datalayer.updateRisk(sapInactiveUsers);
//        } catch (Exception ex) {
//            logger.debug("couldnt update risk " + ex.getMessage(), ex);
//        }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public void populateSAP(DataLayerImpl datalayer) {
        getConfigs();
        //--------------add predictors-------------------- 
        PredictorServiceDescription sapCMPredDesc = generatePredictorServiceDescription();
        datalayer.addPredictor(sapCMPredDesc);

        Community comm;
        //add the different communities
        comm = addSAPCommunity(sapAbapName, sapAbapID, datalayer);
        //add drop activity risk to each
        addSAPRisk(comm, datalayer);

        comm = addSAPCommunity(sapBOAddonName, sapBOAddonID, datalayer);
        //add drop activity risk to each
        addSAPRisk(comm, datalayer);

        comm = addSAPCommunity(sapBOReportName, sapBOReportID, datalayer);
        //add drop activity risk to each
        addSAPRisk(comm, datalayer);

        comm = addSAPCommunity(sapBusinessOneName, sapBusinessOneID, datalayer);
        //add drop activity risk to each
        addSAPRisk(comm, datalayer);

        comm = addSAPCommunity(sapGRCName, sapGRCID, datalayer);
        //add drop activity risk to each
        addSAPRisk(comm, datalayer);

        comm = addSAPCommunity(sapMSName, sapMSID, datalayer);
        //add drop activity risk to each
        addSAPRisk(comm, datalayer);

        comm = addSAPCommunity(sapNetWeaverName, sapNetWeaverID, datalayer);
        //add drop activity risk to each
        addSAPRisk(comm, datalayer);

        comm = addSAPCommunity(sapProcessIntName, sapProcessIntID, datalayer);
        //add drop activity risk to each
        addSAPRisk(comm, datalayer);






    }

    private PredictorServiceDescription generatePredictorServiceDescription() {
        PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), sapPredName, sapPredVer, sapPredDesc);

        try {
            desc.setServiceURI(new URI(sapPredURI));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        desc.setServiceTargetNamespace(sapPredNamespace);
        desc.setServiceName(sapPredServiceName);
        desc.setServicePortName(sapPredPortName);


        // event(s)
        // UUID eventUUID = UUID.fromString("7b8bca1f-0a8a-4321-a885-ecec28ccbfef");
        UUID eventUUID = UUID.randomUUID();

        evt = new Event("User activity drop", "Description");
        evt.setUuid(eventUUID);
        evtCond = new EventCondition(ParameterValueType.FLOAT, "Activity Threshold", "", "");
        evtCond.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond.addValueConstraint("0.2", ValueConstraintType.DEFAULT);
        evtCond.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond.addValueConstraint("1", ValueConstraintType.MAX);
        evt.setEventCondition(evtCond);

        desc.addEvent(evt);

        // size of timewindow
        // It is assumed that the time window will always be based on days and not a value smaller than that i.e. hours
        // To change: it is required to change ParameterValueType.INT to ParameterValueType.FLOAT
        Parameter snapshotTimeWindow = new Parameter(ParameterValueType.FLOAT, "Size of the time window", "defines the total number of days for each covariate", "days");
        snapshotTimeWindow.addValueConstraint("7", ValueConstraintType.DEFAULT);
        snapshotTimeWindow.addValueConstraint("1", ValueConstraintType.MIN);
        snapshotTimeWindow.addValueConstraint("28", ValueConstraintType.MAX);

        ParameterValue snapshotTimeWindowVal = new ParameterValue("3");
        snapshotTimeWindow.setValue(snapshotTimeWindowVal);
        // snapshotTimeWindow.addValueConstraint("1", ValueConstraintType.STEP);
//      bmn-  desc.setForecastPeriod(snapshotTimeWindow);
        desc.addConfigurationParam(snapshotTimeWindow);

        // number of covariate aka number of snapshots required for prediction
        // The total number of covariate is the same as the total number of observation as Y can be inferred from X
        // and X is going to be acting as covariate
        // Intercept is not included
        // Max is yet to be decided due to other restrictions that may apply i.e. the amount of historical observation currently available
        Parameter numCovariates = new Parameter(ParameterValueType.INT, "The total number of covariates", "total number of covariates in the GS", "");
        numCovariates.addValueConstraint("5", ValueConstraintType.DEFAULT);
        numCovariates.addValueConstraint("3", ValueConstraintType.MIN);
        numCovariates.addValueConstraint("50", ValueConstraintType.MAX);
        numCovariates.addValueConstraint("1", ValueConstraintType.STEP);

        ParameterValue numCovariatesVal = new ParameterValue("3");
        numCovariates.setValue(numCovariatesVal);
        // desc.setForecastPeriod(numCovariates);
        desc.addConfigurationParam(numCovariates);

        // Definition of 'user' being active
        // Sets requirement that all users must have a total activity over the prediction period greater than this threshold to ensure that covariate matrix is not sparse & focus on key users
        // Note: for snapshots where 'users' do not 'exist' they have activity -1.
        // Question: Is there any way to set the max to the max within the database - thinking not.
        Parameter activeThreshold = new Parameter(ParameterValueType.FLOAT, "The numeric definition of Active", "The minimum activity for a user to be modelled", "");
        activeThreshold.addValueConstraint("0.0", ValueConstraintType.DEFAULT);
        activeThreshold.addValueConstraint("0.0", ValueConstraintType.MIN);
        activeThreshold.addValueConstraint("100", ValueConstraintType.MAX);

        ParameterValue activeThresholdVal = new ParameterValue("0");
        activeThreshold.setValue(activeThresholdVal);
        // activeThreshold.addValueConstraint("1", ValueConstraintType.STEP);
        // desc.setForecastPeriod(activeThreshold);   
        desc.addConfigurationParam(activeThreshold);

        // 'user' feature representing activity
        // This would change between communities i.e. in SAP we have points but not in IBM.
        // SAP: points/posts/in-degree/out-degree/...
        Parameter activityFeature = new Parameter(ParameterValueType.STRING, "The user feature", "chosen measure of user activity", "");
        activityFeature.addValueConstraint("Posts", ValueConstraintType.DEFAULT);
        activityFeature.addValueConstraint("Points", ValueConstraintType.VALUESALLOWED);
        activityFeature.addValueConstraint("Posts", ValueConstraintType.VALUESALLOWED);

        ParameterValue activityFeatureVal = new ParameterValue("Points");
        activityFeature.setValue(activityFeatureVal);

        // activityFeature.addValueConstraint("3", ValueConstraintType.MIN);
        // activityFeature.addValueConstraint("28", ValueConstraintType.MAX);
        // activityFeature.addValueConstraint("1", ValueConstraintType.STEP);
        // desc.setForecastPeriod(activityFeature);     
        desc.addConfigurationParam(activityFeature);


        // configuration parameters (if any)
//        List<Parameter> configParams = new ArrayList<Parameter>();
//        Parameter configParam = new Parameter(ParameterValueType.STRING, "Uber mode", "Does some really nifty stuff", "Mode");
//        configParam.addValueConstraint("on", ValueConstraintType.DEFAULT);
//        configParam.addValueConstraint("on", ValueConstraintType.VALUESALLOWED);
//        configParam.addValueConstraint("off", ValueConstraintType.VALUESALLOWED);
//        configParams.add(configParam);
//        desc.setConfigurationParams(configParams);

        return desc;
    }
}
