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
 
package eu.robust.simulation.entities;

import eu.robust.simulation.platform.IPlatformOperations;


public abstract class User {
	
	protected int id=0;
	protected int registerTime;
	
	protected int actSimulationTick;
	
	private IPlatformOperations platformOperations;
	


	public void act() {
		browseContent();
		createContent();
		revisitOwnContent();
	}
	
	public void setInitValues(int newID, int newRegisterTime){
		id=newID;
		registerTime=newRegisterTime;
	}
	
	public IPlatformOperations getPlatformOperations() {
		return platformOperations;
	}

	public void setPlatformOperations(IPlatformOperations platformOperations) {
		this.platformOperations = platformOperations;
	}
	
	public int getID() {
		return id;
	}

	
	protected void browseContent() {
	}
	
	protected void createContent() {
	}
	
	protected void revisitOwnContent() {
		// closes thread
		// awards points
		// replies to own thread
	}

	public int getRegisterTime() {
		return registerTime;
	}
	
	public void setCurrentSimulationTick(int newTick) {
		actSimulationTick = newTick;
	}

}
