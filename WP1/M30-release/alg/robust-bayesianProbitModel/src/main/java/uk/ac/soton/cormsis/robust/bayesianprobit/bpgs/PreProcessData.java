/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton
// CORMSIS
// Centre of Operational Research, Management Science and Information Systems
// 2012
//
// Copyright in this software belongs to University of Southampton
// CORMSIS,
// University of Soutampton,
// Highfield Campus,
// Southampton,
// SO17 1BJ,
// UK
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
//      Created By :            Philippa Hiscock
//      Created Date :          2012-10-16
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.bpgs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import uk.ac.soton.cormsis.robust.bayesianprobit.utils.ActiveFilter;
import uk.ac.soton.cormsis.robust.bayesianprobit.utils.FindValue;
import uk.ac.soton.cormsis.robust.bayesianprobit.utils.InputChecks;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;
import uk.ac.soton.cormsis.robust.stats.utils.display;

public class PreProcessData {

    static Logger log = Logger.getLogger(PreProcessData.class);

    /**
     * Constructor to pre-process raw data before running Bayesian probit model
     *
     * @param md model data object to store data
     * @param mapData input data
     * @param isHistorical true if the prediction phase is observed
     * @param isPercentile true if response variable is peer based
     * @param isActiveSum whether to use sum or average statistic when
     * determining observations as active
     * @param activeThreshold threshold for sum of variables for each
     * observation to be over for that observation to be defined as active
     * @param thresholdVector vector of thresholds for changes in activity -
     * used to define categories
     * @throws Exception
     */
    public PreProcessData(ModelData md,
            Map<String, List<Number>> mapData,
            boolean isHistorical,
            boolean isPercentile,
            boolean isActiveSum,
            double activeThreshold,
            double[] thresholdVector) throws Exception {

        //TODO: move up as a variable!
        boolean isDescending = false;

        int numCls = thresholdVector.length + 1;
        log.info("The number of classifications possible is: " + numCls);
        log.info("The number of contributors (observations) is: " + mapData.size());

        int numCov = mapData.entrySet().iterator().next().getValue().size();
        for (Map.Entry<String, List<Number>> e : mapData.entrySet()) {
            numCov = e.getValue().size();
            log.info("The number of covariates observed is: " + numCov);
            break;
        }
        Map<String, List<Number>> mapRefData = new HashMap<String, List<Number>>();
        if (isActiveSum == true) {
            mapRefData = ActiveFilter.activeSum(mapData, activeThreshold, numCov);
        } else {
            mapRefData = ActiveFilter.activeAv(mapData, activeThreshold, numCov);
        }
        log.info("The number of active contributors (observations) is: " + mapRefData.size());

        //Draw out vector of refined Observation identification strings & corresponding matrix of observations
        int i = 0;
        String[] strRefLabels = new String[mapRefData.size()];
        double[][] dblRefObservations = new double[mapRefData.size()][numCov];
        for (Map.Entry<String, List<Number>> e : mapRefData.entrySet()) {
            strRefLabels[i] = e.getKey();
            List<Number> y = e.getValue();
            for (int j = 0; j < numCov; j++) {
                dblRefObservations[i][j] = y.get(j).doubleValue();
            }
            i++;
        }
        ////Check rank of matrix valid
        if (InputChecks.checkMatrixRankValid(dblRefObservations, isHistorical) != true) {
            throw new ExceptionRankDeficiency("Gibbs Sampling cannot be applied - num covariates outweighs num contributors");
        }

        // Create the augmented matrix
        double[][] centeredMatrix = GeneralMatrixOperation.centeredMatrix(dblRefObservations);

        // Create the x learn/predict matrix 
        double[][] xLearnMatrix = createXMatrix(centeredMatrix, isHistorical, true, 0);
//        System.out.println("xLearnMatrixT:");
//        display.print(GeneralMatrixOperation.transposeMatrix(xLearnMatrix));
        ////Check x^{T}x may be pos def
        if (InputChecks.checkMatrixMayPosDef(MatrixMultiplication.innerProduct(xLearnMatrix)) != true) {
            throw new ExceptionPositiveDefiniteMatrix("Gibbs Sampling cannot be applied due to explanatory data");
        }
        double[][] xPredictMatrix = createXMatrix(centeredMatrix, isHistorical, false, 1);
        //display
//        System.out.println("xPredictMatrix");
//        display.print(xPredictMatrix);

        // Create the y learn/predict matrix
        double[] yLearnVector;
        if (isPercentile == true) {
            yLearnVector = createPercentileClass(strRefLabels, dblRefObservations, thresholdVector, isHistorical, true, isDescending);
        } else {
            yLearnVector = createIndividClass(dblRefObservations, thresholdVector, isHistorical, true);
        }
        ////Check yLearn not all 0/1
        if (InputChecks.checkYValid(yLearnVector, numCls) != true) {
            throw new ExceptionNonIdentifiable("Gibbs Sampling cannot be applied due to lack of overlap in binary responses");
        }
        double[] yPropVector = createYProportionVector(yLearnVector, (thresholdVector.length + 1));

        if (isHistorical == true) {
            double[] _yPredictVector;
            if (isPercentile == true) {
                _yPredictVector = createPercentileClass(strRefLabels, dblRefObservations, thresholdVector, isHistorical, false, isDescending);
            } else {
                _yPredictVector = createIndividClass(dblRefObservations, thresholdVector, isHistorical, false);
            }
            System.out.println("yP :");
            display.print(_yPredictVector);
            setModelData(md, strRefLabels, dblRefObservations, xLearnMatrix, xPredictMatrix, yLearnVector, yPropVector, _yPredictVector);
        } else {
            setModelData(md, strRefLabels, dblRefObservations, xLearnMatrix, xPredictMatrix, yLearnVector, yPropVector);
        }

    }

//    /**
//     * Constructor to pre-process raw data before running Bayesian probit model
//     *
//     * @param md model data object to store data
//     * @param mapData input data
//     * @param isHistorical true if the prediction phase is observed
//     * @param yL vector of observed responses for observations of mapData
//     * @throws Exception
//     */
//    public PreProcessData(ModelData md, Map<String, List<Number>> mapData, boolean isHistorical, int[] yL) throws Exception {
//
//        log.info("The number of contributors (observations) is: " + mapData.size());
//
//        //Find number of covariate
//        String id = mapData.keySet().iterator().next();
//        int numCovariate = mapData.get(id).size();
//
//        //Draw out vector of Observation identification strings & corresponding matrix of observations
//        int i = 0;
//        String[] strRefLabels = new String[mapData.size()];
//        double[][] dblRefObservations = new double[mapData.size()][numCovariate];
//        for (Map.Entry<String, List<Number>> e : mapData.entrySet()) {
//            strRefLabels[i] = e.getKey();
//            List<Number> y = e.getValue();
//            for (int j = 0; j < numCovariate; j++) {
//                dblRefObservations[i][j] = y.get(j).doubleValue();
//            }
//            i++;
//        }
//        log.info("No subsetting of contributors by activity is currently done here.");
//        ////Check rank of matrix valid
//        if (InputChecks.checkMatrixRankValid(dblRefObservations, isHistorical) != true) {
//            throw new ExceptionRankDeficiency("Gibbs Sampling cannot be applied - num covariates outweighs num contributors");
//        }
//
//        // Create the augmented matrix
//        double[][] centeredMatrix = GeneralMatrixOperation.centeredMatrix(dblRefObservations);
//
//        // Create the x learn/predict matrix 
//        double[][] xLearnMatrix = createXMatrix(centeredMatrix, isHistorical, true, 0);
//        ////Check x^{T}x may be pos def
//        if (InputChecks.checkMatrixMayPosDef(MatrixMultiplication.innerProduct(xLearnMatrix)) != true) {
//            throw new ExceptionPositiveDefiniteMatrix("Gibbs Sampling cannot be applied due to explanatory data");
//        }
//        double[][] xPredictMatrix = createXMatrix(centeredMatrix, isHistorical, false, 1);
//
//        // Create the y learn/predict matrix
//        double[] yLearnVector = new double[yL.length];
//        for (int j = 0; j < yL.length; j++) {
//            yLearnVector[j] = (double) yL[j];
//        }
//        //Cannot check yL without numCls possible
////        if (InputChecks.checkYValid(yLearnVector,numCls) != true) {
////            throw new ExceptionNonIdentifiable("Gibbs Sampling cannot be applied due to lack of overlap in binary responses");
////        }
//        double[] yPropVector = createYProportionVector(yLearnVector);
//
//        setModelData(md, strRefLabels, dblRefObservations, xLearnMatrix, xPredictMatrix, yLearnVector, yPropVector);
//    }

    /**
     * Utility method to create the X matrix (for learning and prediction)
     *
     * @param dataMatrix the whole 'active' data matrix
     * @param isHistorical true if the prediction phase is observed
     * @param isLearn whether creating for learning or prediction phase
     * @param startIndex starting index of covariates for phase
     * @return
     */
    private double[][] createXMatrix(double[][] dataMatrix, boolean isHistorical, boolean isLearn, int startIndex) {
        int numObs = dataMatrix.length;
        int numCov = dataMatrix[0].length;
        double[][] xMatrix = new double[numObs][];

        int historicalOffset = (isHistorical) ? -1 : 0;
        int learnOffset = (isLearn) ? 0 : 1;
        int dataLength = numCov - 1; //as index start @ 0

        int iFinal = dataLength + historicalOffset + learnOffset;
        int numXCov = dataLength + historicalOffset;
        double[][] tdataMatrix = GeneralMatrixOperation.transposeMatrix(dataMatrix);
        double[][] txMatrix = new double[numXCov][];

        for (int j = learnOffset; j < iFinal; j++) {
            txMatrix[(j - learnOffset)] = tdataMatrix[j];
        }
        xMatrix = GeneralMatrixOperation.augmentedMatrix(GeneralMatrixOperation.transposeMatrix(txMatrix));

        return (xMatrix);
    }

    /**
     * Utility method to calculate the percentage decrease from the penultimate
     * period to the ultimate
     *
     * @param p penultimate vector of observations
     * @param u ultimate vector of observations
     * @return vector length numObs containing percentage change from p to u
     */
    private double[] calcPercentageChange(double[] p, double[] u) {
        int numObs = p.length;
        double[] dblPChange = new double[numObs];

        double[] d = VectorCalculation.subtract(p, u);
        dblPChange = VectorCalculation.divide(d, p);
        //hack for observations who stay at 0 from p to u
        for (int i = 0; i < numObs; i++) {
            if (p[i] == 0 && u[i] == 0) {
                dblPChange[i] = 0;
            }
        }

        return dblPChange;
    }

    /**
     * Utility method to create vector of sequence.
     *
     * @param from start of sequence
     * @param to end of sequence
     * @param step step of sequence
     * @return vector of ints starting at from finishing at to and incremented
     * by step
     */
    private int[] seq(int from, int to, int step) {
        int length = (int) ((to - from) / step) + 1;
        int[] seq = new int[length];

        for (int i = from; i < to + 1; i = i + step) {
            seq[(i - from)] = i;
        }

        return seq;
    }

    /**
     * Utility method to create binary classification matrix from discrete
     * classification vector
     *
     * @param y discrete classification vector
     * @param numCls number of possible classifications
     * @return matrix of binary classifications
     */
    private double[][] createBinaryClsMat(double[] y, int numCls) {
        int numObs = y.length;
        double[][] yMat = new double[numObs][numCls];

        for (int i = 0; i < numObs; i++) {
            for (int c = 0; c < numCls; c++) {
                if (y[i] == c) {
                    yMat[i][c] = 1;
                    break;
                }
            }
        }

        return yMat;
    }

    /**
     * Utility method to create y vector for learning/prediction phase based on
     * percentage change
     *
     * @param dblRefObs matrix of refined observations (without ID)
     * @param clsThreshold vector of thresholds defining observation classes
     * @param isHist boolean indicating whether data is historical
     * @param isLearn boolean indicating learning/prediction phase
     * @return
     */
    private double[] createIndividClass(double[][] dblRefObs,
            double[] clsThreshold,
            boolean isHist,
            boolean isLearn) {
        int numObs = dblRefObs.length;
        int numCls = clsThreshold.length + 1;
        int numCov = dblRefObs[0].length;
        double[][] tdblRefObs = GeneralMatrixOperation.transposeMatrix(dblRefObs);
        double[] dblCls = new double[numObs];
        int[] classVector = seq(0, (numCls - 1), 1);

        if (isHist != true && isLearn != true) {
            return dblCls;
        }

        int histOffset = (isHist) ? -1 : 0;
        int learnOffset = (isLearn) ? 0 : 1;
        int dataLength = numCov - 1; //as index start @ 0

        int iPenult = dataLength + histOffset + learnOffset - 1;
        int iUlt = dataLength + histOffset + learnOffset;
        double[] penultVector = tdblRefObs[iPenult];
        double[] ultVector = tdblRefObs[iUlt];

        //calculate percentage change from p to u
        double[] dblPChange = calcPercentageChange(penultVector, ultVector);

        //classfiy observations
        for (int i = 0; i < numObs; i++) {
            boolean cls0 = false;
            if (dblPChange[i] < clsThreshold[0]) {
                dblCls[i] = classVector[0];
                cls0 = true;
            }
            if (numCls > 2) {
                for (int c = 1; c < (numCls - 1); c++) {
                    if (cls0 == true) {
                        break;
                    }
                    if (dblPChange[i] >= clsThreshold[(c - 1)] && dblPChange[i] < clsThreshold[c]) {
                        dblCls[i] = classVector[c];
                        break;
                    }
                }
            }
            if (dblPChange[i] >= clsThreshold[(numCls - 2)]) {
                dblCls[i] = classVector[(numCls - 1)];
            }
        }

        return dblCls;
    }

    /**
     * Utility method to create y vector for learning/prediction phase based on
     * observations' percentile occurrence.
     *
     * @param dblRefObs matrix of refined observations (without ID)
     * @param clsThreshold vector of thresholds defining observation classes
     * @param isHist boolean indicating whether data is historical
     * @param isLearn boolean indicating learning/prediction phase
     * @return
     */
    private double[] createPercentileClass(String[] strRefLabels,
            double[][] dblRefObs,
            double[] clsThreshold,
            boolean isHist,
            boolean isLearn,
            boolean isDescending) {
        int numObs = dblRefObs.length;
        int numCls = clsThreshold.length + 1;
//        log.info("Num cls is: " + numCls);
        int numCov = dblRefObs[0].length;
        double[][] tdblRefObs = GeneralMatrixOperation.transposeMatrix(dblRefObs);
        double[] dblCls = new double[numObs];
        int[] classVector = seq(0, (numCls - 1), 1);

        if (isHist != true && isLearn != true) {
            return dblCls;
        }

        int histOffset = (isHist) ? -1 : 0;
        int learnOffset = (isLearn) ? 0 : 1;
        int dataLength = numCov - 1; //as index start @ 0

        int iPenult = dataLength + histOffset + learnOffset - 1;
        int iUlt = dataLength + histOffset + learnOffset;
        double[] penultVector = tdblRefObs[iPenult];
        double[] ultVector = tdblRefObs[iUlt];

        //calculate percentage change from p to u
        double[] dblPChange = calcPercentageChange(penultVector, ultVector);
//        System.out.println("Percentage change:"); display.print(dblPChange);
        //create map for sort

        int[] intPosition = indexRanking(strRefLabels, dblPChange, isDescending);
//        System.out.println("Index Ranking:");
//        display.print(intPosition);
        //assume clsThreshold in %
        double[] dblCutoff = VectorCalculation.multiply(clsThreshold, numObs);
        int[] intCutoff = new int[(numCls - 1)];
        for (int c = 0; c < dblCutoff.length; c++) {
            intCutoff[c] = (int) Math.round(dblCutoff[c]);
        }

        //classfiy observations
        for (int i = 0; i < numObs; i++) {
            boolean cls0 = false;
            if (intPosition[i] <= intCutoff[0]) {
                dblCls[i] = classVector[0];
                cls0 = true;
            }
            if (numCls > 2) {
                for (int c = 1; c < (numCls - 1); c++) {
                    if (cls0 == true) {
                        break;
                    }
                    if (intPosition[i] > intCutoff[(c - 1)] && intPosition[i] <= intCutoff[c]) {
                        dblCls[i] = classVector[c];
//                        break;
                    }
                }
            }
            if (intPosition[i] > intCutoff[(numCls - 2)]) {
                dblCls[i] = classVector[(numCls - 1)];
            }
        }

        return dblCls;
    }

    /**
     * Utility function to find ranks for a set of observations
     *
     * @param strID array of observations unique identifiers
     * @param dblP array to be sorted
     * @param isDescending boolean of whether to sort ascending or descending
     * @return vector of indices for descending sort
     */
    private int[] indexRanking(String[] strID, double[] dblP, boolean isDescending) {
//        System.out.println("Percentage change: ");
//        display.print(dblP);
        int numObs = strID.length;
        int[] rank = new int[numObs];
        Map<String, Double> map = new HashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(map);
        Map<String, Double> sortedMap = new TreeMap<String, Double>(bvc);
        Map<String, Integer> mapRank = new HashMap<String, Integer>();

        //put data in map for sort
        for (int i = 0; i < numObs; i++) {
            map.put(strID[i], new Double(dblP[i]));
        }

        //sort
//        System.out.println("unsorted map: " + map);
        sortedMap.putAll(map);
//        System.out.println("results: " + sortedMap);

        //award rankings
        int ie = 0;
        for (Map.Entry<String, Double> e : sortedMap.entrySet()) {
            ie++;
            mapRank.put(e.getKey(), new Integer(ie));
        }
//        display.printmSI(mapRank);

        for (int i = 0; i < numObs; i++) {
            rank[i] = mapRank.get(strID[i]).intValue();
        }
//        System.out.println("Rank vector:"); display.print(rank);

        if (isDescending != true) {
            rank = VectorCalculation.subtract((numObs + 1), rank);
        }

        return rank;
    }

    /**
     * Utility function to return index of sorted array DOES NOT WORK WHEN P
     * ENTRIES NONUNIQUE
     *
     * @param p array to be sorted
     * @return vector of indices for ascending sort
     */
    private int[] indexRanking(double[] p) {
        int numObs = p.length;
        double[] sort = p;
        //sort ascending
        Arrays.sort(sort);
//        System.out.println("Sorted: ");
//        display.print(sort);

        int[] indexRank = new int[numObs];
        for (int i = 0; i < numObs; i++) {
            indexRank[i] = Arrays.binarySearch(sort, p[i]);
        }

        return indexRank;
    }

    /**
     * Utility method to calculate proportion of observations in each class
     *
     * @param y vector of classifications
     * @param numCls number of possible classifications
     * @return
     */
    private double[] createYProportionVector(double[] y, int numCls) {
        int numObs = y.length;
        double[] prop = new double[numCls];

        for (int c = 0; c < numCls; c++) {
            double count = 0;
            for (int i = 0; i < numObs; i++) {
                if (y[i] == c) {
                    count++;
                }
            }
            prop[c] = count / numObs;
        }

        return prop;
    }

    /**
     * Utility method to calculate the proportional occurrence of each category
     * for the observed response of the learning phase.
     *
     * @param y binary matrix of observed responses for learning/prediction
     * phase
     * @return Vector of proportions (length = numCat)
     */
    private double[] createYProportionVector(double[][] y) {
        double[] yProportionVector = new double[y[0].length];
        double[] yCatSumVector = new double[y[0].length];

        for (int c = 0; c < y[0].length; c++) {
            for (int i = 0; i < y.length; i++) {
                yCatSumVector[c] = yCatSumVector[c] + y[i][c];
            }
            yProportionVector[c] = yCatSumVector[c] / y.length;
        }

        return (yProportionVector);
    }

//    /**
//     * Utility method to calculate the proportional occurrence of each category
//     * for the observed response of the learning phase.
//     *
//     * @param y categorical vector of observed responses for learning/prediction
//     * phase
//     * @return Vector of proportions (length = numCat)
//     */
//    private double[] createYProportionVector(double[] y) {
//
//        double[] Cat = FindValue.findUnique(y);
//
//        double[] yProportionVector = new double[Cat.length];
//        double[] yCatSumVector = FindValue.countUnique(Cat, y);
//
//        for (int c = 0; c < Cat.length; c++) {
//            yProportionVector[c] = yCatSumVector[c] / y.length;
//        }
//
//        return (yProportionVector);
//    }

    /**
     * Fill type ModelData for return
     *
     * @param rLabels vector of strings of labels(/userID) for refined data set
     * @param xL matrix of observations for learning phase
     * @param xP matrix of observations for prediction phase
     * @param yL vector of observed responses for learning phase
     * @param yLProp vector of proportion of observed categories
     * @return
     */
    private ModelData setModelData(ModelData md, String[] rLabels, double[][] rObs, double[][] xL, double[][] xP, double[] yL, double[] yLProp) {

        md.setRefinedLabels(rLabels);
        md.setRefinedObservations(rObs);
        md.setXLearn(xL);
        md.setXPred(xP);
        md.setYLearn(yL);
        md.setYLearnProp(yLProp);

        return md;
    }

    /**
     * Fill type ModelData for return
     *
     * @param rLabels vector of strings of labels(/userID) for refined data set
     * @param xL matrix of observations for learning phase
     * @param xP matrix of observations for prediction phase
     * @param yL vector of observed responses for learning phase
     * @param yP vector of observed responses for prediction phase
     * @param yLProp vector of proportion of observed categories
     * @return
     */
    private ModelData setModelData(ModelData md, String[] rLabels, double[][] rObs, double[][] xL, double[][] xP, double[] yL, double[] yLProp, double[] yP) {

        md.setRefinedLabels(rLabels);
        md.setRefinedObservations(rObs);
        md.setXLearn(xL);
        md.setXPred(xP);
        md.setYLearn(yL);
        md.setYLearnProp(yLProp);
        md.setYPred(yP);

        return md;
    }
}


class ValueComparator implements Comparator<String> {

    Map<String, Double> base;

    protected ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.
    @Override
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
