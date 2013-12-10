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
 *
 * @author Vegard Engen
 */
public class ValueConstraint implements Serializable
{
    private String value;
    private ValueConstraintType constraintType;
    
    /**
     * Default, empty constructor. Does nothing.
     */
    public ValueConstraint(){}
    
    /**
     * Copy constructor.
     * @param vc 
     */
    public ValueConstraint(ValueConstraint vc)
    {
        this.value = vc.getValue();
        this.constraintType = vc.getConstraintType();
    }
    
    /**
     * Constructor to set value and constraint type.
     * @param value
     * @param constraint 
     */
    public ValueConstraint(String value, ValueConstraintType constraint)
    {
        this.value = value;
        this.constraintType = constraint;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the constraintType
     */
    public ValueConstraintType getConstraintType() {
        return constraintType;
    }

    /**
     * @param constraintType the constraintType to set
     */
    public void setConstraintType(ValueConstraintType constraintType) {
        this.constraintType = constraintType;
    }
}
