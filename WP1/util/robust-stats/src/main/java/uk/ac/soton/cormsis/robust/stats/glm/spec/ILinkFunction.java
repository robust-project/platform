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
package uk.ac.soton.cormsis.robust.stats.glm.spec;

/**
 *
 * @author Edwin
 *
 *
 * Link Function defined as g(\cdot) \eta_{i} = x_{i}^{T}\beta g(\mu_{i}) =
 * \eta_{i} = g(E(Y_{i})) g^{-1}(\eta_{i}) = \mu_{i}
 *
 *
 * Knowing from the exponential family E(Y_{i}) = b^{\prime}(\theta_{i}) =
 * \mu_{i} Var(Y_{i}) = b^{\prime \prime}(\theta_{i})
 *
 *
 */
public interface ILinkFunction {
    
    public double[] link(double[] mu);
    
    public double[] inverseLink(double[] eta);
    
    public double[] delLink(double[] mu) ;
    
    public double[] varY(double mu[]);
    
    public double deviance(double[] y, double[] yhat);
    
    public double link(double mu);
    
    public double inverseLink(double eta);
    
    public double delLink(double mu);
    
    public double varY(double mu);
    
    public double deviance(double y, double yhat);

    // double loglike(double[] y, double[] yhat);
    
    public double[] initializeMu(double[] Y); 
    
}
