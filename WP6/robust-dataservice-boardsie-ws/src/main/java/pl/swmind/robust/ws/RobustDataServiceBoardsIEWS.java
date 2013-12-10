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

import pl.swmind.robust.dto.ForumDto;
import pl.swmind.robust.dto.PostDto;
import pl.swmind.robust.dto.UserDto;

import javax.jws.WebService;
import java.util.List;
import java.util.Set;


/**
 * Robust Data Service BoardsIE
 */
@WebService
public interface RobustDataServiceBoardsIEWS {

    List<PostDto> getPostsByUris(Set<String> uris);

    List<PostDto> getPostsByUserUri(String userUri);

    List<PostDto> getPostsByUserName(String userName);

    List<ForumDto> getAllForums();

    UserDto getUserAccount(String userUri);

    List<UserDto> getAllUserAccounts();

    List<PostDto> getPostsByForumUri(String forumUri);
}