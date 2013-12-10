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
//      Created Date :          09-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.simulation;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.SimulationStatus;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.ISimulationController;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;

import java.text.SimpleDateFormat;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import java.util.*;




class SimulationControlPanelView extends SimpleView
{
  protected transient ISimulationController simController;
  
  protected Panel  parameterHolder;
  protected Button startButton;
  protected Button stopButton;
  protected Button viewResultsButton;
  protected Button refreshButton;
  
  protected HashMap<UUID, AbstractParamView> paramViews; // Indexed by Parameter ID  
  
  private Label simulationStatusLabel;
  private Table simulationHistoryTable;
  
  private static final String ID  = "ID";
  private static final String STATUS  = "Status";
  private static final String START_DATE  = "Start Date";
  private static final String END_DATE  = "End Date";
  
  private String selectedSimulationID;
  private boolean enableOrDisableButtonsAllowed = true;

  // Protected methods ---------------------------------------------------------  
  protected SimulationControlPanelView( ISimulationController ctrl )
  {
    super();
    
    simController = ctrl;
    paramViews = new HashMap<UUID, AbstractParamView>();
    
    createComponents();
  }
  
  protected String getName() {
      return simController.getName();
  }
  
  protected void addParameterGroupTitle( String title )
  {
    if ( title != null )
    {
      parameterHolder.addComponent( UILayoutUtil.createSpace("4px", null) );
      
      Label label = new Label( title );
      label.addStyleName( "catSectionFont" );
      
      parameterHolder.addComponent( label );
      parameterHolder.addComponent( UILayoutUtil.createSpace("2px", null) );
    }
  }
  
  protected void addParameter( Parameter param )
  {
    if ( param != null )
    {
      if (param instanceof UIParameter) {
          // Check if parameter should be displayed
          UIParameter uiParam = (UIParameter)param;
          if ( ! uiParam.isVisible() )
              return;
      }
      
      AbstractParamView apv = null;
      
      switch ( param.getType() )
      {
        case INT :
        case FLOAT : apv = createNumericParamView( param ); break;
            
        case STRING :
        {
          if ( param.getUnit().equals("date") )
            apv = createDateParamView( param );
          else
            apv = createFieldParamView( param );
          
        } break;
      }
      
      VerticalLayout pVL = new VerticalLayout();
      pVL.addStyleName( "catBorder" );
      pVL.setWidth( "360px" );
      apv.setWidth( 320 );
      pVL.addComponent( (Component) apv.getImplContainer() );
      
      paramViews.put( param.getUUID(), apv );
      parameterHolder.addComponent( pVL );
      
      // Space
      parameterHolder.addComponent( UILayoutUtil.createSpace("2px", null) );
    }
  }
  
  protected void clearParameters() {
      //createParamHolder();
      clearParamHolder();
  }
  
  /*
  ParameterValue getParamValueString( UUID paramID )
  {
    ParameterValue value = null;
    
    if ( paramID != null )
    {
      AbstractParamView abv = paramViews.get( paramID );
      if ( abv != null )
      {
        switch ( abv.getParamType() )
        {
          case FLOAT_TYPE:
          {
            ParamFloatView pfv = (ParamFloatView) abv;
            value = new ParameterValue( (String) new Double( pfv.getValue() ).toString() );
          } break;
            
          case FIELD_TYPE:
          {
            ParamFieldView pfv = (ParamFieldView) abv;
            value = new ParameterValue( pfv.getValue() );
          } break;
            
          case DATE_TYPE:
          {
            ParamDateView pdv = (ParamDateView) abv;
            value = new ParameterValue( pdv.getSimpleFormatDateValue() );
          } break;
        }
      }
    }
    
    return value;
  }
  */

    public UIParameter updateParamFromFields(Parameter param) {
        UIParameter newParam = new UIParameter(param);
        ParameterValue value = null;

        if (param != null) {
            AbstractParamView abv = paramViews.get(param.getUUID());

            if (abv != null) {
                switch (abv.getParamType()) {
                    case FLOAT_TYPE: {
                        ParamFloatView pfv = (ParamFloatView) abv;
                        value = new ParameterValue((String) new Double(pfv.getValue()).toString());
                    }
                    break;

                    case FIELD_TYPE: {
                        ParamFieldView pfv = (ParamFieldView) abv;
                        value = new ParameterValue(pfv.getValue());
                    }
                    break;

                    case DATE_TYPE: {
                        ParamDateView pdv = (ParamDateView) abv;
                        value = new ParameterValue(pdv.getSimpleFormatDateValue());
                    }
                    break;
                }

                newParam.setValue(value);
            } else {
                // Assume parameter must be invisible, so don't update value, but set to non-visible
                newParam.setVisible(false);
            }
        }

        return newParam;
    }

    @Override
    public void updateView() {
        List<SimulationViewListener> listeners = getListenersByType();
        for (SimulationViewListener svl : listeners) {
            svl.onUpdateView(simController);
        }
    }

    @Override
    public void resizeView() {
        super.resizeView(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
  
  protected void onSimulationCompleted()
  {
    startButton.setEnabled( true );
    onSimulationHistoryTableItemSelected(selectedSimulationID);
  }
  
  public void setSimulationStatusLabel(String status) {
      simulationStatusLabel.setValue(status);
  }
  
  public void addSimulationResultToHistory(SimulationStatus simulationStatus) {
    simulationHistoryTable.addItem(
              new Object[] { simulationStatus.getId(), simulationStatus.getStatus(), simulationStatus.getStartDateString(), simulationStatus.getEndDateString() }, simulationStatus.getId() );
    if (simulationStatus.getStatus().equals("started")) {
        simulationHistoryTable.select(simulationStatus.getId());
    }
  }

  public void updateSimulationStatus(SimulationStatus simulationStatus) {
      System.out.println(simulationStatus.getId() + " status: " + simulationStatus.getStatus() + " started: " + simulationStatus.getStartDate() + " finished: " + simulationStatus.getEndDate());
      String id = simulationStatus.getId();
      Item tableRow = simulationHistoryTable.getItem(id);
      
      Property statusProp = tableRow.getItemProperty(STATUS);
      Property startDateProp = tableRow.getItemProperty(START_DATE);
      Property endDateProp = tableRow.getItemProperty(END_DATE);
      
      statusProp.setValue(simulationStatus.getStatus());
      startDateProp.setValue(simulationStatus.getStartDateString());
      endDateProp.setValue(simulationStatus.getEndDateString());
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    
    vl.setWidth( "100%" );
    vl.addStyleName( "catBkgndLight" );
    
    /*
    vl.addComponent( UILayoutUtil.createSpace("20px", null) );
    
    Label label = new Label( simController.getName() );
    label.addStyleName( "catSubHeadlineFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    // Spaces
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    */
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.addStyleName( "catBkgnd" );
    hl.addStyleName( "catBorder" );
    vl.addComponent( hl );
    
    hl.addComponent( createInfoControlView() );
    
    //hl.addComponent( UILayoutUtil.createSpace("8px", null, true) );

    // Spaces
    hl.addComponent( UILayoutUtil.createSpace( "4px", null, true ) );
    Component comp = (Component) UILayoutUtil.createSpace( "2px", 
                                                 "catVertRule",
                                                 true );
    comp.setHeight( "100%" );
    hl.addComponent( comp );
    
    hl.addComponent( createParameterView() );
  }
  
  private Component createInfoControlView()
  {
    VerticalLayout vl = new VerticalLayout();
    vl.addComponent( UILayoutUtil.createSpace("18px", null) );
    
    HorizontalLayout hl = new HorizontalLayout();
    vl.addComponent( hl );
    hl.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    VerticalLayout innerVL = new VerticalLayout();
    innerVL.setWidth( "770px" );
    hl.addComponent( innerVL );
    
    innerVL.addComponent( UILayoutUtil.createSpace("16px", null) );
    
    Label label = new Label( simController.getDescription() );
    label.addStyleName( "small" );
    innerVL.addComponent( label );
    
    // Space
    innerVL.addComponent( UILayoutUtil.createSpace("24px", null) );
    Component spacer = UILayoutUtil.createSpace("4px", null );
    spacer.addStyleName( "catBorderBottom" );
    spacer.setWidth( "100%" );
    innerVL.addComponent( spacer );
    innerVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    HorizontalLayout innerHL = new HorizontalLayout();
    innerVL.addComponent( innerHL );
    innerVL.setComponentAlignment( innerHL, Alignment.BOTTOM_LEFT );
    
    startButton = new Button( "Start simulation" );
    startButton.setDescription("Start simulating the effect of the policy on your community. You can tweak the policy parameters and simulate again if needed.");
    startButton.addListener( new StartButtonListener() );
    startButton.setImmediate( true );
    startButton.setEnabled(false);
    innerHL.addComponent( startButton );
    innerHL.setComponentAlignment( startButton, Alignment.MIDDLE_RIGHT );
    
    stopButton = new Button( "Stop simulation" );
    stopButton.setDescription("Stop simulation");
    stopButton.addListener( new StopButtonListener() );
    stopButton.setImmediate( true );
    stopButton.setEnabled(false);
    innerHL.addComponent( stopButton );
    innerHL.setComponentAlignment( stopButton, Alignment.MIDDLE_RIGHT );
    
    innerVL.addComponent( UILayoutUtil.createSpace("24px", null) );
    
    simulationStatusLabel = new Label();
    //simulationStatusLabel.addStyleName( "small" );
    innerVL.addComponent( simulationStatusLabel );
    
    innerVL.addComponent( UILayoutUtil.createSpace("12px", null) );
    
    //Simulation history table
    simulationHistoryTable = new Table( "Previous simulations" );
    simulationHistoryTable.setWidth( "765px" );
    simulationHistoryTable.setHeight( "150px" );
    simulationHistoryTable.addContainerProperty( ID, String.class, null );
    simulationHistoryTable.addContainerProperty( STATUS, String.class, null );
    simulationHistoryTable.addContainerProperty( START_DATE, String.class, null );
    simulationHistoryTable.addContainerProperty( END_DATE, String.class, null );
    simulationHistoryTable.setColumnWidth(ID, 210);
    simulationHistoryTable.setColumnWidth(STATUS, 80);
    simulationHistoryTable.setColumnWidth(START_DATE, 210);
    simulationHistoryTable.setColumnWidth(END_DATE, 210);
    simulationHistoryTable.setImmediate( true );
    simulationHistoryTable.setSelectable( true );
    simulationHistoryTable.setMultiSelect( false );
    simulationHistoryTable.addListener(new SimulationHistoryTableListener());
    innerVL.addComponent( simulationHistoryTable );
    
    viewResultsButton = new Button( "View Results" );
    viewResultsButton.setDescription("View results of simulation");
    //viewResultsButton.setStyleName(BaseTheme.BUTTON_LINK);
    viewResultsButton.addListener( new ViewResultsButtonListener() );
    viewResultsButton.setImmediate( true );
    //viewResultsButton.setVisible(false);
    viewResultsButton.setEnabled(false);
    innerVL.addComponent( viewResultsButton );

    innerVL.addComponent( UILayoutUtil.createSpace("12px", null) );
    
    return vl;
  }
  
  private Component createParameterView()
  {
    VerticalLayout paramViewVL = new VerticalLayout();
    paramViewVL.setWidth( "420px" );
    
    Label label = new Label( "Simulation configuration" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    paramViewVL.addComponent( label );
    
    // Spaces
    Component spacer = UILayoutUtil.createSpace("4px", "catBorderBottom");
    spacer.setWidth( "100%" );
    paramViewVL.addComponent( spacer );
    paramViewVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    createParamHolder();
    paramViewVL.addComponent( parameterHolder );
    
    // Space
    paramViewVL.addComponent( UILayoutUtil.createSpace("24px", null) );
    spacer = UILayoutUtil.createSpace("4px", null );
    spacer.addStyleName( "catBorderBottom" );
    spacer.setWidth( "100%" );
    paramViewVL.addComponent( spacer );
    paramViewVL.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    HorizontalLayout innerHL = new HorizontalLayout();
    paramViewVL.addComponent( innerHL );
    paramViewVL.setComponentAlignment( innerHL, Alignment.BOTTOM_LEFT );
    
    refreshButton = new Button( "Refresh" );
    refreshButton.setDescription("Refresh view");
    refreshButton.addListener( new RefreshButtonListener() );
    refreshButton.setImmediate( true );
    innerHL.addComponent( refreshButton );
    innerHL.setComponentAlignment( refreshButton, Alignment.MIDDLE_RIGHT );

    paramViewVL.addComponent( UILayoutUtil.createSpace("4px", null) );

    return paramViewVL;
  }
  
  private void createParamHolder() {
      parameterHolder = new Panel();
      parameterHolder.setWidth("100%");
      parameterHolder.setHeight("400px");
      parameterHolder.addStyleName("borderless bubble");
  }
  
  private void clearParamHolder() {
      parameterHolder.removeAllComponents();
  }
  
  private AbstractParamView createNumericParamView( Parameter param )
  {
    // Try extracting data from the parameter
    Float pMin = new Float(0.0f);
    Float pMax = new Float(1.0f);
    Float pVal;
    try
    {
      pMin = new Float( Float.parseFloat( param.getMin() ) );
      pMax = new Float( Float.parseFloat( param.getMax() ) );
      pVal = new Float( Float.parseFloat( param.getValue().getValue()) );
    }
    catch( Exception e )
    {
      // pVal setting is essential to ensure it is in range
      pVal = pMax;
    }

    String paramName = param.getName();
    ParamFloatView fvv = new ParamFloatView( paramName,
                                             param.getDescription(),
                                             param.getUnit(),
                                             pMin,
                                             pVal,
                                             pMax );
    fvv.setAllowThreshold( false );
    
    return fvv;
  }
  
  private AbstractParamView createFieldParamView( Parameter param )
  {
    String paramName = param.getName();

    ParamFieldView pfv = new ParamFieldView( paramName,
                                             param.getDescription(),
                                             param.getValue().getValue() );
 
    return pfv;
  }
  
  private AbstractParamView createDateParamView( Parameter param )
  {
    String paramName = param.getName();
    
    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd.H" );
    Date paramDate = new Date();
    
    try { paramDate = sdf.parse( param.getValue().getValue() ); }
    catch (Exception e ) {}

    ParamDateView pdv = new ParamDateView( paramName,
                                           param.getDescription(),
                                           paramDate );
 
    return pdv;
  }
  
  // Private event handlers ----------------------------------------------------
  private void onStartButtonClicked()
  {
    enableOrDisableButtonsAllowed = true;
    disableStartSimulationButton();
    enableOrDisableButtonsAllowed = false;
    //viewResultsButton.setVisible(false);
    
    List<SimulationViewListener> listeners = getListenersByType();
    for ( SimulationViewListener svl : listeners )
      svl.onUserClickedSimulationStart( simController );
    
    enableOrDisableButtonsAllowed = true;
  }

  private void onStopButtonClicked()
  {
    stopButton.setEnabled( false );
    
    List<SimulationViewListener> listeners = getListenersByType();
    for ( SimulationViewListener svl : listeners )
      svl.onUserClickedSimulationStop( simController );
    
    startButton.setEnabled( true );
  }

  private void onRefreshButtonClicked()
  {
      updateView();
  }
  
  private void onViewResultsButtonClicked() {
      //viewResultsButton.setEnabled(false);
      
      if (selectedSimulationID == null)
          return;
      
      List<SimulationViewListener> listeners = getListenersByType();
      for (SimulationViewListener svl : listeners) {
          svl.onUserClickedViewResults(simController, selectedSimulationID);
      }
  }

    public void enableStartSimulationButton() {
        if (! enableOrDisableButtonsAllowed)
            return;
        
        startButton.setEnabled( true );
        stopButton.setEnabled( false );
    }
  
    public void disableStartSimulationButton() {
        if (! enableOrDisableButtonsAllowed)
            return;
        
        startButton.setEnabled( false );
        stopButton.setEnabled( true );
    }

    //protected void reportError(String message) {
    //}
  private void onSimulationHistoryTableItemSelected(String simulationID) {
      selectedSimulationID = simulationID;
      
      if (simulationID == null) {
          viewResultsButton.setEnabled(false);
      }
      else {
          Item tableRow = simulationHistoryTable.getItem(simulationID);

          Property statusProp = tableRow.getItemProperty(STATUS);
          String status = (String) statusProp.getValue();
          if (status.equals("completed")) {
              viewResultsButton.setEnabled(true);
          }
          else {
              viewResultsButton.setEnabled(false);
          }
      }
      
      simController.onSimulationHistoryTableItemSelected(simulationID);

  }
  
  private class StartButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onStartButtonClicked(); }
  }
  
  private class StopButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onStopButtonClicked(); }
  }
  
  private class RefreshButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onRefreshButtonClicked(); }
  }
  
  private class ViewResultsButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onViewResultsButtonClicked(); }
  }
  
  private class SimulationHistoryTableListener implements Table.ValueChangeListener {
      @Override
      public void valueChange(Property.ValueChangeEvent vce) {
          Object selectedItems = vce.getProperty().getValue();
          String simulationID = (String) selectedItems;
          onSimulationHistoryTableItemSelected(simulationID);
      }
  }
}

