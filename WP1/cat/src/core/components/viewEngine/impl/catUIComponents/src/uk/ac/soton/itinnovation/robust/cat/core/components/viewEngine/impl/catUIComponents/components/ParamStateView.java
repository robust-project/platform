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

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.ui.*;
import com.vaadin.data.Property;
import java.util.*;




public class ParamStateView extends AbstractParamView
{
  private NativeSelect    stateSelect;
  private HashSet<String> knownStates;
  
  public ParamStateView( String title, String desc, String stateLabel )
  {
    super( title, desc );
    
    paramType = eParamType.STATE_TYPE;
    knownStates = new HashSet<String>();
    
    createComponent( stateLabel );
  }
  
  public String getStateValue()
  {
    if ( stateSelect.isVisible() )
      return (String) stateSelect.getValue();
    
    return null; // No value if not visible
  }
  
  public void setStates( Set<String> states )
  {
    if ( states != null )
    {
      knownStates.clear();
      knownStates.addAll( states );
      
      setStatesSelection( stateSelect, states );
      stateSelect.setVisible( true );
    }
  }
  
  public void setCurrentState( String state )
  {
    if ( state != null && knownStates.contains(state) )
      stateSelect.setValue( state );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponent( String stateLabel )
  {
    StateSelectListener genericSSListener = new StateSelectListener();
    
    // Space
    paramContainer.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    stateSelect = new NativeSelect();
    LabelledComponent lc = new LabelledComponent( stateLabel, "80px", stateSelect );
    paramContainer.addComponent( (Component) lc.getImplContainer() );
    stateSelect.setNullSelectionAllowed( false );
    stateSelect.setWidth( "128px" );
    stateSelect.setImmediate( true  );
    stateSelect.setVisible( false ); // Do not diplay until values are set
    stateSelect.addListener( genericSSListener );
  }
  
  private void setStatesSelection( NativeSelect ns, Set<String> states )
  {
    ns.removeAllItems();
    
    for ( String state : states )
      ns.addItem( state );
  }
  
  // Internal event handling ---------------------------------------------------
  private void onStateSelectionChanged()
  { notifyParamChanged(); }
  
  private class StateSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onStateSelectionChanged(); }
  }
}