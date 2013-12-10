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
//      Created Date :          19-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.webapp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.sql.*;
import java.util.Enumeration;




public class CATServletContextListener implements ServletContextListener
{
  @Override
  public void contextInitialized(ServletContextEvent sce)
  {
    // Context initialisation behaviour here
    
    
    System.out.println( "CAT servlet context initialsed.");
  }
  
  @Override
  public void contextDestroyed(ServletContextEvent sce)
  {
    System.out.print( "CAT servlet context destroyed clean up started..." );
    boolean success = false;
    
    // Clean up database drivers
    try
    {
      Enumeration<Driver> driverEnums = DriverManager.getDrivers();
      while ( driverEnums.hasMoreElements() )
        DriverManager.deregisterDriver( driverEnums.nextElement() );
      
      success = true;
    }
    catch (SQLException sqle ) {}
    
    if ( success ) System.out.print( " successfully completed.\n" );
    else System.out.print(" unsuccessfully completed.\n");
  }
}
