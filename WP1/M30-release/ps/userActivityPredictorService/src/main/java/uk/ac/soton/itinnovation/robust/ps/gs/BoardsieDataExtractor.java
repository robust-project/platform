/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Vegard Engen
//      Created Date :          2013-04-12
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.ps.gs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.swmind.robust.sioc.dao.PostDao;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;


/**
 * A class to extract snapshots of user activity on the Boards.ie data via
 * Software Mind's Boards.ie JPA project.
 * 
 * @author Vegard Engen
 */
@Component
public class BoardsieDataExtractor
{
    @Autowired
    private PostDao postDAO;
    
    // the root directory with the forum data files, assumed to be in the form
    //   forum_<ID>.csv
    String forumDataDir;
    String defaultForumDataDir = "./forumdata/";
    
    static Logger log = Logger.getLogger(BoardsieDataExtractor.class);
    
    public BoardsieDataExtractor()
    {
        getConfigs();
    }
    
    /**
     * Gets configuration properties from 'service.properties' on the class
     * path.
     */
    private void getConfigs() {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("service.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
            return;
        }
        
        try {
            forumDataDir = prop.getProperty("forumDataDir");
        } catch (Exception ex) {
            log.warn("Error getting and parsing 'forumDataDir' parameter from service.properties. " + ex.getMessage(), ex);
            log.warn("Will use default value");
            forumDataDir = defaultForumDataDir;
            //return;
        }
        log.info("Forum data dir:  " + forumDataDir);
    }
    
    /**
     * Get snapshot data for the users' activity on the given forum for the time
     * period specified (according to the end date and the number of snapshots
     * prior to this we should get).
     * @param forumID The forum ID.
     * @param endDate The end date of the data snapshots that should be collected.
     * @param freq The frequency type of the snapshot lengths (e.g., WEEKLY)
     * @param numSnapshots The number of snapshots to collect.
     * @return A Map of user IDs and the key is a list specifying the number of posts
     *         each user made within each respective snapshot.
     * @throws Exception on any errors with either the configuration parameters,
     * reading forum user data from file or querying database.
     */
    public Map<String, List<Number>> getSnapshotDataMap(String forumID, Date endDate, FrequencyType freq, int numSnapshots) throws Exception
    {
        log.info("Getting historical snapshot data");
        
        if (forumID == null) {
            throw new IllegalArgumentException("The given forum ID was null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("The given end date was null");
        }
        if (freq == null) {
            throw new IllegalArgumentException("The given frequency type was null");
        }
        if (numSnapshots < 3) {
            throw new IllegalArgumentException("The given number of snapshots was " + numSnapshots + ", which must be 3 or greater");
        }
        
        Map<String, List<Number>> snapshotDataMap = new HashMap<String, List<Number>>();
        
        // get the start/from date
        Date startDate = DateUtil.getFromDate(endDate, freq, numSnapshots);
        
        log.info(" - forumID: " + forumID);
        log.info(" - startDate: " + startDate);
        log.info(" - endDate: " + endDate);
        log.info(" - numSnapshots: " + numSnapshots);
        
        // get users
        Map<String, Date> usersJoinDatesMap = getForumUserDetails(forumID);
        log.debug("Got " + usersJoinDatesMap.size() + " users who have posted in forum " + forumID + "");
//        for (String userID : usersJoinDatesMap.keySet()) {
//            log.debug(" - " + userID + ": " + usersJoinDatesMap.get(userID));
//        }
        
        // get filtered user map according to their join dates
        Map<String, Date> filteredUserDateMap = getFilteredForumUserDetails(usersJoinDatesMap, endDate);
        log.debug("Got " + filteredUserDateMap.size() + " users remaining after filtering out those who had not actually joined the community yet");
        usersJoinDatesMap.clear();
        usersJoinDatesMap = null;
        
        // initialise snapshotDataMap
        log.debug("Initialising the snapshot data map");
        if (filteredUserDateMap != null)
        {
            for (String userID : filteredUserDateMap.keySet()) {
                if (userID != null) {
                    snapshotDataMap.put(userID, new ArrayList<Number>());
                }
            }
        }
        
        filteredUserDateMap.clear();
        filteredUserDateMap = null;
        
        // iterate over each date period to get posts for each user
        log.debug("Getting data for each user for the " + numSnapshots + " snapshots");
        for (String userID : snapshotDataMap.keySet())
        {
            List<Number> postList = snapshotDataMap.get(userID);
            Date fromDate = new Date(startDate.getTime());
//            log.debug(" - User: " + userID);
            for (int i = 0; i < numSnapshots; i++)
            {
                Date toDate = DateUtil.getToDate(fromDate, FrequencyType.WEEKLY);
                Long numPosts = postDAO.countByHasCreatorAndHasContainerHasParentCreatedBetween(userID, forumID, fromDate, toDate);
                
                if (numPosts != null) {
                    postList.add(numPosts);
//                    log.debug("   - " + DateUtil.getDateString(fromDate) + " - " + DateUtil.getDateString(toDate) + ": " + numPosts);
                } else {
                    log.warn("The number of posts returned from the SMIND Boards.ie JPA was NULL for user " + userID + " for snapshot " + DateUtil.getDateString(fromDate) + " - " + DateUtil.getDateString(toDate));
                    log.warn("Adding a 0 value to the snapshot data map");
                    postList.add(0);
                }
                
                fromDate = toDate;
            }
        }
        
        return snapshotDataMap;
    }
    
    /**
     * Filters out the users who joined after the given date.
     * @param usersJoinDatesMap A Map where the key is the user ID and the value is their join date.
     * @param date The date filtering should be done according to.
     * @return A new Map where the key is the user ID and the value is their join date.
     */
    private Map<String, Date> getFilteredForumUserDetails(Map<String, Date> usersJoinDatesMap, Date date)
    {
        Map<String, Date> filteredUserDateMap = new HashMap<String, Date>();
        log.debug("Filtering out users who joined after " + date);
        for (String userID : usersJoinDatesMap.keySet())
        {
            if (usersJoinDatesMap.get(userID).before(date) || usersJoinDatesMap.get(userID).equals(date)) {
                filteredUserDateMap.put(userID, usersJoinDatesMap.get(userID));
            }
        }
        
        return filteredUserDateMap;
    }
    
    /**
     * Get a map of user IDs and their join dates, as read from a file that
     * is assumed to be named as 'forum_<ID>' where <ID> is replaced by the 
     * forumID given as a parameter. The method will look for this file in the
     * forumDataDir that should be specified as a parameter in the service.properties
     * file.
     * @return Map of user IDs and their join dates
     */
    public Map<String, Date> getForumUserDetails(String forumID) throws Exception
    {
        if (forumID == null) {
            throw new IllegalArgumentException("The forum ID given is NULL - cannot read the file for the users in the forum and their join dates then...");
        }
        
        log.info("Getting user data for people who contributed to forum " + forumID);
        
        String filename = forumDataDir + "/forum_" + forumID + ".csv";
        
        File f = null;
        
        try {
            f = new File(filename);
            
            if (!f.isFile()) {
                log.error("Not a valid file: " + f.getAbsolutePath());
                throw new IllegalArgumentException("Not a valid file: " + f.getAbsolutePath());
            }
        } catch (Exception ex) {
            throw new RuntimeException("The filename given is invalid: " + filename, ex);
        }
        
        Map<String, Date> userDateMap = new HashMap<String, Date>();
        //DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(f));

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] lineParts = line.split(",");
                String userID = lineParts[0];
                Date joinDate = (Date)formatter.parse(lineParts[1]);
                userDateMap.put(userID, joinDate);
            }
            br.close();
        } catch (IOException ex) {
            log.error("Unable to process the file: " + f.getAbsolutePath(), ex);
            throw new RuntimeException("Unable to process the file: " + f.getAbsolutePath(), ex);
        } catch (ParseException ex) {
            log.error("Unable to parse the date: " + ex, ex);
            throw new RuntimeException("Unable to parse a join date for a user in the forum data file " + f.getAbsolutePath() + ": " + ex, ex);
        }
        
        return userDateMap;
    }
}
