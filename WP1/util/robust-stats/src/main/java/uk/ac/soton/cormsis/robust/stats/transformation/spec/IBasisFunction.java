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
//      Created Date :          2012-11-24
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.transformation.spec;

/**
 *
 * @author Edwin
 */
public interface IBasisFunction {

    /**
     * Gets the original as well as the interaction terms in order of x_1*x_2, x_1*x_3, \ldots x_{p-1}*x_{p}
     * 
     * @return design matrix
     */
    public double[][] getInteraction();

    /**
     * Gets the original as well as the interaction terms in order of x_1*x_2, x_1*x_3, \ldots x_{p-1}*x_{p}
     * 
     * @param X the observations
     * @return design matrix
     */
    public double[][] getInteraction(double[][] A);

    /**
     * Gets the original, interaction and quadratic terms in order of original, interaction of x_1*x_2 ,x_1*x_3, 
     * \ldots, x_{p-1}*x_{p} and quadratic in order of x_{1}^2, \ldots x_{p}^2
     * 
     * @return design matrix
     */
    public double[][] getQuadratic();

    /**
     * Gets the original, interaction and quadratic terms in order of original, interaction of x_1*x_2 ,x_1*x_3, 
     * \ldots, x_{p-1}*x_{p} and quadratic in order of x_{1}^2, \ldots x_{p}^2

     * @param A observation
     * @return design matrix
     */
    public double[][] getQuadratic(double[][] A);
    
    /**
     * Gets the original as well the pure polynomial term up to the input power from x^{1} to x^{power}
     * 
     * @param degree
     * @return the pure polynomial expansion
     */
    public double[][] getPurePolynomial(int power);

    /**
     * Gets the original as well the pure polynomial term up to the input power from x^{1} to x^{power}
     * 
     * @param X the observations
     * @param power x^{power}
     * @return design matrix
     */
    public double[][] getPurePolynomial(double[][] X,int power);
    

    /**
     * Initialize the observation
     * 
     * @param A The original observed matrix
     */
    // public void initialize(double[][] A);

}
