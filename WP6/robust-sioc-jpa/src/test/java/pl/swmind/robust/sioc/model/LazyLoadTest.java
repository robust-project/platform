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

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import pl.swmind.robust.sioc.common.SpringEnabled;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.dao.ThreadDao;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 22/03/13<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */

public class LazyLoadTest extends SpringEnabled {

    @Resource(name = "threadSpringDao")
    private ThreadDao threadDao;

    @Resource(name = "postSpringDao")
    private PostDao postDao;

    @Test
    @Transactional
    public void shouldLazyLoad() {
        pl.swmind.robust.sioc.model.Thread thread = new Thread();
        thread.setUri("threadUri");
        threadDao.save(thread);

        Post post = new Post();
        post.setUri("postUri");
        post.setHasContainer(thread);
        postDao.save(post);

        Post gotPost = postDao.findByUri("postUri");

        assertEquals(thread.getUri(), gotPost.getHasContainer().getUri());

        postDao.delete(post);
        threadDao.delete(thread);
    }
}
