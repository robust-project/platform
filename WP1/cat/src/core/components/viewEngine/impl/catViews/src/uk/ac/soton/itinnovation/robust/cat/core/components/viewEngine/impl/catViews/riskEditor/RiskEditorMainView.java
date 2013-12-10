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
//      Created Date :          20 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor;

import com.vaadin.data.Property;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.SimpleView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.RiskEditorMainViewListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViews.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;

import com.vaadin.ui.*;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;

import java.util.*;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;




class RiskEditorMainView extends SimpleView
{
  private RiskEditorMainController reController;
  
  private VerticalLayout     browseViewHolder;
  private Tree               roRepTree;
  private RiskEditBrowseView browseView;
  private RiskEditCreateView createView;
  private Button             deleteRiskButton;
  
  private Map<UUID, String> roNameIDs;
  private boolean           resetViewPending = false;
  private boolean selectingRO = false;
  private boolean updatingRepTreeView = false;
  private boolean removingRepTreeViewItems = false;
  private boolean updatingView = false;
  
  private Risk currRO;

  public Risk getCurrRO() {
      return currRO;
  }

  public void setCurrRO(Risk currRO) {
      this.currRO = currRO;
  }
  
  public boolean isUpdatingView() {
      return updatingView;
  }
  
  public void resetView()
  {
    if (updatingView)
      return;
    
    updatingView = true;
      
    updateRepTreeView();
    browseView.resetBrowseView();
    
    updatingView = false;
  }
  
  @Override
  public void updateView()
  {
    if (updatingView)
        return;
    
    updatingView = true;
               
    roNameIDs = reController.getCommunityRONames();
 
    updateRepTreeView();
    browseView.updateView();
    
    updatingView = false;
  }
  
  // Protected methods ---------------------------------------------------------
  protected RiskEditBrowseView getBrowseView() { return browseView; }
  
  protected RiskEditCreateView getCreateView() { return createView; }
  
  protected RiskEditorMainView( RiskEditorMainController ctrl )
  {
    super( true );
    
    reController = ctrl;
    viewContents.setWidth( "956px" );
    
    createComponents();
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    // Horizontal layout
    HorizontalLayout hl = new HorizontalLayout();
    hl.addStyleName( "catBkgndLight" );
    viewContents.addComponent(hl);
   
    // Risks/Opportunities repository panel
    Panel roPanel = createROPanel();
    roPanel.setStyleName( "borderless light" );
    hl.addComponent( roPanel );
    hl.setComponentAlignment( roPanel, Alignment.TOP_LEFT );
    
    // Vertical rule
    Component ruler = UILayoutUtil.createSpace("4px", "catVertRule", true);
    ruler.setHeight( "100%" );
    hl.addComponent( ruler );
    
    // Risk view holder
    browseViewHolder = new VerticalLayout();
    browseViewHolder.setSizeFull();
    hl.addComponent( browseViewHolder );
    hl.setComponentAlignment( browseViewHolder, Alignment.TOP_CENTER );
    
    // Risk browse view
    browseView = new RiskEditBrowseView( reController );
    browseView.addListener( reController );
    browseViewHolder.addComponent( (Component) browseView.getImplContainer() );
  }
  
  private Panel createROPanel()
  {
    Panel roPanel = new Panel( "R&O repository" );
    roPanel.setStyleName( "borderless light" );
    VerticalLayout vl = (VerticalLayout) roPanel.getContent();
    vl.setStyleName( "catBkgnd" );
    
    String fixedWidth = "200px";
    
    // Title
    Label label = new Label( "Current R&Os" );
    label.addStyleName( "catSectionFont" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
            
    // R&O repository container & tree
    VerticalLayout treeVl = new VerticalLayout();
    treeVl.addStyleName( "catBorder" );
    treeVl.setWidth( fixedWidth );
    vl.addComponent( treeVl );
 
    roRepTree = new Tree();
    roRepTree.setHeight( "350px" );
    roRepTree.addListener( new ROSelectListener() );
    treeVl.addComponent( roRepTree );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // R&O item controls
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth( fixedWidth );
    vl.addComponent( hl );
    
    Button button = new Button( "Create new R/O.." );
    button.setWidth( "100%" );
    button.addListener( new CreateROClickListener() );
    hl.addComponent( button );
   
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Review risk/opp button
    hl = new HorizontalLayout();
    hl.setSpacing( true );
    vl.addComponent( hl );
    
    button = new Button( "Review R/O" );
    button.setEnabled( false );
    hl.addComponent( button );
    hl.setExpandRatio( button, 1 );
    
    // Remove button
    deleteRiskButton = new Button( "Remove R/O" );
    deleteRiskButton.addListener( new DeleteROClickListener() );
    deleteRiskButton.setEnabled( false );
    hl.addComponent( deleteRiskButton );
    hl.setExpandRatio( deleteRiskButton, 1 );
    
    // Space & ruler
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    Component ruler = UILayoutUtil.createSpace( "4px", "catHorizRule", false );
    ruler.setWidth( fixedWidth );
    vl.addComponent( ruler );
    
    // Group buttons
    hl = new HorizontalLayout();
    hl.setSpacing( true );
    vl.addComponent( hl );
    
    button = new Button( "Add group" );
    button.setEnabled( false );
    hl.addComponent( button );
    hl.setExpandRatio( button, 1 );
    
    button = new Button( "Remove group" );
    button.setEnabled( false );
    hl.addComponent( button );
    hl.setExpandRatio( button, 1 );
   
    return roPanel;
  }
  
  private void updateRepTreeView()
  {
    //if ( roNameIDs != null && (! updatingRepTreeView))
    if ( roNameIDs != null )
    { 
      //updatingRepTreeView = true;
      
      //selectingRO = true; // disable ROSelectListener in RiskEditorMainView whilst removing items
      //removingRepTreeViewItems = true;
      roRepTree.removeAllItems();
      //removingRepTreeViewItems = false;
      //selectingRO = false;
      
      for ( UUID id : roNameIDs.keySet() )
      {
        UUIDItem it = new UUIDItem( roNameIDs.get(id), id);
        roRepTree.addItem( it );
        roRepTree.setChildrenAllowed( it, false );
      }
      
      if (currRO != null)
        onROSelected(currRO.getId());
      
      //updatingRepTreeView = false;
    }
  }
 
  // Internal event handlers ---------------------------------------------------
  private void onCreateROClicked()
  {
    if ( createView == null )
    {
      createView = new RiskEditCreateView( viewContents );
      
      // Use dummy data for now
      ArrayList<String> list = new ArrayList<String>();
      list.add( "Default group" );
      createView.setGroups( list );
      
      list = new ArrayList<String>();
      list.add( "Risk manager" );
      createView.setOwners( list );
      
      createView.addListener( reController );
    }
    else
      createView.setVisible( true );
  }
  
  private void onDeleteROClicked()
  {
    // TO DO: confirmation dialog box
    if ( reController.currentROSelected() )
    {
      Collection<RiskEditorMainViewListener> listeners = getListenersByType();
      
      for ( RiskEditorMainViewListener listener : listeners )
        listener.onDeleteCurrentRisk();
    }
  }
  
  public void onROSelected(UUID targetID) {
      if (targetID == null) {
          roRepTree.select(null);
      }
      else {
          for (Object riskID : roRepTree.getItemIds()) {
              UUIDItem riskItem = (UUIDItem) riskID;
              if (riskItem.getID().equals(targetID)) {
                  roRepTree.select(riskID);
              }
          }
      }
  }
  
  private void onROSelected( UUIDItem item )
  {
    if (selectingRO || removingRepTreeViewItems || updatingView)
        return;
    
    UUID uuid = null;
    
    if (item != null) {
        uuid = item.getID();
    }

    //selectingRO = true; // prevent any other selection events until this one handled
    reController.selectRiskByID( uuid );
    //selectingRO = false;
    
    if ( reController.currentROSelected() )
    {
      deleteRiskButton.setEnabled( true );
      browseView.updateView();
      browseView.setDataChanged(false);
    }
  }
  
  private class CreateROClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onCreateROClicked(); }
  }
  
  private class DeleteROClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onDeleteROClicked(); }
  }
  
  /*
  private class ROSelectListener implements ItemClickListener
  {
    @Override
    public void itemClick( ItemClickEvent event)
    { onROSelected( (UUIDItem) event.getItemId() ); }
  }
  */
  
  private class ROSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onROSelected( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
}