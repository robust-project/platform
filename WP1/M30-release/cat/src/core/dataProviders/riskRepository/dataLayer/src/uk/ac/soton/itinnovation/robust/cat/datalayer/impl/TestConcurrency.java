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
import java.text.DateFormat;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.Treatment;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.riskmodel.*;

public class TestConcurrency {
   

    static final Logger logger = Logger.getLogger(TestDataLayer.class);

    public static void main(String[] args) throws InterruptedException {
        
     
         
        System.out.println("************************************************************************************************");
        System.out.println("THIS TEST WILL RUN TWO THREADS to READ DATA FROM DB");
        System.out.println("************************************************************************************************");

        BasicConfigurator.configure();

        Thread t1 = new Thread(new WriteThread1());
        t1.start();
        
        Thread t = new Thread(new ReadThread1());
        t.start();

    }
    
   void killIfLong(Thread t, Thread t1) throws InterruptedException{
          long startTime = System.currentTimeMillis();
        long patience = 1 * 60 * 60;
          threadMessage("Waiting for read thread to finish");
        // loop until MessageLoop
        // thread exits
        while (t.isAlive() || t1.isAlive()) {
            threadMessage("Still waiting...");
            // Wait maximum of 1 second
            // for MessageLoop thread
            // to finish.
            t.join(1000);
            t1.join(1000);
            
            if (((System.currentTimeMillis() - startTime) > patience)
                    && t.isAlive()) {
                threadMessage("****************Tired of waiting!*****************");
                t.interrupt();
                // Shouldn't be long now
                // -- wait indefinitely
                t.join();
            }
            
            if (((System.currentTimeMillis() - startTime) > patience)
                    && t1.isAlive()) {
                threadMessage("****************Tired of waiting!*****************");
                t1.interrupt();
                // Shouldn't be long now
                // -- wait indefinitely
                t1.join();
            }
            
        }
    }

    public static void printall(IDataLayer datalayer, Community com) {
        System.out.println("------------ print all SAP risks ------------------");
        Set<Risk> risks = datalayer.getRisks(com.getUuid());
        for (Risk temp : risks) {
            int i = 1;
            System.out.println("\n****Risk " + i + "/" + risks.size());
            System.out.println(
                    "  UUID: " + temp.getId() + "\n"
                    + "  Title:" + temp.getTitle() + "\n");

            for (Event ev : temp.getSetEvent()) {
                PredictorServiceDescription pred = datalayer.getPredictor(ev);
                System.out.println("  Predictor name: " + pred.getName() + ", at " + pred.getServiceURI());
                System.out.println("  Event: " + ev.getTitle());
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
    
      // Display a message, preceded by
    // the name of the current thread
    static void threadMessage(String message) {
        String threadName =
            Thread.currentThread().getName();
        System.out.format("%s: %s%n",
                threadName,
                message);
    }
    
    private static void  insertResults(UUID riskUuid, DataLayerImpl datalayer) {
        System.out.println("---------------- add risk evaluation results ---------------------");
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
            Random randomGenerator = new Random();

            
            for (int i = 0; i < 5; i++) {
                threadMessage("insert results "+i);
                JobStatus jobstatus = new JobStatus(JobStatusType.READY);
                JobDetails job = new JobDetails("ref", jobstatus, new Date(300000000));
                ResultItem itm = new ResultItem("bmn", randomGenerator.nextDouble());
                ResultItem itm2 = new ResultItem("ve", randomGenerator.nextDouble());
                List<ResultItem> items = new LinkedList<ResultItem>();
                items.add(itm);
                items.add(itm2);

                EvaluationResult evalRslt = new EvaluationResult(job, new Date(), formatter.parse("2012.02.01"), items, null);
                datalayer.saveEvaluationResults(riskUuid, evalRslt);

                EvaluationResult evalRslt2 = new EvaluationResult(job, new Date(), formatter.parse("2012.03.01"), items, null);
                datalayer.saveEvaluationResults(riskUuid, evalRslt2);

                EvaluationResult evalRslt3 = new EvaluationResult(job, new Date(), formatter.parse("2012.04.01"), items, null);
                datalayer.saveEvaluationResults(riskUuid, evalRslt3);
                threadMessage("DONE insert results "+i);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error while inserting results" + ex);
        }
    }
    
    private static void getEvalResults(UUID riskUuid, DataLayerImpl datalayer) {
        try {
            riskUuid=(risk==null)? UUID.fromString("aade5116-ade4-42f7-b00e-8802667abb6a"):risk.getId();
            DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
            System.out.println("---------------- get all risk evaluation results ---------------------");
            Set<EvaluationResult> evals = datalayer.getRiskEvalResults(riskUuid, null, null);
            if (evals != null) {
                for (EvaluationResult rst : evals) {
                    logger.debug("Eval " + rst.getResultUUID());
                    logger.debug("current date: " + rst.getCurrentDate());

                    logger.debug("forecast date: " + formatter.format(rst.getForecastDate()));
                    logger.debug("Job ref " + rst.getJobDetails().getJobRef());
                    for (ResultItem rstitm : rst.getResultItems()) {
                        logger.debug("rstITem: " + rstitm.getName() + " " + rstitm.getProbability());
                    }

                    System.out.println("Eval " + rst.getResultUUID());
                    System.out.println("current date: " + rst.getCurrentDate());

                    System.out.println("forecast date: " + formatter.format(rst.getForecastDate()));
                    System.out.println("Job ref " + rst.getJobDetails().getJobRef());
                    for (ResultItem rstitm : rst.getResultItems()) {
                        System.out.println("rstITem: " + rstitm.getName() + " " + rstitm.getProbability());
                    }
                }
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    private static class ReadThread1 implements Runnable {

        public void run() {
            try {
                DataLayerImpl datalayer = new DataLayerImpl();
                
                for (int i = 0; i < 5; i++) {
                    Map<Community, Set<Risk>> map=datalayer.getAllActiveRisks();
                    
                    UUID riskUuid=(risk==null)? UUID.fromString("aade5116-ade4-42f7-b00e-8802667abb6a"):risk.getId();//bmn change this to get uuid from database
                    
                    threadMessage("gettting eval results for "+riskUuid);
                    getEvalResults(riskUuid,datalayer);
                            
                    // Pause for 2 seconds
                    Thread.sleep(2000);
                    // Print a message
                    threadMessage("run " + i);
                }

                threadMessage("DONE!!");
            } catch (Exception ex) {
                ex.printStackTrace();
                threadMessage("I wasn't done!" + ex.getMessage());
            }

        }
    }
    
   static Community com=null;
   static Risk risk=null;

    
    private static class WriteThread1 implements Runnable {
    

        public void run() {
            try {
                DataLayerImpl datalayer = new DataLayerImpl();

                for (int i = 0; i < 5; i++) {
                    threadMessage("getting all active risks ");
                    Map<Community, Set<Risk>> map = datalayer.getAllActiveRisks();
                    threadMessage("got all active risks ");
                    if (map != null) {
                        com = (Community) map.keySet().toArray()[0];
                        if (com == null) {
                            threadMessage("comunity is null!!");
                        } else {
                            threadMessage("got community " + com.getName());
                        }

                        Set<Risk> risks = map.get(com);
                        risk = (Risk) (risks.toArray()[0]);
                        threadMessage("inserting results to risk " + risk.getId());
                        insertResults(risk.getId(), datalayer);

                        // Pause for 2 seconds
                        Thread.sleep(2000);
                        // Print a message
                        threadMessage("run " + i);
                    }else{
                        threadMessage("No communities or risks found!!");
                    }
                }

                threadMessage("DONE!!");
            } catch (Exception ex) {
                threadMessage("?????????????????? I wasn't done!" + ex.getMessage());
                ex.printStackTrace();
            }

        }
    }
}
