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
//      Created By :            Vegard Engen, Mariusz Jacyno
//      Created Date :          2011-06-20
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;

import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import javax.management.RuntimeErrorException;

/**
 * 
 * @author ve
 */
public class DatabaseConnector
{
    static Logger logger = Logger.getLogger(DatabaseConnector.class);
    
    // connection reference to the db
    private Connection connection = null;
    
    // db connection details
    private String dbURL = null;
    private String dbName = null;
    private String userName = null;
    private String password = null;
    private String dbType = "mysql";
    private String driver="com.mysql.jdbc.Driver";
    
    
   

    public DatabaseConnector()
    {

    }

    public DatabaseConnector(String dbURL, String dbName, String userName, String password) throws Exception
    {
        this.dbURL = dbURL;
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;
            
    }

  

    public void connect() throws Exception
    {
        if ((dbURL != null) && (dbName != null) && (userName != null) && (password != null) && (dbType != null)) {
            connect(dbURL, dbName, userName, password, dbType);
        } else {
            logger.error("Cannot connect to the database, because connection details have not been given. Try the overloaded connect method, or use the overloaded constructor when creating this DatabaseConnector object!");
            throw new RuntimeException("Cannot connect to the database, because connection details have not been given. Try the overloaded connect method, or use the overloaded constructor when creating this DatabaseConnector object!");
        }
    }

    public void ConnectToServer() throws Exception{
       connect(dbURL, null, userName, password, dbType);
    }
    
    public void connect(String dbURL, String dbName, String userName, String password, String type) throws Exception
    {
        //logger.info("Attempting to connect to the database: " + dbName);
        try {
         
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error("Did not find the JDBC Driver for " + type, e);
            e.printStackTrace();
            throw e;
        }

//        System.out.println(type + " JDBC Driver Registered!");
//        logger.debug(type + " JDBC Driver Registered!");

        //connect to db
        try { // TODO: if Derby, then we may need to add something like ';create=true'
//            connection = DriverManager.getConnection(

           Properties props= new Properties();
           props.setProperty("user", userName);
           props.setProperty("password", password);
           props.setProperty("allowMultiQueries", "true"); //this will allow running scripts from files
           connection = DriverManager.getConnection("jdbc:" + type + "://" + dbURL + "/" + (dbName!=null?dbName:""), props);
            
        } catch (SQLException e) {
            logger.error("Failed to connect to the database", e);
            e.printStackTrace();
            return;
        }

        if (connection == null) {
            logger.error("Failed to connect to the database: " + dbName);
            throw new RuntimeException("Failed to connect to the database: " + dbName);
        }

        // saving the connection details for future connections, so that the 'connect()' method can be used
        this.dbURL = dbURL;
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;
        this.dbType = type;
    }

    public void close()
    {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.debug("SQLException caught when trying to close the DB connection", ex);
            }
        }
    }

    public boolean isValid(){
        if(connection!=null){
            try{
              return connection.isValid(0);
            }catch(Exception ex){
                return false;
            }
        }
        
        return false;
    }
    
    DatabaseConnector cloneCon(){
        DatabaseConnector con=new DatabaseConnector();
        con.connection=this.connection;
        con.dbName=this.dbName;
        con.dbType=this.dbType;
        con.dbURL=this.dbURL;
        con.driver=this.driver;
        con.password=this.password;
        con.userName=this.userName;
        
        return con;
    }
    /**
     * this method returns the results form selection or the generatedKeys from insert/delete/update
     **/
    public ResultSet execute(String query, int returnGeneratedKeys) throws Exception {
        ResultSet rs = null;

        try {

            if (connection == null) {
                throw new RuntimeException("Cannot execute the query because no connection has been made");
            }
            Statement s = connection.createStatement();
            //int (returnGeneratedKeys==true?Statement.RETURN_GENERATED_KEYS:Statement.NO_GENERATED_KEYS);
            if (s.execute(query, returnGeneratedKeys) == true) {
                rs = s.getResultSet();
            } else {
                rs = s.getGeneratedKeys();
            }

        } catch (Exception ex) {
            throw new RuntimeException("DatabaseConnector: error while executing query. " + ex.getMessage());
        } 

        return rs;
    }
    
    public ResultSet executeQuery(String query) throws Exception
    {
        return execute(query, Statement.NO_GENERATED_KEYS);
    }

    @Override
    public void finalize()
    {
        try {
            close();
            super.finalize();
        } catch (Throwable t) {
            logger.debug("Error caught when trying to close the DB connection", t);
        }
    }
    
    //bmn

    public Connection getConnection() {
        return connection;
    }

  
    
}
