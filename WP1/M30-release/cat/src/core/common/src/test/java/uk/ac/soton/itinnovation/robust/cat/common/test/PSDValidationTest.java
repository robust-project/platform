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

import java.util.UUID;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import junit.framework.*;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValidationObject;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PSDValidator;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;

/**
 *
 * @author Vegard Engen
 */
@RunWith(JUnit4.class)
public class PSDValidationTest extends TestCase
{
    static Logger log = Logger.getLogger(PSDValidationTest.class);
    
    @BeforeClass
    public static void beforeClass()
    {
        log.info("PSDValidation tests executing...");
    }
    
    @Test
    public void testValidPSD()
    {
        log.info("Testing valid PSD");
        PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), "User Activity Predictor Service (GS)", "1.2", "A Predictor Service uses a Gibbs Sampler to calculate the probability of users dropping in activity");

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
        desc.addEvent(evt);

        // forecast parameter
        Parameter forecastPeriod = new Parameter(ParameterValueType.FLOAT, "The forecast period, which specifies the size of the time window", "The parameter that defines the total number of days for each covariate", "weeks");
        forecastPeriod.setValue(new ParameterValue("1"));
        desc.setForecastPeriod(forecastPeriod);

        // general configuration parameters
        UUID numCovariatesUUID = UUID.fromString("75302132-65db-4c59-a8f0-d2c4ae702de3");
        Parameter numCovariates = new Parameter(ParameterValueType.INT, "Number of historical snapshots", "The total number of covariates that will be used in the GS", "");
        numCovariates.setUUID(numCovariatesUUID);
        numCovariates.addValueConstraint("12", ValueConstraintType.DEFAULT);
        numCovariates.addValueConstraint("3", ValueConstraintType.MIN);
        numCovariates.addValueConstraint("28", ValueConstraintType.MAX);
        numCovariates.addValueConstraint("1", ValueConstraintType.STEP);
        desc.addConfigurationParam(numCovariates);

        UUID activeThresholdUUID = UUID.fromString("0dce1066-5c85-4fd0-a115-a5d66ddda6b7");
        Parameter activeThreshold = new Parameter(ParameterValueType.INT, "Activity filter threshold", "The minimum number of posts a user should have posted in the prediction period to be included in the population", "posts");
        activeThreshold.setUUID(activeThresholdUUID);
        activeThreshold.addValueConstraint("0", ValueConstraintType.DEFAULT);
        activeThreshold.addValueConstraint("0", ValueConstraintType.MIN);
        activeThreshold.addValueConstraint("100", ValueConstraintType.MAX);
        desc.addConfigurationParam(activeThreshold);
        
        
        // Validating...
        ValidationObject vo = PSDValidator.isPredictorServiceDescriptionValid(desc);
        assertTrue("PSD not valid: " + vo.msg, vo.valid);
    }
}
