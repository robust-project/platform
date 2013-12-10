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
//      Created Date :          2013-07-30
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 *
 * @author Vegard Engen
 */
public class TestModeUtil
{
    private Map<UUID, Integer> riskResultDistributionMap;
    private Map<Integer, Double[]> resultDistributionMap;
    private Map<UUID, Integer> riskResultIndexMap;
    private int numDistributions = 4;
    private int maxResultEntries = 60;
    private Random rand;
    
    private static final Logger log = LoggerFactory.getLogger(TestModeUtil.class);
    
    public TestModeUtil()
    {
        setUpResultDistributions();
        rand = new Random();
    }
    
    private void setUpResultDistributions()
    {
        riskResultDistributionMap = new HashMap<UUID, Integer>();
        resultDistributionMap = new HashMap<Integer, Double[]>();
        riskResultIndexMap = new HashMap<UUID, Integer>();
        
        Double[] valueArray1 = {0.1, 0.2, 0.3, 0.4, 0.3, 0.2, 0.2, 0.3, 0.2, 0.3, 
                               0.4, 0.7, 0.8, 0.95, 0.95, 0.95, 0.8, 0.8, 0.7, 0.7,
                               0.7, 0.7, 0.7, 0.6, 0.6, 0.5, 0.5, 0.4, 0.4, 0.4,
                               0.3, 0.4, 0.5, 0.5, 0.4, 0.4, 0.3, 0.3, 0.2, 0.1,
                               0.1, 0.2, 0.2, 0.2, 0.3, 0.4, 0.5, 0.4, 0.3, 0.2,
                               0.1, 0.1, 0.2, 0.3, 0.3, 0.2, 0.1, 0.1, 0.2, 0.2};
        
        Double[] valueArray2 = {0.4, 0.5, 0.5, 0.8, 0.8, 0.95, 0.95, 0.8, 0.7, 0.7,
                               0.7, 0.7, 0.6, 0.5, 0.5, 0.4, 0.4, 0.4, 0.4, 0.4,
                               0.3, 0.4, 0.5, 0.5, 0.4, 0.4, 0.3, 0.3, 0.2, 0.1,
                               0.4, 0.5, 0.5, 0.8, 0.8, 0.95, 0.95, 0.8, 0.7, 0.7,
                               0.7, 0.7, 0.6, 0.5, 0.5, 0.4, 0.4, 0.4, 0.4, 0.4,
                               0.3, 0.4, 0.5, 0.5, 0.4, 0.4, 0.3, 0.3, 0.2, 0.1};
        
        Double[] valueArray3 = {0.9, 0.9, 0.8, 0.6, 0.7, 0.6, 0.7, 0.6, 0.7, 0.7, 
                               0.5, 0.5, 0.5, 0.5, 0.5, 0.4, 0.4, 0.5, 0.6, 0.7,
                               0.7, 0.7, 0.7, 0.6, 0.6, 0.5, 0.5, 0.4, 0.4, 0.4,
                               0.3, 0.4, 0.5, 0.5, 0.4, 0.4, 0.3, 0.3, 0.2, 0.1,
                               0.1, 0.2, 0.2, 0.2, 0.5, 0.6, 0.5, 0.6, 0.7, 0.7,
                               0.8, 0.7, 0.7, 0.8, 0.9, 0.99, 0.99, 0.9, 0.4, 0.2};
        
        Double[] valueArray4 = {0.1, 0.1, 0.15, 0.15, 0.15, 0.1, 0.1, 0.15, 0.2, 0.2, 
                               0.2, 0.15, 0.1, 0.15, 0.1, 0.1, 0.1, 0.15, 0.2, 0.2,
                               0.25, 0.25, 0.3, 0.3, 0.35, 0.3, 0.3, 0.2, 0.2, 0.2,
                               0.15, 0.15, 0.2, 0.2, 0.15, 0.1, 0.1, 0.1, 0.05, 0.05,
                               0.1, 0.2, 0.2, 0.2, 0.1, 0.1, 0.15, 0.2, 0.1, 0.1,
                               0.2, 0.25, 0.25, 0.3, 0.3, 0.3, 0.3, 0.2, 0.2, 0.15};
        
        resultDistributionMap.put(0, valueArray1);
        resultDistributionMap.put(1, valueArray2);
        resultDistributionMap.put(2, valueArray3);
        resultDistributionMap.put(3, valueArray4);
    }
    
    public void resetDistributionIndices()
    {
        for (UUID riskUUID : riskResultIndexMap.keySet()) {
            riskResultIndexMap.put(riskUUID, 0);
        }
    }
    
    /**
     * Get a an EvaluationResult with a random probability.
     * @return 
     */
    public EvaluationResult getRandomEvaluationResult(Risk r, Date currentDate, FrequencyType simDateIncrement)
    {
        UUID riskUUID = r.getId();
        if (!riskResultDistributionMap.containsKey(riskUUID))
        {
            log.debug("Adding risk " + riskUUID.toString() + " to risk result distribution map");
            
            if (r.getTitle().equalsIgnoreCase("Undesirable role composition")) // added this for demo purposes
            {
                riskResultDistributionMap.put(riskUUID, 0);
                riskResultIndexMap.put(riskUUID, 0);
            }
            else
            {
                int distributionID = rand.nextInt(numDistributions/*+1*/);
                /*if (distributionID == numDistributions) {
                    log.debug("Creating a new random distribution");
                    // adding a random one
                    Double[] valueArray = new Double[60];
                    for (int i = 0; i < 60; i++) {
                        valueArray[i] = rand.nextDouble();
                    }
                    resultDistributionMap.put(distributionID, valueArray);
                    numDistributions++;
                }*/
                riskResultDistributionMap.put(riskUUID, distributionID);
                riskResultIndexMap.put(riskUUID, 0);
            }
        }
        
        Integer distributionID = riskResultDistributionMap.get(riskUUID);
        Double[] valueArray = resultDistributionMap.get(distributionID);
        int idx = riskResultIndexMap.get(riskUUID);
        if (idx == maxResultEntries) {
            resetDistributionIndices();
            idx = 0;
        }
        
        double randValue = scale(rand.nextDouble(), 0, 1, -0.05, 0.05);
        double probability = valueArray[idx] + randValue;
        if (probability < 0){
            probability = 0;
        } else if (probability > 1) {
            probability = 1;
        }
        log.debug("Risk " + riskUUID.toString() + "\tprob: " + probability + ", val: " + valueArray[idx] + ", rand: " + randValue);
        EvaluationResult res = new EvaluationResult();
        res.setRiskUUID(riskUUID);
        res.setCurrentDate(currentDate);
        res.setForecastDate(DateUtil.getToDate(currentDate, simDateIncrement));
        res.addResultItem(new ResultItem("probability", probability));
        
        JobDetails jobDetails = new JobDetails();
        jobDetails.setJobRef(UUID.randomUUID().toString());
        jobDetails.setRequestDate(new Date());
        jobDetails.setStartDate(new Date());
        jobDetails.setCompletionDate(new Date());
        jobDetails.setJobStatus(new JobStatus(JobStatusType.FINISHED));
        res.setJobDetails(jobDetails);
        
        riskResultIndexMap.put(riskUUID, ++idx);
        
        return res;
    }
    
    public double scale(double num, double oldMin, double oldMax, double newMin, double newMax)
    {
        return (((num - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }
}
