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
// Highfield Campus,24053694
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
//      Created Date :          2012-11-17
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.glm.impl;

import uk.ac.soton.cormsis.robust.stats.glm.spec.ILinkFunction;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;

/**
 *
 * @author Edwin Tye
 */
class PoissonLink extends AbstractLink implements ILinkFunction {

    private static final double EPSILON = 1E-12;

    /**
     * Compute the g(\mu) given name link function
     *
     * @param mu data
     * @return \eta
     */
    @Override
    public double link(double mu) {
        return Math.log(mu);
    }

    /**
     * Compute an array of g^{-1}(\eta) given name of link function
     *
     * @param eta X\beta
     * @return \mu
     */
    @Override
    public double inverseLink(double eta) {
        double mu = 0;
        // \mu = exp(\eta) 
        double lowerBound = Math.log(EPSILON);
        double upperBound = -lowerBound;
        mu = Math.exp(Math.min(Math.max(eta, lowerBound), upperBound));
        return mu;
    }

    /**
     * Compute \nabla_{\theta} g(\mu)
     *
     * @param mu data
     * @return output of the first derivative of link function
     */
    @Override
    public double delLink(double mu) {
        // \mu = \partial \eta \over \partial \mu
        // \mu = \partial log \mu \over \partial \mu
        // \mu = 1 \over \mu
        return 1 / mu;
    }

    @Override
    public double varY(double mu) {
        return mu;
    }

    @Override
    public double deviance(double y, double yhat) {
        double d = 0;
        if (y == 0) {
            // Because if y = 0, the first part is 0
            d -= (y - yhat);
        } else {
            d += y * Math.log(y / yhat) - (y - yhat);
        }
        return 2 * d;
    }

    // @Override
    // 	public double loglike(double[] y, double[] yhat) {
    // 	int numRow = y.length;
    // 	double s = 0;
    // 	for (int i = 0; i < numRow; i++) {
    // 	    s += -yhat[i] + y[i] * Math.log(yhat[i]) - gammaln(y[i]+1);// logFactorial(y[i]);
    // 	}
    // 	return s;
    // }

    @Override
    public double[] initializeMu(double[] Y) {
        int numRow = Y.length;
        double ybar = VectorCalculation.mean(Y);
        double[] arrOut = new double[numRow];
        double s = 0.25;
        for (int i = 0; i < numRow; i++) {
            if (Y[i] < 0) {
                throw new IllegalArgumentException("Bad Data: Poisson Link");
            }
            arrOut[i] = (Y[i] + ybar)/2;
	    // arrOut[i] = Y[i] + s;
        }
        return arrOut;
    }

    final private double gammaln(final double y) {
	final double[] coeff = {76.18009172947146, -86.50532032941677, 24.01409824083091,
			  -1.231739572450155, 0.120865973866179E-2, -0.5395239384953E-5};
	double ser = 1.000000000190015;
	double x = y;
	double tmp = y + 5.5;
	tmp -= (y+0.5) * Math.log(tmp);
	for (int i = 0; i < 6; i++) {
	    ser += coeff[i] / ++x;
	}
	return -tmp + Math.log(2.5066282746310005*ser/y);
    }

    private double logFactorial(double y) {
	// This should be a direct conversion because for Poisson data, because
	// the data should naturally be an integer but was converted to double
	// for calculation purpose
	int max = (int)y + 1;
	double s = 0;
	for (int i = 1; i < max; i++) {
	    s += Math.log(i);
	}
	return s;
    }

}
