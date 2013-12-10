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

public class Policy_Set {

	int id,required_points, post_life_span, admins;
	boolean moderation; 
	
	int getID(){
		return id;
	}
	
	boolean getModeration(){
		return moderation;
	}
	
	int getRequired_points(){
		return required_points;
	}
	
	int getPost_life_span(){
		return post_life_span;
	}
	
	int getAdmins(){
		return admins;
	}
	
	void setID(int id){
		this.id=id;
	}
	
	void setModeration(boolean moderation){
		this.moderation=moderation;
	}
	
	void setRequired_points(int required_points){
		this.required_points=required_points;
	}
	
	void setPost_life_span(int post_life_span){
		this.post_life_span=post_life_span;
	}
	
	void setAdmins(int admins){
		this.admins=admins;
	}
	
	
}
