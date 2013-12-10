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

public class Composition {

    private String communityID;
    private Date date;
    private HashMap<Integer,Double> roleCoverage;
    private HashMap<Integer,String> idToRoleMapping;

    public Composition(String communityID, Date date, HashMap<Integer, Double> roleCoverage, HashMap<Integer, String> idToRoleMapping) {
        this.communityID = communityID;
        this.date = date;
        this.roleCoverage = roleCoverage;
        this.idToRoleMapping = idToRoleMapping;
    }

    public Composition() {
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

    public HashMap<Integer, Double> getRoleCoverage() {
        return roleCoverage;
    }

    public void setRoleCoverage(HashMap<Integer, Double> roleCoverage) {
        this.roleCoverage = roleCoverage;
    }

    public HashMap<Integer, String> getIdToRoleMapping() {
        return idToRoleMapping;
    }

    public void setIdToRoleMapping(HashMap<Integer, String> idToRoleMapping) {
        this.idToRoleMapping = idToRoleMapping;
    }

    @Override
    public String toString() {
        return "" +
                "" + communityID + '\'' +
                ", date=" + date +
                ", roleCoverage=" + roleCoverage +
                ", idToRoleMapping=" + idToRoleMapping +
                '}';
    }

}
