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
//      Created Date :          2012-08-31
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.matrix.test;

import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.GibbsMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.LS;
import uk.ac.soton.cormsis.robust.stats.utils.LUDecomposition;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;

public class gibbsMatTest {

    public static double negInf = Double.NEGATIVE_INFINITY;
    public static double posInf = Double.POSITIVE_INFINITY;
    
    public static void main(String[] args) throws Exception {
        
        /*
        *   LOAD DATA & PARAMETERS
        */       
        double[] _thresholdVector = new double[]{0.0D, 0.2D};
        int b = 20;
        int r = 10;
        boolean isHist = true;
        double[][] data = {{3, 15, 7},
                           {1, 1, 2},
                           {14, 1, 10},
                           {8, 24, 7}};

        double[][] TestData = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                TestData[i][j] = data[i][j];
            }
        }

        
        /*
        *   PROCESS DATA
        */  
        double[][] centerMatrix  = GeneralMatrixOperation.centeredMatrix(TestData);

        double[][] xLMatrix = GibbsMatrixOperation.createXMatrix(centerMatrix, isHist, 0);
        double[][] xPMatrix = GibbsMatrixOperation.createXMatrix(centerMatrix, isHist, 1);
        
        double[][] yLMatrix = GibbsMatrixOperation.createYLearningMatrix(TestData, _thresholdVector, isHist);
        double[][] yPMatrix = GibbsMatrixOperation.createYPredictionMatrix(TestData, _thresholdVector, isHist);

        double[] yLVector  = GibbsMatrixOperation.createYVector(yLMatrix);
        double[] yPVector = GibbsMatrixOperation.createYVector(yPMatrix);

        double[] yLPropVector = GibbsMatrixOperation.createYProportionVector(yLMatrix);

        double[][] gammaMatrix = GibbsMatrixOperation.createGammaMatrix(yLPropVector, b, r);
        double[][] betaMatrix = GibbsMatrixOperation.createBetaMatrix(xLMatrix, yLVector, b, r);
// 
//        double[] beta = new double[betaMatrix.length];
//        for (int i = 0; i< betaMatrix.length ; i++) {
//                beta[i] = betaMatrix[i][0];
//        }
//        double[] yhat = GeneralMatrixOperation.multiplyMatrix(xLMatrix, beta);
//        
//        double[] betaLS = LS.Solve(xLMatrix, yLVector);
    //        for (int i = 0; i < betaLS.length; i++) {
    //            System.out.println(betaLS[i]);
    //        }
    //        System.out.println("Output by Min sorter");

        /*
        *   GIBBS SAMPLER RUN
        */  
        double[][] gibbsResult = GibbsMatrixOperation.simulateMultinmoialGibbs(yLMatrix, xLMatrix, b, r);
        for (int k = 0; k< betaMatrix.length; k++) {
            betaMatrix[k] = gibbsResult[k];
        }
        for (int k = betaMatrix.length; k< (betaMatrix.length+gammaMatrix.length); k++) {
            gammaMatrix[k-betaMatrix.length] = gibbsResult[k];
        }
//        System.out.println ("beta");
//        for (int k = 0; k< beta.length; k++) {
//                for (int t = b; t < b + r; t++) {
//                System.out.printf("%9.3f", gibbsResult[k][t]);
//            }
//            System.out.println();
//        }

//        System.out.println ("gamma");
//        for (int k = beta.length; k< gibbsResult.length; k++) {
//            for (int t = b; t < b + r; t++) {
//                System.out.printf("%9.3f", gibbsResult[k][t]);
//            }
//            System.out.println();
//        }
               
        double[][] etaLMatrix = GibbsMatrixOperation.calculateEtaMatrix(xLMatrix, betaMatrix, gammaMatrix);
//        System.out.println();
//        System.out.println ("eta test");
//        for (int k = 0; k< etaLMatrix.length; k++) {
//            for (int j = 0; j < etaLMatrix[0].length; j++) {
//                System.out.printf("%9.3f", etaLMatrix[k][j]);
//            }
//            System.out.println();
//        }
        
        double[][] PLMatrix = GibbsMatrixOperation.calculatePMatrix(GeneralMatrixOperation.transposeMatrix(etaLMatrix));
//        System.out.println();
//        System.out.println ("P test");
//        for (int k = 0; k< PLMatrix.length; k++) {
//            for (int j = 0; j < PLMatrix[0].length; j++) {
//                System.out.printf("%9.3f", PLMatrix[k][j]);
//            }
//            System.out.println();
//        }
        
        int[] catLVector = GibbsMatrixOperation.calculateCategoryVector(PLMatrix);
        System.out.println();
        System.out.println("Categorical Prediction");
        for (int i = 0; i < catLVector.length; i++) {
            System.out.println(catLVector[i]);
        }
        
    }
    
    
    
    
    public static void GibbsTest(String[] args) throws Exception {
        double[] _thresholdVector = new double[]{0.0D, 0.2D};
        int b = 20;
        int r = 10;
        boolean isHist = true;
        double[][] data = {{3, 15, 7},
            {1, 1, 2},
            {14, 1, 10},
            {8, 24, 7}};

        double[][] TestData = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                TestData[i][j] = data[i][j];
            }
        }

        double[][] xTest = { {1, -2.2421053, -4.1473684, -1.8842105, -2.5473684, -3.5684211, -4.5894737, 4.6210526, 9.7894737, 10.96842105},
            {1, -4.2421053, -6.1473684, -3.8842105, -4.5473684, -4.5684211, 7.4105263, -5.3789474, -5.2105263, 12.96842105},
            {1, 3.7578947, -6.1473684, -3.8842105, -4.5473684, 11.4315789, 12.4105263, 8.6210526, -5.2105263, -3.03157895} };

        double[][] gTest = { {negInf, negInf, negInf, negInf, negInf, negInf, negInf, negInf, negInf, negInf},
                            {0.08379511, 0.09378443, 0.09478365, 0.06673544, 0.05513278, 0.06548965, 0.08160828, 0.05192555, 0.05437116, 0.05640317},
                            {posInf, posInf, posInf, posInf, posInf, posInf, posInf, posInf, posInf, posInf} };
        
        double[][] bTest = { {-0.695905627, -0.624603301, -0.65238500, -0.629083824, -0.476205443, -0.454583171, -0.587935360, -0.6170474499, -0.670842511, -0.791818792},
                             { 0.011027523,  0.016740342,  0.04256994,  0.019433421, -0.016390349, -0.027946616, -0.014537093,  0.0177899866, -0.024693495, -0.016470517},
                             { 0.038846231,  0.008082124,  0.01491097,  0.003565076,  0.016945268,  0.010035597,  0.010407372, -0.0042720229, -0.003254445, -0.030845666},
                             {-0.065930349, -0.020211922, -0.02662012, -0.041457457,  0.013483697, -0.009147478, -0.018842312,  0.0003271443,  0.003975108, -0.029646550},
                             {-0.010274878, -0.036595565, -0.04421768, -0.027104490, -0.029610248, -0.045589722, -0.021926810, -0.0355815554, -0.011628805,  0.009458373},
                             { 0.006656052,  0.003803364, -0.01474497,  0.006475292,  0.004053728,  0.024193998, -0.006201761, -0.0266464718, -0.012209166,  0.038295975},
                             { 0.046997577,  0.030551653,  0.05892536,  0.059406061,  0.011298888, -0.010198935,  0.009007041,  0.0162128329,  0.042721903,  0.025346361},
                             {-0.019494082,  0.018051355, -0.02319668,  0.017539124, -0.016936426, -0.007264263, -0.012848719, -0.0353239020, -0.026366229, -0.078484595},
                             { 0.024393214,  0.021375845,  0.01419335, -0.000941054,  0.014930712,  0.035918082,  0.013417293,  0.0539096885,  0.014208172,  0.028499301},
                             { 0.120641436,  0.091850422,  0.07550430,  0.113742799,  0.112927619,  0.090338128,  0.120806841,  0.0863046851,  0.076492082,  0.145309957} };

        double[][] centerMatrix;
        centerMatrix  = GeneralMatrixOperation.centeredMatrix(TestData);
//        for (int i = 0; i < centerMatrix.length; i++) {
//            for (int j = 0; j < centerMatrix[0].length; j++) {
//                System.out.printf("%9.2f", centerMatrix[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println("xLearnMatrix");
    
        double[][] xLMatrix;
        xLMatrix  = GibbsMatrixOperation.createXMatrix(centerMatrix, isHist, 0);
//        for (int i = 0; i < xLMatrix.length; i++) {
//            for (int j = 0; j < xLMatrix[0].length; j++) {
//                System.out.printf("%9.2f", xLMatrix[i][j]);
//            }
//            System.out.println();
//        }
    
        double[][] xPMatrix;
        xPMatrix  = GibbsMatrixOperation.createXMatrix(centerMatrix, isHist, 1);
//        System.out.println("xPredMatrix");
//        for (int i = 0; i < xPMatrix.length; i++) {
//            for (int j = 0; j < xPMatrix[0].length; j++) {
//                System.out.printf("%9.2f", xPMatrix[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println();
//        System.out.println("yLearnMatrix");
    
        double[][] yLMatrix;
        yLMatrix  = GibbsMatrixOperation.createYLearningMatrix(TestData, _thresholdVector, isHist);
//        for (int i = 0; i < yLMatrix.length; i++) {
//            for (int j = 0; j < yLMatrix[0].length; j++) {
//                System.out.printf("%9.0f", yLMatrix[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println("yPredMatrix");
    
        double[][] yPMatrix;
        yPMatrix  = GibbsMatrixOperation.createYPredictionMatrix(TestData, _thresholdVector, isHist);
//        for (int i = 0; i < yPMatrix.length; i++) {
//            for (int j = 0; j < yPMatrix[0].length; j++) {
//                System.out.printf("%9.0f", yPMatrix[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println();
//        System.out.println("yLearnVector");
    
        double[] yLVector;
        yLVector  = GibbsMatrixOperation.createYVector(yLMatrix);
//        for (int i = 0; i < yLVector.length; i++) {
//            System.out.printf("%9.0f", yLVector[i]);
//            System.out.println();
//        }
//        System.out.println("yPredVector");
    
        double[] yPVector;
        yPVector  = GibbsMatrixOperation.createYVector(yPMatrix);
//        for (int i = 0; i < yPVector.length; i++) {
//            System.out.printf("%9.0f", yPVector[i]);
//            System.out.println();
//        }
//        System.out.println();
//        System.out.println("yLearnProportionVector");
    
        double[] yLPropVector;
        yLPropVector  = GibbsMatrixOperation.createYProportionVector(yLMatrix);
//        for (int i = 0; i < yLPropVector.length; i++) {
//            System.out.printf("%9.2f", yLPropVector[i]);
//            System.out.println();
//        }
//        System.out.println();
//        System.out.println("gammaInitialisation");
    
        double[][] gammaMatrix;
        gammaMatrix  = GibbsMatrixOperation.createGammaMatrix(yLPropVector, b, r);
    //        for (int i = 0; i < gammaMatrix.length; i++) {
    //            for (int j = 0; j < gammaMatrix[0].length; j++) {
    //                System.out.printf("%9.3f", gammaMatrix[i][j]);
    //            }
    //            System.out.println();
    //        }
    //        System.out.println("betaInitialisation");

        double[][] betaMatrix;
        betaMatrix  = GibbsMatrixOperation.createBetaMatrix(xLMatrix, yLVector, b, r);
    //        for (int i = 0; i < betaMatrix.length; i++) {
    //            for (int j = 0; j < betaMatrix[0].length; j++) {
    //                System.out.printf("%9.3f", betaMatrix[i][j]);
    //            }
    //            System.out.println();
    //        }

        double[] beta = new double[betaMatrix.length];
        for (int i = 0; i< betaMatrix.length ; i++) {
                beta[i] = betaMatrix[i][0];
        }
        double[] yhat = MatrixMultiplication.multiply(xLMatrix, beta);
    //        for (int i = 0; i < yhat.length; i++) {
    //            System.out.println("value of yhat " +i+ " = "+yhat[i]);
    //        }
    //        System.out.println("Output by LS solver");

        double[] betaLS = LS.Solve(xLMatrix, yLVector);
    //        for (int i = 0; i < betaLS.length; i++) {
    //            System.out.println(betaLS[i]);
    //        }
    //        System.out.println("Output by Min sorter");

        double[] vector = new double[data[0].length];
        for (int j = 0; j< data[0].length ; j++) {
                vector[j] = data[0][j];
        }


//        double[][] gibbsResult = GibbsMatrixOperation.simulateMultinmoialGibbs(yLMatrix, xLMatrix, b, r);
//        System.out.println ("beta");
//        for (int k = 0; k< beta.length; k++) {
//                for (int t = b; t < b + r; t++) {
//                System.out.printf("%9.3f", gibbsResult[k][t]);
//            }
//            System.out.println();
//        }

//        System.out.println ("gamma");
//        for (int k = beta.length; k< gibbsResult.length; k++) {
//            for (int t = b; t < b + r; t++) {
//                System.out.printf("%9.3f", gibbsResult[k][t]);
//            }
//            System.out.println();
//        }
               
        double[][] etaResult = GibbsMatrixOperation.calculateEtaMatrix(xTest, bTest, gTest);
//        System.out.println();
//        System.out.println ("eta test");
//        for (int k = 0; k< etaResult.length; k++) {
//            for (int j = 0; j < etaResult[0].length; j++) {
//                System.out.printf("%9.3f", etaResult[k][j]);
//            }
//            System.out.println();
//        }
        
        double[][] PResult = GibbsMatrixOperation.calculatePMatrix(GeneralMatrixOperation.transposeMatrix(etaResult));
//        System.out.println();
//        System.out.println ("P test");
//        for (int k = 0; k< PResult.length; k++) {
//            for (int j = 0; j < PResult[0].length; j++) {
//                System.out.printf("%9.3f", PResult[k][j]);
//            }
//            System.out.println();
//        }
        
        int[] CatResult = GibbsMatrixOperation.calculateCategoryVector(PResult);
//        System.out.println();
//        System.out.println("Categorical Prediction");
//        for (int i = 0; i < CatResult.length; i++) {
//            System.out.println(CatResult[i]);
//        }
        
    }
}
