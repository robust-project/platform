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
package pl.swmind.robust.sioc.core;

import pl.swmind.robust.sioc.model.*;

import javax.persistence.*;
import java.util.*;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 03/10/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="containerType",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class Container extends DomainObject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hasSpaceId")
    protected Space hasSpace;

    @Column
    protected String hasStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isPartId")
    protected Community isPartOf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hasParentId")
    protected Container hasParent;

    @OneToMany(mappedBy = "hasParent", fetch = FetchType.LAZY)
    protected List<Container> parentOf = new LinkedList<Container>();

    @Column
    protected Double noOfViews;

    @Column
    protected Integer numItems;

    @ManyToMany(mappedBy = "subscriberOf", fetch = FetchType.LAZY)
    protected List<UserAccount> hasSubscriber = new LinkedList<UserAccount>();

    @OneToMany(mappedBy = "hasContainer", fetch = FetchType.LAZY)
    protected List<Item> containerOf = new LinkedList<Item>();

    @Column
    protected Date lastItemDate;

    @Column
    protected String title;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="ContainerTopic",
        joinColumns={@JoinColumn(name="ItemId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="tagId", referencedColumnName = "id")}
    )
    protected List<Tag> topic = new LinkedList<Tag>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Scope",
        joinColumns={@JoinColumn(name="containerId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="roleId", referencedColumnName = "id")}
    )
    private List<Role> scopeOf = new LinkedList<Role>();

    public Community getPartOf() {
        return isPartOf;
    }

    public void setPartOf(Community partOf) {
        isPartOf = partOf;
    }

    public List<Role> getScopeOf() {
        return scopeOf;
    }

    public void setScopeOf(List<Role> scopeOf) {
        this.scopeOf = scopeOf;
    }

    public String getHasStatus() {
        return hasStatus;
    }

    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }

    public Container getHasParent() {
        return hasParent;
    }

    public void setHasParent(Container hasParent) {
        this.hasParent = hasParent;
    }

    public Double getNoOfViews() {
        return noOfViews;
    }

    public void setNoOfViews(Double noOfViews) {
        this.noOfViews = noOfViews;
    }

    public Integer getNoOfViewsFromInt() {
        return noOfViews.intValue();
    }

    public void setNoOfViewsFromInt(Integer noOfViews) {
        this.noOfViews = noOfViews.doubleValue();
    }

    public Integer getNumItems() {
        return numItems;
    }

    public void setNumItems(Integer numItems) {
        this.numItems = numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public List<UserAccount> getHasSubscriber() {
        return hasSubscriber;
    }

    public void setHasSubscriber(List<UserAccount> hasSubscriber) {
        this.hasSubscriber = hasSubscriber;
    }

    public Date getLastItemDate() {
        return lastItemDate;
    }

    public void setLastItemDate(Date lastItemDate) {
        this.lastItemDate = lastItemDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<Tag> getTopic() {
        return topic;
    }

    public void setTopic(List<Tag> topic) {
        this.topic = topic;
    }

    public Space getHasSpace() {
        return hasSpace;
    }

    public void setHasSpace(Space hasSpace) {
        this.hasSpace = hasSpace;
    }

    public List<Container> getParentOf() {
        return parentOf;
    }

    public void setParentOf(List<Container> parentOf) {
        this.parentOf = parentOf;
    }

    public List<Item> getContainerOf() {
        return containerOf;
    }

    public void setContainerOf(List<Item> containerOf) {
        this.containerOf = containerOf;
    }
}
