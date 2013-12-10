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

package eu.robust.simulation.configuration;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.robust.simulation.dbconnection.Connectdb;
import eu.robust.simulation.dbconnection.DBParameters;

public class ParametersetDBManager {

	Logger  logger = Logger.getLogger(ParametersetDBManager.class);
	
	
	private DBParameters dbparameters;
	private String[] columnNames = {"id","creationdate","description"};
	private String[] columnNamesPar = {"parameterset","parameter","value"};
	
	
	public ParametersetDBManager(DBParameters parameters) {
		
		this.dbparameters = parameters;
		
	}
	
	public Parameterset getParameterset(String id) {

		Parameterset parSet = null;
		Connectdb db = new Connectdb(dbparameters);
		
		String query = "SELECT * FROM parametersets WHERE id='"+id+"'";
		logger.debug("Retrieving parameterset: "+ query);
		List<Map<String,String>> results = db.getTable(columnNames, query);
		
		if (results.size()>1) {
			logger.error("More than one parameterset in db with id: "+id);
			return null;
		}
		
		
		if (results.size()==1) {
		
			parSet = new Parameterset();
			Map<String, String> entry = results.get(0);
			
			parSet.id 			= entry.get("id");
			parSet.creationdate = entry.get("creationdate");
			parSet.description	= entry.get("description");
			
			String queryPar = "SELECT * FROM parametervalues WHERE parameterset='"+id+"'";
			logger.debug("Retrieving parameterset values: "+ query);
			List<Map<String,String>> resultsPar = db.getTable(columnNamesPar, queryPar);
			
			for (Map<String, String> entryPar : resultsPar) {
				String parameter = entryPar.get("parameter");
				String value = entryPar.get("value");
				parSet.parameters.put(parameter, value);
			}
		
		} else {
			logger.debug("No parametersets with id: "+ id);
		}
		
		db.disconnectdb();
		return parSet;
	}
	
	public boolean storeParameterset(Parameterset pset) {
		
		boolean result = false;
		Connectdb db = new Connectdb(dbparameters);
		
		// check if dataset already exists
		String query = "SELECT count(*) num FROM parametersets WHERE id='"+pset.id+"'";
		String[] colnum = {"num"};
		List<Map<String,String>> results = db.getTable(colnum, query);
		String resString = (String) results.get(0).get("num");
		int occurences = Integer.parseInt(resString);
		
		if (occurences>0) {
			logger.error("Parameterset already exists with id: "+pset.id);
		} else {
			// create parameterset
			String[][] data = new String[1][3];
			data[0][0] = pset.id;
			data[0][1] = pset.creationdate;
			data[0][2] = pset.description;
			logger.debug("Creating parameterset in database with id :"+pset.id);
			result = db.writeStringsInTable(columnNames, data, "parametersets");
			
			// create parameter values
			String[][] pars = new String[pset.parameters.size()][3];
			int i=0;
			for (String parameter: pset.parameters.keySet()) {
				pars[i][0] = pset.id;
				pars[i][1] = parameter;
				pars[i][2] = pset.parameters.get(parameter);
				i++;
			}
			logger.debug("Creating parameterset values in database with id :"+pset.id);
			result &= db.writeStringsInTable(columnNamesPar, pars, "parametervalues");
		}
		
		db.disconnectdb();
		return result;

	}
	
	
	
	
}
