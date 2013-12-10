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
import eu.robust.simulation.entities.Thread;
import eu.robust.simulation.entities.User;
import eu.robust.simulation.entities.UserAccount;
import eu.robust.simulation.entities.Usergroup;

/**
 * CommunityPolicyRestricted is delegeted from IPlatformOperations and is used
 * to manage the access rights of the community. If you don't have acces rights
 * on a method, the policy will drop your request and will always return false
 * or null.
 */
public class CommunityPolicyRestrict extends AbstractPolicy implements IPlatformOperations {
	


	public CommunityPolicyRestrict(IPlatformOperations platform) {
		super(platform);
		this.initialize();
	}

	protected void initialize() {
		
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createContainer(Container container, User user) {
		
		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<Container> retrieveContainer(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateContainer(Container container, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createForum(Forum forum, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<Forum> retrieveForum(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateForum(Forum forum, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createItem(ContentItem item, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<ContentItem> retrieveItem(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateItem(ContentItem item, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createPost(Post post, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<Post> retrievePost(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updatePost(Post post, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createRole(Role role, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<Role> retrieveRole(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateRole(Role role, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createSite(Site site, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<Site> retrieveSite(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateSite(Site site, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createSpace(Space space, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<Space> retrieveSpace(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateSpace(Space space, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createThread(Thread thread, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<Thread> retrieveThread(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateThread(Thread thread, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createUserAccount(UserAccount userAccount, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<UserAccount> retrieveUserAccount(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateUserAccount(UserAccount userAccount, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean createUsergroup(Usergroup usergroup, User user) {

		return false;
	}

	/**
	 * Returns null because you don't have the access rights
	 * to operate on the attribute community
	 */
	public Set<Usergroup> retrieveUsergroup(User user) {

		return null;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean updateUsergroup(Usergroup usergroup, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteContainer(Container container, User user) {
		
		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteForum(Forum forum, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteItem(ContentItem item, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deletePost(Post post, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteRole(CommunityConcept role, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteSite(Site site, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteSpace(Space space, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteThread(Thread thread, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteUserAccount(UserAccount userAccount, User user) {

		return false;
	}

	/**
	 * Returns false because you don't have the access rights
	 * to operate on the attribute community
	 */
	public boolean deleteUsergroup(Usergroup usergroup, User user) {

		return false;
	}
	
}
