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
//      Created Date :          2012-08-08
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.ps;

/**
 *
 * @author Vegard Engen
 */
public class CommunityDetails
{
    private String communityName;
    private String communityID;

    public CommunityDetails() {}
    
    public CommunityDetails(String communityName, String communityID)
    {
        this.communityName = communityName;
        this.communityID = communityID;
    }
    
    /**
     * @return the communityName
     */
    public String getCommunityName()
    {
        return communityName;
    }

    /**
     * @param communityName the communityName to set
     */
    public void setCommunityName(String communityName)
    {
        this.communityName = communityName;
    }

    /**
     * @return the communityID
     */
    public String getCommunityID()
    {
        return communityID;
    }

    /**
     * @param communityID the communityURI to set
     */
    public void setCommunityID(String communityID)
    {
        this.communityID = communityID;
    }
}
