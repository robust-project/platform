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
//      Created Date :          2012-08-14
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec;

/**
 *
 * @author Vegard Engen
 */
public class Status
{
    private boolean successful;
    private String metaData;

    /**
     * Default constructor, which does nothing.
     */
    public Status(){}
    
    /**
     * Constructor to set whether the operation was successful or not.
     * @param successful
     */
    public Status(boolean successful)
    {
        this.successful = successful;
    }
    
    /**
     * Constructor to set whether the operation was successful or not, along with
     * any descriptive meta data. The meta data can be useful if the operation
     * was not successful.
     * @param successful
     * @param metaData 
     */
    public Status(boolean successful, String metaData)
    {
        this(successful);
        this.metaData = metaData;
    }
    
    /**
     * @return the successful
     */
    public boolean isSuccessful()
    {
        return successful;
    }

    /**
     * @param successful the successful to set
     */
    public void setSuccessful(boolean successful)
    {
        this.successful = successful;
    }

    /**
     * @return the metaData
     */
    public String getMetaData()
    {
        return metaData;
    }

    /**
     * @param metaData the metaData to set
     */
    public void setMetaData(String metaData)
    {
        this.metaData = metaData;
    }
}
