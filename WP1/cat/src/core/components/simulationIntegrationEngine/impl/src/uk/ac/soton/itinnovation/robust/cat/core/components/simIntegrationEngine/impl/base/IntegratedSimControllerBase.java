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

package uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.base;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;

import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.*;

import java.util.*;
import java.io.Serializable;
import org.apache.log4j.Logger;





public abstract class IntegratedSimControllerBase extends UFAbstractEventManager
                                                  implements Serializable,
                                                             ISimulationController
{
  protected transient HashMap<String, ISimulationParamGroup> simulationParams;
  protected transient Logger simCtrlLogger = Logger.getLogger( IntegratedSimControllerBase.class );
  private transient Timer simCheckTimer;
  
  protected String simulatorName        = "Unknown simulator";
  protected String simulatorDescription = "This simulator needs a description";
  protected String simExeID;
  protected SimulationStatus simStatus;
  
  protected String selectedSimulationID;
  
  protected boolean executingSimulation;
  
  protected IUFView parentView;
  
  
  public IntegratedSimControllerBase( IUFView parent )
  {
    parentView       = parent;
    simulationParams = new HashMap<String, ISimulationParamGroup>();
  }
  
  // ISimulationController -----------------------------------------------------
  @Override
  public boolean initialise( Properties simConfigProps, String currPlatformID, String currCommunityID )
  {
    if ( simConfigProps == null )
    {
      //simCtrlLogger.error( "Could not configure simulator: NULL properties" );
      notifyError( "Could not configure simulator: NULL properties" );
      return false;
    }
    
    boolean initResult = false;
    
    try
    {
      initialiseConfiguration( simConfigProps, currPlatformID, currCommunityID );
      initialiseParameterGroups();
      
      initResult = true;
    }
    catch ( Exception e )
    {
        //simCtrlLogger.error( "Could not configure simulator: " + e.getMessage());
        notifyError( "Could not configure simulator: " + e.getMessage() );
    }
    
    return initResult;
  }

  @Override
  public String getName()
  { return simulatorName; }
  
  @Override
  public String getDescription()
  { return simulatorDescription; }
  
  @Override
  public Set<String> getParameterGroupNames()
  {
    HashSet<String> groupNames = new HashSet<String>();
    groupNames.addAll( simulationParams.keySet() );
    
    return groupNames;
  }
  
  @Override
  public ISimulationParamGroup getCopyOfParameterGroup( String groupName )
  {
    SimulationParamGroup group = null;
    
    ISimulationParamGroup target = simulationParams.get( groupName );
    if ( target != null )
      group = new SimulationParamGroup( (SimulationParamGroup) target );
    
    return group;    
  }
  
  @Override
  public void setParameterGroup( ISimulationParamGroup group ) throws Exception
  {
    // Safety first
    String groupName = group.getName();
    
    if ( groupName == null || group == null ) throw new Exception( "Input parameters are NULL" );
    if ( !simulationParams.containsKey(groupName) ) throw new Exception( "No group defined by that name" );
    
    simulationParams.remove( groupName );
    simulationParams.put( groupName, group );
  }

  @Override
  public HashMap<String, ISimulationParamGroup> getParameters() {
      return simulationParams;
  }

  @Override
  public void setParameters(HashMap<String, ISimulationParamGroup> params) {
      this.simulationParams = params;
  }

  // Sub-classes must implement ------------------------------------------------
  @Override
  public abstract SimulationStatus executeSimulation() throws Exception;

  @Override
  public String getSimulationExecutionID() {
      return simExeID;
  }

  @Override
  public SimulationStatus getSimulationStatus() {
      return simStatus;
  }

  @Override
  public SimulationStatus stopSimulation() throws Exception {
      executingSimulation = false;
      simStatus.setStatus("cancelled");
      stopExecutionMonitor();
      simCtrlLogger.debug(getName() + " simulation " + simExeID + " cancelled by user");
      return simStatus;
  }

  @Override
  public void startExecutionMonitor() {
    simCheckTimer = new Timer();
    simCheckTimer.scheduleAtFixedRate( new SimExeCheckTask(this.getName(), simExeID), 250, 5000 );
  }
  
  @Override
  public void stopExecutionMonitor() {
      simCheckTimer.cancel();
  }

  @Override
  public abstract boolean simulationHasFinished();
  
  @Override
  public boolean isExecutingSimulation()
  { return executingSimulation; }

  @Override
  public abstract void onExecutionComplete();

  @Override
  public void onSimulationHistoryTableItemSelected(String simulationID) {
      selectedSimulationID = simulationID;
      List<ISimulationControllerListener> listeners = getListenersByType();
      for (ISimulationControllerListener list : listeners) {
          list.onSimulationSelected(this, simulationID);
      }
  }

  @Override
  public abstract void viewResults(String simulationID);


  // Protected methods ---------------------------------------------------------
  protected abstract void initialiseConfiguration( Properties props, String currPlatformID, String currCommunityID ) throws Exception;
  
  protected abstract void initialiseParameterGroups();
  
  protected void notifySimulationCompleted()
  {
    List<ISimulationControllerListener> listeners = getListenersByType();
    for ( ISimulationControllerListener list : listeners )
      list.onSimulationCompleted( this );
  }
  
  protected void notifyError( String message )
  {
    simCtrlLogger.error( message );
    List<ISimulationControllerListener> listeners = getListenersByType();
    for ( ISimulationControllerListener list : listeners )
      list.onError( message );
  }
  
    // Private TimerTask class ---------------------------------------------------
    private class SimExeCheckTask extends TimerTask {

        private String _simType;
        private String _simExeID;

        public SimExeCheckTask(String simType, String simExeID) {
            super();
            _simType = simType;
            _simExeID = simExeID;
        }

        @Override
        public void run() {
            boolean simulationFinished = simulationHasFinished();
            
            if (simulationFinished) {
                simCtrlLogger.debug(_simType + " simulation " + _simExeID + " finished!");
                onExecutionComplete();
            }
            else {
                simCtrlLogger.debug(_simType + " simulation " + _simExeID + " running");
            }
        }
    }
}
