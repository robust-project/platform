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
import pl.swmind.robust.sioc.core.Container;

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
public class Role extends DomainObject {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hasPermissionId")
    protected Permission hasPermission;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="FunctionUser",
        joinColumns={@JoinColumn(name="functionId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="userId", referencedColumnName = "id")}
    )
    protected List<UserAccount> functionOfUser = new LinkedList<UserAccount>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "hasFunction")
    protected List<UserGroup> functionOfUserGroup = new LinkedList<UserGroup>();

    @ManyToMany(mappedBy = "scopeOf",fetch = FetchType.LAZY)
    protected List<Container> hasScope = new LinkedList<Container>();

    public Permission getHasPermission() {
        return hasPermission;
    }

    public void setHasPermission(Permission hasPermission) {
        this.hasPermission = hasPermission;
    }

    public List<UserAccount> getFunctionOfUser() {
        return functionOfUser;
    }

    public void setFunctionOfUser(List<UserAccount> functionOfUser) {
        this.functionOfUser = functionOfUser;
    }

    public List<Container> getHasScope() {
        return hasScope;
    }

    public void setHasScope(List<Container> hasScope) {
        this.hasScope = hasScope;
    }

    public List<UserGroup> getFunctionOfUserGroup() {
        return functionOfUserGroup;
    }

    public void setFunctionOfUserGroup(List<UserGroup> functionOfUserGroup) {
        this.functionOfUserGroup = functionOfUserGroup;
    }
}
