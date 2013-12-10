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
//      Created Date :          2011-06-24
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Vegard Engen
 */
public enum FrequencyType implements Serializable
{
    HOURLY("hourly",0),
    DAILY("daily",1),
    WEEKLY("weekly",2),
    BIWEEKLY("biweekly",3),
    MONTHLY("monthly",4),
    BIMONTHLY("bimonthly",5),
    QUARTERLY("quarterly",6),
    SEMIANNUALLY("semiannually",7),
    ANNUALLY("annually",8);
    
    private String name;
    private int code;
    
    private FrequencyType(String type, int code)
    {
        this.name = type;
        this.code = code;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the code
     */
    public int getCode()
    {
        return code;
    }
    
    @Override
    public String toString ()
    {
        return name;
    }

    public static List<String> getAllowedTypes()
    {
        List<String> allowed = new ArrayList<String> ();
        allowed.add(HOURLY.name);
        allowed.add(DAILY.name);
        allowed.add(WEEKLY.name);
        allowed.add(BIWEEKLY.name);
        allowed.add(MONTHLY.name);
        allowed.add(BIMONTHLY.name);
        allowed.add(QUARTERLY.name);
        allowed.add(SEMIANNUALLY.name);
        allowed.add(ANNUALLY.name);
        return allowed;
    }
    
    public static boolean isAllowed (String type)
    {
        for (FrequencyType inst : values())
        {
            if (inst.name.equals(type))
                return true;
        }
        return false;
    }
    
    public static FrequencyType fromValue(String v) {
        return valueOf(v.toUpperCase());
    }
}
