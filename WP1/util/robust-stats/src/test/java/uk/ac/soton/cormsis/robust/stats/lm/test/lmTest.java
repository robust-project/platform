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
package uk.ac.soton.cormsis.robust.stats.lm.test;

import uk.ac.soton.cormsis.robust.stats.lm.impl.LM;
import uk.ac.soton.cormsis.robust.stats.lm.spec.ILM;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;
import uk.ac.soton.cormsis.robust.stats.utils.QRDecomposition;

/**
 * Used for testing of lm (linear model)
 */
public class lmTest {

    public static void main(String[] args) throws Exception {
        
        double[][] p = {{-1, 1}, {-0.5, 0.25}, {0, 0}, {0.5, 0.25}, {1, 1}};
        double[] b = {1, 0.5, 0, 0.5, 2.0};
        ILM ABC = new LM(p,b);

        //RealMatrix beta = ABC.regress(p, b);
        double[] beta = ABC.fit();
        System.out.println("The beta values are ");
        for (int i = 0; i < beta.length; i++) {
                System.out.printf("%9.5f ", beta[i]);
                       System.out.println();
        }
        
        QRDecomposition qr = new QRDecomposition(GeneralMatrixOperation.augmentedMatrix(p));
        double[] bqr = qr.solve(b);
        System.out.println("Size of qr beta = " + bqr.length);
                
                for (int i = 0; i < bqr.length; i++) {
            System.out.printf("%9.3f ", bqr[i]);
            System.out.println();
        }
                System.out.println("R matrix");
        double[][] r = qr.getR();
        for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < r[0].length; j++) {
                System.out.printf("%9.3f ", r[i][j]);
            }
            System.out.println();
        }
        System.out.println("Q matrix");
        double[][] q = qr.getQ();
        for (int i = 0; i < q.length; i++) {
            for (int j = 0; j < q[0].length; j++) {
                System.out.printf("%9.3f ", q[i][j]);
            }
            System.out.println();
        }

        
        ABC = new LM(p, b);
        ABC.fit();
        System.out.println();
        double[] e = ABC.getResidual();
        /*
        for (int i = 0; i < e.getRowDimension(); i++) {
        System.out.println(e.getEntry(i, 0));
        }
         *
         */

        double[]mu = GeneralMatrixOperation.meanMatrix(p);
        System.out.println("The mean of the matrix is ");
        for (int i = 0; i < mu.length; i++) {
            //for (int j = 0; j < mu[0].length; j++) {
                System.out.printf("%9.5f ", mu[i]);
            //}
            System.out.println();
        }
        double[] dblmu = mu;
        System.out.println("first size = " + dblmu.length);

        System.out.println(" the covariance matrix");
        double[][] sigma = GeneralMatrixOperation.covarianceMatrix(p);
        for (int i = 0; i < sigma.length; i++) {
            for (int j = 0; j < sigma[0].length; j++) {
                System.out.printf("%9.5f ", sigma[i][j]);
            }
            System.out.println();
        }
        
        
                System.out.println("Again, the covariance matrix by X^{T} X");
        double[][] sigma1 = MatrixMultiplication.multiply(p,p,true,false);
        for (int i = 0; i < sigma1.length; i++) {
            for (int j = 0; j < sigma1[0].length; j++) {
                System.out.printf("%9.5f ", sigma1[i][j]);
            }
            System.out.println();
        }

        double[][] augPP = GeneralMatrixOperation.augmentedMatrix(p);
        System.out.println(" the augmented matrix with row = " + augPP.length + " and column = " + augPP[0].length);
        for (int i = 0; i < augPP.length; i++) {
            for (int j = 0; j < augPP[0].length; j++) {
                System.out.printf("%9.5f ", augPP[i][j]);
            }
            System.out.println();
        }


        double[] Yhat_plus = ABC.predict(p);
        System.out.println();
        for (int i = 0; i < Yhat_plus.length; i++) {
            System.out.println(Yhat_plus[i] + " and residual " + e[i] + " with observed value = " + b[i]);
        }
                
        /*
        int[] pivot = lu.getPivot();
        for (int i = 0; i < pivot.length; i++) {
            System.out.println("Index " +i+ " is pivot " +pivot[i]);
        }
         * 
         */

        /*
        double[] c = {1,2,3};
        System.out.println();
        System.out.println("Normal solver");
        RealMatrix matTemp =  new QRDecomposition(new Array2DRowRealMatrix(A)).getSolver().solve(new Array2DRowRealMatrix(c));
        for (int i = 0; i < matTemp.getRowDimension(); i++) {
        for (int j = 0; j < matTemp.getColumnDimension(); j++) {
        System.out.printf("%9.3f ", matTemp.getEntry(i,j));
        }
        System.out.println();
        }
         * 
         */
    }
}