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

import java.util.Set;

/**
 * sioc:Item
 * 
 * A ContentItem is an Object, that can be placed in a Container. For example, it is used to
 * describe messages from Users, like Posts. A ContentItem is published with a topic or
 * can be append to another ContentItem.
 * 
 * @see <a href="http://sioc-project.org/ontology#term_Item">sioc:Item</a>
 */
public class ContentItem extends CommunityConcept{
	

	
	/**
	 * sioc:has_creator
	 * Creator of the ContentItem
	 */
	protected User creator;
	/**
	 * Time, when the ContentItem is created
	 */
	protected int creationtime;
	
	private double quality=0;
	
	public ContentItem(User Ccreator) {
		creator = Ccreator;
	}
	
	/**
	 * Initialize the attributes id and creationtime
	 * @param newID
	 * @param newCreationTime
	 */
	public void setInitValues(int newID, int newCreationTime){
		id=newID;
		creationtime=newCreationTime;
	}
	
	
	/**
	 * returns the creator of the ContentItem
	 * @return creator
	 */
	public User getCreator() {
		return creator;
	}
	
	/**
	 * returns the creationtime of the ContentItem
	 * @return creationtime
	 */
	public int getCreationtime() {
		return creationtime;
	}
	
	public void setQuality(double q) {
		quality = q;
	}
	
	public double getQuality() {
		return quality;
	}
	
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Unused SIOC Properties
	 * sioc:about, sioc:addressed_to, sioc:attachment, sioc:embeds_knowledge, sioc:has_reply, 
	 * sioc:last_activity_date, sioc:link, sioc:links_to, sioc:note, sioc:num_authors, 
	 * sioc:num_replies, sioc:num_views, sioc:related_to, sioc:scope_of
	 */
	
	/**
	 * sioc:content
	 * Instance of rdfs:Literal
	 * Content of the ContentItem
	 */
	protected String content;
	
	/**
	 * sioc:earlier_version
	 * A older version of the ContentItem
	 */
	protected ContentItem earlier_version;
	
	/**
	 * sioc:has_container
	 * Container that  contains the ContentItem
	 */
	protected Container has_container;
	
	/**
	 * sioc:has_discussion
	 * Links to the related discussion
	 */
	protected Container has_discussion;
	
	/**
	 * sioc:has_modifier
	 * Contains all UserAccounts, can modify the ContentItem
	 */
	protected Set<UserAccount> has_modifier;
	
	/**
	 * sioc:has_owner
	 * Owner of the ContentItem
	 */
	protected UserAccount has_owner;
	
	/**
	 * sioc:ip_address
	 * Instance of rdfs:Literal
	 * The IP adress, that were used to create the ContentItem
	 */
	protected String ip_adress;
	
	/**
	 * sioc:later_version
	 * A newer version of the ContentItem
	 */
	protected ContentItem later_version;
	
	/**
	 * sioc:latest_version
	 * The newest version of the ContentItem
	 */
	protected ContentItem latest_version;
	
	/**
	 * sioc:next_by_date
	 * Next ContentItem in a Container, sorted by date
	 */
	protected ContentItem next_by_date;
	
	/**
	 * sioc:next_version
	 * Links to the next modification of the ContentItem
	 */
	protected ContentItem next_version;
	
	/**
	 * sioc:previous_by_date
	 * Previous ContentItem in a Container, sorted by date
	 */
	protected ContentItem previous_by_date;
	
	/**
	 * sioc:previous_version
	 * Links to the previous modification of the ContentItem
	 */
	protected ContentItem previous_version;
	
	/**
	 * sioc:reply_of
	 * Links to the replied ContentItem
	 */
	protected ContentItem reply_of;
	
	/**
	 * sioc:sibling
	 * Links to a similar ContenItem in a different Container
	 */
	protected ContentItem sibling;
	
	/**
	 * sioc:topic
	 * A short describtion of the Content of the ContentItem
	 */
	protected String topic;
	
}
