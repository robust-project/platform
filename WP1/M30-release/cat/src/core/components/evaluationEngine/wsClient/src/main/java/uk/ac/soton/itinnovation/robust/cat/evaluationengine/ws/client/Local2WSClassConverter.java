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
//      Created Date :          2012-08-15
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.client;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 *
 * @author Vegard Engen
 */
public class Local2WSClassConverter
{
    static Logger log = Logger.getLogger(Local2WSClassConverter.class);
    
    /**
     * Get a local instance of the job details WS object.
     * @param job Web service job details object.
     * @return 
     */
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobDetails getJobDetails(uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails job)
    {
        if (job == null)
        {
            log.debug("The job details object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobDetails jobDetails;
        jobDetails = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobDetails();
        
        if (job.getJobRef() != null)
            jobDetails.setJobRef(job.getJobRef());
        
        if (job.getJobStatus() != null)
            jobDetails.setJobStatus(getJobStatus(job.getJobStatus()));
        
        try {
            if (job.getRequestDate() != null) jobDetails.setRequestDate(DateUtil.getXMLGregorianCalendar(job.getRequestDate()));
            if (job.getStartDate() != null) jobDetails.setStartDate(DateUtil.getXMLGregorianCalendar(job.getStartDate()));
            if (job.getCompletionDate() != null) jobDetails.setCompletionDate(DateUtil.getXMLGregorianCalendar(job.getCompletionDate()));
        } catch (Exception ex) {
            log.error("An exception was caught when coverting the job status dates into Date objects", ex);
        }
        
        return jobDetails;
    }
    
    /**
     * Get a local instance of the job status from the WS object.
     * @param job Web service job status object.
     * @return 
     */
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobStatus getJobStatus(uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus job)
    {
        if (job == null)
        {
            log.debug("The job status object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobStatus jobStatus;
        jobStatus = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobStatus();
        
        if (getJobStatusType(job.getStatus()) != null)
            jobStatus.setStatus(getJobStatusType(job.getStatus()));
        
        if (job.getMetaData() != null)
            jobStatus.setMetaData(job.getMetaData());
        
        return jobStatus;
    }
    
    /**
     * Get a local instance of the status type from the WS object.
     * @param job Web service job status enum.
     * @return 
     */
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobStatusType getJobStatusType(uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType job)
    {
        if (job == null)
        {
            log.debug("The job status type object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        return uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.JobStatusType.fromValue(job.value());
    }

    /**
     * Get a local instance of the evaluation result from the WS object.
     *
     * @param res Web service evaluation result object.
     * @return
     */
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.EvaluationResult getEvaluationResult(uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult res)
    {
        if (res == null)
        {
            log.debug("The result object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.EvaluationResult evalResult;
        evalResult = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.EvaluationResult();
        
        if (res.getCurrentDate() != null)
        {
            try {
                evalResult.setCurrentDate(DateUtil.getXMLGregorianCalendar(res.getCurrentDate()));
            } catch (Exception ex) {
                log.error("An exception was caught when coverting the current date into a Date object", ex);
            }
        }
        
        if (res.getForecastDate() != null)
        {
            try {
                evalResult.setForecastDate(DateUtil.getXMLGregorianCalendar(res.getForecastDate()));
            } catch (Exception ex) {
                log.error("An exception was caught when coverting the forecast date into a Date object", ex);
            }
        }
        
        if (res.getResultItems() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.ResultItem> resultItems = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.ResultItem>();
            for (uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem resItem : res.getResultItems())
                resultItems.add(getResultItem(resItem));
            
            evalResult.setResultItems(resultItems);
        }
        
        evalResult.setJobDetails(getJobDetails(res.getJobDetails()));
        
        if (res.getMetaData() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.KeyValuePair> metaData = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.KeyValuePair>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair kvp : res.getMetaData())
            {
                uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.KeyValuePair kvp_ws = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.KeyValuePair();
                kvp_ws.setKey(kvp.getKey());
                kvp_ws.setValue(kvp.getValue());
                metaData.add(kvp_ws);
            }
            
            evalResult.setMetaData(metaData);
        }
        
        return evalResult;
    }
    
    /**
     * Get a local instance of the result item from a WS object.
     * @param resItem The WS ResultItem object.
     * @return A local instance of a ResultItem.
     */
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.ResultItem getResultItem(uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem resItem)
    {
        if (resItem == null)
        {
            log.debug("The ResultItem object from the WS was NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.ResultItem resultItem;
        resultItem = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.ResultItem();
        
        if (resItem.getName() != null) {
            resultItem.setName(resItem.getName());
        }
        if (resItem.getProbability() != null) {
            resultItem.setProbability(resItem.getProbability());
        }
        if (resItem.getCurrentObservation() != null) {
            resultItem.setCurrentObservation(resItem.getCurrentObservation());
        }

        return resultItem;
    }

}
