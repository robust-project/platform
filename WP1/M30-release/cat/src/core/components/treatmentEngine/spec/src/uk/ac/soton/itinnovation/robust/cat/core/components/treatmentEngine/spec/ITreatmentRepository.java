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

import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.*;

import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;




public interface ITreatmentRepository
{ 
  void resetRepository() throws Exception;
  
  UUID getID();
  
  String getName();
  
  Entry<UUID,String> getResourceStreamInfo( InputStream resource );
  
  TreatmentTemplate createTreatmentTemplate( UUID resourceID, 
                                             String resourceName,
                                             InputStream resource,
                                             String title,
                                             String description ) throws Exception;
  
  Set<TreatmentTemplate> getCopyofTreatmentTemplates();
  
  void deployTreatmentTemplates() throws Exception;
  
  void updateTreatmentTemplateData( Collection<TreatmentTemplate> templates );
  
  IProcessRender createTemplateRender( UUID templateID ) throws Exception;
  
  Treatment createTreatment( UUID templateID, UUID riskID, Float treatmentIndex ) throws Exception;
  
  void destroyTreatment( UUID treatmentID );
  
  void destroyAllTreatmentsForRisk( UUID riskID );
  
  void startTreatmentProcess( Treatment tmt ) throws Exception;
  
  void forceStopTreatmentProcess( Treatment tmt ) throws Exception;
  
  IProcessRender createTreatmentRender( UUID treatmentID ) throws Exception;
  
  Set<Treatment> getAllLiveTreatments();
  
  ROTreatmentGroup getTreatmentsForRO( String roTitle, 
                                       UUID riskID,
                                       List<String> historicProcessIDs );
  
  void notifyTreatmentTaskCompleted( TreatmentTask task );
  
  List<TreatmentRecord> getHistoricTreatmentRecords( List<String> procInstIDs );
}
