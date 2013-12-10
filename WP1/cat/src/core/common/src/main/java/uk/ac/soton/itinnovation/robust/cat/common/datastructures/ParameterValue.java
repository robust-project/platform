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
//      Created Date :          2012-01-11
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.datastructures;

import java.io.Serializable;


/**
 * PS: not currently handled the situation where a predictor may have a set of possible evaluation types
 * @author Vegard Engen
 */
public class ParameterValue implements Serializable
{
    private String value; // should be validated against the valueConstraints, if any
    private EvaluationType valueEvaluationType; // < > == != etc

    /**
     * Default constructor - does nothing
     */
    public ParameterValue(){}
    
    /**
     * Copy constructor
     * @param paramVal 
     */
    public ParameterValue(ParameterValue paramVal)
    {
        if (paramVal == null) {
            return;
        }
        
        this.value = paramVal.getValue();
        this.valueEvaluationType = paramVal.getValueEvaluationType();
    }
    
    /**
     * Constructor to set the basic information of a parameter value.
     * @param value
     */
    public ParameterValue(String value)
    {
        this.value = value;
        
    }
    
    /**
     * Constructor to set the basic information of a parameter value.
     * @param value
     * @param evalType
     */
    public ParameterValue(String value, EvaluationType evalType)
    {
        this.value = value;
        this.valueEvaluationType = ((evalType==null)?EvaluationType.EQUAL:evalType);
    }
    
    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * @return the valueEvaluationType
     */
    public EvaluationType getValueEvaluationType()
    {
        return valueEvaluationType;
    }

    /**
     * @param valueEvaluationType the valueEvaluationType to set
     */
    public void setValueEvaluationType(EvaluationType valueEvaluationType)
    {
        this.valueEvaluationType = valueEvaluationType;
    }
    
    /**
     * Check if the parameter value is set.
     * @return True if set; false otherwise.
     */
    public boolean isSet()
    {
        if (this.value == null)
            return false;
        if (this.value.isEmpty())
            return false;
        return true;
    }
    
    @Override
    public String toString()
    {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParameterValue other = (ParameterValue) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
