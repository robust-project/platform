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
//      Created Date :          15 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.riskEditor;

import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.ITreatmentRepository;

import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;

import uk.ac.soton.itinnovation.robust.riskmodel.*;

import java.util.*;





public interface IRiskEditorController
{
  void resetEditorUI();
  
  Map<UUID, Risk> getCopyOfCurrentRisks();
  
  /**
   * Provides the controller access to a set of R/Os
   * 
   * @param risks - set of Risk instances
   */
  void setCurrentRisks( Map<UUID, Risk> risks );
  
  /**
   * Provides the controller access to a set of known objectives for a community
   * 
   * @param objectives - set of Objective instances
   */
  void setCurrentObjectives( Set<Objective> objectives );
  
  /**
   * Provides the controller with access to the known predictors (descriptors)
   * 
   * @param predictors - instances of predictor service descriptions
   */
  void setCurrentPSDs( Set<PredictorServiceDescription> psds );
  
  void setTreatmentRepository( ITreatmentRepository rep );
  
  /**
   * Notifies the controller of the PSD instance associated with an event
   * 
   * @param eventID - UUID of the event
   * @param psd     - PSD instance associated with the event
   */
  void addPSDEventMapping( UUID eventID, PredictorServiceDescription psd );
}