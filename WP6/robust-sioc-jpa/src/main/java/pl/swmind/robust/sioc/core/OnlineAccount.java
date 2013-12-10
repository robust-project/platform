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

import pl.swmind.robust.sioc.model.Agent;

import javax.persistence.*;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 09/10/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="accountType",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class OnlineAccount extends DomainObject {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="accountOfId")
    protected Agent accountOf;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="accountServiceHomepageId")
    protected Item accountServiceHomepage;

    @Column
    protected String accountName;

    public Item getAccountServiceHomepage() {
        return accountServiceHomepage;
    }

    public void setAccountServiceHomepage(Item accountServiceHomepage) {
        this.accountServiceHomepage = accountServiceHomepage;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Agent getAccountOf() {
        return accountOf;
    }

    public void setAccountOf(Agent accountOf) {
        this.accountOf = accountOf;
    }
}
