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
package uk.ac.soton.cormsis.robust.stats.matrix.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import uk.ac.soton.cormsis.robust.stats.utils.CholeskyDecomposition;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.LS;
import uk.ac.soton.cormsis.robust.stats.utils.LUDecomposition;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;
import uk.ac.soton.cormsis.robust.stats.utils.QRDecomposition;
import uk.ac.soton.cormsis.robust.stats.utils.display;

/**
 *
 * @author Edwin
 */
public class matTest {

    public static void main(String[] args) throws Exception {

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        // The data used in the original paper
        // Please define your own data path
        File file = new File("/home/et4g08/data/diabetes.csv");

        BufferedReader bufRdr = null;
        bufRdr = new BufferedReader(new FileReader(file));
        String line = null;

        while ((line = bufRdr.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line, ",");
            ArrayList<String> listTemp = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                listTemp.add(st.nextToken());
            }
            data.add(listTemp);
        }

        bufRdr.close();

        int numCol = data.get(0).size();
        int numRow = data.size();

        double[][] x = new double[numRow][numCol - 1];
        double[] y = new double[numRow];

        for (int i = 0; i < numRow; i++) {
            ArrayList<String> listTemp = data.get(i);
            for (int j = 0; j < numCol - 1; j++) {
                x[i][j] = Double.valueOf(listTemp.get(j));
            }
            y[i] = Double.valueOf(listTemp.get(numCol - 1));
        }

        // Now we have all the data we need

        double[][] A = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};

        LUDecomposition lu = new LUDecomposition(A);
        System.out.println("Matrix of L");
        double[][] L = lu.getL();
        for (int i = 0; i < L.length; i++) {
            for (int j = 0; j < L[0].length; j++) {
                System.out.printf("%9.3f ", L[i][j]);
            }
            System.out.println();
        }

        System.out.println("Matrix of U");
        double[][] U = lu.getU();
        for (int i = 0; i < U.length; i++) {
            for (int j = 0; j < U[0].length; j++) {
                System.out.printf("%9.3f ", U[i][j]);
            }
            System.out.println();
        }
        System.out.println("and the determinant = " + lu.getDet());

        double[] c = {1, 2, 3};
        //lu = new LUDecomposition(A);
        System.out.println("Beta value is ");
        double[] dblBeta = lu.solve(c);
        for (int i = 0; i < dblBeta.length; i++) {
            System.out.printf("%9.3f ", dblBeta[i]);
            System.out.println("");
        }

        System.out.println("The A Matrix");
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                System.out.printf("%9.3f ", A[i][j]);
            }
            System.out.println();
        }

        System.out.println("Matrix multiplication");
        double[][] Out = MatrixMultiplication.multiply(A, A);
        for (int i = 0; i < Out.length; i++) {
            for (int j = 0; j < Out[0].length; j++) {
                System.out.printf("%9.3f ", Out[i][j]);
            }
            System.out.println();
        }

        System.out.println("Solving a linear system with QR");
        double[] beta = LS.qrSolve(A, c);
        for (int i = 0; i < beta.length; i++) {
            System.out.printf("%9.3f ", beta[i]);
            System.out.println();
        }

        System.out.println("Vector multiply");
        double[] t = MatrixMultiplication.multiply(A, c);
        for (int i = 0; i < t.length; i++) {
            System.out.printf("%9.3f ", t[i]);
            System.out.println();
        }

        double[][] X = {{1, 1, 1, 1, 1}, {1, 2, 3, 4, 5}, {1, 3, 6, 10, 15}, {1, 4, 10, 20, 35}, {1, 5, 15, 35, 70}};
        System.out.println("The X matrix");
        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < X[0].length; j++) {
                System.out.printf("%9.3f ", X[i][j]);
            }
            System.out.println();
        }

        double[][] X1 = {{1, 1, 15, 1, 6}, {1, 2, 3, 4, 5}, {1, 3, 6, 10, 15}, {1, 4, 10, 20, 35}};
        System.out.append("X^T X ");
        double[][] XTX = MatrixMultiplication.multiply(X1, X1, true,false);
        System.out.println("The X matrix");
        for (int i = 0; i < XTX.length; i++) {
            for (int j = 0; j < XTX[0].length; j++) {
                System.out.printf("%9.3f ", XTX[i][j]);
            }
            System.out.println();
        }
        
        double[] b = {5, 4, 3, 2, 1};
        System.out.println("QR decomposition");

        QRDecomposition qr = new QRDecomposition(X);
        double[][] r = qr.getR();
        System.out.println("R matrix");
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

        System.out.println("Solution with qr");
        double[] bqr = qr.solve(b);
        for (int i = 0; i < bqr.length; i++) {
            System.out.printf("%9.3f ", bqr[i]);
            System.out.println();
        }

        CholeskyDecomposition chol1 = new CholeskyDecomposition(X);
        System.out.println("Solving the system");
        //double[] beta1 = LS.Solve(X, b);
        //double[] beta1 = LS.cholSolve(X,b);
        double[] beta1 = chol1.solve(b);
        for (int i = 0; i < beta1.length; i++) {
            System.out.printf("%9.3f ", beta1[i]);
            System.out.println();
        }

        System.out.println("Solve L");
        double[] beta2 = chol1.solveL(b);
        for (int i = 0; i < beta2.length; i++) {
            System.out.printf("%9.3f ", beta2[i]);
            System.out.println();
        }
        
        System.out.println("Solution with LS.cholSolve");
        double[] betaChol = LS.cholSolve(X, b);
        for (int i = 0; i < betaChol.length; i++) {
            System.out.printf("%9.3f ", betaChol[i]);
            System.out.println();
        }
        
        double[][] LL = chol1.getL();
        System.out.println("The lower matrix");
        for (int i = 0; i < LL.length; i++) {
            for (int j = 0; j < LL[0].length; j++) {
                System.out.printf("%9.3f ", LL[i][j]);
            }
            System.out.println();
        }

        System.out.println("Inverse of the lower matrix");
        double[][] LLInverse = chol1.getLInverse();
        for (int i = 0; i < LLInverse.length; i++) {
            for (int j = 0; j < LLInverse.length; j++) {
                System.out.printf("%9.3f ", LLInverse[i][j]);
            }
            System.out.println();
        }

        System.out.println("The det = " + chol1.getDet() + " and log det = " + chol1.getLogDet());

	//		int index_j = this.A.indexOf(violatingIndex);
	int index_j = 2;
        System.out.println("Deleting row " +index_j);
	chol1.delete(2);
        double[][] L_del = chol1.getL();
                for (int i = 0; i < L_del.length; i++) {
            for (int j = 0; j < L_del[0].length; j++) {
                System.out.printf("%9.3f ", L_del[i][j]);
            }
            System.out.println();
        }

	CholeskyDecomposition chol = new CholeskyDecomposition(x,false);
	L = chol.getL();
	double[][] inverseL = chol.getLInverse();
	System.out.println("L");
	display.print(L);
	System.out.println("L inverse");
	display.print(inverseL);
	System.out.println("A inverse");
	display.print(chol.getAinverse());
	
	// double[] betaOLS = chol.solveXT(y);
	// System.out.println("beta values");
	// display.print(betaOLS);
		
	double[][] G = MatrixMultiplication.multiply(x,x,true,false);
	// System.out.println("G");
	// display.print(G);
	double[][] invG = GeneralMatrixOperation.inverse(G);
	System.out.println("inverse of G");
	display.print(invG);

	lu = new LUDecomposition(G,true);
	double[][] fullG = lu.inverse();
	System.out.println("inverse of G full");
	display.print(fullG);
	int[] columnPivot = lu.getColumnPivot();
	display.print(columnPivot);

    }
}
