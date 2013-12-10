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

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 *
 * @author Vegard Engen
 */
@RunWith(Suite.class)
@SuiteClasses({ 
    PSDValidationTest.class,
    PSConfigValidationTest.class
})
public class CommonTestSuite
{
    static Logger log = Logger.getLogger(CommonTestSuite.class);

    public static void main(String[] args) throws Exception
    {
        log.info("Starting ROBUST Common Test Suite");
        Result result = org.junit.runner.JUnitCore.runClasses(
            PSDValidationTest.class,
            PSConfigValidationTest.class);

        if (processResults(result)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
    
    public static boolean processResults(Result result)
    {
        log.info("");
        if (result.wasSuccessful()) {
            log.info("ROBUST Common tests completed successfully!");
        } else {
            log.info("ROBUST Common tests finished, but with failures!");
        }
        
        log.info("Run: " + result.getRunCount() + "  Failed: " + result.getFailureCount() + "  Ignored: " + result.getIgnoreCount());
        log.info("");
        if (result.getFailureCount() > 0)
        {
            log.info("Errors:");
            for (Failure failure : result.getFailures()) {
                log.info(failure.toString());
            }
            
            return false;
        }
        
        return true;
    }
}
