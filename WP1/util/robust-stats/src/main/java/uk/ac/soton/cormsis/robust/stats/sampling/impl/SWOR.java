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
import uk.ac.soton.cormsis.robust.stats.sampling.spec.ISWOR;
import uk.ac.soton.cormsis.robust.stats.utils.ArrayOperation;


// TODO: Combine the two sampling procedure of index and actual sample into one
/**
 * The name is now SWOR (Sampling WithOut Replacement), and not in the long form
 * @author Edwin
 */
public class SWOR implements ISWOR {
    
    private Random rnd = new Random();
    // This is the original data
    private double[] data;
    //private double[][] data2D;
    // The values
    private double[] dblX;
    private int[] workingIndex;
    //private int[] originalIndex;
    private int size;
    private int originalSize;
    
    // If a sample from the EDF is desired, then sort the array prior to initialization
    public SWOR(double[] Y) {
        this.data = ArrayOperation.copyArray(Y);
        this.size = Y.length;
        this.originalSize = Y.length;
        //this.largestIndex = Y.length - 1;
        //computeCDF();
    }
    
    @Override
    public double[] getListSample() {
        double[] y = new double[this.originalSize];
        copyData();
        for (int i = 0; i < this.originalSize; i++) {
            y[i] = getSample();
        }
        return y;
    }
    
    /**
     * Given a list of original index, it is not that hard to get a sample from 
     * it as well
     * @return array of original index
     */
    @Override
    public int[] getListIndex() {
        int[] y = new int[this.originalSize];
        copyData();
        for (int i = 0; i < this.originalSize; i++) {
            y[i] = getIndex();
        }
        return y;
    }
    
    @Override
    public double getSample() {
        if (this.size == 0) {
            throw new IllegalArgumentException("No more data to be sampled from");
        } else {
            int intIndex = 0;
            // Maybe this can be removed, depending on which one is faster.  i.e.
            // a binary \rightarrow linear search, or generating an integer
            // which means that we always assume that it has weight
            intIndex = rnd.nextInt(this.size);
            double dblTemp = this.dblX[intIndex];
            deleteElement(intIndex);
            return dblTemp;
        }
    }
    
    private int getIndex() {
        if (this.size == 0) {
            throw new IllegalArgumentException("No more data to be sampled from");
        } else {
            int intIndex = 0;
            // Maybe this can be removed, depending on which one is faster.  i.e.
            // a binary \rightarrow linear search, or generating an integer
            // which means that we always assume that it has weight
            intIndex = rnd.nextInt(this.size);
            int y = this.workingIndex[intIndex];
            deleteIndexElement(intIndex);
            return y;
        }
    }
    
    private void deleteIndexElement(int index) {
        //System.out.println("Size of the array = " + this.size + " and the index = " + index);
        int[] y = new int[this.size - 1];
        // Copying the first part of the array where it is less than the index
        System.arraycopy(this.workingIndex, 0, y, 0, index);
        // Copying the latter part of the array
        if (index < (this.size - 1)) {
            System.arraycopy(this.workingIndex, index + 1, y, index, (this.size - index - 1));
        }
        this.workingIndex = y;
        this.size = y.length;
    }
    
    private void deleteElement(int index) {
        //System.out.println("Size of the array = " + this.size + " and the index = " + index);
        double[] y = new double[this.size - 1];
        // Copying the first part of the array where it is less than the index
        System.arraycopy(this.dblX, 0, y, 0, index);
        // Copying the latter part of the array
        if (index < (this.size - 1)) {
            System.arraycopy(this.dblX, index + 1, y, index, (this.size - index - 1));
        }
        this.dblX = y;
        this.size = y.length;
    }

    // To create a set of data without destroying the original
    private void copyData() {
        this.dblX = ArrayOperation.copyArray(data);
        this.size = this.originalSize;
        //this.originalIndex = new int[this.size];
        this.workingIndex = new int[this.size];
        for (int i = 0; i < this.size; i++) {
            //this.originalIndex[i] = i;
            this.workingIndex[i] = i;
        }
    }
}
