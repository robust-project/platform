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

import org.apache.log4j.Logger;

public class DBParameters {
	
	private Logger  logger = Logger.getLogger(DBParameters.class);
	
	public String connectionsString;
	public String user;
	public String password;
	
	public DBParameters(String connectionsString, String user, String password) {
		this.connectionsString = connectionsString;
		this.user = user;
		this.password = password;
		
		System.out.println(this.connectionsString);
	}
	
	public DBParameters() {
	
		super();
	}
	
	/**
	 * @param defaultParametersSetName can produce a standard set of parameters
	 * possible values are: simManagementKoblenz, ibmDataKoblenz, sapDataKoblenz, localFelix
	 * default is localFelix
	 */
	public DBParameters(String defaultParametersSetName) {
		
		super();
		
		String standardpw = "none";
		String robustdbconstring = "jdbc:oracle:thin:@//robustdb:1521/orcl";
		
		if (defaultParametersSetName == "simManagementKoblenz") {
			this.connectionsString = robustdbconstring;
			
			this.user = "results";
			this.password = standardpw;
		} else if (defaultParametersSetName == "ibmDataKoblenz") {
			this.connectionsString = robustdbconstring;
			this.user = "ibm_daten";
			this.password = standardpw;
		} else if (defaultParametersSetName == "boardsieDataKoblenz") {
			this.connectionsString = robustdbconstring;
			this.user = "boardsie_daten";
			this.password = standardpw;
		} else if (defaultParametersSetName == "sapDataKoblenz") {
			this.connectionsString = robustdbconstring;
			this.user = "sap_daten";
			this.password = standardpw;
		} else if (defaultParametersSetName == "localFelix") {
			this.connectionsString = "jdbc:postgresql://localhost/postgres";
			this.user = "postgres";
			this.password = "testdb";
		} else if (defaultParametersSetName == "resultsOLD") {
			this.connectionsString = robustdbconstring;
			this.user = "results";
			this.password = standardpw;
		} else {
			logger.debug("No standard parameter set named '"+defaultParametersSetName+"' found! Using 'localFelix'...");
			this.connectionsString = "jdbc:postgresql://localhost/postgres";
			this.user = "postgres";
			this.password = "testdb";
		}
		
		
	}

}
