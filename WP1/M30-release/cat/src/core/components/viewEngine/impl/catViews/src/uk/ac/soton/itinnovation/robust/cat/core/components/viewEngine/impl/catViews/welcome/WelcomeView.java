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
//      Created Date :          20 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome;


import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.welcome.IWelcomeView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.SimpleView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;

import com.vaadin.ui.*;
import com.vaadin.data.Property;

import java.util.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViews.CommunityCreateView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViews.CommunityObjectivesView;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;


class WelcomeView extends SimpleView implements IWelcomeView
{
    private WelcomeController welcomeController;
    
  private ListSelect communitiesList;
  private Label      currentSelectionLabel;
  private UUIDItem   currSelectedCommunity;
  private CommunityCreateView communityCreateView;
  private CommunityObjectivesView communityObjectivesView;
  
    private Button monitorButton;
    private Button removeButton;
    private Button editCommunityObjectivesButton;
    
    public void setCommunityNames(Set<Community> communities) {
        communitiesList.removeAllItems();

        for (Community community : communities) {
            UUIDItem item = new UUIDItem(community.getName(), community.getUuid());
            communitiesList.addItem(item);
        }
    }
  
  // IWelcomeView --------------------------------------------------------------
  // TO DO: interesting view methods here
  
  // Protected methods ---------------------------------------------------------
  protected WelcomeView(WelcomeController ctrl)
  {
    super();
    
    welcomeController = ctrl;
    createComponents();
  }
  
  private boolean isPublicDemo()
  {
      return welcomeController.isPublicDemo();
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.addStyleName( "catBkgndLight" );
    vl.setHeight( "100%" );
    
    // Headroom
    vl.addComponent( UILayoutUtil.createSpace("16px", null) );
    
    Panel ip = createInternalPanel();
    vl.addComponent( ip );
    vl.setComponentAlignment( ip, Alignment.TOP_CENTER );
    
    // Spacing
    Component space = UILayoutUtil.createSpace("16px", null);
    vl.addComponent( space );
  }
  
  private Panel createInternalPanel()
  {
    Panel internalPanel = new Panel();
    VerticalLayout vl = (VerticalLayout) internalPanel.getContent();
    internalPanel.setWidth( "600px" );
    vl.setStyleName( "catBkgnd" );
    
    // Headroom
    vl.addComponent( UILayoutUtil.createSpace("48", null) );
    
    // Welcome label
    Label welcomeLabel = new Label( "Welcome to the ROBUST Community Analysis Tool" );
    welcomeLabel.addStyleName( "catHeadlineFont" );
    welcomeLabel.addStyleName( "catTextAlignCentre" );
    welcomeLabel.setReadOnly( true );
    vl.addComponent( welcomeLabel );
    vl.setComponentAlignment( welcomeLabel, Alignment.MIDDLE_CENTER );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("32", null) );
    
    // Community list
    communitiesList = new ListSelect( "Please select an on-line community" );
    communitiesList.setWidth( "300px" );
    communitiesList.setHeight( "200px" );
    communitiesList.setImmediate( true );
    communitiesList.addListener( new CommunitySelectListener() );
    vl.addComponent( communitiesList );
    vl.setComponentAlignment( communitiesList, Alignment.MIDDLE_CENTER );
    
    vl.addComponent( UILayoutUtil.createSpace("4", null) );
    
    // Add/remove controls
    HorizontalLayout hl = new HorizontalLayout();
    vl.addComponent( hl );
    vl.setComponentAlignment( hl, Alignment.MIDDLE_CENTER );
    
    Button button = new Button( "Add community" );
    button.addListener(new AddCommunityButtonListener());
    button.setEnabled( ! isPublicDemo() );
    hl.addComponent( button );
    hl.setExpandRatio( button, 0.5f );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4", null, true) );
    
    removeButton = new Button( "Remove community" );
    removeButton.addListener(new RemoveCommunityButtonListener());
    removeButton.setEnabled( false );
    hl.addComponent( removeButton );
    hl.setExpandRatio( removeButton, 0.5f );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4", null, true) );

    // Edit community objectives
    editCommunityObjectivesButton = new Button( "Edit objectives" );
    editCommunityObjectivesButton.addListener(new EditCommunityObjectivesButtonListener());
    editCommunityObjectivesButton.setEnabled( false );
    hl.addComponent( editCommunityObjectivesButton );
    hl.setExpandRatio( removeButton, 0.5f );
    
    // Selection and monitor control
    VerticalLayout selVL = new VerticalLayout();
    selVL.setWidth( "300px" );
    vl.addComponent( selVL );
    vl.setComponentAlignment( selVL, Alignment.MIDDLE_CENTER );
    
    currentSelectionLabel = new Label( "" );
    currentSelectionLabel.addStyleName( "catSubHeadlineFont" );
    currentSelectionLabel.addStyleName( "catTextAlignCentre" );
    currentSelectionLabel.setHeight( "40px" );
    selVL.addComponent( currentSelectionLabel );
    selVL.setComponentAlignment( currentSelectionLabel, Alignment.MIDDLE_LEFT );
    
    // Space
    selVL.addComponent( UILayoutUtil.createSpace("4", null) );
    
    HorizontalLayout butHL = new HorizontalLayout();
    selVL.addComponent( butHL );
    selVL.setComponentAlignment( butHL, Alignment.BOTTOM_RIGHT );
    
    monitorButton = new Button( "Monitor" );
    monitorButton.addListener( new MonitorButtonListener() );
    monitorButton.setEnabled(false);
    butHL.addComponent( monitorButton );
    butHL.setComponentAlignment( monitorButton, Alignment.MIDDLE_RIGHT );
    
    // Space
    butHL.addComponent( UILayoutUtil.createSpace("4", null, true) );
    
    button = new Button( "Refresh list" );
    button.addListener( new RefreshButtonListener() );
    butHL.addComponent( button );
    butHL.setComponentAlignment( button, Alignment.MIDDLE_RIGHT );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("64", null) );
    
    return internalPanel;
  }
  
  // Internal event listeners --------------------------------------------------
  private void onCommunitySelectChange( UUIDItem item )
  {
      if ( item != null ) {
          currSelectedCommunity = item;
          monitorButton.setEnabled(true);
          removeButton.setEnabled( ! isPublicDemo() ); // only enable for public demo
          editCommunityObjectivesButton.setEnabled( true);
      }
      else {
          currSelectedCommunity = null;
          monitorButton.setEnabled(false);
          removeButton.setEnabled(false);
          editCommunityObjectivesButton.setEnabled(false);
      }
  }
  
  private void onMonitorClick()
  {
    String currCommunity = (String) currentSelectionLabel.getValue();
    
    if ( currSelectedCommunity != null &&
         !currSelectedCommunity.getLabel().equals("")  )
    {
      Collection<WelcomeViewListener> listeners = getListenersByType();
    
      for( WelcomeViewListener listener : listeners )
        listener.onMonitorCommunity( currSelectedCommunity.getID() ); 
    }
  }
  
  private void onRefreshClick()
  {
    Collection<WelcomeViewListener> listeners = getListenersByType();
    
    for( WelcomeViewListener listener : listeners )
      listener.onRefreshCommunityList();
  }
  
  private void onAddCommunityClick()
  {
      communityCreateView = new CommunityCreateView(viewContents);
      communityCreateView.addListener(welcomeController);
  }
  
  private void onRemoveCommunityClick()
  {
      if (currSelectedCommunity != null
              && !currSelectedCommunity.getLabel().equals("")) {
          
          //TODO - pop up confirmation dialog
          
          Collection<WelcomeViewListener> listeners = getListenersByType();

          for (WelcomeViewListener listener : listeners) {
              listener.onRemoveCommunity( currSelectedCommunity.getID() );
          }
          
          //currSelectedCommunity = null; //not required, as this is set via the CommunitySelectListener 
      }
  }
          
  private void onEditCommunityObjectivesClick()
  {
      if (currSelectedCommunity != null
              && !currSelectedCommunity.getLabel().equals("")) {
          
          communityObjectivesView = new CommunityObjectivesView(viewContents, welcomeController);
          communityObjectivesView.addListener(welcomeController);
          communityObjectivesView.updateView();
          
          Collection<WelcomeViewListener> listeners = getListenersByType();

          for (WelcomeViewListener listener : listeners) {
              listener.onEditCommunityObjectives( currSelectedCommunity.getID() );
          }
          
      }
  }
  
  public void onCommunityDataUpdated()
  {
      if (communityObjectivesView != null)
          communityObjectivesView.updateView();
  }

  private class CommunitySelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onCommunitySelectChange( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class MonitorButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent ce) { onMonitorClick(); }
  }
  
  public class RefreshButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent ce) { onRefreshClick(); }
  }

  public class AddCommunityButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent ce) { onAddCommunityClick(); }
  }

  public class RemoveCommunityButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent ce) { onRemoveCommunityClick(); }
  }
  
    public class EditCommunityObjectivesButtonListener implements Button.ClickListener
    {
        @Override
        public void buttonClick(Button.ClickEvent ce) { onEditCommunityObjectivesClick(); }
    }

}