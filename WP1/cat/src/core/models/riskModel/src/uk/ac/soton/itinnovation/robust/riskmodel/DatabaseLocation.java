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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DatabaseLocation")
public class DatabaseLocation extends DataLocation {

//    private UUID uuid;
    private String port;
    private String hostname;
    private DBType dbtype;
    private String dbname;

    public DatabaseLocation() {
        type = DataLocationType.DB;
    }

//    public DatabaseLocation(UUID uuid, String port, String hostname, DBType dbtype) {
//        type = DataLocationType.DB;
////        this.uuid = uuid;
//        this.port = port;
//        this.hostname = hostname;
//        this.dbtype = dbtype;
//    }

    public DatabaseLocation(String port, String hostname, DBType dbtype, String dbname) {
        type = DataLocationType.DB;
//        this.uuid = UUID.randomUUID();
        this.port = port;
        this.hostname = hostname;
        this.dbtype = dbtype;
        this.dbname=dbname;
    }

//    public UUID getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(UUID uuid) {
//        this.uuid = uuid;
//    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public DBType getDbtype() {
        return dbtype;
    }

    public void setDbtype(DBType dbtype) {
        this.dbtype = dbtype;
    }



    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.port != null ? this.port.hashCode() : 0);
        hash = 59 * hash + (this.hostname != null ? this.hostname.hashCode() : 0);
        hash = 59 * hash + (this.dbtype != null ? this.dbtype.hashCode() : 0);
        hash = 59 * hash + (this.dbname != null ? this.dbname.hashCode() : 0);
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
        final DatabaseLocation other = (DatabaseLocation) obj;
        if ((this.port == null) ? (other.port != null) : !this.port.equals(other.port)) {
            return false;
        }
        if ((this.hostname == null) ? (other.hostname != null) : !this.hostname.equals(other.hostname)) {
            return false;
        }
        if (this.dbtype != other.dbtype) {
            return false;
        }
        if ((this.dbname == null) ? (other.dbname != null) : !this.dbname.equals(other.dbname)) {
            return false;
        }
        return true;
    }
}
