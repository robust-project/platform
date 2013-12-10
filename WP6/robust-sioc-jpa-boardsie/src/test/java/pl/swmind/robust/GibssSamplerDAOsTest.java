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
package pl.swmind.robust;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.swmind.robust.sioc.dao.ForumDao;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.dao.ThreadDao;
import pl.swmind.robust.sioc.dao.UserAccountDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 29/03/13<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class GibssSamplerDAOsTest  extends SpringEnabled{
    private @Autowired PostDao postDao;
    private @Autowired ForumDao forumDao;
    private @Autowired ThreadDao threadDao;
    private @Autowired UserAccountDao userDao;

    @Test
    public void shouldGetDistinctForums(){
        assertEquals(9, forumDao.findAllByDistinctUri().size());
    }

    @Test
    public void shouldCountPostsWithDistinctParent(){
        assertEquals(new Long(8), postDao.countByDistinctHasContainer());
    }

    @Test
    public void shouldCountForumsByUserAndTimeWindow() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormatter.parse("2000-04-24");
        Date endDate = dateFormatter.parse("2002-04-24");
        assertEquals(new Long(3), threadDao.countByDistinctHasParentAndIsContainerOfBetweenPosteddate("2680", startDate, endDate));
    }

    @Test
    public void shouldCountPostsByUserForumAndDate() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH");
        Date startDate = dateFormatter.parse("2001-05-04 00");
        Date endDate = dateFormatter.parse("2001-05-05 22");
        String userUri = "313";
        String forumUri = "15";

        assertEquals(new Long(1), postDao.countByHasCreatorAndHasContainerHasParentCreatedBetween(userUri,forumUri,startDate,endDate));
    }
}
