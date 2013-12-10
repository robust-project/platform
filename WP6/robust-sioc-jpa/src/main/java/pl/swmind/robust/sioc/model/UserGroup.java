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

import pl.swmind.robust.sioc.core.DomainObject;
import pl.swmind.robust.sioc.core.Space;

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
public class UserGroup extends DomainObject {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userGroupOfId")
    protected Space userGroupOf;

    @ManyToMany(mappedBy = "memberOf",fetch = FetchType.LAZY)
    protected List<UserAccount> hasMember = new LinkedList<UserAccount>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="FunctionUserGroup",
        joinColumns={@JoinColumn(name="userGroupId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="functionId", referencedColumnName = "id")}
    )
    protected List<Role> hasFunction = new LinkedList<Role>();

    public Space getUserGroupOf() {
        return userGroupOf;
    }

    public void setUserGroupOf(Space userGroupOf) {
        this.userGroupOf = userGroupOf;
    }

    public List<UserAccount> getHasMember() {
        return hasMember;
    }

    public void setHasMember(List<UserAccount> hasMember) {
        this.hasMember = hasMember;
    }

    public List<Role> getHasFunction() {
        return hasFunction;
    }

    public void setHasFunction(List<Role> hasFunction) {
        this.hasFunction = hasFunction;
    }
}
