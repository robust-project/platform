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
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;




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
    if ( treatment == null ) throw new Exception( "Process has no associated treatment" );
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
   
      System.out.println("Start process instance: " + activitiProcResID);
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
      treatment.setTreatmentRunning( false );
      String procId = treatment.getActivitiProcInstanceID();
      String message = "Could not start the process";
      message += (procId == null) ? "" : " " + procId;
      message += ": " + ae.getLocalizedMessage();
      throw new Exception(message);
    }
  }
  
  protected void forceStopProcess() throws Exception
  {
    if ( treatment == null ) throw new Exception( "Process has no associated treatment" );
    if ( !treatment.isTreatmentRunning() ) throw new Exception( "Treatment process is not running" );
    if (activitiProcInstance == null) {
        System.out.println("WARNING: forceStopProcess() called but activitiProcInstance already null");
        fatRenderer = null;
        return;
    }
    
    // Check that there is still an active process running
    RuntimeService rs = treatmentRepository.getActivitiRuntimeSerivce();
    
    if ( rs == null ) 
        throw new Exception( "Could not get Activiti run-time serivce" );
    
    ProcessInstanceQuery piq = rs.createProcessInstanceQuery();
    ProcessInstance target = piq.processInstanceId( activitiProcInstance.getProcessInstanceId() ).
                                                    orderByProcessInstanceId().
                                                    asc().
                                                    singleResult();
    
    if ( target == null ) throw new Exception( "Treatment process does not exist: " + activitiProcInstance.getProcessInstanceId());
    
    // Kill off process
    try
    {
      rs.deleteProcessInstance( target.getProcessInstanceId(),
                                "Risk manager stopped the process early" );
      
      // Treatment instance status to be updated by the repository
    }
    catch ( ActivitiException ae ) {
        throw ae;
    }
    finally {
      activitiProcInstance = null;
      fatRenderer          = null;
    }
  }
  
  protected void resumeProcess() throws Exception
  {
    if ( !treatment.isTreatmentRunning() ) throw new Exception( "Process has not started" );
    
    //TODO
  }
  
  protected IProcessRender createProcessImage()
  {
    IProcessRender render = null;
    
    String procDefID = null;

    if ( activitiProcInstance != null ) {
        procDefID = activitiProcInstance.getProcessDefinitionId();
    }
    else {
        procDefID = treatmentRepository.getProcessDefIdForResource(treatment.getActivitiProcResourceID());
    }
    
    if ( procDefID != null )
    {
      // Get (a rendereable) process definition for this instance
      ProcessDefinitionEntity pd = 
              (ProcessDefinitionEntity)treatmentRepository.getRenderableProcessDefinition( procDefID );

      // Current task def ids      
      List<String> currentTaskDefIDs = new ArrayList<String>();

      // Active task def ids
      List<String> activeTaskDefIDs = new ArrayList<String>();
      
      Set<String> currentTaskIDs = treatment.getCurrentTaskIDs();
      
      for ( String tID : currentTaskIDs )
      {
        TreatmentTask tt = treatment.getTask( tID );
        currentTaskDefIDs.add(tt.getTaskDefinitionKey());
        
        if (! tt.getTaskDefinitionKey().equals( tt.getTaskID() )) {
            // only add new (active) task if not a provisional task from an exclusive gateway
            System.out.println("Active task: " + tt.getTaskDefinitionKey() + " (" + tt.getName() + ")");
            activeTaskDefIDs.add( tt.getTaskDefinitionKey() );
        }
      }
              
      if ( pd != null )
      {
        if ( fatRenderer == null ) fatRenderer = new TreatmentProcessFatRenderer(); 
          fatRenderer.setProcessDefinition(pd);
          fatRenderer.setActiveIDs(activeTaskDefIDs);

          InputStream is = fatRenderer.generateBPMNDiagram();
          if (is != null) {
              render = new TreatmentProcessRender(is, ".png");
              if (currentTaskDefIDs.size() > 0) {
                  // Recalculate centre of focus for workflow, based on all current tasks (active or not)
                  recalculateCentre(render, pd, currentTaskDefIDs);
              }
          }
      }
    }
    
    return render;
  }
  
    private void addCurrentActivities(List<ActivityImpl> activities, List<String> taskDefIDs, ArrayList<ActivityImpl> currentActivities) {
        // Get all current activities
        for (ActivityImpl activity : activities) {
            List<ActivityImpl> subActivities = activity.getActivities();
            if (subActivities.size() > 0) {
                // If this activity is a subProcess, get all its subActivities and add them to the list
                addCurrentActivities(subActivities, taskDefIDs, currentActivities);
            }
            else {
                String id = activity.getId();
                if (taskDefIDs.contains(id)) {
                    currentActivities.add(activity);
                    //System.out.println(id + " (active)");
                } else {
                    //System.out.println(id);
                }
            }
        }
    }
  
    private void recalculateCentre(IProcessRender render, ProcessDefinitionEntity pd, List<String> taskDefIDs) {
        // KEM: determine coords of current activity (or centre of current group of activities),
        // so we can scroll to that position
        List<ActivityImpl> activities = pd.getActivities();
        ArrayList<ActivityImpl> currentActivities = new ArrayList<ActivityImpl>();

        addCurrentActivities(activities, taskDefIDs, currentActivities);
        
        int minX = 0;
        int minY = 0;
        int maxX = 0;
        int maxY = 0;
        
        if (currentActivities.size() == 0)
            System.out.println("WARNING: no current activities found");
        
        for (ActivityImpl activity : currentActivities) {
            int actX = activity.getX();
            int actY = activity.getY();
            int actW = activity.getWidth();
            int actH = activity.getHeight();
            
            if ((minX == 0) || (actX < minX)) {
                minX = actX;
            }
            
            if ((minY == 0) || (actY < minY)) {
                minY = actY;
            }
            
            if ((maxX == 0) || ((actX + actW) > maxX)) {
                maxX = actX + actW;
            }
            
            if ((maxY == 0) || ((actY + actH) > maxY)) {
                maxY = actY + actH;
            }
            
            System.out.println(activity.getId() + " minX:"+minX + " maxX:"+maxX + " minY:"+minY + " maxY:"+maxY);
        }
        
        int centreX = minX + (maxX - minX) / 2;
        int centreY = minY + (maxY - minY) / 2;
        
        System.out.println("centreX:" + centreX + " centreY:" + centreY);
        
        render.setCentreX(centreX);
        render.setCentreY(centreY);
    }

  protected void notifyTaskHasCompleted( TreatmentTask task )
  {
    if ( task != null )
    {
      if (task.isTaskCompletionNotified()) {
          System.out.println("WARNING: notifyTaskHasCompleted already called for task: " + task.getTaskID());
          return;
      }
      
      task.setTaskCompletionNotified(true);
          
      Treatment tmt = task.getTreatment();
      tmt.removeTask( task.getTaskID() );
      tmt.removeTask( task.getTaskDefinitionKey() ); // in case old gateway task still in list
      
      TaskService ts = treatmentRepository.getActivitiTaskService();
      if (! task.getTaskID().equals( task.getTaskDefinitionKey() )) {
          //synchronized(ts) {
          try {
              ts.complete( task.getTaskID() );
          }
          catch (ActivitiException e) {
              if (! tmt.isGatewayAborted()) {
                  throw e;
              }
          }
          //}
      }
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
