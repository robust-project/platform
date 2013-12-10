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
//      Created Date :          2012-11-29
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.sampling.impl;

import uk.ac.soton.cormsis.robust.stats.glm.impl.GLM;
import uk.ac.soton.cormsis.robust.stats.glm.spec.IGLM;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.IBootstrapGLM;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.ISWOR;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.ISWR;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;

/**
 * @author Edwin
 */
public class BootstrapGLM implements IBootstrapGLM {

    //TODO: Make sure that the betaStar and beta exist
    private GLM glm;
    private double[][] betaStar;
    
    public BootstrapGLM(GLM glm) {
	this.glm = glm;
    }

    public BootstrapGLM(IGLM glm) {
	this.glm = glm.getObject();
    }

    @Override
    public void getBeta() {
	this.glm.getBeta();
    }

    @Override
    public double[][] getBetaStar() {
	return this.betaStar;
    }

    @Override
    public void betaNonParametric(int iteration) {
	betaStar = new double[iteration][glm.getNumCovariate()+1];
	// This means that we want to case resampling

	// Doing a sample without replacement to get a new y^{*} vector
	ISWOR swor= new SWOR(this.glm.getY());

	for (int i = 0; i < iteration; i++) {
	    double[] yStar = swor.getListSample();
	    IGLM glm2 = new GLM(this.glm.getX(),yStar);
	    betaStar[i] = glm2.fit(this.glm.getDistName());
	    // betaStar[i] = glm2.getBeta();
	}
    }

    @Override
    public void betaSemiParametric(int iteration) {
	VectorCalculation vc = new VectorCalculation();
	int numRow = this.glm.getNumObv();
	betaStar = new double[iteration][this.glm.getNumCovariate()+1];
	/*
	 * This means that we want to generate y_{i}^{*} according to 
	 * y_{i}^{*} = \mu(x_{i}) + \epsilon^{*}
	 */
	ISWR swr = new SWR(vc.subtract(this.glm.getModifiedPearsonResidual(), vc.mean(this.glm.getModifiedPearsonResidual())));
	
	// Here, I am assuming that the modified pearson residual is actually
	// homoscedastic.  Further adjustments may need to be made afterwards
	// swr.initialize(vc.subtract(this.glm.getModifiedPearsonResidual(), vc.mean(this.glm.getModifiedPearsonResidual())));
	
	// swr.initialize(this.glm.getModifiedPearsonResidual());
	// System.out.println("The mean of modified pearson is ");
	// System.out.println(VectorCalculation.mean(this.glm.getModifiedResidual()));
	double[] yhat = vc.subtract(this.glm.getY(),this.glm.getResidual());
	double[] varY = this.glm.getVarY();
	double deviance = this.glm.getDeviance();
	for (int i = 0; i < iteration; i++) {
	    double[] ystar = new double[numRow];
	    for (int j = 0; j < numRow; j++) {
		ystar[j] = yhat[j] + Math.sqrt(deviance * varY[j]) *swr.getSample();
	    }
	    
	    IGLM glm2 = new GLM(this.glm.getX(),ystar,"Poisson");
	    betaStar[i] = glm2.fit();
	}
    } 

    @Override
    public double[] predictionError(double[] x, int iteration) {
	// the default number of iteration is being set at 1000, i.e. 1000 sample of prediction error
	// int iteration = 1000;
	double[] predictionError = new double[iteration];
	VectorCalculation vc = new VectorCalculation();
	int numRow = this.glm.getNumObv();
	double[] betaStar = new double[this.glm.getNumCovariate()+1];
	// betaStar = new double[iteration][this.glm.getNumCovariate()+1];
	/*
	 * This means that we want to generate y_{i}^{*} according to 
	 * y_{i}^{*} = \mu(x_{i}) + \epsilon^{*}
	 */
	ISWR swr = new SWR(vc.subtract(glm.getModifiedPearsonResidual(), vc.mean(glm.getModifiedPearsonResidual())));
	
	double[] yhat = vc.subtract(this.glm.getY(),this.glm.getResidual());
	double[] varY = this.glm.getVarY();
	double deviance = this.glm.getDeviance();
	for (int i = 0; i < iteration; i++) {
	    // First, we want to generate a new beta^{*}
	    double[] ystar = new double[numRow];
	    for (int j = 0; j < numRow; j++) {
		ystar[j] = yhat[j] + Math.sqrt(deviance * varY[j]) *swr.getSample();
	    }
	    
	    IGLM glm2 = new GLM(this.glm.getX(),ystar,"Poisson");
	    betaStar = glm2.fit();
	    // Then we want to find \delta(x_{+}^{T}\hat{\beta}^{*}, x_{+}^{T}\hat{\beta} + \epsilon^{*})
	    double muStar_plus = glm2.predict(x);
	    double yStar_plus = this.glm.predict(x);
	    predictionError[i] = (yStar_plus - muStar_plus) / Math.sqrt(glm2.getVarY(muStar_plus) * glm2.getDeviance());
	}
	return predictionError;
    }


}
