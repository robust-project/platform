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
//      Created By :            Vegard Engen, Edwin Tye
//      Created Date :          2013-01-28
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.ps;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.AParameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValidationObject;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType;

/**
 * A general validator for predictor service configuration objects, based on the
 * predictor service description.
 * @author Vegard Engen and Edwin Tye
 */
public class ConfigValidator
{
    static Logger log = Logger.getLogger(ConfigValidator.class);

    /**
     * Validates a configuration object, given the predictor service description.
     * If matchOnUUID is set to true, Parameters and Events in the config object
     * will be matched against respective Parameters and Events in the PS Description
     * object - otherwise the titles/names will be used.
     * Note that this function will also first validate that the PS Description
     * object is valid.
     * @param jobConfig A Job Configuration object that should be validated.
     * @param psDesc A Predictor Service Description object that the configuration object should be validated against.
     * @param matchOnUUID A flag to indicate if Parameters, Events and EventConditions should be matched against UUIDs - otherwise titles/names will be used. Set to false if you have not set fixed UUIDs for those objects.
     * @return 
     */
    public static ValidationObject isConfigValid (PredictorServiceJobConfig jobConfig, PredictorServiceDescription psDesc, boolean matchOnUUID)
    {
        // Currently there are no mechanism that checks whether the jobConfig that is being
        // entered are for which particular description
        try {
            log.debug("Starting the validation of the job configuration object");
            
            log.debug("Validating the predictor service description object");
            ValidationObject vo = PSDValidator.isPredictorServiceDescriptionValid(psDesc);
            if (!vo.valid) {
                log.debug("PSD not valid: " + vo.msg);
                return vo;
            }
            
            log.debug("Validating that nothing important is NULL or missing/empty collections");
            vo = isMinimalDataConfigured(jobConfig);
            if (!vo.valid) {
                log.debug("PS Config not valid: " + vo.msg);
                return vo;
            }
            
            log.debug("Validating the forecast parameter");
            vo = isForecastParameterValid(jobConfig.getForecastPeriod(), psDesc.getForecastPeriod());
            if (!vo.valid) {
                log.debug("PS Config not valid: " + vo.msg);
                return vo;
            }
            
            log.debug("Validating the events");
            vo = areEventsValid(jobConfig.getEvents(), psDesc.getEvents(), matchOnUUID);
            if (!vo.valid) {
                log.debug("PS Config not valid: " + vo.msg);
                return vo;
            }
            
            log.debug("Validating the configuration parameters");
            vo = areConfigParamsValid(jobConfig.getConfigurationParams(), psDesc.getConfigurationParams(), matchOnUUID);
            if (!vo.valid) {
                log.debug("PS Config not valid: " + vo.msg);
                return vo;
            }
        } catch (Exception e) {
            log.error("Caught an exception when validating the configuration object: " + e.getMessage(), e);
            return new ValidationObject(false, "Caught an exception when validating the configuration object: " + e.toString());
        }
        
        log.debug("Configuration object is valid");
        return new ValidationObject(true);
    }

    /**
     * Checks for NULL or empty data, which should be provided.
     * @return 
     */
    private static ValidationObject isMinimalDataConfigured(PredictorServiceJobConfig jobConfig)
    {
        if (jobConfig == null) {
            return new ValidationObject(false, "The configuration object provided is NULL");
        }
        
        if (jobConfig.getCommunityDetails() == null) {
            return new ValidationObject(false, "The community object in the job config is NULL");
        }
        
        if ((jobConfig.getEvents() == null) || jobConfig.getEvents().isEmpty()) {
            return new ValidationObject(false, "There are no events in the config object");
        }
        
        if (jobConfig.getStartDate() == null) {
            return new ValidationObject(false, "The start date in the job config is NULL");
        }
        
        if (jobConfig.getForecastPeriod() == null) {
            return new ValidationObject(false, "The forecast period parameter in the job config is NULL");
        }
        
        if ((jobConfig.getForecastPeriod().getValue() == null) || (jobConfig.getForecastPeriod().getValue().getValue() == null) || jobConfig.getForecastPeriod().getValue().getValue().isEmpty()) {
            return new ValidationObject(false, "The forecast period parameter in the job config has not been set");
        }
        
        if (jobConfig.getEvaluationEngineServiceURI() == null) {
            return new ValidationObject(false, "The URI of the Evaluation Engine Service (to return results to) is NULL");
        }
        
        return new ValidationObject(true);
    }
    
    private static ValidationObject isForecastParameterValid(Parameter forecastPeriodConfig, Parameter forecastPeriodDesc)
    {
        int forecastValueConfig = Integer.valueOf(forecastPeriodConfig.getValue().getValue());
        Integer min = null;
        Integer max = null;
        
        try {
            String minStr = forecastPeriodDesc.getMin();
            if (minStr != null) {
                min = Integer.parseInt(minStr);
            }
        } catch (Exception ex) { log.debug("Unable to get Parameter min constraint: " + ex.toString()); }
        
        try {
            String maxStr = forecastPeriodDesc.getMax();
            if (maxStr != null) {
                max = Integer.parseInt(maxStr);
            }
        } catch (Exception ex) { log.debug("Unable to get Parameter max constraint: " + ex.toString()); }
        
        if ((min != null) && (forecastValueConfig < min))
        {
            return new ValidationObject(false, "The forecast period (" + forecastValueConfig + ") is below the minimum value allowed (" + min + ")");
        }
        
        if ((max != null) && (forecastValueConfig > max))
        {
            return new ValidationObject(false, "The forecast period (" + forecastValueConfig + ") is above the maximum value allowed (" + max + ")");
        }
        
        return new ValidationObject(true);
    }
    
    private static ValidationObject areEventsValid(Set<Event> configEvents, List<Event> descEvents, boolean matchOnUUID)
    {
        // iterating over each event
        for (Event jobConfigEvent : configEvents)
        {
            ValidationObject vo = isEventDataSet(jobConfigEvent);
            if (!vo.valid) {
                return new ValidationObject(false, "Event invalid: " + vo.msg);
            }
            
            Event descEvent = getMatchedEvent(jobConfigEvent, descEvents, matchOnUUID);
            if (descEvent == null) {
                return new ValidationObject(false, "Unknown event '" + jobConfigEvent.getTitle() + "' with UUID " + jobConfigEvent.getUuid());
            }
            
            // check event's config parameters
            vo = areConfigParamsValid(jobConfigEvent.getConfigParams(), descEvent.getConfigParams(), matchOnUUID);
            if (!vo.valid) {
                return new ValidationObject(false, "Event's config parameters invalid: " + vo.msg);
            }
            
            // check the event's conditions
            vo = areEventConditionsValid(jobConfigEvent.getEventConditions(), descEvent.getEventConditions(), matchOnUUID);
            if (!vo.valid) {
                return new ValidationObject(false, "Event's event conditions invalid: " + vo.msg);
            }
        }
        
        return new ValidationObject(true);
    }
    
    private static ValidationObject isEventDataSet(Event evt)
    {
        if (evt == null) {
            return new ValidationObject(false, "NULL event in the config object");
        }
        
        if (evt.getUuid() == null) {
            return new ValidationObject(false, "The UUID of an event was not set");
        }
        
        if (evt.getTitle() == null) {
            return new ValidationObject(false, "The title of an event was not set");
        }
        
        if (evt.getNumEventParams() == 0) {
            return new ValidationObject(false, "No event conditions set for the event");
        }
        
        for (EventCondition ec : evt.getEventConditions())
        {
            if (ec.getUUID() == null) {
                return new ValidationObject(false, "EventCondition UUID is NULL");
            }
            
            if (ec.getName() == null) {
                return new ValidationObject(false, "EventCondition name is NULL");
            }
            
            if (ec.getType() == null) {
                return new ValidationObject(false, "EventCondition type is NULL");
            }
            
            if (!ec.isPostConditionValueSet()) {
                return new ValidationObject(false, "EventCondition post-condition not set");
            }
            
            ValidationObject vo = isParameterValueSet(ec.getPostConditionValue());
            if (!vo.valid) {
                return new ValidationObject(false, "EventCondition post-condition not valid: " + vo.msg);
            }
            
            // check if pre-condition is set - if so, validate that the necessary info is set
            if (ec.isPreConditionValueSet()) {
                vo = isParameterValueSet(ec.getPreConditionValue());
                if (!vo.valid) {
                    return new ValidationObject(false, "EventCondition pre-condition not valid: " + vo.msg);
                }
            }
        }
        
        return new ValidationObject(true);
    }
    
    private static ValidationObject isParameterValueSet(ParameterValue paramVal)
    {
        if (paramVal == null) {
            return new ValidationObject(false, "The object is NULL");
        }
        
        if (!paramVal.isSet()) {
            return new ValidationObject(false, "Value not set");
        }
        
        if (paramVal.getValueEvaluationType() == null) {
            return new ValidationObject(false, "Evaluation type not set");
        }
        
        return new ValidationObject(true); 
    }
    
    /**
     * Assume here that the Event objects have been validated already
     * @param configEvent
     * @return NULL if no match.
     */
    private static Event getMatchedEvent(Event configEvent, List<Event> descEvents, boolean matchOnUUID)
    {
        for (Event descEvent : descEvents)
        {
            if (matchOnUUID)
            {
                if (configEvent.getUuid().equals(descEvent.getUuid())) {
                    return descEvent;
                }
            }
            else // match on title
            {
                if (configEvent.getTitle().equals(descEvent.getTitle())) {
                    return descEvent;
                }
            }
        }
        
        return null;
    }
    
    private static ValidationObject areEventConditionsValid(List<EventCondition> configConditions, List<EventCondition> descConditions, boolean matchOnUUID)
    {
        for (EventCondition configEC : configConditions)
        {
            EventCondition descEC = getMatchedEventCondition(configEC, descConditions, matchOnUUID);
            
            if (descEC == null) {
                if (matchOnUUID) {
                    return new ValidationObject(false, "No event condition found with the name '" + configEC.getName() + "' (UUID: " + configEC.getUUID().toString() + ")");
                } else {
                    return new ValidationObject(false, "No event condition found with the UUID '" + configEC.getUUID().toString() + "' (name: " + configEC.getName() + ")");
                }
            }
            
            if (!configEC.getType().equals(descEC.getType())) {
                return new ValidationObject(false, "EventCondtion (name: '" + configEC.getName() + "', UUID: " + configEC.getUUID().toString() + ") has a different value type than that in the description (" + configEC.getType() + ", instead of " + descEC.getType() + ")");
            }
            
            // check post-condition
            ValidationObject vo = isParameterValueValid(configEC.getPostConditionValue(), descEC, configEC.getType());
            if (!vo.valid) {
                return new ValidationObject(false, "EventCondtion (name: '" + configEC.getName() + "', UUID: " + configEC.getUUID().toString() + ") post-condition not valid: " + vo.msg);
            }
            
            // check pre-condition - if set
            if (configEC.isPreConditionValueSet())
            {
                vo = isParameterValueValid(configEC.getPostConditionValue(), descEC, configEC.getType());
                if (!vo.valid) {
                    return new ValidationObject(false, "EventCondtion (name: '" + configEC.getName() + "', UUID: " + configEC.getUUID().toString() + ") pre-condition not valid: " + vo.msg);
                }
            }
        }
        return new ValidationObject(true); 
    }
    
    /**
     * Assume here that the EventCondition objects have been validated already
     * @param configEventCondition
     * @param descEventConditions
     * @param matcOnUUID
     * @return NULL if no match.
     */
    private static EventCondition getMatchedEventCondition(EventCondition configEventCondition, List<EventCondition> descEventConditions, boolean matchOnUUID)
    {
        for (EventCondition descEventCond : descEventConditions)
        {
            if (matchOnUUID)
            {
                if (configEventCondition.getUUID().equals(descEventCond.getUUID())) {
                    return descEventCond;
                }
            }
            else // match on title
            {
                if (configEventCondition.getName().equals(descEventCond.getName())) {
                    return descEventCond;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Assume here that the Parameter objects have been validated already
     * @param configEventCondition
     * @param descParam
     * @param valueType
     * @return
     */
    private static ValidationObject isParameterValueValid(ParameterValue configValue, AParameter descParam, ParameterValueType valueType)
    {
        ValidationObject vo = null;
        
        switch (valueType) {
            case BOOLEAN:
                try {
                    Boolean.parseBoolean(configValue.getValue());
                } catch (Exception ex) {
                    return new ValidationObject(false, "The value is not a valid Boolean (" + configValue + "): " + ex.toString());
                }
                break;
            case INT:
                vo = checkNumericConstraints(configValue, descParam.getValueConstraints(), valueType);
                if (!vo.valid) {
                    return new ValidationObject(false, "The value is not a valid INT: " + vo.msg);
                }
                break;
            case FLOAT:
                vo = checkNumericConstraints(configValue, descParam.getValueConstraints(), valueType);
                if (!vo.valid) {
                    return new ValidationObject(false, "The value is not a valid FLOAT: " + vo.msg);
                }
                break;
            case STRING:
                vo = checkStringConstraints(configValue, descParam.getValueConstraints());
                if (!vo.valid) {
                    return new ValidationObject(false, "The value is not a valid STRING: " + vo.msg);
                }
                break;
            default:
               break;
        }
        
        vo = isEvaluationTypeValid(configValue.getValueEvaluationType(), descParam.getAllowedEvaluationTypes(), valueType);
        if (!vo.valid) {
            return vo;
        }
        
        return new ValidationObject(true);
    }
    
    /**
     * Assume here that the EventCondition objects have been validated already
     * @param configEventConditionValue
     * @param descEventCondition
     * @param valueType
     * @return 
     */
    private static ValidationObject checkNumericConstraints(ParameterValue configEventConditionValue, List<ValueConstraint> valueConstraints, ParameterValueType valueType)
    {
        String valueStr = configEventConditionValue.getValue();
        Double valueDouble = null;
        Integer valueInt = null;
        
        // try to parse the value string to a number, depending on value type
        if (valueType.equals(ParameterValueType.INT))
        {
            try {
                valueInt = Integer.parseInt(valueStr);
            } catch (Exception ex) {
                return new ValidationObject(false, "Unable to covert the value to a number (" + valueStr + "): " + ex.toString());
            }
        }
        else if (valueType.equals(ParameterValueType.FLOAT))
        {
            try {
                valueDouble = Double.parseDouble(valueStr);
            } catch (Exception ex) {
                return new ValidationObject(false, "Unable to covert the value to a number (" + valueStr + "): " + ex.toString());
            }
        }
        
        // get constraints - min, max or values allowed
        String minStr = null;
        String maxStr = null;
        Set<String> valuesAllowed = new HashSet<String>();
        
        //iterating over all the conditions in the PSD object
        for (ValueConstraint constraint : valueConstraints)
        {
            ValueConstraintType constraintType = constraint.getConstraintType();
            if (constraintType.equals(ValueConstraintType.MIN)) {
                minStr = constraint.getValue();
            } else if (constraintType.equals(ValueConstraintType.MAX)) {
                maxStr = constraint.getValue();
            } else if (constraintType.equals(ValueConstraintType.VALUESALLOWED)) {
                valuesAllowed.add(constraint.getValue());
            }
        }
        
        // check the constraints
        if (minStr != null)
        {
            if (valueType.equals(ParameterValueType.INT))
            {
                if (valueInt < Integer.parseInt(minStr)) {
                    return new ValidationObject(false, "The value (" + valueStr + ") is below the minimum value allowed (" + minStr + ")");
                }
            }
            else // double
            {
                if (valueDouble < Double.parseDouble(minStr)) {
                    return new ValidationObject(false, "The value (" + valueStr + ") is below the minimum value allowed (" + minStr + ")");
                }
            }
        }
        
        if (maxStr != null)
        {
            if (valueType.equals(ParameterValueType.INT))
            {
                if (valueInt > Integer.parseInt(maxStr)) {
                    return new ValidationObject(false, "The value (" + valueStr + ") is above the maximum value allowed (" + maxStr + ")");
                }
            }
            else // double
            {
                if (valueDouble > Double.parseDouble(maxStr)) {
                    return new ValidationObject(false, "The value (" + valueStr + ") is above the maximum value allowed (" + maxStr + ")");
                }
            }
        }
        
        // check the values allowed
        if (!valuesAllowed.isEmpty())
        {
            if (!valuesAllowed.contains(valueStr)) {
                return new ValidationObject(false, "The value (" + valueStr + ") is not one of the values allowed");
            }
        }
        
        return new ValidationObject(true);
    }
    
    /**
     * Assume here that the EventCondition objects have been validated already
     * @param configEventConditionValue
     * @param descEventCondition
     * @return 
     */
    private static ValidationObject checkStringConstraints(ParameterValue configEventConditionValue, List<ValueConstraint> valueConstraints)
    {
        String valueStr = configEventConditionValue.getValue();
        Set<String> valuesAllowed = new HashSet<String>();
        
        for (ValueConstraint valConstr : valueConstraints)
        {
            if (valConstr.getConstraintType().equals(ValueConstraintType.VALUESALLOWED)) {
                valuesAllowed.add(valConstr.getValue());
            }
        }
        
        // check the values allowed
        if (!valuesAllowed.isEmpty())
        {
            if (!valuesAllowed.contains(valueStr)) {
                return new ValidationObject(false, "The value string (" + valueStr + ") is not one of the values allowed");
            }
        }
        
        return new ValidationObject(true);
    }
    
    /**
     * Checks that the evaluation type is among those set as being allowed.
     * @param evalType
     * @param allowedEvaluationTypes
     * @param valueType
     * @return 
     */
    private static ValidationObject isEvaluationTypeValid(EvaluationType evalType, List<EvaluationType> allowedEvaluationTypes, ParameterValueType valueType)
    {
        if (evalType == null)
        {
            if (allowedEvaluationTypes.isEmpty()) {
                return new ValidationObject(true);
            } else {
                return new ValidationObject(false, "Evaluation type not given, but there were constraints that have been set");
            }
        }
        else if (allowedEvaluationTypes.isEmpty()) // no constraints set, but a value has been set - need to validate against default values
        {
            for (EvaluationType et : EvaluationType.getAllowedEvaluationTypes(valueType)) {
                if (evalType.equals(et)) {
                    return new ValidationObject(true);
                }
            }
        } 
        else 
        {
            for (EvaluationType et : allowedEvaluationTypes) {
                if (evalType.equals(et)) {
                    return new ValidationObject(true);
                }
            }
        }
		
		log.debug("Allowed eval types (" + allowedEvaluationTypes.size() + "): ");
		for (EvaluationType et : allowedEvaluationTypes) {
			log.debug(" - " + et);
		}
        
        return new ValidationObject(false, "Evaluation type (" + evalType + ") not among the allowed types");
    }
    
    /**
     * Assume here that the Parameter objects have been validated already
     * @param configParams
     * @param psDescConfigParams
     * @param matchOnUUID
     * @return 
     */
    private static ValidationObject areConfigParamsValid(List<Parameter> configParams, List<Parameter> psDescConfigParams, boolean matchOnUUID)
    {
        // iterate over all configuration parameters
        for (Parameter configParam : configParams)
        {
            ValidationObject vo = isParameterDataSet(configParam);
            if (!vo.valid) {
                return new ValidationObject(false, "Configuration parameter not valid: " + vo.msg);
            }
            
            Parameter descConfigParam = getMatchedConfigParameter(configParam, psDescConfigParams, matchOnUUID);
            if (descConfigParam == null){
                return new ValidationObject(false, "A configuration parameter (name = '" + configParam.getName() + "', UUID = " + configParam.getUUID().toString() + ") was not found in the PS Description object");
            }
            
            if (!configParam.getType().equals(descConfigParam.getType())) {
                return new ValidationObject(false, "Parameter (name: '" + configParam.getName() + "', UUID: " + configParam.getUUID().toString() + ") has a different value type than that in the description (" + configParam.getType() + ", instead of " + descConfigParam.getType() + ")");
            }
            
            vo = isParameterValueValid(configParam.getValue(), descConfigParam, configParam.getType());
            if (!vo.valid) {
                return new ValidationObject(false, "Configuration parameter (name = '" + configParam.getName() + "', UUID = " + configParam.getUUID().toString() + ") invalid: " + vo.msg);
            }
        }
        
        return new ValidationObject(true);
    }
    
    private static ValidationObject isParameterDataSet(Parameter param)
    {
        if (param == null) {
            return new ValidationObject(false, "Parameter is NULL");
        }
        
        if (param.getUUID() == null) {
            return new ValidationObject(false, "The UUID is NULL");
        }
        
        if (param.getName() == null) {
            return new ValidationObject(false, "The name is NULL");
        }
        
        if (param.getType() == null) {
            return new ValidationObject(false, "The type is NULL");
        }
        
        if (!param.isValueSet()) {
            return new ValidationObject(false, "Value not set");
        }
        
        return new ValidationObject(true);
    }
    
    /**
     * Assume here that the Parameter objects have been validated already
     * @param configParameter
     * @param descParameters
     * @param matchOnUUID
     * @return NULL if no match.
     */
    private static Parameter getMatchedConfigParameter(Parameter configParameter, List<Parameter> descParameters,  boolean matchOnUUID)
    {
        for (Parameter descParam : descParameters)
        {
            if (matchOnUUID)
            {
                if (configParameter.getUUID().equals(descParam.getUUID())) {
                    return descParam;
                }
            }
            else // match on name
            {
                if (configParameter.getName().equals(descParam.getName()))
                {
                    return descParam;
                }
            }
        }
        
        return null;
    }
}
