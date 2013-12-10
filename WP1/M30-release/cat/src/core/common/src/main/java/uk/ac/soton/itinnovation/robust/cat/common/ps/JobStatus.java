/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
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
//      Created By :            Vegard Engen
//      Created Date :          2012-01-11
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.ps;

import java.io.Serializable;

/**
 *
 * @author Vegard Engen
 */
public class JobStatus implements Serializable
{
    private JobStatusType status;
    private String metaData;
    
    public JobStatus() {}
    
    public JobStatus(JobStatusType status)
    {
        this.status = status;
        this.metaData = "";
    }
    
    public JobStatus(JobStatusType status, String metaData)
    {
        this(status);
        this.metaData = metaData;
    }
    
    public JobStatus(JobStatus js)
    {
        if (js == null)
            return;
        
        this.status = js.getStatus();
        this.metaData = js.getMetaData();
    }

    /**
     * @return the status
     */
    public JobStatusType getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(JobStatusType status) {
        this.status = status;
    }

    /**
     * @return the metaData
     */
    public String getMetaData() {
        return metaData;
    }

    /**
     * @param metaData the metaData to set
     */
    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}
