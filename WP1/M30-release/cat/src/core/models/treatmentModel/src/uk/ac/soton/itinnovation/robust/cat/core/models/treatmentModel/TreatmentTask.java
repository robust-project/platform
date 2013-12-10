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
//      Created Date :          23-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel;




public class TreatmentTask
{
  private Treatment treatment;
  private String    activitiTaskID;
  private String    activitiTaskDefKey;
  private String    taskName;
  private String    taskDescription;
  
  private boolean taskCompleted = false;

  public TreatmentTask( Treatment tmt,
                        String taskID,
                        String taskDefID,
                        String name,
                        String desc )
  {
    treatment          = tmt;
    activitiTaskID     = taskID;
    activitiTaskDefKey = taskDefID;
    taskName           = name;
    taskDescription    = desc;
  }
  
  public TreatmentTask( TreatmentTask tt )
  {
    treatment          = tt.treatment;
    activitiTaskID     = tt.activitiTaskID;
    activitiTaskDefKey = tt.activitiTaskDefKey;
    taskName           = tt.taskName;
    taskDescription    = tt.taskDescription;
    taskCompleted      = tt.taskCompleted;
  }
  
  public Treatment getTreatment()
  { return treatment; }
  
  public String getTaskID()
  { return activitiTaskID; }
  
  public String getTaskDefinitionKey()
  { return activitiTaskDefKey; }
  
  public String getName()
  { return taskName; }
  
  public String getDescription()
  { return taskDescription; }
  
  public boolean isTaskCompleted()
  { return taskCompleted; }
}
