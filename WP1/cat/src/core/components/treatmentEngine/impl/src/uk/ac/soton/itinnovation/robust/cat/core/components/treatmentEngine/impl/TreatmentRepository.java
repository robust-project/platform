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
//      Created Date :          14-May-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.impl;

import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;

import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.*;

import org.activiti.engine.*;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.task.*;
import org.activiti.engine.delegate.*;
import org.activiti.engine.history.*;

import java.io.*;
import java.lang.String;
import java.util.*;
import java.util.Map.Entry;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;

import org.apache.log4j.Logger;


class TreatmentRepository extends UFAbstractEventManager
                          implements ITreatmentRepository,
                                     IActivitiProcExeListener,
                                     IActivitiProcTaskListener
{
  private String                           repoName;
  private UUID                             repoID;
  private boolean                          repositoryInitOK = false;
  private HashMap<UUID, TreatmentTemplate> treatmentTemplates;
  private HashMap<UUID, Treatment>         currentTreatments;
  private HashSet<UUID>                    liveTreatments;
  private HashMap<UUID, TreatmentProcess>  liveProcessesByTreamentID;
  private HashMap<UUID, String>            processDefinitions;
  
  private ProcessEngine     activitiProcEngine;
  private RepositoryService activitiRepoService;
  private DeploymentBuilder deploymentBuilder;
  private Deployment        activitiDeployment;
  
  // Unavoidable: need static reference point for Activiti listeners to call on
  // as we do not want the engine to serialize large sections of the treatment engine
  private static IActivitiProcExeListener  procExeListener;
  private static IActivitiProcTaskListener procTaskListener;
  
  public static IActivitiProcExeListener getProcessExeListener()
  { return procExeListener; }
  
  public static IActivitiProcTaskListener getProcessTaskListener()
  { return procTaskListener; }
  
  private HashMap<UUID, TreatmentTemplateDefinition> treatmentTemplateDefinitions; // KEM
  
  static Logger log = Logger.getLogger(TreatmentRepository.class);

  
  // ITreatmentRepository ------------------------------------------------------
  @Override
  public void resetRepository() throws Exception
  {
    //TODO: Tidy up existing resources?
    
    repositoryInitOK = initRepository();
    
    if ( !repositoryInitOK )
      throw new Exception( "Repository did not initialise properly" );
  }
  
  @Override
  public UUID getID()
  { return repoID; }
  
  @Override
  public String getName()
  { return repoName; }
  
  @Override
  public Entry<UUID,String> getResourceStreamInfo( InputStream transientStream )
  {
    // Safety first
    if ( transientStream == null ) return null;
    
    UUID resID;
    String resName;
    BufferedReader br = new BufferedReader( new InputStreamReader(transientStream) );
    StringBuilder  sb = new StringBuilder();
    
    // Read in resource as a string
    String resLine = null;
    boolean reading = true;
    do
    {
      try { resLine = br.readLine(); }
      catch( IOException ioe ) {}
      
      if ( resLine != null )
        sb.append( resLine );
      else
        reading = false;
    }
    while ( reading );
    
    // Tidy up reading resources
    try
    { br.close(); }
    catch( IOException ioe ) {}
    
    // Try get process resource ID
    String xmlRes = sb.toString();
    
    // Try finding process ID (ID needs conversion from Activiti convention)
    String resIDValue = extractAttribute( xmlRes, "<process id=\"", 0, "\"" );
    resIDValue = resIDValue.replace( "Treatment_", "" );
    resID = UUID.fromString( resIDValue );
    resName = extractAttribute( xmlRes,
                                "name=\"", 0,
                                "\">" );
    
    return new HashMap.SimpleEntry<UUID, String>( resID, resName );
  }
  
  @Override
  public TreatmentTemplate createTreatmentTemplate( UUID resourceID,
                                                    String resourceName,
                                                    InputStream stream,
                                                    String title,
                                                    String description ) throws Exception
  {
    // Safety first
    if ( stream == null )            throw new Exception( "Resource stream is null" );
    if ( !repositoryInitOK )         throw new Exception( "Repository did not initialise properly" );
    if ( activitiDeployment != null) throw new Exception( "Resource deployment already in place" );
    if ( deploymentBuilder == null ) throw new Exception( "Deployment builder not available" );
   
    if ( resourceID == null ) throw new Exception( "Resource ID is null" );
    
    if ( treatmentTemplates.containsKey(resourceID) )
      throw new Exception( "Resource UUID already exists" );
    
    // Create a new process resource and store for later (Activiti name formatting required)
    String activitiResID = resourceID.toString();   
    activitiResID += ".bpmn20.xml";
    
    TreatmentTemplate template = null;
    
    if ( deploymentBuilder.addInputStream( activitiResID, stream ) != null )
    {
      template = new TreatmentTemplate( title, description, resourceID );
      treatmentTemplates.put( resourceID, template );
    }
    
    return template;
  }
  
  @Override
  public Set<TreatmentTemplate> getCopyofTreatmentTemplates()
  {
    HashSet<TreatmentTemplate> copyOfTemplates = new HashSet<TreatmentTemplate>();
    
    Iterator<TreatmentTemplate> ttIt = treatmentTemplates.values().iterator();
    while ( ttIt.hasNext() )
    {
      TreatmentTemplate tt = ttIt.next();
      copyOfTemplates.add( new TreatmentTemplate(tt) );
    }
    
    return copyOfTemplates; // return copy
  }
  
  @Override
  public void deployTreatmentTemplates() throws Exception
  {
    if ( !repositoryInitOK ) throw new Exception( "Repository did not initialise properly" );
    if ( activitiDeployment != null ) throw new Exception( "Process resources already deployed" );
    if ( treatmentTemplates.isEmpty() ) throw new Exception( "No process resources currently available" );
    
    activitiDeployment = deploymentBuilder.deploy();
    if ( activitiDeployment == null ) throw new Exception( "Activiti process deployment failed" );  
  }
  
  @Override
  public void updateTreatmentTemplateData( Collection<TreatmentTemplate> templates )
  {
    if ( templates != null )
    {
      Iterator<TreatmentTemplate> ttIt = templates.iterator();
      while ( ttIt.hasNext() )
      {
        TreatmentTemplate upTT     = ttIt.next();
        TreatmentTemplate storedTT = treatmentTemplates.get( upTT.getID() );
        
        if ( storedTT != null )
        {
          storedTT.setTreatmentTitle( upTT.getTitle() );
          storedTT.setTreatmentDescription( upTT.getDescription() );
        }
      }
    }
  }
  
  @Override
  public IProcessRender createTemplateRender( UUID templateID ) throws Exception
  {
    // Safety first
    if ( !repositoryInitOK ) throw new Exception( "Repository has not initialised" );
    if ( templateID == null ) throw new Exception( "Treatment template ID is null" );
    if ( !treatmentTemplates.containsKey(templateID) ) throw new Exception( "Treatment template does not exist" );
    
    // Find process definition for resource
    TreatmentTemplate tt = treatmentTemplates.get( templateID );
    String activitiID = "Treatment_" + tt.getID().toString();
    
    ProcessDefinition pd = 
            activitiRepoService.
              createProcessDefinitionQuery().
                processDefinitionKey( activitiID ).singleResult();
    
    if ( pd == null ) throw new Exception( "Could not find process definition for resource" );
    
    // Create rendering
    InputStream is = activitiRepoService.getProcessDiagram( pd.getId() );
    if ( is == null ) throw new Exception( "Activiti could not render the process resource" );
        
    return new TreatmentProcessRender( is, "png" );
  }
  
  @Override
  public Treatment createTreatment( UUID templateID, UUID riskID, Float tmtIndex ) throws Exception
  {
    Treatment treatment;
    
    // Safety first
    if ( !repositoryInitOK ) throw new Exception( "Treatment repository is not ready" );
    if ( templateID == null ) throw new Exception( "Process resource ID is invalid" );
    if ( riskID == null ) throw new Exception( "Risk ID is invalid" );
    if ( activitiDeployment == null ) throw new Exception( "Process resources have not been deployed" );
    if ( !treatmentTemplates.containsKey(templateID) ) throw new Exception( "Treatment template does not exist" );
    
    // Check if we already have a treatment for this template - if so, return it
    treatment = getTreatmentForTemplate(templateID, riskID);
    
    if (treatment != null) {
        System.out.println("WARNING: treatment already exists for riskID " + riskID + ", templateID " + templateID + " :" + treatment.getID());
        return treatment;
    }
    
    // Create the new treatment (but do not create process yet)
    TreatmentTemplate tt = treatmentTemplates.get( templateID );    
    
    // Create treatment
    treatment = tt.createTreatmentInstance();
    treatment.setTitle( tt.getTitle() );       
    treatment.setDescription( tt.getDescription() );  
    treatment.setLinkedRiskID( riskID );
    treatment.setIndex( tmtIndex );
    
    currentTreatments.put( treatment.getID(), treatment );
    
    return treatment;
  }
  
  // Find any current treatment for this risk and template
  private Treatment getTreatmentForTemplate(UUID templateID, UUID riskID) {
      for (Treatment treatment : currentTreatments.values()) {
          if (treatment.getLinkedRiskID().equals(riskID)) {
              if (treatment.getActivitiProcResourceID().equals(templateID)) {
                  return treatment;
              }
          }
      }
      
      return null;
  }
  
  @Override
  public void destroyTreatment( UUID treatmentID )
  {
    //TODO
  }
  
  @Override
  public void destroyAllTreatmentsForRisk( UUID riskID )
  {
    //TODO
  }
  
  @Override
  public void startTreatmentProcess( Treatment tmt ) throws Exception
  {
    // Safety first
    if ( tmt == null ) throw new Exception( "Treatment is null" );
    if ( tmt.isTreatmentRunning() ) throw new Exception( "Treatment is already active" );
    if ( tmt.getActivitiProcResourceID() == null ) throw new Exception( "Treatment has not treatment template resource" );
    
    // More safety
    UUID tmtID = tmt.getID();
    
    if ( !currentTreatments.containsKey(tmtID) ) throw new Exception( "Unknown treatment" );
    
    if (liveTreatments.contains(tmtID)
            || liveProcessesByTreamentID.containsKey(tmtID)) {
        tmt.setTreatmentRunning(true); // ensure that flag is consistent with live treatments!
        throw new Exception("Treatment is already active");
    }
    
    // Create new treatment process, start it and store
    TreatmentProcess tp = new TreatmentProcess( this, tmt );
    
    // Update internal model first...
    liveTreatments.add( tmtID );
    liveProcessesByTreamentID.put( tmtID, tp );
    
    //... finally start the process
    try {
        tp.startProcess();
    }
    catch (Exception e) {
        e.printStackTrace();
        liveTreatments.remove(tmtID);
        liveProcessesByTreamentID.remove(tmtID);
        tmt.setTreatmentRunning(false);
        throw new Exception(e.getMessage());
    }
    
  }
  
  @Override
  public void forceStopTreatmentProcess( Treatment tmt ) throws Exception
  {
    // Safety first
    if ( tmt == null ) throw new Exception( "Treatment is null" );
    if ( !tmt.isTreatmentRunning() ) throw new Exception( "Treatment is not currently active" );
    if ( tmt.getActivitiProcResourceID() == null ) throw new Exception( "Treatment has not treatment template resource" );
    
    // More safety
    UUID tmtID = tmt.getID();
    if ( !currentTreatments.containsKey(tmtID) ) throw new Exception( "Unknown treatment" );
    if ( !liveTreatments.contains(tmtID) ||
         !liveProcessesByTreamentID.containsKey(tmtID) ) throw new Exception( "Treatment is not currently active" );
    
    // Kill off process
    TreatmentProcess tp = liveProcessesByTreamentID.get( tmtID );
    
    // First abort any active gateway
    try {
        tmt.abortGateway();
    }
    catch (Exception e) {
        e.printStackTrace();
    }
    
    try {
        tp.forceStopProcess();
    }
    catch ( Exception e ) {
        throw e;
    }
    finally {
        liveTreatments.remove(tmtID);
        liveProcessesByTreamentID.remove(tmtID);
        resetTreatment( tmt );
    }
    
    // No notification as (without exceptions) we can assume treatment has ended
  }
  
  @Override
  public IProcessRender createTreatmentRender( UUID treatmentID ) throws Exception
  {
    if ( treatmentID == null ) throw new Exception( "Treatment ID is null" );
    if ( !liveProcessesByTreamentID.containsKey(treatmentID) ) throw new Exception( "Treatment is not currently started" );
    
    TreatmentProcess tp = liveProcessesByTreamentID.get( treatmentID );
    
    return tp.createProcessImage();
  }
  
  @Override
  public Set<Treatment> getAllLiveTreatments()
  {
    HashSet<Treatment> treatments = new HashSet<Treatment>();
    
    Iterator<Treatment> tmtIt = currentTreatments.values().iterator();
    while ( tmtIt.hasNext() )
    {
      Treatment tmt = tmtIt.next();
      if ( tmt.isTreatmentRunning() ) treatments.add( tmt );
    }
    
    return treatments; // return copy
  }
  
  @Override
  public ROTreatmentGroup getTreatmentsForRO( String roTitle, 
                                              UUID riskID,
                                              List<String> historicProcessIDs )
  {
    ROTreatmentGroup group = new ROTreatmentGroup( roTitle, riskID );
        
    // Find treatments referring the the RO
    HashSet<Treatment> roTreatments = new HashSet<Treatment>();
    Iterator<Treatment> tmtIt = currentTreatments.values().iterator();
    
    while ( tmtIt.hasNext() )
    {
      Treatment tmt = tmtIt.next();
      
      // Search for any outstanding tasks persisted for this treatment
      if ( tmt.getLinkedRiskID().equals(riskID) )
        roTreatments.add( tmt );
    }
    
    // Update the group with known current treatments
    if ( !roTreatments.isEmpty() )
      group.addTreatments( roTreatments );
    
    // Get any historic records
    if ( historicProcessIDs != null && !historicProcessIDs.isEmpty() )
      group.setHistoricTreatments( getHistoricTreatmentRecords(historicProcessIDs) );
    
    return group;
  }
  
  @Override
  public void notifyTreatmentTaskCompleted( TreatmentTask task )
  {
    if ( task != null )
    {
      Treatment tmt = task.getTreatment();
      TreatmentProcess tmtProc = liveProcessesByTreamentID.get( tmt.getID() );
      
      if ( tmtProc != null ) tmtProc.notifyTaskHasCompleted( task );
    }
  }
  
  @Override
  public List<TreatmentRecord> getHistoricTreatmentRecords( List<String> procInstIDs )
  {
    ArrayList<TreatmentRecord> recordList = new ArrayList<TreatmentRecord>();
    
    if ( procInstIDs != null && procInstIDs.size() > 0 )
    {
      HistoryService hs = activitiProcEngine.getHistoryService();
      if ( hs != null )
      {
        HistoricProcessInstanceQuery hq = hs.createHistoricProcessInstanceQuery();
        
        // Convert list to a set
        HashSet<String> instanceIDs = new HashSet<String>();
        instanceIDs.addAll( procInstIDs );
        
        // And get historical instances, if there are any
        List<HistoricProcessInstance> historyProcList =
                hq.processInstanceIds( instanceIDs ).
                   orderByProcessInstanceEndTime().
                      asc().list();
        
        // Using these instances, get history for each instance
        Iterator<HistoricProcessInstance> procIt = historyProcList.iterator();
        while ( procIt.hasNext() )
        {
          HistoricProcessInstance hpi = procIt.next();
          Date piStart    = hpi.getStartTime();
          Date piStop     = hpi.getEndTime();
          String piReason = hpi.getDeleteReason();
          
          // Get the variables for this instance and map them
          HashMap<String, Object> procVars = new HashMap<String, Object>();
          
          HistoricDetailQuery hdq = hs.createHistoricDetailQuery();
          List<HistoricDetail> details = 
                  hdq.processInstanceId( hpi.getId() ).variableUpdates().orderByVariableName().asc().list();
          
          Iterator<HistoricDetail> histIt = details.iterator();
          while( histIt.hasNext() )
          {
            HistoricVariableUpdate hvu = (HistoricVariableUpdate) histIt.next();
            procVars.put( (String) hvu.getVariableName(), (Object) hvu.getValue() );
          }
          
          // Finally, create the record, if we can get the data
          String tTitle  = (String) procVars.get( "tmtTitle" );
          String tDesc   = (String) procVars.get( "tmtDesc" );
          String ttIDVal = (String) procVars.get( "tmtResID" );
          String rIDVal  = (String) procVars.get( "riskID" );
          
          if ( piStart != null && piStop != null &&
               tTitle  != null && tDesc  != null &&
               ttIDVal != null && rIDVal  != null )
          {
            UUID rID = UUID.fromString( rIDVal );
            UUID resID = UUID.fromString ( ttIDVal );
            
            recordList.add( new TreatmentRecord( rID, tTitle, tDesc,
                                                 resID,
                                                 piStart, piStop, piReason ) );
          }
        }
      }
    }
    
    return recordList;
  }
  
    @Override
    public String getProcessDefIdForResource(UUID procResID) {
        return processDefinitions.get(procResID);
    }
    
  // IActivitiProcExeListener --------------------------------------------------
  @Override
  public void onExecutionStart( DelegateExecution de )
  {
      Date timeStamp = new Date();

      if (de != null) {
          ExecutionEntity ee = (ExecutionEntity) de;
          ActivityImpl activity = ee.getActivity();
          String activityType = (String) activity.getProperty("type");

          UUID tmtID = UUID.fromString((String) de.getVariable("tmtID"));

          if (tmtID != null) {
              Treatment tmt = currentTreatments.get(tmtID);

              if (tmt != null) {
                  if (activityType.equals("startEvent")) {
                      tmt.setStartDate(timeStamp);

                      List<ITreatmentProcessListener> procList = getListenersByType();
                      for (ITreatmentProcessListener listener : procList) {
                          listener.onProcessStarted(tmt);
                      }
                  } else if (activityType.equals("exclusiveGateway")) {
                      onGatewayActivityStarted(tmt, activity);
                      List<ITreatmentProcessListener> procList = getListenersByType();
                      for (ITreatmentProcessListener listener : procList) {
                          listener.onActivityStarted(tmt, activity);
                      }
                  }
              }
          }
      }
  }
  
    private void onGatewayActivityStarted(Treatment tmt, ActivityImpl activity) {
        String gatewayID = activity.getId();
        List<PvmTransition> outgoingTransitions = activity.getOutgoingTransitions();

        ArrayList<TreatmentTask> gatewayTasks = new ArrayList<TreatmentTask>();
        
        for (PvmTransition pvmt : outgoingTransitions) {
            TransitionImpl transition = (TransitionImpl) pvmt;
            ActivityImpl dest = transition.getDestination();

            String name = (String) dest.getProperty("name");
            String desc = (String) dest.getProperty("documentation");
            String type = (String) dest.getProperty("type"); // e.g. subProcess

            TreatmentTask tt = new TreatmentTask(tmt,
                    null, // id is created automatically
                    dest.getId(),
                    name,
                    desc,
                    type);

            tmt.addTask(tt);
            gatewayTasks.add(tt);
        }
        
        tmt.onGatewayActivityStarted(gatewayID, gatewayTasks);
    }

    @Override
    public void onExecutionEnd(DelegateExecution de) {
        Date timeStamp = new Date();

        if (de != null) {
            ExecutionEntity ee = (ExecutionEntity) de;
            ActivityImpl activity = ee.getActivity();
            String activityType = null;
            
            if (activity != null) {
                activityType = (String) activity.getProperty("type");
            }
            
            // Reset treatment and remove current process
            UUID tmtID = UUID.fromString((String) de.getVariable("tmtID"));
            
            if (tmtID != null) {
                Treatment tmt = currentTreatments.get(tmtID);
                if (tmt != null) {
                    if ( (activityType == null) || activityType.equals("endEvent") ) {
                        resetTreatment(tmt);
                        tmt.setStopDate(timeStamp);

                        liveTreatments.remove(tmtID);
                        liveProcessesByTreamentID.remove(tmtID);

                        List<ITreatmentProcessListener> procList = getListenersByType();
                        for (ITreatmentProcessListener listener : procList) {
                            listener.onProcessCompleted(tmt);
                        }
                    } else if (activityType.equals("exclusiveGateway")) {
                        List<ITreatmentProcessListener> procList = getListenersByType();
                        for (ITreatmentProcessListener listener : procList) {
                            listener.onActivityCompleted(tmt, activity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isSelected(String id, DelegateExecution de) {
        boolean selected = true;
        
        if ((id != null) && (de != null)) {
            String processID = de.getProcessInstanceId();
            UUID tmtID = UUID.fromString((String) de.getVariable("tmtID"));
            ExecutionEntity ee = (ExecutionEntity) de;
            ActivityImpl activity = ee.getActivity();
            String activityType = null;

            if (activity != null) {
                activityType = (String) activity.getProperty("type");
                if ( (activityType != null) && (activityType.equals("exclusiveGateway")) ){
                    if (tmtID != null) {
                        Treatment tmt = currentTreatments.get(tmtID);
                        if (tmt != null) {
                            TreatmentTask tt = tmt.getSelectedGatewayTask();
                            if (tt == null) {
                                return false;
                            }
                            return id.equals( tt.getTaskDefinitionKey() );
                        }
                    }
                }
            }
        }
        
        return selected;
    }
    
  // IActivitiProcTaskListener -------------------------------------------------
  @Override
  public void onTaskCreated( DelegateTask dt )
  { 
    if ( dt != null )
    {
      Treatment targTreatment = null;
      UUID tmtID = UUID.fromString( (String) dt.getVariable( "tmtID" ) );
      
      if ( tmtID != null && liveTreatments.contains(tmtID) )
        targTreatment = currentTreatments.get( tmtID );
      
      if ( targTreatment != null )
      {
        TreatmentTask tt = new TreatmentTask( targTreatment,
                                              dt.getId(),
                                              dt.getTaskDefinitionKey(),
                                              dt.getName(),
                                              dt.getDescription(),
                                              null);
        
        targTreatment.addTask( tt );

        List<ITreatmentProcessListener> procList = getListenersByType();
          for ( ITreatmentProcessListener listener : procList )
            listener.onTaskCreated( tt );
      }
    }
  }
  
  @Override
  public void onTaskCompleted( DelegateTask dt )
  { 
    if ( dt != null )
    {
      Treatment targTreatment = null;
      UUID tmtID = UUID.fromString( (String) dt.getVariable( "tmtID" ) );
      
      if ( tmtID != null && liveTreatments.contains(tmtID) )
        targTreatment = currentTreatments.get( tmtID );
      
      if ( targTreatment != null )
      {
        TreatmentTask tt = new TreatmentTask( targTreatment,
                                              dt.getId(),
                                              dt.getTaskDefinitionKey(),
                                              dt.getName(),
                                              dt.getDescription(),
                                              null );
        
        List<ITreatmentProcessListener> procList = getListenersByType();
          for ( ITreatmentProcessListener listener : procList )
            listener.onTaskCompleted( tt );
      }
    }
  }
  
  // Protected methods ---------------------------------------------------------
  protected TreatmentRepository( UUID id,
                                 String rName,
                                 ProcessEngine pe )
  {
    repoName = rName;
    repoID = id;
    
    TreatmentRepository.procExeListener  = this;
    TreatmentRepository.procTaskListener = this;
    
    activitiProcEngine = pe;
    repositoryInitOK   = initRepository();

    if (treatmentTemplates.isEmpty())  
        demoPopulateTreatmentRepository(); //KEM (moved from CATDemoController)

  }
  
  // Unfortunate, but important: need to free up statically assigned resource
  @Override
  protected void finalize() throws Throwable
  {
    TreatmentRepository.procExeListener  = null;
    TreatmentRepository.procTaskListener = null;
    
    super.finalize();
  }
  
  protected RuntimeService getActivitiRuntimeSerivce()
  { return activitiProcEngine.getRuntimeService(); }
  
  protected TaskService getActivitiTaskService()
  { return activitiProcEngine.getTaskService(); }
  
  protected ProcessDefinition getProcessDefinition( String ID )
  {
    ProcessDefinition procDef = null;
    
    if ( activitiRepoService != null )
      procDef = activitiRepoService.createProcessDefinitionQuery().processDefinitionId(ID).singleResult();
    
    return procDef;
  }
  
  protected ProcessDefinition getRenderableProcessDefinition( ProcessInstance procInstance )
  {
      return getRenderableProcessDefinition(procInstance.getProcessDefinitionId());
  }
  
  protected ProcessDefinition getRenderableProcessDefinition( String procDefID )
  {
    ProcessDefinition procDef = null;
    
    if ( activitiRepoService != null )
    {
      RepositoryServiceImpl rsi = ( RepositoryServiceImpl ) activitiRepoService;
		
      procDef = ( ProcessDefinition ) rsi.getDeployedProcessDefinition( procDefID );
    }
    
    return procDef;
  }
  
  // Private methods -----------------------------------------------------------
  private boolean initRepository()
  {
    createDemoTreatmentTemplateDefinitions(); // KEM
    
    activitiDeployment = null;
    
    if ( activitiProcEngine == null ) return false;
    
    activitiRepoService = activitiProcEngine.getRepositoryService();
    if ( activitiRepoService == null ) return false;
    
    // Update known process resources
    treatmentTemplates  = new HashMap<UUID, TreatmentTemplate>();
    
    // Update known active treatments
    currentTreatments         = new HashMap<UUID, Treatment>();
    liveTreatments            = new HashSet<UUID>();
    liveProcessesByTreamentID = new HashMap<UUID, TreatmentProcess>();
    processDefinitions        = new HashMap<UUID, String>();
    
    // Try get last deployment (and existing treatments)
    if ( retrieveResourcesFromLastDeployment() )
      retrieveLiveTreatmentsFromExistingData();
    else
    {
      // Or create a deployment for treatment templates to be added to
      deploymentBuilder = activitiRepoService.createDeployment();
      if ( deploymentBuilder == null ) return false;
    }
    
    return true;
  }
  
  private boolean retrieveResourcesFromLastDeployment()
  {
    List<Deployment> deployments =
            activitiRepoService.
              createDeploymentQuery().list();
    
    if ( deployments != null && !deployments.isEmpty() )
    {
      activitiDeployment = deployments.get( deployments.size() -1 );
      
      List<ProcessDefinition> currentResDefinitions =
        activitiRepoService.
              createProcessDefinitionQuery().list();
      
      if ( currentResDefinitions != null )
      {
        if (currentResDefinitions.isEmpty()) {
            activitiDeployment = null;  // for some reason there are no process defs in this deployment, so a new deployment will be created
            return false;
        }
        
        for (ProcessDefinition procDef : currentResDefinitions )
        {
          String resKey = procDef.getKey();
          UUID resID = UUID.fromString( resKey.replace( "Treatment_", "" ) );
          
          // Add to process definition map
          processDefinitions.put(resID, procDef.getId());

          // Create skeleton treatments (title and description need updating)
          String name = procDef.getName();
          String desc = procDef.getDescription();
          
          if (desc == null)
              desc = "N/A";
          
          // KEM check if we have stored metadata for treatment template
          TreatmentTemplateDefinition ttd = treatmentTemplateDefinitions.get(resID);
          
          if (ttd != null)
          {
              name = ttd.getTitle();
              desc = ttd.getDescription();
          }
          
          //TreatmentTemplate template = new TreatmentTemplate( "Unknown", "N/A", resID );
          TreatmentTemplate template = new TreatmentTemplate( name, desc, resID );
          treatmentTemplates.put( resID, template );
        }
        
        return true;
      }
    }
    
    return false;
  }
  
  private boolean retrieveLiveTreatmentsFromExistingData()
  {
    boolean foundTreatments = false;
    
    RuntimeService rs        = activitiProcEngine.getRuntimeService();
    ProcessInstanceQuery piq = rs.createProcessInstanceQuery();
    
    List<ProcessInstance> procList = piq.orderByProcessInstanceId().asc().list();
    Iterator<ProcessInstance> plIt = procList.iterator();
    
    while ( plIt.hasNext() )
    {
      ProcessInstance pi = plIt.next();
      String piID        = pi.getId();
      
      UUID treatmentID = UUID.fromString( (String) rs.getVariable(piID, "tmtID") );
      
      // Re-create a new treatment if we don't have it already
      if ( treatmentID != null                                   &&
           !currentTreatments.containsKey(treatmentID)           &&
           !liveProcessesByTreamentID.containsKey(treatmentID) )
      {
        // Get treatment attributes from process
        String title   = (String) rs.getVariable( piID, "tmtTitle" );
        String desc    = (String) rs.getVariable( piID, "tmtDesc" );
        Float  order   = (Float)  Float.parseFloat( (String) rs.getVariable( piID, "tmtOrder" ) );
        UUID   riskID  = (UUID)   UUID.fromString ( (String) rs.getVariable( piID, "riskID" ) );
        
        // Clean up activiti's process definition string to get UUID
        String defID = pi.getProcessDefinitionId();
        defID = defID.replace( "Treatment_", "" );
        defID = defID.substring(0, 36);
        
        UUID procResID = UUID.fromString( defID );
        
        // If all OK, create treatment and treatment process
        if ( title != null && desc != null && 
             order != null && riskID != null && procResID != null )
        {
          // Create new treatment
          Treatment treatment = new Treatment( title, desc, order,
                                               treatmentID, riskID,
                                               procResID, pi.getId() );
          
          treatment.setTreatmentRunning( true );          
          
          // Recreate treatment process
          TreatmentProcess tp = new TreatmentProcess( this, treatment );
          
          // Add to treatments store
          currentTreatments.put( treatmentID, treatment );
          liveTreatments.add( treatmentID );
          liveProcessesByTreamentID.put( treatmentID, tp );
          
          // Update with any outstanding tasks
          updateOutstandingTasks( treatment );
          
          foundTreatments = true;
        }
      }
    }
    
    return foundTreatments;
  }
  
  private void updateOutstandingTasks( Treatment tmtOUT )
  {
    TreatmentProcess tp = liveProcessesByTreamentID.get( tmtOUT.getID() );
    
    if ( tp != null )
    {
      String procInstID = tp.getProcessInstanceID();

      if ( procInstID != null )
      {
        TaskService ts = activitiProcEngine.getTaskService();
        TaskQuery tq = ts.createTaskQuery();

        List<Task> tasks = 
            tq.processInstanceId(procInstID).
                orderByTaskCreateTime().desc().list();

        Iterator<Task> taskIt = tasks.iterator();
        while ( taskIt.hasNext() )
        {
          Task task = taskIt.next();

          TreatmentTask tt = new TreatmentTask( tmtOUT,
                                                task.getId(),
                                                //task.getExecutionId(),
                                                task.getTaskDefinitionKey(),
                                                task.getName(),
                                                task.getDescription(),
                                                null );

          tmtOUT.addTask( tt );
        }
      }
    }
  }
    
  private String extractAttribute( String source,
                                   String startPattern,
                                   int    offset,
                                   String endPattern )
  {
    String result = null;
    
    int indStart = source.indexOf( startPattern, offset );
    if ( indStart > 0 )
    {
      indStart += startPattern.length();
      int indEnd = source.indexOf( endPattern, indStart );
      
      if ( indEnd > indStart )
        result = source.substring( indStart, indEnd  );
    }
    
    return result;
  }
  
  private void resetTreatment( Treatment tmt )
  {
    tmt.setActivitiProcInstanceID( null );
    tmt.setTreatmentRunning( false );
    tmt.clearTasks();
  }

  private void createDemoTreatmentTemplateDefinitions()
  {
      TreatmentTemplateDefinition ttd1 = new TreatmentTemplateDefinition( "TreatmentPlan_01.bpmn20.xml",
                                   "Address drop in user activity",
                                   "Use this treatment plan to address a drop in user activity within your community",
                                   UUID.fromString("01ac6047-4f13-4959-aa52-a9b392edefb0") );
      
      TreatmentTemplateDefinition ttd2 = new TreatmentTemplateDefinition( "TreatmentPlan_02.bpmn20.xml",
                                   "Improve Role Composition",
                                   "A low number of active users might be caused by users feeling ignored when they want to discusss issues. This policy-based treatment includes simulating discussion thread order changes so that newest replies come first.",
                                   UUID.fromString("96c83bf0-773d-4553-b891-84deefe2e779") );
      
      treatmentTemplateDefinitions = new HashMap<UUID, TreatmentTemplateDefinition>();
      
      treatmentTemplateDefinitions.put(ttd1.getID(), ttd1);
      treatmentTemplateDefinitions.put(ttd2.getID(), ttd2);
  }
  
    private void demoPopulateTreatmentRepository() {
        if (!createTreatmentTemplate(UUID.fromString("01ac6047-4f13-4959-aa52-a9b392edefb0"))) {
            log.error("Could not create TreatmentPlan_01");
        }

        if (!createTreatmentTemplate(UUID.fromString("96c83bf0-773d-4553-b891-84deefe2e779"))) {
            log.error("Could not create TreatmentPlan_02");
        }

        // Deploy them now
        try {
            this.deployTreatmentTemplates();
        } catch (Exception e) {
            log.error("Could not deployment treatment templates: " + e.getMessage());
        }
    }
 
    private boolean createTreatmentTemplate(UUID uuid) {
        TreatmentTemplateDefinition ttd = treatmentTemplateDefinitions.get(uuid);

        if (ttd != null) {
            return createTreatmentTemplate(ttd.getResourceFile(), ttd.getTitle(), ttd.getDescription());
        }

        return false;
    }

    private boolean createTreatmentTemplate( String resource, String templateTitle, String templateDesc) {
        if (resource != null) {
            // Original work-flow
            ClassLoader cl = getClass().getClassLoader();
            InputStream is = cl.getResourceAsStream(resource);

            if (is != null) {
                // Get info about resource first
                Entry<UUID, String> resInfo = this.getResourceStreamInfo(is);

                if (resInfo != null) {
                    try {
                        // Re-load stream and add a resource
                        is.close();

                        is = cl.getResourceAsStream(resource);
                        this.createTreatmentTemplate(resInfo.getKey(),
                                resInfo.getValue(),
                                is,
                                templateTitle,
                                templateDesc);
                        
                        return true; //KEM

                    } catch (Exception e) {
                        log.error("Could not deploy treatment template: " + resource + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

}
