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
//      Created Date :          2012-10-31
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.bpgs;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.log4j.Logger;
import uk.ac.soton.cormsis.robust.bayesianprobit.utils.FindValue;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;
import uk.ac.soton.cormsis.robust.stats.utils.display;

public class PostProcess {

    static Logger log = Logger.getLogger(PostProcess.class);
    //Class scope variables
    private static NormalDistribution normDist = new NormalDistribution();

    /**
     * Make prediction for learning and prediction phases.
     *
     * @param md model data
     * @param pd parameter data
     */
    public static void Predict(ModelData md, ParameterData pd) {
        double[][] beta = pd.getBeta();
//        log.info("Beta nrow = " + beta.length + " ncol = " + beta[0].length);
        double[][] gamma = pd.getGamma();

        double[] meanGamma = new double[gamma.length];
        for (int c=0; c<gamma.length;c++) {
            meanGamma[c] = VectorCalculation.mean(gamma[c]);
        }
        System.out.println("Mean of gamma values: "); display.print(meanGamma);
        
        
        // Calculate cumulative probability of each observation being in each category
        double[][] etaLearn = calculateEtaMatrix(md.getXLearn(), beta, gamma);
        double[][] etaPred = calculateEtaMatrix(md.getXPred(), beta, gamma);
        System.out.println("Cumulative probability of classification:");
        display.print(etaPred);

        // Calculate the probability of each observation being in each category
        double[][] pLearn = calculatePMatrix(GeneralMatrixOperation.transposeMatrix(etaLearn));
        double[][] pPred = calculatePMatrix(GeneralMatrixOperation.transposeMatrix(etaPred));

        // Categorise each observation by their p vector
        int[] catLearn = calculateCategoryVector(pLearn);
        int[] catPred = calculateCategoryVector(pPred);

        // Set in md
        md.setPLearn(pLearn);
        md.setPPred(pPred);
        md.setCatLearn(catLearn);
        md.setCatPred(catPred);
    }

    /**
     * Calculate the cumulative probabilities for the classification of all
     * observations.
     *
     * @param xMatrix covariate matrix
     * @param betaMatrix
     * @param gammaMatrix
     * @return cumulative probability of classification
     */
    private static double[][] calculateEtaMatrix(double[][] xMatrix, double[][] betaMatrix, double[][] gammaMatrix) {
        int numCategory = gammaMatrix.length - 1;
        int numObservation = xMatrix.length;
        double[][] etaMatrix = new double[numObservation][numCategory + 1];
        double[] xBeta = new double[betaMatrix[0].length];
        double[] diffGammaXBeta = new double[gammaMatrix[0].length];
        double[] midStep = new double[betaMatrix[0].length];

        for (int c = 1; c < numCategory + 1; c++) {
            for (int i = 0; i < numObservation; i++) {
                xBeta = MatrixMultiplication.multiply(betaMatrix, xMatrix[i], true);
//                diffGammaXBeta = VectorCalculation.add(gammaMatrix[c], (VectorCalculation.multiply(xBeta, -1)));
                diffGammaXBeta = VectorCalculation.subtract(gammaMatrix[c], xBeta);
                for (int j = 0; j < midStep.length; j++) {
                    midStep[j] = normDist.cumulativeProbability(diffGammaXBeta[j]);
                }
                etaMatrix[i][c] = VectorCalculation.mean(midStep);
            }
        }

        return etaMatrix;
    }

    /**
     * Calculate the probabilities for the classification of all observations.
     *
     * @param TetaMatrix transpose of the cumulative classification probability
     * matrix
     * @return classification probability matrix
     */
    private static double[][] calculatePMatrix(double[][] TetaMatrix) {
        double[][] pMatrix = new double[(TetaMatrix.length - 1)][TetaMatrix[0].length];

        for (int c = 1; c < TetaMatrix.length; c++) {
//            pMatrix[c - 1] = VectorCalculation.add(TetaMatrix[c], VectorCalculation.multiply(TetaMatrix[(c - 1)], (-1)));
            pMatrix[c - 1] = VectorCalculation.subtract(TetaMatrix[c], TetaMatrix[(c - 1)]);
        }

        return GeneralMatrixOperation.transposeMatrix(pMatrix);
    }

    /**
     * Calculate the most likely classification for each observation.
     *
     * @param pMatrix classification probability matrix
     * @return classification vector
     */
    private static int[] calculateCategoryVector(double[][] pMatrix) {
        int[] categoryVector = new int[pMatrix.length];

        for (int i = 0; i < categoryVector.length; i++) {
            categoryVector[i] = FindValue.maxVectorIndex(pMatrix[i]);
        }

        return categoryVector;
    }
    
//    //TODO: complete this
//    public static void analysisClassification(double[] c, double[] y) {
//    }
}
