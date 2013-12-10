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
package uk.ac.soton.cormsis.robust.stats.utils;

import java.util.Arrays;

public class ArrayOperation {

    /**
     * Makes a copy of the array
     * @param Y input array
     * @return output array
     */
    static public double[] copyArray(double[] Y) {
        double[] X = new double[Y.length];
        System.arraycopy(Y, 0, X, 0, Y.length);
        return X;
    }
    
    /**
     * Finds the mid point between two integer value.  The name isn't
     * that good and it may be changed later
     * @param lower value of the index at the lower end
     * @param higher value of the index at the higher end
     * @return middle value between the two argument entered
     */
    static public int midPointIndex(int lower, int higher) {
        int value;
        int totalSize = higher - lower + 1;
        if ((totalSize % 2) == 0) {
            value = totalSize / 2;
        } else {
            value = (totalSize - 1) / 2;
        }
        return value;
    }

    static public double innerProduct(double[] x, double[] y) {
	if (x.length != y.length) {	
            throw new IllegalArgumentException("Size of vector not equal");
	} 
	double t = 0;
	for (int i = 0; i < x.length; i++) {
	    t += x[i] * y[i];
	}
	return t;
    }

    static public double[] normalize(double[] x) {
	int max = x.length;
	double[] y = new double[max];
	double s = 0;
	for (int i = 0; i < max; i++) {
	    s += x[i];
	}
	for (int i = 0; i < max; i++) {
	    y[i] = x[i] / s;
	}
	return y;
    }


    /**
     * Returns the median value of the array
     * @param X vector of value
     * @return median value of the array
     */
    static public double median(double[] X) {
        double[] Y = ArrayOperation.copyArray(X);
        Arrays.sort(Y);
        int intTemp = midPointIndex(0, (X.length - 1));
        return Y[intTemp];
    }

    /**
     * This version finds the output when y_i \ge U, which is pretty much tailored
     * to a EDF.  Not to mention that the array it is taking is double, which means 
     * that it is pretty much impossible to get an exact match
     * @param Y vector (sorted) to be searched
     * @param U value to be matched
     * @param lower the starting index
     * @return index
     */
    static public int linearSearch(double[] Y, double U, int lower) {
        int size = Y.length;
        // No point to test the first value, because it has to be smaller by default
        int i = lower;
        int intIndex = 0;
        //System.out.println(" The starting value of lower index of linear search = " +lower);
        while (i < size) {
            //System.out.println("The CDF at " +i+ " is " + this.dblCDF[i]);
            if (Y[i] >= U) {
                // if the CDF is bigger than value, then it must be the value below
                intIndex = i;
                break;
            }
            i++;
        }
        return intIndex;
    }

    /**
     * Doing a binary search until the number of possible index is down to 8 or less, then it 
     * switches to a linear search.  Required a sorted array and will return the first index 
     * that stores a value over the U (entered parameter)
     * @param Y vector (sorted) to be searched
     * @param U value to be matched
     * @return index
     */
    static public int binarySearch(double[] Y, double U) {
        int intIndex = 0;
        int lowerIndex = 0;
        int upperIndex = Y.length - 1;

        int totalSize = Y.length;
        int middleIndex = midPointIndex(lowerIndex, upperIndex);
        // Start using a linear search when the total number that require evaluation is 8 or less
        while (totalSize > 8) {
            //System.out.println("The value in index " + this.dblCDF[middleIndex]+ " and the target value " +U);
            if (Y[middleIndex] < U) {
                lowerIndex = middleIndex;
                middleIndex = lowerIndex + ArrayOperation.midPointIndex(lowerIndex, upperIndex);
            } else {
                upperIndex = middleIndex;
                middleIndex = lowerIndex + ArrayOperation.midPointIndex(lowerIndex, upperIndex);
            }
            //System.out.println("The upper index = " +upperIndex+ " and the lower = " +lowerIndex);
            totalSize = upperIndex - lowerIndex + 1;
        }
        intIndex = linearSearch(Y, U, lowerIndex);
        return intIndex;
    }

}
