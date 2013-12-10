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
//      Created Date :          18-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.treatment;

import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.*;

import uk.ac.soton.itinnovation.robust.riskmodel.Risk;




public interface ITreatmentCentreController
{
  ITreatmentRepository getRepository();
  
  void setRepository( ITreatmentRepository repo );
  
  void setCommunity( UUID communityID );
  
  boolean handleTreatmentOfRisk( Risk risk );
  
  void handleRiskRemoval( Risk risk );
}
