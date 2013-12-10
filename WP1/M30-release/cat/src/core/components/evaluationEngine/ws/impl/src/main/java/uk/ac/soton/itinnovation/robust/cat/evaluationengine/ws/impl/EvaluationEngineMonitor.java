/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
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
//      Created Date :          2012-08-07
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.impl;

import java.util.Date;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationEngineListener;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.IEvaluationResultListener;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.EvaluationEngineStatusType;

/**
 * A class that listens for events from the evaluation engine and interacts with
 * the stream manager to publish events.
 * 
 * @author Vegard Engen
 */
public class EvaluationEngineMonitor extends UFAbstractEventManager implements IEvaluationEngineListener, IEvaluationResultListener
{
    private StreamManager streamManager;
    private boolean isRunning;
    
    static Logger log = Logger.getLogger(EvaluationEngineMonitor.class);
    
    public EvaluationEngineMonitor()
    {
        isRunning = false;
    }
    
    public EvaluationEngineMonitor(StreamManager sm)
    {
        this.streamManager = sm;
    }
    
    public EvaluationEngineMonitor(boolean isRunning)
    {
        this.isRunning = isRunning;
    }
    
    public EvaluationEngineMonitor(StreamManager sm, boolean isRunning)
    {
        this.streamManager = sm;
        this.isRunning = isRunning;
    }
    
    public boolean isEngineRunning()
    {
        return isRunning;
    }
    
    @Override
    public void onEvaluationEngineStart()
    {
        isRunning = true;
        
        log.info("Evaluation Engine has STARTED");
        try {
            log.debug("Publishing STARTED event on ActiveMQ");
            streamManager.publishEngineStatus(EvaluationEngineStatusType.STARTED);
        } catch (Exception ex) {
            log.error("Failed to publish the START event: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public void onEvaluationEngineStop()
    {
        isRunning = false;
        
        log.info("Evaluation Engine has STOPPED");
        try {
            log.debug("Publishing STOPPED event on ActiveMQ");
            streamManager.publishEngineStatus(EvaluationEngineStatusType.STOPPED);
        } catch (Exception ex) {
            log.error("Failed to publish the STOP event: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public void onEvaluationEngineRestart()
    {
        isRunning = true;
        
        log.info("Evaluation Engine has RESTARTED");
        try {
            log.debug("Publishing RESTART event on ActiveMQ");
            streamManager.publishEngineStatus(EvaluationEngineStatusType.RESTARTED);
        } catch (Exception ex) {
            log.error("Failed to publish the RESTART event: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public void onEvaluationEngineEvaluationEpochStarted(Date date)
    {
        log.info("Evaluation Engine has started a new epoch: " + DateUtil.getDateString(date));
    }
    
    @Override
    public void onEvaluationEngineEvaluationEpochFinished(Date date)
    {
        log.info("Evaluation Engine has finished an epoch: " + DateUtil.getDateString(date));
    }
    
    @Override
    public void onNewEvaluationResults(EvaluationResult res)
    {
        if (res == null)
        {
            log.error("Evaluation Engine has received a new evaluation result, but it is NULL!");
            return;
        }
        
        if (res.getResultUUID() == null)
        {
            log.error("Evaluation Engine has received a new evaluation result, but the result UUID is NULL!");
            return;
        }
        
        if (res.getRiskUUID() == null)
        {
            log.error("Evaluation Engine has received a new evaluation result, but the risk UUID is NULL!");
            return;
        }
        
        try {
            streamManager.publishEvaluationResult(res);
        } catch (Exception ex) {
            log.error("Failed to publish evaluation result on the stream: " + ex.toString(), ex);
        }
    }
}
