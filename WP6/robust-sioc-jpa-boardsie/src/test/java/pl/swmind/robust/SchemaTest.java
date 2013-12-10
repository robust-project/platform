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

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.swmind.robust.sioc.core.Container;
import pl.swmind.robust.sioc.dao.ForumDao;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.dao.ThreadDao;
import pl.swmind.robust.sioc.dao.UserAccountDao;
import pl.swmind.robust.sioc.model.Forum;
import pl.swmind.robust.sioc.model.Post;
import pl.swmind.robust.sioc.model.UserAccount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityNotFoundException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 24/09/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"/META-INF/spring/jpa-boards-context.xml"})
@ContextConfiguration(locations={"/jpa-boards-context-test.xml"})
public class SchemaTest {
    private Logger logger = Logger.getLogger(SchemaTest.class);

    private @Autowired PostDao postDao;
    private @Autowired ForumDao forumDao;
    private @Autowired ThreadDao threadDao;
    private @Autowired UserAccountDao userDao;

    @Test
    public void shouldReadUser() throws NoSuchFieldException, IllegalAccessException {
        UserAccount user = userDao.findOne("2680");
        Assert.assertNotNull(user);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format( user.getCreated() );
        Assert.assertEquals( "2006-12-24",date );
        Assert.assertEquals( "2680",user.getUri() );
        Assert.assertNull( user.getReputationScore() );

    }

    @Test
    public void shouldReadForum() throws NoSuchFieldException, IllegalAccessException {
        Forum forum = forumDao.findOne("15");
        Assert.assertNotNull(forum);
        Assert.assertEquals( "15",forum.getUri() );
        Assert.assertEquals( "Test",forum.getTitle() );
    }

    @Test
    public void shouldReadThread() throws NoSuchFieldException, IllegalAccessException {
        pl.swmind.robust.sioc.model.Thread thread = threadDao.findOne("17048");
        Assert.assertNotNull(thread);
        Container parent = thread.getHasParent();
        assertEquals( "17048",thread.getUri() );
        Assert.assertEquals( "Test message",thread.getTitle() );
        Assert.assertNull( thread.getHasStatus() );
        Assert.assertEquals( new Double(6.0).doubleValue(), thread.getNoOfViews().doubleValue(),0.00000000000000000001 );
        Assert.assertEquals( "15",parent.getUri() );
        Assert.assertEquals( "Test",parent.getTitle() );
       }


    @Test
    public void shouldReadPost() throws NoSuchFieldException, IllegalAccessException {
        Post post = postDao.findOne("3");
        assertNotNull(post);
        Assert.assertEquals( "3",post.getUri() );
        Assert.assertEquals( "17048",post.getHasContainer().getUri() );
        Assert.assertEquals( "Test post",post.getTitle() );
        Assert.assertEquals( "2680",post.getHasCreator().getUri() );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format( post.getCreated() );
        Assert.assertEquals( "2001-04-24",date );

        Assert.assertNull( post.getAttachment() );
        Assert.assertNull( post.getAwardedPoints() );
        Assert.assertEquals( "138495",post.getReplyOf().getUri() );

        Assert.assertEquals( 1,post.getHasReply().size() );

    }

    @Test
    public void shouldPostsBeLeftJoined(){
        long postsAmount = postDao.count();
        assertEquals(19, postsAmount);

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldGetEmptyPOJOFromNonExistingEntity(){
        Post post = postDao.findByUri("26");
        assertNotNull(post);

        assertEquals(post.getAbout().size(), 0);
        assertNotNull(post.getHasContainer());
        post.getHasContainer().getUri();

    }

    @Test
    public void shouldFindByUri() throws Exception {
        assertNotNull(postDao.findByUri("3"));
    }


}
