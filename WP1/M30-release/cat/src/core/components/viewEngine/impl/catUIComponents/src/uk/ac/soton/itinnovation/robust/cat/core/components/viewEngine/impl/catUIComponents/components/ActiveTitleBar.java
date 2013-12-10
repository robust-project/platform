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
//      Created By :            sgc
//      Created Date :          19-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.ActiveTitleBarListener;

import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar.*;
import java.util.List;




public class ActiveTitleBar extends SimpleView
{
  private HorizontalLayout optionBar;
  private Button firstButton, lastButton, activeButton;
  
  public ActiveTitleBar( String title )
  {
    super();
    
    createComponent( title );
  }
  
  public void addMenuOption( String option )
  {
    if ( option != null )
    {
      Button optButton = new Button( option );
      optButton.addListener( buttonListener );
      optButton.addStyleName( "default" );
      optButton.addStyleName( "small" );
      optionBar.addComponent( optButton );
      
      // Modify styles accordingly
      if ( firstButton == null )
      {
        firstButton = optButton;
        firstButton.addStyleName( "first" );
        firstButton.addStyleName( "last" );
      }
      else if ( firstButton != null && lastButton == null )
      {
        firstButton.removeStyleName( "last" );
        lastButton = optButton;
        lastButton.addStyleName( "last" );
      }
      else if ( firstButton != null && lastButton != null )
      {
        lastButton.removeStyleName( "last" );
        lastButton = optButton;
        lastButton.addStyleName( "last" );
      }
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponent( String title )
  {
    viewContents.addStyleName( "catBkgnd" );
    
    // Space
    Component space = UILayoutUtil.createSpace("8px", null);
    viewContents.addComponent( space );
    
    HorizontalLayout hl = new HorizontalLayout();
    viewContents.addComponent( hl );
    
    // Space
    space = UILayoutUtil.createSpace("4px", null, true );
    hl.addComponent( space );
    
    // Title
    Label label = new Label( title );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlueDark" );
    hl.addComponent( label );
    hl.setExpandRatio( label, 4.0f );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    // Option bar
    optionBar = new HorizontalLayout();
    optionBar.setStyleName( "segment" );
    hl.addComponent( optionBar );
    hl.setExpandRatio( optionBar, 1.0f );
    
    // Space filler
    space = UILayoutUtil.createSpace("100%", null);
    hl.addComponent( space );
    hl.setExpandRatio( space, 10.0f );
    
    // Rule
    space = UILayoutUtil.createSpace( "100%", "catBorderBottom", true );
    space.setHeight( "4px" );
    viewContents.addComponent( space );
  }
  
  // Internal event handling ---------------------------------------------------
  private void onOptionSelected( Button clicked )
  {
    if ( clicked != null )
    {
      // Toggle group behaviour
      if ( activeButton == null )
      {
        activeButton = clicked;
        activeButton.addStyleName( "down" );
      }
      else if ( clicked != activeButton )
      {
        activeButton.removeStyleName( "down" );
        activeButton = clicked;
        activeButton.addStyleName( "down" );
      }

      // Notify change listener
      List<ActiveTitleBarListener> listeners = getListenersByType();
      if ( !listeners.isEmpty() )
        for ( ActiveTitleBarListener listener : listeners )
          listener.onActiveTitleBarSelection( clicked.getCaption() );
    }
  }
  
  private Button.ClickListener buttonListener = new Button.ClickListener()
  {
    @Override
    public void buttonClick(Button.ClickEvent event)
    { if ( event != null ) onOptionSelected( event.getButton() ); }
  };
}
