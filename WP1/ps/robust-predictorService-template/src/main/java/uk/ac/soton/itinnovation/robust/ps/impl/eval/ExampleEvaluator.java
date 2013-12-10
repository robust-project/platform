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
package uk.ac.soton.itinnovation.robust.ps.impl.eval;

import java.util.Date;
import java.util.Random;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.ps.spec.IEvaluationListener;
import uk.ac.soton.itinnovation.robust.ps.spec.IEvaluator;

/**
 * An example Evaluator implementation class, creating evaluation with randomly
 * generated probabilities. Emulates working on both streaming and non-streaming
 * data.
 * 
 * @author Vegard Engen
 */
public class ExampleEvaluator implements Runnable, IEvaluator
{
    // used in the randomised run-loop for generating evaluation results
    private boolean running;
    private Random rand;
    private long sleepTime;
    
    // job configuration objects
    private String jobRef;
    private PredictorServiceJobConfig jobConfig;
    private FrequencyType forecastPeriod;
    private boolean stream;
    private Date startDate;
    private Date endDate;
    private JobStatus status;
    
    // listener to notify of new result or errors
    private IEvaluationListener evaluationListener;
    
    // thread for running this evaluator in
    private Thread evaluatorThread;
    
    static Logger log = Logger.getLogger(ExampleEvaluator.class);
    
    /**
     * Default constructor that initialises the evaluator.
     */
    public ExampleEvaluator()
    {
        running = true;
        sleepTime = 2 * 1000;
        
        rand = new Random();
    
        forecastPeriod = FrequencyType.WEEKLY;
        status = new JobStatus(JobStatusType.READY);
        
        evaluatorThread = new Thread((ExampleEvaluator)this, "Evaluator Thread");
    }

    @Override
    public void run()
    {
        try
        {
            if (stream)
            {
                log.debug("Evaluator started on a streaming job");
                status = new JobStatus(JobStatusType.EVALUATING);
                
                while (running)
                {
                    int numIt = rand.nextInt(5) + 1;
                    int count = 0;
                    
                    while (count < numIt)
                    {
                        count++;
                        Thread.currentThread().sleep(sleepTime);
                    }
                    
                    double probability = rand.nextDouble();
                    double currentObservation = rand.nextDouble();

                    if (status.getStatus() != JobStatusType.CANCELLED)
                    {
                        log.debug("Stream Evaluator got a result ready - probability: " + probability);
                        status = new JobStatus(JobStatusType.RESULT_AVAILABLE);
                        createEvaluationResult(probability, currentObservation, startDate, endDate);
                        startDate = DateUtil.getToDate(startDate, forecastPeriod);
                        endDate = DateUtil.getToDate(endDate, forecastPeriod);
                    }
                }
                
                log.info("Stream Evaluator thread complete");
            }
            else
            {
                int numIt = rand.nextInt(5) + 1;
                log.debug("Evaluator started - numIt: " + numIt);
                status = new JobStatus(JobStatusType.EVALUATING);

                int count = 0;
                while (running)
                {
                    count++;
                    Thread.currentThread().sleep(sleepTime);

                    if (count >= numIt)
                    {
                        running = false;
                        double probability = rand.nextDouble();
                        double currentObservation = rand.nextDouble();

                        if (status.getStatus() != JobStatusType.CANCELLED)
                        {
                            log.debug("Evaluator finished - probability: " + probability);
                            status = new JobStatus(JobStatusType.FINISHED);
                            createEvaluationResult(probability, currentObservation, startDate, endDate);
                        }
                    }
                }
                
                log.info("Evaluator thread complete");
            }
        } catch (InterruptedException iex) {
            log.error("Caught an interrupted exception in the Evaluator", iex);
            status = new JobStatus(JobStatusType.ERROR, "Caught an exception in the Evaluator");
            log.debug("Notifying listener of error");
            evaluationListener.onError(jobRef, status);
        } catch (Exception ex) {
            log.error("Caught an exception in the Evaluator", ex);
            status = new JobStatus(JobStatusType.ERROR, "Caught an exception in the Evaluator");
            log.debug("Notifying listener of error");
            evaluationListener.onError(jobRef, status);
        }
        
        log.debug("Goodbye and thanks for all the fish!");
    }

    @Override
    public void startJob(String jobRef, PredictorServiceJobConfig config) throws Exception
    {
        this.jobRef = jobRef;
        this.jobConfig = config;
        this.startDate = jobConfig.getStartDate();
        this.endDate = DateUtil.getToDate(startDate, forecastPeriod);
        this.stream = jobConfig.isStreaming();
        
        evaluatorThread.start();
    }

    @Override
    public void cancelJob()
    {
        running = false;
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
    
    /**
     * Creates an evaluation result and notifies the listener.
     * @param probability The calculated probability.
     * @param currentObservation The current observed value.
     * @param currentDate The date from which the result is a forecast. 
     * @param forecastDate The date of the forecast.
     */
    private void createEvaluationResult(double probability, double currentObservation, Date currentDate, Date forecastDate)
    {
        EvaluationResult evalRes = new EvaluationResult();
        evalRes.addResultItem(new ResultItem("probability", probability, String.valueOf(currentObservation)));
        evalRes.setCurrentDate(currentDate);
        evalRes.setForecastDate(forecastDate);
        
        // set meta data if any
        evalRes.addMetaData("key", "value");
        
        if(evaluationListener != null)
        {
            log.debug("Notifying evaluation listener of new result");
            evaluationListener.onNewResult(jobRef, evalRes);
        }
    }
}