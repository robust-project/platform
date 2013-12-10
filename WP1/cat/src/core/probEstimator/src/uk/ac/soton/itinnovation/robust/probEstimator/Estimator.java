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
//      Created By :            Bassem Nasser
//      Created Date :          06-Sep-2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.probEstimator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

public class Estimator {

    static final Logger log = Logger.getLogger(Estimator.class);
    //assuming the data series are per week
    private TreeMap<Date, Double> data = null;
    private double[][] doubleData = null;
    private int iterations = 30;

    public static void main(String[] args) {
         BasicConfigurator.configure();
        Estimator est = new Estimator();

        est.setIterations(30);
        //get timeseries data yt, and store it under treemap<date, double> and double[][]
        est.setData(est.getDummyData());
        //predict likelihood in 1 week time of threshold 6 
        est.assessFutureProb(DateUtil.getToDate(est.getData().lastKey(), FrequencyType.WEEKLY,1), EvaluationType.LESS_OR_EQUAL, 50);
    }

    public int getIterations() {
        return iterations;
    }

    /**
     * this specifies the number of iterations to be performed (adding noise,
     * regression, prediction) the bigger the number, the better the accuracy
     * when computing the likelihood default is set to 30 iterations
     * if iterations is set to zero, then no noise is applied. one simple regression is then done to compute likelihood.
     */
    public void setIterations(int iterations) {
        if(iterations<0)
            throw new IllegalArgumentException("iterations must be positive");
        
        this.iterations = iterations;
    }

    public TreeMap<Date, Double> getData() {
        return data;
    }

    /**
     * set the time series data
     */
    public void setData(TreeMap<Date, Double> mapdata) {       
        this.data = mapdata;
       log.debug("data: " +this.data);
        this.doubleData = ConvertDateMapToDouble(this.data);

    }
    
     /**
     * set the time series data
     */
    public void setData(HashMap<Date, Double> mapdata) {
        if (mapdata == null || mapdata.isEmpty()) {
            this.data = null;
        } else {
            TreeMap<Date, Double> treeMap=new TreeMap<Date, Double>(mapdata);
            this.data = treeMap;
            log.debug("data: " + data);
            
        }
        this.doubleData = ConvertDateMapToDouble(this.data);

    }

    /**
     * assess the future probability in future weeks
     *
     * @param futureSteps the number of weeks in the future for prediction
     * @param eType comparison qualifier <, >, <=, >=. the other ones =, != may
     * not make sense according to the case
     * @param threshold the threshold value
     */
    public double assessFutureProb(Date futureDate, EvaluationType eType, double threshold) {
        if (getDoubleData() == null) {
            throw new RuntimeException("No data provided. Please add your data first.");
        }
        //TODO compare future date with latest date?
//        if(futureSteps<0)
//            throw new IllegalArgumentException("futureWeekStep must be > 0!");

        Set<Double> predictionSet = new HashSet<Double>();
        log.debug("apply regression without noise ");
        SimpleRegression regression = fitRegression(getDoubleData());
        //compute future value, last time +futureSteps
        
        double predictValue = predict(regression, ConvertDateToDouble(futureDate));//assuming ordered dates, next one is add multiple of 7 days

        predictionSet.add(predictValue);

        //calculate errors using the existing timeseries points
        double[] errorSet = getErrors(regression, getDoubleData());

        //calculate mean and standard deviation of the error
        DescriptiveStatistics ds = new DescriptiveStatistics();
        // Add the data from the error array
        for (double value : errorSet) {
            ds.addValue(value);
        }

        double errMean = ds.getMean();
        double errStdDev = ds.getStandardDeviation();

        log.debug("Error timeseries standard deviation is "+errStdDev);
        log.debug("Error timeseries mean is "+errMean+"\n");
        
        if (errStdDev != 0) {
            //create a normal distribution using the mean and variane of error
            NormalDistribution nds = new NormalDistribution(errMean, errStdDev);
            log.debug("Applying noise \n");
            Set<Double> newpredictSet = addNoiseAndPredict(getDoubleData(), nds, ConvertDateToDouble(futureDate));
            predictionSet.addAll(newpredictSet);
        }
        
        //compute likelihood of value being above threshold
        double probability = getProbability(predictionSet, eType, threshold);

        return probability;

    }

    private double getProbability(Set<Double> predictionSet, EvaluationType eType, double threshold) {
        //number of items greater than or equal threshold
        int counter = 0;
        for (Double pred : predictionSet) {
            switch (eType) {
                case EQUAL:
                    if (pred == threshold) {
                        counter++;
                    }
                    break;
                case LESS:
                    if (pred < threshold) {
                        counter++;
                    }
                    break;
                case LESS_OR_EQUAL:
                    if (pred <= threshold) {
                        counter++;
                    }
                    break;
                case GREATER:
                    if (pred > threshold) {
                        counter++;
                    }
                    break;
                case GREATER_OR_EQUAL:
                    if (pred >= threshold) {
                        counter++;
                    }
                    break;
                case IS:
                    if (pred == threshold) {
                        counter++;
                    }
                    break;
                case NOT:
                    if (pred != threshold) {
                        counter++;
                    }
                    break;
                default:
                    throw new AssertionError(eType.name());

            }

        }

        double prob = 0;

        prob = counter / ((double) predictionSet.size());
        log.debug("prob future value "+eType+" "+threshold+" = " + counter + "/" + predictionSet.size() + "=" + prob);
        return prob;
    }

    private Set<Double> addNoiseAndPredict(double[][] doubleData, NormalDistribution nds, double futuredate) {
        Set<Double> predictionSet = new HashSet<Double>();
        //Loop 1
        for (int j = 0; j < iterations; j++) {
            log.debug("iteration "+j);
            double[][] zArray = new double[doubleData.length][2];
            //Loop 2
            for (int i = 0; i < doubleData.length; i++) {
                //draw samples from the normal distribution dt
                double dt = nds.sample();
                //add this noise to yi, zt=yt+dt
                log.debug("adding noise " + dt);
                double zt = dt + doubleData[i][1];
                zArray[i][0] = doubleData[i][0];
                zArray[i][1] = zt;
            }//end loop2

            //fit new regression function on zt and calculate the new future value
            SimpleRegression errRegression = fitRegression(zArray);
            double newpredict = predict(errRegression, futuredate); //should add all the predicted values to a set
            //collect the different future values
            predictionSet.add(newpredict);
        }//end loop1

        return predictionSet;
    }

    private SimpleRegression fitRegression(double[][] data) {
        //fit regression function
        SimpleRegression regression = new SimpleRegression();
        regression.addData(data);

        // displays intercept of regression line
        log.debug("intercept= " +regression.getIntercept());  
        // displays slope of regression line
        log.debug("slope= "+regression.getSlope());
        
        return regression;
    }

    private double predict(SimpleRegression regression, double date) {

        //compute future value
        double predictValue = regression.predict(date);
        log.debug("predicted value for data " + date + "= " + predictValue+"\n");
        return predictValue;
    }

    private static double[][] ConvertDateMapToDouble(TreeMap<Date, Double> data) {
        if(data==null || data.isEmpty()){
//            throw new RuntimeException("Data is null or empty. Please provide data first.");
            return null;
        }
        
        try {
            double[][] twoDarray = new double[data.size()][2];
            Object[] keys = data.keySet().toArray();//date
            Object[] values = data.values().toArray();//double

            for (int row = 0; row < twoDarray.length; row++) {
                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                String s = df.format(keys[row]);
                double output = Double.valueOf(s);
                twoDarray[row][0] = output;
                twoDarray[row][1] = (Double) values[row];
            }

            return twoDarray;
        } catch (Exception ex) {
            throw new RuntimeException("error while loading the data. the date should be formatted as yyyyMMdd. " + ex);
        }
    }

    


    private double[][] getDoubleData() {
        return doubleData;
    }

    private void setDoubleData(double[][] doubleData) {
        this.doubleData = doubleData;
    }

        private static double ConvertDateToDouble(Date data) {
        if (data == null) {
            return 0;
        }

        try {

            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            String s = df.format(data);
            double output = Double.valueOf(s);

            return output;

        } catch (Exception ex) {
            throw new RuntimeException("error while loading the data. the date should be formatted as yyyyMMdd. " + ex);
        }
    }
        
    Date convertDoubleToDate(double data) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        try {
            String str = String.valueOf(data);
            Date date = formatter.parse(str);

            return date;
        } catch (Exception ex) {
            throw new RuntimeException("Error convering double to date. ", ex);
        }
    }
    /**
     * this is for testing purposes. It provides a set of dummy data to test the estimator
     */
    TreeMap<Date, Double> getDummyData() {
        TreeMap<Date, Double> dummyDataMap = new TreeMap<Date, Double>();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
            dummyDataMap.put(formatter.parse("20130902"), 10d);
            dummyDataMap.put(formatter.parse("20130909"), 52d);
            dummyDataMap.put(formatter.parse("20130916"), 6d);
            dummyDataMap.put(formatter.parse("20130923"), 78d);

        } catch (Exception ex) {
            throw new RuntimeException("error while loading dummy data. ", ex);
        }

        return dummyDataMap;
    }

    private double[] getErrors(SimpleRegression regression, double[][] doubles) {
        double[] errorArray = new double[doubles.length];
        //for each data entry, calculate the predicted value and then the error
        //the error set is a data series with the same dates as the doubledata
        for (int i = 0; i < doubles.length; i++) {
            double prediction = regression.predict(doubles[i][0]);
            double error = prediction - doubles[i][1];
            errorArray[i] = error;
        }

        return errorArray;
    }
}
