/*
 * Copyright 2012 WeST Institute - University of Koblenz-Landau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by       	: Felix Schwagereit 
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */
 
package eu.robust.simulation.dbconnection;


import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;


public class Connectdb {

	 Logger  logger = Logger.getLogger(Connectdb.class);
	
	
		Connection connection;	
	   // protected Statement stmt;


		public List<Map<String, String>> getTable(String[] columnNames, String query) {
		    List<Map<String, String>> table = new LinkedList<Map<String, String>>();
//		    Connectdb t = new Connectdb();
		    try {
		    	Statement stmt = connection.createStatement();
		    	ResultSet rs = stmt.executeQuery(query);
		    	while ( rs.next() ) {
		    		//logger.debug(rs);
		    		Map<String, String> row = new HashMap<String, String>();
		    		for(String col : columnNames) {
		    			row.put(col, rs.getString(col));
		    		}
		    		table.add(row);
		    	}
		  
		    } catch (SQLException e) {
		    	logger.error(e.getMessage());
		    		// TODO Auto-generated catch block
		    }	
		    return table;
		}
		

		
		
		public boolean executeUpdate(String updateQuery) {
			
			try {
				PreparedStatement stmt = connection.prepareStatement(updateQuery);
				int result = stmt.executeUpdate();
				
				return (result>0);
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
			
		}

		
		public boolean writeStringsInTable(String[] columnNames, String[][] dataRows, String tableName) {
			
	    	Statement stmt;
			try {
				stmt = connection.createStatement();


				for (int i = 0; i< dataRows.length; i++ ) {
					
					String prefix = "INSERT INTO "+tableName+" (";
					String suffix = " VALUES (";
					
					for (int j = 0; j<columnNames.length; j++) {
						
						prefix=prefix+columnNames[j];
						suffix=suffix+"'"+dataRows[i][j]+"'";
						
						if (j<columnNames.length-1) {
							prefix=prefix+",";
							suffix=suffix+",";
						}
						
					}
					
					prefix=prefix+")";
					suffix=suffix+")";
					
					String insertStatement = prefix+suffix;
					
					logger.info("Writing results to table: "+tableName+" -- "+insertStatement);
					
					stmt.addBatch(insertStatement);
					
				}
				
				@SuppressWarnings("unused")
				int[] res = stmt.executeBatch();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			
			
			return true;
		}
	    
	    
	/*void setResult(int id, int goodpost, int badposts, int goodposts_deleted, int badposts_deleted, int moderators ){
		
		try {
			 System.out.println("Insert into Result Tabelle: Beginne");
			 String sql="INSERT INTO Result (id,goodposts,badposts,goodposts_deleted,badposts_deleted,moderators) VALUES (?,?,?,?,?,?)";
			 PreparedStatement ps = connection.prepareStatement(sql);
			 ps.setInt(1, id);
			 ps.setInt(2, goodpost);
		     ps.setInt(3, badposts);
		     ps.setInt(4, goodposts_deleted);
		     ps.setInt(5, badposts_deleted);
		     ps.setInt(6, moderators);
		     
		     ps.executeUpdate();	
		     System.out.println("Insert into Result Tabelle: Abgeschlossen");
		}catch (Exception exc){System.out.println(exc);}    
	}*/

	public void disconnectdb(){
		try {
	    	// Wenn ein Fehler auftritt, Fehler ausgeben und versuchen die Datenbank-Verbindung zu schliessen.
	    	connection.commit();
			connection.close();
	        logger.info("Database connection successfully closed.");
	    } catch (SQLException sqlexc) {
	    	logger.info("Could not close database connection.");
	    } catch (NullPointerException nulexc) {
	    	logger.info("There is no open database connecton to be closed.");
	    }
	}

	public boolean isConnected() {
		return (connection != null);
	}
	

	public Connectdb() {
		this(new DBParameters("simManagementKoblenz"));
		    //this("jdbc:oracle:thin:@//robustdb.west.uni-koblenz.de:1521/orcl", "SIM_PLATTFORM", "");

	}
	
	public Connectdb(DBParameters parameters) {
		
		this(parameters.connectionsString, parameters.user, parameters.password);
		
	}


	public Connectdb(String constring, String user, String pw) {
		
		
		try {
			   Class.forName("org.postgresql.Driver");
			   Class.forName( "oracle.jdbc.driver.OracleDriver");
			    //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			  } catch (ClassNotFoundException cnfe) {
				logger.error("Couldn't find the database drivers!\n"+cnfe.getMessage());
			    cnfe.printStackTrace();
			  }
		  
		connection = null;
		  
		try {
				Properties props = new Properties();
				props.put("user", user);
				props.put("password", pw);
				//props.put("oracle.jdbc.TcpNoDelay", "FALSE");
				//props.put("tcp.nodelay", "FALSE");
				connection = DriverManager.getConnection(constring, props);
			    // connection = DriverManager.getConnection(constring, user, pw);

			  } catch (SQLException se) {
				logger.error("Couldn't connect to database: "+constring+"\n"+ se.getMessage());
			    se.printStackTrace();
			  }
			  
		if (connection != null) {
			logger.info("Successfully conected to database: "+constring+" with user: "+user+ " and pw: "+pw);
		}
		
		}
	}

