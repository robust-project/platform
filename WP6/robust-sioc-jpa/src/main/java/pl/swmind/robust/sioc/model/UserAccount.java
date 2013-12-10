/** 
*Copyright 2013 Software Mind SA
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
package pl.swmind.robust.sioc.model;

import pl.swmind.robust.sioc.core.Container;
import pl.swmind.robust.sioc.core.Item;
import pl.swmind.robust.sioc.core.OnlineAccount;
import pl.swmind.robust.sioc.core.Space;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * SIOC to JPA mapped class of user in community. <br>
 * <p/>
 * Creation date: 17/08/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Entity
public class UserAccount extends OnlineAccount {
    @Column
    protected String note;

    @Column
    protected String title;
    
    @Column
    protected Double reputationScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isPartId")
    protected Community isPartOf;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Subscriber",
        joinColumns={@JoinColumn(name="subscriberId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="containerId", referencedColumnName = "id")}
    )
    protected List<Container> subscriberOf = new LinkedList<Container>();

    @ManyToMany(mappedBy = "hasModerator", fetch = FetchType.LAZY)
    protected List<Forum> moderatorOf = new LinkedList<Forum>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Administrator",
        joinColumns={@JoinColumn(name="administratorId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="siteId", referencedColumnName = "id")}
    )
    protected List<Site> administratorOf = new LinkedList<Site>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Membership",
        joinColumns={@JoinColumn(name="memberId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="userGroupId", referencedColumnName = "id")}
    )
    protected List<UserGroup> memberOf = new LinkedList<UserGroup>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="FunctionUserAccount",
        joinColumns={@JoinColumn(name="userId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="functionId", referencedColumnName = "id")}
    )
    protected List<Role> hasFunction = new LinkedList<Role>();

    @Column
    protected String emailSHA1;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="User_User_following",
        joinColumns={@JoinColumn(name="followerId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="followedId", referencedColumnName = "id")}
    )
    protected List<UserAccount> follows = new LinkedList<UserAccount>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="CreationSpace",
        joinColumns={@JoinColumn(name="creatorId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="spaceId", referencedColumnName = "id")}
    )
    protected List<Space> creatorOfSpaces = new LinkedList<Space>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="OwnershipSpace",
        joinColumns={@JoinColumn(name="ownerId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="spaceId", referencedColumnName = "id")}
    )
    protected List<Space> ownerOfSpaces = new LinkedList<Space>();


    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="CreationContainer",
        joinColumns={@JoinColumn(name="creatorId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="containerId", referencedColumnName = "id")}
    )
    protected List<Container> creatorOfContainers = new LinkedList<Container>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="OwnershipContainer",
        joinColumns={@JoinColumn(name="ownerId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="containerId", referencedColumnName = "id")}
    )
    protected List<Container> ownerOfContainers = new LinkedList<Container>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="CreationItem",
        joinColumns={@JoinColumn(name="creatorId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="itemId", referencedColumnName = "id")}
    )
    protected List<Item> creatorOfItems = new LinkedList<Item>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="OwnershipItem",
        joinColumns={@JoinColumn(name="ownerId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="itemId", referencedColumnName = "id")}
    )
    protected List<Item> ownerOfItems = new LinkedList<Item>();

    @Column
    protected String avatar;

    @Column
    protected String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Modification",
        joinColumns={@JoinColumn(name="modifierId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="PostId", referencedColumnName = "id")}
    )
    protected List<Item> modifierOf = new LinkedList<Item>();

    public Double getReputationScore() {
        return reputationScore;
    }

    public void setReputationScore(Double reputationScore) {
        this.reputationScore = reputationScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Agent getAccountOf() {
        return accountOf;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAccountOf(Agent accountOf) {
        this.accountOf = accountOf;
    }

    public Community getPartOf() {
        return isPartOf;
    }

    public void setPartOf(Community partOf) {
        isPartOf = partOf;
    }

    public List<Container> getSubscriberOf() {
        return subscriberOf;
    }

    public void setSubscriberOf(List<Container> subscriberOf) {
        this.subscriberOf = subscriberOf;
    }

    public List<Forum> getModeratorOf() {
        return moderatorOf;
    }

    public List<Container> getCreatorOfContainers() {
        return creatorOfContainers;
    }

    public void setCreatorOfContainers(List<Container> creatorOfContainers) {
        this.creatorOfContainers = creatorOfContainers;
    }

    public List<Container> getOwnerOfContainers() {
        return ownerOfContainers;
    }

    public void setOwnerOfContainers(List<Container> ownerOfContainers) {
        this.ownerOfContainers = ownerOfContainers;
    }

    public List<Item> getCreatorOfItems() {
        return creatorOfItems;
    }

    public void setCreatorOfItems(List<Item> creatorOfItems) {
        this.creatorOfItems = creatorOfItems;
    }

    public List<Item> getOwnerOfItems() {
        return ownerOfItems;
    }

    public void setOwnerOfItems(List<Item> ownerOfItems) {
        this.ownerOfItems = ownerOfItems;
    }

    public void setModeratorOf(List<Forum> moderatorOf) {
        this.moderatorOf = moderatorOf;
    }

    public List<Site> getAdministratorOf() {
        return administratorOf;
    }

    public void setAdministratorOf(List<Site> administratorOf) {
        this.administratorOf = administratorOf;
    }

    public List<UserGroup> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(List<UserGroup> memberOf) {
        this.memberOf = memberOf;
    }

    public List<Role> getHasFunction() {
        return hasFunction;
    }

    public void setHasFunction(List<Role> hasFunction) {
        this.hasFunction = hasFunction;
    }

    public String getEmailSHA1() {
        return emailSHA1;
    }

    public void setEmailSHA1(String emailSHA1) {
        this.emailSHA1 = emailSHA1;
    }

    public List<UserAccount> getFollows() {
        return follows;
    }

    public void setFollows(List<UserAccount> follows) {
        this.follows = follows;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Item> getModifierOf() {
        return modifierOf;
    }

    public void setModifierOf(List<Item> modifierOf) {
        this.modifierOf = modifierOf;
    }

    public List<Space> getCreatorOfSpaces() {
        return creatorOfSpaces;
    }

    public void setCreatorOfSpaces(List<Space> creatorOfSpaces) {
        this.creatorOfSpaces = creatorOfSpaces;
    }

    public List<Space> getOwnerOfSpaces() {
        return ownerOfSpaces;
    }

    public void setOwnerOfSpaces(List<Space> ownerOfSpaces) {
        this.ownerOfSpaces = ownerOfSpaces;
    }
}










