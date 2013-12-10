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
package uk.ac.soton.cormsis.robust.stats.lm.spec;
import uk.ac.soton.cormsis.robust.stats.lm.impl.LM;
public interface ILM {

    public LM getObject();

    /**
     * Carries out the fit, i.e. get the initialized value and then find and store beta
     */
    public double[] fit();

    
    /**
     * Carries out OLS directly.  Also stores the X and Y
     * matrix that is entered as well as \beta
     * @param X design matrix
     * @param Y response matrix
     * @return beta matrix
     */
    public double[] fit(double[][] X, double[] Y);

    /**
     * Carries out WLS directly.  Also stores X,Y and the given weights
     * @param X design matrix
     * @param Y response matrix
     * @param W weight matrix
     * @return beta matrix
     */
    public double[] fit(double[][] X, double[] Y, double[] W);

    /**
     * Prediction on a single observation
     * 
     * @param x_plus the new observations for the independent variables
     * @return a prediction
     */
    public double predict(double[] x_plus);

    /**
     * Predict using the entered X_{+} against the stored beta value
     * @param X_plus X_plus X_{+} matrix, defined to be the new observation to be predicted
     * @return \hat{Y} 
     */
    public double[] predict(double[][] X_plus);

    /**
     * Get the weight that has been entered
     * @return weight matrix
     */
    public double[] getWeight();
    
    /**
     * Set the weights
     * @param w weight matrix
     */
    public void setWeight(double[] w);

    /**
     * Get the design matrix that was initialized
     * @return X 
     */
    public double[][] getX();

    /**
     * Get the response matrix that was initialized
     * @return Y
     */
    public double[] getY();
    
    /**
     * Get the \hat{\beta} matrix that was estimated
     * @return \beta
     */
    public double[] getBeta();

    public void setBeta(double[] beta);

    /**
     * Return a matrix of residuals from the fitted values
     * @return e_i = y_i - \hat{y}_i
     */
    public double[] getResidual();

    public double getMSE();

    /**
     * Return a matrix of modified residuals from the fitted values
     * @return e_i = (y_i - \hat{y}_i) / (1 - h_{ii}) where h = leverages
     */
    public double[] getModifiedResidual();

    /**
     * The leverages are defined to be the diagonal elements of the Hat matrix, which 
     * is X(X^{T}X)^{-1}X^{T} or X=\tilda{X} = W^{1/2}X for the WLS case
     * 
     * @return array of h_{jj}
     */
    public double[] getLeverages();
    
    /**
     * Gets the number of observation in the design matrix
     * @return number of observation
     */
    public int getNumObv();
    
    /**
     * Gets the number of covariates in the design matrix
     * @return number of covariates
     */
    public int getNumCovariate();
}
