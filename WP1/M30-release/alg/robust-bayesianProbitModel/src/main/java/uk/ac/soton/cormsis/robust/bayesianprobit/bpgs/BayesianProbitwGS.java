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
//      Created Date :          2012-11-09
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.bpgs;

import java.util.List;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;
import uk.ac.soton.cormsis.robust.bayesianprobit.utils.FindValue;
import uk.ac.soton.cormsis.robust.stats.utils.CholeskyDecomposition;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;
import uk.ac.soton.cormsis.robust.stats.utils.display;

public class BayesianProbitwGS {

    //Class scope variables
    private int _numDiscIt = 1000000;
    private int _numRetIt = 10000;
    private RandomData _rand = new RandomDataImpl();
    private NormalDistribution normDist = new NormalDistribution();
    private final double negInf = Double.NEGATIVE_INFINITY;
    private final double posInf = Double.POSITIVE_INFINITY;
    //Create instance of type parameterData to store simulated parameters
    private ParameterData paramData = new ParameterData();

    /**
     * Constructor to run Bayesian probit model with Gibbs sampler
     *
     * @param xLearn observations for learning phase
     * @param yLearn observed binary responses for learning phase
     * @param yLearnProp proportion of class responses for observed in learning
     * phase
     */
    public BayesianProbitwGS(double[][] xLearn, double[] yLearn, double[] yLearnProp) throws Exception {

        int numCls = yLearnProp.length;

        if (numCls == 2) {
            simulateBinaryGS(xLearn, yLearn, yLearnProp);
        } else {
            simulateMultinomialGS(xLearn, yLearn, yLearnProp);
        }
    }

    /**
     * Constructor to run Bayesian probit model with Gibbs sampler
     *
     * @param xLearn observations for learning phase
     * @param yLearn observed binary responses for learning phase
     * @param yLearnProp proportion of class responses for observed in learning
     * phase
     * @param b - number of total iterations included in the burn-in period
     * @param r - number of total iterations (after burn-in) saved
     */
    public BayesianProbitwGS(double[][] xLearn, double[] yLearn, double[] yLearnProp, int b, int r) throws Exception {

        _numDiscIt = b;
        _numRetIt = r;

        int numCls = yLearnProp.length;

        if (numCls == 2) {
            simulateBinaryGS(xLearn, yLearn, yLearnProp);
        } else {
            simulateMultinomialGS(xLearn, yLearn, yLearnProp);
        }
    }

    /**
     * Return parameter data
     *
     * @return parameter data.
     */
    public ParameterData returnParamData() {
        return paramData;
    }

    /**
     * Simulate Bayesian probit incorporating a Gibbs sampler for predicting
     * binary response
     *
     * @param xL covariate observations for learning phase
     * @param yL observed binary responses for learning phase
     * @param yLProp observed proportion of binary response classification in
     * learning phase
     * @throws Exception when multivariate Markov chain simulated does not
     * converge
     */
    private void simulateBinaryGS(double[][] xL, double[] yL, double[] yLProp) throws Exception {

        //Create commonly used variables:
        CholeskyDecomposition cholDecomp = new CholeskyDecomposition(xL, false);
        VectorCalculation vc = new VectorCalculation();
        int numTotalIt = _numDiscIt + _numRetIt;
        int numObservation = xL.length;

        //Create variables for simulating z only
        double[] LP;
        double[] negLP;
        double[] yLFlipped = vc.add(vc.multiply(yL, (-1)), 1);
        double[] linearPD = new double[numObservation];
        double[] linearPDFlipped = new double[numObservation];
        double[] rUnif;
        double[] leftAssign;
        double[] rightAssign;
        double[] z;

        //Create variable for simulating beta only
        //Initialise beta
        double[] beta0 = cholDecomp.solveXT(yL);
        //Check beta0 is correct
        System.out.println("Beta 0:");
        display.print(beta0);
        double[][] betaT = createBetaTMatrix(beta0, numTotalIt + 1);
        double[] mu;
        double[][] varcov = cholDecomp.getAinverse();
        double[][] R = new CholeskyDecomposition(varcov).getR();
        double[] randZ = new double[beta0.length];

        //Create gamma matrix
        double[][] gammaT = createGammaTMatrix(yLProp, numTotalIt + 1);

        //Simulation of Z and beta given beta0 from t=1 to t=numTotalIt:
        for (int t = 1; t < numTotalIt + 1; t++) {
            ////------------------- Simulate Z -------------------////
            //Simulate z:
            //Linear predictor for Z = xL %*% beta[,t-1]
            LP = MatrixMultiplication.multiply(xL, betaT[t - 1]);
            negLP = vc.multiply(LP, (-1));
            for (int i = 0; i < numObservation; i++) {
                linearPD[i] = normDist.cumulativeProbability(LP[i] * -1);
                linearPDFlipped[i] = normDist.cumulativeProbability(LP[i]);
            }
            rUnif = runif(numObservation);

            //((pnorm(-lP)) * (1-yL) + (1-(pnorm(-lP))) * yL) * runif(n)
            leftAssign = vc.multiply(
                    vc.add(
                    vc.multiply(linearPD, yLFlipped),
                    vc.multiply(linearPDFlipped, yL)),
                    rUnif);
            //(pnorm(-lP))*yL
            rightAssign = vc.multiply(linearPD, yL);
            z = vc.add(leftAssign, rightAssign);
//            System.out.println("Z pre LP shift - note should be between 0 & 1!!!");
//            display.print(z);
            //HACK!!!!!
            for (int i = 0; i < numObservation; i++) {
                if (z[i] == 0) {
//                    System.out.println(z[i]);
                    z[i] = 0.000000000000001D;
                } else if (z[i] == 1) {
//                    System.out.println(z[i]);
                    z[i] = 0.999999999999999D;
                }
            }
            for (int i = 0; i < z.length; i++) {
                z[i] = normDist.inverseCumulativeProbability(z[i]);
            }
            z = vc.add(z, LP);


            ////----------------- Simulate beta -----------------////
            //Simulate beta = mu + L^{-1}*z where z~N(0,1):
            mu = cholDecomp.solveXT(z);
            for (int k = 0; k < randZ.length; k++) {
                randZ[k] = normDist.sample();
            }
            betaT[t] = vc.add(mu, MatrixMultiplication.multiply(R, randZ));
        }

        //Partition beta for retined iterations only
        double[][] betaRetained = GeneralMatrixOperation.transposeMatrix(partitionMatrix(betaT));
        double[][] gammaRetained = GeneralMatrixOperation.transposeMatrix(partitionMatrix(gammaT));


        setParameterData(betaRetained, gammaRetained);
    }

    /**
     * Simulate Bayesian probit incorporating a Gibbs sampler for predicting
     * ordered categorical response
     *
     * @param xL covariate observations for learning phase
     * @param yL observed ordered categorical responses for learning phase
     * @param yLProp observed proportion of ordered response classification in
     * learning phase
     * @throws Exception when multivariate Markov chain simulated does not
     * converge
     */
    private void simulateMultinomialGS(double[][] xL, double[] yL, double[] yLProp) throws Exception {

        //Create commonly used variables:
        CholeskyDecomposition cholDecomp = new CholeskyDecomposition(xL, false);
        VectorCalculation vc = new VectorCalculation();
        MatrixMultiplication mm = new MatrixMultiplication();
        GeneralMatrixOperation mo = new GeneralMatrixOperation();
        FindValue fv = new FindValue();
        int numTotalIt = _numDiscIt + _numRetIt;
        int numObs = xL.length;
        int numCls = yLProp.length;

        //Create variables for simulating z only
        double[] LP;
        double[] aVector = new double[numObs];
        double[] bVector = new double[numObs];
        double[] rUnif;
        double[] aLP;
        double[] bLP;
        double[] pnormALP = new double[numObs];
        double[] pnormBLP = new double[numObs];
        double[] z;

        //Create variables for simulating beta only
        double[] beta0 = cholDecomp.solveXT(yL);
        int numBeta = beta0.length;
//        System.out.println("Beta 0:");
//        display.print(beta0);
        double[][] betaT = createBetaTMatrix(beta0, numTotalIt + 1);
        double[] mu;
        double[][] varcov = cholDecomp.getAinverse();
        double[][] R = new CholeskyDecomposition(varcov).getR();
        double[] randZ = new double[numBeta];

        //Create variables for simulating gamma only
        int numZSplit = numCls + 1;
        double[][] yLMatrix = createBinClsMatrix(yL, numCls);
        double[][] yLMatT = mo.transposeMatrix(yLMatrix);
        double[][] gammaT = createGammaTMatrix(yLProp, numTotalIt + 1);


        //Simulation of Z and beta given beta0 from t=1 to t=numTotalIt:
        for (int t = 1; t < numTotalIt + 1; t++) {
            ////------------------- Simulate Z --------------------////
            LP = mm.multiply(xL, betaT[(t - 1)]);
            for (int i = 0; i < numObs; i++) {
                aVector[i] = gammaT[(t - 1)][((int) yL[i])];
                bVector[i] = gammaT[(t - 1)][((int) yL[i] + 1)];
//                System.out.println("User " + i + ": " + (int) yL[i] + ", " + ((int) yL[i]+1));
//                System.out.println("User " + i + ": " + aVector[i] + ", " + bVector[i]);
                double LPi = LP[i];
                pnormALP[i] = normDist.cumulativeProbability(aVector[i] - LPi);
                pnormBLP[i] = normDist.cumulativeProbability(bVector[i] - LPi);
            }
            rUnif = runif(numObs);
            //z = ( pnorm(aVector-LP) + runif(numObs)*( pnorm(bVector-LP) - pnorm(aVector-LP) ) );
            z = vc.add(pnormALP, vc.multiply(rUnif, vc.subtract(pnormBLP, pnormALP)));
            //HACK!!!!!
            for (int i = 0; i < numObs; i++) {
                if (z[i] == 0) {
//                    System.out.println(z[i]);
                    z[i] = 0.000000000000001D;
                } else if (z[i] == 1) {
//                    System.out.println(z[i]);
                    z[i] = 0.999999999999999D;
                }
            }
            for (int i = 0; i < numObs; i++) {
                z[i] = normDist.inverseCumulativeProbability(z[i]);
            }
            z = vc.add(z, LP);
//            for (int i = 0; i < numObs; i++) {
//                System.out.println("User " + i + ": " + z[i]);
//            }

            ////------------------ Simulate Beta ------------------////
            //Simulate beta = mu + L^{-1}*z where z~N(0,1):
            mu = cholDecomp.solveXT(z);
            for (int f = 0; f < numBeta; f++) {
                randZ[f] = normDist.sample();
            }
            betaT[t] = vc.add(mu, mm.multiply(R, randZ));


            ////----------------- Simulate Gamma ------------------////
            double[][] minZMatrix = fill(posInf, numObs, numZSplit);
            double[][] maxZMatrix = fill(negInf, numObs, numZSplit);
            for (int c = 1; c < numCls; c++) {
                //TODO first thing!!! Check this!!!
                List<Integer> maxClsObs = fv.grep(1, yLMatT[(c - 1)]);
                int maxNumOcc = maxClsObs.size();
                for (int i = 0; i < maxNumOcc; i++) {
                    int index = maxClsObs.get(i).intValue();
                    maxZMatrix[index][(c - 1)] = z[index];
                }
                List<Integer> minClsObs = fv.grep(1, yLMatT[c]);
                int minNumOcc = minClsObs.size();
                for (int i = 0; i < minNumOcc; i++) {
                    int index = minClsObs.get(i).intValue();
                    minZMatrix[index][(c - 1)] = z[index];
                }
                double[][] maxZMatrixT = mo.transposeMatrix(maxZMatrix);
                double[][] minZMatrixT = mo.transposeMatrix(minZMatrix);

                double minGamma = fv.maxValue(fv.maxVectorValue(maxZMatrixT[(c - 1)]), gammaT[(t - 1)][(c - 1)]);
                double maxGamma = fv.minValue(fv.minVectorValue(minZMatrixT[(c - 1)]), gammaT[(t - 1)][(c + 1)]);
//                System.out.println("Class " + c + ", minG " + minGamma + ", maxG " + maxGamma);

                gammaT[t][c] = runif(minGamma, maxGamma);
            } //end loop over class
        } //end loop over t

        //Partition beta for retained iterations only
        double[][] betaRetained = mo.transposeMatrix(partitionMatrix(betaT));
        double[][] gammaRetained = mo.transposeMatrix(partitionMatrix(gammaT));


        setParameterData(betaRetained, gammaRetained);
    }

    /**
     * Create matrix of classifications from vector
     *
     * @param y vector of observation classifications
     * @param numCls number of possible classifications
     * @return binary matrix of classifications (for each observation)
     */
    private static double[][] createBinClsMatrix(double[] y, int numCls) {
        int numObs = y.length;
        double[][] yMat = new double[numObs][numCls];

        for (int c = 0; c < numCls; c++) {
            List<Integer> clsObs = FindValue.grep(c, y);
            int numOcc = clsObs.size();
            for (int i = 0; i < numOcc; i++) {
                yMat[clsObs.get(i).intValue()][c] = 1;
            }
        }

        return yMat;
    }

    /**
     * Create matrix filled with pattern
     *
     * @param pattern fill for matrix
     * @param nrow number of rows
     * @param ncol number of columns
     * @return matrix of pattern
     */
    private static double[][] fill(double pattern, int nrow, int ncol) {
        double[][] f = new double[nrow][ncol];

        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) {
                f[i][j] = pattern;
            }
        }

        return f;
    }

    /**
     * Set parameter data for return
     *
     * @param beta simulated beta matrix (retained iterations only)
     * @return filled parameter data for beta
     */
    private ParameterData setParameterData(double[][] beta) {

        paramData.setBeta(beta);

        return paramData;
    }

    /**
     * Set parameter data for return
     *
     * @param beta simulated beta matrix (retained iterations only)
     * @param gamma simulated gamma matrix (retained iterations only)
     * @return filled parameter data
     */
    private ParameterData setParameterData(double[][] beta, double[][] gamma) {

        paramData.setBeta(beta);
        paramData.setGamma(gamma);

        return paramData;
    }

    /**
     * Utility method to construct the beta matrix.
     *
     * @param beta0 initial estimate of beta (OLS)
     * @param numTotalIt total number of simulations to be made (including
     * initial)
     * @return Beta matrix - initial estimate of beta in first position
     */
    private double[][] createBetaTMatrix(double[] beta0, int numTotalIt) {

        double[][] betaMatrix = new double[numTotalIt][beta0.length];
        System.arraycopy(beta0, 0, betaMatrix[0], 0, beta0.length);

        return (betaMatrix);
    }

    /**
     * Utility method to construct the gamma matrix and make initial estimate.
     *
     * @param proportionVector observed proportion of ordered response
     * classifications in learning phase
     * @param numTotalIt number of gamma values to be stored
     * @return partially filled gamma matrix capable of holding all future
     * simulations
     */
    private double[][] createGammaTMatrix(double[] proportionVector, int numTotalIt) {
        double[][] gammaTMatrix = new double[numTotalIt][proportionVector.length + 1];

        double muGamma;
        double sum = 0;
        muGamma = -(normDist.inverseCumulativeProbability(proportionVector[0]));
        for (int t = 0; t < numTotalIt; t++) {
            gammaTMatrix[t][0] = negInf;
            gammaTMatrix[t][proportionVector.length] = posInf;
        }
        for (int s = 1; s < proportionVector.length; s++) {
            for (int h = 0; h < s; h++) {
                sum = sum + proportionVector[h];
            }
            gammaTMatrix[0][s] = muGamma + normDist.inverseCumulativeProbability(sum);
            sum = 0D;
        }

        return (gammaTMatrix);
    }

    /**
     * Utility method to exclude the parameters simulated within the burn-in
     * phase. Note that the reason for this is that it is assumed that the after
     * the burn-in period completes all following beta iterations have converged
     * to the true distribution.
     *
     * @param AT transpose of matrix
     * @return partitioned transpose of matrix
     */
    private double[][] partitionMatrix(double[][] AT) {

        double[][] ATRet = new double[_numRetIt][AT[0].length];

        System.arraycopy(AT, (_numDiscIt + 1), ATRet, 0, _numRetIt);

        return ATRet;
    }

    /**
     * Utility method to generate vector of random values from the uniform
     * distribution between 0 and 1.
     *
     * @param numObs dimension of vector (number of random numbers to generate)
     * @return Set of random values from uniform distribution
     */
    private double[] runif(int numObs) {

        double[] runif = new double[numObs];

        for (int i = 0; i < numObs; i++) {
            runif[i] = _rand.nextUniform(0, 1);
        }

        return runif;
    }

    /**
     * Utility method to generate vector of random values from the uniform
     * distribution given bounds.
     *
     * @param numObs length of vector to produce
     * @param min lower bound
     * @param max upper bound
     * @return a vector of uniformly distributed random values between min and
     * max (exclusive)
     */
    private double[] runif(int numObs, double min, double max) {

        double[] runif = new double[numObs];

        for (int i = 0; i < numObs; i++) {
            runif[i] = _rand.nextUniform(min, max);
        }

        return runif;
    }

    /**
     * Utility method to generate a random value from the uniform distribution
     * given bounds.
     *
     * @param min lower bound
     * @param max upper bound
     * @return a uniform random value between min and max (exclusive)
     */
    private double runif(double min, double max) {

        double runif = min + ((max - min) * _rand.nextUniform(0, 1));

        return runif;
    }
}
