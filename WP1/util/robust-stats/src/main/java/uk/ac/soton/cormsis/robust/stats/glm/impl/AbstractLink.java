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
//      Created Date :          2012-11-17
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.glm.impl;

/**
 *
 * @author Edwin Tye
 */
// This one contains the link function
abstract class AbstractLink {

    /**
     * Compute an array of g(\mu) given name link function
     *
     * @param mu data
     * @param distName link function
     * @return \eta
     */
    public double[] link(double[] mu) {
        int numRow = mu.length;
        double[] eta = new double[numRow];

        for (int i = 0; i < numRow; i++) {
            eta[i] = link(mu[i]);
        }
        return eta;
    }

    /**
     * Compute an array of g^{-1}(\eta) given name of link function
     *
     * @param eta X\beta
     * @param distName link function
     * @return \mu
     */
    public double[] inverseLink(double[] eta) {
        int numRow = eta.length;
        double[] mu = new double[numRow];

        for (int i = 0; i < numRow; i++) {
            mu[i] = inverseLink(eta[i]);
        }
        return mu;
    }

    /**
     * Compute an array of \nabla_{\theta} g(\mu)
     *
     * @param mu data
     * @return output of the first derivative of link function
     */
    public double[] delLink(double[] mu) {
        int numRow = mu.length;
        double[] arrOut = new double[numRow];

        for (int i = 0; i < numRow; i++) {
            arrOut[i] = delLink(mu[i]);
        }
        return arrOut;
    }

    public double[] varY(double mu[]) {
        int max = mu.length;
        double[] arrOut = new double[max];
        for (int i = 0; i < max; i++) {
            arrOut[i] = varY(mu[i]);
        }
        return arrOut;
    }
    
    public double deviance(double[] y, double[] yhat){
        int max = y.length;
        if (y.length != yhat.length) {
            throw new RuntimeException("Length of y and y hat is different");
        }
        double d = 0;
	for (int i = 0; i < max; i++) {
	    d += deviance(y[i],yhat[i]);
	}
	return d;
    };

    public double chi2(double[] y, double[] yhat) {
	int max = y.length;
        if (y.length != yhat.length) {
            throw new RuntimeException("Length of y and y hat is different");
        }
        double d = 0;
	for (int i = 0; i < max; i++) {
	    double s = y[i] - yhat[i];
	    d += (s*s) / varY(yhat[i]);
	}
	return d;
    }

    abstract double link(double mu);

    abstract double inverseLink(double eta);

    abstract double delLink(double mu);

    abstract double varY(double mu);
    
    abstract double deviance(double y, double yhat);

    // abstract double loglike(double[] y, double[] yhat);

    abstract double[] initializeMu(double[] Y);

}
