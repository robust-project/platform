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
//      Created Date :          2013-01-31
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.datastructures;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * An abstract Parameter class, containing meta-data about a parameter. The actual
 * value of a parameter should be defined in the class that extends this.
 * @see Parameter
 * @see EventCondition
 * @author Vegard Engen
 */
public abstract class AParameter implements Serializable
{
    protected UUID uuid;   
    protected ParameterValueType type; // assuming that the pre- and post-values are of the same type
    protected String name;
    protected String description;
    protected String unit; // can be null
    
    protected ValuesAllowedType valuesAllowedType; // single/multiple/between - not used at the moment
    protected List<ValueConstraint> valueConstraints; // should define the bounds on the value, if any (can be NULL)
    protected List<EvaluationType> allowedEvaluationTypes; // should define the allowed evaluation types - if empty, means all are allowed
    
    static Logger log = Logger.getLogger(AParameter.class);
    
    /**
     * Default constructor.
     * The value type is set to 'string'
     * The values allowed is set to 'single'
     */
    public AParameter()
    {
        this.uuid = UUID.randomUUID();
        this.valuesAllowedType = ValuesAllowedType.SINGLE; // default to single
        this.type = ParameterValueType.STRING; // default to string
        this.valueConstraints = new ArrayList<ValueConstraint>();
        this.allowedEvaluationTypes = new ArrayList<EvaluationType>();
    }
    
    /**
     * Copy constructor.
     * @eventCond The Parameter object you want to make a copy of.
     */
    public AParameter(AParameter param)
    {
        if (param == null) {
            return;
        }
        
        this.uuid = param.getUUID();
        this.type = param.getType();
        this.name = param.getName();
        this.description = param.getDescription();
        this.unit = param.getUnit();
        
        this.valuesAllowedType = param.getValuesAllowedType();
        this.valueConstraints = new ArrayList<ValueConstraint>();
        if (param.getValueConstraints() != null)
        {
            for (ValueConstraint vc : param.getValueConstraints()) {
                this.valueConstraints.add(new ValueConstraint(vc));
            }
        }
        
        this.allowedEvaluationTypes = new ArrayList<EvaluationType>();
        if (param.getAllowedEvaluationTypes() != null)
        {
            for (EvaluationType et : param.getAllowedEvaluationTypes()) {
                this.allowedEvaluationTypes.add(et);
            }
        }
    }
    
    /**
     * Constructor which allows to set the UUID of the Parameter.
     * The value type is set to 'string'
     * The values allowed is set to 'single'
     * @param uuid The UUID of the EventCondition.
     */
    public AParameter(UUID uuid)
    {
        this();
        this.uuid = uuid;
    }
    
    /**
     * Constructor to set the parameter information - be aware that some can be null.
     * The values allowed type is set to 'single'
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     */
    public AParameter(ParameterValueType type, String name, String desc, String unit)
    {
        this();
        this.type = type;
        this.name = name;
        this.description = desc;
        this.unit = unit;
    }
    
    /**
     * Constructor to set the parameter details - be aware that some can be null.
     * The value constraint is set to 'default'
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param valsAllowedType The values allowed type {single, multiple, between}
     */
    public AParameter(ParameterValueType type, String name, String desc, String unit, 
            ValuesAllowedType valsAllowedType)
    {
        this(type, name, desc, unit);
        this.valuesAllowedType = valsAllowedType;
    }
    
    /**
     * Constructor to set the parameter details - be aware that some can be null.
     * The values allowed type is set to 'single'
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param val The parameter value.
     * @param valsAllowedType The values allowed type {single, multiple, between}
     * @param valConstr The value constraint
     *
    public AParameter(ParameterValueType type, String name, String desc, String unit,
            ValuesAllowedType valsAllowedType, ValueConstraint valConstr)
    {
        this(type, name, desc, unit, valsAllowedType);
        if (valConstr != null) {
            this.valueConstraints.add(valConstr); // default constructor will have initialised the array list
        }
    }
    */
    
    /**
     * Constructor to set the parameter details - be aware that some can be null.
     * The values allowed type is set to 'single'
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param val The parameter value.
     * @param valConstr The value constraints
     */
    public AParameter(ParameterValueType type, String name, String desc, String unit,
            List<ValueConstraint> valConstr)
    {
        this(type, name, desc, unit);
        if (valConstr != null) {
            this.valueConstraints.addAll(valConstr); // default constructor will have initialised the array list
        }
    }
    
    /**
     * Constructor to set the parameter details - be aware that some can be null.
     * The values allowed type is set to 'single'
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param val The parameter value.
     * @param valsAllowedType The values allowed type {single, multiple, between}
     * @param valConstr The value constraints
     */
    public AParameter(ParameterValueType type, String name, String desc, String unit,
            ValuesAllowedType valsAllowedType, List<ValueConstraint> valConstr)
    {
        this(type, name, desc, unit, valsAllowedType);
        if (valConstr != null) {
            this.valueConstraints.addAll(valConstr); // default constructor will have initialised the array list
        }
    }
    
    /**
     * Constructor to set the parameter details - be aware that some can be null.
     * The values allowed type is set to 'single'
     * @param uuid The UUID.
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param val The parameter value.
     * @param valsAllowedType The values allowed type {single, multiple, between}
     * @param valConstr The value constraints
     */
    public AParameter(UUID uuid, ParameterValueType type, String name, String desc, String unit,
            List<ValueConstraint> valConstr)
    {
        this(type, name, desc, unit, valConstr);
        this.uuid = uuid;
    }
    
    /**
     * Constructor to set the parameter details - be aware that some can be null.
     * The values allowed type is set to 'single'
     * @param uuid The UUID.
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param val The parameter value.
     * @param valsAllowedType The values allowed type {single, multiple, between}
     * @param valConstr The value constraints
     */
    public AParameter(UUID uuid, ParameterValueType type, String name, String desc, String unit,
            ValuesAllowedType valsAllowedType, List<ValueConstraint> valConstr)
    {
        this(uuid, type, name, desc, unit, valConstr);
        this.valuesAllowedType = valsAllowedType;
    }
    
    /**
     * Get the UUID.
     * @return 
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Set the UUID
     * @param uuid The UUID
     */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
    
    /**
     * @return the type
     */
    public ParameterValueType getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ParameterValueType type)
    {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the unit
     */
    public String getUnit()
    {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    
    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * @return the valuesAllowedType
     */
    public ValuesAllowedType getValuesAllowedType()
    {
        return valuesAllowedType;
    }

    /**
     * @param valuesAllowedType the valuesAllowedType to set
     */
    public void setValuesAllowedType(ValuesAllowedType valuesAllowedType)
    {
        this.valuesAllowedType = valuesAllowedType;
    }
    
    /**
     * @return the valueConstraints
     */
    public List<ValueConstraint> getValueConstraints()
    {
        return valueConstraints;
    }

    /**
     * @param valueConstraints the valueConstraints to set
     */
    public void setValueConstraints(List<ValueConstraint> valueConstraints)
    {
        this.valueConstraints = valueConstraints;
    }
    
    /**
     * Add a value constraint
     * @param valConstr The value constraint
     */
    public void addValueConstraint(ValueConstraint valConstr)
    {
        if (this.valueConstraints == null) {
            this.valueConstraints = new ArrayList<ValueConstraint>();
        }
        this.valueConstraints.add(valConstr);
    }
    
        /**
     * Add a value constraint
     * @param value 
     * @param constraint
     */
    public void addValueConstraint(String value, ValueConstraintType constraint) {
        if (value != null && constraint != null) {
            if (this.valueConstraints == null) {
                this.valueConstraints = new ArrayList<ValueConstraint>();
            }
            this.valueConstraints.add(new ValueConstraint(value, constraint));
        }
    }

    /**
     * Add value constraints
     * @param valConstr The value constraints
     */
    public void addValueConstraints(List<ValueConstraint> valConstr)
    {
        if (this.valueConstraints == null) {
            this.valueConstraints = new ArrayList<ValueConstraint>();
        }
        this.valueConstraints.addAll(valConstr);
    }

    /**
     * Get the allowed evaluation types.
     * If none have been set, an empty list is returned.
     * @return the allowedEvaluationTypes
     */
    public List<EvaluationType> getAllowedEvaluationTypes()
    {
        if (allowedEvaluationTypes == null) {
            this.allowedEvaluationTypes = new ArrayList<EvaluationType>();
        }
        
        return allowedEvaluationTypes;
    }
    
    /**
     * Get the default allowed evaluation types. This is particularly useful 
     * if none have been set, the default list of allowed evaluation types
     * given the parameter type will be returned
     * @return the allowedEvaluationTypes
     */
    public List<EvaluationType> getDefaultAllowedEvaluationTypes()
    {
        if (allowedEvaluationTypes == null) {
            this.allowedEvaluationTypes = new ArrayList<EvaluationType>();
        }
        
        // if empty, return a temporary list with all
        if (allowedEvaluationTypes.isEmpty()) {
            return EvaluationType.getAllowedEvaluationTypes(this.type);
        }
        
        return allowedEvaluationTypes;
    }

    /**
     * Set the allowed evaluation types.
     * @param allowedEvaluationTypes the allowedEvaluationTypes to set
     */
    public void setAllowedEvaluationTypes(List<EvaluationType> allowedEvaluationTypes)
    {
        this.allowedEvaluationTypes = allowedEvaluationTypes;
    }
    
    /**
     * Add an allowed evaluation type
     * @param evalType The evaluation type
     */
    public void addAllowedEvaluationType(EvaluationType evalType)
    {
        if (this.allowedEvaluationTypes == null) {
            this.allowedEvaluationTypes = new ArrayList<EvaluationType>();
        }
        this.allowedEvaluationTypes.add(evalType);
    }

    /**
     * Add allowed evaluation types
     * @param evalTypes The evaluation types
     */
    public void addAllowedEvaluationTypes(List<EvaluationType> evalTypes)
    {
        if (this.allowedEvaluationTypes == null) {
            this.allowedEvaluationTypes = new ArrayList<EvaluationType>();
        }
        this.allowedEvaluationTypes.addAll(evalTypes);
    }
    
    
    
    /**
     * Check if the parameter is state based. 
     * @return true if the value type is a boolean or a string with one or more valuesallowed.
     */
    public boolean isStateBased()
    {
        if (type.equals(ParameterValueType.BOOLEAN))
        {
            return true;
        }
        else if (type.equals(ParameterValueType.STRING))
        {
            if ((valueConstraints == null) || valueConstraints.isEmpty())
            {
                log.warn("Cannot determine the parameter type because no value constraints have been defined");
                return false;
            }
            
            int count = 0;
            for (ValueConstraint valConstr : valueConstraints)
            {
                if (valConstr.getConstraintType().equals(ValueConstraintType.VALUESALLOWED)) {
                    count++;
                }
            }
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Check if the parameter is threshold based.
     * @return true if the parameter value type is numeric (INT or FLOAT).
     */
    public boolean isThresholdBased()
    {
        if (type.equals(ParameterValueType.INT) || type.equals(ParameterValueType.FLOAT)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Check if the parameter defines a discrete set of values.
     * @return true if state based, or if numerically defining a discrete range.
     */
    public boolean isDiscreteSet()
    {
        if (isStateBased()) {
            return true;
        }
        
        if (isDiscreteRange()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if the parameter is defined to be within a numeric discrete range
     * @return
     */
    public boolean isDiscreteRange()
    {
        // check if numeric first
        if (!type.equals(ParameterValueType.INT) && !type.equals(ParameterValueType.FLOAT)) {
            return false;
        }
        
        if ((valueConstraints == null) || valueConstraints.isEmpty()) {
            log.warn("Cannot determine if the parameter defines a discrete range because no value constraints have been defined");
            return false;
        }
        if (valueConstraints.size() == 1) {
            log.warn("Cannot determine if the parameter defines a discrete range because only a single value constraint has been defined");
            return false;
        }
        
        if (type.equals(ParameterValueType.INT))
        {
            if (getMin() == null) {
                return false;
            }

            if (getMax() == null) {
                return false;
            }
        }
        
        if (type.equals(ParameterValueType.FLOAT))
        {
            if (getMin() == null) {
                return false;
            }

            if (getMax() == null) {
                return false;
            }
            
            if (getStep() == null) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if the parameter is defined to be within a continuous range
     * @param type
     * @return
     */
    public boolean isContinuousRange()
    {
        if (!type.equals(ParameterValueType.FLOAT)) {
            return false;
        }
            
        if ((valueConstraints == null) || valueConstraints.isEmpty()) {
            log.warn("Cannot determine the parameter type because no value constraints have been defined");
            return false;
        }
        
        if (valueConstraints.size() == 1) {
            log.warn("Cannot determine the parameter type because only a single value constraint has been defined");
            return false;
        }
        
        if (getMin() == null) {
            return false;
        }

        if (getMax() == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get the set of values for constraints defined as VALUESALLOWED.
     * @return Empty set if there are no VALUESALLOWED constraints defined.
     */
    public Set<String> getValuesAllowedSet()
    {
        Set<String> valueSet = new HashSet<String>();
        
        for (ValueConstraint valConstr : valueConstraints)
        {
            if (valConstr.getConstraintType().equals(ValueConstraintType.VALUESALLOWED)) {
                valueSet.add(valConstr.getValue());
            }
        }
        
        return valueSet;
    }
    
    /**
     * Get the set of discrete values allowed. 
     * If a String type of parameter, then the constraint values defined as 
     * VALUESALLOWED will be returned.
     * If a Numeric type of parameter, a set of values according to min,
     * max and possibly step size will be returned.
     * If BOOLEAN a set of true/false will be returned if no constraints have
     * been set - otherwise VALUESALLOWED will be returned (if 2 - otherwise 
     * nothing).
     * @return Empty set if there are no discrete values.
     */
    public List<String> getDiscreteValues()
    {
        List<String> valueList = new ArrayList<String>();
        
        // Type String - states
        if (type.equals(ParameterValueType.BOOLEAN))
        {
            // default true/false values if no constraints set
            if ((valueConstraints == null) || valueConstraints.isEmpty())
            {
                valueList.add("true");
                valueList.add("false");
            }
            else // return values allowed
            {
                for (ValueConstraint valConstr : valueConstraints)
                {
                    if (valConstr.getConstraintType().equals(ValueConstraintType.VALUESALLOWED)) {
                        valueList.add(valConstr.getValue());
                    }
                }
                
                if (valueList.size() != 2)
                {
                    if (this.name != null) { log.warn("Unable to get the possible binary values for parameter '" + name + "'"); }
                    else { log.warn("Unable to get the possible binary values for a parameter (whose name is NULL)"); }
                }
            }
        }
        else if (type.equals(ParameterValueType.STRING))
        {
            for (ValueConstraint valConstr : valueConstraints)
            {
                if (valConstr.getConstraintType().equals(ValueConstraintType.VALUESALLOWED)) {
                    valueList.add(valConstr.getValue());
                }
            }
        }
        else if ( (type.equals(ParameterValueType.INT) || type.equals(ParameterValueType.FLOAT)) && isDiscreteRange())
        {
            // generate discrete values according to min/max/step range
            BigDecimal min  = null;
            BigDecimal max  = null;
            BigDecimal step = null;

            if (type.equals(ParameterValueType.INT))
            {
                min = new BigDecimal(getMin());
                max = new BigDecimal(getMin());
                
                // if no step is set, then assume 1
                String stepStr = getStep();
                if (stepStr == null) {
                    stepStr = "1";
                }
                step = new BigDecimal(stepStr);
            }
            else // FLOAT
            {
                min = new BigDecimal(getMin());
                max = new BigDecimal(getMin());
                step = new BigDecimal(getStep());
            }
            
            // if min/max/step set, populate value set
            if ((min != null) && (max != null) && (step != null))
            {
                BigDecimal val = min;
                do {
                    valueList.add(String.valueOf(val));
                    val = val.add(step);
                } while (val.compareTo(max) != 1);
            }
            else
            {
                if (this.name != null) { log.warn("Unable to get the discrete values for numerical parameter '" + name + "'"); }
                else { log.warn("Unable to get the discrete values for a numeric parameter (whose name is NULL)"); }
            }
        }
        
        return valueList;
    }
    
    /**
     * Get the minimum value, if the defined.
     * @return null if no minimum value has been defined.
     */
    public String getMin()
    {
        for (ValueConstraint valConstr : valueConstraints)
        {
            if (valConstr.getConstraintType().equals(ValueConstraintType.MIN)) {
                return valConstr.getValue();
            }
        }
        log.warn("There is no minimum value defined");
        return null;
    }
    
    /**
     * Get the maximum value, if defined.
     * @return null if no maximum value has been defined.
     */
    public String getMax()
    {
        for (ValueConstraint valConstr : valueConstraints)
        {
            if (valConstr.getConstraintType().equals(ValueConstraintType.MAX)) {
                return valConstr.getValue();
            }
        }
        log.warn("There is no maximum value defined");
        return null;
    }
    
    /**
     * Get the step size, if it has been defined.
     * @return null if no step size has been defined.
     */
    public String getStep()
    {
        for (ValueConstraint valConstr : valueConstraints)
        {
            if (valConstr.getConstraintType().equals(ValueConstraintType.STEP)) {
                return valConstr.getValue();
            }
        }
        log.warn("There is no step value defined");
        return null;
    }
    
    /**
     * Get the default, if it has been defined.
     * @return null if no default value has been defined.
     */
    public String getDefaultValue()
    {
        for (ValueConstraint valConstr : valueConstraints)
        {
            if (valConstr.getConstraintType().equals(ValueConstraintType.DEFAULT)) {
                return valConstr.getValue();
            }
        }
        log.warn("There is no default value defined");
        return null;
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AParameter other = (AParameter) obj;
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        
        return true;
    }
}
