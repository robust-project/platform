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
//      Created Date :          26-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.impl;

import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.*;

import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.*;

import org.activiti.engine.*;
import org.activiti.engine.runtime.*;
import org.activiti.engine.repository.ProcessDefinition;

import java.util.*;
import java.io.*;




class TreatmentProcess
{
  private TreatmentRepository treatmentRepository;
  private Treatment           treatment;
  
  private ProcessInstance             activitiProcInstance;
  private TreatmentProcessFatRenderer fatRenderer;
  
  
  // Protected methods ---------------------------------------------------------
  protected Treatment getTreatment()
  { return treatment; }
  
  protected UUID getProcessResourceID()
  { return treatment.getActivitiProcResourceID(); }
  
  protected String getProcessInstanceID()
  { return treatment.getActivitiProcInstanceID(); }
  
  protected boolean isProcessStarted()
  { return treatment.isTreatmentRunning(); }
  
  protected void startProcess() throws Exception
  {
    if ( treatment == null ) throw new Exception( "Process has no asscoiated treatment" );
    if ( treatment.isTreatmentRunning() ) throw new Exception( "Process has already started" );
    
    try
    {
      RuntimeService rs = treatmentRepository.getActivitiRuntimeSerivce();
      
      if ( rs == null ) 
        throw new Exception( "Could not get Activiti run-time serivce" );
      
      String activitiProcResID = "Treatment_" + treatment.getActivitiProcResourceID().toString();
      
      // Set up process instance variables
      HashMap<String, Object> procVars = new HashMap<String, Object>();
      procVars.put( "tmtTitle",  treatment.getTitle() );
      procVars.put( "tmtDesc",   treatment.getDescription() );
      procVars.put( "tmtID",     treatment.getID().toString() );
      procVars.put( "tmtResID",  treatment.getActivitiProcResourceID().toString() );
      procVars.put( "tmtOrder",  treatment.getCopyOfIndex().toString() );
      procVars.put( "riskID",    treatment.getLinkedRiskID().toString() );
      
      // Execution listeners as well
      procVars.put( "ActivitiProcExeListener",  new ActivitiProcExeListener() );
      procVars.put( "ActivitiProcTaskListener", new ActivitiProcTaskListener() );
   
      activitiProcInstance = rs.startProcessInstanceByKey( activitiProcResID,
                                                           procVars );
      
      String activitiProcInstanceID = activitiProcInstance.getProcessInstanceId();
      
      if ( activitiProcInstanceID != null )
      {
        treatment.setActivitiProcInstanceID( activitiProcInstanceID );
        treatment.setTreatmentRunning( true );
      }
        
    }
    catch (ActivitiException ae )
    {
      throw new Exception( "Could not start the process " + treatment.getActivitiProcInstanceID() + 
                           ", see also:" + ae.getLocalizedMessage() );
    }
  }
  
  protected void forceStopProcess() throws Exception
  {
    if ( treatment == null ) throw new Exception( "Process has no asscoiated treatment" );
    if ( !treatment.isTreatmentRunning() ) throw new Exception( "Treatment process is not running" );
    
    // Check that there is still an active process running
    RuntimeService rs = treatmentRepository.getActivitiRuntimeSerivce();
    
    if ( rs == null ) 
        throw new Exception( "Could not get Activiti run-time serivce" );
    
    ProcessInstanceQuery piq = rs.createProcessInstanceQuery();
    ProcessInstance target = piq.processInstanceId( activitiProcInstance.getProcessInstanceId() ).
                                                    orderByProcessInstanceId().
                                                    asc().
                                                    singleResult();
    
    if ( target == null ) throw new Exception( "Treatment process does not exist" );
    
    // Kill off process
    try
    {
      rs.deleteProcessInstance( target.getProcessInstanceId(),
                                "Risk manager stopped the process early" );
      
      activitiProcInstance = null;
      fatRenderer          = null;
      // Treatment instance status to be updated by the repository
    }
    catch ( ActivitiException ae ) { throw ae; }
  }
  
  protected void resumeProcess() throws Exception
  {
    if ( !treatment.isTreatmentRunning() ) throw new Exception( "Process has not started" );
    
    //TODO
  }
  
  protected IProcessRender createProcessImage()
  {
    IProcessRender render = null;
    
    if ( activitiProcInstance != null )
    {
      // Get (a rendereable) process definition for this instance
      ProcessDefinition pd = 
              treatmentRepository.getRenderableProcessDefinition( activitiProcInstance );
            
      // Collect active tasks
      List<String> taskDefIDs = new ArrayList<String>();
      
      Set<String> taskIDs = treatment.getCurrentTaskIDs();
      for ( String tID : taskIDs )
      {
        TreatmentTask tt = treatment.getTask( tID );
        taskDefIDs.add( tt.getTaskDefinitionKey() );
      }
              
      if ( pd != null )
      {
        if ( fatRenderer == null ) fatRenderer = new TreatmentProcessFatRenderer();
        
        fatRenderer.setProcessDefinition( pd );
        fatRenderer.setActiveIDs( taskDefIDs );
        
        InputStream is = fatRenderer.generateBPMNDiagram();
        if ( is != null )
          return new TreatmentProcessRender( is, ".png" );
      }
    }
    
    return render;
  }
  
  protected void notifyTaskHasCompleted( TreatmentTask task )
  {
    if ( task != null )
    {
      Treatment tmt = task.getTreatment();
      tmt.removeTask( task.getTaskID() );
      
      TaskService ts = treatmentRepository.getActivitiTaskService();
      ts.complete( task.getTaskID() );
    }
  }
  
  protected TreatmentProcess( TreatmentRepository tr,
                              Treatment tmt )
  {
    treatmentRepository = tr;
    treatment           = tmt;
    
    // Retreive activity process instance if ID is known
    String activitiProcInstID = tmt.getActivitiProcInstanceID();
    
    if ( activitiProcInstID != null )
      activitiProcInstance = retrieveProcInstance( activitiProcInstID );
  }
  
  // Private methods -----------------------------------------------------------
  private ProcessInstance retrieveProcInstance( String procID )
  {
    ProcessInstance instance = null;
   
    RuntimeService rs = treatmentRepository.getActivitiRuntimeSerivce();
    ProcessInstanceQuery piq = rs.createProcessInstanceQuery();
    List<ProcessInstance> procList = null;

    try
    { 
      procList = 
          piq.processInstanceId( procID ).
              orderByProcessInstanceId().
                asc().list(); 
    }
    catch (ActivitiException ae ) {}

    if ( procList != null && procList.size() == 1 )
      instance = procList.get( 0 );
   
    return instance;
  }
}
