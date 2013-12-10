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

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.ViewResources;

import com.vaadin.ui.*;
import com.vaadin.terminal.*;

import java.net.*;





public class GfxLizerView extends AbstractDashIndicatorView
{
  private Embedded embeddedView;
  
  public GfxLizerView()
  {
    super();
    
    createComponents();
  }
  
  public void updateURL( String url )
  {
    try
    {
      URL polecatURL = new URL( url );
      embeddedView.setSource( new ExternalResource(polecatURL) );
    }
    catch ( MalformedURLException mue )
    { System.out.println("Could not update POLECAT graphic equalizer - URL is malformed"); };
  }
  
  // AbstractDashIndicatorView -------------------------------------------------
  @Override
  public String getIndicatorName() { return "Gfx Equalizer"; }
  
  @Override
  public ThemeResource getIndicatorIcon()
  { return ViewResources.CATAPPResInstance.getResource( "GfxLizerIcon" ); }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    // Title
    Label label = new Label( "Community Graphic Equalizer" );
    label.addStyleName( "catSubHeadlineFont" );
    viewContents.addComponent( label );
    
    // Space
    viewContents.addComponent( UILayoutUtil.createSpace( "4px", null) );
    
    // Embedded IFrame for graphic equalizer
    embeddedView = new Embedded();
    embeddedView.setType( Embedded.TYPE_BROWSER );
    embeddedView.setWidth( "800px" );
    embeddedView.setHeight( "500px" );
    viewContents.addComponent( embeddedView );
  }
}
