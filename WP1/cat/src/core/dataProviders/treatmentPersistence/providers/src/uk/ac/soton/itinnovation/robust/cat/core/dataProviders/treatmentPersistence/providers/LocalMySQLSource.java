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

package uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.providers;

import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.spec.ITreatmentWorkflowProvider;
import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.impl.localMySQL.LocalMySQLProvider;
import uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.impl.sqlUtils.SQLConfigUtil;

import java.util.Properties;





public class LocalMySQLSource
{
  private static String targetUser = "catActiviti";
  private static String targetPassword = "password";
  
  public LocalMySQLSource()
  {}
  
  public ITreatmentWorkflowProvider getProvider()
  {    
    LocalMySQLProvider lmsp = new LocalMySQLProvider();
    return lmsp;
  }
  
  /**
   * Use this run-time entry point to ready your MySQL database for an ACTIVITI
   * database and its usage.
   * 
   * @param args [0] username (admin)
   *             [1] password
   */
  public static void main( String[] args )
  {
    Properties sqlProps = createProps( args );
    if ( sqlProps == null ) return;
    String result = "FAILED";
    
    System.out.print( "Attempting to log into MySQL source..." );
    
    SQLConfigUtil util = new SQLConfigUtil( sqlProps );
    if ( util.connect() )
    {
      System.out.print( " connected OK.\n" );
      
      // Set up Activiti User
      if ( setUpActivitiUser( util ) );
        if ( setActivitDatabase(util) )
          result = "SUCCEEDED";

    }
    else System.out.println( "Could not connect to local MySQL server." );
    
    System.out.println("Finished: " + result );
    
  }
  
  // Private (static) methods --------------------------------------------------
  private static Properties createProps( String[] args )
  {
    // Safety first
    if ( args == null )
    {
      System.out.println( "No arguments supplied." );
      return null;
    }
    
    if ( args.length != 2 )
    {
      System.out.println( "Need arguments: (admin) username, password" );
      return null;
    }
    
    Properties props = new Properties();
    
    props.put( "jdbcDriver", "com.mysql.jdbc.Driver" );
    props.put( "jdbcUrl", "jdbc:mysql://localhost:3306" );
    props.put( "jdbcUsername", args[0] );
    props.put( "jdbcPassword", args[1] );
    
    return props;
  }
  
  private static boolean setUpActivitiUser( SQLConfigUtil util )
  {
    System.out.print( "Creating Activiti User..." );
    
    // If user exists, then just exist
    String userListQuery = "select User from mysql.user";
    
    
    if ( util.executeSimpleQueryAndResult( userListQuery, targetUser ) )
    {
      System.out.print( " user already exists.\n" );
      return true;
    }
    
    // If not, try creating the user
    String createQuery = "create user '" + targetUser + "'@'localhost' " +
                         "IDENTIFIED BY '"+ targetPassword + "';";
    
    if ( util.executeSimpleUpdateAndResult( createQuery, "Updated OK" ) )
    {
      // Just check we've definitely got the user
      if ( util.executeSimpleQueryAndResult( userListQuery, targetUser ) )
      {
        System.out.print( " created OK.\n" );
        return true;
      }
    }
    
    System.out.print( " failed to create Activiti user.\n" );
    return false;
  }
  
  private static boolean setActivitDatabase( SQLConfigUtil util )
  {
    System.out.print( "Creating Activiti Database..." );
    
    // Create database if it does not exist
    if ( !util.executeSimpleQueryAndResult( "show databases;", "activiti") )
    {
      if ( !util.executeSimpleUpdateAndResult( "create database activiti", "Updated OK") )
      {
        System.out.print(" failed to create Activiti database.\n" );
        return false;
      }
      
      // Check that database really does exist
      if ( !util.executeSimpleQueryAndResult( "show databases;", "activiti") )
      {
        System.out.print(" failed to create Activiti database.\n" );
        return false;
      }
      else System.out.print( " created database OK.\n" );
    }
    else System.out.print( " database already exists.\n" );
    
    // Finally, make sure proper privileges are assigned to the activiti user
    System.out.print( "Assigning user privileges... " );
    
    if ( util.executeSimpleUpdateAndResult( "grant all privileges on activiti.* to " +
                                            targetUser + "@localhost;", 
                                            "Updated OK" ) )
    {
      System.out.print( " assignment OK.\n" );
      return true;
    }
    else
      System.out.print( " could not assign user privileges to database.\n" );
    
    return false;
  }
}
