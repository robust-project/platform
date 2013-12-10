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
 * I have decided to make this class final because I don't see why anyone will
 * want to extend Cholesky decomposition. Every operation that is related should
 * be done within the class. Also be aware that there aren't any setter's (or
 * mutators) which can change the values of L or number of rows apart from using the
 * insert and delete function
 *
 * @author Edwin
 * @version 6 th
 */
final public class CholeskyDecomposition {

    /*
     * L matrix, I am only interested in computing L, instead of R (or U/Upper)
     * because that is what I normally use when decomposing the covariance of a
     * normal distribution
     * <p>
     * The algorithm actually intends to compute R, but is reflected here to L
     * There is no test for Symmetry... the reason why this code is fast
     */
    private double[][] L = null;
    //private double[][] X;
    // row must be equal to column
    private int numRow = 0;
    private boolean positiveDefinite = true;
    // fields that are stored if some data are being stored and we would like to
    // solve some linear equation
    private double[][] xT = null;
    private boolean hasXTranspose = false;

    /**
     * Empty Cholesky constructor. 
     */
    public CholeskyDecomposition() {
    }

    /**
     * Cholesky decomposition given a matrix X, then generate A depending on
     * whether X is transposed.  i.e X^{T}X for normal usage, or XX^{T} if
     * the input matrix needs to be transposed
     * 
     * @param X design matrix where row = observation, col = variables
     * @param transposeX whether X is X (false) or X^{T} (true)
     */
    public CholeskyDecomposition(double[][] X, boolean transposeX) {
	if (transposeX) {	    
	    double[][] xxT = MatrixMultiplication.outerProduct(X);
	    this.xT = GeneralMatrixOperation.copyMatrix(X);
	    this.hasXTranspose = true;
	    initialize(xxT);
	} else { 
	    double[][] xTx = MatrixMultiplication.innerProduct(X);
	    this.xT = GeneralMatrixOperation.transposeMatrix(X);
	    this.hasXTranspose = true;
	    initialize(xTx);
	}
    }

    /**
     * Given a matrix A, initialize the class, compute and store the lower
     * triangle L from the Cholesky decomposition
     *
     * @param A positive-definite symmetric matrix
     */
    public CholeskyDecomposition(double[][] A) {
        initialize(A);
    }

    private void initialize(final double[][] A) {
        // Checking whether it is a sqaure matrix
        if (A[0].length != A.length) {
            throw new IllegalArgumentException("Must be a square matrix");
        }

        //this.L = GeneralMatrixOperation.copyMatrix(A);
        this.L = new double[A.length][A.length];
        this.numRow = this.L.length;

        /* This is the replacemnt code which goes more in line with
         * the standard formula of calculating a Cholesky decomposition i.e.
         * 1.) L_{i,j} = \frac{1}{L_{j,j}}(A_{i,j}-\sum_{k=1}^{j-1}L_{i,k}L_{j,k}
         * 2.) L_{j,j} = sqrt{ A_{j,j} - \sum_{k=1}^{j-1} L_{j,k}^{2}}
         * <p>
         * It also skips the check of (i == j) which is a waste of operation.
         * The changes were made partly due to the extension of the various
         * possible operation on the L matrix given by Cholesky decomposition
         * and to make the code seem more structured and in sync with the rest
         * i.e. insert of a new row/column
         * <p>
         * The reason why the loop goes the other way is that the original 
         * code was designed not to manipulate the input matrix because I 
         * suspected there might be some referencing issue when porting it 
         * over to the least square solver, but I was being stupid, because
         * I never needed to change anything in matrix A anyway
         */
        for (int j = 0; j < this.numRow; j++) {
            // d = diagonal = a summation over L_{j,i}^2
            double d = 0;
            double[] Lj = L[j];
            double[] Aj = A[j];
            for (int i = 0; i < j; i++) {
                // double s = A[j][i];
                double s = Aj[i];
                double[] Li = L[i];
                for (int k = 0; k < i; k++) {
                    s -= Li[k] * Lj[k];
                }
                // L[j][i] = s / L[i][i];
                Lj[i] = s / L[i][i];
                d += Lj[i] * Lj[i];
            }
            double p = Aj[j] - d;
            if (p <= 0 ) {
                this.positiveDefinite = false;
                System.out.print("Not a positive definite matrix");
                Lj[j] = 0;
            } else {
                Lj[j] = Math.sqrt(p);
            }
        }
	// Throws an error if it is not a positive definite matrix.  Maybe throwing
	// an error will disrupt the flow so maybe just a flag/message is fine
	if (this.positiveDefinite) {
	} else {
	    throw new RuntimeException("Not a positive definite matrix");
	}
    }

    /**
     * Getting the lower triangle of a Cholesky decomposition of LL^{T}
     *
     * @return L matrix
     */
    public double[][] getL() {
        return this.L;
    }

    /**
     * Finds the inverse of the L matrix after decomposition
     * 
     * @return L^{-1}
     */
    public double[][] getLInverse() {
        double[][] X = new double[numRow][numRow];

        // Solve against an identity, each time a single column of the identity
        for (int k = 0; k < numRow; k++) {
            double[] e = new double[numRow];
            e[k] = 1;
            // Forward substitution to get the individual solution of each column
            // of the inverse of L.  
	    double[] x = GeneralMatrixOperation.forwardSubstitution(L, e);
            for (int i = 0; i < numRow; i++) {
		X[i][k] = x[i];
	    }
        }
        return X;
    }

    public double[][] getAinverse() {
	double[][] Linv = getLInverse();
	return MatrixMultiplication.multiply(Linv,Linv,true,false);
    }

    /**
     * If you are some crazy guy that wants to have a R instead of an L, feel
     * free to do so.
     *
     * @return R
     */
    public double[][] getR() {
        return GeneralMatrixOperation.transposeMatrix(this.L);
    }

    /**
     * Flag to determine whether the matrix is positive definite
     *
     * @return True or False
     */
    public boolean getFlag() {
        return this.positiveDefinite;
    }

    /**
     * Finding the solution of Ax=b given that A was initialized by this class
     *
     * @param response b vector
     * @return result! (x)
     */
    public double[] solve(double[] response) {
	checkDimension(response);
        double[] Y = ArrayOperation.copyArray(response);
        Y = GeneralMatrixOperation.forwardSubstitution(this.L, Y);
         Y = GeneralMatrixOperation.backwardSubstitution(GeneralMatrixOperation.transposeMatrix(this.L), Y);

        return Y;
    }

    /**
     * Solve Ax=b via the normal equation where it is A^{T}Ax=A^{T}b (where A can be transposed of X,
     * depending on what is being initialized).
     * <p>
     * Given that A was initialized, an A^{T} was calculated
     * and stored in this class.  Then the Cholesky decompose A^{T}A and do the multiplcation of A^{T}
     * b here and solve.
     *
     * @param response vector
     * @return x in the linear system
     */
    public double[] solveXT(double[] response) {
        if (this.hasXTranspose) {
        } else {
            throw new IllegalArgumentException("Chol: does not have X^{T}");
        }
	if (response.length != xT[0].length) {
            throw new IllegalArgumentException("Chol: y dimension not equal to x");
        }
        double[] Y = MatrixMultiplication.multiply(xT, response);
        Y = GeneralMatrixOperation.forwardSubstitution(this.L, Y);
        Y = GeneralMatrixOperation.backwardSubstitution(GeneralMatrixOperation.transposeMatrix(this.L), Y);
        return Y;
    }

    /**
     * Finding the solution of Lx=b given that L is a Cholesky decomposition of
     * A where A was matrix initialized by this class
     *
     * @param response b
     * @return result! (x)
     */
    public double[] solveL(double[] response) {
	checkDimension(response);
        return GeneralMatrixOperation.forwardSubstitution(this.L, response);
    }

    /**
     * Returns the log determinant i.e. | \Sigma | the constant part in normal
     * distribution (log likelihood)
     *
     * @return log ( | A | )
     */
    public double getLogDet() {
        double value = 0;
        for (int i = 0; i < this.numRow; i++) {
            value += Math.log(this.L[i][i]);
        }
        return 2 * value;
    }

    /**
     * Actually gives the determinant of the matrix in normal scale
     *
     * @return determinant
     */
    public double getDet() {
        return Math.exp(getLogDet());
    }

    /**
     * Currently, it does not check the validity of the output, i.e. whether it
     * is positive definite or not. Performs an update on the lower triangle of
     * current A matrix, by inserting a column/row to the right/bottom of
     * current A. 
     * <p>
     * Given that A = LL^{T}, define L^{T} = R, then L = R^{-1}A. It
     * is then obvious that each column vector of A gives the corresponding row
     * of L. 
     * <p>
     * Equip with the knowledge that the non-diagonal of the Cholesky is
     * being calculated by the equations L_{i,j} = \frac{1}{L_{j,j}} (A_{i,j} -
     * \sum_{k=1}^{j-1}L_{i,j}L_{j,k} ) which only depend on the previous column
     * (or row, as L = R^{T}) it is immediately obvious R^{-1} x_j = L_j for all
     * the off-diagonal elements. And the diagonal element in the new row can be
     * found by the standard procedure which is L_{j,j} = sqrt( A_{j,j} -
     * \sum_{k=1}^{j-1} L_{j,k}^2)
     *
     * @param v vector of size n+1 given matrix A is \left[ n \times \right]
     */
    public void insert(double[] v) {
        if (numRow == 0) {
            L = new double[1][1];
            L[0][0] = Math.sqrt(v[0]);
            numRow++;
        } else {
            // Test if we are getting a sensible input vector
            if (v.length == (numRow + 1)) {
                // we are good
            } else {
                throw new IllegalArgumentException("Not a valid vector");
            }
            // System.out.println("Size of vector v = " +v.length+ " and L = " +numRow);
            // First, we have to copy the original into the new matrix
            double[][] L_new = new double[numRow + 1][numRow + 1];

            for (int i = 0; i < L.length; i++) {
                System.arraycopy(L[i], 0, L_new[i], 0, numRow);
            }
            /* Now we want to find the new row through forward substitution 
             * on the Cov(j,A), which is the j row/column of the Gram matrix
             * of the active set A.
             */
            double[] b = new double[numRow];
            System.arraycopy(v, 0, b, 0, numRow);

            double[] xhat = GeneralMatrixOperation.forwardSubstitution(L, b);
            double s = 0;

            double[] LnumRow = L_new[numRow];
            for (int i = 0; i < numRow; i++) {
                // L_new[numRow][i] = xhat[i];
                LnumRow[i] = xhat[i];
                s += xhat[i] * xhat[i];
            }

            L_new[numRow][numRow] = Math.sqrt(v[numRow] - s);
            numRow++;
            this.L = L_new;
        }
    }

    /**
     * Delete a row of the input matrix A
     *
     * @param index to be deleted
     */
    public void delete(int index) {
        if (numRow == 1) {
            this.L = null;
            numRow--;
        } else {
            double[][] L_new = new double[numRow - 1][numRow - 1];

            // First, we keep the column length but delete the row
            double[][] L_temp = new double[numRow - 1][numRow];

            for (int j = 0; j < index; j++) {
                System.arraycopy(L[j], 0, L_temp[j], 0, numRow);
            }

            for (int j = index + 1; j < numRow; j++) {
                System.arraycopy(L[j], 0, L_temp[j - 1], 0, numRow);
            }

            /* A built in givens plane rotation for a vector with 2 element 
             * such that it produce Qx = r, where r[2] = 0 and Q is the orthogonal
             * matrix.  We also check that the second element of x isn't a zero, else
             * it will just simply return an identity matrix
             */
            for (int k = index; k < numRow - 1; k++) {
                int k1 = k + 1;
                // Find both of our components of Q and r
                double[][] Q = new double[2][2];
                if (L_temp[k][k1] != 0) {
                    // Find the norm of x
                    double s = Math.hypot(L_temp[k][k1], L_temp[k][k]);
                    Q[0][0] = L_temp[k][k] / s;
                    Q[0][1] = L_temp[k][k1] / s;
                    Q[1][0] = -L_temp[k][k1] / s;
                    Q[1][1] = L_temp[k][k] / s;
                    L_temp[k][k] = s;
                    L_temp[k][k1] = 0;
                } else {
                    Q[0][0]++;
                    Q[1][1]++;
                }

                /* Check the position of the elements, if there are off-diagonal 
                 * elements on the right hand size, we need to eliminate them
                 * Now we have our r (which is x), and Q, it is simply multiplying
                 * it through 
                 */
                if (k < L_temp.length - 1) {
                    double[][] U1 = new double[2][L_temp.length - k + 1];
                    for (int i = k + 1; i < L_temp.length; i++) {
                        int ii = i - (k + 1);
                        U1[0][ii] = L_temp[i][k];
                        U1[1][ii] = L_temp[i][k1];
                    }
                    /* And we set it back to the matrix.  Seems complicated but
                     * that is only because we can't set and compute at the same
                     * line, so we have to do the multiplication beforehand
                     */
                    double[][] V = MatrixMultiplication.multiply(Q, U1);
                    for (int i = k + 1; i < L_temp.length; i++) {
                        int ii = i - (k + 1);
                        L_temp[i][k] = V[0][ii];
                        L_temp[i][k1] = V[1][ii];
                    }
                }
            }
            // Remove the last column (which is already a vector of zero)
            for (int i = 0; i < L_new.length; i++) {
                System.arraycopy(L_temp[i], 0, L_new[i], 0, L_new.length);
            }
            numRow--;
            this.L = L_new;
        }
    }

    private void checkDimension(double[] y) {
        if (y.length != this.numRow) {
            throw new IllegalArgumentException("Chol: Dimension not equal to solve.  b = " +y.length+ " and A = " +this.numRow);
        }
    }
}
