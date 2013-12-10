/////////////////////////////////////////////////////////////////////////
//
// Â© University of Southampton IT Innovation Centre, 2011
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
//      Created Date :          2011-10-24
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.ComponentValueChangeListener;

import com.vaadin.ui.*;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.data.Property;

import java.util.*;




public class LabelledSlider extends SimpleView
{
  private String    unitLabel;
  private Label     minLabel;
  private Label     maxLabel;
  private Slider    slider;
  private TextField valueField;
  
  public LabelledSlider( String unitLabel,
                         double min,
                         double defaultValue,
                         double max )
  {
    super();
    
    if ( min > max ) min = max;
    if ( defaultValue < min ) defaultValue = min;
    if ( defaultValue > max ) defaultValue = max;
    
    this.unitLabel = unitLabel;
    
    createComponents( min, defaultValue, max );
  }
  
  public double getMin()
  { return slider.getMin(); }
  
  public double getMax()
  { return slider.getMax(); }
  
  public double getValue()
  { return (Double) slider.getValue(); }
  
  public void setMin( double min )
  {
    slider.setMin( min );
    minLabel.setCaption( new Double(min).toString() + unitLabel );
  }
  
  public void setMax( double max )
  {
    slider.setMax( max );
    maxLabel.setCaption( new Double(max).toString() + unitLabel );
  }
  
  public void setValue( double value )
  {
    value = validateValue( value );
    
    valueField.setValue( value );
    
    try { slider.setValue( value ); }
    catch (ValueOutOfBoundsException voob) {}
  }
  
  public void setWidth( int pixels )
  {
    viewContents.setWidth( pixels, VerticalLayout.UNITS_PIXELS );
    slider.setWidth( pixels - 2, VerticalLayout.UNITS_PIXELS );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents( double min, double value, double max ) 
  {  
    HorizontalLayout hl = new HorizontalLayout();
    hl.setWidth( "100%" );
    viewContents.addComponent( hl );
    
    // Min label
    minLabel = new Label( new Float(min).toString() + " " + unitLabel );
    minLabel.addStyleName( "tiny" );
    hl.addComponent( minLabel );
    hl.setComponentAlignment( minLabel, Alignment.MIDDLE_LEFT );
    
    // Space
    Component spacer = UILayoutUtil.createSpace( "40%", null, true );
    hl.addComponent( spacer );
    
    // Value field
    valueField = new TextField();
    valueField.setWidth( "40px" );
    valueField.setValue( value );
    valueField.setImmediate( true );
    valueField.addStyleName( "small" );
    valueField.addStyleName( "catTextAlignCentre" );
    valueField.addListener( new FieldListener() );
    hl.addComponent( valueField );
    hl.setComponentAlignment( valueField, Alignment.MIDDLE_CENTER );
    
    // Space
    spacer = UILayoutUtil.createSpace( "40%", null, true );
    hl.addComponent( spacer );
    
    // Max label
    maxLabel = new Label( new Float(max).toString() + " " + unitLabel );
    maxLabel.addStyleName( "tiny" );
    maxLabel.addStyleName( "catTextAlignRight" );
    hl.addComponent( maxLabel );
    hl.setComponentAlignment( maxLabel, Alignment.MIDDLE_RIGHT);
    
    // Slider
    slider = new Slider();
    slider.setWidth( "100%" );
    slider.setMin( min );
    slider.setMax( max );
    slider.setResolution( 3 );
    slider.setImmediate( true );
    slider.addListener( new SliderListener() );
    
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.addComponent( slider );
    vl.setComponentAlignment( slider, Alignment.BOTTOM_CENTER );
    
    setValue( value );
    
  }
  
  private double validateValue( double value )
  {
    double bound = slider.getMin();
    if ( value < bound )
      value = bound;
    
    bound = slider.getMax();
    if ( value > bound )
      value = bound;
    
    return value;
  }
  
  // Internal event handling ---------------------------------------------------
  private void onSliderChanged()
  {
    // Update field to reflect
    Double value = (Double) slider.getValue();
    
    // Just update field
    valueField.setValue( value.toString() );
    
    List<ComponentValueChangeListener> listeners = getListenersByType();
    if ( !listeners.isEmpty() )
      for ( ComponentValueChangeListener listener : listeners )
        listener.onCATCompValueChanged( this );
  }
  
  private void onFieldChanged()
  {
    Double value = Double.parseDouble( (String) valueField.getValue() );
    
    // Validate
    value = validateValue( value );
    
    // Just update slider
    try { slider.setValue( value ); }
    catch (ValueOutOfBoundsException voob) {}
    
    List<ComponentValueChangeListener> listeners = getListenersByType();
    if ( !listeners.isEmpty() )
      for ( ComponentValueChangeListener listener : listeners )
        listener.onCATCompValueChanged( this );
  }
  
  private class SliderListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange(Property.ValueChangeEvent event)
    { onSliderChanged(); }
  }
  
  private class FieldListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange(Property.ValueChangeEvent event)
    { onFieldChanged(); }
  }
}

