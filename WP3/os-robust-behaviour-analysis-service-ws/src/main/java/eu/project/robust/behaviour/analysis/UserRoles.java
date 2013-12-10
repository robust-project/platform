/**
 *Copyright 2013 Knowledge Media Institute
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
package eu.project.robust.behaviour.analysis;

import java.util.Date;
import java.util.HashMap;

public class UserRoles {

    private String communityID;
    private Date date;
    private HashMap<String,Integer> userToRoleMap;

    public UserRoles(String communityID, Date date, HashMap<String, Integer> userToRoleMap) {
        this.communityID = communityID;
        this.date = date;
        this.userToRoleMap = userToRoleMap;
    }

    public UserRoles() {
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HashMap<String, Integer> getUserToRoleMap() {
        return userToRoleMap;
    }

    public void setUserToRoleMap(HashMap<String, Integer> userToRoleMap) {
        this.userToRoleMap = userToRoleMap;
    }

    @Override
    public String toString() {
        return "UserRoles{" +
                "communityID='" + communityID + '\'' +
                ", date=" + date +
                ", userToRoleMap=" + userToRoleMap +
                '}';
    }
}
