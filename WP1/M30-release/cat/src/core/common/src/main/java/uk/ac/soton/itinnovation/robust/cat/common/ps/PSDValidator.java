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
//      Created Date :          2013-01-30
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
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValidationObject;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType;

/**
 * A class to validate a Predictor Service Description object.
 * @author Vegard Engen
 */
public class PSDValidator
{
    static Logger log = Logger.getLogger(PSDValidator.class);
    
    /**
     * Validates a Predictor Service Description object, checking that the minimum
     * required data is provided and that any value constraints are valid.
     * @param psDesc A PredictorServiceDescription object.
     * @return 
     */
    public static ValidationObject isPredictorServiceDescriptionValid(PredictorServiceDescription psDesc)
    {
        try {
            log.debug("Validating that nothing important is NULL or missing/empty collections");
            ValidationObject vo = isMinimalDataConfigured(psDesc);
            if (!vo.valid) {
                return vo;
            }
            
            log.debug("Validating the forecast parameter");
            vo = isForecastParameterValid(psDesc.getForecastPeriod());
            if (!vo.valid) {
                log.debug("PS Config not valid: " + vo.msg);
                return vo;
            }
            
            log.debug("Validating the events");
            vo = areEventsValid(psDesc.getEvents());
            if (!vo.valid) {
                log.debug("PS Config not valid: " + vo.msg);
                return vo;
            }
            
            log.debug("Validating the configuration parameters");
            vo = areConfigParamsValid(psDesc.getConfigurationParams());
            if (!vo.valid) {
                log.debug("PS Config not valid: " + vo.msg);
                return vo;
            }
        } catch (Exception e) {
            log.error("Caught an exception when validating the PS Description object: " + e.getMessage(), e);
            return new ValidationObject(false, "Caught an exception when validating the PS Description object: " + e.toString());
        }
        
        return new ValidationObject(true);
    }
    
    /**
     * Checks for NULL or empty data, which should be provided.
     * @return 
     */
    private static ValidationObject isMinimalDataConfigured(PredictorServiceDescription psDesc)
    {
        if (psDesc == null) {
            return new ValidationObject(false, "The PSD object provided is NULL");
        }
        
        if (psDesc.getName() == null) {
            return new ValidationObject(false, "The PSD name is NULL");
        }
        
        if (psDesc.getVersion() == null) {
            return new ValidationObject(false, "The PSD version is NULL");
        }
        
        if (psDesc.getDescription() == null) {
            return new ValidationObject(false, "The PSD description name is NULL");
        }
        
        if ((psDesc.getEvents() == null) || psDesc.getEvents().isEmpty()) {
            return new ValidationObject(false, "There are no events in the PSD object");
        }
        
        if (psDesc.getForecastPeriod() == null) {
            return new ValidationObject(false, "The forecast period parameter in the PSD is NULL");
        }
        
        if ((psDesc.getEvents() == null) || psDesc.getEvents().isEmpty()) {
            return new ValidationObject(false, "There are no events in the PSD object");
        }
        
        return new ValidationObject(true);
    }
    
    private static ValidationObject isForecastParameterValid(Parameter forecastPeriod)
    {
        // TODO: need to validate the forecasting parameter
        
        return new ValidationObject(true);
    }
    
    private static ValidationObject areEventsValid(List<Event> events)
    {
        // iterating over each event
        for (Event event : events)
        {
            ValidationObject vo = isEventDataSet(event);
            if (!vo.valid) {
                return new ValidationObject(false, "Event invalid: " + vo.msg);
            }
            
            // check event's config parameters
            vo = areConfigParamsValid(event.getConfigParams());
            if (!vo.valid) {
                return new ValidationObject(false, "Event's config parameters invalid: " + vo.msg);
            }
            
            // check the event's conditions
            for (AParameter eventCond : event.getEventConditions())
            {
                vo = isParameterDataSet(eventCond);
                if (!vo.valid) {
                    return new ValidationObject(false, "Event condition not valid: " + vo.msg);
                }
                
                vo = areParameterValueConstraintsValid(eventCond);
                if (!vo.valid) {
                    return new ValidationObject(false, "Event condition (name = '" + eventCond.getName() + "', UUID = " + eventCond.getUUID().toString() + ") invalid: " + vo.msg);
                }
                
                vo = areParameterEvaluationConstraintsValid(eventCond);
                if (!vo.valid) {
                    return new ValidationObject(false, "Event condition (name = '" + eventCond.getName() + "', UUID = " + eventCond.getUUID().toString() + ") invalid: " + vo.msg);
                }
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
            ValidationObject vo = isParameterDataSet(ec);
            if (!vo.valid) {
                return new ValidationObject(false, "EventCondition invalid: " + vo.msg);
            }
        }
        
        return new ValidationObject(true);
    }
    
    /**
     * Assume here that the Parameter objects have been validated already
     * @param configParams
     * @param psDescConfigParams
     * @param matchOnUUID
     * @return 
     */
    private static ValidationObject areConfigParamsValid(List<Parameter> configParams)
    {
        // iterate over all configuration parameters
        for (AParameter configParam : configParams)
        {
            ValidationObject vo = isParameterDataSet(configParam);
            if (!vo.valid) {
                return new ValidationObject(false, "Configuration parameter not valid: " + vo.msg);
            }
            
            vo = areParameterValueConstraintsValid(configParam);
            if (!vo.valid) {
                return new ValidationObject(false, "Configuration parameter (name = '" + configParam.getName() + "', UUID = " + configParam.getUUID().toString() + ") invalid: " + vo.msg);
            }
            
            vo = areParameterEvaluationConstraintsValid(configParam);
            if (!vo.valid) {
                return new ValidationObject(false, "Configuration parameter (name = '" + configParam.getName() + "', UUID = " + configParam.getUUID().toString() + ") invalid: " + vo.msg);
            }
        }
        
        return new ValidationObject(true);
    }
     
    private static ValidationObject isParameterDataSet(AParameter param)
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
        
        return new ValidationObject(true);
    }
    
    /**
     * Check whether the value constraints of an AParameter instance are valid, 
     * if any are set. Fails the validation if there are "silly" constraints
     * defined, such as min/max/step for BOOLEAN and STRING parameters.
     * @param param Could be a Parameter or EventCondition
     * @return 
     */
    private static ValidationObject areParameterValueConstraintsValid(AParameter param)
    {
        // check value constraints
        List<ValueConstraint> valueConstraints = param.getValueConstraints();
        
        if((valueConstraints == null) || valueConstraints.isEmpty()) {
            // assume here that also STRING type of parameters are valid without
            // constraints, i.e., free text is possible
            return new ValidationObject(true);
        }
        
        // get "stats" about how many instances of different constraint types there are
        int minCount = 0;
        int maxCount = 0;
        int stepCount = 0;
        int defaultCount = 0;
        int valuesAllowedCount = 0;

        for (ValueConstraint vc : valueConstraints)
        {
            if (vc.getConstraintType().equals(ValueConstraintType.DEFAULT)) {
                defaultCount++;
            } else if (vc.getConstraintType().equals(ValueConstraintType.MIN)) {
                minCount++;
            } else if (vc.getConstraintType().equals(ValueConstraintType.MAX)) {
                maxCount++;
            } else if (vc.getConstraintType().equals(ValueConstraintType.STEP)) {
                stepCount++;
            } else if (vc.getConstraintType().equals(ValueConstraintType.VALUESALLOWED)) {
                valuesAllowedCount++;
            }
        }
        
        if (param.getType().equals(ParameterValueType.BOOLEAN))
        {
            // VALID CASES:
            //   1: no constraints - defaults to true/false
            //   2: 2 constraints - both VALUESALLOWED
            //   3: 3 constraints - 2 VALUESALLOWED and 1 DEFAULT
            
            if (valueConstraints.size() > 3) {
                return new ValidationObject(false, "BOOLEAN parameter type with more than 3 value constraints defined - should be (a) none if true/false, (b) two VALUESALLOWED defined, or (c) two VALUESALLOWED and one DEFAULT value defined");
            }
            
            if ((valueConstraints.size() == 3) && (valuesAllowedCount != 2)) {
                return new ValidationObject(false, "BOOLEAN parameter type with 3 value constraints should have defined two VALUESALLOWED and one DEFAULT value; got " + valuesAllowedCount + " VALUESALLOWED and " + defaultCount + " DEFAULT constraints");
            }
            
            if ((valueConstraints.size() == 2) && (valuesAllowedCount != 2)) {
                return new ValidationObject(false, "BOOLEAN parameter type with 2 value constraints should have defined two VALUESALLOWED (got " + valuesAllowedCount + ")");
            }
            
            if ((minCount > 0) || (maxCount > 0) || (stepCount > 0)) {
                return new ValidationObject(false, "BOOLEAN parameter with MIN, MAX or STEP constraints defined");
            }
        }
        else if (param.getType().equals(ParameterValueType.STRING))
        {
            // VALID CASES:
            //   1: no constraints - assume free text is possible
            //   2: 2 constraints - both of which VALUESALLOWED
            //   3: more than 2 constraints - all either VALUESALLOWED, or one could also be DEFAULT
            
            if ( (valueConstraints.size() == 2) && (valuesAllowedCount != 2) ) {
                return new ValidationObject(false, "STRING parameter type with 2 value constraints should have defined two VALUESALLOWED (got " + valuesAllowedCount + ")");
            }
            
            if ( (valueConstraints.size() > 2) && ( 
                    (valuesAllowedCount < (valueConstraints.size()-1) ) || 
                    ( (valuesAllowedCount == (valueConstraints.size()-1) ) && (defaultCount != 1) ) ) )  {
                return new ValidationObject(false, "STRING parameter type with more than 2 value constraints (" + valueConstraints.size() + ") should have defined either (a) all as VALUESALLOWED, or (b) all but one as VALUESALLOWED and one as DEFAULT (got " + valuesAllowedCount + " VALUESALLOWED and " + defaultCount + " DEFAULT)");
            }
            
            if ((minCount > 0) || (maxCount > 0) || (stepCount > 0)) {
                return new ValidationObject(false, "STRING parameter with MIN, MAX or STEP constraints defined");
            }
        }
        else if (param.getType().equals(ParameterValueType.INT) || param.getType().equals(ParameterValueType.FLOAT))
        {
            // VALID CASES
            //   1: only one DEFAULT constraint
            //   2: only one MIN constraint
            //   3: only one MAX constraint
            //   4: one MIN and one MAX constraint
            //   5: one MIN, one MAX, one STEP constraint
            //   6: only VALUESALLOWED, but only if more than one constraints
            //   7: all the above with an additional DEFAULT constraint (except for #1)
            
            if (valueConstraints.size() == 1)
            {
                if (valuesAllowedCount == 1) {
                    return new ValidationObject(false, "NUMERIC parameter with only one, invalid constraint set, VALUESALLOWED");
                } else if (stepCount == 1) {
                    return new ValidationObject(false, "NUMERIC parameter with only one, invalid constraint set, STEP");
                }
            }
            else if (valueConstraints.size() == 2)
            {
                if (valuesAllowedCount == 1) {
                    return new ValidationObject(false, "NUMERIC parameter with 2 constraints where one is VALUESALLOWED, which doesn't make sense");
                } else if (stepCount == 1) {
                    return new ValidationObject(false, "NUMERIC parameter with 2 constraints where one is STEP, which doesn't make sense");
                }
                else if ((minCount > 1) || (maxCount > 1) || (defaultCount > 1)) {
                    return new ValidationObject(false, "NUMERIC parameter with 2 constraints where both is the same type (min/max/default), which doesn't make sense");
                }
            }
            else if (valueConstraints.size() == 3)
            {
                // VALID CASES
                //   1: min, max, step
                //   2: min, max, default
                //   3: valuesallowed x 3
                //   4: valuesallowed x 2, default
                
                if ( (minCount == 1) && (maxCount == 1) && (stepCount == 1) ) {
                    return new ValidationObject(true);
                } else if ( (minCount == 1) && (maxCount == 1) && (defaultCount == 1) ) {
                    return new ValidationObject(true);
                } else if (valuesAllowedCount == valueConstraints.size()) {
                    return new ValidationObject(true);
                } else if ( (defaultCount == 1) && (valuesAllowedCount == (valueConstraints.size()-1)) ) {
                    return new ValidationObject(true);
                } else {
                    return new ValidationObject(false, "NUMERIC parameter with " + valueConstraints.size() + " constraints doesn't satisfy the only valid combinations: (1) MIN, MAX, STEP; (2) (1) MIN, MAX, DEFAULT; (3) all VALUESALLOWED; or (4) one DEFAULT and rest VALUESALLOWED");
                }
            }
            else if (valueConstraints.size() == 4)
            {
                // VALID CASES
                //   1: min, max, step, default
                //   2: valuesallowed x 4
                //   3: valuesallowed x 3, default
                
                if ( (minCount == 1) && (maxCount == 1) && (stepCount == 1) && (defaultCount == 1) ) {
                    return new ValidationObject(true);
                } else if (valuesAllowedCount == valueConstraints.size()) {
                    return new ValidationObject(true);
                } else if ( (defaultCount == 1) && (valuesAllowedCount == (valueConstraints.size()-1)) ) {
                    return new ValidationObject(true);
                } else {
                    return new ValidationObject(false, "NUMERIC parameter with " + valueConstraints.size() + " constraints doesn't satisfy the only valid combinations: (1) MIN, MAX, STEP, DEFAULT; (2) all VALUESALLOWED; or (3) one DEFAULT and rest VALUESALLOWED");
                }
            }
            else if (valueConstraints.size() >= 5)
            {
                // VALID CASES
                //   1: valuesallowed x valueConstraints.size()
                //   2: valuesallowed x valueConstraints.size()-1, default
                if (valuesAllowedCount == valueConstraints.size()) {
                    return new ValidationObject(true);
                } else if ( (defaultCount == 1) && (valuesAllowedCount == (valueConstraints.size()-1)) ) {
                    return new ValidationObject(true);
                } else {
                    return new ValidationObject(false, "NUMERIC parameter with " + valueConstraints.size() + " constraints doesn't satisfy the only valid combinations: (1) all VALUESALLOWED or (2) one DEFAULT and rest VALUESALLOWED");
                }
            }
        }
        
        return new ValidationObject(true);
    }
    
    /**
     * Check whether the evaluation type constraints of an AParameter instance are
     * valid, if any are set.
     * @param param Could be a Parameter or EventCondition
     * @return 
     */
    private static ValidationObject areParameterEvaluationConstraintsValid(AParameter param)
    {
        Set<EvaluationType> validEvaluationTypes = new HashSet<EvaluationType>();
        validEvaluationTypes.addAll(EvaluationType.getAllowedEvaluationTypes(param.getType()));
        
        for (EvaluationType et : param.getAllowedEvaluationTypes())
        {
            if (!validEvaluationTypes.contains(et)) {
                return new ValidationObject(false, "Evaluation type " + et + " not valid for parameter type " + param.getType());
            }
        }
        
        // check the evaluation type constraints
        
        return new ValidationObject(true);
    }
}
