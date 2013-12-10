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
//      Created Date :          21 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators.AbstractDashIndicatorView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.ui.*;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button.ClickListener;

import java.util.*;




class DashIndicatorContainer extends SimpleView
{
  private HorizontalLayout indicatorBrowseContainer;
  private VerticalLayout   indicatorPlaceholder;
  
  private HashMap<LizButton, AbstractDashIndicatorView> attachedViews;
  private LinkedList<LizButton>                         navButtonList;
  private LizButton                                     currentVizButton;
  
  private ClickListener leftNavListener;
  private ClickListener rightNavListener;
          
  // Protected methods ---------------------------------------------------------
  protected DashIndicatorContainer()
  {
    super();
    
    viewContents.addStyleName( "catDashC2" );
    
    attachedViews = new HashMap<LizButton, AbstractDashIndicatorView>();
    navButtonList = new LinkedList<LizButton>();
    
    // Shared navigation listeners
    leftNavListener  = new NavListener( true );
    rightNavListener = new NavListener( false );
    
    createComonents();
  }
  
  protected void setInitialView() {
      if (navButtonList.size() > 0) {
          LizButton vizButton = navButtonList.get(0);
          setIndicatorView(vizButton);
      }
  }
  
  protected void addIndicatorView( AbstractDashIndicatorView view )
  {
    if ( view != null )
    {
      if ( currentVizButton != null ) currentVizButton.setHighlight( false );
      
      currentVizButton = new LizButton( view.getIndicatorIcon() );
      attachedViews.put( currentVizButton, view );
      navButtonList.add( currentVizButton );
      
      indicatorBrowseContainer.addComponent( (Component) currentVizButton.getImpl() );
      
      setIndicatorView( currentVizButton );
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void createComonents()
  {
    // Space
    VerticalLayout vcVL = (VerticalLayout) viewContents;
    vcVL.addStyleName( "catDashGradientBottom" );
    vcVL.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    createBrowseContainer();
    
    // Space & ruler
    vcVL.addComponent( UILayoutUtil.createSpace("2px", null) );
    Component ruler = UILayoutUtil.createSpace("1px", "catDashC4");
    ruler.setWidth( "100%" );
    vcVL.addComponent( ruler );
    
    // Space
    vcVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.setHeight( "95%" );
    vcVL.addComponent( hl );
    vcVL.setComponentAlignment( hl, Alignment.TOP_CENTER );
    
    // Left navigation
    Button button = new Button();
    button.setWidth( "64px" );
    button.setHeight( "64px" );
    button.addStyleName( "borderless" );
    button.setIcon( ViewResources.CATAPPResInstance.getResource("dashLeftNavIcon") );
    button.addListener( leftNavListener );
    hl.addComponent( button );
    hl.setComponentAlignment( button, Alignment.MIDDLE_LEFT );
    
    // Indicator vertical sheet
    indicatorPlaceholder = new VerticalLayout();
    indicatorPlaceholder.setHeight( "90%" );
    hl.addComponent( indicatorPlaceholder );
    hl.setComponentAlignment( indicatorPlaceholder, Alignment.MIDDLE_CENTER );
    
    // Right navigation
    button = new Button();
    button.setWidth( "64px" );
    button.setHeight( "64px" );
    button.addStyleName( "borderless" );
    button.setIcon( ViewResources.CATAPPResInstance.getResource("dashRightNavIcon") );
    button.addListener( rightNavListener );
    hl.addComponent( button );
    hl.setComponentAlignment( button, Alignment.MIDDLE_RIGHT );
    
    // Footer
    Button footer = new Button();
    footer.addStyleName( "borderless" );
    footer.setIcon( ViewResources.CATAPPResInstance.getResource("dashFooter") );
    vcVL.addComponent( footer );
    vcVL.setComponentAlignment( footer, Alignment.BOTTOM_CENTER );
  }
  
  private void createBrowseContainer()
  {
    VerticalLayout vcVL = (VerticalLayout) viewContents;    
    HorizontalLayout hl = new HorizontalLayout();
    hl.addStyleName( "catDashGradientTop" );
    hl.setWidth( "100%" );
    hl.setSpacing( true );
    vcVL.addComponent( hl );
    vcVL.setComponentAlignment( hl, Alignment.MIDDLE_CENTER );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("2px", null, true) );
    
    Button button = new Button();
    button.setWidth( "64px" );
    button.setHeight( "64px" );
    button.addStyleName( "borderless" );
    button.setIcon( ViewResources.CATAPPResInstance.getResource("dashLeftNavIcon") );
    button.addListener( leftNavListener );
    hl.addComponent( button );
    hl.setComponentAlignment( button, Alignment.MIDDLE_LEFT );
    
    indicatorBrowseContainer = new HorizontalLayout();
    indicatorBrowseContainer.setHeight( "100px" );
    indicatorBrowseContainer.addStyleName( "catDashGradientTop" );
    hl.addComponent( indicatorBrowseContainer );
    hl.setExpandRatio( indicatorBrowseContainer, 10.0f );
    
    button = new Button();
    button.setWidth( "64px" );
    button.setHeight( "64px" );
    button.addStyleName( "borderless" );
    button.setIcon( ViewResources.CATAPPResInstance.getResource("dashRightNavIcon") );
    button.addListener( rightNavListener );
    hl.addComponent( button );
    hl.setComponentAlignment( button, Alignment.MIDDLE_RIGHT );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("2px", null, true) );
  }
  
  private void setIndicatorView( LizButton button )
  {
    if ( button != null )
    {
      AbstractDashIndicatorView targetView = attachedViews.get( button );
      if ( targetView != null )
      {
        if ( currentVizButton != null ) currentVizButton.setHighlight( false );
        
        currentVizButton = button;
        currentVizButton.setHighlight( true );
        
        indicatorPlaceholder.removeAllComponents();
        indicatorPlaceholder.addComponent( (Component) targetView.getImplContainer() );
      }
    }
  }
  
  private void onVisualizationButtonClicked( LizButton button )
  { setIndicatorView( button ); }
  
  private void onNavigationVisualisation( boolean navigateLeft )
  {
    int navListSize = navButtonList.size();
    
    if ( navListSize > 1 && currentVizButton != null )
    {
      int currButtonIndex = navButtonList.indexOf( currentVizButton );
      LizButton nextButton = null; 
      
      if ( navigateLeft )
      {
        if ( currButtonIndex > 0 )
          nextButton = navButtonList.get( currButtonIndex -1 );
      }
      else
      {
        if ( currButtonIndex < navListSize -1 )
          nextButton = navButtonList.get( currButtonIndex +1 );
      }
      
      if ( nextButton != null ) setIndicatorView( nextButton );
    }
  }
  
  private class NavListener implements ClickListener
  {
    private boolean navLeft;
    
    public NavListener( boolean isLeft )
    { navLeft = isLeft; }
    
    @Override
    public void buttonClick(Button.ClickEvent event)
    { onNavigationVisualisation( navLeft ); }
  }
  
  // Visualisation button mini-component ---------------------------------------
  private class LizButton implements ClickListener
  {
    private VerticalLayout buttonVL;
    private VerticalLayout highlight;
    
    public LizButton( ThemeResource icon )
    {
      buttonVL = new VerticalLayout();
      buttonVL.setWidth( "132px" );
      buttonVL.setHeight( "92px" );
      
      buttonVL.addComponent( UILayoutUtil.createSpace("4px", null) );
      
      Button button = new Button();
      button.setWidth( "128px" );
      button.setHeight( "80px" );
      button.addStyleName( "borderless" );
      button.setIcon( icon );
      button.addListener( this );
      buttonVL.addComponent( button );
      
      buttonVL.addComponent( UILayoutUtil.createSpace("4px", null) );
      
      highlight = new VerticalLayout();
      highlight.setHeight( "2px" );
      buttonVL.addComponent( highlight );
    }
    
    public Component getImpl()
    { return buttonVL; }
    
    public void setHighlight( boolean lit )
    {
      if ( lit )
        highlight.addStyleName( "catDashRedHighlight" );
      else
        highlight.removeStyleName( "catDashRedHighlight" );
    }
    
    // Private methods ---------------------------------------------------------
    private void onClicked()
    { onVisualizationButtonClicked(this); }
    
    // Event handler -----------------------------------------------------------
    @Override
    public void buttonClick(Button.ClickEvent event)
    { onClicked(); }
  }
}