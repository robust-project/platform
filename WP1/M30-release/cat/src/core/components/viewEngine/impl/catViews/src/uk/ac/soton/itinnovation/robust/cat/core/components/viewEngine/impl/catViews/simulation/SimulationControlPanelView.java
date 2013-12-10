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

import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.ISimulationController;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;

import java.text.SimpleDateFormat;

import com.vaadin.ui.*;
import java.util.*;




class SimulationControlPanelView extends SimpleView
{
  protected transient ISimulationController simController;
  
  protected Panel  parameterHolder;
  protected Button startButton;
  
  protected HashMap<UUID, AbstractParamView> paramViews; // Indexed by Parameter ID
  

  // Protected methods ---------------------------------------------------------  
  protected SimulationControlPanelView( ISimulationController ctrl )
  {
    super();
    
    simController = ctrl;
    paramViews = new HashMap<UUID, AbstractParamView>();
    
    createComponents();
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
  
  protected void onSimulationCompleted()
  {
    startButton.setEnabled( true );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    
    vl.setWidth( "100%" );
    vl.addStyleName( "catBkgndLight" );
    
    vl.addComponent( UILayoutUtil.createSpace("20px", null) );
    
    Label label = new Label( simController.getName() );
    label.addStyleName( "catSubHeadlineFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    // Spaces
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    HorizontalLayout hl = new HorizontalLayout();
    hl.addStyleName( "catBkgnd" );
    hl.addStyleName( "catBorder" );
    vl.addComponent( hl );
    
    hl.addComponent( createInfoControlView() );
    
    hl.addComponent( UILayoutUtil.createSpace("8px", null, true) );
    
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
    innerVL.setWidth( "400px" );
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
    startButton.addListener( new StartButtonListener() );
    startButton.setImmediate( true );
    innerHL.addComponent( startButton );
    innerHL.setComponentAlignment( startButton, Alignment.MIDDLE_RIGHT );
    
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    return vl;
  }
  
  private Component createParameterView()
  {
    VerticalLayout vl = new VerticalLayout();
    vl.setWidth( "420px" );
    
    Label label = new Label( "Simulation configuration" );
    label.addStyleName( "catSectionFont" );
    label.addStyleName( "catBlue" );
    vl.addComponent( label );
    
    // Spaces
    Component spacer = UILayoutUtil.createSpace("4px", "catBorderBottom");
    spacer.setWidth( "100%" );
    vl.addComponent( spacer );
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    parameterHolder = new Panel();
    parameterHolder.setWidth( "100%" );
    parameterHolder.setHeight( "400px" );
    parameterHolder.addStyleName( "borderless bubble" );
    vl.addComponent( parameterHolder );
    
    return vl;
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
    startButton.setEnabled( false );
    
    List<SimulationViewListener> listeners = getListenersByType();
    for ( SimulationViewListener svl : listeners )
      svl.onUserClickedSimulationStart( simController );
  }
  
  private class StartButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onStartButtonClicked(); }
  }
}

