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
//      Created Date :          2012-01-18
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.util.Date;

/**
 * This is an interface for listeners of events exposed by the Evaluation
 * Engine, which includes start, stop and new epochs start/stop.
 * 
 * @author Vegard Engen
 */
public interface IEvaluationEngineListener
{
    /**
     * Get a notification when the engine has started.
     */
    void onEvaluationEngineStart();

    /**
     * Get a notification when the engine has stopped.
     */
    void onEvaluationEngineStop();
    
    /**
     * Get a notification when the engine has restarted.
     */
    void onEvaluationEngineRestart();
    
    /**
     * Get a notification about the date for the evaluations
     * the evaluation engine has started on.
     * @param date Date object.
     */
    void onEvaluationEngineEvaluationEpochStarted(Date date);
    
    /**
     * Get a notification about the date that the evaluation engine has just
     * finished evaluating jobs for.
     * @param date Date object.
     */
    void onEvaluationEngineEvaluationEpochFinished(Date date);
}
