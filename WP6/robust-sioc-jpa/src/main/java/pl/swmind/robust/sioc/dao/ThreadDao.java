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
import pl.swmind.robust.sioc.core.Container;
import pl.swmind.robust.sioc.model.Thread;
import pl.swmind.robust.sioc.utils.GenericDao;

import java.util.Date;
import java.util.List;


public interface ThreadDao extends GenericDao<Thread> {
    public List<Thread> findByHasParent(Container parent);

    @Query("select count(distinct t.hasParent) from Thread t join t.containerOf p where p.hasCreator.uri = :userId and p.created between :startDate and :endDate")
    public Long countByDistinctHasParentAndIsContainerOfBetweenPosteddate(String userId, Date starDate, Date endDate);
}
