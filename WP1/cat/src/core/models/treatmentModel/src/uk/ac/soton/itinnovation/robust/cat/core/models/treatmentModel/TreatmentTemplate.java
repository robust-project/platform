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
//      Created Date :          16-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel;

import java.util.UUID;




public class TreatmentTemplate
{
  private String treatmentTitle;
  private String treatmentDescription;
  private UUID   activitiProcResourceID;
  
  public TreatmentTemplate()
  {}
  
  public TreatmentTemplate( String title,
                            String description,
                            UUID   activitiProcResID )
  {
    treatmentTitle         = title;
    treatmentDescription   = description;
    activitiProcResourceID = activitiProcResID;
  }
  
  public TreatmentTemplate( TreatmentTemplate tt )
  {
    treatmentTitle         = tt.treatmentTitle;
    treatmentDescription   = tt.treatmentDescription;
    activitiProcResourceID = tt.activitiProcResourceID;
  }

    public UUID getActivitiProcResourceID() {
        return activitiProcResourceID;
    }

    public void setActivitiProcResourceID(UUID activitiProcResourceID) {
        this.activitiProcResourceID = activitiProcResourceID;
    }

    public String getTreatmentDescription() {
        return treatmentDescription;
    }

    public void setTreatmentDescription(String treatmentDescription) {
        this.treatmentDescription = treatmentDescription;
    }

    public String getTreatmentTitle() {
        return treatmentTitle;
    }

    public void setTreatmentTitle(String treatmentTitle) {
        this.treatmentTitle = treatmentTitle;
    }
  
  
  public String getTitle()
  { return treatmentTitle; }
  
  public String getDescription()
  { return treatmentDescription; }
  
  public UUID getID()
  { return activitiProcResourceID; }
  
  public Treatment createTreatmentInstance()
  {
    return new Treatment( treatmentTitle,
                          treatmentDescription,
                          1.0f,
                          null,
                          activitiProcResourceID );
  }
  
  public boolean isEqual( TreatmentTemplate rhs )
  {
    if ( treatmentTitle.equals( rhs.getTitle() )             &&
         treatmentDescription.equals( rhs.getDescription() ) &&
         activitiProcResourceID.equals( rhs.getID() ) )
      return true;
    
    return false;
  }
}
