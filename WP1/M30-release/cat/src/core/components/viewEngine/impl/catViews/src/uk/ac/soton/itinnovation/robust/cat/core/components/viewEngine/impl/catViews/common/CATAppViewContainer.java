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
//      Created Date :          2011-09-15
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.common;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.exceptions.CATUICompException;

import com.vaadin.ui.*;
import java.io.Serializable;
import java.util.List;



/**
 * CATAppContainer is the top-level view object that web-based CAT applications
 * will use to display their UIs. Since it will not be used in any other component
 * it is useful to conform to IUFView and not be directly externally accessible.
 * 
 * @author sgc
 */
class CATAppViewContainer extends UFAbstractEventManager
                          implements Serializable,
                                     IUFView
{
  private Window coreWindow;
  
  // IUFView -------------------------------------------------------------------
  @Override
  public Object getImplContainer()
  { return coreWindow.getContent(); }
  
  @Override
  public boolean isVisible()
  {
    if ( coreWindow != null )
      return coreWindow.isVisible();
    
    return false;
  }
  
  @Override
  public void setVisible( boolean visible )
  {
    if ( coreWindow != null )
      coreWindow.setVisible( visible );
  }
  
  @Override
  public void updateView()
  { }
  
  @Override
  public void displayMessage( String title, String content )
  {
    coreWindow.showNotification( title, content,
                                 com.vaadin.ui.Window.Notification.TYPE_HUMANIZED_MESSAGE );
  }
  
  @Override
  public void displayWarning( String title, String content )
  {
    coreWindow.showNotification( title, content,
                                 com.vaadin.ui.Window.Notification.TYPE_WARNING_MESSAGE ); 
  }
  
  // Protected methods ---------------------------------------------------------
  protected CATAppViewContainer()
  {}
  
  protected void initialise( com.vaadin.Application vaadinApp,
                             String appTitle )
                            throws CATUICompException
  {
    if ( vaadinApp == null ) throw new CATUICompException( "Vaadin application is NULL" );
    
    vaadinApp.setTheme( "CATStyles" );
    
    coreWindow = new Window( appTitle );
    vaadinApp.setMainWindow( coreWindow );

    coreWindow.setSizeFull();
    coreWindow.addListener( new CoreWindowResizeListener() );
    coreWindow.addListener( new CoreWindowCloseListener() );
  }
  
  protected void setViewContent( Component comp ) throws CATUICompException
  {
    if ( comp == null ) throw new CATUICompException( "Content is null." );
    
    coreWindow.removeAllComponents();
    coreWindow.addComponent( comp );
  }
  
  protected void addFloatingWindow( Window wind ) throws CATUICompException
  {
    if ( coreWindow == null ) throw new CATUICompException( "App container not properly initialized." );
    if ( wind == null ) throw new CATUICompException( "Floating window is null." );
    
    coreWindow.addWindow( wind );
  }
  
  protected void removeFloatingWindow( Window wind ) throws CATUICompException
  {
    if ( coreWindow == null ) throw new CATUICompException( "App container not properly initialized." );
    if ( wind == null ) throw new CATUICompException( "Floating window is null." );
    
    coreWindow.removeWindow( wind );
  }
  
  // Private methods -----------------------------------------------------------
  private void onMainWindowResized()
  {
    List<CATAppContainerListener> listeners = getListenersByType();
    for ( CATAppContainerListener listener: listeners )
      listener.onUserResizedAppView();
  }
  
  private void onMainWindowClosed()
  {
    List<CATAppContainerListener> listeners = getListenersByType();
    for ( CATAppContainerListener listener: listeners )
      listener.onUserClosedAppView();
  }
  
  // Internal event handling ---------------------------------------------------
  private class CoreWindowCloseListener implements Window.CloseListener
  {
    @Override
    public void windowClose( Window.CloseEvent ce ) { onMainWindowClosed(); }
  }
  
  public class CoreWindowResizeListener implements Window.ResizeListener
  {
    @Override
    public void windowResized( Window.ResizeEvent re ) { onMainWindowResized(); }
  }
}