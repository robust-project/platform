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

import java.util.List;
import java.util.UUID;

/**
 * @author Vegard Engen
 */
public class Parameter extends AParameter
{
    private ParameterValue value;

    /**
     * Default constructor.
     * The value type is set to 'string'
     * The values allowed is set to 'single'
     */
    public Parameter()
    {
        super();
    }
    
    
    /**
     * Copy constructor.
     * @eventCond The Parameter object you want to make a copy of.
     */
    public Parameter(AParameter param)
    {
        super(param);
        
        if (param instanceof Parameter)
        {
            this.value = ((Parameter)param).getValue();
        }
    }
    
    /**
     * Constructor which allows to set the UUID of the Parameter.
     * The value type is set to 'string'
     * The values allowed is set to 'single'
     * @param uuid The UUID of the EventCondition.
     */
    public Parameter(UUID uuid)
    {
        super(uuid);
    }
    
    public Parameter(ParameterValue paramVal)
    {
        super();
        this.value = paramVal;
//        if(paramVal.getValueEvaluationType()==null){
//            paramVal.setValueEvaluationType(EvaluationType.EQUAL);
//        }
    }
    
    /**
     * Constructor to set the parameter information - be aware that some can be null.
     * The values allowed type is set to 'single'
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     */
    public Parameter(ParameterValueType type, String name, String desc, String unit)
    {
        super();
        this.type = type;
        this.name = name;
        this.description = desc;
        this.unit = unit;
    }
    
    /**
     * Constructor to set the parameter details - be aware that some can be null.
     * The values allowed type is set to 'single'
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param val The parameter value.
     */
    public Parameter(ParameterValueType type, String name, String desc, String unit, ParameterValue val)
    {
        this(type, name, desc, unit);
        this.value = val;
    }
    
    /**
     * Constructor to set the parameter details - be aware that some can be null.
     * The value constraint is set to 'default'
     * @param type The parameter type (bool, float, int, string)
     * @param name The name of the parameter.
     * @param desc A description of the parameter.
     * @param unit The unit of the parameter value, which can be null.
     * @param val The parameter value.
     * @param valsAllowedType The values allowed type {single, multiple, between}
     */
    public Parameter(ParameterValueType type, String name, String desc, String unit, ParameterValue val,
            ValuesAllowedType valsAllowedType)
    {
        this(type, name, desc, unit, val);
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
     */
    public Parameter(ParameterValueType type, String name, String desc, String unit, ParameterValue val,
            ValuesAllowedType valsAllowedType, ValueConstraint valConstr)
    {
        this(type, name, desc, unit, val, valsAllowedType);
        if (valConstr != null) {
            this.valueConstraints.add(valConstr); // default constructor will have initialised the array list
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
    public Parameter(ParameterValueType type, String name, String desc, String unit, ParameterValue val,
            ValuesAllowedType valsAllowedType, List<ValueConstraint> valConstr)
    {
        this(type, name, desc, unit, val, valsAllowedType);
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
    public Parameter(UUID uuid, ParameterValueType type, String name, String desc, String unit, ParameterValue val,
            ValuesAllowedType valsAllowedType, List<ValueConstraint> valConstr)
    {
        this(type, name, desc, unit, val, valsAllowedType);
        if (valConstr != null) {
            this.valueConstraints.addAll(valConstr); // default constructor will have initialised the array list
        }
        this.uuid = uuid;
    }
    
    /**
     * @param val the values to set
     */
    public void setValue(ParameterValue val)
    {
        this.value = val;
    }

    /**
     * @return the values
     */
    public ParameterValue getValue()
    {
        return value;
    }
    
    /**
     * Check if the parameter's value is set.
     * @return True if set; false otherwise.
     */
    public boolean isValueSet()
    {
        if (this.value == null)
            return false;
        
        return this.value.isSet();
    }
    
    /**
     * Checks that the value is valid according to the constraints set.
     * @return
     * @throws Exception 
     *
    public boolean isValueValid() throws Exception
    {
        throw new UnsupportedOperationException("Validation method not supported yet");
    }*/
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
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
        final Parameter other = (Parameter) obj;
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }
}
