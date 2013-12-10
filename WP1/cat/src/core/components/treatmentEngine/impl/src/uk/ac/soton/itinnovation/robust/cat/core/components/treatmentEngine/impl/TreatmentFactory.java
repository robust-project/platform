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
//      Created Date :          14-May-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.impl;

import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.*;

import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.spec.*;
import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.providers.LocalMySQLSource;

import java.util.*;

import org.activiti.engine.ProcessEngine;




public class TreatmentFactory implements ITreatmentFactory
{
  private HashMap<UUID, ITreatmentWorkflowProvider> treatmentWorkflowProviders;
  private static TreatmentRepository treatmentRepo;
  
  public TreatmentFactory()
  {}
  
  // ITreatmentFactory ---------------------------------------------------------
  @Override
  public void discoverProviders()
  {
    enumerateProviders();
  }
  
  @Override
  public void releaseProviders()
  {
    if ( treatmentWorkflowProviders != null )
    {
      Collection<ITreatmentWorkflowProvider> providers = treatmentWorkflowProviders.values();
      Iterator<ITreatmentWorkflowProvider> provideIt = providers.iterator();
      
      while ( provideIt.hasNext() )
        provideIt.next().cleanUpProvider();
    } 
  }
  
  @Override
  public HashMap<UUID, String> getKnownWorkflowProviderIDs()
  {
    HashMap<UUID, String> repos = new HashMap<UUID, String>();
    
    for ( ITreatmentWorkflowProvider provider : treatmentWorkflowProviders.values() )
      repos.put( provider.getTypeID(), provider.getName() );
    
    return repos;
  }
  
  @Override
  public ITreatmentWorkflowProvider getTreatmentWorkflowProvider( UUID tmtRepoID ) throws Exception
  {
    if ( treatmentWorkflowProviders == null ) throw new Exception( "Need to discover providers first" );
    
    return treatmentWorkflowProviders.get( tmtRepoID );
  }
  
  @Override
  public synchronized ITreatmentRepository createTreatmentRepository( UUID id,
                                                         String name,
                                                         IWorkflowEngineWrapper wfew )
  {
    if ( wfew != null )
    {
      if (treatmentRepo != null)
          return treatmentRepo;
      
      ProcessEngine pe = (ProcessEngine) wfew.getImpl();
      
      // Create new singleton instance of TreatmentRepository
      TreatmentRepository te = new TreatmentRepository( id, name, pe );
      treatmentRepo = te;
      
      return te;
    }
    
    return null;
  }
  
  // Private methods -----------------------------------------------------------
  private void enumerateProviders()
  {
    treatmentWorkflowProviders = new HashMap<UUID, ITreatmentWorkflowProvider>();
    
    // Not a lot to look for right now... just the local MySQL source
    LocalMySQLSource lmss = new LocalMySQLSource();
    ITreatmentWorkflowProvider twfp = lmss.getProvider();
    
    treatmentWorkflowProviders.put( twfp.getTypeID(), twfp );
  }
}
