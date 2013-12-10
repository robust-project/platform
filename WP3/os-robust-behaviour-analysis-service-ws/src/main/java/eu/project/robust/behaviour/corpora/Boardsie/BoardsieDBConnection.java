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
package eu.project.robust.behaviour.corpora.Boardsie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class BoardsieDBConnection {

    private static Connection connection;

    public static Connection getConnection() throws Exception{

        if(connection == null){
            return openConnection();
        }
        if(!connection.isValid(5)){
            return openConnection();
        }
        return connection;

    }

    private static Connection openConnection() throws Exception {
        Properties properties = new Properties();
        properties.load(BoardsieDBConnection.class.getResourceAsStream("/boardsie-db.properties"));
        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        connection = DriverManager.getConnection(properties.getProperty("dbURL") + properties.getProperty("dbNAME"),
                properties.getProperty("dbUSER"),
                properties.getProperty("dbPASS"));

        return connection;
    }

    public static void close() throws Exception {
       connection.close();
    }

    public static HashMap<String, String> getCommunityIDs() throws Exception {
        HashMap<String, String> communityIDSet = new HashMap<String, String>();
        //String query = "select forumid, title from forums limit 25";
        String query = "select forumid, title from forums limit 100";
        Statement s1 = getConnection().createStatement();
        ResultSet resultSet = s1.executeQuery(query);
        while(resultSet.next()){
            String forumID    = resultSet.getString("forumid");
            String forumTitle = resultSet.getString("title");
            communityIDSet.put(forumID, forumTitle);
        }
        return communityIDSet;
    }


    public static Set<String> getUserIDs(String communityID, int numUsers) throws Exception {

        Set<String> userSet = new HashSet<String>();
        String query = "select distinct(userid) from ou_roles_dimensionstats where forumuri ='" + communityID + "' limit " + numUsers;
        Statement s = getConnection().createStatement();
        ResultSet resultSet = s.executeQuery(query);
        while(resultSet.next()){
            userSet.add(resultSet.getString("userid"));
        }
        return userSet;
    }
}