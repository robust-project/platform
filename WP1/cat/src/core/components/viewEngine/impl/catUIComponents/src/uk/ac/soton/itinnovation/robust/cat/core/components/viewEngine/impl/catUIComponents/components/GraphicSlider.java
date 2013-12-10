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
//      Created Date :          2011-10-26
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.*;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents.*;
import com.vaadin.event.MouseEvents;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.*;

import java.util.*;
import java.util.Map.Entry;





public class GraphicSlider extends SimpleView
{
  private HorizontalLayout           gfxLayout;
  private Slider                     slider;
  private HashMap<String, Component> sliderGfx;
  private ArrayList<String>          orderedNames;
  private Label                      currSelLabel;
  private int                        currSelIndex;
  private Component                  currGfxComp;
  private boolean                    internalCompUpdate = false;
  private UUID                       dataID;
  
  public GraphicSlider( String title )
  {
    super();
    
    viewContents.setStyleName( "borderless" );
    
    sliderGfx = new HashMap<String, Component>();
    orderedNames = new ArrayList<String>();
    
    createComponents( title );
  }
  
  public GraphicSlider( String title, UUID id )
  {
    super();
    
    viewContents.setStyleName( "borderless" );
    
    sliderGfx = new HashMap<String, Component>();
    orderedNames = new ArrayList<String>();
    
    dataID = id;
    
    createComponents( title );
  }
  
  public UUID getDataID() { return dataID; }
  
  public void setIsSelected( boolean selected )
  {
    if ( selected )
    {
      viewContents.removeStyleName( "catLowlight" );
      viewContents.addStyleName( "catHighlight" );
    }
    else
    {
      viewContents.removeStyleName( "catHighlight" );
      viewContents.addStyleName( "catLowlight" );
    }
  }
  
  public void addItem( String name, ThemeResource res )
  {
    // Remove old item if it already exists
    removeItem( name );
    
    // Current gfx object
    Embedded em = new Embedded();
    em.setSource( res );
    em.addListener( new GfxListener() );
    
    // First item is treated diferently (no spacers etc)
    if ( orderedNames.isEmpty() )
    {
      sliderGfx.put( name, em );
      orderedNames.add( name );
      
      gfxLayout.addComponent( em );
      gfxLayout.setComponentAlignment( em, Alignment.MIDDLE_CENTER );
    }
    else
    {
      sliderGfx.put( name, em );
      orderedNames.add( name );
      
      // Space first
      Component spacer = UILayoutUtil.createSpace( "100%", null, true );
      gfxLayout.addComponent( spacer );
      gfxLayout.setExpandRatio( spacer, 1.0f );
      
      // Then resource
      gfxLayout.addComponent( em );
      gfxLayout.setComponentAlignment( em, Alignment.MIDDLE_CENTER );
      
      // Push slider up
      try { slider.setValue( 1 ); }
      catch (ValueOutOfBoundsException voob) {
          return;
      }
    }
    
    setCurrentItem( name );
  }
  
  public void removeItem( String name )
  {
    // Remove component
    Component comp = sliderGfx.get( name );
    if ( comp != null )
    {
      gfxLayout.removeComponent( comp );
      sliderGfx.remove( name );
      orderedNames.remove( name );
      
      if ( comp == currGfxComp ) currGfxComp = null;
      
      if ( !orderedNames.isEmpty() )
        setCurrentItem( orderedNames.get( orderedNames.size() -1) );
      
      // Push slider up
      try { slider.setValue( 1 ); }
      catch (ValueOutOfBoundsException voob) {
      return;
      }
    }
  }
  
  public int getCurrentItemIndex()
  {
    return currSelIndex;
  }
  
  public String getCurrentItemName()
  {
    return currSelLabel.getCaption();
  }
  
  public Double getSliderValue()
  {
    return (Double) slider.getValue();
  }
  
  public void setCurrentItem( String name )
  {
    if ( sliderGfx.containsKey(name) )
    {
      internalCompUpdate = true;
      
      updateCurrCompLabelGfx( name );
      updateCurrCompSlider( name );
      
      internalCompUpdate = false;
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents( String title )
  {
    // Listen for focus clicks
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.addListener( new CompClickListener() );
    vl.setWidth( "98%" );
    vl.setMargin( false, true, false, true );
    vl.setStyleName( "catBkgnd" );
    
    // Label
    Label label = new Label( title );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    // Current selection label
    currSelLabel = new Label();
    currSelLabel.addStyleName( "tiny" );
    currSelLabel.addStyleName( "catTextAlignCentre" );
    vl.addComponent( currSelLabel );
    vl.setComponentAlignment( currSelLabel, Alignment.MIDDLE_CENTER );
   
    // Gfx layout
    gfxLayout = new HorizontalLayout();
    gfxLayout.setWidth( "100%" );
    gfxLayout.setSpacing( true );
    vl.addComponent( gfxLayout );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("2px", null) );
    
    // Slider
    slider = new Slider();
    slider.setWidth( "100%" );
    slider.setMin( 0.0 );
    slider.setMax( 1.0 );
    slider.setResolution( 2 );
    slider.setImmediate( true );
    slider.addListener( new SliderListener() );
    vl.addComponent( slider );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace( "2px", null) );
    
    setIsSelected( false );
  }
  
  private void updateCurrCompLabelGfx( String name )
  {
    if ( sliderGfx.containsKey(name) )
    {
      if ( currGfxComp != null )
        currGfxComp.removeStyleName( "catHighlight" );
      
      currGfxComp = sliderGfx.get( name );
      currGfxComp.addStyleName( "catHighlight" );
      currSelLabel.setCaption( name );
    }
  }
  
  private void updateCurrCompSlider( String name )
  {
    // Select gfx based on position of slider
    if ( sliderGfx.containsKey(name) )
    {
      // Find index of name and use to position slider
      currSelIndex = 0;
      for ( String n : orderedNames )
      {
        if ( n.equals( name ) ) break;
        currSelIndex++;
      }
      
      // Update slider (stop repeated updates..)
      internalCompUpdate = true;
      
      Double relPos = currSelIndex / (double) ( orderedNames.size() -1);
      try { slider.setValue( relPos ); }
      catch (ValueOutOfBoundsException voob) {return;}
      
      internalCompUpdate = false;
    }
  }
  
  // Internal event handling ---------------------------------------------------
  private void onSliderChange()
  {
    if ( !internalCompUpdate && (orderedNames.size() > 1) )
    {
      // Use slider position to index into slider name
      Double relPos = (Double) slider.getValue() / slider.getMax();
      
      // Offset position to use nicer boundaries between options
      int    nameCount = orderedNames.size();      
      currSelIndex     = (int) ( (relPos * (nameCount -1)) + 0.5 );
      
      updateCurrCompLabelGfx( orderedNames.get( currSelIndex ) );
      
      // Notify change listener
      List<ComponentValueChangeListener> listeners = getListenersByType();
      if ( !listeners.isEmpty() )
        for ( ComponentValueChangeListener listener : listeners )
          listener.onCATCompValueChanged( this );
    }
  }
  
  private void onGfxClicked( Component gfxComp )
  {
    // Run through existing components to find the name of the gfx clicked & update
    Set<Entry<String,Component>> components = sliderGfx.entrySet();
    for ( Entry ec : components )
      if ( ec.getValue() == gfxComp )
      {
        String name = (String) ec.getKey();
        updateCurrCompSlider( name );
        updateCurrCompLabelGfx( name );
        
        // Notify change listener
        List<ComponentValueChangeListener> listeners = getListenersByType();
        if ( !listeners.isEmpty() )
          for ( ComponentValueChangeListener listener : listeners )
            listener.onCATCompValueChanged( this );
          
        break;
      }
  }
  
  private void onControlClicked()
  {
    setIsSelected( true );
    
    List<ComponentSelectedListener> listeners = getListenersByType();
    if ( !listeners.isEmpty() )
      for ( ComponentSelectedListener listener : listeners )
        listener.onCATCompSelected( this );
  }
  
  private class SliderListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce ) { onSliderChange(); }
  }
  
  private class GfxListener implements MouseEvents.ClickListener
  {
    @Override
    public void click( MouseEvents.ClickEvent event) { onGfxClicked( event.getComponent() ); }
  }
  
  private class CompClickListener implements LayoutClickListener
  {
    @Override
    public void layoutClick(LayoutClickEvent event) { onControlClicked(); }
  }
}