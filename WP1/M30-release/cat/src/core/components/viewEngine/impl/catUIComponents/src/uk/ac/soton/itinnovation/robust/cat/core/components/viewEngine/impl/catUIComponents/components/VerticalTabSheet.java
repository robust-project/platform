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
//      Created Date :          2011-10-21
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.ViewResources;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.ui.*;
import java.util.*;


public class VerticalTabSheet extends SimpleView
{
  private VerticalLayout             tabVL;
  private HashMap<String, Button>    tabButtons;
  private HashMap<String, Component> tabContents;
  private VerticalLayout             contentHolder;
  private Button                     currButton;
  private Embedded                   currArrowImage;
  private Component                  currContents;
  private String                     tabStyle = "small";
  
  public VerticalTabSheet()
  {
    super();
    
    viewContents = new HorizontalLayout();
    viewContents.setStyleName( "borderless light" );
    
    tabButtons  = new HashMap<String, Button>();
    tabContents = new HashMap<String, Component>();
    
    createComponents();
  }
  
  public void addTab( String label, Component component )
  {
    // Remove old tab if it already exists
    if ( tabButtons.containsKey(label) ) removeTab( label );
    
    // Un-focus current button (if it exists) & empty current contents
    if ( currButton != null )
    {
      currButton.removeStyleName( "catBkgndLight" );
      currButton.removeStyleName( "down" );
    }
    
    // Remove old contents
    contentHolder.removeAllComponents();
    
    // New button label
    Button button = new Button( label );
    button.setWidth( "100%" );
    button.setHeight( "100%" );
    button.addStyleName( "small" );
    button.addStyleName( "borderless" );
    button.addStyleName( tabStyle );
    button.addListener( new TabClickListener() );
    
    // Add buttons
    tabVL.addComponent( button );
    tabVL.setComponentAlignment( button, Alignment.TOP_RIGHT );
    
    // Set new contents
    currButton = button;
    currContents = component;
    contentHolder.addComponent( currContents );
    
    // Update internal model
    tabButtons.put( label, button );
    tabContents.put( label, component );
  }
  
  public String getTabStyle()
  { return tabStyle; }
  
  public void setTabStyle( String style )
  { tabStyle = style; }
  
  public void removeTab( String label )
  {
    if ( tabButtons.containsKey(label) )
    {
      // Remove tab button
      Button tabButton = tabButtons.get( label );
      tabVL.removeComponent( tabButton );
      
      // Remove contents
      Component killComp = tabContents.get( label );
      if ( currContents == killComp )
      {
        contentHolder.removeAllComponents();
        currButton = null;
        currContents = null;
      }
      
      tabButtons.remove( label );
      tabContents.remove( label );
    }
  }
  
  public void selectTab( String label )
  {
    if ( tabContents.containsKey(label) )
    {
      // Un-focus last tab
      if ( currButton != null )
      {
        currButton.removeStyleName( "catBkgndLight" );
        currButton.removeStyleName( "down" );
      }
      
      if ( currArrowImage != null )
        currArrowImage.setSource( null );
      
      currButton = tabButtons.get( label );
      
      if ( currButton != null )
      {
        currButton.addStyleName( "catBkgndLight" );
        currButton.addStyleName( "down" );
      }
      
      // Update contents
      currContents = tabContents.get( label );
      contentHolder.removeAllComponents();
      
      contentHolder.addComponent( currContents );
    }
  }
  
  public void clearAllTabs()
  {
    // Copy list of tabs to remove
    Set<String> killTabs = new HashSet<String>( tabButtons.keySet() );
    
    for ( String tab : killTabs ) removeTab( tab );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    HorizontalLayout hl = (HorizontalLayout) viewContents;
    hl.setSpacing( true );
    hl.setSizeFull();
    
    // Tabs
    tabVL = new VerticalLayout();
    hl.addComponent( tabVL );
        
    // Space
    hl.addComponent( UILayoutUtil.createSpace( "2px", null, true) );
    Component spacer = UILayoutUtil.createSpace("2px", "catVertRule", true);
    spacer.setHeight( "100%" );
    hl.addComponent( spacer );
    
    // Content holder
    contentHolder = new VerticalLayout();
    contentHolder.setSizeFull();
    hl.addComponent( contentHolder );
    
    // Layout
    hl.setExpandRatio( tabVL, 5 );
    hl.setExpandRatio( contentHolder, 20 );
  }
  
  // Internal method handlers --------------------------------------------------  
  private class TabClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick( Button.ClickEvent event )
    { selectTab( event.getButton().getCaption() ); }
  }
}