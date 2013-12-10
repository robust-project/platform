/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton
// CORMSIS
// Centre of Operational Research, Management Science and Information Systems
// 2012
//
// Copyright in this software belongs to University of Southampton
// CORMSIS
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
//      Created Date :          2012-08-30
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.cormsis.robust.stats.lossfunction.spec;

public interface IRegressionLoss {

    /**
     * Initialize the class with data
     * @param obv vector of observation
     */
    // public void initialize(double[] obv);

    /**
     * Initialize the class with data
     * @param obv matrix of observation
     */
    // public void initialize(double[][] obv);

    /**
     * Get the mean square error
     * @param pred vector of prediction
     * @return MSE
     */
    public double getMSE(double[] pred);    

    
    /**
     * Get the mean square error
     * @param pred matrix of prediction
     * @return MSE
     */
    public double getMSE(double[][] pred);    

}
