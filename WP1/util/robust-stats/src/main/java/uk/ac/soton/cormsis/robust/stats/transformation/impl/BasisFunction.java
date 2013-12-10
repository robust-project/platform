////////////////////////////////////////////////////////////////////////
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
//      Created Date :          2012-11-24
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.transformation.impl;

import uk.ac.soton.cormsis.robust.stats.transformation.spec.IBasisFunction;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;

/**
 *
 * @author Edwin
 */
public class BasisFunction implements IBasisFunction {
    
    private boolean isInitialized = false;
    private double[][] X;
    private int numRow;
    private int numCol;

    public BasisFunction(double[][] A) {
	initialize(A);
    }

    @Override
	public double[][] getQuadratic() {
	if (isInitialized) {
	    return quadratic();
	} else {
	    throw new IllegalArgumentException("Has not been initialized");
	}
    }

    @Override
	public double[][] getQuadratic(double[][] A) {
	initialize(A);
	return quadratic();
    }

    @Override
	public double[][] getPurePolynomial(int power) {
	if (isInitialized) {
	    return polynomial(power);
	} else {
	    throw new IllegalArgumentException("Has not been initialized");
	}
    }

    @Override
	public double[][] getPurePolynomial(double[][] A,int power) {
	initialize(A);
	return polynomial(power);
    }

    @Override
    public double[][] getInteraction() {
	if (isInitialized) {
	    return interaction();
	} else {
	    throw new IllegalArgumentException("Has not been initialized");
	}
    }	

    @Override
    public double[][] getInteraction(double[][] A) {
	initialize(A);
	return interaction();
    }

    /* Private method starts here
     *
     */

    private void initialize(double[][] A) {
	this.X = GeneralMatrixOperation.copyMatrix(A);
	this.numRow = A.length;
	this.numCol = A[0].length;
	this.isInitialized = true;
    }

    private double[][] quadratic() {
	int interactingColumn = (numCol * (numCol-1) / 2);
	double[][] Y = new double[numRow][interactingColumn+numCol+numCol];
	for (int i = 0; i < numRow; i++) {
	    double[] Xi = X[i];
	    double[] Yi = Y[i];
	    int counter = 0;
	    for (int j = 0; j < numCol; j++) {
		Yi[j] = Xi[j];
		counter++;
	    }
	    for (int k1 = 0; k1 < numCol; k1++) {
		for (int k2 = k1+1; k2 < numCol; k2++) {
		    Yi[counter++] = Xi[k1]*Xi[k2];
		}
	    }
	    for (int j = 0; j < numCol; j++) {
		Yi[counter++] = Xi[j]*Xi[j];
	    }
	}
	return Y;
    }
    
    private double[][] polynomial(int power) {
	if (power < 1) {
	    throw new IllegalArgumentException("BasisFunction: Input power term less than 1 ");
	}
	if (power == 1) {
	    return X;
	} else {
	    double[][] Y = new double[numRow][numCol * power];
	    for (int i = 0; i < numRow; i++) {
		double[] Xi = X[i];
		double[] Yi = Y[i];
		for (int d = 0; d < power; d++) {
		    int indexAddition = d*numCol;
		    for (int j = 0; j < numCol; j++) {
			Yi[j+indexAddition] = Math.pow(Xi[j],d+1);
		    }
		}
	    }
	return Y;
	}
    }
    
    private double[][] interaction() {
	int interactingColumn = (numCol * (numCol-1) / 2);
	double[][] Y = new double[numRow][interactingColumn+numCol];
	for (int i = 0; i < numRow; i++) {
	    double[] Xi = X[i];
	    double[] Yi = Y[i];
	    int counter = 0;
	    for (int j = 0; j < numCol; j++) {
		Yi[j] = Xi[j];
		counter++;
	    }
	    for (int k1 = 0; k1 < numCol; k1++) {
		for (int k2 = k1+1; k2 < numCol; k2++) {
		    Yi[counter++] = Xi[k1]*Xi[k2];
		}
	    }
	}
	return Y;
    }

}
