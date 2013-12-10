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

public class User_Config {

	int user_set_id, config_id;
	
	int getUser_set_id(){
		return user_set_id;
	}
	
	int getConfig_id(){
		return config_id;
	}
	
	void setUser_set_id(int user_set_id){
		this.user_set_id=user_set_id;
	}
	
	void setConfig_id(int config_id){
		this.config_id=config_id;
	}
	
	
}
