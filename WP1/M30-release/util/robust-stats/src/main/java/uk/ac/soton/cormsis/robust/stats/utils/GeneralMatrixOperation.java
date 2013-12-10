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
package uk.ac.soton.cormsis.robust.stats.utils;


public class GeneralMatrixOperation {

    /**
     * Makes a copy of the matrix
     * @param A input matrix
     * @return output matrix
     */
    static public double[][] copyMatrix(double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;
        double[][] X = new double[numRow][numCol];
        for (int i = 0; i < numRow; i++) {
            System.arraycopy(A[i], 0, X[i], 0, numCol);
        }
        return X;
    }
    
    /**
     * Need to be aware that the output is a column vector, or rather, just an array
     * (unlike matlab)
     * @param A design matrix
     * @return mu = E(X)
     */
    static public double[] meanMatrix(double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;

        // mu = E(X)
        double[] dblMu = new double[numCol];
        // n observation
        // p variables
        for (int p = 0; p < numCol; p++) {
            for (int n = 0; n < numRow; n++) {
                dblMu[p] += A[n][p];
            }
            dblMu[p] /= (double) numRow;
        }
        return dblMu;
    }

    /** 
     * Finding the covariance matrix given n number of observation in row and p 
     * number of observation in column
     * 
     * Also note that this is a naive version, which is more prone to numerical error
     * The standard two pass algorithm by far the most stable and should be used
     * when high accuracy is required
     * 
     * Using Var(X) = E(X^2) - E(X)^2
     * where X^2 = XX^{T}
     * and E(X)^2 = \mu \mu^{T}
     *
     * @param A design matrix
     * @return Var(X)
     */
    static public double[][] covarianceMatrix(double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;

        double[][] T1 = new double[numCol][numCol];
        double[] T2 = new double[numCol];

        // First, find XX^{T} and n\mu
        for (int n = 0; n < numRow; n++) {
            for (int p1 = 0; p1 < numCol; p1++) {
                T2[p1] += A[n][p1];
                //A.getEntry(n, p1);
                // Fills up the Upper triangle
                for (int p2 = p1; p2 < numCol; p2++) {
                    T1[p1][p2] += A[n][p1] * A[n][p2];
                    //A.getEntry(n, p1) * A.getEntry(n, p2);
                }
            }
        }

        // Now, find \frac{XX^{T}}{n} and \mu
        for (int p1 = 0; p1 < numCol; p1++) {
            for (int p2 = p1; p2 < numCol; p2++) {
                if (p1 == p2) {
                    // Diagonal, only need to adjust according to number observation
                    T1[p1][p2] /= (double) numRow;
                } else {
                    // First Adjust, then set lower triangle
                    T1[p1][p2] /= (double) numRow;
                    T1[p2][p1] = T1[p1][p2];
                }
            }
            // Getting the mean
            T2[p1] /= (double) numRow;
        }

        // Now T1 = XX^{T} and T2 = E(X) = \mu
        // Variance is then simply T1 - T2 T2^{T}
        double[][] S = new double[numCol][numCol];
        for (int p1 = 0; p1 < numCol; p1++) {
            for (int p2 = 0; p2 < numCol; p2++) {
                S[p1][p2] = T1[p1][p2] - (T2[p1] * T2[p2]);
            }
        }
        return S;
    }

    /**
     * Adding a column vector of 1 to the left hand side of the input matrix
     * @param A design matrix
     * @return the augmented matrix
     */
    static public double[][] augmentedMatrix(double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;
        double[][] dblAugmented = new double[numRow][numCol + 1];

        //RealMatrix A_augmented = new Array2DRowRealMatrix(numRow, numCol + 1);
        //A_augmented.setSubMatrix(A.getData(), 0, 1);
        for (int i = 0; i < numRow; i++) {
            System.arraycopy(A[i], 0, dblAugmented[i], 1, numCol);
            dblAugmented[i][0] = 1;
        }
        return dblAugmented;
    }

    /**
     * Given a matrix of n observation and p variables, calculates the centered matrix
     * @param A design matrix
     * @return centered matrix
     */
    static public double[][] centeredMatrix(double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;

        // mu = E(X)
        //RealMatrix mu = meanMatrix(A);
        double[] mu = meanMatrix(A);
        //RealMatrix X = new Array2DRowRealMatrix(numRow, numCol);
        double[][] X = new double[numRow][numCol];

        for (int n = 0; n < numRow; n++) {
            for (int p = 0; p < numCol; p++) {
                X[n][p] = A[n][p] - mu[p];
                //double dblTemp = A.getEntry(n, p) - mu.getEntry(p, 1);
                ///X.setEntry(n, p, dblTemp);
            }
        }
        return X;
    }
    
    static public double[][] covarianceCenteredMatrix(double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;
                
        double[][] X = new double[numCol][numCol];
        for (int i = 0; i < numCol; i++) {
	    double[] Xi = X[i];
            for (int j = i+1; j < numCol; j++) {
                for (int k = 0; k < numRow; k++) {
                    // X[i][j] += (A[k][i] * A[k][j]);
		    Xi[j] += A[k][i] * A[k][j];
                }
                // X[j][i] = X[i][j];
		X[j][i] = Xi[j];
            }
            for (int k = 0; k < numRow; k++) {
                // X[i][i] += (A[k][i] * A[k][i]);
		Xi[i] += (A[k][i] * A[k][i]);
            }
        }
        return X;
    }
    
    /**
     * Transpose a matrix
     * @param A matrix
     * @return A^{T}
     */
    static public double[][] transposeMatrix(final double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;
        double[][] X = new double[numCol][numRow];
        for (int i = 0; i < numRow; i++) {
	    double[] Ai = A[i];
            for (int j = 0; j < numCol; j++) {
		X[j][i] = Ai[j];
            }
        }
        return X;
    }

    /**
     * Backward substitution solving Ux = y given that U is a upper triangle
     * @param U upper triangle
     * @param b response
     * @return result!
     */
    static public double[] backwardSubstitution(double[][] U, double[] b) {
        double[] Y = ArrayOperation.copyArray(b);
	int numRow = U.length;

	for (int i = numRow - 1; i >= 0; i--) {
	    double[] Ui = U[i];
	    for (int j = i+1; j < numRow; j++) {
		Y[i] -= Ui[j] * Y[j];
	    }
	    Y[i] /= Ui[i];
	}


        return Y;
    }

    /**
     * Forward substitution solving Lx = y given that L is a lower triangle
     * @param L lower triangle
     * @param b response
     * @return result!
     */
    static public double[] forwardSubstitution(double[][] L, double[] b) {
        double[] Y = ArrayOperation.copyArray(b);
	int numRow = L.length;

	for (int i = 0; i < numRow; i++) {
	    double[] Li = L[i];
	    for (int j = 0; j < i; j++) {
		Y[i] -= Li[j] * Y[j];
	    }
	    Y[i] /= Li[i];
	}
        return Y;
    }
    
    static public double[][] inverse(double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;
        if (numRow != numCol) {
            throw new RuntimeException("Not a square matrix");
        }
        
        double[][] B = new double[numRow][numRow];
        
        LUDecomposition lu = new LUDecomposition(A);
        for (int i = 0; i < numRow; i++) {
            double[] e = new double[numRow];
            e[i] = 1;
            double[] b = lu.solve(e);
            for (int j = 0; j < numRow; j++) {
                B[j][i] = b[j];
            }
        }
        return B;
    }
    
     /**
      * Calculating the sum of each row of a matrix
      * @param A n by m matrix
      * @return sum of matrix row by row
      */
    static public double[] matrixRowSum(double[][] A) {
        double[] rowSum = new double[A.length];

        for (int i = 0; i < A[0].length; i++) {
            for (int j = 0; j < A.length; j++) {
                rowSum[j] = rowSum[j] + A[j][i];
            }
        }

        return (rowSum);
    }

    
    
}
