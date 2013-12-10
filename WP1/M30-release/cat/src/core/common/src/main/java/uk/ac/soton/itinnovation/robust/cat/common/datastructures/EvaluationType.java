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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vegard Engen
 */
public enum EvaluationType
{
    EQUAL, 
    LESS, 
    LESS_OR_EQUAL, 
    GREATER, 
    GREATER_OR_EQUAL,
    IS, 
    NOT;
    //BETWEEN,
    //IN,
    //NOT_IN;

    public String value() {
        return name();
    }

    public static EvaluationType fromValue(String v) {
        return valueOf(v.toUpperCase());
    }
    
    public static List<EvaluationType> getAllowedEvaluationTypes (ParameterValueType paramValueType)
    {
        List<EvaluationType> allowedList = new ArrayList<EvaluationType>();
        
        if (paramValueType.equals(ParameterValueType.BOOLEAN))
        {
            allowedList.add(EvaluationType.IS);
            allowedList.add(EvaluationType.NOT);
        }
        else if (paramValueType.equals(ParameterValueType.STRING))
        {
            allowedList.add(EvaluationType.IS);
            allowedList.add(EvaluationType.NOT);
        }
        else // INT or FLOAT
        {
            allowedList.add(EvaluationType.EQUAL);
            allowedList.add(EvaluationType.LESS);
            allowedList.add(EvaluationType.LESS_OR_EQUAL);
            allowedList.add(EvaluationType.GREATER);
            allowedList.add(EvaluationType.GREATER_OR_EQUAL);
        }
        
        return allowedList;
    }
}
