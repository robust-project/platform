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

import java.util.LinkedList;
import java.util.List;

/**
 * sioc:Thread
 * 
 * Thread is a subclass of ContentItem. In a Thread, multiple Posts are collected,
 * which belongs together, because, for example, it is a discussion. A Thread is initialized
 * by a Post of one UserAccount and is replied by other Posts.
 * 
 * @see <a href="http://sioc-project.org/ontology#term_Thread">sioc:Thread</a>
 */
public class Thread extends ContentItem {
	
	/**
	 * The Forum, which this Thread belongs to
	 */
	public int forum;
	
	/**
	 * A List of Replies for this Thread
	 */
	public List<Post> replies;
	
	/**
	 * Displays, if the initialized Post was answered
	 */
	public Boolean answered = false;

	
	public Thread(User user, int Cforum) {
		super(user);
		forum = Cforum;
		replies = new LinkedList<Post>();
	}
	
	/**
	 * Adds a Reply to this Thread
	 * @param rep
	 */
	public void addReply(Post rep) {
		replies.add(rep);
	}
	
	/**
	 * Returns all Replies
	 * @return replies
	 */
	public List<Post> getReplies() {
		return replies;
	}

	/**
	 * Returns the answered attribute
	 * @return answered
	 */
	public Boolean getAnswered() {
		return answered;
	}

	/**
	 * Modifies the answered attribute
	 * @param answered
	 */
	public void setAnswered(Boolean answered) {
		this.answered = answered;
	}
	
	/**
	 * Returns the connected Forum of the Thread
	 * @return forum
	 */
	public int getForum() {
		return forum;
	}
	
	/**
	 * Returns the Creator of the Thread
	 * @return creator
	 */
	public User getCreator() {
		return creator;
	}
	
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Unused SIOC Properties
	 * sioc:last_reply_date
	 */

}
