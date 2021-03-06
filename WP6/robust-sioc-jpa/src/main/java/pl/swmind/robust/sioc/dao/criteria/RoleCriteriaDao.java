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

import org.springframework.stereotype.Component;
import pl.swmind.robust.sioc.dao.RoleDao;
import pl.swmind.robust.sioc.model.Role;
import pl.swmind.robust.sioc.utils.CriteriaDao;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 22/03/13<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Component
public class RoleCriteriaDao extends CriteriaDao<Role> implements RoleDao {
    public RoleCriteriaDao(){
        super(Role.class);
    }
    protected RoleCriteriaDao(Class<Role> clazz) {
        super(clazz);
    }
}
