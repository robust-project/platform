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
//      Created for Project :   robust-cat-core-dataProviders-treatmentPersistence-impl
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.impl.localMySQL;

import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.spec.*;

import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.impl.sqlUtils.SQLConfigUtil;

import org.activiti.engine.*;

import java.util.*;




public class LocalMySQLProvider implements ITreatmentWorkflowProvider
{
  private static final UUID id = UUID.fromString( "00610445-1bdf-4ecc-9d91-14bdbce7000a" );
  private static final String name = "Local MySQL treatment provider";
  
  private Properties providerConfigProperties;
  
  public LocalMySQLProvider()
  {}
  
  // ITreatmentWorkflowProvider ------------------------------------------------
  @Override
  public UUID getTypeID()
  { return id; }
  
  @Override
  public String getName()
  { return name; }
  
  @Override
  public void intialiseProvider( Properties providerConfig,
                                 ITreatmentWorkflowProviderListener listener ) throws Exception
  {
    // Need to ensure resource target is valid
    if ( providerConfig == null ) throw new Exception( "Provider configuration properties are NULL" );
    providerConfigProperties = providerConfig;
    
    // Need a good listener to run provider
    if ( listener == null ) throw new Exception( "TreatmentProviderListener is NULL" );
    
    // Try connecting to the local MySQL database
    SQLConfigUtil util = new SQLConfigUtil( providerConfigProperties );
    
    // Try connecting and report result
    if ( util.isDBConnectionOK() )
      listener.onProviderInitialiseResult( ITreatmentWorkflowProviderListener.eInitResult.eProviderInitialisedOK );
    else
      listener.onProviderInitialiseResult( ITreatmentWorkflowProviderListener.eInitResult.eProviderDBConnectionFailed );
  }
  
  @Override
  public void requestProcessEngine( ITreatmentWorkflowProviderListener listener ) throws Exception
  {
    // Need to ensure resource target is valid
    if ( providerConfigProperties == null ) throw new Exception( "Provider configuration properties are NULL" );
    
    // Need a good listener to run provider
    if ( listener == null ) throw new Exception( "TreatmentProviderListener is NULL" );
    
    // Try creating a process engine
    ITreatmentWorkflowProviderListener.eEngineResult result = 
            ITreatmentWorkflowProviderListener.eEngineResult.eActivitiProcessEngineCreatedOK;
    
    ProcessEngine processEngine = ProcessEngineConfiguration
      .createProcessEngineConfigurationFromResourceDefault()
      .setJdbcUsername( providerConfigProperties.getProperty("jdbcUsername") )
      .setJdbcPassword( providerConfigProperties.getProperty("jdbcPassword") )
      .setDatabaseSchemaUpdate( "true" )
      .buildProcessEngine();
    
    if ( processEngine == null )
      result = ITreatmentWorkflowProviderListener.eEngineResult.eActivitiProcessEngineBuildFailed;
    
    // Return the (wrapped) result
    listener.onProviderProcessEngineResult( new LocalMySQLProcessEngine( processEngine ), 
                                            result );
  }
  
  @Override
  public void cleanUpProvider()
  {
    // TODO
  }
}
