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
//      Created Date :          21 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.RiskEditorMainController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.*;

import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;

import com.vaadin.ui.*;
import com.vaadin.data.Property;

import java.net.URI;
import java.util.*;





public class RiskEditPredictorView extends WindowView
                                   implements ParamViewListener,
                                              RiskEditEventCopyViewListener
{
  private RiskEditorMainController reController;
  
  private TabSheet eventMainSheet;

  // Current event components
  private ListSelect currEventList;
  private Label      currEventLabel;
  private Label      currEventInfoLabel;
  
  private transient UUID  currEventSelID;
  private transient Event eventCopyTarget;
  
  // Predictor components
  private com.vaadin.ui.Tree psdTree;
  private Label              currPSDLabel;
  private Label              currPSDURILabel;
  private Label              currBrowseEventLabel;
  private Label              currPSDInfoLabel;
  private Label              currBrowseEventInfoLabel;
  private Label              currBrowseUsageEventLabel;
  private VerticalTabSheet   currPSDConfigParamSheet;
  private VerticalTabSheet   currEventConfigParamSheet;
  private VerticalTabSheet   currEventCondParamSheet;
  private Button             useEventButton;
  private CheckBox           predictorActiveCB;
  
  private transient HashSet<UUID> browsePSDIDs;
  private transient HashSet<UUID> browseEventIDs;
  
  private transient HashMap<UUID, Parameter>      currActiveParameters;
  private transient HashMap<UUID, EventCondition> currActiveEventConditions;
  private transient PredictorServiceDescription   currSelPSD;
  private transient Event                         currBrowseSelectEvent;
  private transient Event                         currEditEvent;
  private transient PredictorServiceDescription   inUsePSD;
  private transient PredictorViewCD               changeData;
  
  
  public RiskEditPredictorView( Component parent, RiskEditorMainController ctrl )
  {
    super( parent, "" );
    
    reController = ctrl;
    
    // Size & position window
    window.setWidth( "900px" );
    window.setHeight( "550px" );
    centreWindow();
    
    browsePSDIDs              = new HashSet<UUID>();
    browseEventIDs            = new HashSet<UUID>();
    currActiveParameters      = new HashMap<UUID, Parameter>();
    currActiveEventConditions = new HashMap<UUID, EventCondition>();
    
    changeData = new PredictorViewCD();
    
    createComponents();
  }
  
  @Override
  public void updateView()
  {
    setHeadline( "Event prediction: " + reController.getCurrentROTitle() );
    
    changeData.reset();
    
    updateComponents();
  }
  
  // ParamValueViewListener ----------------------------------------------------
  @Override
  public void onParamValueChanged( AbstractParamView apv )
  {
    UUID             modelID = apv.getID();
    Parameter   changedParam = null;
    EventCondition changedEC = null;
    
    // Update either parameter or event condition instances
    if ( currActiveParameters.containsKey(modelID) )
    {
      changedParam = currActiveParameters.get(modelID);
      String newValue = extractValueFromView( changedParam.getType(), apv );
      
      // Update parameter
      if ( newValue != null ) changedParam.setValue( new ParameterValue(newValue) );
    }
    else if ( currActiveEventConditions.containsKey(modelID) )
    {
      changedEC = currActiveEventConditions.get( modelID );
      ParameterValueType pvt = changedEC.getType();
      
      String newValue = extractValueFromView( pvt, apv );
      EvaluationType et = extractEvalTypeFromView( pvt, apv );
      
      // Set value
      if ( newValue != null )
      {
        if ( apv.getMetadata().equals("pre") ) // Pre-condition
          changedEC.setPreConditionValue( new ParameterValue(newValue) );
        else // Post-condition
          changedEC.setPostConditionValue( new ParameterValue(newValue) );
      }
      
      // Set evaluation type (actual value may not have changed)
      if ( et != null )
      {
        if ( apv.getMetadata().equals("pre") ) // Pre-condition
        {
          ParameterValue pv = changedEC.getPreConditionValue();
          pv.setValueEvaluationType( et );
        }
        else // Post condition
        {
          ParameterValue pv = changedEC.getPostConditionValue();
          pv.setValueEvaluationType( et );
        }
      }
    }
    
    // Update change data if changes relate to active PSD/Event; we need to
    // take care of EventCondition data locally
    if ( (inUsePSD == currSelPSD) && (currEditEvent == currBrowseSelectEvent) )
    {
      if ( changedEC != null ) // Change was an event condition
      {
        // Remove the old event condition instance, if it exists
        EventCondition oldEC = currEditEvent.getEventConditionByID( modelID );
        if ( oldEC != null )
          currEditEvent.removeEventCondition( oldEC );
        
        // Add in the new one
        currEditEvent.addEventCondition( changedEC );
      }
      
      // Force a data change
      changeData.forceChanged( true );
    }
  }
  
  // RiskEditEventCopyViewListener ---------------------------------------------
  @Override
  public void onEventSubTitleViewClosed( String subTitle )
  { 
    if ( eventCopyTarget != null )
    {
      eventCopyTarget.setTitle( eventCopyTarget.getTitle() + " " + subTitle );
        
      // Make note of change
      if ( changeData.addEvent( eventCopyTarget ) )
        addEventToCurrentEventView( eventCopyTarget );

      // Commit these changes locally
      onCommitLocalChanges();
    }
    
    eventCopyTarget = null;
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {  
    VerticalLayout vl = (VerticalLayout) window.getContent();
    
    // Headline
    vl.addComponent( createHeadline("Unknown r/o") );
    
    // Main controls
    vl.addComponent( createMainControls() );
    
    // OK/cancel controls
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
    
    // Spacer
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
  }
  
  private Component createMainControls()
  {
    // Separate views for currently configured events and predictors
    eventMainSheet = new TabSheet();
    eventMainSheet.addStyleName( "borderless" );
    eventMainSheet.setWidth( "100%" );
    eventMainSheet.setHeight( "430px" );
    
    // Event view
    eventMainSheet.addTab( createCurrentEventsView(),
                           "Events for this risk", null );
    
    // Predictor service view
    eventMainSheet.addTab( createPredictorServiceView(),
                           "Predictor services", null );
    
    return eventMainSheet;
  }
  
  private Component createCurrentEventsView()
  {
    Panel panel = new Panel();
    panel.addStyleName( "small" );
    panel.addStyleName( "borderless" );
    AbstractLayout al = (AbstractLayout) panel.getContent(); // Compact panel
    al.setMargin( false );
    
    // Space
    al.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    HorizontalLayout hl = new HorizontalLayout();
    panel.addComponent( hl );
    
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth( "260px" );
    hl.addComponent( vl );
    
    Label label = new Label( "Currently defined events" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    currEventList = new ListSelect();
    currEventList.setWidth( "258px" );
    currEventList.setHeight( "285px" );
    currEventList.setImmediate( true );
    currEventList.addListener( new EventSelectListener() );
    vl.addComponent( currEventList );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    Button button = new Button( "Edit event" );
    button.setWidth( "100%" );
    button.addStyleName( "default" );
    button.addListener( new CurrentEventEditListener() );
    vl.addComponent( button );
    vl.setComponentAlignment( button, Alignment.BOTTOM_CENTER );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    button = new Button( "Copy event" );
    button.setWidth( "100%" );
    button.addListener( new CurrentEventCopyListener() );
    vl.addComponent( button );
    vl.setComponentAlignment( button, Alignment.BOTTOM_CENTER );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    button = new Button( "Remove event" );
    button.setWidth( "100%" );
    button.addListener( new CurrentEventRemoveListener() );
    vl.addComponent( button );
    vl.setComponentAlignment( button, Alignment.BOTTOM_CENTER );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("2px", null, true) );
    
    vl = new VerticalLayout();
    vl.setWidth( "100%" );
    vl.setHeight( "320" );
    hl.addComponent( vl );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("16px", null) );
    
    currEventLabel = new Label( "No event currently selected"  );
    currEventLabel.addStyleName( "catSectionFont" );
    currEventLabel.addStyleName( "catBlue" );
    currEventLabel.setImmediate( true );
    vl.addComponent( currEventLabel );
    
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    label = new Label( "Event description:" );
    vl.addComponent( label );
    
    currEventInfoLabel = new Label( "No event information yet" );
    currEventInfoLabel.setWidth( "400px" );
    currEventInfoLabel.setHeight( "300px" );
    currEventInfoLabel.addStyleName( "small" );
    currEventInfoLabel.setImmediate( true );
    vl.addComponent( currEventInfoLabel );

    return panel;
  }
  
  private Component createPredictorServiceView()
  {
    // Predictor browser
    Panel panel = new Panel();
    panel.addStyleName( "small" );
    panel.addStyleName( "borderless" );
    AbstractLayout al = (AbstractLayout) panel.getContent(); // Compact panel
    al.setMargin( false );
    
    HorizontalLayout hl = new HorizontalLayout();
    panel.addComponent( hl );
    hl.addComponent( createPSDBrowser() );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    // Sub-views
    VerticalLayout vl = new VerticalLayout();
    hl.addComponent( vl );
    
    HorizontalLayout subHL = new HorizontalLayout();
    vl.addComponent( subHL );
    
    // Predictor Service Description view
    subHL.addComponent( createPSDInfoView() );
    
    // Space
    subHL.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    // Event info view
    subHL.addComponent( createEventInfoView() );
    
    vl.addComponent( UILayoutUtil.createSpace("24px", null) );
    
    // Current PSD/Event use controls
    vl.addComponent( createUseControls() );
    
    return panel;
  }
  
  private Component createPSDBrowser()
  {
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth( "160px" );
    
    Label label = new Label( "Current predictors" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    VerticalLayout treeVL = new VerticalLayout();
    treeVL.setWidth( "98%" );
    treeVL.addStyleName( "catBorder" );
    vl.addComponent( treeVL );
    
    // Current predictors
    psdTree = new com.vaadin.ui.Tree();
    psdTree.setHeight( "368px" );
    psdTree.setImmediate( true );
    psdTree.addListener( new PredictorSelectListener() );
    treeVL.addComponent( psdTree );
        
    return vl;
  }
  
  private Component createPSDInfoView()
  {
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth( "315px" );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("20px", null) );
    
    currPSDLabel = new Label( "Selected predictor: None" );
    currPSDLabel.addStyleName( "catSectionFont" );
    currPSDLabel.addStyleName( "catBlue" );
    vl.addComponent( currPSDLabel );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    // Info and configuration sheets
    TabSheet psdSheet = new TabSheet();
    psdSheet.addStyleName( "borderless" );
    psdSheet.setWidth( "100%" );
    psdSheet.setHeight( "268px" );
    vl.addComponent( psdSheet );
    
    VerticalLayout psdVL = new VerticalLayout();
    psdSheet.addTab( psdVL, "Predictor info" );
    
    // Space
    psdVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    psdVL.addComponent( new Label( "About this predictor:" ) );
    psdVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    currPSDInfoLabel = new Label( "Currently no information" );
    currPSDInfoLabel.setWidth( "100%" );
    currPSDInfoLabel.setHeight( "215px" );
    currPSDInfoLabel.addStyleName( "small" );
    psdVL.addComponent( currPSDInfoLabel );
    
    currPSDConfigParamSheet = new VerticalTabSheet();
    psdSheet.addTab( (Component) currPSDConfigParamSheet.getImplContainer(),
                     "Predictor config" );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // PSD URI info
    currPSDURILabel = new Label();
    currPSDURILabel.setWidth( "100%" );
    currPSDURILabel.setHeight( "26px" );
    currPSDURILabel.addStyleName( "tiny" );
    currPSDURILabel.addStyleName( "catBorder" );
    vl.addComponent( currPSDURILabel );
    
    return vl;
  }
  
  private Component createEventInfoView()
  {
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth( "390px" );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("20px", null) );
    
    currBrowseEventLabel = new Label( "Selected event: None" );
    currBrowseEventLabel.addStyleName( "catSectionFont" );
    currBrowseEventLabel.addStyleName( "catBlue" );
    vl.addComponent( currBrowseEventLabel );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    // Info, config and conditions sheet
    TabSheet evSheet = new TabSheet();
    evSheet.addStyleName( "borderless" );
    evSheet.setWidth( "100%" );
    evSheet.setHeight( "297px" );
    vl.addComponent( evSheet );
    
    VerticalLayout evVL = new VerticalLayout();
    evSheet.addTab( evVL, "Event info" );
    
     // Space
    evVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    evVL.addComponent( new Label( "About this event:" ) );
    evVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    currBrowseEventInfoLabel = new Label( "Currently no information" );
    currBrowseEventInfoLabel.setWidth( "100%" );
    currBrowseEventInfoLabel.setHeight( "240px" );
    currBrowseEventInfoLabel.addStyleName( "small" );
    evVL.addComponent( currBrowseEventInfoLabel );
    
    currEventConfigParamSheet = new VerticalTabSheet();
    evSheet.addTab( (Component) currEventConfigParamSheet.getImplContainer(),
                     "Event config" );
    
    
    currEventCondParamSheet = new VerticalTabSheet();
    evSheet.addTab( (Component) currEventCondParamSheet.getImplContainer(),
                     "Event conditions" );
    
    return vl;
  }
  
  private Component createUseControls()
  {
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth( "100%" ); 
    
    // Usage label
    currBrowseUsageEventLabel = new Label( "No event in use" );
    currBrowseUsageEventLabel.addStyleName( "catSectionFont" );
    currBrowseUsageEventLabel.addStyleName( "catBlueDark" );
    currBrowseUsageEventLabel.setWidth( "320px" );
    hl.addComponent( currBrowseUsageEventLabel );
    hl.setComponentAlignment( currBrowseUsageEventLabel, Alignment.TOP_LEFT );
    
    // Use and active buttons
    HorizontalLayout subHL = new HorizontalLayout();
    hl.addComponent( subHL );
    hl.setComponentAlignment( subHL, Alignment.TOP_RIGHT );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    useEventButton = new Button( "Use this event" );
    useEventButton.setWidth( "200px" );
    useEventButton.addListener( new UseEventClickListener() );
    subHL.addComponent( useEventButton );
    subHL.setComponentAlignment( useEventButton, Alignment.MIDDLE_RIGHT );
    
    predictorActiveCB = new CheckBox( "Make predictor active" );
    predictorActiveCB.addListener( new PredictionActiveClickListener() );
    subHL.addComponent( predictorActiveCB );
    subHL.setComponentAlignment( predictorActiveCB, Alignment.MIDDLE_RIGHT );
    
    return hl;
  }
  
  private void updateComponents()
  {
    // Tidy up
    browsePSDIDs.clear();
    browseEventIDs.clear();
    currActiveParameters.clear();
    currActiveEventConditions.clear();
    psdTree.removeAllItems();
    currEventList.removeAllItems();
    currEditEvent = null;
    currBrowseSelectEvent = null;
    
    // Run through previously defined events (if any)
    Collection<Event> events = reController.getAllEventsFromCurrentRO();
    if ( events != null && !events.isEmpty() )
    {
      // Update the known event and related PSD
      Iterator<Event> evIt = events.iterator();
      while ( evIt.hasNext() )
      { addEventToCurrentEventView( evIt.next() ); }
    }
    
    // Run through all known predictors adding each and its events into the tree
    Map<UUID, String> predSummaryInfo = reController.getPredictorSummaryInfo();
    for ( UUID pID : predSummaryInfo.keySet() )
    {      
      // Add PSD
      UUIDItem psdItem = new UUIDItem( predSummaryInfo.get(pID), pID );
      browsePSDIDs.add( pID );
      psdTree.addItem( psdItem );
      
      // Add child events (swapping in any active event data as required)
      PredictorServiceDescription psd = reController.getPSDByID( pID );
      
      // Add in events
      for ( Event ev : psd.getEvents() )
      {
        UUID eventID = ev.getUuid();
        browseEventIDs.add( eventID );        
        UUIDItem evItem = new UUIDItem( ev.getTitle(), eventID );
        psdTree.addItem( evItem );
        psdTree.setChildrenAllowed( evItem, false );
        psdTree.setParent( evItem, psdItem );
      }
    }
    
    updateCurrentEventInfo();
  }
  
  private void updateCurrentEventInfo()
  {
    if ( inUsePSD != null && currEditEvent != null )
    {
      updatePSDInfoView( inUsePSD );
      updateEventInfoView( currEditEvent );
    }
    
    // Update usage controls
    updateUsageControls();
    
    // Update activity status
    predictorActiveCB.setValue( reController.getCurrentROIsNotifyingUser() );
  }
  
  private void updatePSDInfoView( PredictorServiceDescription psd )
  {
    final int paramWidth = 220;
    
    if ( psd != null )
    {
      currPSDLabel.setValue( psd.getName() );
      currPSDInfoLabel.setValue( psd.getDescription() );
      
      //test adding forecast param to the config params to be displayed
      List<Parameter> params=psd.getConfigurationParams();
      if(params!=null)
          params.add(psd.getForecastPeriod());
      // Update PSD parameters
      updateSheet( currPSDConfigParamSheet,
                  params,
                   paramWidth );
      
      // Update URI info
      URI psdURI = psd.getServiceURI();
      if ( psdURI != null ) currPSDURILabel.setValue( psdURI.toString() );
    }
  }
  
  private void updateEventInfoView( Event event )
  {    
    final int eventParamWidth = 250;    
    
    // Clear event info if null
    if ( event == null )
    {
      currBrowseEventLabel.setValue( "Selected event: None" );
      currBrowseEventInfoLabel.setValue( "Currently no information" );
      
      // Clear event data
      updateSheet( currEventConfigParamSheet, null, eventParamWidth );
      updateSheet( currEventCondParamSheet  , null, eventParamWidth );
    }
    else
    {   
      currBrowseEventLabel.setValue( event.getTitle() );
      currBrowseEventInfoLabel.setValue( event.getDescription() );
      
      // Config params
      updateSheet( currEventConfigParamSheet,
                   event.getConfigParams(),
                   eventParamWidth );
      
      // Conditional params
      updateSheetWithConditions( currEventCondParamSheet,
                                 event.getEventConditions(),
                                 eventParamWidth );
    }
  }
  
  private void updateUsageControls()
  {
    // Update current PSD and Event label
    if ( inUsePSD != null )
    {
      if ( currEditEvent != null )
        currBrowseUsageEventLabel.setValue( "Using: " + currEditEvent.getTitle() + " (" +
                                      inUsePSD.getName() + ")" );
      else
        currBrowseUsageEventLabel.setValue( "No event in use" );
    }
    
    // Reflect in-use options
    if ( currEditEvent != null && currBrowseSelectEvent != null )
    {
      if ( currEditEvent.getUuid().compareTo( currBrowseSelectEvent.getUuid()) == 0 )
      {
        useEventButton.setCaption( "Using this event" );
        useEventButton.setEnabled( false );
      }
      else
      {
        useEventButton.setCaption( "Use " + currBrowseSelectEvent.getTitle() );
        useEventButton.setEnabled( true );
      }
    }
    
    // Update active status
    predictorActiveCB.setValue( reController.getCurrentROIsNotifyingUser() );
  }
    
  private void updateSheet( VerticalTabSheet sheet,
                            List<Parameter> params,
                            int paramWidth )
  {
    sheet.clearAllTabs();
    
    if ( params != null )
    {
      for ( Parameter param : params )
        if ( param != null )
        {
          boolean isStateParam = false;
          //bmn: the state based test method looks for a string value and multiple values allowed. This is not accurate.
          try { if ( param.isStateBased() ) isStateParam = true; } 
          catch ( Exception e ) {}
          
          AbstractParamView apv = null;
          
          if ( isStateParam )
             apv = createStateParamView( param );
          else
            switch ( param.getType() )
            {
              case    INT :
              case  FLOAT : apv = createNumericParamView( param, false ); break;
              
              case STRING : apv = createFieldParamView( param ); break;
            }
          
          if ( apv != null )
          {
            apv.setWidth( paramWidth );
            apv.setID( param.getUUID() );
            sheet.addTab( param.getName(),
                          (Component) apv.getImplContainer() );
            
            // Update known parameters
            UUID targetID = param.getUUID();
            currActiveParameters.remove( targetID );
            currActiveParameters.put( targetID, param );            
          }
        }
    }
    else { sheet.clearAllTabs(); }// Clear the sheet if no parameters supplied
  }
  
  private void updateSheetWithConditions( VerticalTabSheet sheet, 
                                          List<EventCondition> conditions,
                                          int paramWidth )
  {
    sheet.clearAllTabs();
    
    if ( conditions != null )
    {
      for ( EventCondition ec : conditions )
      {
        // Pre and Post condition parameters...
        UUID eventCondID                = ec.getUUID();
        Parameter ecPreParam            = null;
        Parameter ecPostParam           = null;
        AbstractParamView preParamView  = null;
        AbstractParamView postParamView = null;
        
        // Create pre-condition, if one exists
        if ( ec.isPreConditionValueSet() )
        {
          ecPreParam = new Parameter( ec.getType(),
                                      "Pre-condition",
                                      ec.getDescription(),
                                      ec.getUnit() );
          
          ecPreParam.setUUID( eventCondID );
          ecPreParam.setValueConstraints( ec.getValueConstraints() );
          ecPreParam.setValue( ec.getPreConditionValue() );
        }
        else // Otherwise always assume we have a post-parameter
        {
          ecPostParam = new Parameter( ec.getType(),
                                       "Post-condition",
                                       ec.getDescription(),
                                       ec.getUnit() );

          ecPostParam.setUUID( eventCondID );
          ecPostParam.setName( "Post-condition" );
          ecPostParam.setValueConstraints( ec.getValueConstraints() );

          try
          {
            ParameterValue value = (ec.getPostConditionValue() == null ? new ParameterValue( ec.getDefaultValue() )
                                                                       : ec.getPostConditionValue() );

            ecPostParam.setValue( value );
          }
          catch (Exception ex) { /*Shout for help here!*/ }
        }
        
        // Only create views if we have something to use
        if ( ecPreParam != null || ecPostParam != null )
        {
          // Determine stateness of param conditions
          Parameter stateTest = ecPreParam;
          if ( stateTest == null ) stateTest = ecPostParam;
          
          boolean isStateParam = false;
          
          try { if ( stateTest.isStateBased() ) isStateParam = true; }
          catch ( Exception e ) { /*Nothing to do here - it's just not a state */ }
          
          // Create pre-condition view if required
          if ( ecPreParam != null )
            preParamView = createConditionView( ecPreParam, isStateParam,
                                                true, paramWidth );
          
          // Create post-condition view if required
          if ( ecPostParam != null )
            postParamView = createConditionView( ecPostParam, isStateParam,
                                                 false, paramWidth );
          
          // Wrap up the views
          String ecName = ec.getName();
          
          ParamMultiView pmv = new ParamMultiView( ecName, ec.getDescription() );
          
          if ( preParamView != null  ) pmv.addParamView( preParamView.getTitle(), preParamView );
          if ( postParamView != null ) pmv.addParamView( postParamView.getTitle(), postParamView );
          // Listeners already included in sub param views
          
          sheet.addTab( ecName, (Component) pmv.getImplContainer() );
          
          // Update known event conditions
          currActiveEventConditions.remove( eventCondID );
          currActiveEventConditions.put( eventCondID, ec );
        }
      }
    }
  }
  
  private AbstractParamView createConditionView( Parameter condition, 
                                                 boolean isStateBased,
                                                 boolean isPreCondition,
                                                 int paramWidth )
  {
    AbstractParamView condView = null;
    
    // Create the appopriate kind of view
    if ( isStateBased )
      condView = createStateParamView( condition );
    else
      switch ( condition.getType() )
      {
        case    INT :
        case  FLOAT : condView  = createNumericParamView( condition, true ); break;

        case STRING : condView  = createFieldParamView( condition ); break;
      }
    
    condView.setWidth( paramWidth );
    condView.setID( condition.getUUID() );
    
    if ( isPreCondition )
      condView.setMetadata( "pre" );
    else
      condView.setMetadata( "post" );
    
    return condView;
  }
  
  private AbstractParamView createNumericParamView( Parameter param,
                                                    boolean displayConditions )
  {
    AbstractParamView apv = null;
    
    if ( param != null )
    {
      // Try extracting data from the parameter
        System.out.println("param name ="+param.getName());
        System.out.println("param value ="+param.getValue());
        System.out.println("param def value ="+param.getDefaultValue());
         
       Float pVal =((param.getValue()==null)||(param.getValue().getValue()==null))?new Float( Float.parseFloat(param.getDefaultValue())):new Float( Float.parseFloat( param.getValue().getValue()) );
       if(pVal==null){
            System.out.println("Parameter "+param.getName()+" has no value or default value!");
            throw new RuntimeException("Parameter "+param.getName()+" has no value or default value!");
        }
        
      Float pMin, pMax;
      
      // Try finding mix/max
      try { pMin = new Float( Float.parseFloat( param.getMin() ) ); }
      catch ( Exception e )
      { pMin = pVal - 1; /*What else to do here? */ }
      
      try { pMax = new Float( Float.parseFloat( param.getMax() ) ); }
      catch( Exception e )
      { pMax = pVal + 1; /*What else to do here? */ }
      
      String paramName = param.getName();
      ParamFloatView fvv = new ParamFloatView( paramName,
                                               param.getDescription(),
                                               param.getUnit(),
                                               pMin,
                                               pVal,
                                               pMax );
      fvv.addListener( this );
      
      // Display firing conditions for numeric view if required
      if ( displayConditions )
      {
        ParameterValue pv = param.getValue();
        if ( pv != null )
        {
          EvaluationType et = pv.getValueEvaluationType();
          if ( et != null )
            switch ( et )
            {
              case LESS             : fvv.setThreshold(ParamFloatView.eNumericThreshold.LESS); break;
              case LESS_OR_EQUAL    : fvv.setThreshold(ParamFloatView.eNumericThreshold.LESS_EQUAL); break;
              case EQUAL            : fvv.setThreshold(ParamFloatView.eNumericThreshold.EQUAL); break;
              case GREATER_OR_EQUAL : fvv.setThreshold(ParamFloatView.eNumericThreshold.GREATER_EQUAL); break;
              case GREATER          : fvv.setThreshold(ParamFloatView.eNumericThreshold.GREATER); break;
            }
        }
      }
      else
        fvv.setAllowThreshold( false );
      
      apv = fvv;
    }
    
    return apv;
  }
  
  private AbstractParamView createStateParamView( Parameter param )
  {
    AbstractParamView apv = null;
    
    if ( param != null )
    {      
      try
      {
        Set<String> states = param.getValuesAllowedSet();
        
        ParamStateView svv = new ParamStateView( param.getName(),
                                                 param.getDescription(),
                                                 param.getUnit() );
        
        svv.addListener( this );
        svv.setStates( states );
        
        //String value=(param.getValue()==null || param.getValue().getValue()==null)?param.getDefaultValue():param.getValue().getValue();
        
        // KEM start new code
        String value = param.getDefaultValue(); // Set default to start with
        
        ParameterValue paramValue = param.getValue();
        
        if (paramValue != null) {
            String paramValueValue = paramValue.getValue();
            if (paramValueValue != null)
                value = paramValueValue;
        }
        else {
            paramValue = new ParameterValue();
        }
        
        paramValue.setValue(value);
        param.setValue(paramValue);
        // KEM end new code
        
        svv.setCurrentState( value/*param.getDefaultValue()*/ );//bmn
        
        apv = svv;
      }
      catch ( NumberFormatException nfe ) { System.out.println("State parameter could not be constructed!"); }
      catch ( Exception e ) { System.out.println("State parameter could not be constructed (" + e.getMessage() + ")"); }      
    }
    
    return apv;
  }
  
  private AbstractParamView createFieldParamView( Parameter param )
  {
    AbstractParamView apv = null;
    
    if ( param != null )
    {
      String paramName = param.getName();
      
      String value=null;//bmn
      try{
          value=(param.getValue()==null || param.getValue().getValue()==null?param.getDefaultValue(): param.getValue().getValue());
      }
      catch(Exception ex){}//bmn
      
      ParamFieldView pfv = new ParamFieldView( paramName,
                                               param.getDescription(),
                                          value);//bmn
      
      pfv.addListener( this );
      apv = pfv;
    }
    
    return apv;
  }
  
  private String extractValueFromView( ParameterValueType pvt,
                                       AbstractParamView apv )
  {
    String newValue = null;
    
    switch ( pvt )
    {
      case INT:
      case FLOAT : {
                      ParamFloatView pfv = (ParamFloatView) apv;
                      Double val = pfv.getValue();
                      newValue = val.toString();
                   } break;

      case STRING: {
                      try
                      {
                        ParamStateView psv = (ParamStateView) apv;
                        newValue = psv.getStateValue();
                      }
                      catch (ClassCastException cce)
                      {
                        ParamFieldView pfv = (ParamFieldView) apv;
                        newValue = pfv.getValue();
                      }
                   } break;
    }
    
    return newValue;
  }
  
  private EvaluationType extractEvalTypeFromView( ParameterValueType pvt,
                                                  AbstractParamView apv )
  {
    EvaluationType et = null;
    
    switch ( pvt )
    {
      case INT:
      case FLOAT : {
                      ParamFloatView pfv = (ParamFloatView) apv;
                      
                      switch ( pfv.getThreshold() )
                      {
                        case LESS          : et = EvaluationType.LESS;             break;
                        case LESS_EQUAL    : et = EvaluationType.LESS_OR_EQUAL;    break;
                          
                        case NO_THRESHOLD  : /* Default to EQUAL if nothing selected */
                        case EQUAL         : et = EvaluationType.EQUAL;            break;
                        
                        case GREATER_EQUAL : et = EvaluationType.GREATER_OR_EQUAL; break;
                        case GREATER       : et = EvaluationType.GREATER;          break;
                      }
                   } break;
    }
    
    return et;
  }
  
  private void addEventToCurrentEventView( Event event )
  {
    if ( event != null )
    {
      UUIDItem evItem = new UUIDItem( event.getTitle(), event.getUuid() );
      currEventList.addItem( evItem );
    }
  }
  
  // Private event handlers ----------------------------------------------------
  private void onCancelCommit()
  { setVisible( false ); }
  
  private void onCommitLocalChanges()
  {
    if ( changeData.isDataChanged() )
    {
      changeData.setPSD( inUsePSD );
      changeData.setPredictionActive( predictorActiveCB.booleanValue() );
      
      Collection<RiskEditCommitListener> listeners = getListenersByType();
    
      for ( RiskEditCommitListener recl : listeners )
        recl.onCommitEventPredictorChanges( changeData );
    }
  }
  
  private void onPSDBrowseItemSelected( UUIDItem item )
  {
    if ( item != null )
    {
      UUID id = item.getID();
      
      if ( browsePSDIDs.contains(id) ) // Got a PSD
      {
        PredictorServiceDescription targPSD = reController.getPSDByID( id );
        if ( targPSD != null )
        {
          currSelPSD = targPSD;
          updatePSDInfoView( currSelPSD );
        }
        
        updateEventInfoView( null ); // Don't know which event to use, so clear
      }
      else if ( browseEventIDs.contains(id) ) // Got a specific event
      {
        // Locally commit previous changes, if any
        if ( currEditEvent != null ) onCommitLocalChanges();
        
        // Get parent PSD
        UUIDItem psdItem = (UUIDItem) psdTree.getParent(item);
        
        // Update PSD view
        PredictorServiceDescription targPSD = reController.getPSDByID( psdItem.getID() );
        if ( targPSD != null )
        {
          currSelPSD = targPSD;
          updatePSDInfoView( currSelPSD );
          
          // Update Event view
          Event targEvent = getEventFromPSDByID( currSelPSD, id );
          
          // If we've already got this event defined, get the details
          Event roEvent = reController.getEventFromCurrentRO( id );
          if ( roEvent != null ) currEditEvent = roEvent;
          
          if ( targEvent != null )
          {
            currBrowseSelectEvent = targEvent;

            // Check Use in-use Event data if in-use
            if ( currEditEvent != null )
              if ( currBrowseSelectEvent.getUuid().equals( currEditEvent.getUuid()) )
              {
                currSelPSD            = inUsePSD;
                currBrowseSelectEvent = currEditEvent;
              }

            // Update event and usage
            updateEventInfoView( currBrowseSelectEvent );
            updateUsageControls();
          }
        }
      }
    }
  }
  
  private Event getEventFromPSDByID( PredictorServiceDescription psd, UUID eventID )
  {
    Event targetEvent = null;
    
    if ( psd != null )
    {
      List<Event> psdEvents = psd.getEvents();
      if ( psdEvents != null )
        for ( Event ev : psdEvents )
          if ( ev.getUuid().equals(eventID) )
          {
            targetEvent = ev;
            break;
          }
    }
    
    return targetEvent;
  }
  
  private void onUseEventClicked()
  {
    if ( currSelPSD != null && currBrowseSelectEvent != null )
    {
      if ( isEventDataValid(currBrowseSelectEvent) )
      {
        inUsePSD      = currSelPSD;
        currEditEvent = currBrowseSelectEvent;
        
        if ( changeData.addEvent( currBrowseSelectEvent ) )
          addEventToCurrentEventView( currBrowseSelectEvent );
        
        // Commit these changes locally
        onCommitLocalChanges();

        // Update PSD, Event and usage info controls
        currPSDLabel.setValue( inUsePSD.getName() );
        currBrowseEventLabel.setValue( currEditEvent.getTitle() );
        
        updateUsageControls();
      }
      else displayMessage( "Please set all configuration/condition parameters first" );
      
    }
    else displayMessage( "Please select a predictor and an event" ); 
  }
  
  // TODO: Not do this. This is the data model's responsibility :(
  private boolean isEventDataValid( Event ev )
  {
    boolean dataValid = true;
    
    if ( ev != null )
    {
      // Check we have a parameter container
      if ( false /*bmn ev.getConfigParams() == null */) dataValid = false;// bmn: it is possible to have event without config params.
      else
      {
        // Check config params
        List<Parameter> configParams = ev.getConfigParams();
        
        if (configParams == null) {
            // not a problem if there are no config params
        }
        else
        {
            for (Parameter configParam : configParams) {
                if (configParam == null) {
                    dataValid = false;
                    break;
                }
                else {
                    if (configParam.getValue() == null) {
                        dataValid = false;
                        break;
                    }
                }
            }
        }
        
        // Check we have an event container
        List<EventCondition> conditions = ev.getEventConditions();
        
        if ( conditions == null ) dataValid = false;
        else
        {
          Iterator<EventCondition> ecIt = conditions.iterator();
          while ( ecIt.hasNext() )
          {
            EventCondition ec = ecIt.next();
            if ( ec == null )
            {
              dataValid = false;
              break;
            } 
            
//            if ( ec.getPreConditionValue() == null )//bmn: again this is not correct
//            {
//              dataValid = false;
//              break;
//            }
            
            if ( ec.getPostConditionValue() == null )
            {
              dataValid = false;
              break;
            }
          }
        }
      }
    }
    
    return dataValid;
  }
  
  private void onActivatePredictionClicked()
  {
    // Only allow active if we've got some events defined
    changeData.setPredictionActive( (Boolean) predictorActiveCB.getValue() ); 
  }
  
  private void onSavedEventSelected( UUIDItem item )
  {
    if ( item != null )
    {
      UUID evID = item.getID();
      
      Event targetEvent = reController.getEventFromCurrentRO( evID );
      if ( targetEvent != null )
      {
        currEventSelID = evID;
        currEventLabel.setValue( targetEvent.getTitle() );
        currEventInfoLabel.setValue( targetEvent.getDescription() );
      }
    }
  }
  
  private void onEditCurrentEvent()
  {
    if ( currEventSelID != null )
    {
      Event targetEvent = reController.getEventFromCurrentRO( currEventSelID );
      if ( targetEvent != null )
      {
        inUsePSD = reController.getPSDByEvent( targetEvent );
        currBrowseSelectEvent = targetEvent;
        currEditEvent         = targetEvent;
        
        updateCurrentEventInfo();
        eventMainSheet.setSelectedTab( 1 );        
      }
    }
  }
  
  private void onCopyCurrentEvent()
  {
    if ( currEventSelID != null )
    {      
      eventCopyTarget = reController.createCopyOfEventFromCurrentRO( currEventSelID );
      if ( eventCopyTarget != null )
      {
        // Ask the user to sub-title this event (modal dialogue) ...
        RiskEditEventCopyView dialog = new RiskEditEventCopyView( window,
                                                                  "(copy)" );
        
        dialog.addListener( this );
        // ... event handling will deal with the rest
      }
    }
  }
  
  private void onRemoveCurrentEvent()
  {
    UUIDItem targetItem = (UUIDItem) currEventList.getValue();
    
    if ( targetItem != null )
    {
      currEventList.removeItem( targetItem );
      
      // Commit removal locally
      changeData.removeEvent( targetItem.getID() );
      onCommitLocalChanges();
    }
  }
  
  // Private event handlers ----------------------------------------------------  
  private class CancelClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onCancelCommit(); }
  }
  
  private class OKClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) 
    { 
      onCommitLocalChanges();
      setVisible( false );
    }
  }
  
  private class UseEventClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onUseEventClicked(); }
  }
  
  private class PredictorSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onPSDBrowseItemSelected( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class PredictionActiveClickListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onActivatePredictionClicked(); }
  }
  
  private class EventSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onSavedEventSelected( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class CurrentEventCopyListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onCopyCurrentEvent(); }
  }
  
  private class CurrentEventEditListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onEditCurrentEvent(); }
  }
  
  private class CurrentEventRemoveListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onRemoveCurrentEvent(); }
  }
}