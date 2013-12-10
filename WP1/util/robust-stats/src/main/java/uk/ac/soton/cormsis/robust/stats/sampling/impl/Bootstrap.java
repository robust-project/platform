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

import uk.ac.soton.cormsis.robust.stats.sampling.spec.IBootstrap;
import uk.ac.soton.cormsis.robust.stats.sampling.spec.ISWR;

/**
 * The name bootstrap instead of bootstrapping is chosen because this is what
 * Efron & Tibshirani, Davison & Hinkley used.  Even if Wikipedia use the term
 * "bootstrapping", lets just say that the four names given above has a lot more 
 * weight on this issue over however many million edited the Wiki page
 * @author Edwin
 */
public class Bootstrap implements IBootstrap{

    /**
     * First of all, I am not extending the SWR class, but instead, just using
     * it's functionality because I don't feel that those method in SWR are really
     * that applicable
     */
    private ISWR swr;;
    private double[] data1D;
    private double[][] data2D;
    private int numRow;
    private int numCol;

    /*
     * Constructors
     *
     */
    
    public Bootstrap(double[] Y) {
        initialize(Y);
    }

    public Bootstrap(double[][] Y) {
        initialize(Y);
    }

    /*
     * Start of public methods
     *
     */

    /**
     * It is actually more convenient to have things in an index if we wish to 
     * compute some sort of statistics at the end or with multivariate data
     * @param n replicates
     * @return all sampled index of size \left[ observation \times iteration \right]
     */
    @Override
	public int[][] getIndex(int n) {
        int[][] x = new int[this.numRow][n];
        for (int i = 0; i < this.numRow; i++) {
            for (int j = 0; j < n; j++) {
                x[i][j] = swr.getIndex();
            }
        }
        return x;
    }

    /**
     * It doesn't work if multivariate data was initialized.  Values will be returned
     * but the output given will just be the treatment of univariate
     * @param n replicates
     * @return samples from the data of size \left[ observation \times iteration \right]
     */
    @Override
	public double[][] getSample(int n) {
        double[][] x = new double[this.numRow][n];
        for (int i = 0; i < this.numRow; i++) {
            for (int j = 0; j < n; j++) {
                x[i][j] = swr.getSample();
            }
        }
        return x;
    }

    /**
     * Returns a single array of sample of the original size
     * @return returns a sample 
     */
    @Override
	public double[] getSample() {
        double[] x = new double[this.numRow];
        for (int i = 0; i < this.numRow; i++) {
            x[i] = swr.getSample();
        }
        return x;
    }

    /**
     * Calculates the mean of each of the variables
     * @param n replicates
     * @return the mean for each variables of all replicates
     */
    @Override
	public double[][] getMean(int n) {
        // First, we get the index we need
        int[][] indexTemp = getIndex(n);
        
        // Number of iteration as row
        // and number of covriate as column
        double[][] mu = new double[n][this.numCol];
        // Beginning looping
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < this.numRow; j++) {
                for (int p = 0; p < this.numCol; p++) {
                    mu[i][p] += this.data2D[indexTemp[j][i]][p];
                }
            }
            for (int p = 0; p < this.numCol; p++) {
                mu[i][p] /= (double) this.numRow;
            }
        }
        return mu;
    }


    /*
     * Start of private methods
     *
     */
	private void initialize(double[] Y) {
        this.data1D = Y;
        this.numRow = Y.length;
        // Convert data into 2D array format, this is to simplify the mean
        // output which is always given in a 2D array format
        this.data2D = new double[numRow][1];
        for (int i = 0; i < this.numRow; i++) {
	    this.data2D[i][0] = Y[i]; 
        }
        this.numCol = this.data2D[0].length;
        swr = new SWR(this.data1D);
    }
    
	private void initialize(double[][] Y) {
        this.data2D = Y;
        this.numRow = Y.length;
        this.numCol = Y[0].length;
        this.data1D = new double[this.numRow];
        for (int i = 0; i < this.numRow; i++) {
            this.data1D[i] = Y[i][0];
        }
        swr = new SWR(this.data1D);
    }

}
