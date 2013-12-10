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
//      Created Date :          21 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.SimpleView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.VerticalLabel;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.RiskEditorMainController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.*;

import uk.ac.soton.itinnovation.robust.riskmodel.*;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.Treatment;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;

import com.vaadin.ui.*;
import com.vaadin.data.*;
import com.vaadin.event.FieldEvents;

import java.util.*;




public class RiskEditBrowseView extends SimpleView
{
  private Label        roLabel;
  private TextField    roTitleField;
  private NativeSelect roGroupSelect;
  private NativeSelect roOwnerSelect;
  private Label        roStateDescription;
  private NativeSelect roScopeSelect;
  private Label        roEventLabel;
  private Label        roEventDescription;
  private ListSelect   roImpactList;
  private Label        roImpactDescription;
  private Label        roCATReviewFreq;
  private DateField    roExpiryField;
  private Label        roHumanReviewFreq;
  private ListSelect   roTreatmentList;
  private Label        roTreatmentDescription;
  private TextField    roDataChangedTestField;
  
  private Button editEventButton;
  private Button editImpactButton;
  private Button editScheduleButton;
  private Button editTreatmentSelectButton;
  
  private Button saveChangesButton;
  private Button discardChangesButton;
  
  private String[] roTypeValues = new String[] { "Risk", "Opportunity" };
  
  private RiskEditorMainController reController;
  private RiskEditPredictorView    predictorView;
  private RiskEditImpactView       impactView;
  private RiskEditScheduleView     scheduleView;
  private RiskEditTreatmentView    treatmentView;
  private EnumMap<Scope, UUIDItem> scopeItems;
  
  private transient HashMap<UUID, Objective> impactInfo;
  private transient HashMap<UUID, Treatment> treatmentInfo;
  private transient BrowseViewCD             changeData;
  
  private boolean canFireChangeEvents = false; //disable change listeners until required
  
  
  public RiskEditBrowseView( RiskEditorMainController ctrl )
  {
    super( false );
    
    reController = ctrl;
    
    impactInfo    = new HashMap<UUID, Objective>();
    treatmentInfo = new HashMap<UUID, Treatment>();
    scopeItems    = new EnumMap<Scope, UUIDItem>( Scope.class );
    changeData    = new BrowseViewCD();
    
    createComponents();
    setEditingEnabled( false );
  }
  
  public RiskEditPredictorView getPredictorView() { return predictorView; }
  
  public RiskEditImpactView getImpactView() { return impactView; }
  
  public RiskEditScheduleView getScheduleView() { return scheduleView; }
  
  public RiskEditTreatmentView getTreatmentView() { return treatmentView; }
  
  public void resetBrowseView()
  {
    canFireChangeEvents = false; //disable change listeners for now
    roLabel.setValue( "No current R/O" );
    roTitleField.setValue( "No current R/O" );
    roGroupSelect.removeAllItems();
    roOwnerSelect.removeAllItems();
    roStateDescription.setValue( "no current state" );
    roEventLabel.setValue( "No current predictors" );
    roEventDescription.setValue( "" );
    roImpactList.removeAllItems();
    roImpactDescription.setValue( "" );
    roCATReviewFreq.setValue( "No review set" );
    roExpiryField.setValue( new Date() );
    roHumanReviewFreq.setValue( "No review set" );
    roTreatmentList.removeAllItems();
    roTreatmentDescription.setValue( "" );
  }
  
  @Override
  public void updateView()
  {
    if ( reController.currentROSelected() )
    {
      setEditingEnabled( true );
      canFireChangeEvents = false;
      updateComponents();
      changeData.forceChanged(false);
      canFireChangeEvents = true;
    }
    else
    {
      resetBrowseView();
      setEditingEnabled( false );
      changeData.reset();
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    // Contents
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.addStyleName( "catBkgndLight" );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );
            
    // R/O label
    roLabel = new Label( "No Risk/Opportunity currently selected" );
    roLabel.addStyleName( "catSubHeadlineFont" );
    vl.addComponent( roLabel );
    vl.setComponentAlignment( roLabel, Alignment.TOP_LEFT );
    
    // Space, ruler, space
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    Component ruler = UILayoutUtil.createSpace("4px", "catHorizRule");
    ruler.setWidth( "725px" );
    vl.addComponent( ruler );
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    // Browse contents
    Panel browse = createBrowseContents();
    vl.addComponent( browse );
    vl.setComponentAlignment( browse, Alignment.MIDDLE_LEFT );
    vl.setExpandRatio( browse, 1.0f );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("16px", null) );
    
    // Save/discard controls
    HorizontalLayout hl = new HorizontalLayout();
    vl.addComponent( hl );
    vl.setComponentAlignment( hl, Alignment.BOTTOM_RIGHT );
    
    // test field to show if risk data changed (used only for debugging)
    roDataChangedTestField = new TextField();
    roDataChangedTestField.setImmediate(true);
    roDataChangedTestField.setVisible(false); // Set to true for debugging
    //roDataChangedTestField.addListener(new RODataChangedListener());
    //hl.addComponent( roDataChangedTestField );
    //hl.setComponentAlignment( roDataChangedTestField, Alignment.MIDDLE_CENTER );
    
    saveChangesButton = new Button( "Save changes" );
    saveChangesButton.setImmediate( true );
    saveChangesButton.setEnabled(false);
    saveChangesButton.addListener( new SaveClickListener() );
    hl.addComponent( saveChangesButton );
    hl.setComponentAlignment( saveChangesButton, Alignment.MIDDLE_CENTER );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    discardChangesButton = new Button( "Discard changes" );
    discardChangesButton.setImmediate( true );
    discardChangesButton.setEnabled(false);
    discardChangesButton.addListener( new DiscardClickListener() );
    hl.addComponent( discardChangesButton );
    hl.setComponentAlignment( discardChangesButton, Alignment.MIDDLE_CENTER );
    
    // Space
    Component space = UILayoutUtil.createSpace("10px", null, true);
    hl.addComponent( space );
    hl.setComponentAlignment( space , Alignment.MIDDLE_RIGHT );
  }
  
  private Panel createBrowseContents()
  {
    Panel panel = new Panel();
    panel.addStyleName( "borderless" );
    panel.setSizeFull();
    
    HorizontalLayout hl = new HorizontalLayout();
    panel.setContent( hl );
    
    hl.addComponent( UILayoutUtil.createSpace("24px", null, true) );
    
    VerticalLabel summaryTitle = new VerticalLabel( "Summary", 16.0f );
    summaryTitle.setHeight( "128px" );
    Component comp = (Component) summaryTitle.getImplContainer();
    hl.addComponent( comp );
    hl.setComponentAlignment( comp, Alignment.TOP_LEFT );  
    
    // Spaces
    hl.addComponent( UILayoutUtil.createSpace("12px", null, true) );
    
    Component spacer = UILayoutUtil.createSpace( "4px", "catVertRule", true );
    spacer.setHeight( "400px" );
    hl.addComponent( spacer );
    
    // Browse controls
    VerticalLayout vl = new VerticalLayout();
    hl.addComponent( vl );
    
    // Name & type
    vl.addComponent( createGeneralInfoControls() );
    vl.addComponent( UILayoutUtil.createSpace("16px", null) );
    spacer = UILayoutUtil.createSpace("4px", "catHorizRule");
    spacer.setWidth( "650px" );
    vl.addComponent( spacer );
    
    // Event predictor
    vl.addComponent( createEventPredictorControls() );
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    spacer = UILayoutUtil.createSpace("4px", "catHorizRule");
    spacer.setWidth( "650px" );
    vl.addComponent( spacer );
    
    // Impacts
    vl.addComponent( createImpactControls() );
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );
    spacer = UILayoutUtil.createSpace("4px", "catHorizRule");
    spacer.setWidth( "650px" );
    vl.addComponent( spacer );
    
    // Schedule
    vl.addComponent( createScheduleControls() );
    vl.addComponent( UILayoutUtil.createSpace("16px", null) );
    spacer = UILayoutUtil.createSpace("4px", "catHorizRule");
    spacer.setWidth( "650px" );
    vl.addComponent( spacer );
    
    // Treatments
    vl.addComponent( createTreatmentControls() );
    vl.addComponent( UILayoutUtil.createSpace("4px", "catHorizRule") );
    
    return panel;
  }
  
  private Component createGeneralInfoControls()
  {
    // Main container
    HorizontalLayout hl = new HorizontalLayout();
    
    // Title & edit control
    VerticalLayout teVL = new VerticalLayout();
    teVL.setWidth( "120px" );
    hl.addComponent( teVL );
    hl.setComponentAlignment( teVL, Alignment.TOP_RIGHT );
    
    Label natLabel = new Label( "General" );
    natLabel.addStyleName( "catSectionFont" );
    natLabel.addStyleName( "catBlue" );
    teVL.addComponent( natLabel );
    teVL.setComponentAlignment( natLabel, Alignment.TOP_LEFT );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    // Browse controls
    VerticalLayout bcVL = new VerticalLayout();
    hl.addComponent( bcVL );
    
    // R/O title
    HorizontalLayout ctHL = new HorizontalLayout();
    bcVL.addComponent( ctHL );
    Label label = new Label( "Title" );
    label.setWidth( "50px" );
    ctHL.addComponent( label );
    
    roTitleField = new TextField();
    roTitleField.setWidth( "128px" );
    roTitleField.addStyleName( "small" );
    roTitleField.setImmediate( true );
    roTitleField.addListener( new ROTitleTextChangeListener() );
    roTitleField.addListener( new ROTitleChangeListener() );
    ctHL.addComponent( roTitleField );
    
    // Space
    ctHL.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    // R/O group
    label = new Label( "Group" );
    label.setWidth( "50px" );
    ctHL.addComponent( label );
    
    roGroupSelect = new NativeSelect();
    roGroupSelect.setWidth( "100px" );
    roGroupSelect.addStyleName( "small" );
    roGroupSelect.setImmediate( true );
    roGroupSelect.addListener( new ROGroupChangeListener() );
    ctHL.addComponent( roGroupSelect );
    
    // Space
    ctHL.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    label = new Label( "State:" );
    label.setWidth( "50px" );
    ctHL.addComponent( label );
    
    roStateDescription = new Label();
    roStateDescription.setValue( "No current state" );
    roStateDescription.addStyleName( "small" );
    ctHL.addComponent( roStateDescription );
    
    // Space
    bcVL.addComponent( UILayoutUtil.createSpace("4px", null)  );
    
    // R/O Owner
    ctHL = new HorizontalLayout();
    bcVL.addComponent( ctHL );
    label = new Label( "Owner" );
    label.setWidth( "50px" );
    ctHL.addComponent( label );
    
    roOwnerSelect = new NativeSelect();
    roOwnerSelect.setWidth( "128px" );
    roOwnerSelect.addStyleName( "small" );
    roOwnerSelect.setImmediate( true );
    roOwnerSelect.addListener( new ROOwnerChangeListener() );
    ctHL.addComponent( roOwnerSelect );
    
    // Space
    ctHL.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    label = new Label( "Scope" );
    label.setWidth( "50px" );
    ctHL.addComponent( label );
    
    roScopeSelect = new NativeSelect();
    roScopeSelect.setWidth( "100px" );
    roScopeSelect.addStyleName( "small" );
    roScopeSelect.setImmediate( true );
    roScopeSelect.addListener( new ROScopeChangeListener() );
    
    UUIDItem rsItem = new UUIDItem( "All users" );
    rsItem.setData( Scope.ALL_USERS );
    roScopeSelect.addItem( rsItem );
    scopeItems.put( Scope.ALL_USERS, rsItem );
    
    rsItem = new UUIDItem( "User" );
    rsItem.setData( Scope.USER );
    roScopeSelect.addItem( rsItem );
    scopeItems.put( Scope.USER, rsItem );
    
    rsItem = new UUIDItem( "Group" );
    rsItem.setData( Scope.GROUP );
    roScopeSelect.addItem( rsItem );
    scopeItems.put( Scope.GROUP, rsItem );
    
    rsItem = new UUIDItem( "Community" );
    rsItem.setData( Scope.COMMUNITY );
    roScopeSelect.addItem( rsItem );
    scopeItems.put( Scope.COMMUNITY, rsItem );
    
    roScopeSelect.select( scopeItems.get(Scope.ALL_USERS) );
    ctHL.addComponent( roScopeSelect );
    
    return hl;
  }
  
  private Component createEventPredictorControls()
  {
    // Main container
    HorizontalLayout hl = new HorizontalLayout();
    
    // Title & edit control
    VerticalLayout teVL = new VerticalLayout();
    teVL.setWidth( "120px" );
    hl.addComponent( teVL );
    hl.setComponentAlignment( teVL, Alignment.TOP_RIGHT );
    
    Label epLabel = new Label( "Event" );
    epLabel.addStyleName( "catSectionFont" );
    epLabel.addStyleName( "catBlue" );
    teVL.addComponent( epLabel );
    teVL.setComponentAlignment( epLabel, Alignment.TOP_LEFT );
    
    teVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    editEventButton = new Button( "Edit event.." );
    editEventButton.setDescription("Edit the risk or opportunity event. You can choose an event from the available predictor services and provide the required input values.");
    editEventButton.setWidth( "110px" );
    editEventButton.addStyleName( "small" );
    editEventButton.addListener( new EditPredictorClickListener() );
    teVL.addComponent( editEventButton );
    teVL.setComponentAlignment( editEventButton, Alignment.BOTTOM_RIGHT );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    // Browse controls
    VerticalLayout bcVL = new VerticalLayout();
    hl.addComponent( bcVL );
    hl.setExpandRatio( bcVL, 1.0f );
    
    // Current predictor label & value
    HorizontalLayout labHL = new HorizontalLayout();
    bcVL.addComponent( labHL );
    Label label = new Label( "Current event predictor: " );
    labHL.addComponent( label );
    
    labHL.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    roEventLabel = new Label( "No current predictors" );
    labHL.addComponent( roEventLabel );
    
    // Predictor short description
    label = new Label( "Event summary" );
    label.addStyleName( "catSubSectionFont" );
    bcVL.addComponent( label );
    
    // Space
    bcVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    roEventDescription = new Label();
    roEventDescription.setWidth( "515px" );
    roEventDescription.setHeight( "40px" );
    roEventDescription.setEnabled( false ); // No UI changes allowed here
    roEventDescription.addStyleName( "catBorder" );
    roEventDescription.addStyleName( "tiny" );
    bcVL.addComponent( roEventDescription );
    
    return hl;
  }
 
  private Component createImpactControls()
  {
    // Main container
    HorizontalLayout hl = new HorizontalLayout();
    
    // Title & edit control
    VerticalLayout teVL = new VerticalLayout();
    teVL.setWidth( "120px" );
    hl.addComponent( teVL );
    hl.setComponentAlignment( teVL, Alignment.TOP_RIGHT );
    
    Label sectionLabel = new Label( "Impacts" );
    sectionLabel.addStyleName( "catSectionFont" );
    sectionLabel.addStyleName( "catBlue" );
    teVL.addComponent( sectionLabel );
    teVL.setComponentAlignment( sectionLabel, Alignment.TOP_LEFT );
    
    teVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    editImpactButton = new Button( "Edit impacts.." );
    editImpactButton.setDescription("Edit the impacts of your event on the community objectives.");
    editImpactButton.setWidth( "110px" );
    editImpactButton.addStyleName( "small" );
    editImpactButton.addListener( new EditImpactClickListener() );
    teVL.addComponent( editImpactButton );
    teVL.setComponentAlignment( editImpactButton, Alignment.BOTTOM_RIGHT );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    // Browse controls
    VerticalLayout bcVL = new VerticalLayout();
    hl.addComponent( bcVL );
    hl.setExpandRatio( bcVL, 1.0f );
    
    // Impact list
    HorizontalLayout imHL = new HorizontalLayout();
    bcVL.addComponent( imHL );
    roImpactList = new ListSelect( "Current impacts" );
    roImpactList.addStyleName( "catBorder" );
    roImpactList.setImmediate( true );
    roImpactList.addListener( new ImpactListSelectListener() );
    roImpactList.setWidth( "220px" );
    roImpactList.setHeight( "60px" );
    imHL.addComponent( roImpactList );
    
    // Space
    imHL.addComponent( UILayoutUtil.createSpace("8px", null, true) );
    
    // Impact summary
    VerticalLayout sumVL = new VerticalLayout();
    imHL.addComponent( sumVL );
    
    Label label = new Label( "Impact summary" );
    label.addStyleName( "catSubSectionFont" );
    sumVL.addComponent( label );
    
    // Space
    sumVL.addComponent( UILayoutUtil.createSpace("5px",null) );
    
    roImpactDescription = new Label();
    roImpactDescription.setWidth( "285px" );
    roImpactDescription.setHeight( "60px" );
    roImpactDescription.setEnabled( false ); // No direct UI changes allowed here
    roImpactDescription.addStyleName( "catBorder" );
    roImpactDescription.addStyleName( "tiny" );
    sumVL.addComponent( roImpactDescription );
    
    return hl;
  }
  
  private Component createScheduleControls()
  {
    // Main container
    HorizontalLayout hl = new HorizontalLayout();
    
    // Title & edit control
    VerticalLayout teVL = new VerticalLayout();
    teVL.setWidth( "120px" );
    hl.addComponent( teVL );
    hl.setComponentAlignment( teVL, Alignment.TOP_RIGHT );
    
    Label sectionLabel = new Label( "Schedule" );
    sectionLabel.addStyleName( "catSectionFont" );
    sectionLabel.addStyleName( "catBlue" );
    teVL.addComponent( sectionLabel );
    teVL.setComponentAlignment( sectionLabel, Alignment.TOP_LEFT );
    
    teVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    editScheduleButton = new Button( "Edit schedule.." );
    editScheduleButton.setDescription("This sets alerts for reviewing the defined risk or opportunity at certain periods.");
    editScheduleButton.setWidth( "110px" );
    editScheduleButton.addStyleName( "small" );
    editScheduleButton.addListener( new ScheduleClickListener() );
    teVL.addComponent( editScheduleButton );
    teVL.setComponentAlignment( editScheduleButton, Alignment.BOTTOM_RIGHT );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    // Browse controls
    VerticalLayout bcVL = new VerticalLayout();
    hl.addComponent( bcVL );
    hl.setExpandRatio( bcVL, 1.0f );
    
    // CAT review label/status
    HorizontalLayout schHL = new HorizontalLayout();
    bcVL.addComponent( schHL );
    Label label = new Label( "Cat review: " );
    label.setWidth( "70px" );
    schHL.addComponent( label );
    
    // Space
    schHL.addComponent( UILayoutUtil.createSpace("2px", null, true) );
    
    roCATReviewFreq = new Label( "No review set" );
    roCATReviewFreq.addStyleName( "tiny" );
    roCATReviewFreq.setWidth( "128px" );
    schHL.addComponent( roCATReviewFreq );
    
    // Space
    schHL.addComponent( UILayoutUtil.createSpace("30px", null, true) );
    
    // Human review label/status
    label = new Label( "Human review: " );
    label.setWidth( "90px" );
    schHL.addComponent( label );
    
    // Space
    schHL.addComponent( UILayoutUtil.createSpace("2px", null, true) );
    
    roHumanReviewFreq = new Label( "No review set" );
    roHumanReviewFreq.addStyleName( "tiny" );
    roHumanReviewFreq.setWidth( "128px" );
    schHL.addComponent( roHumanReviewFreq );
    
    // Space
    bcVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Expiry label/status
    schHL = new HorizontalLayout();
    bcVL.addComponent( schHL );
    
    label = new Label( "Expires: " );
    label.setWidth( "64px" );
    schHL.addComponent( label );
    
    roExpiryField = new DateField();
    roExpiryField.setWidth( "128px" );
    roExpiryField.setResolution( DateField.RESOLUTION_MIN );
    roExpiryField.setImmediate( true );
    roExpiryField.addListener( new RODateChangeListener() );
    schHL.addComponent( roExpiryField );
   
    return hl;
  }
  
  private Component createTreatmentControls()
  {
    // Main container
    HorizontalLayout hl = new HorizontalLayout();
    
    // Title & edit control
    VerticalLayout teVL = new VerticalLayout();
    teVL.setWidth( "120px" );
    hl.addComponent( teVL );
    hl.setComponentAlignment( teVL, Alignment.TOP_RIGHT );
    
    Label sectionLabel = new Label( "Treatments" );
    sectionLabel.addStyleName( "catSectionFont" );
    sectionLabel.addStyleName( "catBlue" );
    teVL.addComponent( sectionLabel );
    teVL.setComponentAlignment( sectionLabel, Alignment.TOP_LEFT );
    
    teVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    editTreatmentSelectButton = new Button( "Choose treatment.." );
    editTreatmentSelectButton.setDescription("Assign a treatment workflow to your risk or opportunity");
    editTreatmentSelectButton.addListener( new TreatmentClickListener() );
    editTreatmentSelectButton.setWidth( "110px" );
    editTreatmentSelectButton.addStyleName( "small" );
    teVL.addComponent( editTreatmentSelectButton );
    teVL.setComponentAlignment( editTreatmentSelectButton, Alignment.BOTTOM_RIGHT );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    // Browse controls
    VerticalLayout bcVL = new VerticalLayout();
    hl.addComponent( bcVL );
    hl.setExpandRatio( bcVL, 1.0f );
    
    // Impact list
    HorizontalLayout imHL = new HorizontalLayout();
    bcVL.addComponent( imHL );
    roTreatmentList = new ListSelect( "Current treatments" );
    roTreatmentList.addStyleName( "catBorder" );
    roTreatmentList.setWidth( "220px" );
    roTreatmentList.setHeight( "60px" );
    roTreatmentList.addListener( new TreatmentListSelectListener() );
    roTreatmentList.setImmediate( true );
    imHL.addComponent( roTreatmentList );
    
    // Space
    imHL.addComponent( UILayoutUtil.createSpace("8px", null, true) );
    
    // Impact summary
    VerticalLayout sumVL = new VerticalLayout();
    imHL.addComponent( sumVL );
    
    Label label = new Label( "Treatment summary" );
    label.addStyleName( "catSubSectionFont" );
    sumVL.addComponent( label );
    
    // Space
    sumVL.addComponent( UILayoutUtil.createSpace("5px", null) );
            
    // Description
    roTreatmentDescription = new Label();
    roTreatmentDescription.setWidth( "285px" );
    roTreatmentDescription.setHeight( "60px" );
    roTreatmentDescription.setEnabled( false ); // No direct UI changes allowed here
    roTreatmentDescription.addStyleName( "catBorder" );
    roTreatmentDescription.addStyleName( "tiny" );
    sumVL.addComponent( roTreatmentDescription );
    
    return hl;
  }
  
  private void updateComponents()
  {
    String title = reController.getCurrentROTitle();
    roLabel.setValue( title );
    roTitleField.setValue( title );

    // TO DO: sort out grouping assignment (if any)
    roGroupSelect.removeAllItems();
    UUIDItem item = new UUIDItem( "Default group ");
    roGroupSelect.addItem( item );
    roGroupSelect.select( item );

    // TO DO: get actual owners from a group
    roOwnerSelect.removeAllItems();
    item = new UUIDItem( reController.getCurrentROOwnerInfo() );
    roOwnerSelect.addItem( item );
    roOwnerSelect.select( item );
    
    roStateDescription.setValue( reController.getCurrentROState().toString() );
    
    Scope currScope = reController.getCurrentROScope();
    if ( currScope != null )
    {
      UUIDItem scopeItem = scopeItems.get( currScope );
      if ( scopeItem != null )
        roScopeSelect.select( scopeItem );
    }

    updateEventInfo();
    updateImpactInfo();
    updateScheduleInfo();
    updateTreatmentInfo();
  }
  
  private void setEditingEnabled( boolean enabled )
  {
    editEventButton.setEnabled( enabled );
    editImpactButton.setEnabled( enabled );
    editScheduleButton.setEnabled( enabled );
    editTreatmentSelectButton.setEnabled( enabled );
    
    roTitleField.setEnabled( enabled );
    roGroupSelect.setEnabled( enabled );
    roOwnerSelect.setEnabled( enabled );
    roScopeSelect.setEnabled( enabled );
    roExpiryField.setEnabled( enabled );
  }
  
  private void commitBrowseInfoChanges()
  {
    Collection<RiskEditCommitListener> listeners = getListenersByType();
    
    for ( RiskEditCommitListener listener : listeners )
      listener.onCommitBrowseViewChanges( changeData );
  }
  
  private void updateEventInfo()
  {
    Collection<Event> events = reController.getAllEventsFromCurrentRO();
    if ( events == null )
    {
      roEventLabel.setValue( "No event selected." );
      roEventDescription.setValue( "" ); 
    }
    else
    {
      String evPSDTitle = "Unknown predictor";
      String eventDescs = "Monitoring events: ";
      Event event       = null;
      
      Iterator<Event> evIt = events.iterator();
      while ( evIt.hasNext() )
      {
        event = evIt.next();
        eventDescs += event.getTitle() + ", ";
      }
      
      if ( event != null )
      {
        PredictorServiceDescription psd = reController.getPSDByEvent( event );
        evPSDTitle = psd.getName();
      }
      
      roEventLabel.setValue( "Events predicted by: " + evPSDTitle );
      roEventDescription.setValue( eventDescs );
    }
  }
  
  private void updateImpactInfo()
  {
    impactInfo.clear();
    roImpactList.removeAllItems();
    roImpactDescription.setValue( "" );
    
    Map<UUID, ImpactViewCD.eImpact> impacts = reController.getCurrentROImpacts();
    if ( impacts != null )
    {
      for ( UUID id : impacts.keySet() )
      {
        Objective obj = reController.getObjective( id );
        
        if ( obj != null )
        {
          impactInfo.put( id, obj );
          roImpactList.addItem( new UUIDItem( obj.getTitle(), id ));
        }
      }
    }
  }
  
  private void updateScheduleInfo()
  {
    roCATReviewFreq.setValue( reController.getCurrentROCATReviewSummaryInfo() );

    roExpiryField.setValue( reController.getCurrentROExpiryDate() );

    roHumanReviewFreq.setValue( reController.getCurrentROHumanReviewSummaryInfo() );
  }
  
  private void updateTreatmentInfo()
  {
    treatmentInfo.clear();
    roTreatmentList.removeAllItems();
    roTreatmentDescription.setValue( "" );
    
    List<Treatment> treatments = reController.getCurrentROTreatments();
    if ( treatments != null )
    {
      for ( Treatment treatment : treatments )
      {
        treatmentInfo.put( treatment.getID(), treatment );
        roTreatmentList.addItem( new UUIDItem(treatment.getTitle(), treatment.getID()) );
      }
    }
  }
  
  // Internal event handlers ---------------------------------------------------
  private void onEditPredictorClicked()
  {
    if ( reController.currentROSelected() )
    {
      // Commit any browse changes first (or they will be lost)
      commitBrowseInfoChanges();
      
      // Always create a new view
      predictorView = new RiskEditPredictorView( viewContents, reController );
      predictorView.addListener( reController );
      predictorView.updateView();
    } 
  }
  
  private void onImpactClicked()
  {
    if ( reController.currentROSelected() )
    {
      // Commit any browse changes first (or they will be lost)
      commitBrowseInfoChanges();
      
      // Always create a new view
      impactView = new RiskEditImpactView( viewContents, reController );
      impactView.addListener( reController );
      impactView.updateView();
    }
  }
  
  private void onScheduleClicked()
  {
    if ( reController.currentROSelected() )
    {
      // Commit any browse changes first (or they will be lost)
      commitBrowseInfoChanges();
      
      // Always create a new view
      scheduleView = new RiskEditScheduleView( viewContents, reController );
      scheduleView.addListener( reController );
      scheduleView.updateView();
    } 
  }
  
  private void onTreatmentClicked()
  {
    if ( reController.currentROSelected() )
    {
      // Commit any browse changes first (or they will be lost)
      commitBrowseInfoChanges();
      
      // Always create a new view
      treatmentView = new RiskEditTreatmentView( viewContents, reController );
      treatmentView.addListener( reController );
      treatmentView.updateView();
    }
  }
    
  private void onObjectiveSelected( UUIDItem item )
  {
    if ( item != null )
    {
      Objective obj = impactInfo.get( item.getID() );
      
      if ( obj != null )
        roImpactDescription.setValue( obj.getDescription() );
    }
  }
  
  private void onTreatmentSelected( UUIDItem item )
  {
    if ( item != null )
    {
      Treatment tmt = treatmentInfo.get( item.getID() );
      if ( tmt != null )
        roTreatmentDescription.setValue( tmt.getDescription() );
    }
  }
  
  private void onDataChanged() {
    if (! canFireChangeEvents)
        return;
    
    Collection<RiskEditCommitListener> listeners = getListenersByType();
    
    for ( RiskEditCommitListener listener : listeners )
      listener.onDataChanged();
  }
  
  private void onTitleFieldChanged() {
      onTitleFieldChanged((String)roLabel.getValue());
  }
  
  private void onTitleFieldChanged(String value)
  {
    String newTitle = value;
    
    roLabel.setValue( newTitle );
    changeData.setROTitle( newTitle );
    onDataChanged();
  }
  
  /* KEM - doesn't appear to be used
  private void onTypeChanged( String typeLabel )
  {
    if ( typeLabel.equals( roTypeValues[0]) )
      changeData.setROType( false );
    else
      changeData.setROType( true );
    onDataChanged();
  }
  */
  
  private void onGroupChanged( UUIDItem groupItem )
  {
    if ( groupItem != null ) {
      changeData.setROGroup( groupItem.getLabel() );
      onDataChanged();
    }
  }
  
  private void onOwnerChanged( UUIDItem ownerItem )
  { 
    if ( ownerItem != null ) {
      changeData.setROOwner( ownerItem.getLabel() );
      onDataChanged();
    }
  }
  
  private void onScopeChanged( UUIDItem item )
  {
      if ( item != null ) {
        changeData.setROScope( (Scope) item.getData() );
        onDataChanged();
      }
  }
  
  private void onExpirationChanged( Date date )
  {
      changeData.setROExpiration( date );
      onDataChanged();
  }
  
    private void onSaveROClicked() {
        try {
            Collection<RiskEditCommitListener> listeners = getListenersByType();

            if (changeData.isDataChanged()) {
                commitBrowseInfoChanges();
            }

            for (RiskEditCommitListener listener : listeners) {
                listener.onCommitAllChanges();
            }
            
            
            
        } catch (RuntimeException e) {
            this.displayMessage("ERROR: ", e.getMessage());
        }
    }
  
  private void onDiscardROClicked()
  {
    Collection<RiskEditCommitListener> listeners = getListenersByType();
    
    for ( RiskEditCommitListener listener : listeners )
      listener.onDiscardAllChanges();
  }

    public void setDataChanged(boolean dataChanged) {
        changeData.forceChanged(dataChanged);
        //onDataChanged();
    }
    
    public void onRODataHasChanged(boolean value) {
        roDataChangedTestField.setValue(Boolean.toString(value));
        saveChangesButton.setEnabled(value);
        discardChangesButton.setEnabled(value);
    }
  
  private class EditPredictorClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onEditPredictorClicked(); }
  }
  
  private class EditImpactClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onImpactClicked(); }
  }
  
  private class ScheduleClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onScheduleClicked(); }
  }
  
  private class TreatmentClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onTreatmentClicked(); }
  }
    
  private class ImpactListSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onObjectiveSelected( (UUIDItem) vce.getProperty().getValue() ); }    
  }
  
  private class TreatmentListSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onTreatmentSelected( (UUIDItem) vce.getProperty().getValue() ); }    
  }
  
  private class ROTitleChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onTitleFieldChanged(); }
  }
  
  private class ROTitleTextChangeListener implements FieldEvents.TextChangeListener
  {
    @Override
    public void textChange(FieldEvents.TextChangeEvent event) {
        //if (canFireChangeEvents)
            onTitleFieldChanged(event.getText());
    }
  }
  
  private class ROGroupChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    {
        //if (canFireChangeEvents)
            onGroupChanged( (UUIDItem) vce.getProperty().getValue() );
    }
  }
  
  private class ROOwnerChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    {
        //if (canFireChangeEvents)
            onOwnerChanged( (UUIDItem) vce.getProperty().getValue() );
    }
  }
  
  private class ROScopeChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    {
        //if (canFireChangeEvents)
            onScopeChanged( (UUIDItem) vce.getProperty().getValue() );
    }
  }
  
  private class RODateChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    {
        //if (canFireChangeEvents)
            onExpirationChanged( (Date) vce.getProperty().getValue() );
    }
  }
  
  /*
  private class RODataChangedListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    {
        //TODO( (Date) vce.getProperty().getValue() );
        String value = (String)vce.getProperty().getValue();
    }
  }
  */
  
  private class SaveClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) {
        onSaveROClicked();
    }
  }
  
  private class DiscardClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) {
        onDiscardROClicked();
    }
  }
}