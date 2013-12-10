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
//      Created Date :          10-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.InlineDateField;
import java.text.SimpleDateFormat;
import java.util.Date;




public class ParamDateView extends AbstractParamView
{
  private InlineDateField dateField;
  private String          dateSimpleFormatValue;

  public ParamDateView( String title, String desc, Date date )
  {
    super( title, desc );
    
    paramType = eParamType.DATE_TYPE;
    createComponents( date );
    updateSimpleFormatValue( date );
  }
  
  public Date getFullDateValue()
  { return (Date) dateField.getValue(); }
  
  public String getSimpleFormatDateValue()
  { return dateSimpleFormatValue; }
  
  // Private methods -----------------------------------------------------------
  private void createComponents( Date date )
  {
    dateField = new InlineDateField();
    dateField.setResolution( InlineDateField.RESOLUTION_HOUR );
    dateField.setImmediate( true );
    dateField.setValue( date );
    dateField.addListener( new DateListener() );
    
    paramContainer.addComponent( dateField );
    paramContainer.setComponentAlignment( dateField, Alignment.MIDDLE_CENTER );
  }
  
  // Private event handling ----------------------------------------------------
  private void onDateChange( Date date )
  {
    updateSimpleFormatValue( date );
  }
  
  private void updateSimpleFormatValue( Date date )
  {
    if ( date != null )
    {
      SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.d.H" );
      dateSimpleFormatValue = sdf.format( date );
    }
  }
  
  private class DateListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( ValueChangeEvent event )
    { onDateChange( (Date) event.getProperty().getValue() ); }
  }
}
