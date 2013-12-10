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
//      Created Date :          09-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.simulation;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;

import com.vaadin.ui.*;




class SimulationCentreMainView extends SimpleView
{
  private VerticalLayout simCtrlPanelHolder;
  
  
  // Protected methods ---------------------------------------------------------
  protected SimulationCentreMainView()
  {
    createComponents();
  }
  
  protected void addControlPanelView( SimulationControlPanelView view )
  {
    Component comp = (Component) view.getImplContainer();
    if ( comp != null ) simCtrlPanelHolder.addComponent( comp );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    
    ActiveTitleBar atb = new ActiveTitleBar( "CAT Simulation centre" );
    vl.addComponent( (Component) atb.getImplContainer() );
    
    simCtrlPanelHolder = new VerticalLayout();
    simCtrlPanelHolder.setWidth( "100%" );
    vl.addComponent( simCtrlPanelHolder );
  }
}
