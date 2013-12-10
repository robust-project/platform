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

/**
 * Note that the class is final because there are no constructors for it
 * and it is a straight input/output. 
 * 
 * @author Edwin
 */
final public class LS {

    /**
     * Get the coefficient of linear system.
     * @param data design matrix X
     * @param response vector Y
     * @return \beta vector
     */
    static public double[] Solve(double[][] data, double[] response) {
        if (data.length != response.length) {
            throw new IllegalArgumentException("Number of row not equal");
        }
        double[] beta;
        if (data.length > data[0].length) {
            beta = qrTSolve(data, response);
        } else if (data.length == data[0].length) {
            beta = luSolve(data, response);
        } else {
            beta = qrODSolve(data,response);
            //throw new IllegalArgumentException("More column than row");
        }
        return beta;
    }

    /**
     * Compute and return \beta from Y=X\beta using LU decomposition, the method
     * of choice given a square matrix
     * @param data X
     * @param response Y
     * @return \beta
     */
    static public double[] luSolve(double[][] data, double[] response) {
        int numRow;
        int numCol;
        double[][] X;
        double[] Y;
        X = GeneralMatrixOperation.copyMatrix(data);
        numRow = X.length;
        numCol = X[0].length;
        // When the number of row and column are the same
        // LU decomposition instead.  It is quicker, especially when it 
        // can make use of java native methods when doing partial pivoting

        double d = 1;
        int[] pivot = new int[numRow];
        // Setting the index of the pivot, which is the same at the start
        for (int i = 0; i < numRow; i++) {
            pivot[i] = i;
        }

        /*
         * From Golub and Van Loan, there are 6 different version of the 
         * arrangment / version of index i,j,k
         * I am making the choice of using the so-called scaled daxpy 
         * version, which is 3.2.9 from Golub and Van Loan.  No reason apart 
         * from the fact that I find it easier to understand due to similarity
         * relatively to the standard gaussian elimination
         */
        for (int k = 0; k < numCol; k++) {
            // Initialize pivot row index
            int p = k;
            // Setting the first value as max
            double maxValue = Math.abs(X[k][k]);
            // finding the pivot, i.e. the biggest number in the column and
            // the row index
            for (int i = k + 1; i < numRow; i++) {
                if (Math.abs(X[i][k]) > maxValue) {
                    p = i;
                }
            }

            // Exchange the rows if it has a pivot
            double[] dblTemp1D = new double[numCol];
            if (k != p) {
                /*
                 * First by defining a temporary array to copy the original row
                 * to. Then copy the pivot row to the current row, and finally
                 * re-inserting the original array (just looks complicated)
                 */
                System.arraycopy(X[p], 0, dblTemp1D, 0, numCol);
                System.arraycopy(X[k], 0, X[p], 0, numCol);
                System.arraycopy(dblTemp1D, 0, X[k], 0, numCol);
                // Also change the index
                int intTemp = pivot[p];
                pivot[p] = pivot[k];
                pivot[k] = intTemp;
                d = -d;
            }

            /* 
             * Compute multipliers and eliminate k-th column.
             * Testing if it is singular, else eliminate the column by first
             * dividing through the pivot and then the subtraction 
             */
            if (X[k][k] != 0.0) {
                for (int i = k + 1; i < numRow; i++) {
                    X[i][k] /= X[k][k];
                    for (int j = k + 1; j < numCol; j++) {
                        X[i][j] -= X[i][k] * X[k][j];
                    }
                }
            }
        }
        // Finish the main part of LU decomposition
        // Don't actually need the L or U matrix since they are in X

        // First check for singularity 
        for (int i = 0; i < numCol; i++) {
            if (X[i][i] == 0) {
                throw new RuntimeException("Singular matrix");
            }
        }

        // Lets set a working response (where Y = X\beta)
        // This can only be done after finding the pivot
        // Cannot use the copy operation
        Y = new double[numRow];
        for (int i = 0; i < numRow; i++) {
            Y[i] = response[pivot[i]];
        }

        /*
         * As per normal, solving in two stages
         * Ly = Pb
         * Ux = y
         * Just look at wikipedia...
         * Forward substitution on the lower triangle
         */
        for (int i = 0; i < numCol; i++) {
            // Because L[i][i] = 1, ignore diagonal terms
            for (int j = i + 1; j < numCol; j++) {
                //System.out.println("Index i = " +i+ " and j = " +j);
                Y[j] -= X[j][i] * Y[i];
            }
        }

        // Now back substitution on solving U
        for (int i = numCol - 1; i >= 0; i--) {
            Y[i] /= X[i][i];
            for (int j = 0; j < i; j++) {
                Y[j] -= X[j][i] * Y[i];
            }
        }
        return Y;
    }

    /**
     * Compute and return \beta from Y=X\beta using QR decomposition, this method
     * of choice given a rectangular matrix.  Householder transformation is used 
     * for the computation instead of Gram-Schmidt
     * 
     * @param data X
     * @param response Y
     * @return \beta
     */
    static public double[] qrSolve(double[][] data, double[] response) {
        int numRow;
        int numCol;
        double[][] X;
        double[] Y;

        X = GeneralMatrixOperation.copyMatrix(data);
        Y = ArrayOperation.copyArray(response);

        numRow = X.length;
        numCol = X[0].length;

        // The number of rows are greater than column
        // Getting a working copy of Y

        /*
         * QR decomposition  
         * Compute the H_{i} in sequence, of the number of column
         * H = I - \rho * u * u^{T}
         */
        for (int k = 0; k < numCol; k++) {
            /*
             * Funny enough, java provides a built in hypot but not for norm. So
             * lets just use that to prevent floating point issue
             * Get the column vector H_{k} out 
             */
            double[] u = new double[numRow];
            // double sigma = 0;
            for (int i = k; i < numRow; i++) {
                u[i] = X[i][k];
                // sigma = Math.hypot(sigma, X[i][k]);
            }
	    double sigma = VectorCalculation.norm(u);

            if (sigma != 0.0) {
                // Adjust the sign to make sure that it is an addition
                if (u[k] < 0) {
                    sigma = -sigma;
                }
                /*
                 * This is basically forming u = x + \sigma e_{k}
                 * where e = {1,0,\ldots,0}^{T}
                 * u = the the original vector
                 * then \rho = 1 / (\sigma u_{k})
                 * \sigma is \| u \| which is a scaling factor to convert the column to
                 * a unit vector
                 */
                u[k] += sigma;
                double rho = 1 / (u[k] * sigma);
                X[k][k] = -sigma;

                /*
                 * Transformation to the rest of the columns (to get R)
                 * Never compute Q
                 * \rho * u^{T} * X = uu^{T} \over \| u \| * X
                 * with X the original matrix (remaining of the original matrix)
                 * and uu^{T} \over \| u \| is the vector project of the form
                 * A_{B} = (< A, B > \over < B, B > ) B
                 * but now it is projecting all of column j \ldots p in a particular
                 * direction to i, where i < j, so that j is orthogonal to i
                 */
                for (int j = k + 1; j < numCol; j++) {
                    // compute v = \rho*u*X
                    double s = 0;
                    for (int i = k; i < numRow; i++) {
                        s += rho * u[i] * X[i][j];
                    }
                    // A - u * v i.e. HX, direct application
                    for (int i = k; i < numRow; i++) {
                        X[i][j] -= u[i] * s;
                    }
                }
                
                /*
                 * Transforming the response as well
                 * i.e. H_{i} Y
                 * This can be thought of regression the residual with column i
                 * which will have decreased the size of the residual vector Y
                 * and it can be interpreted as a feature selection mechanism
                 * as each column i can shrink/ decrease y-\hat{y} by a certain 
                 * amount.  If we let all the columns be free to rearrange 
                 * themselves then the column which make the biggest gain in terms
                 * of reducing y - \hat{y} is the most important feature (for the
                 * possible variables/ remaining variables).  It is a better 
                 * indication than, for example, calculating the p-value of the 
                 * \beta estimate which is a test that follows some asymtoptic 
                 * theory (fuck me!).
                 * 
                 * Personally, after coding it and really thinking through the problem
                 * I really like this interpretation of QR decomposition where we 
                 * think in terms of p number of n-dimension vector (p column, n row),
                 * and the aim is to find a set of scalar (\beta) for each of the 
                 * vector to reduce the distance between y and a linear combination 
                 * of these x (column) vector, which are the variables.  
                 */
                double e = 0;
                for (int i = k; i < numRow; i++) {
                    e -= rho * u[i] * Y[i];
                }
                // Needs to be in two stage, because Y[i] is in both the
                // LHS and the RHS while trying to do a multiplication and
                // also an addition on the whole array
                for (int i = k; i < numRow; i++) {
                    Y[i] += e * u[i];
                }

            }
            // Finished the computation of a single column
        }

        // Now Y = Q^{T}Y and X = R, back substitution
	for (int i = numCol - 1; i >= 0; i--) {
	    double[] Ui = X[i];
	    for (int j = i+1; j < numCol; j++) {
		// y[i] -= U[i][j] * y[j];
		Y[i] -= Ui[j] * Y[j];
	    }
	    Y[i] /= Ui[i];
	}
                
        // Because Y is the original resposne, but actually the back
        // substitution will give the output of the covariate, it has to
        // be truncated.
        double[] out = new double[numCol];
        System.arraycopy(Y, 0, out, 0, numCol);
        return out;
    }

    static public double[] qrTSolve(double[][] data, double[] response) {
        double[][] X = GeneralMatrixOperation.transposeMatrix(data);
        double[] Y = ArrayOperation.copyArray(response);

        int numRow = X[0].length;
        int numCol = X.length;

        for (int k = 0; k < numCol; k++) {
            double[] u = new double[numRow];
            System.arraycopy(X[k], k, u, k, numRow - k);
	    double sigma = VectorCalculation.norm(u);

            if (sigma != 0.0) {
                // Adjust the sign to make sure that it is an addition
                sigma *= Math.signum(u[k]);
                
                u[k] += sigma;
                double rho = 1 / (u[k] * sigma);
                X[k][k] = -sigma;

                for (int j = k + 1; j < numCol; j++) {
                    // compute v = \rho*u*X
                    double[] Xj = X[j];
                    double s = 0;
                    for (int i = k; i < numRow; i++) {
                        s += rho * u[i] * Xj[i];
                    }
                    // A - u * v i.e. HX, direct application
                    for (int i = k; i < numRow; i++) {
                        Xj[i] -= u[i] * s;
                    }
                }
                
                double e = 0;
                for (int i = k; i < numRow; i++) {
                    e -= rho * u[i] * Y[i];
                }
                for (int i = k; i < numRow; i++) {
                    Y[i] += e * u[i];
                }
            }
            // Finished the computation of a single column
        }

	for (int i = numCol - 1; i >= 0; i--) {
	    double[] Ui = X[i];
	    for (int j = i+1; j < numCol; j++) {
		Y[i] -= X[j][i] * Y[j];
	    }
	    Y[i] /= Ui[i];
	}
        
        double[] out = new double[numCol];
        System.arraycopy(Y, 0, out, 0, numCol);
        return out;
    }

    
    /**
     * Compute and return \beta from Y=X\beta using Cholesky decomposition, the 
     * best method given that X is a square positive-definite matrix and will 
     * remain constant and we require to solve many different Y.
     * 
     * @param data X
     * @param response Y
     * @return \beta
     */
    static public double[] cholSolve(double[][] data, double[] response) {
        if (data.length != data[0].length) {
            throw new IllegalArgumentException("Chol: X not square");
        } 
        if (data.length != response.length) {
            throw new IllegalArgumentException("Chol: X and Y not same size");
        }
        
        int numRow = data.length;
        boolean positiveDefinite = true;
        double[][] L = new double[numRow][numRow];
        double[] Y;
        
        Y = ArrayOperation.copyArray(response);
        
	for (int j = 0; j < numRow; j++) {
	    // d is a summation over L_{j,i}^2
	    double d = 0;
	    for (int i = 0; i < j; i++) {
		double s = data[j][i];
		for (int k = 0; k < i; k++) {
		    s -= L[i][k] * L[j][k];
		}
		L[j][i] = s / L[i][i];
		d += L[j][i]*L[j][i];
	    }
	    L[j][j] = Math.sqrt(data[j][j] - d);
	    if (L[j][j] <= 0) {
		positiveDefinite = false;
		System.out.println("Not a positive definite matrix");
		L[j][j] = 0;
	    }
	}
        /*
         * Using the original matrix instead of L, because in theory (code setup), 
         * it never goes out of the triangle of L.  Saves the time of copying
         */

        // Forward substitution of L
        for (int i = 0; i < numRow; i++) {
            Y[i] /= L[i][i];
            for (int j = i + 1; j < numRow; j++) {
                Y[j] -= L[j][i] * Y[i];
            }
        }

        // L' 
        // Exactly the same as U in LU
        for (int i = numRow - 1; i >= 0; i--) {
            Y[i] /= L[i][i];
            for (int j = 0; j < i; j++) {
                Y[j] -= L[i][j] * Y[i];
            }
        }
        return Y;
    }
    
    /**
     * Compute and return \beta from Y=X\beta using QR decomposition, even if 
     * the number of column is bigger than the number of rows. 
     * @param data X
     * @param response Y
     * @return \beta
     */
    static public double[] qrODSolve(double[][] data, double[] response) {
        QRDecomposition qr = new QRDecomposition(data);
        return qr.solve(response);  
    }
    
}
