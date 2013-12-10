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
//      Created By :            Bassem Nasser
//      Created Date :          19-Oct-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.webapp;

import java.net.URI;
import java.util.*;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.apache.log4j.Logger;
import pl.swmind.robust.streaming.activemq.topic.ActiveMQSubscriber;
import pl.swmind.robust.streaming.topic.RobustMessageListener;
import pl.swmind.robust.streaming.topic.RobustSubscriber;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.impl.EvaluationEngine;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationEngine;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationEngineListener;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationResultListener;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.client.EvaluationEngineClient;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.EvaluationEngineStatusType;

//the configuration information is found in cat.properties
//this class should implement all the EE interface and relay it to the actual EE either locally or remotely
//if we are dealing with local EE then there is no need to subscribe to ActiveMQ. the local EE directly notifies the evalEngineListeners.
//we need to subscribe to ActiveMQ in case the EE is deployed as WS
public class EvaluationEngineProxy implements IEvaluationEngine, RobustMessageListener {

    static boolean ws = false;
    private static EvaluationEngineProxy proxy = null;
    private static Logger log = Logger.getLogger(EvaluationEngineProxy.class);
    private static String serviceURI = "";
    private static String nameSpace = "";
    private static String serviceName = "";
    private static String portName = "";
    private EvaluationEngineClient evaluationEngineClient = null; //remote EE client
    private IEvaluationEngine roEvaluationEngine = null; //local EE
    RobustSubscriber generalStream = null;
    RobustSubscriber communityStream=null;
    UUID commUUID=null;
    Set<IEvaluationEngineListener> evalEngineListeners = new HashSet<IEvaluationEngineListener>();
    Set<IEvaluationResultListener> evalResultListeners = new HashSet<IEvaluationResultListener>();
    

    private void getconfigs() {
        //get configs from cat.properties

        Properties prop = new Properties();

        try {
            prop.load(EvaluationEngineProxy.class.getClassLoader().getResourceAsStream("cat.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
        }

        //get if webservice
        
        ws = Boolean.parseBoolean(prop.getProperty("ws"));
         log.info("WS config extracted: "+ws);

        //if WS get the service information
        if (ws) {
            serviceURI = prop.getProperty("serviceURI");
            nameSpace = prop.getProperty("nameSpace");
            serviceName = prop.getProperty("serviceName");
            portName = prop.getProperty("portName");
            log.info("Evaluation engine WS is used");
            log.info("information extracted from cat.properties: "
                    +serviceURI
                    + nameSpace
                    +serviceName
                    +portName);
        }else{
            log.info("Evaluation engine jar is used");
        }
    }

    public EvaluationEngineProxy(IDataLayer dataLayer) {
        getconfigs();
        try {
            if (ws) {

                log.info("Starting web-service client");
                log.info("----------------------------");

                evaluationEngineClient = new EvaluationEngineClient(serviceURI, nameSpace, serviceName, portName);

                // RobustMessageListener robustMessageListener = new EvaluationEngineListener();

            } else {
                //local EE
                URI eeServiceURI = new URI(serviceURI); // service URI should have been read from properties file
                roEvaluationEngine = EvaluationEngine.getInstance(dataLayer, eeServiceURI);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error while initializing the Evaluation Engine proxy. ", ex);
        }

    }

    private void subscribeTOActMQ() {
        StreamDetails sd = null;

        try {
            log.info("Getting general stream details");
//            System.out.println("Getting general stream details");
            sd = evaluationEngineClient.getGeneralStreamDetails();
            log.info("  - Name: " + sd.getStreamName());
            log.info("  - URI:  " + sd.getStreamURI());
        } catch (Exception ex) {
            log.error("error while Getting general stream details. ", ex);
            throw new RuntimeException("error while Getting general stream details. ", ex);
        }


        try {
            log.info("Connecting to general ActiveMQ Topic (subscribing)");
//            System.out.println("Connecting to general ActiveMQ Topic (subscribing)");
            generalStream = new ActiveMQSubscriber(sd.getStreamName(), sd.getStreamURI().toString());
            generalStream.subscribe(this);
        } catch (Exception ex) {
            log.error("Unable to subscribe to the stream: " + ex.getMessage(), ex);
//            System.out.println("Unable to subscribe to the stream: " + ex.getMessage());
            throw new RuntimeException("Unable to subscribe to the stream: " + ex.getMessage(), ex);
        }

    }

    private void subscribeToCommunityStream(UUID communityUUID) {
        log.info("Subscribing to community stream");
        StreamDetails sd = null;
        try {
            sd = evaluationEngineClient.getCommunityResultsStreamDetails(communityUUID);
            log.info("  - Name: " + sd.getStreamName());
            log.info("  - URI:  " + sd.getStreamURI());
        } catch (Exception ex) {
            log.error("  - Failed to get the community stream details: " + ex.getMessage());
            sd = null;
            throw new RuntimeException(" - Failed to get the community stream details: " + ex.getMessage(),ex);
        }

        try {
            if (sd != null) {
                log.info("Connecting to community ActiveMQ Community Topic (subscribing)");
                communityStream = new ActiveMQSubscriber(sd.getStreamName(), sd.getStreamURI().toString());
                communityStream.subscribe(this);
            }
        } catch (Exception ex) {
            log.error("Unable to subscribe to the community stream: " + ex.getMessage(), ex);
            throw new RuntimeException("unable to subscribe to the community stream. ",ex);
        }
    }

    private void unsubscribeToCommunityStream() {
        try {
            log.info("Closing Community stream subscriber to ActiveMQ Topics");

            if (communityStream != null) {
                communityStream.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error while closing community stream. ", ex);
        }
    }
    
    private void unsubscribeTOActMQ() {
        try {
            log.info("Closing subscribers/listeners to ActiveMQ Topics");

            if (generalStream != null) {
                generalStream.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error while closing general stream. ", ex);
        }
    }

    public static EvaluationEngineProxy getInstance(IDataLayer dataLayer) throws Exception {
        log.debug("Evaluation Engine getInstance() issued");
        if (proxy == null) {
            log.debug("Creating Evaluation Engine instance");
            proxy = new EvaluationEngineProxy(dataLayer);
        }

        return proxy;
    }

    @Override
    public void onMessage(String s) {
    }

    @Override
    public void onMessage(Message message) {
        log.debug("New Message on stream");
        if (message instanceof ObjectMessage) {
            ObjectMessage objMsg = (ObjectMessage) message;
            Object obj = null;
            try {
                obj = objMsg.getObject();
                log.debug("  - ObjectMessage is of type: " + obj.getClass());

                if (obj instanceof EvaluationEngineStatusType) {//There is no epoch started/finished type messages. dont know if needed
                    EvaluationEngineStatusType status = (EvaluationEngineStatusType) obj;
                    log.debug("  - Got a status update from the evaluation engine: " + status);
                    String statusValue = status.value();
                    String statusStarted = status.STARTED.value();
                    String statusStopped = status.STOPPED.value();
                    
                    /*
                    if(status.value().equals(status.STARTED)) {
                        notifyListenersOfStart();
                    }
                    
                    if(status.value().equals(status.STOPPED)) {
                        notifyListenersOfStop();
                    }
                    */
                    
                    if (statusValue.equals(statusStarted)) {
                        notifyListenersOfStart();
                    }

                    if (statusValue.equals(statusStopped)) {
                        notifyListenersOfStop();
                    }
                    

                } else if (obj instanceof EvaluationResult) {
                    EvaluationResult evalRes = (EvaluationResult) obj;
                    log.debug("  - Got a new evaluation result");

                    notifyListenersOfNewResults(evalRes);

                    printEvaluationResultDetails(evalRes);
                }
            } catch (JMSException ex) {
                log.error("Failed to get and deserialise the ObjectMessage", ex);
            }
        }
    }

    private void notifyListenersOfStart() {
        log.debug("Notifying any listeners that the engine has started");

        log.debug("There's " + evalEngineListeners.size() + " who will now get a notification...");
        for (IEvaluationEngineListener listener : evalEngineListeners) {
            try {
                listener.onEvaluationEngineStart();
            } catch (Exception ex) {
                log.error("Caught an exception when trying to notify a listener", ex);
            }
        }

    }

    private void notifyListenersOfStop() {
        log.debug("Notifying any listeners that the engine has stopped");

        log.debug("There's " + evalEngineListeners.size() + " who will now get a notification...");
        for (IEvaluationEngineListener listener : evalEngineListeners) {
            try {
                listener.onEvaluationEngineStop();
            } catch (Exception ex) {
                log.error("Caught an exception when trying to notify a listener", ex);
            }
        }

    }

    private void notifyListenersOfNewResults(EvaluationResult res) {
        log.debug("  - Notifying listeners about new results");
        
        if(res==null)
            log.error("recieved evaluation result object null from active MQ");

        printEvaluationResultDetails(res);
        
        for (IEvaluationResultListener il : evalResultListeners) {
            il.onNewEvaluationResults(res); // ve: removed community UUID
        }
    }

    @Override
    public void start() throws Exception {
        if (ws) {
            if (evaluationEngineClient != null) {
                log.info("calling WS start engine.");
                evaluationEngineClient.startEngine();               
            }

        } else {
             log.info("calling EE local start engine.");
            roEvaluationEngine.start();
        }

       
        

    }

    @Override
    public void start(Date date, Date date1) throws Exception {
        if (ws) {
            if (evaluationEngineClient != null) {
                evaluationEngineClient.startEngineForTimePeriod(date, date1);
            }

        } else {
            roEvaluationEngine.start(date, date1);
        }
    }

    @Override
    public void start(Date date, Date date1, FrequencyType ft) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stop() throws Exception {
        if (ws) {
            if (evaluationEngineClient != null) {
                 log.info("calling WS stop engine.");
                evaluationEngineClient.stopEngine();
            }

        } else {
             log.info("calling EE local stop engine.");
            roEvaluationEngine.stop();
        }
    }

    @Override
    public void newEvaluationResult(String string, EvaluationResult er) {
        //THESE METHODS ARE USED BY PREDICTOR SERVICES ONLY
        //NO NEED TO IMPLEMENT FOR THE DASHBOARD
        throw new UnsupportedOperationException("Not implemented for dashboard");
        
//        if (ws) {
//            if (evaluationEngineClient != null) {
//                try {
//                    evaluationEngineClient.newEvaluationResult(string, er);
//                } catch (Exception ex) {
//                    log.error("error while calling newEvaluationResult", ex);
//                }
//            }
//
//        } else {
//            roEvaluationEngine.newEvaluationResult(string, er);
//        }
    }

    @Override
    public void updateEvaluationJobStatus(String string, JobStatus js) {
          //THESE METHODS ARE USED BY PREDICTOR SERVICES ONLY
        //NO NEED TO IMPLEMENT FOR THE DASHBOARD
        throw new UnsupportedOperationException("Not implemented for dashboard");
//        if (ws) {
//            if (evaluationEngineClient != null) {
//                evaluationEngineClient.updateEvaluationJobStatus(string, js);
//            }
//
//        } else {
//            roEvaluationEngine.updateEvaluationJobStatus(string, js);
//        }
    }

    private void printEvaluationResultDetails(EvaluationResult evalRes) {
        if (evalRes == null) {
            log.debug("    - The object is NULL");
        }

        if (evalRes.getResultUUID() != null) {
            log.debug("    - Result UUID: " + evalRes.getResultUUID().toString());
        }

        if (evalRes.getRiskUUID() != null) {
            log.debug("    - Risk UUID: " + evalRes.getRiskUUID().toString());
        }

        if (evalRes.getForecastDate() != null) {
            log.debug("    - Risk forecast date: " + evalRes.getForecastDate());
        }

        if ((evalRes.getResultItems() != null) && !evalRes.getResultItems().isEmpty()) {
            log.debug("    - Result items:");
            for (ResultItem ri : evalRes.getResultItems()) {
                if (ri == null) {
                    continue;
                }

                log.debug("      - " + ri.getName() + "\t" + ri.getProbability());
            }
        }
    }

    @Override
    public void addEvaluationEngineListener(IEvaluationEngineListener listener) {
        
        //this listens to stop/start events
        if (ws) {
            //the listener should be this class
            if (evaluationEngineClient != null) {
                 log.info("subscribing to activeMQ as evaluation engine listener");
                this.subscribeTOActMQ();
            }

        } else {
             log.info("adding dashboard as listener to local EE.");
            roEvaluationEngine.addEvaluationEngineListener(listener);
        }

        //keep a local list of evalEngineListeners in order to relay the messages to them
        evalEngineListeners.add(listener);
        
    }

    @Override
    public void removeEvaluationEngineListener(IEvaluationEngineListener listener) {
        
        if (ws) {//the listener should be this class
            if (evaluationEngineClient != null) {
                 log.info("unsubscribing from activeMQ as evaluation engine listener.");
                this.unsubscribeTOActMQ();
            }

        } else {
            log.info("removing dashboard as listener to local EE.");
            roEvaluationEngine.removeEvaluationEngineListener(listener);
        }

        //remove the listener from the local listerners list
        evalEngineListeners.remove(listener);
    }

    @Override
    public void addEvaluationResultListener(IEvaluationResultListener listener) {
                //this listens to evaluation result events
        if (ws) {
            //the listener should be this class
            if (evaluationEngineClient != null) {
                this.subscribeToCommunityStream(null);//TODO check if this is the required behaviour???
            }

        } else {
            roEvaluationEngine.addEvaluationResultListener(listener);
        }

        //keep a local list of evalEngineListeners in order to relay the messages to them
        evalResultListeners.add(listener);
    }

    @Override
    public boolean addEvaluationResultListener(IEvaluationResultListener listener, UUID communityUUID) {
                    //this listens to evaluation result events
        boolean success = false; // indicates if subscribed to stream
        
        if (ws) {
            //the listener should be this class
            if (evaluationEngineClient != null) {
                log.info("proxy subscribing to ActiveMQ comunity "+communityUUID);
                try {
                    this.commUUID = null;
                    this.subscribeToCommunityStream(communityUUID);
                    this.commUUID = communityUUID;
                    success = true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            log.info("proxy subscribing the dashboard to local EE, comunity "+communityUUID);
            roEvaluationEngine.addEvaluationResultListener(listener,communityUUID);
        }

        //keep a local list of evalEngineListeners in order to relay the messages to them
        evalResultListeners.add(listener);
        
        return success;
    }

    @Override
    public void removeEvaluationResultListener(IEvaluationResultListener listener) {
         if (ws) {//the listener should be this class
            if (evaluationEngineClient != null) {
                this.unsubscribeToCommunityStream();
                this.commUUID=null;
            }

        } else {
            roEvaluationEngine.removeEvaluationResultListener(listener);
        }

        //remove the listener from the local listerners list
        evalResultListeners.remove(listener);
    }
    
    //    @Override
//    public void addListener(IEvaluationEngineListener il) {
//
//        //the listener should be this class
//        if (ws) {
//            if (evaluationEngineClient != null) {
//                this.subscribeTOActMQ();
//            }
//
//        } else {
//            roEvaluationEngine.addListener(il);
//        }
//
//        //keep a local list of evalEngineListeners in order to relay the messages to them
//        evalEngineListeners.add(il);
//    }

//    @Override
//    public void removeListener(IEvaluationEngineListener il) {
//        //the listener should be this class
//        if (ws) {
//            if (evaluationEngineClient != null) {
//                this.unsubscribeTOActMQ();
//            }
//
//        } else {
//            roEvaluationEngine.removeListener(il);
//        }
//
//        //remove the listener from the local listerners list
//        evalEngineListeners.remove(il);
//    }
    @Override
    public boolean isEngineRunning() {
        boolean isRunning=false;
        try {
            if (ws) {

                log.info("Checking if running");
//                System.out.println("Checking if running");
                isRunning = evaluationEngineClient.isEngineRunning();
                log.info("  - Return value: " + isRunning);
//System.out.println("  - Return value: " + isRunning);

            } else {
                isRunning=roEvaluationEngine.isEngineRunning();
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error while checking if EE is running. ", ex);
        }
        
        return isRunning;
    }
}
