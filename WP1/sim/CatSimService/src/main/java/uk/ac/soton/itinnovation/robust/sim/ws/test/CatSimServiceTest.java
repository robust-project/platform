/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
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
//      Created Date :          2013-10-10
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.sim.ws.test;

import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.ps.CommunityDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.sim.ws.impl.SimulationService;

/**
 * A test class.
 * @author Vegard Engen
 */
public class CatSimServiceTest
{
	private static Logger logger = Logger.getLogger(CatSimServiceTest.class);
	
	public static void main(String[] args) throws Exception
	{
		SimulationService simService = new SimulationService();
		SimulationServiceDescription desc = simService.getSimulationServiceDescription();
		printSimDesc(desc);
		
		SimulationServiceJobConfig jobConfig = getJobConfig(desc);
		
		JobDetails jobDetails = simService.createSimulationJob(jobConfig);
		logger.info("Job reference: " + jobDetails.getJobRef());
        logger.info("Job status: " + jobDetails.getJobStatus().getStatus());

        if ((jobDetails.getJobStatus().getStatus().equals(JobStatusType.FAILED)) ||
            (jobDetails.getJobStatus().getStatus().equals(JobStatusType.ERROR)))
        {
            logger.error("ERROR MSG: " + jobDetails.getJobStatus().getMetaData());
            return;
        }
		
		logger.info("Starting simulation job: " + jobDetails.getJobRef());
        JobStatus jobStatus = simService.startSimulation(jobDetails.getJobRef());
        logger.info("Job status: " + jobStatus.getStatus());
        
        if ((jobStatus.getStatus().equals(JobStatusType.FAILED)) ||
            (jobStatus.getStatus().equals(JobStatusType.ERROR)))
        {
            logger.error("ERROR MSG: " + jobStatus.getMetaData());
            return;
        }
		
		// polling for results
        while (true)
        {
            logger.info("Checking status of job: " + jobDetails.getJobRef());
            jobStatus = simService.getJobStatus(jobDetails.getJobRef());
            logger.info("Job status: " + jobStatus.getStatus());
            
            if ((jobStatus.getStatus().equals(JobStatusType.FINISHED)) || (jobStatus.getStatus().equals(JobStatusType.RESULT_AVAILABLE)))
            {
				SimulationResult simRes = simService.getResult(jobDetails.getJobRef());
				
                if (simRes == null) {
                    logger.error("SimulationResult == NULL");
					break;
                }
				
				printResults(simRes);
                
                break;
            }
            else if ((jobStatus.getStatus().equals(JobStatusType.FAILED)) ||
                     (jobStatus.getStatus().equals(JobStatusType.ERROR)))
            {
                logger.error("There was an error in the evaluation: " + jobStatus.getMetaData());
                break;
            }
            
            Thread.sleep(5000);
        }
	}
	
	private static void printSimDesc(SimulationServiceDescription desc)
	{
		logger.info("Simulation Service Description:");
		logger.info(" * Name:    " + desc.getName());
		logger.info(" * Desc:    " + desc.getDescription());
		logger.info(" * Version: " + desc.getVersion());
		if ((desc.getConfigurationParameters() != null) && !desc.getConfigurationParameters().isEmpty())
        {
            logger.info(" * Configuration parameters:");
            for (Parameter p : desc.getConfigurationParameters())
            {
                if (p == null) {
                    logger.error("   - got a parameter that was NULL");
					continue;
                } else if (p.getName() == null) {
                    logger.error("   - got a parameter whose name was NULL");
					continue;
                }
                
                try {
                    String defaultValue = p.getDefaultValue();
                    logger.info("   - " + p.getName() + ": " + defaultValue);
                } catch (Exception e) {
                    logger.info("   - " + p.getName());
                }
            }
        }
	}
	
	private static SimulationServiceJobConfig getJobConfig(SimulationServiceDescription desc) throws Exception
    {
		logger.info("Generating job configuration object");
		SimulationServiceJobConfig config = new SimulationServiceJobConfig();
		
		config.setCommunityDetails(new CommunityDetails("SAP Business One","http://forums.sdn.sap.com/uim/forum/264#id"));
		config.setStartDate(DateUtil.getDateObject("07/01/2008"));
		config.setEndDate(DateUtil.getDateObject("14/01/2008"));
		
		if ((desc.getConfigurationParameters() != null) && !desc.getConfigurationParameters().isEmpty())
        {
            // find default value and set this as the parameter value for each config parameter
            for (Parameter p: desc.getConfigurationParameters())
			{
				if (p.getName().trim().equalsIgnoreCase("User ID"))
				{
					p.setValue(new ParameterValue("b16fc8c258dc893ac33d0ae30c25c925"));
				}
				else if (p.getName().trim().equalsIgnoreCase("User activity drop"))
				{
					p.setValue(new ParameterValue("100"));
				}
				else if (p.getName().trim().equalsIgnoreCase("Number of runs"))
				{
					p.setValue(new ParameterValue("3"));
				}
				else
				{
					ParameterValue paramVal = new ParameterValue(p.getDefaultValue());
					p.setValue(paramVal);
				}
            }
            
            config.setConfigurationParameters(desc.getConfigurationParameters());
        }
		
		return config;
	}

	private static void printResults(SimulationResult simRes) throws Exception
	{
		logger.info("Results from running job with id " + simRes.getJobDetails().getJobRef() + ":");
		logger.info(" * Simulation date: " + DateUtil.getDateString(simRes.getSimulationDate()));
		logger.info(" * " + simRes.getResultGroups().size() + " Result Groups:");

		for (ResultGroup resGrp : simRes.getResultGroups())
		{
			if (resGrp == null) {
				logger.info("   - Got a NULL group :-(");
				continue;
			}
			
			logger.info("   - " + resGrp.getName());
			for (KeyValuePair kp : resGrp.getResults())
			{
				logger.info("      - " + kp.getKey() + ": " + kp.getValue());
			}
		}
	}
}
