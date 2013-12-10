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
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViews.DeleteDatabaseOptionsView;
import uk.ac.soton.itinnovation.robust.cat.datasource.impl.DataSourceImpl;
import uk.ac.soton.itinnovation.robust.cat.datasource.spec.IDataSource;

/**
 * Top-level demo controller class - for this demo, this controller will act as
 * the listener for the main data changing events between components
 *
 * @author Simon Crowle
 */
public class CATDemoController extends UFAbstractEventManager
        implements Serializable,
        ICATAppViewListener,
        IWelcomeListener,
        IRiskEditorListener,
        IEvaluationEngineListener,
        IEvaluationResultListener,//Bassem Nasser
        ITreatmentWorkflowProviderListener,
        ITreatmentProcessListener,
        IRODataChangedListener {

    private ICATAppViewController appViewController;
    private IWelcomeController welcomeController;
    private IRiskEditorController riskEditorController;
    private ISimulationCentreController simulationCentreController;
    private ITreatmentCentreController treatmentController;
    private IDashboardController dashController;
    // Do not serialize these items over the GWT
    private transient IDataLayer dataLayer;
    private transient IDataSource dataSource;
    private transient EvaluationEngineProxy roEvaluationEngine;//Bassem Nasser: changed IEvaluationEngine to EvaluationEngineProxy
    private transient ITreatmentFactory treatmentFactory;
    private transient ITreatmentWorkflowProvider treatmenWFProvider;
    private String currPlatformID; // platform that communities belong to
    private UUID currCommunityID;
    private boolean tryingToStopEvalEngine = false;
    private boolean tryingToRestartEvalEngine = false;
    private boolean tryingToShutdown = false;
    private boolean treatmentEngineAvailable = false;
    
    static Logger log = Logger.getLogger(CATDemoController.class);
    
    private RODataMonitor roDataMonitor; // KEM
    private boolean dataSourcesOK = false;
    private boolean isPublicDemo = true; // default set as true for safety (should be set by properties)
    private Properties props;
    private com.vaadin.Application application;
    private HashMap<UUID, String> communityPlatformsMap;

    public void runWebApp(com.vaadin.Application app) {
        application = app;
        restartApplication();
    }
    
    private void restartApplication() {
        initialiseProperties();

        // Always do this first
        getViews(application);

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

        //getPolecatConfigs();
        getCATConfig();
    }

    private void initialiseDataSources() {
        if (getDataSources()) {
            dataSourcesOK = true;
            integrateComponents();
        }
    }

    public void shutdownWebApp() {
        cleanUpAndExit();
    }

    // ICATAppViewListener -------------------------------------------------------
    @Override
    public void onAppViewClosed() {
        cleanUpAndExit();
    }

    // IWelcomeViewListener ------------------------------------------------------
    @Override
    public void onDataSourceSelected(UUID dataSourceID) {
        if (!tryingToShutdown
                && dataLayer != null
                && dataSourceID != null) {
            DataSource ds = dataLayer.getDataSourceByUUID(dataSourceID);
            if (ds != null) {
                //URI endpoint = ds.getEndpoint();
                dataSource = new DataSourceImpl(ds);
                currPlatformID = dataSource.getPlatform();
                Set<Community> dataSourceCommunities = dataSource.getCommunities();
                Set<Community> currentCommunities = getCommunities();
                welcomeController.setCommunitiesForCurrentDataSource(dataSourceCommunities, currentCommunities);
            }
        }
    }

    @Override
    public void onAddDataSource(DataSource dataSource) {
        if (!tryingToShutdown
                && dataLayer != null
                && dataSource != null) {
            
            log.info("adding data source " + dataSource.getName());

            // Add new data source
            dataLayer.addDataSource(dataSource);
            
            roDataMonitor.onDataSourceAdded(dataSource);
        }
    }

    @Override
    public void onRemoveDataSource(UUID dataSourceID) {
        if (!tryingToShutdown
                && dataLayer != null
                && dataSourceID != null) {

            log.info("removing data source " + dataSourceID);

            // Remove data source
            dataLayer.deleteDataSourceByUUID(dataSourceID);
            
            roDataMonitor.onDataSourceDeleted(dataSourceID);
        }
    }

    @Override
    public void onRefreshCommunityList() {
        if (!tryingToShutdown) {
            if (dataLayer != null && appViewController != null) {
                // Stop evaluating whatever is being evaluated
                //stopEvaluationEngine( true ); //KEM not required

                if (!dataSourcesOK) {
                    initialiseDataSources();
                }

                // Update welcome view
                Set<Community> communities = getCommunities();
                welcomeController.setCurrentPlatform(currPlatformID);
                welcomeController.setCommunityInfo(communities);

                boolean commExists = false;
                if (currCommunityID != null) {
                    for (Community comm : communities) {
                        if (comm.getUuid().equals(currCommunityID)) {
                            commExists = true;
                            break;
                        }
                    }
                }

                if (commExists) {
                    // Refresh the risk data
                    onMonitorCommunity(currCommunityID);
                }
                else {
                    ((ICATAppViewNavListener)appViewController).onNavigateApp( ICATAppViewNavListener.eNavDest.WELCOME_NO_DASHBOARD );
                }

                // Notify the user of changes
                IUFView view = appViewController.getCurrentView();
                //view.displayMessage( "Finished", "Community data has been refreshed" );
            }
        }
    }

    @Override
    public void onMonitorCommunity(UUID communityID) {
        if (!tryingToShutdown
                && dataLayer != null
                && communityID != null) {
            
            currCommunityID = communityID;
            //currPlatformID = platform;

            updateCurrentROData(null);
            configureDashboardForCommunity();

            //KEM should not need to stop EE.
            //stopEvaluationEngine( true ); // Reset the evaluation engine
            startEvaluationEngine(); // Start evaluation engine (if not already running)

            log.info("subscribing to the community " + currCommunityID);
            boolean subscribed = roEvaluationEngine.addEvaluationResultListener(this, currCommunityID);
            System.err.println("Subscribed to community " + currCommunityID + ": " + subscribed);

            riskEditorController.resetEditorUI();

            IROMatrixController matCtrl = dashController.getROMatrixController();
            matCtrl.resetROMatrix();

            // Place all known risks in the treatment centre
            updateTreatmentCentre(currCommunityID);
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
            
            // Store community platform in map (as platform is not yet persisted in database)
            setPlatformForCommunity(community.getUuid(), community.getPlatform());

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
    public void onAddCommunities(Set<Community> addedCommunities) {
        for (Community comm : addedCommunities) {
            onAddCommunity(comm);
        }
    }

    @Override
    public void onRemoveCommunities(Set<Community> removedCommunities) {
        for (Community comm : removedCommunities) {
            onRemoveCommunity(comm.getUuid());
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

    @Override
    public void onResetDatabaseClicked(String deleteDatabaseOption, String authToken) {
        try {
            dataLayer.resetDB(authToken);
        }
        catch (Exception e) {
            e.printStackTrace();
            appViewController.getCurrentView().displayMessage("ERROR: could not reset database", e.getMessage());
            return;
        }
        
        try {
            if (deleteDatabaseOption.equals(DeleteDatabaseOptionsView.EMPTY_DATABASE_OPTION)) {
                // No population of data
            }
            else if (deleteDatabaseOption.equals(DeleteDatabaseOptionsView.IBM_DATABASE_OPTION)) {
                PopulateIBM popIBM = new PopulateIBM();
                popIBM.populateIBM((DataLayerImpl) dataLayer);
                onRefreshCommunityList();
            }
            else if (deleteDatabaseOption.equals(DeleteDatabaseOptionsView.BOARDSIE_DATABASE_OPTION)) {
                PopulateBoards popBoards = new PopulateBoards();
                popBoards.populateBoards((DataLayerImpl) dataLayer);
                onRefreshCommunityList();
            }
            else if (deleteDatabaseOption.equals(DeleteDatabaseOptionsView.SAP_DATABASE_OPTION)) {
                PopulateSAP popSAP = new PopulateSAP();
                popSAP.populateSAP((DataLayerImpl) dataLayer);
                onRefreshCommunityList();
            }
            else if (deleteDatabaseOption.equals(DeleteDatabaseOptionsView.SQL_SCRIPT_DATABASE_OPTION)) {
                dataLayer.runScript(authToken);
                onRefreshCommunityList();
            }
            else {
                throw new Exception("Unrecognised option: " + deleteDatabaseOption);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            appViewController.getCurrentView().displayMessage("ERROR: could not populate database", e.getMessage());
        }
    }

    // IRiskEditorListener -------------------------------------------------------
    @Override
    public void onRiskEditorWantsFreshRiskData() {
        if (!tryingToShutdown) {
            if (currCommunityID != null && dataLayer != null) {
                updateCurrentROData(null);
            }
        }
    }

    @Override
    public void onCreateNewRO(String title, boolean risk, String owner, String group) {
        if (!tryingToShutdown) {
            if (dataLayer != null && currCommunityID != null) {
                // Get community instance
                Set<Community> currCommunities = getCommunities();
                Community targetCommunity = null;

                // See if we can find our current community instance
                if (currCommunities != null) {
                    Iterator<Community> comIt = currCommunities.iterator();

                    while (comIt.hasNext()) {
                        Community thisCom = comIt.next();
                        if (thisCom.getUuid().equals(currCommunityID)) {
                            targetCommunity = thisCom;
                            break;
                        }
                    }

                    // If we've found the community, create the RO
                    if (targetCommunity != null) {
                        // Update our risk editor view
                        Risk newRisk = dataLayer.saveRisk(targetCommunity, title, owner, risk, group);
                        //updateCurrentROData(); //KEM called via roDataChanged

                        roDataMonitor.onRiskAdded(newRisk); //KEM

                        // Stop, then re-start the evaluation engine first (threaded)
                        //stopEvaluationEngine( true ); //KEM not required
                    }
                }
            }
        }
    }

    @Override
    public void onUpdateRisk(Risk risk) {
        if (!tryingToShutdown && dataLayer != null) {
            boolean savedOK = false;
            try {
                dataLayer.updateRisk(risk);
                savedOK = true;
                roDataMonitor.onRiskUpdated(risk);
            } catch (Exception e) {
                appViewController.getCurrentView().displayWarning("Data layer: ", e.getMessage());
            }

            if (savedOK) {
                appViewController.getCurrentView().displayMessage("Saved " + risk.getTitle() + " OK", "");
            }
        }

        // Make sure treatment centre has got the updated risk
        treatmentController.handleTreatmentOfRisk(risk);
        //updateCurrentROData(); // KEM called via onRiskUpdated
    }

    @Override
    public void onDeleteRisk(Risk risk) {
        if (!tryingToShutdown) {
            if (risk != null && dataLayer != null) {
                try {
                    dataLayer.deleteRisk(risk.getId());
                    roDataMonitor.onRiskDeleted(risk);
                } catch (Exception e) {
                    appViewController.getCurrentView().displayWarning("Data layer problem: ", e.getMessage());
                }

                //updateCurrentROData(); //KEM called via onRiskDeleted    

                // Tell the treatment centre to not bother with this risk any more
                treatmentController.handleRiskRemoval(risk);

                // Stop, then re-start the evaluation engine first (threaded)
                //stopEvaluationEngine( true ); //KEM not necessary
            }
        }
    }

    @Override
    public void onGetPSD(Event event) {
        if (event != null && dataLayer != null) {
            PredictorServiceDescription psd = dataLayer.getPredictor(event);

            // If we've got a valid PSD for the event, return the result
            if (psd != null) {
                riskEditorController.addPSDEventMapping(event.getUuid(), psd);
            }
        }
    }

    // IEvaluationEngineListener -------------------------------------------------
    @Override
    public synchronized void onEvaluationEngineStart() {
        log.info("CAT Evaluation Engine started...");
        tryingToStopEvalEngine = false;
        tryingToRestartEvalEngine = false;
    }

    @Override
    public synchronized void onEvaluationEngineStop() {
        log.info("CAT Evaluation Engine stopped...");

        // Attempt to re-start the evaluation engine if required
        if (!tryingToShutdown && tryingToRestartEvalEngine) {
            startEvaluationEngine();
        }

        tryingToStopEvalEngine = false;
    }

    @Override
    public synchronized void onEvaluationEngineRestart() {
        log.info("CAT Evaluation Engine restarted...");

        // TODO: do something with EE restart event if required
    }

    @Override
    public void onEvaluationEngineEvaluationEpochStarted(Date date) {
//  TO REMOVE
//    if ( !tryingToShutdown )
//    {
//      IROMatrixController matCtrl = dashController.getROMatrixController();
//      matCtrl.setStartedEvalEpochDate( date );
//    }
    }

    @Override
    public void onEvaluationEngineEvaluationEpochFinished(Date date) {
// TO REMOVE
//    if ( !tryingToShutdown )
//    {
//      IROMatrixController matCtrl = dashController.getROMatrixController();
//      matCtrl.setFinishedEpochDate( date );
//    }
    }

    @Override
    public synchronized void onNewEvaluationResults(EvaluationResult res)//ve removed uuid
    {
        if (res == null)
            return;
        
        //Bassem Nasser
        UUID roID = res.getRiskUUID();
        
        if (!tryingToShutdown) {
            if (roID != null) {
                // Try to find the risk that the result refers to
                Map<UUID, Risk> currRisks = riskEditorController.getCopyOfCurrentRisks();
                Risk riskTarget = currRisks.get(roID);

                if (riskTarget != null) {
                    // If we have a result, then send it to the RO matrix
                    List<ResultItem> results = res.getResultItems();
                    if (results != null && !results.isEmpty()) {
                        IROMatrixController matCtrl = dashController.getROMatrixController();
                        matCtrl.addROEvaluationResult(riskTarget, res);
                    }
                }
            }
        }
    }

    // ITreatmentProviderListener ------------------------------------------------
    @Override
    public void onProviderInitialiseResult(ITreatmentWorkflowProviderListener.eInitResult result) {
        boolean initOK = false;
        String failMessage = "";

        if (result == ITreatmentWorkflowProviderListener.eInitResult.eProviderInitialisedOK) {
            try {
                if (treatmenWFProvider != null) {
                    treatmenWFProvider.requestProcessEngine(this);
                }
                initOK = true;
            } catch (Exception e) {
                failMessage = e.getMessage();
            }
        } else {
            failMessage = "Provider could not initialise";
        }

        // Warn user if no treatment data provider could be found
        if (!initOK) {
            appViewController.getCurrentView().displayWarning("Treatment work-flow initialisation problem",
                    failMessage);
        }
    }

    @Override
    public void onProviderProcessEngineResult(IWorkflowEngineWrapper wfew,
            ITreatmentWorkflowProviderListener.eEngineResult result) {
        String failMessage = null;

        if (result == ITreatmentWorkflowProviderListener.eEngineResult.eActivitiProcessEngineCreatedOK) {
            if (wfew != null) {
                treatmentEngineAvailable = true;

                // Pass repository over to the treatment centre
                ITreatmentRepository repo =
                        treatmentFactory.createTreatmentRepository(UUID.randomUUID(),
                        "MS12DemoRepo",
                        wfew);

                // .. populate it with a few (demo) treatment processes first through (if needed)
                if (repo != null) {
                    // Set up listeners (control centre and this controller to update risks)
                    UFAbstractEventManager aem = (UFAbstractEventManager) repo;
                    aem.addListener((IUFListener) treatmentController);
                    aem.addListener((IUFListener) this);

                    /* KEM moved creation of demo treatments into TreatmentRepository
                     Set<TreatmentTemplate> currResources = repo.getCopyofTreatmentTemplates();
          
                     // No data yet? Then create for the first time
                     if ( currResources.isEmpty() )
                     demoPopulateTreatmentRepository( repo );
                     */

                    treatmentController.setRepository(repo);
                    riskEditorController.setTreatmentRepository(repo);
                } else {
                    failMessage = "Repository could not be created";
                }
            } else {
                failMessage = "Workflow engine wrapper was null";
            }
        } else {
            failMessage = "Activiti Engine was not created properly";
        }

        if (failMessage != null) {
            appViewController.getCurrentView().displayWarning("Treatment workflow engine start-up problem",
                    failMessage);
        }
    }

    // ITreatmentProcessListener -------------------------------------------------
    @Override
    public void onProcessStarted(Treatment tmt) { /* Don't care about this */ }

    @Override
    public void onProcessCompleted(Treatment tmt) {
        boolean riskUpdatedOK = false;

        if (tmt != null && dataLayer != null) {
            Risk risk = dataLayer.getRiskByUUID(tmt.getLinkedRiskID());
            if (risk != null) {
                risk.addTreatProcID(tmt.getActivitiProcInstanceID());
                try {
                    dataLayer.updateRisk(risk);
                    riskUpdatedOK = true;
                } catch (Exception e) {
                }
            }
        }

        if (!riskUpdatedOK) {
            appViewController.getCurrentView().
                    displayWarning("Treatment update problem",
                    "Could not find risk linked to treatment!");

            log.error("Could not save treatment history to data layer");
        }
    }

    @Override
    public void onActivityStarted(Treatment tmt, ActivityImpl activity) {
        /* Don't care about this */
    }

    @Override
    public void onActivityCompleted(Treatment tmt, ActivityImpl activity) {
        /* Don't care about this */
    }

    @Override
    public void onTaskCreated(TreatmentTask task) { /* Don't care about this */ }

    @Override
    public void onTaskCompleted(TreatmentTask task) { /* Don't care about this */ }

    // Private methods -----------------------------------------------------------  
    private void cleanUpAndExit() {
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

    private void getViews(com.vaadin.Application app) {
        appViewController = CATViewEngine.initialize(app, "CAT Demo", props);

        IUFNotifier notifier = (IUFNotifier) appViewController;
        notifier.addListener(this);

        // Listen to the welcome view
        welcomeController = appViewController.getWelcomeViewController();
        notifier = (IUFNotifier) welcomeController;
        notifier.addListener(this);

        // Listen to the risk view
        riskEditorController = appViewController.getRiskEditorViewController();
        notifier = (IUFNotifier) riskEditorController;
        notifier.addListener(this);

        // Connect to simulation centre
        simulationCentreController = appViewController.getSimulationCentreController();

        // Listen to treatment centre view
        treatmentController = appViewController.getTreatmentCentreController();
        notifier = (IUFNotifier) treatmentController;
        notifier.addListener(this);

        // Connect to the dashboard
        dashController = appViewController.getDashboardViewController();
    }

    private boolean getDataSources() {
        boolean dataSourceOK = true;

        // Data layer ----------------------------------------------------
        try {
            dataLayer = new DataLayerImpl();
        } catch (Exception e) {
            String dbError = "Could not create the data layer";
            appViewController.getCurrentView().displayWarning("CAT Database problem", dbError);
            log.error(dbError);
            dataSourceOK = false;
        }

        // get the set of predictors and save in database
        try {
            log.info("---------------------------------------");
            log.info("getting predictors from predictors.xml");
            log.info("---------------------------------------");
            XMLConfiguration config = new XMLConfiguration("predictors.xml");

            List<HierarchicalConfiguration> services = config.configurationsAt("predictor");
            log.info("Number of predictors: " + (services).size());
            for (HierarchicalConfiguration service : services) {
                String url = service.getString("predurl");
                 log.info("pred url "+url);
                String ns = service.getString("predns");
                String predsvcname = service.getString("predsvcname");
                String predportname = service.getString("predportname");

                getSavePSD(url, ns, predsvcname, predportname);
            }

        } catch (Exception ex) {
            log.error("error while getting the predictors from predictors.xml: " + ex.getMessage(), ex);
        }

        // Evaluation engine --------------------------------------------
        if (dataSourceOK) {
            try {
                log.info("creating evaluation engine proxy instance");
                roEvaluationEngine = EvaluationEngineProxy.getInstance(dataLayer); //Bassem Nasser: changed EvaluationEngine to EvaluationEngineProxy
                roEvaluationEngine.addEvaluationEngineListener(this);
            } catch (Exception e) {
                String msg = e.getLocalizedMessage();
                appViewController.getCurrentView().displayMessage("CAT Evaluation engine problem", msg);
                log.error("CAT Evaluation engine problem: " + msg, e);
                dataSourceOK = false;
            }
        }

        // RO Data Monitor
        roDataMonitor = RODataMonitor.getInstance();
        roDataMonitor.addListener(this);

        

        return dataSourceOK;
    }
    
    private void getSavePSD(String url, String nameSpace, String serviceName, String portName) {
        try {
            PredictorDiscovery pd = new PredictorDiscovery();
            PredictorServiceDescription psd = pd.getPSD(url,nameSpace,serviceName, portName);
            log.info("adding one predictor to db");
            dataLayer.addPredictor(psd);
        } catch (Exception ex) {
            log.error("Error while getting and adding PSD at " + url + ". " + ex, ex);
        }
    }

    private void startEvaluationEngine() {
        if (riskEditorController != null && roEvaluationEngine != null) {
            // Reset evaluation engine (if data is available)
            //Map<UUID, Risk> risks = riskEditorController.getCopyOfCurrentRisks();

            //if ( !risks.isEmpty() )
            try {
                boolean isrunning = roEvaluationEngine.isEngineRunning();
                if (!isrunning) { //KEM

                    // Send the evaluation engine IDs of all risks
                    log.debug("Trying to start the evaluation engine...");

                    tryingToStopEvalEngine = false;
                    roEvaluationEngine.start();
                }

                //KEM - moved to onMonitorCommunity
                //log.info("subscribing to the community "+currCommunityID);
                //roEvaluationEngine.addEvaluationResultListener(this, currCommunityID);//Bassem Nasser
            } catch (Exception e) {
                log.error("Could not start new evaluation process!");
            }
        }
    }

    private void stopEvaluationEngine(boolean thenRestart)//Bassem Nasser: should check if the EE is running in the first place
    {
        if (roEvaluationEngine != null) {
            try {
                log.debug("Trying to stop the evaluation engine...");

                tryingToRestartEvalEngine = thenRestart;
                tryingToStopEvalEngine = true;
                boolean isrunning = roEvaluationEngine.isEngineRunning();
                if (isrunning) {
                    roEvaluationEngine.stop();//Bassem Nasser: should check if the EE is running in the first place, may need to call onEvaluationEngineStop()
                } else {
                    onEvaluationEngineStop();
                }
            } catch (Exception e) {
                log.error("Could not stop the evaluation engine!");
            }
        }
    }

    private void integrateComponents() {
        // Data Layer
        if (dataLayer != null) {
            welcomeController.setDataSources(dataLayer.getDataSources());
            welcomeController.setCurrentPlatform(currPlatformID);
            welcomeController.setCommunityInfo(getCommunities());
        }

        // Get treatment repository up
        initialiseTreatmentRepository();
    }

    private void updateCurrentROData(Risk selectedRisk) {
        Set<Risk> riskSet = dataLayer.getRisks(currCommunityID);
        HashMap<UUID, Risk> risksByID = new HashMap<UUID, Risk>();

        if (riskSet != null) {
            Iterator<Risk> rIt = riskSet.iterator();
            while (rIt.hasNext()) {
                Risk risk = rIt.next();
                risksByID.put(risk.getId(), risk);
            }

            // Also: send on to the risk editor
            riskEditorController.setCurrentRisks(risksByID, selectedRisk);
            /*
            if (selectedRisk != null) {
                riskEditorController.selectRiskByID(selectedRisk.getId());
            }
            else {
                riskEditorController.selectRiskByID(null);
            }
            */
            
            riskEditorController.setCurrentObjectives(dataLayer.getObjectives(currCommunityID));
            riskEditorController.setCurrentPSDs(dataLayer.getPredictors());
        }
    }

    private void updateSelectedCommunityData(UUID communityID) {
        welcomeController.setCurrentCommunity(getCommunityByUUID(communityID));
        welcomeController.setCurrentObjectives(dataLayer.getObjectives(communityID));
        welcomeController.onCommunityDataUpdated();
    }
    
    private String getPlatformForCommunity(UUID communityID) {
        if (communityPlatformsMap == null)
            communityPlatformsMap = new HashMap<UUID, String>();
        
        return communityPlatformsMap.get(communityID);
    }
    
    private void setPlatformForCommunity(UUID communityID, String platform) {
        if (communityPlatformsMap == null)
            communityPlatformsMap = new HashMap<UUID, String>();
        
        communityPlatformsMap.put(communityID, platform);
    }
    
    private Set<Community> getCommunities() {
        Set<Community> communities = dataLayer.getCommunities();
        
        for (Community community : communities) {
            setCommunityPlatform(community);
        }
        
        return communities;
    }
    
    private Community getCommunityByUUID(UUID communityID) {
        Community community = dataLayer.getCommunityByUUID(communityID);
        
        if (community != null) {
            setCommunityPlatform(community);
        }
        
        return community;
    }
    
    private void setCommunityPlatform(Community community) {
        String platform = community.getPlatform();
        if (platform == null) {
            platform = getPlatformForCommunity(community.getUuid()); // Get from map, if available
            if (platform == null) {
                platform = currPlatformID; //N.B. this might be wrong!
                setPlatformForCommunity(community.getUuid(), platform); // Store for later use
            }
            community.setPlatform(platform);
        }
    }

    
    /**
     * Gets configuration properties from 'cat.properties' on the class path.
     */
    private void getCATConfig() {
        try {
            isPublicDemo = Boolean.parseBoolean(props.getProperty("isPublicDemo"));
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
    }
            
    private void configureDashboardForCommunity() {
        Community currCommunity = null;
        String commID = null;

        if (currCommunityID != null) {
            currCommunity = getCommunityByUUID(currCommunityID);
            
            if (currCommunity != null) {
                commID = currCommunity.getCommunityID();
            }

            // For now pass only the current community and the datalayer instance
            IROEvalHistoryController evalHistoryCtrl = dashController.getRoEvalHistoryController();
            evalHistoryCtrl.setContextInfo(currCommunityID, null, dataLayer);

            // Set platform/community for Simulation Centre
            this.simulationCentreController.setCurrPlatformID(currCommunity.getPlatform());
            this.simulationCentreController.setCurrCommunityID(commID);
            
            // Set platform/community for Graphic equalizer (POLECAT)
            //getPolecatConfigs(); // config moved into GfxLizerView
            IPolecatGfxLizerController lizerCtrl = dashController.getPolecatLizerController();
            //lizerCtrl.setLizerURL(getPolecatEqualizerURL());
            lizerCtrl.updateParameters(currCommunity.getPlatform(), currCommunityID.toString());
            
            // Set platform/community for Role Composition
            IRoleCompWrapperController roleCtrl = dashController.getRoleCompositionController();
            
            try {
                //TODO: see why this takes so long
                roleCtrl.setPlatformID(currCommunity.getPlatform());
                roleCtrl.setCommunityID(commID); // Need 'c76ebc42−5b21−41c9−99f4−7f05b926ff0c' here
            }
            catch (Exception e) {
                e.printStackTrace();
                appViewController.getCurrentView().displayMessage("ERROR in Role Composition", e.getMessage());
            }
        }

        //appViewController.setCommunity(currCommunity);
        dashController.setCommunity(currCommunity);
    }

    private void initialiseTreatmentRepository() {
        treatmentEngineAvailable = false;
        treatmenWFProvider = null;

        // Find providers
        treatmentFactory = CATTreatmentEngine.getTreatmentFactory();
        treatmentFactory.discoverProviders();

        // For now, just use the first one available (local MySQL)
        HashMap<UUID, String> providerIDs = treatmentFactory.getKnownWorkflowProviderIDs();
        if (!providerIDs.isEmpty()) {
            try {
                treatmenWFProvider = treatmentFactory.getTreatmentWorkflowProvider(providerIDs.keySet().iterator().next());

                // Get log-in properties and try initialising
                if (treatmenWFProvider != null) {
                    Properties dbLoginProps = new Properties();
                    try {
                        dbLoginProps.load(getClass().
                                getClassLoader().
                                getResourceAsStream("treatmentDBLogin.properties"));

                        treatmenWFProvider.intialiseProvider(dbLoginProps, this);
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }

        // Warn user of major problem with treatment provider!
        if (treatmenWFProvider == null) {
            appViewController.getCurrentView().displayWarning("Treatment workflow problem",
                    "No treatment providers could be found.");
        }
    }

    private void updateTreatmentCentre(UUID communityID) {
        if (dataLayer != null && treatmentController != null) {
            treatmentController.setCommunity(communityID);
            Set<Risk> communityRisks = dataLayer.getRisks(communityID);

            // Load the treatment centre up with latest risks
            Iterator<Risk> riskIt = communityRisks.iterator();
            while (riskIt.hasNext()) {
                treatmentController.handleTreatmentOfRisk(riskIt.next());
            }
        }
    }

    @Override
    public void onRiskAdded(Risk risk) {
        log.debug("CATDemoController onRiskAdded()");
        if (risk.getCommunity().getUuid().equals(currCommunityID)) {
            updateCurrentROData(risk);
        }
    }

    @Override
    public void onRiskUpdated(Risk risk) {
        log.debug("CATDemoController onRiskUpdated() " + risk.getTitle());
        if (risk.getCommunity().getUuid().equals(currCommunityID)) {
            updateCurrentROData(risk);
        }
    }

    @Override
    public void onRiskDeleted(Risk risk) {
        log.debug("CATDemoController onRiskDeleted() " + risk.getTitle());
        if (risk.getCommunity().getUuid().equals(currCommunityID)) {
            updateCurrentROData(null);
        }

        IROMatrixController matCtrl = dashController.getROMatrixController();
        matCtrl.removeEvaluationResultsForRisk(risk);
    }

    @Override
    public void onDataSourceAdded(DataSource dataSource) {
        log.debug("CATDemoController onDataSourceAdded()");

        // Refresh data sources
        if (dataLayer != null) {
            //welcomeController.setDataSources(dataLayer.getDataSources());
            welcomeController.onDataSourceAdded(dataSource);
        }
    }

    @Override
    public void onDataSourceUpdated(UUID dataSourceID) {
        log.debug("CATDemoController onDataSourceUpdated()");

        // Refresh data sources
        if (dataLayer != null) {
            welcomeController.setDataSources(dataLayer.getDataSources());
            //welcomeController.onDataSourceUpdated(dataSource); //TODO
        }
    }

    @Override
    public void onDataSourceDeleted(UUID dataSourceID) {
        log.debug("CATDemoController onDataSourceDeleted()");

        // Refresh data sources
        if (dataLayer != null) {
            //welcomeController.setDataSources(dataLayer.getDataSources());
            welcomeController.onDataSourceDeleted(dataSourceID);
        }
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
            updateCurrentROData(null);
        }
    }

    @Override
    public void onCommunityDeleted(UUID communityID) {
        log.debug("CATDemoController onCommunityDeleted()");

        // Refresh community list
        onRefreshCommunityList();

        if (communityID.equals(currCommunityID)) {
            updateCurrentROData(null); //TODO: check for consequences here!
            welcomeController.startProgressIndicator(); // enable dynamic updates again, as dashboard
        }
    }

    @Override
    public void onObjectiveAdded(UUID communityID, UUID objectiveID) {
        log.debug("CATDemoController onObjectiveAdded()");

        if (welcomeController.getCurrentCommunity() != null && welcomeController.getCurrentCommunity().getUuid().equals(communityID)) {
            updateSelectedCommunityData(communityID);
        }

        if (communityID.equals(currCommunityID)) {
            updateCurrentROData(null);
        }
    }

    @Override
    public void onObjectiveUpdated(UUID communityID, UUID objectiveID) {
        log.debug("CATDemoController onObjectiveUpdated()");

        if (welcomeController.getCurrentCommunity() != null && welcomeController.getCurrentCommunity().getUuid().equals(communityID)) {
            updateSelectedCommunityData(communityID);
        }

        if (communityID.equals(currCommunityID)) {
            updateCurrentROData(null);
        }
    }

    @Override
    public void onObjectiveDeleted(UUID communityID, UUID objectiveID) {
        log.debug("CATDemoController onObjectiveDeleted()");

        if (welcomeController.getCurrentCommunity() != null && welcomeController.getCurrentCommunity().getUuid().equals(communityID)) {
            updateSelectedCommunityData(communityID);
        }
        if (communityID.equals(currCommunityID)) {
            updateCurrentROData(null);
        }
    }

}