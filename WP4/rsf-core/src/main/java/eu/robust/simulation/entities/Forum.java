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
 * sioc:Forum
 * 
 * Forum is a subclass of Container. It specifies the Container as a discussion area.
 * It is used to organize Threads and Posts.
 * 
 * @see <a href="http://sioc-project.org/ontology#term_Forum">sioc:Forum</a>
 */
public class Forum extends Container {

	/**
	 * Unused SIOC Properties
	 * sioc:has_host
	 */
	
	/**
	 * sioc:has_moderator
	 * Contains a Set of all User who are the moderators of the Forum
	 */
	protected Set<UserAccount> has_moderator;
	
	/**
	 * sioc:num_threads
	 * Instance of xsd:nonNegativeInteger
	 * Number of all Threads in the Forum
	 */
	protected int num_threads;
	
}
