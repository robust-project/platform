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
//      Created Date :          2011-10-27
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.IProcessRender;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.RiskEditorMainController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.*;

import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.*;

import com.vaadin.ui.*;
import com.vaadin.data.Property;

import java.util.*;
import java.io.InputStream;





public class RiskEditTreatmentView extends WindowView
{
  private RiskEditorMainController reController;
  
  private Tree              repTreatmentTree;
  private ListSelect        selTemplateList;
  private Label             treatmentDescription;
  private Button            addTreatmentButton;
  private StreamedImageView bpmnView;
 
  private List<UUIDItem> orderedTemplateItems;
  private UUIDItem       currRepItem;
  private UUIDItem       currOrderedItem;
  
  private transient TreatmentViewCD changeData;
  
  
  public RiskEditTreatmentView( Component parent, RiskEditorMainController ctrl )
  {
    super( parent, "" );
    
    reController = ctrl;
    
    // Size & position window
    window.setWidth( "550px" );
    window.setHeight( "500px" );
    centreWindow();
    
    changeData = new TreatmentViewCD();
    orderedTemplateItems = new LinkedList<UUIDItem>();
    
    createComponents();
  }
  
  @Override
  public void updateView()
  {
    setHeadline( "Treatment selection: " + reController.getCurrentROTitle() );
    
    changeData.reset();
    updateComponents();
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) window.getContent();
    
    // Headline
    vl.addComponent( createHeadline("Unknown r/o") );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("18px", null) );
    
    // Treatement selection components
    vl.addComponent( createTreatmentSelectComps() );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Treatment description components
    vl.addComponent( createTreatmentDescriptionComps() );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );    
    
    // Save/discard buttons
    HorizontalLayout hl = new HorizontalLayout();
    vl.addComponent( hl );
    vl.setComponentAlignment( hl, Alignment.BOTTOM_RIGHT );
    
    Button button = new Button( "OK" );
    button.setDescription("Assign this treatment to current risk or opportunity");
    button.addListener( new OKClickListener() );
    hl.addComponent( button );
    hl.setComponentAlignment( button, Alignment.BOTTOM_RIGHT );
    
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    button = new Button( "Cancel" );
    button.addListener( new CancelClickListener() );
    hl.addComponent( button );
    hl.setComponentAlignment( button, Alignment.BOTTOM_RIGHT );
    
    // Spacer
    hl.addComponent( UILayoutUtil.createSpace("8px", null, true) );
  }
  
  private Component createTreatmentSelectComps()
  {
    HorizontalLayout hl = new HorizontalLayout();
    
    // Treatment browse components
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth( "230px" );
    hl.addComponent( vl );
    
    Label label = new Label( "Treatments available" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    VerticalLayout treeVL = new VerticalLayout();
    treeVL.setWidth( "98%" );
    treeVL.addStyleName( "catBorder" );
    vl.addComponent( treeVL );
    
    repTreatmentTree = new Tree();
    repTreatmentTree.setHeight( "150px" );
    repTreatmentTree.setImmediate( true );
    repTreatmentTree.addListener( new RepTreatmentSelectListener() );
    treeVL.addComponent( repTreatmentTree );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    // Arrow controls
    hl.addComponent( createArrowControls() );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    // Selected treatment list
    VerticalLayout treatVL = new VerticalLayout();
    hl.addComponent( treatVL );
    
    label = new Label( "Selected treatments" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    treatVL.addComponent( label );
    
    // Space
    treatVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    selTemplateList = new ListSelect();
    selTemplateList.setWidth( "245px" );
    selTemplateList.setHeight( "130px" );
    selTemplateList.setImmediate( true );
    selTemplateList.addListener( new OrderedTreatmentSelectListener() );
    treatVL.addComponent( selTemplateList );
    
    // Space
    treatVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Treatment buttons
    HorizontalLayout butHL = new HorizontalLayout();
    treatVL.addComponent( butHL );
    treatVL.setComponentAlignment( butHL, Alignment.MIDDLE_RIGHT );
    
    Button button = new Button( "Remove" );
    button.setWidth( "80px" );
    button.setStyleName( "small" );
    button.setImmediate( true );
    button.addListener( new RemoveClickListener() );
    butHL.addComponent( button );
    
    // Space
    butHL.addComponent( UILayoutUtil.createSpace("2px", null, true) );
    
    button = new Button( "Up" );
    button.setWidth( "80px" );
    button.addStyleName( "small" );
    button.setImmediate( true );
    button.addListener( new UpClickListener() );
    butHL.addComponent( button );
    
    // Space
    butHL.addComponent( UILayoutUtil.createSpace("2px", null, true) );
    
    button = new Button( "Down" );
    button.setWidth( "80px" );
    button.addStyleName( "small" );
    button.setImmediate( true );
    button.addListener( new DownClickListener() );
    butHL.addComponent( button );
    
    return hl;
  }
  
  private Component createArrowControls()
  {
    VerticalLayout vl = new VerticalLayout();
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("30px", null) );
    
    addTreatmentButton = new Button();
    addTreatmentButton.addStyleName( "small" );
    addTreatmentButton.addStyleName( "icon-on-top" );
    addTreatmentButton.setIcon( ViewResources.CATAPPResInstance.getResource( "16x16RightArrow" ) );
    addTreatmentButton.setEnabled( false );
    addTreatmentButton.setImmediate( true );
    addTreatmentButton.addListener( new RightClickListener() );
    vl.addComponent( addTreatmentButton );
    
    return vl;
  }
  
  private Component createTreatmentDescriptionComps()
  {
    HorizontalLayout hl = new HorizontalLayout();
    
    // Treatment description
    VerticalLayout descVL = new VerticalLayout();
    hl.addComponent( descVL );
    
    // Label
    Label label = new Label( "Description of treatment" );
    label.addStyleName( "catSubSectionFont" );
    label.addStyleName( "catBlueDark" );
    descVL.addComponent( label );
    
    // Space
    descVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Description
    treatmentDescription = new Label();
    treatmentDescription.addStyleName( "catBorder" );
    treatmentDescription.addStyleName( "small" );
    treatmentDescription.setWidth( "230px" );
    treatmentDescription.setHeight( "162px" );
    descVL.addComponent( treatmentDescription );
    
    // Spaces
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    VerticalLayout bpmnVL = new VerticalLayout();
    hl.addComponent( bpmnVL );
    
    bpmnVL.addComponent( UILayoutUtil.createSpace("16px", null) );
    
    // BPMN holder and view
    Panel bpmnHolder = new Panel();
    bpmnVL.addComponent( bpmnHolder );
    bpmnHolder.addStyleName( "catBorder" );
    bpmnHolder.setScrollable( true );
    bpmnHolder.setWidth( "292px" );
    bpmnHolder.setHeight( "158px" );
    bpmnHolder.setImmediate( true );
    bpmnHolder.getContent().setSizeUndefined();
    
    bpmnView = new StreamedImageView();
    bpmnHolder.addComponent( (Component) bpmnView.getImplContainer() );
    
    return hl;
  }
  
  private void updateComponents()
  {
    Map<UUID, TreatmentTemplate> knownTemplates = reController.getRepositoryTreatmentTemplates();
    
    repTreatmentTree.removeAllItems();
    selTemplateList.removeAllItems();
    orderedTemplateItems.clear();
    currRepItem = null;
    currOrderedItem = null;
    
    for ( TreatmentTemplate template : knownTemplates.values() )
    {
      UUIDItem ti = new UUIDItem( template.getTitle(), template.getID(), template );
      repTreatmentTree.addItem( ti );
      repTreatmentTree.setChildrenAllowed( ti, false );
    }
    
    // Go through the list of current treatments for this risk and match against
    // the known templates we currently have
    List<Treatment> roTreatments = reController.getCurrentROTreatments();
    Iterator<Treatment> tIT = roTreatments.iterator();
    
    while ( tIT.hasNext() )
    {
      Treatment tmt = tIT.next();
      TreatmentTemplate template = knownTemplates.get( tmt.getActivitiProcResourceID() ); 
      
      if ( template !=  null )
      {
        UUIDItem ti = new UUIDItem( template.getTitle(),
                                    template.getID(),
                                    template );
        
        selTemplateList.addItem( ti );
        orderedTemplateItems.add( ti );
      }
    }
      
  }
 
  private void updateTreatmentSummaryInfo( TreatmentTemplate tt )
  {
    treatmentDescription.setValue( tt.getDescription() );
    
    IProcessRender render = reController.getTreatmentTemplateRender( tt );
    if ( render != null )
    {
      bpmnView.updateImage( (InputStream) render.getImpl(),
                                          render.getType() );
    }
  }
  
  private void refreshSelTreatmentList()
  {
    selTemplateList.removeAllItems();
    
    for ( UUIDItem item : orderedTemplateItems )
      selTemplateList.addItem( item );
  }
  
  private void updateChangeData()
  {
    // Always do an update (order maintenance is easiest this way)
    changeData.reset();
    changeData.forceChanged(true); // this would not be set below, if no templates in list
    float index = 1.0f;
    
    Iterator<UUIDItem> itIt = orderedTemplateItems.iterator();
    while ( itIt.hasNext() )
    {
      TreatmentTemplate tt = (TreatmentTemplate) itIt.next().getData();
      changeData.addTreatmentTemplate( tt, index );
      index++;
    }
  }
  
  // Private internal event handling -------------------------------------------
  private void onCancelCommit()
  { setVisible( false ); }
  
  private void onCommitLocalChanges()
  {
    updateChangeData();
    
    Collection<RiskEditCommitListener> listeners = getListenersByType();
    
    for ( RiskEditCommitListener listener : listeners )
      listener.onCommitTreatmentChanges( changeData );
    
    setVisible( false );
  }
  
  private void onRepTreatmentSelect( UUIDItem item )
  {
    if ( item != null )
    {
      currRepItem = item;
      addTreatmentButton.setEnabled( true );

      TreatmentTemplate tt = (TreatmentTemplate) currRepItem.getData();
      updateTreatmentSummaryInfo( tt );
    }
  }
  
  private void onOrderedTreatmentSelect( UUIDItem item )
  {
    if ( item != null )
    {
      currOrderedItem = item;
      addTreatmentButton.setEnabled( false );

      TreatmentTemplate tt = (TreatmentTemplate) currOrderedItem.getData();
      updateTreatmentSummaryInfo( tt );
    }
  }
  
  private void onAddTreatmentClick()
  {
    if ( currRepItem != null )
    {
      if ( !orderedTemplateItems.contains(currRepItem) )
      {
        selTemplateList.addItem( currRepItem );
        orderedTemplateItems.add( currRepItem );

        changeData.forceChanged( true );
      }
    }
  }
  
  private void onRemoveTreatmentClick()
  {
    if ( currOrderedItem != null )
    {
      selTemplateList.removeItem( currOrderedItem );
      orderedTemplateItems.remove( currOrderedItem );
      currOrderedItem = null;
      
      changeData.forceChanged( true );
    }
  }
  
  private void onUpClick()
  {
    if ( currOrderedItem != null && orderedTemplateItems.size() > 1 )
    {
      int index = orderedTemplateItems.indexOf( currOrderedItem );
      if ( index > 0 )
      {
        orderedTemplateItems.remove( currOrderedItem );
        orderedTemplateItems.add( index -1, currOrderedItem );
        
        refreshSelTreatmentList();
        changeData.forceChanged( true );
      }
    }
  }
  
  private void onDownClick()
  {
    if ( currOrderedItem != null && orderedTemplateItems.size() > 1 )
    {
      int index = orderedTemplateItems.indexOf( currOrderedItem );
      if ( index < (orderedTemplateItems.size() -1) )
      {
        orderedTemplateItems.remove( currOrderedItem );
        orderedTemplateItems.add( index +1, currOrderedItem );
        
        refreshSelTreatmentList();
        changeData.forceChanged( true );
      }
    }
  }
  
  private class CancelClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onCancelCommit(); }
  }
  
  private class OKClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onCommitLocalChanges(); }
  }
  
  private class RepTreatmentSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onRepTreatmentSelect( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class OrderedTreatmentSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onOrderedTreatmentSelect( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class RightClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onAddTreatmentClick(); }
  }
  
  private class RemoveClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onRemoveTreatmentClick(); }
  }
  
  private class UpClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onUpClick(); }
  }
  
  private class DownClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onDownClick(); }
  }
}