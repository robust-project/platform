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
//      Created Date :          15 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews;


import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.IDashboardController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.riskEditor.IRiskEditorController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.simulation.ISimulationCentreController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.welcome.IWelcomeController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.treatment.ITreatmentCentreController;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;



/**
 * ICATAppViewController is a super-controller type that does not directly provide
 * access to views, but to further controllers that exercise view access/control.
 * 
 * @author Simon Crowle
 */
public interface ICATAppViewController
{
  void addViewResource( String ID, String resPath );
  
  void removeViewResource( String ID );
  
  IUFView getCurrentView();
  
  IWelcomeController getWelcomeViewController();
  
  IDashboardController getDashboardViewController();
  
  IRiskEditorController getRiskEditorViewController();
  
  ISimulationCentreController getSimulationCentreController();
  
  ITreatmentCentreController getTreatmentCentreController();

  void setCommunity(Community currCommunity);

}