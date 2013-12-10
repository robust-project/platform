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
//      Created Date :          2011-09-19
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.terminal.*;
import com.vaadin.ui.*;


public class StatusLabel extends SimpleView
{
  private static Resource defaultIconRes = null;
  
  private String   statusLabel;
  private Embedded statusIcon;
  
  public StatusLabel( String labelVal )
  {
    super();
    
    if ( StatusLabel.defaultIconRes == null )
      StatusLabel.defaultIconRes = new ThemeResource( "../runo/icons/16/ok.png" );
    
    statusLabel = labelVal;
    
    createComponent();
  }
  
  public void setIcon( Resource iconResource )
  { statusIcon.setIcon( iconResource ); }
  
  // Private methods -----------------------------------------------------------
  private void createComponent()
  {
    viewContents.setHeight( "16px" );
    
    viewContents.addComponent( UILayoutUtil.createSpace( "8px", null, true) );
    
    // Icon
    statusIcon = new Embedded();
    statusIcon.setWidth ( "24px" );
    statusIcon.setHeight( "24px" );
    statusIcon.setIcon( StatusLabel.defaultIconRes );
    viewContents.addComponent( statusIcon );
    
    // Label
    Label label = new Label( statusLabel );
    label.setWidth( "100%" );
    viewContents.addComponent( label );
  }
}