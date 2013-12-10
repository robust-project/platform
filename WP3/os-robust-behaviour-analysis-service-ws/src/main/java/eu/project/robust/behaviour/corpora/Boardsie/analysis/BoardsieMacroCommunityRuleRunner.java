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

import eu.project.robust.behaviour.analysis.Composition;
import eu.project.robust.behaviour.analysis.MacroCommunityRuleRunner;
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

public class BoardsieMacroCommunityRuleRunner extends MacroCommunityRuleRunner {

    private static String INIT_DATE_PRECOMPUTED_ROLES = "2005-02-13";

    public BoardsieMacroCommunityRuleRunner() throws Exception {
    }

    public Connection getConnection() throws Exception {
        return BoardsieDBConnection.getConnection();
    }

    @Override
    public Composition deriveComposition(String communityID, Date date) throws Exception {

        System.out.println("derive macro composition");

        // load the default composition
        Composition composition = new Composition();
        composition.setCommunityID(communityID);
        composition.setDate(date);
        composition.setIdToRoleMapping(new BoardsieRuleSetBuilder().getIDToRoleMapping());

        HashMap<Integer,Double> roleCoverage = new HashMap<Integer, Double>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String parsedDate = sdf.format(date);
        String checkRecordSQL = "SELECT * FROM ou_roles_compositions WHERE " +
                "communityid='" + communityID + "' AND " +
                "collectdate='" + parsedDate + "'";
        System.out.println(checkRecordSQL);

        Statement s = getConnection().createStatement();
        ResultSet checkRecordRes = s.executeQuery(checkRecordSQL);
        boolean contains = false;
        while(checkRecordRes.next()) {
            int roleID = checkRecordRes.getInt("roleid");
            double rolePerc = checkRecordRes.getDouble("coverage");
            roleCoverage.put(roleID,rolePerc);
            contains = true;
        }
        s.close();

        // if the role composition has been stored for this time step then return it
        if(contains) {
            composition.setRoleCoverage(roleCoverage);

        // if not return an exception
        } else {
            throw new Exception("composition not pre-computed for the community " + communityID + " on date " + date);
        }

        return composition;
    }

    @Override
    public TreeMap<Date, Composition> deriveTimeSeriesComposition(String communityID, Date startDate, Date endDate) throws Exception {

        // get the time steps that fall within the start and end dates
        String getInnerTimesteps = "SELECT collectdate FROM ou_roles_compositions WHERE " +
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

        TreeMap<Date,Composition> tsCompositions = new TreeMap<Date, Composition>();
        for (Date timestep : timesteps) {
            Composition composition = this.deriveComposition(communityID,timestep);
            tsCompositions.put(timestep,composition);
        }

        return tsCompositions;
    }

    @Override
    public int getRoleCount(String communityid, int roleid, Date date) throws Exception {

        BoardsieMicroCommunityRuleRunner boardsieMicroCommunityRuleRunner = new BoardsieMicroCommunityRuleRunner();
        UserRoles userRoles = boardsieMicroCommunityRuleRunner.deriveComposition(communityid,date);
        boardsieMicroCommunityRuleRunner.close();

        HashMap<Integer,Double> roleTallies = new HashMap<Integer, Double>();
        for (String userid : userRoles.getUserToRoleMap().keySet()) {
            int roleID = userRoles.getUserToRoleMap().get(userid);
            if(roleTallies.containsKey(roleID)) {
                double tally = roleTallies.get(roleID);
                tally++;
                roleTallies.put(roleID,tally);
            } else {
                roleTallies.put(roleID,1.0);
            }
        }

        int roleCount = 0;
        if(roleTallies.containsKey(roleid))
            roleCount = roleTallies.get(roleid).intValue();

        return roleCount;
    }

    @Override
    public double getRoleProportion(String communityid, int role, Date date) throws Exception {
        Composition composition = this.deriveComposition(communityid,date);
        double roleProp = 0;
        if(composition.getRoleCoverage().containsKey(role))
            roleProp = composition.getRoleCoverage().get(role);

        return roleProp;
    }

    @Override
    public TreeMap<Date, Integer> getRolePathOfUser(String communityid, String userid, Date endDate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(INIT_DATE_PRECOMPUTED_ROLES);

        BoardsieMicroCommunityRuleRunner boardsieMicroCommunityRuleRunner = new BoardsieMicroCommunityRuleRunner();
        TreeMap<Date,UserRoles> tsUserRoles = boardsieMicroCommunityRuleRunner.deriveTimeSeriesComposition(communityid, startDate, endDate);
        TreeMap<Date, Integer> rolePath = new TreeMap<Date, Integer>();

        for (Date ts : tsUserRoles.keySet()) {
            UserRoles userRoles = tsUserRoles.get(ts);

            // get the role of the user from the user Roles
            if(userRoles.getUserToRoleMap().containsKey(userid)) {
                int roleid = userRoles.getUserToRoleMap().get(userid);
                rolePath.put(ts,roleid);
            } else {
                rolePath.put(ts,0);
            }
        }
        return rolePath;
    }

    public TreeMap<Date, Integer> getTSRolePathOfUser(String communityid, String userid, Date startDate, Date endDate) throws Exception {

        BoardsieMicroCommunityRuleRunner boardsieMicroCommunityRuleRunner = new BoardsieMicroCommunityRuleRunner();
        TreeMap<Date,UserRoles> tsUserRoles = boardsieMicroCommunityRuleRunner.deriveTimeSeriesComposition(communityid, startDate, endDate);
        TreeMap<Date, Integer> rolePath     = new TreeMap<Date, Integer>();

        for (Date ts : tsUserRoles.keySet()) {
            UserRoles userRoles = tsUserRoles.get(ts);

            // get the role of the user from the user Roles
            if(userRoles.getUserToRoleMap().containsKey(userid)) {
                int roleid = userRoles.getUserToRoleMap().get(userid);
                rolePath.put(ts,roleid);
            } else {
                rolePath.put(ts,0);
            }
        }
        return rolePath;
    }


    @Override
    public void close() throws Exception {
        //this.connection.close();
    }



    public static void main(String[] args) {
        try {
            System.out.print("geting roles along time");
            // get the roles for each community's users
            String platformID = "Boardsie";
            String communityID = "224";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse("2007-01-01");
            Date endDate = sdf.parse("2008-01-01");
            String userID = "40542";

            BoardsieMacroCommunityRuleRunner boardsieMacroCommunityRuleRunner = new BoardsieMacroCommunityRuleRunner();

            TreeMap<Date, Integer> userpath = boardsieMacroCommunityRuleRunner.getTSRolePathOfUser(communityID,userID,startDate,endDate);
            for(Date date:userpath.descendingKeySet()){
                System.out.println(date);
                System.out.println(userpath.get(date));
            }

            boardsieMacroCommunityRuleRunner.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
