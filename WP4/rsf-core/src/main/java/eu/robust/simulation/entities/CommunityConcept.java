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

public class CommunityConcept {
	
	private static int globalCreationOrderId = 0;
	private int myCreationOrderId;

	/**
	 * sioc:id
	 * ID of the Concept
	 */
	protected int id;

	
	/**
	 * sioc:id
	 * ID as Literal of the Entity (usable for URIs)
	 */
	private String idLiteral;
	

	public String getIdLiteral() {
		return idLiteral;
	}


	public void setIdLiteral(String idLiteral) {
		this.idLiteral = idLiteral;
	}


	public CommunityConcept() {
		super();
		myCreationOrderId = globalCreationOrderId;
		globalCreationOrderId++;
	}
	
	public int getCreationOrderId() {
		return myCreationOrderId;
	}
	
	
	/**
	 * Returns the ID of the Role
	 * @return sioc:id
	 */
	public int getID() {
		return id;
		}
	
	
	void setID(int id) {
		this.id = id;
	}

}
