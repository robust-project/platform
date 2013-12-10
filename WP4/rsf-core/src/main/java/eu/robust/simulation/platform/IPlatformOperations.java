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

package eu.robust.simulation.platform;

import java.util.Set;

import eu.robust.simulation.entities.CommunityConcept;
import eu.robust.simulation.entities.Container;
import eu.robust.simulation.entities.ContentItem;
import eu.robust.simulation.entities.Forum;
import eu.robust.simulation.entities.Post;
import eu.robust.simulation.entities.Role;
import eu.robust.simulation.entities.Site;
import eu.robust.simulation.entities.Space;
import eu.robust.simulation.entities.User;
import eu.robust.simulation.entities.Thread;
import eu.robust.simulation.entities.UserAccount;
import eu.robust.simulation.entities.Usergroup;

/**
 * IPlatformOperations is an Interface for a community platform or a community policiy.
 * It provides all functions to realize a community, for example, there is a method
 * to create a Forum or to delete a Post. The PlatformOperations deduce from this Interface
 * and implements all methods. A community policy deduce from this Interface, too but has
 * a PlatformOperations as attribute and delegates all method calls to it, if it is desired.
 */
public interface IPlatformOperations {
	
	
	public void setCurrentTick(int tick);
	
	/*
	 * sioc:Container
	 */
	
	/**
	 * Adds a Container to the community
	 * @param container
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createContainer(Container container, User user);
	
	/**
	 * Returns all Containers of the community
	 * @param user
	 * @return returns all Containers of the Community
	 */
	public Set<Container> retrieveContainer(User user);
	
	/**
	 * Updates a Container of the community
	 * @param container
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateContainer(Container container, User user);
	
	/**
	 * Deletes a Container from the community
	 * @param container
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteContainer(Container container, User user);
	
	/*
	 * sioc:Forum
	 */
	
	/**
	 * Adds a Forum to the community 
	 * @param forum
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createForum(Forum forum, User user);
	
	/**
	 * Returns all Forums of the community
	 * @param user
	 * @return returns all Forums of the Community
	 */
	public Set<Forum> retrieveForum(User user);
	
	/**
	 * Updates a Forum of the community
	 * @param forum
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateForum(Forum forum, User user);
	
	/**
	 * Deletes a Forum from the community
	 * @param forum
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteForum(Forum forum, User user);
	
	/*
	 * sioc:Item
	 */

	/**
	 * Adds a ContentItem to the community
	 * @param Item
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createItem(ContentItem item, User user);
	
	/**
	 * Returns all ContentItems of the community
	 * @param user
	 * @return returns all Items of the Community
	 */
	public Set<ContentItem> retrieveItem(User user);
	
	/**
	 * Updates a ContentItem of the community
	 * @param item
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateItem(ContentItem item, User user);
	
	/**
	 * Deletes a ContentItem from the community
	 * @param item
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteItem(ContentItem item, User user);
	/*
	 * sioc:Post
	 */
	
	/**
	 * Adds a Post to the community
	 * @param post
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createPost(Post post, User user);
	
	/**
	 * Returns all Posts of the community
	 * @param user
	 * @return returns all Posts of the Community
	 */
	public Set<Post> retrievePost(User user);
	
	/**
	 * Updates a Post of the community
	 * @param post
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updatePost(Post post, User user);
	
	/**
	 * Deletes a Post from the community
	 * @param post
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deletePost(Post post, User user);
	
	/*
	 * sioc:Role
	 */
	
	/**
	 * Adds a Role to the community
	 * @param role
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createRole(Role role, User user);
	
	/**
	 * Returns all Roles of the community
	 * @param user
	 * @return returns all Roles of the Community
	 */
	public Set<Role> retrieveRole(User user);
	
	/**
	 * Updates a Role of the community
	 * @param role
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateRole(Role role, User user);
	
	/**
	 * Deletes a Role from the community
	 * @param role
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteRole(CommunityConcept role, User user);
	
	/*
	 * sioc:Site
	 */
	
	/**
	 * Adds a Site to the community
	 * @param site
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createSite(Site site, User user);
	
	/**
	 * Returns all Sites of the community
	 * @param user
	 * @return returns all Sites of the Community
	 */
	public Set<Site> retrieveSite(User user);
	
	/**
	 * Updates a Site of the community
	 * @param site
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateSite(Site site, User user);
	
	/**
	 * Deletes a Site from the community
	 * @param site
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteSite(Site site, User user);
	
	/*
	 * sioc:Space
	 */
	
	/**
	 * Adds a Space to the community
	 * @param Space
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createSpace(Space space, User user);
	
	/**
	 * Returns all Spaces from the community
	 * @param user
	 * @return returns all Spaces of the Community
	 */
	public Set<Space> retrieveSpace(User user);
	
	/**
	 * Updates a Space of the community
	 * @param space
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateSpace(Space space, User user);
	
	/**
	 * Deletes a Space from the community
	 * @param space
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteSpace(Space space, User user);
	
	/*
	 * sioc:Thread
	 */
	
	/**
	 * Adds a Thread to the community
	 * @param Thread
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createThread(Thread thread, User user);
	
	/**
	 * Returns all Threads of the community
	 * @param user
	 * @return returns all Threads of the Community
	 */
	public Set<Thread> retrieveThread(User user);
	
	/**
	 * Updates a Thread of the community
	 * @param thread
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateThread(Thread thread, User user);
	
	/**
	 * Deletes a Thread from the community
	 * @param thread
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteThread(Thread thread, User user);
	
	/*
	 * sioc:UserAccount
	 */
	
	/**
	 * Adds a UserAccount to the community
	 * @param UserAccount
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createUserAccount(UserAccount userAccount, User user);
	
	/**
	 * Returns all UserAccounts of the community
	 * @param user
	 * @return returns all UserAccounts of the Community
	 */
	public Set<UserAccount> retrieveUserAccount(User user);
	
	/**
	 * Updates a UserAccount of the community
	 * @param userAccount
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateUserAccount(UserAccount userAccount, User user);
	
	/**
	 * Deletes a UserAccount from the community
	 * @param userAccount
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteUserAccount(UserAccount userAccount, User user);
	/*
	 * sioc:Usergroup
	 */
	
	/**
	 * Adds a Usergroup to the community
	 * @param Usergroup
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean createUsergroup(Usergroup usergroup, User user);
	
	/**
	 * Returns all Usergroups of the community
	 * @param user
	 * @return returns all Usergroups of the Community
	 */
	public Set<Usergroup> retrieveUsergroup(User user);
	
	/**
	 * Updates a Usergroup of the community
	 * @param usergroup
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean updateUsergroup(Usergroup usergroup, User user);
	
	/**
	 * Deletes a Usergroup from the community
	 * @param usergroup
	 * @param user
	 * @return returns true, if the operation was successful
	 */
	public boolean deleteUsergroup(Usergroup usergroup, User user);
	
}
