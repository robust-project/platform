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
package pl.swmind.robust.sioc.dao;

import org.springframework.data.jpa.repository.Query;
import pl.swmind.robust.sioc.model.Post;
import pl.swmind.robust.sioc.utils.GenericDao;

import java.util.Date;
import java.util.List;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 21/03/13<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public interface PostDao extends GenericDao<Post> {
    List<Post> findByHasContainer(pl.swmind.robust.sioc.model.Thread thread);
    List<Post> findByCreatedBetween(Date start, Date end);

    @Query("select count(distinct p.hasContainer) from Post p")
    Long countByDistinctHasContainer();


//    @Query("select count(distinct p.hasContainer) from Post p")
    @Query("select count(*) from Post p where p.hasCreator.uri = ?1 and p.hasContainer.hasParent.uri = ?2 and p.created between ?3 and ?4")
    Long countByHasCreatorAndHasContainerHasParentCreatedBetween(String userAccountUri, String forumUri, Date start, Date end);
}
