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
//      Created Date :          2013-04-18
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.test;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValidationObject;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.CommunityDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ConfigValidator;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 *
 * @author Vegard Engen
 */
@RunWith(JUnit4.class)
public class PSConfigValidationTest extends TestCase
{
    static Logger log = Logger.getLogger(PSConfigValidationTest.class);
    
    static PredictorServiceDescription psd;
    static String eeServiceURI = "http://localhost:8080/evaluationEngineService-1.5/service";
    static Date startDate;
    
    @BeforeClass
    public static void beforeClass()
    {
        log.info("PSConfigValidation tests executing...");
        psd = new PredictorServiceDescription(UUID.randomUUID(), "User Activity Predictor Service (GS)", "1.2", "A Predictor Service uses a Gibbs Sampler to calculate the probability of users dropping in activity");

        // event
        UUID eventUUID = UUID.fromString("e19b4722-4257-4a7c-a36c-970565ecb552");
        Event evt = new Event("User activity drop", "Users activity drop according to some percentage treshold");
        evt.setUuid(eventUUID);
        
        // event condition
        UUID eventCondUUID = UUID.fromString("87084ba9-edac-4e79-b23a-76a14e52730e");
        EventCondition evtCond = new EventCondition(ParameterValueType.FLOAT, "Activity drop Threshold", "", "");
        evtCond.setUUID(eventCondUUID);
        evtCond.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond.addValueConstraint("0.2", ValueConstraintType.DEFAULT);
        evtCond.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond.addValueConstraint("1", ValueConstraintType.MAX);
        evtCond.addAllowedEvaluationType(EvaluationType.GREATER_OR_EQUAL);

        evt.setEventCondition(evtCond);
        psd.addEvent(evt);

        // forecast parameter
        Parameter forecastPeriod = new Parameter(ParameterValueType.FLOAT, "The forecast period, which specifies the size of the time window", "The parameter that defines the total number of days for each covariate", "weeks");
        forecastPeriod.setValue(new ParameterValue("1"));
        psd.setForecastPeriod(forecastPeriod);

        // general configuration parameters
        UUID numCovariatesUUID = UUID.fromString("75302132-65db-4c59-a8f0-d2c4ae702de3");
        Parameter numCovariates = new Parameter(ParameterValueType.INT, "Number of historical snapshots", "The total number of covariates that will be used in the GS", "");
        numCovariates.setUUID(numCovariatesUUID);
        numCovariates.addValueConstraint("12", ValueConstraintType.DEFAULT);
        numCovariates.addValueConstraint("3", ValueConstraintType.MIN);
        numCovariates.addValueConstraint("28", ValueConstraintType.MAX);
        numCovariates.addValueConstraint("1", ValueConstraintType.STEP);
        psd.addConfigurationParam(numCovariates);

        UUID activeThresholdUUID = UUID.fromString("0dce1066-5c85-4fd0-a115-a5d66ddda6b7");
        Parameter activeThreshold = new Parameter(ParameterValueType.INT, "Activity filter threshold", "The minimum number of posts a user should have posted in the prediction period to be included in the population", "posts");
        activeThreshold.setUUID(activeThresholdUUID);
        activeThreshold.addValueConstraint("0", ValueConstraintType.DEFAULT);
        activeThreshold.addValueConstraint("0", ValueConstraintType.MIN);
        activeThreshold.addValueConstraint("100", ValueConstraintType.MAX);
        psd.addConfigurationParam(activeThreshold);
        
        try { startDate = DateUtil.getDateObject("2003-02-09"); } catch (Exception ex) { fail("Unable to instantiate the start date object"); }
    }
    
    @Test
    public void testValidPSConfig()
    {
        log.info("Testing valid PS Config");
        
        PredictorServiceJobConfig jobConfig = new PredictorServiceJobConfig();
        jobConfig.setStartDate(startDate);
        jobConfig.setForecastPeriod(psd.getForecastPeriod()); // using the one in the PSD
        try { jobConfig.setEvaluationEngineServiceURI(new URI(eeServiceURI)); } catch (Exception ex) { fail("Failed to set the evaluation engine URI"); }
        jobConfig.setCommunityDetails(new CommunityDetails("A Boards.ie forum", "1"));
        jobConfig.setStreaming(false);
        
        // set event - taking the first if more than one
        Set<Event> events = new HashSet<Event>();
        Event event = psd.getEvents().iterator().next();
        events.add(event);
        
        // set the post condition value to the default value
        for (EventCondition ec : event.getEventConditions()) {
            ec.setPostConditionValue(new ParameterValue(ec.getDefaultValue(), ec.getAllowedEvaluationTypes().get(0)));
        }
        
        // set default values for any event config parameters
        if ((event.getConfigParams() != null) && !event.getConfigParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psd.getConfigurationParams()) {
                p.setValue(new ParameterValue(p.getDefaultValue()));
            }
        }
        jobConfig.setEvents(events);
        
        // set config paramters with default values
        if ((psd.getConfigurationParams() != null) && !psd.getConfigurationParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psd.getConfigurationParams()) {
                p.setValue(new ParameterValue(p.getDefaultValue()));
            }
            
            jobConfig.setConfigurationParams(psd.getConfigurationParams()); // don't think this is required
        }
        
        if ((psd.getConfigurationParams() != null) && !psd.getConfigurationParams().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: psd.getConfigurationParams()) {
                p.setValue(new ParameterValue(p.getDefaultValue()));
            }
            
            jobConfig.setConfigurationParams(psd.getConfigurationParams());
        }
        
        // validating...
        ValidationObject vo = ConfigValidator.isConfigValid(jobConfig, psd, true);
        assertTrue("PS COnfig not valid: " + vo.msg, vo.valid);
    }
}
