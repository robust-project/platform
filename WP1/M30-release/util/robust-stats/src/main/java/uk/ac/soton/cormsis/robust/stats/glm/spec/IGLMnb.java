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
//      Created Date :          2012-07-30
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.glm.spec;

import uk.ac.soton.cormsis.robust.stats.lm.spec.ILM;
import uk.ac.soton.cormsis.robust.stats.glm.impl.GLMnb;

public interface IGLMnb extends ILM {

    // This extends the normal lm, which means that a fit() exist and
    // it actually carries out the OLS fit.
    // In that case, it will just be equivalent to the identity link
    // but without specifying it explicitly.

    public GLMnb getObject();
    
    public double predict(double[] x_plus);

    /**
     * Generate the predicted result using the stored beta value against the entered X matrix
     * @param X_plus X_{+} matrix, defined to be the new observation to be predicted
     * @param LinkName Name of the link function
     * @return fitted values of \hat{Y_{+}}
     */
    @Override
	public double[] predict(double[][] X_plus);

    /**
     * Returns the number of iteration take to reach the working precision
     * 
     * @return total number of IRLS update
     */
    public int getTotalIteration();
    

    /**
     * Returns the estimated dispersion parameter. The default is 1 and it is the value used
     * to estimate the model
     * 
     * @return dispersion parameter
     */
    public double getDispersion();

    public double getAlpha();
    
    /**
     * Deviance as defined by 2L(y;y) - 2L(\mu;y)
     * 
     * @return total deviance
     */
    public double getDeviance();

    public double getLogLike();

    public double getAIC();

    /**
     * Returns the Pearsons residual where \phi is the dispersion parameter
     * prior is the prior weight for the observation
     * 
     * @return e_i = y_i - \hat{y}_i / (\phi \prior_i Var(\hat{y}_i)) ^{1/2}
     */
    public double[] getPearsonResidual();

    /**
     * Returns the Pearson residual with leverage correction
     * 
     * @return e_i = y_i - \hat{y}_i / (\phi \prior_i Var(\hat{y}_i) (1 - h_{ii}))^{1/2}
     */
    public double[] getModifiedPearsonResidual();

    /**
     * Residual on the Linear Predictor
     * 
     * @return e_i = g(y_i) - g(\hat{y}_i) / (\phi \prior_i g^{\prime}(\hat{\mu}_{i})^{2} Var(\hat{y}_i) )^{1/2}
     */
    public double[] getModifiedLPResidual();

    /**
     * Residual defined by the deviance of the model
     * 
     * @return sign(y_{i} - \hat{y}_{i}) [2( L(y_i) - L(\hat{y}_i))]^{1/2} 
     */
    public double[] getDevianceResidual();

    /**
     * Deviance residual with leverage correction
     * 
     * @return sign(y_{i} - \hat{y}_{i}) [2( L(y_i) - L(\hat{y}_i))]^{1/2} / (1 - h_{ii})^{1/2}
     */
    public double[] getModifiedDevianceResidual();

    /**
     * The variance of estimates of each observation
     * 
     * @return Var(\hat{y})
     */
    public double[] getVarY();

    public double getVarY(double s);

    public double[] getVarY(double[] s);

    public double[] getW();


}
