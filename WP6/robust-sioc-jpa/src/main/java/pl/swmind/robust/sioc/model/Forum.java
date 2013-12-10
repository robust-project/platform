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
@AttributeOverrides(
    @AttributeOverride(name="numItems", column = @Column(name="numThreads") )
)
public class Forum extends Container {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hasHostId")
    protected Site hasHost;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Moderator",
        joinColumns={@JoinColumn(name="forumId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="moderatorId", referencedColumnName = "id")}
    )
    protected List<UserAccount> hasModerator = new LinkedList<UserAccount>();

    public Site getHasHost() {
        return hasHost;
    }

    public void setHasHost(Site hasHost) {
        this.hasHost = hasHost;
    }

    public List<UserAccount> getHasModerator() {
        return hasModerator;
    }

    public void setHasModerator(List<UserAccount> hasModerator) {
        this.hasModerator = hasModerator;
    }
}
