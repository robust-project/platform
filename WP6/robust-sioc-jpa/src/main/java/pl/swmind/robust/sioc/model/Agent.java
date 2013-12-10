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
import pl.swmind.robust.sioc.core.OnlineAccount;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 09/10/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Entity
public class Agent extends DomainObject{
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weblogId")
    protected Item weblog;

    @Column
    protected String icqChatId;

    @Column
    protected String msnChatId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "accountOf")
    protected OnlineAccount account;

    @Column
    protected int age;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mboxId")
    protected Container mbox;

    @Column
    protected String mboxSha1sum;

    @Column
    protected String yahooChatId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipjarId")
    protected Item tipjar;

    @Column
    protected String jabberId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "openId")
    protected Item openId;

    @Column
    protected String gender;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="Interest",
        joinColumns={@JoinColumn(name="agentId", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="interestId", referencedColumnName = "id")}
    )
    protected List<Item> interest = new LinkedList<Item>();

    @Column
    protected String aimChatId;

    @Column
    protected Date birthday;

    @Column
    protected String skypeId;

    public Item getWeblog() {
        return weblog;
    }

    public void setWeblog(Item weblog) {
        this.weblog = weblog;
    }

    public String getIcqChatId() {
        return icqChatId;
    }

    public void setIcqChatId(String icqChatId) {
        this.icqChatId = icqChatId;
    }

    public String getMsnChatId() {
        return msnChatId;
    }

    public void setMsnChatId(String msnChatId) {
        this.msnChatId = msnChatId;
    }

    public OnlineAccount getAccount() {
        return account;
    }

    public void setAccount(OnlineAccount account) {
        this.account = account;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Container getMbox() {
        return mbox;
    }

    public void setMbox(Container mbox) {
        this.mbox = mbox;
    }

    public String getMboxSha1sum() {
        return mboxSha1sum;
    }

    public void setMboxSha1sum(String mboxSha1sum) {
        this.mboxSha1sum = mboxSha1sum;
    }

    public String getYahooChatId() {
        return yahooChatId;
    }

    public void setYahooChatId(String yahooChatId) {
        this.yahooChatId = yahooChatId;
    }

    public Item getTipjar() {
        return tipjar;
    }

    public void setTipjar(Item tipjar) {
        this.tipjar = tipjar;
    }

    public String getJabberId() {
        return jabberId;
    }

    public void setJabberId(String jabberId) {
        this.jabberId = jabberId;
    }

    public Item getOpenId() {
        return openId;
    }

    public void setOpenId(Item openId) {
        this.openId = openId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Item> getInterest() {
        return interest;
    }

    public void setInterest(List<Item> interest) {
        this.interest = interest;
    }

    public String getAimChatId() {
        return aimChatId;
    }

    public void setAimChatId(String aimChatId) {
        this.aimChatId = aimChatId;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSkypeId() {
        return skypeId;
    }

    public void setSkypeId(String skypeId) {
        this.skypeId = skypeId;
    }
}
