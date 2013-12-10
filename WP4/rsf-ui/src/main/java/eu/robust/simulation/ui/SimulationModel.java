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
package eu.robust.simulation.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class SimulationModel {

	private String path;

	private String simulationName;

	private boolean useCurrentTime;

	private String startYear;

	private String startMonth;

	private String startDay;

	private String startHour;

	private String endYear;

	private String endMonth;

	private String endDay;

	private String endHour;

	private int numberOfAgents;

	private String communityModel;

	private String community;

	private String subCommunity;

	private Map<String, String> additionalProperties;

	public SimulationModel() {

		path = new String();
		simulationName = new String();
		useCurrentTime = false;
		startYear = new String();
		startMonth = new String();
		startDay = new String();
		endHour = new String();
		endYear = new String();
		endMonth = new String();
		endDay = new String();
		endHour = new String();
		numberOfAgents = 0;
		communityModel = new String();
		community = new String();
		subCommunity = new String();
		additionalProperties = new HashMap<String, String>();

	}

	public SimulationModel(SimulationModel model) {

		this.path = model.getPath();
		this.simulationName = model.getSimulationName();
		this.useCurrentTime = model.isUseCurrentTime();
		this.startYear = model.getStartYear();
		this.startMonth = model.getStartMonth();
		this.startDay = model.getStartDay();
		this.startHour = model.getStartHour();
		this.endYear = model.getEndYear();
		this.endMonth = model.getEndMonth();
		this.endDay = model.getEndDay();
		this.endHour = model.getEndHour();
		this.numberOfAgents = model.getNumberOfAgents();
		this.communityModel = model.getCommunityModel();
		this.community = model.getCommunity();
		this.subCommunity = model.getSubCommunity();
		this.additionalProperties = model.getAdditionalProperties();

	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSimulationName() {
		return simulationName;
	}

	public void setSimulationName(String simulationName) {
		this.simulationName = simulationName;
	}

	public boolean isUseCurrentTime() {
		return useCurrentTime;
	}

	public void setUseCurrentTime(boolean useCurrentTime) {
		this.useCurrentTime = useCurrentTime;
	}

	public String getStartHour() {
		return startHour;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	public String getEndHour() {
		return endHour;
	}

	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getStartDay() {
		return startDay;
	}

	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getEndDay() {
		return endDay;
	}

	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}

	public int getNumberOfAgents() {
		return numberOfAgents;
	}

	public void setNumberOfAgents(int numberOfAgents) {
		this.numberOfAgents = numberOfAgents;
	}

	public String getCommunityModel() {
		return communityModel;
	}

	public void setCommunityModel(String communityModel) {
		this.communityModel = communityModel;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getSubCommunity() {
		return subCommunity;
	}

	public void setSubCommunity(String subCommunity) {
		this.subCommunity = subCommunity;
	}

	public Map<String, String> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, String> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public boolean readFromFile() {

		if (this.path.isEmpty()) {
			return false;
		}

		Input input = new Input();
		Map<String, String> data = input.inputFile(this.path);

		// Template name
		if (data.containsKey("templateName") == true) {
			this.simulationName = data.get("templateName");
			data.remove("templateName");
		}

		// Current time = Start time?
		if (data.containsKey("useCurrentTimeAsStartDate") == true) {
			if (data.get("useCurrentTimeAsStartDate").equals("true")) {
				this.useCurrentTime = true;
			} else {
				this.useCurrentTime = false;
			}
			data.remove("useCurrentTimeAsStartDate");
		}

		// Start date
		if (data.containsKey("templateName") == true) {
			this.simulationName = data.get("templateName");
			data.remove("templateName");
		}

		// Start Hour
		if (data.containsKey("startHour") == true) {
			this.startHour = data.get("startHour");
			data.remove("startHour");
		}
		
		// Start Year
		if (data.containsKey("startYear") == true) {
			this.startYear = data.get("startYear");
			data.remove("startYear");
		}

		// Start Month
		if (data.containsKey("startMonth") == true) {
			this.startMonth = data.get("startMonth");
			data.remove("startMonth");
		}

		// Start Day
		if (data.containsKey("startDay") == true) {
			this.startDay = data.get("startDay");
			data.remove("startDay");
		}

		// End Hour
		if (data.containsKey("endHour") == true) {
			this.endHour = data.get("endHour");
			data.remove("endHour");
		}
		
		// End Year
		if (data.containsKey("endYear") == true) {
			this.endYear = data.get("endYear");
			data.remove("endYear");
		}

		// End Month
		if (data.containsKey("endMonth") == true) {
			this.endMonth = data.get("endMonth");
			data.remove("endMonth");
		}

		// End Day
		if (data.containsKey("endDay") == true) {
			this.endDay = data.get("endDay");
			data.remove("endDay");
		}


		// Number of forums
		if (data.containsKey("communityModel") == true) {
			this.communityModel = data.get("communityModel");
			data.remove("communityModel");
		}

		// Number of agents
		if (data.containsKey("numberOfAgents") == true) {
			this.numberOfAgents = Integer.parseInt(data.get("numberOfAgents"));
			data.remove("numberOfAgents");
		}

		// Community Name
		if (data.containsKey("community") == true) {
			this.community = data.get("community");
			data.remove("community");
		}

		// subCommunity
		if (data.containsKey("subCommunity") == true) {
			this.subCommunity = data.get("subCommunity");
			data.remove("subCommunity");
		}

		// Checking if extra data is available and putting this straight to
		// the table
		for (String elem : data.keySet()) {
			this.additionalProperties.put(elem, data.get(elem));
		}

		// Update SimulationTask

		return true;

	}

	// mode: if(==0): first call of method
	// if(==1): User wants to overwrite file
	// if(==2): User didn't want to overwrite file
	public boolean writeToFile(int mode) {

		if (mode == 0) {
			try {
				File file = new File(path);
				if (file.createNewFile()) {
					System.out.println("Success!");
					writeData(true);
					return true;
				} else {
					System.out.println("Error, file already exists.");
					return false;
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return false;

			}
		} else if (mode == 1) {
			System.out.println("Success!");
			writeData(false);
			return true;
		} else {
			return false;
		}

	}

	private boolean writeData(boolean overwrite) {
		try {

			System.out.println("Start writing to File");

			PrintStream out = new PrintStream(new FileOutputStream(path,
					overwrite));

			out.println("{");
			out.println("\"templateName\": " + simulationName + ",");
			out.println("\"useCurrentTimeAsStartDate\": " + useCurrentTime
					+ ",");
			out.println("\"startYear\": " + startYear + ",");
			out.println("\"startMonth\": " + startMonth + ",");
			out.println("\"startDay\": " + startDay + ",");
			out.println("\"startHour\": " + startHour + ",");
			out.println("\"endYear\": " + endYear + ",");
			out.println("\"endMonth\": " + endMonth + ",");
			out.println("\"endDay\": " + endDay + ",");
			out.println("\"endHour\": " + endHour + ",");
			out.println("\"numberOfAgents\": " + numberOfAgents + ",");
			out.println("\"communityModel\": " + communityModel + ",");
			out.println("\"community\": " + community + ",");
			out.print("\"subCommunity\": " + subCommunity);
			for (String s : additionalProperties.keySet()) {
				out.println(",");
				out.print("\"" + s + "\": " + additionalProperties.get(s));
			}
			out.println();
			out.println("}");

			out.close();

			System.out.println("Finished writing to File");

			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void print() {

		System.out.println("Path: " + path);
		System.out.println("SimulationName: " + simulationName);
		System.out.println("useCurrentTimeAsStartDate: " + useCurrentTime);
		System.out.println("startYear: " + startYear);
		System.out.println("startMonth: " + startMonth);
		System.out.println("startDay: " + startDay);
		System.out.println("startHour: " + startHour);
		System.out.println("endYear: " + endYear);
		System.out.println("endMonth: " + endMonth);
		System.out.println("endDay: " + endDay);
		System.out.println("endHour: " + endHour);
		System.out.println("numberOfAgents: " + numberOfAgents);
		System.out.println("communityModel: " + communityModel);
		System.out.println("community: " + community);
		System.out.println("subCommunity: " + subCommunity);
		System.out.println("additionProperties:");
		for (String s : additionalProperties.keySet()) {
			System.out.println("--- " + s + ": " + additionalProperties.get(s));
		}
	}
	
	public Map<String, String> getMap() {
		
		Map<String, String> tmp = new HashMap<String, String>();
		
		tmp.put("communityModel", communityModel);
		tmp.put("templateName", simulationName);
		tmp.put("community", community);
		tmp.put("subCommunity", subCommunity);
		tmp.put("numberOfAgents", String.valueOf(numberOfAgents));
		tmp.put("startHour", startHour);
		tmp.put("endHour", endHour);
		tmp.put("startYear", startYear);
		tmp.put("startMonth", startMonth);
		tmp.put("startDay", startDay);
		tmp.put("endYear", endYear);
		tmp.put("endMonth", endMonth);
		tmp.put("endDay", endDay);

		for (String s : additionalProperties.keySet()) {
			tmp.put(s, additionalProperties.get(s));
		}
		
		return tmp;
		
	}

}
