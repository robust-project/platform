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
package pl.swmind.robust.ws;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.swmind.robust.dto.PostDto;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.model.Post;
import pl.swmind.robust.sioc.model.UserAccount;

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
//TODO wszystkie metody pwoinny byc przetestowane, dodatkowo ich charakterystyczne zachowania - np sprawdzenie

@ContextConfiguration(locations={"classpath:dataservice-ws-test-context.xml"})
public class RobustDataServiceBoardsIEWSTest extends AbstractTestNGSpringContextTests {

    private static final String USER_1_URI = "1233";

    private static final String POST_1_URI = "4234543";
    private static final String POST_2_URI = "5234543";
    private static final String POST_3_URI = "6234543";
    private static final String POST_4_URI = "8234543";
    private static final String UNEXISTING_POST = "999999";

    private static final String USER_1_NAME = "madonna";
    private static final String USER_2_NAME = "joseph34";

    private static final String POST_1_CONTENT = "content1";

    @Autowired
    PostDao postDao;

    @Autowired
    RobustDataServiceBoardsIEWS robustDataServiceWS;

    @BeforeClass
    public void setUp(){
        MockitoAnnotations.initMocks( this );
        assertNotNull(robustDataServiceWS);
    }

    private List<Post> getExampleListOfPosts(){
        List<Post> list = new LinkedList<Post>();
        list.add(createPost2());
        list.add(createPost3());
        list.add(createPost4());
        list.add(createPost5());
        return list;
    }

    private UserAccount createUserMadonna(){
        UserAccount madonna = new UserAccount();
        madonna.setAccountName(USER_1_NAME);
        return madonna;
    }

    private UserAccount createUserJoseph34(){
        UserAccount joseph = new UserAccount();
        joseph.setAccountName(USER_2_NAME);

        return joseph;
    }

    private PostDto createPost1() {
        PostDto post = new PostDto();
        post.setUri(USER_1_URI);
        post.setCreateDate(new Date().getTime());
        post.setUserName(USER_1_NAME);
        post.setUserUri(POST_1_URI);
        post.setContent(POST_1_CONTENT);
        return post;
    }

    private Post createPost2(){
        Post post = new Post();
        post.setUri(POST_2_URI);
        post.setHasCreator(createUserMadonna());
        return post;
    }

    private Post createPost3(){
        Post post = new Post();
        post.setUri(POST_3_URI);
        post.setHasCreator(createUserJoseph34());
        return post;
    }

    private Post createPost4(){
        Post post = new Post();
        post.setUri(POST_3_URI);
        post.setHasCreator(createUserMadonna());
        return post;
    }

    private Post createPost5(){
        Post post = new Post();
        post.setUri(POST_4_URI);
        post.setHasCreator(createUserMadonna());
        return post;
    }

    @Test
    public void shouldNonFoundTestPost() throws Exception {
        PostDto post = createPost1();
        Set<String> uris = new HashSet<String>();
        uris.add(post.getUri());
        assertEquals(robustDataServiceWS.getPostsByUris(uris).size(),0);
    }

    @Test
    public void testGetUnExistingPosts(){
        Set set = new HashSet<String>();
        set.add(UNEXISTING_POST);
        assertNotNull(robustDataServiceWS.getPostsByUris(set));
    }

//    @Test
//    public void testGetPostByUserName(){
//        postDao = mock(PostDao.class);
//
//        when(postDao.findAll()).thenReturn(getExampleListOfPosts());
//        ReflectionTestUtils.setField(robustDataServiceWS, "postDao", postDao);
//        assertEquals(robustDataServiceWS.getPostsByUserName("madonna").size(), 3);
//    }

//    Make test after made userAccountDao mock
//    @Test
//    public void testGetUserById(){
//        assertNotSame(robustDataServiceWS.getUserAccount("323").getUserName(),"maddona");
//    }

//    Make test after made userAccountDao mock
//    @Test
//    public void testGetPostByUserId(){
//        List <PostDto> userPosts;
//        userPosts = robustDataServiceWS.getPostsByUserUri("313");
//        logger.info("Number of user Posts: " + userPosts.size() );
//        for (PostDto postDto : userPosts){
//            logger.info("Post id: " + postDto.getUri());
//        }
//    }


//    Make test after made forumDao mock
//    @Test
//    public void testGetAllForums(){
//        List<ForumDto> forumDtos= robustDataServiceWS.getAllForums();
//        for (ForumDto forumDto : forumDtos){
//            logger.info("Forum name: " + forumDto.getForumTitle());
//            logger.info("Forum id: " + forumDto.getForumId());
//        }
//    }
}
