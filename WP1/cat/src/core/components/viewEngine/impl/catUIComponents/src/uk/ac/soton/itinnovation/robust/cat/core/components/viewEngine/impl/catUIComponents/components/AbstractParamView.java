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
//      Created Date :          21 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.ParamViewListener;

import com.vaadin.ui.*;

import java.util.*;






public abstract class AbstractParamView extends SimpleView
{
  protected eParamType     paramType = eParamType.UNKNOWN_TYPE;
  protected UUID           paramID;
  protected Label          paramTitle;
  protected VerticalLayout paramContainer;
  protected Label          paramDescription;
  protected String         metaData; // Sometimes required to differentiate views
  
  public enum eParamType { UNKNOWN_TYPE,
                           FLOAT_TYPE,
                           FIELD_TYPE,
                           DATE_TYPE,
                           STATE_TYPE,
                           MULTI_VIEW_TYPE };
  
  public eParamType getParamType()
  { return paramType; }
  
  public UUID getID() { return paramID; }
  
  public void setID( UUID pID ) { paramID = pID; }
  
  public String getTitle() { return (String) paramTitle.getValue(); }
  
  public void setMetadata( String data )
  { metaData = data; }
  
  public String getMetadata()
  { return metaData; }
  
  public void setWidth( int pixels )
  { paramContainer.setWidth( pixels, VerticalLayout.UNITS_PIXELS ); }
  
  // Protected methods ---------------------------------------------------------
  protected AbstractParamView( String label, String paramDesc )
  {
    super();
    
    viewContents.setStyleName( "borderless light" );
    paramID = UUID.randomUUID();
    
    createComponents( label, paramDesc );
  }
 
  protected void notifyParamChanged()
  {
    List<ParamViewListener> listeners = getListenersByType();
      if ( !listeners.isEmpty() )
        for ( ParamViewListener listener : listeners )
          listener.onParamValueChanged( this );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents( String pTitle, String pDesc )
  {
    // Space
    viewContents.addComponent( UILayoutUtil.createSpace("2px",null) );
    
    paramTitle = new Label();
    paramTitle.setValue( pTitle );
    paramTitle.addStyleName( "catSubSectionFont" );
    viewContents.addComponent( paramTitle );
    
    Component spacer = UILayoutUtil.createSpace( "2px",null );
    spacer.addStyleName( "catBorderBottom" );
    spacer.setWidth( "100%" );
    viewContents.addComponent( spacer );
    
    // Space
    viewContents.addComponent( UILayoutUtil.createSpace("8px",null) );
    
    // Indented space for parameter controls
    HorizontalLayout hl = new HorizontalLayout();
    hl.addComponent( UILayoutUtil.createSpace("8px", null, true) );
    viewContents.addComponent( hl );
  
    // Parameter container
    paramContainer = new VerticalLayout();
    paramContainer.setWidth( "100%" );
    hl.addComponent( paramContainer );
    
    // Space
    viewContents.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    // Parameter description
    viewContents.addComponent( UILayoutUtil.createSpace("16px", null, true) );
    
    if ( pDesc != null )
    {
      Label label = new Label( "About " + paramTitle + "..." );
      label.addStyleName( "small" );
      viewContents.addComponent( label );

      // Space
      viewContents.addComponent( UILayoutUtil.createSpace("4px", null, true) );

      paramDescription = new Label();
      paramDescription.setStyleName( "tiny" );
      paramDescription.setWidth( "95%" );
      paramDescription.setHeight( "40px" );
      paramDescription.setValue( pDesc );
      viewContents.addComponent( paramDescription );
    }
  }
}