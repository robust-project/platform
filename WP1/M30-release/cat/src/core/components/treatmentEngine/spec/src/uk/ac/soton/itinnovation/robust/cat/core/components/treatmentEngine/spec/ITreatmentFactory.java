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
//      Created Date :          14-May-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec;

import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.spec.*;

import java.util.*;





public interface ITreatmentFactory
{
  void discoverProviders();
  
  void releaseProviders();
  
  HashMap<UUID, String> getKnownWorkflowProviderIDs();
  
  ITreatmentWorkflowProvider getTreatmentWorkflowProvider( UUID tmtRepoID ) throws Exception;

  ITreatmentRepository createTreatmentRepository( UUID id, String name,
                                                  IWorkflowEngineWrapper wfew );
}
