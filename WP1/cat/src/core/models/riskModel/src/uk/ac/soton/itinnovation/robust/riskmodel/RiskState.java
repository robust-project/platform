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

public enum RiskState {
    
    
    INACTIVE("inactive"),ACTIVE("active"),FLAGGED("flagged"),TREATMENT("treatment");
    String state;

    private RiskState() {
    }
 

    private RiskState(String state) {
        this.state = state;
      
    }

    public String getName() {
        return state;
    }



    @Override
    public String toString() {
        return state;
    }

    public static List<String> getAllowedTypes() {
        List<String> allowed = new ArrayList<String>();
        allowed.add(INACTIVE.state);
        allowed.add(ACTIVE.state);
        allowed.add(FLAGGED.state);
        allowed.add(TREATMENT.state);
       
        return allowed;
    }

    public static boolean isAllowed(String type) {
        for (RiskState inst : values()) {
            if (inst.state.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static RiskState fromString(String state) {
        if (state != null) {
            for (RiskState b : RiskState.values()) {
                if (state.equalsIgnoreCase(b.getName())) {
                    return b;
                }
            }

            throw new IllegalArgumentException("No enum const " + RiskState.class + " for " + state);

        }

        throw new RuntimeException("input parameter is null");

    }
}
