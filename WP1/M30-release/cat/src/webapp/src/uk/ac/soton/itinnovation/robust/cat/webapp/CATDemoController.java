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
//      Created Date :          2012-06-21
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.webapp;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.welcome.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.riskEditor.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.IDashboardController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.indicators.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.simulation.ISimulationCentreController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.treatment.ITreatmentCentreController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.engine.CATViewEngine;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.common.RODataMonitor;

import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.engine.CATTreatmentEngine;

import uk.ac.soton.itinnovation.robust.riskmodel.*;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.*;

import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.datalayer.impl.*;

import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.spec.*;

import uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec.*;

import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;


/**
 * Top-level demo controller class - for this demo, this controller will act as the
 * listener for the main data changing events between components
 * 
 * @author sgc
 */
public class CATDemoController extends    UFAbstractEventManager
                               implements Serializable,
                                          ICATAppViewListener,
                                          IWelcomeListener,
                                          IRiskEditorListener,
                                          IEvaluationEngineListener,
                                          IEvaluationResultListener ,//bmn
                                          ITreatmentWorkflowProviderListener,
                                          ITreatmentProcessListener,
                                          IRODataChangedListener
{
  private ICATAppViewController       appViewController;
  private IWelcomeController          welcomeController;
  private IRiskEditorController       riskEditorController;
  private ISimulationCentreController simulationCentreController;
  private ITreatmentCentreController  treatmentController;
  private IDashboardController        dashController;

  // Do not serialize these items over the GWT
  private transient IDataLayer                 dataLayer;
  private transient EvaluationEngineProxy      roEvaluationEngine;//BMN: changed IEvaluationEngine to EvaluationEngineProxy
  private transient ITreatmentFactory          treatmentFactory;
  private transient ITreatmentWorkflowProvider treatmenWFProvider;
    
  private String currPlatformID; // platform that communities belong to
  private UUID currCommunityID;
  
  private boolean tryingToStopEvalEngine    = false;
  private boolean tryingToRestartEvalEngine = false;
  private boolean tryingToShutdown          = false;
  private boolean treatmentEngineAvailable  = false;
  
  // POLECAT gfx equalizer defaults (added by VE)
  private int    polecatDataSourceID = 3;
  private int    polecatCommunityID  = 264;
  private String polecatIndicators   = "Popularity,Reciprocity,Activity";
  private String polecatStartDateStr = "01-01-2010";
  private String polecatEndDateStr   = "01-01-2012";
  private String polecatTimeInterval = "weekly";
  
  static Logger log = Logger.getLogger(CATDemoController.class);
  
  private RODataMonitor roDataMonitor; // KEM
  
  private boolean dataSourcesOK = false;
  private boolean isPublicDemo = true; // default set as true for safety (should be set by properties)
  private Properties props;
  
  
  public void runWebApp( com.vaadin.Application app )
  {
    initialiseProperties();
    
    // Always do this first
    getViews( app );
    
    // Then go get your data
    initialiseDataSources();
  }
  
    private void initialiseProperties() {
        props = new Properties();

        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream("cat.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
            return;
        }
        
        getPolecatConfigs();
    }
  
  private void initialiseDataSources()
  {
    if ( getDataSources() )
    {
        dataSourcesOK = true;
        integrateComponents();
    }
  }
  
  public void shutdownWebApp()
  { cleanUpAndExit(); }
  
  // ICATAppViewListener -------------------------------------------------------
  @Override
  public void onAppViewClosed()
  { cleanUpAndExit(); }
  
  // IWelcomeViewListener ------------------------------------------------------
  @Override
  public void onRefreshCommunityList()
  {
    if ( !tryingToShutdown )
    {
      if ( dataLayer != null && appViewController != null )
      {
        // Stop evaluating whatever is being evaluated
        //stopEvaluationEngine( true ); //KEM not required
        
        if (! dataSourcesOK)
        {
            initialiseDataSources();
        }
          
        // Update welcome view
        welcomeController.setCommunityInfo( dataLayer.getCommunities() );
        
        // Refresh the risk data
        if ( currCommunityID != null ) onMonitorCommunity( currCommunityID );
        
        // Notify the user of changes
        IUFView view = appViewController.getCurrentView();
        //view.displayMessage( "Finished", "Community data has been refreshed" );
      }
    }
  }
  
  @Override
  public void onMonitorCommunity( UUID communityID )
  {
    if ( !tryingToShutdown && 
         dataLayer != null && 
         communityID != null )
    {
      currCommunityID = communityID;
      
      updateCurrentROData();
      configureDashboardForCommunity();
      
      //KEM should not need to stop EE.
      //stopEvaluationEngine( true ); // Reset the evaluation engine
      startEvaluationEngine(); // Start evaluation engine (if not already running)

      log.info("subscribing to the community " + currCommunityID);
      roEvaluationEngine.addEvaluationResultListener(this, currCommunityID);

      riskEditorController.resetEditorUI();
      
      IROMatrixController matCtrl = dashController.getROMatrixController();
      matCtrl.resetROMatrix();
      
      // Place all known risks in the treatment centre
      updateTreatmentCentre( currCommunityID );
    }
  }
  
    @Override
    public void onAddCommunity(Community community) {
        if (!tryingToShutdown
                && dataLayer != null
                && community != null) {
            
            log.info("adding community " + community.getName());
            
            // Add new community
            dataLayer.addCommunity(community);
            
            roDataMonitor.onCommunityAdded(community.getUuid());
            
            // Refresh community list
            //onRefreshCommunityList();
        }
    }

    
    @Override
    public void onRemoveCommunity(UUID communityID) {
        if (!tryingToShutdown
                && dataLayer != null
                && communityID != null) {
            
            log.info("removing community " + communityID);
            
            // Remove community
            dataLayer.deleteCommunityByUUID(communityID);
            
            // Check if we have deleted the currently monitored community
            if (currCommunityID == communityID) {
                currCommunityID = null;
                configureDashboardForCommunity();
            }
            
            roDataMonitor.onCommunityDeleted(communityID);
            
            // Refresh community list
            //onRefreshCommunityList();
        }
    }

    @Override
    public void onEditCommunityObjectives(UUID communityID) {
        if (!tryingToShutdown
                && dataLayer != null
                && communityID != null) {
            
            log.info("getting community and objectives: " + communityID);
            updateSelectedCommunityData(communityID);
        }
    }

    @Override
    public void onAddCommunityObjective(UUID communityID, Objective objective) {
        if (!tryingToShutdown
                && dataLayer != null
                && communityID != null
                && objective != null) {
            
            log.info("adding community objective: " + objective.getTitle());
            dataLayer.addObjective(communityID, objective);
            updateSelectedCommunityData(communityID);
            roDataMonitor.onObjectiveAdded(communityID, objective.getIdAsUUID());
        }
    }

    @Override
    public void onRemoveCommunityObjective(UUID communityID, Objective objective) {
        if (!tryingToShutdown
                && dataLayer != null
                && objective != null) {
            
            log.info("removing community objective: " + objective.getTitle());
            dataLayer.deleteObjective(communityID, objective);
            updateSelectedCommunityData(communityID);
            roDataMonitor.onObjectiveDeleted(communityID, objective.getIdAsUUID());
        }
    }
    
  // IRiskEditorListener -------------------------------------------------------
  @Override
  public void onRiskEditorWantsFreshRiskData()
  {
    if ( !tryingToShutdown )
      if ( currCommunityID != null && dataLayer != null ) updateCurrentROData();
  }
  
  @Override
  public void onCreateNewRO( String title, boolean risk, String owner, String group )
  {
    if ( !tryingToShutdown )
      if ( dataLayer != null && currCommunityID != null )
      {
        // Get community instance
        Set<Community> currCommunities = dataLayer.getCommunities();
        Community targetCommunity = null;
        
        // See if we can find our current community instance
        if ( currCommunities != null )
        {
          Iterator<Community> comIt = currCommunities.iterator();
          
          while ( comIt.hasNext() )
          {
            Community thisCom = comIt.next();
            if ( thisCom.getUuid().equals(currCommunityID) )
            {
              targetCommunity = thisCom;
              break;
            }
          }
          
          // If we've found the community, create the RO
          if ( targetCommunity != null )
          {      
            // Update our risk editor view
            Risk newRisk = dataLayer.saveRisk( targetCommunity, title, owner, risk, group );
            //updateCurrentROData(); //KEM called via roDataChanged
            
            roDataMonitor.onRiskAdded(newRisk); //KEM
      
            // Stop, then re-start the evaluation engine first (threaded)
            //stopEvaluationEngine( true ); //KEM not required
          }
        }
      }
  }
  
  @Override
  public void onUpdateRisk( Risk risk )
  {
    if ( !tryingToShutdown && dataLayer != null )
    {
      boolean savedOK = false;      
      try
      {
        dataLayer.updateRisk( risk );
        savedOK = true;
        roDataMonitor.onRiskUpdated(risk);
      }
      catch ( Exception e )
      { appViewController.getCurrentView().displayWarning( "Data layer: ", e.getMessage() ); }
      
      if ( savedOK )
        appViewController.getCurrentView().displayMessage( "Saved " + risk.getTitle() + " OK", "" );
    }
    
    // Make sure treatment centre has got the updated risk
    treatmentController.handleTreatmentOfRisk( risk );
    //updateCurrentROData(); // KEM called via onRiskUpdated
  }
  
  @Override
  public void onDeleteRisk( Risk risk )
  {
    if ( !tryingToShutdown )
      if ( risk != null && dataLayer != null )
      {
        try {
            dataLayer.deleteRisk( risk.getId() );
            roDataMonitor.onRiskDeleted(risk);
        }
        catch ( Exception e )
        { appViewController.getCurrentView().displayWarning( "Data layer problem: ", e.getMessage() ); }

        //updateCurrentROData(); //KEM called via onRiskDeleted    
        
        // Tell the treatment centre to not bother with this risk any more
        treatmentController.handleRiskRemoval( risk );

        // Stop, then re-start the evaluation engine first (threaded)
        //stopEvaluationEngine( true ); //KEM not necessary
      }
  }
  
  @Override
  public void onGetPSD( Event event )
  {
    if ( event != null && dataLayer != null )
    {
      PredictorServiceDescription psd = dataLayer.getPredictor( event );
      
      // If we've got a valid PSD for the event, return the result
      if ( psd != null )
        riskEditorController.addPSDEventMapping( event.getUuid(), psd );
    }
  }
  
  // IEvaluationEngineListener -------------------------------------------------
  @Override
  public synchronized void onEvaluationEngineStart()
  {
    log.info( "CAT Evaluation Engine started..." );
    tryingToStopEvalEngine = false;
    tryingToRestartEvalEngine = false;
  }
  
  @Override
  public synchronized void onEvaluationEngineStop()
  {
    log.info( "CAT Evaluation Engine stopped..." );
    
    // Attempt to re-start the evaluation engine if required
    if ( !tryingToShutdown && tryingToRestartEvalEngine ) startEvaluationEngine();
    
    tryingToStopEvalEngine = false;
  }
  
  @Override
  public synchronized void onEvaluationEngineRestart()
  {
    log.info( "CAT Evaluation Engine restarted..." );
    
    // TODO: do something with EE restart event if required
  }
  
  @Override
  public void onEvaluationEngineEvaluationEpochStarted( Date date )
  {
//  TO REMOVE
//    if ( !tryingToShutdown )
//    {
//      IROMatrixController matCtrl = dashController.getROMatrixController();
//      matCtrl.setStartedEvalEpochDate( date );
//    }
  }
  
  @Override
  public void onEvaluationEngineEvaluationEpochFinished( Date date )
  {
// TO REMOVE
//    if ( !tryingToShutdown )
//    {
//      IROMatrixController matCtrl = dashController.getROMatrixController();
//      matCtrl.setFinishedEpochDate( date );
//    }
  }
  
  @Override
  public synchronized void onNewEvaluationResults(EvaluationResult res )//ve removed uuid
  {
      //bmn
     UUID roID=res.getRiskUUID();
    if ( !tryingToShutdown )
      if ( roID != null && res != null )
      {
        // Try to find the risk that the result refers to
        Map<UUID, Risk> currRisks = riskEditorController.getCopyOfCurrentRisks();
        Risk riskTarget = currRisks.get( roID );

        if ( riskTarget != null )
        {
          // If we have a result, then send it to the RO matrix
          List<ResultItem> results = res.getResultItems();
          if ( results != null && !results.isEmpty() )
          {
            IROMatrixController matCtrl = dashController.getROMatrixController();
            ResultItem ri = results.get(0);

            matCtrl.addROEvaluationResult( riskTarget,
                                           res.getCurrentDate(),
                                           res.getForecastDate(),
                                           ri.getProbability() );
          }
        }
      }
  }
  
  // ITreatmentProviderListener ------------------------------------------------
  @Override
  public void onProviderInitialiseResult( ITreatmentWorkflowProviderListener.eInitResult result )
  {
    boolean initOK     = false;
    String failMessage = "";
    
    if ( result == ITreatmentWorkflowProviderListener.eInitResult.eProviderInitialisedOK )
    {
      try
      {
        if ( treatmenWFProvider != null ) treatmenWFProvider.requestProcessEngine( this );
        initOK = true;
      }
      catch (Exception e )
      { failMessage = e.getMessage(); }
    }
    else
      failMessage = "Provider could not initialise";
    
    // Warn user if no treatment data provider could be found
    if ( !initOK )
      appViewController.getCurrentView().displayWarning( "Treatment work-flow initialisation problem", 
                                                         failMessage ); 
  }
  
  @Override
  public void onProviderProcessEngineResult( IWorkflowEngineWrapper wfew, 
                                             ITreatmentWorkflowProviderListener.eEngineResult result )
  {
    String failMessage = null;
    
    if ( result == ITreatmentWorkflowProviderListener.eEngineResult.eActivitiProcessEngineCreatedOK )
    {
      if ( wfew != null )
      {
        treatmentEngineAvailable = true;
        
        // Pass repository over to the treatment centre
        ITreatmentRepository repo = 
            treatmentFactory.createTreatmentRepository( UUID.randomUUID(), 
                                                        "MS12DemoRepo",
                                                        wfew );
        
        // .. populate it with a few (demo) treatment processes first through (if needed)
        if ( repo != null )
        {
          // Set up listeners (control centre and this controller to update risks)
          UFAbstractEventManager aem = (UFAbstractEventManager) repo;
          aem.addListener( (IUFListener) treatmentController );
          aem.addListener( (IUFListener) this );
          
          /* KEM moved creation of demo treatments into TreatmentRepository
          Set<TreatmentTemplate> currResources = repo.getCopyofTreatmentTemplates();
          
          // No data yet? Then create for the first time
          if ( currResources.isEmpty() )
            demoPopulateTreatmentRepository( repo );
          */
          
          treatmentController.setRepository( repo );
          riskEditorController.setTreatmentRepository( repo );
        }
        else
          failMessage = "Repository could not be created";
      }
      else
        failMessage = "Workflow engine wrapper was null";
    }
    else
      failMessage = "Activiti Engine was not created properly";
    
    if ( failMessage != null )
      appViewController.getCurrentView().displayWarning( "Treatment workflow engine start-up problem", 
                                                         failMessage ); 
  }
  
  // ITreatmentProcessListener -------------------------------------------------
  @Override
  public void onProcessStarted( Treatment tmt )
  { /* Don't care about this */ }
  
  @Override
  public void onProcessCompleted( Treatment tmt )
  {
    boolean riskUpdatedOK = false;
    
    if ( tmt != null && dataLayer != null )
    {
      Risk risk = dataLayer.getRiskByUUID( tmt.getLinkedRiskID() );
      if ( risk != null )
      {
        risk.addTreatProcID( tmt.getActivitiProcInstanceID() );
        try
        { 
          dataLayer.updateRisk( risk );
          riskUpdatedOK = true;
        }
        catch ( Exception e ) {}
      }
    }
    
    if ( !riskUpdatedOK )
    {
      appViewController.getCurrentView().
                        displayWarning( "Treatment update problem",
                                        "Could not find risk linked to treatment!" );
      
      log.error( "Could not save treatment history to data layer");
    }
  }
  
   @Override
  public void onTaskCreated( TreatmentTask task )
  { /* Don't care about this */ }
  
    @Override
  public void onTaskCompleted( TreatmentTask task )
  { /* Don't care about this */ }
  
  // Private methods -----------------------------------------------------------  
  private void cleanUpAndExit()
  {
    /* KEM we should not shut down evaluation engine here, as other users may require it to be running!
    // Try shutting down, if not already
    if ( !tryingToShutdown )
    {
      tryingToShutdown = true;
      
      stopEvaluationEngine( false );
      
      if ( treatmentFactory != null ) treatmentFactory.releaseProviders();
    }
    */
  }
  
  private void getViews( com.vaadin.Application app )
  {
    appViewController = CATViewEngine.initialize( app, "CAT Demo" , isPublicDemo);
    
    IUFNotifier notifier = (IUFNotifier) appViewController;
    notifier.addListener( this );
    
    // Listen to the welcome view
    welcomeController    = appViewController.getWelcomeViewController();
    notifier = (IUFNotifier) welcomeController;
    notifier.addListener( this );
    
    // Listen to the risk view
    riskEditorController = appViewController.getRiskEditorViewController();
    notifier = (IUFNotifier) riskEditorController;
    notifier.addListener( this );
    
    // Connect to simulation centre
    simulationCentreController = appViewController.getSimulationCentreController();
    
    // Listen to treatment centre view
    treatmentController = appViewController.getTreatmentCentreController();
    notifier = (IUFNotifier) treatmentController;
    notifier.addListener( this );
    
    // Connect to the dashboard
    dashController = appViewController.getDashboardViewController();
  }
  
  private boolean getDataSources()
  {
    boolean dataSourceOK = true;
    
    // Data layer ----------------------------------------------------
    try
    { dataLayer = new DataLayerImpl(); }
    catch ( Exception e )
    { 
      String dbError = "Could not create the data layer";
      appViewController.getCurrentView().displayWarning( "CAT Database problem", dbError );
      log.error( dbError );
      dataSourceOK = false;
    }
    
    // Evaluation engine --------------------------------------------
    if ( dataSourceOK )
    {
      try
      { 
          log.info("creating evaluation engine proxy instance");
        roEvaluationEngine = EvaluationEngineProxy.getInstance(dataLayer); //BMN: changed EvaluationEngine to EvaluationEngineProxy
        roEvaluationEngine.addEvaluationEngineListener( this );
      }
      catch (Exception e)
      { 
        String msg = e.getLocalizedMessage();
        appViewController.getCurrentView().displayMessage( "CAT Evaluation engine problem", msg );
        log.error( "CAT Evaluation engine problem: " + msg , e);
        dataSourceOK = false;
      }
    }
    
    // RO Data Monitor
    roDataMonitor = RODataMonitor.getInstance();
    roDataMonitor.addListener(this);
    
    return dataSourceOK;
  }
  
  private void startEvaluationEngine()
  {
    if ( riskEditorController != null && roEvaluationEngine != null )
    {
      // Reset evaluation engine (if data is available)
      //Map<UUID, Risk> risks = riskEditorController.getCopyOfCurrentRisks();

      //if ( !risks.isEmpty() )
        try
        {
            boolean isrunning=roEvaluationEngine.isEngineRunning();
            if (!isrunning) { //KEM

                // Send the evaluation engine IDs of all risks
                log.debug("Trying to start the evaluation engine...");

                tryingToStopEvalEngine = false;
                roEvaluationEngine.start();
            }
          
            //KEM - moved to onMonitorCommunity
            //log.info("subscribing to the community "+currCommunityID);
            //roEvaluationEngine.addEvaluationResultListener(this, currCommunityID);//bmn
        }
        catch( Exception e ) { log.error( "Could not start new evaluation process!" ); }
    }
  }
  
  private void stopEvaluationEngine( boolean thenRestart )//BMN: should check if the EE is running in the first place
  {
    if ( roEvaluationEngine != null)
      try
      {
        log.debug( "Trying to stop the evaluation engine..." );

        tryingToRestartEvalEngine = thenRestart; 
        tryingToStopEvalEngine = true;
        boolean isrunning=roEvaluationEngine.isEngineRunning();
        if(isrunning)
            roEvaluationEngine.stop();//BMN: should check if the EE is running in the first place, may need to call onEvaluationEngineStop()
        else
            onEvaluationEngineStop();
      }
      catch( Exception e ) { log.error( "Could not stop the evaluation engine!" ); }
  }
  
  private void integrateComponents()
  {
    // Data Layer
    if ( dataLayer != null )
      welcomeController.setCommunityInfo( dataLayer.getCommunities() );
    
    // POLECAT URL set-up
    getPolecatConfigs();
    IPolecatGfxLizerController lizerCtrl = dashController.getPolecatLizerController();
    lizerCtrl.setLizerURL( getPolecatEqualizerURL() );
    
    // Get treatment repository up
    initialiseTreatmentRepository();
  }
  
  private void updateCurrentROData()
  {
    Set<Risk> riskSet = dataLayer.getRisks( currCommunityID );
    HashMap<UUID, Risk> risksByID = new HashMap<UUID, Risk>();
    
    if ( riskSet != null )
    {
      Iterator<Risk> rIt = riskSet.iterator();
      while ( rIt.hasNext() )
      {
        Risk risk = rIt.next();
        risksByID.put( risk.getId(), risk );
      }

      // Also: send on to the risk editor
      riskEditorController.setCurrentRisks( risksByID );
      riskEditorController.setCurrentObjectives( dataLayer.getObjectives(currCommunityID) );
      riskEditorController.setCurrentPSDs( dataLayer.getPredictors() );
    }
  }
  
  private void updateSelectedCommunityData(UUID communityID)
  {
      welcomeController.setCurrentCommunity(dataLayer.getCommunityByUUID(communityID));
      welcomeController.setCurrentObjectives(dataLayer.getObjectives(communityID));
      welcomeController.onCommunityDataUpdated();
  }
  
  /**
    * Gets configuration properties from 'cat.properties' on the class path.
    */
  private void getPolecatConfigs()
  {
      try {
          isPublicDemo = Boolean.parseBoolean( props.getProperty("isPublicDemo") );
      } catch (Exception ex) {
          log.error("Error with loading getting and converting 'isPublicDemo' parameter from cat.properties. " + ex.getMessage(), ex);
      }
      log.info("isPublicDemo:  " + isPublicDemo);

      try {
          currPlatformID = props.getProperty("platformID");
      } catch (Exception ex) {
          log.error("Error with loading getting and converting 'platformID' parameter from cat.properties. " + ex.getMessage(), ex);
      }
      log.info("currPlatformID:  " + currPlatformID);

      try {
          polecatDataSourceID = Integer.parseInt(props.getProperty("equalizerDataSourceID"));
      } catch (Exception ex) {
          log.error("Error with loading getting and converting 'equalizerDataSourceID' parameter from cat.properties. " + ex.getMessage(), ex);
      }
      log.info("equalizerDataSourceID:  " + polecatDataSourceID);

      try {
          polecatCommunityID = Integer.parseInt(props.getProperty("equalizerCommunityID"));
      } catch (Exception ex) {
          log.error("Error with loading getting and converting 'equalizerCommunityID' parameter from cat.properties. " + ex.getMessage(), ex);
      }
      log.info("equalizerCommunityID:   " + polecatCommunityID);

      try {
          polecatIndicators = props.getProperty("equalizerIndicators");
      } catch (Exception ex) {
          log.error("Error with loading getting and converting 'equalizerIndicators' parameter from cat.properties. " + ex.getMessage(), ex);
      }
      log.info("equalizerIndicators:   " + polecatIndicators);

      try {
          polecatStartDateStr = props.getProperty("equalizerStartDate");
      } catch (Exception ex) {
          log.error("Error with loading getting and converting 'equalizerStartDate' parameter from cat.properties. " + ex.getMessage(), ex);
      }
      log.info("equalizerStartDate:  " + polecatStartDateStr);

      try {
          polecatEndDateStr = props.getProperty("equalizerEndDate");
      } catch (Exception ex) {
          log.error("Error with loading getting and converting 'equalizerEndDate' parameter from cat.properties. " + ex.getMessage(), ex);
      }
      log.info("equalizerEndDate:  " + polecatEndDateStr);

      try {
          polecatTimeInterval = props.getProperty("equalizerTimeInterval");
      } catch (Exception ex) {
          log.error("Error with loading getting and converting 'equalizerTimeInterval' parameter from cat.properties. " + ex.getMessage(), ex);
      }
      log.info("timeInterval:  " + polecatTimeInterval);

  }

  private String getPolecatEqualizerURL()
  {
      String URL = "http://robust.meaningmine.com:8080/equalizer-0.5/index.jsp";
      URL += "?ci=" + polecatCommunityID; // OBS: change the question mark to an & if the data source is added first
      URL += "&sd=" + polecatStartDateStr;
      URL += "&ed=" + polecatEndDateStr;
      URL += "&ti=" + polecatTimeInterval;
      URL += "&ins=" + polecatIndicators;

      return URL;
  }
  
  private void configureDashboardForCommunity()
  {
      Community currCommunity = null;
      
      if (currCommunityID != null) {
          currCommunity = dataLayer.getCommunityByUUID(currCommunityID);

          // For now pass only the current community and the datalayer instance
          IROEvalHistoryController evalHistoryCtrl = dashController.getRoEvalHistoryController();
          evalHistoryCtrl.setContextInfo(currCommunityID, null, dataLayer);

          IRoleCompWrapperController roleCtrl = dashController.getRoleCompositionController();
          
          // Set default platform id
          roleCtrl.setPlatformID(currPlatformID);
          
          // Set community id
          //roleCtrl.setCommunityID(currCommunityID.toString()); // Need 'c76ebc42−5b21−41c9−99f4−7f05b926ff0c' here  
          roleCtrl.setCommunityID(currCommunity.getCommunityID()); // Need 'c76ebc42−5b21−41c9−99f4−7f05b926ff0c' here
      }

      //appViewController.setCommunity(currCommunity);
      dashController.setCommunity(currCommunity);
  }
  
  private void initialiseTreatmentRepository()
  {
    treatmentEngineAvailable = false;
    treatmenWFProvider = null;
    
    // Find providers
    treatmentFactory = CATTreatmentEngine.getTreatmentFactory();
    treatmentFactory.discoverProviders();
    
    // For now, just use the first one available (local MySQL)
    HashMap<UUID, String> providerIDs = treatmentFactory.getKnownWorkflowProviderIDs();
    if ( !providerIDs.isEmpty() )
    {
      try
      {
        treatmenWFProvider = treatmentFactory.getTreatmentWorkflowProvider( providerIDs.keySet().iterator().next() );
        
        // Get log-in properties and try initialising
        if ( treatmenWFProvider != null )
        {
          Properties dbLoginProps = new Properties();
          try
          {
            dbLoginProps.load( getClass().
                               getClassLoader().
                               getResourceAsStream("treatmentDBLogin.properties") );
            
            treatmenWFProvider.intialiseProvider( dbLoginProps, this );
          }
          catch(Exception e) {}
        }
      }
      catch (Exception e) {}
    }
    
    // Warn user of major problem with treatment provider!
    if ( treatmenWFProvider == null )
      appViewController.getCurrentView().displayWarning( "Treatment workflow problem", 
                                                         "No treatment providers could be found." );
  }
  
  private void updateTreatmentCentre( UUID communityID )
  {
    if ( dataLayer != null && treatmentController != null )
    {
      Set<Risk> communityRisks = dataLayer.getRisks( communityID );
      
      // Load the treatment centre up with latest risks
      Iterator<Risk> riskIt = communityRisks.iterator();
      while( riskIt.hasNext() )
      { treatmentController.handleTreatmentOfRisk( riskIt.next() ); }
    }
  }

    @Override
    public void onRiskAdded(Risk risk) {
        log.debug("CATDemoController onRiskAdded()");
        if (risk.getCommunity().getUuid().equals(currCommunityID))
            updateCurrentROData();
    }

    @Override
    public void onRiskUpdated(Risk risk) {
        log.debug("CATDemoController onRiskUpdated() " + risk.getTitle());
        if (risk.getCommunity().getUuid().equals(currCommunityID))
            updateCurrentROData();
    }

    @Override
    public void onRiskDeleted(Risk risk) {
        log.debug("CATDemoController onRiskDeleted() " + risk.getTitle());
        if (risk.getCommunity().getUuid().equals(currCommunityID))
            updateCurrentROData();
        
        IROMatrixController matCtrl = dashController.getROMatrixController();
        matCtrl.removeEvaluationResultsForRisk(risk);
    }

    @Override
    public void onCommunityAdded(UUID communityID) {
        log.debug("CATDemoController onCommunityAdded()");
        
        // Refresh community list
        onRefreshCommunityList();
    }

    @Override
    public void onCommunityUpdated(UUID communityID) {
        log.debug("CATDemoController onCommunityUpdated()");
        
        // Refresh community list
        onRefreshCommunityList();
        
        if (communityID.equals(currCommunityID)) {
            updateCurrentROData();
        }
    }

    @Override
    public void onCommunityDeleted(UUID communityID) {
        log.debug("CATDemoController onCommunityDeleted()");
        
        // Refresh community list
        onRefreshCommunityList();
        
        if (communityID.equals(currCommunityID)) {
            updateCurrentROData(); //TODO: check for consequences here!
        }
    }

    @Override
    public void onObjectiveAdded(UUID communityID, UUID objectiveID) {
        log.debug("CATDemoController onObjectiveAdded()");
        
        if (welcomeController.getCurrentCommunity() != null && welcomeController.getCurrentCommunity().getUuid().equals(communityID))
            updateSelectedCommunityData(communityID);
        
        if (communityID.equals(currCommunityID)) {
            updateCurrentROData();
        }
    }

    @Override
    public void onObjectiveUpdated(UUID communityID, UUID objectiveID) {
        log.debug("CATDemoController onObjectiveUpdated()");
        
        if (welcomeController.getCurrentCommunity() != null && welcomeController.getCurrentCommunity().getUuid().equals(communityID)) {
            updateSelectedCommunityData(communityID);
        }

        if (communityID.equals(currCommunityID)) {
            updateCurrentROData();
        }
    }

    @Override
    public void onObjectiveDeleted(UUID communityID, UUID objectiveID) {
        log.debug("CATDemoController onObjectiveDeleted()");

        if (welcomeController.getCurrentCommunity() != null && welcomeController.getCurrentCommunity().getUuid().equals(communityID)) {
            updateSelectedCommunityData(communityID);
        }
        if (communityID.equals(currCommunityID)) {
            updateCurrentROData();
        }
    }

}