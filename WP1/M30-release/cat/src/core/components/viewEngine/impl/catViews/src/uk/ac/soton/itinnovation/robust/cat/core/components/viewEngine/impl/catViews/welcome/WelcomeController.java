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
//      Created By :            sgc
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
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;

public class WelcomeController extends    UFAbstractEventManager
                               implements Serializable,
                                          IUFController,
                                          IWelcomeController,
                                          WelcomeViewListener
{
  private CATAppViewController    appViewCtrl;
  private WelcomeView             welcomeView;
  
  private transient HashMap<UUID,Community> currentCommunities;
  private transient Community currentCommunity;
  private transient Set<Objective> currentObjectives;
    
  public WelcomeController( CATAppViewController avc )
  {
    appViewCtrl = avc;
    
    currentCommunities = new HashMap<UUID,Community>();
    
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
  public void setCommunityInfo( Set<Community> communities )
  {
    if ( communities != null )
    {
      currentCommunities.clear();
      
      // Create presentation list for view
      //HashMap<UUID, String> nameIDMap = new HashMap<UUID, String>();
      
      Iterator<Community> comIt = communities.iterator();
      while ( comIt.hasNext() )
      {
        Community community = comIt.next();
        
        UUID comID = community.getUuid();
        currentCommunities.put( community.getUuid(), community );
        //nameIDMap.put( comID, community.getName() );
      }
      
      // Update view with basic info
      //welcomeView.setCommunityNames( nameIDMap );
      welcomeView.setCommunityNames( communities );
    }
  }
  
  // WelcomeViewListener ------------------------------------------------------
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
}