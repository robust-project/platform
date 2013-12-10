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
 * Creation Time    	: 18.10.2012
 *
 * Created for Project  : ROBUST
 */

package eu.robust.simulation.entities;

import java.util.Set;

/**
 * sioc:UserAccount
 * 
 * UserAccount is the virtual ID of an User in a Community. It is the connection between User
 * and Community. A UserAccount can subscribe to Containers and can follow other UserAccounts.
 * 
 * @see <a
 *      href="http://sioc-project.org/ontology#term_UserAccount">sioc:UserAccount</a>
 */
public class UserAccount extends CommunityConcept {

	/**
	 * Unused SIOC Properties sioc:administrator_of, sioc:creator_of,
	 * sioc:has_function, sioc:member_of, sioc:moderator_of, sioc:modifier_of,
	 * sioc:owner_of
	 */


	/**
	 * sioc:account_of
	 * Instance of foaf:Agent
	 * Links the UserAccount to a web site or something near to it 
	 */
	protected String account_of;

	/**
	 * sioc:avatar
	 * Sub-property of foaf:depiction
	 * Consists of the URL to the avatar image
	 */
	protected String avatar;

	/**
	 * sioc:email
	 * The email address of the UserAccount
	 */
	protected String email;

	/**
	 * sioc:email_sha1
	 * Instance of rdfs:Literal
	 * The email address of the UserAccount, encoded using SHA1
	 */
	protected String email_sha1;

	/**
	 * sioc:follows
	 * Set of Users, which follows this UserAccount
	 */
	protected Set<UserAccount> follows;

	/**
	 * sioc:name Instance of rdfs:Literal
	 * The name of the UserAccount
	 */
	protected String name;

	/**
	 * sioc:subscriber_of
	 * Set of Container that the UserAccount is subscribed to
	 */
	protected Set<Container> subscriber_of;



}
