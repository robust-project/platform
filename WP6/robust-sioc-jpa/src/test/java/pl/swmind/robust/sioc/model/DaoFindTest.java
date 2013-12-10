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
package pl.swmind.robust.sioc.model;


import junit.framework.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.swmind.robust.sioc.common.ParameterizedEnabled;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.dao.ThreadDao;
import pl.swmind.robust.sioc.dao.criteria.PostCriteriaDao;
import pl.swmind.robust.sioc.dao.criteria.ThreadCriteriaDao;
import pl.swmind.robust.sioc.dao.spring.PostSpringDao;
import pl.swmind.robust.sioc.dao.spring.ThreadSpringDao;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class DaoFindTest extends ParameterizedEnabled {
    private PostDao postDao;
    private ThreadDao threadDao;

    private static PostDao postSpringDao;
    private static ThreadDao threadSpringDao;

    private static final String URI_1 = "find_testUri1";
    private static final String URI_2 = "find_testUri2";
    private static final String URI_3 = "find_testUri3";

    private Date date1 = new Date();
    private Date date2 = new Date(date1.getTime() + 1000);
    private Date date3 = new Date(date2.getTime() + 1000);

    private Post post1 = new Post();
    private Post post2 = new Post();
    private Post post3 = new Post();

    public DaoFindTest(PostDao postDao, ThreadDao threadDao) {
        this.postDao = postDao;
        this.threadDao = threadDao;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/sioc-jpa-test-context.xml");

        PostDao postSpringDao = ctx.getBean("postSpringDao", PostSpringDao.class);
        DaoFindTest.postSpringDao = postSpringDao;
        PostDao postCriteriaDao = ctx.getBean("postCriteriaDao", PostCriteriaDao.class);
        ThreadDao threadSpringDao = ctx.getBean("threadSpringDao", ThreadSpringDao.class);
        DaoFindTest.threadSpringDao = threadSpringDao;
        ThreadDao threadCriteriaDao = ctx.getBean("threadCriteriaDao", ThreadCriteriaDao.class);

        return Arrays.asList(
            new Object[]{postSpringDao,threadSpringDao},
            new Object[]{postCriteriaDao,threadCriteriaDao}
        );
    }

    @Test
    public void shouldJoiningWork() {
        Thread thread1 = new Thread();
        thread1.setUri("threadUri1");
        threadSpringDao.save(thread1);

        Thread thread2 = new Thread();
        thread2.setUri("threadUri2");
        threadSpringDao.save(thread2);


        Post post1 = new Post();
        post1.setUri("postUri1");
        post1.setHasContainer(thread1);
        postSpringDao.save(post1);


        Post post2 = new Post();
        post2.setUri("postUri2");
        post2.setHasContainer(thread2);
        postSpringDao.save(post2);


        List<Post> gotPosts = postDao.findByHasContainer(thread1);

        Assert.assertEquals(1, gotPosts.size());
        Assert.assertEquals("postUri1", gotPosts.get(0).getUri());

        postSpringDao.delete(Arrays.asList(new Post[]{post1,post2}));
        threadSpringDao.delete(Arrays.asList(new Thread[]{thread1,thread2}));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void storeAndFindByDateTest(){
        setPostDTO(post1, URI_1, date1);
        setPostDTO(post2, URI_2, date2);
        setPostDTO(post3, URI_3, date3);

        List<Post> result = postDao.findByCreatedBetween(date1, date2);
        assertNotNull(result);
        assertEquals(2, result.size());

        result = postDao.findByCreatedBetween(date1, date3);
        assertNotNull(result);
        assertEquals(3, result.size());

        result = postDao.findByCreatedBetween(date1, date1);
        assertNotNull(result);
        assertEquals(1, result.size());

        postSpringDao.deleteInBatch(Arrays.asList(new Post[]{post1,post2,post3}));
    }

    private void setPostDTO(Post post, String uri, Date date) {
        post.setUri(uri);
        post.setCreated(date);
        postSpringDao.save(post);
    }
}
