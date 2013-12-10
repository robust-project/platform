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
package eu.project.robust.behaviour.corpora.Boardsie.analysis;

import eu.project.robust.behaviour.analysis.MicroCommunityRuleRunner;
import eu.project.robust.behaviour.analysis.UserRoles;
import eu.project.robust.behaviour.corpora.Boardsie.BoardsieDBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class BoardsieMicroCommunityRuleRunner extends MicroCommunityRuleRunner {


    public BoardsieMicroCommunityRuleRunner() throws Exception {

    }

    public Connection getConnection() throws Exception {
        return BoardsieDBConnection.getConnection();
    }

    @Override
    public UserRoles deriveComposition(String communityID, Date date) throws Exception {

        // load the default user roles
        UserRoles userRoles = new UserRoles();
        userRoles.setCommunityID(communityID);
        userRoles.setDate(date);

        HashMap<String,Integer> userToRoleID = new HashMap<String, Integer>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String parsedDate = sdf.format(date);

        // check to see if the roles have been stored for this time step already:
        String checkRecordSQL = "SELECT * FROM ou_roles_labelledusers WHERE " +
                "communityid='" + communityID + "' AND " +
                "collectdate='" + parsedDate + "'";


        Statement s = getConnection().createStatement();
        boolean contains = false;
        ResultSet checkRecordRes = s.executeQuery(checkRecordSQL);
        while(checkRecordRes.next()) {
            String userid = checkRecordRes.getString("userid");
            int roleid = checkRecordRes.getInt("roleid");
            //if(roleid != 0)
                userToRoleID.put(userid,roleid);

            contains = true;
        }
        s.close();

        if(contains) {
            userRoles.setUserToRoleMap(userToRoleID);
        }// if the roles are not stored trow an exception. Note that we could also take the
          //desing decision of returning the closest date for which there is data
        else {
            throw new Exception("composition not pre-computed for the community " + communityID + " on date " + date);
        }

        return userRoles;
    }

    @Override
    public TreeMap<Date, UserRoles> deriveTimeSeriesComposition(String communityID, Date startDate, Date endDate) throws Exception {

        // get the time steps that fall within the start and end dates
        String getInnerTimesteps = "SELECT collectdate FROM ou_roles_labelledusers WHERE " +
                "communityid='" + communityID + "' AND " +
                "collectdate >= '" + new Timestamp(startDate.getTime()) + "' AND " +
                "collectdate <= '" + new Timestamp(endDate.getTime()) + "'";
        Statement statement = getConnection().createStatement();
        ResultSet innerTimestepRes = statement.executeQuery(getInnerTimesteps);
        TreeSet<Date> timesteps = new TreeSet<Date>();
        while(innerTimestepRes.next()) {
            Date date = innerTimestepRes.getDate("collectdate");
            timesteps.add(date);
        }
        statement.close();

        TreeMap<Date,UserRoles> tsUserRoles = new TreeMap<Date, UserRoles>();
        for (Date timestep : timesteps) {
            UserRoles userRoles = this.deriveComposition(communityID,timestep);
            tsUserRoles.put(timestep,userRoles);
        }

        return tsUserRoles;
    }

    @Override
    public void close() throws Exception {
        //this.connection.close();
    }


}
