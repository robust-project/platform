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
//      Created By :            Ken Meacham
//      Created Date :          19 Jun 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.riskmodel;

import java.net.URI;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "DataSource")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dataSource", propOrder = {
    "endpoint",
    "name",
    "uuid"
})
public class DataSource extends DataLocation{
    private String name;
    private UUID uuid;
    private URI endpoint;
    
    public DataSource() {
        type=DataLocationType.SIOCSVC;
    }

    public DataSource(String name, UUID uuid, URI endpoint) {
        type=DataLocationType.SIOCSVC;
        this.name = name;
        this.uuid = uuid;
        this.endpoint = endpoint;
    }

    public DataSource(String name, URI endpoint) {
        type=DataLocationType.SIOCSVC;
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.endpoint = endpoint;
    }

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

    public URI getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 97 * hash + (this.endpoint != null ? this.endpoint.hashCode() : 0);
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
        final DataSource other = (DataSource) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
            return false;
        }
        if (this.endpoint != other.endpoint && (this.endpoint == null || !this.endpoint.equals(other.endpoint))) {
            return false;
        }
        return true;
    }


}
