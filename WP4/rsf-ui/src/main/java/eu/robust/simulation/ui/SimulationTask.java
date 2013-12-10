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

import java.awt.Color;
import java.awt.Shape;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.security.auth.login.FailedLoginException;

public class SimulationTask {

	// Id
	private int Id;

	// Id as String
	private String simId;

	//
	private int tab;

	// Name of the Simulation
	private String name;

	// Data from the Input File
	// private Map<String, String> inputData;
	private SimulationModel input;

	// Generated Output from the Simulation
	private Map<String, String> outputData;

	// Status of the Simulation
	private SimulationStatus status;
	
	private Color color = null;
	
	private Shape shape = null;
	
	private boolean chartVisibility;

	// Constructor
	public SimulationTask(int Id) {
		this.Id = Id;
		this.status = SimulationStatus.EMPTY;
		this.input = new SimulationModel();
	}

	public SimulationTask(int Id,
			int tab/* , HashMap<String, String> inputData */,
			SimulationModel model) {
		this.Id = Id;
		this.tab = tab;
		this.status = SimulationStatus.EMPTY;
		// this.inputData = inputData;
		this.input = new SimulationModel(model);
	}

	// Getter & Setter
	public int getId() {
		return Id;
	}

	// public void setId(int Id) {
	// this.Id = Id;
	// }

	public String getSimId() {
		return simId;
	}

	public void setSimId(String simId) {
		this.simId = simId;
	}

	public int getTab() {
		return tab;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// public Map<String, String> getInputData() {
	// return inputData;
	// }
	//
	// public void setInputData(Map<String, String> inputData) {
	// //System.out.println("Wirte Input");
	// this.inputData = inputData;
	// }

	public SimulationModel getModel() {

		return this.input;

	}

	public Map<String, String> getOutputData() {
		return outputData;
	}

	public void setOutputData(Map<String, String> map) {
		this.outputData = map;
	}

	public SimulationStatus getStatus() {
		return status;
	}

	public void setStatus(SimulationStatus status) {
		this.status = status;
	}

	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}



	public Shape getShape(){
		return shape;
	}
	
	public void setShape(Shape shape){
		this.shape = shape;
	}
	
	public boolean getChartVisibility(){
		return chartVisibility;
	}
	
	public void setChartVisibility(boolean chartVisibility){
		this.chartVisibility = chartVisibility;
	}
	
	public boolean writeResult() {
		
		try {

			System.out.println("Start writing to File");

			String[] splitArray = input.getPath().split("\\\\");
			String path = new String();
			for(int i=0; i<splitArray.length-1; i++) {
				path += splitArray[i]+"\\";
			}
			path += simId+".csv";
			System.out.println(path);
			
			TreeMap<Integer, Integer> output_sorted = new TreeMap<Integer, Integer>();
			for (Map.Entry<String, String> e : outputData.entrySet()) {
				output_sorted.put(Integer.parseInt(e.getKey()), Integer.parseInt(e.getValue()));
			}
			
			PrintStream out = new PrintStream(new FileOutputStream(path,
					true));
			

			//out.println("{");
			for (Map.Entry<Integer, Integer> e : output_sorted.entrySet()) {
				out.println(e.getKey()+";"+e.getValue());
			}
			//out.println("}");

			out.close();

			System.out.println("Finished writing to File");

			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
	}

}
