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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Simulationrun {
	
	public String id;
	public String model;
	public String parameterset;
	public String creationdate;
	public String status;
	public String startdate;
	public String finishdate;
	
	/**
	 * @return timestamp string of the format yyyy-MM-dd-HH-mm-ss
	 */
	public static String getTimestamp() {
		
    	Date dateNow = new Date();
    	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    	StringBuilder dateString = new StringBuilder(dateformat.format(dateNow));
    	return dateString.toString();
	}
	
	/**
	 * @return string of the current computer name .
	 */
	public static String getComputername() {
		String computername = "";
		try {
			computername = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return computername;
	}
	
	/**
	 * @return ID string of the format yyyy-MM-dd-HH-mm-ss-computername .
	 */
	public static String createID() {
		return getTimestamp()+"-"+getComputername();
	}
	
	@SuppressWarnings("static-access")
	public Simulationrun() {
		id = this.getTimestamp()+"-"+this.getComputername();
		creationdate = getTimestamp();
		this.status = "created";
	}

}
