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

package uk.ac.soton.itinnovation.robust.cat.core.dataProviders.treatmentPersistence.impl.sqlUtils;

import java.util.*;
import java.io.*;
import java.sql.*;




public class SQLConfigUtil
{
  private Properties dbConnectionProps;
  private Connection mySQLConnection;
  
  public SQLConfigUtil( String connectPropsResource )
  {
    dbConnectionProps = new Properties();
    
    try
    { dbConnectionProps.load( new FileInputStream(connectPropsResource) ); }
    catch (IOException ioe ) { dbConnectionProps = null; }
  }
  
  public SQLConfigUtil( Properties connectProps )
  {
    dbConnectionProps = new Properties();
    
    if ( connectProps != null )
      dbConnectionProps.putAll( connectProps );
  }
  
  public boolean isDBConnectionOK()
  {
    if ( connectDB() == false ) return false;
    disconnectDB();    
    
    return true;
  }
  
  public boolean connect()
  { return connectDB(); }
  
  public boolean disconnect()
  { return disconnectDB(); }
  
  /**
   * Executes a simple SQL statement and returns the result (of column 1) as a
   * list of strings (applicable for simple queries).
   * 
   * @param statement  - SQL statement to execute
   * @param asQuery    - execute the statement as a query
   * @return           - List<String> of results (successful updates return 'UPDATED OK')
   * @throws Exception - Parameter or update execution exceptions
   */
  public List<String> executeSimpleStatement( String statement, boolean asQuery ) throws Exception
  {
    // Safety first
    if ( statement == null )
      throw new Exception( "Query is null" );
    
    if ( mySQLConnection == null )
      throw new Exception( "No database connection" );
    
    // Create statement and result container
    Statement sqlStatement = mySQLConnection.createStatement();
    ArrayList<String> resultList = new ArrayList<String>();
   
    // Perform query
    if ( asQuery )
    {
      ResultSet resultSet = sqlStatement.executeQuery( statement );
      
      if ( resultSet != null )
        while ( resultSet.next() )
          { resultList.add( resultSet.getString(1) ); }
    }
    else // or perform update
      try
      {
        sqlStatement.execute( statement );
        resultList.add( "Updated OK" );
      }
      catch (SQLException sqle ) { throw sqle; }

    return resultList;
  }
  
  public boolean executeSimpleQueryAndResult( String query, String searchTarget )
  {
    boolean searchResult = false;
    
    try
    { 
      List<String> results = executeSimpleStatement( query, true );
      
      if ( results != null )
        searchResult = searchResults( results, searchTarget );
    }
    catch (Exception e) { searchResult = false; }
   
    return searchResult;
  }
  
  public boolean executeSimpleUpdateAndResult( String update, String searchTarget )
  {
    boolean searchResult = false;
    
    try
    { 
      List<String> results = executeSimpleStatement( update, false );
      
      if ( results != null )
        searchResult = searchResults( results, searchTarget );
    }
    catch (Exception e) { searchResult = false; }
   
    return searchResult;
  }
  
  // Private methods -----------------------------------------------------------
  private boolean connectDB()
  {
    boolean connectOK = true;
    
    // Close previous connection if required
    if ( mySQLConnection != null ) disconnectDB();
    
    // Try connection
    if ( connectOK )
    {
      try
      {
        // Need to instantiate class (in case we're not using local DB elsewhere)
        Class.forName( "com.mysql.jdbc.Driver" );
        
        mySQLConnection = DriverManager.getConnection( dbConnectionProps.getProperty( "jdbcUrl" ),
                                                       dbConnectionProps.getProperty( "jdbcUsername" ),
                                                       dbConnectionProps.getProperty( "jdbcPassword" ) );
      }
      catch( Exception e )
      {
        System.out.println( "---------------MYSQL CONNECTION PROBLEM" );
        System.out.println( "Could not connect to: " );
        System.out.println( dbConnectionProps.toString() );
        System.out.println( "Because: " + e.getMessage() );
      }       
    }
    
    return connectOK;
  }
  
  private boolean disconnectDB()
  {
    boolean disconnectedOK = true;
    
    try { mySQLConnection.close(); }
    catch( SQLException sqle ) { disconnectedOK = false; }
    
    mySQLConnection = null;
    
    return disconnectedOK;
  }
  
  private boolean searchResults( List<String> results, String target )
  {
    boolean searchResult = false;
    
    Iterator<String> resIt = results.iterator();
    
    while ( resIt.hasNext() )
    {
      String result = resIt.next();
      if ( result.contains(target) )
      {
        searchResult = true;
        break;
      }
    }
    
    return searchResult;
  }
}
