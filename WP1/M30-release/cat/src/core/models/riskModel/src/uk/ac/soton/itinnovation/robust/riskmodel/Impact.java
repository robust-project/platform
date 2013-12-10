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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Impact {
    
    private Map<Objective, ImpactLevel> impactMap;

    public Impact() {
        impactMap=new HashMap<Objective, ImpactLevel>();
    }

    
    public Impact(Map<Objective, ImpactLevel> impactMap) {
        this.impactMap = impactMap;
    }

    public Map<Objective, ImpactLevel> getImpactMap() {
        return impactMap;
    }

    public void setImpactMap(Map<Objective, ImpactLevel> impactMap) {
        this.impactMap = impactMap;
    }
    
    public void addImpactObj(Objective obj, ImpactLevel level){
        if(impactMap==null){
            impactMap=new HashMap<Objective, ImpactLevel>();
        }
        
        impactMap.put(obj, level);
    }
    
    public void removeImpactObj(Objective obj, ImpactLevel level){
        if(impactMap!=null){
            impactMap.remove(obj);
        }
    }
    


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Impact other = (Impact) obj;
        if (this.impactMap != other.impactMap && (this.impactMap == null || !this.hasEqualMaps(other.impactMap))) {
            return false;
        }
        return true;
    }
    
    private boolean hasEqualMaps(Map<Objective, ImpactLevel> otherMap) {
        if (otherMap.size() == this.impactMap.size()) {
            for (Entry<Objective, ImpactLevel> entry : otherMap.entrySet()) {
                if (!this.containsEntry(entry)) {
                    return false;
                }
            }

            return true;
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.impactMap != null ? this.impactMap.hashCode() : 0);
        return hash;
    }

    /**
     * tests of a map containsObjective a specific objective impact
     */
    public boolean containsObjective(Entry<Objective, ImpactLevel> entry) {
        if(impactMap!=null && impactMap.get(entry.getKey())!=null)
            return true;
        
        return false;
    }

    private boolean containsEntry(Entry<Objective, ImpactLevel> entry) {
          Objective obj=entry.getKey();
        if(impactMap!=null && /*impactMap.containsKey(obj)*/mapContainsObjective(obj) ){
            String name=entry.getValue().name();
            String othername=impactMap.get(obj).name();
            if(/*(impactMap.get(obj).isIsPositive()== entry.getValue().isIsPositive()) &&*/ name.equals(othername)){
                    return true;
            }
        }
            
        
        return false;
    }
      
    private boolean mapContainsObjective(Objective obj){
        boolean found=false;
        
        for(Objective tmp:impactMap.keySet()){
            if(tmp.getTitle().equals(obj.getTitle()) && tmp.getId().equals(obj.getId()))
                found=true;
        }
        
        return found;
    }
    

    
}
