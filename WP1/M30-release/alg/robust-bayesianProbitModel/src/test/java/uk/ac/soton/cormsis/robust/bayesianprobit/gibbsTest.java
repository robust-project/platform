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
//      Created Date :          2012-08-05
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit;

/**
 *
 * @author Philippa Hiscock
 */
public class gibbsTest {
    
    public static void main(String[] args) throws Exception {
        double[][] data = { {3,15,7,15,18,9,1,15,9,2,17,11,13,1},
                            {1,2,2,8,1,9,2,1,2,1,2,1,2,1},
                            {14,1,10,1,1,9,1,2,8,1,1,14,11,21},
                            {8,24,7,2,2,2,2,8,11,1,2,10,9,4} };
        
        double[][] TestData = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                TestData[i][j] = data[i][j];
            }
        }
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                System.out.printf("%9.0f", data[i][j]);
            }
            System.out.println();
        }
        for (int i = 0; i < TestData.length; i++) {
            for (int j = 0; j < TestData[0].length; j++) {
                System.out.printf("%9.0f", TestData[i][j]);
            }
            System.out.println();
        }
        
//        MultinomialGibbsSampler gs = new MultinomialGibbsSampler(TestData, true);
//        double[][] yMatrix = new double[TestData.length][]; 
//        yMatrix = gs.createYLearningMatrix(TestData);
        
    }
}
