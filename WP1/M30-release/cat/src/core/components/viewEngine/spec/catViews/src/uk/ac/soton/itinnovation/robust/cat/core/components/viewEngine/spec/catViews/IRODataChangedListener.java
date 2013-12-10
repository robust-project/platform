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
//      Created Date :          21 Mar 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews;

import java.util.UUID;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 *
 * @author kem
 */
public interface IRODataChangedListener {

    public void onCommunityAdded(UUID communityID);
    
    public void onCommunityUpdated(UUID communityID);
    
    public void onCommunityDeleted(UUID communityID);
    
    public void onObjectiveAdded(UUID communityID, UUID objectiveID);
    
    public void onObjectiveUpdated(UUID communityID, UUID objectiveID);
    
    public void onObjectiveDeleted(UUID communityID, UUID objectiveID);

    public void onRiskAdded(Risk risk);
    
    public void onRiskUpdated(Risk risk);
    
    public void onRiskDeleted(Risk risk);
    
}
