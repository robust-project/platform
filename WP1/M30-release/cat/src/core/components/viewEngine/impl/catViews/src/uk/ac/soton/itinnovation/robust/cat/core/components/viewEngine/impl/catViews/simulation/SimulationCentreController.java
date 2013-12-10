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

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.simulation.ISimulationCentreController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFException;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.*;

import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.engine.CATSimIntegrationEngine;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.common.CATAppViewController;

import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;

import java.io.Serializable;
import java.util.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.base.IntegratedSimControllerBase;





public class SimulationCentreController extends UFAbstractEventManager
                                        implements Serializable,
                                                   IUFController,
                                                   ISimulationCentreController,
                                                   ISimulationControllerListener,
                                                   SimulationViewListener
                                                   
{
  private transient HashMap<ISimulationController, SimulationControlPanelView> simCtrlViews;
  
  private CATAppViewController     appViewCtrl;
  private SimulationCentreMainView simulationMainView;
  
  
  public SimulationCentreController( CATAppViewController avc )
  {
    appViewCtrl = avc;
    
    simulationMainView = new SimulationCentreMainView();
    simCtrlViews       = new HashMap<ISimulationController, SimulationControlPanelView>();
    
    initialiseSimulators();
  }
  
  // IUFController -------------------------------------------------------------
  @Override
  public IUFModelRO getModel( UUID id ) throws UFException
  { return null; }
  
  @Override
  public IUFView getView( IUFModelRO model ) throws UFException
  { return simulationMainView; /*Return the main view only*/ }
  
  @Override
  public IUFView getView( String ID )
  { return simulationMainView; /* Return the main view only */ }
  
  // ISimulationCentreController -----------------------------------------------
  @Override
  public void addSimulator( ISimulationController simController )
  {
    if ( simController != null && !simCtrlViews.containsKey(simController) )
    {
      // Listen to controller for simulation events
      IntegratedSimControllerBase ctrlBase = (IntegratedSimControllerBase) simController;
      ctrlBase.addListener( this );
      
      // Create view and listen to view events
      SimulationControlPanelView ctrlPanel = new SimulationControlPanelView( simController );
      ctrlPanel.addListener( this );
      
      simulationMainView.addControlPanelView( ctrlPanel );
      simCtrlViews.put( simController, ctrlPanel );
      
      // Load up parameters
      Iterator<String> pgIt = simController.getParameterGroupNames().iterator();
      while ( pgIt.hasNext() )
      {
        String groupID = pgIt.next();
        
        ctrlPanel.addParameterGroupTitle( groupID );
        
        ISimulationParamGroup spg = simController.getCopyOfParameterGroup( groupID );
        Iterator<String> pIt = spg.getParameterNames().iterator();
        while ( pIt.hasNext() )
        {
          Parameter param = spg.getParameter( pIt.next() );
          ctrlPanel.addParameter( param );
        }
      }
      
    }
  }
  
  // ISimulationControllerListener
  @Override
  public void onSimulationCompleted( ISimulationController ctrl )
  {
    SimulationControlPanelView pView = simCtrlViews.get( ctrl );
    
    if ( pView != null ) pView.onSimulationCompleted();
  }
  
  // SimulationViewListener ----------------------------------------------------
  @Override
  public void onUserClickedSimulationStart( ISimulationController linkedSimulator )
  {
    // Update our parameter values (from the UI)
    if ( linkedSimulator != null )
    {
      SimulationControlPanelView view = simCtrlViews.get( linkedSimulator );
      if ( view != null )
      {
        updateSimParamsFromView( linkedSimulator, view );
        
        try { linkedSimulator.executeSimulation(); }
        catch ( Exception e )
        { simulationMainView.displayWarning( "Could not start simulation:", e.getMessage() ); }
      }
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void initialiseSimulators()
  {
    ISimulationController ctrl = 
              CATSimIntegrationEngine.getSimulation( simulationMainView,
                                                     CATSimIntegrationEngine.eIntegratedSimulation.eKoblenzForumRestriction );
              
    addSimulator( ctrl );
  }
  
  private void updateSimParamsFromView( ISimulationController simCtrl,
                                        SimulationControlPanelView view )
  {
    Iterator<String> groupIt = simCtrl.getParameterGroupNames().iterator();
    while ( groupIt.hasNext() )
    {
      ISimulationParamGroup group = simCtrl.getCopyOfParameterGroup( groupIt.next() );
      if ( group != null )
      {
        Iterator<String> pNameIt = group.getParameterNames().iterator();
        while ( pNameIt.hasNext() )
        {
          Parameter param = group.getParameter( pNameIt.next() );
          if ( param != null )
          {
            ParameterValue updatedValue = view.getParamValueString( param.getUUID() );
            
            if ( updatedValue != null ) param.setValue( updatedValue );
          }
        }
      }
    }
  }
}
