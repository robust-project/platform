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

public interface ISWR {

    /**
     * Initialize the class with a double array
     * @param Y data
     */
    // public void initialize(double[] Y);
        
        //public void initialize(double[][] Y);

    /**
     * Initialize the class with a double array as well the the corresponding weights
     * @param Y data
     * @param W weights
     */
    public void initialize(double[] Y, double[] W);
    
    /**
     * Returns the index of the sample
     * @return index
     */
    public int getIndex();

    /**
     * Get a sample, using weights if it has been provided from the initialized array
     * @return a value from the array
     */
    public double getSample();

    /**
     * Same as getSample() but using a user specified value.  When the initialized array is sorted, the it assumes that it
     * is an EDF and it will take a sample given a number from a Uniform [0,1].
     * @param U \in [0,1]
     * @return a value from the array
     */
    public double getSample(double U);

}
