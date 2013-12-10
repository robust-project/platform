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
//      Created Date :          2011-10-27
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.RiskEditorMainController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.ScheduleViewCD;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UUIDItem;

import uk.ac.soton.itinnovation.robust.riskmodel.*;

import com.vaadin.ui.*;
import com.vaadin.data.Property;

import java.util.*;
import java.util.Map.Entry;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.RiskEditCommitListener;




public class RiskEditScheduleView extends WindowView
{
  private RiskEditorMainController reController;
  
  private TextField catFreqField;
  private ComboBox  catPeriodCB;
  
  private Table     humanReviewTable;
  private TextField humanFreqField;
  private ComboBox  humanPeriodCB;
  private CheckBox  humanNotifCB;
  
  private UIValueChangeListener genericChangeListener;
  
  private List<UUIDItem> catPeriods;
  private List<UUIDItem> humanPeriods;
  
  private transient ScheduleViewCD changeData;
  
  
  public RiskEditScheduleView( Component parent, RiskEditorMainController ctrl )
  {
    super( parent, "" );
    
    reController = ctrl;
    
    // Size & position window
    window.setWidth( "550px" );
    window.setHeight( "550px" );
    centreWindow();
    
    catPeriods   = createPeriodItems();
    humanPeriods = createPeriodItems();
    changeData = new ScheduleViewCD();
    
    createComponents();
  }
  
  @Override
  public void updateView()
  {
    setHeadline( "Schedule: " + reController.getCurrentROTitle() );
    
    changeData.reset();
    updateComponents();
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    genericChangeListener = new UIValueChangeListener();
            
    VerticalLayout vl = (VerticalLayout) window.getContent();
    
    // Headline
    vl.addComponent( createHeadline("Unknown r/o") );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("12px", null) );
    
    // CAT review component
    vl.addComponent( createCATReviewComponents() );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("16px", null) );
    
    // Human review component
    vl.addComponent( createHumanReviewComponents() );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    // Discard/save buttons
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
    hl.addComponent( UILayoutUtil.createSpace("8px", null, true) );
  }
  
  private Component createCATReviewComponents()
  {
    VerticalLayout vl = new VerticalLayout();
    
    Label label = new Label( "CAT review" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Temporary image for probability graph
    Embedded graph = new Embedded( "Event prediction graph" );
    graph.setWidth( "100%" );
    graph.setHeight( "120px" );
    graph.addStyleName( "catBkgnd" );
    graph.addStyleName( "catBorder" );
    vl.addComponent( graph );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Anaylze components
    HorizontalLayout hl = new HorizontalLayout();
    vl.addComponent( hl );
    label = new Label( "Analyze risk every: " );
    hl.addComponent( label );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true)  );
    
    // CAT count field
    catFreqField = new TextField();
    catFreqField.setValue( "0" );
    catFreqField.setWidth( "40px" );
    catFreqField.setImmediate( true );
    catFreqField.addListener( genericChangeListener );
    hl.addComponent( catFreqField );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true)  );
    
    // CAT Unit CB
    catPeriodCB = new ComboBox();
    catPeriodCB.setWidth( "80px" );
    for ( UUIDItem item : catPeriods )
      catPeriodCB.addItem( item );
    
    catPeriodCB.setImmediate( true );
    catPeriodCB.addListener( new CATPeriodListener() );
        
    hl.addComponent( catPeriodCB );
     
    return vl;
  }
  
  private Component createHumanReviewComponents()
  {
    VerticalLayout vl = new VerticalLayout();
    
    Label label = new Label( "Human review" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Human review log
    humanReviewTable = new Table( "Human review history" );
    humanReviewTable.addContainerProperty( "Time", String.class, null );
    humanReviewTable.addContainerProperty( "Date", String.class, null );
    humanReviewTable.addContainerProperty( "Review result", String.class, null );
    humanReviewTable.setWidth( "100%" );
    humanReviewTable.setHeight( "120" );
    humanReviewTable.setColumnReorderingAllowed( true );
    vl.addComponent( humanReviewTable );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null)  );
    
    // Review components
    HorizontalLayout hl = new HorizontalLayout();
    vl.addComponent( hl );
    label = new Label( "Request human review of risk every: " );
    hl.addComponent( label );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true)  );
    
    // CAT count field
    humanFreqField = new TextField();
    humanFreqField.setWidth( "40px" );
    humanFreqField.setImmediate( true );
    humanFreqField.addListener( genericChangeListener );
    hl.addComponent( humanFreqField );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true)  );
    
    // CAT Unit CB
    humanPeriodCB = new ComboBox();
    humanPeriodCB.setWidth( "80px" );
    for ( UUIDItem item : humanPeriods )
      humanPeriodCB.addItem( item );
    humanPeriodCB.setImmediate( true );
    humanPeriodCB.addListener( new HumanPeriodListener() );
    
    hl.addComponent( humanPeriodCB );
    
    // Notification
    humanNotifCB = new CheckBox( "e-mail review due notification" );
    humanNotifCB.setImmediate( true );
    humanNotifCB.addListener( genericChangeListener );
    vl.addComponent( humanNotifCB );
    
    return vl;
  }
  
  private List<UUIDItem> createPeriodItems()
  {
    ArrayList<UUIDItem> items = new ArrayList<UUIDItem>();
    
    UUIDItem it = new UUIDItem( "hour(s)" );
    it.setData( Period.HOUR );
    items.add( it );
    
    it = new UUIDItem( "day(s)" );
    it.setData( Period.DAY );
    items.add( it );
    
    it = new UUIDItem( "week(s)" );
    it.setData( Period.WEEK );
    items.add( it );
    
    it = new UUIDItem( "month(s)" );
    it.setData( Period.MONTH );
    items.add( it );
    
    it = new UUIDItem( "year(s)" );
    it.setData( Period.YEAR );
    items.add( it );
    
    return items;
  }
  
  private void updateComponents()
  {
    Entry<Integer, Period> entry = reController.getCurrentROCATReview();
    if ( entry != null )
    {
      // Convert numerical value to string value for text field
      catFreqField.setValue( entry.getKey().toString() );
      
      Period per = entry.getValue();
      if ( per != null )
        catPeriodCB.select( getPeriodCBIndex(catPeriods, per) );
    }
    
    entry = reController.getCurrentROHumanReview();
    if ( entry != null )
    {
      // Convert numerical value to string value for text field
      humanFreqField.setValue( entry.getKey().toString() );
      
      Period per = entry.getValue();
      if ( per != null )
        humanPeriodCB.select( getPeriodCBIndex(humanPeriods, per) );
    }
    
    if ( reController.getCurrentROIsNotifyingUser() )
      humanNotifCB.setValue( true );
    else
      humanNotifCB.setValue( false );
  }
  
  private UUIDItem getPeriodCBIndex( List<UUIDItem> items, Period per )
  {
    int index = -1;
    
    switch ( per )
    {
      case HOUR  : index = 0; break;
      case DAY   : index = 1; break;
      case WEEK  : index = 2; break;
      case MONTH : index = 3; break;
      case YEAR  : index = 4; break;
    }
    
    return items.get( index );
  }  
  
  // Private internal event handlers -------------------------------------------
  private void onCancelCommit()
  {
    setVisible( false );
  }
  
  private void onCommitLocalChanges()
  {
    if ( changeData.isDataChanged() )
    {
      Integer cf = Integer.parseInt( (String) catFreqField.getValue() );
      Integer hf = Integer.parseInt( (String) humanFreqField.getValue() );
      
      // Period changes managed by event handlers
      changeData.setCatReviewFrequency( cf );
      changeData.setHumanReviewFrequency( hf );
      changeData.setNotifyUser( humanNotifCB.booleanValue() );
    } 
    
    Collection<RiskEditCommitListener> listeners = getListenersByType();
    for ( RiskEditCommitListener listener : listeners )
      listener.onCommitScheduleChanges( changeData );
    
    setVisible( false );
  }
  
  private void onUIValueChanged()
  { changeData.forceChanged( true ); }
  
  private void onCATPeriodChanged( UUIDItem item )
  {
    if ( item != null )
      changeData.setCatReviewPeriod( (Period) item.getData() );
  }
  
  private void onHumanPeriodChanged( UUIDItem item )
  {
    if ( item != null)
      changeData.setHumanReviewPeriod( (Period) item.getData() );
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
  
  private class CATPeriodListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onCATPeriodChanged( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class HumanPeriodListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onHumanPeriodChanged( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  // Generic change listener (no immediate model action required)
  private class UIValueChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onUIValueChanged(); }
  }
  
}