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
 
package eu.robust.simulation.ukob.policies;


import eu.robust.simulation.entities.CIManager;
import eu.robust.simulation.entities.ContentItem;
import eu.robust.simulation.entities.User;
import eu.robust.simulation.policies.Policy;

public class SCNForumPolicy extends Policy{
	
	// Content Selection
	public ContentItem selectCI(User user, CIManager contentItems) {
		return contentItems.getRandom();
	}

}
