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
//      Created Date :          2012-09-02
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.utils;

import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;

/**
 * This is a brand new version of the QR decomposition.  Where it took the 
 * original version (now in the LS class) and extended it to calculate the Q
 * matrix in a very naive way.  Currently lacking comments
 *
 * Similar to Cholesky decomposition, this is also declared as final because
 * there shouldn't be another way to do QR decomposition
 * 
 * @author Edwin
 */
final public class QRDecomposition {

    // Storing the working matrix
    private double[][] fullR;
    // The economy R matrix
    private double[][] R;
    // The economy Q matrix
    private double[][] Q;
    // the full Q matrix
    private double[][] fullQ;
    private int numRow;
    private int numCol;
    
    private boolean underDetermined = false;

    public QRDecomposition(double[][] data) {
        // Initialize.
        if (data[0].length > data.length) {
            /* Since it is an underdeterminded system, we need to find the 
             * decomposition of A^{T} to get QR
             */
            this.fullR = GeneralMatrixOperation.transposeMatrix(data);
            this.underDetermined = true;
        } else {
            this.fullR = GeneralMatrixOperation.copyMatrix(data);
        }
        
	//The full R will be computed using the original matrix
        this.numRow = this.fullR.length;
        this.numCol = this.fullR[0].length;
        this.fullQ = new double[numRow][numRow];
        this.Q = new double[numRow][numCol];
        this.R = new double[numCol][numCol];

	for (int i = 0; i < numRow; i++) {
	    fullQ[i][i]++;
	}

        // Compute the H_{i} in sequence, of the number of column
        // H = I - \rho * u * u^{T}
        for (int k = 0; k < numCol; k++) {
            double[] u = new double[numRow];
            // double sigma = 0;

            for (int i = k; i < numRow; i++) {
                u[i] = fullR[i][k];
                // sigma = Math.hypot(sigma, fullR[i][k]);
            }
	    double sigma = VectorCalculation.norm(u);
            if (sigma != 0.0) {
                // Adjust the sign to make sure that it is an addition
		// System.out.println("u[k] = " +u[k]+ " and sigma = " +sigma);
                if (u[k] < 0) {
                    sigma = -sigma;
		    // sigma *= -1;
                }

                u[k] += sigma;
                double rho = 1 / (u[k] * sigma);
                for (int i = k+1; i < numRow; i++) {
                    fullR[i][k] = 0;
                }

                fullR[k][k] = -sigma;
                // Find the Q at each step
		// Create the Identity matrix first
                double[][] newQ = new double[numRow][numRow];
                for (int i = 0; i < numRow; i++) {
                    newQ[i][i]++;
                }

		// // Then it does the subtraction
                // for (int j = k; j < numRow; j++) {
                //     for (int i = k; i < numRow; i++) {
                //         newQ[i][j] -= rho * u[i] *u[j];
                //     }
		// }

		// Since we are finding the outter product of two vector
		// it is guarantee to be symmetric
                for (int j = k; j < numRow; j++) {
		    // off diagonal
                    for (int i = j+1; i < numRow; i++) {
                        newQ[i][j] -= rho * u[i] *u[j];
			newQ[j][i] = newQ[i][j];
                    }
		    // Then the diagonal
		    newQ[j][j] -= rho * u[j] *u[j];
		}

                fullQ = MatrixMultiplication.multiply(fullQ,newQ,false,true);
                // fullQ = GeneralMatrixOperation.multiplyTransposeMatrix(fullQ, newQ);
                for (int j = k + 1; j < numCol; j++) {
                    double s = 0;
                    for (int i = k; i < numRow; i++) {
                        s += rho * u[i] * fullR[i][j];
                    }
                    for (int i = k; i < numRow; i++) {
                        fullR[i][j] -= u[i] * s;
                    }
                }
            }
        }

        // Getting economy R
        for (int i = 0; i < numCol; i++) {
            System.arraycopy(fullR[i], 0, R[i], 0, numCol);
        }
     
	// Economy size Q
        for (int i = 0; i < numRow; i++) {
//            for (int j = 0; j < numCol; j++) {
//                Q[i][j] = fullQ[i][j];
//            }
            System.arraycopy(fullQ[i], 0, Q[i], 0, numCol);
        }        
    }

    public double[][] getR() {
        return this.R;
    }

    public double[][] getRInverse() {
	double[][] X = new double[numCol][numCol];
	for (int k = 0; k < numCol; k++) {
            double[] e = new double[numCol];
            e[k] = 1;
            // Forward substitution to get the individual solution of each column
            // of the inverse of L.  
	    double[] x = GeneralMatrixOperation.backwardSubstitution(this.R, e);
            for (int i = 0; i < numCol; i++) {
		X[i][k] = x[i];
	    }
        }
	return X;
    }

    public double[][] getFullR() {
        return this.fullR;
    }

    public double[][] getQ() {
        return this.Q;
    }
    
    public double[][] getFullQ() {
        return this.fullQ;
    }
    
    public double[] solve(double[] b) {
        if (this.underDetermined) {
            // Solving where the matrix has more row than column
            return solveUD(b);
        } else {
            return solveOD(b);
        }
    }
    
    private double[] solveOD(double[] b) {
        /* 
         * For a standard over determined system, we require A=QR, which can 
         * then be solved with QRx = b 
         * Rx = Q^{T}b
         * x = R^{-1}Q^{T}b
         */
        double[] y = MatrixMultiplication.multiply(this.Q,b,true);
        // double[] y = GeneralMatrixOperation.transposeMultiplyMatrix(this.Q, b);
        return GeneralMatrixOperation.backwardSubstitution(this.R, y);
    }
    
    private double[] solveUD(double[] b) {
        /*
         * Under determined system, we already computed A^{T} = QR and then
         * (QR)^{T}x = b
         * R^{T}Q^{T}x = b where R^{T} = L
         * Q^{T}x = L^{-1}b
         * x = QL^{-1}b
         * where the Q here is the original decomposition from A^{T}
         */
        double[][] L = GeneralMatrixOperation.transposeMatrix(this.R);
        double[] y = GeneralMatrixOperation.forwardSubstitution(L, b);
        return MatrixMultiplication.multiply(this.Q, y);
    }
    
}
