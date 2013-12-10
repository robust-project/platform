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
//      Created Date :          14 Nov 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UIChangeData;

import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;

import java.util.*;




public class PredictorViewCD extends UIChangeData
{
  private boolean isPredictionActive;
  
  private HashMap<UUID, Event>        eventsToAdd;
  private HashSet<UUID>               eventsToRemove;
  private PredictorServiceDescription predictorServiceDesc;
  
  
  public PredictorViewCD()
  {
    super();
    
    eventsToAdd    = new HashMap<UUID, Event>();
    eventsToRemove = new HashSet<UUID>();
  }
  
  @Override
  public void reset()
  {
    isPredictionActive = false;
    predictorServiceDesc = null;
    eventsToAdd.clear();
    eventsToRemove.clear();
    dataChanged = false;
  }

  public boolean isPredictionActive() { return isPredictionActive; }
  public void setPredictionActive( boolean active ) { isPredictionActive = active; dataChanged = true; }
  
  public Collection<Event> getEventsToUpdate() 
  { return eventsToAdd.values(); }
  
  public Collection<UUID> getEventsToRemove()
  { return eventsToRemove; }
  
  public boolean addEvent( Event ev )
  {
    boolean addedOK = false;
    
    if ( ev != null )
    {
      UUID eventID = ev.getUuid();
      
      eventsToRemove.remove( eventID );
      
      if ( !eventsToAdd.containsKey( eventID ) )
      {
        eventsToAdd.put( eventID, ev );
        dataChanged = true;
        addedOK = true;
      }
    }
    
    return addedOK;
  }
  
  public void removeEvent( UUID evID )
  {
    if ( eventsToAdd.containsKey(evID) )
      eventsToAdd.remove( evID );
    
    eventsToRemove.add( evID );
    
    dataChanged = true;
  }
  
  public PredictorServiceDescription getPSD() { return predictorServiceDesc; }
  
  public void setPSD( PredictorServiceDescription psd ) { predictorServiceDesc = psd; dataChanged = true; }
}