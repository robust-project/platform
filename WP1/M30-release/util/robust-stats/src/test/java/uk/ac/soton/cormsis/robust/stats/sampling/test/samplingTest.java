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
//      Created By :            Edwin Tye
//      Created Date :          2012-08-05
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.sampling.test;

import java.util.Arrays;

import uk.ac.soton.cormsis.robust.stats.glm.impl.GLM;
import uk.ac.soton.cormsis.robust.stats.glm.spec.IGLM;
import uk.ac.soton.cormsis.robust.stats.sampling.impl.Bootstrap;
import uk.ac.soton.cormsis.robust.stats.sampling.impl.BootstrapGLM;
import uk.ac.soton.cormsis.robust.stats.sampling.impl.SWOR;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.IBootstrap;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.IBootstrapGLM;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.ISWOR;
import uk.ac.soton.cormsis.robust.stats.utils.ArrayOperation;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;
import uk.ac.soton.cormsis.robust.stats.utils.display;

/**
 * The test package for the sampling package
 * @author Edwin
 */
public class samplingTest {

    public static void main(String[] args) throws Exception {
        // This is the data from Efron and Tibshirani (Introduction to Bootstrap)
        // Use to test... everything!
        double[] lsat = {576, 635, 558, 578, 666, 580, 555, 661, 651, 605, 653, 575, 545, 572, 594};
        double[] gpa = {3.39, 3.30, 2.81, 3.03, 3.44, 3.07, 3.00, 3.43, 3.36, 3.13, 3.12, 2.74, 2.76, 2.88, 2.96};


        double[][] data = new double[gpa.length][2];
        for (int i = 0; i < gpa.length; i++) {
            data[i][0] = lsat[i];
            data[i][1] = gpa[i];
        }
        // Sampling with replacement


        // Sampling without replacement
        ISWOR sam = new SWOR(lsat);
        double[] y = sam.getListSample();
        Arrays.sort(y);
        int[] y1 = sam.getListIndex();

        
        for (int i = 0; i < y.length; i++) {
            System.out.println("The index = " +i+ " and the sample is " +y[i]);
        }
        
        System.out.println("Size of y1 = " +y1.length);
        
        for (int i = 0; i < y1.length; i++) {
            System.out.println("The index = " +i+ " and the sample is " +y1[i]);
        }
        
        int[] y11 = sam.getListIndex();
                 System.out.println("Size of y1 = " +y11.length);
        
        for (int i = 0; i < y11.length; i++) {
            System.out.println("The index = " +i+ " and the sample is " +y11[i]);
        }
         


        IBootstrap boot = new Bootstrap(data);
        double[] y2 = boot.getSample();
        System.out.println("Bootstrapping");
        for (int i = 0; i < y2.length; i++) {
            System.out.println(y2[i]);
        }

        System.out.println("Second bootstrapping");
        int[][] y3 = boot.getIndex(100000);
        /*
        for (int i = 0; i < y3.length; i++) {
        for (int j = 0; j < y3[0].length; j++) {
        System.out.print(y3[i][j] + " ");
        }
        System.out.println("");
        }
	         * 
         */

        // Testing the how equal are the overall samples
        int[] y5 = new int[y3.length];
        for (int i = 0; i < y3.length; i++) {
            for (int j = 0; j < y3[0].length; j++) {
                y5[y3[i][j]]++;
            }

        }

        for (int i = 0; i < y5.length; i++) {
            System.out.println("The index is = " + i + " and frequency = " + y5[i]);
        }

        double[][] mu = boot.getMean(100);
        System.out.println("Column of mu = " +mu[0].length+ " and Row = " +mu.length);
        for (int i = 0; i < mu.length; i++) {
            for (int j = 0; j < mu[0].length;j++) {
                System.out.print(mu[i][j]+ " ");
            }
            System.out.println(" ");
        }
        
        System.out.println("Correlation of the two is " + VectorCalculation.correlation(lsat, gpa));
        System.out.println("With rank correlation = " +VectorCalculation.rankCorrelation(lsat,gpa));
        System.out.println("Bootstrap the correlation... to be next");
        //Arrays.sort(lsat,gpa);
        /*
        double[] ranks = ArrayOperation.orderedRank(lsat);
        for (int i = 0; i < ranks.length; i++) {
            System.out.println("Value = " + lsat[i]+ " and after = " +ranks[i]);//+ " and index = " +ranks[i][1]);
        }
         * 
         */

        
        	double[] glmy = {18,17,15,20,10,20,25,13,12};
	double[][] glmx = { {0,0,0,0},{1,0,0,0},{0,1,0,0},{0,0,1,0},{1,0,1,0},{0,1,1,0},{0,0,0,1},{1,0,0,1},{0,1,0,1} };
	IGLM glm = new GLM(glmx,glmy);
	double[] beta= glm.fit("Poisson");
	
	BootstrapGLM bootglm = new BootstrapGLM(glm);
	bootglm.betaNonParametric(1000);
	
	double[][] betaStar = bootglm.getBetaStar();
	display.print(betaStar);

	System.out.println("The semi parametric approach");
	BootstrapGLM bootglmSemi = new BootstrapGLM(glm);
	bootglmSemi.betaSemiParametric(1000);
	double[][] betaStarSemi = bootglmSemi.getBetaStar();
	display.print(betaStar);
	
	double[] x_plus = {1, 0, 0, 1};
	double[] predictionError = bootglmSemi.predictionError(x_plus,10000);
	display.print(predictionError);
        
    }
}
