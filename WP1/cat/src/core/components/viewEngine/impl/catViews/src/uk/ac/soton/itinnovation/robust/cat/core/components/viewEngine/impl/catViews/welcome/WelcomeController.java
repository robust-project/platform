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

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.welcome.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.ICATAppViewNavListener.eNavDest;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.common.CATAppViewController;

import uk.ac.soton.itinnovation.robust.riskmodel.Community;

import java.io.Serializable;
import java.util.*;
import uk.ac.soton.itinnovation.robust.riskmodel.DataSource;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;

public class WelcomeController extends    UFAbstractEventManager
                               implements Serializable,
                                          IUFController,
                                          IWelcomeController,
                                          WelcomeViewListener
{
  private CATAppViewController    appViewCtrl;
  private WelcomeView             welcomeView;
  
  private transient Set<DataSource> currentDataSources;
  private transient HashMap<UUID,Community> currentCommunities;
  private transient String currentPlatform;
  private transient Community currentCommunity;
  private transient Set<Objective> currentObjectives;
    
  public WelcomeController( CATAppViewController avc )
  {
    appViewCtrl = avc;
    
    currentCommunities = new HashMap<UUID,Community>();
    currentDataSources = new LinkedHashSet<DataSource>();
    
    welcomeView = new WelcomeView( this );
    welcomeView.addListener( this );
  }
  
  public boolean isPublicDemo() {
      return appViewCtrl.isPublicDemo();
  }
  
  // IUFController -------------------------------------------------------------
  @Override
  public IUFModelRO getModel( UUID id ) throws UFException
  {
    // TO DO - need community model set-up data
    return null;
  }
  
  @Override
  public IUFView getView( IUFModelRO model ) throws UFException
  {
    // Only one view to get - the single welcome view
    return welcomeView;
  }
  
  @Override
  public IUFView getView( String ID )
  {
    // Only one view to get - the single welcome view
    return welcomeView;
  }
  
  // IWelcomeController --------------------------------------------------------
  @Override
  public void setDataSources( Set<DataSource> dataSources ) {
      if (dataSources != null) {
          /*
          currentDataSources.clear();

          Iterator<DataSource> dsIt = dataSources.iterator();
          while (dsIt.hasNext()) {
              DataSource ds = dsIt.next();
              currentDataSources.put(ds.getUuid(), ds);
          }
          */
          this.currentDataSources = dataSources;

          //welcomeView.setDataSourceNames(dataSources);
      }
      else {
          this.currentDataSources = new LinkedHashSet<DataSource>();
      }
  }
  
  public Set<DataSource> getCurrentDataSources() {
      return currentDataSources;
  }

  @Override
  public void setCommunitiesForCurrentDataSource(Set<Community> dataSourceCommunities, Set<Community> currentCommunities) {
      this.welcomeView.getDataSourcesView().setCommunitiesForCurrentDataSource(dataSourceCommunities, currentCommunities);
  }

  @Override
  public void setCommunityInfo( Set<Community> communities )
  {
    if ( communities != null )
    {
      currentCommunities.clear();
      
      Iterator<Community> comIt = communities.iterator();
      while ( comIt.hasNext() )
      {
        Community community = comIt.next();
        
        currentCommunities.put( community.getUuid(), community );
        
        System.out.println("Added community " + community.getUuid() + ", " + community.getCommunityID() + ", " + community.getPlatform());
      }
      
      // Update view with basic info
      welcomeView.setCommunityNames( communities );
    }
  }
  
  // WelcomeViewListener ------------------------------------------------------
  @Override
  public void onDataSourceSelected(UUID dataSourceID)
  {
    // Notify listeners of change in data source selection
    Collection<IWelcomeListener> listeners = getListenersByType();

    for ( IWelcomeListener listener : listeners )
      listener.onDataSourceSelected(dataSourceID);
  }
  
  @Override
  public void onAddDataSource(DataSource dataSource)
  {
    // Notify listeners of data source added
    Collection<IWelcomeListener> listeners = getListenersByType();

    for ( IWelcomeListener listener : listeners )
      listener.onAddDataSource(dataSource);
  }
  
  @Override
  public void onRemoveDataSource(UUID dataSourceID)
  {
    // Notify listeners of data source removed
    Collection<IWelcomeListener> listeners = getListenersByType();

    for ( IWelcomeListener listener : listeners )
      listener.onRemoveDataSource(dataSourceID);
  }
  
  @Override
  public void onRefreshCommunityList()
  {
    // Notify listeners of change in community monitoring
    Collection<IWelcomeListener> listeners = getListenersByType();

    for ( IWelcomeListener listener : listeners )
      listener.onRefreshCommunityList();
  }
  
  @Override
  public void onMonitorCommunity( UUID communityID )
  {
    // Found the community, so notify
    if ( communityID != null )
    {
      // Notify listeners of change in community monitoring
      Collection<IWelcomeListener> listeners = getListenersByType();

      for ( IWelcomeListener listener : listeners )
        listener.onMonitorCommunity( communityID );

      // Make sure we at least have a Welcome view & dashboard visible
      appViewCtrl.onNavigateApp( eNavDest.WELCOME );
    } 
  }

    @Override
    public void onAddCommunity(Community community) {
        if (community != null)
        {
            // Notify listeners of adding community
            Collection<IWelcomeListener> listeners = getListenersByType();

            for (IWelcomeListener listener : listeners) {
                listener.onAddCommunity(community);
            }
        }
    }

    @Override
    public void onRemoveCommunity(UUID communityID) {
        if (communityID != null) {
            // Notify listeners of removing community
            Collection<IWelcomeListener> listeners = getListenersByType();

            for (IWelcomeListener listener : listeners) {
                listener.onRemoveCommunity(communityID);
            }
        }
    }
    
    @Override
    public void onAddCommunities(Set<Community> addedCommunities) {
        if ( (addedCommunities != null) && (addedCommunities.size() > 0) ) {
            // Notify listeners of adding communities
            Collection<IWelcomeListener> listeners = getListenersByType();

            for (IWelcomeListener listener : listeners) {
                listener.onAddCommunities(addedCommunities);
            }
        }
    }

    @Override
    public void onRemoveCommunities(Set<Community> removedCommunities) {
        if ( (removedCommunities != null) && (removedCommunities.size() > 0) ) {
            // Notify listeners of adding communities
            Collection<IWelcomeListener> listeners = getListenersByType();

            for (IWelcomeListener listener : listeners) {
                listener.onRemoveCommunities(removedCommunities);
            }            
        }
    }
    
    @Override
    public void onEditCommunityObjectives(UUID communityID) {
        if (communityID != null) {
            // Notify listeners of editing community objectives
            Collection<IWelcomeListener> listeners = getListenersByType();

            for (IWelcomeListener listener : listeners) {
                listener.onEditCommunityObjectives(communityID);
            }
        }
    }

    @Override
    public void onAddCommunityObjective(UUID communityID, Objective objective) {
        if (communityID != null && objective != null) {
            // Notify listeners of adding community objective
            Collection<IWelcomeListener> listeners = getListenersByType();

            for (IWelcomeListener listener : listeners) {
                listener.onAddCommunityObjective(communityID, objective);
            }
        }
    }

    @Override
    public void onRemoveCommunityObjective(UUID communityID, Objective objective) {
        if (objective != null) {
            // Notify listeners of removing community objective
            Collection<IWelcomeListener> listeners = getListenersByType();

            for (IWelcomeListener listener : listeners) {
                listener.onRemoveCommunityObjective(communityID, objective);
            }
        }
    }

    @Override
    public void onResetDatabaseClicked(String deleteDatabaseOption, String authToken) {
        Collection<IWelcomeListener> listeners = getListenersByType();

        for (IWelcomeListener listener : listeners) {
            listener.onResetDatabaseClicked(deleteDatabaseOption, authToken);
        }
    }

    @Override
    public void setCurrentPlatform(String platform) {
        currentPlatform = platform;
    }
    
    @Override
    public void setCurrentCommunity(Community community) {
        currentCommunity = community;
    }

    @Override
    public void setCurrentObjectives(Set<Objective> objectives) {
        currentObjectives = objectives;
    }
    
    public String getCurrentCommunityName()
    {
        if (currentCommunity != null)
        {
            String name = currentCommunity.getName();
            if (name != null)
                return name;
        }
        
        return "Unknown name";
    }
    
    @Override
    public Community getCurrentCommunity()
    {
        return currentCommunity;
    }
    
    public Set<Objective> getCurrentObjectives() {
        return currentObjectives;
    }
    
    @Override
    public void onCommunityDataUpdated() {
        welcomeView.onCommunityDataUpdated();
    }

    public void createDataSourcesAndCommunitiesView() {
        welcomeView.createDataSourcesAndCommunitiesView();
    }

    public void createCommunityCreateView() {
        welcomeView.createCommunityCreateView();
    }

    @Override
    public void onDataSourceAdded(DataSource dataSource) {
        currentDataSources.add(dataSource);
        welcomeView.onDataSourceAdded(dataSource);
    }

    @Override
    public void onDataSourceDeleted(UUID dataSourceID) {
        for (DataSource dataSource : currentDataSources) {
            if (dataSource.getUuid().equals(dataSourceID)) {
                currentDataSources.remove(dataSource);
                break;
            }
        }
        welcomeView.onDataSourceDeleted(dataSourceID);
    }

    @Override
    public void startProgressIndicator() {
        welcomeView.startProgressIndicator();
    }
}