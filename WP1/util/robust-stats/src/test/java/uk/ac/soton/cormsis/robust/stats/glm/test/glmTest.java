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
//      Created Date :          2012-08-05
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.glm.test;

import uk.ac.soton.cormsis.robust.stats.glm.impl.GLM;
import uk.ac.soton.cormsis.robust.stats.glm.impl.GLMnb;
import uk.ac.soton.cormsis.robust.stats.glm.spec.IGLM;
import uk.ac.soton.cormsis.robust.stats.glm.spec.IGLMnb;
import uk.ac.soton.cormsis.robust.stats.utils.display;

/*
 * The main class of the glm testing
 */
public class glmTest {

    public static void main(String[] args) throws Exception {
        double[][] p = {{-1, 1}, {-0.5, 0.25}, {0, 0}, {0.5, 0.25}, {1, 1}};
        double[] b = {1, 0.5, 0, 0.5, 2.0};
        IGLM glm = new GLM(p,b);
        
        System.out.println("Log of -1 = " +Math.log(-1));

        //RealMatrix beta = ABC.regress(p, b);
        double[] beta = glm.fit();
        for (int i = 0; i < beta.length; i++) {
	    System.out.printf("%9.5f ", beta[i]);
            System.out.println();
        }

        System.out.println();
        double[] e5 = glm.getResidual();
        System.out.println("Residual for the identity link function");
	display.print(e5);

	double[] y = {18,17,15,20,10,20,25,13,12};
	double[][] x = { {0,0,0,0},{1,0,0,0},{0,1,0,0},{0,0,1,0},{1,0,1,0},{0,1,1,0},{0,0,0,1},{1,0,0,1},{0,1,0,1} };

        beta= glm.fit(x, y, "Poisson");
	System.out.println("design matrix");
	display.print(x);
        System.out.println(" for Y ");
        double[] Y = glm.getY();
        //RealMatrix YY = getY();
	display.print(Y);

	System.out.println("");
	double[] beta1 = glm.getBeta();
        for (int i = 0; i < beta.length; i++) {
	    System.out.printf("%9.5f ", beta[i]);
	    System.out.print(" and from getBeta " + beta1[i]);
            System.out.println();
        }
	try {
	double[] e1 = glm.getResidual();
        double[] Yhat_plus = glm.predict(x);
	System.out.println();
        for (int i = 0; i < Yhat_plus.length; i++) {
            System.out.println(Yhat_plus[i] + " and residual " +e1[i]+ " with observed value = " +Y[i]);
        }
	System.out.println("\n The total number of iteration = "+glm.getTotalIteration());

	System.out.println("\n The estimated dispersion parameter = " + glm.getDispersion());

	System.out.println("Leverages");
	double[] h = glm.getLeverages();
	display.print(h);

	System.out.println("Variance of Y");
	double[] varY = glm.getVarY();
	display.print(varY);

	System.out.println("Working weight");
	double[] W = glm.getW();
	display.print(W);

	System.out.println("Pearson residuals");
	double[] pResidual = glm.getPearsonResidual();
	display.print(pResidual);

	System.out.println("Modified Pearson residuals");
	double[] mpResidual = glm.getModifiedPearsonResidual();
	display.print(mpResidual);

	System.out.println("LP residuals");
	double[] LPResidual = glm.getModifiedLPResidual();
	display.print(LPResidual);

	System.out.println("Deviance residuals");
	double[] devianceResidual = glm.getDevianceResidual();
	display.print(devianceResidual);
	} catch (Exception e) {
	    System.out.println(e);
	}

	System.out.println("Modified Deviance residuals");
	double[] devianceResidual = glm.getModifiedDevianceResidual();
	display.print(devianceResidual);

	System.out.println("\n And the deviance = " +glm.getDeviance());
	System.out.println(" with the loglike = " +glm.getLogLike());
	System.out.println(" aic = " +glm.getAIC());

	System.out.println(" and number of observation = " +glm.getNumObv());
	System.out.println(" and number of covariate = " +glm.getNumCovariate());

	/*
	 * Start testing of the NB2 function
	 */
	IGLMnb nb = new GLMnb(x,y);
	double[] betaNB = nb.getBeta();
	System.out.println("The beta value of the nb estimate = ");
	display.print(betaNB);
	System.out.println("The beta value of the nb estimate = ");
	System.out.println("The dispersion of the nb model = " + nb.getDispersion());
	System.out.println("The alpha value of the nb model = " + nb.getAlpha());
	
    }
}