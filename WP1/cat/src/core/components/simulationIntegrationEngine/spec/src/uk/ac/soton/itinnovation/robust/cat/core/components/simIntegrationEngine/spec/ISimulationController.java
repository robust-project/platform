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
//      Created Date :          06-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec;

import java.util.*;




public interface ISimulationController
{
  boolean initialise( Properties simConfigProps, String currPlatformID, String currCommunityID );
  
  String getName();
  
  String getDescription();
  
  HashMap<String, ISimulationParamGroup> getParameters();
  
  void setParameters(HashMap<String, ISimulationParamGroup> params);

  Set<String> getParameterGroupNames();
  
  ISimulationParamGroup getCopyOfParameterGroup( String groupName );
  
  void setParameterGroup( ISimulationParamGroup group ) throws Exception;
  
  SimulationStatus executeSimulation() throws Exception;
  
  String getSimulationExecutionID();
  
  SimulationStatus getSimulationStatus();

  SimulationStatus stopSimulation() throws Exception;
  
  boolean simulationHasFinished();
  
  boolean isExecutingSimulation();

  void startExecutionMonitor();
  
  void stopExecutionMonitor();

  void onExecutionComplete();
  
  void onSimulationHistoryTableItemSelected(String simulationID);
  
  void viewResults(String simulationID);
}
