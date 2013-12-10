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
//      Created Date :          2011-09-15
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.common;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.riskEditor.IRiskEditorController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.welcome.IWelcomeController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.treatment.ITreatmentCentreController;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.ViewResources;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.exceptions.CATUICompException;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.DashboardController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.RiskEditorMainController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.treatments.TreatmentCentreController;

import com.vaadin.ui.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.simulation.SimulationCentreController;
import static uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.ICATAppViewNavListener.eNavDest.WELCOME_NO_DASHBOARD;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.simulation.ISimulationCentreController;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import org.apache.log4j.Logger;




public class CATAppViewController extends UFAbstractEventManager
                                  implements Serializable,
                                             ICATAppViewController,
                                             ICATAppViewNavListener,
                                             CATAppContainerListener
{
  private ViewResources              viewResources;
  private CATAppViewContainer        appViewContainer;
  private IUFView                    currMainView;
  private WelcomeController          welcomeController;
  private DashboardController        dashboardController;
  private RiskEditorMainController   riskEditorController;
  private SimulationCentreController simulationCentreController;
  private TreatmentCentreController  treatmentCentreController;
  
  private boolean justStartedUp = true;
  private boolean isPublicDemo = true;
  
  static Logger log = org.apache.log4j.Logger.getLogger(CATAppViewController.class);
  
  public CATAppViewController()
  {
    viewResources = new ViewResources();
    ViewResources.CATAPPResInstance = viewResources;
  }
  
  public boolean isPublicDemo() {
      return isPublicDemo;
  }
  
  public void initialiseCATViews( com.vaadin.Application vaadinApp,
                                  String appTitle, Properties props)
  {    
    try {
        isPublicDemo = Boolean.parseBoolean(props.getProperty("isPublicDemo"));
    } catch (Exception ex) {
        log.error("Error with loading getting and converting 'isPublicDemo' parameter from cat.properties. " + ex.getMessage(), ex);
    }
    log.info("isPublicDemo:  " + isPublicDemo);

    // If we cannot correctly initialise the app container there's not much
    // point continuing, so clean up if necessary
    try
    {
      appViewContainer = new CATAppViewContainer();
      appViewContainer.initialise( vaadinApp, appTitle );
      appViewContainer.addListener( this );

      initViewResources();

      // Welcome view
      welcomeController = new WelcomeController( this );

      // Dashboard view
      dashboardController = new DashboardController( this, props);
      appViewContainer.addFloatingWindow( (Window) dashboardController.getView("").getImplContainer() );

      // Risk editor
      riskEditorController = new RiskEditorMainController();
      
      // Simulation centre
      try {
        simulationCentreController = new SimulationCentreController( this );
        simulationCentreController.setProperties(props);
      }
      catch (RuntimeException re) {
          appViewContainer.displayMessage("ERROR", re.getMessage());
      }
      
      // Treatment centre
      treatmentCentreController = new TreatmentCentreController( this );

      // Push into main view
      setMainView( welcomeController.getView("") );
    }
    catch ( CATUICompException cuce ) {
        appViewContainer = null;
    }
  }
  
  // ICATAppViewController -----------------------------------------------------
  @Override
  public void addViewResource( String ID, String resPath )
  { viewResources.createResource( ID, resPath ); }
  
  @Override
  public void removeViewResource( String ID )
  { viewResources.removeResource( ID ); }
  
  @Override
  public IUFView getCurrentView()
  { return currMainView; }
  
  @Override
  public IWelcomeController getWelcomeViewController()
  { return welcomeController; }
  
  @Override
  public IDashboardController getDashboardViewController()        
  { return dashboardController; }
  
  @Override
  public IRiskEditorController getRiskEditorViewController()       
  { return riskEditorController; }
  
  @Override
  public ISimulationCentreController getSimulationCentreController()
  { return simulationCentreController; }
  
  @Override
  public ITreatmentCentreController getTreatmentCentreController()
  { return treatmentCentreController; }
    
  // ICATAppViewNavListener ----------------------------------------------------
  @Override
  public void onNavigateApp( ICATAppViewNavListener.eNavDest destination )
  {
    // On the first navigation, always create the dashboard (then size appropriately)
    if ( justStartedUp )
    {
      IDashboardView dashView = (IDashboardView) dashboardController.getView("");
      dashView.setFullSize( false );
      
      IUFView view = (IUFView) dashView;
      if (destination.equals(WELCOME_NO_DASHBOARD)) {
        view.setVisible( false );
      }
      else {
        view.setVisible( true );
      }
    }
    else
      // Disappear current view
      if ( currMainView != null ) currMainView.setVisible( false );
      
    // Navigation for other views
    switch ( destination )
    {
      case WELCOME_NO_DASHBOARD : setMainView( welcomeController.getView("") ); break;
      case WELCOME          : setMainView( welcomeController.getView("") ); break;
      case RISK_EDITOR      : setMainView( riskEditorController.getView("") ); break;
      case SIMULATOR_CENTRE : setMainView( simulationCentreController.getView("") ); break;
      case TREATMENT_CENTRE : setMainView( treatmentCentreController.getView("") ); break;
    }
  }
  
  // CATAppContainerListener ---------------------------------------------------
  @Override
  public void onUserResizedAppView()
  {
    boolean resizing = true;
    
    // Update current main view
    if ( currMainView != null ) currMainView.updateView(resizing);
    
    // Update dashboard view (floating and dynamic)
    if ( dashboardController != null )
    {
      IUFView view = dashboardController.getView( "" );
      //view.updateView();
      view.resizeView();
    }
  }
  
  @Override
  public void onUserClosedAppView()
  {
    dashboardController.closedownDashboard(); // Threaded components here
    
    List<ICATAppViewListener> listeners = this.getListenersByType();
    for ( ICATAppViewListener listener : listeners )
      listener.onAppViewClosed();
  }
  
  // Private methods -----------------------------------------------------------
  private void initViewResources()
  {
    // 16x16 icons
    viewResources.createResource( "16x16EmptyIcon", "img/commonIcons/16x16Empty.png" );
    viewResources.createResource( "16x16LeftArrow", "img/commonIcons/16x16LeftArrow.png" );
    viewResources.createResource( "16x16RightArrow", "img/commonIcons/16x16RightArrow.png" );
    
    // 24x24 icons
    viewResources.createResource( "24x24EmptyIcon", "img/commonIcons/24x24Empty.png" );
    viewResources.createResource( "24x24LeftArrow", "img/commonIcons/24x24LeftArrow.png" );
    viewResources.createResource( "24x24RightArrow", "img/commonIcons/24x24RightArrow.png" );
    
    // 32x32 icons
    viewResources.createResource( "32x32LeftArrow", "img/commonIcons/32x32LeftArrow.png" );
    viewResources.createResource( "32x32RightArrow", "img/commonIcons/32x32RightArrow.png" );
    viewResources.createResource( "32x32EmptyIcon", "img/commonIcons/32x32Empty.png" );
    
    // Impact icons
    viewResources.createResource( "highNegImpactArrow", "img/commonIcons/impactIcons/highNeg.png" );
    viewResources.createResource( "highPosImpactArrow", "img/commonIcons/impactIcons/highPos.png" );
    viewResources.createResource( "lowNegImpactArrow", "img/commonIcons/impactIcons/lowNeg.png" );
    viewResources.createResource( "lowPosImpactArrow", "img/commonIcons/impactIcons/lowPos.png" );
    viewResources.createResource( "mediumNegImpactArrow", "img/commonIcons/impactIcons/mediumNeg.png" );
    viewResources.createResource( "mediumPosImpactArrow", "img/commonIcons/impactIcons/mediumPos.png" );
    viewResources.createResource( "veryHighNegImpactArrow", "img/commonIcons/impactIcons/veryHighNeg.png" );
    viewResources.createResource( "veryHighPosImpactArrow", "img/commonIcons/impactIcons/veryHighPos.png" );
    viewResources.createResource( "veryLowNegImpactArrow", "img/commonIcons/impactIcons/veryLowNeg.png" );
    viewResources.createResource( "veryLowPosImpactArrow", "img/commonIcons/impactIcons/veryLowPos.png" );
    
    // Dashboard resources
    viewResources.createResource( "dashFooter", "img/dashIcons/DashFooter.png" );
    viewResources.createResource( "dashMinIcon", "img/dashIcons/DashMin.png" );
    viewResources.createResource( "dashMaxIcon", "img/dashIcons/DashMax.png" );
    viewResources.createResource( "dashLeftNavIcon", "img/dashIcons/DashNavLeft.png" );
    viewResources.createResource( "dashRightNavIcon", "img/dashIcons/DashNavRight.png" );
    viewResources.createResource( "riskMatrixIcon", "img/dashIcons/RiskMatrixIcon.png" );
    viewResources.createResource( "GfxLizerIcon", "img/dashIcons/LizerIcon.png" );
    viewResources.createResource( "ROEvalHistoryIcon", "img/dashIcons/ROEvalHistoryIcon.png" );
    viewResources.createResource( "RoleCompIcon", "img/dashIcons/RoleCompositionIcon.png" );
    
    // Buttons
    viewResources.createResource( "dashWelcomeButton", "img/dashIcons/WelcomeNavButton.png" );
    viewResources.createResource( "dashRiskEditorButton", "img/dashIcons/RiskEditorNavButton.png" );
    viewResources.createResource( "dashComSimButton", "img/dashIcons/CommunitySimulationNavButton.png" );
    viewResources.createResource( "dashTreatEditorButton", "img/dashIcons/TreatmentEditorNavButton.png" );
    
    // Misc
    viewResources.createResource( "comingSoon", "img/misc/comingSoon.png" );
    viewResources.createResource( "noWorkflow", "img/misc/noWorkflowImage.jpg" );
  }
  
  private void setMainView( IUFView view )
  {
    if ( view != null )
    {
      try
      {
        Component cc = (Component) view.getImplContainer();
        appViewContainer.setViewContent( cc );
        
        currMainView = view;
        currMainView.updateView(false); // not resizing
      }
      catch ( CATUICompException cuce ) { System.out.println(cuce.toString()); }
    }
  }

    @Override
    public void setCommunity(Community currCommunity) {
        dashboardController.setCommunity(currCommunity);
    }
}