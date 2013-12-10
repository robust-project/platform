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
 * sioc:Container
 * 
 * A Container includes ContentItems. It is used to arrange the various topics of ContentItems.
 * Containers have a hierarchy to produce a more accurate classification.
 * 
 * @see <a
 *      href="http://sioc-project.org/ontology#term_Container">sioc:Container</a>
 */
public class Container extends CommunityConcept {

	/**
	 * Unused SIOC Properties sioc:has_parent, sioc:has_subscriber
	 */

	/**
	 * sioc:container_of
	 * All Items which are arranged in the Container
	 */
	protected Set<ContentItem> container_of;

	/**
	 * sioc:has_owner
	 * Owner of the Container
	 */
	protected UserAccount has_owner;


	/**
	 * sioc:last_item_date
	 * Instance of rdfs:Literal
	 * Date of the last modified ContentItem
	 */
	protected String last_item_date;

	/**
	 * sioc:num_items
	 * Instance of xsd:nonNegativeInteger
	 * Number of all items in the Container
	 */
	protected int num_items;

	/**
	 * sioc:parent_of
	 * A child Container the Container is parent of
	 */
	protected Container parent_of;



}
