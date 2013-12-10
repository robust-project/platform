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
//      Created By :            sgc
//      Created Date :          21 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;




public class EmptyDashIndicatorView extends AbstractDashIndicatorView
{
  public EmptyDashIndicatorView()
  {
    super();
    createComponent();
  }
  
  // AbstractDashIndicatorView -------------------------------------------------
  @Override
  public String getIndicatorName() { return "empty"; }
  
  @Override
  public ThemeResource getIndicatorIcon()
  { return null; }
  
  // Private methods -----------------------------------------------------------
  private void createComponent()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    
    vl.setSizeUndefined();
    vl.addStyleName( "catDashedBorder" );
   
    Label empty = new Label( "no current indicator" );
    empty.addStyleName( "catCentredText" );
    vl.addComponent( empty );
    vl.setComponentAlignment( empty, Alignment.TOP_CENTER );
  }
}