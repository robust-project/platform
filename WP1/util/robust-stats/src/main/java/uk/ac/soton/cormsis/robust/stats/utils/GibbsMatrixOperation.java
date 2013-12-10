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
//      Created By :            Pip
//      Created Date :          2012-08-31
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.utils;

import java.util.Random;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;


public class GibbsMatrixOperation {  
    private static NormalDistribution normDist = new NormalDistribution();
    private static Random _fRandom = new Random();
    
    /**
    * Utility method to create the X matrix (for learning and prediction)
    * @param centeredMatrix The centered data matrix
    * @return The matrix as an array object
    */
    public static double[][] createXMatrix(double[][] centeredMatrix, boolean isHistorical ,int startIndex) {
        int historicalOffset = (isHistorical) ? -2 : -1;
        double[][] xMatrix = new double[centeredMatrix.length][];
        for (int i = 0; i < centeredMatrix.length; i++) {
            int vectorLength = centeredMatrix[0].length + historicalOffset;
            xMatrix[i] = new double[vectorLength + 1];
            xMatrix[i][0] = 1;
            for (int j = 0; j < vectorLength; j++) {
                xMatrix[i][j + 1] = centeredMatrix[i][j + startIndex];
            }
        }
        
        return(xMatrix);
    }
     
    public static double[][] createYLearningMatrix(double[][] originalMatrix, double[] thresholdVector, boolean isHistorical) {
        double[][] yLearningMatrix = new double[originalMatrix.length][thresholdVector.length+1];
        
        int historicalOffset = (isHistorical) ? 1 : 0;
        boolean isCat1 = false;
        int originalLength = originalMatrix[0].length-1;
        int categoryLength = thresholdVector.length +1;

        for (int i = 0; i < yLearningMatrix.length; i++) {
            double ultimateValue = originalMatrix[i][originalLength - historicalOffset];
            double penultimateValue = originalMatrix[i][originalLength - historicalOffset - 1];
            isCat1 = false;
            if (ultimateValue > (1-thresholdVector[0])*penultimateValue) {
                yLearningMatrix[i][0] = 1;
                isCat1 = true;
            }
            if (categoryLength > 2) {
                for (int c = 1; c < (categoryLength-1); c++) {
                    if (isCat1 != false) {
                    break;
                    }
                    if (ultimateValue <= ((1-thresholdVector[c-1])*penultimateValue) && (ultimateValue > (1-thresholdVector[c])*penultimateValue)) {
                        yLearningMatrix[i][c] = 1;
                        break;
                    }
                }
            }
            if (ultimateValue <= (1-thresholdVector[categoryLength-1-1])*penultimateValue) {
                yLearningMatrix[i][categoryLength-1] = 1;
            }
        }
        
        return(yLearningMatrix);
    }
    
    public static double [][] createYPredictionMatrix(double[][] originalMatrix, double[] thresholdVector, boolean isHistorical) {
        double[][] yPredictionMatrix = new double[originalMatrix.length][thresholdVector.length+1];
        if (! isHistorical) return(yPredictionMatrix);
        
        boolean isCat1 = false;
        int originalLength = originalMatrix[0].length -1;
        int categoryLength = thresholdVector.length +1;

        for (int i = 0; i < yPredictionMatrix.length; i++) {
            double ultimateValue = originalMatrix[i][originalLength];
            double penultimateValue = originalMatrix[i][originalLength - 1];
            isCat1 = false;
            if (ultimateValue > (1-thresholdVector[0])*penultimateValue) {
                yPredictionMatrix[i][0] = 1;
                isCat1 = true;
            }
            for (int c = 1; c < (categoryLength-1); c++) {
                if (isCat1 != false) {
                    break;
                }
                if (ultimateValue <= ((1-thresholdVector[c-1])*penultimateValue) && (ultimateValue > (1-thresholdVector[c])*penultimateValue)) {
                    yPredictionMatrix[i][c] = 1;
                    break;
                }
            }
            if (ultimateValue <= (1-thresholdVector[categoryLength-1-1])*penultimateValue) {
                yPredictionMatrix[i][categoryLength-1] = 1;
            }
        }
        
        return(yPredictionMatrix);
    }
      
    public static double[] createYVector(double[][] yMatrix) {
        double[] yVector = new double[yMatrix.length];
        double[][] catYMatrix = new double[yMatrix.length][yMatrix[0].length];
        
        int categoryLength = yMatrix[0].length;        
        int[] categoryVector = new int[categoryLength];
        for (int c = 0; c < categoryLength; c++) {
            categoryVector[c] = c + 1;
        }
        for (int i = 0; i < yMatrix.length; i++) {
            for (int c = 0; c < categoryLength; c++) {
                catYMatrix[i][c] = categoryVector[c] * yMatrix[i][c];
                yVector[i] = yVector[i] + catYMatrix[i][c];
            }
        }
        
        return (yVector);
    }
    
    public static double[] createYProportionVector(double[][] yMatrix) {
        double[] yProportionVector = new double[yMatrix[0].length];
        double[] yCatSumVector = new double[yMatrix[0].length];
        
        for (int c = 0; c < yMatrix[0].length; c++) {
            for (int i = 0; i < yMatrix.length; i++) {
                yCatSumVector[c] = yCatSumVector[c] + yMatrix[i][c];
            }
            yProportionVector[c] = yCatSumVector[c] / yMatrix.length;
        }
                
        return (yProportionVector);
    }
    
    public static double[] generateUniformRandomVector(int noObservation){
            double[] randUnifVector = new double[noObservation];
            for (int i = 0; i < randUnifVector.length; i++) {
            	randUnifVector[i] = _fRandom.nextFloat();
            }
            
            return randUnifVector;
	}
    
    public static int minVectorIndex(double[] data) {
        int minValueIndex = 0;
        
        for (int i = 1; i < data.length; i++) {
            if (data[i] < data[minValueIndex]) {
                minValueIndex = i;
            }
        }
        
        return(minValueIndex);
    }
    
    public static int maxVectorIndex(double[] data) {
        int maxValueIndex = 0;
        
        for (int i = 1; i < data.length; i++) {
            if (data[i] > data[maxValueIndex]) {
                maxValueIndex = i;
            }
        }
        
        return(maxValueIndex);
    }
    
    public static double minVectorValue(double[] x) {
        double min = x[0];
        
        for (int i = 1; i < x.length; i++) {
            double xi = x[i];
            if (xi < min) {
                min = xi;
            }
        }
        
        return(min);
    }
    
    public static double maxVectorValue(double[] x) {
        double max = x[0];
        
        for (int i = 1; i < x.length; i++) {
            double xi = x[i];
            if (xi > max) {
                max = xi;
            }
        }
        
        return(max);
    }
    
    public static double minValue(double a, double b) {
        double min = a;
        if (a > b) min = b;
            
        return(min);
    }
    
    public static double maxValue(double a, double b) {
        double max = a;
        if (a < b) max = b;
            
        return(max);
    }
    
    //// --------------------- To move accross once fully developed here for tsting only ----------------------////
    public static double negInf = Double.NEGATIVE_INFINITY;
    public static double posInf = Double.POSITIVE_INFINITY;
    
    
    public static double[][] createGammaMatrix(double[] proportionVector, int b, int r) {
        double[][] gammaMatrix = new double[proportionVector.length+1][b+r];
        
        double muGamma;
        double sum = 0;
        muGamma = -(normDist.inverseCumulativeProbability(proportionVector[0]));
//        System.out.println("muGamma");
//        System.out.println(muGamma);
        for (int t = 0; t < b+r; t++) {
            gammaMatrix[0][t] = negInf;
            gammaMatrix[proportionVector.length][t] = posInf;
        }
        for (int s = 1; s < proportionVector.length; s++) {
            for (int h = 0; h < s; h++) {
                sum = sum + proportionVector[h];
            }
//            System.out.println(sum);
            gammaMatrix[s][0] = muGamma + normDist.inverseCumulativeProbability(sum);
            sum = 0D;
        }
        
        return(gammaMatrix);
    }
    
    public static double[][] createBetaMatrix(double[][] xLearnMatrix, double[] yLearnVector, int b, int r) {
        double[][] betaMatrix = new double[xLearnMatrix[0].length][b+r];
        double[] intBeta = solveOperation(xLearnMatrix,yLearnVector);
        for (int k = 0; k < betaMatrix.length; k++) {
            betaMatrix[k][0] = intBeta[k];
        }
        
        return(betaMatrix);
    }
    
    public static double[] solveOperation(double[][] operatorMatrix, double[] bVector) {
        QRDecomposition _qrSolve = new QRDecomposition(operatorMatrix);
        
        return _qrSolve.solve(bVector);
    }
 
    public static double[][] simulateMultinmoialGibbs(double[][] _yLearningMatrix, double[][] _xLearningMatrix, int b, int r) {
        double[][] parameterMatrix = new double[_xLearningMatrix[0].length + _yLearningMatrix[0].length + 1][(b + r)];
        
        QRDecomposition _qrSolve = new QRDecomposition(_xLearningMatrix);
        
        int noIteration = b + r;
        int noObservation = _yLearningMatrix.length;
        int noCategory = _yLearningMatrix[0].length;
        int noZsplit = noCategory - 1;
        double[] _yLearningVector = createYVector(_yLearningMatrix);
        int[] yLearningVector = new int[_yLearningVector.length];
        for (int i = 0; i < noObservation; i++) {
            yLearningVector[i] = (int)_yLearningVector[i];
        }
        double[] yLearnProportion = createYProportionVector(_yLearningMatrix);
        double[][] _gammaMatrix = createGammaMatrix(yLearnProportion, b, r);
        double[][] _betaMatrix = createBetaMatrix(_xLearningMatrix, _yLearningVector, b, r);
        double[][] tx = GeneralMatrixOperation.transposeMatrix(_xLearningMatrix);
        double[][] _invSigmaMatrix = MatrixMultiplication.multiply(tx, _xLearningMatrix);
        CholeskyDecomposition _cDecomp = new CholeskyDecomposition(_invSigmaMatrix);
        double[][] _sigmaMatrix = _cDecomp.getLInverse();
        RealMatrix xLearnMatrix = new Array2DRowRealMatrix(_xLearningMatrix);
//        for (int i=1; i< _xLearningMatrix.length; i ++) {
//            for (int j = 0; j < _xLearningMatrix[0].length; j++) {
//                System.out.printf("%9.0", xLearnMatrix)
//            }
//        }
//        RealMatrix _invsigmaMatrix = xLearnMatrix.transpose().multiply(xLearnMatrix);
//        RealMatrix _sigmaMatrix = new org.apache.commons.math3.linear.CholeskyDecomposition(_invsigmaMatrix).getSolver().getInverse();
        double[][] dSigmaMatrix = new double[_sigmaMatrix.length][_sigmaMatrix[0].length];
        for (int i=1; i< dSigmaMatrix.length; i ++) {
            for (int j = 0; j < dSigmaMatrix[0].length; j++) {
//                dSigmaMatrix[i][j] = _sigmaMatrix.getEntry(i, j);
                dSigmaMatrix[i][j] = _sigmaMatrix[i][j];
            }
        }
        
//        double[][] invSigmaMatrix = GeneralMatrixOperation.multiplyTransposeMatrix(_xLearningMatrix, _xLearningMatrix);
//        System.out.println("Inverse Sigma");
//        for (int i = 0; i < invSigmaMatrix.length; i++) {
//            for (int j = 0; j < invSigmaMatrix[0].length; j++) {
//                System.out.printf("%9.2f", invSigmaMatrix[i][j]);
//            }
//            System.out.println();
//        }
//        CholeskyDecomposition cDecomp = new CholeskyDecomposition(invSigmaMatrix);
//        double[][] LMatrix = cDecomp.getL();
//        System.out.println("lower tirangular matrix for beta simulation");
//        for (int i = 0; i < LMatrix.length; i++) {
//            for (int j = 0; j < LMatrix[0].length; j++) {
//                System.out.printf("%9.2f", LMatrix[i][j]);
//            }
//            System.out.println();
//        }
        double[] betaVector = new double[_betaMatrix.length];
        double[] multVector = new double[_betaMatrix.length];
        double[] linearPredictorVector = new double[noObservation];
        double[] aVector = new double[noObservation];
        double[] bVector = new double[noObservation];
        double[] aSep = new double[noObservation];
        double[] bSep = new double[noObservation];
        double[] aPnorm = new double[noObservation];
        double[] bPnorm = new double[noObservation];
        double[] rUnifVector = generateUniformRandomVector(noObservation);
        double[] invLeftAssign = new double[noObservation];
        double[] leftAssign = new double[noObservation];
        double[] rightAssign = new double[noObservation];
        double[] zVector = new double[noObservation];
        double[] muVector = new double[noObservation];
        double[][] minZMatrix = new double[noObservation][noZsplit];
        double[][] maxZMatrix = new double[noObservation][noZsplit];
        double[] minZVector = new double[noObservation];
        double[] maxZVector = new double[noObservation];
        int maxMaxZIndex;
        int minMinZIndex;
        double minBoundGamma;
        double maxBoundGamma;
        
        for (int t = 1; t < noIteration; t++) {
            System.out.println();
            System.out.println("Iteration number " + t);
            ////-------------------------------- Simulate Z --------------------------------////
            //Calculate linear predictor for Z
            for (int k = 0; k < _betaMatrix.length; k++){
                betaVector[k] = _betaMatrix[k][t-1];
            }
            //Check prev beta vector
//            System.out.println();
//            System.out.println("betaVector " + (t-1));
//            for (int k = 0; k < betaVector.length; k++) {
//                System.out.printf("%9.2f", betaVector[k]);
//                System.out.println();
//            }
            //Check prev gamma vector
//            System.out.println();
//            System.out.println("gammaVector " + (t-1));
//            for (int k = 0; k < _gammaMatrix.length; k++) {
//                System.out.printf("%9.2f", _gammaMatrix[k][t-1]);
//                System.out.println();
//            }
            
            multVector = MatrixMultiplication.multiply(_xLearningMatrix, betaVector);
            //Check multvector
//            System.out.println();
//            System.out.println("MultVector (xLearn%*%beta)");
//            for (int k = 0; k < multVector.length; k++) {
//                System.out.printf("%9.2f", multVector[k]);
//                System.out.println();
//            }
            
            for (int i = 0; i < noObservation; i++){
                linearPredictorVector[i] = multVector[i];// - _gammaMatrix[1][t-1];
            }
            //Check linear predictor
//            System.out.println("gamma 1 " + _gammaMatrix[1][t-1]);
            System.out.println();
            System.out.println("linearPred");
            for (int i = 0; i < linearPredictorVector.length; i++) {
                System.out.printf("%9.2f", linearPredictorVector[i]);
                System.out.println();
            }
            //aSep = aVector-linearPredictorVector //bSep = bVector-linearPredictorVector
            //aPnorm = pnorm(aVector-linearPredictorVector) //bPnorm = pnorm(bVector-linearPredictorVector) 
//            System.out.println();
//            System.out.println("Z vector");
            double[] gammaVector = new double[_gammaMatrix.length];
            int[] aIndex = new int[noObservation];
            int[] bIndex = new int[noObservation];
            for (int k = 0; k < gammaVector.length; k++) {
                gammaVector[k] = _gammaMatrix[k][t-1];
            }
            for (int i = 0; i < noObservation; i++) {
                aIndex[i] = yLearningVector[i]-1;
                bIndex[i] = yLearningVector[i]-1+1;
            }
//            System.out.println();
//            System.out.println("aIndex Vector");
//            for (int i = 0; i < noObservation; i++) {
//                System.out.println(aIndex[i]);
////                System.out.println();
//            }
//            System.out.println();
//            System.out.println("bIndex Vector");
//            for (int i = 0; i < noObservation; i++) {
//                System.out.println(bIndex[i]);
////                System.out.println();
//            }
            for (int i = 0; i < noObservation; i++) {
                //Bounds for Z[i]
//                System.out.println();
//                System.out.println("gammaVector " + (t-1));
//                for (int k = 0; k < _gammaMatrix.length; k++) {
//                    System.out.printf("%9.2f", _gammaMatrix[k][t-1]);
//                    System.out.println();
//                }
//                aVector[i] = _gammaMatrix[yLearningVector[i]-1][t-1];
//                bVector[i] = _gammaMatrix[yLearningVector[i]-1+1][t-1];
                aVector[i] = gammaVector[aIndex[i]];
                bVector[i] = gammaVector[bIndex[i]];
//                System.out.println();
//                System.out.println("aVector " + i + " " + aVector[i]);
//                System.out.println("bVector " + i + " " + bVector[i]);
                //Dist of upper and lower bounds from linearPredictor
                aSep[i] = aVector[i] - linearPredictorVector[i];
                bSep[i] = bVector[i] - linearPredictorVector[i];
//                System.out.println("aSep " + i + " " + aSep[i]);
//                System.out.println("bSep " + i + " " + bSep[i]);
                //Pnorm of seperation
                aPnorm[i] = normDist.cumulativeProbability(aSep[i]);
                bPnorm[i] = normDist.cumulativeProbability(bSep[i]);
                if (aPnorm[i] == 0) aPnorm[i]=0.000000000001D;
                if (aPnorm[i] == 1) aPnorm[i]=0.999999999999D;
//                System.out.println("aPnorm " + i + " " + aPnorm[i]);
//                System.out.println("bPnorm " + i + " " + bPnorm[i]);
                //Left and right parts of Z
                invLeftAssign[i] = aPnorm[i] + rUnifVector[i] * (bPnorm[i] - aPnorm[i]);
//                System.out.println("invLeftAssign " + i + " " + invLeftAssign[i]);
                leftAssign[i] = normDist.inverseCumulativeProbability(invLeftAssign[i]);
//                System.out.println("left assign" + i + " " + leftAssign[i]);
                rightAssign[i] = linearPredictorVector[i];
//                System.out.println("right assign" + i + " " + rightAssign[i]);
                //Z result
                zVector[i] = leftAssign[i] + rightAssign[i];
//                System.out.println("Z " + i + " " + zVector[i]);
            }
                //Check Z output:
                System.out.println();
                System.out.println("a Vector");
                for (int i = 0; i < aVector.length; i++) {
                    System.out.printf("%9.2f", aVector[i]);
                    System.out.println();
                }
                System.out.println("b Vector");
                for (int i = 0; i < bVector.length; i++) {
                    System.out.printf("%9.2f", bVector[i]);
                    System.out.println();
                }
                System.out.println();
                System.out.println("Diff between a and linear predictor");
                for (int i = 0; i < aSep.length; i++) {
                    System.out.printf("%9.2f", aSep[i]);
                    System.out.println();
                }
                System.out.println("Diff between b and linear predictor");
                for (int i = 0; i < bSep.length; i++) {
                    System.out.printf("%9.2f", bSep[i]);
                    System.out.println();
                }
                System.out.println();
                System.out.println("Normal CDF of Diff between a and linear predictor (a shifted back to normal with mean zero by subtract linPred)");
                for (int i = 0; i < aPnorm.length; i++) {
                    System.out.printf("%9.2f", aPnorm[i]);
                    System.out.println();
                }
                System.out.println("Normal CDF of Diff between b and linear predictor (b shifted back to normal with mean zero by subtract linPred)");
                for (int i = 0; i < bPnorm.length; i++) {
                    System.out.printf("%9.2f", bPnorm[i]);
                    System.out.println();
                }
                System.out.println();
                System.out.println("aPnorm + rUnif * (bPnorm-aPnorm)");
                for (int i = 0; i < invLeftAssign.length; i++) {
                    System.out.printf("%9.2f", invLeftAssign[i]);
                    System.out.println();
                }
                System.out.println();
                System.out.println("leftAssign");
                for (int i = 0; i < leftAssign.length; i++) {
                    System.out.printf("%9.2f", leftAssign[i]);
                    System.out.println();
                }
                System.out.println("rightAssign");
                for (int i = 0; i < rightAssign.length; i++) {
                    System.out.printf("%9.2f", rightAssign[i]);
                    System.out.println();
                }
                System.out.println();
                System.out.println("Z simulation");
                for (int i = 0; i < zVector.length; i++) {
                    System.out.printf("%9.2f", zVector[i]);
                    System.out.println();
                }
            
            ////-------------------------------- Simulate beta --------------------------------////
//            muVector = solveOperation(_xLearningMatrix,zVector);
            muVector = _qrSolve.solve(zVector);
            System.out.println();
            System.out.println(" beta muVector");
            for (int k = 0; k < muVector.length; k++) {
                System.out.printf("%9.2f", muVector[k]);
                System.out.println();
            }
            
            double[] standardZVector = new double[_betaMatrix.length];
            for (int k = 0; k < _betaMatrix.length; k++) {
                standardZVector[k] = _fRandom.nextGaussian();
            }
            System.out.println();
            System.out.println(" beta standardZVector");
            for (int k = 0; k < standardZVector.length; k++) {
                System.out.printf("%9.2f", standardZVector[k]);
                System.out.println();
            }
            
//            double[] LZMult = GeneralMatrixOperation.multiplyMatrix(LMatrix, standardZVector);
            double[] LZMult = MatrixMultiplication.multiply(dSigmaMatrix, standardZVector);
            System.out.println();
            System.out.println("L%*%Z ");
            for (int k = 0; k < LZMult.length; k++) {
                System.out.printf("%9.2f", LZMult[k]);
                System.out.println();
            }
            for (int k = 0; k < muVector.length; k++) {
                _betaMatrix[k][t] = muVector[k] + LZMult[k];
            }
            
            for (int i = 0; i < _betaMatrix.length; i++) {
                parameterMatrix[i][t] = _betaMatrix[i][t];
            }
            System.out.println();
            System.out.println("BetaSimulation");
            for (int i = 0; i < parameterMatrix.length; i++) {
                System.out.printf("%9.2f", parameterMatrix[i][t]);
                System.out.println();
            }
            
            ////-------------------------------- Simulate gamma --------------------------------////
            for (int s = 1; s < noCategory; s++) {
                //Find bounds for each gamm value from each observation
                for (int i = 0; i < noObservation; i++) {
                    if (yLearningVector[i] == s) {
                        maxZMatrix[i][s-1] = zVector[i];
                    } else if (yLearningVector[i] != s) {
                        maxZMatrix[i][s-1] = negInf;
                    }
                    if (yLearningVector[i] == s+1) {
                        minZMatrix[i][s-1] = zVector[i];
                    } else if (yLearningVector[i] != s+1) {
                        minZMatrix[i][s-1] = posInf;
                    }
                }
                
                
                //Find max of min value & min of max value for min and max bound values for gamma
                for (int i = 0; i < noObservation; i++) {
                    maxZVector[i] = maxZMatrix[i][s-1];
                    minZVector[i] = minZMatrix[i][s-1];
                }
                maxMaxZIndex = maxVectorIndex(maxZVector);
                minMinZIndex = minVectorIndex(minZVector);
//                System.out.println();
//                System.out.println("Category" + s);
//                System.out.println();
//                System.out.println("maxZIndex");
//                System.out.println(maxMaxZIndex);
//                System.out.println("minZIndex");
//                System.out.println( minMinZIndex);
                minBoundGamma = maxValue(maxZVector[maxMaxZIndex],_gammaMatrix[s-1][t-1]);
                maxBoundGamma = minValue(minZVector[minMinZIndex],_gammaMatrix[s+1][t-1]);
//                System.out.println("maxZ");
//                System.out.printf("%9.2f",maxZVector[maxMaxZIndex]);
//                System.out.println();
//                System.out.println("gamma");
//                System.out.printf("%9.2f",_gammaMatrix[s-1][t-1]);
//                System.out.println();
//                System.out.println("minBoundGamma");
//                System.out.printf("%9.2f", minBoundGamma);
//                System.out.println();
//                System.out.println("minZ");
//                System.out.printf("%9.2f",minZVector[minMinZIndex]);
//                System.out.println();
//                System.out.println("gamma");
//                System.out.printf("%9.2f",_gammaMatrix[s+1][t-1]);
//                System.out.println();
//                System.out.println("maxBoundGamma");
//                System.out.printf("%9.2f", maxBoundGamma);
//                double gammaIntLength = maxBoundGamma - minBoundGamma;
//                System.out.println();
//                System.out.println("Interval Length");
//                System.out.printf("%9.2f", gammaIntLength);
                _gammaMatrix[s][t] = minBoundGamma + _fRandom.nextDouble()*(maxBoundGamma - minBoundGamma);
            }
            
            System.out.println();
            System.out.println("minZMatrix");
            for (int i = 0; i < minZMatrix.length; i++) {
                for (int j = 0; j < minZMatrix[0].length; j++) {
                    System.out.printf("%9.3f", minZMatrix[i][j]);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("maxZMatrix");
            for (int i = 0; i < maxZMatrix.length; i++) {
                for (int j = 0; j < maxZMatrix[0].length; j++) {
                    System.out.printf("%9.3f", maxZMatrix[i][j]);
                }
                System.out.println();
            }
            
            for (int s = 0; s < _gammaMatrix.length; s++) {
//                System.out.println(s);
                parameterMatrix[s+_betaMatrix.length][t] = _gammaMatrix[s][t];
            }
            System.out.println("Beta&GammaSimulation");
            for (int i = 0; i < parameterMatrix.length; i++) {
                System.out.printf("%9.2f", parameterMatrix[i][t]);
                System.out.println();
            }
        }
        
        return(parameterMatrix);
    }
    
    public static double[][] calculateEtaMatrix(double[][] xMatrix, double[][] betaMatrix, double[][] gammaMatrix) {
        int numCategory = gammaMatrix.length - 1;
        int numObservation = xMatrix.length;
        double[][] etaMatrix = new double[numObservation][numCategory+1];
        double[] xBeta = new double[betaMatrix[0].length];
        double[] diffGammaXBeta = new double[gammaMatrix[0].length];
        double[] midStep = new double[betaMatrix[0].length];
        
        for (int c = 1; c < numCategory+1; c++) {
            for (int i = 0; i < numObservation; i++) {
                xBeta = MatrixMultiplication.multiply(betaMatrix, xMatrix[i], true);
                diffGammaXBeta = VectorCalculation.add(gammaMatrix[c], (VectorCalculation.multiply(xBeta, -1)));
                for (int j = 0; j < midStep.length; j++) {
                    midStep[j] = normDist.cumulativeProbability(diffGammaXBeta[j]);
                }
                etaMatrix[i][c] = VectorCalculation.mean(midStep);
            }
        }
                
        return etaMatrix;
    }
    
    // Note that here eta and p are transposed as Java is row orientated.
    public static double[][] calculatePMatrix(double[][] TetaMatrix) {
        double[][] pMatrix = new double[(TetaMatrix.length-1)][TetaMatrix[0].length];
        
        for (int c = 1; c < TetaMatrix.length; c++) {
            pMatrix[c-1] = VectorCalculation.add(TetaMatrix[c], VectorCalculation.multiply(TetaMatrix[(c-1)], (-1)));
        }
        
        return GeneralMatrixOperation.transposeMatrix(pMatrix);        
    }
    
    public static int[] calculateCategoryVector(double[][] pMatrix) {
        int[] categoryVector = new int[pMatrix.length];
        
        for (int i = 0; i < categoryVector.length; i++) {
            categoryVector[i] = maxVectorIndex(pMatrix[i]);
        }
        
        return categoryVector;
    }
}
