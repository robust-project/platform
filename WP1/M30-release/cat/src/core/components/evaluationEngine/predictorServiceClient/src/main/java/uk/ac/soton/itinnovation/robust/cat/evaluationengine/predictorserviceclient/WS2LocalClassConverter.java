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
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 *
 * @author ve
 */
public class WS2LocalClassConverter
{
    static Logger log = Logger.getLogger(WS2LocalClassConverter.class);
    
    /**
     * Get a local instance of the predictor service description from the WS
     * object.
     *
     * @param predictorServiceDescription Web service prediction service description object.
     * description.
     * @return
     */
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription getPredictorServiceDescription(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.PredictorServiceDescription desc)
    {
        if (desc == null)
        {
            log.debug("The PredictorServiceDescription object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription predictorServiceDesc;
        predictorServiceDesc = new uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription();

        predictorServiceDesc.setUuid(UUID.fromString(desc.getUuid()));
        predictorServiceDesc.setName(desc.getName());
        predictorServiceDesc.setDescription(desc.getDescription());
        predictorServiceDesc.setVersion(desc.getVersion());
        predictorServiceDesc.setForecastPeriod(getParameter(desc.getForecastPeriod()));
        
        if (desc.getEvents() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event> eventList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event>();
            
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Event e : desc.getEvents())
                eventList.add(getEvent(e));
            
            predictorServiceDesc.setEvents(eventList);
        }
        
        if (desc.getConfigurationParams() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter> paramList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter>();
            
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter p : desc.getConfigurationParams())
                paramList.add(getParameter(p));
            
            predictorServiceDesc.setConfigurationParams(paramList);
        }

        return predictorServiceDesc;
    }
    
    /**
     * Get a local instance of the job details WS object.
     * @param job Web service job details object.
     * @return 
     */
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails getJobDetails(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobDetails job)
    {
        if (job == null)
        {
            log.debug("The job details object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails jobDetails;
        jobDetails = new uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails();
        
        if (job.getJobRef() != null)
            jobDetails.setJobRef(job.getJobRef());
        
        if (job.getJobStatus() != null)
            jobDetails.setJobStatus(getJobStatus(job.getJobStatus()));
        
        try {
            if (job.getRequestDate() != null) jobDetails.setRequestDate(DateUtil.getDate(job.getRequestDate()));
            if (job.getStartDate() != null) jobDetails.setStartDate(DateUtil.getDate(job.getStartDate()));
            if (job.getCompletionDate() != null) jobDetails.setCompletionDate(DateUtil.getDate(job.getCompletionDate()));
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
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus getJobStatus(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobStatus job)
    {
        if (job == null)
        {
            log.debug("The job status object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus jobStatus;
        jobStatus = new uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus();
        
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
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType getJobStatusType(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.JobStatusType job)
    {
        if (job == null)
        {
            log.debug("The job status type object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        return uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType.fromValue(job.value());
    }

    /**
     * Get a local instance of the evaluation result from the WS object.
     *
     * @param res Web service evaluation result object.
     * @return
     */
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult getEvaluationResult(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationResult res)
    {
        if (res == null)
        {
            log.debug("The result object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult evalResult;
        evalResult = new uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult();
        
        evalResult.setResultUUID(UUID.fromString(res.getResultUUID()));
        if ((res.getRiskUUID() != null) && !res.getRiskUUID().isEmpty()) {
            evalResult.setRiskUUID(UUID.fromString(res.getResultUUID()));
        }
        
        if (res.getCurrentDate() != null)
        {
            try {
                evalResult.setCurrentDate(DateUtil.getDate(res.getCurrentDate()));
            } catch (Exception ex) {
                log.debug("An exception was caught when coverting the current date into a Date object", ex);
            }
        }
        
        if (res.getForecastDate() != null)
        {
            try {
                evalResult.setForecastDate(DateUtil.getDate(res.getForecastDate()));
            } catch (Exception ex) {
                log.error("An exception was caught when coverting the forecast date into a Date object", ex);
            }
        }
        
        if (res.getResultItems() != null)
        {
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ResultItem resItem : res.getResultItems()) {
                evalResult.addResultItem(getResultItem(resItem));
            }
        }
        
        evalResult.setJobDetails(getJobDetails(res.getJobDetails()));
        
        // get meta-data if any
        if (res.getMetaData() != null)
        {
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.KeyValuePair entry : res.getMetaData())
            {
                evalResult.addMetaData(entry.getKey(), entry.getValue());
            }
        }
        
        return evalResult;
    }
    
    /**
     * Get a local instance of the result item from a WS object.
     * @param resItem The WS ResultItem object.
     * @return A local instance of a ResultItem.
     */
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem getResultItem(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ResultItem resItem)
    {
        if (resItem == null)
        {
            log.debug("The ResultItem object from the WS was NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem resultItem;
        resultItem = new uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem();
        
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
    
     public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event getEvent (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Event wsEvent)
    {
        if (wsEvent == null)
        {
            log.debug("The WS Event object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event event;
        event = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event();
        
        event.setUuid(wsEvent.getUuid());
        event.setTitle(wsEvent.getTitle());
        event.setDescription(wsEvent.getDescription());
        
        List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition> eventList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition>();
        for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EventCondition ec : wsEvent.getEventConditions())
            eventList.add(getEventCondition(ec));
        
        event.setEventConditions(eventList);
        
        if (wsEvent.getConfigParams() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter> configParams = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter>();
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter param : wsEvent.getConfigParams())
                configParams.add(getParameter(param));
            
            event.setConfigParams(configParams);
        }
        
        return event;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition getEventCondition(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EventCondition wsEventCondition)
    {
        if (wsEventCondition == null)
        {
            log.debug("The WS EventCondition object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition eventCondition;
        eventCondition = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition();
        
        eventCondition.setUUID(UUID.fromString(wsEventCondition.getUUID()));
        eventCondition.setName(wsEventCondition.getName());
        eventCondition.setDescription(wsEventCondition.getDescription());
        eventCondition.setUnit(wsEventCondition.getUnit());
        eventCondition.setType(getParameterValueType(wsEventCondition.getType()));
        eventCondition.setValuesAllowedType(getValuesAllowedType(wsEventCondition.getValuesAllowedType()));
        
        // pre and post condition ParameterValue
        eventCondition.setPreConditionValue(getParameterValue(wsEventCondition.getPreConditionValue()));
        eventCondition.setPostConditionValue(getParameterValue(wsEventCondition.getPostConditionValue()));
        
        // ValueConstraint list
        if (wsEventCondition.getValueConstraints() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint> valueConstrList;
            valueConstrList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint>();
            
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint vc : wsEventCondition.getValueConstraints()) {
                valueConstrList.add(getValueConstraint(vc));
            }
            
            eventCondition.setValueConstraints(valueConstrList);
        }
        
        if (wsEventCondition.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType> evaluationTypeList;
            evaluationTypeList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType et : wsEventCondition.getAllowedEvaluationTypes()) {
                evaluationTypeList.add(getEvaluationType(et));
            }
            
            eventCondition.setAllowedEvaluationTypes(evaluationTypeList);
        }
        
        return eventCondition;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter getParameter(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter wsParam)
    {
        if (wsParam == null)
        {
            log.debug("The WS Parameter object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter param;
        param = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter();
        
        param.setUUID(UUID.fromString(wsParam.getUUID()));
        param.setName(wsParam.getName());
        param.setDescription(wsParam.getDescription());
        param.setUnit(wsParam.getUnit());
        param.setType(getParameterValueType(wsParam.getType()));
        param.setValue(getParameterValue(wsParam.getValue()));
        param.setValuesAllowedType(getValuesAllowedType(wsParam.getValuesAllowedType()));
        
        // ValueConstraint list
        if (wsParam.getValueConstraints() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint> valueConstrList;
            valueConstrList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint>();
            
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint vc : wsParam.getValueConstraints())
                valueConstrList.add(getValueConstraint(vc));
            
            param.setValueConstraints(valueConstrList);
        }
        
        if (wsParam.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType> evaluationTypeList;
            evaluationTypeList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType et : wsParam.getAllowedEvaluationTypes()) {
                evaluationTypeList.add(getEvaluationType(et));
            }
            
            param.setAllowedEvaluationTypes(evaluationTypeList);
        }
        
        return param;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue getParameterValue(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ParameterValue wsParameterValue)
    {
        if (wsParameterValue == null)
        {
            log.debug("The WS ParameterValue object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue parameterValue;
        parameterValue = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue();
        
        parameterValue.setValue(wsParameterValue.getValue());
        parameterValue.setValueEvaluationType(getEvaluationType(wsParameterValue.getValueEvaluationType()));
        
        return parameterValue;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType getParameterValueType (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ParameterValueType wsParamValueType)
    {
        if (wsParamValueType == null)
        {
            log.debug("The WS ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType paramValueType;
        paramValueType = uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType.fromValue(wsParamValueType.name());
        
        return paramValueType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint getValueConstraint(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint wsValueConstraint)
    {
        if (wsValueConstraint == null)
        {
            log.debug("The WS ValueConstraint object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint valueConstraint;
        valueConstraint = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint();
        
        valueConstraint.setValue(wsValueConstraint.getValue());
        valueConstraint.setConstraintType(getValueConstraintType(wsValueConstraint.getConstraintType()));
        
        return valueConstraint;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType getValueConstraintType (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraintType wsValueConstraintType)
    {
        if (wsValueConstraintType == null)
        {
            log.debug("The WS ValueConstraintType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType valueConstraintType;
        valueConstraintType = uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType.fromValue(wsValueConstraintType.name());
        
        return valueConstraintType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType getValuesAllowedType (uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValuesAllowedType wsValuesAllowedType)
    {
        if (wsValuesAllowedType == null)
        {
            log.debug("The WS ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType valuesAllowedType;
        valuesAllowedType = uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType.fromValue(wsValuesAllowedType.name());
        
        return valuesAllowedType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType getEvaluationType(uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType wsEvaluationType)
    {
        if (wsEvaluationType == null)
        {
            log.debug("The WS ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType evaluationType;
        evaluationType = uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType.fromValue(wsEvaluationType.name());
        
        return evaluationType;
    }
}
