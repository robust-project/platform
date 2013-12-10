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
 * sioc:Usergroup
 * 
 * A Usergroup is a collection of multiple UserAccounts. It can be used for  access rights, too,
 * or to let Users with common interests have a own Usergroup to share information.
 * 
 * @see <a
 *      href="http://sioc-project.org/ontology#term_Usergroup">sioc:Usergroup</a>
 */
public class Usergroup extends CommunityConcept {

	/**
	 * Unused SIOC Properties sioc:usergroup_of
	 */

	/**
	 * sioc:has_member
	 * Set of all UserAccounts, connected to the Usergroup
	 */
	protected Set<UserAccount> has_member;


	/**
	 * sioc:name
	 * Instance of rdfs:Literal
	 * Name of the Usergroup
	 */
	protected String name;



}
