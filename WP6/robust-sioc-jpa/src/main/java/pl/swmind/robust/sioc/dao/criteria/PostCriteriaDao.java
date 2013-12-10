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
package pl.swmind.robust.sioc.dao.criteria;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Component;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.model.Post;
import pl.swmind.robust.sioc.utils.CriteriaDao;

import java.util.Date;
import java.util.List;

import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.between;
import static org.hibernate.criterion.Restrictions.eq;

@Component
public class PostCriteriaDao extends CriteriaDao<Post> implements PostDao {
    public PostCriteriaDao(){
        super(Post.class);
    }

    protected PostCriteriaDao(Class<Post> clazz) {
        super(clazz);
    }

    public List<Post> findByHasContainer(pl.swmind.robust.sioc.model.Thread thread){
        return getCriteriaFor(Post.class)
            .add(eq("hasContainer", thread))
            .list();
    }

    @Override
    public Long countByDistinctHasContainer() {
        return (Long) getCriteriaFor(Post.class)
            .setProjection(Projections.countDistinct("hasContainer"))
            .uniqueResult();
    }

    @Override
    public Long countByHasCreatorAndHasContainerHasParentCreatedBetween(String userAccountUri, String forumUri, Date start, Date end) {
        Criterion userUriRestriction = getUriRestriction("u.", userAccountUri);
        Criterion forumUriRestriction = getUriRestriction("f.", forumUri);

        return (Long) getCriteriaFor(Post.class)
            .createAlias("hasCreator","u")
            .createAlias("hasContainer","t")
            .createAlias("t.hasParent","f")
            .add(userUriRestriction)
            .add(forumUriRestriction)
            .add(between("created",start,end))
            .setProjection(rowCount())
            .uniqueResult();
    }
}
