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
import org.springframework.transaction.annotation.Transactional;
import pl.swmind.robust.sioc.model.Forum;
import pl.swmind.robust.sioc.utils.GenericDao;

import java.util.List;


@Transactional
public interface ForumDao extends GenericDao<Forum> {

    @Query("select distinct(f.uri) from Forum f")
    List<Forum> findAllByDistinctUri();
}
