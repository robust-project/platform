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
//      Created By :            bmn
//      Created Date :          03-Nov-2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.riskmodel;

import java.util.ArrayList;
import java.util.List;

public enum ImpactLevel {
//TODO the ispositive boolean isnot working properly, need to be sorted out. oportunity IBM is saved as risk
  //  VERY_LOW("very_low"), LOW("low"), MEDUIM("meduim"),HIGH("high"),VERY_HIGH("very_high");
    NEG_VHIGH("NEG_VHIGH"), NEG_HIGH("NEG_HIGH"), NEG_MEDIUM("NEG_MEDIUM"),NEG_LOW("NEG_LOW"),NEG_VLOW("NEG_VLOW"), 
    POS_VLOW("POS_VLOW"), POS_LOW("POS_LOW"), POS_MEDIUM("POS_MEDIUM"),POS_HIGH("POS_HIGH"), POS_VHIGH("POS_VHIGH");
     
    String level;
    boolean isPositive=false;//true for oppo and false for risk

    private ImpactLevel() {
    }
 
    
     private ImpactLevel(String level) {
        this.level = level;
    }

     
    public int getOrdinal()
    {
      int ord = -1;
      
      if ( this == ImpactLevel.NEG_VHIGH )       ord = 1;
      else if ( this == ImpactLevel.NEG_VHIGH )  ord = 2;
      else if ( this == ImpactLevel.NEG_HIGH )   ord = 3;
      else if ( this == ImpactLevel.NEG_MEDIUM ) ord = 4;
      else if ( this == ImpactLevel.NEG_LOW )    ord = 5;
      else if ( this == ImpactLevel.NEG_VLOW )   ord = 6;
      else if ( this == ImpactLevel.POS_VLOW )   ord = 7;
      else if ( this == ImpactLevel.POS_LOW )    ord = 8;
      else if ( this == ImpactLevel.POS_MEDIUM ) ord = 9;
      else if ( this == ImpactLevel.POS_HIGH )   ord = 10;
      else if ( this == ImpactLevel.POS_VHIGH )  ord = 11;
      
      return ord;
    }
   

    public String getName() {
        return level;
    }
    

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }



    @Override
    public String toString() {
        return level;
    }

    public static List<String> getAllowedTypes() {
        List<String> allowed = new ArrayList<String>();
        allowed.add(NEG_VHIGH.level);
        allowed.add(NEG_HIGH.level);
        allowed.add(NEG_MEDIUM.level);
        allowed.add(NEG_LOW.level);
        allowed.add(NEG_VLOW.level);
        allowed.add(POS_VLOW.level);
        allowed.add(POS_LOW.level);
        allowed.add(POS_MEDIUM.level);
        allowed.add(POS_HIGH.level);
        allowed.add(POS_VHIGH.level);
        
        return allowed;
    }

    public static boolean isAllowed(String type) {
        for (ImpactLevel inst : values()) {
            if (inst.level.equals(type)) {
                return true;
            }
        }
        return false;
    }
    
    public static ImpactLevel fromString(String impact) {
        if (impact != null) {
            for (ImpactLevel b : ImpactLevel.values()) {
                if (impact.equalsIgnoreCase(b.getName())) {
                    return b;
                }
            }

            throw new IllegalArgumentException("No enum const " + ImpactLevel.class + " for " + impact);
        }

        throw new RuntimeException("input parameter is null");
    }
    
    public static ImpactLevel fromValue(String impact){
        return fromString(impact);
        
    }
    
    public String value() {
        return name();
    }
    
    public boolean isLessThan( ImpactLevel rhs )
    { return ( this.getOrdinal() < rhs.getOrdinal() ); }
}
