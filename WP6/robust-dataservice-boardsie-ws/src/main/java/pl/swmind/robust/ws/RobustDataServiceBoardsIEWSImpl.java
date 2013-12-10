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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.swmind.robust.dto.ForumDto;
import pl.swmind.robust.dto.PostDto;
import pl.swmind.robust.dto.UserDto;
import pl.swmind.robust.sioc.dao.ForumDao;
import pl.swmind.robust.sioc.dao.PostDao;
import pl.swmind.robust.sioc.dao.UserAccountDao;
import pl.swmind.robust.sioc.model.Forum;
import pl.swmind.robust.sioc.model.Post;
import pl.swmind.robust.sioc.model.UserAccount;

import javax.jws.WebService;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@WebService(endpointInterface = "pl.swmind.robust.ws.RobustDataServiceBoardsIEWS")
@Transactional
public class RobustDataServiceBoardsIEWSImpl implements RobustDataServiceBoardsIEWS {

    private final Logger log = Logger.getLogger(RobustDataServiceBoardsIEWS.class);
    @Autowired
    private PostDao postDao;
    @Autowired
    private UserAccountDao userAccountDao;
    @Autowired
    private ForumDao forumDao;

    @Override
    @Transactional
    public List<PostDto> getPostsByUris(Set<String> uris) {
        List<PostDto> postDtos = new LinkedList<PostDto>();

        if(uris == null || uris.isEmpty()){
            log.info("List of uris is null or empty.");
            return postDtos;
        }

        log.info("Getting posts for " + uris.size() + " uris...");
        for (String uri : uris){
            if (uri != null && !uri.isEmpty()){
                log.info("Getting post for uri: " + uri);
                Post result = postDao.findOne(uri);
                if (null != result){
                    PostDto postDto = mapSiocPost2PostDtoWithContent(result);
                    postDtos.add(postDto);
                }
            }
        }
        log.info("Posts found : " + postDtos.size());
        return postDtos;
    }

    @Override
    @Transactional
    public List<PostDto> getPostsByUserUri(String userUri){

        log.info("Looking for all posts of " + userUri + ".");

        if (null == userUri || userUri.isEmpty()){
            String msg = "User uris is empty or null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        List<PostDto> postDtos = new LinkedList<PostDto>();
        List<Post> postAll = postDao.findAll();

        for (Post post : postAll){
            if (post.getHasCreator().getUri().equals(userUri)){
                PostDto postDto = mapSiocPost2PostDto(post);
                postDtos.add(postDto);
            }
        }
        log.info("Get " +postDtos.size() +" posts from user "+ userUri + " complete" );
        return postDtos;
    }

    @Override
    @Transactional
    public List<PostDto> getPostsByUserName(String userName){
        log.info("Getting posts from user name: " + userName);

        if (null == userName || userName.isEmpty()){
            String msg = "Username is empty or null";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        List<PostDto> postDtos = new LinkedList<PostDto>();
        List<Post> postAll = postDao.findAll();
        log.info("Posts found for user" + userName + ": " + postAll.size());
        for (Post post : postAll){
            if (post.getHasCreator().getAccountName().equals(userName)){
                PostDto postDto = mapSiocPost2PostDto(post);
                postDtos.add(postDto);
            }
        }
        log.info("Getting posts from user " + userName + " completed.");
        return postDtos;
    }

    @Override
    @Transactional
    public List<ForumDto> getAllForums(){
        log.info("Getting all forums");
        List<ForumDto> forumDtoList = new LinkedList<ForumDto>();
        List<Forum> forumList = forumDao.findAll();

        for(Forum forum: forumList){
            ForumDto forumDto = mapSiocForum2ForumDto(forum);
            forumDtoList.add(forumDto);
        }
        log.info("Getting all forums completed");
        return  forumDtoList;
    }

    @Override
    @Transactional
    public UserDto getUserAccount(String userUri){

        if (null == userUri || userUri.isEmpty()){
            String msg = "UserUri is empty or null: '" + userUri +"'";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        log.info("Getting information about user with userUri: " + userUri);
        UserAccount userAccount = userAccountDao.findByUri(userUri);
        UserDto userDto = mapSiocUser2UserDto(userAccount);
        log.info("Getting information about user completed");
        return userDto;
    }

    @Override
    @Transactional
    public List<UserDto> getAllUserAccounts(){
        log.info("Getting all userAccounts...");
        List<UserDto> userDtos = new LinkedList<UserDto>();
        List<UserAccount> userAccountList = userAccountDao.findAll();

        for(UserAccount userAccount : userAccountList){
            UserDto userDto = mapSiocUser2UserDto(userAccount);
            userDtos.add(userDto);
        }
        log.info("Getting all userAccounts completed.");
        return userDtos;
    }

    @Override
    @Transactional
    public List<PostDto> getPostsByForumUri(String forumUri){
        log.info("Getting all posts for forum: " + forumUri);
        List<PostDto> postsDtoList = new LinkedList<PostDto>();
        List<Post> postsList = postDao.findAll();

        for(Post post : postsList){
            if(post.getPartOf().getUri().equals(forumUri)){
                PostDto postDto = mapSiocPost2PostDto(post);
                postsDtoList.add(postDto);
            }
        }
        log.info("Getting posts by forum uri completed");
        return postsDtoList;
    }

    private PostDto mapSiocPost2PostDto(Post post) {
        PostDto postDto = new PostDto();

        postDto.setUri(post.getUri());
        postDto.setCreateDate(post.getCreated().getTime());

        UserAccount userAccount = post.getHasCreator();
        postDto.setUserName(userAccount.getAccountName());
        postDto.setUserUri(userAccount.getUri());
        return postDto;
    }

    private PostDto mapSiocPost2PostDtoWithContent(Post post){
        PostDto postDto = mapSiocPost2PostDto(post);
        postDto.setContent(post.getContent());
        return postDto;
    }

    private ForumDto mapSiocForum2ForumDto(Forum forum) {
        ForumDto forumDto = new ForumDto();
        forumDto.setForumId(forum.getUriFromBigInt());
        forumDto.setForumTitle(forum.getTitle());
        return forumDto;
    }

    private UserDto mapSiocUser2UserDto(UserAccount userAccount) {
        UserDto userDto = new UserDto();
        userDto.setUserName(userAccount.getAccountName());
        userDto.setUserTitle(userAccount.getTitle());
        userDto.setUserUri(userAccount.getUri());
        userDto.setJoinDate(userAccount.getCreated().toString());
        return userDto;
    }
}