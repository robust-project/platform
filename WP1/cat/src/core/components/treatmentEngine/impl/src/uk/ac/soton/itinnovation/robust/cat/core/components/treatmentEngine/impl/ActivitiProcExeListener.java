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


import org.activiti.engine.delegate.*;
import java.io.Serializable;


class ActivitiProcExeListener implements Serializable,
                                         ExecutionListener
{
  private static final long serialVersionUID = 1L;
  
  @Override
  public void notify(DelegateExecution de) throws Exception
  { 
    if ( de != null )
    {
      IActivitiProcExeListener apel = TreatmentRepository.getProcessExeListener();
      String eventName = de.getEventName();
      
      if ( eventName.equals("start") )
          apel.onExecutionStart( de );
      else if ( eventName.equals("end") )
          apel.onExecutionEnd( de );
    }
  }
  
  public boolean isSelected(String id, DelegateExecution de) {
      boolean selected = false;
      
      if ( (id != null) && (de != null)) {
          IActivitiProcExeListener apel = TreatmentRepository.getProcessExeListener();
          selected = apel.isSelected(id, de);
      }
      
      return selected;
  }
}
