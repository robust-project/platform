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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationEngine;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationEngineListener;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationManager;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationManagerListener;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationResultListener;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 * The Evaluation Engine is a singleton class, which is meant to be running in
 * the background to evaluate risks and opportunities according to their
 * respective schedules.
 *
 * @author Vegard Engen
 */
public class EvaluationEngine extends UFAbstractEventManager implements IEvaluationEngine, IEvaluationManagerListener
{
    private static EvaluationEngine evaluationEngine = null;
    private URI eeServiceURI;
    private IDataLayer dataLayer;
    private IEvaluationManager manager = null;
    private Thread managerThread = null;
    private Map<IEvaluationResultListener, UUID> listenerCommunityMap;
    private boolean isRunning; // flag to keep track of if it is running or not
    
    static Logger log = Logger.getLogger(EvaluationEngine.class);
    
    /**
     * Constructor, which creates an EvaluationManager instance and uses the
     * provided datalayer instance.
     * @param dataLayer Data Layer instance.
     * @throws Exception 
     */
    private EvaluationEngine(IDataLayer dataLayer, URI eeServiceURI) throws Exception
    {
        this.dataLayer = dataLayer;
        this.eeServiceURI = eeServiceURI;
        
        this.manager = new EvaluationManager(dataLayer, eeServiceURI);
        this.manager.addListener(this);
        this.listenerCommunityMap = new HashMap<IEvaluationResultListener, UUID>();
    }
    
    /**
     * Returns an instance of the EvaluationEngine.
     * @param dataLayer Data Layer instance.
     * @return
     * @throws Exception 
     */
    public static IEvaluationEngine getInstance(IDataLayer dataLayer, URI eeServiceURI) throws Exception
    {
        log.debug("Evaluation Engine getInstance() issued");
        
        if (dataLayer == null)
        {
            log.error("Cannot instantiate EvaluationEngine because the data layer object provided is NULL!");
            throw new IllegalArgumentException("The data layer object provided is NULL!");
        }
        
        if (eeServiceURI == null)
        {
            log.error("Cannot instantiate EvaluationEngine because the service URI object provided is NULL!");
            throw new IllegalArgumentException("The service URI object provided is NULL!");
        }

        if (evaluationEngine == null)
        {
            log.debug("Creating Evaluation Engine instance");
            evaluationEngine = new EvaluationEngine(dataLayer, eeServiceURI);
        }
        else
        {
            evaluationEngine.setDataLayer(dataLayer);
            evaluationEngine.setEvaluationEngineServiceURI(eeServiceURI);
        }

        return evaluationEngine;
    }
    
    /**
     * Private method to set the datalayer instance in both this class and the
     * EvaluationManager class.
     * 
     * @param dataLayer Data Layer implementation.
     */
    private void setDataLayer(IDataLayer dataLayer)
    {
        this.dataLayer = dataLayer;
        ((EvaluationManager)manager).setDataLayer(dataLayer);
    }
    
    /**
     * Private method to set the evaluation engine service URI in both this class
     * and the EvaluationManager class.
     * @param eeSrvURI 
     */
    private void setEvaluationEngineServiceURI(URI eeSrvURI)
    {
        this.eeServiceURI = eeSrvURI;
        ((EvaluationManager)manager).setEvaluationEngineServiceURI(eeSrvURI);
    }

    @Override
    public void start() throws Exception
    {
        log.debug("Evaluation Engine start() issued");

        if (manager == null)
        {
            log.debug("Creating EvaluationEngineManager instance");
            manager = new EvaluationManager(dataLayer, eeServiceURI);
            manager.addListener(this);
        }

        if (managerThread == null)
        {
            log.debug("Creating EvaluationEngineManager thread");
            managerThread = new Thread((EvaluationManager) manager, "Evaluation Engine Manager Thread");
        }

        if (!managerThread.isAlive())
        {
            log.debug("EvaluationManager thread is not alive = starting!");
            managerThread.start();
        } 
        else
        {
            log.debug("EvaluationManager thread is already alive - doing nothing...");
        }
        
        isRunning = true;
        notifyListenersOfStart();
    }
    
    @Override
    public void start(Date startDate, Date endDate) throws Exception
    {
        manager.setSimulationDates(startDate, endDate);
        this.start();
    }
    
    @Override
    public void start(Date startDate, Date endDate, FrequencyType incrementFrequency) throws Exception
    {
        manager.setSimulationDatesAndFrequency(startDate, endDate, incrementFrequency);
        this.start();
    }

    @Override
    public void stop() throws Exception
    {
        log.debug("Evaluation Engine stop() issued");

        if (!isRunning)
        {
            log.debug("Not running, so doing nothing");
            return;
        }
        
        if (managerThread == null)
        {
            log.debug("The manager thread is already NULL, so doing nothing");
            return;
        }

        if (!managerThread.isAlive())
        {
            log.debug("The manager thread is not alive. Setting it to NULL and returning.");
            managerThread = null;
            return;
        }

        log.debug("Calling on the EvaluationManager to stop");
        manager.stop();
        log.debug("Waiting for the thread to stop");
        managerThread.join();
        log.debug("Setting the manager thread to NULL so it can be recreated if start() is issued again");
        managerThread = null;
        
        isRunning = false;
        notifyListenersOfStop();
    }
    
    @Override
    public boolean isEngineRunning()
    {
        return isRunning;
    }
    
    @Override
    public void newEvaluationResult(String jobRef, EvaluationResult evalRes)
    {
        this.manager.newEvaluationResult(jobRef, evalRes);
    }
    
    @Override
    public void updateEvaluationJobStatus(String jobRef, JobStatus jobStatus)
    {
        this.manager.updateEvaluationJobStatus(jobRef, jobStatus);
    }
    
    @Override
    public void addEvaluationEngineListener(IEvaluationEngineListener listener)
    {
        super.addListener((IUFListener) listener);
    }
    
    @Override
    public void removeEvaluationEngineListener(IEvaluationEngineListener listener)
    {
        log.warn("OBS: removeEvaluationEngineListener method not implemented yet!");
    }
    
    @Override
    public void addEvaluationResultListener(IEvaluationResultListener listener)
    {
        super.addListener((IUFListener) listener);
    }
    
    @Override
    public void addEvaluationResultListener(IEvaluationResultListener listener, UUID communityUUID)
    {
        super.addListener((IUFListener) listener);
        listenerCommunityMap.put(listener, communityUUID);
    }
    
    @Override
    public void removeEvaluationResultListener(IEvaluationResultListener listener)
    {
        log.warn("OBS: removeEvaluationResultListener method not fully implemented yet!");
        listenerCommunityMap.remove(listener);
    }
    
    @Override
    public void onEvaluationManagerStop()
    {
        managerThread = null;
        isRunning = false;
        notifyListenersOfStop();
    }
    
    @Override
    public void onEvaluationManagerRestart()
    {
        notifyListenersOfRestart();
    }
    
    @Override
    public void onEvaluationManagerEvaluationEpochStarted(Date date)
    {
        notifyListenersOfEvaluationEpochStarted(date);
    }

    @Override
    public void onEvaluationManagerEvaluationEpochFinished(Date date)
    {
        notifyListenersOfEvaluationEpochFinished(date);
    }

    @Override
    public void onNewEvaluationResults(EvaluationResult res)
    {
        notifyListenersOfNewResults(res);
    }
    
    @Override
    public void onNewEvaluationResults(Set<EvaluationResult> results)
    {
        notifyListenersOfNewResults(results);
    }
    
    /**
     * Will notify any listeners that the evaluation engine has started.
     */
    private void notifyListenersOfStart()
    {
        log.debug("Notifying any listeners that the engine has started");
        List<IEvaluationEngineListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            log.debug("There's " + listeners.size() + " who will now get a notification...");
            for (IEvaluationEngineListener listener : listeners)
            {
                try {
                listener.onEvaluationEngineStart();
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener", ex);
                }
            }
        }
    }
    
    /**
     * Will notify any listeners that the evaluation engine has stopped.
     */
    private void notifyListenersOfStop()
    {
        log.debug("Notifying any listeners that the engine has stopped");
        List<IEvaluationEngineListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            log.debug("There's " + listeners.size() + " who will now get a notification...");
            for (IEvaluationEngineListener listener : listeners)
            {
                try {
                listener.onEvaluationEngineStop();
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener", ex);
                }
            }
        }
    }
    
    /**
     * Will notify any listeners that the evaluation engine has started.
     */
    private void notifyListenersOfRestart()
    {
        log.debug("Notifying any listeners that the engine has restarted");
        List<IEvaluationEngineListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            log.debug("There's " + listeners.size() + " who will now get a notification...");
            for (IEvaluationEngineListener listener : listeners)
            {
                try {
                listener.onEvaluationEngineRestart();
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener", ex);
                }
            }
        }
    }
    
    /**
     * Will notify any listeners of starting an evaluation "epoch".
     */
    private void notifyListenersOfEvaluationEpochStarted(Date date)
    {
        log.debug("Notifying any listeners that a new evaluation epoch started ("+ DateUtil.getDateString(date) +")");
        List<IEvaluationEngineListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            log.debug("There's " + listeners.size() + " who will now get a notification...");
            for (IEvaluationEngineListener listener : listeners)
            {
                try {
                listener.onEvaluationEngineEvaluationEpochStarted(date);
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener", ex);
                }
            }
        }
    }
    
    /**
     * Will notify any listeners of starting an evaluation "epoch".
     */
    private void notifyListenersOfEvaluationEpochFinished(Date date)
    {
        log.debug("Notifying any listeners that an evaluation epoch finished ("+ DateUtil.getDateString(date) +")");
        List<IEvaluationEngineListener> listeners = getListenersByType();
        if (!listeners.isEmpty())
        {
            log.debug("There's " + listeners.size() + " who will now get a notification...");
            for (IEvaluationEngineListener listener : listeners)
            {
                try {
                listener.onEvaluationEngineEvaluationEpochFinished(date);
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener", ex);
                }
            }
        }
    }
    
    /**
     * Will notify any listeners of the new evaluation result.
     * @param res Evaluation result object.
     */
    private void notifyListenersOfNewResults(EvaluationResult res)
    {
        List<IEvaluationResultListener> listeners = getListenersByType();
        if ((listeners == null) || listeners.isEmpty()){
            return;
        }
        
        if (dataLayer == null) {
            log.error("DataLayer is NULL, cannot notify listeners of the new results!");
            return;
        }
        
        if (res == null) {
            log.error("EvaluationResult object is NULL, cannot notify listeners of the new results!");
            return;
        } else if (res.getRiskUUID() == null) {
            log.error("EvaluationResult Risk UUID is NULL, cannot notify listeners of the new results!");
            return;
        }
        
        Risk risk = dataLayer.getRiskByUUID(res.getRiskUUID());
        if (risk == null) {
            log.error("Risk object from dataLayer is NULL, cannot notify listeners of the new results");
            return;
        } else if (risk.getCommunity() == null) {
            log.error("Community object in risk is NULL, cannot notify listeners of the new results");
            return;
        } else if (risk.getCommunity().getUuid() == null) {
            log.error("Community UUID is NULL, cannot notify listeners of the new results");
            return;
        }
        
        log.debug("Notifying listeners about the new evaluation result");
        for (IEvaluationResultListener listener : listeners)
        {
            if (!listenerCommunityMap.containsKey(listener) || listenerCommunityMap.get(listener).equals(risk.getCommunity().getUuid()))
            {
                try {
                    listener.onNewEvaluationResults(res);
                } catch (Exception ex) {
                    log.error("Caught an exception when trying to notify a listener of the new result", ex);
                }
            }
        } // for each listener
    }
    
    /**
     * Will notify any listeners of the new evaluation results.
     * @param results A Set of evaluation results.
     */
    private void notifyListenersOfNewResults(Set<EvaluationResult> results)
    {
        List<IEvaluationResultListener> listeners = getListenersByType();
        if ((listeners == null) || listeners.isEmpty()){
            return;
        }
        
        log.debug("Notifying listeners about the new evaluation results");
        
        if (results == null) {
            log.error("EvaluationResult object is NULL, cannot notify listeners of the new results!");
            return;
        }
        
        if (dataLayer == null) {
            log.error("DataLayer is NULL, cannot notify listeners of the new results!");
            return;
        }
        
        for (EvaluationResult res : results)
        {
            if (res == null) {
                log.error("EvaluationResult object is NULL, cannot notify listeners of the new results!");
                continue;
            } else if (res.getRiskUUID() == null) {
                log.error("EvaluationResult Risk UUID is NULL, cannot notify listeners of the new results!");
                continue;
            }

            Risk risk = dataLayer.getRiskByUUID(res.getRiskUUID());
            if (risk == null) {
                log.error("Risk object from dataLayer is NULL, cannot notify listeners of the new results");
                continue;
            } else if (risk.getCommunity() == null) {
                log.error("Community object in risk is NULL, cannot notify listeners of the new results");
                continue;
            } else if (risk.getCommunity().getUuid() == null) {
                log.error("Community UUID is NULL, cannot notify listeners of the new results");
                continue;
            }

            for (IEvaluationResultListener listener : listeners)
            {
                if (!listenerCommunityMap.containsKey(listener) || listenerCommunityMap.get(listener).equals(risk.getCommunity().getUuid()))
                {
                    try {
                        listener.onNewEvaluationResults(res);
                    } catch (Exception ex) {
                        log.error("Caught an exception when trying to notify a listener of the new result", ex);
                    }
                }
            } // for each listener
        } // for each result
    }

    @Override
    public void finalize()
    {
        log.debug("EvaluationEngine.finalize() - attempting to stop the Evaluation Engine if it hasn't already.");
        try {
            this.stop();
        } catch (Exception ex) {
            log.error("Exception caught when the finalize method attempted to stop the evaluation engine", ex);
        } finally {
            try {
                super.finalize();
            } catch (Throwable ex) {
                log.error("Exception caught when the finalize method attempted to stop the evaluation engine", ex);
            }
        }
    }
}
