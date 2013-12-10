/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2011
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
//      Created By :            Bassem Nasser
//      Created Date :          03-Nov-2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.riskmodel;

import java.util.ArrayList;
import java.util.List;

public enum Period {

    HOUR("hour"),DAY("day"), WEEK("week"), MONTH("month"),YEAR("year");
    String period;

    private Period() {
    }
 

    private Period(String period) {
        this.period = period;
      
    }

    public String getPeriod() {
        return period;
    }

  

    @Override
    public String toString() {
        return period;
    }

    public static List<String> getAllowedTypes() {
        List<String> allowed = new ArrayList<String>();
        allowed.add(HOUR.period);
        allowed.add(DAY.period);
        allowed.add(WEEK.period);
        allowed.add(MONTH.period);
        
        allowed.add(YEAR.period);
        return allowed;
    }

    public static boolean isAllowed(String type) {
        for (Period inst : values()) {
            if (inst.period.equals(type)) {
                return true;
            }
        }
        return false;
    }
    
      public static Period fromString(String period) {
        if (period != null) {
            for (Period b : Period.values()) {
                if (period.equalsIgnoreCase(b.getPeriod())) {
                    return b;
                }
            }

            throw new IllegalArgumentException("No enum const " + Period.class + " for " + period);

        }

        throw new RuntimeException("input parameter is null");

    }
}
