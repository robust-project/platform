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
package pl.swmind.robust.dto;


import java.util.Date;

public class PostDto {

    private String uri;
    private String userName;
    private String userUri;
    private String content;
    private long createDate;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets createDate in long (date.getTime())
     * @param createDate
     */
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserUri(String userUri) {
        this.userUri = userUri;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUri() {
        return userUri;
    }

    public String getUri() {
        return uri;
    }

    public String getContent() {
        return content;
    }

    /**
     * Gets createDate in long (date.getTime())
     * @return
     */
    public long getCreateDate() {
        return createDate;
    }
}
