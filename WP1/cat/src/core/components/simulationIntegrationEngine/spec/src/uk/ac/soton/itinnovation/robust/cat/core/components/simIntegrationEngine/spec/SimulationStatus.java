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
//      Created By :            Ken Meachem
//      Created Date :          04-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec;

import java.util.Date;

/**
 *
 * @author kem
 */
public class SimulationStatus {
    
    private String id;
    private String status;
    private Date startDate;
    private Date endDate;

    public SimulationStatus(String id, String status, Date startdate, Date endDate) {
        this.id = id;
        this.status = status;
        this.startDate = startdate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }
    
    public String getStartDateString() {
        if (startDate != null)
            return startDate.toString();
        else
            return "";
    }

    public void setStartDate(Date startdate) {
        this.startDate = startdate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateString() {
        if (endDate != null)
            return endDate.toString();
        else
            return "";
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    
}
