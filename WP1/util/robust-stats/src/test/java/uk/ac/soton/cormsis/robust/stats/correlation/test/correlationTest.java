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
package uk.ac.soton.cormsis.robust.stats.correlation.test;

import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;
import uk.ac.soton.cormsis.robust.stats.utils.display;
import uk.ac.soton.cormsis.robust.stats.utils.ranking;

/**
 * The test package for the sampling package
 * @author Edwin
 */
public class correlationTest {

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

        System.out.println("Correlation of the two is " + VectorCalculation.correlation(lsat, gpa));
        System.out.println("With rank correlation = " +VectorCalculation.rankCorrelation(lsat,gpa));
        
        double[][] rank = ranking.rankSort(gpa);
        display.print(rank);
        
        rank = ranking.rankSort(lsat);
        display.print(rank);
        
    }
}
