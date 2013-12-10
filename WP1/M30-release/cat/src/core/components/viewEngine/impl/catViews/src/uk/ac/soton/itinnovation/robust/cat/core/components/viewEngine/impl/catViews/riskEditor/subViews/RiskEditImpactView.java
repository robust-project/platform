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
//      Created Date :          24 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.RiskEditorMainController;

import uk.ac.soton.itinnovation.robust.riskmodel.*;

import com.vaadin.ui.*;
import com.vaadin.data.Property;
import java.util.*;





public class RiskEditImpactView extends WindowView
                                implements ComponentSelectedListener,
                                           ComponentValueChangeListener
{
  private RiskEditorMainController reController;
  
  private ListSelect    commObjTitleList;
  private Label         commObjSummary;
  private Button        leftButton, rightButton;
  private Panel         impactPanel;
  private UUIDItem      currTitleListItem;
  private GraphicSlider currGfxSlider;
  
  private HashMap<UUID, UUIDItem>      currObjectives;
  private HashMap<UUID, GraphicSlider> currImpactSliders;
  private String[] impactLabels = { "-v high", "-high", "-medium", "-low", "-v low",
                                    "+v low", "+low", "+medium", "+high", "+v high" };
    
  private transient ImpactViewCD changeData;
  
  
  public RiskEditImpactView( Component parent, RiskEditorMainController ctrl )
  {
    super( parent, "" );
    
    reController = ctrl;
    
    // Size & position window
    window.setWidth( "530px" );
    window.setHeight( "520px" );
    centreWindow();
    
    currObjectives = new HashMap<UUID, UUIDItem>();
    currImpactSliders = new HashMap<UUID, GraphicSlider>();
    changeData = new ImpactViewCD();
    
    createComponents();
  }
  
  @Override
  public void updateView()
  {
    setHeadline( "Impacts: " + reController.getCurrentROTitle() );
    
    changeData.reset();
    updateComponents();
  }
  
  // CAT UI Component listeners ------------------------------------------------
  @Override
  public void onCATCompSelected( IUFView viewComp )
  {
    if ( currGfxSlider !=  null ) currGfxSlider.setIsSelected( false );
    
    currGfxSlider = (GraphicSlider) viewComp;
    
    leftButton.setEnabled( true );
    rightButton.setEnabled( false );
    
    Map<UUID, Objective> commObjectives = reController.getAllCommunityObjectives();
    Objective targObj = commObjectives.get( currGfxSlider.getDataID() );
    commObjSummary.setValue( targObj.getDescription() );
  }
  
  @Override
  public void onCATCompValueChanged( IUFView viewComp )
  {
    GraphicSlider gs = (GraphicSlider) viewComp;
    changeData.modifyImpact( gs.getDataID(), getImpactLevel(gs) );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) window.getContent();
    
    // Headline
    vl.addComponent( createHeadline("Unknown r/o") );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("24px", null) );
    
    // Main controls
    vl.addComponent( createMainControls() );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    // Undo/use buttons
    HorizontalLayout hl = new HorizontalLayout();
    vl.addComponent( hl );
    vl.setComponentAlignment( hl, Alignment.BOTTOM_RIGHT );
    
    Button button = new Button( "OK" );
    button.addListener( new OKClickListener() );
    hl.addComponent( button );
    hl.setComponentAlignment( button, Alignment.BOTTOM_RIGHT );
    
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    button = new Button( "Cancel" );
    button.addListener( new CancelClickListener() );
    hl.addComponent( button );
    hl.setComponentAlignment( button, Alignment.BOTTOM_RIGHT );
  }
  
  private Component createMainControls()
  {
    HorizontalLayout hl = new HorizontalLayout();
    
    // Community objectives controls
    hl.addComponent( createCommObjControls() );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    // Arrow controls
    hl.addComponent( createArrowControls() );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    // Impact controls
    hl.addComponent( createImpactControls() );
    
    return hl;
  }
  
  private Component createCommObjControls()
  {
    // Objectives controls
    VerticalLayout vl = new VerticalLayout();
    
    // Label
    Label label = new Label( "Community objectives" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlueDark" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Objectives list
    commObjTitleList = new ListSelect( "Current objectives" );
    commObjTitleList.setWidth( "200px" );
    commObjTitleList.setHeight( "250px" );
    commObjTitleList.addListener( new ObjectiveSelectListener() );
    commObjTitleList.setImmediate( true );
    vl.addComponent( commObjTitleList );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Objectives description
    label = new Label( "Objective summary" );
    label.addStyleName( "catSubSectionFont" );
    label.addStyleName( "catBlueDark" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    commObjSummary = new Label();
    commObjSummary.setWidth( "200px" );
    commObjSummary.setHeight( "50px" );
    commObjSummary.addStyleName( "catBorder" );
    vl.addComponent( commObjSummary );
    
    return vl;
  }
  
  private Component createArrowControls()
  {
    VerticalLayout vl = new VerticalLayout();
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("40px", null) );
    
    // Buttons
    rightButton = new Button();
    rightButton.addStyleName( "small" );
    rightButton.addStyleName( "icon-on-top" );
    rightButton.setIcon( ViewResources.CATAPPResInstance.getResource( "16x16RightArrow" ) );
    rightButton.addListener( new MoveRightClickListener() );
    rightButton.setEnabled( false );
    vl.addComponent( rightButton );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    leftButton = new Button();
    leftButton.addStyleName( "small" );
    leftButton.setIcon( ViewResources.CATAPPResInstance.getResource( "16x16LeftArrow" ) );
    leftButton.addStyleName( "icon-on-top" );
    leftButton.addListener( new MoveLeftClickListener() );
    leftButton.setEnabled( false );
    vl.addComponent( leftButton );
   
    return vl;
  }
  
  private Component createImpactControls()
  {
    VerticalLayout vl = new VerticalLayout();
    
    // Label
    Label label = new Label( "Current impacts" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("22px", null) );
    
    // Impact panel
    impactPanel = new Panel();
    impactPanel.setStyleName( "borderless light" );
    impactPanel.addStyleName( "catBorder" );
    impactPanel.setWidth( "256px" );
    impactPanel.setHeight( "320px" );
    VerticalLayout ipvl = (VerticalLayout) impactPanel.getContent();
    ipvl.setMargin( false );
    vl.addComponent( impactPanel );
    
    return vl;
  }
  
  private void addGfxSlider( Objective obj, ImpactViewCD.eImpact impact )
  {
    GraphicSlider gs = new GraphicSlider( obj.getTitle(), obj.getIdAsUUID() );
    
    gs.addItem( impactLabels[0], ViewResources.CATAPPResInstance.getResource( "veryHighNegImpactArrow" ));
    gs.addItem( impactLabels[1], ViewResources.CATAPPResInstance.getResource( "highNegImpactArrow" ));
    gs.addItem( impactLabels[2], ViewResources.CATAPPResInstance.getResource( "mediumNegImpactArrow" ));
    gs.addItem( impactLabels[3], ViewResources.CATAPPResInstance.getResource( "lowNegImpactArrow" ));
    gs.addItem( impactLabels[4], ViewResources.CATAPPResInstance.getResource( "veryLowNegImpactArrow" ));
    
    gs.addItem( impactLabels[5], ViewResources.CATAPPResInstance.getResource( "veryLowPosImpactArrow" ));
    gs.addItem( impactLabels[6], ViewResources.CATAPPResInstance.getResource( "lowPosImpactArrow" ));
    gs.addItem( impactLabels[7], ViewResources.CATAPPResInstance.getResource( "mediumPosImpactArrow" ));
    gs.addItem( impactLabels[8], ViewResources.CATAPPResInstance.getResource( "highPosImpactArrow" ));
    gs.addItem( impactLabels[9], ViewResources.CATAPPResInstance.getResource( "veryHighPosImpactArrow" ));
    gs.addListener( this );
    
    switch ( impact )
    {
      case NEG_VHIGH  : gs.setCurrentItem( impactLabels[0] ); break;
      case NEG_HIGH   : gs.setCurrentItem( impactLabels[1] ); break;
      case NEG_MEDIUM : gs.setCurrentItem( impactLabels[2] ); break;
      case NEG_LOW    : gs.setCurrentItem( impactLabels[3] ); break;
      case NEG_VLOW   : gs.setCurrentItem( impactLabels[4] ); break;
      
      case POS_VLOW   : gs.setCurrentItem( impactLabels[5] ); break;
      case POS_LOW    : gs.setCurrentItem( impactLabels[6] ); break;
      case POS_MEDIUM : gs.setCurrentItem( impactLabels[7] ); break;
      case POS_HIGH   : gs.setCurrentItem( impactLabels[8] ); break;
      case POS_VHIGH  : gs.setCurrentItem( impactLabels[9] ); break;
    }
    
    VerticalLayout vl = (VerticalLayout) impactPanel.getContent();
    Component comp = (Component) gs.getImplContainer();
    vl.addComponent( comp );
    vl.setComponentAlignment( comp, Alignment.MIDDLE_LEFT );
    
    currImpactSliders.put( obj.getIdAsUUID(), gs );
  }
  
  private void updateComponents()
  {
    currObjectives.clear();
    currImpactSliders.clear();
    
    commObjTitleList.removeAllItems();
    impactPanel.removeAllComponents();
    currGfxSlider = null;
    currTitleListItem = null;
    
    Map<UUID, Objective> commObjs = reController.getAllCommunityObjectives();
    Map<UUID, ImpactViewCD.eImpact> roImpacts = reController.getCurrentROImpacts();
    
    if ( (commObjs != null) && (roImpacts != null) )
    {
      for ( UUID objID : commObjs.keySet() )
      {
        Objective obj = reController.getObjective( objID );
        if ( obj != null )
        {
          if ( roImpacts.containsKey(objID) )
          {
            ImpactViewCD.eImpact impact = roImpacts.get( objID );
            addGfxSlider( obj, impact );
            
            changeData.modifyImpact( objID, impact );
            changeData.forceChanged( false ); // No actual change when updating
          }
          else
            addCurrentObjective( obj );
        }
      }
    }    
  }
  
  private void addCurrentObjective( Objective obj )
  {
    UUIDItem item = new UUIDItem( obj.getTitle(), obj.getIdAsUUID() );
    
    currObjectives.put( item.getID(), item );
    commObjTitleList.addItem( item );
  }
  
  private void setSelectedObjectiveImpacted()
  { 
    if ( currTitleListItem != null )
    {
      UUID targetID = currTitleListItem.getID();
      
      currObjectives.remove( targetID );
      commObjTitleList.removeItem( currTitleListItem );
      
      Map<UUID, Objective> commObjs = reController.getAllCommunityObjectives();
      Objective obj = commObjs.get( targetID );
      
      if ( obj != null )
      {
        addGfxSlider( obj, ImpactViewCD.eImpact.POS_HIGH );
        changeData.modifyImpact( obj.getIdAsUUID(), ImpactViewCD.eImpact.POS_HIGH );
      }
    }
  }
  
  private void removeObjectiveImpacted( UUID id )
  {
    GraphicSlider gs = currImpactSliders.get( id );
    if ( gs != null )
    {
      impactPanel.removeComponent( (Component) gs.getImplContainer() );
      currImpactSliders.remove( id );
      changeData.removeImpact( id );
      
      Map<UUID, Objective> commObjs = reController.getAllCommunityObjectives();
      Objective obj = commObjs.get( id );
      
      if ( obj != null )
        addCurrentObjective( obj );
    }
  }
  
  private ImpactViewCD.eImpact getImpactLevel( GraphicSlider gs )
  {
    ImpactViewCD.eImpact impact = ImpactViewCD.eImpact.POS_VHIGH;
    int index = gs.getCurrentItemIndex();
    
    switch ( index )
    {
      case 0 : impact = ImpactViewCD.eImpact.NEG_VHIGH; break;
      case 1 : impact = ImpactViewCD.eImpact.NEG_HIGH; break;
      case 2 : impact = ImpactViewCD.eImpact.NEG_MEDIUM; break;
      case 3 : impact = ImpactViewCD.eImpact.NEG_LOW; break;
      case 4 : impact = ImpactViewCD.eImpact.NEG_VLOW; break;
      case 5 : impact = ImpactViewCD.eImpact.POS_VLOW; break;
      case 6 : impact = ImpactViewCD.eImpact.POS_LOW; break;
      case 7 : impact = ImpactViewCD.eImpact.POS_MEDIUM; break;
      case 8 : impact = ImpactViewCD.eImpact.POS_HIGH; break;
      case 9 : impact = ImpactViewCD.eImpact.POS_VHIGH; break;
    }
    
    return impact;
  }
    
  // Private internal event handling -------------------------------------------
  private void onCancelCommit()
  {
    changeData.reset();
    setVisible( false );
  }
  
  private void onCommitLocalChanges()
  {
    Collection<RiskEditCommitListener> listeners = getListenersByType();
    
    for ( RiskEditCommitListener listener : listeners )
      listener.onCommitImpactChanges( changeData );
    
    setVisible( false );
  }
  
  private void onLeftClick()
  {
    if ( currGfxSlider != null )
    {
      removeObjectiveImpacted( currGfxSlider.getDataID() );
      currGfxSlider = null;
    }
  }
  
  private void onRightClick()
  {
    if ( currTitleListItem != null )
      setSelectedObjectiveImpacted();
  }
  
  private void onObjSelected( UUIDItem item )
  {    
    if ( item != null )
    {
      currTitleListItem = item;
    
      if ( currGfxSlider !=  null )
        currGfxSlider.setIsSelected( false );

      leftButton.setEnabled( false );
      rightButton.setEnabled( true );

      Map<UUID, Objective> commObjs = reController.getAllCommunityObjectives();
      Objective targObj = commObjs.get( currTitleListItem.getID() );
      
      if ( targObj != null )
        commObjSummary.setValue( targObj.getDescription() );
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
  
  private class MoveRightClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onRightClick(); }
  }
  
  private class MoveLeftClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onLeftClick(); }
  }
  
  private class ObjectiveSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onObjSelected( (UUIDItem) vce.getProperty().getValue() ); }
  }
}