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

import pl.swmind.robust.sioc.core.Space;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * SIOC to JPA mapped class of Twitter site <br>
 * <p/>
 * Creation date: 17/08/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Entity
public class Site extends Space {

    @OneToMany(mappedBy = "hasHost", fetch = FetchType.LAZY)
    protected List<Forum> hostOf = new LinkedList<Forum>();

    @ManyToMany(mappedBy = "administratorOf", fetch = FetchType.LAZY)
    protected List<UserAccount> hasAdministrator = new LinkedList<UserAccount>();

    public List<Forum> getHostOf() {
        return hostOf;
    }

    public void setHostOf(List<Forum> hostOf) {
        this.hostOf = hostOf;
    }

    public List<UserAccount> getHasAdministrator() {
        return hasAdministrator;
    }

    public void setHasAdministrator(List<UserAccount> hasAdministrator) {
        this.hasAdministrator = hasAdministrator;
    }

    public Community getPartOf() {
        return isPartOf;
    }

    public void setPartOf(Community partOf) {
        isPartOf = partOf;
    }
}
