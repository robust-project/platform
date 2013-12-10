/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton
// CORMSIS
// Centre of Operational Research, Management Science and Information Systems
// 2012
//
// Copyright in this software belongs to University of Southampton
// CORMSIS
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
//      Created By :            Edwin Tye
//      Created Date :          2012-07-01
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.compartmentmodel.impl;

//import org.apache.commons.math3.linear.EigenDecomposition;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import uk.ac.soton.cormsis.robust.compartmentmodel.spec.ICompartmentModel;
import uk.ac.soton.cormsis.robust.stats.glm.impl.GLM;
import uk.ac.soton.cormsis.robust.stats.glm.spec.IGLM;
import uk.ac.soton.cormsis.robust.stats.lossfunction.impl.RegressionLoss;
import uk.ac.soton.cormsis.robust.stats.lossfunction.spec.IRegressionLoss;
import uk.ac.soton.cormsis.robust.stats.sampling.impl.Bootstrap;
import uk.ac.soton.cormsis.robust.stats.sampling.impl.SWR;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.IBootstrap;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.ISWR;
import uk.ac.soton.cormsis.robust.stats.utils.ArrayOperation;
import uk.ac.soton.cormsis.robust.stats.utils.LS;
import uk.ac.soton.cormsis.robust.stats.utils.LUDecomposition;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;

public class CompartmentModel implements ICompartmentModel {
    
    static Logger log = Logger.getLogger(CompartmentModel.class);

    public final double TORLERENCE = 1E-8;

    /* 
     * No more sampling method, this is due to the interpretaion of 'stochastic'
     * and the difference in result, i.e. if x \sim \N(0,1)
     * exp(\E{x}) \le \E{exp(x)}  
     * from jensen inequality
     */

    // No more choice for the method of forecasting the number of new users, 
    // because the only reasonble method to use is glm Poisson
    private IGLM glmfit;
    private ISWR Sampling;;
    private IBootstrap bootStrapError;
    private IRegressionLoss regressionLoss;

    // Total number of observation in terms of mass
    private int lengthTimeSeries;
    // Total number of groups 
    private int totalGroup;
    private int totalStep;
    // Not sure why I would need the total number of transitions
    //private int totalTransition;

    // Variables that gets initialized

    // There are currently not required, useless we think that storing some of 
    // information will take up too much memory

    // private double[] massInGroup;
    // private double[] massTotal;
    // private double[] newUser;
    private double[][] matForecastMean;
    private double[][] matForecastUCI;
    private double[][] matForecastLCI;

    private double[] currentObservation;

    //private double[][] matSimMeanLambda;
    // Assuming that the desire confidence interval is 5%;
    private double significantLevel = 0.025;
    //private ArrayList<Integer> listSample = new ArrayList<Integer>();

    // the default number of iteration to generate the confidence interval
    // This is the "realistic" time that seem to be suitable, as it doesn't take
    // too long and the compromise on accuracy is minimal
    private int iteration = (int) 1E5;

    // It is assumed that the penalizing term is at 0.9, unless being optimised
    private double alpha = 0.9;

    // The mass and rate matrix in ArrayList form, which will be the output of role collection
    private ArrayList<double[]> listMatMassInGroup = new ArrayList<double[]>();
    private ArrayList<double[][]> listMatLambda = new ArrayList<double[][]>();
    // The followings is to get the new user numbers for every single group
    // This needs to be set to Double purely because ArrayList only takes an object
    private ArrayList<Double> listNewUser = new ArrayList<Double>();
    private ArrayList<Double> listMassTotal = new ArrayList<Double>();
    // The actual forecast.  matrix of size [ T \times k]
    // private ArrayList<double[][]> listMassForecast = new ArrayList<double[][]>();
    // Testing code
    private double[][][] forecast3D;// = new double[][][];
    private int iterationCounter;
    // currently useless
    // But the natural assumption is that it is stable because the resulting matrix 
    // is actually a markov matrix
    private boolean stable = true;
    private boolean hasCI = false;

    // Computed variables

    /**
     * Empty constructor
     */
    public CompartmentModel() {};

    // Another method for initialization
    @Override
	public void initialize(ArrayList<double[]> m, ArrayList<double[][]> r) {
        this.listMatMassInGroup = m;
        this.listMatLambda = r;
        this.lengthTimeSeries = m.size();
        this.totalGroup = r.get(0).length;

        // Getting the total number of user at each time step
        for (int t = 0; t < this.lengthTimeSeries; t++) {
            double[] matMass = this.listMatMassInGroup.get(t);
            double[] dblNewUserInGroup = new double[this.totalGroup];
            double dblTemp = 0;
            for (int i = 0; i < this.totalGroup; i++) {
                // It can only be a column vector, impossible to have a matrix of mass
                dblTemp += matMass[i];
            }
            this.listMassTotal.add(dblTemp);
        }

	this.currentObservation = this.listMatMassInGroup.get(0);

        // Getting the number of new user joining the system
        // Going in reverse order as usual, so index 0 is the current time
        // I actually think that it is actually impossible to get the number of 
        // new user using this subtraction method
        for (int t = 1; t < this.listMatMassInGroup.size(); t++) {
            this.listNewUser.add(this.listMassTotal.get(t) - this.listMassTotal.get(t - 1));
        }
        // Need to think of a good way to determine the number of new user joining the system
        // Then getting the new user array?
    }

    @Override
	public double[] getCurrentObservation() {
	return this.currentObservation;
    }

    @Override 
	public double getCurrentObservation(int i) {
	if ( (i-1) > this.totalGroup) {
	    throw new IllegalArgumentException("That role does not exist");
	} else {
	    return this.currentObservation[i];
	}
    }

    @Override
	public int getForecastStep() {
	return this.totalStep;
    }

    @Override
	public int getTotalRole() {
	return this.totalGroup;
    }

    @Override
	public void computeForecast(int forecastStep) {
        double penalty = getOptimiseWeight(forecastStep);
        computeForecast(forecastStep, this.iteration, penalty);
    }

    @Override
	public void computeForecast(int forecastStep, int iter) {
        double penalty = getOptimiseWeight(forecastStep);
        computeForecast(forecastStep, iter, penalty);
    }

    @Override
	public void computeForecast(int forecastStep, int iter, double penalty) {
        computeForecastCI(forecastStep, iter, penalty);
        findForecastSummary();
    }

    // @Override
    // 	public double getProbabilityUnderThreshold(int groupid, int timestep, double threshold) {
    // 	double t = this.currentObservation[groupid] * ( 1 - threshold);
    // 	return getProbabilityUnderForecast(groupid, timestep, t);
    // }

    // Finds the probability that the number of user drops below certain threshold
    @Override
	public double getProbabilityUnderForecast(int groupid, int timeStep, double threshold) {
        return getProbabilityForForecast(groupid, timeStep, threshold);
    }

    @Override
	public double getProbabilityOverForecast(int groupid, int timeStep, double threshold) {
        return (1 - getProbabilityForForecast(groupid, timeStep, threshold));
        // return getProbabilityForForecast(groupid, timeStep, threshold);
    }

    @Override
	public double getProbabilityForecast(int[] groupid, int timeStep, double[] threshold,boolean[] under) {
        if (this.hasCI) {

	    // We start off by setting it to zero and build it up during the sampling process
	    int probability = 0;

	    int numRow = forecast3D.length;
	    int numCol = forecast3D[0].length;
	    int numIter = forecast3D[0][0].length;
	    int numCondition = groupid.length;

	    // Lets check that all the input parameter are actually valid
	    if (groupid.length != threshold.length) {
		throw new IllegalArgumentException("Number of group and threshold not of equal length");
	    }
	    if (groupid.length != under.length) {
		throw new IllegalArgumentException("Number of group and predicate not of equal length");
	    }
            if (timeStep > numRow) {
                throw new IllegalArgumentException("Have not computed forecast that far ahead");
            }
            log.debug("The number of input conditions = " +numCondition);
	    for (int i = 0; i < numCondition; i++) {
		if (groupid[i] > numCol) {
		    throw new IllegalArgumentException("Group index not valid");
		}
		/* It has to be noted that a threshold here is number of users and it is expected that
		 * if there is a percentage change, the absolute value will be calculated first and 
		 * such value will be passed through to this method. So the number is only invalid if
		 * it is under zero as we are assuming that the upper bound is going to be \infty
		 */
		if (threshold[i] < 0) {
		    throw new IllegalArgumentException("Not a valid threshold, the entered value = "+threshold[i]);
		}
	    }

            for (int t = 0; t < numIter; t++) {
		boolean satisfyCondition = true;
		for (int k = 0; k < numCondition; k++) {
                    // \P(X \le m^*)
                    if (this.forecast3D[timeStep-1][groupid[k]][t] > threshold[k]) {
                        if (under[k]) {
                            satisfyCondition = false;
                        }
                    } else {
                        if (under[k]) {
                        } else {
                            satisfyCondition = false;
                        }
                    }
		    // Using the equality sign
		    //		    if (under[k]) {
		    //                        // purely under m^*
		    //                        // \P(X \le m^*)
		    //			if (this.forecast3D[timeStep-1][groupid[k]][t] > threshold[k]) {
		    //			    satisfyCondition = false;
		    //			}
		    //		    } else {
		    //                        // \P(m^* \le X) 
		    //			if (this.forecast3D[timeStep-1][groupid[k]][t] <= threshold[k]) {
		    //			    satisfyCondition = false;
		    //			}
		    //		    }
		}
		if (satisfyCondition) {
		    probability++;
		}
	    }
	    
	    /* Changed it so that it will never produce a probability of 0 and 1, which means that
	     * although we are talking about taking numbers from the simulated EDF, we don't discount
	     * the fact that the simulation does not contain the full set of information as the 
	     * support may well be on the real line and bounded between -\infty and \infty
	     * This is similar to a bootstrap confident interval, where we create a total of R bootstrap 
	     * where (R+1) is a nice integer i.e. 1000 and the upper/lower CI is (R+1)\alpha
             * 
             * Not that because the end user have the ability to set the number of interation, the 
             * assumption here is to add one regardless
	     */
	    if (probability == 0) {
		return (double)(probability+1) / ((double)numIter+1);
	    } else {
		return (double)probability / ((double)numIter+1);
	    }
        } else {
            throw new IllegalArgumentException("Forecast not yet computed");
        }
    }

    @Override
	public double getProbabilityForecast(int[] groupid, int timeStep, double[] threshold,boolean[] under, boolean[] logicalCondition) {
        if (this.hasCI) {
	    // We start off by setting it to zero and build it up during the sampling process
	    int probability = 0;
	    int numOrCondition = 0;

	    int numRow = forecast3D.length;
	    int numCol = forecast3D[0].length;
	    int numIter = forecast3D[0][0].length;
	    int numCondition = groupid.length;

	    // Lets check that all the input parameter are actually valid
	    if (groupid.length != threshold.length) {
		throw new IllegalArgumentException("Number of group and threshold not of equal length");
	    }
	    if (groupid.length != under.length) {
		throw new IllegalArgumentException("Number of group and predicate not of equal length");
	    }
	    if (groupid.length != logicalCondition.length) {
		throw new IllegalArgumentException("Number of group and condition not of equal length");
	    }
            if (timeStep > numRow) {
                throw new IllegalArgumentException("Have not computed forecast that far ahead");
            }
            log.debug("The number of input conditions = " +numCondition);


            
	    for (int i = 0; i < numCondition; i++) {
		if (groupid[i] > numCol) {
		    throw new IllegalArgumentException("Group index not valid");
		}
		/* This in reality will always be satisfied, simply because the threshold is derived from the
		 * current observation of user anyway
		 */
		if (threshold[i] < 0) {
		    throw new IllegalArgumentException("Not a valid threshold, the entered value = "+threshold[i]);
		}
		// First, we need to count the number of OR condition.  Defined as false, where AND condition is true
		if (logicalCondition[i]==false) {
		    numOrCondition++;
		}
	    }
            
            // This is to ensure that a probability of 0 is the output when the currentObservation is zero
            int zeroMass = 0;
            for (int i = 0; i < numCondition; i++) {
                // Only valid if it is a drop, i.e. non-negativity constraint
                if (under[i]) {
                    if (logicalCondition[i]) {
                        if (currentObservation[groupid[i]] == 0) {
                            return 0;
                        }
                    } else {
                        if (currentObservation[groupid[i]] == 0) {
                            zeroMass++;
                        }
                    }   // if(logicalCondition)
                } // if (under)
            }  // Loop number of condition
            // Because all the or condition also have zero mass
            if (zeroMass == numOrCondition) {
                return 0;
            }

            // It has passed through all the checks and now we start to scan the edf.
	    for (int t = 0; t < numIter; t++) {
		boolean satisfyCondition = true;
		boolean orCondition = false;
		if (numOrCondition==0) {
		    orCondition = true;
		}
		    
		for (int k = 0; k < numCondition; k++) {
		    if (logicalCondition[k]) {
			// Using the equality sign
			if (under[k]) {
			    if (this.forecast3D[timeStep-1][groupid[k]][t] > threshold[k]) {
				satisfyCondition = false;
			    }
			} else {
			    if (this.forecast3D[timeStep-1][groupid[k]][t] <= threshold[k]) {
				satisfyCondition = false;
			    }
			}
		    } else { // fail test. This is an or condition
			if (under[k]) {
			    if (this.forecast3D[timeStep-1][groupid[k]][t] <= threshold[k]) {
				orCondition = true;
			    }
			} else {
			    if (this.forecast3D[timeStep-1][groupid[k]][t] > threshold[k]) {
				orCondition = true;
			    }
			}
		    }
		}

		if (satisfyCondition && orCondition) {
		    probability++;
		}
	    }
	    
	    /* Changed it so that it will never produce a probability of 0 and 1, which means that
	     * although we are talking about taking numbers from the simulated EDF, we don't discount
	     * the fact that the simulation does not contain the full set of information as the 
	     * support may well be on the real line and bounded between -\infty and \infty
	     * This is similar to a bootstrap confident interval, where we create a total of R bootstrap 
	     * where (R+1) is a nice integer i.e. 1000 and the upper/lower CI is (R+1)\alpha
	     */
	    if (probability == 0) {
		return (double)(probability+1) / ((double)numIter+1);
	    } else {
		return (double)probability / ((double)numIter+1);
	    }
        } else {
            throw new IllegalArgumentException("Forecast not yet computed");
        }
    }

    @Override
	public double getForecastOverProbability(int groupid, int timeStep, double probability) {
	return getForecastUnderProbability(groupid,timeStep,1-probability);
    }

    // This one finds the number of user from the forecast that satisfy certain probability
    // It returns \P(X \le x), from the EDF
    @Override
	public double getForecastUnderProbability(int groupid, int timeStep, double probability) {
        if (this.hasCI) {
            // number of row represent the total number of time step
            // number of column represent the total number of group
	    int numRow = forecast3D.length;
	    int numCol = forecast3D[0].length;
	    int numIter = forecast3D[0][0].length;

            if (probability < 0 || probability > 1) {
                throw new IllegalArgumentException("Discount factor needs to be between 0 and 1");
            } else {
                // Happy!
            }

            if (timeStep > numRow || groupid > numCol) {
                throw new IllegalArgumentException("Have not computed forecast that far ahead");
            } else {
                double[] dblTemp1D = new double[numIter];
                for (int t = 0; t < numIter; t++) {
                    // Here we assume that the number starts at 1, up to group or timestep
		    dblTemp1D[t] = this.forecast3D[timeStep-1][groupid][t];
                }
                Arrays.sort(dblTemp1D);
                ISWR swr = new SWR(dblTemp1D);
                return swr.getSample(probability);
            }
        } else {
            throw new IllegalArgumentException("Forecast not yet computed");
        }
    }

    @Override
	public double getOptimiseWeight(int forecastStep) {
	if (forecastStep > this.lengthTimeSeries) {
	    return this.alpha;
	} else {
	    return optimiseWeight(forecastStep);
	}
    }

    @Override
	public double[][] getForecastMean() {
	return this.matForecastMean;
    }

    @Override
	public double getForecastMean(int forecastStep, int groupIndex) {
	return this.matForecastMean[forecastStep][groupIndex];
    }

    @Override    
	public double[][] getForecastUCI() {
	return this.matForecastUCI;
    }

    @Override
	public double getForecastUCI(int forecastStep, int groupIndex) {
	return this.matForecastUCI[forecastStep][groupIndex];
    }

    @Override
	public double[][] getForecastLCI() {
	return this.matForecastLCI;
    }

    @Override
	public double getForecastLCI(int forecastStep, int groupIndex) {
	return this.matForecastLCI[forecastStep][groupIndex];
    }


    /*
     *
     * Begin the private methods
     *
     */

    private double getProbabilityForForecast(int groupid, int timeStep, double threshold) {
        if (this.hasCI) {
            // // number of column represent the total number of group
	    int numRow = forecast3D.length;
	    int numCol = forecast3D[0].length;
	    int numIter = forecast3D[0][0].length;

            if (timeStep > numRow || groupid > numCol) {
                throw new IllegalArgumentException("Have not computed forecast that far ahead");
            }
            double[] dblTemp1D = this.forecast3D[timeStep-1][groupid];

	    int probability = 0;
	    for (int t = 0; t < numIter; t++) {
		if (dblTemp1D[t] <= threshold) {
		    probability++;
		}
	    }

	    if (probability == 0) {
		return 1 / ((double)numIter + 1);
	    } else {
		return (double)probability/ ((double)numIter + 1);
	    }
        } else {
            throw new IllegalArgumentException("Forecast not yet computed");
        }
    }

    // Finding the upper and lower predictive interval
    
    private void findForecastSummary() {
	if (this.hasCI) {
	    // number of row is equal to the number of forecast step
	    // number of column is equal to the number of groups
	    int numRow = forecast3D.length;
	    int numCol = forecast3D[0].length;
	    int numIter = forecast3D[0][0].length;
    
	    double[][] dblUCI = new double[numRow][numCol];
	    double[][] dblLCI = new double[numRow][numCol];
	    double[][] dblMean = new double[numRow][numCol];
    
	    int sigValue = (int) Math.ceil(this.significantLevel * (double) numIter);
    
	    for (int i = 0; i < numRow; i++) {
		for (int j = 0; j < numCol; j++) {
		    double[] dblTemp1D = new double[numIter];
		    double dblTemp = 0;
		    for (int t = 0; t < numIter; t++) {
			//dblTemp1D[t] = this.listMassForecast.get(t)[i][j];
			dblTemp1D[t] = this.forecast3D[i][j][t];
			dblTemp += dblTemp1D[t];
		    }
		    Arrays.sort(dblTemp1D);
		    if (dblTemp > 0) {
			dblMean[i][j] = dblTemp / (double) numIter;
		    }
		    dblUCI[i][j] = dblTemp1D[numIter - sigValue];
		    dblLCI[i][j] = dblTemp1D[sigValue];
		}
	    }
    
	    this.matForecastMean = dblMean;
	    this.matForecastUCI = dblUCI;
	    this.matForecastLCI = dblLCI;
	} else {
	    throw new IllegalArgumentException("Forecast not yet computed");
	}
    }
     
    
    // Run the iterations to get the confidence interval for the forecast
    private void computeForecastCI(int forecastStep) {
        computeForecastCI(forecastStep, this.iteration);
    }

    // Run the iterations to get the confidence interval for the forecast, with user specified number
    private void computeForecastCI(int forecastStep, int iter) {
        // Getting the optimal weight to use as forecast
        double optWeight = getOptimiseWeight(forecastStep);
        computeForecastCI(forecastStep, iter, optWeight);
    }

    private void computeForecastCI(int forecastStep, int iter, double penalty) {
        if (iter < 1000) {
            throw new IllegalArgumentException("The number of iteration needs to be higher in order to get reasonable accuracy");
        } else if (penalty < 0 || penalty > 1) {
            throw new IllegalArgumentException("Discount factor needs to be between 0 and 1");
        } else {
            // Happy!
        }

        double[] dblW1D = findWeight(penalty);
        double[] dblIndex1D = new double[dblW1D.length];
        for (int i = 0; i < dblW1D.length; i++) {
            dblIndex1D[i] = i;
        }

        this.Sampling.initialize(dblIndex1D, dblW1D);
        double[] currentMass = this.listMatMassInGroup.get(0);
	this.iterationCounter = 0;
	this.forecast3D = new double[forecastStep][this.totalGroup][iter];
        for (int i = 0; i < iter; i++) {
            computeMassForecast(forecastStep, currentMass);
	    this.iterationCounter++;
        }
        this.hasCI = true;
	this.totalStep = forecastStep;
    }

    // Find the optimal discount given the number of forecasting steps
    private double optimiseWeight(int forecastStep) {
        double minPenalty = 0;
        double penalty = 1;
        double minMSE = 1E9;

	double[][] currentObv = new double[forecastStep][this.totalGroup];
	int count = 0;
	for (int i = forecastStep; i > 0; i--) {
	    System.arraycopy(this.listMatMassInGroup.get(i), 0, currentObv[count], 0, this.totalGroup);
	    count++;
	}
	// this initializes the class with the observation that set the base value
	regressionLoss = new RegressionLoss(currentObv);
        while (penalty > 0.001) {
            double[][] currentPred = computeForecastMean(forecastStep, forecastStep, penalty);
            double currentMSE = 0;
	    // Using square loss as the measure of error
            currentMSE = regressionLoss.getMSE(currentPred);
            if (currentMSE < minMSE) {
                minMSE = currentMSE;
                minPenalty = penalty;
            }
            penalty -= 0.001;
        }
        return minPenalty;
    }

    // This is probably the only method that we ever need..., as it accepts everything
    // And it is better to split this one so that it is a standalone method, just to save confusion
    // even though it pretty much carries out the same computation to computeMassForecast
    private double[][] computeForecastMean(int forecastStep, int startAtLag, double penalty) {
        double[][] matForecastMass = new double[forecastStep][this.totalGroup];
        double[] matCurrentMass = this.listMatMassInGroup.get(startAtLag);

	// finds the expected matrix given some amount of penalty
        double[][] matLambdaMean = getLambdaMean(penalty);

	if (this.stable) {
	    for (int t = 0; t < forecastStep; t++) {
		// The explicit version can be used given that the system is stable
		double[] matNewMass = explicitEulerMethod(matCurrentMass , matLambdaMean);
		for (int j = 0; j < this.totalGroup; j++) {
		    matForecastMass[t][j] = matNewMass[j];
		}
		matCurrentMass = matNewMass;
	    }
	} else {
	    // Then find the (\I-\E(\Lambda)) matrix which is used in the unstable case
	    double[][] matA = new double[this.totalGroup][this.totalGroup];
	    for (int i = 0; i < this.totalGroup; i++) {
		for (int j = i+1; j < this.totalGroup; j++) {
		    matA[i][j] = 0 - matLambdaMean[j][i];
		    matA[j][i] = 0 - matLambdaMean[i][j];
		}
		matA[i][i] = 1 - matLambdaMean[i][i];
	    }

	    try {
		LUDecomposition lu = new LUDecomposition(matA);
		boolean singular = lu.isSingular();
		if (singular) {
		    // There really shouldn't be any singular matrix because we are operator on
		    // the matrix (I - A)
		    log.debug("Singular matrix");
		}

		for (int t = 0; t < forecastStep; t++) {
		    // The standard technique is to use the backward euler
		    // and also solve it using Iterative Refinement (IR version)
		    double[] matNewMass = lu.solveIR(matCurrentMass);
		    for (int j = 0; j < this.totalGroup; j++) {
			matForecastMass[t][j] = matNewMass[j];
		    }
		    matCurrentMass = matNewMass;
		}
	    } catch (Exception e) {
		log.error("Error in computing Iterative Refinement" +e);
	    }
	}

        return matForecastMass;
    }

    // Return the mean value of the Rate matrix according to the amount of penalty being used
    private double[][] getLambdaMean(double penalty) {
        //double[][] matLambdaMean = new double[this.totalGroup][this.totalGroup];
        double[][] dblLambdaMean = new double[this.totalGroup][this.totalGroup];
        double weight = 1;
        double totalWeight = 0;
        int totalObv = this.listMatLambda.size();
        int count = 0;

        while (weight > 0.001 && count < totalObv) {
            weight = Math.pow(penalty, (double) count);
            totalWeight += weight;
            double[][] dblTemp = this.listMatLambda.get(count);
            for (int i = 0; i < this.totalGroup; i++) {
                for (int j = 0; j < this.totalGroup; j++) {
                    dblLambdaMean[i][j] += dblTemp[i][j] * weight;
                }
            }
            count++;
        }

        // Using the computed total weight instead of getting the total weight from a 
        // geometric summation to reduce the floating point error

        // Also manipulate each entry in case of a division of zero
        for (int i = 0; i < this.totalGroup; i++) {
            double dblTemp = 0;
            for (int j = 0; j < this.totalGroup; j++) {
		dblLambdaMean[i][j] /= totalWeight;
		dblTemp += dblLambdaMean[i][j];
            }
	    dblTemp -= dblLambdaMean[i][i];
            dblLambdaMean[i][i] = -1 * dblTemp;
        }
        return dblLambdaMean;
    }

    private double[] findWeight(double penalty) {
        int totalObv = this.listMatLambda.size();
        ArrayList<Double> listWeight = new ArrayList<Double>();
        int count = 0;
        double weight = 1;
        // It has to be noted that this test is before getting the weight
        // so that the first value going under 0.001 will still be taken into account
        while (weight > 0.001 && count < totalObv) {
            weight = Math.pow(penalty, (double) count);
            //System.out.println("Weight at " + count + " is " + weight);
            listWeight.add(weight);
            count++;
        }

        double[] dblW1D = new double[listWeight.size()];
        for (int i = 0; i < listWeight.size(); i++) {
            dblW1D[i] = listWeight.get(i);
        }
        return dblW1D;
    }

    // Computing the forecast using the explicit eurler method
    // TODO: introduce eigenvalue checks to determine whether it is A-stable
    private void computeMassForecast(int forecastStep, double[] startingMatrix) {
        double[][] dblForecastMass = new double[forecastStep][this.totalGroup];
        double[] currentMass = startingMatrix;

        for (int t = 0; t < forecastStep; t++) {
            // Getting the mass at the latest observation
            // Need to think about this one, because there is no reason why I shouldn't be usign
            // the backward euler method at all occasion
            double[] newMass;
            int intTemp = (int) this.Sampling.getSample();
            double[][] matLambda = this.listMatLambda.get(intTemp);
            if (this.stable) {
                newMass = explicitEulerMethod(currentMass, matLambda);
            } else {
                newMass = implicitEulerMethod(currentMass, matLambda);
            }
            //System.out.println("Size of the matrix = " +matNewMass.length+ " " +matNewMass.getColumnDimension());
            //double[][] dblNewMass = newMass.getData();
            for (int i = 0; i < this.totalGroup; i++) {
                //dblForecastMass[t][i] = matNewMass.getEntry(i,0);
                dblForecastMass[t][i] = newMass[i];
            }
            // Not using the operation below because transpose is really really slow
            //matForecastMass.setRowMatrix(t, matNewMass.transpose());
            currentMass = ArrayOperation.copyArray(newMass);
            
            for (int i = 0; i < this.totalGroup; i++) {
		this.forecast3D[t][i][iterationCounter] = newMass[i];   
            }

        }
    }


    /* 
     * The standard forward Euler method
     * It is stable for |1 + z| < 1, i.e. it is within a unit circle centered at Re(-1) + Im(0)
     * A more detailed explanation as follows 
     * Given x^{\prime} = kx, and define h as the step size
     * The solution of the forward euler is x_{t+1} = x_{t} + hkx_{t}
     * x_{t+1} = (1 + hk) x_{t} \Rightarroa x_{n} = (1+hk}^{n} x_{0} (initial value problem)
     * It is clear that if hk > 1, it cannot be stable
     * Now, the backward euler method gives the equation x_{t+1} = x_{t} + hkx_{t+1}
     * x_{t+1} = (1 - hk)^{-1} x_{t}
     * Requirement is that (1- hk) > 1, which means that any negative eigenvalue is good
     * Why eigenvalue? Because an o.d.e of the form x^{\prime} = Ax can be expressed in the form
     * x_{n} = \sum_{i=1}^{p} v_{i}e^{\lambda_{i}) x_{0}
     * where \lambda_{i} and v_{i} are the eigenvalue and eigenvectore of A respectively
     */
    // Passing through the \Lambda value
    private double[] explicitEulerMethod(double[] mass, double[][] b) {
        // Have not yet optimise this method
        // double[] outMass = new double[this.totalGroup];

        // double[][] c = GeneralMatrixOperation.transposeMatrix(b);
        // double[] changeInMass = MatrixMultiplication.multiply(c, mass);
	
        // for (int i = 0; i < this.totalGroup; i++) {
        //     outMass[i] = mass[i] + changeInMass[i];
        // }
        // return outMass;
	return VectorCalculation.add(mass,MatrixMultiplication.multiply(b,mass,true));
    }

    /*
     * The only unstable region is the unit circle centered around Re(1) + Im(0)
     * So any output with negative Re(\cdot) can use the backwork euler method
     * Higher order implicit methods are available
     * 1.) Generalized RK4 (Kaps-Rentrop)
     * 2.) Bader and Dueflhard variation of the Bulirsch-Stoer method
     * but the Euler method is the most natural from the estiation derivation
     */
    // Passing through the \Lambda Value
    private double[] implicitEulerMethod(double[] mass, double[][] A) {
        double[][] dblB = new double[this.totalGroup][this.totalGroup];
        
        for (int i = 0; i < this.totalGroup; i++) {
            for (int j = i+1; j < this.totalGroup; j++) {
                dblB[i][j] = 0 - A[j][i];
                dblB[j][i] = 0 - A[i][j];
            }
            dblB[i][i] = 1 - A[i][i];
        }    
        return LS.luSolve(dblB,mass);
    }


    private void newUserForecast(int forecastStep) {
        // Just a random choice of 5 covariate, don't ask why

        for (int t = 0; t < forecastStep; t++) {
            int lag = t + 1;
            int numCovariate = 5;
            int numObv = this.listNewUser.size();
            int numRow = numObv - (numCovariate + lag);
            double[] Y = new double[numRow];
            double[][] X = new double[numRow][numCovariate];

            for (int i = 0; i < numRow; i++) {
                Y[i] = this.listNewUser.get(i);
                for (int j = 0; j < numCovariate; j++) {
                    X[i][j] = i + j + lag;
                }
            }
            this.glmfit = new GLM(X, Y);
            this.glmfit.fit("Poisson");
            // Doesn't need to predict to find residual
            //glmfit.predict(A,"Poisson");
            double[] e = this.glmfit.getResidual();
            // double[] e = this.glmfit.getModifiedPearsonResidual();

	    // This part is where we initialize the bootstrap of error
	    // I guess this should also be the part where we make a prediction
	    // So that a set of prediction error can be simulated if we want to
	    bootStrapError = new Bootstrap(e);
	    double[] epsilon = bootStrapError.getSample();
	    // TODO: This needs to be a x plus
	    this.glmfit.predict(X);

        }
    }

}
