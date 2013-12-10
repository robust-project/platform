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
 * Created by       	: Florian Kathe, Felix Schwagereit
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
import eu.robust.simulation.entities.Thread;
import eu.robust.simulation.entities.User;
import eu.robust.simulation.entities.UserAccount;
import eu.robust.simulation.entities.Usergroup;

/**
 * CommunityPolicyAllow is delegeted from IPlatformOperations and is used to manage
 * the access rights of the community. If you have acces rights on a method,
 * the policy will  forward your request to the next higher instance of IPlattformOperations.
 * The CommunityPolicyAllow class is the highest policy, so it will be forward
 * the request directly to a PlatformOperations
 */
public class CommunityPolicyAllow extends AbstractPolicy implements IPlatformOperations {
	


	public CommunityPolicyAllow(IPlatformOperations platform) {
		super(platform);
		this.initialize();
	}

	protected void initialize() {
		
	}
	

	/**
	 * Forwards the request to community
	 */
	public boolean createContainer(Container container, User user) {

		return platform.createContainer(container, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<Container> retrieveContainer(User user) {

		return platform.retrieveContainer(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateContainer(Container container, User user) {

		return platform.updateContainer(container, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createForum(Forum forum, User user) {

		return platform.createForum(forum, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<Forum> retrieveForum(User user) {
		
		return platform.retrieveForum(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateForum(Forum forum, User user) {

		return platform.updateForum(forum, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createItem(ContentItem item, User user) {

		return platform.createItem(item, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<ContentItem> retrieveItem(User user) {

		return platform.retrieveItem(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateItem(ContentItem item, User user) {

		return platform.updateItem(item, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createPost(Post post, User user) {
		
		return platform.createPost(post, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<Post> retrievePost(User user) {

		return platform.retrievePost(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updatePost(Post post, User user) {

		return platform.updatePost(post, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createRole(Role role, User user) {

		return platform.createRole(role, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<Role> retrieveRole(User user) {

		return platform.retrieveRole(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateRole(Role role, User user) {
		
		return platform.updateRole(role, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createSite(Site site, User user) {
		
		return platform.createSite(site, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<Site> retrieveSite(User user) {

		return platform.retrieveSite(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateSite(Site site, User user) {
		
		return platform.updateSite(site, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createSpace(Space space, User user) {

		return platform.createSpace(space, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<Space> retrieveSpace(User user) {
		
		return platform.retrieveSpace(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateSpace(Space space, User user) {
		
		return platform.updateSpace(space, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createThread(Thread thread, User user) {
		
		return platform.createThread(thread, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<Thread> retrieveThread(User user) {
		
		return platform.retrieveThread(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateThread(Thread thread, User user) {
		
		return platform.updateThread(thread, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createUserAccount(UserAccount userAccount, User user) {
		
		return platform.createUserAccount(userAccount, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<UserAccount> retrieveUserAccount(User user) {
		
		return platform.retrieveUserAccount(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateUserAccount(UserAccount userAccount, User user) {
		
		return platform.updateUserAccount(userAccount, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean createUsergroup(Usergroup usergroup, User user) {
		
		return platform.createUsergroup(usergroup, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public Set<Usergroup> retrieveUsergroup(User user) {
		
		return platform.retrieveUsergroup(user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean updateUsergroup(Usergroup usergroup, User user) {
		
		return platform.updateUsergroup(usergroup, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteContainer(Container container, User user) {

		return platform.deleteContainer(container, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteForum(Forum forum, User user) {

		return platform.deleteForum(forum, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteItem(ContentItem item, User user) {
		
		return platform.deleteItem(item, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deletePost(Post post, User user) {
		
		return platform.deletePost(post, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteRole(CommunityConcept role, User user) {

		return platform.deleteRole(role, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteSite(Site site, User user) {
		
		return platform.deleteSite(site, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteSpace(Space space, User user) {
		
		return platform.deleteSpace(space, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteThread(Thread thread, User user) {
		
		return platform.deleteThread(thread, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteUserAccount(UserAccount userAccount, User user) {
		
		return platform.deleteUserAccount(userAccount, user);
		
	}

	/**
	 * Forwards the request to community
	 */
	public boolean deleteUsergroup(Usergroup usergroup, User user) {
		
		return platform.deleteUsergroup(usergroup, user);
		
	}

}
