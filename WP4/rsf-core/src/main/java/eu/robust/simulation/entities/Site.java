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
 * sioc:Site
 * 
 * Site is a subclass of Space. A Community is placed on a Site, so a Site is a data space.
 * Forums and Post, organized in Containers, will be stored on a Site and can be accessed via
 * the Site.
 * 
 * @see <a href="http://sioc-project.org/ontology#term_Site">sioc:Site</a>
 */
public class Site extends Space {
	
	/**
	 * sioc:has_administrator
	 * Set of all UserAccounts with the administrator rights for the specific Site
	 */
	Set<UserAccount> has_administrator;
	
	/**
	 * sioc:host_of
	 * Set of all Forums, located on the Site
	 */
	Set<Forum> host_of;
	
}
