/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
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
//      Created Date :          02-Jul-2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.riskmodel;

/**
 *
 * @author Bassem Nasser
 */
public enum DBType {

    
       POSTGRES("POSTGRES"), SQL("SQL");
     
    String type;


    private DBType() {
    }
 
    
     private DBType(String type) {
        this.type = type;
    }

    

    public String getType() {
        return type;
    }
    


    @Override
    public String toString() {
        return type;
    }

   
    
    public static DataLocationType fromString(String type) {
        if (type != null) {
            for (DataLocationType b : DataLocationType.values()) {
                if (type.equalsIgnoreCase(b.getType())) {
                    return b;
                }
            }

            throw new IllegalArgumentException("No enum const " + DataLocationType.class + " for " + type);
        }

        throw new RuntimeException("input parameter is null");
    }
    
    public static DataLocationType fromValue(String type){
        return fromString(type);
        
    }
    
    public String value() {
        return name();
    }
    
 
    
}
