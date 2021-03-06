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
//      Created Date :          2012-04-12
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.util.Date;
import java.util.Set;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;

/**
 *
 * @author Vegard Engen
 */
public interface IEvaluationManagerListener
{
    /**
     * Get a notification when the manager thread has stopped.
     */
    void onEvaluationManagerStop();
    
    /**
     * Get a notification when the manager thread has restarted.
     */
    void onEvaluationManagerRestart();
    
    /**
     * Get a notification about the date for the evaluations
     * the evaluation engine has started on.
     * @param date Date object.
     */
    void onEvaluationManagerEvaluationEpochStarted(Date date);
    
    /**
     * Get a notification about the date that the evaluation engine has just
     * finished evaluating jobs for.
     * @param date Date object.
     */
    void onEvaluationManagerEvaluationEpochFinished(Date date);
    
    /**
     * Get notification of new evaluation results.
     * @param res The evaluation result for the respective risk or opportunity.
     */
    void onNewEvaluationResults(EvaluationResult res);
    
    /**
     * Get notification of new evaluation results.
     *
     * @param results A Set of evaluation results.
     */
    void onNewEvaluationResults(Set<EvaluationResult> results);
}
