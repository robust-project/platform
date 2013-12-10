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

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import pl.swmind.robust.sioc.core.Container;
import pl.swmind.robust.sioc.dao.ThreadDao;
import pl.swmind.robust.sioc.model.Thread;
import pl.swmind.robust.sioc.utils.CriteriaDao;

import java.util.Date;
import java.util.List;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 22/03/13<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Component
public class ThreadCriteriaDao extends CriteriaDao<Thread> implements ThreadDao {
    public ThreadCriteriaDao(){
        super(Thread.class);
    }
    protected ThreadCriteriaDao(Class<Thread> clazz) {
        super(clazz);
    }

    @Override
    public List<Thread> findByHasParent(Container parent) {
        return getCriteriaFor(Thread.class)
            .add(Restrictions.eq("hasParent", parent))
            .list();
    }

    @Override
    public Long countByDistinctHasParentAndIsContainerOfBetweenPosteddate(String userId, Date starDate, Date endDate) {
        return (Long) getCriteriaFor(Thread.class)
            // TODO
//            .createAlias("containerOf", "p", CriteriaSpecification.INNER_JOIN)
//            .createAlias("p.hasCreator", "u", CriteriaSpecification.INNER_JOIN)
//            .add(Restrictions.eq("u." + getUriName(), userId))
//            .add(Restrictions.between("p.created", starDate,endDate))
            .setProjection(Projections.countDistinct("hasParent"))
            .uniqueResult();
    }
}
