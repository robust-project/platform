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
//      Created Date :          2012-07-29
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

/*
 * TODO: Currently doesn't allow for multivariate Y  
 * Probably need to create something else <Extend> for that
 * TODO: Also assume that it doesn't have constant (column of 1), should have a choice
 */
package uk.ac.soton.cormsis.robust.stats.lm.impl;

import uk.ac.soton.cormsis.robust.stats.lm.spec.ILM;
import uk.ac.soton.cormsis.robust.stats.utils.ArrayOperation;
import uk.ac.soton.cormsis.robust.stats.utils.CholeskyDecomposition;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.LS;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;
import uk.ac.soton.cormsis.robust.stats.utils.Projection;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;

/*
 * A matrix is identified by using mat at the beginning of the variable when require
 * 
 * Personally, I think that everything should be defined as a matrix.  Because it gets
 * rather confusing when things are set between double[] and double[][];
 */
public class LM implements ILM {

    private int numRow;
    private int numCol;
    /*
     * The attempt to simplify the code probably caused a bit of confusion.  
     * Aim is to provide the most consistent variable names at all time, and 
     * one of the method here is to use the fact that a linear system can be
     * denoted by both Ax=b and y=x\beta
     */
    private double[] W;  // This in fact should always be a vector (apart from GLS case)
    private double[][] X;
    private double[][] X1; // X matrix with constant 1 at the left hand side
    private double[] h;  // The diagonal elements of the hat matrix X(X^{T}X)^{-1}X^{T}
    private double[] Y;
    private double[] beta;
    // Residual is always defined as (y_{i}-\hat{y}_{i}) and not adjusted
    private double[] residual;
    private boolean hasInitialized = false;
    private boolean hasWeight = false;
    private boolean hasBeta = false;
    private boolean hasResidual = false;
    private boolean hasHatMatrix = false;

    public LM (double[][] A, double[] b) {
        setVariables(A, b);
    }

    public LM (double[][] A, double[] b, double[] w) {
        setVariables(A, b, w);
    }

    @Override
	public LM getObject() {
	return this;
    }

    @Override
    public double[] fit() {
        if (this.hasInitialized) {
            if (this.hasWeight) {
                wls(this.X, this.Y, this.W);
                setResidual();
            } else {
                ols(this.X, this.Y);
                setResidual();
            }
        } else {
            throw new IllegalArgumentException("X and Y Matrix do not exist");
        }
        return getBeta();
    }

    @Override
    public double[] fit(double[][] A, double[] b) {
        ols(A, b);
        return getBeta();
        // Does not compute residual to cut down computation time
    }

    @Override
    public double[] fit(double[][] A, double[] b, double[] w) {
        wls(A, b, w);
        return getBeta();
        // Does not compute residual to cut down computation time
    }

    @Override
	public double predict(double[] x_plus) {
	    double[][] X_plus = new double[1][x_plus.length];
    double[] mu_plus = predict(X_plus);
    return mu_plus[0];
    }

    // Lets assume that the new data is in a matrix X_{+}
    // and the predicted values is \hat{Y}_{+}
    // Automatically add a column of 1 to the end (right hand side)
    @Override
    public double[] predict(double[][] X_plus) {
        double[] Yhat_plus;
        double[][] X1_plus = GeneralMatrixOperation.augmentedMatrix(X_plus);
        double[] betaHat = getBeta();

        if (X1_plus[0].length == betaHat.length) {
            Yhat_plus = MatrixMultiplication.multiply(X1_plus, betaHat);
            return Yhat_plus;
        } else {
            throw new IllegalArgumentException("Number of covariates are not equal, entered"
                    + X1_plus[0].length + " but number of beta = " + betaHat.length);
        }
    }

    @Override
    public double[] getWeight() {
        if (this.hasWeight) {
            return this.W;
        } else {
            throw new IllegalArgumentException("Do not have weights");
        }
    }

    // Setting a 
    @Override
    public void setWeight(double[] w) {
        if (w.length != this.numRow) {
            throw new IllegalArgumentException("Number of row is not consistent");
        } else {
            this.W = w;
        }
    }

    @Override
    public double[][] getX() {
        return GeneralMatrixOperation.copyMatrix(this.X);
    }

    @Override
    public double[] getY() {
        return ArrayOperation.copyArray(this.Y);
    }

    @Override
    public double[] getBeta() {
        if (this.hasBeta) {
            return this.beta;
        } else {
            if (this.hasInitialized) {
                fit();
                return ArrayOperation.copyArray(this.beta);
                //throw new IllegalArgumentException("Coefficients not yet computed");
            } else {
                throw new IllegalArgumentException("X and Y Matrix do not exist");
            }
        }
    }

    @Override
    public void setBeta(double[] b) {
        this.beta = b;
    	this.hasBeta = true;
    }

    @Override
    public double[] getResidual() {
        if (this.hasResidual) {
            return this.residual;
        } else {
            setResidual();
            return this.residual;
        }
    }


    @Override
    public double getMSE() {
        if (this.hasResidual) {
        } else {
            setResidual();
        }
	double s = 0;
	for (int i = 0; i < numRow; i++) {
	    double d = residual[i];
	    s += d*d;
	}
	return s / (double)numRow;
    }

    @Override
    public double[] getModifiedResidual() {
        if (this.hasResidual) {
        } else {
            setResidual();
        }
	double[] modifiedResidual = new double[numRow];
	getLeverages();
	for (int i = 0; i < numRow; i++) {
	    modifiedResidual[i] = this.residual[i] / Math.sqrt(1 - h[i]);
	}
	return modifiedResidual;
    }

    @Override
    public int getNumObv() {
        return this.numRow;
    }

    @Override
    public int getNumCovariate() {
        return this.numCol;
    }

    @Override
	public double[] getLeverages() {
	// if (this.hasHatMatrix) {
	// } else {
	    if (hasWeight) {
		// hatMatrix(weightedX(this.X, this.W));
		return Projection.Leverage(weightedX(this.X,this.W));
	    } else {
		hatMatrix(this.X1);
		return Projection.Leverage(this.X1);
	    }
	    // }
	//return this.h;
    }

    /*
     * Private Method starts here
     */

    private void setVariables(double[][] A, double[] b) {
        this.numCol = A[0].length;
        this.numRow = A.length;
        this.X = new double[this.numRow][this.numCol];
        this.Y = new double[this.numRow];

        // Not checking whether it is an overdetermined system
        // Or the rank
        if (numRow == b.length) {
	    this.X = GeneralMatrixOperation.copyMatrix(A);
	    this.Y = ArrayOperation.copyArray(b);
            this.residual = new double[this.numRow];
            // Augumenting the design matrix with a constant at the right hand side
            this.X1 = GeneralMatrixOperation.augmentedMatrix(this.X);
            //this.X1 = augmentedMatrix(this.X);
            // To alert User that the class has been properly initialized
            this.hasInitialized = true;
        } else {
            throw new IllegalArgumentException("LM: Number of observation not equal");
        }
    }

    private void setVariables(double[][] A, double[] b, double[] w) {
        setVariables(A, b);
        this.W = new double[this.numRow];
        System.arraycopy(w, 0, this.W, 0, this.numRow);
        this.hasWeight = true;
    }

    private void ols(double[][] A, double[] b) {
        setVariables(A, b);
        // Solving the linear system
	solve(this.X1,this.Y);
	// hatMatrix(this.X1);
        // double[] betaHat = LS.Solve(this.X1, this.Y);
        // setBeta(betaHat);
        // this.hasBeta = true;
    }

    private void wls(double[][] A, double[] b, double[] w) {
        setVariables(A, b, w);
        /* 
         * Using the definition W = diag(W)I assuming zero correlation
         * Define w = diag(W)^{1/2} and wX = \tilda{X}, wY = \tilda{Y} 
         * X^{T}WX\beta = X^{T}Wy
         * \RightArrow \tilda{X}^{T}\tilda{X}\beta = \tilda{X}^{T}\tilda{Y}
         */
	double[][] Xtilda = weightedX(A,w);
	double[] Ytilda = weightedY(b,w);

        // Finished transforming the two matrix.  solve the system
	solve(Xtilda,Ytilda);
	// hatMatrix(Xtilda);
    }

    private void setResidual() {
        double[] betaHat = getBeta();
        double[] Yhat = MatrixMultiplication.multiply(this.X1, betaHat);
	this.residual = VectorCalculation.subtract(this.Y,Yhat);
        // for (int i = 0; i < this.Y.length; i++) {
        //     this.residual[i] = (this.Y[i] - Yhat[i]);
        // }
        this.hasResidual = true;
    }

    /*
     * Protected methods that will be used in GLM as well
     * Change to private?
     */
    
        
    // protected void setBeta(double[] b) {
    //     this.beta = b;
    // 	this.hasBeta = true;
    // }

    protected void initialize(double[][] A, double[] b, double[] w) {
        setVariables(A, b, w);
    }

    protected void initialize(double[][] A, double[] b) {
	setVariables(A,b);
    }

    protected void solve(double[][] A, double[] b) {
	CholeskyDecomposition chol = new CholeskyDecomposition(A,false);
	double[] betaHat = chol.solveXT(b);
	// double[] betaHat = LS.Solve(A,b);
	setBeta(betaHat);
    }
    
    // This method is now useless because it is being moved to the projection instead
    protected void hatMatrix(double[][] A) {
	CholeskyDecomposition chol = new CholeskyDecomposition(A,false);
	double[][] H = MatrixMultiplication.multiply(A,MatrixMultiplication.multiply(chol.getAinverse(),A,false,true));
	this.h = new double[numRow];
	// System.out.println("The hat matrix");
	// display.print(H);
	for (int i = 0; i < numRow; i ++) {
	    this.h[i] = H[i][i];
	}
	this.hasHatMatrix = true;
    }

    protected double[][] weightedX(double[][] X,double[] w) {
        double[][] Xtilda = GeneralMatrixOperation.augmentedMatrix(X);
	int numRow = X.length;
	int numCol = X[0].length;
        for (int i = 0; i < numRow; i++) {
	    double[] Xtildai = Xtilda[i];
            for (int j = 0; j < numCol+1; j++) {
		//Xtildai[j] *= Xtildai[j] * w[i];
		Xtildai[j] *= w[i];
            }
        }
	return Xtilda;
    }

    protected double[] weightedY(double[] Y, double[] w) {
	return VectorCalculation.multiply(Y,w);
    }

    // End of Methods
}
