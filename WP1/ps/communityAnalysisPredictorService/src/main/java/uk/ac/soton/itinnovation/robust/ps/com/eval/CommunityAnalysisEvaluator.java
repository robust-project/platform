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
//      Created Date :          2012-03-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.ps.com.eval;

import java.util.Date;
import java.util.Properties;
import java.util.Random;
import org.apache.log4j.Logger;
import pl.softwaremind.robust.healthindicatorservice.Score;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.communityAnalysisClient.CommunityAnalysisClient;
import uk.ac.soton.itinnovation.robust.ps.com.spec.IEvaluationListener;
import uk.ac.soton.itinnovation.robust.ps.com.spec.IEvaluator;

/**
 *
 * @author Vegard Engen
 */
public class CommunityAnalysisEvaluator implements Runnable, IEvaluator
{
	// event details
    private double threshold;
    private double probability;
	private EvaluationType evalType;
    
	// community analysis service variables
    private String accessKey;
    private int dataSourceID;
    private int communityID;
    private int indicatorID;
    
    private String serviceURI = "http://robust-www.softwaremind.pl/CommunityAnalysisService/";
    private String serviceNameSpace = "http://robust.softwaremind.pl/HealthIndicatorService/";
    private String serviceName = "HealthIndicatorService";
    private String servicePort = "HealthIndicatorServiceSoap";
    
	// job configuration
    private String jobRef;
    private PredictorServiceJobConfig jobConfig;
    private FrequencyType forecastPeriod;
    
    private Date startDate;
    private Date endDate;
    private JobStatus status;
    
    private IEvaluationListener evaluationListener;
    private Thread evaluatorThread;
    private Random rand;
        
    static Logger log = Logger.getLogger(CommunityAnalysisEvaluator.class);
    
    public CommunityAnalysisEvaluator()
    {
        rand = new Random();
        
        accessKey = "66fffc9289c4c4009dac543fd5a9a817f699ca75"; // may need to use demo321
        dataSourceID = 3;  // SAP
        communityID = 264; // SAP Business One Core
        indicatorID = 3;   // Popularity
        
        getConfigs();
    
        threshold = 0.8;
        forecastPeriod = FrequencyType.WEEKLY;
        status = new JobStatus(JobStatusType.READY);
        
        evaluatorThread = new Thread((CommunityAnalysisEvaluator)this, "Community Analysis Evaluator Thread");
    }
    
    /**
     * Gets configuration properties from 'service.properties' on the class path.
     */
    private void getConfigs()
    {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("service.properties"));
        } catch (Exception ex) {
            log.warn("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
            return;
        }

        try {
            accessKey = prop.getProperty("accessKey");
        } catch (Exception ex) {
            log.warn("Error with loading getting and converting 'accessKey' parameter from service.properties. " + ex.getMessage(), ex);
        }
        log.debug("Service accessKey:  " + accessKey);
        
        try {
            dataSourceID = Integer.parseInt(prop.getProperty("dataSourceID"));
        } catch (Exception ex) {
            log.warn("Error with loading getting and converting 'dataSourceID' parameter from service.properties. " + ex.getMessage(), ex);
        }
        log.debug("dataSourceID:  " + dataSourceID);
        
        try {
            communityID = Integer.parseInt(prop.getProperty("communityID"));
        } catch (Exception ex) {
            log.warn("Error with loading getting and converting 'communityID' parameter from service.properties. " + ex.getMessage(), ex);
        }
        log.debug("communityID:   " + communityID);
        
        try {
            indicatorID = Integer.parseInt(prop.getProperty("indicatorID"));
        } catch (Exception ex) {
            log.warn("Error with loading getting and converting 'indicatorID' parameter from service.properties. " + ex.getMessage(), ex);
        }
        log.debug("indicatorID:   " + indicatorID);
        
        try {
            // try to get service details - save if only all are set
            String tmp1 = prop.getProperty("serviceURI");
            String tmp2 = prop.getProperty("serviceNameSpace");
            String tmp3 = prop.getProperty("serviceName");
            String tmp4 = prop.getProperty("servicePort");

            serviceURI = tmp1;
            serviceNameSpace = tmp2;
            serviceName = tmp3;
            servicePort = tmp4;
        } catch (Exception ex) {
            log.error("Error with getting service parameter from service.properties. " + ex.getMessage(), ex);
            return;
        }

        log.debug("Service configs:");
        log.debug("  - serviceURI: " + serviceURI);
        log.debug("  - serviceNameSpace: " + serviceNameSpace);
        log.debug("  - serviceName: " + serviceName);
        log.debug("  - servicePort: " + servicePort);
    }

    @Override
    public void run()
    {
        try
        {
            status = new JobStatus(JobStatusType.EVALUATING);
            double currentObservation = 0;
            
            try {
                log.info("Instantiating Community Analysis Client");
                CommunityAnalysisClient client = new CommunityAnalysisClient(accessKey, serviceURI, serviceNameSpace, serviceName, servicePort);

                log.info("Getting community health metric");
				log.info(" - Start date: " + startDate);
				log.info(" - End date:   " + endDate);
                Score healthScore = client.getHealthScore(dataSourceID, communityID, indicatorID, startDate, endDate);
				
                if (healthScore != null)
                {
                    log.info("Got health score: " + healthScore.getValue());
                    currentObservation = healthScore.getValue();
                    probability = getProbability(healthScore);
                }
                else
                {
                    log.warn("Didn't get a health score - creating a random probability");
                    probability = rand.nextDouble();
                }
            } catch (Throwable ex) {
                log.error("Caught an exception when trying to use the Community Analysis Service: " + ex.getMessage(), ex);
                log.error("Creating a random probability so the service can continue running ok");
                probability = rand.nextDouble();
            } finally {
                log.info("Probability: " + probability);
                status = new JobStatus(JobStatusType.FINISHED);
                createEvaluationResult(probability, currentObservation, startDate, endDate);
            }
        } catch (Exception ex) {
            log.error("Caught an exception in the Evaluator", ex);
            status = new JobStatus(JobStatusType.ERROR, "Caught an exception in the Evaluator");
            log.debug("Notifying listener of error");
            evaluationListener.onError(jobRef, status);
        }
        
        log.info("Evaluator thread complete");
    }
	
	private double getProbability(Score healthScore)
	{
		if (evalType.equals(EvaluationType.GREATER) || 
			evalType.equals(EvaluationType.GREATER_OR_EQUAL) || 
			evalType.equals(EvaluationType.EQUAL))
		{
			if (healthScore.getValue() >= threshold) {
				return 1;
			} else {
				return 1 - ((threshold-healthScore.getValue()) / threshold);
			}
		}
		else // less than or less/equal to
		{
			if (healthScore.getValue() <= threshold) {
				return 1;
			} else {
				return 1 - (healthScore.getValue()-threshold);
			}
		}
	}

    @Override
    public void startJob(String jobRef, PredictorServiceJobConfig config) throws Exception
    {
        this.jobRef = jobRef;
        this.jobConfig = config;
        this.startDate = jobConfig.getStartDate();
        this.endDate = DateUtil.getToDate(jobConfig.getStartDate(), forecastPeriod);
        
        // getting the health indicator and threshold
        Event evt = null;
		ParameterValue indicatorName = null;
        ParameterValue thresholdParamValue = null;
        threshold = 0;
        try {
			evt = config.getEvents().iterator().next();
		} catch (Exception e) {
            throw new RuntimeException("Unable to get event", e);
        }
		
		try {
			indicatorName = evt.getConfigParams().iterator().next().getValue();
		} catch (Exception e) {
			throw new RuntimeException("Unable to get the health indicator from the event config", e);
		}

		try {
			this.indicatorID = getIndicatorValue(indicatorName.getValue());
		} catch (Exception ex) {
			throw new RuntimeException("Unable to get the health indicator indicator ID", ex);
		}

		thresholdParamValue = evt.getEventConditions().iterator().next().getPostConditionValue();
		try {
			thresholdParamValue = evt.getEventConditions().iterator().next().getPostConditionValue();
		} catch (Exception e) {
			throw new RuntimeException("Unable to get threshold from event condition", e);
		}

		try {
			threshold = Double.parseDouble(thresholdParamValue.getValue());
		} catch (Exception e) {
			log.error("Unable to parse the event threshold to a double", e);
		}

		if (thresholdParamValue.getValueEvaluationType() == null) {
			throw new RuntimeException("Evaluation type for the post-condition value not set!");
		} else {
			evalType = thresholdParamValue.getValueEvaluationType();
		}

        log.info("Event health indicator set to " + indicatorID);
        log.info("Event threshold set to " + threshold);
		log.info("Event evaluation type set to " + evalType);
        
        // starting the thread
        evaluatorThread.start();
    }
	
	private Integer getIndicatorValue(String indicatorName) throws Exception
	{
		if (indicatorName == null) {
			throw new NullPointerException("The indicator name is NULL");
		}

		if (indicatorName.equals("In-Degree")) {
			return 1;
		} else if (indicatorName.equals("Out-Degree")) {
			return 2;
		} else if (indicatorName.equals("Popularity")) {
			return 3;
		} else if (indicatorName.equals("Reciprocity")) {
			return 4;
		} else if (indicatorName.equals("Activity")) {
			return 5;
		} else {
			throw new IllegalArgumentException("Unknown indicator name: " + indicatorName);
		}
	}

    @Override
    public void cancelJob()
    {
        status = new JobStatus(JobStatusType.CANCELLED);
        log.debug("Cancelled job");
    }

    @Override
    public JobStatus getJobStatus()
    {
        return status;
    }

    @Override
    public void setEvaluationListener(IEvaluationListener evaluationListener)
    {
        this.evaluationListener = evaluationListener;
    }
    
    private void createEvaluationResult(double probability, double currentObservation, Date startDate, Date forecastDate)
    {
        EvaluationResult evalRes = new EvaluationResult();
        evalRes.addResultItem(new ResultItem("probability", probability, String.valueOf(currentObservation)));
        evalRes.setCurrentDate(startDate);
        evalRes.setForecastDate(forecastDate);
        
        if(evaluationListener != null)
        {
            log.debug("Notifying evaluation listener of new result");
            evaluationListener.onNewResult(jobRef, evalRes);
        }
    }
}
