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

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.ICATAppViewNavListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.IDashboardView;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.EmbeddedTimerListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.shared.appNavigator.AppNavView;

import com.vaadin.ui.*;
import com.vaadin.terminal.*;

import java.io.Serializable;
import java.util.*;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;





class DashboardView extends UFAbstractEventManager implements Serializable,
                                                              EmbeddedTimerListener,
                                                              IUFView,
                                                              IDashboardView
{
  public static final int MIN_HEIGHT = 128;
  public static final String DASHSIZE_MAXIMIZE_DESC = "Maximize dashboard";
  public static final String DASHSIZE_MINIMIZE_DESC = "Minimize dashboard";
  
  private Window                 dashWindow;
  private VerticalLayout         dashMainPanelContainer;
  private DashIndicatorContainer dashIndicatorContainer;
  private boolean                isFullSizeView = true;
  private EmbeddedTimer          dashboardTimer;
  
  private Button   dashSizeToggle;
  private Resource maximizeIcon;
  private Resource minimizeIcon;
  
  private Label    timeLabel;
  private Label    dateLabel;
  private boolean  timeDash = true;
  
  public void registerETListener( EmbeddedTimerListener etl )
  { dashboardTimer.addListener( (IUFListener) etl ); }
  
  public void shutdownView()
  {
    dashboardTimer.stop();
  }
  
  // EmbeddedTimerListener -----------------------------------------------------
  @Override
  public boolean isRecurring()
  { return true; }
  
  @Override
  public int getEventInterval()
  { return 2; }
  
  @Override
  public void onTimerEvent()
  {
    Calendar calendar = Calendar.getInstance();
    
    // Update time
    int tVal = calendar.get( Calendar.HOUR );
    if ( calendar.get( Calendar.AM_PM ) == Calendar.PM ) tVal += 12;
    String timeValue = tVal + (timeDash ? ":" : " ");
    
    tVal = calendar.get( Calendar.MINUTE );
    timeValue += ( tVal < 10 ) ? "0" + tVal : tVal;
    timeLabel.setValue( timeValue );
    
    // and date
    dateLabel.setValue( calendar.get(Calendar.DAY_OF_MONTH) + " : " +
                        (1 + calendar.get(Calendar.MONTH)) + " : " + 
                        calendar.get(Calendar.YEAR) );
    
    timeDash = !timeDash;
  }
  
  // IUFView -------------------------------------------------------------------
  @Override
  public Object getImplContainer()
  { return dashWindow; }
	
  @Override
  public boolean isVisible()
  {
    if ( dashWindow != null )
      return dashWindow.isVisible();
    
    return false;
  }
  
  @Override
  public void setVisible( boolean visible )
  {
    if ( dashWindow != null )
      dashWindow.setVisible( visible );
  }
  
  @Override
  public void updateView(boolean resizing)
  { layoutComponents(false); } // not resizing
  
  @Override
  public void resizeView()
  { layoutComponents(true); } // resizing
  
  @Override
  public void displayMessage( String title, String content )
  {
    dashWindow.showNotification( title, content,
                                 com.vaadin.ui.Window.Notification.TYPE_HUMANIZED_MESSAGE );
  }
  
  @Override
  public void displayWarning( String title, String content )
  {
    dashWindow.showNotification( title, content,
                                 com.vaadin.ui.Window.Notification.TYPE_WARNING_MESSAGE ); 
  }
  
  //IDashboardView -------------------------------------------------------------
  @Override
  public boolean isFullSize()
  { return isFullSizeView; }
  
  @Override
  public void setFullSize( boolean fs )
  {
    isFullSizeView = fs;
    layoutComponents();
  }
  
  // Protected methods ---------------------------------------------------------
  protected DashboardView( ICATAppViewNavListener navListener )
  {
    createResources();
    createComponents( navListener );
    layoutComponents();
  }
  
  protected DashIndicatorContainer getIndicatorContainer()
  { return dashIndicatorContainer; }
  
  // Private methods -----------------------------------------------------------
  private void createResources()
  {
    minimizeIcon = ViewResources.CATAPPResInstance.getResource( "dashMinIcon" );
    maximizeIcon = ViewResources.CATAPPResInstance.getResource( "dashMaxIcon" );
  }
  
  private void createComponents( ICATAppViewNavListener navListener )
  {
    dashWindow = new Window( "CAT dashboard" );
    dashWindow.addStyleName( "opaque" );
    dashWindow.addStyleName( "catDashC3" );
    dashWindow.setVisible( false ); // don't show this initially
    dashWindow.setClosable( false );
    dashWindow.setDraggable( false );
    dashWindow.setResizable( false );
    dashWindow.setWidth( "100%" );
    
    VerticalLayout topVL = new VerticalLayout();
    dashWindow.setContent( topVL );
    
    dashboardTimer = new EmbeddedTimer();
    topVL.addComponent( (Component) dashboardTimer.getImplContainer() );
    
    topVL.addComponent( createDashSummaryBar( navListener ) );
    topVL.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    Component divider = UILayoutUtil.createSpace("1px", "catDashC6");
    divider.setWidth( "100%" );
    topVL.addComponent( divider );
    
    // Main dash container (layout will inject actual dash as required)    
    dashMainPanelContainer = new VerticalLayout();
    topVL.addComponent( dashMainPanelContainer ); 
    
    dashIndicatorContainer = new DashIndicatorContainer(); // injected later
  }
  
  private ComponentContainer createDashSummaryBar( ICATAppViewNavListener navListener )
  {
    HorizontalLayout hl = new HorizontalLayout();
    
    // Dashboard size toggle
    dashSizeToggle = new Button();
    dashSizeToggle.setIcon( ViewResources.CATAPPResInstance.getResource("minDashIcon") );
    dashSizeToggle.setDescription(DASHSIZE_MINIMIZE_DESC);
    dashSizeToggle.addStyleName( "borderless icon-on-top" );
    dashSizeToggle.addListener( new ToggleClickListener() );
    hl.addComponent( dashSizeToggle );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace( "16px", null, true) );
    
    // Status panel
    ComponentContainer statusPanel  = createDashStatusPanel();
    hl.addComponent( statusPanel );
    hl.addComponent( UILayoutUtil.createSpace( "16px", null, true) );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace( "16px", null, true) );
    
    // Navigation
    AppNavView navView = new AppNavView();
    navView.addListener( (IUFListener) navListener );
    Component navContainer = (Component) navView.getImplContainer();
    hl.addComponent( navContainer );
    
    return hl;
  }
  
  private ComponentContainer createDashStatusPanel()
  {
    HorizontalLayout contents = new HorizontalLayout();
    
    VerticalLayout timeVL = new VerticalLayout();
    contents.addComponent( timeVL );
    
    timeLabel = new Label();
    timeLabel.addStyleName( "catDashBIGFont" );
    timeLabel.setWidth( "200px" );
    timeVL.addComponent( timeLabel );
    timeVL.setComponentAlignment( timeLabel, Alignment.MIDDLE_CENTER );
    
    // Space
    timeVL.addComponent( UILayoutUtil.createSpace( "2px", null) );
    
    dateLabel = new Label();
    dateLabel.addStyleName( "catSubSectionFont" );
    dateLabel.setWidth( "200px" );
    timeVL.addComponent( dateLabel );
    timeVL.setComponentAlignment( dateLabel, Alignment.MIDDLE_CENTER );
    
    onTimerEvent(); // update the time
    dashboardTimer.addListener( this );
    
    return contents;
  }
  
  private void layoutComponents() {
      layoutComponents(false);
  }
  
  private void layoutComponents(boolean resizing)
  {
    if ( dashWindow != null )
    {
      // Map dash window to parent window size
      Window parentWindow = dashWindow.getParent();
      
      if ( isFullSizeView )
      {
        dashWindow.setPositionY( 0 );
        dashWindow.setSizeFull();
        
        // Inject main dash panel
        Component div = (Component) dashIndicatorContainer.getImplContainer();
        dashIndicatorContainer.refreshTargetView(resizing);
        dashMainPanelContainer.addComponent( div );
        dashMainPanelContainer.setComponentAlignment( div, Alignment.MIDDLE_CENTER );
        
        dashSizeToggle.setIcon( minimizeIcon );
        dashSizeToggle.setDescription(DASHSIZE_MINIMIZE_DESC);
      }
      else
      {
        int yPos = (int) parentWindow.getHeight();
        yPos -= MIN_HEIGHT;
        
        // Hide main dash panel
        if ( dashIndicatorContainer != null )
          dashMainPanelContainer.removeAllComponents();
        
        dashWindow.setPositionY( yPos );
        dashWindow.setHeight( new Float(MIN_HEIGHT).toString() +"px" );
        
        dashSizeToggle.setIcon( maximizeIcon );
        dashSizeToggle.setDescription(DASHSIZE_MAXIMIZE_DESC);
      }
    }
  }
  
  private void onToggleDashSize()
  {
    isFullSizeView = !isFullSizeView;
    layoutComponents();
  }

    void setCommunity(Community currCommunity) {
        if (currCommunity != null) {
            String community = (currCommunity.getName() != null) ? currCommunity.getName() : "";
            String platform = (currCommunity.getPlatform() != null) ? " (" + currCommunity.getPlatform() + ")" : "";
            dashWindow.setCaption("CAT dashboard: " + community + platform);
            dashWindow.setVisible(true);
        }
        else {
            dashWindow.setVisible(false);
        }
    }
  
  // Internal event handlers ---------------------------------------------------
  private class ToggleClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onToggleDashSize(); }
  }
}