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
//      Created Date :          20-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.treatments.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;

import com.vaadin.ui.*;
import com.vaadin.terminal.ThemeResource;




public class TreatmentEditorView extends SimpleView
{
  public TreatmentEditorView()
  {
    super();
    
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.setWidth( "100%" );
    vl.addStyleName( "catBkgnd" );
    
    createComponents();
  }
  
  @Override
  public void updateView()
  {
    //TODO
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.setHeight( "600px" );
    
    // TODO: Create the editor and remove this!
    Embedded image = new Embedded( "",
                                   ViewResources.CATAPPResInstance.getResource("comingSoon") );
    
    vl.addComponent( image );
    vl.setComponentAlignment( image, Alignment.MIDDLE_CENTER );
  }
}
