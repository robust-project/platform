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
//      Created Date :          2013-01-23
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.glm.impl;

/**
 * 
 * 
 * @author Edwin
 */
public class Loglikelihood {

    /**
	 * To return the log-likelihood for poisson GLM
	 * 
	 * @param y observed y
	 * @param yhat predicted y
	 * @return the log-likelihood for the poisson model
	 */
    static public double poisson(double[] y, double[] yhat) {
	int numRow = y.length;
	double s = 0;
	for (int i = 0; i < numRow; i++) {
	    s += -yhat[i] + y[i] * Math.log(yhat[i]) - gammaln(y[i]+1);
	}
	return s;
    }
    
    /**
     * To return the log-likelihood for normal GLM 
     * 
     * @param y observed y
     * @param yhat predicted y
     * @param dispersion the estimated variance
     * @return the log-likelihood for the normal model
     */
    static public double normal(double[] y, double[] yhat, double dispersion) {
	int numRow = y.length;
	double s = 0;
	for (int i = 0; i < numRow; i++) {
	    double e = y[i] - yhat[i];
	    s += -0.5 * (Math.log(2*Math.PI*dispersion) + e*e / dispersion);
	}
	return s;
    }

    final static private double gammaln(final double y) {
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
