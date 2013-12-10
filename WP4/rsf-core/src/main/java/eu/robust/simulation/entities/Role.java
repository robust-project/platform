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
 * sioc:Role
 * 
 * An UserAccount can have a Role. For example, with various Roles, various access rights
 * can be implemented. Roles depend on the Container, so an UserAccount can have different Roles
 * in different Containers.
 * 
 * @see <a href="http://sioc-project.org/ontology#term_Role">sioc:Role</a>
 */
public class Role extends CommunityConcept {
	
	/**
	 * sioc:function_of
	 * Set of all UserAccounts whit this Role
	 */
	protected Set<UserAccount> function_of;
	
	/**
	 * sioc:has_scope
	 * Resource to which this Role is applied to
	 */
	protected Object has_scope;

}
