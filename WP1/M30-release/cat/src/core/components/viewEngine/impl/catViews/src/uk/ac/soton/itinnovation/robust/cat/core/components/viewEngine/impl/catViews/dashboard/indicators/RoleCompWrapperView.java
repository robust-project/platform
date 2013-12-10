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
//      Created Date :          30-Oct-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.ViewResources;

import pl.swmind.robust.webapp.integration.RoleCompositionController;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import java.util.Date;




public class RoleCompWrapperView extends AbstractDashIndicatorView
{
  private RoleCompositionController smindController;
  private String styleName = "catDashC1";
  
  public RoleCompWrapperView()
  {
    super();
    
    createComponents();
  }
  
  public void updateView( String platformId, String communityID, Date startDate )
  {
    viewContents.removeAllComponents();
    
    // Get the view - currently only using community ID - date to do?
    try
    {
      IUFView smindView = getRoleCompositionView( platformId, communityID, startDate);
    
      if ( smindView != null )
      {
        AbstractLayout al = (AbstractLayout) smindView.getImplContainer();
        if ( al != null )
          viewContents.addComponent( al );
      }
    }
    catch ( Exception e )
    {
      Label label = new Label("Role Composition Visualisation currently unavailable: " + e.getMessage() );
      label.addStyleName( "medium" );
      viewContents.addComponent( label );
    }
    
  }
  
  // IDashIndicatorView --------------------------------------------------------
  @Override
  public String getIndicatorName()
  {
    return "Role Composition Visualisation";
  }
  
  @Override
  public ThemeResource getIndicatorIcon()
  {
    // TODO: Create new icon for this visualisation view
    return ViewResources.CATAPPResInstance.getResource( "RoleCompIcon" );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.addStyleName( styleName );
    
    vl.setWidth( "850px" );
    vl.setHeight( "850px" );
    
    // Create the controller; get the view - embed the view later
    smindController = new RoleCompositionController();
  }
  
    private IUFView getRoleCompositionView(String platformId, String communityId, Date startDate) {
        IUFView smindView = smindController.getView(platformId, communityId, styleName);
        return smindView;
    }
  
}
