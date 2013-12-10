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
//      Created Date :          2011-11-03
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.datastructures;

import java.io.Serializable;
import java.util.*;

/**
 * PS:  not currently handled the situation where there could be more than one pre/post condition value
 * 
 * @author Vegard Engen
 */
public class EventCondition extends AParameter implements Serializable
{
    // OBS: only supporting that a parameter can have a single value 
    //      there's no use case at the time of writing that suggests
    //      multiple values are needed for event parameters.
    private ParameterValue preConditionValue; // this can be null
    private ParameterValue postConditionValue;
    
    // ASSUME HERE THAT THE PRE- AND POST- CONDITION VALUES ARE BOUND BY THE SAME TYPE AND CONSTRAINTS

    /**
     * Default constructor.
     * The value type is set to 'string'
     * The values allowed is set to 'single'
     */
    public EventCondition()
    {
        super();
    }
    
    /**
     * Copy constructor.
     * @eventCond The Parameter object you want to make a copy of.
     */
    public EventCondition(AParameter eventCond)
    {
        super(eventCond);
        
        if (eventCond instanceof EventCondition)
        {
            this.preConditionValue = new ParameterValue(((EventCondition)eventCond).getPreConditionValue());
            this.postConditionValue = new ParameterValue(((EventCondition)eventCond).getPostConditionValue());
        }
    }
    
    /**
     * Constructor which allows to set the UUID of the EventCondition.
     * The value type is set to 'string'
     * The values allowed is set to 'single'
     * @param uuid The UUID of the EventCondition.
     */
    public EventCondition(UUID uuid)
    {
        super(uuid);
    }
    
    /**
     * Constructor to set the parameter values - be aware that some can be null.
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     */
    public EventCondition(ParameterValueType type, String name, String desc, String unit)
    {
        super(type, name, desc, unit);
    }
    
    /**
     * Constructor to set the parameter values - be aware that some can be null.
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param preConditionValue The pre-condition parameter value.
     * @param postConditionValue The post-condition parameter value.
     */
    public EventCondition(ParameterValueType type, String name, String desc, String unit, ParameterValue preConditionValue, ParameterValue postConditionValue)
    {
        this (type, name, desc, unit);
        this.preConditionValue = preConditionValue;
        this.postConditionValue = postConditionValue;
    }
    
    /**
     * Constructor to set the parameter values - be aware that some can be null.
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param preConditionValue The pre-condition parameter value.
     * @param postConditionValue The post-condition parameter value.
     * @param valuesAllowedType The values allowed type {single, multiple, between}
     */
    public EventCondition(ParameterValueType type, String name, String desc, String unit, 
            ParameterValue preConditionValue, ParameterValue postConditionValue,
            ValuesAllowedType valuesAllowedType)
    {
        this (type, name, desc, unit, preConditionValue, postConditionValue);
        this.valuesAllowedType = valuesAllowedType;
    }
    
    /**
     * Constructor to set the parameter values - be aware that some can be null.
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param preConditionValue The pre-condition parameter value.
     * @param postConditionValue The post-condition parameter value.
     * @param valuesAllowedType The values allowed type {single, multiple, between}
     * @param constraints The value constraints.
     */
    public EventCondition(ParameterValueType type, String name, String desc, String unit, 
            ParameterValue preConditionValue, ParameterValue postConditionValue,
            ValuesAllowedType valuesAllowedType, List<ValueConstraint> constraints)
    {
        this (type, name, desc, unit, preConditionValue, postConditionValue, valuesAllowedType);
        if (constraints != null) {
            this.valueConstraints.addAll(constraints);
        }
    }
    
    /**
     * Constructor to set the parameter values - be aware that some can be null.
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param valuesAllowedType The values allowed type {single, multiple, between}
     * @param constraints The value constraints.
     */
    public EventCondition(ParameterValueType type, String name, String desc, String unit, 
            ValuesAllowedType valuesAllowedType, List<ValueConstraint> constraints)
    {
        this (type, name, desc, unit, null, null, valuesAllowedType);
        if (constraints != null) {
            this.valueConstraints.addAll(constraints);
        }
    }
    
    /**
     * Constructor to set the parameter values - be aware that some can be null.
     * @param uuid The UUID
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param valuesAllowedType The values allowed type {single, multiple, between}
     * @param constraints The value constraints.
     */
    public EventCondition(UUID uuid, ParameterValueType type, String name, String desc, String unit, 
            ValuesAllowedType valuesAllowedType, List<ValueConstraint> constraints)
    {
        this (type, name, desc, unit, null, null, valuesAllowedType);
        if (constraints != null) {
            this.valueConstraints.addAll(constraints);
        }
        this.uuid = uuid;
    }

    /**
     * @return the preConditionValue
     */
    public ParameterValue getPreConditionValue()
    {
        return preConditionValue;
    }

    /**
     * @param preConditionValue the preConditionValue to set
     */
    public void setPreConditionValue(ParameterValue preConditionValue)
    {
        this.preConditionValue = preConditionValue;
    }
    
    /**
     * Check if the pre-condition's value is set.
     * @return True if set; false otherwise.
     */
    public boolean isPreConditionValueSet()
    {
        if (this.preConditionValue == null) {
            return false;
        }
        
        return this.preConditionValue.isSet();
    }

    /**
     * @return the postConditionValue
     */
    public ParameterValue getPostConditionValue()
    {
        return postConditionValue;
    }

    /**
     * @param postConditionValue the postConditionValue to set
     */
    public void setPostConditionValue(ParameterValue postConditionValue)
    {
        this.postConditionValue = postConditionValue;
    }
    
    /**
     * Check if the post-condition's value is set.
     * @return True if set; false otherwise.
     */
    public boolean isPostConditionValueSet()
    {
        if (this.postConditionValue == null) {
            return false;
        }
        
        return this.postConditionValue.isSet();
    }

    
    
    /**
     * Checks that the value is valid according to the constraints set.
     * @return
     * @throws Exception 
     *
    public boolean isValid() throws Exception
    {
        throw new UnsupportedOperationException("Validation method not supported yet");
    }*/
    
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventCondition other = (EventCondition) obj;
        if (this.type != other.type) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.preConditionValue != other.preConditionValue && (this.preConditionValue == null || !this.preConditionValue.equals(other.preConditionValue))) {
            return false;
        }
        if (this.postConditionValue != other.postConditionValue && (this.postConditionValue == null || !this.postConditionValue.equals(other.postConditionValue))) {
          return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 47 * hash + (this.preConditionValue != null ? this.preConditionValue.hashCode() : 0);
        hash = 47 * hash + (this.postConditionValue != null ? this.postConditionValue.hashCode() : 0);
        return hash;
    }
    
    /**
     * this tests whether the conditions are equal but without the pre and post conditions
     */
    public boolean equalsConditionValues(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EventCondition other = (EventCondition) obj;
        if (this.type != other.type) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        
        if (this.preConditionValue != other.preConditionValue && (this.preConditionValue == null || !this.preConditionValue.equals(other.preConditionValue))) {
            return false;
        }
        
        if (this.postConditionValue != other.postConditionValue && (this.postConditionValue == null || !this.postConditionValue.equals(other.postConditionValue))) {
            return false;
        }
     
        return true;
    }
}
