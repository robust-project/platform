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
//      Created By :            Vegard Engen
//      Created Date :          2011-11-09
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.impl;

import java.net.URI;
import java.util.Date;
import java.util.UUID;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.datalayer.impl.DataLayerImpl;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationEngine;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationEngineListener;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationResultListener;

/**
 *
 * @author ve
 */
public class EvaluationEngineTest
{
    static Logger log = Logger.getLogger(EvaluationEngineTest.class);
    DateUtil dateUtil = new DateUtil();
    
    private static boolean restartedEventCaptured = false;
    
    public EvaluationEngineTest(){}
    
    public void testEngine()
    {
        log.info("--- Testing Evaluation Engine ---");

        Runnable r1 = new Runnable()
        {
            public void run()
            {
                try {
                    long sleeptime = 1000 * 6;
                    Date startDate = DateUtil.getDateObject("2009-01-01");
                    //Date endDate = DateUtil.getDateObject("2009-01-08");
                    Date endDate = DateUtil.getDateObject("2009-01-03");
                    FrequencyType frequency = FrequencyType.DAILY;
                    boolean restartEventTest = true; // test to wait until the EE has restarted before then stopping it and finishing this test
                    
                    log.info("Creating DataLayer instance");
                    IDataLayer dataLayer = null;
                    try { dataLayer = new DataLayerImpl(); } catch (Exception ex) {}
                    URI eeServiceURI = new URI("http://localhost:8080/evaluationEngineService-1.5/service");
                    
                    log.info("Getting EvaluationEngine instance");
                    IEvaluationEngine engine = EvaluationEngine.getInstance(dataLayer, eeServiceURI);
                    
                    log.info("Creating test listener and adding to the engine");
                    //UUID communityUUID = UUID.fromString("6e2bb159-12c8-434c-8497-a7824a07bf27");
                    UUID communityUUID = UUID.fromString("a517e238-cc46-4ae0-84dd-67670357f77d");
                    TestListener testListener = new TestListener(engine);
                    testListener.setRestartTest(false);
                    engine.addEvaluationEngineListener(testListener);
                    //engine.addEvaluationResultListener(testListener);
                    engine.addEvaluationResultListener(testListener, communityUUID);

                    log.info("Starting the engine");
                    engine.start(startDate, endDate, frequency);
                    
                    Thread.sleep(sleeptime);
                    
                    if (restartEventTest)
                    {
                        int count = 0;
                        while (count < 10) {
                            if (!restartedEventCaptured) {
                                Thread.sleep(sleeptime);
                            } else {
                                break;
                            }
                            count++;
                        }
                    }
/**
                    log.info("Starting the engine");
                    engine.start();
                    Thread.sleep(sleeptime);

                    log.info("Stopping the engine");
                    engine.stop();
                    Thread.sleep(sleeptime);

                    log.info("Stopping the engine");
                    engine.stop();
                    Thread.sleep(sleeptime);

                    log.info("Starting the engine");
                    engine.start();
                    Thread.sleep(sleeptime);
/**/
                    // turning off the restart test mode to avoid an infinite loop
                    testListener.setRestartTest(false);
                    
                    log.info("Stopping the engine");
                    engine.stop();
                    Thread.sleep(sleeptime);
                    
                    log.info("Test thread finished");
                } catch (InterruptedException iex) {
                    log.error("Caught an interrupted exception when in the evaluation loop of the engine test class", iex);
                } catch (Exception e) {
                    log.error("Caught an exception when in the evaluation loop of the engine test class", e);
                }
            }
        };
        Thread thr1 = new Thread(r1);
        thr1.start();
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        EvaluationEngineTest test = new EvaluationEngineTest();
        test.testEngine();
    }
    
    private class TestListener extends UFAbstractEventManager implements IEvaluationEngineListener, IEvaluationResultListener
    {
        IEvaluationEngine engine = null;
        boolean restartTest = false;
        Logger log = Logger.getLogger(TestListener.class);
        
        
        public TestListener(){}
        
        public TestListener(IEvaluationEngine engine)
        {
            this.engine = engine;
        }
        
        public void setRestartTest(boolean flag)
        {
            restartTest = flag;
        }
        
        @Override
        public void onNewEvaluationResults(EvaluationResult er)
        {
            log.info("\n\nW00P - got some evaluation result stuff\t::" +
                    "\t" + er.getRiskUUID().toString() + 
                    "\t" + dateUtil.getDateString(er.getForecastDate()) + 
                    "\t" + er.getResultItems().get(0).getProbability() + "\n");
        }

        @Override
        public void onEvaluationEngineStart()
        {
            log.info("Got a notification that the engine has STARTED - how nifty!");
        }

        @Override
        public void onEvaluationEngineStop()
        {
            log.info("Got a notification that the engine has STOPPED - the fun is over I guess :(");
            
            if ((engine != null) && restartTest)
            {
                log.info("Or perhaps not! Trying to start the engine again...");
                try
                {
                    engine.start();
                } catch (Exception ex)
                {
                    log.error("Failed to start the engine: " + ex.getMessage(), ex);
                }
            }
        }
        
        @Override
        public void onEvaluationEngineRestart()
        {
            log.info("Got a notification that the engine has RESTARTED - so here we go again...");
            restartedEventCaptured = true;
        }

        @Override
        public void onEvaluationEngineEvaluationEpochStarted(Date date)
        {
            log.info("Got a notification that an evaluation epoch has STARTED - " + DateUtil.getDateString(date));
        }

        @Override
        public void onEvaluationEngineEvaluationEpochFinished(Date date)
        {
            log.info("Got a notification that an evaluation epoch has FINISHED - " + DateUtil.getDateString(date));
        }
        
    }
}
