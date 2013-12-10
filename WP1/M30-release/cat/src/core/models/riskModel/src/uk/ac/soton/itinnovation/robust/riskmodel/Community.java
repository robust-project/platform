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



import com.sun.org.apache.xpath.internal.operations.Equals;
import java.net.URI;
import java.util.UUID;

public class Community {
    
//    private Set<Objective> objectives;
//    private Set<Risk> risks;
//    private Impact impact;
//    private Event event;
    private String name;
    private UUID uuid;
    
    private String communityID;
    private String streamName;//stream name
    private URI uri; //stream uri
    private Boolean isStream; //whethr the data source is stream or not



    public Community() {
    }


    public Community(String name, UUID uuid, String communityID, String streamName, URI uri, Boolean isStream) {
        this.name = name;
        this.uuid = uuid;
        this.communityID = communityID;
        this.streamName = streamName;
        this.uri = uri;
        this.isStream = isStream;
    }
    
    public Community(String name, URI uri, Boolean isStreaming,String communityID, String streamName ) {
        this.name = name;
        uuid=UUID.randomUUID();
        this.uri = uri;
        this.isStream = isStreaming;
        
        this.communityID=communityID;
        this.streamName=streamName;
    }
        

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    
    public Boolean getIsStream() {
        return isStream;
    }

    public void setIsStream(Boolean isStream) {
        this.isStream = isStream;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
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
    
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 29 * hash + (this.uri != null ? this.uri.hashCode() : 0);
        hash = 29 * hash + (this.isStream != null ? this.isStream.hashCode() : 0);
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
        if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri))) {
            return false;
        }
        if (this.isStream != other.isStream && (this.isStream == null || !this.isStream.equals(other.isStream))) {
            return false;
        }
        return true;
    }
      
}

