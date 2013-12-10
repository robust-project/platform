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
//      Created Date :          2012-01-19
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 * This is a client interface to be used by the evaluation engine to start
 * evaluation jobs and get results from predictor services.
 * 
 * @author Vegard Engen
 */
public interface IPredictorClient
{

	/**
	 * Start an evaluation job for the risk/opportunity passed on in the method call.
	 * The method returns a job details object, which will specify the job reference
	 * and status of the job.
	 * @param riskOp Risk or Opportunity that should be evaluated.
	 * @param startDate The data for which the R/O should be evaluated from.
	 * @return A job details object; will contain a failure state for issues on the Predictor Service side.
	 * @throws Exception for any technical issues on the Evaluation Engine side - errors from the Predictor Service is in the job details object.
	 */
	EEJobDetails startEvaluationJob(Risk riskOp, Date startDate) throws Exception;

    /**
     * Get evaluation results for the jobs specified in the method call, if any
     * results are indeed available. If no results are available, an empty list
     * of EvaluationResult objects is returned.
     *
     * @param jobs Set of evaluation job detail objects, which will contain all the
     * required information for connecting predictor services for specific jobs.
     * @return A set EvaluationResult objects for the jobs that have finished. 
     * If no jobs have finished, an empty set is returned.
     * @throws Exception
     */
    Set<EvaluationResult> getEvaluationResults(Collection<EEJobDetails> jobs) throws Exception;
    
    /**
     * Cancel the evaluation jobs in the given collection.
     * @param jobs Set of evaluation job detail objects.
     * @throws Exception 
     */
    void cancelEvaluationJobs(Collection<EEJobDetails> jobs) throws Exception;
}
