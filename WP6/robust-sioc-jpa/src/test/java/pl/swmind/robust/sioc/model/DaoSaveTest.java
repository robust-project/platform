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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import pl.swmind.robust.sioc.common.SpringEnabled;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.dao.UserAccountDao;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: rafal
 * Date: 19.11.12
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
public class DaoSaveTest extends SpringEnabled {

    @Resource(name = "postSpringDao")
    private PostDao postDao;

    @Resource(name = "userAccountSpringDao")
    private UserAccountDao userAccountDao;

    private static final String URI_1 = "testUri1";
    private static final String URI_2 = "testUri2";
    private static final String URI_3 = "testUri3";
    private static final String URI_4 = "testUri4";
    private static final String URI_5 = "testUri4";
    private static final String CONTENT = "content";
    private static final String ACCOUNT_NAME = "name";

    @Test
    public void saveAndGetDtoTest(){
        Post post = new Post();
        post.setUri(URI_5);
        post.setContent(CONTENT);
        postDao.save(post);

        Post result = postDao.findByUri(URI_5);
        assertNotNull(result);
        assertEquals(CONTENT, result.getContent());
        assertEquals(URI_5, result.getUri());

        postDao.delete(post);
    }


    @Test(expected = DataIntegrityViolationException.class )
    @Transactional
    public void saveDtoTwiceTest(){
        Post post = new Post();
        post.setUri(URI_2);
        postDao.save(post);

        post = new Post();
        post.setUri(URI_2);
        postDao.save(post);

        postDao.delete(post);
    }

    @Test
    @Transactional
    public void saveDtoTwiceAndWorkAfterTest(){

        Post post = new Post();
        post.setUri(URI_3);
        postDao.save(post);


        Post postCopy = new Post();
        postCopy.setUri(URI_3);
        try{
            postDao.save(postCopy);
        } catch(Exception e){
            assertTrue(e instanceof DataIntegrityViolationException);
        }

        testStoreNextPost();

        postDao.delete(post);
    }

    @Test
    @Transactional
    public void testStoreNextPost() {
        Post post;
        post = new Post();
        post.setUri(URI_4);
        post.setContent(CONTENT);

        UserAccount userAccount = new UserAccount();
        userAccount.setUri(URI_4+"user");
        userAccount.setAccountName(ACCOUNT_NAME);
        userAccountDao.save(userAccount);
        post.setHasCreator(userAccount);

        postDao.save(post);
        postDao.delete(post);
        userAccountDao.delete(userAccount);
    }



}
