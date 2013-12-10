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
//      Created By :            Simon Crowle
//      Created Date :          10-Dec-2012
//      Created for Project :   robust-cat-core-components-viewEngine-impl-catUIComponents
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFHook;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.EmbeddedTimerListener;

import com.vaadin.ui.ProgressIndicator;
import java.util.*;




public class EmbeddedTimer extends SimpleView
{
  private ProgressIndicator progIndicator; // Required to keep connection with Vaadin server 
                                           // so that updates can be performed quickly
  
  private Timer                                       embeddedTimer;
  private HashMap<EmbeddedTimerListener, UITimerTask> embeddedTasks;
  
  public EmbeddedTimer()
  {
    super();
    
    embeddedTimer = new Timer();
    embeddedTasks = new HashMap<EmbeddedTimerListener, UITimerTask>();
    
    createComponents();
  }
  
  public void stop()
  {
    // Stop all remaining tasks
    Iterator<EmbeddedTimerListener> etlIt = embeddedTasks.keySet().iterator();
    while ( etlIt.hasNext() )
    {
      EmbeddedTimerListener etl = etlIt.next();
      UITimerTask task          = embeddedTasks.get( etl );
      
      if ( task != null ) task.cancel();
    }
    
    // Clear out listeners
    embeddedTasks.clear();
  }
  
  // Override IUFNotifier add and remove listener methods to wire in additional
  // time based event handling -------------------------------------------------
  @Override
  public void addListener( IUFListener listener )
  {
    EmbeddedTimerListener etl = (EmbeddedTimerListener) listener;
    
    // Only allow new listeners with sensible values in
    if ( etl != null )
      if ( !embeddedTasks.containsKey(etl) && etl.getEventInterval() >= 0 )
      {
        addETListener( etl );
        super.addListener( listener );
      }
  }
  
  @Override
  public void onListenerHookDestroyed( UFHook hook )
  {
    if ( hook != null )
    {
      EmbeddedTimerListener etl = (EmbeddedTimerListener) hook.getListener();
      if ( etl != null )
      {
        removeETListener( etl );
        super.onListenerHookDestroyed( hook );
      }
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    progIndicator = new ProgressIndicator();
    progIndicator.setWidth( "0px" );
    progIndicator.setHeight( "0px" );
    progIndicator.setImmediate( true );
    progIndicator.setIndeterminate( true );
    viewContents.addComponent( progIndicator );
  }
  
  private synchronized void addETListener( EmbeddedTimerListener etl )
  {
    // Add to the task list
    UITimerTask newTask = new UITimerTask( etl );
    embeddedTasks.put( etl, newTask);
    
    // Schedule
    int interval = etl.getEventInterval();
    
    if ( etl.isRecurring() )
      embeddedTimer.scheduleAtFixedRate( newTask, 50, interval ); // delay first slightly
    else
      embeddedTimer.schedule( newTask, interval ); // Fire once and forget
  }
  
  private synchronized void removeETListener( EmbeddedTimerListener etl )
  {
    if ( etl != null )
    {
      // Kill task first
      UITimerTask task = embeddedTasks.get( etl );
      if ( task != null ) task.cancel();
      
      // And then remove
      embeddedTasks.remove( etl );
    }
  }
  
  // Private timer task --------------------------------------------------------
  private class UITimerTask extends TimerTask
  {
    private EmbeddedTimerListener timerListener;
    
    public UITimerTask( EmbeddedTimerListener listener )
    { 
      super();
      timerListener = listener;
    }
    
    @Override
    public void run ()
    {
      // Fire the event
      timerListener.onTimerEvent();
      
      // If this is a one-shot event, get rid of it
      if ( !timerListener.isRecurring() ) removeETListener( timerListener );
    }
  }
}
