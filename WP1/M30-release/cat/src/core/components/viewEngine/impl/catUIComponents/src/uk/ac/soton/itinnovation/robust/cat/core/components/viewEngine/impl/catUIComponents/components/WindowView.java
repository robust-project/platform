/////////////////////////////////////////////////////////////////////////
//
// ï¿½ University of Southampton IT Innovation Centre, 2011
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
//      Created Date :          20 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.exceptions.CATUICompException;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.Application;
import com.vaadin.terminal.Terminal;
import com.vaadin.ui.*;
import java.io.Serializable;



public class WindowView extends UFAbstractEventManager implements Serializable,
                                                                  IUFView
{
  protected Component compParent;
  protected Window    window;

  private Label windowHeadline;
  
  public WindowView( Component parent, String title )
  {
    compParent = parent;
    
    try { createComponents( title ); }
    catch (CATUICompException ce) {}
  }
  
  public void displayMessage( String message )
  {
    if ( window != null )
      window.showNotification( message, "", Window.Notification.TYPE_HUMANIZED_MESSAGE );
  }
  
  public void centreWindow()
  {
    if ( window != null && compParent != null )
    {
      // Default 
      float tWidth = 1000;
      float tHeight = 1000;
      
      Terminal term = compParent.getApplication().getMainWindow().getTerminal();
      
      if (term != null)
      {
        tWidth  = term.getScreenWidth();
        tHeight = term.getScreenHeight();
      }
      else {
          System.err.println("WARNING: terminal is null - cannot centre window");
      }
      
      float wWidth  = window.getWidth();
      float wHeight = window.getHeight();
      
      // Centre if we can within the client's view
      if ( tWidth >= wWidth && tHeight >= wHeight )
      {
        float wDiff = (tWidth  - wWidth)  / 2.0f;
        float hDiff = (tHeight - wHeight) / 4.0f;
        
        window.setPositionX( (int) wDiff );
        window.setPositionY( (int) hDiff );
      }
    }
  }
  
  // IUFView -------------------------------------------------------------------
  @Override
	public Object getImplContainer()
	{
		return window;
	}
	
	@Override
  public boolean isVisible()
  {
    if ( window != null )
      return window.isVisible();
    
    return false;
  }
  
  @Override
  public void setVisible( boolean visible )
  {
    if ( window != null )
      window.setVisible( visible );
  }
  
  @Override
  public void updateView() {}
  
  @Override
  public void displayMessage( String title, String content )
  {
    window.showNotification( title, content,
                             com.vaadin.ui.Window.Notification.TYPE_HUMANIZED_MESSAGE );
  }
  
  @Override
  public void displayWarning( String title, String content )
  {
    window.showNotification( title, content,
                             com.vaadin.ui.Window.Notification.TYPE_WARNING_MESSAGE ); 
  }
  
  // Protected methods ---------------------------------------------------------
  @Override
  protected void finalize() throws Throwable
  {
    if ( compParent == null ) throw new Exception( "CAT Window: May not have been able to realse window!" );
    
    Application app = compParent.getApplication();
    app.getMainWindow().removeWindow( window );
    
    super.finalize();
  }
  
  protected Component createHeadline( String title )
  {
    VerticalLayout vl = new VerticalLayout();
    vl.addStyleName( "catSubHeadlineFont" );
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    // Headline
    if ( windowHeadline == null )
    {
      windowHeadline = new Label();
      vl.addComponent( windowHeadline );
    }
    
    windowHeadline.setCaption( title );
    
    // Space & ruler & space
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    Component spacer = UILayoutUtil.createSpace("4px", "catHorizRule", true);
    spacer.setWidth( "100%" );
    vl.addComponent( spacer );
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    return vl;
  }
  
  protected void setHeadline( String title )
  {
    if ( windowHeadline != null ) windowHeadline.setCaption( title );
  }
  
  protected void destroyView()
  {
    // Remove (and destroy) this window
    Application app = compParent.getApplication();
    
    if ( app != null )
    {
      Window mw = app.getMainWindow();
      if ( mw != null && mw != window ) mw.removeWindow( window );
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents( String title ) throws CATUICompException
  {
    if ( compParent == null ) throw new CATUICompException( "CAT Window: parent is NULL!" );
            
    window = new Window( title );
    window.setWidth( "20px" );
    window.setHeight( "40px" );
    window.setResizable( false );
    window.setVisible( true );
    window.setClosable( false ); // Other controls will handle this
    
    Application app = compParent.getApplication();
    app.getMainWindow().addWindow( window );
  }
}