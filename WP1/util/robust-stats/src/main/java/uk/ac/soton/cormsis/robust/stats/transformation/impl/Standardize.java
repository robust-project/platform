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

import uk.ac.soton.cormsis.robust.stats.transformation.spec.IStandardize;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;

public class Standardize implements IStandardize {

    private double[][] Z;
    private int numRow;
    private int numCol;

    private double[] mu;
    private double[] varX;

    public Standardize(double[][] A) {
	initialize(A);
    }

    public Standardize(double[][] A, double[] w) {
	if (A.length != w.length) {
            throw new IllegalArgumentException("Vector: Wrong Dimension");
        } else {
	   initialize(weightedX(A,w));
	}
    }

    @Override
    public double[][] getZ() {
        return this.Z;
    }
    
    @Override
    public double[] getMean() {
	return this.mu;
    }

    @Override
    public double[] getVar() {
	   return this.varX;
    }

    @Override
    public double[] getStd() {
	double[] std = new double[numCol];
	for (int i = 0; i < numCol; i++) {
	   std[i] = Math.sqrt(varX[i]);
	}
	return std;
    }

    private double[][] weightedX(double[][] A, double[] w) {
	double[][] X = GeneralMatrixOperation.copyMatrix(A);
	numRow = X.length;
	numCol = X[0].length;
        for (int i = 0; i < numRow; i++) {
	    double[] Xi = X[i];
            for (int j = 0; j < numCol; j++) {
		Xi[j] *= w[i];
            }
        }
	return X;
    }

    private void initialize(double[][] A) {
	this.numRow = A.length;
	this.numCol = A[0].length;
	double[][] xt = GeneralMatrixOperation.transposeMatrix(A);

	mu = new double[numCol];
	varX = new double[numCol];

	// Now we want to find the mean and variance 
	for (int j = 0; j < numCol; j++) {
	   double[] XTj = xt[j];
	   mu[j] = VectorCalculation.mean(XTj);
	   XTj = VectorCalculation.subtract(XTj,mu[j]);
	   varX[j] = VectorCalculation.variance(XTj,mu[j]);
	   // varX[j] = VectorCalculation.mean(VectorCalculation.multiply(XTj,XTj));
	   XTj = VectorCalculation.divide(XTj,Math.sqrt(varX[j]));
	}
	this.Z = GeneralMatrixOperation.transposeMatrix(xt);
    }
    
}
