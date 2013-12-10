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

import java.net.URI;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "community", propOrder = {
    "communityID",
    "dataLocation",
    "name",
    "uuid",
    "platform"
})
public class Community {

    private String name;
    private UUID uuid;
    private String communityID;
    private DataLocation dataLocation;
    private String platform;

    public Community() {
    }
    
    
    public Community(String name, URI uri, String communityID, String streamName) {
        this.name = name;
        uuid = UUID.randomUUID();
        this.communityID = communityID;
        dataLocation = new StreamLocation(uri, name);
    }
    

    public Community(String name, UUID uuid, String communityID, DataLocation dataLocation, String platform) {
        this.name = name;
        this.uuid = uuid;
        this.communityID = communityID;
        this.dataLocation = dataLocation;
        this.platform = platform;
    }

    public Community(String name, UUID uuid, String communityID, DataLocation dataLocation) {
        this.name = name;
        this.uuid = uuid;
        this.communityID = communityID;
        this.dataLocation = dataLocation;
    }

    public Community(String name, String communityID, DataLocation dataLocation, String platform) {
        uuid = UUID.randomUUID();
        this.name = name;
        this.communityID = communityID;
        this.dataLocation = dataLocation;
        this.platform = platform;
    }

    public boolean getIsStream(){
        if(dataLocation instanceof StreamLocation)
            return true;
        
        return false;
    }
    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public DataLocation getDataLocation() {
        return dataLocation;
    }

    public void setDataLocation(DataLocation dataLocation) {
        this.dataLocation = dataLocation;
    }

//    public Community(String name, UUID uuid) {
//        this.name = name;
//        this.uuid = uuid;
//    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUuid(String strUuid) {
        this.uuid = UUID.fromString(strUuid);
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 29 * hash + (this.dataLocation != null ? this.dataLocation.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Community other = (Community) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
            return false;
        }
        if (this.dataLocation != other.dataLocation && (this.dataLocation == null || !this.dataLocation.equals(other.dataLocation))) {
            return false;
        }

        if ((this.platform == null) ? (other.platform != null) : !this.platform.equals(other.platform)) {
            return false;
        }

        return true;
    }
}
