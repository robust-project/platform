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

import uk.ac.soton.cormsis.robust.stats.glm.spec.ILinkFunction;
import uk.ac.soton.cormsis.robust.stats.utils.ArrayOperation;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;

/**
 *
 * @author Edwin Tye
 */

class NormalLink extends AbstractLink implements ILinkFunction {

    /**
     * Compute the g(\mu) given name link function
     *
     * @param mu data
     * @return \eta
     */
    @Override
    public double link(double mu) {
        return mu;
    }

    /**
     * Compute an array of g^{-1}(\eta) given name of link function
     *
     * @param eta X\beta
     * @return \mu
     */
    @Override
    public double inverseLink(double eta) {
        return eta;
    }

    /**
     * Compute \nabla_{\theta} g(\mu)
     *
     * @param mu data
     * @return output of the first derivative of link function
     */
    @Override
    public double delLink(double mu) {
        // \nabla g(\mu) = 1
        return 1;
    }

    // final because I am thinking about whether to move it to linkFunction as well
    @Override
    public double varY(double mu) {
        return 1;
    }

    @Override
    public double deviance(double y, double yhat) {
        double s = y - yhat;
        return s * s;
    }

    @Override
    public double[] initializeMu(double[] Y) {
	int numRow = Y.length;
	double s = VectorCalculation.mean(Y);
	double[] v = new double[numRow];
	for (int i = 0; i < numRow; i++) {
	    v[i] = s;
	}
        return v;
    }

    // @Override 
    // 	public double loglike (double[] y, double[] yhat) {
    // 	int numRow = y.length;
    // 	double dispersion = 0;
    // 	for (int i = 0; i < numRow; i++) {
    // 	    double s = y[i] - yhat[i];
    // 	    dispersion += s*s;
    // 	}
    // 	// numCovariate + 1 because numCovariate doesn't include intercept
    // 	dispersion /= numRow;

    // 	double loglike = 0;
    // 	for (int i = 0; i < numRow; i++) {
    // 	    loglike += y[i] * Math.log(mu);
    // 	    loglike -= mu;
    // 	}
    // 	return loglike;
    // }
}
