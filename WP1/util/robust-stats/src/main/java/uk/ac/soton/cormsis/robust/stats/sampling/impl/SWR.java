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
package uk.ac.soton.cormsis.robust.stats.sampling.impl;

import java.util.Random;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.ISWR;
import uk.ac.soton.cormsis.robust.stats.utils.ArrayOperation;

// My estimate is that this will be the building block for pretty much everything else
public class SWR implements ISWR {
    
    private Random rnd = new Random();
            
    // The values
    //private double[][] data2D;
    private double[] data1D;
    // The weight
    private double[] dblW;
    
    private boolean hasWeight = false;
    
    private double totalWeight;
    // Although it may be more accurate to call it EDF, I think the expression 
    // of CDF is easiser to understand
    private double[] dblCDF;
    private double totalProbability;
    
    private int size;
    //private int largestIndex;
    
    // If a sample from the EDF is desired, then sort the array prior to initialization
    public SWR(double[] Y) {
        this.data1D = ArrayOperation.copyArray(Y);
        this.size = Y.length;
        //this.largestIndex = Y.length - 1;
        computeCDF();
    }
    
    /*
    //@Override
    private void initialize(double[][] Y) {
        this.data2D = MatrixOperation.copyMatrix(Y);
        this.size = Y.length;
    } 
     * 
     */
    
    // It should always be a sorted array along with the weights
    @Override
    public void initialize(double[] Y, double[] W) {
        this.data1D = ArrayOperation.copyArray(Y);
        this.size = Y.length;
        //this.largestIndex = Y.length - 1;
        this.dblW = ArrayOperation.copyArray(W);
        computeCDFWithWeight();
        this.hasWeight = true;
    }
    
    @Override
    public int getIndex() {
        int intIndex = 0;
        if (this.hasWeight) {
            double U = rnd.nextDouble();
            // This is a safety mechanism so that we will not actually fail a search
            while (U > this.totalProbability) {
                U = rnd.nextDouble();
            }
            // It may be safer to use U * this.totalProbability
            intIndex = ArrayOperation.binarySearch(this.dblCDF, U);
        } else {
            // Maybe this can be removed, depending on which one is faster.  i.e.
            // a binary \rightarrow linear search, or generating an integer
            // which means that we always assume that it has weight
            intIndex = rnd.nextInt(size);
        }
        return intIndex;
    }

    @Override
    public double getSample() {
        int intIndex = 0;
        if (this.hasWeight) {
            double U = rnd.nextDouble();
            // This is a safety mechanism so that we will not actually fail a search
            while (U > this.totalProbability) {
                U = rnd.nextDouble();
            }
            // It may be safer to use U * this.totalProbability
            intIndex = ArrayOperation.binarySearch(this.dblCDF,U);
        } else {
            // Maybe this can be removed, depending on which one is faster.  i.e.
            // a binary \rightarrow linear search, or generating an integer
            // which means that we always assume that it has weight
            intIndex = rnd.nextInt(size);
        }
        return this.data1D[intIndex];
    }

    // This basically allows us to get a value from the EDF given probability
    // assuming that we have a sorted array 
    @Override
    public double getSample(double U) {
        int intIndex = 0;
        if (U > 1) {
            throw new IllegalArgumentException("Must be a number from a Uniform [0,1]");
        } else if (U < 0) {
            throw new IllegalArgumentException("Must be a number from a Uniform [0,1]");
        } else {
            // Although no weights have been initialized, this is thought of as drawing from
            // an EDF
            intIndex = ArrayOperation.binarySearch(this.dblCDF,U);
        }
        return this.data1D[intIndex];
    }
    
    //  Not only do we want the CDF for the inversion method, in reality, we also
    // want the quantile functions to reduce the search area.  The CDF is naturally
    // in order, so we don't need to sort
    private void computeCDFWithWeight() {
        this.dblCDF = new double[size];
        // It is assumed here that the weights do not sum to one
        this.totalWeight = 0;
        this.totalProbability = 0;
        for (int i = 0; i < this.size; i++) {
            this.totalWeight += this.dblW[i];
            this.dblCDF[i] = this.totalWeight;
        }
        for (int i = 0; i < this.size; i++) {
            this.dblCDF[i] /= this.totalWeight;
            //System.out.println("The cdf of index " +i+ " is " +this.dblCDF[i]);
            // Also summing the total probability to deal with machine precision
            this.totalProbability += this.dblCDF[i];
            //System.out.println("Total probability");
        }
    }
    
    private void computeCDF() {
        this.dblCDF = new double[size];
        // It is assumed here that the weights do not sum to one
        double weight = 1 / (double) size;
        this.totalWeight = 0;
        for (int i = 0; i < this.size; i++) {
            this.totalWeight += weight;
            this.dblCDF[i] = this.totalWeight;
        }
    }
    
}
