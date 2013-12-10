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

import org.apache.commons.io.IOUtils;
import pl.swmind.robust.sioc.model.Community;
import pl.swmind.robust.sioc.model.Feature;
import pl.swmind.robust.sioc.model.Tag;
import pl.swmind.robust.sioc.model.UserAccount;

import javax.persistence.*;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
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
    name="itemType",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class Item extends DomainObject {
    private static final String ENCODING = "UTF-8";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hasContainerId")
    protected Container hasContainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isPartId")
    protected Community isPartOf;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Sibling",
        joinColumns={@JoinColumn(name="firstItemId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="secondItemId", referencedColumnName = "id")}
    )
    protected List<Item> sibling = new LinkedList<Item>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replyOfId")
    protected Item replyOf;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Reply",
        joinColumns={@JoinColumn(name="replyId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="originalId", referencedColumnName = "id")}
    )
    protected Set<Item> hasReply = new HashSet<Item>();

    @ManyToMany(mappedBy = "modifierOf", fetch = FetchType.LAZY)
    protected List<UserAccount> hasModifier = new LinkedList<UserAccount>();

    @Column
    protected Integer awardedPoints;

    @Column
    protected String content;

    @Column
    protected String ipAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nextByDateId")
    protected Item nextByDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previousByDateId")
    protected Item previousByDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previousVersionId")
    protected Item previousVersion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nextVersionId")
    protected Item nextVersion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="latestVersionId")
    protected Item latestVersion;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="ItemTopic",
        joinColumns={@JoinColumn(name="ItemId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="tagId", referencedColumnName = "id")}
    )
    protected List<Tag> topic = new LinkedList<Tag>();

    @Column
    protected String title;

    @Column
    protected Date modified;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="About",
        joinColumns={@JoinColumn(name="itemId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="featureId", referencedColumnName = "id")}
    )
    protected List<Feature> about = new LinkedList<Feature>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="addressedToId")
    protected UserAccount addressedTo;

    @Column
    protected String attachment;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Discussion",
        joinColumns={@JoinColumn(name="itemId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="containerId", referencedColumnName = "id")}
    )
    protected List<Container> hasDiscussion = new LinkedList<Container>();

    public Community getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(Community partOf) {
        isPartOf = partOf;
    }

    public Integer getAwardedPoints() {
        return awardedPoints;
    }

    public void setAwardedPoints(Integer awardedPoints) {
        this.awardedPoints = awardedPoints;
    }

    public Container getHasContainer() {
        return hasContainer;
    }

    public void setHasContainer(Container hasContainer) {
        this.hasContainer = hasContainer;
    }

    public List<Item> getSibling() {
        return sibling;
    }

    public void setSibling(List<Item> sibling) {
        this.sibling = sibling;
    }

    public Item getReplyOf() {
        return replyOf;
    }

    public void setReplyOf(Item replyOf) {
        this.replyOf = replyOf;
    }

    public Set<Item> getHasReply() {
        return hasReply;
    }

    public void setHasReply(Set<Item> hasReply) {
        this.hasReply = hasReply;
    }

    public Community getPartOf() {
        return isPartOf;
    }

    public void setPartOf(Community partOf) {
        isPartOf = partOf;
    }

    public List<UserAccount> getHasModifier() {
        return hasModifier;
    }

    public void setHasModifier(List<UserAccount> hasModifier) {
        this.hasModifier = hasModifier;
    }

    public Item getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Item latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Blob getContentFromBlob() throws SQLException {
        Blob blob = new SerialBlob(content.getBytes());
        return blob;
    }

    public void setContentFromBlob(Blob content) throws SQLException, IOException {
        InputStream inStream = content.getBinaryStream();
        this.content = IOUtils.toString(inStream, ENCODING);
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Item getNextByDate() {
        return nextByDate;
    }

    public void setNextByDate(Item nextByDate) {
        this.nextByDate = nextByDate;
    }

    public Item getPreviousByDate() {
        return previousByDate;
    }

    public void setPreviousByDate(Item previousByDate) {
        this.previousByDate = previousByDate;
    }

    public Item getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(Item previousVersion) {
        this.previousVersion = previousVersion;
    }

    public Item getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(Item nextVersion) {
        this.nextVersion = nextVersion;
    }

    public List<Tag> getTopic() {
        return topic;
    }

    public void setTopic(List<Tag> topic) {
        this.topic = topic;
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

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public List<Feature> getAbout() {
        return about;
    }

    public void setAbout(List<Feature> about) {
        this.about = about;
    }

    public UserAccount getAddressedTo() {
        return addressedTo;
    }

    public void setAddressedTo(UserAccount addressedTo) {
        this.addressedTo = addressedTo;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public List<Container> getHasDiscussion() {
        return hasDiscussion;
    }

    public void setHasDiscussion(List<Container> hasDiscussion) {
        this.hasDiscussion = hasDiscussion;
    }
}
