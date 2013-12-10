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
//      Created Date :          16-Oct-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel;

import java.util.*;




public class TreatmentRecord
{
  private UUID   riskID;
  private String treatmentTitle;
  private String treatmentDescription;
  private UUID   tmtTemplateID;
  private Date   treatmentStarted;
  private Date   treatmentStopped;
  private String stoppedReason;
  
  
  public TreatmentRecord( UUID rID,
                          String tTitle,
                          String tDesc,
                          UUID ttResID,
                          Date tmtStart,
                          Date tmtStop,
                          String reason )
  {
    riskID           = rID;
    treatmentTitle        = tTitle;
    treatmentDescription  = tDesc;
    tmtTemplateID    = ttResID;
    treatmentStarted = tmtStart;
    treatmentStopped = tmtStop;
    stoppedReason    = reason;
  }
  
  public TreatmentRecord( Treatment tmt )
  {
    riskID               = tmt.getLinkedRiskID();
    treatmentTitle       = tmt.getTitle();
    treatmentDescription = tmt.getDescription();
    tmtTemplateID        = tmt.getActivitiProcResourceID();
    treatmentStarted     = tmt.getCopyofStartDate();
    treatmentStopped     = tmt.getCopyofStopDate();
    stoppedReason        = "Not available";
  }
  
  public UUID getRiskID()
  { return riskID; }
  
  public String getTreatmentTitle()
  { return treatmentTitle; }
  
  public String getTreatmentDescription()
  { return treatmentDescription; }
  
  public UUID getTreatmentTemplateID()
  { return tmtTemplateID; }
  
  public Date getTreatmentStartDate()
  { return (Date) treatmentStarted.clone(); }
  
  public Date getTreatmentStopDate()
  { return (Date) treatmentStopped.clone(); }
  
  public String getStoppingReason()
  { return stoppedReason; }
}
