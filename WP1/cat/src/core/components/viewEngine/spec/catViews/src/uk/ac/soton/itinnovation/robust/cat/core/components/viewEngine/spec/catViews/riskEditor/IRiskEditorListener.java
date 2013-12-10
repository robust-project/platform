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
//      Created By :            Simon Crowle
//      Created Date :          04 Nov 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.riskEditor;


import uk.ac.soton.itinnovation.robust.riskmodel.Risk;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;


public interface IRiskEditorListener
{
  /**
   * The risk editor requires updated risk data to be supplied for the user
   */
  void onRiskEditorWantsFreshRiskData();
  
  /**
   * Event indicating that user wishes to create a new risk with the
   * basic information provided
   * 
   * @param title - Name of the risk or opportunity
   * @param risk  - true if described as a risk, false if an opportunity
   * @param owner - String identifier of risk owner (TO DO: unique ID)
   * @param group - String identifier of group (TO DO: this may change to other classifier method)
   */
  void onCreateNewRO( String title, boolean risk, String owner, String group );
  
  /**
   * Event indicating that Risk information has been updated via the CAT UI
   * 
   * @param risk - risk instance with updated data
   */
  void onUpdateRisk( Risk risk );
  
  /**
   * Event indicating that the user wishes to delete the risk instance
   * 
   * @param risk - risk instance to delete
   */
  void onDeleteRisk( Risk risk );
  
  /**
   * The Risk Editor needs to get the PredictorServiceDescription instance
   * for the associated event. Use IRiskEditorController.addPSDEventMapping(..)
   * to do this.
   * 
   * @param event - the instance of the Event that requires a PSD mapping
   */
  void onGetPSD( Event event );
}