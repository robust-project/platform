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
//      Created Date :          09-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.engine;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.ISimulationController;
import java.io.*;
import java.util.Properties;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.catsim.CATSimController;
import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.koblenz.KoblenzForumRestrictionController;


public class CATSimIntegrationEngine
{
  private static final Logger simEngLogger = Logger.getLogger( CATSimIntegrationEngine.class );
  
  public enum eIntegratedSimulation { eKoblenzForumRestriction, CATSim };
  
  
  public static ISimulationController getSimulation( IUFView parent, eIntegratedSimulation sim, String currPlatformID, String currCommunityID )
  {
    ISimulationController ctrl = null;
    
    switch ( sim )
    {
      case eKoblenzForumRestriction :
      {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream propsStream = cl.getResourceAsStream( "koblenzSim.properties" );
        
        if ( propsStream != null )
        {
          try
          {
            Properties props = new Properties();
            props.load( propsStream );
            
            ctrl = (ISimulationController) new KoblenzForumRestrictionController( parent );
            ctrl.initialise( props, currPlatformID, currCommunityID );
          }
          catch (IOException ioe)
          {
              simEngLogger.error( "Simulation Engine could not create Koblenz Forum Restriction simulation" );
          }
        }
        
      } break;
          
      case CATSim :
      {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream propsStream = cl.getResourceAsStream( "catSim.properties" );
        
        if ( propsStream != null )
        {
          try
          {
            Properties props = new Properties();
            props.load( propsStream );
            
            ctrl = (ISimulationController) new CATSimController( parent );
            ctrl.initialise( props, currPlatformID, currCommunityID );
          }
          catch (IOException ioe)
          {
              simEngLogger.error( "Simulation Engine could not create CATSim simulation" );
          }
        }
        
      } break;
    }
    
    return ctrl;
  }
}
