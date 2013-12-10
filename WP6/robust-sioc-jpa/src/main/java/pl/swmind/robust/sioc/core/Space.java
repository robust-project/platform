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

import pl.swmind.robust.sioc.model.Community;
import pl.swmind.robust.sioc.model.UserGroup;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

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
    discriminatorType=DiscriminatorType.STRING
)
public abstract class Space extends DomainObject {

    @OneToMany(mappedBy = "hasSpace", fetch = FetchType.LAZY)
    protected List<Container> spaceOf = new LinkedList<Container>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isPartId")
    protected Community isPartOf;

    @OneToMany(mappedBy = "userGroupOf", fetch = FetchType.LAZY)
    protected List<UserGroup> hasUserGroup = new LinkedList<UserGroup>();

    public List<Container> getSpaceOf() {
        return spaceOf;
    }

    public Community getPartOf() {
        return isPartOf;
    }

    public void setPartOf(Community partOf) {
        isPartOf = partOf;
    }

    public void setSpaceOf(List<Container> spaceOf) {
        this.spaceOf = spaceOf;
    }

    public List<UserGroup> getHasUserGroup() {
        return hasUserGroup;
    }

    public void setHasUserGroup(List<UserGroup> hasUserGroup) {
        this.hasUserGroup = hasUserGroup;
    }
}
