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
import org.springframework.beans.factory.annotation.Autowired;
import pl.swmind.robust.sioc.core.Container;
import pl.swmind.robust.sioc.dao.ForumDao;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.dao.ThreadDao;
import pl.swmind.robust.sioc.dao.UserAccountDao;
import pl.swmind.robust.sioc.model.Forum;
import pl.swmind.robust.sioc.model.Post;
import pl.swmind.robust.sioc.model.UserAccount;

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
public class ReadingTest extends SpringEnabled{
    private Logger logger = Logger.getLogger(ReadingTest.class);

    private @Autowired PostDao postDao;
    private @Autowired ForumDao forumDao;
    private @Autowired ThreadDao threadDao;
    private @Autowired UserAccountDao userDao;

    @Test
    public void shouldReadUser() throws NoSuchFieldException, IllegalAccessException {
        UserAccount user = userDao.findOne("2680");
        Assert.assertNotNull(user);
        logger.info(user.getCreated() + ", " + user.getUri() + ", " + user.getReputationScore());
    }

    @Test
    public void shouldReadForum() throws NoSuchFieldException, IllegalAccessException {
        Forum forum = forumDao.findOne("15");
        Assert.assertNotNull(forum);
        logger.info(forum.getUri() + ", " + forum.getTitle());
    }

    @Test
    public void shouldReadThread() throws NoSuchFieldException, IllegalAccessException {
        pl.swmind.robust.sioc.model.Thread thread = threadDao.findOne("17048");
        Assert.assertNotNull(thread);
        Container parent = thread.getHasParent();
        logger.info(thread.getUri() + ", " + thread.getTitle() + ", " + thread.getHasStatus() + ", " + thread.getNoOfViews() + ", " + parent.getUri() + ", " + parent.getTitle());
    }


    @Test
    public void shouldReadPost() throws NoSuchFieldException, IllegalAccessException {
        Post post = postDao.findOne("3");
        assertNotNull(post);
        logger.info(post.getUri() + ", " + post.getHasContainer().getUri() + ", " + post.getTitle() + ", " +
            post.getHasCreator().getUri() + ", " + post.getCreated() + ", " + post.getContent() + ", " +
            post.getAttachment() + ", " + post.getAwardedPoints() + ", " + post.getReplyOf().getUri() + ", " + post.getHasReply().size());
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
