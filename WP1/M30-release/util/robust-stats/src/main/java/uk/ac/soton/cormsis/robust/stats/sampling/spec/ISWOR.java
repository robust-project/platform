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
public interface ISWOR {
    
    /**
     * Initial the class with an array of data
     * @param data 
     */
    // public void initialize(double[] data);

    /**
     * Get a list of the sample, where each data point will be sampled once
     * @return array of data
     */
    public double[] getListSample();
    
    /**
     * Get a  list of index of the sample.  Which can then be used on the original
     * data to reconstruct a sequence
     * @return array of index
     */
    public int[] getListIndex();
    
    /**
     * Get a single sample from the list
     * @return data point
     */
    public double getSample();
    
}
