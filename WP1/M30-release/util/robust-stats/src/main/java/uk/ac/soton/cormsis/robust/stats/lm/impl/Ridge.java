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
//      Created Date :          2012-07-30
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.lm.impl;

import uk.ac.soton.cormsis.robust.stats.lm.impl.LM;
import uk.ac.soton.cormsis.robust.stats.lm.spec.IRidge;
import uk.ac.soton.cormsis.robust.stats.utils.CholeskyDecomposition;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;

// TODO: implement offset
// TODO: Check if \mu is without bound (i.e. less than 0 for gamma/poisson)
// TODO: Prior weights
public class Ridge extends LM implements IRidge {

    // The default value 
    private double lambda = 1;
    
    // Residual is always defined as (y_{i}-\hat{y}_{i}) the raw residual

    public Ridge(double[][] A, double[] b) {
        super(A,b);
    }
    
    public Ridge(double[][] A, double[] b, double lambda) {
        super(A,b);
        this.lambda = lambda;
    }
    
    @Override 
	public void fit(double lambda) {
	this.lambda = lambda;
	fit();
    }

    @Override
	public double[] predict(double[][] X_plus) {
        int m = X_plus[0].length;
	if (X_plus[0].length!=m) {
	    throw new IllegalArgumentException("Number of covariate not equal");
	}

        double[][] X1_plus = GeneralMatrixOperation.augmentedMatrix(X_plus);
        double[] beta = getBeta();
        
        return MatrixMultiplication.multiply(X1_plus, beta);
    }

    @Override
	public void setLambda(double lambda) {
	this.lambda = lambda;
    }

@Override
    public double getLambda() {
    return this.lambda;
}

    @Override
    public double[] fit(double[][] X, double[] Y, double lambda) {
	this.lambda = lambda;
	fit(X,Y);
	return getBeta();
    }

    /*
     * Private Method starts here
     */
    @Override
    protected void solve(double[][] A, double[] b) {
	double[][] Gram = MatrixMultiplication.multiply(A,A,true,false);
	int numRow = Gram.length;
	// We do not penalise the intercept term here 
	// If not, the answer will look weird.  i.e. \bar{y} \ne \beta_{0}
	for (int i = 1; i < numRow; i++) {
	    Gram[i][i] += this.lambda;
	}
	CholeskyDecomposition chol = new CholeskyDecomposition(Gram);
	double[] betaHat = chol.solve(MatrixMultiplication.multiply(GeneralMatrixOperation.transposeMatrix(A),b));
	setBeta(betaHat);
    }
    

}
