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
package uk.ac.soton.itinnovation.robust.ps.gs;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.ps.spec.IEvaluationListener;
import uk.ac.soton.itinnovation.robust.ps.spec.IEvaluator;

/**
 *
 * @author Vegard Engen
 */
public class GSBoardsieEvaluator implements Runnable, IEvaluator {
    // general job configuration
    private String jobRef;
    private PredictorServiceJobConfig jobConfig;
    private FrequencyType forecastPeriod;
    private boolean stream;
    private JobStatus status;
    
    // General prediction parameters
    private Date currentDate;
    private Date forecastDate;
    private int totalPeriods;
    private double dropThreshold;
    private double activeThreshold;
    
    // evaluation listener (will be notified of results and errors) and thread object
    private IEvaluationListener evaluationListener;
    private Thread evaluatorThread;
    private Queue<GSExecutor> gsExecutorQueue;
    
    // Data extractor that's used for getting historical user activity data
    BoardsieDataExtractor dataExtractor;
    
    // logger
    static Logger log = Logger.getLogger(GSBoardsieEvaluator.class);

    /**
     * Default constructor that initialises the evaluator object.
     */
    public GSBoardsieEvaluator() 
    {
        this.status = new JobStatus(JobStatusType.READY);
        this.gsExecutorQueue = new LinkedList<GSExecutor>();

        // get configuration parameters
        this.getConfigs();
        
        // create thread instance
        this.evaluatorThread = new Thread((GSBoardsieEvaluator) this, "GS Boards.ie Evaluator Thread");
    }
    
    /**
     * A constructor that initialises the evaluator object and sets up the
     * evaluation listener.
     * 
     * @param evaluationListener 
     */
    public GSBoardsieEvaluator(IEvaluationListener evaluationListener) {
        this();
        this.evaluationListener = evaluationListener;
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
    }

    @Override
    public void run() {
        try {
            log.info("GS Boards.ie Evaluator started on historical data");
            status = new JobStatus(JobStatusType.EVALUATING);

            // get historical data from DB
            Map<String, List<Number>> snapshotDataMap = null;
            try {
                snapshotDataMap = getHistoricalData(forecastDate);
            } catch (Throwable ex) {
                log.error("Failed to extract historical data from the database: " + ex.toString(), ex);
                throw new RuntimeException("Failed to extract historical data from the database");
            }

            // execute the GS (if not cancelled by this point already)
            if (status.getStatus() != JobStatusType.CANCELLED) {
                executeGibbsSampler(snapshotDataMap, currentDate, forecastDate);
            }

            // loop to check if the GS has finished (or failed)
            while (true) {
                // sleeping for 5 seconds
                Thread.sleep(5000);

                if (status.getStatus() == JobStatusType.CANCELLED) {
                    break;
                }

                // check if any gs executors running
                if (!gsExecutorQueue.isEmpty())
                {
                    // this call checks if the GS executor is finished and process results
                    // notifies evaluation engine of new results or errors too
                    processGSExecutorJob();
                    if ((status.getStatus() == JobStatusType.ERROR)
                     || (status.getStatus() == JobStatusType.FAILED)
                     || (status.getStatus() == JobStatusType.FINISHED)) {
                        break;
                    }
                }
            }

            log.info("GS Boards.ie Evaluator thread complete");
            
        } catch (InterruptedException iex) {
            log.error("Caught an interrupted exception in the Evaluator", iex);
            status = new JobStatus(JobStatusType.ERROR, "Caught an exception when running the evaluation job");
            log.debug("Notifying listener of error");
            evaluationListener.onError(jobRef, status);
        } catch (Throwable ex) {
            log.error("Caught an exception in the Evaluator", ex);
            status = new JobStatus(JobStatusType.ERROR, "Caught an exception when running the evaluation job: " + ex.getMessage());
            log.debug("Notifying listener of error");
            evaluationListener.onError(jobRef, status);
        }

        log.info("Goodbye and thanks for all the fish...");
    }
    
    /**
     * Processes a GS Executor job - checking the status first; if finished or
     * failed, will process further accordingly. Will notify the Evaluation
     * Engine Service of any new results or failures and update the 
     */
    private void processGSExecutorJob()
    {
        GSExecutor gsExec = gsExecutorQueue.peek();
        JobStatus gsExecStatus = gsExec.getStatus();
        
        // check if failed/error or finished
        if ((gsExecStatus.getStatus() == JobStatusType.ERROR)
         || (gsExecStatus.getStatus() == JobStatusType.FAILED))
        {
            log.info("The execution of the GS failed - notifying the Evaluation Engine Service");
            status = gsExecStatus;
            if (evaluationListener != null) {
                evaluationListener.onError(jobRef, status);
            }
            
            // remove the gs executor from the queue
            gsExecutorQueue.poll();
        }
        else if (gsExecStatus.getStatus() == JobStatusType.FINISHED)
        {
            if (stream) {
                status = new JobStatus(JobStatusType.RESULT_AVAILABLE);
            } else {
                status = new JobStatus(JobStatusType.FINISHED);
            }
            
            // get the evaluation result and notify the evaluation listener
            try {
                log.info("Getting the prediction result from the GS");
                EvaluationResult evalRes = gsExec.getEvaluationResult(currentDate, forecastDate);

                if (evaluationListener != null) {
                    log.debug("Notifying evaluation listener of new result");
                    evaluationListener.onNewResult(jobRef, evalRes);
                }
            } catch (Exception ex) {
                log.error("Failed to get the results from the Gibbs Sampler: " + ex.toString(), ex);
                if (evaluationListener != null) {
                    status = new JobStatus(JobStatusType.ERROR, "Failed to get the results from the Gibbs Sampler: " + ex.toString());
                    evaluationListener.onError(jobRef, status);
                }
            }

            // remove the gs executor from the queue
            gsExecutorQueue.poll();
        }
    }

    
    /**
     * Get historical data according to the from-to dates
     * 
     * @param endDate The date to which historical data should be collected.
     * @return An empty map if no data in the database for the given time
     * period.
     * @throws Exception If there are any technical issues with extracting the
     * historical data.
     * @throws Throwable 
     */
    private Map<String, List<Number>> getHistoricalData(Date endDate) throws Exception, Throwable {

        log.info("Getting historical data for endDate " + endDate);
        
        if (dataExtractor == null) {
            log.error("\n\n\ndataExtractor not autowired :( \n\n\n");
        }
        Map<String, List<Number>> snapshotDataMap = dataExtractor.getSnapshotDataMap(jobConfig.getCommunityDetails().getCommunityID(), endDate, forecastPeriod, totalPeriods);
        log.info("Data collection completed");
        
        // TODO: remove logging of snapshot data map
        log.info("Snapshot data map (REMOVE THIS WHEN DONE TESTING):");
        for (String userID : snapshotDataMap.keySet())
        {
            String outputString = userID + ":";
            for (Number num : snapshotDataMap.get(userID)) {
                outputString += "  " + num;
            }
            log.info(" - " + outputString);
        }
     
        return snapshotDataMap;
    }


    @Override
    public void startJob(String jobRef, PredictorServiceJobConfig config) throws Exception {
        this.jobRef = jobRef;
        this.jobConfig = config;
        this.currentDate = jobConfig.getStartDate();
        this.stream = jobConfig.isStreaming();
        this.forecastPeriod = FrequencyType.WEEKLY; // Ignoring the forecastPeriod in the jobConfig because it is fixed
        
        Set<Event> setEvent = jobConfig.getEvents();
        // We are assuming that there will only be single event for now
        for (Event evt : setEvent) {
            
            // We are assuming that there will be only one event condition for now
            EventCondition ec = evt.getEventConditionByID(UUID.fromString("87084ba9-edac-4e79-b23a-76a14e52730e"));
            dropThreshold = Double.valueOf(ec.getPostConditionValue().getValue());

            for (Parameter p : jobConfig.getConfigurationParams()) {
                // 2. Number of covariates
                if (p.getUUID().equals(UUID.fromString("75302132-65db-4c59-a8f0-d2c4ae702de3"))) {
                    ParameterValue pv = p.getValue();
                    totalPeriods = Integer.valueOf(pv.getValue()); log.info("The number of periods to observe is: " + totalPeriods);
                }
                // 3. Active definition
                if (p.getUUID().equals(UUID.fromString("0dce1066-5c85-4fd0-a115-a5d66ddda6b7"))) {
                    ParameterValue pv = p.getValue();
                    activeThreshold = Double.valueOf(pv.getValue()); log.info("The active threshold for observations is: " + activeThreshold);
                }
            }
        }

        this.forecastDate = DateUtil.getToDate(currentDate, forecastPeriod);

        // starting the thread
        evaluatorThread.start();
    }

    @Override
    public void cancelJob() {
        status = new JobStatus(JobStatusType.CANCELLED);
        log.debug("Cancelled job");
    }

    @Override
    public JobStatus getJobStatus() {
        return status;
    }

    @Override
    public void setEvaluationListener(IEvaluationListener evaluationListener) {
        this.evaluationListener = evaluationListener;
    }

    /**
     * Executes the Gibbs Sampler, given the snapshot data map. The current date
     * and forecast date are used to create an evaluation result. After the
     * Gibbs Sampler has run, an evaluation result is created and notified to
     * the listener if successful; otherwise an error notification is given.
     *
     * @param snapshotDataMap Snapshot data map, where the key should be the
     * user ID and values a list of snapshot data.
     * @param currentDate The current date from which a forecast is made.
     * @param forecastDate The date of the forecast - used when creating an
     * evaluation result.
     */
    private synchronized void executeGibbsSampler(Map<String, List<Number>> snapshotDataMap, Date currentDate, Date forecastDate) {
        log.info("Executing the Gibbs Sampler (" + currentDate + " --> " + forecastDate + ")");
        GSExecutor exec = new GSExecutor(snapshotDataMap, dropThreshold, activeThreshold);
        gsExecutorQueue.add(exec);
        exec.execute();
    }
    
    /**
     * Setting the Boardsie data extractor, which was autowired in the 
     * PredictorServiceImpl class.
     * @param dataExtractor Boardsie data extractor.
     */
    public void setBoardsieDataExtractor (BoardsieDataExtractor dataExtractor)
    {
        this.dataExtractor = dataExtractor;
    }
}
