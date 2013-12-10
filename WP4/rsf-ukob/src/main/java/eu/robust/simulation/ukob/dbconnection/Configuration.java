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
 
package eu.robust.simulation.ukob.dbconnection;
public class Configuration {
	
	int id, policy_set_id, cycles;
	
	int getID(){
		return id;
	}
	
	int getPolicy_set_id(){
		return policy_set_id;
	}
	
	int getCycles(){
		return cycles;
	}
	
	void setID(int id){
		this.id =id;
	}
	
	void setPolicy_set_id(int policy_set_id){
		this.policy_set_id =policy_set_id;
	}
	
	void setCycles(int cycles){
		this.cycles =cycles;
	}
}
