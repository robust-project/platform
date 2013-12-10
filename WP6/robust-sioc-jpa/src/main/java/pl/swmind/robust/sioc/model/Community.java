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
import pl.swmind.robust.sioc.core.DomainObject;
import pl.swmind.robust.sioc.core.Item;
import pl.swmind.robust.sioc.core.Space;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
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
public class Community extends DomainObject {

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "isPartOf")
    protected List<Space> hasSpacePart = new LinkedList<Space>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "isPartOf")
    protected List<Container> hasContainerPart = new LinkedList<Container>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "isPartOf")
    protected List<Item> hasItemPart = new LinkedList<Item>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "isPartOf")
    protected List<UserAccount> hasUserAccountPart = new LinkedList<UserAccount>();

    public List<UserAccount> getHasUserAccountPart() {
        return hasUserAccountPart;
    }

    public void setHasUserAccountPart(List<UserAccount> hasUserAccountPart) {
        this.hasUserAccountPart = hasUserAccountPart;
    }

    public List<Space> getHasSpacePart() {
        return hasSpacePart;
    }

    public void setHasSpacePart(List<Space> hasSpacePart) {
        this.hasSpacePart = hasSpacePart;
    }

    public List<Container> getHasContainerPart() {
        return hasContainerPart;
    }

    public void setHasContainerPart(List<Container> hasContainerPart) {
        this.hasContainerPart = hasContainerPart;
    }

    public List<Item> getHasItemPart() {
        return hasItemPart;
    }

    public void setHasItemPart(List<Item> hasItemPart) {
        this.hasItemPart = hasItemPart;
    }
}
