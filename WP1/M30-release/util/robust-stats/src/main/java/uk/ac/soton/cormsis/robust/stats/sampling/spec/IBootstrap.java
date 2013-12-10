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
package uk.ac.soton.cormsis.robust.stats.sampling.spec;

/**
 *
 * @author Edwin
 */
public interface IBootstrap {

    //public void Bootstrap();

    //public void Bootstrap(double[] data);

    //public void Bootstrap(double[][] data);

    /**
     * Initialize the class with an array of data
     * @param data vector x
     */
    // public void initialize(double[] data);

    /**
     * Initialize the class with a matrix of data, where rows are the observations
     * and the columns represent the variables
     * @param data matrix X
     */
    // public void initialize(double[][] data);

    /**
     * Get a matrix of index, with the original number of data point and the desired 
     * number of replicates as entered (across row)
     * @param replicate number of repeated sample
     * @return matrix of index
     */
    public int[][] getIndex(int replicate);

    /**
     * Get a matrix of sample.  Same as getIndex, but instead of the index, the 
     * output is the actual sample.  Number of data point as row, and number of 
     * replicate as column
     * @param replicate number of repeated sample
     * @return matrix of sample
     */
    public double[][] getSample(int replicate);

    /**
     * Return a single array of sample from the original data
     * @return vector of sample
     */
    public double[] getSample();

    /**
     * Calculate the mean of each replicate
     * This NEEDS to be changed in the future.  Very confusing
     * @param replicate n
     * @return matrix of mean, row as replicates and column as variables
     */
    public double[][] getMean(int replicate);

}
