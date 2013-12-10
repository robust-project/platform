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

import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.SimulationStatus;
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
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.base.IntegratedSimControllerBase;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.base.SimulationParamGroup;





public class SimulationCentreController extends UFAbstractEventManager
                                        implements Serializable,
                                                   IUFController,
                                                   ISimulationCentreController,
                                                   ISimulationControllerListener,
                                                   SimulationViewListener
                                                   
{
  private transient HashMap<ISimulationController, SimulationControlPanelView> simCtrlViews;
  private transient HashMap<String, HashMap<String, ISimulationParamGroup>> simulationParamsMap;
  
  private CATAppViewController     appViewCtrl;
  private SimulationCentreMainView simulationMainView;
  private Properties props;
  
  private List<String> availableSimulators;
  private HashMap<String, List<String>> availableSimulatorsForPlatform;
  private String currPlatformID;
  private String currCommunityID;
    
  static Logger log = org.apache.log4j.Logger.getLogger(SimulationCentreController.class);
  
  /*
  public SimulationCentreController( CATAppViewController avc, Properties props )
  {
    appViewCtrl = avc;
    this.props = props;
    getSimulationConfigs();
  }
  */

  public SimulationCentreController( CATAppViewController avc )
  {
    appViewCtrl = avc;
  }
  
  @Override
  public void setProperties(Properties props) {
      this.props = props;
      getSimulationConfigs();
  }

  private void initialiseViews() {
    simulationMainView = new SimulationCentreMainView();
    simCtrlViews       = new HashMap<ISimulationController, SimulationControlPanelView>();
    simulationParamsMap = new HashMap<String, HashMap<String, ISimulationParamGroup>>();
    
    initialiseSimulators();
  }
  
    private void getSimulationConfigs() {
        String availableSimulatorsProp = null;
        String availableSimulatorsIBMProp = null;
        String availableSimulatorsBOARDSIEProp = null;
        String availableSimulatorsSAPProp = null;
        
        // Mapping of simulators available for each platform
        availableSimulatorsForPlatform = new HashMap<String,List<String>>();
        
        // List of undefined properties
        ArrayList<String> undefinedProps = new ArrayList<String>();
        
        try {
            availableSimulatorsProp = props.getProperty("availableSimulators");
            if (availableSimulatorsProp == null) {
                undefinedProps.add("availableSimulators");
            }
            log.info("availableSimulators:  " + availableSimulatorsProp);
        }
        catch (Exception ex) {
            log.error("Error with loading getting and converting 'availableSimulators' parameter from cat.properties. " + ex.getMessage(), ex);
            undefinedProps.add("availableSimulators");
        }
        
        try {
            availableSimulatorsIBMProp = props.getProperty("availableSimulators.IBM");
            if (availableSimulatorsIBMProp == null) {
                undefinedProps.add("availableSimulators.IBM");
            }
            log.info("availableSimulators.IBM:  " + availableSimulatorsIBMProp);
        }
        catch (Exception ex) {
            log.error("Error with loading getting and converting 'availableSimulators.IBM' parameter from cat.properties. " + ex.getMessage(), ex);
            undefinedProps.add("availableSimulators.IBM");
        }
        
        try {
            availableSimulatorsBOARDSIEProp = props.getProperty("availableSimulators.BOARDSIE");
            if (availableSimulatorsBOARDSIEProp == null) {
                undefinedProps.add("availableSimulators.BOARDSIE");
            }
            log.info("availableSimulators.BOARDSIE:  " + availableSimulatorsBOARDSIEProp);
        }
        catch (Exception ex) {
            log.error("Error with loading getting and converting 'availableSimulators.BOARDSIE' parameter from cat.properties. " + ex.getMessage(), ex);
            undefinedProps.add("availableSimulators.BOARDSIE");
        }
        
        try {
            availableSimulatorsSAPProp = props.getProperty("availableSimulators.SAP");
            if (availableSimulatorsSAPProp == null) {
                undefinedProps.add("availableSimulators.SAP");
            }
            log.info("availableSimulators.SAP:  " + availableSimulatorsSAPProp);
        }
        catch (Exception ex) {
            log.error("Error with loading getting and converting 'availableSimulators.SAP' parameter from cat.properties. " + ex.getMessage(), ex);
            undefinedProps.add("availableSimulators.SAP");
        }
        
        if (! undefinedProps.isEmpty()) {
            String undefProps = "";
            for (String prop : undefinedProps) {
                if (! undefProps.equals(""))
                    undefProps += ", ";
                undefProps += prop;
            }
            throw new RuntimeException("Undefined properties in cat.properties: " + undefProps);
        }
        
        if (availableSimulatorsProp != null) { 
            availableSimulators = Arrays.asList(availableSimulatorsProp.split(","));
            
            if (availableSimulatorsIBMProp != null) {
                List<String> availableSimulatorsIBM = Arrays.asList(availableSimulatorsIBMProp.split(","));
                availableSimulatorsForPlatform.put("IBM", availableSimulatorsIBM);
            }
            if (availableSimulatorsBOARDSIEProp != null) {
                List<String> availableSimulatorsBOARDSIE = Arrays.asList(availableSimulatorsBOARDSIEProp.split(","));
                availableSimulatorsForPlatform.put("BOARDSIE", availableSimulatorsBOARDSIE);
            }
            if (availableSimulatorsSAPProp != null) {
                List<String> availableSimulatorsSAP = Arrays.asList(availableSimulatorsSAPProp.split(","));
                availableSimulatorsForPlatform.put("SAP", availableSimulatorsSAP);
            }
        }
        else {
            availableSimulators = new ArrayList<String>(); // no available simulators defined
            log.error("WARNING: no available simulators defined (Hint: check cat.properties file");
        }
        
        System.out.println();
    }

    public String getCurrPlatformID() {
        return currPlatformID;
    }

    
  @Override
  public void setCurrPlatformID(String currPlatformID) {
      this.currPlatformID = currPlatformID;
  }

  @Override
  public void setCurrCommunityID(String currCommunityID) {
      this.currCommunityID = currCommunityID;
      initialiseViews();
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
      
      //We don't need to actually update the view until it is required, e.g. when we click Simulation Centre
      //updateSimulatorView(simController, ctrlPanel);
    }
  }
  
  private void updateSimulatorView(ISimulationController simController, SimulationControlPanelView ctrlPanel) {
      ctrlPanel.clearParameters();
      
      // Load up parameters
      Set<String> pgNames = simController.getParameterGroupNames();
      
      if ( (pgNames == null) || (pgNames.isEmpty()) ) {
          onError("No parameters available. Please click Refresh to try again.");
          return;
      }
      
      Iterator<String> pgIt = pgNames.iterator();
      
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
      
      ctrlPanel.enableStartSimulationButton();
  }
  
  // ISimulationControllerListener

  @Override
  public void onSimulationSelected( ISimulationController ctrl, String simulationID)
  {
      HashMap<String, ISimulationParamGroup> params = this.getSimulationParams(simulationID);
      ctrl.setParameters(params);
      
      SimulationControlPanelView pView = simCtrlViews.get( ctrl );
      
      if ( pView != null ) {
          pView.updateView();
      }
  }

  @Override
  public void onSimulationCompleted( ISimulationController ctrl )
  {
    SimulationControlPanelView pView = simCtrlViews.get( ctrl );
    
    if ( pView != null ) {
        pView.setSimulationStatusLabel("Simulation " + ctrl.getSimulationExecutionID() + " completed");
        SimulationStatus simulationStatus = ctrl.getSimulationStatus();
        pView.updateSimulationStatus(simulationStatus);
        pView.onSimulationCompleted();
    }
  }

    @Override
    public void onError(String message) {
        simulationMainView.displayWarning( "ERROR: ", message );
    }
    
  // SimulationViewListener ----------------------------------------------------
  @Override
  public void onUserClickedSimulationStart( ISimulationController linkedSimulator )
  {
    // Update our parameter values (from the UI)
    if ( linkedSimulator != null )
    {
      SimulationControlPanelView pView = simCtrlViews.get( linkedSimulator );
      
      if ( pView != null )
      {
        pView.setSimulationStatusLabel("Starting simulation...");
        
        try {
            updateSimParamsFromView( linkedSimulator, pView );
            SimulationStatus simulationStatus = linkedSimulator.executeSimulation();
            saveSimulationParams(simulationStatus.getId(), linkedSimulator.getParameters());
            pView.addSimulationResultToHistory(simulationStatus);
            linkedSimulator.startExecutionMonitor();
            pView.setSimulationStatusLabel("Simulation " + linkedSimulator.getSimulationExecutionID() + " started");
        }
        catch ( Exception e ) {
            simulationMainView.displayWarning( "Could not start simulation:", e.getMessage() );
            pView.setSimulationStatusLabel("");
            pView.enableStartSimulationButton();
        }
      }
    }
  }

  @Override
  public void onUserClickedSimulationStop( ISimulationController linkedSimulator )
  {
    // Update our parameter values (from the UI)
    if ( linkedSimulator != null )
    {
      SimulationControlPanelView pView = simCtrlViews.get( linkedSimulator );
      
      if ( pView != null )
      {
        try {
            SimulationStatus simulationStatus = linkedSimulator.stopSimulation();
            pView.updateSimulationStatus(simulationStatus);
            pView.setSimulationStatusLabel("Simulation " + linkedSimulator.getSimulationExecutionID() + " cancelled");
        }
        catch ( Exception e ) {
            simulationMainView.displayWarning( "Could not stop simulation:", e.getMessage() );
        }
      }
    }
  }

  @Override
  public void onUserClickedViewResults(ISimulationController linkedSimulator, String simulationID) {
    // Update our parameter values (from the UI)
    if ( linkedSimulator != null )
    {
      SimulationControlPanelView view = simCtrlViews.get( linkedSimulator );
      
      if ( view != null )
      {
        try {
            linkedSimulator.viewResults(simulationID);
        }
        catch ( Exception e ) {
            simulationMainView.displayWarning( "Could not view results:", e.getMessage() );
        }
      }
    }
  }

  @Override
  public void onUpdateView(ISimulationController linkedSimulator) {
    // Update our parameter values (from the UI)
    if ( linkedSimulator != null )
    {
      SimulationControlPanelView view = simCtrlViews.get( linkedSimulator );
      
      if ( view != null )
      {
          updateSimulatorView(linkedSimulator, view);
      }
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void initialiseSimulators()
  {
      if ((availableSimulators == null) || availableSimulators.isEmpty()) {
          log.error("WARNING: initialiseSimulators(): no simulators available. Check cat.properties file");
      } else {
          if (availableSimulators.contains("KOBLENZ")) {
              ISimulationController ctrl =
                      CATSimIntegrationEngine.getSimulation(simulationMainView,
                      CATSimIntegrationEngine.eIntegratedSimulation.eKoblenzForumRestriction,
                      currPlatformID, currCommunityID);

              if (simulatorAvailableForPlatform("KOBLENZ")) {
                  addSimulator(ctrl);
              }
          }

          if (availableSimulators.contains("CATSIM")) {
              ISimulationController ctrl2 =
                      CATSimIntegrationEngine.getSimulation(simulationMainView,
                      CATSimIntegrationEngine.eIntegratedSimulation.CATSim,
                      currPlatformID, currCommunityID);

              if (simulatorAvailableForPlatform("CATSIM")) {
                  addSimulator(ctrl2);
              }
          }
      }
  }
  
    private boolean simulatorAvailableForPlatform(String simulator) {
        if (currPlatformID == null)
            return false;
        
        List<String> availSimulators = availableSimulatorsForPlatform.get(currPlatformID);
        
        if (availSimulators == null)
            return false;
        
        return availSimulators.contains(simulator);
    }

    private void updateSimParamsFromView(ISimulationController simCtrl, SimulationControlPanelView view) throws Exception {
        
        HashMap<String, ISimulationParamGroup> newParams = new HashMap<String, ISimulationParamGroup>();

        Set<String> pgNames = simCtrl.getParameterGroupNames();

        if ((pgNames == null) || (pgNames.isEmpty())) {
            throw new Exception("No parameters available. Please click Refresh to try again.");
        }

        Iterator<String> groupIt = pgNames.iterator();

        while (groupIt.hasNext()) {
            String groupName = groupIt.next();
            ISimulationParamGroup group = simCtrl.getCopyOfParameterGroup(groupName);

            if (group != null) {
                SimulationParamGroup newGroup = new SimulationParamGroup(group.getName());
                Iterator<String> pNameIt = group.getParameterNames().iterator();

                while (pNameIt.hasNext()) {
                    Parameter param = group.getParameter(pNameIt.next());

                    if (param != null) {
                        Parameter newParam = view.updateParamFromFields(param);
                        newGroup.addParameter(newParam.getName(), newParam);
                    }
                }

                newParams.put(groupName, newGroup);
            }
        }

        simCtrl.setParameters(newParams);
    }

  private void saveSimulationParams(String simulationID, HashMap<String, ISimulationParamGroup> parameters) {
      simulationParamsMap.put(simulationID, parameters);
  }

  private HashMap<String, ISimulationParamGroup> getSimulationParams(String simulationID) {
      HashMap<String, ISimulationParamGroup> params = simulationParamsMap.get(simulationID);
      return params;
  }
}
