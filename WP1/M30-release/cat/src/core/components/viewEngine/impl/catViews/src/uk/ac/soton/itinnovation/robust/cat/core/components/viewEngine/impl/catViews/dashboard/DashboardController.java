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
//      Created Date :          15 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.ICATAppViewNavListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.IDashboardController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.indicators.IROMatrixController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.indicators.*;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators.ROMatrixController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators.*;

import java.util.UUID;
import java.io.Serializable;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;




public class DashboardController extends UFAbstractEventManager
                                 implements Serializable,
                                            IUFController,
                                            IDashboardController
{
  private DashboardView             dashboardView;
  private ROMatrixController        roMatrixController;
  private GfxLizerController        gfxLizerController;
  private RoleCompWrapperController roleCompositionController;
  private ROEvalHistoryController   roEvalHistoryController;
  
  
  public DashboardController( ICATAppViewNavListener navListener )
  {
    dashboardView = new DashboardView( navListener );
    
    createComponents();
  }
  
  // IUFController -------------------------------------------------------------
  @Override
  public IUFModelRO getModel( UUID id ) throws UFException
  {
    // TO DO: may be a requirement to access visualization by model here
    return null;
  }
  
  @Override
  public IUFView getView( IUFModelRO model ) throws UFException
  {
    // Only one view to get - the single dashboard
    return dashboardView;
  }
  
  @Override
  public IUFView getView( String ID )
  {
    // Only one view to get - the single dashboard
    return dashboardView;
  }
  
  // IDashboardController ------------------------------------------------------
  @Override
  public void closedownDashboard()
  {
    dashboardView.shutdownView();
  }
  
  @Override
  public IROMatrixController getROMatrixController()
  { return roMatrixController; }
  
  @Override
  public IPolecatGfxLizerController getPolecatLizerController()
  { return gfxLizerController; }

  @Override
  public IRoleCompWrapperController getRoleCompositionController()
  { return roleCompositionController; }
  
   @Override
  public ROEvalHistoryController getRoEvalHistoryController() {
        return roEvalHistoryController;
    }
  
 
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    // RO matrix controller
    roMatrixController        = new ROMatrixController();
    gfxLizerController        = new GfxLizerController();
    roEvalHistoryController   = new ROEvalHistoryController();
    roleCompositionController = new RoleCompWrapperController();

    // Add matrix to dash container
    DashIndicatorContainer dic = dashboardView.getIndicatorContainer();
    dic.addIndicatorView( roMatrixController.getIndicatorView() );
    
    // Add POLECAT graphic equalizer to to dash container
    dic.addIndicatorView( gfxLizerController.getIndicatorView() );
    
    // Add RO History view container
    dic.addIndicatorView( roEvalHistoryController.getIndicatorView() );
    
    // Add (wrapped) SMIND role composition container
    dic.addIndicatorView( roleCompositionController.getIndicatorView() );
    
    dic.setInitialView(); // defaults to first view/button in list
  }

    @Override
    public void setCommunity(Community community) {
        dashboardView.setCommunity(community);
    }

}