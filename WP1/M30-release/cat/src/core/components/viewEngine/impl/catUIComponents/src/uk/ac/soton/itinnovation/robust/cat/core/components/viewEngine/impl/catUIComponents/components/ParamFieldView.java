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
//      Created By :            sgc
//      Created Date :          21-Mar-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;




public class ParamFieldView extends AbstractParamView
{
  private TextField valueField;
  
  public ParamFieldView( String title, String desc, String value )
  {
    super ( title, desc );
    
    paramType = eParamType.FIELD_TYPE;
    createComponents( value );
  }
  
  public String getValue()
  { return (String) valueField.getValue(); }
  
  // Private methods -----------------------------------------------------------
  private void createComponents( String defaultValue )
  {   
    valueField = new TextField();
    valueField.setCaption( paramTitle + " value: " );
    valueField.setWidth( "95%" );
    if ( defaultValue != null ) valueField.setValue( defaultValue );
    
    valueField.addListener( new FieldChangeListener() );
    paramContainer.addComponent( valueField );
  }
  
  // Internal event handling ---------------------------------------------------
  private void onFieldChanged()
  { notifyParamChanged(); }
  
  private class FieldChangeListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onFieldChanged(); }
  }
}
