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
 * Created by       	: Florian Kathe
 *
 * Creation Time    	: 21.09.2012
 *
 * Created for Project  : ROBUST
 */
 
package eu.robust.simulation.entities;

import java.util.Set;

/**
 * sioc:Space
 * 
 * Space is a location, where the data is provided. For example, Space is the location
 * for a Set of Containers.
 * 
 * @see <a href="http://sioc-project.org/ontology#term_Space">sioc:Space</a>
 */
public class Space extends CommunityConcept{
	
	/**
	 * Unused SIOC Properties
	 * sioc:has_space, sioc:feed
	 */
	
	/**
	 * sioc:has_owner
	 * Owner of the Space
	 */
	protected UserAccount has_owner;
	
	/** 
	 * sioc:has_usergroup
	 * Set of Usergroups, that have access to the Space
	 */
	protected Set<Usergroup> has_usergroup;

	
	/**
	 * sioc:space_of
	 * Resource, which belongs to the Space
	 */
	protected Space space_of;
	

	
}
