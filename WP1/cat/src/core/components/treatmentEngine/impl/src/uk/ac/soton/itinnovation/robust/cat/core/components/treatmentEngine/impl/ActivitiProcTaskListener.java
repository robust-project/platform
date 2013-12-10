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

package uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.impl;

import java.io.Serializable;
import org.activiti.engine.delegate.*;




class ActivitiProcTaskListener implements Serializable,
                                          TaskListener
{
  private static final long serialVersionUID = 1L;
  
  
  @Override
  public void notify( DelegateTask delegateTask )
  {
    if ( delegateTask != null )
    {
      IActivitiProcTaskListener aptl = TreatmentRepository.getProcessTaskListener();
      String eventName = delegateTask.getEventName();
      
      if ( eventName.equals("create") )
        aptl.onTaskCreated( delegateTask );
      else
        if ( eventName.equals("complete") )
          aptl.onTaskCompleted( delegateTask );
    }
  }
}
