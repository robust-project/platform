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
//      Created Date :          30-Oct-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.ViewResources;

import pl.swmind.robust.webapp.integration.RoleCompositionController;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import java.util.Date;
import org.apache.log4j.Logger;




public class RoleCompWrapperView extends AbstractDashIndicatorView
{
  private RoleCompositionController smindController;
  private String styleName = "catDashC1";
  private VerticalLayout statusVL;
  private VerticalLayout errorVL;
  private Label statusLabel;
  private Label errorMessageLabel;
  private Thread viewUpdaterThread;
  private String platformId;
  private String communityID;
  private Date startDate;
  private final RoleCompWrapperController roleCompWrapperController;
    private VerticalLayout statusDiv;
    private VerticalLayout mainDiv;
    private String status;
    private String errorMessage;
	
	private static Logger logger = Logger.getLogger(RoleCompWrapperView.class);
  
  public RoleCompWrapperView(RoleCompWrapperController roleCompWrapperController)
  {
    super();
    
    this.roleCompWrapperController = roleCompWrapperController;
    
    createComponents();
  }
  
  @Override
  public void refreshView(boolean resizing) {
    // Get the view - currently only using community ID - date to do?
    try
    {
      //IUFView smindView = getRoleCompositionView( platformId, communityID, startDate);
      if (! resizing) updateRoleCompositionView();
      
      /*
      if ( smindView != null )
      {
        AbstractLayout al = (AbstractLayout) smindView.getImplContainer();
        if ( al != null ) {
          hideStatus();
          viewContents.addComponent( al );
        }
      }
      */
    }
    catch ( Exception e )
    {
      /*  
      Label label = new Label("Role Composition Visualisation currently unavailable: " + e.getMessage() );
      label.addStyleName( "medium" );
      viewContents.addComponent( label );
      */
      logger.error("Unable to refresh the role composition view: " + e, e);
      String message = (e.getMessage() != null ? e.getMessage() : "internal error");
      updateStatus("Role Composition Visualisation currently unavailable: " + message, null);
    }
  }
  
  public void updateView( String platformId, String communityID, Date startDate )
  {
    this.platformId = platformId;
    this.communityID = communityID;
    this.startDate = startDate;
    viewContents.removeAllComponents();
    statusLabel = null;
    errorMessageLabel = null;
  }
  
  private void updateView(IUFView smindView) {
      if ( smindView != null )
      {
        AbstractLayout al = (AbstractLayout) smindView.getImplContainer();
        if ( al != null ) {
          //hideStatus();
          viewContents.removeAllComponents();
          viewContents.addComponent( al );
        }
      }
      else {
          updateStatus("Role Composition Visualisation currently unavailable", null);
      }
  }
  
  // IDashIndicatorView --------------------------------------------------------
  @Override
  public String getIndicatorName()
  {
    return "Role Composition Visualisation";
  }
  
  @Override
  public ThemeResource getIndicatorIcon()
  {
    // TODO: Create new icon for this visualisation view
    return ViewResources.CATAPPResInstance.getResource( "RoleCompIcon" );
  }
  
    @Override
    public String getDescription() {
        return "Role composition view. Shows the different roles within a community.";
    }

  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.addStyleName( styleName );
    
    //vl.setSizeUndefined();
    vl.setHeight( "850px" );
    vl.setWidth( "850px" );
    
    createContentDivs();
    //createStatusLabel();
    //createErrorMessageLabel();
    
    // Create the controller; get the view - embed the view later
    smindController = new RoleCompositionController();
  }
  
  private void createContentDivs() {
      statusDiv = new VerticalLayout();
      statusDiv.setHeight("40px");
      
      mainDiv = new VerticalLayout();
      mainDiv.setHeight("810px");

      VerticalLayout vl = (VerticalLayout) viewContents;
      
      vl.addComponent( statusDiv );
      vl.setComponentAlignment( statusDiv, Alignment.TOP_CENTER );
      
      createStatusLabel();
      createErrorMessageLabel();
      
      vl.addComponent( mainDiv );
      vl.setComponentAlignment( mainDiv, Alignment.TOP_CENTER );
  }
    
  private void createStatusLabel() {
      if (status == null)
          status = "Updating view...";
      
      statusLabel = new Label(status);
      statusLabel.addStyleName("medium");
      
      /*
      statusVL = new VerticalLayout();
      statusVL.setHeight("20px");
      statusVL.addComponent(statusLabel);
      statusVL.setComponentAlignment( statusLabel, Alignment.TOP_CENTER );
      */
      
      statusDiv.addComponent(statusLabel);
      statusDiv.setComponentAlignment( statusLabel, Alignment.TOP_CENTER );
      
      /*
      viewContents.addComponent( statusVL );
      
      VerticalLayout vl = (VerticalLayout)viewContents;
      vl.addComponent( statusVL );
      vl.setComponentAlignment( statusVL, Alignment.TOP_CENTER );
      */
      
  }
  
  private void createErrorMessageLabel() {
      if (errorMessage == null)
          errorMessage = "";
      
      errorMessageLabel = new Label(errorMessage);
      errorMessageLabel.addStyleName("medium");
      
      /*
      errorVL = new VerticalLayout();
      errorVL.setHeight("20px");
      errorVL.addComponent(errorMessageLabel);
      errorVL.setComponentAlignment( errorMessageLabel, Alignment.TOP_CENTER );
      */
      
      statusDiv.addComponent(errorMessageLabel);
      statusDiv.setComponentAlignment( errorMessageLabel, Alignment.TOP_CENTER );
      
      /*
      VerticalLayout vl = (VerticalLayout)viewContents;
      vl.addComponent( errorVL );
      vl.setComponentAlignment( errorVL, Alignment.TOP_CENTER );
      */
  }
  
  /*
  private void updateStatus(String status) {
      updateStatus(status, null);
  }
  */
  
  private void updateStatus(String userMessage, String errorMessage) {
      this.status = userMessage;
      this.errorMessage = errorMessage;
      
      /*
      if (statusLabel == null)
          createStatusLabel();
      
      if (errorMessageLabel == null)
          createErrorMessageLabel();
      
      statusLabel.setValue(userMessage);
      statusLabel.setVisible(true);
      
      if (errorMessage != null) {
          errorMessageLabel.setValue(errorMessage);
          errorMessageLabel.setVisible(true);
      }
      else {
          errorMessageLabel.setValue("");
          errorMessageLabel.setVisible(false);
      }
      */
      
      viewContents.removeAllComponents();
      createContentDivs();
  }
  
  private void hideStatus() {
      statusLabel.setVisible(false);
  }
  
    private void updateRoleCompositionView() {
        if (communityID == null) {
            updateStatus("Waiting for community to be monitored...", null);
            return;
        }
        
        updateStatus("Updating view...", null);
        
        if (viewUpdaterThread != null) {
            // Cancel any previous running thread
            viewUpdaterThread.interrupt();
        }
        viewUpdaterThread = new Thread(new ViewUpdaterThread(platformId, communityID, startDate));
        viewUpdaterThread.start();
    }
    
    private IUFView getRoleCompositionView(String platformId, String communityId, Date startDate) {
        IUFView smindView = smindController.getView(platformId, communityId, styleName);
        return smindView;
    }

    private class ViewUpdaterThread implements Runnable {
        private final String platformId;
        private final String communityId;
        private final Date startDate;

        private ViewUpdaterThread(String platformId, String communityId, Date startDate) {
            this.platformId = platformId;
            this.communityId = communityId;
            this.startDate = startDate;
        }

        @Override
        public void run() {
            try {
                int maxUsers = 10; //TODO: where to get this?
                
                try {
                    //attempt to get users for platform/community
                    // if this fails then the subsequent getRoleCompositionView() call will also fail,
                    // however error handling is buggy!
                    roleCompWrapperController.getBehaviourAnalysisService().getUserIDs(platformId, communityID, maxUsers);
                }
                catch (Exception e) {
                    String errorMessage = (e.getMessage() != null ? e.getMessage() : "unknown");
                    String userMessage = "Behaviour analysis service failed to get users for platform: " + platformId + ", community: " + communityID;
                    updateStatus(userMessage, "Remote error: " + errorMessage);
                    logger.error(userMessage + ": " + errorMessage, e);
                    return;
                }
                
                IUFView smindView = getRoleCompositionView(platformId, communityId, startDate);
                if (! Thread.currentThread().isInterrupted()) {
                    updateView(smindView);
                }
            }
            catch (Exception e) {
                if (! Thread.currentThread().isInterrupted()) {
                    String message = (e.getMessage() != null ? e.getMessage() : "internal error");
                    updateStatus("Role Composition Visualisation currently unavailable: " + message, null);
                }
				logger.error("Exception thrown from Role Composition Visualisation: " + e, e);
            }
        }

        
    }
  
}
