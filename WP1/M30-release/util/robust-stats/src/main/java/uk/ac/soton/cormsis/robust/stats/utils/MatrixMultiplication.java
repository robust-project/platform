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
//      Created Date :          2012-09-21
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.utils;

import org.apache.log4j.Logger;

/**
 * A class that will (should) carry out all of the matrix multiplication <p> A
 * lot of the things are being declared as final here because it there are so
 * many different small component so I was getting confused and assigning final
 * to a variable means that I am not expecting it to change <p> The other idea
 * is to make as many class private as possible in an attempt to convice the JVM
 * that this method needs to be compiled into native code and also many sure
 * that some of the computation is forced to go over as many times as possible
 * and make it a 'HotSpot' <p> Most of the calculation have transposed some part
 * in an attempt to abuse the row operation of java as much as possible
 *
 * @author Edwin
 * @version 2nd
 */
public class MatrixMultiplication {

    static Logger log = Logger.getLogger(LUDecomposition.class);

    /**
     * Multiply two matrix with decision of transposing one or both matrix
     *
     * @param A matrix
     * @param B matrix
     * @param transposeA false = A, true = A^{T}
     * @param transposeB false = B, true = B^{T}
     * @return some form of AB depending on the tranpsose option
     */
    static public double[][] multiply(final double[][] A,
				      final double[][] B,
				      final boolean transposeA,
				      final boolean transposeB) {

	if (transposeA) {
	    if (transposeB) {
		return multiplyTranspose(transpose(A), B);
	    } else {
		return multiplyTranspose(transpose(A), transpose(B));
	    }
	} else {
	    if (transposeB) {
		return multiplyTranspose(A, B);
	    } else {
		return multiplyTranspose(A, transpose(B));
	    }
	} 
    }


    /**
     * Outer product defined to be XX^{T}
     *
     * @param A matrix of \left[ n \times p \right]
     * @return matrix of \left[ n \times n \right]
     */
    static public double[][] outerProduct(final double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;
        double[][] C = new double[numRow][numRow];

        selfMultiply(A, C, numRow, numCol);
        return C;
    }

    /**
     * Inner product defined to be X^{T}X
     *
     * @param A matrix of \left[ n \times p \right]
     * @return matrix of \left[p \times p \right]
     */
    static public double[][] innerProduct(final double[][] A) {
        int numRow = A.length;
        int numCol = A[0].length;
        double[][] C = new double[numCol][numCol];
        selfMultiply(transpose(A), C, numCol, numRow);
        return C;
    }

    /**
     * Multiply two matrix
     *
     * @param A first matrix
     * @param B second matrix
     * @return A*B
     */
    static public double[][] multiply(final double[][] A, final double[][] B) {
	//try {
	return multiply(A, B, false, false);
	// } catch (Exception e) {
	// log.error("Matrix multiplication:" +e);
	// }
    }

    /**
     * Alternative way to multiply a matrix. Will be the faster method on some
     * machine
     *
     * @param A matrix
     * @param B matrix
     * @return A*B
     */
    static public double[][] multiplyBrow(final double[][] A, final double[][] B) {
        int Arow = A.length;
        int Brow = B.length;
        int Bcol = B[0].length;
	try {
	    checkDimension(A, B);
	} catch (Exception e) {
	    log.error("Matrix multiplication:" +e);
	}
        double[][] C = new double[Arow][Bcol];
        for (int j = 0; j < Bcol; j++) {
            double[] bColj = new double[Brow];
            for (int k = 0; k < Brow; k++) {
                bColj[k] = B[k][j];
            }
            for (int i = 0; i < Arow; i++) {
                C[i][j] = dot(A[i], bColj);
            }
        }
        return C;
    }

    /**
     * Multiply a matrix with a vector with a choice of transposing the matrix
     *
     * @param A Matrix
     * @param b vector
     * @param transposeA whether A should be transposed
     * @return vector of length A row/ column (no transpose/ transposed)
     */
    static public double[] multiply(final double[][] A, final double[] b, boolean transposeA) {
        if (transposeA) {
            return multiplyVector(transpose(A), b);
        } else {
            return multiplyVector(A, b);
        }
    }

    /**
     * Multiply a matrix with a vector
     *
     * @param A matrix
     * @param b vector
     * @return vector of \left[ A row \times 1 \right]
     */
    static public double[] multiply(final double[][] A, final double[] b) {
	try {
	    checkDimension(A, b);
	} catch (Exception e) {
	    log.error("Matrix multiplication"+e);
	}
        return multiplyVector(A, b);
    }

    static private double[][] multiplyTranspose(final double[][] A, final double[][] B) {
        // Because [n x p] [q x m], so B row is the C column because we have transposed B
        int Acol = A[0].length;
        int Arow = A.length;
        // int Bcol = B[0].length;
        int Brow = B.length;
	int Bcol = B[0].length;
        if (Acol == Bcol) {
	} else {
	    log.error("Matrix multiplication: Wrong dimension.  A is [" +Arow+"x"+Acol+"] and B is [" +Bcol+"x"+Brow+"]");
	}
	double[][] C = new double[Arow][Brow];
	for (int i = 0; i < Arow; i++) {
	    double[] Ai = A[i];
	    double[] Ci = C[i];
	    for (int j = 0; j < Brow; j++) {
		// double[] Bj = B[j];
		Ci[j] = dot(Ai, B[j], Acol);
	    }
	}
	return C;
    }

    static private void selfMultiply(double[][] A, double[][] C, int numRow, int numCol) {
	for (int i = 0; i < numRow; i++) {
	    double[] Ai = A[i];
	    double[] Ci = C[i];
	    for (int j = i + 1; j < numRow; j++) {
		double[] Aj = A[j];
		Ci[j] = dot(Ai, Aj, numCol);
		C[j][i] = Ci[j];
	    }
	    Ci[i] = dot(Ai, Ai, numCol);
	}
    }

    static private double dot(final double[] a, final double[] b) {
	double t = 0;
	int max = a.length;
	for (int i = 0; i < max; i++) {
	    t += a[i] * b[i];
	}
	return t;
    }

    static private double dot(final double[] a, final double[] b, final int max) {
	double t = 0;
	for (int i = 0; i < max; i++) {
	    t += a[i] * b[i];
	}
	return t;
    }

    static private double[][] transpose(final double[][] A) {
	int Arow = A.length;
	int Acol = A[0].length;
	double[][] X = new double[Acol][Arow];

	for (int i = 0; i < Arow; i++) {
	    double[] Ai = A[i];
	    for (int j = 0; j < Acol; j++) {
		X[j][i] = Ai[j];
	    }
	}
	return X;
    }

    static private double[] multiplyVector(final double[][] A, final double[] b) {
	try {
	    checkDimension(A, b);
	} catch (Exception e) {
	    log.error("Matrix multiplication:" + e);
	}
	int numRow = A.length;
	double[] c = new double[numRow];
	for (int i = 0; i < numRow; i++) {
	    c[i] = dot(A[i], b);
	}
	return c;
    }

    static private void checkDimension(final double[][] A, final double[] b) throws Exception {
	if (A[0].length != b.length) {
	    throw new RuntimeException("Matrix Multiplication: Dimensions not correct");
	}
    }

    static private void checkDimension(final double[] a, final double[][] B) throws Exception {
	if (a.length != B.length) {
	    throw new RuntimeException("Matrix Multiplication: Dimensions not correct");
	}
    }

    static private void checkDimension(final double[][] A, final double[][] B) throws Exception {
	if (A[0].length != B.length) {
	    throw new RuntimeException("Matrix Multiplication: Dimensions not correct");
	}
    }

}
