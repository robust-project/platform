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
//      Created Date :          21-Jun-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.spec;

import java.util.*;




public interface ITreatmentWorkflowProvider
{
  /**
   * ID should be statically defined for each specific provider type
   * 
   * @return - UUID uniquely identifying the provider type
   */
  UUID getTypeID();
  
  /**
   * Returns the human-readable name for the treatment provider
   * 
   * @return - String of human-readable name for treatment provider
   */
  String getName();
  
  /**
   * Attempts to initialise the treatment provider. Must be non-blocking call 
   * (listener will return result)
   * 
   * @param providerConfig - Properties instance containing key-value pairs specific to the provider
   * @param listener       - Listener of for the result of initialisation
   * @throws Exception     - Throws when above parameters are invalid
   */
  void intialiseProvider( Properties providerConfig,
                          ITreatmentWorkflowProviderListener listener ) throws Exception;
  
  /**
   * Attempts to create a process engine for use with the Treatment Engine proper.
   * Must be non-blocking call (listener will return result)
   * 
   * @param listener       - Listener of for the result of initialisation
   * @throws Exception     - Throws when above parameters are invalid
   */
  void requestProcessEngine( ITreatmentWorkflowProviderListener listener ) throws Exception;
  
  /**
   * Attempts to release the resources associated with the provider (such as database
   * connections/drivers etc)
   */
  void cleanUpProvider();
}
