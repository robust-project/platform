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

import java.util.UUID;

public class Objective {
    private String title;
    private String description;
    private UUID id;
    
    public Objective(){
        
    }
    
    public Objective(String title, String description) {
        this.title = title;
        this.description = description;
        id = UUID.randomUUID();
    }

    public Objective(String title, String description, UUID id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }
    
    public UUID getIdAsUUID() {
        return id;
    }
    
    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        this.id = UUID.fromString( id );
    }
    
    public void setId(UUID id) {
      this.id = id;
    }
    
    public String getTitle() {
      return title;
    }
    
    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Objective other = (Objective) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
//        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
//            return false;
//        }
//        System.out.println("returning true");
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.title != null ? this.title.hashCode() : 0);
//        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
