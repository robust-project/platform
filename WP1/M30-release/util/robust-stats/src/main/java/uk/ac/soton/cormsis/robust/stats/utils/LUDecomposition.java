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

import org.apache.log4j.Logger;

/* TODO: Might have problem when the number of column is greater than row even though
* we would normally expect a square matrix
*/

public class LUDecomposition {

    static Logger log = Logger.getLogger(LUDecomposition.class);

    // Storing the data i.e. matrix A!!!
    private double[][] A;
    // Storing the movified data
    // private double[][] X;
    // The lower matrix
    private double[][] L;
    // The upper matrix
    private double[][] U;
    private int numRow;
    private int numCol;
    // It has to be noted that I am only storing the pivot and not the permutation matrix
    // even though storing the permutation is easier to understand i.e. matlab output
    private int[] pivot;
    // the pivoting of the column if we want to do a full pivot
    private int[] pivotColumn;

    /*
     * This is the sign calculated during pivoting, which is required for determinant 
     * calculation.  Variable name d for simplicity, which is also what Numerical 
     * Recipe calls it
     */
    private double d = 1;
    private double error = 0;
    private boolean fullPivot = false;
    
    /**
     * LU decomposition on the matrix A.
     * @param data A
     */
    public LUDecomposition(double[][] data) {
	try {
	    checkDimension(data);
	} catch (Exception e) {
	    log.error("LU decomposition:"+e);
	}
	try {
	decompose(data);
	} catch (Exception e) {
	    log.error("Lu decomposition" + e);
	}
    }

    public LUDecomposition(double[][] data, boolean fullPivot) {
	try {
	    checkDimension(data);
	} catch (Exception e) {
	    log.error("LU decomposition:"+e);
	}
	try {
	    if (fullPivot) {
		decomposeFull(data);
		fullPivot = true;
	    } else {
		decompose(data);
	    }
	} catch (Exception e) {
	    log.error("LU decomposition" + e);
	}
    }

    /**
     * Returns the U matrix from A
     * @return U
     */
    public double[][] getU() {
        return this.U;
    }

    /**
     * Returns the L matrix from A
     * @return L
     */
    public double[][] getL() {
	double[][] Lp = new double[this.L.length][this.L[0].length];
	for (int i = 0; i < numRow; i++) {
	    System.arraycopy(L[pivot[i]],0,Lp[i],0,this.L[0].length);
	}
        return Lp;
    }

    /**
     * Return the pivot row index from A
     * @return pivots
     */
    public int[] getPivot() {
        return this.pivot;
    }

    public int[] getColumnPivot() {
	return this.pivotColumn;
    }

    /**
     * Return the determinant from A
     * @return \left| A \right|
     */
    public double getDet() {
        if (this.numCol != this.numRow) {
            throw new IllegalArgumentException("Must be square matrix");
        }
        double t = this.d;
        for (int i = 0; i < this.numCol; i++) {
            // Since L[i][i] := 1
            t *= this.U[i][i];
        }
        return t;
    }

    /**
     * Return the flag of whether the matrix is singular
     * @return True or False
     */
    public boolean isSingular() {
        for (int i = 0; i < this.numCol; i++) {
            // Because L[i][i] is set to 1 by default
            if (this.U[i][i] == 0) {
                return true;
            }
        }
        return false;
    }

    public double[] solve(double[] b) {

        // using the notation Ax=b
        if (this.numRow != b.length) {
            throw new IllegalArgumentException("Unequal row dimensions");
        }
        // Testing whether it the matrix is singular
        if (isSingular()) {
            throw new RuntimeException("Singular matrix");
        }

        // Lets set a working working (change of notation Y = X\beta)
        double[] Y = new double[this.numRow];

        for (int i = 0; i < this.numRow; i++) {
            Y[i] = b[this.pivot[i]];
        }
        Y = GeneralMatrixOperation.forwardSubstitution(this.L, Y);
        Y = GeneralMatrixOperation.backwardSubstitution(this.U, Y);

        return Y;
    }

    /**
     * Iterative refinement, passing through the first solution once, i.e. we
     * assume that there are error in the first solution, that is 
     * A(x + \delta x) = b + \delta b
     * \delta b = A(x + \delta b) - b (2)
     * Subtracting with Ax=b gives
     * A \delta x = \delta b
     * and substitution back to (2) is
     * A\delta x = A(x+\delta x) - b
     * It is immediate that (x+\delta x) is the \hat{x} from the original Ax=b
     * which means that \delta x = A^{-1} (A\hat{x} - b).  
     * and x is refined through \hat{x} - \delta x
     * Can probably extend on this to refine it a few more times
     * @param b
     * @return the solved vector after iterative refinement
     */
    public double[] solveIR(double[] b) {
        if (this.numRow != b.length) {
            throw new IllegalArgumentException("Unequal row dimensions");
        }
        // Testing whether it the matrix is singular
        if (isSingular()) {
            throw new RuntimeException("Singular matrix");
        }

        // Lets set a working working (change of notation Y = X\beta)
        double[] xhat = new double[this.numRow];
        for (int i = 0; i < this.numRow; i++) {
            xhat[i] = b[this.pivot[i]];
        }
        xhat = GeneralMatrixOperation.forwardSubstitution(this.L, xhat);
        xhat = GeneralMatrixOperation.backwardSubstitution(this.U, xhat);
        
        // Now we have \hat{x} = x + \delta x
        // and we make use of the original deep copy of the data
        double[] bstar =MatrixMultiplication.multiply(this.A, xhat);
        for (int i = 0; i < this.numRow; i++) {
            bstar[i] -= b[i];
        }
        // \star{b} = A(x + \delta x) - b = A\hat{x} b
        double[] deltaX = GeneralMatrixOperation.forwardSubstitution(this.L, bstar);
        deltaX = GeneralMatrixOperation.backwardSubstitution(this.U, deltaX);
        this.error = 0;
        for (int i = 0; i < this.numRow; i++) {
            xhat[i] -= deltaX[i];
            // We really want to use hypot because we are basically finding the 
            // machine error
            this.error = Math.hypot(deltaX[i], error);
        }
        return xhat;
    }

    /**
     * Finds the inverse of the square matrix A after performing LU decomposition
     * 
     * @return A^{-1}
     */
    public double[][] inverse() {
        double[][] B = new double[numRow][numRow];
        for (int i = 0; i < numRow; i++) {
            double[] e = new double[numRow];
            e[pivot[i]] = 1;
	    e = GeneralMatrixOperation.forwardSubstitution(this.L, e);
	    e = GeneralMatrixOperation.backwardSubstitution(this.U, e);
	    for (int j = 0; j < numRow; j++) {
		B[j][i] = e[j];
	    }
        }
	if(fullPivot) {
	    double[][] Y = new double[numRow][numRow];
	    for (int i = 0; i < numRow; i++) {
		Y[pivotColumn[i]] = B[i];
	    }
	}
	return B;
    }
    
    public double getError() {
        return this.error;
    }

    private void decompose(double[][] data) throws Exception {
        // Making a local copy to operate on
        // this.X = GeneralMatrixOperation.copyMatrix(data);
	double[][] X = GeneralMatrixOperation.copyMatrix(data);

        // Making a deep copy so that the original can be obtained later
        // i.e. X = working copy, A = original copy
        this.A = GeneralMatrixOperation.copyMatrix(data);
        // Finding the other information required to do the decomposition
        this.numRow = data.length;
        this.numCol = data[0].length;
        // The number of pivot cannot exceed the number of row
        this.pivot = new int[this.numRow];
        // Setting the index of the pivot, which is the same at the start
        for (int i = 0; i < this.numRow; i++) {
            this.pivot[i] = i;
        }

        /*
         * From Golub and Van Loan, there are 6 different version of the 
         * arrangment / version of index i,j,k
         * I am making the choice of using the so-called scaled daxpy 
         * version, which is 3.2.9 from Golub and Van Loan.  It is only 
         * because I find it easier to understand as it is similar to 
         * the standard gaussian elimination which I learned in school
         */

        for (int k = 0; k < this.numCol; k++) {
            // Initialize pivot
            int p = k;
            // Setting the first value as max
            // finding the pivot, i.e. the biggest number in the column and
            // the row index
            for (int i = k; i < this.numRow; i++) {
                if (Math.abs(X[i][k]) > Math.abs(X[i][i])) {
                    p = i;
                }
            }

            // Exchange the rows if it has a pivot
            double[] dblTemp1D = new double[this.numCol];
            if (k != p) {
                /*
                 * First by defining a temporary array to copy the original row
                 * to. Then copy the pivot row to the current row, and finally
                 * re-inserting the original array (just looks complicated)
                 */
                System.arraycopy(X[p], 0, dblTemp1D, 0, this.numCol);
                System.arraycopy(X[k], 0, X[p], 0, this.numCol);
                System.arraycopy(dblTemp1D, 0, X[k], 0, this.numCol);
                // Also change the index
                int intTemp = this.pivot[p];
                this.pivot[p] = this.pivot[k];
                this.pivot[k] = intTemp;
                this.d = -this.d;
            }

            /* 
             * Compute multipliers and eliminate k-th column.
             * Testing if it is singular, else eliminate the column by first
             * dividing through the pivot and then the subtraction 
             */
            if (X[k][k] == 0.0) {
		throw new RuntimeException("Order " +k+ "is zero");
	    } else {
		double[] Xk = X[k];
                for (int i = k + 1; i < this.numRow; i++) {
		    double[] Xi = X[i];
		    Xi[k] /= Xk[k];
                    for (int j = k + 1; j < this.numCol; j++) {
			Xi[j] -= Xi[k] * Xk[j];
                    }
                }
            }
        }

        // Now get the L and U matrix.  Like matlab, L has 1.0 in diagonal
        this.L = new double[this.numRow][this.numCol];
        this.U = new double[this.numRow][this.numCol];

	for (int i = 0; i < this.numRow; i++) {
            System.arraycopy(X[i], i, this.U[i], i, this.numCol - i);
//	    for (int j = i; j < this.numCol; j++) {
//		this.U[i][j] = this.X[i][j];
//	    }
	    // this.U[i][i] = this.X[i][i];
	    this.L[i][i]++;
            System.arraycopy(X[i], 0, this.L[i], 0, i);
	}
        // This gets the correct L and U, so the pivot needs to be obtained separately
    }

    private void decomposeFull(double[][] data) throws Exception {
        // Making a local copy to operate on
        // this.X = GeneralMatrixOperation.copyMatrix(data);
        double[][] X = GeneralMatrixOperation.copyMatrix(data);

        // Making a deep copy so that the original can be obtained later
        // i.e. X = working copy, A = original copy
        this.A = GeneralMatrixOperation.copyMatrix(data);
        // Finding the other information required to do the decomposition
        this.numRow = data.length;
        this.numCol = data[0].length;
        // The number of pivot cannot exceed the number of row
        this.pivot = new int[this.numRow];
	this.pivotColumn = new int[this.numRow];
        // Setting the index of the pivot, which is the same at the start
        for (int i = 0; i < this.numRow; i++) {
            this.pivot[i] = i;
	    this.pivotColumn[i] = i;
        }

        for (int k = 0; k < this.numCol; k++) {
            // Initialize pivot
            int maxRow = k;
	    int maxColumn = k;
            // finding the pivot, i.e. the biggest number in the column and
            // the row index
	    for (int j = k; j < numRow; j++) {
		for (int i = k; i < this.numRow; i++) {
		    if (Math.abs(X[i][k]) > X[maxRow][maxColumn]) {
			maxRow = j;
			maxColumn = i;
		    }
		}
	    }

	    int tempColumn = pivotColumn[k];
	    pivotColumn[k] = pivotColumn[maxColumn];
	    pivotColumn[maxColumn] = tempColumn;

            // Exchange the rows if it has a pivot
            double[] dblTemp1D = new double[this.numCol];
            if (k != maxRow) {
                System.arraycopy(X[maxRow], 0, dblTemp1D, 0, this.numCol);
                System.arraycopy(X[k], 0, X[maxRow], 0, this.numCol);
                System.arraycopy(dblTemp1D, 0, X[k], 0, this.numCol);
                // Also change the index
                int intTemp = this.pivot[maxRow];
                this.pivot[maxRow] = this.pivot[k];
                this.pivot[k] = intTemp;
                this.d = -this.d;
            }

            /* 
             * Compute multipliers and eliminate k-th column.
             * Testing if it is singular, else eliminate the column by first
             * dividing through the pivot and then the subtraction 
             */
            if (X[k][k] == 0.0) {
		throw new RuntimeException("Order " +k+ "is zero");
	    } else {
		double[] Xk = X[k];
                for (int i = k + 1; i < this.numRow; i++) {
		    double[] Xi = X[i];
		    Xi[k] /= Xk[k];
                    for (int j = k + 1; j < this.numCol; j++) {
			Xi[j] -= Xi[k] * Xk[j];
                    }
                }
            } 
        }

        // Now get the L and U matrix.  Like matlab, L has 1.0 in diagonal
        this.L = new double[this.numRow][this.numCol];
        this.U = new double[this.numRow][this.numCol];

	for (int i = 0; i < this.numRow; i++) {
            System.arraycopy(X[i], i, this.U[i], i, this.numCol - i);
	    this.L[i][i]++;
            System.arraycopy(X[i], 0, this.L[i], 0, i);
	}
    }

    private void checkDimension(double[][] A) throws Exception {
	if (A.length != A[0].length) {
	    throw new RuntimeException("Number of row and column not equal");
	}
    }
    

}
