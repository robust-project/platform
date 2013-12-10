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
//      Created Date :          2012-01-20
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient;

import java.util.List;
import java.util.ArrayList;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 *
 * @author Vegard Engen
 */
public class Local2WSClassConverter
{
    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Local2WSClassConverter.class);
    
    /**
     * Get a WS instance of the predictor service job configuration object from
     * the local object.
     *
     * @param config The local predictor service job configuration object.
     * @return
     */
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.PredictorServiceJobConfig getPredictorServiceJobConfig(uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig config)
    {
        if (config == null)
        {
            log.debug("The local PredictorServiceJobConfig object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.PredictorServiceJobConfig jobConfig;
        jobConfig = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.PredictorServiceJobConfig();
        
        try {
            if (config.getStartDate() != null)
                jobConfig.setStartDate(DateUtil.getXMLGregorianCalendar(config.getStartDate()));
        } catch (Exception ex) {
            log.error("An exception was caught when coverting the start date into an XMLGregorianCalendar format", ex);
        }
        
        jobConfig.setCommunityDetails(getCommunityDetails(config.getCommunityDetails()));
        jobConfig.setStreamDetails(getStreamDetails(config.getStreamDetails()));
        jobConfig.setStreaming(config.isStreaming());
        
        if (config.getForecastPeriod() != null)
            jobConfig.setForecastPeriod(getParameter(config.getForecastPeriod()));
        else
            log.debug("The local forecast period (Parameter) object is NULL...");
        
        if (config.getEvents() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Event> events = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Event>();
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event evt : config.getEvents())
                events.add(getEvent(evt));
            
            jobConfig.setEvents(events);
        }
        
        if (config.getConfigurationParams() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter> configParams = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter>();
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter param : config.getConfigurationParams())
                configParams.add(getParameter(param));
            
            jobConfig.setConfigParams(configParams);
        }
        
        if (config.getEvaluationEngineServiceURI() != null)
        {
            jobConfig.setEvaluationEngineServiceURI(config.getEvaluationEngineServiceURI().toString());
        }
        
        return jobConfig;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.CommunityDetails getCommunityDetails(uk.ac.soton.itinnovation.robust.cat.common.ps.CommunityDetails cd)
    {
        if (cd == null)
        {
            log.debug("The local CommunityDetails object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.CommunityDetails communityDetails = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.CommunityDetails();
        
        communityDetails.setCommunityName(cd.getCommunityName());
        communityDetails.setCommunityID(cd.getCommunityID());
        
        return communityDetails;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.StreamDetails getStreamDetails(uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails sd)
    {
        if (sd == null)
        {
            log.debug("The local StreamDetails object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.StreamDetails streamDetails = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.StreamDetails();
        
        streamDetails.setStreamName(sd.getStreamName());
        
        if (sd.getStreamURI() != null)
            streamDetails.setStreamURI(sd.getStreamURI().toString());
        else
            log.debug("The local stream URI object is NULL...");
        
        return streamDetails;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Event getEvent (uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event localEvent)
    {
        if (localEvent == null)
        {
            log.debug("The local Event object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Event event;
        event = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Event();
        
        event.setUuid(localEvent.getUuid().toString());
        event.setTitle(localEvent.getTitle());
        event.setDescription(localEvent.getDescription());
        
        List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EventCondition> eventList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EventCondition>();
        for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition ec : localEvent.getEventConditions())
            eventList.add(getEventCondition(ec));
        
        event.setEventConditions(eventList);
        
        if (localEvent.getConfigParams() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter> configParams = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter>();
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter param : localEvent.getConfigParams())
                configParams.add(getParameter(param));
            
            event.setConfigParams(configParams);
        }
        
        return event;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EventCondition getEventCondition(uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition localEventCondition)
    {
        if (localEventCondition == null)
        {
            log.debug("The local EventCondition object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EventCondition eventCondition;
        eventCondition = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EventCondition();
        
        eventCondition.setUUID(localEventCondition.getUUID().toString());
        eventCondition.setName(localEventCondition.getName());
        eventCondition.setDescription(localEventCondition.getDescription());
        eventCondition.setUnit(localEventCondition.getUnit());
        eventCondition.setType(getParameterValueType(localEventCondition.getType()));
        eventCondition.setValuesAllowedType(getValuesAllowedType(localEventCondition.getValuesAllowedType()));
        
        // pre and post condition ParameterValue
        eventCondition.setPreConditionValue(getParameterValue(localEventCondition.getPreConditionValue()));
        eventCondition.setPostConditionValue(getParameterValue(localEventCondition.getPostConditionValue()));
        
        // ValueConstraint list
        if (localEventCondition.getValueConstraints() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint> valueConstrList;
            valueConstrList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint vc : localEventCondition.getValueConstraints()) {
                valueConstrList.add(getValueConstraint(vc));
            }
            
            eventCondition.setValueConstraints(valueConstrList);
        }
        
        if (localEventCondition.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType> evaluationTypeList;
            evaluationTypeList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType et : localEventCondition.getAllowedEvaluationTypes()) {
                evaluationTypeList.add(getEvaluationType(et));
            }
            
            eventCondition.setAllowedEvaluationTypes(evaluationTypeList);
        }
        
        return eventCondition;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter getParameter(uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter localParam)
    {
        if (localParam == null)
        {
            log.debug("The local Parameter object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter param;
        param = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.Parameter();
        
        param.setUUID(localParam.getUUID().toString());
        param.setName(localParam.getName());
        param.setDescription(localParam.getDescription());
        param.setUnit(localParam.getUnit());
        param.setType(getParameterValueType(localParam.getType()));
        param.setValue(getParameterValue(localParam.getValue()));
        param.setValuesAllowedType(getValuesAllowedType(localParam.getValuesAllowedType()));
        
        // ValueConstraint list
        if (localParam.getValueConstraints() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint> valueConstrList;
            valueConstrList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint vc : localParam.getValueConstraints())
                valueConstrList.add(getValueConstraint(vc));
            
            param.setValueConstraints(valueConstrList);
        }
        
        if (localParam.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType> valueConstrList;
            valueConstrList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType et : localParam.getAllowedEvaluationTypes()) {
                valueConstrList.add(getEvaluationType(et));
            }
            
            param.setAllowedEvaluationTypes(valueConstrList);
        }
        
        return param;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ParameterValue getParameterValue(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue localParameterValue)
    {
        if (localParameterValue == null)
        {
            log.debug("The local ParameterValue object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ParameterValue parameterValue;
        parameterValue = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ParameterValue();
        
        parameterValue.setValue(localParameterValue.getValue());
        parameterValue.setValueEvaluationType(getEvaluationType(localParameterValue.getValueEvaluationType()));
        
        return parameterValue;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ParameterValueType getParameterValueType (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType localParamValueType)
    {
        if (localParamValueType == null)
        {
            log.debug("The local ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ParameterValueType paramValueType;
        paramValueType = uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ParameterValueType.fromValue(localParamValueType.name());
        
        return paramValueType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint getValueConstraint(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint localValueConstraint)
    {
        if (localValueConstraint == null)
        {
            log.debug("The local ValueConstraint object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint valueConstraint;
        valueConstraint = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraint();
        
        valueConstraint.setValue(localValueConstraint.getValue());
        valueConstraint.setConstraintType(getValueConstraintType(localValueConstraint.getConstraintType()));
        
        return valueConstraint;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraintType getValueConstraintType (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType localValueConstraintType)
    {
        if (localValueConstraintType == null)
        {
            log.debug("The local ValueConstraintType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraintType valueConstraintType;
        valueConstraintType = uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValueConstraintType.fromValue(localValueConstraintType.name());
        
        return valueConstraintType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValuesAllowedType getValuesAllowedType (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType localValuesAllowedType)
    {
        if (localValuesAllowedType == null)
        {
            log.debug("The local ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValuesAllowedType valuesAllowedType;
        valuesAllowedType = uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.ValuesAllowedType.fromValue(localValuesAllowedType.name());
        
        return valuesAllowedType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType getEvaluationType(uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType localEvaluationType)
    {
        if (localEvaluationType == null)
        {
            log.debug("The local ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType evaluationType;
        evaluationType = uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen.EvaluationType.fromValue(localEvaluationType.name());
        
        return evaluationType;
    }
}
