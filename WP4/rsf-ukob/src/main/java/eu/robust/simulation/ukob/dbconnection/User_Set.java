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

public class User_Set {
	int id,quantity;
	float prob_posting_good, prob_deciding_good;
	

	int getID(){
		return id;
	}
	
	float getProb_posting_good(){
		return prob_posting_good; 
	}
	
	float getProb_deciding_good(){
		return prob_deciding_good; 
	}
	
	int getQuantity(){
		return quantity;
	}
	
	
	void setID(int id){
		this.id=id;
	}
	
	void setProb_posting_good(float prob_posting_good){
		this.prob_posting_good=prob_posting_good;
	}
	
	void setProb_deciding_good(float prob_deciding_good){
		this.prob_deciding_good=prob_deciding_good;
	}
	
	void setQuantity(int quantity){
		this.quantity=quantity;
	}
	
}
