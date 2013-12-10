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
//      Created Date :          15 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.welcome;

import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import java.util.Set;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.riskmodel.DataSource;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;




public interface IWelcomeController
{
    public void setDataSources( Set<DataSource> dataSources );
    
    public void onDataSourceAdded(DataSource dataSource);
    
    public void onDataSourceDeleted(UUID dataSourceID);
    
    public void setCommunitiesForCurrentDataSource(Set<Community> dataSourceCommunities, Set<Community> currentCommunities);
    
  /**
   * Supply welcome controller with currently available communities
   * 
   * @param communities - list of communities from which the user will select
   */
    public void setCommunityInfo( Set<Community> communities );

    public void setCurrentPlatform(String platform);

    public void setCurrentCommunity(Community community);

    public Community getCurrentCommunity();

    public void setCurrentObjectives(Set<Objective> objectives);
    
    public void onCommunityDataUpdated();

    public void startProgressIndicator();
}