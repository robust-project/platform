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
package uk.ac.soton.cormsis.robust.stats.glm.impl;

import uk.ac.soton.cormsis.robust.stats.glm.spec.ILinkFunction;
import uk.ac.soton.cormsis.robust.stats.glm.spec.IGLMnb;
import uk.ac.soton.cormsis.robust.stats.lm.impl.LM;
import uk.ac.soton.cormsis.robust.stats.utils.ArrayOperation;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.MatrixMultiplication;
import uk.ac.soton.cormsis.robust.stats.utils.Projection;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;
import uk.ac.soton.cormsis.robust.stats.utils.display;

// TODO: implement offset
// TODO: Check if \mu is within bound (i.e. less than 0 for gamma/poisson)
// TODO: Prior weights
public class GLMnb extends LM implements IGLMnb {

    private final double EPSILON = 1E-12;
    private final double TOLERENCE = 1E-8;
    
    private ILinkFunction LF = new PoissonLink();

    // Lets be ranther generous here, it is not like this takes forever to run
    private int iterationLimit = 100000;
    private int totalIteration = 0;
    // Define the standard link function as normal

    // Residual is always defined as (y_{i}-\hat{y}_{i}) the raw residual
    private boolean hasResidual = false;
    private double[] residual;
    
    // private double[] workingWeight;
    // private double[] Z;
    private double alpha = 0;
    private double dispersion = 0;

    public GLMnb(double[][] A, double[] b) {
	super(A,b);
     	// initialize(A,b);
    }

    public GLMnb getObject() {
	return this;
    }
    
    @Override 
	public double[] fit() {
        irls();
        setResidual();
        return getBeta();
    }

    @Override
	public double[] fit(double[][] A, double[] b) {
        initialize(A, b);
	fit();
        return getBeta();
    }

    @Override 
	public double predict(double[] x_plus) {
	double[][] X_plus = new double[1][x_plus.length];
	double[] mu_plus = predict(X_plus);
	return mu_plus[0];
    }

    @Override
	public double[] predict(double[][] X_plus) {
        double[] Yhat_plus;
        double[][] X1_plus = GeneralMatrixOperation.augmentedMatrix(X_plus);
        int m = X1_plus[0].length;
        double[] beta = getBeta();
        
        if (m == beta.length) {
	    Yhat_plus = LF.inverseLink(MatrixMultiplication.multiply(X1_plus,getBeta()));
        } else {
            throw new IllegalArgumentException("Number of covariates are not equal, entered is "
					       + m + " but number of beta = " + beta.length);
        }
        return Yhat_plus;
    }
            
    // Need to Override because of the change in distribution name
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
	for (int i = 0; i < getNumObv(); i++) {
	    double d = residual[i];
	    s += d*d;
	}
	return s / (double)getNumObv();
    }

    @Override
	public double[] getModifiedResidual() {
        if (this.hasResidual) {
        } else {
	    setResidual();
        }
	double[] modifiedResidual = new double[getNumObv()];
	double[] h = getLeverages();
	for (int i = 0; i < getNumObv(); i++) {
	    modifiedResidual[i] = this.residual[i] / (1 - h[i]);
	}
	return modifiedResidual;
    }

    @Override
	public double[] getPearsonResidual() {
        if (this.hasResidual) {
        } else {
	    setResidual();
        }
	double[] pearsonResidual = new double[getNumObv()];
	double[] denominator = NBvarY(VectorCalculation.subtract(getY(),getResidual()),this.alpha);

	for (int i = 0; i < getNumObv(); i++) {
	    pearsonResidual[i] = this.residual[i] / Math.sqrt(denominator[i]);
	}
	return pearsonResidual;
    }

    // Sacrifie speed for simplier and easier to understand code
    @Override
	public double[] getModifiedPearsonResidual() {
	double[] p = getPearsonResidual();
	double[] h = getLeverages();
	for (int i = 0; i < h.length; i++) {
	    p[i] = p[i] / Math.sqrt(1-h[i]);
	}
	return p;
    }

    @Override
	public double[] getModifiedLPResidual() {
        if (this.hasResidual) {
        } else {
	    setResidual();
        }
	double[] yhat = VectorCalculation.subtract(
						   getY(),
						   getResidual());
	double[] h = getLeverages();
	double[] nominator = VectorCalculation.subtract(
							LF.link(getY()),
							LF.link(yhat));
	double[] denominator = VectorCalculation.multiply(
							  NBvarY(yhat,alpha),
							  dispersion);
	for (int i = 0; i < getNumObv(); i++) {
	    denominator[i] = Math.sqrt(denominator[i] * ( 1 - h[i] ));
	}
	denominator = VectorCalculation.multiply(
						 denominator,
						 LF.delLink(yhat));
	return VectorCalculation.divide(nominator,denominator);
    }

    @Override
	public double[] getDevianceResidual() {
	double[] error = new double[getNumObv()];
	double[] y = getY();
	double[] yhat = VectorCalculation.subtract(getY(),getResidual());
	
	for (int i = 0; i < getNumObv(); i++) {
	    error[i] = Math.signum(y[i]-yhat[i]) * Math.sqrt(LF.deviance(y[i],yhat[i]));
	}
	
	return error;
    }

    @Override
	public double[] getModifiedDevianceResidual() {
	double[] error = getDevianceResidual();
	double[] h = getLeverages();	
	for (int i = 0; i < getNumObv(); i++) {
	    error[i] /= Math.sqrt((1-h[i]));
	}
	return error;
    }


    @Override
	public int getTotalIteration() {
	return this.totalIteration;
    }

    @Override
	public double getDispersion() {
	return this.dispersion;
    }

    @Override 
    public double getAlpha() {
	return this.alpha;
    }

    @Override
	public double getLogLike() {
        double[] yhat = VectorCalculation.subtract(getY(),getResidual());
	return Loglikelihood.poisson(getY(),yhat);
    }

    @Override
	public double getDeviance() {
        double[] yhat = VectorCalculation.subtract(getY(),getResidual());
        return LF.deviance(getY(),yhat);
    }

    @Override
	public double getAIC() {
	return -2* getLogLike() + 2 * (getNumCovariate()+1);
    }

    @Override
	public double[] getVarY() {
	return NBvarY(VectorCalculation.subtract(getY(),getResidual()),getAlpha());
    }

    @Override 
	public double getVarY(double s) {
	return NBvarY(s,this.alpha);
    }

    @Override 
	public double[] getVarY(double[] s) {
	return NBvarY(s,this.alpha);
    }
    
    public double[] getW(double[] betahat) {
        double[] w = new double[getNumObv()];
        double[] eta = computeEta(getX(), betahat);
        double[] mu = LF.inverseLink(eta);
        for (int i = 0; i < getNumObv(); i++) {
            double mu_i = mu[i];
            double s = 1 / (Math.sqrt(NBvarY(mu_i,this.alpha)) * LF.delLink(mu_i));
            w[i] = s*s;
        }
        return w;
    }

    @Override
    public double[] getW() {
        // double[] y = getY();
        double[] w = new double[getNumObv()];
        double[] eta = computeEta(getX(), getBeta());
        double[] mu = LF.inverseLink(eta);
        for (int i = 0; i < getNumObv(); i++) {
	    // System.out.println("Value of mu "+i+ " = " +mu[i]);
            double mu_i = mu[i];
            double s = 1 / (Math.sqrt(NBvarY(mu_i,this.alpha)) * LF.delLink(mu_i));
            w[i] = s*s;
        }
	setWeight(w);
        return w;
    }

    public double[] getZ() {
	return getZ(getBeta());
    }

    public double[] getZ(double[] betahat) {
	double[] eta = computeEta(getX(), betahat);
        double[] mu = LF.inverseLink(eta);
        double[] z = VectorCalculation.multiply(VectorCalculation.subtract(getY(), mu), LF.delLink(mu));
        return VectorCalculation.add(eta, z);
    }

    @Override
    public double[] getLeverages() {
        return Projection.Leverage(weightedX(getX(), getW()));
    }

    /*
     * Protected Method starts here
     */
    protected double[] irls() {
        double[] Y_obv = getY();
        // double[] beta_old;
        double[] mu = LF.initializeMu(Y_obv);
        double[] eta = LF.link(mu);
        double[] beta = new double[getNumCovariate() + 1];
        double[] W = new double[getNumObv()];
        double[] Z = new double[getNumObv()];
	double oldDispersion = 0;
	
	dispersion = getDispersion(Y_obv,mu,alpha);
	alpha = 1 / dispersion;
        totalIteration = 0;
        // There is a problem with convergence if it reaches the iteration limit. Terminate!
        while (this.iterationLimit > this.totalIteration) {
	    oldDispersion = dispersion;
            for (int i = 0; i < getNumObv(); i++) {
                // Setting the weight, a vector because it is assumed that the elements are 
                // uncorrelated so W \equiv WI
                double mu_i = mu[i];
                // We always want the square root of Var(\mu_{i}) because of the way WLS
                // is being fitted.  as in \tilda{X} = W^{1/2}X
                W[i] = 1 / (Math.sqrt(NBvarY(mu_i,this.alpha)) * LF.delLink(mu_i));

                // Setting the working Z
                // Defined as z_{i}^{t} = \eta_{i}^{t} + (y_{i} - \mu_{i}^{t}) * g^{\prime}(\mu_{i}^{t})
                double z_i = eta[i] + (Y_obv[i] - mu_i) * LF.delLink(mu_i);// - offset[i];
                Z[i] = z_i;
            }
            // Finding \beta^{t+1} = (X^{T}W^{t}X)^{-1}X^{T}W^{t}\tilda{Z}^{t}
            beta = irlsUpdate(getX(), Z, W);

            // compute the updated values of \eta^{t} = X\beta^{t}
            eta = computeEta(getX(), beta);
            mu = LF.inverseLink(eta);
	    dispersion = getDispersion(Y_obv,mu,this.alpha);
	    alpha = dispersion * alpha;
	    totalIteration++;
	    
            /*
             * Stopping criteria is different from normal glm, because the aim is to
	     * make sure that the estimated disperison is correct to the theoreticl
	     * value of 1
             */
            if (Math.abs(dispersion-oldDispersion) < EPSILON) {
                break;
            }
        } // ~ while loop
        return beta;
    }

    protected void setResidual() {
	double[] yhat = predict(getX());
	this.residual = VectorCalculation.subtract(getY(),yhat);

	dispersion = 0;
	for (int i = 0; i < getNumObv(); i++) {
	    dispersion += (residual[i] * residual[i]) / NBvarY(yhat[i],this.alpha);
	}
	// numCovariate + 1 because numCovariate doesn't include intercept
	dispersion/= (getNumObv() - getNumCovariate() - 1);
	this.hasResidual = true;
    }


    /*
     * Private Method starts here
     */

    // Not sure if this needs to be separated
    private double[] irlsUpdate(double[][] X, double[] Y, double[] w) {
        // The update equation
        // \beta^{t+1} = (X^{T}WX)^{-1} X^{T}W(z + \eta + offset)
        // using the fit function from lm (which is a WLS)
	double[][] Xtilda = weightedX(X,w);
	double[] Ytilda = weightedY(Y,w);
	solve(Xtilda,Ytilda);
	return getBeta();
    }

    // compute the updated values of \eta^{t} = X\beta^{t}
    private double[] computeEta(double[][] A, double[] b) {   
	return MatrixMultiplication.multiply(GeneralMatrixOperation.augmentedMatrix(A),b);
    }

    private double[] NBvarY(double[] mu, double alpha) {
	double[] out = new double[getNumObv()];
	for (int i = 0; i < getNumObv(); i++) {
	    out[i] = NBvarY(mu[i],alpha);
	}
	return out;
    }

    private double NBvarY(double mu, double alpha) {
	return mu + mu * mu * alpha;
    }

    private double getDispersion(double[] y, double[] yhat, double alpha) {
	double s = 0;
	for (int i = 0; i < getNumObv(); i++) {
	    double d = yhat[i] - y[i];
	    s += (d*d) / NBvarY(yhat[i],getAlpha());
	}
	// numCovariate + 1 because numCovariate doesn't include intercept
	s /= (getNumObv() - getNumCovariate() - 1);
	return s;
	
    }


    
}
