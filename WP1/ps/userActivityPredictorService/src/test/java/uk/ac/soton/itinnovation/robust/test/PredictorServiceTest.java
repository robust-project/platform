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
//      Created for Project :   
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.test;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.ps.CommunityDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.IPredictorService;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.ps.gs.BoardsieDataExtractor;

/**
 *
 * @author Vegard Engen
 */
@ContextConfiguration(locations={"classpath:ps-test-context.xml"})
public class PredictorServiceTest extends AbstractTestNGSpringContextTests
{
    @Autowired
    IPredictorService ps;
    
    @Autowired
    BoardsieDataExtractor dataExtractor;
    
    // needed for the PS job configuration testing
    private static final String eeServiceURI = "http://localhost:8080/evaluationEngineService-1.5/service";
    
    static Logger log = Logger.getLogger(PredictorServiceTest.class);
    
    @BeforeClass
    public void setUp()
    {
        log.info("Checking PS not NULL");
        assertNotNull(ps);
        
        log.info("Checking BoardsieDataExtractor not NULL");
        assertNotNull(dataExtractor);
    }
    
    @Test
    public void testGetSnapshotData()
    {
        int numSnapshots = 10;
//        String forumID = "7"; // forum 1 has 7 users; forum 53 has 33 users
        String forumID = "224"; 
        Date endDate = null;
        
        try {
            //endDate = DateUtil.getDateObject("2001-05-30"); // 2003-05-07 is the date of first posts in forum 53
            endDate = DateUtil.getDateObject("2007-04-09"); // 2003-02-02 is the only date where forum 1 had posts
        } catch (Exception ex) { }
        
        Map<String, List<Number>> snapshotData = null;
        
        try {
            snapshotData = dataExtractor.getSnapshotDataMap(forumID, endDate, FrequencyType.WEEKLY, numSnapshots);
        } catch (Exception ex) {}
        
        assertNotNull(snapshotData);
        
        // check that we got 24 users (should be filtered from 33 according to the date given above)
        //assertTrue (snapshotData.size() == 24, "Should have filtered " + snapshotData.size() + " users down to 24");
        
        // check that each user has 3 snapshots worth of data
        boolean all3snapshots = true;
        log.info("Snapshot data map:");
        for (String userID : snapshotData.keySet())
        {
            if (snapshotData.get(userID).size() != 3) {
                all3snapshots = false;
            }
            String outputString = userID + ":";
            for (Number num : snapshotData.get(userID)) {
                outputString += "  " + num;
            }
            log.info(" - " + outputString);
        }
        assertTrue(all3snapshots, "Not all users had 3 snapshots worth of data!");
    }
    
    
    @Test
    public void testPredictorService()
    {
        log.info("Testing the Predictor Service - creating an evaluation job");
        int numSnapshots = 10;
//        String forumID = "7"; // forum 1 has 7 users; forum 53 has 33 users
        String forumID = "224"; 
        Date startDate = null;
        
        try {
            //endDate = DateUtil.getDateObject("2001-05-30"); // 2003-05-07 is the date of first posts in forum 53
//            startDate = DateUtil.getDateObject("2003-02-09"); // 2003-02-02 is the only date where forum 1 had posts
            startDate = DateUtil.getDateObject("2007-04-09");
        } catch (Exception ex) { }
        
        PredictorServiceDescription psDesc = ps.getPredictorServiceDescription();
        
        log.info("Creating job configuration object based on available events and configuration parameters");
        PredictorServiceJobConfig jobConfig = null;
        
        try {
            jobConfig = getPredictorServiceJobConfig(psDesc, startDate, forumID, numSnapshots);
        } catch (Exception ex) {}
        
        log.info("Creating a new evaluation job");
        JobDetails jobDetails = ps.createEvaluationJob(jobConfig);
        
        log.info("Job reference: " + jobDetails.getJobRef());
        log.info("Job status: " + jobDetails.getJobStatus().getStatus());

        if ((jobDetails.getJobStatus().getStatus() == JobStatusType.FAILED) ||
            (jobDetails.getJobStatus().getStatus() == JobStatusType.ERROR))
        {
            log.error("ERROR MSG: " + jobDetails.getJobStatus().getMetaData());
            return;
        }
        
        log.info("Starting evaluation job: " + jobDetails.getJobRef());
        JobStatus jobStatus = ps.evaluate(jobDetails.getJobRef());
        log.info("Job status: " + jobStatus.getStatus());
        
        if ((jobStatus.getStatus() == JobStatusType.FAILED) ||
            (jobStatus.getStatus() == JobStatusType.ERROR))
        {
            log.error("ERROR MSG: " + jobStatus.getMetaData());
            return;
        }
        
        boolean polling = true;
        while (polling)
        {
            log.info("Checking status of job: " + jobDetails.getJobRef());
            jobStatus = ps.getJobStatus(jobDetails.getJobRef());
            log.info("Job status: " + jobStatus.getStatus());
            
            if (jobStatus.getStatus() == JobStatusType.FINISHED)
            {
                EvaluationResult evaluationResult = ps.getEvaluationResult(jobDetails.getJobRef());
                if (evaluationResult == null)
                    log.error("evaluationResult == null");
                else if (evaluationResult.getForecastDate() == null)
                    log.error("evaluationResult.getForecastDate() == null");
                log.info("Results from running job with id " + jobDetails.getJobRef() + ":");
                log.info(" - Forecast date: " + DateUtil.getDateString(evaluationResult.getForecastDate()));

                List<ResultItem> resultItems = evaluationResult.getResultItems();
                if ((resultItems != null) && !resultItems.isEmpty())
                {
                    for (ResultItem ri : resultItems)
                        log.info(" - " + ri.getName() + ": " + ri.getProbability());
                }
                else
                {
                    log.info("No results :(");
                }
                polling = false;
                break;
            }
            else if ((jobStatus.getStatus() == JobStatusType.FINISHED) ||
                     (jobStatus.getStatus() == JobStatusType.ERROR))
            {
                log.error("There was an error in the evaluation: " + jobStatus.getMetaData());
                polling = false;
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                log.warn("Caught an exception when trying to sleep for a bit... " + ex, ex);
            }
        }
        
        log.info("----------------------------");
        log.info("Done");
    }
    
    private static PredictorServiceJobConfig getPredictorServiceJobConfig(PredictorServiceDescription psDesc, Date startDate, String forumID, int numSnapshots) throws Exception
    {
        PredictorServiceJobConfig jobConfig = new PredictorServiceJobConfig();
        jobConfig.setStartDate(startDate);
        jobConfig.setForecastPeriod(psDesc.getForecastPeriod()); // using the one in the PSD
        jobConfig.setEvaluationEngineServiceURI(new URI(eeServiceURI));
//TODO!!! - set snapshot period
        //        jobConfig.setConfigurationParams(null);
//        jobConfig.setCommunityDetails(new CommunityDetails("A Boards.ie forum", "1"));
        jobConfig.setCommunityDetails(new CommunityDetails("A Boards.ie forum", forumID));
        jobConfig.setStreaming(false);
        
        // set event - taking the first if more than one
        Set<Event> events = new HashSet<Event>();
        Event event = psDesc.getEvents().iterator().next();
        events.add(event);
        
        // set the post condition value to the default value
        for (EventCondition ec : event.getEventConditions()) {
            ec.setPostConditionValue(new ParameterValue(ec.getDefaultValue(), ec.getAllowedEvaluationTypes().get(0)));
        }
        
        // set default values for any event config parameters
        if ((event.getConfigParams() != null) && !event.getConfigParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psDesc.getConfigurationParams()) {
                p.setValue(new ParameterValue(p.getDefaultValue()));
            }
        }
        jobConfig.setEvents(events);
        
        if ((jobConfig.getEvents() == null) || jobConfig.getEvents().isEmpty()) {
            log.error("\n\n\nCONFIG OBJECT HAS NO EVENTS\n\n\n");
        }
        
        // set config paramters with default values
        if ((psDesc.getConfigurationParams() != null) && !psDesc.getConfigurationParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psDesc.getConfigurationParams()) {
                p.setValue(new ParameterValue(p.getDefaultValue()));
            }
            
            jobConfig.setConfigurationParams(psDesc.getConfigurationParams()); // don't think this is required
        }
        
        if ((psDesc.getConfigurationParams() != null) && !psDesc.getConfigurationParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psDesc.getConfigurationParams()) {
                p.setValue(new ParameterValue(p.getDefaultValue()));
            }
            
            jobConfig.setConfigurationParams(psDesc.getConfigurationParams());
        }
        
        return jobConfig;
    }
    
    private  static void printConfig(PredictorServiceJobConfig pjc)
    {
        log.info("The PredictorServiceJobConfig details");
        
        log.info(" * Num events: " + pjc.getEvents().size());
        for (Event evt : pjc.getEvents())
        {
            log.info("    - " + evt.getTitle());
            log.info("    - Conditions: " + evt.getEventConditions().size());
            for (EventCondition ec : evt.getEventConditions())
            {
                log.info("      - " + ec.getName() + ": " + ec.getPostConditionValue().getValue());
            }
            log.info("    - Configs: " + evt.getConfigParams().size());
            for (Parameter p : evt.getConfigParams())
            {
                log.info("      - " + p.getName() + ": " + p.getValue().getValue());
            }
        }
        
        log.info(" * Num config params: " + pjc.getConfigurationParams().size());
        for (Parameter p : pjc.getConfigurationParams())
        {
            log.info("    - " + p.getName());
        }
        
        log.info(" * Start date:      " + pjc.getStartDate());
        
        log.info(" * Forecast period: " + pjc.getForecastPeriod().getValue().getValue());
        
        log.info(" * Community details:");
        log.info("   - Community name: " + pjc.getCommunityDetails().getCommunityName());
        log.info("   - Community ID:   " + pjc.getCommunityDetails().getCommunityID());
    }
}
