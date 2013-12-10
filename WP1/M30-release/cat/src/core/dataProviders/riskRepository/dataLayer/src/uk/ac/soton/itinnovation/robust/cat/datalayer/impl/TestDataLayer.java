/////////////////////////////////////////////////////////////////////////
//
// Â© University of Southampton IT Innovation Centre, 2011
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
//      Created Date :          04-Nov-2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;

import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.jaxws.binding.soap.JaxWsSoapBindingConfiguration;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.Treatment;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.riskmodel.*;

public class TestDataLayer {
  

    static final Logger logger = Logger.getLogger(TestDataLayer.class);

    public static void main(String[] args) {
       
//       
//        Object[] res=null;
//        List<Community> rescom=new ArrayList<Community>();
//        JaxWsDynamicClientFactory dcf=JaxWsDynamicClientFactory.newInstance();
//        
//        Client client=dcf.createClient("robustRODataService.wsdl");
//        try{
//        res=client.invoke("getCommunities", "");
//        for(int i=0;i<res.length;i++){
//            Object obj=res[i];
//            rescom.add((Community)obj);  
//        }
////       
//        }catch(Exception ex){
//            throw new RuntimeException(ex);
//        }
//        
                
        
        
        System.out.println("************************************************************************************************");
        System.out.println("THIS TEST WILL RUN THE SCRIPT ON THE DATABASE. THIS WILL DROP AND RECREATE THE DB STRUCTURE");
        System.out.println("************************************************************************************************");

        BasicConfigurator.configure();
        
        IDataLayer datalayer = null;
        try {
            datalayer = new DataLayerImpl();
            long lDateTime1 = new Date().getTime();
           
          
            datalayer.runScript("C://Users//bmn//Desktop//Projects//robust//code//WP1//cat//trunk//src//core//repository//robust.sql");//../../../repository/robust.sql 
            long lDateTime2 = new Date().getTime();
            
            logger.debug("Time to connect: "+(lDateTime2-lDateTime1));
            System.out.println("Time to runscript: "+(lDateTime2-lDateTime1));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex);
        }

        Objective obj1 = new Objective("obj1", "obj1");
        Objective obj2 = new Objective("obj2", "obj2");
        Objective obj3 = new Objective("obj3", "obj3");
        Objective obj4 = new Objective("obj4", "obj4");
        Objective obj5 = new Objective("obj5", "obj5");
        Objective obj6 = new Objective("obj6", "obj6");


        System.out.println("--------------test insert communities--------------");
        URI uri;
        try {
            uri = new URI("mysql.it-innovation.soton.ac.uk");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        //(String name, UUID uuid, String communityID, String streamName, URI uri, Boolean isStream)
        
        Community com1 = new Community("comm1", UUID.randomUUID(),"id1", null,null,false);
        Community com2 = new Community("comm2", uri, Boolean.FALSE, "","");
        Community com3 = new Community("comm3", uri, Boolean.FALSE, "","");
        Community com4 = new Community("comm4", uri, Boolean.FALSE, "","");
        Community com5 = new Community("comm5", uri, Boolean.FALSE, "","");

        datalayer.addCommunity(com1);
        datalayer.addCommunity(com2);
        datalayer.addCommunity(com3);
        datalayer.addCommunity(com4);
        datalayer.addCommunity(com5);

        Set<Community> comms = datalayer.getCommunities();
        if (comms.size() != 5) {
            throw new RuntimeException("TEST FAILED: Error in number of communities actually stored");
        }


        System.out.println("TEST OK");

        System.out.println("--------------test get community by UUID--------------");
        Community co = datalayer.getCommunityByUUID(com1.getUuid());
        if (!co.equals(com1)) {
            throw new RuntimeException("error when getting community by UUID");
        }

        System.out.println("TEST OK");


        System.out.println("------------test insert objectives-----------");
        System.out.println("setting objective obj1 for community comm1");
        obj1 = datalayer.addObjective(com1.getUuid(), obj1);

        System.out.println("setting objective obj2 for community comm2");
        obj2 = datalayer.addObjective(com2.getUuid(), obj2);

        System.out.println("setting objective obj5 for community comm2");
        obj5 = datalayer.addObjective(com2.getUuid(), obj5);

        System.out.println("setting objective obj3 for community comm3");
        obj3 = datalayer.addObjective(com3.getUuid(), obj3);

        Set<Objective> obstemp=datalayer.getObjectives(com1.getUuid());
                
        if (!(datalayer.getObjectives(com1.getUuid()).contains(obj1))) {
            throw new RuntimeException("Com1 Objectives werenot inserted properly ");
        }
          if (!(datalayer.getObjectives(com2.getUuid()).contains(obj2)
                && datalayer.getObjectives(com2.getUuid()).contains(obj5))) {
            throw new RuntimeException("Com2 Objectives werenot inserted properly");
        }

            if (!(datalayer.getObjectives(com3.getUuid()).contains(obj3))) {
            throw new RuntimeException("Com3 Objectives werenot inserted properly");
        }



        System.out.println("TEST OK");

        System.out.println("------------test delete one objective------------");
        datalayer.deleteObjective(com1.getUuid(), obj1);
        if (datalayer.getObjectives(com1.getUuid()).contains(obj1)) {
            throw new RuntimeException("Objective wasnt deleted!");
        }
        System.out.println("TEST OK");

        System.out.println("------------test deleting non existing objective:----------- ");
        datalayer.deleteObjective(com2.getUuid(), obj1);
        System.out.println("TEST OK");

        System.out.println("---------- test get objectives from DB---------");
        Set<Objective> obs = datalayer.getObjectives(com1.getUuid());
        System.out.println("Comm1 objectives: ");
        for (Objective obj : obs) {
            System.out.println(" " + obj.getDescription());
        }


        obs = datalayer.getObjectives(com2.getUuid());
        System.out.println("Comm2 objectives: ");
        for (Objective obj : obs) {
            System.out.println(" " + obj.getDescription());
        }
        System.out.println("TEST OK");



        System.out.println("---------------test delete community by uuid comm1, this should delete everything related to the community as well--------");
        try {
            datalayer.deleteCommunityByUUID(com1.getUuid());
        } catch (Exception ex) {
            System.out.println(ex);
        }
        System.out.println("TEST OK");

        System.out.println("--------------- test get communities names after deletion ------------");
        comms = datalayer.getCommunities();
        System.out.println("Listing communities after deletion ");
        for (Community com : comms) {
            System.out.println("- " + com.getName());
            if (com.getName().equals("comm1")) {
                throw new RuntimeException("Test Failed. comm1 should have been deleted in the previous step");
            }
        }

        Set<Risk> risks = datalayer.getRisks(com1.getUuid());
        if (!risks.isEmpty()) {
            throw new RuntimeException("Test Failed. comm1 Risks still exist in the DB");
        }
        System.out.println("TEST OK");
//        //-----------test add treatmentWF to the available treatments ----------------
//        UUID uuid = UUID.randomUUID();
//        datalayer.addTreatment(uuid.toString());

        System.out.println("--------------add predictor--------------------");
        PredictorServiceDescription pred = new PredictorServiceDescription("Gibbs", "1.2", "gibbs sampler");
        try {
            pred.setServiceURI(new URI("localhost.com"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        pred.setServiceTargetNamespace("namespace");
        pred.setServiceName("somename");
        //first event
        ValueConstraint consMin = new ValueConstraint("5", ValueConstraintType.MIN);
        ValueConstraint consMax = new ValueConstraint("10", ValueConstraintType.MAX);
        List<ValueConstraint> cons = new ArrayList<ValueConstraint>();
        cons.add(consMax);
        cons.add(consMin);

        EventCondition cond1 = new EventCondition(ParameterValueType.INT, "cond1", "cond1 descript", null, ValuesAllowedType.SINGLE, cons);
        EventCondition cond2 = new EventCondition(ParameterValueType.INT, "cond2", "cond2 descript", null, ValuesAllowedType.SINGLE, cons);

        List<EventCondition> evtConditions = new ArrayList<EventCondition>();
        evtConditions.add(cond1);
        evtConditions.add(cond2);

        Event event = new Event("title", "event description", evtConditions, (List<Parameter>) null);
        
 //add event parameter
        Parameter roleParamIBMDecrease = new Parameter(ParameterValueType.STRING, "Roles for the decrease IBM use case", "Insert the name of the role", "");
        roleParamIBMDecrease.addValueConstraint("Lurker", ValueConstraintType.DEFAULT);
        roleParamIBMDecrease.addValueConstraint("Inactive", ValueConstraintType.VALUESALLOWED);                      // 0 
        roleParamIBMDecrease.addValueConstraint("Lurker", ValueConstraintType.VALUESALLOWED);                        // 1
        roleParamIBMDecrease.addValueConstraint("Contributor", ValueConstraintType.VALUESALLOWED);                        // 2
        roleParamIBMDecrease.addValueConstraint("Super User", ValueConstraintType.VALUESALLOWED);                        // 3
        roleParamIBMDecrease.addValueConstraint("Follower", ValueConstraintType.VALUESALLOWED);                        // 4
        roleParamIBMDecrease.addValueConstraint("BroadCaster", ValueConstraintType.VALUESALLOWED);                        // 5
        roleParamIBMDecrease.addValueConstraint("Daily User", ValueConstraintType.VALUESALLOWED);                        // 6
        roleParamIBMDecrease.addValueConstraint("Leader", ValueConstraintType.VALUESALLOWED);                        // 7
        roleParamIBMDecrease.addValueConstraint("Celebrity", ValueConstraintType.VALUESALLOWED);                        // 8
        roleParamIBMDecrease.addValueConstraint("Unmatched", ValueConstraintType.VALUESALLOWED);         
        event.addConfigParam(roleParamIBMDecrease);
        
        //second certificate
        ValueConstraint consMin12 = new ValueConstraint("15", ValueConstraintType.MIN);
        ValueConstraint consMax12 = new ValueConstraint("22", ValueConstraintType.MAX);
        List<ValueConstraint> cons12 = new ArrayList<ValueConstraint>();
        cons12.add(consMax12);
        cons12.add(consMin12);

        EventCondition cond12 = new EventCondition(ParameterValueType.INT, "cond12", "cond12 descript", null, ValuesAllowedType.SINGLE, cons12);
        EventCondition cond22 = new EventCondition(ParameterValueType.INT, "cond22", "cond22 descript", null, ValuesAllowedType.SINGLE, cons12);

        List<EventCondition> evtConditions12 = new ArrayList<EventCondition>();
        evtConditions12.add(cond12);
        evtConditions12.add(cond22);

        Event event12 = new Event("anothertitle", "event description2", evtConditions12, (List<Parameter>) null);

       
        
        pred.addEvent(event);
        pred.addEvent(event12);
//        pred.addConfigurationParams(null);

        datalayer.addPredictor(pred);

        Set<PredictorServiceDescription> predics = datalayer.getPredictors();
        boolean exist = false;
        System.out.println("current predic svc URI=" + pred.getServiceURI() + ", ServiceName=" + pred.getServiceName());
        for (PredictorServiceDescription predic : predics) {
            if (predic.equals(pred)) {
                System.out.println("equal");
                exist = true;
            }

        }
        if (exist == false) {
            System.out.println("predictor doesnt seem to be properly stored. Check the equals and hashcode methods in predictor");
            throw new RuntimeException("adding predictor failed");
        }

//        if (!predics.contains(pred)) {
//            System.out.println("predictor doesnt seem to be properly stored. Check the equals and hashcode methods in predictor");
//            throw new RuntimeException("adding predictor failed");
//        }

        //  datalayer.deletePredictor(pred.getUuid());
        System.out.println("TEST OK");

        System.out.println("--------------add second predictor--------------------");
        PredictorServiceDescription predCM = new PredictorServiceDescription("CM", "1.2", "CM");
        try {
            predCM.setServiceURI(new URI("localhost.com"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        predCM.setServiceTargetNamespace("namespace");
        predCM.setServiceName("somename");
        ValueConstraint consMinCM = new ValueConstraint("5", ValueConstraintType.MIN);
        ValueConstraint consMaxCM = new ValueConstraint("10", ValueConstraintType.MAX);
        List<ValueConstraint> consCM = new ArrayList<ValueConstraint>();
        consCM.add(consMaxCM);
        consCM.add(consMinCM);

        EventCondition cond1CM = new EventCondition(ParameterValueType.INT, "cond1CM", "cond1CM descript", null, ValuesAllowedType.SINGLE, consCM);
        EventCondition cond2CM = new EventCondition(ParameterValueType.INT, "cond2CM", "cond2CM descript", null, ValuesAllowedType.SINGLE, consCM);

        List<EventCondition> evtConditionsCM = new ArrayList<EventCondition>();
        evtConditionsCM.add(cond1CM);
        evtConditionsCM.add(cond2CM);

        Event eventCM = new Event("titleCM", "event description", evtConditionsCM, (List<Parameter>) null);


        predCM.addEvent(eventCM);
//        pred.addConfigurationParams(null);

        datalayer.addPredictor(predCM);

        Set<PredictorServiceDescription> predicsCM = datalayer.getPredictors();
        boolean existCM = false;
        System.out.println("current predic svc URI=" + predCM.getServiceURI() + ", ServiceName=" + predCM.getServiceName());
        for (PredictorServiceDescription predic : predicsCM) {
            if (predic.equals(predCM)) {
                System.out.println("equal");
                existCM = true;
            }

        }
        if (existCM == false) {
            System.out.println("predictor doesnt seem to be properly stored. Check the equals and hashcode methods in predictor");
            throw new RuntimeException("adding predictor failed");
        }

//        if (!predics.contains(pred)) {
//            System.out.println("predictor doesnt seem to be properly stored. Check the equals and hashcode methods in predictor");
//            throw new RuntimeException("adding predictor failed");
//        }

        //  datalayer.deletePredictor(pred.getUuid());
        System.out.println("TEST OK");


        System.out.println("-------------- add risk -----------------------");
        System.out.println("");
        System.out.println("saving risk title2");
        Risk risk = datalayer.saveRisk(com2, "Risktitle", "manager", Boolean.TRUE, "group");
risk.setState(RiskState.ACTIVE);
        printall(datalayer, com2);

//        if (!datalayer.getRisks(com2.getUuid()).contains(risk)) {
//            throw new RuntimeException("adding risk failed");
//        }
        boolean found = false;
        for (Risk rs : datalayer.getRisks(com2.getUuid())) {
            if (rs.equals(risk)) {
                found = true;
                System.out.println("here\n\n");
                logger.debug("here");
                break;
            }
        }
        if (found == true) {
            System.out.println("TEST OK");
        } else {
            throw new RuntimeException("adding risk failed");
        }
//        System.out.println("");
//         System.out.println("updating risk title 2 newTitle");
//        risk.setTitle("newTitle");
//        risk.setOwner("me");
//        try{
//            datalayer.updateRisk(risk);
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }

        System.out.println("---------------- add risk evaluation results ---------------------");
        try{
        DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        JobStatus jobstatus = new JobStatus(JobStatusType.READY);

        JobDetails job = new JobDetails("ref", jobstatus, new Date(300000000));
        ResultItem itm = new ResultItem("bmn", 0.85);
        ResultItem itm2 = new ResultItem("ve", 0.95);
        List<ResultItem> items = new LinkedList<ResultItem>();
        items.add(itm);
        items.add(itm2);

        // VE: OBS - no current observations set
        EvaluationResult evalRslt = new EvaluationResult(job, new Date(), formatter.parse("2012.02.01"), items, null);
        datalayer.saveEvaluationResults(risk.getId(), evalRslt);
        
         EvaluationResult evalRslt2 = new EvaluationResult(job, new Date(), formatter.parse("2012.03.01"), items, null);
        datalayer.saveEvaluationResults(risk.getId(), evalRslt2);
        
         EvaluationResult evalRslt3 = new EvaluationResult(job, new Date(), formatter.parse("2012.04.01"), items, null);
        datalayer.saveEvaluationResults(risk.getId(), evalRslt3);

        
        System.out.println("---------------- get all risk evaluation results ---------------------");
        Set<EvaluationResult> evals = datalayer.getRiskEvalResults(risk.getId(), null,null);
        if (evals != null && !evals.isEmpty() && evals.size()==3) {
            for (EvaluationResult rst : evals) {
                logger.debug("Eval " + rst.getResultUUID());
                logger.debug("current date: " + rst.getCurrentDate());
                 
                logger.debug("forecast date: "+formatter.format(rst.getForecastDate()));
                logger.debug("Job ref " + rst.getJobDetails().getJobRef());
                for (ResultItem rstitm : rst.getResultItems()) {
                    logger.debug("rstITem: " + rstitm.getName() + " " + rstitm.getProbability());
                }
                
                 System.out.println("Eval " + rst.getResultUUID());
                 System.out.println("current date: " + rst.getCurrentDate());
                 
                 System.out.println("forecast date: "+formatter.format(rst.getForecastDate()));
                 System.out.println("Job ref " + rst.getJobDetails().getJobRef());
                for (ResultItem rstitm : rst.getResultItems()) {
                     System.out.println("rstITem: " + rstitm.getName() + " " + rstitm.getProbability());
                }
            }
        }else 
            throw new RuntimeException("Error during the  evaluation results test. the saved ones are not all there !! ");
        }catch(Exception exc){
    throw new RuntimeException(exc);
}
        System.out.println("TEST OK");

        System.out.println("---------------- delete evaluation results ---------------------");
        datalayer.deleteEvaluationResults(risk.getId());
        Set<EvaluationResult> evals = datalayer.getRiskEvalResults(risk.getId(), null,null);
        if(evals==null || evals.isEmpty())
            System.out.println("TEST OK");
        else
            throw new RuntimeException("Error during the  evaluation results delete test. not all deleted !! ");
        
        System.out.println("---------------- add risk events ---------------------");
        System.out.println("");
        System.out.println("adding risk event instance");
        //1- get the event id to be instantiated
        //2- get the risk op id
        //3- get the pre and post values
        //4- insert new instance
//------------event1-------------
        ParameterValue precon = new ParameterValue("12", EvaluationType.LESS);
        ParameterValue postcon = new ParameterValue("60", EvaluationType.EQUAL);
        cond1.setPreConditionValue(precon);
        cond1.setPostConditionValue(postcon);

        cond2.setPreConditionValue(precon);
        cond2.setPostConditionValue(postcon);
        List<EventCondition> conds = new ArrayList<EventCondition>();
        conds.add(cond1);
        conds.add(cond2);
        event.setEventConditions(conds);
//add event parameter
roleParamIBMDecrease.setValue(new ParameterValue("Lurker", EvaluationType.EQUAL));
event.setConfigParam(roleParamIBMDecrease);



//------------event2-------------        
        ParameterValue precon12 = new ParameterValue("20", EvaluationType.LESS);
        ParameterValue postcon12 = new ParameterValue("50", EvaluationType.EQUAL);
        cond12.setPreConditionValue(precon12);
        cond12.setPostConditionValue(postcon12);

        cond22.setPreConditionValue(precon12);
        cond22.setPostConditionValue(postcon12);
        List<EventCondition> conds12 = new ArrayList<EventCondition>();
        conds12.add(cond12);
        conds12.add(cond22);
        event12.setEventConditions(conds12);

        Set<Event> events = new HashSet<Event>();
        events.add(event);
        events.add(event12);

        risk.setSetEvent(events);
        try {
            datalayer.updateRisk(risk);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("couldnt update risk " + ex.getMessage());
        }
        //check that event was properly stored
        //get events and check whether event is there

        logger.debug("getting risks for comm2");
        risks = datalayer.getRisks(com2.getUuid());

        boolean contained;
        for (Risk rsk : risks) {
            logger.debug("herer");
            for (Event ev : rsk.getSetEvent()) {
                logger.debug("herer");
                contained = false;

                if (risk.containEvent(ev)) {
                    contained = true;
                    break;
                }
                if (contained == false) {
                    throw new RuntimeException("risk wasnt properly updated. ");
                }
            }
        }
System.out.println("TEST OK");
//----------------add another risk + same pred event and different param and pre/post values
        
        System.out.println("saving risk title3");
        Risk risk2 = datalayer.saveRisk(com2, "Risktitle2", "manager", Boolean.TRUE, "group");
        risk2.setState(RiskState.ACTIVE);
        printall(datalayer, com2);
        ParameterValue precon2 = new ParameterValue("55", EvaluationType.LESS);
        ParameterValue postcon2 = new ParameterValue("22");
        cond1.setPreConditionValue(precon2);
        cond1.setPostConditionValue(postcon2);

        cond2.setPreConditionValue(precon2);
        cond2.setPostConditionValue(postcon2);
        List<EventCondition> conds2 = new ArrayList<EventCondition>();
        conds2.add(cond1);
        conds2.add(cond2);
        event.setEventConditions(conds2);
//add event parameter
        roleParamIBMDecrease.setValue(new ParameterValue("Contributer", EvaluationType.GREATER));
        event.setConfigParam(roleParamIBMDecrease);

        Set<Event> events2 = new HashSet<Event>();
        events2.add(event);


        risk2.setSetEvent(events2);
        try {
            datalayer.updateRisk(risk2);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("couldnt update risk " + ex.getMessage());
        }

         logger.debug("getting risks for comm2");
        risks = datalayer.getRisks(com2.getUuid());
        
        if (!risksIncluded(risks, risk2))
            throw new RuntimeException("Error while adding risk2. it is not in the DB");

        printall(datalayer, com2);

System.out.println("Test OK");
//--------------- update event parameter with a value
        System.out.println("---------------- update event parameter ---------------------");
        System.out.println("");
        roleParamIBMDecrease.setValue(new ParameterValue("Expert", EvaluationType.EQUAL));
        try {
            datalayer.updateRisk(risk);
        } catch (Exception ex) {
            throw new RuntimeException("error updating the risk with the new param value. ", ex);
        }
        Risk storedRisk = datalayer.getRiskByUUID(risk.getId());

        boolean containedEv;

        for (Event ev : storedRisk.getSetEvent()) {
            containedEv = false;

            if (risk.containEvent(ev)) {
                containedEv = true;
            }
            if (containedEv == false) {
                throw new RuntimeException("event parameter wasnt properly updated. ");
            }
        }

System.out.println("Test OK");
//----------------------------------------------------------

System.out.println("---------------- get Predictor for event ---------------------");
        System.out.println("");
        Set<PredictorServiceDescription> preds = datalayer.getPredictors();

        PredictorServiceDescription ppp = datalayer.getPredictor(event);
        System.out.println("Test OK");

        /// -------------getactive risks
        Set<Risk> actRisks=datalayer.getActiveRisks(com2.getUuid());
        Map<Community, Set<Risk>> maps = datalayer.getAllActiveRisks();

        ///-----------------------------update risk event condition instances----------------------
        System.out.println("");
        System.out.println("----------------updating risk event condition instance----------------");
        //1- get the event id to be instantiated
        //2- get the risk op id
        //3- get the pre and post values
        //4- insert new instance

        ParameterValue preconUpdated = new ParameterValue("30", EvaluationType.LESS);
        ParameterValue postconUpdated = new ParameterValue("40", EvaluationType.EQUAL);
        cond1.setPreConditionValue(preconUpdated);
        cond1.setPostConditionValue(postconUpdated);

        cond2.setPreConditionValue(preconUpdated);
        cond2.setPostConditionValue(postconUpdated);
        List<EventCondition> condsUpdated = new ArrayList<EventCondition>();
        condsUpdated.add(cond1);
        condsUpdated.add(cond2);
        event.setEventConditions(condsUpdated);

        //risk.addEvent(event);


        try {
            datalayer.updateRisk(risk);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("couldnt update risk " + ex.getMessage());
        }
        //check that event was properly stored
        //get events and check whether event is there

        storedRisk = datalayer.getRiskByUUID(risk.getId());

         if(!risk.equals(storedRisk))
            throw new RuntimeException("error while updating risk event condition instance. risk wasnt properly updated.\"");
         

        ppp = null;
        ppp = datalayer.getPredictor(event);
        if (ppp == null) {
            throw new RuntimeException("couldnt find the predictor for the event");
        }

        System.out.println("Test OK");

        printall(datalayer, com2);

        ///--------- change risk event/predictor to predCM------------------------ 
        System.out.println("");
        System.out.println("----------------change risk event/predictor to predCM----------------");
        //1- get the event id to be instantiated
        //2- get the risk op id
        //3- get the pre and post values
        //4- insert new instance

        ParameterValue preconCM = new ParameterValue("30cm", EvaluationType.LESS);
        ParameterValue postconCM = new ParameterValue("40cm", EvaluationType.EQUAL);
        cond1CM.setPreConditionValue(preconCM);
        cond1CM.setPostConditionValue(postconCM);

        cond2CM.setPreConditionValue(preconCM);
        cond2CM.setPostConditionValue(postconCM);
        List<EventCondition> condsCM = new ArrayList<EventCondition>();
        condsCM.add(cond1CM);
        condsCM.add(cond2CM);
        eventCM.setEventConditions(condsCM);

        Set<Event> events3 = new HashSet<Event>();
        events3.add(eventCM);

        risk.setSetEvent(events3);
        try {
            datalayer.updateRisk(risk);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("couldnt update risk " + ex.getMessage());
        }
        //check that event was properly stored
        //get events and check whether event is there

        storedRisk = datalayer.getRiskByUUID(risk.getId());

          if(!risk.equals(storedRisk))
            throw new RuntimeException("error while changing pred of risk. risk wasnt properly updated.\"");
         
//        for (Event ev : storedRisk.getSetEvent()) {
//            containsCM = false;
//
//            if (risk.containEvent(ev)) {
//                containsCM = true;
//            }
//            if (containsCM == false) {
//                throw new RuntimeException("risk wasnt properly updated.");
//            }
//        }

        ppp = null;
        ppp = datalayer.getPredictor(eventCM);
        if (ppp == null) {
            throw new RuntimeException("couldnt find the predictor for the event");
        }

        System.out.println("Test OK");

        printall(datalayer, com2);


        System.out.println("---------------- test get risk by uuid ----------------------------");
        Risk r = datalayer.getRiskByUUID(risk.getId());
        if (!r.equals(risk)) {
            throw new RuntimeException("error when getting risk by uuid. risks are not equal");
        }
        System.out.println("Test OK");

//        //---------------- delete predictor ---------------------
////        datalayer.deletePredictor(pred.getName(), pred.getVersion()); (Uncomment if needed)

        System.out.println("---------------- test adding treatment process ids to risk----------------------------");
        
        r.addTreatProcID("1");
        r.addTreatProcID("2");
        try {
            datalayer.updateRisk(r);
        } catch (Exception ex) {
            throw new RuntimeException("error when updating risk with new treatment process IDs. " + ex.getMessage());
        }

        Risk rTemp=datalayer.getRiskByUUID(r.getId());
        if(!rTemp.equals(r))
            throw new RuntimeException("error when teating adding treatment process ids. risk wasnot updated properly");

 System.out.println("Test OK");
 
        System.out.println("---------------- test add impact----------------------------");

        System.out.println("");
        System.out.println("adding risk impact");
        Map<Objective, ImpactLevel> impactMap = new HashMap<Objective, ImpactLevel>();

        ImpactLevel impLevel = ImpactLevel.POS_LOW;
//        impLevel.setIsPositive(true);

        ImpactLevel impLevel2 = ImpactLevel.NEG_HIGH;
//        impLevel.setIsPositive(false);

        impactMap.put(obj2, impLevel);
//        impactMap.put(obj5, impLevel2);
        Impact impact = new Impact(impactMap);

        risk.setImpact(impact);

        try {
            datalayer.updateRisk(risk);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("couldnt update risk " + ex.getMessage());
        }

        //check that impact is added into the repository
        risks = datalayer.getRisks(com2.getUuid());

        if (!risksIncluded(risks,risk)) {
            throw new RuntimeException("impact wasnt properly added into risk. ");
        }

        System.out.println("Test OK");

        System.out.println("----------------- updating impacts--------------------------");
        System.out.println("");
        System.out.println("removing risk impact");
        impactMap.remove(obj2);
        System.out.println("removing obj2 uuid:" + obj2.getId());
        try {
            datalayer.updateRisk(risk);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("couldnt update risk " + ex.getMessage());
        }

        //check that impact is added into the repository
        risks = datalayer.getRisks(com2.getUuid());

        boolean fd=false;
        for(Risk rkTemp:risks){
            fd=rkTemp.equals(risk);
        }
        if(fd=false)
            throw new RuntimeException("impact wasnt properly updated. ");
//        if (!risks.contains(risk)) {
//            throw new RuntimeException("impact wasnt properly updated. ");
//        }

        System.out.println("Test OK");

        System.out.println("---------------- test add impact again----------------------------");

        System.out.println("");
        System.out.println("adding risk impact");
        impactMap = new HashMap<Objective, ImpactLevel>();

        impLevel = ImpactLevel.POS_LOW;
//        impLevel.setIsPositive(true);

        impLevel2 = ImpactLevel.NEG_HIGH;
//        impLevel.setIsPositive(false);

        impactMap.put(obj2, impLevel);
//        impactMap.put(obj5, impLevel2);
        impact = new Impact(impactMap);

        risk.setImpact(impact);

        try {
            datalayer.updateRisk(risk);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("couldnt update risk " + ex.getMessage());
        }

        //check that impact is added into the repository
        risks = datalayer.getRisks(com2.getUuid());

        if (!risksIncluded(risks, risk)) {
            throw new RuntimeException("impact wasnt properly added into risk. ");
        }

        System.out.println("Test OK");


        System.out.println("----------------- test add treatment--------------------------");
        System.out.println("");
        System.out.println("adding risk treatment");
        TreatmentWFs treatment = new TreatmentWFs();
        
        TreatmentTemplate exampleTemplate = new TreatmentTemplate( "Manage drop in user activity",
                                                                   "Use this treatment to try to prevent any further drop in user activity with your forum",
                                                                   UUID.fromString("01ac6047-4f13-4959-aa52-a9b392edefb0") );
        treatment.addTreatmentTemplate( exampleTemplate, 1.0f );
        
        risk.setTreatment(treatment);

        try {
            datalayer.updateRisk(risk);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("couldnt update risk " + ex.getMessage());
        }

        //check that treatment is added into the repository
        Risk tempRisk=datalayer.getRiskByUUID(risk.getId());
        //risks = datalayer.getRisks(com2.getUuid());

//        if (!risks.contains(risk)) 
        if(!tempRisk.equals(risk)) {
            throw new RuntimeException("treatment wasnt properly added to repository. ");
        }

        System.out.println("Test OK");

//        System.out.println("----------------- updating treatments--------------------------");
//        System.out.println("");
//        System.out.println("removing risk treatment");
//        treatment.removeWorkflow(UUID.fromString("abdef864-cfca-419e-b94e-bca1f11a06d7"));
//        try {
//            datalayer.updateRisk(risk, null);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            System.out.println("couldnt update risk " + ex.getMessage());
//        }
//
//        //check that impact is added into the repository
//        risks = datalayer.getRisks("comm2");
//
//        if (!risks.contains(risk)) {
//            throw new RuntimeException("treatment wasnt properly updated. ");
//        }
//
//        System.out.println("Test OK");
        System.out.println("------------ delete risk ------------------");
        risks = datalayer.getRisks(com2.getUuid());
        if (risks != null && !risks.isEmpty()) {
            try {
                datalayer.deleteRisk(((Risk) risks.toArray()[0]).getId());
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(TestDataLayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("------------ print all risks ------------------");
        printall(datalayer, com2);



    }

    public static void printall(IDataLayer datalayer, Community com) {
        System.out.println("------------ print all risks ------------------");
        Set<Risk> risks = datalayer.getRisks(com.getUuid());
        int i = 1;
        for (Risk temp : risks) {        
            System.out.println("\n****Risk " + i + "/" + risks.size());
            System.out.println(
                    "  UUID: " + temp.getId() + "\n"
                    + "  Title:" + temp.getTitle() + "\n");

            for (Event ev : temp.getSetEvent()) {
                PredictorServiceDescription pred = datalayer.getPredictor(ev);
                System.out.println("  Predictor name: " + pred.getName() + ", at " + pred.getServiceURI());
                System.out.println("  Event: " + ev.getTitle());

                if (ev.getConfigParams() != null) {
                    for (Parameter param : ev.getConfigParams()) {
                        System.out.println("Event param: " + param.getName());
                        System.out.println("  param max: " + param.getMax());
                        System.out.println("  param min: " + param.getMin());
                        System.out.println("  param default: " + param.getDefaultValue());
                        System.out.println("  param value: " + param.getValue());
                        System.out.println("  param value eval type: " + param.getValue().getValueEvaluationType());
                    }
                }

                System.out.println(
                        "     Event conditions: " + ev.getEventConditions().size());
                
                for (EventCondition evCond : ev.getEventConditions()) {
                    System.out.println("     - condition name: " + evCond.getName());
                    System.out.println("        condition type: " + evCond.getType());
                    System.out.println("        precondition value: " + evCond.getPreConditionValue().getValue());
                    System.out.println("        postcondition value: " + evCond.getPostConditionValue().getValue());
                }
            }

            if (temp.getImpact() != null && temp.getImpact().getImpactMap() != null) {
                System.out.println("  Impact: ");
                for (Objective obj : temp.getImpact().getImpactMap().keySet()) {
                    System.out.println(
                            "     obj id: " + obj.getId()
                            + "     title:" + obj.getTitle()
                            + "     impactlevel: " + temp.getImpact().getImpactMap().get(obj).name() /*
                             * + " impactPolarity: " +
                             * temp.getImpact().getImpactMap().get(obj).isIsPositive()
                             */);
                }
            }


            if (temp.getTreatment() != null) {
                    System.out.println("  Treatment templates: ");
                    
                    List<Map.Entry<Float,TreatmentTemplate>> templates = temp.getTreatment().getOrderedCopyOfTreatmentTemplates();
                    for ( Map.Entry<Float,TreatmentTemplate> tempEntry : templates )
                      System.out.println("     Template: " + tempEntry.getValue().getTitle() + ", uuid: " +  tempEntry.getValue().getID().toString() );
                
                }

            System.out.println(
                    "  Other:" + "\n"
                    + "    owner: " + temp.getOwner() + "\n"
                    + "    type: " + temp.getType() + "\n"
                    + "    expirydate: " + temp.getExpiryDate() + "\n"
                    + "    admin_review_freq: " + temp.getAdmin_review_freq() + "\n"
                    + "    cat_review_freq: " + temp.getCat_review_freq() + "\n"
                    + "    group: " + temp.getGroup() + "\n"
                    + "    state: " + temp.getState() + "\n"
                    + "    scope: " + temp.getScope() + "\n"
                    + "    cat_review_period: " + temp.getCat_review_period() + "\n"
                    + "    admin_review_period: " + temp.getAdmin_review_period());
            i++;
        }
    }
    
    public static boolean risksIncluded(Set<Risk> risks, Risk risk){
        boolean found=false;
        
        for(Risk r:risks){
            if(risk.equals(r)){
                found=true;
                break;
            }
        }
        
        return found;
        
    }
}
