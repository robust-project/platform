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

import java.util.HashSet;
import java.util.Set;

import eu.robust.simulation.entities.CIManagerT;
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
 * The PlatformOperations class is the core of a community. It has a Set for each SIOC class
 * of the community, where all community data is stored. The Methods will operate
 * on these Sets of SIOC classes.
 */
public class PlatformOperations implements IPlatformOperations {

	/**
	 * All Containers of the community will be stored in this Set
	 */
	private Set<Container> containers;

	/**
	 * All Forums of the community will be stored in this Set
	 */
	private Set<Forum> forums;

	/**
	 * All ContentItems of the community will be stored in this Set
	 */
	private CIManagerT<ContentItem> items;

	/**
	 * All Posts of the community will be stored in this Set
	 */
	private CIManagerT<Post> posts;

	/**
	 * All Roles of the community will be stored in this Set
	 */
	private Set<Role> roles;

	/**
	 * All Sites of the community will be stored in this Set
	 */
	private Set<Site> sites;

	/**
	 * All Spaces of the community will be stored in this Set
	 */
	private Set<Space> spaces;

	/**
	 * All Threads of the community will be stored in this Set
	 */
	private CIManagerT<Thread> threads;

	/**
	 * All UserAccounts of the community will be stored in this Set
	 */
	private Set<UserAccount> useraccounts;

	/**
	 * All Usergroups of the community will be stored in this Set
	 */
	private Set<Usergroup> usergroups;
	
	private int currentTick=0;
	
	
	public PlatformOperations() {
		
		initialize();
		
	}

	/**
	 * Initializes all Sets of CommunityPlatform
	 */
	private void initialize() {

		this.containers = new HashSet<Container>();
		this.forums = new HashSet<Forum>();
		this.items = new CIManagerT<ContentItem>();
		this.posts = new CIManagerT<Post>();
		this.roles = new HashSet<Role>();
		this.sites = new HashSet<Site>();
		this.spaces = new HashSet<Space>();
		this.threads = new CIManagerT<Thread>();
		this.useraccounts = new HashSet<UserAccount>();
		this.usergroups = new HashSet<Usergroup>();

	}
	
	public void setCurrentTick(int cTick) {
		//System.out.println("PO "+currentTick);
		currentTick = cTick;
	}
	

	/**
	 * Adds a Container to the Set containers.
	 * Returns true, if the operation was successful
	 */
	public boolean createContainer(Container container, User user) {

		return containers.add(container);

	}
	
	/**
	 * Returns the Set containers
	 */
	public Set<Container> retrieveContainer(User user) {

		return containers;

	}

	/**
	 * Updates the committed Container in the Set containers.
	 * Returns false, if the Container wasn't in the Set
	 */
	public boolean updateContainer(Container container, User user) {

		
		boolean check = false;
		for (Container c: containers) {
			if (c.getID() == container.getID()) {
				containers.remove(c);
				containers.add(container);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed Container from the Set containers.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteContainer(Container container, User user) {
		
		return containers.remove(container);

		
	}

	/**
	 * Adds a Forum to the Set forums.
	 * Returns true, if the operation was successful
	 */
	public boolean createForum(Forum forum, User user) {

		return forums.add(forum);

	}

	/**
	 * Returns the Set fourms
	 */
	public Set<Forum> retrieveForum(User user) {

		return forums;
	}

	/**
	 * Updates the committed Forum in the Set forums.
	 * Returns false, if the Forum wasn't in the Set
	 */
	public boolean updateForum(Forum forum, User user) {

		boolean check = false;
		for (Forum f: forums) {
			if (f.getID() == forum.getID()) {
				forums.remove(f);
				forums.add(forum);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed Forum from the Set containers.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteForum(Forum forum, User user) {

		return forums.remove(forum);
		
	}

	/**
	 * Adds a ContentItem to the Set items.
	 * Returns true, if the operation was successful
	 */
	public boolean createItem(ContentItem item, User user) {

		items.setCurrentTick(currentTick);
		items.add(item);
		return true;

	}

	/**
	 * Returns the Set items
	 */
	public Set<ContentItem> retrieveItem(User user) {

		return items.getAllItems().getAll();

	}

	/**
	 * Updates the committed ContentItem in the Set items.
	 * Returns false, if the ContentItem wasn't in the Set
	 */
	public boolean updateItem(ContentItem item, User user) {

		boolean check = false;
		for (ContentItem ci: items.getAllItems().getAll()) {
			if (ci.getID() == item.getID()) {
				items.remove(ci);
				items.add(item);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed ContentItem from the Set items.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteItem(ContentItem item, User user) {
		

		return items.remove(item);

		
	}

	/**
	 * Adds a Post to the Set posts.
	 * Returns true, if the operation was successful
	 */
	public boolean createPost(Post post, User user) {
	
		posts.setCurrentTick(currentTick);
		posts.add(post);
		return true;

	}

	/**
	 * Returns the Set posts
	 */
	public Set<Post> retrievePost(User user) {

		return posts.getAllItems().getAll();

	}

	/**
	 * Updates the committed Post in the Set posts.
	 * Returns false, if the Post wasn't in the Set
	 */
	public boolean updatePost(Post post, User user) {

		boolean check = false;
		for (Post p: posts.getAllItems().getAll()) {
			if (p.getID() == post.getID()) {
				posts.remove(p);
				posts.add(post);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed Post from the Set posts.
	 * Returns true, if the operation was successful
	 */
	public boolean deletePost(Post post, User user) {
		
		return posts.remove(post);
		
		
	}
	
	/**
	 * Adds a Role to the Set roles.
	 * Returns true, if the operation was successful
	 */
	public boolean createRole(Role role, User user) {

		return roles.add(role);

	}

	/**
	 * Returns the Set roles
	 */
	public Set<Role> retrieveRole(User user) {

		return roles;

	}

	/**
	 * Updates the committed Role in the Set roles.
	 * Returns false, if the Role wasn't in the Set
	 */
	public boolean updateRole(Role role, User user) {

		boolean check = false;
		for (Role r: roles) {
			if (r.getID() == role.getID()) {
				roles.remove(r);
				roles.add(role);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed Role from the Set roles.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteRole(CommunityConcept role, User user) {
		
		return roles.remove(role);
		
	}

	/**
	 * Adds a Site to the Set sites.
	 * Returns true, if the operation was successful
	 */
	public boolean createSite(Site site, User user) {

		return sites.add(site);

	}

	/**
	 * Returns the Set sites
	 */
	public Set<Site> retrieveSite(User user) {

		return sites;

	}

	/**
	 * Updates the committed Site in the Set sites.
	 * Returns false, if the Site wasn't in the Set
	 */
	public boolean updateSite(Site site, User user) {

		boolean check = false;
		for (Site s: sites) {
			if (s.getID() == site.getID()) {
				sites.remove(s);
				sites.add(site);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed Site from the Set sites.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteSite(Site site, User user) {
		
		return sites.remove(site);
		
	}

	/**
	 * Adds a Space to the Set spaces.
	 * Returns true, if the operation was successful
	 */
	public boolean createSpace(Space space, User user) {

		return spaces.add(space);

	}

	/**
	 * Returns the Set spaces
	 */
	public Set<Space> retrieveSpace(User user) {

		return spaces;

	}

	/**
	 * Updates the committed Space in the Set spaces.
	 * Returns false, if the Space wasn't in the Set
	 */
	public boolean updateSpace(Space space, User user) {

		boolean check = false;
		for (Space s: spaces) {
			if (s.getID() == space.getID()) {
				spaces.remove(s);
				spaces.add(space);
				check = true;
				break;
			}
		}
		return check;


	}
	
	/**
	 * Deletes the committed Space from the Set spaces.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteSpace(Space space, User user) {
		
		return spaces.remove(space);
		
	}

	/**
	 * Adds a Thread to the Set threads.
	 * Returns true, if the operation was successful
	 */
	public boolean createThread(Thread thread, User user) {
		
		threads.setCurrentTick(currentTick);
		threads.add(thread);
		return true;

	}

	/**
	 * Returns the Set threads
	 */
	public Set<Thread> retrieveThread(User user) {

		return threads.getAllItems().getAll();

	}

	/**
	 * Updates the committed Thread in the Set threads.
	 * Returns false, if the Thread wasn't in the Set
	 */
	public boolean updateThread(Thread thread, User user) {

		boolean check = false;
		for (Thread t: threads.getAllItems().getAll()) {
			if (t.getID() == thread.getID()) {
				threads.remove(t);
				threads.add(thread);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed Thread from the Set threads.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteThread(Thread thread, User user) {
		
		return threads.remove(thread);

		
	}

	/**
	 * Adds a UserAccount to the Set useraccounts.
	 * Returns true, if the operation was successful
	 */
	public boolean createUserAccount(UserAccount userAccount, User user) {

		return useraccounts.add(userAccount);

	}
	
	/**
	 * Returns the Set useraccounts
	 */
	public Set<UserAccount> retrieveUserAccount(User user) {

		return useraccounts;

	}

	/**
	 * Updates the committed UserAccount in the Set useraccounts.
	 * Returns false, if the UserAccounts wasn't in the Set
	 */
	public boolean updateUserAccount(UserAccount userAccount, User user) {

		boolean check = false;
		for (UserAccount a: useraccounts) {
			if (a.getID() == userAccount.getID()) {
				useraccounts.remove(a);
				useraccounts.add(userAccount);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed UserAccount from the Set useraccounts.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteUserAccount(UserAccount userAccount, User user) {
		
		
		return useraccounts.remove(userAccount);
		
	}

	/**
	 * Adds a Usergroup to the Set usergroups.
	 * Returns true, if the operation was successful
	 */
	public boolean createUsergroup(Usergroup usergroup, User user) {

		return usergroups.add(usergroup);

	}

	/**
	 * Returns the Set usergroups
	 */
	public Set<Usergroup> retrieveUsergroup(User user) {
		
		return usergroups;
		
	}

	/**
	 * Updates the committed Usergroup in the Set usergroups.
	 * Returns false, if the Usergroup wasn't in the Set
	 */
	public boolean updateUsergroup(Usergroup usergroup, User user) {

		boolean check = false;
		for (Usergroup u: usergroups) {
			if (u.getID() == usergroup.getID()) {
				usergroups.remove(u);
				usergroups.add(usergroup);
				check = true;
				break;
			}
		}
		return check;

	}
	
	/**
	 * Deletes the committed Usergroup from the Set usergroups.
	 * Returns true, if the operation was successful
	 */
	public boolean deleteUsergroup(Usergroup usergroup, User user) {
		
		return usergroups.remove(usergroup);
		
	}

}
