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
//      Created By :            Simon Crowle
//      Created Date :          20 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.welcome;

import java.util.Set;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.DataSource;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;

public interface IWelcomeListener
{
  void onDataSourceSelected(UUID dataSourceID);
  
  void onAddDataSource(DataSource dataSource);
  
  void onRemoveDataSource(UUID dataSourceID);
  
  void onMonitorCommunity( UUID communityID );
  
  void onRefreshCommunityList();
  
  void onAddCommunity( Community community );
  
  void onRemoveCommunity( UUID communityID );

  void onAddCommunities(Set<Community> addedCommunities);

  void onRemoveCommunities(Set<Community> removedCommunities);

  void onEditCommunityObjectives( UUID communityID );
  
  void onAddCommunityObjective(UUID communityID, Objective objective);

  void onRemoveCommunityObjective(UUID communityID, Objective objective);
  
  void onResetDatabaseClicked(String deleteDatabaseOption, String authToken);

}