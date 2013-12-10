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
//      Created Date :          14 Nov 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents;


public interface RiskEditCommitListener
{
  void onDataChanged();

  void onCommitCreateViewChanges( CreateViewCD changeData );

  void onCommitBrowseViewChanges( BrowseViewCD changeData );
  
  void onCommitEventPredictorChanges( PredictorViewCD changeData );
  
  void onCommitImpactChanges( ImpactViewCD changeData );
  
  void onCommitScheduleChanges( ScheduleViewCD changeData );
  
  void onCommitTreatmentChanges( TreatmentViewCD changeData );
  
  void onCommitAllChanges();
  
  void onDiscardAllChanges();
}