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

import org.hibernate.annotations.Index;
import pl.swmind.robust.sioc.model.UserAccount;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 05/09/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@MappedSuperclass
public abstract class DomainObject{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(unique = true)
    @Index(name = "uriIndex" )
    protected String uri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hasCreatorId")
    protected UserAccount hasCreator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hasOwnerId")
    protected UserAccount hasOwner;

    @Column
    protected Date created;

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (this.getClass().equals(other.getClass())) {
            return this.hashCode() == other.hashCode();
        } else {
            return false;
        }
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserAccount getHasOwner() {
        return hasOwner;
    }

    public void setHasOwner(UserAccount hasOwner) {
        this.hasOwner = hasOwner;
    }

    public UserAccount getHasCreator() {
        return hasCreator;
    }

    public void setHasCreator(UserAccount hasCreator) {
        this.hasCreator = hasCreator;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUriFromInt(Integer uri){
        this.uri = uri.toString();
    }

    public Integer getUriFromInt(){
        return Integer.parseInt(uri);
    }

    public void setUriFromBigInt(Long uri){
        this.uri = uri.toString();
    }

    public Long getUriFromBigInt(){
        return Long.parseLong(uri);
    }


    public void setUri(String uri) {
        this.uri = uri;
    }
}
