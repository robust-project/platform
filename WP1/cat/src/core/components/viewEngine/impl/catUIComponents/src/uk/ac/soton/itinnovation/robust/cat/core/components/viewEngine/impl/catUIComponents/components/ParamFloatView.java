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
//      Created Date :          20 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.ComponentValueChangeListener;

import com.vaadin.ui.*;
import com.vaadin.ui.Slider.*;
import com.vaadin.data.Property;

import java.util.*;





public class ParamFloatView extends AbstractParamView
                            implements ComponentValueChangeListener
{
  private LabelledComponent thresholdComponent;
  private NativeSelect      thresholdSelect;
  private LabelledSlider    paramSlider;
 
  private EnumMap<eNumericThreshold, UUIDItem> thresholdItems;
  private eNumericThreshold currThreshold = eNumericThreshold.NO_THRESHOLD;
  
  public enum eNumericThreshold { NO_THRESHOLD,
                                  LESS,
                                  LESS_EQUAL,
                                  EQUAL,
                                  GREATER_EQUAL,
                                  GREATER };
  
  public ParamFloatView( String title, String desc, String unit,
                         float min, float value, float max )
  {
    super( title, desc );
    
    paramType = eParamType.FLOAT_TYPE;
    thresholdItems = new EnumMap<eNumericThreshold, UUIDItem>( eNumericThreshold.class );
    
    // Don't display 'nulls'
    if ( unit == null ) unit = "";
    
    createComponents( unit, min, value, max );
  }
  
  public void setAllowThreshold( boolean allow )
  {
    if ( allow )
      thresholdComponent.setVisible( true );
    else
      thresholdComponent.setVisible( false );
  }
  
  public double getValue()
  { return paramSlider.getValue(); }
  
  public eNumericThreshold getThreshold()
  { return currThreshold; }
  
  public void setThreshold( eNumericThreshold thresh )
  {
    UUIDItem item = thresholdItems.get( thresh );
    if ( item != null )
      thresholdSelect.select( item );
  }
  
  @Override
  public void setWidth( int pixels )
  {
    super.setWidth( pixels );
    paramSlider.setWidth( pixels );
  }
  
  // ComponentValueChangeListener ----------------------------------------------
  @Override
  public void onCATCompValueChanged( IUFView compView )
  { notifyParamChanged(); }
  
  // Private methods -----------------------------------------------------------
  private void createComponents( String unit, float min, float value, float max )
  {
    // Parameter slider
    paramSlider = new LabelledSlider( unit, min, value, max );
    paramSlider.addListener( this );
    Component comp = (Component) paramSlider.getImplContainer();
    paramContainer.addComponent( comp );

    // Space
    paramContainer.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    // Threshold selector
    thresholdSelect = new NativeSelect();
    thresholdSelect.setWidth( "100px" );
    thresholdSelect.setImmediate( true );
    thresholdSelect.addListener( new ThesholdChangeListener() );
    
    createThresholdItem( "Less than"       , eNumericThreshold.LESS );
    createThresholdItem( "Less/equal to"   , eNumericThreshold.LESS_EQUAL );
    createThresholdItem( "Equal to"        , eNumericThreshold.EQUAL );
    createThresholdItem( "Greater/equal to", eNumericThreshold.GREATER_EQUAL );
    createThresholdItem( "Greater than"    , eNumericThreshold.GREATER );
    
    thresholdComponent = new LabelledComponent( "Fire when:", "80px", thresholdSelect );
    comp = (Component) thresholdComponent.getImplContainer();
    paramContainer.addComponent( comp );
    paramContainer.setComponentAlignment( comp, Alignment.MIDDLE_LEFT );
  }
  
  private void createThresholdItem( String label, eNumericThreshold thresh )
  {
    UUIDItem item = new UUIDItem( label );
    item.setData( thresh );
    thresholdItems.put( thresh, item );
    thresholdSelect.addItem( item );
  }
  
  // Internal event handlers ---------------------------------------------------
  private void onThresholdChanged( UUIDItem item )
  {
    if ( item != null )
      currThreshold = (eNumericThreshold) item.getData();
    
    notifyParamChanged();
  }
  
  private class ThesholdChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onThresholdChanged( (UUIDItem) vce.getProperty().getValue() ); }
  }
}

