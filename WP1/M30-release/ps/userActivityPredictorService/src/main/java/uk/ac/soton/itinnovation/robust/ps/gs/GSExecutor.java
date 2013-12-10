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
//      Created Date :          2012-10-16
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.ps.gs;

//import com.polecat.prediction.GibbsSampler;
//import com.polecat.prediction.LabelledObservations;
//import com.polecat.prediction.PredictionException;
//import com.polecat.prediction.PredictionResult;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.ModelData;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.RunModel;
//import uk.ac.soton.cormsis.robust.multinomialgs.mgs.*;
//import uk.ac.soton.cormsis.robust.multinomialgs.utils.GibbsSamplerToThrow;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValidationObject;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;

/**
 * A helper class to execute the Gibbs Sampler and generate an evaluation
 * result.
 *
 * @author Vegard Engen
 */
public class GSExecutor implements Runnable {
    // input parameters

    private double eventThreshold;
    private double activeThreshold;
    private Map<String, List<Number>> snapshotDataMap;
    // output and job status
    private JobStatus status;
    private Double currentObservation;
//    private PredictionResult<String> predictionResult;
    private ModelData modelData = new ModelData();
    // other parameters, pre-configured in constructor
    private int numberOfRetainedIterations;
    private int numberOfDiscardedIterations;
    // Thread
    private Thread gsExecutorThread;
    // logging
    static Logger log = Logger.getLogger(GSExecutor.class);

    /**
     * Constructor to set the input data and activity threshold for the
     * pre-processing.
     *
     * @param snapshotDataMap Map of data records and respective feature values.
     * @param eventThreshold The threshold defining the binary event.
     * @param activeThreshold Threshold for pre-processing - any users' activity
     * not above this threshold will be filtered out.
     */
    public GSExecutor(Map<String, List<Number>> snapshotDataMap, double eventThreshold, double activeThreshold) {
        this.snapshotDataMap = snapshotDataMap;
        this.eventThreshold = eventThreshold;
        this.activeThreshold = activeThreshold;
        this.numberOfRetainedIterations = 50;
        this.numberOfDiscardedIterations = 200;
        this.status = new JobStatus(JobStatusType.EVALUATING);
    }

    @Override
    public void run() {
        try {
            // apply preprocessing to the data in the map
            // don't do this here - do this later after moved to labelled observations
            //        preprocess(snapshotDataMap);

//            LabelledObservations<String> labelledObservations = new LabelledObservations<String>();

            //System.out.println("Input data:");
//            try {
//                for (String key : snapshotDataMap.keySet()) // key is assumed to be the user ID
//                {
//                    //String dataString = key + ": ";
//                    double[] array = new double[snapshotDataMap.get(key).size()];
//                    for (int i = 0; i < snapshotDataMap.get(key).size(); i++) {
//                        Number num = snapshotDataMap.get(key).get(i);
//                        array[i] = num.doubleValue();
//                        //dataString += num.toString() + "\t";
//                    }
//                    labelledObservations.add(key, array);
//                    //System.out.println("  - " + dataString);
//                }
//            } catch (Throwable t) {
//                log.error("Failed to preprocess the data before executing the GS: " + t.toString(), t);
//                status = new JobStatus(JobStatusType.ERROR, "Failed to preprocess the data before executing the GS: " + t.toString());
//                throw t;
//            }

            //        double[][] gsData = preprocess(labelledObservations);
//            log.info("Pre-processing the input data");
//            LabelledObservations<String> refinedLabelledObservations = null;
//            try {
//                refinedLabelledObservations = preprocess(labelledObservations, activeThreshold);
//            } catch (Throwable t) {
//                log.error("Failed to preprocess the data before executing the GS: " + t.toString(), t);
//                status = new JobStatus(JobStatusType.ERROR, "Failed to preprocess the data before executing the GS: " + t.toString());
//                throw t;
//            }

//            log.info("Initialising the gibbs sampler");
//            GibbsSampler<String> g = GibbsSampler.getInstance(refinedLabelledObservations, false); // false = not on historical data
//            GibbsSampler<String> g = GibbsSampler.getInstance(refinedLabelledObservations, activeThreshold, false); // false = not on historical data
//            g.setNumberOfDiscardedIterations(numberOfDiscardedIterations);
//            g.setNumberOfRetainedIterations(numberOfRetainedIterations);

            try {
                log.info("Running the gibbs sampler");
//                predictionResult = g.sample();
                ////--------------------- Initialise Probit GS ---------------------////
                boolean isHistorical = false; double[] clsThreshold = {eventThreshold};
                boolean isPercentile = false; boolean isActiveSum = false;
                int b = numberOfDiscardedIterations; int r = numberOfRetainedIterations;
                RunModel pgs = new RunModel(snapshotDataMap, isHistorical, isPercentile, isActiveSum, activeThreshold, clsThreshold, b, r);
                ////------------------- Get Resulting Model Data -------------------////
                modelData = pgs.returnModelData();
            } catch (Exception e) {
                log.error("Failed to execute the GS: " + e.toString(), e);
                status = new JobStatus(JobStatusType.ERROR, "Failed to execute the GS: " + e.toString());
                throw e;
            }

//            ValidationObject vo = isPredictionResultValid(predictionResult);
            ValidationObject vo = isPredictionResultValid(modelData);
            if (vo.valid) {
                status = new JobStatus(JobStatusType.FINISHED);
            } else {
                status = new JobStatus(JobStatusType.ERROR, "The prediction result generated by the GS is invalid: " + vo.msg);
            }
        } catch (Throwable t) {
            if (status.getStatus() != JobStatusType.ERROR) { // if already set to ERROR, then the exception was just thrown to stop further code from executing
                log.error("Failed to execute the Gibbs Sampler: " + t.toString(), t);
                status = new JobStatus(JobStatusType.ERROR, "Failed to execute the Gibbs Sampler: " + t.toString());
            }
        }

        log.info("GSExecutor thread finished");
    }

    /**
     * Execute the Gibbs Sampler with the given data provided in the
     * constructor. Will create a thread and start it.
     *
     * @return
     */
    public void execute() {
        if (gsExecutorThread == null) {
            gsExecutorThread = new Thread((GSExecutor) this, "Gibbs Sampler Executor Thread");
        }

        if (!gsExecutorThread.isAlive()) {
            gsExecutorThread.start();
        }
    }

    /**
     *
     * @return The status of the execution.
     */
    public JobStatus getStatus() {
        return status;
    }

//    private LabelledObservations preprocess(LabelledObservations<String> rawInput, double activeThreshold) {
//        // TODO: implement GSExecutor preprocessing method - e.g., removing users not exceeding a certain activity threshold
//
//        // Convert the rawInput into a 2 dimensional array of double primitives
//        double[][] rawObservations = new double[rawInput.size()][];
//        String[] strLabels = new String[rawInput.size()];
//        for (int i = 0; i < rawInput.size(); i++) {
//            rawObservations[i] = rawInput.get(i);
//        }
//        List<String> lstLabels = rawInput.getLabels();
//        for (int i = 0; i < strLabels.length; i++) {
//            strLabels[i] = (String) lstLabels.get(i);
//            if (strLabels[i] == null) {
//                strLabels[i] = "Null UserID";
//            }
//        }
//
//        //System.out.println("Raw observations:");
//        //display.print(rawObservations);
//        //System.out.println("");
////        display.print(strLabels);
//
//        // Find indicies of active users
//        int[] activeIndex;
//        activeIndex = GibbsSamplerToThrow.indexRefineData(rawObservations, activeThreshold);
//        //System.out.println("Active Indices:");
//        //display.print(activeIndex);
//        //System.out.println("");
//
//        // Use activeIndex to refine observations for active users only
//        double[][] refinedObservations;
//        refinedObservations = GibbsSamplerToThrow.refineData(rawObservations, activeIndex);
//        //System.out.println("Refined observations:");
//        //display.print(refinedObservations);
//        //System.out.println("");
//
//        // Use activeIndex to refine labels to active users only
//        String[] strRefinedLabels;
//        strRefinedLabels = GibbsSamplerToThrow.refineLabels(strLabels, activeIndex);
//        lstLabels = (List<String>) Arrays.asList(strRefinedLabels);
//        //System.out.println("Refined labels:");
//        //display.print(strRefinedLabels);
//        //System.out.println("");
//
//        LabelledObservations<String> refLO = new LabelledObservations<String>();
//        //System.out.println("So, we finally have:");
//        for (int i = 0; i < refinedObservations.length; i++) {
//            refLO.add(lstLabels.get(i), refinedObservations[i]);
//            /*String dataString = lstLabels.get(i) + ": ";
//             for (int j = 0; j < refinedObservations[i].length; j++) {
//             dataString += refinedObservations[i][j] + " \t";
//             }
//             System.out.println("  " + dataString);*/
//        }
//
//        return refLO;
//    }

    private ValidationObject isPredictionResultValid(ModelData modelData) {
//            PredictionResult<String> predictionResult) {
        if (modelData == null) {
            return new ValidationObject(false, "The prediction result returned is NULL");
        }

//        if (predictionResult.getResultRecords() == null) {
//            return new ValidationObject(false, "The prediction result record collection is NULL");
//        }
        if (modelData.getPLearn() == null) {
            return new ValidationObject(false, "The prediction result record collection for learning phase is NULL");
        }

        if (modelData.getPPred() == null) {
            return new ValidationObject(false, "The prediction result record collection for prediction phase is NULL");
        }
        
//        if (predictionResult.getResultRecords().isEmpty()) {
//            return new ValidationObject(false, "No prediction result record collection is empty");
//        }

        return new ValidationObject(true);
    }

    /**
     * Creates and returns an EvaluationResult item from the prediction result
     * generated by executing the Gibbs Sampler.
     *
     * @param currentDate The date of the last known historical data.
     * @param forecastDate The date of the forecasted results.
     * @return An EvaluationResult object, provided the Gibbs Sampler has
     * executed successfully prior to this call.
     */
    public EvaluationResult getEvaluationResult(Date currentDate, Date forecastDate) throws Exception {
//        if (predictionResult == null) {
        if (modelData == null) {
            throw new RuntimeException("There's no prediction result - perhaps the GS was not executed, or there was an error");
        }

        EvaluationResult evalRes = new EvaluationResult();

        try {
            log.debug("AAaaaand the results are....");
//            for (PredictionResult<String>.Record record : predictionResult) {
//                // TODO: set the current observation
//                ResultItem ri = new ResultItem(record.getLabel(), record.getPredictionResult());
//                evalRes.addResultItem(ri);
//                log.debug("  " + ri.getName() + ": " + ri.getProbability());
//            }
            double[][] predictionResult = modelData.getPPred(); int iCatTrue = (predictionResult.length-1);
            String[] label = modelData.getRefinedLabels();
            int numObs = label.length;
            for (int i=0; i<numObs; i++) {
                ResultItem ri = new ResultItem(label[i],predictionResult[i][iCatTrue]);
                evalRes.addResultItem(ri);
                log.debug("  " + ri.getName() + ": " + ri.getProbability());
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to process the prediction results. " + t.toString(), t);
        }

        evalRes.setCurrentDate(currentDate);
        evalRes.setForecastDate(forecastDate);

        return evalRes;
    }
}