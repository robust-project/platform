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
//      Created Date :          07-Nov-2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel;

import java.util.*;




public class Treatment
{
  private UUID   treatmentID;
  private String treatmentTitle;
  private String treatmentDesc;
  private Float  treatmentIndex;
  
  private UUID   linkedRiskID;
  private UUID   activitiProcResourceID;
  private String activitiProcInstanceID;
  
  private Date treatmentStartDate;
  private Date treatmentStopDate;
  
  private boolean treatmentRunning = false;
  
  private HashMap<String, TreatmentTask> treatmentTasks;
  
  
  public Treatment( String title,
                    String desc,
                    Float index,
                    UUID linkedRisk,
                    UUID activitiProcResID )
  {
    treatmentTitle         = title;
    treatmentDesc          = desc;
    treatmentIndex         = index;
    treatmentID            = UUID.randomUUID();
    linkedRiskID           = linkedRisk;
    activitiProcResourceID = activitiProcResID;
    
    treatmentTasks     = new HashMap<String, TreatmentTask>();
    treatmentStartDate = new Date();
    treatmentStopDate  = treatmentStartDate;
  }
  
  
  public Treatment( String title,
                    String desc,
                    Float index,
                    UUID tmtID,
                    UUID linkedRisk,
                    UUID activitiProcResID,
                    String activitiProcID )
  {
    treatmentTitle         = title;
    treatmentDesc          = desc;
    treatmentIndex         = index;
    treatmentID            = tmtID;
    linkedRiskID           = linkedRisk;
    activitiProcResourceID = activitiProcResID;
    activitiProcInstanceID = activitiProcID;
    
    treatmentTasks     = new HashMap<String, TreatmentTask>();
    treatmentStartDate = new Date();
    treatmentStopDate  = treatmentStartDate;
  }
  
  public Treatment( Treatment tmt )
  {
    treatmentTitle         = tmt.getTitle();
    treatmentDesc          = tmt.getDescription();
    treatmentStartDate     = tmt.getCopyofStartDate();
    treatmentStopDate      = tmt.getCopyofStopDate();
    treatmentIndex         = tmt.getCopyOfIndex();
    treatmentID            = tmt.getID();
    linkedRiskID           = tmt.getLinkedRiskID();
    activitiProcResourceID = tmt.getActivitiProcResourceID();
    activitiProcInstanceID = tmt.getActivitiProcInstanceID();
    
    treatmentTasks = new HashMap<String, TreatmentTask>();
    treatmentTasks.putAll( tmt.treatmentTasks );
  }
  
  public UUID getID()
  { return treatmentID; }
  
  public String getTitle()
  { return treatmentTitle; }
  
  public void setTitle( String title )
  { treatmentTitle = title; }
  
  public String getDescription()
  { return treatmentDesc; }
  
  public Date getCopyofStartDate()
  { return (Date) treatmentStartDate.clone(); }
  
  public void setStartDate( Date start )
  { if ( start != null ) treatmentStartDate = start; }
  
  public Date getCopyofStopDate()
  { return (Date) treatmentStopDate.clone(); }
  
  public void setStopDate( Date stop )
  { if ( stop != null ) treatmentStopDate = stop; }
  
  public void setDescription( String desc )
  { treatmentDesc = desc; }
  
  public void setIndex( Float index )
  { treatmentIndex = index; }
  
  public Float getCopyOfIndex()
  { return new Float(treatmentIndex); }
  
  public void setLinkedRiskID( UUID id )
  { linkedRiskID = id; }
  
  public UUID getLinkedRiskID()
  { return linkedRiskID; }
  
  public UUID getActivitiProcResourceID()
  { return activitiProcResourceID; }
  
  public String getActivitiProcInstanceID()
  { return activitiProcInstanceID; }
  
  public void setActivitiProcInstanceID( String ID )
  { activitiProcInstanceID = ID; }
  
  public boolean isTreatmentRunning()
  { return treatmentRunning; }
  
  public void setTreatmentRunning( boolean started )
  { treatmentRunning = started; }
  
  public void clearTasks()
  { treatmentTasks.clear(); }
  
  public boolean addTask( TreatmentTask task )
  {
    // Safety first
    if ( task == null ) return false;
    if ( treatmentTasks.containsKey(task.getTaskID()) ) return false;
    
    treatmentTasks.put( task.getTaskID(), task );
    return true;
  }
  
  public TreatmentTask getTask( String id )
  { return treatmentTasks.get( id ); }
  
  public void removeTask( String taskID )
  { if ( taskID != null ) treatmentTasks.remove( taskID ); }
  
  public Set<String> getCurrentTaskIDs()
  {
    HashSet<String> ids = new HashSet<String>();
    ids.addAll( treatmentTasks.keySet() );
    
    return ids;
  }
  
  public boolean isEqual( Treatment rhs )
  {
    if ( treatmentID.equals( rhs.getID() )             &&
         treatmentTitle.equals( rhs.getTitle() )       &&
         treatmentDesc.equals( rhs.getDescription() )  &&
         treatmentIndex.equals( rhs.getCopyOfIndex() ) &&
         linkedRiskID.equals( rhs.getLinkedRiskID() )  &&
         activitiProcResourceID.equals( rhs.getActivitiProcResourceID() ) &&
         activitiProcInstanceID.equals( rhs.getActivitiProcInstanceID() ) &&
         treatmentRunning == rhs.isTreatmentRunning() &&
         areTasksEqual( rhs ) )
      return true;
    
    return false;
  }
  
  // Private methods -----------------------------------------------------------
  private boolean areTasksEqual( Treatment rhs )
  {
    
    
    return true;
  }
}

